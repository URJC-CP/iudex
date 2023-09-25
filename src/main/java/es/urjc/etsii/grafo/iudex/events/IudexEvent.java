package es.urjc.etsii.grafo.iudex.events;

public class IudexEvent {

    private String text;

    public IudexEvent() {
    }

    public IudexEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
