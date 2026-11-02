# מבנה הפרויקט: סקיצה
רק שמות קבצים ותפקיד של כל אחד, עדיין בלי קוד. נגזר מ-`docs/architecture.md`.

> **שם החבילה:** `com.yuval.tashchetz` (סופי). הקבצים נוצרו ריקים ב-repository.

## שורש ה-repository
```
ai-crossword-android/
├── CLAUDE.md
├── .gitignore
├── docs/                        מסמכים, מכוון, שיעורים
├── app/                         אפליקציית האנדרואיד (Java)
├── functions/                   Cloud Functions — מתווך מול ה-AI ואימות לפני שמירה
├── firebase.json                הגדרות פרויקט Firebase
├── firestore.rules              כללי אבטחה: מי רשאי לקרוא ולכתוב מה
└── firestore.indexes.json       אינדקסים לשאילתות (size + index)
```

## האפליקציה: `app/src/main/java/com/yuval/tashchetz/`
```
App.java                         מחלקת Application: אתחול Firebase, ערוצי התראות, תזמון Workers

model/                           אובייקטים של המשחק. Java נקי, בלי אנדרואיד
├── Cell.java                    (מופשטת) משבצת בגריד: שורה ועמודה
├── LetterCell.java              משבצת אות: אות נכונה, אות שהמשתמש הזין
├── ClueCell.java                משבצת הגדרה: אחת או שתי הגדרות
├── BlockedCell.java             משבצת שלא נוצלה
├── Clue.java                    טקסט הגדרה, הגדרה חלופית לרמז, חץ, המילה שהיא מגדירה
├── Arrow.java                   (enum) LEFT, DOWN, DOWN_THEN_LEFT, LEFT_THEN_DOWN
├── Direction.java               (enum) HORIZONTAL, VERTICAL — הכיוון שבו רצה התשובה
├── Word.java                    תשובה, משבצת התחלה, כיוון, אורך
├── Grid.java                    מערך Cell[][] בגודל קבוע
├── Puzzle.java                  תשחץ שלם: מזהה, גודל, אינדקס, נושא, קושי, מילים, גריד
├── PuzzleSize.java              (enum) SMALL = 10×10, LARGE = 15×15
├── PuzzleSource.java            (enum) POOL, LEARNING, DAILY
├── GameRoom.java                חדר רב-משתתפים: שחקנים, מצב, מנצח
├── UserProfile.java             פרטי משתמש, nextIndex לכל גודל, רצף, נקודות
├── ScoreEntry.java              שורה בטבלת המובילים
└── Achievement.java             הישג: מזהה, שם, האם נפתח

logic/                           הלוגיקה המרכזית. Java נקי, נבדק ב-unit tests
├── HebrewNormalizer.java        סופיות → רגילות, הסרת ניקוד, רווחים, גרש ומקף
├── ArrowGridGenerator.java      אלגוריתם השיבוץ (backtracking) עם אילוצי תשחץ
├── SlotPattern.java             תבנית חסרה בגריד ("4 אותיות, השנייה ל'") לסבב השלמת חורים
├── CrossingGraph.java           גרף ההצטלבויות: מילה = צומת, הצטלבות = קשת
├── PuzzleValidator.java         בדיקת תקינות של גריד מוגמר
├── PuzzleHasher.java            hash של רשימת המילים, למניעת כפילויות במאגר
└── ScoreCalculator.java         ניקוד לפי זמן, רמזים וגודל

game/                            מצבי משחק — הורשה
├── GameMode.java                (מופשטת) onCellInput, isComplete, onComplete, useHint
├── SoloMode.java                משחק רגיל מול המאגר
├── DailyMode.java               + CountDownTimer ושליחה לטבלת המובילים
├── DuelMode.java                שני שחקנים, כל אחד בגריד משלו, מי מסיים ראשון
└── CoopMode.java                גריד משותף בזמן אמת, כתיבה ב-transaction (ניהול קונפליקטים)

ai/
├── AiClient.java                (interface) generateWords, fillPatterns
├── CloudFunctionAiClient.java   מימוש: קורא ל-Cloud Functions
└── WordCandidate.java           מילה + הגדרה + הגדרה חלופית, כפי שחוזרות מה-AI

data/                            שכבת הנתונים. ה-ViewModels מדברים רק עם ה-Repositories
├── PuzzleRepository.java        "תשחץ הבא": מטמון → מאגר → יצירה ב-AI
├── AuthRepository.java          התחברות, הרשמה, התנתקות
├── UserRepository.java          פרופיל, nextIndex, רצף ונקודות
├── StatsRepository.java         היסטוריה, סטטיסטיקות, הישגים
├── MultiplayerRepository.java   חדרים, הצטרפות, האזנה לשינויים בזמן אמת
├── DailyRepository.java         התשחץ היומי וטבלת המובילים
├── RepositoryCallback.java      (interface) onSuccess / onFailure
├── local/                       Room (SQLite)
│   ├── AppDatabase.java
│   ├── Converters.java          TypeConverters: enums, מצב הגריד כמחרוזת
│   ├── dao/
│   │   ├── PuzzleDao.java
│   │   ├── TopicDao.java
│   │   ├── ProgressDao.java
│   │   ├── HistoryDao.java      כולל שאילתות JOIN ו-GROUP BY לסטטיסטיקות
│   │   ├── PendingSyncDao.java
│   │   └── AchievementDao.java
│   ├── entity/
│   │   ├── PuzzleEntity.java
│   │   ├── WordEntity.java
│   │   ├── TopicEntity.java
│   │   ├── PuzzleTopicCrossRef.java   טבלת קישור רבים-לרבים
│   │   ├── ProgressEntity.java
│   │   ├── PlayHistoryEntity.java
│   │   ├── PendingSyncEntity.java
│   │   └── AchievementEntity.java
│   └── relation/
│       ├── PuzzleWithWords.java       @Embedded + @Relation
│       ├── PuzzleWithTopics.java      @Relation + Junction
│       └── TopicStats.java            תוצאה של שאילתת GROUP BY
├── remote/                      Firebase
│   ├── FirestoreDataSource.java
│   ├── AuthDataSource.java
│   └── FirestorePaths.java      שמות האוספים והשדות כקבועים
└── mapper/
    └── PuzzleMapper.java        המרה בין model ↔ Room entity ↔ מסמך Firestore

ui/                              מסכים. לכל מסך ViewModel משלו (MVVM)
├── ViewModelFactory.java        יוצר את כל ה-ViewModels עם ה-Repositories שלהם
├── auth/
│   ├── SplashActivity.java
│   ├── LoginActivity.java       + LoginViewModel.java
│   └── RegisterActivity.java    + RegisterViewModel.java
├── main/
│   └── MainActivity.java        ניווט תחתון בין ה-Fragments, רישום NetworkReceiver
├── home/
│   ├── HomeFragment.java        + HomeViewModel.java
├── learn/
│   ├── LearnFragment.java       + LearnViewModel.java
├── daily/
│   ├── DailyFragment.java       + DailyViewModel.java
│   └── LeaderboardAdapter.java  RecyclerView
├── multiplayer/
│   ├── MultiplayerFragment.java + MultiplayerViewModel.java
│   └── RoomAdapter.java         RecyclerView
├── profile/
│   ├── ProfileFragment.java     + ProfileViewModel.java
│   ├── HistoryAdapter.java      RecyclerView
│   └── AchievementAdapter.java  RecyclerView
├── game/
│   ├── GameActivity.java        + GameViewModel.java
│   └── ArrowGridView.java       View מותאם: ציור הגריד, חיצים, טקסט, זום וגרירה
├── settings/
│   └── SettingsActivity.java    + SettingsViewModel.java
└── dialogs/
    ├── HintDialog.java
    ├── PuzzleSolvedDialog.java  כולל דירוג בכוכבים
    ├── ReportClueDialog.java
    └── ExitGameDialog.java

work/                            WorkManager
├── PrefetchPuzzleWorker.java    יצירת תשחץ ברקע כשנשארים 2 או פחות
├── SyncWorker.java              שליחת התור של pending_sync כשהרשת חוזרת
└── WorkScheduler.java           מקום אחד שמתזמן את כל ה-Workers

system/
├── NetworkReceiver.java         BroadcastReceiver: זיהוי ניתוק וחזרה של הרשת
├── DailyReminderReceiver.java   מקבל את ה-AlarmManager ומציג התראה יומית
├── BootReceiver.java            אחרי הפעלה מחדש של הטלפון — מתזמן שוב את התזכורת
├── ReminderScheduler.java       תזמון התזכורת היומית ב-AlarmManager
├── NotificationHelper.java      ערוצי התראות ובניית התראות
├── SpeechHelper.java            TextToSpeech: הקראת הגדרה
└── SettingsManager.java         SharedPreferences: גודל ברירת מחדל, צלילים, שעת תזכורת

util/
└── AppExecutors.java            threads לעבודה ברקע (Room, יצירה)
```

