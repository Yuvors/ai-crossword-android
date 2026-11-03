package com.yuval.tashchetz.model;

/**
 * The direction in which an answer runs in the grid.
 *
 * Coordinates: row 0 is the top row, and column 0 is the RIGHTMOST column,
 * because Hebrew is read from right to left. So the next letter of a horizontal
 * answer is at col + 1, and the next letter of a vertical answer is at row + 1.
 */
public enum Direction {

    /** Right to left, as Hebrew is read. */
    HORIZONTAL(0, 1),

    /** Top to bottom. */
    VERTICAL(1, 0);

    /** How many rows to move to get to the next letter. */
    private final int rowStep;

    /** How many columns to move to get to the next letter. */
    private final int colStep;
    Direction(int rowStep, int colStep) {
        this.rowStep = rowStep;
        this.colStep = colStep;
    }

    public int getRowStep() {
        return rowStep;
    }

    public int getColStep() {
        return colStep;
    }
}
