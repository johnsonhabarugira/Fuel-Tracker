package com.codehills.fueltracker.service;

import com.codehills.fueltracker.dto.CarRequest;
import com.codehills.fueltracker.dto.CarResponse;
import com.codehills.fueltracker.dto.FuelEntryRequest;
import com.codehills.fueltracker.dto.FuelEntryResponse;
import com.codehills.fueltracker.dto.FuelStatsResponse;
import com.codehills.fueltracker.exception.BadRequestException;
import com.codehills.fueltracker.exception.ResourceNotFoundException;
import com.codehills.fueltracker.model.Car;
import com.codehills.fueltracker.model.FuelEntry;
import com.codehills.fueltracker.repository.CarRepository;
import com.codehills.fueltracker.repository.FuelEntryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final FuelEntryRepository fuelEntryRepository;

    public CarService(CarRepository carRepository, FuelEntryRepository fuelEntryRepository) {
        this.carRepository = carRepository;
        this.fuelEntryRepository = fuelEntryRepository;
    }

    public CarResponse createCar(CarRequest request) {
        validateCarRequest(request);
        Car car = carRepository.save(request.getBrand().trim(), request.getModel().trim(), request.getYear());
        return toCarResponse(car);
    }

    public List<CarResponse> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(this::toCarResponse)
                .collect(Collectors.toList());
    }

    public FuelEntryResponse addFuelEntry(long carId, FuelEntryRequest request) {
        ensureCarExists(carId);
        validateFuelRequest(request);
        FuelEntry entry = new FuelEntry(request.getLiters(), request.getPrice(), request.getOdometer(), Instant.now());
        fuelEntryRepository.addEntry(carId, entry);
        return toFuelEntryResponse(entry);
    }

    public FuelStatsResponse getFuelStats(long carId) {
        ensureCarExists(carId);
        List<FuelEntry> entries = fuelEntryRepository.findByCarId(carId);

        double totalFuel = entries.stream().mapToDouble(FuelEntry::getLiters).sum();
        double totalCost = entries.stream().mapToDouble(FuelEntry::getPrice).sum();
        double averageConsumption = 0.0;

        if (entries.size() >= 2) {
            double minOdometer = entries.stream().min(Comparator.comparingDouble(FuelEntry::getOdometer))
                    .map(FuelEntry::getOdometer).orElse(0.0);
            double maxOdometer = entries.stream().max(Comparator.comparingDouble(FuelEntry::getOdometer))
                    .map(FuelEntry::getOdometer).orElse(0.0);
            double distance = maxOdometer - minOdometer;
            if (distance > 0) {
                averageConsumption = (totalFuel / distance) * 100.0;
            }
        }

        return new FuelStatsResponse(totalFuel, totalCost, averageConsumption);
    }

    private void validateCarRequest(CarRequest request) {
        if (request.getBrand() == null || request.getModel() == null) {
            throw new BadRequestException("brand and model are required");
        }
        if (request.getBrand().isBlank() || request.getModel().isBlank()) {
            throw new BadRequestException("brand and model must not be blank");
        }
        if (request.getYear() <= 0) {
            throw new BadRequestException("year must be a positive number");
        }
    }

    private void validateFuelRequest(FuelEntryRequest request) {
        if (request.getLiters() == null || request.getPrice() == null || request.getOdometer() == null) {
            throw new BadRequestException("liters, price, and odometer are required");
        }
        if (request.getLiters() <= 0) {
            throw new BadRequestException("liters must be greater than zero");
        }
        if (request.getPrice() < 0) {
            throw new BadRequestException("price must not be negative");
        }
        if (request.getOdometer() <= 0) {
            throw new BadRequestException("odometer must be greater than zero");
        }
    }

    private void ensureCarExists(long carId) {
        carRepository.findById(carId).orElseThrow(() -> new ResourceNotFoundException("Car not found: " + carId));
    }

    private CarResponse toCarResponse(Car car) {
        return new CarResponse(car.getId(), car.getBrand(), car.getModel(), car.getYear());
    }

    private FuelEntryResponse toFuelEntryResponse(FuelEntry entry) {
        return new FuelEntryResponse(entry.getLiters(), entry.getPrice(), entry.getOdometer(), entry.getTimestamp());
    }
}
