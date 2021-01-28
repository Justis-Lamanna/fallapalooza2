package com.github.lucbui.birb.obj;

import java.util.*;

public class Team {
    private final String name;
    private final Integer display;
    private final Integer seed;
    private final List<Player> players;
    private final List<Round> rounds;

    public Team(String name, Integer display, Integer seed, List<Player> players, List<Round> rounds) {
        this.name = name;
        this.display = display;
        this.seed = seed;
        this.players = players;
        this.rounds = rounds;
    }

    public String getName() {
        return name;
    }

    public Integer getDisplay() {
        return display;
    }

    public Integer getSeed() {
        return seed;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Round> getRounds() {
        return rounds;
    }

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
                boolean isEmpty = round.getScores().stream()
                        .flatMap(score -> Arrays.stream(score.getScore()))
                        .allMatch(Objects::isNull);
                if(isEmpty) {
                    if(idx > 0) {
                        return rounds.get(idx - 1);
                    } else {
                        return rounds.get(0);
                    }
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

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", display=" + display +
                ", seed=" + seed +
                ", players=" + players +
                ", rounds=" + rounds +
                '}';
    }
}
