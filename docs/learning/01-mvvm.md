# שיעור 1: MVVM

> הקוד בשיעור הזה הוא דוגמה ללימוד, שנכתבה בעזרת AI. אם קוד ממנה נכנס לפרויקט, צריך לסמן אותו בתיעוד כקוד שנוצר בעזרת AI.

## הבעיה ש-MVVM פותר
בלי שיטה, כל הקוד של מסך נכנס ל-Activity: קריאה לנתונים, בדיקה אם התשחץ נפתר, ציור הגריד, טיימר. יש בזה שלוש בעיות:
1. **ה-Activity מתנפחת** ואי אפשר להבין מה היא עושה.
2. **סיבוב מסך מוחק הכול.** באנדרואיד, כשמסובבים את הטלפון ה-Activity נהרסת ונבנית מחדש. כל המשתנים שלה נעלמים, כולל האותיות שהמשתמש כבר כתב.
3. **אין הפרדה בין לוגיקה לתצוגה.** מדד 12 במכוון דורש אותה במפורש.

## שלוש השכבות

```
 ┌──────────────┐   פעולות משתמש    ┌──────────────┐   בקשת נתונים   ┌──────────────┐
 │     View     │ ───────────────▶ │  ViewModel   │ ──────────────▶ │    Model     │
 │ Activity,    │                  │ מצב המסך     │                 │ Repository,  │
 │ Fragment,    │ ◀─────────────── │ והחלטות      │ ◀────────────── │ Room,        │
 │ ArrowGridView│  LiveData (צפייה) │              │   תוצאות         │ Firestore,   │
 └──────────────┘                  └──────────────┘                 │ לוגיקה       │
                                                                    └──────────────┘
```

| שכבה | מה היא עושה | מה אסור לה |
|---|---|---|
| **View** | מציגה נתונים ומעבירה הלאה לחיצות | לקבל החלטות, לגשת ישירות ל-Room או ל-Firestore |
| **ViewModel** | מחזיקה את מצב המסך ומחליטה מה קורה בכל פעולה | להחזיק הפניה ל-Activity, ל-View או ל-Context (זה גורם לדליפת זיכרון) |
| **Model** | הנתונים והלוגיקה: `Puzzle`, `ArrowGridGenerator`, `PuzzleRepository` | לדעת בכלל שיש מסך |

הכיוון חשוב: **כל שכבה מכירה רק את השכבה שמתחתיה.** ה-View מכירה את ה-ViewModel, ה-ViewModel מכירה את ה-Repository. אף פעם לא להפך.

## LiveData: איך ה-View יודעת שמשהו השתנה
`LiveData` היא "קופסה" שמחזיקה ערך ומודיעה למי שמאזין לה בכל פעם שהערך משתנה.
- ה-ViewModel **כותבת** לקופסה.
- ה-View **מאזינה** לקופסה (`observe`) ומעדכנת את המסך.
- `LiveData` מכירה את מחזור החיים של המסך. היא לא שולחת עדכונים למסך שנסגר, ולכן אין קריסות ואין דליפות.

יש שני סוגים:
- `MutableLiveData`: אפשר לשנות את הערך. נשארת **פרטית** בתוך ה-ViewModel.
- `LiveData`: אפשר רק לקרוא ולהאזין. זה מה שה-ViewModel **חושפת** החוצה.

ככה רק ה-ViewModel יכולה לשנות את המצב, וה-View רק מציגה אותו.

## דוגמה מהפרויקט: מסך המשחק

### ה-ViewModel
```java
public class GameViewModel extends ViewModel {

    private final PuzzleRepository repository;

    // פרטי: רק ה-ViewModel משנה את המצב
    private final MutableLiveData<Puzzle> puzzle = new MutableLiveData<>();
    private final MutableLiveData<Boolean> solved = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private int selectedRow = -1;
    private int selectedCol = -1;

    public GameViewModel(PuzzleRepository repository) {
        this.repository = repository;
    }

    // ציבורי: ה-View רק קוראת ומאזינה
    public LiveData<Puzzle> getPuzzle() { return puzzle; }
    public LiveData<Boolean> isSolved() { return solved; }
    public LiveData<String> getError() { return error; }

    public void loadNextPuzzle(PuzzleSize size) {
        repository.getNextPuzzle(size, new PuzzleRepository.Callback() {
            @Override
            public void onSuccess(Puzzle result) {
                puzzle.postValue(result);      // postValue: נקרא מ-thread ברקע
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
            }
        });
    }

    public void onCellSelected(int row, int col) {
        selectedRow = row;
        selectedCol = col;
    }

    public void onLetterTyped(char letter) {
        Puzzle current = puzzle.getValue();
        if (current == null || selectedRow < 0) {
            return;
        }
        current.getGrid().setUserLetter(selectedRow, selectedCol, letter);
        puzzle.setValue(current);              // setValue: נקרא מה-thread הראשי
        if (current.isSolved()) {
            solved.setValue(true);
        }
    }
}
```

