package es.urjc.etsii.grafo.iudex.spa;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SPAController {
    @PreAuthorize("permitAll()")
    @RequestMapping(value = {"/", "/admin/**", "/judge/**", "/student/**", "/admin", "/judge", "/student"})
    public String redirect() {
        return "forward:/index.html";
    }
}
