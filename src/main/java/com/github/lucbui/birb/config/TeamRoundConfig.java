package com.github.lucbui.birb.config;

import lombok.Data;

@Data
public class TeamRoundConfig {
    private SingleRowColumnConfig score;
    private SingleCellConfig total;
    private SingleCellConfig status;
}
