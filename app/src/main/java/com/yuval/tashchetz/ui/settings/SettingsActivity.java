package com.yuval.tashchetz.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.yuval.tashchetz.R;
import com.yuval.tashchetz.ui.auth.LoginActivity;

/**
 * Settings screen.
 * Sketch only: the switches are not saved yet (SharedPreferences will come later).
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        findViewById(R.id.button_logout).setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            // Clear the back stack, so Back does not return to the app after logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
