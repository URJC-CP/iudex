package com.example.aplicacion.services;


import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Entities.User;
import com.example.aplicacion.Pojos.TeamString;
import com.example.aplicacion.Repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    Logger logger = LoggerFactory.getLogger(TeamService.class);
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserService userService;

    public TeamString crearTeam(String nickTeam, boolean isUser) {
        logger.debug("Create team {}", nickTeam);
        TeamString salida = new TeamString();

        Team team = new Team(nickTeam);
        team.setEsUser(isUser);
        if (teamRepository.existsTeamByNombreEquipo(nickTeam)) {
            logger.error("Team name duplicated");
            salida.setSalida("TEAM NAME DUPLICATED");
            return salida;
        } else {
            teamRepository.save(team);
            salida.setSalida("OK");
            salida.setTeam(team);
            logger.debug("Finish create team {}", nickTeam);
            return salida;
        }
    }

    public String addUserToTeam(Team team, User user) {
        logger.debug("Add user {} to team {}", user.getId(), team.getId());
        if (teamRepository.existsTeamByParticipantesContains(user)) {
            logger.error("User {} already in team {}", user.getId(), team.getId());
            return "USER ALREADY IN TEAM";
        } else {
            team.addUserToTeam(user);
            teamRepository.save(team);
            logger.debug("Finish add user {} to team {} ", user.getId(), team.getId());
            return "OK";
        }
    }

    public String deleteTeamByName(String name) {
        logger.debug("Delete team " + name);
        Optional<Team> teamOptional = teamRepository.findByNombreEquipo(name);
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", name);
            return "TEAM NOT FOUND";
        }
        Team team = teamOptional.get();

        //Si es un team user borramos el user
        if (team.isEsUser()) {
            userService.deleteUserByNickname(team.getNombreEquipo());
        }
        teamRepository.delete(team);
        logger.debug("Finish delete user {} from team {}", name, team.getId());
        return "OK";
    }

    public String deleteTeamByTeamId(String teamId) {
        logger.debug("Delete team {}", teamId);
        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(teamId));
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", teamId);
            return "TEAM NOT FOUND";
        }
        Team team = teamOptional.get();

        //Si es un team user borramos el user
        if (team.isEsUser()) {
            userService.deleteUserByNickname(team.getNombreEquipo());
        }
        teamRepository.delete(team);
        logger.debug("Finish delete team {}", teamId);
        return "OK";

    }

    public Optional<Team> getTeamByNick(String nick) {
        return teamRepository.findByNombreEquipo(nick);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamFromId(String teamId) {
        return teamRepository.findTeamById(Long.parseLong(teamId));
    }

    public TeamString addUserToTeamUssingIds(String teamId, String userId) {
        logger.debug("Add user {} to team {}", userId, teamId);
        TeamString salida = new TeamString();

        Optional<Team> teamOptional = getTeamFromId(teamId);
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", teamId);
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        Team team = teamOptional.get();

        Optional<User> userOptional = userService.getUserById(Long.parseLong(userId));
        if (userOptional.isEmpty()) {
            logger.error("User {} not found" + userId);
            salida.setSalida("USER NOT FOUND");
            return salida;
        }
        User user = userOptional.get();

        if (team.getParticipantes().contains(user)) {
            logger.error("User {} already in team {}", userId, teamId);
            salida.setSalida("USER ALREADY IN TEAM");
            return salida;
        }
        team.getParticipantes().add(user);
        teamRepository.save(team);

        salida.setSalida("OK");
        salida.setTeam(team);
        logger.debug("Finish add user {} to team {}", userId, teamId);
        return salida;
    }

    public TeamString deleteUserFromTeam(String teamId, String userId) {
        logger.debug("Delete user {} from team {}", userId, teamId);
        TeamString salida = new TeamString();

        Optional<Team> teamOptional = getTeamFromId(teamId);
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", teamId);
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        Team team = teamOptional.get();

        Optional<User> userOptional = userService.getUserById(Long.parseLong(userId));
        if (userOptional.isEmpty()) {
            logger.error("User {} not found", userId);
            salida.setSalida("USER NOT FOUND");
            return salida;
        }
        User user = userOptional.get();

        if (!team.getParticipantes().contains(user)) {
            logger.error("User {} already in team {}", userId, teamId);
            salida.setSalida("USER NOT IN TEAM");
            return salida;
        }

        team.getParticipantes().remove(user);
        teamRepository.save(team);

        logger.debug("Finish delete user {} from team {}", userId, teamId);
        salida.setSalida("OK");
        return salida;
    }

    public TeamString updateTeam(String teamId, Optional<String> teamName) {
        logger.debug("Update team {}", teamId);
        TeamString salida = new TeamString();

        Optional<Team> teamOptional = getTeamFromId(teamId);
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", teamId);
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        Team team = teamOptional.get();

        if (teamName.isPresent()) {
            if (teamRepository.existsTeamByNombreEquipo(teamName.get())) {
                logger.error("Team {} duplicated", teamName.get());
                salida.setSalida("TEAM NAME DUPLICATED");
                return salida;
            } else {
                team.setNombreEquipo(teamName.get());
                teamRepository.save(team);
            }
        }

        teamRepository.save(team);
        salida.setSalida("OK");
        salida.setTeam(team);
        logger.debug("Finish update team {}", teamId);
        return salida;
    }
}
