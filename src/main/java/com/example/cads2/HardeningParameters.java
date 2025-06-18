package com.example.cads2;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

public class HardeningParameters {
    private Integer diameter;
    private Integer time;
    private Integer length;
    private String steelGrade;
    private Double quenchDepths;
    private String partName;

    public HardeningParameters(Integer diameter, Integer time, Integer length, Double quenchDepths, String steelGrade, String partName) {
        this.diameter = diameter;
        this.time = time;
        this.length = length;
        this.quenchDepths = quenchDepths;
        this.steelGrade = steelGrade;
        this.partName = partName;
    }

    public Integer getDiameter() {
        return diameter;
    }

    public Integer getTime() {
        return time;
    }

    public Integer getLength() {
        return length;
    }

    public String getSteelGrade() {
        return steelGrade;
    }

    public Double getQuenchDepths() {
        return quenchDepths;
    }

    public String getPartName() {
        return partName;
    }
}
