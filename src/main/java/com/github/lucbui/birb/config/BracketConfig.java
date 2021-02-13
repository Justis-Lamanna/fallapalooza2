package com.github.lucbui.birb.config;

import lombok.Data;

import java.util.List;

@Data
public class BracketConfig {
    private String sheetName;
    private List<BracketCellConfig> cells;
}
