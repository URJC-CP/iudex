package es.urjc.etsii.grafo.iudex.utils;

import java.util.Optional;

public class Sanitizer {
    private static final String SANITIZE_REGEX = "[\n\r\t\f]/g";

    private Sanitizer() {}

    public static String removeLineBreaks(String s) {
        return s.trim().replaceAll(SANITIZE_REGEX, "_");
    }

    public static Optional<String> removeLineBreaks(Optional<String> op) {
        if (op.isPresent()) {
            op = Optional.of(removeLineBreaks(op.get()));
        }
        return op;
    }
}
