package com.yuval.tashchetz.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yuval.tashchetz.R;
import com.yuval.tashchetz.ui.main.MainActivity;

/**
 * Login screen.
 * Sketch only: the login button goes straight to the main screen, without Firebase Auth.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.button_login).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.link_register).setOnClickListener(
                v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}
