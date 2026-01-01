# Fuel Tracker

Fuel Tracker is a two-module Java project:
- **Backend**: Spring Boot REST API with an integrated manual servlet, entirely in-memory (no DB/auth).
- **CLI**: Standalone Java command-line client that calls the API over HTTP using java.net.http.HttpClient.

## Project layout
- `backend/` – Spring Boot app (controllers  service  repositories, servlet registration)
- `cli/` – CLI tool with fat-jar packaging
- `pom.xml` – parent Maven build

## Requirements
- Java 17+
- Maven 3.9+

## Build
```bash
mvn package
```

## Run the backend
```bash
mvn -pl backend spring-boot:run
# or run the built jar
java -jar backend/target/fuel-tracker-backend-1.0.0.jar
```
Default port is `8080`. To change: `-Dspring-boot.run.arguments=--server.port=8081` or set `server.port` in `backend/src/main/resources/application.properties`.

If the port is busy, stop the other process (e.g., `netstat -ano | findstr :8080` then `taskkill /PID <pid> /F`) or use another port as above.

## API (REST)
- **Create car**: `POST /api/cars`  
  Body: `{"brand":"Toyota","model":"Corolla","year":2018}`  `201 Created` with created car.
- **List cars**: `GET /api/cars`  array of cars.
- **Add fuel entry**: `POST /api/cars/{id}/fuel`  
  Body: `{"liters":40,"price":52.5,"odometer":45000}`  `201 Created` with entry.
- **Fuel stats**: `GET /api/cars/{id}/fuel/stats`  
  Returns `{ "totalFuelLiters": ..., "totalCost": ..., "averageConsumptionPer100Km": ... }`.

Validation errors return `400 Bad Request`; unknown car IDs return `404 Not Found`.

## Servlet endpoint
- Path: `GET /servlet/fuel-stats?carId={id}`
- Uses the same `CarService` as the REST controller; responds with the same stats JSON and explicit status codes.

## Data model
- Car: `id`, `brand`, `model`, `year`
- FuelEntry: `liters`, `price`, `odometer`, `timestamp`

## In-memory storage
- Cars: `Map<Long, Car>`
- Fuel entries per car: `Map<Long, List<FuelEntry>>`
IDs are generated with an `AtomicLong`.

## Stats calculation
- `totalFuelLiters`: sum of liters for the car
- `totalCost`: sum of price for the car
- `averageConsumptionPer100Km`:
  - if fewer than 2 entries or distance <= 0  `0`
  - `distanceKm = maxOdometer - minOdometer`
  - `avg = (totalFuelLiters / distanceKm) * 100`

## Run the CLI
After `mvn package`, run:
```bash
java -jar cli/target/fuel-tracker-cli-1.0.0-jar-with-dependencies.jar <command> [options]
```
Commands:
- `create-car --brand Toyota --model Corolla --year 2018`
- `list-cars`
- `add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000`
- `fuel-stats --carId 1`

Default base URL: `http://localhost:8080`. Override with `--baseUrl http://host:port` or `FUEL_API_BASE_URL`.

## Quick start
1) `mvn package`
2) `mvn -pl backend spring-boot:run`
3) In another shell, create a car and add fuel via CLI or curl:
   ```bash
   java -jar cli/target/fuel-tracker-cli-1.0.0-jar-with-dependencies.jar create-car --brand Toyota --model Corolla --year 2018
   java -jar cli/target/fuel-tracker-cli-1.0.0-jar-with-dependencies.jar add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000
   java -jar cli/target/fuel-tracker-cli-1.0.0-jar-with-dependencies.jar fuel-stats --carId 1
   ```
