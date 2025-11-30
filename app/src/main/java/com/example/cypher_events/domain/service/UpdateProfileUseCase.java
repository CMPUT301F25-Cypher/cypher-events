package com.example.cypher_events.domain.service;

import java.util.Map;

public final class UpdateProfileUseCase {


    public static String update(
            Map<String, Object> entrantMap,
            String name,
            String email,
            String phone
    ) {
        /**
         * @param entrantMap mutable entrant profile map
         * @param name optional name to set
         * @param email optional email to set
         * @param phone optional phone to set
         * @return status message indicating result
         */


        if (entrantMap == null) {
            return "Invalid profile.";
        }


        String n = (name  == null) ? null : name.trim();
        String e = (email == null) ? null : email.trim();
        String p = (phone == null) ? null : phone.trim();


        if (e != null && !basicEmail(e)) {
            return "Invalid email.";
        }


        if (n != null && n.isEmpty()) {
            return "Name cannot be empty.";
        }


        if (n != null) {
            entrantMap.put("Entrant_name", n);
        }

        if (e != null) {
            entrantMap.put("Entrant_email", e);
        }


        if (p != null) {
            entrantMap.put("Entrant_phone", p);
        }

        return "Profile updated.";
    }


    private static boolean basicEmail(String s) {
        /**
         * @param s email string to validate
         * @return true if looks like a basic email
         */
        int at = s.indexOf('@');
        int dot = s.lastIndexOf('.');
        return at > 0 && dot > at + 1 && dot < s.length() - 1;
    }


    private UpdateProfileUseCase() {
    }
}
