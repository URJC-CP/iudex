package com.example.aplicacion.utils;

import java.util.Optional;

public class Sanitizer {
    public static String sanitize(String s) {
        return s.replaceAll("\r\n\t", "");
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
