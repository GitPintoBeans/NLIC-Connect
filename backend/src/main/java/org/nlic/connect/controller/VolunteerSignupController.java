package org.nlic.connect.controller;

import org.nlic.connect.model.VolunteerSignup;
import org.nlic.connect.service.VolunteerSignupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/volunteer/signups")
@CrossOrigin(origins = "http://localhost:5173")
public class VolunteerSignupController {

    private final VolunteerSignupService signupService;

    public VolunteerSignupController(
            VolunteerSignupService signupService
    ) {
        this.signupService = signupService;
    }

    @PostMapping
    public ResponseEntity<?> createSignup(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long userId =
                    Long.valueOf(request.get("userId").toString());

            Long opportunityId =
                    Long.valueOf(request.get("opportunityId").toString());

            String notes = request.get("notes") != null
                    ? request.get("notes").toString()
                    : null;

            VolunteerSignup signup =
                    signupService.signUp(
                            userId,
                            opportunityId,
                            notes
                    );

            return ResponseEntity.ok(signup);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserSignups(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                signupService.getUserSignups(userId)
        );
    }
}