package com.example.cypher_events.domain.service;

import java.util.Map;

public final class UpdateProfileUseCase {

    // Update profile fields directly on the given map.
    // entrantMap must match the structure produced by Entrant.toMap():
    //  - "Entrant_name"
    //  - "Entrant_email"
    //  - "Entrant_phone"
    // Partial updates are supported: pass null to keep an existing value.
    public static String update(
            Map<String, Object> entrantMap,
            String name,
            String email,
            String phone
    ) {

        // Validate map
        if (entrantMap == null) {
            return "Invalid profile.";
        }

        // Normalize inputs (trim whitespace, keep null as "no change")
        String n = (name  == null) ? null : name.trim();
        String e = (email == null) ? null : email.trim();
        String p = (phone == null) ? null : phone.trim();

        // Validate email format if provided
        if (e != null && !basicEmail(e)) {
            return "Invalid email.";
        }

        // Validate name if provided (cannot be empty string)
        if (n != null && n.isEmpty()) {
            return "Name cannot be empty.";
        }

        // Apply updates to map
        if (n != null) {
            entrantMap.put("Entrant_name", n);
        }

        if (e != null) {
            entrantMap.put("Entrant_email", e);
        }

        // Phone is optional; allow empty or null (null means "no change")
        if (p != null) {
            entrantMap.put("Entrant_phone", p);
        }

        return "Profile updated.";
    }

    // Very basic email validation: must contain '@' and a '.' after '@'
    private static boolean basicEmail(String s) {
        int at = s.indexOf('@');
        int dot = s.lastIndexOf('.');
        return at > 0 && dot > at + 1 && dot < s.length() - 1;
    }

    // Private constructor to prevent instantiation (utility class)
    private UpdateProfileUseCase() {
    }
}
