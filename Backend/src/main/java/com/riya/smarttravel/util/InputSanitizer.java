package com.riya.smarttravel.util;

public class InputSanitizer {

    private InputSanitizer() {
    }

    public static String normalize(String input) {
        if (input == null) {
            return null;
        }
        String cleaned = input.trim().replaceAll("\\s+", " ");
        return cleaned.isEmpty() ? null : cleaned;
    }
}