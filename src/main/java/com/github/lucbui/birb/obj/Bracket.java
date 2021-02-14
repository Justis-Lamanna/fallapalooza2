package com.github.lucbui.birb.obj;

import lombok.Data;

import java.util.List;

@Data
public class Bracket {
    private final List<BracketRound> rounds;
}
