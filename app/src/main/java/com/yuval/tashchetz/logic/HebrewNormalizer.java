package com.yuval.tashchetz.logic;

/**
 * ממירה מילים בעברית לצורה שבה הן נכתבות בגריד של התשחץ:
 * רק אותיות רגילות — בלי סופיות, בלי ניקוד, בלי רווחים ובלי סימני פיסוק.
 * לדוגמה: "שָׁלוֹם" → "שלומ", "בית ספר" → "ביתספר", "ג'ירפה" → "גירפה".
 *
 * מחלקת עזר: כל הפעולות סטטיות, ואין צורך ליצור ממנה עצם.
 *
 * הקוד נכתב בעזרת AI (Claude) ומוסבר בשיעור docs/learning/02-hebrew-normalizer.md.
 */
public final class HebrewNormalizer {

    /** האות הראשונה בטבלת יוניקוד של העברית (U+05D0). */
    private static final char FIRST_LETTER = 'א';

    /** האות האחרונה בטבלת יוניקוד של העברית (U+05EA). */
    private static final char LAST_LETTER = 'ת';

    /** תחילת טווח הניקוד וטעמי המקרא ביוניקוד (U+0591). */
    private static final char FIRST_MARK = '֑';

    /** סוף טווח הניקוד וטעמי המקרא ביוניקוד (U+05C7). */
    private static final char LAST_MARK = 'ׇ';

    /** תווים שמותר שיופיעו בתשובה מה-AI, ופשוט מוחקים אותם. */
    private static final String IGNORED_SYMBOLS = " '\"-׳״";

    /** אורך מינימלי של תשובה בתשחץ. */
    public static final int MIN_ANSWER_LENGTH = 2;

    /** בנאי פרטי: אי אפשר ליצור עצם ממחלקת עזר. */
    private HebrewNormalizer() {
    }

    /**
     * בודקת אם התו הוא אות עברית (כולל סופיות).
     */
    public static boolean isHebrewLetter(char c) {
        return c >= FIRST_LETTER && c <= LAST_LETTER;
    }

    /**
     * מחזירה את האות הרגילה שמתאימה לאות סופית. כל תו אחר חוזר כמו שהוא.
     * משמשת גם במסך המשחק: אם המשתמש מקליד "ם", בגריד נכתב "מ".
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
     * מחזירה רק את האותיות של הטקסט, כשכל סופית הופכת לרגילה.
     * כל תו שאינו אות עברית נמחק. עבור null מוחזרת מחרוזת ריקה.
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
     * בודקת אם תשובה שהגיעה מה-AI מתאימה לתשחץ: מכילה רק אותיות עבריות,
     * ניקוד ותווים מותרים (רווח, גרש, מקף), ויש בה לפחות MIN_ANSWER_LENGTH אותיות.
     * תשובה עם ספרות או אותיות לועזיות נפסלת, במקום שהאותיות האלה ייעלמו בשקט.
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
     * בודקת אם התו הוא ניקוד, טעם מקרא או סימן פיסוק מותר, שאותו מוחקים.
     */
    private static boolean isIgnorable(char c) {
        boolean isMark = c >= FIRST_MARK && c <= LAST_MARK;
        return isMark || IGNORED_SYMBOLS.indexOf(c) >= 0;
    }
}
