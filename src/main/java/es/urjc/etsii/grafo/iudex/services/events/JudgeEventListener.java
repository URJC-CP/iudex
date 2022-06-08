package es.urjc.etsii.grafo.iudex.services.events;


import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Async
@EventListener
@Service
public @interface JudgeEventListener {

}
