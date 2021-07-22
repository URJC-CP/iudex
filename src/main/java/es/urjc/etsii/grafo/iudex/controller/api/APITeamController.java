package es.urjc.etsii.grafo.iudex.controller.api;

import es.urjc.etsii.grafo.iudex.entity.Team;
import es.urjc.etsii.grafo.iudex.pojo.TeamAPI;
import es.urjc.etsii.grafo.iudex.pojo.TeamString;
import es.urjc.etsii.grafo.iudex.service.TeamService;
import es.urjc.etsii.grafo.iudex.util.Sanitizer;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class APITeamController {
    @Autowired
    TeamService teamService;

    @ApiOperation("Return all Teams")
    @GetMapping("/API/v1/team")
    public ResponseEntity<List<TeamAPI>> getteams() {
        List<TeamAPI> teamAPIS = new ArrayList<>();
        for (Team team : teamService.getAllTeams()) {
            teamAPIS.add(team.toTeamAPISimple());
        }
        return new ResponseEntity<>(teamAPIS, HttpStatus.OK);
    }

    @ApiOperation("Return Team")
    @GetMapping("/API/v1/team/{teamId}")
    public ResponseEntity<TeamAPI> getTeam(@PathVariable String teamId) {
        teamId = Sanitizer.sanitize(teamId);

        Optional<Team> teamOptional = teamService.getTeamFromId(teamId);
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            return new ResponseEntity<>(team.toTeamAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity("TEAM NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Creates a Team")
    @PostMapping("/API/v1/team")
    public ResponseEntity<TeamAPI> createTeam(@RequestParam String nombreEquipo) {
        nombreEquipo = Sanitizer.sanitize(nombreEquipo);

        //false pq no es un usuario
        TeamString salida = teamService.crearTeam(nombreEquipo, false);

        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getTeam().toTeamAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Delete a Team")
    @DeleteMapping("/API/v1/team/{teamId}")
    public ResponseEntity<String> deleteTeam(@PathVariable String teamId) {
        teamId = Sanitizer.sanitize(teamId);

        String salida = teamService.deleteTeamByTeamId(teamId);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Update a Team")
    @PutMapping("/API/v1/team/{teamId}")
    public ResponseEntity<TeamAPI> updateTeam(@PathVariable String teamId, @RequestParam(required = false) Optional<String> teamName) {
        teamId = Sanitizer.sanitize(teamId);
        teamName = Sanitizer.sanitize(teamName);

        TeamString salida = teamService.updateTeam(teamId, teamName);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getTeam().toTeamAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Add user to Team")
    @PutMapping("/API/v1/team/{teamId}/{userId}")
    public ResponseEntity<TeamAPI> addUserToTeam(@PathVariable String teamId, @PathVariable String userId) {
        teamId = Sanitizer.sanitize(teamId);
        userId = Sanitizer.sanitize(userId);

        TeamString salida = teamService.addUserToTeamUssingIds(teamId, userId);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getTeam().toTeamAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Delete user from team")
    @DeleteMapping("/API/v1/team/{teamId}/{userId}")
    public ResponseEntity<String> deleteUserFromTeam(@PathVariable String teamId, @PathVariable String userId) {
        teamId = Sanitizer.sanitize(teamId);
        userId = Sanitizer.sanitize(userId);

        TeamString salida = teamService.deleteUserFromTeam(teamId, userId);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }
}
