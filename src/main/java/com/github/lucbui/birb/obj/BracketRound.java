package com.github.lucbui.birb.obj;

import lombok.Data;

import java.util.List;

@Data
public class BracketRound {
    private final String name;
    private final List<BracketMatchup> matchups;
}
