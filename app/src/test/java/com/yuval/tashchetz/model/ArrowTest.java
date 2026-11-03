package com.yuval.tashchetz.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for Arrow and Direction.
 * Column 0 is the rightmost column, so "to the left" means col + 1.
 */
public class ArrowTest {

    @Test
    public void direction_horizontalStepsLeft() {
        assertEquals(0, Direction.HORIZONTAL.getRowStep());
        assertEquals(1, Direction.HORIZONTAL.getColStep());
    }

    @Test
    public void direction_verticalStepsDown() {
        assertEquals(1, Direction.VERTICAL.getRowStep());
        assertEquals(0, Direction.VERTICAL.getColStep());
    }

    @Test
    public void left_startsLeftOfClueAndRunsHorizontally() {
        // Clue at (2, 3) → the answer starts at (2, 4)
        assertEquals(2, Arrow.LEFT.getStartRow(2));
        assertEquals(4, Arrow.LEFT.getStartCol(3));
        assertEquals(Direction.HORIZONTAL, Arrow.LEFT.getDirection());
    }

    @Test
    public void down_startsBelowClueAndRunsVertically() {
        assertEquals(3, Arrow.DOWN.getStartRow(2));
        assertEquals(3, Arrow.DOWN.getStartCol(3));
        assertEquals(Direction.VERTICAL, Arrow.DOWN.getDirection());
    }

    @Test
    public void downThenLeft_startsBelowClueAndRunsHorizontally() {
        assertEquals(3, Arrow.DOWN_THEN_LEFT.getStartRow(2));
        assertEquals(3, Arrow.DOWN_THEN_LEFT.getStartCol(3));
        assertEquals(Direction.HORIZONTAL, Arrow.DOWN_THEN_LEFT.getDirection());
    }

    @Test
    public void leftThenDown_startsLeftOfClueAndRunsVertically() {
        assertEquals(2, Arrow.LEFT_THEN_DOWN.getStartRow(2));
        assertEquals(4, Arrow.LEFT_THEN_DOWN.getStartCol(3));
        assertEquals(Direction.VERTICAL, Arrow.LEFT_THEN_DOWN.getDirection());
    }

    @Test
    public void isBent_onlyForTheTwoBentArrows() {
        assertFalse(Arrow.LEFT.isBent());
        assertFalse(Arrow.DOWN.isBent());
        assertTrue(Arrow.DOWN_THEN_LEFT.isBent());
        assertTrue(Arrow.LEFT_THEN_DOWN.isBent());
    }

    @Test
    public void puzzleSize_hasTheRightSizes() {
        assertEquals(10, PuzzleSize.SMALL.getSize());
        assertEquals(15, PuzzleSize.LARGE.getSize());
    }
}
