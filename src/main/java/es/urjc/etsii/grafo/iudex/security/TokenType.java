package es.urjc.etsii.grafo.iudex.security;

import java.time.Duration;

public enum TokenType {

    ACCESS(Duration.ofMinutes(5)),
    REFRESH(Duration.ofDays(7));

    /**
     * Token lifetime in seconds
     */
    public final Duration duration;

    TokenType(Duration duration) {
        this.duration = duration;
    }
}
