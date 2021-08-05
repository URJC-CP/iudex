package es.urjc.etsii.grafo.iudex.util;

import com.google.common.primitives.Floats;
import es.urjc.etsii.grafo.iudex.pojo.TeamScore;

import java.util.Comparator;

public class TeamScoreComparator implements Comparator<TeamScore> {
    @Override
    public int compare(TeamScore o1, TeamScore o2) {
        int cmp = Integer.compare(o2.getSolvedProblems(), o1.getSolvedProblems());
        if (cmp == 0) {
            return Floats.compare(o1.getScore(), o2.getScore());
        }
        return cmp;
    }
}
