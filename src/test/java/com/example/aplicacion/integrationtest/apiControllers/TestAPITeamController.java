package com.example.aplicacion.integrationtest.apiControllers;

import com.example.aplicacion.Controllers.apiControllers.APITeamController;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Entities.User;
import com.example.aplicacion.Pojos.TeamString;
import com.example.aplicacion.services.TeamService;
import com.example.aplicacion.utils.JSONConverter;
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
public class TestAPITeamController {
    private final JSONConverter jsonConverter = new JSONConverter();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TeamService teamService;
    private Team team;
    private User user;

    @BeforeEach
    public void init() {
        team = new Team();
        team.setId(305);
        team.setEsUser(false);
        team.setNombreEquipo("Equipo de prueba");

        when(teamService.getTeamFromId(String.valueOf(team.getId()))).thenReturn(Optional.of(team));
        when(teamService.getTeamByNick(team.getNombreEquipo())).thenReturn(Optional.of(team));
        when(teamService.getAllTeams()).thenReturn(List.of(team));

        user = new User();
        user.setId(307);
        user.setNickname("usuario de prueba");
        user.setEmail("prueba@prueba.com");
        team.addUserToTeam(user);
    }

    @Test
    @DisplayName("Get all teams")
    public void testAPIGetTeams() throws Exception {
        String url = "/API/v1/team";
        String salida = jsonConverter.convertObjectToJSON(List.of(team.toTeamAPISimple()));
        String result = mockMvc.perform(get(url).characterEncoding("utf8")).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Get Team")
    public void testAPIGetTeam() throws Exception {
        String badTeam = "843";
        String goodTeam = String.valueOf(team.getId());
        String badURL = "/API/v1/team/" + badTeam;
        String goodURL = "/API/v1/team/" + goodTeam;

        String salida = "TEAM NOT FOUND";
        HttpStatus status = HttpStatus.NOT_FOUND;
        testGetTeam(badURL, status, salida);

        status = HttpStatus.OK;
        salida = jsonConverter.convertObjectToJSON(team.toTeamAPI());
        testGetTeam(goodURL, status, salida);
    }

    private void testGetTeam(String url, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(get(url).characterEncoding("utf8")).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Create Team")
    public void testAPICreateTeam() throws Exception {
        String badTeam = "";
        String goodTeam = team.getNombreEquipo();
        String url = "/API/v1/team/";

        TeamString ts = new TeamString();
        String salida = "TEAM NAME DUPLICATED";
        ts.setSalida(salida);
        HttpStatus status = HttpStatus.NOT_FOUND;
        when(teamService.crearTeam(badTeam, false)).thenReturn(ts);
        testCreateTeam(url, badTeam, status, salida);

        salida = "OK";
        ts.setSalida(salida);
        ts.setTeam(team);
        status = HttpStatus.OK;
        when(teamService.crearTeam(goodTeam, false)).thenReturn(ts);
        salida = jsonConverter.convertObjectToJSON(team.toTeamAPI());
        testCreateTeam(url, goodTeam, status, salida);
    }

    private void testCreateTeam(String url, String team, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(post(url).characterEncoding("utf8").param("nombreEquipo", team)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Delete Team")
    public void testAPIDeleteTeam() throws Exception {
        String badTeam = "856";
        String goodTeam = String.valueOf(team.getId());
        String badURL = "/API/v1/team/" + badTeam;
        String goodURL = "/API/v1/team/" + goodTeam;

        HttpStatus status = HttpStatus.NOT_FOUND;
        String salida = "TEAM NOT FOUND";
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
    public void testAPIUpdateTeam() throws Exception {
        String badTeam = "834";
        String goodTeam = String.valueOf(team.getId());
        String badURL = "/API/v1/team/" + badTeam;
        String goodURL = "/API/v1/team/" + goodTeam;

        String badTeamName = "nombreFalso";
        String goodTeamName = team.getNombreEquipo();
        TeamString ts = new TeamString();

        String salida = "TEAM NOT FOUND";
        HttpStatus status = HttpStatus.NOT_FOUND;
        ts.setSalida(salida);
        when(teamService.updateTeam(badTeam, Optional.of(badTeamName))).thenReturn(ts);
        testUpdateTeam(badURL, badTeamName, status, salida);

        when(teamService.updateTeam(badTeam, Optional.of(goodTeamName))).thenReturn(ts);
        testUpdateTeam(badURL, goodTeamName, status, salida);

        salida = "TEAM NAME DUPLICATED";
        ts.setSalida(salida);
        when(teamService.updateTeam(goodTeam, Optional.of(badTeamName))).thenReturn(ts);
        testUpdateTeam(goodURL, badTeamName, status, salida);

        salida = "OK";
        status = HttpStatus.OK;
        ts.setSalida(salida);
        ts.setTeam(team);
        when(teamService.updateTeam(goodTeam, Optional.of(goodTeamName))).thenReturn(ts);
        salida = jsonConverter.convertObjectToJSON(team.toTeamAPI());
        testUpdateTeam(goodURL, goodTeamName, status, salida);
    }

    private void testUpdateTeam(String url, String team, HttpStatus status, String salida) throws Exception {
        String result = mockMvc.perform(put(url).characterEncoding("utf8").param("teamName", team)).andExpect(status().is(status.value())).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(salida, result);
    }

    @Test
    @DisplayName("Add User to Team")
    public void testAPIAddUser() throws Exception {
        String badUser = "872";
        String goodUser = String.valueOf(user.getId());
        String badTeam = "667";
        String goodTeam = String.valueOf(team.getId());

        String badURL = "/API/v1/team/" + badTeam + "/" + badUser;
        String badURL2 = "/API/v1/team/" + badTeam + "/" + goodUser;
        String badURL3 = "/API/v1/team/" + goodTeam + "/" + badUser;
        String goodURL = "/API/v1/team/" + goodTeam + "/" + goodUser;
        TeamString ts = new TeamString();

        String salida = "TEAM NOT FOUND";
        ts.setSalida(salida);
        HttpStatus status = HttpStatus.NOT_FOUND;

        when(teamService.addUserToTeamUssingIds(badTeam, badUser)).thenReturn(ts);
        testAddUser(badURL, status, salida);

        when(teamService.addUserToTeamUssingIds(badTeam, goodUser)).thenReturn(ts);
        testAddUser(badURL2, status, salida);

        salida = "USER NOT FOUND";
        ts.setSalida(salida);
        when(teamService.addUserToTeamUssingIds(goodTeam, badUser)).thenReturn(ts);
        testAddUser(badURL3, status, salida);

        salida = "OK";
        status = HttpStatus.OK;
        ts.setSalida(salida);
        ts.setTeam(team);
        when(teamService.addUserToTeamUssingIds(goodTeam, goodUser)).thenReturn(ts);
        salida = jsonConverter.convertObjectToJSON(team.toTeamAPI());
        testAddUser(goodURL, status, salida);

        salida = "USER ALREADY IN TEAM";
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
    public void testAPIDeleteUser() throws Exception {
        String badUser = "872";
        String goodUser = String.valueOf(user.getId());
        String badTeam = "667";
        String goodTeam = String.valueOf(team.getId());

        String badURL = "/API/v1/team/" + badTeam + "/" + badUser;
        String badURL2 = "/API/v1/team/" + badTeam + "/" + goodUser;
        String badURL3 = "/API/v1/team/" + goodTeam + "/" + badUser;
        String goodURL = "/API/v1/team/" + goodTeam + "/" + goodUser;
        TeamString ts = new TeamString();

        String salida = "TEAM NOT FOUND";
        HttpStatus status = HttpStatus.NOT_FOUND;
        ts.setSalida(salida);

        when(teamService.deleteUserFromTeam(badTeam, badUser)).thenReturn(ts);
        testDeleteUser(badURL, status, salida);

        when(teamService.deleteUserFromTeam(badTeam, goodUser)).thenReturn(ts);
        testDeleteUser(badURL2, status, salida);

        salida = "USER NOT FOUND";
        ts.setSalida(salida);
        when(teamService.deleteUserFromTeam(goodTeam, badUser)).thenReturn(ts);
        testDeleteUser(badURL3, status, salida);

        salida = "USER IS NOT IN TEAM";
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
