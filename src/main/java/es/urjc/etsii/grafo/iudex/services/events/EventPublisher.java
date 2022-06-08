package es.urjc.etsii.grafo.iudex.services.events;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import es.urjc.etsii.grafo.iudex.entities.JudgeEvent;

import java.util.logging.Logger;

@Service
public class EventPublisher {
    private static final Logger log = Logger.getLogger(EventPublisher.class.getName());
    private static EventPublisher eventPublisher;

    /**
     * Disable event propagation
     */
    private boolean blockEvents = false;
    private EventPublisher instance;

    /**
     * Spring integration constructor
     *
     * @param publisher Spring ApplicationEventPublisher
     */
    protected EventPublisher(ApplicationEventPublisher publisher) {
        var eventInterceptor = new EventInterceptor(publisher);
        new Thread(eventInterceptor).start();
        eventPublisher = this;
    }

    /**
     * Get event publisher instance
     * @return event publisher instance
     */
    public static EventPublisher getInstance(){
        return EventPublisher.eventPublisher;
    }

    /**
     * Asynchronously send and process an event
     *
     * @param event Event to propagate
     * @return eventPlublisher.publishEvent
     */
    public Object publishEvent(JudgeEvent event) {
        if (blockEvents) {
            log.fine("Event system disabled: " + event);
            return event;
        }
        return instance.publishEvent(event);
    }


    /**
     * Block dispatch of all future events. All calls to publishEvent() will be ignored.
     */
    public void block() {
        this.blockEvents = true;
    }

    /**
     * Enable event dispatching.
     */
    public void unblock() {
        this.blockEvents = false;
    }

    private static class EventInterceptor implements Runnable {
        
        private final ApplicationEventPublisher destination;

        private EventInterceptor(ApplicationEventPublisher destination) {
            this.destination = destination;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    var event = EventPublisher.getInstance();
                    log.fine("Publishing event: " + event);
                    this.destination.publishEvent(event);
                    if (event instanceof EventPublisher) {
                        log.info("Stopping event interceptor thread");
                        return;
                    }
                } catch (Exception e) {
                    log.warning("Event interceptor interrupted, exiting thread. Events will stop being propagated");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
