# ארכיטקטורה — טיוטה ראשונה
סטטוס: טיוטה לדיון. כל מה שמסומן **[החלטה פתוחה]** עוד לא נסגר.

## 1. מסכים
| מסך | סוג | תפקיד |
|---|---|---|
| `SplashActivity` | Activity | טעינה, בדיקת התחברות |
| `LoginActivity` | Activity | התחברות (Firebase Auth) — נדרש כדי לשמור התקדמות אישית במאגר |
| `MainActivity` | Activity + ניווט תחתון | מארח את ה-Fragments הראשיים |
| `HomeFragment` | Fragment | "תשבץ הבא" לפי גודל (10×10 / 15×15), המשך תשבץ פתוח |
| `LearnFragment` | Fragment | תשבץ לימודי לפי נושא שהמשתמש מקליד |
| `DailyFragment` | Fragment | התשבץ היומי + טבלת מובילים |
| `MultiplayerFragment` | Fragment | יצירת/הצטרפות לחדר: פתרון משותף או דו-קרב |
| `ProfileFragment` | Fragment | סטטיסטיקות, רצף ימים, הישגים |
| `GameActivity` | Activity | מסך המשחק: הגריד + רשימת הגדרות |
| `SettingsActivity` | Activity | הגדרות (SharedPreferences) |

דיאלוגים: רמז, סיום תשבץ + דירוג, דיווח על הגדרה, אישור יציאה, "אין רשת — מצב אופליין".

## 2. חבילות ומחלקות עיקריות

```
model/      Cell, Word, Direction, Grid, Puzzle, PuzzleSize
logic/      CrosswordGenerator, CrossingGraph, PuzzleValidator, HebrewNormalizer, ScoreCalculator
game/       GameMode (abstract) → SoloMode, DailyMode, DuelMode, CoopMode
ai/         AiClient (interface) → CloudFunctionAiClient
data/local/ AppDatabase, DAOs, Entities (Room)
data/remote/FirestoreDataSource, RoomSyncListener
data/       PuzzleRepository  — נקודת הגישה היחידה של ה-UI לנתונים
ui/         Activities, Fragments, ViewModels, CrosswordView, ClueAdapter
work/       PrefetchPuzzleWorker, SyncWorker
system/     NetworkReceiver, DailyReminderReceiver, NotificationHelper
```

### model
- `Cell` — אות נכונה, אות שהמשתמש הזין, האם חסומה, מספר הגדרה.
- `Word` — תשובה, הגדרה, הגדרה חלופית (לרמז), שורה, עמודה, כיוון, אורך.
- `Grid` — `Cell[][]` בגודל קבוע.
- `Puzzle` — מזהה, גודל, אינדקס במאגר, נושא, רמת קושי, `List<Word>`, `Grid`, מקור (מאגר / לימודי / יומי).

### logic
- `HebrewNormalizer` — הופך סופיות לרגילות (ם→מ, ן→נ, ץ→צ, ף→פ, ך→כ), מסיר ניקוד, רווחים, גרש ומקף.
- `CrosswordGenerator` — מקבל רשימת מילים מה-AI ומשבץ בגריד ב-backtracking.
  - מבני נתונים: `Map<Character, List<int[]>>` — לכל אות, המיקומים שלה בגריד; `Set<String>` — מילים ששובצו; `CrossingGraph` — גרף שבו כל מילה היא צומת וכל הצטלבות היא קשת.
- `PuzzleValidator` — מוודא שכל ההצטלבויות תקינות, שאין מילים לא מכוונות שנוצרו מהצמדות, ושהגריד מכוסה מספיק.

### game — הורשה
`GameMode` (מופשטת): `onCellInput()`, `isComplete()`, `onComplete()`, `useHint()`.
- `SoloMode` — משחק רגיל מול המאגר.
- `DailyMode` — מוסיף `CountDownTimer` ושליחת תוצאה לטבלת המובילים.
- `DuelMode` — שני שחקנים, כל אחד בגריד משלו, אותו תשבץ, מי מסיים ראשון.
- `CoopMode` — גריד משותף בזמן אמת; כל כתיבה למשבצת עוברת ב-Firestore transaction (ניהול קונפליקטים, סעיף 9.15).

