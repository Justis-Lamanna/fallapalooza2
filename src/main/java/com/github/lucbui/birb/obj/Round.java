package com.github.lucbui.birb.obj;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
public class Round {
    public static final Round EMPTY_ROUND = new Round(-1, 0, RoundState.NP,
            "1/3", "Round 1",
            Collections.nCopies(2, new Score(new Integer[]{0, 0, 0, 0, 0})));

    private final int number;
    private final int total;
    private final RoundState roundState;
    private final String roundBadge;
    private final String roundNameBadge;
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

    public int getCurrentEpisode() {
        return scores.stream()
                .mapToInt(score -> {
                    for(int episode = 0; episode < score.getScore().length; episode++) {
                        if(score.getScore()[episode] == null || score.getScore()[episode] == 0) {
                            return episode;
                        }
                    }
                    return score.getScore().length - 1;
                })
                .max()
                .orElse(1);
    }
}
