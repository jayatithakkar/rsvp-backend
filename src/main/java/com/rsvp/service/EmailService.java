package com.rsvp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * @author Jayati Thakkar
 * @version 3.0 (Native Java HTTP Client - No SDK required!)
 */
@Service
public class EmailService {

    @Value("${brevo.api.key:}")
    private String apiKey;

    @Value("${brevo.sender.email:}")
    private String senderEmail;

    public void sendConfirmation(String toEmail, List<String> guests) {
        String guestList = (guests != null && !guests.isEmpty()) ? String.join(", ", guests) : "Just you!";
        String subject = "RSVP Confirmation 💜";
//        String body = "Thank you for your RSVP!\n\nGuests:\n" + guestList + "\n\nWe look forward to celebrating with you!";
        String body = String.format(
                "Hi there,\n\n" +
                        "Thank you for your RSVP! We are so excited to celebrating with you!\n\n" +
                        "📅 Date: Sunday, May 31, 2026\n" +
                        "⏰ Time: 9:00 AM (Followed by lunch)\n" +
                        "📍 Location: Grand Empire Banquet And Convention Centre\n" +
                        "🗺️ Address: 100 Nexus Ave, Brampton, ON L6P 3R6\n\n" +
                        "Registered Guests: %s\n\n" +
                        "See you there!\n\nGuestlist : ",
                guestList
        );
        sendEmailViaBrevo(toEmail, subject, body);
    }

    public void sendEventReminder(String toEmail, List<String> guests) {
        String guestList = (guests != null && !guests.isEmpty()) ? String.join(", ", guests) : "Just you!";
        String subject = "Reminder: Krina's Baby Shower is Tomorrow!";

        String body = String.format(
                "Hi there,\n\n" +
                        "We are so excited to celebrate with you tomorrow! This is a quick reminder with the details for Krina's baby shower:\n\n" +
                        "📅 Date: Tomorrow, May 31, 2026\n" +
                        "⏰ Time: 9:00 AM (Followed by lunch)\n" +
                        "📍 Location: Grand Empire Banquet And Convention Centre\n" +
                        "🗺️ Address: 100 Nexus Ave, Brampton, ON L6P 3R6\n\n" +
                        "Registered Guests: %s\n\n" +
                        "See you there!",
                guestList
        );

        sendEmailViaBrevo(toEmail, subject, body);
    }

    // --- NATIVE HTTP REQUEST ---
    private void sendEmailViaBrevo(String toEmail, String subject, String textContent) {
        try {
            // 1. Create the JSON payload safely using Spring's built-in Jackson tool
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("email", senderEmail, "name", "Krina's Baby Shower"),
                    "to", List.of(Map.of("email", toEmail)),
                    "subject", subject,
                    "textContent", textContent
            );
            String jsonPayload = mapper.writeValueAsString(payload);

            // 2. Build the HTTP POST request to Brevo
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // 3. Send it using Java 11+ native HttpClient
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Check the results
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("✅ Email sent successfully via Native API to: " + toEmail);
            } else {
                System.err.println("❌ Brevo API Error: " + response.body());
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to send email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}