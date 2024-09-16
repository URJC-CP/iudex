package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.*;
import es.urjc.etsii.grafo.iudex.exceptions.IudexException;
import es.urjc.etsii.grafo.iudex.pojos.TeamString;
import es.urjc.etsii.grafo.iudex.pojos.UserAPI;
import es.urjc.etsii.grafo.iudex.pojos.UserString;
import es.urjc.etsii.grafo.iudex.repositories.TeamRepository;
import es.urjc.etsii.grafo.iudex.repositories.UserRepository;
import es.urjc.etsii.grafo.iudex.repositories.UserTeamRespository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserAndTeamService {
    private static final Logger logger = LoggerFactory.getLogger(UserAndTeamService.class);

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    private final UserTeamRespository userTeamRespository;

    public UserAndTeamService(UserRepository userRepository, TeamRepository teamRepository, UserTeamRespository userTeamRespository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.userTeamRespository = userTeamRespository;
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
        logger.debug("Add user {} to team {}", user.getId(), team.getId());
        if ( userTeamRespository.existsByTeamAndUser(team, user)) {
            logger.error("User {} already in team {}", user.getId(), team.getId());
            return "USER ALREADY IN TEAM";
        } else {
            TeamUser teamUser = new TeamUser(team,user,LocalDateTime.now());
            team.addUserToTeam(teamUser);
            teamRepository.save(teamUser.getTeam());
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

        userTeamRespository.save(teamUser);
        team.getParticipantes().add(teamUser);
        teamRepository.save(team);

        salida.setSalida("OK");
        salida.setTeam(teamRepository.findTeamById(team.getId()).orElseThrow());
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

    public User addRoleToUser(String role, User user) {
        user.getRoles().add(role);
        userRepository.save(user);

        return user;
    }

    public User removeRoleFromUser(String role, User user) {
        user.getRoles().remove(role);
        userRepository.save(user);

        return user;
    }

    public Collection<Long> getTeamIdsFromUser(User user) {
        Collection<TeamUser> teamUsers = user.getEquiposParticipantes();
        return teamUsers.stream().map(TeamUser::getTeam).map(Team::getId).toList();
    }

    public User getUserFromAuthentication(String nickname) throws IudexException {
        if(nickname == null || nickname.isBlank()){
            throw new IudexException("Invalid username extracted from auth token: " + (nickname));
        }

        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        if(optionalUser.isEmpty()){
            // May happen but should be extremely rare, log it
            throw new IudexException("User %s has a signed token but the username does not exist?".formatted(nickname));
        }

        return optionalUser.get();
    }

    public List<Contest> getContestsFromUser(User user) {
        Set<Contest> contests = new HashSet<>();

        contests.addAll(getCreatedContests(user));
        contests.addAll(getParticipatingContests(user));

        return new ArrayList<>(contests);
    }

    public List<Contest> getParticipatingContests(User user) {
        Set<TeamUser> equiposParticipantes = user.getEquiposParticipantes();
        List<Contest> contests = new ArrayList<>();
        for (TeamUser teamUser : equiposParticipantes) {
            for (ContestTeams contestTeam : teamUser.getTeam().getListaContestsParticipados()) {
                contests.add(contestTeam.getContest());
            }
        }

        return contests;
    }

    public List<Contest> getCreatedContests(User user) {
        Set<TeamUser> equiposParticipantes = user.getEquiposParticipantes();
        List<Contest> contests = new ArrayList<>();
        for (TeamUser teamUser : equiposParticipantes) {
            for (ContestTeams contestTeam : teamUser.getTeam().getListaContestsCreados()) {
                contests.add(contestTeam.getContest());
            }
        }

        return contests;
    }

    public List<UserAPI> getAllUsers() {
        return userRepository.findAll().stream().map(User::toUserAPI).toList();
    }

}
