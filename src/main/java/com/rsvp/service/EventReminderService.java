package com.rsvp.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.rsvp.model.Rsvp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventReminderService {

    @Autowired
    private EmailService emailService;

    private static final String COLLECTION = "rsvps";

    // Triggers exactly on May 30 at 9:00 AM in the Eastern time zone
    @Scheduled(cron = "0 0 9 30 5 *", zone = "America/Toronto")
    public void send24HourReminders() {
        System.out.println("Starting 24-hour reminder job for Krina's Baby Shower...");

        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                Rsvp rsvp = doc.toObject(Rsvp.class);

                // Check if they are a "Yes" (meaning they provided an email)
                if (rsvp.getEmail() != null && !rsvp.getEmail().trim().isEmpty()) {

                    // Send the reminder
                    emailService.sendEventReminder(
                            rsvp.getEmail(),
                            rsvp.getGuestNames()
                    );
                }
            }

            System.out.println("Finished sending all reminders.");

        } catch (Exception e) {
            System.err.println("Failed to execute scheduled reminder task: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
