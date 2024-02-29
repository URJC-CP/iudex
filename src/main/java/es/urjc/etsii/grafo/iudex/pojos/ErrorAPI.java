package es.urjc.etsii.grafo.iudex.pojos;

public class ErrorAPI {
    private final String reason;

    public ErrorAPI(String reason) {
        this.reason = reason;
    }

    public ErrorAPI(Throwable t){
        this.reason = "%s: %s".formatted(t.getClass().getSimpleName(), t.getLocalizedMessage());
    }

    public String getReason() {
        return reason;
    }
}
