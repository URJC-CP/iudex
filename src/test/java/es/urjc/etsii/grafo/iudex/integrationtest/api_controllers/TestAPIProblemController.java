package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.controller.api.APIProblemController;
import es.urjc.etsii.grafo.iudex.entity.Contest;
import es.urjc.etsii.grafo.iudex.entity.Problem;
import es.urjc.etsii.grafo.iudex.entity.Team;
import es.urjc.etsii.grafo.iudex.pojo.ProblemAPI;
import es.urjc.etsii.grafo.iudex.pojo.ProblemString;
import es.urjc.etsii.grafo.iudex.service.ProblemService;
import es.urjc.etsii.grafo.iudex.util.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APIProblemController.class)
class TestAPIProblemController {
    private final JSONConverter jsonConverter = new JSONConverter();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProblemService problemService;
    private Contest contest;
    private Team owner;
    private Problem problem;

    @BeforeEach
    public void init() {
        contest = new Contest();
        contest.setId(101);
        contest.setNombreContest("elConcurso");
        contest.setDescripcion("concurso de prueba");

        owner = new Team();
        owner.setId(201);
        owner.setNombreEquipo("propietario");
        contest.setTeamPropietario(owner);

        problem = new Problem();
        problem.setId(244);
        problem.setNombreEjercicio("Ejercicio de prueba");
        problem.setEquipoPropietario(owner);

        when(problemService.getAllProblemas()).thenReturn(List.of(problem));
        when(problemService.getProblem(String.valueOf(problem.getId()))).thenReturn(Optional.of(problem));
    }

    @Test
    @DisplayName("Get All Problems")
    void testAPIGetProblems() throws Exception {
        String url = "/API/v1/problem";
        String result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();

        ProblemAPI problemAPI = problem.toProblemAPI();
        List<ProblemAPI> problems = List.of(problemAPI);
        assertEquals(jsonConverter.convertObjectToJSON(problems), result);
    }

    @Test
    @DisplayName("Get All Problems With Pagination")
    @Disabled("Get All Problems With Pagination - Averiguar como probar la paginación")
    void testAPIGetProblemsWithPagination() {
        fail("Not implemented yet!");
    }

    @Test
    @DisplayName("Get Selected Problem")
    void testAPIGetProblem() throws Exception {
        String goodProblem = String.valueOf(problem.getId());
        String badProblem = "745";

        String goodURL = "/API/v1/problem/" + goodProblem;
        String badURL = "/API/v1/problem/" + badProblem;

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = ""; // Status 404 with no body - "ERROR PROBLEM NOT FOUND";
        testGetProblem(badURL, status, salida);

        status = HttpStatus.OK;
        salida = jsonConverter.convertObjectToJSON(problem.toProblemAPI());
        testGetProblem(goodURL, status, salida);
    }

