package com.yuval.tashchetz.model;

/**
 * Where a puzzle comes from.
 */
public enum PuzzleSource {

    /** The global pool in Firestore, played in order by index. */
    POOL,

    /** A learning puzzle on a topic the user typed. */
    LEARNING,

    /** The daily puzzle, the same for all users. */
    DAILY
}
