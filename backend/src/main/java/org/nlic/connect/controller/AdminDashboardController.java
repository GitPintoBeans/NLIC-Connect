package org.nlic.connect.controller;

import org.nlic.connect.dto.AdminDashboardSummary;
import org.nlic.connect.service.AdminDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only reporting API. SecurityConfig restricts this URL to ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {
    private final AdminDashboardService service;

    public AdminDashboardController(AdminDashboardService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public AdminDashboardSummary getSummary() {
        return service.getSummary();
    }
}
