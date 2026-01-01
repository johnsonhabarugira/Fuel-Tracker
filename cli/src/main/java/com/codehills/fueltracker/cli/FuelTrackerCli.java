package com.codehills.fueltracker.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FuelTrackerCli {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public FuelTrackerCli() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static void main(String[] args) {
        FuelTrackerCli cli = new FuelTrackerCli();
        cli.run(args);
    }

    private void run(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];
        Map<String, String> options = parseOptions(args, 1);
        String baseUrl = resolveBaseUrl(options);

        try {
            switch (command) {
                case "create-car" -> handleCreateCar(baseUrl, options);
                case "add-fuel" -> handleAddFuel(baseUrl, options);
                case "fuel-stats" -> handleFuelStats(baseUrl, options);
                default -> {
                    System.err.println("Unknown command: " + command);
                    printUsage();
                }
            }
        } catch (IllegalArgumentException ex) {
            System.err.println("Error: " + ex.getMessage());
            printUsage();
        }
    }

    private void handleCreateCar(String baseUrl, Map<String, String> options) {
        String brand = options.get("brand");
        String model = options.get("model");
        String yearStr = options.get("year");

        if (brand == null || model == null || yearStr == null) {
            throw new IllegalArgumentException("create-car requires --brand, --model, and --year");
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("year must be a number");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("brand", brand);
        payload.put("model", model);
        payload.put("year", year);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/cars"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(writeJson(payload)))
                .build();

        sendAndPrint(request, response -> {
            CarResponse car = readJson(response.body(), CarResponse.class);
            System.out.printf("Created car #%d: %s %s (%d)%n", car.id(), car.brand(), car.model(), car.year());
        });
    }

    private void handleAddFuel(String baseUrl, Map<String, String> options) {
        String carIdStr = options.get("carId");
        String litersStr = options.get("liters");
        String priceStr = options.get("price");
        String odometerStr = options.get("odometer");

        if (carIdStr == null || litersStr == null || priceStr == null || odometerStr == null) {
            throw new IllegalArgumentException("add-fuel requires --carId, --liters, --price, and --odometer");
        }

        long carId = parseLong(carIdStr, "carId");
        double liters = parseDouble(litersStr, "liters");
        double price = parseDouble(priceStr, "price");
        double odometer = parseDouble(odometerStr, "odometer");

        Map<String, Object> payload = new HashMap<>();
        payload.put("liters", liters);
        payload.put("price", price);
        payload.put("odometer", odometer);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/cars/" + carId + "/fuel"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(writeJson(payload)))
                .build();

        sendAndPrint(request, response -> {
            FuelEntryResponse entry = readJson(response.body(), FuelEntryResponse.class);
            System.out.printf("Added fuel: %.2f L, cost %.2f at odometer %.1f%n",
                    entry.liters(), entry.price(), entry.odometer());
        });
    }

    private void handleFuelStats(String baseUrl, Map<String, String> options) {
        String carIdStr = options.get("carId");
        if (carIdStr == null) {
            throw new IllegalArgumentException("fuel-stats requires --carId");
        }

        long carId = parseLong(carIdStr, "carId");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/cars/" + carId + "/fuel/stats"))
                .GET()
                .build();

        sendAndPrint(request, response -> {
            FuelStatsResponse stats = readJson(response.body(), FuelStatsResponse.class);
            System.out.printf("Total fuel: %.1f L%n", stats.totalFuelLiters());
            System.out.printf("Total cost: %.2f%n", stats.totalCost());
            System.out.printf("Average consumption: %.1f L/100km%n", stats.averageConsumptionPer100Km());
        });
    }

    private void sendAndPrint(HttpRequest request, ResponseHandler handler) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (isSuccess(response)) {
                handler.handle(response);
            }
        } catch (IOException | InterruptedException ex) {
            System.err.println("Request failed: " + ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private boolean isSuccess(HttpResponse<String> response) {
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return true;
        }
        String body = response.body();
        System.err.printf("Request failed (%d): %s%n", status, extractMessage(body));
        return false;
    }

    private String extractMessage(String body) {
        if (body == null || body.isBlank()) {
            return "no response body";
        }
        try {
            JsonNode node = objectMapper.readTree(body);
            if (node.has("message")) {
                return node.get("message").asText();
            }
            return body;
        } catch (JsonProcessingException e) {
            return body;
        }
    }

    private Map<String, String> parseOptions(String[] args, int startIndex) {
        Map<String, String> options = new HashMap<>();
        for (int i = startIndex; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("--")) {
                throw new IllegalArgumentException("Invalid argument: " + arg);
            }
            String key = arg.substring(2);
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException("Missing value for " + arg);
            }
            String value = args[++i];
            options.put(key, value);
        }
        return options;
    }

    private String resolveBaseUrl(Map<String, String> options) {
        if (options.containsKey("baseUrl")) {
            return options.remove("baseUrl");
        }
        String envUrl = System.getenv("FUEL_API_BASE_URL");
        return envUrl != null && !envUrl.isBlank() ? envUrl : DEFAULT_BASE_URL;
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Failed to serialize request body");
        }
    }

    private <T> T readJson(String body, Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Failed to parse response body: " + ex.getMessage());
        }
    }

    private long parseLong(String value, String name) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(name + " must be a number");
        }
    }

    private double parseDouble(String value, String name) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(name + " must be a number");
        }
    }

    private void printUsage() {
        System.out.println("""
                Fuel Tracker CLI

                Commands:
                  create-car --brand <brand> --model <model> --year <year> [--baseUrl <url>]
                  add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer> [--baseUrl <url>]
                  fuel-stats --carId <id> [--baseUrl <url>]

                Defaults:
                  Base URL: http://localhost:8080 (override with --baseUrl or env FUEL_API_BASE_URL)
                """);
    }

    private record CarResponse(long id, String brand, String model, int year) {
    }

    private record FuelEntryResponse(double liters, double price, double odometer, String timestamp) {
    }

    private record FuelStatsResponse(double totalFuelLiters, double totalCost, double averageConsumptionPer100Km) {
    }

    @FunctionalInterface
    private interface ResponseHandler {
        void handle(HttpResponse<String> response);
    }
}
