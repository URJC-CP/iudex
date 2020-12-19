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
        logger.info("Create team/user " + nickTeam);
        TeamString salida = new TeamString();

        Team team = new Team(nickTeam);
        team.setEsUser(isUser);
        if (teamRepository.existsByNombreEquipo(nickTeam)) {
            logger.error("Team/user " + nickTeam + " duplicated");
            salida.setSalida("TEAM NAME DUPLICATED");
            return salida;
        } else {
            teamRepository.save(team);
            salida.setSalida("OK");
            salida.setTeam(team);
            logger.info("Finish create team/user " + nickTeam + " id: " + team.getId());
            return salida;
        }
    }

    public String addUserToTeam(Team team, User user) {
        logger.info("Add user " + user.getId() + " to team " + team.getId());
        if (teamRepository.existsTeamByParticipantesContains(user)) {
            logger.error("User " + user.getId() + " already in team " + team.getId());
            return "USUARIO YA APUNTADO";
        } else {
            team.addUserToTeam(user);
            teamRepository.save(team);
            logger.info("Finish add user " + user.getId() + " to team " + team.getId());
            return "OK";
        }
    }

    public String deleteTeamByName(String name) {
        logger.info("Delete team " + name);
        Team team = teamRepository.findByNombreEquipo(name);
        if (team == null) {
            logger.error("Team/user " + name + " not found");
            return "TEAM NOT FOUND";
        } else {
            //Si es un team user borramos el user
            if (team.isEsUser()) {
                userService.deleteUserByNickname(team.getNombreEquipo());
            }
            teamRepository.delete(team);
            logger.info("Finish delete team/user " + name + "\nTeam/user id: " + team.getId());
            return "OK";
        }
    }

    public String deleteTeamByTeamId(String teamId) {
        logger.info("Delete team/user " + teamId);
        Team team = teamRepository.findTeamById(Long.valueOf(teamId));
        if (team == null) {
            logger.error("Team/user " + teamId + " not found");
            return "TEAM NOT FOUND";
        } else {
            //Si es un team user borramos el user
            if (team.isEsUser()) {
                userService.deleteUserByNickname(team.getNombreEquipo());
            }
            teamRepository.delete(team);
            logger.info("Finish delete team/user " + teamId);
            return "OK";
        }
    }

    public Team getTeamByNick(String nick) {
        return teamRepository.findByNombreEquipo(nick);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamFromId(String teamId) {
        return teamRepository.findTeamById(Long.valueOf(teamId));
    }

    public TeamString addUserToTeamUssingIds(String teamId, String userId) {
        logger.info("Add user " + userId + " to team " + teamId);
        TeamString salida = new TeamString();
        Team team = getTeamFromId(teamId);
        if (team == null) {
            logger.error("Team " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }

        User user = userService.getUserById(Long.valueOf(userId));
        if (user == null) {
            logger.info("User " + userId + " not found");
            salida.setSalida("USER NOT FOUND");
            return salida;
        }

        if (team.getParticipantes().contains(user)) {
            logger.error("User " + userId + " already in team " + teamId);
            salida.setSalida("USER ALREADY IN TEAM");
            return salida;
        }

        team.getParticipantes().add(user);
        teamRepository.save(team);

        salida.setSalida("OK");
        salida.setTeam(team);
        logger.info("Finish add user " + userId + " to team " + teamId);
        return salida;
    }

    public String deleteUserFromTeam(String teamId, String userId) {
        logger.info("Delete user " + userId + " from team " + teamId);
        TeamString salida = new TeamString();
        Team team = getTeamFromId(teamId);
        if (team == null) {
            logger.error("Team " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return "TEAM NOT FOUND";
        }

        User user = userService.getUserById(Long.valueOf(userId));
        if (user == null) {
            logger.error("User " + userId + " not found");
            salida.setSalida("USER NOT FOUND");
            return "USER NOT FOUND";
        }

        if (!team.getParticipantes().contains(user)) {
            logger.error("User " + userId + " already in team " + teamId);
            salida.setSalida("USER ALREADY IN TEAM");
            return "USER IS NOT IN TEAM";
        }

        team.getParticipantes().remove(user);
        teamRepository.save(team);
        logger.info("Finish delete user " + userId + " from team " + teamId);
        return "OK";
    }

    public TeamString updateTeam(String teamId, Optional<String> teamName) {
        logger.info("Update team " + teamId);
        TeamString salida = new TeamString();
        Team team = getTeamFromId(teamId);
        if (team == null) {
            logger.error("Team " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }

        if (teamName.isPresent()) {
            if (teamRepository.existsByNombreEquipo(teamName.get())) {
                logger.error("Team " + teamName.get() + " duplicated");
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
        logger.info("Finish update team " + teamId);
        return salida;
    }
}
