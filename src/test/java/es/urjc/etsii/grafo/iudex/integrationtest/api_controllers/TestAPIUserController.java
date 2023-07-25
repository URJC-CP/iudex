package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.api.v1.APIUserController;
import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.pojos.UserString;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestAPIUserController {

    @Autowired
    private WebApplicationContext context;

    private final JSONConverter jsonConverter = new JSONConverter();

    private MockMvc mockMvc;

    @MockBean
    UserAndTeamService userService;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void createUser() throws Exception {
        String url = "/API/v1/user";

        User user = new User("iudex", "iudex@example.org");

        UserString userString = new UserString();
        userString.setUser(user);
        userString.setSalida("OK");

        when(userService.crearUsuario(user.getNickname(), user.getEmail())).thenReturn(userString);

        String result = mockMvc.perform(post(url)
                        .param("username", user.getNickname())
                        .param("email", user.getEmail()))
                .andExpect(status().is(200)).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(jsonConverter.convertObjectToJSON(user.toUserAPI()), result);


        user.setNickname("iudex2");
        userString.setSalida("USER MAIL DUPLICATED");
        when(userService.crearUsuario(user.getNickname(), user.getEmail())).thenReturn(userString);

        mockMvc.perform(post(url)
                        .param("username", user.getNickname())
                        .param("email", user.getEmail()))
                .andExpect(status().is(404)).andDo(print()).andReturn().getResponse().getContentAsString();
    }

}