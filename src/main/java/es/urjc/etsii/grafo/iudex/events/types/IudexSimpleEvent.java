package es.urjc.etsii.grafo.iudex.events.types;

public class IudexSimpleEvent implements IudexEvent {

    private String text;

    public IudexSimpleEvent() {
    }

    public IudexSimpleEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
