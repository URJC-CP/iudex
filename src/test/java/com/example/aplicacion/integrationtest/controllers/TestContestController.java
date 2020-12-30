package com.example.aplicacion.integrationtest.controllers;

import com.example.aplicacion.Controllers.standarControllers.BasicController;
import com.example.aplicacion.Entities.*;
import com.example.aplicacion.services.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WebMvcTest(BasicController.class)
public class TestContestController {
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

    Optional<Contest> contest;
    Optional<Problem> problem;
    Optional<Submission> submission;
    Optional<Team> team;
    Optional<User> user;

    @BeforeAll
    public void setUp(){
        contest = Optional.of(new Contest());
        contest.get().setNombreContest("elConcurso");
        contest.get().setDescripcion("concurso de prueba");
        when(contestService.getContest(String.valueOf(contest.get().getId()))).thenReturn(contest);

        problem = Optional.of(new Problem());
        problem.get().setNombreEjercicio("elEjercicio");
        when(problemService.getProblem(String.valueOf(problem.get().getId()))).thenReturn(problem);

        team = Optional.of(new Team());
        team.get().setNombreEquipo("elEquipo");
        when(teamService.getTeamFromId(String.valueOf(team.get().getId()))).thenReturn(team);
        when(teamService.getTeamByNick(team.get().getNombreEquipo())).thenReturn(team);

        user = Optional.of(new User());
        user.get().setNickname("elUsuario");
        user.get().setEmail("example@example.com");
        when(userService.getUserById(user.get().getId())).thenReturn(user);

        submission = Optional.of(new Submission());
        when(submissionService.getSubmission(String.valueOf(submission.get().getId()))).thenReturn(submission);

    }

    @Test
    public void testGoToContest(){

    }
}
