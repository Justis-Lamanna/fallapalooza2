package com.github.lucbui.birb.config;

import lombok.Data;

@Data
public class TeamConfig {
    private SingleCellConfig name;
    private SingleCellConfig color;
    private SingleCellConfig server;
    private SingleCellConfig display;
    private SingleCellConfig seed;
}
