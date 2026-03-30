package com.rsvp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Jayati Thakkar
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class RsvpMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsvpMainApplication.class, args);
    }
}