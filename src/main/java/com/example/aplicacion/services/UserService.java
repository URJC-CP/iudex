package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Entities.User;
import com.example.aplicacion.Pojos.UserString;
import com.example.aplicacion.Repository.TeamRepository;
import com.example.aplicacion.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamService teamService;

    //Cuando se crea un usuario tambien se creara un equipo con el mismo nombre que el usuario
    public UserString crearUsuario(String nickname, String mail) {
        logger.debug("Create user with name " + nickname + " and mail " + mail);
        UserString userString = new UserString();
        //Comprobamos que el usuario sea unico
        if (userRepository.existsByNickname(nickname)) {
            logger.error("User " + nickname + " duplicated");
            userString.setSalida("USER NICKNAME DUPLICATED");
            return userString;
        } else if (userRepository.existsByEmail(mail)) {
            logger.error("User mail " + mail + " duplicated");
            userString.setSalida("USER MAIL DUPLICATED");
            return userString;
        }

        User user = new User(nickname, mail);
        userRepository.save(user);
        String salidaCreaTeam = teamService.crearTeam(nickname, true).getSalida();

        if (salidaCreaTeam.equals("OK")) {
            //Sacamos el equipo creado anteriormente
            Optional<Team> teamOptional = teamRepository.findByNombreEquipo(nickname);
            Team team = teamOptional.get();
            user.addTeam(team);
            //Anyadimos el user al equipo
            teamService.addUserToTeam(team, user);
            userRepository.save(user);
        } else {
            userString.setSalida(salidaCreaTeam);
            return userString;
        }

        userString.setSalida("OK");
        userString.setUser(user);
        logger.debug("Finish create user with name " + nickname + " and mail " + mail);
        return userString;
    }


    public String deleteUserByNickname(String nickname) {
        logger.debug("Delete user " + nickname);
        Optional<User> userOptional = userRepository.findByNickname(nickname);
        if (userOptional.isEmpty()) {
            logger.error("User " + nickname + " not found");
            return "USER NOT FOUND";
        }
        User user = userOptional.get();

        //borramos el usuario
        userRepository.delete(user);
        //borramos el equipo del usuario
        Optional<Team> teamOptional = teamRepository.findByNombreEquipo(nickname);
        Team team = teamOptional.get();
        teamRepository.delete(team);

        logger.debug("Finish delete user " + nickname + ", user id " + user.getId());
        return "OK";
    }

    public UserString updateUser(String userId, Optional<String> nickname, Optional<String> mail) {
        logger.debug("Update user " + userId);
        UserString userString = new UserString();

        Optional<User> userOptional = userRepository.findUserById(Long.parseLong(userId));
        if (userOptional.isEmpty()) {
            logger.error("User " + userId + " not found");
            userString.setSalida("USER NOT FOUND");
            return userString;
        }
        User user = userOptional.get();

        if (nickname.isPresent()) {
            if (!(userRepository.findByNickname(nickname.get()) == null)) {
                logger.error("User nickname " + nickname + " already in use");
                userString.setSalida("USER ALREADY IN USE");
                return userString;
            }

            user.setNickname(nickname.get());
            userRepository.save(user);
        }

        if (mail.isPresent()) {
            user.setEmail(mail.get());
        }

        userRepository.save(user);
        userString.setSalida("OK");
        userString.setUser(user);

        logger.debug("Finish update user " + userId);
        return userString;
    }

    public Optional<User> getUserById(long userId) {
        return userRepository.findUserById(userId);
    }

    public boolean existsUserByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
