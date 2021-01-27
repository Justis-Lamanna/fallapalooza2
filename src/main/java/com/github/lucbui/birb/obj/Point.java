package com.github.lucbui.birb.obj;

public class Point {
    private final int row;
    private final int col;

    public Point(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Point add(Point other) {
        return new Point(this.row + other.row, this.col + other.col);
    }

    public Point addRow(int row) {
        return new Point(this.row + row, this.col);
    }

    public Point addCol(int col) {
        return new Point(this.row, this.col + col);
    }

    public Range to(Point other) {
        return new Range(this, other);
    }

    public Range toRow(int relativeRow) {
        return new Range(this, addRow(relativeRow));
    }

    public Range toCol(int relativeCol) {
        return new Range(this, addCol(relativeCol));
    }

    public String toExcel() {
        return getExcelColumnFromNumber(col) + row;
    }

    public String toExcelWithSheet(String sheet) {
        return sheet + "!" + toExcel();
    }

    private String getExcelColumnFromNumber(int number) {
        StringBuilder s = new StringBuilder();
        while (number > 0) {
            int remainder = (number - 1) % 26;
            number = (number - 1) / 26;
            s.insert(0, (char)(65 + remainder));
        }
        return s.toString();
    }

    @Override
    public String toString() {
        return "Point{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
