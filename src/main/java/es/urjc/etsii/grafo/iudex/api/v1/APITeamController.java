package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.Team;
import es.urjc.etsii.grafo.iudex.pojos.TeamAPI;
import es.urjc.etsii.grafo.iudex.pojos.TeamString;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static es.urjc.etsii.grafo.iudex.utils.Sanitizer.sanitize;

@RestController
@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class APITeamController {
    @Autowired
    UserAndTeamService teamService;

    @Operation( summary = "Return all Teams")
    @GetMapping("/API/v1/team")
    public ResponseEntity<List<TeamAPI>> getteams() {
        List<TeamAPI> teamAPIS = new ArrayList<>();
        for (Team team : teamService.getAllTeams()) {
            teamAPIS.add(team.toTeamAPISimple());
        }
        return new ResponseEntity<>(teamAPIS, HttpStatus.OK);
    }

    @Operation( summary = "Return Team")
    @GetMapping("/API/v1/team/{teamId}")
    public ResponseEntity<TeamAPI> getTeam(@PathVariable String teamId) {
        teamId = sanitize(teamId);

        Optional<Team> teamOptional = teamService.getTeamFromId(teamId);
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            return new ResponseEntity<>(team.toTeamAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation( summary = "Creates a Team")
    @PostMapping("/API/v1/team")
    public ResponseEntity<TeamAPI> createTeam(@RequestParam String nombreEquipo) {
        nombreEquipo = sanitize(nombreEquipo);

        //false pq no es un usuario
        TeamString salida = teamService.crearTeam(nombreEquipo, false);

        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getTeam().toTeamAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation( summary = "Delete a Team")
    @DeleteMapping("/API/v1/team/{teamId}")
    public ResponseEntity<String> deleteTeam(@PathVariable String teamId) {
        teamId = sanitize(teamId);

        String salida = teamService.deleteTeamByTeamId(teamId);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
    }

    @Operation( summary = "Update a Team")
    @PutMapping("/API/v1/team/{teamId}")
    public ResponseEntity<TeamAPI> updateTeam(@PathVariable String teamId, @RequestParam(required = false) Optional<String> teamName) {
        teamId = sanitize(teamId);
        teamName = sanitize(teamName);

        TeamString salida = teamService.updateTeam(teamId, teamName);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getTeam().toTeamAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation( summary = "Add user to Team")
    @PutMapping("/API/v1/team/{teamId}/{userId}")
    public ResponseEntity<TeamAPI> addUserToTeam(@PathVariable String teamId, @PathVariable String userId) {
        teamId = sanitize(teamId);
        userId = sanitize(userId);

        TeamString salida = teamService.addUserToTeamUssingIds(teamId, userId);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getTeam().toTeamAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation( summary = "Delete user from team")
    @DeleteMapping("/API/v1/team/{teamId}/{userId}")
    public ResponseEntity<String> deleteUserFromTeam(@PathVariable String teamId, @PathVariable String userId) {
        teamId = sanitize(teamId);
        userId = sanitize(userId);

        TeamString salida = teamService.deleteUserFromTeam(teamId, userId);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }
}