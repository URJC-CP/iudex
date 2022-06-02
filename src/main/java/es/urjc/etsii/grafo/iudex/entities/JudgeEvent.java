package es.urjc.etsii.grafo.iudex.entities;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.ApplicationEvent;

import es.urjc.etsii.grafo.iudex.services.events.EventPublisher;

public class JudgeEvent extends ApplicationEvent{

    private static AtomicInteger nextEventId = new AtomicInteger(0);

    private final int eventId;
    private final String problemName;


    public JudgeEvent() {
        super(EventPublisher.class);
        this.eventId = nextEventId.getAndIncrement();
        this.problemName = Thread.currentThread().getName();
    }

}
