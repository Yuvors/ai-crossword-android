package com.yuval.tashchetz.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yuval.tashchetz.R;
import com.yuval.tashchetz.ui.main.MainActivity;

/**
 * Registration screen.
 * Sketch only: the register button goes straight to the main screen, without Firebase Auth.
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.button_register).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        findViewById(R.id.link_login).setOnClickListener(v -> finish());
    }
}
