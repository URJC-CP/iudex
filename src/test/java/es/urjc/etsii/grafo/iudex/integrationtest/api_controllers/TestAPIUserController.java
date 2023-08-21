package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.api.v1.APIUserController;
import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.pojos.UserString;
import es.urjc.etsii.grafo.iudex.security.jwt.JwtRequestFilter;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = APIUserController.class, excludeFilters =
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtRequestFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class TestAPIUserController {

    private final JSONConverter jsonConverter = new JSONConverter();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserAndTeamService userService;

}