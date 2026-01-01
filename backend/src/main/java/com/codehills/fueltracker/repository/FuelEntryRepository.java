package com.codehills.fueltracker.repository;

import com.codehills.fueltracker.model.FuelEntry;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FuelEntryRepository {
    private final Map<Long, List<FuelEntry>> fuelEntriesByCar = new ConcurrentHashMap<>();

    public FuelEntry addEntry(long carId, FuelEntry fuelEntry) {
        fuelEntriesByCar.computeIfAbsent(carId, id -> Collections.synchronizedList(new ArrayList<>())).add(fuelEntry);
        return fuelEntry;
    }

    public List<FuelEntry> findByCarId(long carId) {
        return fuelEntriesByCar.getOrDefault(carId, Collections.emptyList());
    }
}
