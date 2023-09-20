package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.configuration.TestConfigSecurity;
import es.urjc.etsii.grafo.iudex.services.ResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = TestConfigSecurity.class
)
@AutoConfigureMockMvc
public class TestAPIAccessRoles {

    @Autowired
    private MockMvc mockMvc;

    private String ADMIN_ONLY_URL = "/API/v1/result/";

    @MockBean
    private ResultService resultService;

    @BeforeEach
    public void init() {
        when(resultService.getAllResults()).thenReturn(List.of());
    }

    @Test
    @WithUserDetails("admin")
    public void adminShouldAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get(ADMIN_ONLY_URL).with(jwt().authorities(new SimpleGrantedAuthority("ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("judge")
    public void judgeShouldBeDeniedAccessToAdminEndpoint() throws Exception {
        mockMvc.perform(get(ADMIN_ONLY_URL).with(jwt().authorities(new SimpleGrantedAuthority("JUDGE"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("user")
    public void userShouldBeDeniedAccessToAdminEndpoint() throws Exception {
        mockMvc.perform(get(ADMIN_ONLY_URL).with(jwt().authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void unauthorizedShouldBeRedirectedOnAdminEndpoint() throws Exception {
        mockMvc.perform(get(ADMIN_ONLY_URL).with(anonymous()))
                .andExpect(status().isTemporaryRedirect());
    }

}
