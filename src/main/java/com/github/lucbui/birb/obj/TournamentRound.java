package com.github.lucbui.birb.obj;

import java.util.List;

public class TournamentRound {
    private final String name;
    private final List<TournamentMatchup> tournamentMatchups;

    public TournamentRound(String name, List<TournamentMatchup> tournamentMatchups) {
        this.name = name;
        this.tournamentMatchups = tournamentMatchups;
    }

    public String getName() {
        return name;
    }

    public List<TournamentMatchup> getTournamentMatchups() {
        return tournamentMatchups;
    }

    @Override
    public String toString() {
        return "TournamentRound{" +
                "name='" + name + '\'' +
                ", tournamentMatchups=" + tournamentMatchups +
                '}';
    }
}
