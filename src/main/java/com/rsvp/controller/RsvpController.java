package com.rsvp.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.rsvp.model.Rsvp;
import com.rsvp.service.EmailService;
import com.rsvp.service.RsvpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Jayati Thakkar
 * @version 1.0
 */
@RestController
@RequestMapping("/api/rsvp")
public class RsvpController {

    @Autowired
    private RsvpService rsvpService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Awake");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Rsvp>> getAllRsvps() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection("rsvps").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            List<Rsvp> allRsvps = new ArrayList<>();
            for (QueryDocumentSnapshot doc : documents) {
                allRsvps.add(doc.toObject(Rsvp.class));
            }
            return ResponseEntity.ok(allRsvps);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> createRsvp(@RequestBody Rsvp rsvp) {
        try {
            String id = rsvpService.saveRsvp(rsvp);

            // Only send confirmation email if they said "Yes" and provided an email
            if ("yes".equalsIgnoreCase(rsvp.getStatus()) && rsvp.getEmail() != null) {
                CompletableFuture.runAsync(() -> {
                    try {
                        emailService.sendConfirmation(rsvp.getEmail(), rsvp.getGuestNames());
                    } catch (Exception e) {
                        System.err.println("Background email task failed: " + e.getMessage());
                    }
                });
            }

            Map<String, String> response = new HashMap<>();
            response.put("id", id);
            response.put("message", "RSVP submitted successfully");
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            if ("EMAIL_ALREADY_EXISTS".equals(e.getMessage())) {
                Map<String, String> conflictResponse = new HashMap<>();
                conflictResponse.put("error", "An RSVP with this email address has already been submitted.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictResponse);
            }
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
