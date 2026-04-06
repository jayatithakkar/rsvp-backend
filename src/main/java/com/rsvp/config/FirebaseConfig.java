package com.rsvp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Jayati Thakkar
 * @version 1.0
 */
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            // 1. Check Render's specific Secret File path first
            File file = new File("/etc/secrets/firebase-service-account.json");

            // 2. If it doesn't exist there, look in the current working directory (for local testing)
            if (!file.exists()) {
                file = new File("firebase-service-account.json");
            }

            if (!file.exists()) {
                throw new FileNotFoundException("Firebase config not found at /etc/secrets/ or local root");
            }

            System.out.println("✅ Loading Firebase config from: " + file.getAbsolutePath());

            InputStream serviceAccount = new FileInputStream(file);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized successfully!");
            }
        } catch (Exception e) {
            System.err.println("❌ Firebase Init Error: " + e.getMessage());
            // Do not let the app crash, but log the full error
            e.printStackTrace();
        }
    }
}
