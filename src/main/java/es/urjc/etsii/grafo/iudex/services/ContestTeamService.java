package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.Contest;
import es.urjc.etsii.grafo.iudex.entities.ContestTeams;
import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.repositories.ContestTeamRespository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class ContestTeamService {

    private final ContestTeamRespository contestTeamRespository;

    public ContestTeamService(ContestTeamRespository contestTeamRespository) {
        this.contestTeamRespository = contestTeamRespository;
    }

    public void save(ContestTeams contestTeams) {
        contestTeamRespository.save(contestTeams);
    }

    public Collection<ContestTeams> getContestTeamsByTeamIdIn(Collection<Long> teamIds) {
        return contestTeamRespository.findByTeamsIdIn(teamIds);
    }

    public int countContestByContestTeams(Collection<ContestTeams> contestTeams) {
        return contestTeams.stream()
                .map(ContestTeams::getContest)
                .map(Contest::getId)
                .collect(Collectors.toSet())
                .size();
    }

    public int countContestByTeamIds(Collection<Long> teamIds) {
        return countContestByContestTeams(getContestTeamsByTeamIdIn(teamIds));
    }

    public int countContestByUser(User user) {
        return countContestByTeamIds(user.getEquiposParticipantes().stream()
                .map(teamUser -> teamUser.getTeam().getId())
                .collect(Collectors.toList()));
    }

}
