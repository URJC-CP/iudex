package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.api.v1.APIContestController;
import es.urjc.etsii.grafo.iudex.entities.Contest;
import es.urjc.etsii.grafo.iudex.entities.Problem;
import es.urjc.etsii.grafo.iudex.entities.Team;
import es.urjc.etsii.grafo.iudex.pojos.ContestAPI;
import es.urjc.etsii.grafo.iudex.pojos.ContestString;
import es.urjc.etsii.grafo.iudex.pojos.TeamScore;
import es.urjc.etsii.grafo.iudex.services.ContestService;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestAPIContestController {

    @Autowired
    private WebApplicationContext context;
    private final JSONConverter jsonConverter = new JSONConverter();
    private final String baseURL = "/API/v1/contest";

    private MockMvc mockMvc;
    @MockBean
    private ContestService contestService;
    private Contest contest;
    private Team owner;
    private Problem problem;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

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

        when(contestService.getContestById(String.valueOf(contest.getId()))).thenReturn(Optional.of(contest));
        when(contestService.getAllContests()).thenReturn(List.of(contest));
    }

    @Test
    @DisplayName("Get All Contests")
    void testAPIGetAllContests() throws Exception {
        String url = baseURL;
        String result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();

        ContestAPI[] contestArray = new ContestAPI[1];
        contestArray[0] = contest.toContestAPI();
        assertEquals(jsonConverter.convertObjectToJSON(contestArray), result);
    }

    @Test
    @DisplayName("Get All Contests with Pagination")
    void testAPIGetAllContestsWithPagination() throws Exception {
        String url = baseURL + "/page";
        final int page = 1, size = 5;
        Pageable pageable = PageRequest.of(page, size);
        when(contestService.getContestPage(pageable)).thenReturn(new PageImpl<>(List.of(contest)));
        mockMvc.perform(get(url).param("size", String.valueOf(size)).param("page", String.valueOf(page))).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get One Contest")
    void testAPIGetContest() throws Exception {
        String badContest = "523";
        String goodContest = String.valueOf(contest.getId());

        String badURL = baseURL + "/" + badContest;
        String goodURL = baseURL + "/" + goodContest;

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        testGetContest(badURL, status, salida);

        status = HttpStatus.OK;
        salida = jsonConverter.convertObjectToJSON(contest.toContestAPI());
        testGetContest(goodURL, status, salida);
    }

    private void testGetContest(String url, HttpStatus status, String salida) throws Exception {
        String result;
        result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Create Contest")
    void testAPICreateContest() throws Exception {
        String url = baseURL;

        String badContest = "concurso_falso_123";
        String goodContest = contest.getNombreContest();
        String badTeam = "643";
        String goodTeam = String.valueOf(owner.getId());
        String description = contest.getDescripcion();

        ContestString cs = new ContestString();
        HttpStatus status = HttpStatus.NOT_FOUND;

        long startDateTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
        long endDateTime = LocalDateTime.now().plusDays(1).atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();

        String salida = "";
        cs.setSalida(salida);
        when(contestService.creaContest(badContest, badTeam, Optional.of(description), startDateTime, endDateTime)).thenReturn(cs);
        testAddContest(url, badContest, badTeam, description, String.valueOf(startDateTime), String.valueOf(endDateTime), status, salida);
        when(contestService.creaContest(badContest, goodTeam, Optional.of(description), startDateTime, endDateTime)).thenReturn(cs);
        testAddContest(url, badContest, goodTeam, description, String.valueOf(startDateTime), String.valueOf(endDateTime), status, salida);

        salida = "";
        cs.setSalida(salida);
        when(contestService.creaContest(goodContest, badTeam, Optional.of(description), startDateTime, endDateTime)).thenReturn(cs);
        testAddContest(url, goodContest, badTeam, description, String.valueOf(startDateTime), String.valueOf(endDateTime), status, salida);

        cs.setSalida("OK");
        status = HttpStatus.CREATED;
        salida = jsonConverter.convertObjectToJSON(contest.toContestAPI());
        cs.setContest(contest);
        when(contestService.creaContest(goodContest, goodTeam, Optional.of(description), startDateTime, endDateTime)).thenReturn(cs);
        testAddContest(url, goodContest, goodTeam, description, String.valueOf(startDateTime), String.valueOf(endDateTime), status, salida);
    }

    private void testAddContest(String url, String contest, String team, String description, String startDateTime, String endDateTime, HttpStatus status, String salida) throws Exception {
        String result;
        result = mockMvc.perform(post(url).characterEncoding("utf8").param("contestName", contest).param("teamId", team).param("descripcion", description).param("startTimestamp", startDateTime).param("endTimestamp", endDateTime)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Delete Contest")
    void testAPIDeleteContest() throws Exception {
        String badURL = baseURL + "/521";
        String goodURL = baseURL + "/" + contest.getId();

        String badContest = "521";
        String goodContest = String.valueOf(contest.getId());
        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";

        when(contestService.deleteContest(badContest)).thenReturn(salida);
        testDeleteContest(badURL, status, salida);

        status = HttpStatus.OK;
        salida = "";
        when(contestService.deleteContest(goodContest)).thenReturn("OK");
        testDeleteContest(goodURL, status, salida);
    }

    private void testDeleteContest(String url, HttpStatus status, String salida) throws Exception {
        String result;
        result = mockMvc.perform(delete(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        System.out.println(result);
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Update Contest")
    void testAPIUpdateContest() throws Exception {
        String badURL = baseURL + "/521";
        String goodURL = baseURL + "/" + contest.getId();

        String badContest = "521";
        String goodContest = String.valueOf(contest.getId());
        Optional<String> badName = Optional.of("654");
        Optional<String> goodName = Optional.of(contest.getNombreContest());
        Optional<String> badTeam = Optional.of("435");
        Optional<String> goodTeam = Optional.of(String.valueOf(owner.getId()));
        Optional<String> description = Optional.of(contest.getDescripcion());

        ContestString cs = new ContestString();
        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";

        long startDateTimeLong = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
        long endDateTimeLong = LocalDateTime.now().plusDays(1).atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
        Optional<Long> startDateTime = Optional.of(startDateTimeLong);
        Optional<Long> endDateTime = Optional.of(endDateTimeLong);

        cs.setSalida(salida);
        when(contestService.updateContest(badContest, badName, badTeam, description, startDateTime, endDateTime)).thenReturn(cs);
        testUpdateContest(badURL, badName.get(), badTeam.get(), description.get(), String.valueOf(startDateTime.get()), String.valueOf(endDateTime.get()), status, salida);

        when(contestService.updateContest(badContest, goodName, badTeam, description, startDateTime, endDateTime)).thenReturn(cs);
        testUpdateContest(badURL, goodName.get(), badTeam.get(), description.get(), String.valueOf(startDateTime.get()), String.valueOf(endDateTime.get()), status, salida);

        when(contestService.updateContest(badContest, badName, goodTeam, description, startDateTime, endDateTime)).thenReturn(cs);
        testUpdateContest(badURL, badName.get(), goodTeam.get(), description.get(), String.valueOf(startDateTime.get()), String.valueOf(endDateTime.get()), status, salida);

        salida = "";
        cs.setSalida(salida);
        when(contestService.updateContest(goodContest, badName, badTeam, description, startDateTime, endDateTime)).thenReturn(cs);
        testUpdateContest(goodURL, badName.get(), badTeam.get(), description.get(), String.valueOf(startDateTime.get()), String.valueOf(endDateTime.get()), status, salida);

        when(contestService.updateContest(goodContest, badName, goodTeam, description, startDateTime, endDateTime)).thenReturn(cs);
        testUpdateContest(goodURL, badName.get(), goodTeam.get(), description.get(), String.valueOf(startDateTime.get()), String.valueOf(endDateTime.get()), status, salida);

        salida = "";
        cs.setSalida(salida);
        when(contestService.updateContest(goodContest, goodName, badTeam, description, startDateTime, endDateTime)).thenReturn(cs);
        testUpdateContest(goodURL, goodName.get(), badTeam.get(), description.get(), String.valueOf(startDateTime.get()), String.valueOf(endDateTime.get()), status, salida);

        salida = jsonConverter.convertObjectToJSON(contest.toContestAPI());
        status = HttpStatus.CREATED;
        cs.setSalida("OK");
        cs.setContest(contest);
        when(contestService.updateContest(goodContest, goodName, goodTeam, description, startDateTime, endDateTime)).thenReturn(cs);
        testUpdateContest(goodURL, goodName.get(), goodTeam.get(), description.get(), String.valueOf(startDateTime.get()), String.valueOf(endDateTime.get()), status, salida);
    }

    private void testUpdateContest(String url, String contestName, String teamId, String description, String startDateTime, String endDateTime, HttpStatus status, String salida) throws Exception {
        String result;
        result = mockMvc.perform(put(url).characterEncoding("utf8").param("contestName", contestName).param("teamId", teamId).param("descripcion", description).param("startTimestamp", startDateTime).param("endTimestamp", endDateTime)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Add Problem to Contest")
    void testAPIAddProblemToContest() throws Exception {
        String badURL = baseURL + "/532/756";
        String badURL2 = baseURL + "/532/" + problem.getId();
        String badURL3 = baseURL + "/" + contest.getId() + "/756";
        String goodURL = baseURL + "/" + contest.getId() + "/" + problem.getId();

        String badContest = "532";
        String goodContest = String.valueOf(contest.getId());
        String badProblem = "756";
        String goodProblem = String.valueOf(problem.getId());

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        when(contestService.anyadeProblemaContest(badContest, badProblem)).thenReturn(salida);
        testAddProblem(badURL, status, salida);
        when(contestService.anyadeProblemaContest(badContest, goodProblem)).thenReturn(salida);
        testAddProblem(badURL2, status, salida);

        salida = "";
        when(contestService.anyadeProblemaContest(goodContest, badProblem)).thenReturn(salida);
        testAddProblem(badURL3, status, salida);

        salida = "";
        when(contestService.anyadeProblemaContest(goodContest, badProblem)).thenReturn(salida);
        testAddProblem(badURL3, status, salida);

        status = HttpStatus.OK;
        salida = "OK";
        when(contestService.anyadeProblemaContest(goodContest, goodProblem)).thenReturn(salida);
        salida = "";
        testAddProblem(goodURL, status, salida);
    }

    private void testAddProblem(String url, HttpStatus status, String salida) throws Exception {
        String result;
        result = mockMvc.perform(put(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Delete Problem From Contest")
    void testAPIDeleteProblemFromContest() throws Exception {
        String badURL = baseURL + "/531/764";
        String badURL2 = baseURL + "/531/" + problem.getId();
        String badURL3 = baseURL + "/" + contest.getId() + "/764";
        String goodURL = baseURL + "/" + contest.getId() + "/" + problem.getId();

        String badContest = "531";
        String goodContest = String.valueOf(contest.getId());
        String badProblem = "764";
        String goodProblem = String.valueOf(problem.getId());

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        when(contestService.deleteProblemFromContest(badContest, badProblem)).thenReturn(salida);
        testDeleteProblem(badURL, status, salida);
        when(contestService.deleteProblemFromContest(badContest, goodProblem)).thenReturn(salida);
        testDeleteProblem(badURL2, status, salida);

        salida = "";
        when(contestService.deleteProblemFromContest(goodContest, badProblem)).thenReturn(salida);
        testDeleteProblem(badURL3, status, salida);

        salida = "";
        when(contestService.deleteProblemFromContest(goodContest, badProblem)).thenReturn(salida);
        testDeleteProblem(badURL3, status, salida);

        salida = "OK";
        status = HttpStatus.OK;
        when(contestService.deleteProblemFromContest(goodContest, goodProblem)).thenReturn(salida);
        salida = "";
        testDeleteProblem(goodURL, status, salida);
    }

    private void testDeleteProblem(String url, HttpStatus status, String salida) throws Exception {
        String result;
        result = mockMvc.perform(delete(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Add Team to Contest")
    void testAPIAddTeamToContest() throws Exception {
        String badContest = "531";
        String goodContest = String.valueOf(contest.getId());
        String badTeam = "764";
        String goodTeam = String.valueOf(owner.getId());

        String badURL = String.format("%s/%s/team/%s", baseURL, badContest, badTeam);
        String badURL2 = String.format("%s/%s/team/%s", baseURL, badContest, goodTeam);
        String badURL3 = String.format("%s/%s/team/%s", baseURL, goodContest, badTeam);
        String goodURL = String.format("%s/%s/team/%s", baseURL, goodContest, goodTeam);

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        String expected = "";

        when(contestService.addTeamToContest(badContest, badTeam)).thenReturn(salida);
        testAddTeamToContest(badURL, badContest, badTeam, status, expected);

        when(contestService.addTeamToContest(badContest, goodTeam)).thenReturn(salida);
        testAddTeamToContest(badURL2, badContest, goodTeam, status, expected);

        when(contestService.addTeamToContest(goodContest, badTeam)).thenReturn(salida);
        testAddTeamToContest(badURL3, goodContest, badTeam, status, expected);

        salida = "OK";
        status = HttpStatus.OK;

        when(contestService.addTeamToContest(goodContest, goodTeam)).thenReturn(salida);
        testAddTeamToContest(goodURL, goodContest, goodTeam, status, expected);
    }

    private void testAddTeamToContest(String url, String contestId, String teamId, HttpStatus status, String expected) throws Exception {
        String result;
        result = mockMvc.perform(put(url)
                    .characterEncoding("utf8")
                    .param("contestId", contestId)
                    .param("teamId", teamId)
                ).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Bulk add Team to Contest")
    void testAPIBulkAddTeamsToContest() throws Exception {
        String badContest = "531";
        String goodContest = String.valueOf(contest.getId());
        String badTeam = "764";
        String goodTeam = String.valueOf(owner.getId());

        String goodUrl = String.format("%s/%s/team/addBulk", baseURL, goodContest);
        String badUrl = String.format("%s/%s/team/addBulk", baseURL, badContest);

        String[] teamList;

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        String expected = "";

        teamList = new String[5];
        teamList[0] = goodTeam;
        when(contestService.addTeamToContest(goodContest, teamList)).thenReturn(salida);
        testBulkAddTeamsToContest(goodUrl, teamList, status, expected);

        teamList[0] = badTeam;
        when(contestService.addTeamToContest(badContest, teamList)).thenReturn(salida);
        testBulkAddTeamsToContest(badUrl, teamList, status, expected);

        teamList[1] = goodTeam;
        when(contestService.addTeamToContest(badContest, teamList)).thenReturn(salida);
        testBulkAddTeamsToContest(badUrl, teamList, status, expected);
        teamList[1] = null;

        salida = "OK";
        status = HttpStatus.OK;

        teamList[0] = goodTeam;
        when(contestService.addTeamToContest(goodContest, teamList)).thenReturn(salida);
        testBulkAddTeamsToContest(goodUrl, teamList, status, expected);

        Team newTeam = new Team();
        newTeam.setId(201);
        newTeam.setNombreEquipo("propietario");
        teamList[1] = String.valueOf(newTeam.getId());
        when(contestService.addTeamToContest(goodContest, teamList)).thenReturn(salida);
        testBulkAddTeamsToContest(goodUrl, teamList, status, expected);
    }

    private void testBulkAddTeamsToContest(String url, String[] teamList, HttpStatus status, String expected) throws Exception {
        String result;
        result = mockMvc.perform(put(url)
                    .characterEncoding("utf8")
                    .param("teamList", teamList)
                ).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Delete Team from Contest")
    void testAPIDeleteTeamToContest() throws Exception {
        String badContest = "531";
        String goodContest = String.valueOf(contest.getId());
        String badTeam = "764";
        String goodTeam = String.valueOf(owner.getId());

        String badURL = String.format("%s/%s/team/%s", baseURL, badContest, badTeam);
        String badURL2 = String.format("%s/%s/team/%s", baseURL, badContest, goodTeam);
        String badURL3 = String.format("%s/%s/team/%s", baseURL, goodContest, badTeam);
        String goodURL = String.format("%s/%s/team/%s", baseURL, goodContest, goodTeam);

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        String expected = "";

        when(contestService.deleteTeamFromContest(badContest, badTeam)).thenReturn(salida);
        testDeleteTeamToContest(badURL, badContest, badTeam, status, expected);

        when(contestService.deleteTeamFromContest(badContest, goodTeam)).thenReturn(salida);
        testDeleteTeamToContest(badURL2, badContest, goodTeam, status, expected);

        when(contestService.deleteTeamFromContest(goodContest, badTeam)).thenReturn(salida);
        testDeleteTeamToContest(badURL3, goodContest, badTeam, status, expected);

        salida = "OK";
        status = HttpStatus.OK;

        when(contestService.deleteTeamFromContest(goodContest, goodTeam)).thenReturn(salida);
        testDeleteTeamToContest(goodURL, goodContest, goodTeam, status, expected);
    }

    private void testDeleteTeamToContest(String url, String contestId, String teamId, HttpStatus status, String expected) throws Exception {
        String result;
        result = mockMvc.perform(delete(url)
                .characterEncoding("utf8")
                .param("contestId", contestId)
                .param("teamId", teamId)
        ).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Bulk delete Team to Contest")
    void testAPIBulkDeleteTeamsToContest() throws Exception {
        String badContest = "531";
        String goodContest = String.valueOf(contest.getId());
        String badTeam = "764";
        String goodTeam = String.valueOf(owner.getId());

        String goodUrl = String.format("%s/%s/team/removeBulk", baseURL, goodContest);
        String badUrl = String.format("%s/%s/team/removeBulk", baseURL, goodContest);

        String[] teamList;

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        String expected = "";

        teamList = new String[5];
        teamList[0] = goodTeam;
        when(contestService.deleteTeamFromContest(goodContest, teamList)).thenReturn(salida);
        testBulkDeleteTeamsToContest(goodUrl, teamList, status, expected);

        teamList[0] = badTeam;
        when(contestService.deleteTeamFromContest(badContest, teamList)).thenReturn(salida);
        testBulkDeleteTeamsToContest(badUrl, teamList, status, expected);

        teamList[1] = goodTeam;
        when(contestService.deleteTeamFromContest(badContest, teamList)).thenReturn(salida);
        testBulkDeleteTeamsToContest(badUrl, teamList, status, expected);
        teamList[1] = null;

        salida = "OK";
        status = HttpStatus.OK;

        teamList[0] = goodTeam;
        when(contestService.deleteTeamFromContest(goodContest, teamList)).thenReturn(salida);
        testBulkDeleteTeamsToContest(goodUrl, teamList, status, expected);
    }

    private void testBulkDeleteTeamsToContest(String url, String[] teamList, HttpStatus status, String expected) throws Exception {
        String result;
        result = mockMvc.perform(delete(url)
                .characterEncoding("utf8")
                .param("teamList", teamList)
        ).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Add Language to Contest")
    void testAPIAddLanguageToContest() throws Exception {
        String badContest = "531";
        String goodContest = String.valueOf(contest.getId());
        String badLanguageName = "iudex";
        String goodLanguageName = "cpp";

        String badURL = String.format("%s/%s/language", baseURL, badContest);
        String badURL2 = String.format("%s/%s/language", baseURL, badContest);
        String badURL3 = String.format("%s/%s/language", baseURL, goodContest);
        String goodURL = String.format("%s/%s/language", baseURL, goodContest);

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        String expected = "";

        when(contestService.addLanguageToContest(badContest, badLanguageName)).thenReturn(salida);
        testAddLanguageToContest(badURL, badLanguageName, status, expected);

        when(contestService.addLanguageToContest(badContest, goodLanguageName)).thenReturn(salida);
        testAddLanguageToContest(badURL2, goodLanguageName, status, expected);

        when(contestService.addLanguageToContest(goodContest, badLanguageName)).thenReturn(salida);
        testAddLanguageToContest(badURL3, badLanguageName, status, expected);

        salida = "OK";
        status = HttpStatus.OK;

        when(contestService.addLanguageToContest(goodContest, goodLanguageName)).thenReturn(salida);
        testAddLanguageToContest(goodURL, goodLanguageName, status, expected);
    }

    private void testAddLanguageToContest(String url, String language, HttpStatus status, String expected) throws Exception {
        String result;
        result = mockMvc.perform(post(url)
                .characterEncoding("utf8")
                .param("language", language)
        ).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Delete Language from Contest")
    void testAPIDeleteLanguageFromContest() throws Exception {
        String badContest = "531";
        String goodContest = String.valueOf(contest.getId());
        String badLanguageName = "iudex";
        String goodLanguageName = "cpp";

        String badURL = String.format("%s/%s/language/%s", baseURL, badContest, badLanguageName);
        String badURL2 = String.format("%s/%s/language/%s", baseURL, badContest, goodLanguageName);
        String badURL3 = String.format("%s/%s/language/%s", baseURL, goodContest, badLanguageName);
        String goodURL = String.format("%s/%s/language/%s", baseURL, goodContest, goodLanguageName);

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        String expected = "";

        when(contestService.removeLanguageFromContest(badContest, badLanguageName)).thenReturn(salida);
        testDeleteLanguageFromContest(badURL, status, expected);

        when(contestService.removeLanguageFromContest(badContest, goodLanguageName)).thenReturn(salida);
        testDeleteLanguageFromContest(badURL2, status, expected);

        when(contestService.removeLanguageFromContest(goodContest, badLanguageName)).thenReturn(salida);
        testDeleteLanguageFromContest(badURL3, status, expected);

        salida = "OK";
        status = HttpStatus.OK;

        when(contestService.removeLanguageFromContest(goodContest, goodLanguageName)).thenReturn(salida);
        testDeleteLanguageFromContest(goodURL, status, expected);
    }

    private void testDeleteLanguageFromContest(String url, HttpStatus status, String expected) throws Exception {
        String result;
        result = mockMvc.perform(delete(url))
                .andExpect(status().is(status.value()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Set accepted languages of a contest")
    void testAPIAddAcceptedLanguagesToContest() throws Exception {
        String badContest = "531";
        String goodContest = String.valueOf(contest.getId());
        String badLanguageName = "iudex";
        String goodLanguageName = "cpp";
        String goodLanguageName2 = "java";

        String goodUrl = String.format("%s/%s/language/addBulk", baseURL, goodContest);
        String badurl = String.format("%s/%s/language/addBulk", baseURL, badContest);

        String[] languageList;

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        String expected = "";

        languageList = new String[5];
        languageList[0] = goodLanguageName;
        when(contestService.addAcceptedLanguagesToContest(goodContest, languageList)).thenReturn(salida);
        testAddAcceptedLanguagesToContest(goodUrl, languageList, status, expected);

        languageList[0] = badLanguageName;
        when(contestService.addAcceptedLanguagesToContest(badContest, languageList)).thenReturn(salida);
        testAddAcceptedLanguagesToContest(badurl, languageList, status, expected);

        languageList[1] = goodLanguageName;
        when(contestService.addAcceptedLanguagesToContest(badContest, languageList)).thenReturn(salida);
        testAddAcceptedLanguagesToContest(badurl, languageList, status, expected);
        languageList[1] = null;

        salida = "OK";
        status = HttpStatus.OK;

        languageList[0] = goodLanguageName;
        when(contestService.addAcceptedLanguagesToContest(goodContest, languageList)).thenReturn(salida);
        testAddAcceptedLanguagesToContest(goodUrl, languageList, status, expected);

        languageList[1] = goodLanguageName2;
        when(contestService.addAcceptedLanguagesToContest(goodContest, languageList)).thenReturn(salida);
        testAddAcceptedLanguagesToContest(goodUrl, languageList, status, expected);
    }

    private void testAddAcceptedLanguagesToContest(String url, String[] languageList, HttpStatus status, String expected) throws Exception {
        String result;
        result = mockMvc.perform(post(url)
                .characterEncoding("utf8")
                .param("languageList", languageList)
        ).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Set accepted languages of a contest")
    void testAPIGetScores() throws Exception {
        String badContest = "531";
        String goodContest = String.valueOf(contest.getId());

        String badUrl = String.format("%s/%s/scoreboard", baseURL, badContest);
        String goodUrl = String.format("%s/%s/scoreboard", baseURL, goodContest);


        HttpStatus status = HttpStatus.NOT_FOUND;

        when(contestService.getScore(badContest)).thenThrow(new RuntimeException());
        testGetScores(badUrl, status, "");

        status = HttpStatus.OK;
        List<TeamScore> teamScoreList = List.of(new TeamScore(owner.toTeamAPISimple()));

        when(contestService.getScore(goodContest)).thenReturn(teamScoreList);
        testGetScores(goodUrl, status, jsonConverter.convertObjectToJSON(teamScoreList));
    }
    private void testGetScores(String url, HttpStatus status, String expected) throws Exception {
        String result;
        result = mockMvc.perform(get(url))
                .andExpect(status().is(status.value()))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(expected, result);
    }
}
