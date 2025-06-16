package com.example.cads2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ResultsFormController {

    HardeningResult result;

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


    }

    @FXML
    void exportWordButton(ActionEvent event) {
        Stage stage = (Stage) exportWordButtonId.getScene().getWindow();

        new WordExporter(stage).export(result);
    }



    public void setResult(HardeningResult result) {
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
