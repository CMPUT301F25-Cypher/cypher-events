package com.example.cypher_events.models;

import com.google.firebase.firestore.GeoPoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Event model representing an event in the lottery system.
 * Stores event details, registration info, and entrant management data.
 *
 * Outstanding issues: Need to implement QR code generation
 */
public class Event {
    private String eventId;
    private String organizerId;
    private String eventName;
    private String description;
    private String location;
    private Date eventDate;
    private Date registrationStartDate;
    private Date registrationEndDate;
    private String eventPosterUrl;
    private String qrCodeData;

    // Lottery and capacity management
    private Integer maxEntrants;           // null = unlimited (US 02.03.01)
    private int currentWaitingListCount;
    private int selectedCount;
    private int enrolledCount;
    private int cancelledCount;

    // Geolocation settings (US 02.02.03)
    private boolean geolocationRequired;

    // Lists of entrant IDs
    private List<String> waitingListIds;
    private List<String> selectedEntrantIds;
    private List<String> enrolledEntrantIds;
    private List<String> cancelledEntrantIds;
    private List<String> declinedEntrantIds;

    // Price and capacity
    private double price;
    private int capacity;

    /**
     * Default constructor required for Firestore
     */
    public Event() {
        this.geolocationRequired = false;
        this.currentWaitingListCount = 0;
        this.selectedCount = 0;
        this.enrolledCount = 0;
        this.cancelledCount = 0;
        this.waitingListIds = new ArrayList<>();
        this.selectedEntrantIds = new ArrayList<>();
        this.enrolledEntrantIds = new ArrayList<>();
        this.cancelledEntrantIds = new ArrayList<>();
        this.declinedEntrantIds = new ArrayList<>();
    }

    /**
     * Constructor with essential fields
     */
    public Event(String eventName, String description, String organizerId) {
        this();
        this.eventName = eventName;
        this.description = description;
        this.organizerId = organizerId;
    }

    // Getters and Setters

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public Date getRegistrationStartDate() { return registrationStartDate; }
    public void setRegistrationStartDate(Date date) { this.registrationStartDate = date; }

    public Date getRegistrationEndDate() { return registrationEndDate; }
    public void setRegistrationEndDate(Date date) { this.registrationEndDate = date; }

    public String getEventPosterUrl() { return eventPosterUrl; }
    public void setEventPosterUrl(String url) { this.eventPosterUrl = url; }

    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }

    // US 02.03.01 - Limit entrants
    public Integer getMaxEntrants() { return maxEntrants; }
    public void setMaxEntrants(Integer maxEntrants) { this.maxEntrants = maxEntrants; }

    public int getCurrentWaitingListCount() { return currentWaitingListCount; }
    public void setCurrentWaitingListCount(int count) { this.currentWaitingListCount = count; }

    public int getSelectedCount() { return selectedCount; }
    public void setSelectedCount(int count) { this.selectedCount = count; }

    public int getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(int count) { this.enrolledCount = count; }

    public int getCancelledCount() { return cancelledCount; }
    public void setCancelledCount(int count) { this.cancelledCount = count; }

    // US 02.02.03 - Enable/disable geolocation
    public boolean isGeolocationRequired() { return geolocationRequired; }
    public void setGeolocationRequired(boolean required) { this.geolocationRequired = required; }

    public List<String> getWaitingListIds() { return waitingListIds; }
    public void setWaitingListIds(List<String> ids) { this.waitingListIds = ids; }

    public List<String> getSelectedEntrantIds() { return selectedEntrantIds; }
    public void setSelectedEntrantIds(List<String> ids) { this.selectedEntrantIds = ids; }

    public List<String> getEnrolledEntrantIds() { return enrolledEntrantIds; }
    public void setEnrolledEntrantIds(List<String> ids) { this.enrolledEntrantIds = ids; }

    public List<String> getCancelledEntrantIds() { return cancelledEntrantIds; }
    public void setCancelledEntrantIds(List<String> ids) { this.cancelledEntrantIds = ids; }

    public List<String> getDeclinedEntrantIds() { return declinedEntrantIds; }
    public void setDeclinedEntrantIds(List<String> ids) { this.declinedEntrantIds = ids; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    /**
     * Checks if the waiting list has reached its maximum capacity
     * @return true if full, false otherwise or if unlimited
     */
    public boolean isWaitingListFull() {
        if (maxEntrants == null) {
            return false; // Unlimited
        }
        return currentWaitingListCount >= maxEntrants;
    }}

/**
 * Gets the available spots remai */