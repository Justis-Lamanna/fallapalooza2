package com.github.lucbui.birb.obj;

import lombok.Data;

import java.util.List;

@Data
public class BracketMatchup {
    private final Team teamOne;
    private final Team teamTwo;

    @Data
    public static class Team {
        private final String name;
        private final List<Player> players;
        private final Round thisRound;
    }
}
