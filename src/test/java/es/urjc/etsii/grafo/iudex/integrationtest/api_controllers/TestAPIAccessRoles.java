package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.services.ResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TestAPIAccessRoles {

    @Autowired
    private MockMvc mockMvc;

    private final String ADMIN_ONLY_URL = "/API/v1/result";

    @MockBean
    private ResultService resultService;

    @BeforeEach
    public void init() {
        when(resultService.getAllResults()).thenReturn(List.of());
    }

    @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    void adminShouldAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get(ADMIN_ONLY_URL)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="judge",roles={"USER","JUDGE"})
    void judgeShouldBeDeniedAccessToAdminEndpoint() throws Exception {
        mockMvc.perform(get(ADMIN_ONLY_URL)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="user")
    void userShouldBeDeniedAccessToAdminEndpoint() throws Exception {
        mockMvc.perform(get(ADMIN_ONLY_URL)).andExpect(status().isForbidden());
    }

    @Test
    void unauthorizedShouldBeRedirectedOnAdminEndpoint() throws Exception {
        mockMvc.perform(get(ADMIN_ONLY_URL)).andExpect(status().isUnauthorized());
    }

}
