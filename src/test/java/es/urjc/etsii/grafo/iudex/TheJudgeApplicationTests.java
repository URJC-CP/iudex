package es.urjc.etsii.grafo.iudex;

import es.urjc.etsii.grafo.iudex.pojos.ContestAPI;
import es.urjc.etsii.grafo.iudex.pojos.ProblemAPI;
import es.urjc.etsii.grafo.iudex.pojos.SubmissionAPI;
import es.urjc.etsii.grafo.iudex.pojos.TeamAPI;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TheJudgeApplicationTests {
    private final String basePathTestFiles = "/src/main/resources/testfiles";
    private final RestTemplate restTemplate = new RestTemplate();
    private final JSONConverter jsonConverter = new JSONConverter();
    @LocalServerPort
    private int port;

    @Test
    @DisplayName("Application's initial state")
    @Disabled
    void test0() {
        ResponseEntity<String> response;
        String url = getBaseURL() + "/contest";
        response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        // get default contest
        ContestAPI contest = ((ContestAPI[]) jsonConverter.convertTreeStringToObject(response.getBody(), ContestAPI[].class))[0];
        String contestId = String.valueOf(contest.getId());
        testGetContest(contestId);
        assertThat(contest.getNombreContest(), equalTo("contestPrueba"));

        // get default team
        TeamAPI team = contest.getTeamPropietario();
        String teamId = String.valueOf(team.getId());
        assertThat(team.getNombreEquipo(), equalTo("pavloXd"));
        testGetTeam(teamId);
    }

    private String getContestId(int index) {
        ResponseEntity<String> response;
        String url = getBaseURL() + "/contest";
        response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        ContestAPI contestAPI = ((ContestAPI[]) jsonConverter.convertTreeStringToObject(response.getBody(), ContestAPI[].class))[index];
        return String.valueOf(contestAPI.getId());
    }

    private String getTeamId(int index) {
        ResponseEntity<String> response;
        String url = getBaseURL() + "/team";
        response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        TeamAPI teamAPI = ((TeamAPI[]) jsonConverter.convertTreeStringToObject(response.getBody(), TeamAPI[].class))[index];
        return String.valueOf(teamAPI.getId());
    }

    private String getProblemId(int index) {
        ResponseEntity<String> response;
        String url = getBaseURL() + "/problem";
        response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        ProblemAPI problemAPI = ((ProblemAPI[]) jsonConverter.convertTreeStringToObject(response.getBody(), ProblemAPI[].class))[index];
        return String.valueOf(problemAPI.getId());
    }

    @Test
    @DisplayName("Application basic test - create contest, add problem and do submission")
    @Disabled
    void test1() {
        String contestId = getContestId(0);
        String teamId = getTeamId(0);

        // create new contest
        String contestName = "concurso prueba 001";
        String contestDescription = "concurso de prueba";
        testCreateNewContest(contestName, contestDescription, teamId);

        // create new problem from zip and no name
        String filename = "primavera.zip";
        String problemName = "";
        testCreateProblemFromZip(filename, problemName, teamId, contestId);
        String problemPrimavera = getProblemId(0);

        // create new problem from zip with problem name
        filename = "mysql.zip";
        problemName = "problema de mysql";
        testCreateProblemFromZip(filename, problemName, teamId, contestId);
        String problemMysql = getProblemId(1);

        // submit java file to primavera
        String codeFile = "primavera/submissions/accepted/main.java";
        String language = getLanguage("java");
        testAddSubmission(contestId, problemPrimavera, teamId, language, codeFile);

        // submit sql file to problema de mysql
        codeFile = "mysql/submissions/accepted/accepted.sql";
        language = getLanguage("java");
        testAddSubmission(contestId, problemMysql, teamId, language, codeFile);

        testGetTeamWithAllData(teamId);
    }

    private void testCreateNewContest(String name, String description, String owner) {
        String url = getBaseURL() + "/contest";

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params;
        HttpEntity<MultiValueMap<String, String>> request;
        ResponseEntity<String> response;

        params = new LinkedMultiValueMap<>();
        params.add("contestName", name);
        params.add("teamId", String.valueOf(owner));
        params.add("descripcion", description);
        params.add("startTimestamp", String.valueOf(Instant.now().toEpochMilli()));
        params.add("endTimestamp", String.valueOf(Instant.now().plusSeconds(24 * 60 * 60).toEpochMilli()));

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        request = new HttpEntity<>(params, headers);
        response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        ContestAPI contest = (ContestAPI) jsonConverter.convertTreeStringToObject(response.getBody(), ContestAPI.class);
        String contestId = String.valueOf(contest.getId());
        assertThat(contest.getNombreContest(), equalTo(name));
        assertThat(contest.getDescripcion(), equalTo(description));
        assertThat(String.valueOf(contest.getTeamPropietario().getId()), equalTo(owner));

        testGetContest(contestId);
    }

    @Test
    @DisplayName("Get contest with invalid id")
    @Disabled
    void test2() {
        String badContestId = "564";
        String salida = "no body";
        testGetContestWithException(badContestId, salida);
    }

    private void testGetContest(String contestId) {
        String getContestURL = getBaseURL() + "/contest/" + contestId;
        ResponseEntity<String> response = restTemplate.getForEntity(getContestURL, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        System.out.println(response.getBody());

        ContestAPI contest = (ContestAPI) jsonConverter.convertTreeStringToObject(response.getBody(), ContestAPI.class);
        assertThat(String.valueOf(contest.getId()), equalTo(contestId));
    }

    private void testGetContestWithException(String contestId, String salida) {
        String getContestURL = getBaseURL() + "/contest/" + contestId;
        Exception exception = assertThrows(Exception.class, () -> restTemplate.getForEntity(getContestURL, String.class));
        assertThat(exception.getMessage(), equalTo("404 : [" + salida + "]"));
    }

    @Test
    @DisplayName("Create problem from zip with invalid parameters")
    @Disabled
    void test3() {
        String contestId = getContestId(0);
        String teamId = getTeamId(0);
        String badTeamId = "897";
        String badContestId = "576";
        String salida;

        String filename = "primavera.zip";
        String problemName = "";
        salida = "no body";
        testCreateProblemFromZipWithException(filename, problemName, badTeamId, badContestId, salida);

        salida = "no body";
        testCreateProblemFromZipWithException(filename, problemName, teamId, badContestId, salida);

        // unnamed empty file without problem name
        filename = ".zip.zip";
        salida = "no body";
        testCreateProblemFromZipWithException(filename, problemName, teamId, contestId, salida);

        // unnamed empty file with problem name
        filename = ".zip.zip";
        problemName = "pruba vacio";
        salida = "no body";
        testCreateProblemFromZipWithException(filename, problemName, teamId, contestId, salida);
    }

    private void testCreateProblemFromZip(String filename, String problemName, String teamId, String contestId) {
        String createProblemURL = getBaseURL() + "/problem/fromZip";
        File file = new File("." + basePathTestFiles + "/" + filename);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", new FileSystemResource(file));
        params.add("problemName", problemName);
        params.add("teamId", teamId);
        params.add("contestId", contestId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(createProblemURL, requestEntity, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        System.out.println(response.getBody());

        ProblemAPI problem = (ProblemAPI) jsonConverter.convertTreeStringToObject(response.getBody(), ProblemAPI.class);
        String problemId = String.valueOf(problem.getId());
        TeamAPI team = problem.getEquipoPropietario();

        assertThat(String.valueOf(team.getId()), equalTo(teamId));
        assertThat(team.getNombreEquipo(), equalTo("pavloXd"));

        if (problemName.isEmpty()) {
            problemName = filename.trim().split("\\.")[0];
        }
        assertThat(problem.getNombreEjercicio(), equalTo(problemName));
        testGetProblem(problemId);
    }

    private void testCreateProblemFromZipWithException(String filename, String problem, String team, String contest, String salida) {
        String createProblemURL = getBaseURL() + "/problem/fromZip";
        File file = new File("." + basePathTestFiles + "/" + filename);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", new FileSystemResource(file));
        params.add("problemName", problem);
        params.add("teamId", team);
        params.add("contestId", contest);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        Exception exception = assertThrows(Exception.class, () -> restTemplate.postForEntity(createProblemURL, requestEntity, String.class));
        assertThat(exception.getMessage(), equalTo("404 : [" + salida + "]"));
    }

    @Test
    @DisplayName("Get problem with invalid id")
    @Disabled
    void test4() {
        String badProblemId = "756";
        String salida = "no body";
        testGetProblemWithException(badProblemId, salida);
    }

    private void testGetProblem(String problemId) {
        String getProblemURL = getBaseURL() + "/problem/" + problemId;
        ResponseEntity<String> response = restTemplate.getForEntity(getProblemURL, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        System.out.println(response.getBody());

        ProblemAPI problem = (ProblemAPI) jsonConverter.convertTreeStringToObject(response.getBody(), ProblemAPI.class);
        assertThat(String.valueOf(problem.getId()), equalTo(problemId));
    }

    private void testGetProblemWithException(String problemId, String salida) {
        String getProblemURL = getBaseURL() + "/problem/" + problemId;
        Exception exception = assertThrows(Exception.class, () -> restTemplate.getForEntity(getProblemURL, String.class));
        assertThat(exception.getMessage(), equalTo("404 : [" + salida + "]"));
    }

    //realizar entregas
    @Test
    @DisplayName("Add submission with invalid parameters")
    @Disabled
    void test5() {
        String contestId = getContestId(0);
        String teamId = getTeamId(0);
        String anotherContestId = getContestId(1);
        String problemId = getProblemId(0);

        String badContestId = "768";
        String badProblemId = "874";
        String badTeamId = "987";

        // all okay
        String codeFile = "vacio.java";
        String language = "";
        String salida = "no body";
        testAddSubmissionWithException(badContestId, badProblemId, badTeamId, language, codeFile, salida);

        salida = "no body";
        testAddSubmissionWithException(contestId, badProblemId, badTeamId, language, codeFile, salida);

        salida = "no body";
        testAddSubmissionWithException(contestId, problemId, badTeamId, language, codeFile, salida);

		/*
		salida = "LANGUAGE NOT FOUND"; --> cannot be reached --> 500 : [ERROR GENERAL DEL SISTEMA]
	 	language = getLanguage("php");
	 	testAddSubmissionWithException(contestId, problemPrimavera, teamId, language, codeFile, salida);
		 */

        language = getLanguage("java");
        salida = "no body";
        testAddSubmissionWithException(anotherContestId, problemId, teamId, language, codeFile, salida);
    }

    private String getLanguage(String language) {
        language = language.toLowerCase().trim();
        return switch (language) {
            case "java" -> "1";
            case "python" -> "2";
            case "c" -> "3";
            case "cpp", "c++" -> "4";
            case "sql" -> "5";
            default -> "1023";
        };
    }

    private void testAddSubmission(String contestId, String problemId, String teamId, String language, String codeFileName) {
        String addSubmissionURL = getBaseURL() + "/submission";
        File file = new File("." + basePathTestFiles + "/" + codeFileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("problemId", problemId);
        params.add("contestId", contestId);
        params.add("codigo", new FileSystemResource(file));
        params.add("lenguaje", language);
        params.add("teamId", teamId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(addSubmissionURL, requestEntity, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        System.out.println(response.getBody());

        SubmissionAPI submission = (SubmissionAPI) jsonConverter.convertTreeStringToObject(response.getBody(), SubmissionAPI.class);
        String submissionId = String.valueOf(submission.getId());
        testGetSubmission(submissionId);
    }

    private void testAddSubmissionWithException(String contestId, String problemId, String teamId, String language, String codeFileName, String salida) {
        String addSubmissionURL = getBaseURL() + "/submission";
        File file = new File("." + basePathTestFiles + "/" + codeFileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("problemId", problemId);
        params.add("contestId", contestId);
        params.add("codigo", new FileSystemResource(file));
        params.add("lenguaje", language);
        params.add("teamId", teamId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        Exception exception = assertThrows(Exception.class, () -> restTemplate.postForEntity(addSubmissionURL, requestEntity, String.class));
        assertThat(exception.getMessage(), equalTo("404 : [" + salida + "]"));
    }

    @Test
    @DisplayName("Get submission with invalid id")
    @Disabled
    void test6() {
        String badSubId = "756";
        String salida = "no body";
        testGetSubmissionWithException(badSubId, salida);
    }

    private void testGetSubmission(String subId) {
        String getSubmissionURL = getBaseURL() + "/submission/" + subId;
        ResponseEntity<String> response = restTemplate.getForEntity(getSubmissionURL, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        System.out.println(response.getBody());

        SubmissionAPI submission = (SubmissionAPI) jsonConverter.convertTreeStringToObject(response.getBody(), SubmissionAPI.class);
        assertThat(String.valueOf(submission.getId()), equalTo(subId));
    }

    private void testGetSubmissionWithException(String subId, String salida) {
        String getSubmissionURL = getBaseURL() + "/submission/" + subId;
        Exception exception = assertThrows(Exception.class, () -> restTemplate.getForEntity(getSubmissionURL, String.class));
        assertThat(exception.getMessage(), equalTo("404 : [" + salida + "]"));
    }

    @Test
    @DisplayName("Get team with invalid id")
    @Disabled
    void test7() {
        String badTeam = "867";
        String salida = "no body";
        testGetTeamWithException(badTeam, salida);
    }

    private void testGetTeam(String teamID) {
        String getTeamURL = getBaseURL() + "/team/" + teamID;
        ResponseEntity<String> response = restTemplate.getForEntity(getTeamURL, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        System.out.println(response.getBody());

        TeamAPI team = (TeamAPI) jsonConverter.convertTreeStringToObject(response.getBody(), TeamAPI.class);
        assertThat(String.valueOf(team.getId()), equalTo(teamID));
    }

    private void testGetTeamWithAllData(String teamID) {
        String getTeamURL = getBaseURL() + "/team/" + teamID;
        ResponseEntity<String> response = restTemplate.getForEntity(getTeamURL, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        System.out.println(response.getBody());

        TeamAPI team = (TeamAPI) jsonConverter.convertTreeStringToObject(response.getBody(), TeamAPI.class);
        assertThat(String.valueOf(team.getId()), equalTo(teamID));
        assertThat(team.getNombreEquipo(), equalTo("pavloXd"));

        assertFalse(team.getListaDeSubmissions().isEmpty());
        assertFalse(team.getListaProblemasCreados().isEmpty());
        assertFalse(team.getListaProblemasParticipados().isEmpty());
        assertFalse(team.getListaContestsCreados().isEmpty());
        assertFalse(team.getListaContestsParticipados().isEmpty());
    }

    private void testGetTeamWithException(String teamID, String salida) {
        String getSubmissionURL = getBaseURL() + "/team/" + teamID;
        Exception exception = assertThrows(Exception.class, () -> restTemplate.getForEntity(getSubmissionURL, String.class));
        assertThat(exception.getMessage(), equalTo("404 : [" + salida + "]"));
    }

    private String getBaseURL() {
        return "http://localhost:" + port + "/API/v1";
    }
}
