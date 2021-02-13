package com.github.lucbui.birb.obj;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public class Round {
    private final int number;
    private final int total;
    private final RoundState roundState;
    private final List<Score> scores;

    public boolean isFinal() {
        return number == 4;
    }

    public boolean isEmpty() {
        return scores.stream()
                .flatMap(score -> Arrays.stream(score.getScore()))
                .allMatch(Objects::isNull);
    }

    public boolean isFull() {
        return scores.stream()
                .flatMap(score -> Arrays.stream(score.getScore()).limit(isFinal() ? 5 : 3))
                .allMatch(Objects::nonNull);
    }
}
