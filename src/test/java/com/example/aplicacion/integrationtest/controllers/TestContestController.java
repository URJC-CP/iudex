package com.example.aplicacion.integrationtest.controllers;

import com.example.aplicacion.Controllers.standarControllers.ContestController;
import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Repository.ContestRepository;
import com.example.aplicacion.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestContestController {
    @InjectMocks
    ContestController contestController;
    @MockBean
    SubmissionService submissionService;
    @MockBean
    ProblemService problemService;
    @MockBean
    LanguageService languageService;
    @MockBean
    ContestService contestService;
    @MockBean
    UserService userService;
    @MockBean
    TeamService teamService;
    Contest contest;
    Problem problem;
    Submission submission;
    Team team;
    User user;
    @Autowired
    ContestRepository contestRepository;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() {

    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        contest = new Contest();
        contest.setId(101);
        contest.setNombreContest("elConcurso");
        contest.setDescripcion("concurso de prueba");
        when(contestService.getContest(String.valueOf(contest.getId()))).thenReturn(Optional.of(contest));

        problem = new Problem();
        problem.setId(102);
        problem.setNombreEjercicio("elEjercicio");
        when(problemService.getProblem(String.valueOf(problem.getId()))).thenReturn(Optional.of(problem));

        team = new Team();
        team.setId(103);
        team.setEsUser(false);
        team.setNombreEquipo("elEquipo");
        when(teamService.getTeamFromId(String.valueOf(team.getId()))).thenReturn(Optional.of(team));
        when(teamService.getTeamByNick(team.getNombreEquipo())).thenReturn(Optional.of(team));

        user = new User();
        user.setId(104);
        user.setNickname("elUsuario");
        user.setEmail("example@example.com");
        when(userService.getUserById(user.getId())).thenReturn(Optional.of(user));

        submission = new Submission();
        submission.setId(105);
        when(submissionService.getSubmission(String.valueOf(submission.getId()))).thenReturn(Optional.of(submission));
    }

    @Test
    public void getContestById() {
        assertThat(contestService.getContest(String.valueOf(contest.getId())), equalTo(Optional.of(contest)));
    }

    
    @Test
    public void testGoToContest() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/goToContest/" + contest.getId(), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        contestRepository.save(contest);
        response = restTemplate.getForEntity("http://localhost:" + port + "/goToContest/" + contest.getId(), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void testGetLanguage() {
        System.out.println(Arrays.toString(languageService.getNLanguages().toArray()));
    }
}
