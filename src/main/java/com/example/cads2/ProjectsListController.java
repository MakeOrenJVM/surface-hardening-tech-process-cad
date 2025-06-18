package com.example.cads2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ProjectsListController {
    @FXML
    private Button chooseButtonId;


    @FXML
    private TableColumn<Project, String> authorColumn;

    @FXML
    private TableColumn<Project, String> dateColumn;

    @FXML
    private Spinner<Integer> diameterSpinner;

    @FXML
    private Button calculateButton;

    @FXML
    private TableColumn<Project, Integer> idColumn;

    @FXML
    private Spinner<Integer> lengthSpinner;

    @FXML
    private TableColumn<Project, String> nameProjectColumn;

    @FXML
    private Button onBackButtonId;

    @FXML
    private ComboBox<String> partNameComboBox;

    @FXML
    private TableView<Project> projectsTable;

    @FXML
    private ComboBox<Double> quenchDepthsComboBox;

    @FXML
    private ComboBox<String> steelGradeComboBox;

    @FXML
    private Spinner<Integer> timeSpinner;

    @FXML
    private TextField freqTextFieldId;

    @FXML
    private TextField heatingTempTextFieldId;

    @FXML
    private TextField speedTextFieldId;

    @FXML
    private TextField temperingTempTextFieldId;

    @FXML
    private TextField powerTextFieldId;

    @FXML
    private TextField coolingMediumTextFieldId;

    @FXML
    void onBackButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cads2/view.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root, 300, 520)); // Укажи нужные размеры
        stage.setTitle("Поверхностная закалка");
        stage.show();

        // Закрываем текущее окно
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
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
    public void initialize() {
        setupTableColumns();
        loadAndDisplayProjects();

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
    }

    @FXML
    void chooseButton(ActionEvent event) {
        Project selectedProject = projectsTable.getSelectionModel().getSelectedItem();

        if (selectedProject == null) {
            new Alert(Alert.AlertType.WARNING, "Сначала выберите проект из таблицы.").show();
            return;
        }

        int projectId = selectedProject.getId();

        String query = """
    SELECT 
        p.*, sg.steel_grade, sg.cooling_medium, qd.depth_mm, oc.part_name 
    FROM project p
    JOIN steel_grades sg ON p.steel_grades_id = sg.id
    JOIN quench_depths qd ON p.quench_depths_id = qd.id
    JOIN operation_card oc ON p.operation_card_id = oc.id
    WHERE p.id = ?
    """;

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, projectId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Параметры детали
                    diameterSpinner.getValueFactory().setValue((int) rs.getDouble("part_diameter_mm"));
                    lengthSpinner.getValueFactory().setValue((int) rs.getDouble("part_length_mm"));
                    timeSpinner.getValueFactory().setValue(rs.getInt("heating_time_sec"));

                    partNameComboBox.setValue(rs.getString("part_name"));
                    steelGradeComboBox.setValue(rs.getString("steel_grade"));
                    quenchDepthsComboBox.setValue(rs.getDouble("depth_mm"));

                    // Результаты расчёта
                    freqTextFieldId.setText(String.valueOf(rs.getDouble("process_frequency_khz")));
                    heatingTempTextFieldId.setText(String.valueOf(rs.getInt("heating_temperature_c")));
                    temperingTempTextFieldId.setText(String.valueOf(rs.getInt("tempering_temperature_c")));
                    speedTextFieldId.setText(String.valueOf(rs.getDouble("inductor_speed_mm_per_sec")));
                    powerTextFieldId.setText(String.valueOf(rs.getDouble("process_power_kw")));

                    // Здесь добавляем установку cooling_medium
                    String coolingMedium = rs.getString("cooling_medium");
                    coolingMediumTextFieldId.setText(coolingMedium);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Ошибка при загрузке данных проекта из БД.").show();
        }
    }

    private ObservableList<Project> loadProjectsFromDatabase() {
        ObservableList<Project> projects = FXCollections.observableArrayList();

        String query = "SELECT id, case_author, project_name, case_created_at FROM project";

        try (Connection conn = Connector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String author = rs.getString("case_author");
                String name = rs.getString("project_name");
                String dateTime = rs.getString("case_created_at");
                String dateOnly = dateTime.split(" ")[0];

                projects.add(new Project(id,author, name, dateOnly));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }

    // Метод для настройки колонок таблицы
    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        nameProjectColumn.setCellValueFactory(cellData -> cellData.getValue().projectNameProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
    }

    // Метод для загрузки данных из БД и отображения в таблице
    private void loadAndDisplayProjects() {
        ObservableList<Project> projects = loadProjectsFromDatabase();
        projectsTable.setItems(projects);
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

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("");
        alert.setHeaderText("Произошла ошибка");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Generator findMatchingGenerator(List<Generator> generators, double requiredPowerKw, double requiredFrequencyKHz) {
        return generators.stream()
                .filter(g -> g.getPowerKw() >= requiredPowerKw && g.getFrequencyKHz() >= requiredFrequencyKHz)
                .min(Comparator.comparingDouble(g ->
                        (g.getPowerKw() - requiredPowerKw) + (g.getFrequencyKHz() - requiredFrequencyKHz)
                ))
                .orElse(null);
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
}