## משאבים: `app/src/main/res/`
```
layout/
├── activity_splash.xml   activity_login.xml   activity_register.xml
├── activity_main.xml     activity_game.xml    activity_settings.xml
├── fragment_home.xml     fragment_learn.xml   fragment_daily.xml
├── fragment_multiplayer.xml                   fragment_profile.xml
├── item_leaderboard.xml  item_room.xml        item_history.xml   item_achievement.xml
└── dialog_hint.xml       dialog_solved.xml    dialog_report.xml
menu/
├── bottom_nav_menu.xml   חמשת הטאבים
└── game_menu.xml         תפריט במסך המשחק: רמז, הקראה, דיווח, הגדרות
values/
├── strings.xml           כל הטקסטים בעברית
├── colors.xml   themes.xml   dimens.xml
drawable/                 אייקונים
```

## קבצים נוספים באפליקציה
```
app/src/main/AndroidManifest.xml   מסכים, receivers, הרשאות:
                                   INTERNET, ACCESS_NETWORK_STATE, POST_NOTIFICATIONS,
                                   RECEIVE_BOOT_COMPLETED; supportsRtl="true"
app/build.gradle                   תלויות: Firebase, Room, WorkManager, Lifecycle
app/google-services.json           ההגדרות של Firebase — לא נכנס ל-git
```

## בדיקות: `app/src/test/java/com/yuval/tashchetz/`
```
logic/
├── HebrewNormalizerTest.java
├── ArrowGridGeneratorTest.java
└── PuzzleValidatorTest.java
```
הלוגיקה כתובה ב-Java נקי, בלי אנדרואיד, ולכן אפשר לבדוק אותה בלי אמולטור.

## שרת: `functions/`
```
functions/
├── package.json
├── index.js              generateWords, fillPatterns, submitPuzzle
├── prompts.js            הנוסח של הבקשות ל-AI
└── validate.js           בדיקת הגריד בצד השרת לפני שמירה
```

## הערות
- `NetworkReceiver` נרשם בקוד, ב-`MainActivity`, ולא ב-Manifest. מאז אנדרואיד 7, אירוע ניתוק רשת לא מגיע ל-receiver שרשום ב-Manifest. `DailyReminderReceiver` ו-`BootReceiver` כן רשומים ב-Manifest.
- `game/` ו-`logic/` נפרדות בכוונה: `logic` בונה ובודקת תשחצים, ו-`game` מנהלת משחק שכבר קיים.
