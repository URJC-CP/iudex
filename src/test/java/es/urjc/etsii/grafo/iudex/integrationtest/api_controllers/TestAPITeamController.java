package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.api.v1.APITeamController;
import es.urjc.etsii.grafo.iudex.entities.Team;
import es.urjc.etsii.grafo.iudex.entities.TeamUser;
import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.pojos.TeamString;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;


@WebMvcTest(APITeamController.class)
@AutoConfigureMockMvc
class TestAPITeamController {
    private final JSONConverter jsonConverter = new JSONConverter();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserAndTeamService teamService;
    private Team team;
    private User user;
    private TeamUser userTeam;

    @BeforeEach
    public void init() {
        team = new Team("Equipo de prueba");
        team.setId(305);
        team.setEsUser(false);

        user = new User("usuario de prueba", "prueba@prueba.com");
        user.setId(307);

        userTeam = new TeamUser();
        userTeam.setTeam(team);
        userTeam.setUser(user);
        team.addUserToTeam(userTeam);
        user.addTeam(userTeam);

        when(teamService.getTeamFromId(String.valueOf(userTeam.getTeam().getId()))).thenReturn(Optional.of(userTeam.getTeam()));
        when(teamService.getTeamByNick(userTeam.getTeam().getNombreEquipo())).thenReturn(Optional.of(userTeam.getTeam()));
        when(teamService.getAllTeams()).thenReturn(List.of(team));

    }

    @Test
    @DisplayName("Get all teams")
    void testAPIGetTeams() throws Exception {
        String url = "/API/v1/team";
        String salida = jsonConverter.convertObjectToJSON(List.of(team.toTeamAPISimple()));
        String result = mockMvc.perform(get(url).with(csrf()).with(csrf()).characterEncoding("utf8")).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();
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
        salida = jsonConverter.convertObjectToJSON(team.toTeamAPI());
        testGetTeam(goodURL, status, salida);
    }

    private void testGetTeam(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url).with(csrf()).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Create Team")
    void testAPICreateTeam() throws Exception {
        String badTeam = "";
        String goodTeam = team.getNombreEquipo();
        String url = "/API/v1/team/";

        TeamString ts = new TeamString();
        String salida = "";
        ts.setSalida(salida);
        HttpStatus status = HttpStatus.NOT_FOUND;
        when(teamService.crearTeam(badTeam, false)).thenReturn(ts);
        testCreateTeam(url, badTeam, status, salida);

        salida = "OK";
        ts.setSalida(salida);
        ts.setTeam(userTeam.getTeam());
        status = HttpStatus.OK;
        when(teamService.crearTeam(goodTeam, false)).thenReturn(ts);
        salida = jsonConverter.convertObjectToJSON(userTeam.getTeam().toTeamAPI());
        testCreateTeam(url, goodTeam, status, salida);
    }

    private void testCreateTeam(String url, String team, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(post(url).with(csrf()).characterEncoding("utf8").param("nombreEquipo", team)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Delete Team")
    void testAPIDeleteTeam() throws Exception {
        String badTeam = "856";
        String goodTeam = String.valueOf(userTeam.getId());
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
        String result = mockMvc.perform(delete(url).with(csrf()).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Update Team")
    void testAPIUpdateTeam() throws Exception {
        String badTeam = "834";
        String goodTeam = String.valueOf(userTeam.getId());
        String badURL = "/API/v1/team/" + badTeam;
        String goodURL = "/API/v1/team/" + goodTeam;

        String badTeamName = "nombreFalso";
        String goodTeamName = userTeam.getTeam().getNombreEquipo();
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
        ts.setTeam(userTeam.getTeam());
        when(teamService.updateTeam(goodTeam, Optional.of(goodTeamName))).thenReturn(ts);
        salida = jsonConverter.convertObjectToJSON(userTeam.getTeam().toTeamAPI());
        testUpdateTeam(goodURL, goodTeamName, status, salida);
    }

    private void testUpdateTeam(String url, String team, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(put(url).with(csrf()).characterEncoding("utf8").param("teamName", team)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Add User to Team")
    void testAPIAddUser() throws Exception {
        String badUser = "872";
        String goodUser = String.valueOf(userTeam.getUser().getId());
        String badTeam = "667";
        String goodTeam = String.valueOf(userTeam.getTeam().getId());

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
        ts.setTeam(userTeam.getTeam());
        when(teamService.addUserToTeamUssingIds(goodTeam, goodUser)).thenReturn(ts);
        salida = jsonConverter.convertObjectToJSON(userTeam.getTeam().toTeamAPI());
        testAddUser(goodURL, status, salida);

        salida = "";
        status = HttpStatus.NOT_FOUND;
        ts.setSalida(salida);
        when(teamService.addUserToTeamUssingIds(goodTeam, goodUser)).thenReturn(ts);
        testAddUser(goodURL, status, salida);
    }

    private void testAddUser(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(put(url).with(csrf()).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
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
        String result = mockMvc.perform(delete(url).with(csrf()).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }
}
