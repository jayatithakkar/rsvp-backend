package com.rsvp.controller;

import com.rsvp.model.Rsvp;
import com.rsvp.service.EmailService;
import com.rsvp.service.RsvpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Jayati Thakkar
 * @version 1.0
 */
@RestController
@RequestMapping("/api/rsvp")
@CrossOrigin(origins = "https://krineel-babyshower.web.app")
public class RsvpController {

    @Autowired
    private RsvpService rsvpService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Awake");
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> createRsvp(@RequestBody Rsvp rsvp) {
        try {
            // This will throw IllegalStateException if the email exists
            String id = rsvpService.saveRsvp(rsvp);

            // Send email in the background so the user doesn't have to wait
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendConfirmation(
                            rsvp.getEmail(),
                            rsvp.getGuestNames()
                    );
                } catch (Exception e) {
                    System.err.println("Background email task failed: " + e.getMessage());
                }
            });

            Map<String, String> response = new HashMap<>();
            response.put("id", id);
            response.put("message", "RSVP submitted successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            // 👇 Catch our specific duplicate email exception
            if ("EMAIL_ALREADY_EXISTS".equals(e.getMessage())) {
                Map<String, String> conflictResponse = new HashMap<>();
                conflictResponse.put("error", "An RSVP with this email address has already been submitted.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictResponse);
            }

            // Fallback for other illegal states
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid request data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error saving RSVP");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}