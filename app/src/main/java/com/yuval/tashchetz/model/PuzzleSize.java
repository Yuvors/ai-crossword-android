package com.yuval.tashchetz.model;

/**
 * The two fixed grid sizes the user can choose from.
 */
public enum PuzzleSize {

    /** 10×10 grid. */
    SMALL(10),

    /** 15×15 grid. */
    LARGE(15);

    /** Number of rows, which is also the number of columns (the grid is square). */
    private final int size;

    PuzzleSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
