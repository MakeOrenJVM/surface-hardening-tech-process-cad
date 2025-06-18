package com.example.cads2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class ResultsFormController {


    HardeningResult result;
    HardeningParameters parameters;


    @FXML
    private Button saveInBdButtonId;

    @FXML
    private TextField authorTextFieldId;

    @FXML
    private TextField nameProjectTextFieldId;

    @FXML
    private TextField coolingMediumTextFieldId;

    @FXML
    private TextField freqTextFieldId;

    @FXML
    private TextField heatingTempTextFieldId;

    @FXML
    private TextField powerTextFieldId;

    @FXML
    private TextField speedTextFieldId;

    @FXML
    private TextField temperingTempTextFieldId;

    @FXML
    private Button onBackButton;

    @FXML
    private Button exportWordButtonId;

    @FXML
    void onBackButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cads2/view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 300, 520));
            stage.setTitle("САПР ТП поверхностной закалки");
            stage.show();

            // Закрываем текущее окно
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Не удалось вернуться на главную форму.").show();
        }
    }

    @FXML
    void exportWordButton(ActionEvent event) {
        Stage stage = (Stage) exportWordButtonId.getScene().getWindow();

        String author = authorTextFieldId.getText();
        String projectName = nameProjectTextFieldId.getText();
        LocalDate date = LocalDate.now();

        new WordExporter(stage).export(result, parameters, author, projectName, date);
    }

    @FXML
    void saveInBdButton(ActionEvent event) throws SQLException {
        IdFetcher idFetcher = new IdFetcher();
        int steelGradeId = IdFetcher.getSteelGradeId(parameters.getSteelGrade());
        int quenchDepthId = IdFetcher.getQuenchDepthId(parameters.getQuenchDepths());
        int generatorId = IdFetcher.getTvchGeneratorId(result.getGenerator().getType());
        int opCardId = IdFetcher.getOperationCardId(parameters.getPartName());

        // Остальные данные из текстовых полей и параметров
        String author = authorTextFieldId.getText();
        String projectName = nameProjectTextFieldId.getText();
        LocalDate createdAt = LocalDate.now();

        double power = Double.parseDouble(powerTextFieldId.getText());
        double freq = Double.parseDouble(freqTextFieldId.getText());
        double speed = Double.parseDouble(speedTextFieldId.getText());
        int heatingTemp = Integer.parseInt(heatingTempTextFieldId.getText());
        int temperingTemp = Integer.parseInt(temperingTempTextFieldId.getText());

        int time = parameters.getTime();
        double diameter = parameters.getDiameter();
        double length = parameters.getLength();

        String insertQuery = """
        INSERT INTO project (
            case_author, case_created_at, part_type_name,
            steel_grades_id, quench_depths_id, tvch_generators_id, operation_card_id,
            process_power_kw, process_frequency_khz, heating_time_sec,
            heating_temperature_c, part_diameter_mm, part_length_mm,
            inductor_speed_mm_per_sec, project_name, tempering_temperature_c
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = Connector.getConnection();
            statement = connection.prepareStatement(insertQuery);

            statement.setString(1, author);
            statement.setDate(2, java.sql.Date.valueOf(createdAt));
            statement.setString(3, parameters.getPartName());
            statement.setInt(4, steelGradeId);
            statement.setInt(5, quenchDepthId);
            statement.setInt(6, generatorId);
            statement.setInt(7, opCardId);
            statement.setDouble(8, power);
            statement.setDouble(9, freq);
            statement.setInt(10, time);
            statement.setInt(11, heatingTemp);
            statement.setDouble(12, diameter);
            statement.setDouble(13, length);
            statement.setDouble(14, speed);
            statement.setString(15, projectName);
            statement.setInt(16, temperingTemp);

            statement.executeUpdate();
            System.out.println("Данные успешно сохранены в таблицу project.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }


    public void setResult(HardeningResult result, HardeningParameters parameters) {
        this.parameters = parameters;
        this.result = result;
        displayData();

    }

    private void displayData() {
        if (result == null) return;
        coolingMediumTextFieldId.setText(result.getCoolingMedium());
        freqTextFieldId.setText(String.valueOf(result.getFrequency()));
        heatingTempTextFieldId.setText(String.valueOf(result.getHeatingTemperature()));
        temperingTempTextFieldId.setText(String.valueOf(result.getTemperingTemperature()));
        powerTextFieldId.setText(String.valueOf(result.getPower()));
        speedTextFieldId.setText(String.valueOf(result.getDetailSpeed()));
    }

}
