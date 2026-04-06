package com.rsvp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
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
            // Reading directly from the physical file
            InputStream serviceAccount = new FileInputStream("app/firebase-service-account.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized successfully from local file!");
            }

        } catch (Exception e) {
            System.err.println("❌ Firebase Init Error: Could not find firebase-service-account.json");
            e.printStackTrace();
        }
    }
}
