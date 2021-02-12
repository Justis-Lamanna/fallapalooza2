package com.github.lucbui.birb.config;

import lombok.Data;

@Data
public class TeamPlayerConfig {
    private SingleRowColumnConfig name;
    private SingleRowColumnConfig pronouns;
    private SingleRowColumnConfig discord;
    private SingleRowColumnConfig crowns;
    private SingleRowColumnConfig achievement;
    private SingleRowColumnConfig twitter;
    private SingleRowColumnConfig twitch;
}
