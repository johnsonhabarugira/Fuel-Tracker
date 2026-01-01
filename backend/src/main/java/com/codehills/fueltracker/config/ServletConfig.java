package com.codehills.fueltracker.config;

import com.codehills.fueltracker.service.CarService;
import com.codehills.fueltracker.servlet.FuelStatsServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public FuelStatsServlet fuelStatsServlet(CarService carService, ObjectMapper objectMapper) {
        return new FuelStatsServlet(carService, objectMapper);
    }

    @Bean
    public ServletRegistrationBean<FuelStatsServlet> fuelStatsServletRegistration(FuelStatsServlet servlet) {
        ServletRegistrationBean<FuelStatsServlet> registrationBean =
                new ServletRegistrationBean<>(servlet, "/servlet/fuel-stats");
        registrationBean.setName("fuelStatsServlet");
        return registrationBean;
    }
}
