package com.github.lucbui.birb.obj;

import java.util.List;

public class Round {
    private final int number;
    private final int total;
    private final RoundState roundState;
    private final List<Score> scores;

    public Round(int number, int total, RoundState roundState, List<Score> scores) {
        this.number = number;
        this.total = total;
        this.scores = scores;
        this.roundState = roundState;
    }

    public int getNumber() {
        return number;
    }

    public int getTotal() {
        return total;
    }

    public List<Score> getScores() {
        return scores;
    }

    public RoundState getRoundState() {
        return roundState;
    }

    public boolean isFinal() {
        return number == 4;
    }

    @Override
    public String toString() {
        return "Round{" +
                "number=" + number +
                ", total=" + total +
                ", roundState=" + roundState +
                ", scores=" + scores +
                '}';
    }
}