## 3. Room (SQLite מקומי)
| טבלה | תוכן |
|---|---|
| `puzzles` | עותק מקומי של תשבצים (מטמון אופליין) |
| `words` | מילים והגדרות, `puzzleId` כמפתח זר |
| `topics` | נושאים |
| `puzzle_topic` | טבלת קישור — רבים-לרבים בין תשבצים לנושאים |
| `progress` | מצב הגריד של תשבץ פתוח, רמזים, זמן שעבר |
| `play_history` | תשבצים שהסתיימו: זמן, ניקוד, רמזים, מצב משחק |
| `pending_sync` | תור פעולות שנעשו בלי רשת |
| `achievements` | הישגים שנפתחו |

אובייקטים מורכבים: `PuzzleWithWords` (`@Embedded` + `@Relation`), `PuzzleWithTopics` (`@Relation` עם `Junction`).
שאילתות לדוגמה: ממוצע זמן פתרון לפי נושא (`JOIN` + `GROUP BY`), מספר תשבצים לפי גודל.

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

## 5. זרימה: "תשבץ הבא"
1. בודקים ב-Room אם יש תשבץ שהורד מראש לגודל המבוקש.
2. אחרת: `puzzles where size == X and index == users.nextIndexX`.
3. אם אין כזה: מבקשים מה-AI מילים → `CrosswordGenerator` → `PuzzleValidator` → שמירה במאגר עם האינדקס הבא מ-`counters` (transaction).
4. כשנשארים למשתמש 2 תשבצים או פחות במאגר, `PrefetchPuzzleWorker` מייצר תשבץ חדש ברקע.
5. בסיום: `nextIndexX++`, שמירה ב-`play_history`. בלי רשת — נכנס ל-`pending_sync` ו-`SyncWorker` שולח כשהרשת חוזרת.

## 6. שרת (Cloud Functions)
- `generateWords(size, topic?, difficulty)` — קורא ל-AI עם מפתח שנשמר בשרת בלבד, מחזיר JSON של מילים והגדרות.
- `submitPuzzle(puzzle)` — בודק את הגריד שוב בצד השרת, בודק hash לכפילויות, נותן אינדקס ושומר.

**[החלטה פתוחה]** איפה רץ אלגוריתם השיבוץ. ההמלצה: באפליקציה, ב-Java — זה החלק האלגוריתמי שהבוחן ישאל עליו, והוא צריך להיות בקוד שלך. השרת רק מתווך מול ה-AI ומאמת לפני שמירה.

## 7. מיפוי לדרישות המכוון (תשפ"ז)
| סעיף | מימוש |
|---|---|
| 1–5 | אנדרואיד; טיפול בכשל AI ורשת; אלגוריתם השיבוץ; Fragments; `NetworkReceiver`; הרשאת התראות |
| 6 (≥2) | GenAI (12), WorkManager (1), Room עם יחסים (4), RecyclerView (9) |
| 7 | Firestore בקריאה, כתיבה וסנכרון בזמן אמת + Room |
| 8, אפשרות 1 | שלושה מסעיף 9 ↓ |
| 9 | ‏View מותאם — `CrosswordView` (3); רב-משתתפים עם ניהול קונפליקטים — `CoopMode` (15); `AlarmManager` + Notification — תזכורת יומית (2); API בצד שרת — Cloud Functions (10) |
| 10 | CountDownTimer, SharedPreferences, BroadcastReceiver, TextToSpeech (הקראת הגדרה) |
| 12 (OOP) | הורשת `GameMode`, הפרדת model/logic/ui, Repository |
| 13 | תפריט ודיאלוגים |
| 15 | מערך דו-ממדי, Map, Set, List, גרף ההצטלבויות |

## 8. החלטות פתוחות
1. איפה רץ אלגוריתם השיבוץ (המלצה: באפליקציה).
2. שיטת התחברות: Google Sign-In או אימייל וסיסמה.
3. ספק ה-AI — נדחה עד שיהיה ידוע התקציב.
4. MVVM מלא (נושא 11 בסעיף 6) או רק ViewModel במסכים המרכזיים.
