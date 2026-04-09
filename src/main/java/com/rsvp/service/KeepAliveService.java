package com.rsvp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class KeepAliveService {

    // Runs every 14 minutes (840,000 milliseconds)
    @Scheduled(fixedRate = 840000)
    public void pingSelf() {
        try {
            // Replace this with your exact Render URL + your dumb ping endpoint
//            String url = "https://rsvp-backend-1-3xsy.onrender.com/api/ping";
            String url = "https://rsvp-backend-1-3xsy.onrender.com/api/rsvp/health";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("⏰ Internal Keep-Alive Ping Status: " + response.statusCode());

        } catch (Exception e) {
            System.err.println("❌ Internal Keep-Alive Ping Failed: " + e.getMessage());
        }
    }
}