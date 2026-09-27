package org.nlic.connect.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a simple health-check endpoint for the NLIC Connect backend.
 * This controller can be used to verify that the REST API is running
 * and accepting HTTP requests.
 *
 * @author James Pinto
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Returns the current status of the NLIC Connect backend.
     *
     * @return application status information
     */
    @GetMapping("/health")
    public Map<String, String> getHealthStatus() {

        // LinkedHashMap preserves the display order of the response fields.
        Map<String, String> status = new LinkedHashMap<>();

        status.put("status", "UP");
        status.put("application", "NLIC Connect");
        status.put("phase", "Milestone 4 - Development");

        return status;
    }
}