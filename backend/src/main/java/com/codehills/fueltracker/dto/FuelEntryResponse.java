package com.codehills.fueltracker.dto;

import java.time.Instant;

public class FuelEntryResponse {
    private double liters;
    private double price;
    private double odometer;
    private Instant timestamp;

    public FuelEntryResponse() {
    }

    public FuelEntryResponse(double liters, double price, double odometer, Instant timestamp) {
        this.liters = liters;
        this.price = price;
        this.odometer = odometer;
        this.timestamp = timestamp;
    }

    public double getLiters() {
        return liters;
    }

    public void setLiters(double liters) {
        this.liters = liters;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOdometer() {
        return odometer;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
