package com.example.cads2;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Controller {

    @FXML
    private Button calculateButton;

    @FXML
    private Spinner<Integer> diameterSpinner;

    @FXML
    private Spinner<Integer> timeSpinner;

    @FXML
    private Spinner<Integer> lengthSpinner;

    @FXML
    private ComboBox<String> steelGradeComboBox;

    @FXML
    private ComboBox<String> quenchDepthsComboBox;

    @FXML
    private ComboBox<String> partNameComboBox;



    @FXML
    public void initialize() {
        List<String> grades = fetchSteelGradesFromDB();
        steelGradeComboBox.setItems(FXCollections.observableArrayList(grades));

        List<String> depths = fetchQuenchDepthsFromDB();
        quenchDepthsComboBox.setItems(FXCollections.observableArrayList(depths));

        List<String> parts = fetchPartsFromDB();
        partNameComboBox.setItems(FXCollections.observableArrayList(parts));

        SpinnerValueFactory<Integer> diameterFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 500, 50, 1);
        diameterSpinner.setValueFactory(diameterFactory);
        diameterSpinner.setEditable(true);

        SpinnerValueFactory<Integer> lengthFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 3000, 100, 1);
        lengthSpinner.setValueFactory(lengthFactory);
        lengthSpinner.setEditable(true);

        SpinnerValueFactory<Integer> timeFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 60, 10);
        timeSpinner.setValueFactory(timeFactory);
        timeSpinner.setEditable(true);

        addValidation(diameterSpinner, diameterFactory, 10, 500);
        addValidation(lengthSpinner, lengthFactory, 5, 3000);
        addValidation(timeSpinner,timeFactory,2,60);


        diameterSpinner.getEditor().setTextFormatter(createDigitOnlyFormatter());
        lengthSpinner.getEditor().setTextFormatter(createDigitOnlyFormatter());
        timeSpinner.getEditor().setTextFormatter(createDigitOnlyFormatter());



    }

    private TextFormatter<Integer> createDigitOnlyFormatter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        });
    }

    //Получаем список стали
    private List<String> fetchSteelGradesFromDB() {
        List<String> grades = new ArrayList<>();

        String query = "select steel_grade from steel_grades";

        try (Connection conn = Connector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                grades.add(rs.getString("steel_grade"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grades;
    }


    private List<String> fetchQuenchDepthsFromDB() {
        List<String> depths = new ArrayList<>();

        String query = "select depth_mm from quench_depths";

        try (Connection conn = Connector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                depths.add(rs.getString("depth_mm"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return depths;

    }

    private List<String> fetchPartsFromDB() {
        List<String> parts = new ArrayList<>();

        String query = "select part_name from operation_card";

        try (Connection conn = Connector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                parts.add(rs.getString("part_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parts;
    }

    private void addValidation(Spinner<Integer> spinner, SpinnerValueFactory<Integer> factory, int min, int max) {
        spinner.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                try {
                    int val = Integer.parseInt(spinner.getEditor().getText());
                    if (val >= min && val <= max) {
                        factory.setValue(val);
                    } else {
                        spinner.getEditor().setText(String.valueOf(factory.getValue()));
                    }
                } catch (NumberFormatException e) {
                    spinner.getEditor().setText(String.valueOf(factory.getValue()));
                }
            }
        });
    }

    //Получение температуры
    private Integer getHeatingTemperatureByGradeFromDB(String gradeName) {
        Integer heatingTemp = null;
        String query = "SELECT hardening_temp_recommended FROM steel_grades WHERE steel_grade = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, gradeName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    heatingTemp = rs.getInt("hardening_temp_recommended"); // или нужный столбец с температурой
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return heatingTemp;
    }


    @FXML
    void runCalculate(ActionEvent event) {
        HardeningResult hardeningResult = null;
        HardeningParameters hardeningParameters = null;

        try {
            // 1. Получаем марку стали из ComboBox
            String grade = steelGradeComboBox.getValue();
            if (grade == null || grade.isEmpty()) {
                System.out.println("Выберите марку стали");
                showError("Выберите марку стали");
                return;
            }

            // 2. Получаем температуру нагрева из БД
            Integer heatingTemp = getHeatingTemperatureByGradeFromDB(grade);
            if (heatingTemp == null) {
                System.out.println("Не удалось получить температуру нагрева для марки стали: " + grade);
                return;
            }

            //Температура отпуска
            Integer temperingTemp = getTemperingTemperatureByGradeFromDB(grade);
            if (temperingTemp == null) {
                System.out.println("Не удалось получить температуру отпуска для марки стали: " + grade);
                return;
            }
            System.out.println(temperingTemp);

            String coolingMedium = getCoolingMediumByGradeFromDB(grade);
            if (coolingMedium == null) {
                System.out.println("Не удалось получить среду охлаждения для марки стали: " + grade);
                return;
            }
            System.out.println(coolingMedium);

            Hardness hardness = getHardnessByGradeFromDB(grade);
            System.out.println(hardness.max + " харднес");

            // 3. Получаем остальные параметры
            String partName = partNameComboBox.getValue();
            Integer diameter = diameterSpinner.getValue();  // мм
            Integer length = lengthSpinner.getValue();      // мм
            Double depth = Double.valueOf(quenchDepthsComboBox.getValue()); // мм
            if (depth + 10 > Double.valueOf(diameter) ) {
                showError("Слишком маленький диаметр детали для такой глубины закалки");
                return;
            }
            Integer timeSeconds = timeSpinner.getValue();// секунды

            // 4. Создаём экземпляр калькулятора
            SurfaceHardeningCalculator calculator = new SurfaceHardeningCalculator();

            // 5. Вычисления
            double mass = calculator.calculateMass(depth, diameter, length);
            double energy = calculator.calculateEnergy(mass, heatingTemp);
            double usefulPower = calculator.calculateUsefulPower(energy, timeSeconds);
            double totalPower = calculator.calculateTotalPower(usefulPower);
            double specificPower = calculator.calculateSpecificPower(totalPower, diameter, length);
            double[] freqRange = calculator.calculateFrequencyRange(depth);
            double optimalFreq = calculator.calculateOptimalFrequency(depth);
            double detailSpeed = calculator.calculateDetailSpeed(length, timeSeconds);
            double heatingRate = calculator.calculateHeatingRate(heatingTemp, timeSeconds);
            double productivity = calculator.calculateProductivity(diameter, detailSpeed);

            //Передаем параметры в объект
            hardeningParameters = new HardeningParameters(
                    diameter,
                    timeSeconds,
                    length,
                    depth,
                    grade,
                    partName
            );

            //Подбираем генератор
            List<Generator> generators = loadGeneratorsFromDatabase(Connector.getConnection());
            Generator generator = findMatchingGenerator(generators,totalPower, optimalFreq / 1000);
            System.out.println(generator);

            // 6. Выводим результаты
            System.out.println("Масса нагреваемой части детали (г): " + mass);
            System.out.println("Энергия нагрева (кДж): " + energy);
            System.out.println("Полезная мощность (кВт): " + usefulPower);
            System.out.println("Полная мощность нагрева (кВт): " + totalPower);
            System.out.println("Удельная мощность нагрева (кВт/см²): " + specificPower);
            System.out.println("Диапазон частот (Гц): от " + freqRange[0] + " до " + freqRange[1]);
            System.out.println("Оптимальная частота (Гц): " + optimalFreq);
            System.out.println("Скорость перемещения детали (мм/с): " + detailSpeed);
            System.out.println("Средняя скорость нагрева (°C/с): " + heatingRate);
            System.out.println("Производительность установки (кг/ч): " + productivity);

            hardeningResult = new HardeningResult(
                    generator,
                    heatingTemp,
                    temperingTemp,
                    totalPower,
                    optimalFreq / 1000,
                    detailSpeed,
                    coolingMedium,
                    hardness);
        } catch (Exception e) {
            e.printStackTrace();
        }


        FXMLLoader loader = new FXMLLoader(getClass().getResource("ResultsForm.fxml"));
        try {
            Parent root = loader.load();

            ResultsFormController resultsFormController = loader.getController();
            resultsFormController.setResult(hardeningResult, hardeningParameters);


            Stage resultsStage = new Stage();
            resultsStage.setScene(new Scene(root,700,600));
            resultsStage.setTitle("Результаты расчёта");

            // Показываем новое окно
            resultsStage.show();

            // Скрываем главное окно (откуда пришли)
            ((Node)(event.getSource())).getScene().getWindow().hide();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void projectsListButtonClicked(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProjectsListView.fxml"));
        try {
            Parent root = loader.load();
            Stage resultsStage = new Stage();
            resultsStage.setScene(new Scene(root,1000,600));
            resultsStage.setTitle("Результаты расчёта");

            // Показываем новое окно
            resultsStage.show();

            // Скрываем главное окно (откуда пришли)
            Stage currentStage = (Stage)partNameComboBox.getScene().getWindow(); // menuBar — любой элемент из главной сцены
            currentStage.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("");
        alert.setHeaderText("Произошла ошибка");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Integer getTemperingTemperatureByGradeFromDB(String gradeName) {
        Integer heatingTemp = null;
        String query = "SELECT tempering_temp_recommended FROM steel_grades WHERE steel_grade = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, gradeName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    heatingTemp = rs.getInt("tempering_temp_recommended"); // или нужный столбец с температурой
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return heatingTemp;
    }

    private String getCoolingMediumByGradeFromDB(String gradeName) {
        String coolingMedium = null;
        String query = "SELECT cooling_medium FROM steel_grades WHERE steel_grade = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, gradeName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    coolingMedium = rs.getString("cooling_medium"); // или нужный столбец с температурой
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coolingMedium;
    }

    public List<Generator> loadGeneratorsFromDatabase(Connection connection) throws SQLException {
        List<Generator> generators = new ArrayList<>();

        String query = "SELECT id, number, type, power_kw, frequency_khz, category FROM tvch_generators";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Generator g = new Generator(
                        rs.getInt("id"),
                        rs.getInt("number"),
                        rs.getString("type"),
                        rs.getDouble("power_kw"),
                        rs.getDouble("frequency_khz"),
                        rs.getString("category")
                );
                generators.add(g);
            }
        }

        return generators;
    }

    public Generator findMatchingGenerator(List<Generator> generators, double requiredPowerKw, double requiredFrequencyKHz) {
        return generators.stream()
                .filter(g -> g.getPowerKw() >= requiredPowerKw && g.getFrequencyKHz() >= requiredFrequencyKHz)
                .min(Comparator.comparingDouble(g ->
                        (g.getPowerKw() - requiredPowerKw) + (g.getFrequencyKHz() - requiredFrequencyKHz)
                ))
                .orElse(null);
    }

    private Hardness getHardnessByGradeFromDB(String gradeName) {
        Hardness hardness = null;

        String query = "SELECT hardness_min, hardness_max FROM steel_grades WHERE steel_grade = ?";



        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {


            pstmt.setString(1, gradeName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                   hardness = new Hardness(
                            rs.getInt("hardness_max"),
                            rs.getInt("hardness_min"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hardness;
    }

}