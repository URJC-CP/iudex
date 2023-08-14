package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.api.v1.APIProblemController;
import es.urjc.etsii.grafo.iudex.entities.*;
import es.urjc.etsii.grafo.iudex.pojos.ProblemAPI;
import es.urjc.etsii.grafo.iudex.pojos.ProblemString;
import es.urjc.etsii.grafo.iudex.security.jwt.JwtRequestFilter;
import es.urjc.etsii.grafo.iudex.services.ProblemService;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import es.urjc.etsii.grafo.iudex.utils.Sanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = APIProblemController.class, excludeFilters =
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtRequestFilter.class))
@AutoConfigureMockMvc(addFilters = false)
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
    void testAPIGetProblemsWithPagination() throws Exception {
        String url = "/API/v1/problem/page";
        final int page = 1, size = 5;
        Pageable pageable = PageRequest.of(page, size);
        when(problemService.getProblemsPage(pageable)).thenReturn(new PageImpl<>(List.of(problem)));
        mockMvc.perform(get(url).param("size", String.valueOf(size)).param("page", String.valueOf(page))).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Selected Problem")
    void testAPIGetProblem() throws Exception {
        String goodProblem = String.valueOf(problem.getId());
        String badProblem = "745";

        String goodURL = "/API/v1/problem/" + goodProblem;
        String badURL = "/API/v1/problem/" + badProblem;

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        testGetProblem(badURL, status, salida);

        status = HttpStatus.OK;
        salida = jsonConverter.convertObjectToJSON(problem.toProblemAPI());
        testGetProblem(goodURL, status, salida);
    }

    private void testGetProblem(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

//    @Test
//	/*
//	  WARNING:
//		no hay ningun metodo para pasar un objeto como parametro en MockMvc
//		y el controlador no acepta JSON
//	*/
//    @DisplayName("Create problem Using a Problem Object")
//    @Disabled("Create problem Using a Problem Object - Cannot be mocked")
//    void testAPICreateProblem() throws Exception {
//        fail("Could not send an instance as a param with mock");
//    }

    @Test
    @DisplayName("Create Problem From Zip")
    void testAPICreateProblemFromZip() throws Exception {
        String url = "/API/v1/problem/fromZip";

        Problem problem2 = new Problem();
        problem2.setId(673);
        problem2.setNombreEjercicio("Ejercicio de prueba sin añadir a la lista");
        problem2.setEquipoPropietario(owner);

        MockMultipartFile problem2File = new MockMultipartFile(
                "file",
                "primavera.zip",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new ClassPathResource("primavera.zip").getInputStream());

        ProblemString problemString2 = new ProblemString();
        problemString2.setProblem(problem2);
        problemString2.setSalida("OK");

        when(problemService.addProblemFromZip(
                    problem2File,
                    Sanitizer.removeLineBreaks(String.valueOf(owner.getId())),
                    Sanitizer.removeLineBreaks(problem2.getNombreEjercicio()),
                    Sanitizer.removeLineBreaks(String.valueOf(contest.getId()))))
                .thenReturn(problemString2);

        mockMvc.perform(multipart(url)
                    .file(problem2File)
                    .param("problemName", problem2.getNombreEjercicio())
                    .param("teamId", String.valueOf(owner.getId()))
                    .param("contestId", String.valueOf(contest.getId())))
                .andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();


        String nonExisingTeamId = "999";
        problemString2.setSalida("TEAM NOT FOUND");
        when(problemService.addProblemFromZip(
                    problem2File,
                    Sanitizer.removeLineBreaks(nonExisingTeamId),
                    Sanitizer.removeLineBreaks(problem2.getNombreEjercicio()),
                    Sanitizer.removeLineBreaks(String.valueOf(contest.getId()))))
                .thenReturn(problemString2);

        mockMvc.perform(multipart(url)
                    .file(problem2File)
                    .param("problemName", problem2.getNombreEjercicio())
                    .param("teamId", nonExisingTeamId)
                    .param("contestId", String.valueOf(contest.getId())))
                .andExpect(status().isNotFound()).andDo(print()).andReturn().getResponse().getContentAsString();


        problem2File = new MockMultipartFile(
                "file",
                "primavera.zip",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                InputStream.nullInputStream());

        when(problemService.addProblemFromZip(
                    problem2File,
                    Sanitizer.removeLineBreaks(String.valueOf(owner.getId())),
                    Sanitizer.removeLineBreaks(problem2.getNombreEjercicio()),
                    Sanitizer.removeLineBreaks(String.valueOf(contest.getId()))))
                .thenThrow(new Exception());

        mockMvc.perform(multipart(url)
                    .file(problem2File)
                    .param("problemName", problem2.getNombreEjercicio())
                    .param("teamId", String.valueOf(owner.getId()))
                    .param("contestId", String.valueOf(contest.getId())))
                .andExpect(status().isNotAcceptable()).andDo(print()).andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("Update Problem with Multiple Optional Params")
    void testAPIUpdateProblem() throws Exception {
        String badProblem = "534";
        String goodProblem = String.valueOf(problem.getId());
        Optional<String> badProblemName = Optional.of("");
        Optional<String> problemName = Optional.of(problem.getNombreEjercicio());
        Optional<String> badTeam = Optional.of("673");
        Optional<String> goodTeam = Optional.of(String.valueOf(owner.getId()));
        Optional<String> timeout = Optional.of("timeout");

        MockMultipartFile pdf = new MockMultipartFile(
                "pdf",
                "primavera.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                new ClassPathResource("primavera.pdf").getInputStream());

        ProblemString ps = new ProblemString();
        String badURL = "/API/v1/problem/" + badProblem;
        String goodURL = "/API/v1/problem/" + goodProblem;

        String salida = "";
        HttpStatus status = HttpStatus.NOT_FOUND;
        ps.setSalida(salida);
        when(problemService.updateProblemMultipleOptionalParams(badProblem, badProblemName, badTeam, pdf, timeout)).thenReturn(ps);
        testUpdateProblemMultipleOptions(badURL, badProblemName, badTeam, pdf, timeout, status, salida);

        when(problemService.updateProblemMultipleOptionalParams(badProblem, problemName, badTeam, pdf, timeout)).thenReturn(ps);
        testUpdateProblemMultipleOptions(badURL, problemName, badTeam, pdf, timeout, status, salida);

        when(problemService.updateProblemMultipleOptionalParams(badProblem, badProblemName, goodTeam, pdf, timeout)).thenReturn(ps);
        testUpdateProblemMultipleOptions(badURL, badProblemName, goodTeam, pdf, timeout, status, salida);

        when(problemService.updateProblemMultipleOptionalParams(badProblem, problemName, goodTeam, pdf, timeout)).thenReturn(ps);
        testUpdateProblemMultipleOptions(badURL, problemName, goodTeam, pdf, timeout, status, salida);

        salida = "";
        ps.setSalida(salida);
        problem.setNombreEjercicio("");
        when(problemService.updateProblemMultipleOptionalParams(goodProblem, badProblemName, badTeam, pdf, timeout)).thenReturn(ps);
        problem.setNombreEjercicio(problemName.get());
        testUpdateProblemMultipleOptions(goodURL, badProblemName, badTeam, pdf, timeout, status, salida);

        when(problemService.updateProblemMultipleOptionalParams(goodProblem, problemName, badTeam, pdf, timeout)).thenReturn(ps);
        testUpdateProblemMultipleOptions(goodURL, problemName, badTeam, pdf, timeout, status, salida);

        salida = "OK";
        ps.setProblem(problem);
        ps.setSalida(salida);
        status = HttpStatus.OK;
        when(problemService.updateProblemMultipleOptionalParams(goodProblem, problemName, goodTeam, pdf, timeout)).thenReturn(ps);
        salida = jsonConverter.convertObjectToJSON(problem.toProblemAPI());
        testUpdateProblemMultipleOptions(goodURL, problemName, goodTeam, pdf, timeout, status, salida);
    }

    private void testUpdateProblemMultipleOptions(String url, Optional<String> problemName, Optional<String> team, MockMultipartFile pdf, Optional<String> timeout, HttpStatus status, String salida) throws Exception {
        var multipartBuilder = (MockMultipartHttpServletRequestBuilder) multipart(url).with(request -> {
            request.setMethod(String.valueOf(HttpMethod.PUT));
            return request;
        });
        multipartBuilder.file(pdf);
        problemName.ifPresent(value -> multipartBuilder.param("problemName", value));
        team.ifPresent(value -> multipartBuilder.param("teamId", value));
        timeout.ifPresent(value -> multipartBuilder.param("timeout", value));

        String result = mockMvc.perform(multipartBuilder).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Update Problem From Zip")
    void testAPIUpdateProblemFromZip() throws Exception {
        String url = "/API/v1/problem/" + problem.getId() + "/fromZip";

        String problemId = String.valueOf(problem.getId());
        String problemName = problem.getNombreEjercicio() + " modificado";
        String teamId = String.valueOf(problem.getEquipoPropietario().getId());
        String contestId = String.valueOf(contest.getId());

        Problem problem2 = new Problem();
        problem2.setId(problem.getId());
        problem2.setNombreEjercicio(problemName);
        problem2.setEquipoPropietario(owner);

        MockMultipartFile problem2File = new MockMultipartFile(
                "file",
                "primavera.zip",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new ClassPathResource("primavera.zip").getInputStream());

        ProblemString problemString2 = new ProblemString();
        problemString2.setProblem(problem2);
        problemString2.setSalida("OK");

        when(problemService.updateProblem(
                    problemId,
                    problem2File.getOriginalFilename(),
                    problem2File,
                    Sanitizer.removeLineBreaks(teamId),
                    Sanitizer.removeLineBreaks(problem2.getNombreEjercicio()),
                    Sanitizer.removeLineBreaks(contestId)))
                .thenReturn(problemString2);

        MockMultipartHttpServletRequestBuilder multipart = (MockMultipartHttpServletRequestBuilder) multipart(url).with(request -> {
            request.setMethod(String.valueOf(HttpMethod.PUT));
            return request;
        });

        mockMvc.perform(multipart
                        .file(problem2File)
                        .param("problemId", problemId)
                        .param("problemName", problem2.getNombreEjercicio())
                        .param("teamId", teamId)
                        .param("contestId", contestId))
                .andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();


        String nonExisingProblemId = "999";
        problemString2.setSalida("PROBLEM NOT FOUND");
        when(problemService.updateProblem(
                    nonExisingProblemId,
                    problem2File.getOriginalFilename(),
                    problem2File,
                    Sanitizer.removeLineBreaks(teamId),
                    Sanitizer.removeLineBreaks(problem2.getNombreEjercicio()),
                    Sanitizer.removeLineBreaks(contestId)))
                .thenReturn(problemString2);

        multipart = (MockMultipartHttpServletRequestBuilder) multipart(url).with(request -> {
            request.setMethod(String.valueOf(HttpMethod.PUT));
            return request;
        });

        mockMvc.perform(multipart
                        .file(problem2File)
                        .param("problemId", nonExisingProblemId)
                        .param("problemName", problem2.getNombreEjercicio())
                        .param("teamId", teamId)
                        .param("contestId", contestId))
                .andExpect(status().isNotFound()).andDo(print()).andReturn().getResponse().getContentAsString();


        problem2File = new MockMultipartFile(
                "file",
                "primavera.zip",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                InputStream.nullInputStream());

        multipart = (MockMultipartHttpServletRequestBuilder) multipart(url).with(request -> {
            request.setMethod(String.valueOf(HttpMethod.PUT));
            return request;
        });

        when(problemService.updateProblem(
                    problemId,
                    problem2File.getOriginalFilename(),
                    problem2File,
                    Sanitizer.removeLineBreaks(teamId),
                    Sanitizer.removeLineBreaks(problem2.getNombreEjercicio()),
                    Sanitizer.removeLineBreaks(contestId)))
                .thenThrow(new Exception());

        mockMvc.perform(multipart
                        .file(problem2File)
                        .param("problemId", problemId)
                        .param("problemName", problem2.getNombreEjercicio())
                        .param("teamId", teamId)
                        .param("contestId", contestId))
                .andExpect(status().isNotAcceptable()).andDo(print()).andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("Get PDF from Problem")
    void testAPIGetPdfFromProblem() throws Exception {
        String goodProblem = String.valueOf(problem.getId());
        String badProblem = "543";
        String goodURL = "/API/v1//problem/" + goodProblem + "/getPDF";
        String badURL = "/API/v1//problem/" + badProblem + "/getPDF";

        String salida = "";
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

        String salida = "";
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

    @Test
    @DisplayName("Add sample to problem")
    void testAPIAddSampleToProblem() throws Exception {
        String url = "/API/v1/problem/%s/sample";

        String problemId = String.valueOf(problem.getId());
        String badProblemId = "999";
        String name = problem.getNombreEjercicio();
        boolean isPublic = true;
        MockMultipartFile sampleInput = new MockMultipartFile(
                "entrada",
                "sample.in",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new ClassPathResource("primavera/data/sample/sample.in").getInputStream()
        );
        MockMultipartFile sampleOutput = new MockMultipartFile(
                "salida",
                "sample.ans",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new ClassPathResource("primavera/data/sample/sample.ans").getInputStream()
        );
        String salida;


        testAddSampleToProblem(url, problemId, Optional.empty(), Optional.empty(), name, isPublic, status().isBadRequest(), "");
        testAddSampleToProblem(url, problemId, Optional.of(sampleInput), Optional.empty(), name, isPublic, status().isBadRequest(), "");
        testAddSampleToProblem(url, problemId, Optional.empty(), Optional.of(sampleOutput), name, isPublic, status().isBadRequest(), "");

        MockMultipartFile invalidSampleOutput = mock(MockMultipartFile.class);
        when(invalidSampleOutput.getName()).thenReturn("salida");
        when(problemService.addSampleToProblem(problemId, name, sampleInput, invalidSampleOutput, isPublic)).thenThrow(new IOException());
        testAddSampleToProblem(url, problemId, Optional.of(sampleInput), Optional.of(invalidSampleOutput), name, isPublic, status().isUnsupportedMediaType(), "ERROR IN INPUT FILE");

        salida = "OK";
        when(problemService.addSampleToProblem(problemId, name, sampleInput, sampleOutput, isPublic)).thenReturn(salida);
        testAddSampleToProblem(url, problemId, Optional.of(sampleInput), Optional.of(sampleOutput), name, isPublic, status().isOk(), "");

        salida = "PROBLEM NOT FOUND";
        when(problemService.addSampleToProblem(badProblemId, name, sampleInput, sampleOutput, isPublic)).thenReturn(salida);
        testAddSampleToProblem(url, badProblemId, Optional.of(sampleInput), Optional.of(sampleOutput), name, isPublic, status().isNotFound(), salida);
    }

    private void testAddSampleToProblem(String url, String problemId, Optional<MockMultipartFile> optionalSampleInput, Optional<MockMultipartFile> optionalSampleOutput, String name, boolean isPublic, ResultMatcher status, String expected) throws Exception {
        var multipartBuilder = (MockMultipartHttpServletRequestBuilder) multipart(String.format(url, problemId)).with(request -> {
            request.setMethod(String.valueOf(HttpMethod.POST));
            return request;
        });

        multipartBuilder.param("name", name);
        multipartBuilder.param("isPublic", String.valueOf(isPublic));
        optionalSampleInput.ifPresent(multipartBuilder::file);
        optionalSampleOutput.ifPresent(multipartBuilder::file);

        String result = mockMvc.perform(multipartBuilder)
                .andExpect(status)
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Update sample from problem")
    void testAPIUpdateSampleFromProblem() throws Exception {
        String url = "/API/v1/problem/%s/sample/%s";

        String problemId = String.valueOf(problem.getId());
        String badProblemId = "999";

        String name = problem.getNombreEjercicio();
        boolean isPublic = true;
        MockMultipartFile sampleInput = new MockMultipartFile(
                "entrada",
                "sample.in",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new ClassPathResource("primavera/data/sample/sample.in").getInputStream()
        );
        String inputText = new String(sampleInput.getBytes());
        MockMultipartFile sampleOutput = new MockMultipartFile(
                "salida",
                "sample.ans",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new ClassPathResource("primavera/data/sample/sample.ans").getInputStream()
        );
        String outputText = new String(sampleOutput.getBytes());
        Sample sample = new Sample(5L, name, new String(sampleInput.getBytes()), new String(sampleOutput.getBytes()), isPublic);
        String salida;

        testUpdateSampleFromProblem(
                url,
                problemId,
                sample.getId(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                status().isBadRequest(),
                ""
        );

        MockMultipartFile invalidSampleInput = mock(MockMultipartFile.class);
        when(invalidSampleInput.getName()).thenReturn("entrada");
        when(invalidSampleInput.getBytes()).thenThrow(new IOException());
        testUpdateSampleFromProblem(
                url,
                problemId,
                sample.getId(),
                Optional.of(invalidSampleInput),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                status().isUnsupportedMediaType(),
                "ERROR IN INPUT FILE"
        );

        MockMultipartFile invalidSampleOutput = mock(MockMultipartFile.class);
        when(invalidSampleOutput.getName()).thenReturn("salida");
        when(invalidSampleOutput.getBytes()).thenThrow(new IOException());
        testUpdateSampleFromProblem(
                url,
                problemId,
                sample.getId(),
                Optional.empty(),
                Optional.of(invalidSampleOutput),
                Optional.empty(),
                Optional.empty(),
                status().isUnsupportedMediaType(),
                "ERROR IN OUTPUT FILE"
        );

        salida = "OK";
        when(problemService.updateSampleFromProblem(
                Optional.empty(),
                problemId,
                String.valueOf(sample.getId()),
                Optional.of(inputText),
                Optional.empty(),
                Optional.empty())
        ).thenReturn(salida);
        testUpdateSampleFromProblem(
                url,
                problemId,
                sample.getId(),
                Optional.of(sampleInput),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                status().isOk(),
                ""
        );

        when(problemService.updateSampleFromProblem(
                Optional.empty(),
                problemId,
                String.valueOf(sample.getId()),
                Optional.empty(),
                Optional.of(outputText),
                Optional.empty())
        ).thenReturn(salida);
        testUpdateSampleFromProblem(
                url,
                problemId,
                sample.getId(),
                Optional.empty(),
                Optional.of(sampleOutput),
                Optional.empty(),
                Optional.empty(),
                status().isOk(),
                ""
        );

        when(problemService.updateSampleFromProblem(
                Optional.of(name),
                problemId,
                String.valueOf(sample.getId()),
                Optional.empty(),
                Optional.empty(),
                Optional.empty())
        ).thenReturn(salida);
        testUpdateSampleFromProblem(
                url,
                problemId, sample.getId(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(name),
                Optional.empty(),
                status().isOk(),
                ""
        );

        when(problemService.updateSampleFromProblem(
                Optional.empty(),
                problemId,
                String.valueOf(sample.getId()),
                Optional.empty(),
                Optional.empty(),
                Optional.of(isPublic))
        ).thenReturn(salida);
        testUpdateSampleFromProblem(
                url,
                problemId, sample.getId(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(isPublic),
                status().isOk(),
                ""
        );
    }

    private void testUpdateSampleFromProblem(String url, String problemId, long sampleId, Optional<MockMultipartFile> sampleInputOptional, Optional<MockMultipartFile> sampleOutputOptional, Optional<String> nameOptional, Optional<Boolean> isPublicOptional, ResultMatcher status, String expected) throws Exception {
        var multipartBuilder = (MockMultipartHttpServletRequestBuilder) multipart(String.format(url, problemId, sampleId)).with(request -> {
            request.setMethod(String.valueOf(HttpMethod.PUT));
            return request;
        });

        nameOptional.ifPresent(value -> multipartBuilder.param("name", value));
        isPublicOptional.ifPresent(value -> multipartBuilder.param("isPublic", String.valueOf(value)));
        sampleInputOptional.ifPresent(multipartBuilder::file);
        sampleOutputOptional.ifPresent(multipartBuilder::file);

        String result = mockMvc.perform(multipartBuilder)
                .andExpect(status)
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Delete sample from problem")
    void testAPIDeleteSampleFromProblem() throws Exception {
        String url = "/API/v1/problem/%s/sample/%s";
        String problemId = String.valueOf(problem.getId());
        String badProblemId = "999";
        Sample sample = new Sample(5L, problem.getNombreEjercicio(), "", "", true);
        String sampleId = String.valueOf(sample.getId());
        String badSampleId = "999";
        String salida;

        salida = "PROBLEM NOT FOUND";
        when(problemService.deleteSampleFromProblem(badProblemId, sampleId)).thenReturn(salida);
        testDeleteSampleFromProblem(url, badProblemId, sampleId, status().isNotFound(), salida);

        salida = "SAMPLE NOT FOUND";
        when(problemService.deleteSampleFromProblem(problemId, badSampleId)).thenReturn(salida);
        testDeleteSampleFromProblem(url, problemId, badSampleId, status().isNotFound(), salida);

        salida = "SAMPLE NOT IN PROBLEM";
        when(problemService.deleteSampleFromProblem(badProblemId, badSampleId)).thenReturn(salida);
        testDeleteSampleFromProblem(url, badProblemId, badSampleId, status().isNotFound(), salida);

        salida = "OK";
        when(problemService.deleteSampleFromProblem(problemId, sampleId)).thenReturn(salida);
        testDeleteSampleFromProblem(url, problemId, sampleId, status().isOk(), "");
    }

    private void testDeleteSampleFromProblem(String url, String problemId, String sampleId, ResultMatcher status, String expected) throws Exception {
        String result = mockMvc.perform(delete(String.format(url, problemId, sampleId))).andExpect(status).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(expected, result);
    }
}
