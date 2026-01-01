package com.codehills.fueltracker.model;

public class Car {
    private final long id;
    private final String brand;
    private final String model;
    private final int year;

    public Car(long id, String brand, String model, int year) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }
}
