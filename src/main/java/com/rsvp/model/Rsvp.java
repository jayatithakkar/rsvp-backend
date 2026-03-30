package com.rsvp.model;

import lombok.Data;
import java.util.List;

/**
 * @author Jayati Thakkar
 * @version 1.0
 */
@Data
public class Rsvp {

    private String id;
    private int guestCount;
    private List<String> guestNames;
    private String email;
}