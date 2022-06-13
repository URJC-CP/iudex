package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.Team;
import es.urjc.etsii.grafo.iudex.entities.TeamUser;
import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.pojos.TeamString;
import es.urjc.etsii.grafo.iudex.pojos.UserString;
import es.urjc.etsii.grafo.iudex.repositories.TeamRepository;
import es.urjc.etsii.grafo.iudex.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserAndTeamService {
    private static final Logger logger = LoggerFactory.getLogger(UserAndTeamService.class);

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public UserAndTeamService(UserRepository userRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    //Cuando se crea un usuario tambien se creara un equipo con el mismo nombre que el usuario
    public UserString crearUsuario(String nickname, String mail) {
        logger.debug("Create user {}", nickname);
        UserString userString = new UserString();
        //Comprobamos que el usuario sea unico
        if (userRepository.existsUserByNickname(nickname)) {
            logger.error("User nickname duplicated");
            userString.setSalida("USER NICKNAME DUPLICATED");
            return userString;
        } else if (userRepository.existsUserByEmail(mail)) {
            logger.error("User mail duplicated");
            userString.setSalida("USER MAIL DUPLICATED");
            return userString;
        }

        User user = new User(nickname, mail);
        userRepository.save(user);
        String salidaCreaTeam = crearTeam(nickname, true).getSalida();

        if (salidaCreaTeam.equals("OK")) {
            //Sacamos el equipo creado anteriormente
            Optional<Team> teamOptional = teamRepository.findByNombreEquipo(nickname);
            Team team = teamOptional.orElseThrow();

            TeamUser teamUser = new TeamUser(team,user, LocalDateTime.now());

            user.addTeam(teamUser);

            //Anyadimos el user al equipo

            addUserToTeam(team, user);
            userRepository.save(user);
        } else {
            userString.setSalida(salidaCreaTeam);
            return userString;
        }

        userString.setSalida("OK");
        userString.setUser(user);
        logger.debug("Finish create user {}", nickname);
        return userString;
    }


    public String deleteUserByNickname(String nickname) {
        logger.debug("Delete user {}", nickname);
        Optional<User> userOptional = userRepository.findByNickname(nickname);
        if (userOptional.isEmpty()) {
            logger.error("User {} not found", nickname);
            return "USER NOT FOUND";
        }
        User user = userOptional.get();

        //borramos el usuario
        userRepository.delete(user);
        //borramos el equipo del usuario
        Optional<Team> teamOptional = teamRepository.findByNombreEquipo(nickname);
        Team team = teamOptional.orElseThrow();
        teamRepository.delete(team);

        logger.debug("Finish delete user {} with id {}", nickname, user.getId());
        return "OK";
    }

    public UserString updateUser(String userId, Optional<String> nickname, Optional<String> mail) {
        logger.debug("Update user {}", userId);
        UserString userString = new UserString();

        Optional<User> userOptional = userRepository.findUserById(Long.parseLong(userId));
        if (userOptional.isEmpty()) {
            logger.error("User {} not found", userId);
            userString.setSalida("USER NOT FOUND");
            return userString;
        }
        User user = userOptional.get();

        if (nickname.isPresent()) {
            if (existsUserByNickname(nickname.get())) {
                logger.error("User nickname duplicated");
                userString.setSalida("USER NICKNAME DUPLICATED");
                return userString;
            }
            user.setNickname(nickname.get());
        }

        if (mail.isPresent()) {
            if (existsUserByMail(mail.get())) {
                logger.error("User mail duplicated");
                userString.setSalida("USER MAIL DUPLICATED");
                return userString;
            }
            user.setEmail(mail.get());
        }

        userRepository.save(user);
        userString.setSalida("OK");
        userString.setUser(user);

        logger.debug("Finish update user {}", userId);
        return userString;
    }

    private boolean existsUserByMail(String email) {
        return userRepository.existsUserByEmail(email);
    }

    public Optional<User> getUserById(long userId) {
        return userRepository.findUserById(userId);
    }

    public boolean existsUserByNickname(String nickname) {
        return userRepository.existsUserByNickname(nickname);
    }


    public TeamString crearTeam(String nickTeam, boolean isUser) {
        logger.debug("Create team {}", nickTeam);
        TeamString salida = new TeamString();

        Team team = new Team(nickTeam);
        team.setEsUser(isUser);
        if (teamRepository.existsTeamByNombreEquipo(nickTeam)) {
            logger.error("Team name duplicated");
            salida.setSalida("TEAM NAME DUPLICATED");
        } else {
            teamRepository.save(team);
            salida.setSalida("OK");
            salida.setTeam(team);
            logger.debug("Finish create team {}", nickTeam);
        }
        return salida;
    }

    public String addUserToTeam(Team team, User user) {
        TeamUser teamUser = new TeamUser(team,user,LocalDateTime.now());
        logger.debug("Add user {} to team {}", user.getId(), team.getId());
        if (teamRepository.existsTeamByParticipantesContains(user)) {
            logger.error("User {} already in team {}", user.getId(), team.getId());
            return "USER ALREADY IN TEAM";
        } else {
            team.addUserToTeam(teamUser);
            teamRepository.save(team);
            logger.debug("Finish add user {} to team {} ", user.getId(), team.getId());
            return "OK";
        }
    }

    public String deleteTeamByName(String name) {
        logger.debug("Delete team {}", name);
        Optional<Team> teamOptional = teamRepository.findByNombreEquipo(name);
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", name);
            return "TEAM NOT FOUND";
        }
        Team team = teamOptional.get();

        //Si es un team user borramos el user
        if (team.isEsUser()) {
            this.deleteUserByNickname(team.getNombreEquipo());
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
            this.deleteUserByNickname(team.getNombreEquipo());
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

        Optional<User> userOptional = this.getUserById(Long.parseLong(userId));
        if (userOptional.isEmpty()) {
            logger.error("User {} not found", userId);
            salida.setSalida("USER NOT FOUND");
            return salida;
        }
        User user = userOptional.get();

        TeamUser teamUser = new TeamUser(team,user,LocalDateTime.now());

        if (team.getParticipantes().contains(teamUser)) {
            logger.error("User {} already in team {}", userId, teamId);
            salida.setSalida("USER ALREADY IN TEAM");
            return salida;
        }

        team.getParticipantes().add(teamUser);
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

        Optional<User> userOptional = this.getUserById(Long.parseLong(userId));
        if (userOptional.isEmpty()) {
            logger.error("User {} not found", userId);
            salida.setSalida("USER NOT FOUND");
            return salida;
        }
        User user = userOptional.get();

        TeamUser teamUser = new TeamUser(team,user,LocalDateTime.now());

        if (!team.getParticipantes().contains(teamUser)) {
            logger.error("User {} already in team {}", userId, teamId);
            salida.setSalida("USER NOT IN TEAM");
            return salida;
        }

        team.getParticipantes().remove(teamUser);
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
