package com.codehills.fueltracker.dto;

public class FuelStatsResponse {
    private double totalFuelLiters;
    private double totalCost;
    private double averageConsumptionPer100Km;

    public FuelStatsResponse() {
    }

    public FuelStatsResponse(double totalFuelLiters, double totalCost, double averageConsumptionPer100Km) {
        this.totalFuelLiters = totalFuelLiters;
        this.totalCost = totalCost;
        this.averageConsumptionPer100Km = averageConsumptionPer100Km;
    }

    public double getTotalFuelLiters() {
        return totalFuelLiters;
    }

    public void setTotalFuelLiters(double totalFuelLiters) {
        this.totalFuelLiters = totalFuelLiters;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getAverageConsumptionPer100Km() {
        return averageConsumptionPer100Km;
    }

    public void setAverageConsumptionPer100Km(double averageConsumptionPer100Km) {
        this.averageConsumptionPer100Km = averageConsumptionPer100Km;
    }
}
