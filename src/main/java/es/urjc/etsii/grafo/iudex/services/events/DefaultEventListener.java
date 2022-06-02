package es.urjc.etsii.grafo.iudex.services.events;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import es.urjc.etsii.grafo.iudex.entities.JudgeEvent;

import java.util.logging.Logger;

public class DefaultEventListener  extends AbstractEventListener {
    private static Logger log = Logger.getLogger(DefaultEventListener.class.getName());

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final String eventPath = "/topic/events";


    /**
     * Create DefaultEventListener
     *
     * @param simpMessagingTemplate websocket messaging template
     * @param memoryEventStorage memory event storage
     */
    protected DefaultEventListener(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Store event in memory and send to websocket
     *
     * @param morkEvent Mork event
     */
    @JudgeEventListener
    public void processEvent(JudgeEvent judgeEvent){
        log.fine(String.format("Sending event to websocket path %s: %s", eventPath, judgeEvent));
        simpMessagingTemplate.convertAndSend(eventPath, judgeEvent);
    }
}
