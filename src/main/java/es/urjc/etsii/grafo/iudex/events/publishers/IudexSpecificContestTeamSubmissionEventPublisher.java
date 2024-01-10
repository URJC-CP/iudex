package es.urjc.etsii.grafo.iudex.events.publishers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class IudexSpecificContestTeamSubmissionEventPublisher extends IudexEventPublisher {

    public IudexSpecificContestTeamSubmissionEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        super(simpMessagingTemplate, "/specific/contests/%s/teams/%s/submissions");
    }
}
