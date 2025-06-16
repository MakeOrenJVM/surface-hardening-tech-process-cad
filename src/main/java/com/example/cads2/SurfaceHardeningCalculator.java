package com.example.cads2;

public class SurfaceHardeningCalculator {

    // Константы
    private static final double STEEL_DENSITY = 7.8;      // г/см^3
    private static final double SPECIFIC_HEAT = 0.46;     // кДж/(кг·°С)
    private static final double INITIAL_TEMPERATURE = 20; // °C, начальная температура детали

    // КПД
    private static final double THERMAL_EFFICIENCY = 0.7;    // ηт термический КПД
    private static final double TRANSFORMER_EFFICIENCY = 0.9; // ηтр КПД трансформатора
    private static final double INDUCTOR_EFFICIENCY = 0.85;   // ηи КПД индуктора

    /**
     * Расчет массы нагреваемой части детали (г)
     * m = π * Xк * (d - Xк) * ℓ * γ
     * Входы в мм, переводим в см
     */
    public double calculateMass(double depth, double diameter, double length) {
        double depth_cm = depth / 10.0;
        double diameter_cm = diameter / 10.0;
        double length_cm = length / 10.0;
        return Math.PI * depth_cm * (diameter_cm - depth_cm) * length_cm * STEEL_DENSITY;
    }

    /**
     * Расчет энергии, необходимой для нагрева (кДж)
     * A = m * cр * Δt
     */
    public double calculateEnergy(double massGrams, double finalTemperature) {
        double massKg = massGrams / 1000.0; // г -> кг
        double deltaT = finalTemperature - INITIAL_TEMPERATURE;
        return massKg * SPECIFIC_HEAT * deltaT;
    }

    /**
     * Расчет полезной мощности, потребляемой деталью (кВт)
     * P = A / (ηт * τ)
     */
    public double calculateUsefulPower(double energy, double heatingTimeSeconds) {
        return energy / (THERMAL_EFFICIENCY * heatingTimeSeconds);
    }

    /**
     * Расчет полной мощности нагрева (кВт)
     * Pп = P / (ηтр * ηи)
     */
    public double calculateTotalPower(double usefulPower) {
        return usefulPower / (TRANSFORMER_EFFICIENCY * INDUCTOR_EFFICIENCY);
    }

    /**
     * Расчет удельной мощности нагрева (кВт/см²)
     * P0 = Pп / S, где S = π * d * ℓ (площадь боковой поверхности)
     * Входы в мм, переводим в см
     */
    public double calculateSpecificPower(double totalPower, double diameter, double length) {
        double diameter_cm = diameter / 10.0;
        double length_cm = length / 10.0;
        double surfaceArea = Math.PI * diameter_cm * length_cm; // см²
        return totalPower / surfaceArea;
    }

    /**
     * Расчет минимальной и максимальной частоты (Гц)
     * fmin = 2*10^4 / sqrt(Xк)
     * fmax = 2*10^6 / sqrt(Xк)
     */
    public double[] calculateFrequencyRange(double depth) {
        return new double[]{2e4 / Math.sqrt(depth), 2e6 / Math.sqrt(depth)};
    }

    /**
     * Расчет оптимальной частоты (Гц)
     * fopt = 2*10^5 / sqrt(Xк)
     */
    public double calculateOptimalFrequency(double depth) {
        return 2e5 / Math.sqrt(depth);
    }

    /**
     * Расчет скорости перемещения детали в индукторе (мм/с)
     * Vд = ℓ / τ
     */
    public double calculateDetailSpeed(double length, double heatingTimeSeconds) {
        return length / heatingTimeSeconds;
    }

    /**
     * Расчет средней скорости нагрева (°C/с)
     * Vс = Δt / τ
     */
    public double calculateHeatingRate(double finalTemperature, double heatingTimeSeconds) {
        double deltaT = finalTemperature - INITIAL_TEMPERATURE;
        return deltaT / heatingTimeSeconds;
    }

    /**
     * Расчет производительности установки (кг/ч)
     * П = F * γ * Vд * 3600, где F - площадь поперечного сечения (см²)
     * Диаметр в мм, переводим в см
     */
    public double calculateProductivity(double diameter, double detailSpeed) {
        double diameter_cm = diameter / 10.0;
        double crossSectionArea = Math.PI * Math.pow(diameter_cm / 2.0, 2); // см²
        return crossSectionArea * STEEL_DENSITY * detailSpeed * 3600 / 1000.0; // кг/ч
    }
}

