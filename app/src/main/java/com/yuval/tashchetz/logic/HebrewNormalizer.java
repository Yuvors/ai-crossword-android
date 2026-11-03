package com.yuval.tashchetz.logic;

/**
 * Converts Hebrew words to the form they take in the arrow-word grid:
 * regular letters only — no final letters, no niqqud, no spaces and no punctuation.
 * Examples: "שָׁלוֹם" → "שלומ", "בית ספר" → "ביתספר", "ג'ירפה" → "גירפה".
 *
 * Utility class: all methods are static, so there is no need to create an instance.
 */
public final class HebrewNormalizer {

    /** First letter of the Hebrew block in Unicode (U+05D0). */
    private static final char FIRST_LETTER = 'א';

    /** Last letter of the Hebrew block in Unicode (U+05EA). */
    private static final char LAST_LETTER = 'ת';

    /** Start of the niqqud and cantillation marks range in Unicode (U+0591). */
    private static final char FIRST_MARK = 0x0591;

    /** End of the niqqud and cantillation marks range in Unicode (U+05C7). */
    private static final char LAST_MARK = 0x05C7;

    /** Symbols that may appear in an answer from the AI and are simply removed. */
    private static final String IGNORED_SYMBOLS = " '\"-׳״";

    /** Minimum length of an answer in the puzzle. */
    public static final int MIN_ANSWER_LENGTH = 2;

    /** Private constructor: a utility class cannot be instantiated. */
    private HebrewNormalizer() {
    }

    /**
     * Checks whether the character is a Hebrew letter (including final letters).
     */
    public static boolean isHebrewLetter(char c) {
        return c >= FIRST_LETTER && c <= LAST_LETTER;
    }

    /**
     * Returns the regular letter that matches a final letter. Any other character is returned as is.
     * Also used by the game screen: if the user types "ם", the grid shows "מ".
     */
    public static char toRegular(char c) {
        switch (c) {
            case 'ך':
                return 'כ';
            case 'ם':
                return 'מ';
            case 'ן':
                return 'נ';
            case 'ף':
                return 'פ';
            case 'ץ':
                return 'צ';
            default:
                return c;
        }
    }

    /**
     * Returns only the letters of the text, with every final letter turned into a regular one.
     * Every character that is not a Hebrew letter is removed. For null, returns an empty string.
     */
    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isHebrewLetter(c)) {
                result.append(toRegular(c));
            }
        }
        return result.toString();
    }

    /**
     * Checks whether an answer from the AI fits the puzzle: it contains only Hebrew letters,
     * niqqud and allowed symbols (space, geresh, hyphen), and at least MIN_ANSWER_LENGTH letters.
     * An answer with digits or Latin letters is rejected, instead of those characters silently disappearing.
     */
    public static boolean isValidAnswer(String text) {
        if (text == null) {
            return false;
        }
        int letterCount = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isHebrewLetter(c)) {
                letterCount++;
            } else if (!isIgnorable(c)) {
                return false;
            }
        }
        return letterCount >= MIN_ANSWER_LENGTH;
    }

    /**
     * Checks whether the character is niqqud, a cantillation mark or an allowed symbol, which is removed.
     */
    private static boolean isIgnorable(char c) {
        boolean isMark = c >= FIRST_MARK && c <= LAST_MARK;
        return isMark || IGNORED_SYMBOLS.indexOf(c) >= 0;
    }
}
