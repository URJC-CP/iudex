package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.api.v1.APIAdminController;
import es.urjc.etsii.grafo.iudex.entities.Result;
import es.urjc.etsii.grafo.iudex.entities.Sample;
import es.urjc.etsii.grafo.iudex.security.JwtRequestFilter;
import es.urjc.etsii.grafo.iudex.services.ResultService;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;
import es.urjc.etsii.grafo.iudex.services.UserService;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = APIAdminController.class, excludeFilters =
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtRequestFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class TestAPIAdminController {
    private final JSONConverter jsonConverter = new JSONConverter();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ResultService resultService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserAndTeamService userAndTeamService;

    private Result result;

    @BeforeEach
    public void init() {
        result = new Result();
        result.setId(564);
        result.setFileName("Resultado de prueba");

        Sample sample = new Sample(756, "Datos de prueba", "Probando", "Probando", true);
        result.setSample(sample);

        when(resultService.getResult(String.valueOf(result.getId()))).thenReturn(result);
    }

    @Test
    @DisplayName("Get Result")
    void testAPIGetResult() throws Exception {
        String badResult = "654";
        String goodResult = String.valueOf(result.getId());
        String badURL = "/API/v1/result/" + badResult;
        String goodURL = "/API/v1/result/" + goodResult;

        String salida = "";
        HttpStatus status = HttpStatus.NOT_FOUND;
        testGetResult(badURL, status, salida);

        salida = jsonConverter.convertObjectToJSON(result);
        status = HttpStatus.OK;
        testGetResult(goodURL, status, salida);
    }

    private void testGetResult(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Get All Results")
    void testAPIGetAllResults() throws Exception {
        String url = "/API/v1/result";

        String salida = "";
        HttpStatus status = HttpStatus.NOT_FOUND;
        when(resultService.getAllResults()).thenReturn(null);
        testGetResult(url, status, salida);

        salida = jsonConverter.convertObjectToJSON(List.of(result));
        status = HttpStatus.OK;
        when(resultService.getAllResults()).thenReturn(List.of(result));
        testGetResult(url, status, salida);
    }
}
