package com.example.cads2;

public class HardeningResult {

    private Generator generator;
    private int heatingTemperature;
    private int temperingTemperature;
    private double power;
    private double frequency;
    private double detailSpeed;
    private String coolingMedium;

    public Hardness getHardness() {
        return hardness;
    }

    public Generator getGenerator() {
        return generator;
    }

    private Hardness hardness;

    public HardeningResult(Generator generator, int heatingTemperature, int temperingTemperature, double power,
                           double frequency, double detailSpeed, String coolingMedium, Hardness hardness) {
        this.generator = generator;
        this.heatingTemperature = heatingTemperature;
        this.temperingTemperature = temperingTemperature;
        this.power = power;
        this.frequency = frequency;
        this.detailSpeed = detailSpeed;
        this.coolingMedium = coolingMedium;
        this.hardness = hardness;
    }

    public int getHeatingTemperature() {
        return heatingTemperature;
    }

    public int getTemperingTemperature() {
        return temperingTemperature;
    }

    public double getPower() {
        return power;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getDetailSpeed() {
        return detailSpeed;
    }

    public String getCoolingMedium() {
        return coolingMedium;
    }
}