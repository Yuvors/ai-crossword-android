package com.yuval.tashchetz.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yuval.tashchetz.R;
import com.yuval.tashchetz.ui.game.GameActivity;

/**
 * Home tab: continue the open puzzle, or start a new one by size.
 * Sketch only: all the numbers on the screen are placeholders in the layout.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View.OnClickListener openGame = v -> startActivity(new Intent(requireContext(), GameActivity.class));
        view.findViewById(R.id.card_continue).setOnClickListener(openGame);
        view.findViewById(R.id.card_small).setOnClickListener(openGame);
        view.findViewById(R.id.card_large).setOnClickListener(openGame);
        view.findViewById(R.id.card_daily_teaser).setOnClickListener(openGame);
    }
}
