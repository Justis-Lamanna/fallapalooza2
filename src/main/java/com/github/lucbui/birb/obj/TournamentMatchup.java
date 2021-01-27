package com.github.lucbui.birb.obj;

public class TournamentMatchup {
    private final Score teamOne;
    private final Score teamTwo;

    public TournamentMatchup(Score teamOne, Score teamTwo) {
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
    }

    public Score getTeamOne() {
        return teamOne;
    }

    public Score getTeamTwo() {
        return teamTwo;
    }

    @Override
    public String toString() {
        return "TournamentMatchup{" +
                "teamOne=" + teamOne +
                ", teamTwo=" + teamTwo +
                '}';
    }

    public static class Score {
        private final String name;
        private final Integer score;

        public Score(String name, Integer score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public Integer getScore() {
            return score;
        }

        @Override
        public String toString() {
            return "Score{" +
                    "name='" + name + '\'' +
                    ", score=" + score +
                    '}';
        }
    }
}
