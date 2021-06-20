package com.example.aplicacion.utils;

import java.util.Optional;

public class Sanitizer {
    private static final String SANITIZE_REGEX = "[\n\r\t\f]/g";

    private Sanitizer() {}

    public static String sanitize(String s) {
        return s.trim().replaceAll(SANITIZE_REGEX, "_");
    }

    public static Optional<String> sanitize(Optional<String> op) {
        if (op.isPresent()) {
            op = Optional.of(sanitize(op.get()));
        }
        return op;
    }

    public static String[] sanitize(String[] sList) {
        for (int i = 0; i < sList.length; i++) {
            sList[i] = sanitize(sList[i]);
        }
        return sList;
    }
}
