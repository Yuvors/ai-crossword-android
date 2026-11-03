# שיעור 3: Gradle — איך הפרויקט נבנה

> קבצי הבנייה נכתבו בעזרת AI (Claude), על בסיס התבנית של Android Studio.

## מה זה Gradle
Gradle היא **מערכת בנייה**. כשלוחצים Run ב-Android Studio, Gradle:
1. מורידה את הספריות שהפרויקט צריך (AppCompat, Lifecycle, JUnit...).
2. מקמפלת את קוד ה-Java.
3. מעבדת את המשאבים (`res/`) ויוצרת את המחלקה `R`.
4. אורזת הכול לקובץ APK ומתקינה אותו על הטלפון או האמולטור.

את כל זה מגדירים בקבצי `.gradle.kts`. הם כתובים ב-Kotlin, אבל זה רק תחביר הגדרות, ולא צריך לדעת Kotlin כדי לקרוא אותם.

## הקבצים
| קובץ | תפקיד |
|---|---|
| `settings.gradle.kts` | שם הפרויקט, אילו מודולים יש בו (`:app`), ומאיפה מורידים ספריות (`google()`, `mavenCentral()`) |
| `build.gradle.kts` (בשורש) | מצהיר על הפלאגין של אנדרואיד לכל הפרויקט |
| `app/build.gradle.kts` | **הקובץ החשוב:** הגדרות האפליקציה והתלויות שלה |
| `gradle/libs.versions.toml` | קטלוג גרסאות: כל הספריות והגרסאות במקום אחד |
| `gradle.properties` | הגדרות ל-Gradle עצמה: זיכרון, קידוד UTF-8, AndroidX |
| `gradlew`, `gradlew.bat`, `gradle/wrapper/` | ה-Wrapper (ראו למטה) |
| `local.properties` | איפה מותקן ה-Android SDK במחשב הזה. **לא נכנס ל-git**, כי בכל מחשב הנתיב שונה |
| `app/src/main/AndroidManifest.xml` | ה"תעודת זהות" של האפליקציה: שם, מסכים, הרשאות |

## `app/build.gradle.kts`, שורה אחרי שורה
```kotlin
android {
    namespace = "com.yuval.tashchetz"      // החבילה שבה נוצרת המחלקה R
    compileSdk = 36                        // מול איזו גרסת אנדרואיד מקמפלים

    defaultConfig {
        applicationId = "com.yuval.tashchetz"  // המזהה הייחודי של האפליקציה בטלפון וב-Play
        minSdk = 28                            // הגרסה הנמוכה ביותר שהאפליקציה תותקן עליה (Android 9)
        targetSdk = 36                         // הגרסה שהאפליקציה נבדקה מולה
        versionCode = 1                        // מספר גרסה פנימי, עולה בכל עדכון
        versionName = "1.0"                    // הגרסה שהמשתמש רואה
    }
    ...
}
```
- **`minSdk` מול `targetSdk`:** ‏`minSdk` קובע מי **יכול** להתקין. ‏`targetSdk` אומר לאנדרואיד לאיזו התנהגות האפליקציה מוכנה. למשל, ההרשאה להתראות בזמן ריצה קיימת רק מאנדרואיד 13.

```kotlin
dependencies {
    implementation(libs.appcompat)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    testImplementation(libs.junit)
}
```
- **`implementation`** — ספרייה שנכנסת לאפליקציה עצמה.
- **`testImplementation`** — ספרייה שרק הבדיקות ב-`app/src/test` צריכות. היא לא נכנסת ל-APK.
- `libs.appcompat` — שם מקוצר מתוך `libs.versions.toml`. כדי לעדכן גרסה משנים מספר אחד בקטלוג.

## למה יש ספריות "בסיס" בלבד
כרגע יש רק AppCompat, Material, Lifecycle (ל-MVVM) ו-JUnit. Room, WorkManager ו-Firebase ייכנסו כל אחת כשנגיע לנושא שלה, יחד עם שיעור. ככה כל שורה בקובץ מוכרת לך. ל-Firebase צריך גם את `google-services.json`, שעדיין לא קיים.

## ה-Wrapper (`gradlew`)
`gradlew` הוא סקריפט קטן שמוריד ומריץ **בדיוק** את גרסת Gradle שכתובה ב-`gradle/wrapper/gradle-wrapper.properties` (כאן: 9.3.1). ככה הפרויקט נבנה אותו דבר בכל מחשב, גם בלי להתקין Gradle מראש. הקבצים הועתקו מ-SchoolQuest, שבו אותה גרסה כבר עבדה.

## ה-Manifest המינימלי
```xml
<application
    android:allowBackup="true"
    android:label="תשחץ"
    android:supportsRtl="true" />
```
- `supportsRtl="true"` — האפליקציה תומכת בכתיבה מימין לשמאל. חובה לעברית.
- עדיין אין `<activity>` ואין הרשאות. כל אחד מהם יתווסף ביחד עם הקובץ שצריך אותו.

## קבצי ה-XML הריקים ב-`res/`
Gradle קוראת את כל קבצי ה-XML ב-`res/` כדי ליצור את `R`. קובץ ריק אינו XML תקין, והבנייה נכשלת ("Premature end of file"). לכן כל קובץ קיבל את התוכן התקין המינימלי: `<resources />` ב-`values`, `<menu />` ב-`menu`, ו-`FrameLayout` ריק בכל layout. התוכן האמיתי ייכתב עם כל מסך.

## איך מריצים
- **בדיקות:** ב-Android Studio, לחיצה ימנית על `HebrewNormalizerTest` → Run. משורת הפקודה: `gradlew testDebugUnitTest`.
- **בניית APK:** ‏`gradlew assembleDebug`. הקובץ נוצר ב-`app/build/outputs/apk/debug/`.

## שאלות שבוחן עשוי לשאול
1. **מה ההבדל בין `minSdk`, `targetSdk` ו-`compileSdk`?** ‏`minSdk`: הגרסה הנמוכה שאפשר להתקין עליה. ‏`targetSdk`: הגרסה שהאפליקציה מותאמת להתנהגות שלה. ‏`compileSdk`: הגרסה שמולה מקמפלים.
2. **מה ההבדל בין `implementation` ל-`testImplementation`?** הראשונה נכנסת ל-APK. השנייה רק לבדיקות.
3. **מה זה `applicationId`?** המזהה הייחודי של האפליקציה. שתי אפליקציות עם אותו מזהה לא יכולות להיות מותקנות יחד.
4. **למה `local.properties` לא ב-git?** הוא מכיל נתיב שתלוי במחשב.
5. **איפה מוסיפים ספרייה חדשה?** שורה ב-`libs.versions.toml`, ושורת `implementation` ב-`app/build.gradle.kts`.
6. **מה התפקיד של ה-Manifest?** מצהיר על המסכים, ה-receivers וההרשאות. Activity שלא רשומה בו לא תיפתח, והאפליקציה תקרוס.
