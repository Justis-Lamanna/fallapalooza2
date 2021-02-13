package com.github.lucbui.birb.config;

import lombok.Data;

@Data
public class FallapaloozaConfig {
    private String spreadsheetId;

    private int numberOfTeams;
    private int playersPerTeam;

    private TeamCardConfig teamCard;
    private BracketConfig bracket;

    public int getNumberOfRounds() {
        return teamCard.getRound().getEpisodeCount().size();
    }

    public int getEpisodeCountForRound(int idx) {
        return teamCard.getRound().getEpisodeCount().get(idx);
    }
}
