package com.example.aplicacion.integrationtest.apiControllers;

import com.example.aplicacion.Controllers.apiControllers.APIAdminController;
import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Entities.Sample;
import com.example.aplicacion.services.ResultService;
import com.example.aplicacion.utils.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APIAdminController.class)
class TestAPIAdminController {
    private final JSONConverter jsonConverter = new JSONConverter();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ResultService resultService;

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

        String salida = "RESULT NOT FOUND";
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
        String url = "/API/v1/result/";

        String salida = "RESULT NOT FOUND";
        HttpStatus status = HttpStatus.NOT_FOUND;
        when(resultService.getAllResults()).thenReturn(null);
        testGetResult(url, status, salida);

        salida = jsonConverter.convertObjectToJSON(List.of(result));
        status = HttpStatus.OK;
        when(resultService.getAllResults()).thenReturn(List.of(result));
        testGetResult(url, status, salida);
    }
}