### ה-View
```java
public class GameActivity extends AppCompatActivity {

    private GameViewModel viewModel;
    private ArrowGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gridView = findViewById(R.id.arrow_grid);

        // לא "new GameViewModel" — ה-Provider מחזיר את אותו מופע גם אחרי סיבוב מסך
        viewModel = new ViewModelProvider(this, new GameViewModelFactory(this))
                .get(GameViewModel.class);

        // 1. מאזינים למצב ומציגים אותו
        viewModel.getPuzzle().observe(this, puzzle -> gridView.setPuzzle(puzzle));
        viewModel.isSolved().observe(this, solved -> {
            if (solved) {
                showSolvedDialog();
            }
        });
        viewModel.getError().observe(this, message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show());

        // 2. מעבירים פעולות משתמש ל-ViewModel, בלי לקבל החלטות כאן
        gridView.setOnCellSelectedListener((row, col) -> viewModel.onCellSelected(row, col));
        gridView.setOnLetterTypedListener(letter -> viewModel.onLetterTyped(letter));

        if (savedInstanceState == null) {
            viewModel.loadNextPuzzle(PuzzleSize.SMALL);
        }
    }
}
```

שים לב מה **אין** ב-Activity: אין בדיקה אם התשחץ נפתר, אין גישה ל-Firestore, אין לוגיקה בכלל. היא רק מחברת את המסך ל-ViewModel.

### למה צריך Factory
ל-`GameViewModel` יש בנאי שמקבל `PuzzleRepository`. ה-`ViewModelProvider` לא יודע לבד מה להעביר לבנאי, אז כותבים מחלקה קטנה, `GameViewModelFactory`, שיודעת ליצור את ה-ViewModel עם הפרמטרים הנכונים.

## מה קורה בסיבוב מסך, צעד אחרי צעד
1. המשתמש באמצע תשחץ ומסובב את הטלפון.
2. אנדרואיד הורסת את `GameActivity` ובונה חדשה. `onCreate` רץ שוב.
3. `ViewModelProvider` מחזיר את **אותו** `GameViewModel`, שלא נהרס.
4. `observe` נרשם מחדש, ו-`LiveData` שולחת מיד את הערך האחרון. הגריד מצטייר עם כל האותיות.
5. בגלל `savedInstanceState != null` לא טוענים תשחץ חדש.

## `setValue` מול `postValue`
- `setValue`: רק מה-thread הראשי (UI thread). העדכון מיידי.
- `postValue`: מכל thread. העדכון נשלח ל-thread הראשי ויקרה בהקדם.

קריאות ל-Firestore ול-Room חוזרות ב-thread ברקע, ולכן שם משתמשים ב-`postValue`.

## איך זה ייראה בכל הפרויקט
| מסך | ViewModel | מה הוא מחזיק |
|---|---|---|
| `LoginActivity` | `LoginViewModel` | מצב התחברות, הודעת שגיאה |
| `HomeFragment` | `HomeViewModel` | תשחץ פתוח להמשך, כמה תשחצים מוכנים בכל גודל |
| `LearnFragment` | `LearnViewModel` | הנושא שהוקלד, מצב יצירה (טוען / מוכן / נכשל) |
| `DailyFragment` | `DailyViewModel` | התשחץ היומי, טבלת המובילים |
| `MultiplayerFragment` | `MultiplayerViewModel` | רשימת חדרים, החדר הנוכחי |
| `ProfileFragment` | `ProfileViewModel` | סטטיסטיקות והישגים |
| `GameActivity` | `GameViewModel` | התשחץ, המשבצת הנבחרת, זמן, האם נפתר |
| `SettingsActivity` | `SettingsViewModel` | ההגדרות |

## שאלות שבוחן עשוי לשאול
1. **מה ההבדל בין ViewModel ל-Activity?**
   ה-Activity היא תצוגה ונהרסת בסיבוב מסך. ה-ViewModel מחזיקה את המצב ושורדת סיבוב.
2. **למה אסור ל-ViewModel להחזיק Context של Activity?**
   ה-ViewModel חיה יותר זמן מה-Activity. אם היא מחזיקה הפניה ל-Activity שנהרסה, ה-Activity לא תשתחרר מהזיכרון. זו דליפת זיכרון.
3. **למה חושפים `LiveData` ולא `MutableLiveData`?**
   כדי שרק ה-ViewModel תוכל לשנות את המצב. זו כימוס (encapsulation).
4. **מה ההבדל בין `setValue` ל-`postValue`?**
   ראה סעיף למעלה.
5. **איפה בקוד נבדק אם התשחץ נפתר, ולמה שם?**
   ב-`GameViewModel.onLetterTyped`, כי זו החלטה ולא תצוגה.
6. **מה התפקיד של ה-Repository?**
   נקודת גישה אחת לנתונים. ה-ViewModel לא יודעת אם התשחץ הגיע מ-Room, מ-Firestore או מה-AI.

## תרגיל
לפני שממשיכים: נסה להסביר במילים שלך מה קורה, שכבה אחרי שכבה, מהרגע שהמשתמש מקליד אות ועד שהיא מופיעה בגריד.
