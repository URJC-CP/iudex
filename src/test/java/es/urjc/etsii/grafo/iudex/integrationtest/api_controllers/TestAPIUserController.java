package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.api.v1.APIUserController;
import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.security.jwt.JwtRequestFilter;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    UserAndTeamService userAndTeamService;

    String baseUrl = "/API/v1/user";

    User user;

    @BeforeEach
    public void init() {
        user = new User("user", "user@test.com", "user", "test");
        user.setId(1L);
        user.setRoles(List.of("ROLE_USER", "ROLE_JUDGE"));

        when(userAndTeamService.getUserById(user.getId())).thenReturn(Optional.of(user));
    }

    @Test
    void addRoleToUser() throws Exception {
        checkRequests(
                put(String.format("%s/%s/role/%s", baseUrl, user.getId(), "admin")),
                HttpStatus.OK,
                List.of("ROLE_USER", "ROLE_JUDGE", "ROLE_ADMIN")
        );
        checkRequests(
                put(String.format("%s/%s/role/%s", baseUrl, user.getId(), "AdMiN")),
                HttpStatus.OK,
                List.of("ROLE_USER", "ROLE_JUDGE", "ROLE_ADMIN")
        );

        // User not found
        when(userAndTeamService.getUserById(2L)).thenReturn(Optional.empty());
        checkRequests(
                put(String.format("%s/%s/role/%s", baseUrl, 2L, "admin")),
                HttpStatus.NOT_FOUND,
                ""
        );

        // Tries to add user a non-existing role
        checkRequests(
                put(String.format("%s/%s/role/%s", baseUrl, user.getId(), "non_existing")),
                HttpStatus.BAD_REQUEST,
                ""
        );

        // User already has JUDGE role
        checkRequests(
                put(String.format("%s/%s/role/%s", baseUrl, user.getId(), "JUDGE")),
                HttpStatus.BAD_REQUEST,
                ""
        );
    }

    @Test
    void removeRoleFromUser() throws Exception {
        checkRequests(
                delete(String.format("%s/%s/role/%s", baseUrl, user.getId(), "JUDGE")),
                HttpStatus.OK,
                List.of("ROLE_USER")
        );
        checkRequests(
                delete(String.format("%s/%s/role/%s", baseUrl, user.getId(), "uSeR")),
                HttpStatus.OK,
                List.of("ROLE_JUDGE")
        );

        // User not found
        when(userAndTeamService.getUserById(2L)).thenReturn(Optional.empty());
        checkRequests(
                delete(String.format("%s/%s/role/%s", baseUrl, 2L, "judge")),
                HttpStatus.NOT_FOUND,
                ""
        );

        // Tries to add user a non-existing role
        checkRequests(
                delete(String.format("%s/%s/role/%s", baseUrl, user.getId(), "non_existing")),
                HttpStatus.BAD_REQUEST,
                ""
        );

        // User does not have admin role
        checkRequests(
                delete(String.format("%s/%s/role/%s", baseUrl, user.getId(), "admin")),
                HttpStatus.BAD_REQUEST,
                ""
        );
    }

    private void checkRequests(RequestBuilder requestBuilder, HttpStatus status, Object output) throws Exception {
        String outputJson = jsonConverter.convertObjectToJSON(output);

        String result = mockMvc.perform(requestBuilder)
                .andExpect(status().is(status.value()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(outputJson, result);
    }

}