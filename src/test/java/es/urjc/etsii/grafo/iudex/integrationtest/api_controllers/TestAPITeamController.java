package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.api.v1.APITeamController;
import es.urjc.etsii.grafo.iudex.entities.Team;
import es.urjc.etsii.grafo.iudex.entities.TeamUser;
import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.pojos.TeamString;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APITeamController.class)
class TestAPITeamController {
    private final JSONConverter jsonConverter = new JSONConverter();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserAndTeamService teamService;
    private TeamUser team;
    private TeamUser user;

    @BeforeEach
    public void init() {
        team = new TeamUser();
        team.getTeams().setId(305);
        team.getTeams().setEsUser(false);
        team.getTeams().setNombreEquipo("Equipo de prueba");

        when(teamService.getTeamFromId(String.valueOf(team.getId()))).thenReturn(Optional.of(team.getTeams()));
        when(teamService.getTeamByNick(team.getTeams().getNombreEquipo())).thenReturn(Optional.of(team.getTeams()));
        when(teamService.getAllTeams()).thenReturn(List.of(team.getTeams()));

        user = new TeamUser();
        user.getUser().setId(307);
        user.getUser().setNickname("usuario de prueba");
        user.getUser().setEmail("prueba@prueba.com");
        team.getTeams().addUserToTeam(user);
    }

