package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Entities.User;
import com.example.aplicacion.Repository.LanguageRepository;
import com.example.aplicacion.Repository.TeamRepository;
import com.example.aplicacion.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamService teamService;


    //Cuando se crea un usuario tambien se creara un equipo con el mismo nombre que el usuario
    public String crearUsuario(String nickname, String mail){

        User user = new User(nickname, mail);
        //Comprobamos que el usuario sea unico
        if(userRepository.existsByNickname(nickname)){
            return "USER NICKNAME DUPLICATED";
        }
        else if(userRepository.existsByEmail(mail)){
            return "USER MAIL DUPLICATED";
        }
        else {
            userRepository.save(user);

           String salidaCreaTeam = teamService.crearTeam(nickname, true).getSalida();

           if (salidaCreaTeam.equals("OK")){
               //Sacamos el equipo creado anteriormente
               Team team = teamRepository.findByNombreEquipo(nickname);
               user.addTeam(team);

               //Anyadimos el user al equipo
               teamService.addUserToTeam(team,user);

               userRepository.save(user);
           }
           else{
               return salidaCreaTeam;
           }
        }
        return "OK";
    }


    public String deleteUserByNickname(String nickname){
        User user = userRepository.findByNickname(nickname);
        if(user==null){
            return "USER NOT FOUND";
        }else {
            //borramos el usuario
            userRepository.delete(user);
            //borramos el equipo del usuario
            Team team = teamRepository.findByNombreEquipo(nickname);
            teamRepository.delete(team);
            return "OK";
        }

    }

    public User getUserById(long userId){
        return userRepository.findUserById(userId);
    }

}
