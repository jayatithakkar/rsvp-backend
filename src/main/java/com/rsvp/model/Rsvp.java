package com.rsvp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * @author Jayati Thakkar
 * @version 1.0
 */
@Data
@NoArgsConstructor // REQUIRED by Firebase to read data
public class Rsvp {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public List<String> getGuestNames() {
        return guestNames;
    }

    public void setGuestNames(List<String> guestNames) {
        this.guestNames = guestNames;
    }

    private String status;       // "yes" or "no"
    private String name;         // For people who can't make it
    private String email;        // For people attending
    private Integer guestCount;  // Use Integer so it can be safely null
    private List<String> guestNames;
}
