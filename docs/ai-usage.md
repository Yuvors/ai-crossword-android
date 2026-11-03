# תיעוד שימוש בכלי AI בכתיבת הקוד

המכוון דורש שתיעוד הקוד יציין בבירור כל קטע קוד שנוצר בעזרת כלי AI. הסימון נעשה כאן, ולא בהערות שבתוך הקוד.
הכלי: Claude (Anthropic), דרך Claude Code.

כל קובץ שנכתב או שונה בעזרת AI נרשם כאן מיד, עם תאריך ומה בדיוק נעשה.

| תאריך | קובץ | מה נעשה בעזרת AI | שיעור |
|---|---|---|---|
| 2026-11-03 | `app/src/main/java/com/yuval/tashchetz/logic/HebrewNormalizer.java` | כל המחלקה | `docs/learning/02-hebrew-normalizer.md` |
| 2026-11-03 | `app/src/test/java/com/yuval/tashchetz/logic/HebrewNormalizerTest.java` | כל הבדיקות | `docs/learning/02-hebrew-normalizer.md` |
| 2026-11-03 | `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml`, `app/build.gradle.kts` | קבצי הבנייה, על בסיס התבנית של Android Studio | `docs/learning/03-gradle.md` |
| 2026-11-03 | `app/src/main/AndroidManifest.xml` | Manifest מינימלי | `docs/learning/03-gradle.md` |
| 2026-11-03 | `app/src/main/res/` (layout, menu, values) | מעטפת XML תקינה מינימלית בקבצים שהיו ריקים | `docs/learning/03-gradle.md` |
| 2026-11-03 | `model/Direction.java`, `model/Arrow.java`, `model/PuzzleSize.java`, `model/PuzzleSource.java`, `model/Difficulty.java` | כל ה-enums | `docs/learning/04-model.md` (חלק א') |
| 2026-11-03 | `app/src/test/java/com/yuval/tashchetz/model/ArrowTest.java` | כל הבדיקות | `docs/learning/04-model.md` (חלק א') |
| 2026-11-03 | **סקיצת המסכים:** כל קבצי `res/layout`, `res/menu`, `res/values`, `res/drawable`; ‏`ui/auth/*Activity`, `ui/main/MainActivity`, חמשת ה-Fragments, `ui/game/GameActivity`, `ui/settings/SettingsActivity`, ארבעת הדיאלוגים ב-`ui/dialogs`; `AndroidManifest.xml` | עיצוב ומימוש של סקיצה להצגה למורה, עם נתוני placeholder | עדיין אין שיעור |

קבצים שלא נכתבו בעזרת AI: `gradlew`, `gradlew.bat` ו-`gradle/wrapper/` הועתקו מפרויקט קודם (SchoolQuest), ונוצרו במקור על ידי Android Studio.

קוד לדוגמה בתוך שיעורי `docs/learning/` נכתב גם הוא בעזרת AI, וכל שיעור מציין זאת בראשו.