    @Test
    @DisplayName("Get all teams")
    void testAPIGetTeams() throws Exception {
        String url = "/API/v1/team";
        String salida = jsonConverter.convertObjectToJSON(List.of(team.getTeams().toTeamAPISimple()));
        String result = mockMvc.perform(get(url).characterEncoding("utf8")).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Get Team")
    void testAPIGetTeam() throws Exception {
        String badTeam = "843";
        String goodTeam = String.valueOf(team.getId());
        String badURL = "/API/v1/team/" + badTeam;
        String goodURL = "/API/v1/team/" + goodTeam;

        String salida = "";
        HttpStatus status = HttpStatus.NOT_FOUND;
        testGetTeam(badURL, status, salida);

        status = HttpStatus.OK;
        salida = jsonConverter.convertObjectToJSON(team.getTeams().toTeamAPI());
        testGetTeam(goodURL, status, salida);
    }

    private void testGetTeam(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Create Team")
    void testAPICreateTeam() throws Exception {
        String badTeam = "";
        String goodTeam = team.getTeams().getNombreEquipo();
        String url = "/API/v1/team/";

        TeamString ts = new TeamString();
        String salida = "";
        ts.setSalida(salida);
        HttpStatus status = HttpStatus.NOT_FOUND;
        when(teamService.crearTeam(badTeam, false)).thenReturn(ts);
        testCreateTeam(url, badTeam, status, salida);

        salida = "OK";
        ts.setSalida(salida);
        ts.setTeam(team.getTeams());
        status = HttpStatus.OK;
        when(teamService.crearTeam(goodTeam, false)).thenReturn(ts);
        salida = jsonConverter.convertObjectToJSON(team.getTeams().toTeamAPI());
        testCreateTeam(url, goodTeam, status, salida);
    }

    private void testCreateTeam(String url, String team, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(post(url).characterEncoding("utf8").param("nombreEquipo", team)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Delete Team")
    void testAPIDeleteTeam() throws Exception {
        String badTeam = "856";
        String goodTeam = String.valueOf(team.getId());
        String badURL = "/API/v1/team/" + badTeam;
        String goodURL = "/API/v1/team/" + goodTeam;

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "";
        when(teamService.deleteTeamByTeamId(badTeam)).thenReturn(salida);
        testDeleteTeam(badURL, status, salida);

        salida = "OK";
        status = HttpStatus.OK;
        when(teamService.deleteTeamByTeamId(goodTeam)).thenReturn(salida);
        salida = "";
        testDeleteTeam(goodURL, status, salida);
    }

    private void testDeleteTeam(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(delete(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Update Team")
    void testAPIUpdateTeam() throws Exception {
        String badTeam = "834";
        String goodTeam = String.valueOf(team.getId());
        String badURL = "/API/v1/team/" + badTeam;
        String goodURL = "/API/v1/team/" + goodTeam;

        String badTeamName = "nombreFalso";
        String goodTeamName = team.getTeams().getNombreEquipo();
        TeamString ts = new TeamString();

        String salida = "";
        HttpStatus status = HttpStatus.NOT_FOUND;
        ts.setSalida(salida);
        when(teamService.updateTeam(badTeam, Optional.of(badTeamName))).thenReturn(ts);
        testUpdateTeam(badURL, badTeamName, status, salida);

        when(teamService.updateTeam(badTeam, Optional.of(goodTeamName))).thenReturn(ts);
        testUpdateTeam(badURL, goodTeamName, status, salida);

        salida = "";
        ts.setSalida(salida);
        when(teamService.updateTeam(goodTeam, Optional.of(badTeamName))).thenReturn(ts);
        testUpdateTeam(goodURL, badTeamName, status, salida);

        salida = "OK";
        status = HttpStatus.OK;
        ts.setSalida(salida);
        ts.setTeam(team.getTeams());
        when(teamService.updateTeam(goodTeam, Optional.of(goodTeamName))).thenReturn(ts);
        salida = jsonConverter.convertObjectToJSON(team.getTeams().toTeamAPI());
        testUpdateTeam(goodURL, goodTeamName, status, salida);
    }

    private void testUpdateTeam(String url, String team, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(put(url).characterEncoding("utf8").param("teamName", team)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Add User to Team")
    void testAPIAddUser() throws Exception {
        String badUser = "872";
        String goodUser = String.valueOf(user.getId());
        String badTeam = "667";
        String goodTeam = String.valueOf(team.getId());

        String badURL = "/API/v1/team/" + badTeam + "/" + badUser;
        String badURL2 = "/API/v1/team/" + badTeam + "/" + goodUser;
        String badURL3 = "/API/v1/team/" + goodTeam + "/" + badUser;
        String goodURL = "/API/v1/team/" + goodTeam + "/" + goodUser;
        TeamString ts = new TeamString();

        String salida = "";
        ts.setSalida(salida);
        HttpStatus status = HttpStatus.NOT_FOUND;

        when(teamService.addUserToTeamUssingIds(badTeam, badUser)).thenReturn(ts);
        testAddUser(badURL, status, salida);

        when(teamService.addUserToTeamUssingIds(badTeam, goodUser)).thenReturn(ts);
        testAddUser(badURL2, status, salida);

        salida = "";
        ts.setSalida(salida);
        when(teamService.addUserToTeamUssingIds(goodTeam, badUser)).thenReturn(ts);
        testAddUser(badURL3, status, salida);

        salida = "OK";
        status = HttpStatus.OK;
        ts.setSalida(salida);
        ts.setTeam(team.getTeams());
        when(teamService.addUserToTeamUssingIds(goodTeam, goodUser)).thenReturn(ts);
        salida = jsonConverter.convertObjectToJSON(team.getTeams().toTeamAPI());
        testAddUser(goodURL, status, salida);

        salida = "";
        status = HttpStatus.NOT_FOUND;
        ts.setSalida(salida);
        when(teamService.addUserToTeamUssingIds(goodTeam, goodUser)).thenReturn(ts);
        testAddUser(goodURL, status, salida);
    }

    private void testAddUser(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(put(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Delete User from Team")
    void testAPIDeleteUser() throws Exception {
        String badUser = "872";
        String goodUser = String.valueOf(user.getId());
        String badTeam = "667";
        String goodTeam = String.valueOf(team.getId());

        String badURL = "/API/v1/team/" + badTeam + "/" + badUser;
        String badURL2 = "/API/v1/team/" + badTeam + "/" + goodUser;
        String badURL3 = "/API/v1/team/" + goodTeam + "/" + badUser;
        String goodURL = "/API/v1/team/" + goodTeam + "/" + goodUser;
        TeamString ts = new TeamString();

        String salida = "";
        HttpStatus status = HttpStatus.NOT_FOUND;
        ts.setSalida(salida);

        when(teamService.deleteUserFromTeam(badTeam, badUser)).thenReturn(ts);
        testDeleteUser(badURL, status, salida);

        when(teamService.deleteUserFromTeam(badTeam, goodUser)).thenReturn(ts);
        testDeleteUser(badURL2, status, salida);

        salida = "";
        ts.setSalida(salida);
        when(teamService.deleteUserFromTeam(goodTeam, badUser)).thenReturn(ts);
        testDeleteUser(badURL3, status, salida);

        salida = "";
        ts.setSalida(salida);
        when(teamService.deleteUserFromTeam(goodTeam, goodUser)).thenReturn(ts);
        testDeleteUser(goodURL, status, salida);

        salida = "OK";
        status = HttpStatus.OK;
        ts.setSalida(salida);
        when(teamService.deleteUserFromTeam(goodTeam, goodUser)).thenReturn(ts);
        salida = "";
        testDeleteUser(goodURL, status, salida);
    }

    private void testDeleteUser(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(delete(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }
}
