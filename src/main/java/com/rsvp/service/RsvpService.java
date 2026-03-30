package com.rsvp.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.rsvp.model.Rsvp;
import org.springframework.stereotype.Service;

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

        // 1. Use the email (lowercased) as the guaranteed unique Document ID
        String documentId = rsvp.getEmail().toLowerCase();
        DocumentReference docRef = db.collection(COLLECTION).document(documentId);

        rsvp.setId(documentId);

        // 2. Use .create() instead of .set().
        // If a document with this email already exists, .create() will throw an exception.
        ApiFuture<WriteResult> result = docRef.create(rsvp);

        // 3. We MUST call .get() to wait for Firebase to confirm the write succeeded
        result.get();

        return documentId;
    }
}