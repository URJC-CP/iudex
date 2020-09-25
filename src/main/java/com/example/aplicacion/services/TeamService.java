package com.example.aplicacion.services;


import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Entities.User;
import com.example.aplicacion.Repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    public String crearTeam(String nickTeam){

        Team team = new Team(nickTeam);
        if(teamRepository.existsByNombreEquipo(nickTeam)){
            return "TEAM NAME DUPLICATED";
        }
        else {
            teamRepository.save(team);
            return "OK";
        }
    }

    public String addUserToTeam(Team team, User user){

        if(teamRepository.existsTeamByParticipantesContains(user)){
            return "USUARIO YA APUNTADO";
        }
        else {
            team.addUserToTeam(user);
            teamRepository.save(team);
            return "OK";
        }

    }

    public String deleteTeamByName(String name){
        Team team = teamRepository.findByNombreEquipo(name);
        if(team==null){
            return "TEAM NOT FOUND";
        }else {
            teamRepository.delete(team);
            return "OK";
        }
    }
    public Team getTeamByNick(String nick){
        return teamRepository.findByNombreEquipo(nick);
    }
    public List<Team> getAllTeams(){
        return teamRepository.findAll();
    }

}
