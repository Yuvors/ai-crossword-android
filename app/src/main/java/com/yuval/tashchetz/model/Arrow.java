package com.yuval.tashchetz.model;

/**
 * The arrow next to a clue: where the answer starts relative to the clue cell,
 * and in which direction it runs.
 *
 * Straight arrows: the answer continues in the same direction as the step from the clue.
 * Bent arrows: the answer starts next to the clue and then turns. They allow vertical
 * answers in the top row and horizontal answers in the rightmost column, without
 * wasting that row or column on clue cells.
 *
 * Column 0 is the rightmost column (see {@link Direction}), so "to the left" is col + 1.
 */
public enum Arrow {

    /** Starts to the left of the clue and runs left. */
    LEFT(0, 1, Direction.HORIZONTAL),

    /** Starts below the clue and runs down. */
    DOWN(1, 0, Direction.VERTICAL),

    /** Bent: starts below the clue and runs left. */
    DOWN_THEN_LEFT(1, 0, Direction.HORIZONTAL),

    /** Bent: starts to the left of the clue and runs down. */
    LEFT_THEN_DOWN(0, 1, Direction.VERTICAL);

    /** Row of the first letter, relative to the clue cell. */
    private final int startRowOffset;

    /** Column of the first letter, relative to the clue cell. */
    private final int startColOffset;

    /** The direction in which the answer runs. */
    private final Direction direction;

    Arrow(int startRowOffset, int startColOffset, Direction direction) {
        this.startRowOffset = startRowOffset;
        this.startColOffset = startColOffset;
        this.direction = direction;
    }

    public int getStartRowOffset() {
        return startRowOffset;
    }

    public int getStartColOffset() {
        return startColOffset;
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * Returns the row of the first letter of the answer, for a clue in the given row.
     */
    public int getStartRow(int clueRow) {
        return clueRow + startRowOffset;
    }

    /**
     * Returns the column of the first letter of the answer, for a clue in the given column.
     */
    public int getStartCol(int clueCol) {
        return clueCol + startColOffset;
    }

    /**
     * Checks whether the arrow is bent: the step from the clue to the first letter
     * is not in the direction of the answer.
     */
    public boolean isBent() {
        return startRowOffset != direction.getRowStep()
                || startColOffset != direction.getColStep();
    }
}
