package com.example.cypher_events.models;

/**
 * User model representing a user in the system
 * Users are identified by device ID (no username/password)
 *
 * Outstanding issues: None
 */
public class User {
    private String userId;
    private String deviceId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private boolean isOrganizer;
    private boolean isAdmin;

    /**
     * Default constructor required for Firestore
     */
    public User() {
        this.isOrganizer = false;
        this.isAdmin = false;
    }

    /**
     * Constructor with device ID
     */
    public User(String deviceId) {
        this();
        this.deviceId = deviceId;
        this.userId = deviceId; // Use device ID as user ID
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String url) { this.profileImageUrl = url; }

    public boolean isOrganizer() { return isOrganizer; }
    public void setOrganizer(boolean organizer) { isOrganizer = organizer; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
}