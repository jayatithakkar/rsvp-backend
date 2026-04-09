package com.rsvp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * @author Jayati Thakkar
 * @version 2.0 (Updated to use Brevo API)
 */
@Service
public class EmailService {

    // Pulls the API key from your environment variables
    @Value("${brevo.api.key}")
    private String apiKey;

    // The email address you verified in your Brevo account
    @Value("${brevo.sender.email}")
    private String senderEmail;

    @PostConstruct
    public void init() {
        // This connects your app to Brevo when Spring Boot starts up
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);
    }

    public void sendConfirmation(String toEmail, List<String> guests) {
        String guestList = (guests != null && !guests.isEmpty()) ? String.join(", ", guests) : "Just you!";
        String subject = "RSVP Confirmation 💜";
        String body = "Thank you for your RSVP!\n\nGuests:\n" + guestList + "\n\nWe look forward to celebrating with you!";

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

    // --- HELPER METHOD TO HANDLE THE API CALL ---
    private void sendEmailViaBrevo(String toEmail, String subject, String textContent) {
        try {
            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();

            // Set who is sending it
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail(senderEmail);
            sender.setName("Krina's Baby Shower");
            sendSmtpEmail.setSender(sender);

            // Set who is receiving it
            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(toEmail);
            sendSmtpEmail.setTo(Collections.singletonList(to));

            // Set content
            sendSmtpEmail.setSubject(subject);
            sendSmtpEmail.setTextContent(textContent); // We use TextContent to match your original formatting

            // Send the email
            apiInstance.sendTransacEmail(sendSmtpEmail);
            System.out.println("✅ Email sent successfully via Brevo to: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Failed to send email via Brevo to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}