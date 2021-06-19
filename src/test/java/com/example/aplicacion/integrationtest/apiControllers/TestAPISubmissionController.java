package com.example.aplicacion.integrationtest.apiControllers;

import com.example.aplicacion.Controllers.apiControllers.APISubmissionController;
import com.example.aplicacion.Entities.*;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import com.example.aplicacion.utils.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APISubmissionController.class)
class TestAPISubmissionController {
    private final JSONConverter jsonConverter = new JSONConverter();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SubmissionService submissionService;
    @MockBean
    private ContestService contestService;
    @MockBean
    private ProblemService problemService;

    private Contest contest;
    private Problem problem;
    private Submission submission;

    @BeforeEach
    public void init() {
        contest = new Contest();
        contest.setId(101);
        contest.setNombreContest("elConcurso");
        contest.setDescripcion("concurso de prueba");

        Team owner = new Team();
        owner.setId(201);
        owner.setNombreEquipo("propietario");
        contest.setTeamPropietario(owner);

        problem = new Problem();
        problem.setId(244);
        problem.setNombreEjercicio("Ejercicio de prueba");
        problem.setEquipoPropietario(owner);
        contest.addProblem(problem);

        submission = new Submission();
        submission.setId(342);
        submission.setProblema(problem);
        submission.setContest(contest);
        submission.setTeam(owner);
        submission.setLanguage(new Language());
        submission.setResults(new HashSet<>());
        problem.setSubmissions(Set.of(submission));

        when(contestService.getContestById(String.valueOf(contest.getId()))).thenReturn(Optional.of(contest));
        when(contestService.getAllContests()).thenReturn(List.of(contest));

        when(problemService.getAllProblemas()).thenReturn(List.of(problem));
        when(problemService.getProblem(String.valueOf(problem.getId()))).thenReturn(Optional.of(problem));

        when(submissionService.getSubmission(String.valueOf(submission.getId()))).thenReturn(Optional.of(submission));
        when(submissionService.getAllSubmissions()).thenReturn(List.of(submission));
        when(submissionService.getSubmissionFromProblem(problem)).thenReturn(Set.of(submission));
        when(submissionService.getSubmissionsFromContest(contest)).thenReturn(Set.of(submission));
    }

    @Test
    @DisplayName("Get Submissions with problemId and/or contestId")
    void testAPIGetSubmissions() throws Exception {
        String goodProblem = String.valueOf(problem.getId());
        String badProblem = "312";
        String goodContest = String.valueOf(contest.getId());
        String badContest = "654";
        String url = "/API/v1/submissions/";

        String salida = "Problem or contest not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        testGetSubmissions(url, badContest, badProblem, status, salida);
        testGetSubmissions(url, badContest, goodProblem, status, salida);
        testGetSubmissions(url, goodContest, badProblem, status, salida);

        salida = "Problem not found";
        testGetSubmissionsWithProblemId(url, badProblem, status, salida);

        salida = "CONTEST NOT FOUND";
        testGetSubmissionsWithContestId(url, badContest, status, salida);

        //salida = "OK";
        status = HttpStatus.OK;
        salida = jsonConverter.convertObjectToJSON(List.of(submission.toSubmissionAPI()));
        testGetSubmissionsWithProblemId(url, goodProblem, status, salida);
        testGetSubmissionsWithContestId(url, goodContest, status, salida);

        // return all submissions if there are no contestId and problemId
        String result = mockMvc.perform(get(url)).andExpect(status().is(200)).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    private void testGetSubmissions(String url, String contest, String problem, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url).param("contestId", contest).param("problemId", problem)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    private void testGetSubmissionsWithProblemId(String url, String problem, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url).param("problemId", problem)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    private void testGetSubmissionsWithContestId(String url, String contest, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url).param("contestId", contest)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Get All Submissions with Pagination")
    @Disabled("Get All Submissions with Pagination - Not implemented yet!")
    void testAPIGetAllSubmissions() {
        //String badProblem;
        //String goodProblem;
        //String badContest;
        //String goodContest;
    }

    @Test
    @DisplayName("Get Submission with Results")
    void testAPIGetSubmission() throws Exception {
        String badSubmission = "987";
        String goodSubmission = String.valueOf(submission.getId());
        String badURL = "/API/v1/submission/" + badSubmission;
        String goodURL = "/API/v1/submission/" + goodSubmission;

        String salida = "SUBMISSION NOT FOUND";
        HttpStatus status = HttpStatus.NOT_FOUND;
        testGetSubmission(badURL, status, salida);

        salida = jsonConverter.convertObjectToJSON(submission.toSubmissionAPIFull());
        status = HttpStatus.OK;
        testGetSubmission(goodURL, status, salida);
    }

    private void testGetSubmission(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Add Submission to a Problem and Contest")
    @Disabled("Create Submission - Not implemented yet!")
    void testAPICreateSubmission() {
    }

    @Test
    @DisplayName("Delete Submission")
    void testAPIDeleteSubmission() throws Exception {
        String badSubmission = "987";
        String goodSubmission = String.valueOf(submission.getId());
        String badURL = "/API/v1/submission/" + badSubmission;
        String goodURL = "/API/v1/submission/" + goodSubmission;

        String salida = "SUBMISSION NOT FOUND";
        HttpStatus status = HttpStatus.NOT_FOUND;
        when(submissionService.deleteSubmission(badSubmission)).thenReturn(salida);
        testDeleteSubmission(badURL, status, salida);

        salida = "SUBMISSION IS FROM PROBLEM VALIDATOR YOU CANT DELETE IT FROM HERE. IT CAN ONLY BE DELETED BY DELETING THE PROBLEM";
        when(submissionService.deleteSubmission(goodSubmission)).thenReturn(salida);
        submission.setEsProblemValidator(true);
        testDeleteSubmission(goodURL, status, salida);

        salida = "OK";
        status = HttpStatus.OK;
        submission.setEsProblemValidator(false);
        when(submissionService.deleteSubmission(goodSubmission)).thenReturn(salida);
        salida = "";
        testDeleteSubmission(goodURL, status, salida);
    }

    private void testDeleteSubmission(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(delete(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }
}
