package com.yuval.tashchetz.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for HebrewNormalizer. They run on the computer, without an emulator.
 */
public class HebrewNormalizerTest {

    /** Kamatz — a niqqud mark (U+05B8), not a letter. */
    private static final char KAMATZ = 0x05B8;

    @Test
    public void normalize_turnsFinalLettersToRegular() {
        assertEquals("שלומ", HebrewNormalizer.normalize("שלום"));
        assertEquals("כנפ", HebrewNormalizer.normalize("כנף"));
        assertEquals("ארצ", HebrewNormalizer.normalize("ארץ"));
        assertEquals("דרכ", HebrewNormalizer.normalize("דרך"));
        assertEquals("גנ", HebrewNormalizer.normalize("גן"));
    }

    @Test
    public void normalize_keepsRegularLettersInTheMiddle() {
        // Only final letters change; a regular "מ" in the middle of the word stays as is
        assertEquals("ממלכה", HebrewNormalizer.normalize("ממלכה"));
    }

    @Test
    public void normalize_removesNiqqud() {
        assertEquals("שלומ", HebrewNormalizer.normalize("שָׁלוֹם"));
    }

    @Test
    public void normalize_removesSpacesAndPunctuation() {
        assertEquals("ביתספר", HebrewNormalizer.normalize("בית ספר"));
        assertEquals("גירפה", HebrewNormalizer.normalize("ג'ירפה"));
        assertEquals("גירפה", HebrewNormalizer.normalize("ג׳ירפה"));
        assertEquals("צהל", HebrewNormalizer.normalize("צה\"ל"));
        assertEquals("בנדוד", HebrewNormalizer.normalize("בן-דוד"));
        assertEquals("בנדוד", HebrewNormalizer.normalize("בן־דוד")); // Hebrew maqaf
    }

    @Test
    public void normalize_nullOrEmpty_returnsEmpty() {
        assertEquals("", HebrewNormalizer.normalize(null));
        assertEquals("", HebrewNormalizer.normalize(""));
    }

    @Test
    public void toRegular_changesOnlyFinalLetters() {
        assertEquals('מ', HebrewNormalizer.toRegular('ם'));
        assertEquals('א', HebrewNormalizer.toRegular('א'));
        assertEquals('x', HebrewNormalizer.toRegular('x'));
    }

    @Test
    public void isHebrewLetter_checksTheRange() {
        assertTrue(HebrewNormalizer.isHebrewLetter('א'));
        assertTrue(HebrewNormalizer.isHebrewLetter('ת'));
        assertTrue(HebrewNormalizer.isHebrewLetter('ם'));
        assertFalse(HebrewNormalizer.isHebrewLetter('a'));
        assertFalse(HebrewNormalizer.isHebrewLetter('5'));
        assertFalse(HebrewNormalizer.isHebrewLetter(KAMATZ));
    }

    @Test
    public void isValidAnswer_acceptsHebrewWithAllowedSymbols() {
        assertTrue(HebrewNormalizer.isValidAnswer("שלום"));
        assertTrue(HebrewNormalizer.isValidAnswer("שָׁלוֹם"));
        assertTrue(HebrewNormalizer.isValidAnswer("בית ספר"));
        assertTrue(HebrewNormalizer.isValidAnswer("ג'ירפה"));
    }

    @Test
    public void isValidAnswer_rejectsDigitsAndLatin() {
        assertFalse(HebrewNormalizer.isValidAnswer("שלום1"));
        assertFalse(HebrewNormalizer.isValidAnswer("הtest"));
    }

    @Test
    public void isValidAnswer_rejectsTooShort() {
        assertFalse(HebrewNormalizer.isValidAnswer("א"));
        assertFalse(HebrewNormalizer.isValidAnswer("'-"));
        assertFalse(HebrewNormalizer.isValidAnswer(""));
        assertFalse(HebrewNormalizer.isValidAnswer(null));
    }
}
