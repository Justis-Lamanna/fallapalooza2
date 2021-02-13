package com.github.lucbui.birb.config;

import lombok.Data;

import java.util.List;

@Data
public class TeamRoundConfig {
    private List<Integer> episodeCount;
    private MultiByOriginDirection score;
    private SingleCellConfig total;
    private SingleCellConfig status;
}
