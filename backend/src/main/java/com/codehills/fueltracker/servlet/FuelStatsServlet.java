package com.codehills.fueltracker.servlet;

import com.codehills.fueltracker.dto.FuelStatsResponse;
import com.codehills.fueltracker.exception.BadRequestException;
import com.codehills.fueltracker.exception.ResourceNotFoundException;
import com.codehills.fueltracker.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FuelStatsServlet extends HttpServlet {

    private final CarService carService;
    private final ObjectMapper objectMapper;

    public FuelStatsServlet(CarService carService, ObjectMapper objectMapper) {
        this.carService = carService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String carIdParam = req.getParameter("carId");
        if (carIdParam == null || carIdParam.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\":\"carId is required\"}");
            return;
        }

        long carId;
        try {
            carId = Long.parseLong(carIdParam);
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\":\"carId must be a number\"}");
            return;
        }

        try {
            FuelStatsResponse stats = carService.getFuelStats(carId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(stats));
        } catch (ResourceNotFoundException ex) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(objectMapper.writeValueAsString(new Error(ex.getMessage())));
        } catch (BadRequestException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(new Error(ex.getMessage())));
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(objectMapper.writeValueAsString(new Error("Unexpected error: " + ex.getMessage())));
        }
    }

    private record Error(String message) {
    }
}
