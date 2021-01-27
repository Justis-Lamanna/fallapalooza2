package com.github.lucbui.birb.obj;

import java.util.Arrays;

public class Score {
    private final Integer[] score;

    public Score(Integer[] score) {
        this.score = score;
    }

    public Integer[] getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Score{" +
                "score=" + Arrays.toString(score) +
                '}';
    }
}
