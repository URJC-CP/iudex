package com.example.aplicacion.integrationtest.controllers;

import com.example.aplicacion.Controllers.standarControllers.ContestController;
import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Repository.ContestRepository;
import com.example.aplicacion.Repository.TeamRepository;
import com.example.aplicacion.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.management.modelmbean.ModelMBean;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ContestController.class)
public class TestContestController {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ContestService contestService;
    @MockBean
    SubmissionService submissionService;
    @MockBean
    ProblemService problemService;
    @MockBean
    LanguageService languageService;
    @MockBean
    UserService userService;
    @MockBean
    TeamService teamService;

    Contest contest;

    @BeforeEach
    public void setUp() {

    }

    @BeforeEach
    public void init() {
        contest = new Contest();
        contest.setId(101);
        contest.setNombreContest("elConcurso");
        contest.setDescripcion("concurso de prueba");
        when(contestService.getContest(String.valueOf(contest.getId()))).thenReturn(Optional.of(contest));
    }

    @Test
    public void getContestById() {
        assertThat(contestService.getContest(String.valueOf(contest.getId())), equalTo(Optional.of(contest)));
    }

    @Test
    public void testGoToContest() throws Exception {
        assertThat(mockMvc.perform(get("/goToContest/"+523+"/"))
                .andReturn().getResponse().getStatus(), equalTo(404));
        assertThat(mockMvc.perform(get("/goToContest/"+contest.getId()+"/"))
                .andReturn().getResponse().getStatus(), equalTo(200));
    }

}
