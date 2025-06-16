package com.example.cads2;

public class Generator {
    private int id;
    private int number;
    private String type;
    private double powerKw;
    private double frequencyKHz;
    private String category;

    public Generator(int id, int number, String type, double powerKw, double frequencyKHz, String category) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.powerKw = powerKw;
        this.frequencyKHz = frequencyKHz;
        this.category = category;
    }

    public double getPowerKw() { return powerKw; }
    public double getFrequencyKHz() { return frequencyKHz; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public int getId() { return id; }
    public int getNumber() { return number; }

    @Override
    public String toString() {
        return "Generator{" +
                "id=" + id +
                ", number=" + number +
                ", type='" + type + '\'' +
                ", powerKw=" + powerKw +
                ", frequencyKHz=" + frequencyKHz +
                ", category='" + category + '\'' +
                '}';
    }
}
