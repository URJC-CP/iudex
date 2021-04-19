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
        logger.debug("Create team/user " + nickTeam);
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
            logger.debug("Finish create team/user " + nickTeam + " id: " + team.getId());
            return salida;
        }
    }

    public String addUserToTeam(Team team, User user) {
        logger.debug("Add user " + user.getId() + " to team " + team.getId());
        if (teamRepository.existsTeamByParticipantesContains(user)) {
            logger.error("User " + user.getId() + " already in team " + team.getId());
            return "USUARIO YA APUNTADO";
        } else {
            team.addUserToTeam(user);
            teamRepository.save(team);
            logger.debug("Finish add user " + user.getId() + " to team " + team.getId());
            return "OK";
        }
    }

    public String deleteTeamByName(String name) {
        logger.debug("Delete team " + name);
        Optional<Team> team = teamRepository.findByNombreEquipo(name);
        if (team.isEmpty()) {
            logger.error("Team/user " + name + " not found");
            return "TEAM NOT FOUND";
        } else {
            //Si es un team user borramos el user
            if (team.get().isEsUser()) {
                userService.deleteUserByNickname(team.get().getNombreEquipo());
            }
            teamRepository.delete(team.get());

            logger.debug("Finish delete team/user " + name + "\nTeam/user id: " + team.get().getId());
            return "OK";
        }
    }

    public String deleteTeamByTeamId(String teamId) {
        logger.debug("Delete team/user " + teamId);
        Optional<Team> team = teamRepository.findTeamById(Long.valueOf(teamId));
        if (team.isEmpty()) {
            logger.error("Team/user " + teamId + " not found");
            return "TEAM NOT FOUND";
        } else {
            //Si es un team user borramos el user
            if (team.get().isEsUser()) {
                userService.deleteUserByNickname(team.get().getNombreEquipo());
            }
            teamRepository.delete(team.get());

            logger.debug("Finish delete team/user " + teamId);
            return "OK";
        }
    }

    public Optional<Team> getTeamByNick(String nick) {
        return teamRepository.findByNombreEquipo(nick);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamFromId(String teamId) {
        return teamRepository.findTeamById(Long.valueOf(teamId));
    }

    public TeamString addUserToTeamUssingIds(String teamId, String userId) {
        logger.debug("Add user " + userId + " to team " + teamId);
        TeamString salida = new TeamString();
        Optional<Team> team = getTeamFromId(teamId);
        if (team.isEmpty()) {
            logger.error("Team " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }

        Optional<User> user = userService.getUserById(Long.valueOf(userId));
        if (user.isEmpty()) {
            logger.error("User " + userId + " not found");
            salida.setSalida("USER NOT FOUND");
            return salida;
        }

        if (team.get().getParticipantes().contains(user.get())) {
            logger.error("User " + userId + " already in team " + teamId);
            salida.setSalida("USER ALREADY IN TEAM");
            return salida;
        }

        team.get().getParticipantes().add(user.get());

        teamRepository.save(team.get());

        salida.setSalida("OK");
        salida.setTeam(team.get());
        logger.debug("Finish add user " + userId + " to team " + teamId);
        return salida;
    }

    public TeamString deleteUserFromTeam(String teamId, String userId) {
        logger.debug("Delete user " + userId + " from team " + teamId);
        TeamString salida = new TeamString();
        Optional<Team> team = getTeamFromId(teamId);
        if (team.isEmpty()) {
            logger.error("Team " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }

        Optional<User> user = userService.getUserById(Long.valueOf(userId));
        if (user.isEmpty()) {
            logger.error("User " + userId + " not found");
            salida.setSalida("USER NOT FOUND");
            return salida;
        }

        if (!team.get().getParticipantes().contains(user.get())) {
            logger.error("User " + userId + " already in team " + teamId);
            salida.setSalida("USER IS NOT IN TEAM");
            return salida;
        }

        team.get().getParticipantes().remove(user.get());
        teamRepository.save(team.get());

        logger.debug("Finish delete user " + userId + " from team " + teamId);
        salida.setSalida("OK");
        return salida;
    }

    public TeamString updateTeam(String teamId, Optional<String> teamName) {
        logger.debug("Update team " + teamId);
        TeamString salida = new TeamString();
        Optional<Team> team = getTeamFromId(teamId);
        if (team.isEmpty()) {
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
                team.get().setNombreEquipo(teamName.get());
                teamRepository.save(team.get());
            }
        }

        teamRepository.save(team.get());
        salida.setSalida("OK");
        salida.setTeam(team.get());
        logger.debug("Finish update team " + teamId);

        return salida;
    }
}
