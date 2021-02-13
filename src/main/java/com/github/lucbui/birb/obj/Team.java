package com.github.lucbui.birb.obj;

import lombok.Data;

import java.util.*;

@Data
public class Team {
    private final String name;
    private final Integer display;
    private final Integer seed;
    private final List<Player> players;
    private final List<Round> rounds;

    public Round getCurrentRound() {
        for(Round round : rounds) {
            RoundState roundState = round.getRoundState();
            if(roundState.isInProgress() || roundState.isNotStarted()) {
                return round;
            }
        }
        //Last round
        return rounds.get(rounds.size() - 1);
    }

    public Round getLastPlayedRound() {
        for(int idx = 0; idx < rounds.size(); idx++) {
            Round round = rounds.get(idx);
            RoundState roundState = round.getRoundState();
            if(roundState.isInProgress() || roundState.isNotStarted()) {
                if(round.isEmpty()) {
                    return rounds.get(Math.max(0, idx - 1));
                } else {
                    return round;
                }
            }
        }
        //Last round
        return rounds.get(rounds.size() - 1);
    }

    public int getCurrentEpisode(Round round) {
        int scoreCount = round.isFinal() ? 5 : 3;
        return round.getScores().stream()
                .map(Score::getScore)
                .mapToInt(scores -> {
                    for(int ep = 0; ep < scoreCount; ep++) {
                        if(scores[ep] == null) {
                            return ep + 1;
                        }
                    }
                    return scoreCount;
                })
                .min()
                .orElse(1);
    }
}
