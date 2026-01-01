package com.codehills.fueltracker.repository;

import com.codehills.fueltracker.model.Car;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CarRepository {
    private final Map<Long, Car> cars = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Car save(String brand, String model, int year) {
        long id = idGenerator.getAndIncrement();
        Car car = new Car(id, brand, model, year);
        cars.put(id, car);
        return car;
    }

    public Collection<Car> findAll() {
        return new ArrayList<>(cars.values());
    }

    public Optional<Car> findById(long id) {
        return Optional.ofNullable(cars.get(id));
    }
}
