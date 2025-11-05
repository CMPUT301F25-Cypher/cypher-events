package com.example.cypher_events.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin {
    private String Admin_name;
    private String Admin_email;
    private int Admin_phone_no;
    private boolean Admin_notificationsEnabled;
    private String Admin_status;
    private List<String> Admin_delEvents;
    private List<String> Admin_delProfiles;
        private List<String> Admin_delImages;
        private List<String> Admin_visitedEvents;
        private List<String> Admin_visitedProfiles;
        private List<String> Admin_visitedImages;
        private List<String> Admin_reviewedLogs;

    public String getAdmin_name() {
        return Admin_name;
    }

    public void setAdmin_name(String admin_name) {
        Admin_name = admin_name;
    }

    public String getAdmin_email() {
        return Admin_email;
    }

    public void setAdmin_email(String admin_email) {
        Admin_email = admin_email;
    }

    public int getAdmin_phone_no() {
        return Admin_phone_no;
    }

    public void setAdmin_phone_no(int admin_phone_no) {
        Admin_phone_no = admin_phone_no;
    }

    public boolean isAdmin_notificationsEnabled() {
        return Admin_notificationsEnabled;
    }

    public void setAdmin_notificationsEnabled(boolean admin_notificationsEnabled) {
        Admin_notificationsEnabled = admin_notificationsEnabled;
    }

    public String getAdmin_status() {
        return Admin_status;
    }

    public void setAdmin_status(String admin_status) {
        Admin_status = admin_status;
    }

    public List<String> getAdmin_delEvents() {
        return Admin_delEvents;
    }

    public void setAdmin_delEvents(List<String> admin_delEvents) {
        Admin_delEvents = admin_delEvents;
    }

    public List<String> getAdmin_delProfiles() {
        return Admin_delProfiles;
    }

    public void setAdmin_delProfiles(List<String> admin_delProfiles) {
        Admin_delProfiles = admin_delProfiles;
    }

    public List<String> getAdmin_delImages() {
        return Admin_delImages;
    }

    public void setAdmin_delImages(List<String> admin_delImages) {
        Admin_delImages = admin_delImages;
    }

    public List<String> getAdmin_visitedEvents() {
        return Admin_visitedEvents;
    }

    public void setAdmin_visitedEvents(List<String> admin_visitedEvents) {
        Admin_visitedEvents = admin_visitedEvents;
    }

    public List<String> getAdmin_visitedProfiles() {
        return Admin_visitedProfiles;
    }

    public void setAdmin_visitedProfiles(List<String> admin_visitedProfiles) {
        Admin_visitedProfiles = admin_visitedProfiles;
    }

    public List<String> getAdmin_visitedImages() {
        return Admin_visitedImages;
    }

    public void setAdmin_visitedImages(List<String> admin_visitedImages) {
        Admin_visitedImages = admin_visitedImages;
    }

    public List<String> getAdmin_reviewedLogs() {
        return Admin_reviewedLogs;
    }

    public void setAdmin_reviewedLogs(List<String> admin_reviewedLogs) {
        Admin_reviewedLogs = admin_reviewedLogs;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> Admin_firebase_info = new HashMap<>();

        Admin_firebase_info.put("Admin_name", Admin_name);
        Admin_firebase_info.put("Admin_email", Admin_email);
        Admin_firebase_info.put("Admin_phone_no", Admin_phone_no);
        Admin_firebase_info.put("Admin_notificationsEnabled", Admin_notificationsEnabled);
        Admin_firebase_info.put("Admin_status", Admin_status);
        Admin_firebase_info.put("Admin_delEvents", Admin_delEvents);
        Admin_firebase_info.put("Admin_delProfiles", Admin_delProfiles);
        Admin_firebase_info.put("Admin_delImages", Admin_delImages);
        Admin_firebase_info.put("Admin_visitedEvents", Admin_visitedEvents);
        Admin_firebase_info.put("Admin_visitedProfiles", Admin_visitedProfiles);
        Admin_firebase_info.put("Admin_visitedImages", Admin_visitedImages);
        Admin_firebase_info.put("Admin_reviewedLogs", Admin_reviewedLogs);

        return Admin_firebase_info;
    }

}
