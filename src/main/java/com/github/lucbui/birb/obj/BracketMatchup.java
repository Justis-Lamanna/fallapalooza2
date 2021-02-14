package com.github.lucbui.birb.obj;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class BracketMatchup {
    private final Team teamOne;
    private final Team teamTwo;

    @Data
    public static class Team {
        public static final Team EMPTY_TEAM = new Team("",Collections.emptyList(), Round.EMPTY_ROUND);

        private final String name;
        private final List<Player> players;
        private final Round thisRound;
    }
}
