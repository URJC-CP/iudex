package com.example.aplicacion.utils;

import com.example.aplicacion.Pojos.TeamScore;
import com.google.common.primitives.Floats;

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