    private void testGetProblem(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
	/*
	  WARNING:
		no hay ningun metodo para pasar un objeto como parametro en MockMvc
		y el controlador no acepta JSON
	*/
    @DisplayName("Create problem Using a Problem Object")
    @Disabled("Create problem Using a Problem Object - Cannot be mocked")
    void testAPICreateProblem() throws Exception {
        fail("Could not send an instance as a param with mock");
    }

    @Test
    @DisplayName("Create Problem From Zip")
    @Disabled("Create Problem From Zip - Cannot be mocked")
    void testAPICreateProblemFromZip() throws Exception {
        fail("the Input Stream is created in the controller, so it will be different from the one specified in when");
    }

    @Test
    @DisplayName("Update Problem with Multiple Optional Params")
    @Disabled("Update Problem with Multiple Optional Params - Averiguar como pasar bytes[] del pdf como param")
    void testAPIUpdateProblem() throws Exception {
        String badProblem = "534";
        String goodProblem = String.valueOf(problem.getId());
        String badProblemName = "";
        String problemName = problem.getNombreEjercicio();
        String badTeam = "673";
        String goodTeam = String.valueOf(owner.getId());
        String timeout = "timeout";
        byte[] pdf = new byte[0];

        ProblemString ps = new ProblemString();
        String badURL = "/API/v1/problem/" + badProblem;
        String goodURL = "/API/v1/problem/" + goodProblem;

        String salida = ""; // Status 404 with no body - "ERROR PROBLEMID NOT FOUND";
        HttpStatus status = HttpStatus.NOT_FOUND;
        ps.setSalida(salida);
        when(problemService.updateProblemMultipleOptionalParams(badProblem, Optional.of(badProblemName), Optional.of(badTeam), Optional.of(pdf), Optional.of(timeout))).thenReturn(ps);
        testUpdateProblemMultipleOptions(badURL, badProblemName, badTeam, pdf, timeout, status, salida);

        when(problemService.updateProblemMultipleOptionalParams(badProblem, Optional.of(problemName), Optional.of(badTeam), Optional.of(pdf), Optional.of(timeout))).thenReturn(ps);
        testUpdateProblemMultipleOptions(badURL, problemName, badTeam, pdf, timeout, status, salida);

        when(problemService.updateProblemMultipleOptionalParams(badProblem, Optional.of(badProblemName), Optional.of(goodTeam), Optional.of(pdf), Optional.of(timeout))).thenReturn(ps);
        testUpdateProblemMultipleOptions(badURL, badProblemName, goodTeam, pdf, timeout, status, salida);

        when(problemService.updateProblemMultipleOptionalParams(badProblem, Optional.of(problemName), Optional.of(goodTeam), Optional.of(pdf), Optional.of(timeout))).thenReturn(ps);
        testUpdateProblemMultipleOptions(badURL, problemName, goodTeam, pdf, timeout, status, salida);

        salida = ""; // Status 404 with no body - "ERROR TEAM NOT FOUND";
        ps.setSalida(salida);
        problem.setNombreEjercicio("");
        when(problemService.updateProblemMultipleOptionalParams(goodProblem, Optional.of(badProblemName), Optional.of(badTeam), Optional.of(pdf), Optional.of(timeout))).thenReturn(ps);
        problem.setNombreEjercicio(problemName);
        testUpdateProblemMultipleOptions(goodURL, badProblemName, badTeam, pdf, timeout, status, salida);

        when(problemService.updateProblemMultipleOptionalParams(goodProblem, Optional.of(problemName), Optional.of(badTeam), Optional.of(pdf), Optional.of(timeout))).thenReturn(ps);
        testUpdateProblemMultipleOptions(goodURL, problemName, badTeam, pdf, timeout, status, salida);

        salida = "OK";
        ps.setProblem(problem);
        ps.setSalida(salida);
        status = HttpStatus.OK;
        when(problemService.updateProblemMultipleOptionalParams(goodProblem, Optional.of(problemName), Optional.of(goodTeam), Optional.of(pdf), Optional.of(timeout))).thenReturn(ps);
        salida = jsonConverter.convertObjectToJSON(problem.toProblemAPI());
        testUpdateProblemMultipleOptions(goodURL, problemName, goodTeam, pdf, timeout, status, salida);
    }

    private void testUpdateProblemMultipleOptions(String url, String problemName, String team, byte[] pdf, String timeout, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(put(url).characterEncoding("utf8").param("nombreProblema", problemName).param("teamId", team)
            //TODO: pass byte[]
            .param("pdf", String.valueOf(pdf)).param("timeout", timeout)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Update Problem From Zip")
    @Disabled("Update Problem From Zip - Cannot be mocked")
    void testAPIUpdateProblemFromZip() {
        fail("the Input Stream is created in the controller, so it will be different from the one specified in when");
    }

    @Test
    @DisplayName("Get PDF from Problem")
    void testAPIGetPdfFromProblem() throws Exception {
        String goodProblem = String.valueOf(problem.getId());
        String badProblem = "543";
        String goodURL = "/API/v1//problem/" + goodProblem + "/getPDF";
        String badURL = "/API/v1//problem/" + badProblem + "/getPDF";

        String salida = ""; // Status 404 with no body - "PROBLEM NOT FOUND";
        HttpStatus status = HttpStatus.NOT_FOUND;
        testGoToProblem(badURL, status, salida);

        salida = "";
        testGoToProblem(goodURL, status, salida);

        status = HttpStatus.OK;
        File pdf = new File("DOCKERS/entrada.in");
        byte[] contents = Files.readAllBytes(pdf.toPath());
        problem.setDocumento(contents);
        salida = Files.readString(pdf.toPath());
        testGoToProblem(goodURL, status, salida);
    }

    private void testGoToProblem(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Delete problem from all contests")
    void testAPIDeleteProblemFromALLContests() throws Exception {
        String badProblem = "546";
        String goodProblem = String.valueOf(problem.getId());

        String badURL = "/API/v1/problem/" + badProblem;
        String goodURL = "/API/v1/problem/" + goodProblem;

        String salida = ""; // Status 404 with no body - "PROBLEM NOT FOUND";
        HttpStatus status = HttpStatus.NOT_FOUND;
        when(problemService.deleteProblem(badProblem)).thenReturn(salida);
        testDeleteProblem(badURL, status, salida);

        salida = "OK";
        status = HttpStatus.OK;
        when(problemService.deleteProblem(goodProblem)).thenReturn(salida);
        salida = "";
        testDeleteProblem(goodURL, status, salida);
    }

    private void testDeleteProblem(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(delete(url)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }
}
