package es.urjc.etsii.grafo.iudex.services.events;


import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Async
@EventListener
public @interface JudgeEventListener {

}
