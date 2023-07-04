package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.api.v1.APISubmissionController;
import es.urjc.etsii.grafo.iudex.services.ContestProblemService;
import es.urjc.etsii.grafo.iudex.services.ContestService;
import es.urjc.etsii.grafo.iudex.services.ProblemService;
import es.urjc.etsii.grafo.iudex.services.SubmissionService;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import es.urjc.etsii.grafo.iudex.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
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
    @MockBean
    private ContestProblemService contestProblemService;

    private ContestProblem contestProblem;
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

        contestProblem = new ContestProblem();
        contestProblem.setProblem(problem);
        contestProblem.setContest(contest);

        submission = new Submission();
        submission.setId(342);
        submission.setProblem(contestProblem.getProblem());
        submission.setContest(contestProblem.getContest());
        submission.setTeam(owner);
        submission.setLanguage(new Language());
        submission.setResults(new HashSet<>());
        contestProblem.getProblem().setSubmissions(Set.of(submission));

        when(contestService.getContestById(String.valueOf(contest.getId()))).thenReturn(Optional.of(contestProblem.getContest()));
        when(contestService.getAllContests()).thenReturn(List.of(contestProblem.getContest()));

        when(problemService.getAllProblemas()).thenReturn(List.of(contestProblem.getProblem()));
        when(problemService.getProblem(String.valueOf(problem.getId()))).thenReturn(Optional.of(contestProblem.getProblem()));

        when(submissionService.getSubmission(String.valueOf(submission.getId()))).thenReturn(Optional.of(submission));
        when(submissionService.getAllSubmissions()).thenReturn(List.of(submission));
        when(submissionService.getSubmissionFromProblem(contestProblem.getProblem())).thenReturn(Set.of(submission));
        when(submissionService.getSubmissionsFromContest(contestProblem.getContest())).thenReturn(Set.of(submission));
    }

    @Test
    @DisplayName("Get Submissions with problemId and/or contestId")
    void testAPIGetSubmissions() throws Exception {
        String goodProblem = String.valueOf(problem.getId());
        String badProblem = "312";
        String goodContest = String.valueOf(contest.getId());
        String badContest = "654";
        String url = "/API/v1/submissions/";

        String salida = "";
        HttpStatus status = HttpStatus.NOT_FOUND;
        testGetSubmissions(url, badContest, badProblem, status, salida);
        testGetSubmissions(url, badContest, goodProblem, status, salida);
        testGetSubmissions(url, goodContest, badProblem, status, salida);

        salida = "";
        testGetSubmissionsWithProblemId(url, badProblem, status, salida);

        salida = "";
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
    void testAPIGetAllSubmissions() throws Exception {
        String url = "/API/v1/submission/page";
        final int page = 1, size = 5;
        Pageable pageable = PageRequest.of(page, size);
        when(submissionService.getSubmissionsPage(pageable)).thenReturn(new PageImpl<>(List.of(submission)));
        mockMvc.perform(get(url).param("size", String.valueOf(size)).param("page", String.valueOf(page))).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Submission with Results")
    void testAPIGetSubmission() throws Exception {
        String badSubmission = "987";
        String goodSubmission = String.valueOf(submission.getId());
        String badURL = "/API/v1/submission/" + badSubmission;
        String goodURL = "/API/v1/submission/" + goodSubmission;

        String salida = "";
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
        fail("Not implemented yet");
    }

    @Test
    @DisplayName("Delete Submission")
    void testAPIDeleteSubmission() throws Exception {
        String badSubmission = "987";
        String goodSubmission = String.valueOf(submission.getId());
        String badURL = "/API/v1/submission/" + badSubmission;
        String goodURL = "/API/v1/submission/" + goodSubmission;

        String salida = "";
        HttpStatus status = HttpStatus.NOT_FOUND;
        when(submissionService.deleteSubmission(badSubmission)).thenReturn(salida);
        testDeleteSubmission(badURL, status, salida);

        salida = "";
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
