package com.rsvp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author Jayati Thakkar
 * @version 1.0
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmation(String toEmail, List<String> guests) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("RSVP Confirmation 💜");

        String guestList = (guests != null && !guests.isEmpty()) ? String.join(", ", guests) : "Just you!";

        message.setText(
                "Thank you for your RSVP!\n\nGuests:\n" +
                        guestList +
                        "\n\nWe look forward to celebrating with you!"
        );

        mailSender.send(message);
    }

    public void sendEventReminder(String toEmail, List<String> guests) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reminder: Krina's Baby Shower is Tomorrow!");

        String guestList = (guests != null && !guests.isEmpty()) ? String.join(", ", guests) : "Just you!";

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

        message.setText(body);
        mailSender.send(message); // 👈 This was missing before!
    }
}
