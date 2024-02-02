package es.urjc.etsii.grafo.iudex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SPAController {
    @RequestMapping( "/{path:web[^.]*}")
    public String redirect() {
        return "forward:/web/index.html";
    }
}
