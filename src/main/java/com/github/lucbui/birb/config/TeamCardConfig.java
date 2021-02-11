package com.github.lucbui.birb.config;

import lombok.Data;

@Data
public class TeamCardConfig {
    private int height;
    private SingleCellConfig teamName;
    private SingleCellConfig teamColor;
    private SingleCellConfig teamServer;
    private SingleCellConfig teamDisplay;
    private SingleCellConfig teamSeed;

    private SingleRowColumnConfig playerName;
    private SingleRowColumnConfig playerPronouns;
    private SingleRowColumnConfig playerDiscord;
    private SingleRowColumnConfig playerCrowns;
    private SingleRowColumnConfig playerAchievement;
    private SingleRowColumnConfig playerTwitter;
    private SingleRowColumnConfig playerTwitch;

    private SingleRowColumnConfig roundScore;
    private SingleCellConfig roundTotal;
    private SingleCellConfig roundStatus;
}
