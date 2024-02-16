package es.urjc.etsii.grafo.iudex.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SPAController {
    @PreAuthorize("permitAll()")
    @RequestMapping( "/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }
}
