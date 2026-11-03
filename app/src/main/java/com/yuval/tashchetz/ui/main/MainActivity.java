package com.yuval.tashchetz.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yuval.tashchetz.R;
import com.yuval.tashchetz.ui.daily.DailyFragment;
import com.yuval.tashchetz.ui.home.HomeFragment;
import com.yuval.tashchetz.ui.learn.LearnFragment;
import com.yuval.tashchetz.ui.multiplayer.MultiplayerFragment;
import com.yuval.tashchetz.ui.profile.ProfileFragment;

/**
 * Hosts the five main tabs and switches between them with the bottom navigation bar.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            showFragment(createFragment(item.getItemId()));
            return true;
        });

        // On first launch show the home tab; after rotation the FragmentManager restores the current one
        if (savedInstanceState == null) {
            showFragment(new HomeFragment());
        }
    }

    private Fragment createFragment(int itemId) {
        if (itemId == R.id.nav_learn) {
            return new LearnFragment();
        } else if (itemId == R.id.nav_daily) {
            return new DailyFragment();
        } else if (itemId == R.id.nav_multiplayer) {
            return new MultiplayerFragment();
        } else if (itemId == R.id.nav_profile) {
            return new ProfileFragment();
        }
        return new HomeFragment();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
