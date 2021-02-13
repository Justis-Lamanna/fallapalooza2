package com.github.lucbui.birb.obj;

import lombok.Data;

import java.util.List;

@Data
public class TournamentRound {
    private final List<TournamentMatchup> tournamentMatchups;
}
