# ארכיטקטורה — טיוטה שנייה
סטטוס: טיוטה לדיון. ההחלטות הפתוחות מרוכזות בסעיף 8.

## 1. מסכים
| מסך | סוג | תפקיד |
|---|---|---|
| `SplashActivity` | Activity | טעינה, בדיקת התחברות |
| `LoginActivity` | Activity | התחברות (Firebase Auth) — נדרש כדי לשמור התקדמות אישית במאגר |
| `MainActivity` | Activity + ניווט תחתון | מארח את ה-Fragments הראשיים |
| `HomeFragment` | Fragment | "תשחץ הבא" לפי גודל (10×10 / 15×15), המשך תשחץ פתוח |
| `LearnFragment` | Fragment | תשחץ לימודי לפי נושא שהמשתמש מקליד |
| `DailyFragment` | Fragment | התשחץ היומי + טבלת מובילים |
| `MultiplayerFragment` | Fragment | יצירת/הצטרפות לחדר: פתרון משותף או דו-קרב |
| `ProfileFragment` | Fragment | סטטיסטיקות, רצף ימים, הישגים |
| `GameActivity` | Activity | מסך המשחק: הגריד, עם ההגדרות בתוך משבצות; פס עליון שמציג בגדול את ההגדרה של המילה הנבחרת |
| `SettingsActivity` | Activity | הגדרות (SharedPreferences) |

דיאלוגים: רמז, סיום תשחץ + דירוג, דיווח על הגדרה, אישור יציאה, "אין רשת — מצב אופליין".

## 2. חבילות ומחלקות עיקריות

```
model/      Cell (abstract) → LetterCell, ClueCell, BlockedCell; Clue, Word, Arrow, Grid, Puzzle, PuzzleSize
logic/      ArrowGridGenerator, CrossingGraph, PuzzleValidator, HebrewNormalizer, ScoreCalculator
game/       GameMode (abstract) → SoloMode, DailyMode, DuelMode, CoopMode
ai/         AiClient (interface) → CloudFunctionAiClient
data/local/ AppDatabase, DAOs, Entities (Room)
data/remote/FirestoreDataSource, RoomSyncListener
data/       PuzzleRepository  — נקודת הגישה היחידה של ה-UI לנתונים
ui/         Activities, Fragments, ViewModels, ArrowGridView, Adapters (היסטוריה, מובילים, חדרים)
work/       PrefetchPuzzleWorker, SyncWorker
system/     NetworkReceiver, DailyReminderReceiver, NotificationHelper
```

### מה זה תשחץ — ומה זה משנה
בתשחץ (תשבץ חץ) ההגדרות כתובות **בתוך משבצות בגריד**. כל משבצת הגדרה מכילה עד שתי הגדרות, ולכל אחת חץ שמצביע לאן הולכת התשובה. בעברית התשובה רצה **שמאלה** (מאוזן) או **למטה** (מאונך), ומתחילה במשבצת הצמודה למשבצת ההגדרה. אין רשימת הגדרות נפרדת ואין משבצות שחורות "ריקות". זה משפיע על שלושה דברים:
1. **מודל הנתונים** — לגריד יש סוגי משבצות שונים (הורשה מ-`Cell`).
2. **האלגוריתם** — כל מילה חייבת משבצת הגדרה פנויה מיד לפניה, ומשבצת הגדרה מחזיקה לכל היותר שתי הגדרות. הגריד צפוף יותר מתשבץ רגיל.
3. **התצוגה** — טקסט בתוך משבצת בגריד 15×15 קטן מאוד בטלפון. פתרון: טקסט מקוצר במשבצת, זום וגרירה בשתי אצבעות, ופס עליון שמציג בגדול את ההגדרה של המילה הנבחרת. לחיצה על משבצת הגדרה בוחרת את המילה שלה.

### model
- `Cell` (מופשטת) — שורה ועמודה.
  - `LetterCell` — אות נכונה, אות שהמשתמש הזין.
  - `ClueCell` — אחת או שתי `Clue`.
  - `BlockedCell` — משבצת שלא נוצלה (רצוי כמה שפחות).
