package com.yuval.tashchetz.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.yuval.tashchetz.R;
import com.yuval.tashchetz.ui.dialogs.ExitGameDialog;
import com.yuval.tashchetz.ui.dialogs.HintDialog;
import com.yuval.tashchetz.ui.dialogs.PuzzleSolvedDialog;
import com.yuval.tashchetz.ui.dialogs.ReportClueDialog;
import com.yuval.tashchetz.ui.settings.SettingsActivity;

/**
 * The game screen: clue bar, grid and Hebrew keyboard.
 * Sketch only: the grid is a placeholder, and the keys do not type anything yet.
 */
public class GameActivity extends AppCompatActivity {

    /** Keyboard rows, left to right as on a physical Hebrew keyboard. No final letters: the grid has none. */
    private static final String[] KEYBOARD_ROWS = {"קראטופ", "שדגכעיחל", "זסבהנמצת"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.game_menu);
        toolbar.setNavigationOnClickListener(v -> showExitDialog());
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_hint) {
                new HintDialog().show(getSupportFragmentManager(), "hint");
            } else if (id == R.id.action_report) {
                new ReportClueDialog().show(getSupportFragmentManager(), "report");
            } else if (id == R.id.action_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            return true;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });

        makeGridPlaceholderSquare();
        buildKeyboard();

        // For the sketch: tapping the clue bar shows the "puzzle solved" dialog
        findViewById(R.id.text_clue).setOnClickListener(
                v -> new PuzzleSolvedDialog().show(getSupportFragmentManager(), "solved"));
    }

    private void showExitDialog() {
        new ExitGameDialog().show(getSupportFragmentManager(), "exit");
    }

    /** The grid is square: after layout, shrink the placeholder to the smaller of its width and height. */
    private void makeGridPlaceholderSquare() {
        View placeholder = findViewById(R.id.grid_placeholder);
        placeholder.post(() -> {
            int side = Math.min(placeholder.getWidth(), placeholder.getHeight());
            ViewGroup.LayoutParams params = placeholder.getLayoutParams();
            params.width = side;
            params.height = side;
            placeholder.setLayoutParams(params);
        });
    }

    private void buildKeyboard() {
        LinearLayout keyboard = findViewById(R.id.keyboard);
        int keyHeight = dp(46);
        int margin = dp(3);

        for (int r = 0; r < KEYBOARD_ROWS.length; r++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER);
            // The first row has fewer keys: side padding keeps its keys the same size as the others
            if (r == 0) {
                row.setPadding(dp(40), 0, dp(40), 0);
            }

            String letters = KEYBOARD_ROWS[r];
            for (int i = 0; i < letters.length(); i++) {
                TextView key = new TextView(this);
                key.setText(String.valueOf(letters.charAt(i)));
                key.setTextSize(20);
                key.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                key.setGravity(Gravity.CENTER);
                key.setBackgroundResource(R.drawable.bg_key);
                row.addView(key, keyParams(keyHeight, margin, 1f));
            }

            // Backspace at the end of the last row
            if (r == KEYBOARD_ROWS.length - 1) {
                ImageView backspace = new ImageView(this);
                backspace.setImageResource(R.drawable.ic_backspace);
                backspace.setScaleType(ImageView.ScaleType.CENTER);
                backspace.setBackgroundResource(R.drawable.bg_key);
                backspace.setContentDescription("מחיקה");
                row.addView(backspace, keyParams(keyHeight, margin, 1.5f));
            }
            keyboard.addView(row);
        }
    }

    /** Keys share the row width by weight; the backspace gets a bigger weight, so it is wider. */
    private LinearLayout.LayoutParams keyParams(int height, int margin, float weight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, height, weight);
        params.setMargins(margin, margin, margin, margin);
        return params;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
