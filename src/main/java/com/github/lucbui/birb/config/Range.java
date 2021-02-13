package com.github.lucbui.birb.config;

public class Range {
    private final Point start;
    private final Point end;

    public Range(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public String toExcel() {
        return start.toExcel() + ":" + end.toExcel();
    }

    public String toExcelWithSheet(String sheet) {
        return sheet + "!" + toExcel();
    }
}
