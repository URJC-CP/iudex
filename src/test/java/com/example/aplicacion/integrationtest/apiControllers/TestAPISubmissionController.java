package com.example.aplicacion.integrationtest.apiControllers;

import com.example.aplicacion.Controllers.apiControllers.APISubmissionController;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
}
