package com.github.lucbui.birb.obj;

import java.util.Arrays;

public enum RoundState {
    NP,
    TBP,
    DRAW,
    WIN,
    LOSS,
    IP,
    BREAK;

    public boolean isNotStarted() {
        return this == NP || this == TBP;
    }

    public boolean isInProgress() {
        return this == IP || this == DRAW || this == BREAK;
    }

    public boolean isCompleteState() {
        return this == WIN || this == LOSS;
    }

    public static RoundState parse(String s) {
        if(s == null) {
            return IP;
        }
        String normalized = s.trim();
        try {
            return RoundState.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            char firstLetter = Character.toUpperCase(normalized.charAt(0));
            return Arrays.stream(RoundState.values())
                    .filter(rs -> rs.name().charAt(0) == firstLetter)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No round state for string " + s));
        }
    }
}