- `Clue` — טקסט ההגדרה, הגדרה חלופית (לרמז), `Arrow`, והמילה שהיא מגדירה.
- `Arrow` — `LEFT` (מאוזן), `DOWN` (מאונך). אפשרות הרחבה: חיצים "שבורים" (ההגדרה מעל המילה והתשובה פונה שמאלה, וההפך), כמו בתשחצים בעיתון.
- `Word` — תשובה, שורה ועמודה של האות הראשונה, כיוון, אורך.
- `Grid` — `Cell[][]` בגודל קבוע.
- `Puzzle` — מזהה, גודל, אינדקס במאגר, נושא, רמת קושי, `List<Word>`, `Grid`, מקור (מאגר / לימודי / יומי).

### logic
- `HebrewNormalizer` — הופך סופיות לרגילות (ם→מ, ן→נ, ץ→צ, ף→פ, ך→כ), מסיר ניקוד, רווחים, גרש ומקף.
- `ArrowGridGenerator` — מקבל רשימת מילים מה-AI ומשבץ בגריד ב-backtracking, עם האילוצים של תשחץ:
  - לפני כל מילה יש משבצת הגדרה (או משבצת שהופכת להיות כזו), ובה לכל היותר שתי הגדרות.
  - מילה לא יכולה להיצמד לאותיות אחרות בלי להצטלב איתן נכון.
  - בסוף, משבצות שנשארו ריקות הופכות ל-`BlockedCell`. מודדים **אחוז מילוי** ודוחים גריד שמתחת לסף.
  - **השלמת חורים בעזרת AI:** אם נשארו אזורים ריקים, האלגוריתם יודע בדיוק איזו תבנית חסרה (למשל 4 אותיות, השנייה ל'). הוא שולח ל-AI בקשה שנייה למילים שמתאימות לתבניות האלה, ומנסה שוב. כך ה-AI ממשיך להיות זה שמייצר את התוכן, והאלגוריתם אחראי על המבנה.
  - מבני נתונים: `Map<Character, List<int[]>>` — לכל אות, המיקומים שלה בגריד; `Set<String>` — מילים ששובצו; `CrossingGraph` — גרף שבו כל מילה היא צומת וכל הצטלבות היא קשת.
- `PuzzleValidator` — מוודא שכל ההצטלבויות תקינות, שלכל מילה יש הגדרה עם חץ נכון, שאין משבצת הגדרה עם יותר משתי הגדרות, ושאין רצפי אותיות שלא שייכים לאף מילה.

### game — הורשה
`GameMode` (מופשטת): `onCellInput()`, `isComplete()`, `onComplete()`, `useHint()`.
- `SoloMode` — משחק רגיל מול המאגר.
- `DailyMode` — מוסיף `CountDownTimer` ושליחת תוצאה לטבלת המובילים.
- `DuelMode` — שני שחקנים, כל אחד בגריד משלו, אותו תשחץ, מי מסיים ראשון.
- `CoopMode` — גריד משותף בזמן אמת; כל כתיבה למשבצת עוברת ב-Firestore transaction (ניהול קונפליקטים, סעיף 9.15).

## 3. Room (SQLite מקומי)
| טבלה | תוכן |
|---|---|
| `puzzles` | עותק מקומי של תשחצים (מטמון אופליין) |
| `words` | מילים, הגדרות, מיקום משבצת ההגדרה וכיוון החץ; `puzzleId` כמפתח זר |
| `topics` | נושאים |
| `puzzle_topic` | טבלת קישור — רבים-לרבים בין תשחצים לנושאים |
| `progress` | מצב הגריד של תשחץ פתוח, רמזים, זמן שעבר |
| `play_history` | תשחצים שהסתיימו: זמן, ניקוד, רמזים, מצב משחק |
| `pending_sync` | תור פעולות שנעשו בלי רשת |
| `achievements` | הישגים שנפתחו |

אובייקטים מורכבים: `PuzzleWithWords` (`@Embedded` + `@Relation`), `PuzzleWithTopics` (`@Relation` עם `Junction`).
שאילתות לדוגמה: ממוצע זמן פתרון לפי נושא (`JOIN` + `GROUP BY`), מספר תשחצים לפי גודל.

## 4. Firestore (מאגר גלובלי)
```
puzzles/{puzzleId}        size, index, topic, difficulty, words[], hash,
                          ratingSum, ratingCount, reports, status, createdAt
counters/{size}           next            ← האינדקס הבא שיינתן בכל גודל
users/{uid}               displayName, nextIndex10, nextIndex15, streak, points
daily/{yyyy-MM-dd}        puzzleId
daily/{date}/scores/{uid} seconds, hints, finishedAt
rooms/{roomId}            mode, puzzleId, players[], cells{}, status, winner
```

## 5. זרימה: "תשחץ הבא"
1. בודקים ב-Room אם יש תשחץ שהורד מראש לגודל המבוקש.
2. אחרת: `puzzles where size == X and index == users.nextIndexX`.
3. אם אין כזה: מבקשים מה-AI מילים → `ArrowGridGenerator` (כולל סבב השלמת חורים) → `PuzzleValidator` → שמירה במאגר עם האינדקס הבא מ-`counters` (transaction).
4. כשנשארים למשתמש 2 תשחצים או פחות במאגר, `PrefetchPuzzleWorker` מייצר תשחץ חדש ברקע.
5. בסיום: `nextIndexX++`, שמירה ב-`play_history`. בלי רשת — נכנס ל-`pending_sync` ו-`SyncWorker` שולח כשהרשת חוזרת.

## 6. שרת (Cloud Functions)
- `generateWords(size, topic?, difficulty)` — קורא ל-AI עם מפתח שנשמר בשרת בלבד, מחזיר JSON של מילים והגדרות.
- `fillPatterns(patterns[])` — סבב השלמת החורים: מקבל תבניות ומחזיר מילים והגדרות שמתאימות להן.
- `submitPuzzle(puzzle)` — בודק את הגריד שוב בצד השרת, בודק hash לכפילויות, נותן אינדקס ושומר.

**הוחלט:** אלגוריתם השיבוץ רץ באפליקציה, ב-Java. השרת רק מתווך מול ה-AI ומאמת לפני שמירה.

## 7. מיפוי לדרישות המכוון (תשפ"ז)
| סעיף | מימוש |
|---|---|
| 1–5 | אנדרואיד; טיפול בכשל AI ורשת; אלגוריתם השיבוץ; Fragments; `NetworkReceiver`; הרשאת התראות |
| 6 (≥2) | GenAI (12), WorkManager (1), Room עם יחסים (4), RecyclerView (9) — להיסטוריה, למובילים ולחדרים |
| 7 | Firestore בקריאה, כתיבה וסנכרון בזמן אמת + Room |
| 8, אפשרות 1 | שלושה מסעיף 9 ↓ |
| 9 | ‏View מותאם — `ArrowGridView`, כולל ציור חיצים, טקסט במשבצות וזום (3); רב-משתתפים עם ניהול קונפליקטים — `CoopMode` (15); `AlarmManager` + Notification — תזכורת יומית (2); API בצד שרת — Cloud Functions (10) |
| 10 | CountDownTimer, SharedPreferences, BroadcastReceiver, TextToSpeech (הקראת הגדרה) |
| 12 (OOP) | הורשת `GameMode` והורשת `Cell`, הפרדת model/logic/ui, Repository |
| 13 | תפריט ודיאלוגים |
| 15 | מערך דו-ממדי, Map, Set, List, גרף ההצטלבויות |

## 8. החלטות
סגורות:
- אלגוריתם השיבוץ — באפליקציה, ב-Java.
- התחברות — Firebase Auth, אימייל וסיסמה.

פתוחות:
1. ספק ה-AI — נדחה עד שיהיה ידוע התקציב.
2. MVVM מלא (נושא 11 בסעיף 6) או רק ViewModel במסכים המרכזיים.
3. האם לתמוך בחיצים "שבורים" כבר בגרסה הראשונה.
