package com.example.cads2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OperationFormController {
    @FXML
    private Button addButtonId;

    @FXML
    private Button deleteButtonId;

    @FXML
    private TextField hardness_control;

    @FXML
    private TextField induction_heating;

    @FXML
    private ComboBox<String> partNameComboBox;

    @FXML
    private TextField partNameTextField;

    @FXML
    private TextField post_tempering_cooling;

    @FXML
    private TextField prep_surface;

    @FXML
    private TextField quenching;

    @FXML
    private TextField setup;

    @FXML
    private TextField tempering;

    @FXML
    private Button updateButtonId;

    @FXML
    private Button onBackButtonId;

    @FXML
    void onBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cads2/view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 300, 520));
            stage.setTitle("САПР ТП поверхностной закалка");
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
    public void initialize() {
        loadPartNamesFromDB();

        partNameComboBox.setOnAction(event -> loadOperationData(partNameComboBox.getValue()));
    }
    private void loadPartNamesFromDB() {
        partNameComboBox.getItems().clear();

        String query = "SELECT part_name FROM operation_card";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                partNameComboBox.getItems().add(rs.getString("part_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadOperationData(String partName) {
        String query = "SELECT * FROM operation_card WHERE part_name = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, partName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    prep_surface.setText(rs.getString("prep_surface"));
                    setup.setText(rs.getString("setup"));
                    induction_heating.setText(rs.getString("induction_heating"));
                    quenching.setText(rs.getString("quenching"));
                    tempering.setText(rs.getString("tempering"));
                    post_tempering_cooling.setText(rs.getString("post_tempering_cooling"));
                    hardness_control.setText(rs.getString("hardness_control"));

                    partNameTextField.setText(partName); // заполним также текстовое поле
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void addButton(ActionEvent event) {
        String query = """
        INSERT INTO operation_card (part_name, prep_surface, setup, induction_heating, quenching, tempering, post_tempering_cooling, hardness_control)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, partNameTextField.getText());
            stmt.setString(2, prep_surface.getText());
            stmt.setString(3, setup.getText());
            stmt.setString(4, induction_heating.getText());
            stmt.setString(5, quenching.getText());
            stmt.setString(6, tempering.getText());
            stmt.setString(7, post_tempering_cooling.getText());
            stmt.setString(8, hardness_control.getText());

            stmt.executeUpdate();

            loadPartNamesFromDB(); // обновить список
            partNameComboBox.setValue(partNameTextField.getText()); // выбрать новую
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deleteButton(ActionEvent event) {
        String partName = partNameComboBox.getValue();
        if (partName == null) return;

        String query = "DELETE FROM operation_card WHERE part_name = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, partName);
            stmt.executeUpdate();

            partNameComboBox.getItems().remove(partName);
            partNameComboBox.setValue(null);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    void updateButtonId(ActionEvent event) {
        String query = """
        UPDATE operation_card SET
        prep_surface = ?, setup = ?, induction_heating = ?, quenching = ?, tempering = ?, post_tempering_cooling = ?, hardness_control = ?
        WHERE part_name = ?
        """;

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, prep_surface.getText());
            stmt.setString(2, setup.getText());
            stmt.setString(3, induction_heating.getText());
            stmt.setString(4, quenching.getText());
            stmt.setString(5, tempering.getText());
            stmt.setString(6, post_tempering_cooling.getText());
            stmt.setString(7, hardness_control.getText());
            stmt.setString(8, partNameComboBox.getValue());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        prep_surface.clear();
        setup.clear();
        induction_heating.clear();
        quenching.clear();
        tempering.clear();
        post_tempering_cooling.clear();
        hardness_control.clear();
        partNameTextField.clear();
    }

}
