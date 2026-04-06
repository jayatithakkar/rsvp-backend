package com.rsvp.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.rsvp.model.Rsvp;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author Jayati Thakkar
 * @version 1.0
 */
@Service
public class RsvpService {

    private static final String COLLECTION = "rsvps";

    public String saveRsvp(Rsvp rsvp) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        String documentId;

        // If they have an email (Yes RSVP), use it as the ID to prevent duplicates
        if (rsvp.getEmail() != null && !rsvp.getEmail().trim().isEmpty()) {
            documentId = rsvp.getEmail().toLowerCase();
        } else {
            // If they said "No" (No email), generate a random ID for them
            documentId = UUID.randomUUID().toString();
        }

        DocumentReference docRef = db.collection(COLLECTION).document(documentId);
        rsvp.setId(documentId);

        try {
            // Use .create() to fail if the email already exists
            ApiFuture<WriteResult> result = docRef.create(rsvp);
            result.get(); // Wait for confirmation
        } catch (ExecutionException e) {
            // Translate the Firebase error into our Custom error for the Controller
            if (e.getMessage() != null && e.getMessage().contains("ALREADY_EXISTS")) {
                throw new IllegalStateException("EMAIL_ALREADY_EXISTS");
            }
            throw e;
        }

        return documentId;
    }
}
