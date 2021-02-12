package com.github.lucbui.birb.config;

import com.github.lucbui.birb.obj.Point;
import lombok.Data;

@Data
public class SingleCellConfig {
    private int row;
    private int col;

    public Point toPoint() {
        return new Point(row, col);
    }
}