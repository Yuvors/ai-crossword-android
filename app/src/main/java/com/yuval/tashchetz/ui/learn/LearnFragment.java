package com.yuval.tashchetz.ui.learn;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yuval.tashchetz.R;
import com.yuval.tashchetz.ui.game.GameActivity;

/**
 * Learning tab: the user types a topic and gets a puzzle about it.
 * Sketch only: the list of recent puzzles is placeholder data.
 */
public class LearnFragment extends Fragment {

    /** Placeholder rows: size, topic, details, score. */
    private static final String[][] PLACEHOLDER_RECENT = {
            {"10×10", "מערכת השמש", "לפני יומיים · 9:12 דקות", "★ 3"},
            {"10×10", "מלחמת העצמאות", "לפני 4 ימים · 11:40 דקות", "★ 2"},
            {"15×15", "תורת הקבוצות", "לפני שבוע · 24:05 דקות", "★ 3"},
    };

    public LearnFragment() {
        super(R.layout.fragment_learn);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_generate).setOnClickListener(
                v -> startActivity(new Intent(requireContext(), GameActivity.class)));

        LinearLayout list = view.findViewById(R.id.list_recent);
        LayoutInflater inflater = getLayoutInflater();
        for (String[] row : PLACEHOLDER_RECENT) {
            View item = inflater.inflate(R.layout.item_history, list, false);
            ((TextView) item.findViewById(R.id.text_size)).setText(row[0]);
            ((TextView) item.findViewById(R.id.text_topic)).setText(row[1]);
            ((TextView) item.findViewById(R.id.text_details)).setText(row[2]);
            ((TextView) item.findViewById(R.id.text_score)).setText(row[3]);
            list.addView(item);
        }
    }
}
