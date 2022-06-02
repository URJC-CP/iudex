package es.urjc.etsii.grafo.iudex.services.events;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Inherited
public @interface InheritedComponent {

}
