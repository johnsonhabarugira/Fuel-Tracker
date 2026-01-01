package com.codehills.fueltracker.controller;

import com.codehills.fueltracker.dto.CarRequest;
import com.codehills.fueltracker.dto.CarResponse;
import com.codehills.fueltracker.dto.FuelEntryRequest;
import com.codehills.fueltracker.dto.FuelEntryResponse;
import com.codehills.fueltracker.dto.FuelStatsResponse;
import com.codehills.fueltracker.service.CarService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    public ResponseEntity<CarResponse> createCar(@Valid @RequestBody CarRequest request) {
        CarResponse response = carService.createCar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<CarResponse> getCars() {
        return carService.getAllCars();
    }

    @PostMapping("/{id}/fuel")
    public ResponseEntity<FuelEntryResponse> addFuel(@PathVariable("id") long carId,
                                                     @Valid @RequestBody FuelEntryRequest request) {
        FuelEntryResponse response = carService.addFuelEntry(carId, request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()).body(response);
    }

    @GetMapping("/{id}/fuel/stats")
    public FuelStatsResponse getFuelStats(@PathVariable("id") long carId) {
        return carService.getFuelStats(carId);
    }
}
