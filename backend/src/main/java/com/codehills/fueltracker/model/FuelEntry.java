package com.codehills.fueltracker.model;

import java.time.Instant;

public class FuelEntry {
    private final double liters;
    private final double price;
    private final double odometer;
    private final Instant timestamp;

    public FuelEntry(double liters, double price, double odometer, Instant timestamp) {
        this.liters = liters;
        this.price = price;
        this.odometer = odometer;
        this.timestamp = timestamp;
    }

    public double getLiters() {
        return liters;
    }

    public double getPrice() {
        return price;
    }

    public double getOdometer() {
        return odometer;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
