package com.github.lucbui.birb.config;

import lombok.Data;

@Data
public class TeamCardConfig {
    private int height;
    private String sheetName;
    private SingleCellConfig origin;
    private TeamConfig team;
    private TeamPlayerConfig player;
    private TeamRoundConfig round;
}
