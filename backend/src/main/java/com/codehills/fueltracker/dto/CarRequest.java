package com.codehills.fueltracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CarRequest {
    @NotBlank(message = "brand is required")
    private String brand;

    @NotBlank(message = "model is required")
    private String model;

    @Min(value = 1886, message = "year must be a valid positive year")
    private int year;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
