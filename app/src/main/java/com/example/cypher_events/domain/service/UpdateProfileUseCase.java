package com.example.cypher_events.domain.service;

import java.util.Map;

/**
 * Updates entrant profile fields in-place on an Entrant-shaped map
 * using the exact keys from Entrant.toMap():
 *  - "Entrant_name"
 *  - "Entrant_email"
 *  - "Entrant_phone"
 *
 * Partial updates supported: pass null to keep an existing value.
 */
public final class UpdateProfileUseCase {

    /**
     * Update profile fields directly on the given map.
     * @param entrantMap Entrant-shaped map (same structure as Entrant.toMap()).
     * @param name       new name, or null to keep current.
     * @param email      new email, or null to keep current.
     * @param phone      new phone (optional), or null to keep current.
     * @return short status message for UI.
     */
    public static String update(Map<String, Object> entrantMap,
                                String name,
                                String email,
                                String phone) {
        if (entrantMap == null) return "Invalid profile.";

        String n = (name  == null) ? null : name.trim();
        String e = (email == null) ? null : email.trim();
        String p = (phone == null) ? null : phone.trim();

        if (e != null && !basicEmail(e)) return "Invalid email.";
        if (n != null && n.length() == 0) return "Name cannot be empty.";

        if (n != null) entrantMap.put("Entrant_name", n);
        if (e != null) entrantMap.put("Entrant_email", e);
        if (p != null) entrantMap.put("Entrant_phone", p); // phone is optional, allow empty

        return "Profile updated.";
    }

    private static boolean basicEmail(String s) {
        int at = s.indexOf('@');
        int dot = s.lastIndexOf('.');
        return at > 0 && dot > at + 1 && dot < s.length() - 1;
    }

    private UpdateProfileUseCase() {}
}