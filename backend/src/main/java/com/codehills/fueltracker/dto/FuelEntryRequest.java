package com.codehills.fueltracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class FuelEntryRequest {
    @NotNull
    @Positive(message = "liters must be greater than zero")
    private Double liters;

    @NotNull
    @PositiveOrZero(message = "price must not be negative")
    private Double price;

    @NotNull
    @Positive(message = "odometer must be greater than zero")
    private Double odometer;

    public Double getLiters() {
        return liters;
    }

    public void setLiters(Double liters) {
        this.liters = liters;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOdometer() {
        return odometer;
    }

    public void setOdometer(Double odometer) {
        this.odometer = odometer;
    }
}
