package com.github.lucbui.birb.config;

import lombok.Data;

@Data
public class FallapaloozaConfig {
    private String spreadsheetId;
    private String scorecardSheetName;
    private String bracketSheetName;

    private int numberOfTeams;
    private int playersPerTeam;
    private RoundConfig[] rounds;

    private SingleCellConfig origin;
    private TeamCardConfig teamCard;

    public int getNumberOfRounds() {
        return rounds.length;
    }

    public RoundConfig getRoundConfig(int round) {
        return rounds[round];
    }
}
