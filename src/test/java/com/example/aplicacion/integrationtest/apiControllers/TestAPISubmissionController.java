package com.example.aplicacion.integrationtest.apiControllers;

import com.example.aplicacion.Controllers.apiControllers.APISubmissionController;
import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@WebMvcTest(APISubmissionController.class)
public class TestAPISubmissionController {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private SubmissionService submissionService;
	@MockBean
	private ContestService contestService;
	@MockBean
	private ProblemService problemService;

	private Contest contest;
	private Team owner;
	private Problem problem;
	private Submission submission;

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

		submission = new Submission();
		submission.setId(342);
		submission.setProblema(problem);
		submission.setContest(contest);

		when(contestService.getContest(String.valueOf(contest.getId()))).thenReturn(Optional.of(contest));
		when(contestService.getAllContests()).thenReturn(List.of(contest));

		when(problemService.getAllProblemas()).thenReturn(List.of(problem));
		when(problemService.getProblem(String.valueOf(problem.getId()))).thenReturn(Optional.of(problem));
	}

	@Test
	@DisplayName("Get Submissions with problemId and/or contestId")
	public void testAPIGetSubmissions() {

	}

	@Test
	@DisplayName("Get All Submissions with Pagination")
	public void testAPIGetAllSubmissions() {
		String badProblem;
		String goodProblem;
		String badContest;
		String goodContest;


	}

	@Test
	@DisplayName("Create Submission")
	public void testAPICreateSubmission() {

	}

	@Test
	@DisplayName("Delete Submission")
	public void testAPIDeleteSubmission() {

	}
}
