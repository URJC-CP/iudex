package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Entities.User;
import com.example.aplicacion.Pojos.UserString;
import com.example.aplicacion.Repository.LanguageRepository;
import com.example.aplicacion.Repository.TeamRepository;
import com.example.aplicacion.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamService teamService;


    //Cuando se crea un usuario tambien se creara un equipo con el mismo nombre que el usuario
    public UserString crearUsuario(String nickname, String mail){
        UserString userString = new UserString();
        User user = new User(nickname, mail);
        //Comprobamos que el usuario sea unico
        if(userRepository.existsByNickname(nickname)){
            userString.setSalida("USER NICKNAME DUPLICATED");
            return userString;
        }
        else if(userRepository.existsByEmail(mail)){
            userString.setSalida("USER MAIL DUPLICATED");
            return userString;
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
               userString.setSalida(salidaCreaTeam);
               return userString;
           }
        }
        userString.setSalida("OK");
        userString.setUser(user);
        return userString;
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

    public UserString updateUser(String userId, Optional<String> nickname, Optional<String> mail){
        UserString userString = new UserString();
        User user = userRepository.findUserById(Long.valueOf(userId));
        if(user==null){
            userString.setSalida("USER NOT FOUND");
            return userString;
        }

        if(nickname.isPresent()){
            if(!(userRepository.findByNickname(nickname.get()) == null)){
                userString.setSalida("USER ALREADY IN USE");
                return userString;
            }
            else {
                user.setNickname(nickname.get());
                userRepository.save(user);
            }
        }
        if(mail.isPresent()){
            user.setEmail(mail.get());
        }

        userRepository.save(user);
        userString.setSalida("OK");
        userString.setUser(user);
        return userString;
    }

    public User getUserById(long userId){
        return userRepository.findUserById(userId);
    }

}
