package com.yuval.tashchetz.ui.profile;

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
import com.yuval.tashchetz.ui.settings.SettingsActivity;

/**
 * Profile tab: statistics, achievements and history.
 * Sketch only: the stats, achievements and history are placeholder data.
 */
public class ProfileFragment extends Fragment {

    /** Placeholder rows: achievement name, description, unlocked. */
    private static final String[][] PLACEHOLDER_ACHIEVEMENTS = {
            {"צעד ראשון", "פתרת את התשחץ הראשון שלך", "true"},
            {"רצף של שבוע", "פתרת תשחץ 7 ימים ברצף", "true"},
            {"בלי רמזים", "פתרת 15×15 בלי אף רמז", "false"},
            {"שחקן קבוצתי", "פתרת 5 תשחצים משותפים", "false"},
    };

    /** Placeholder rows: size, topic, details, score. */
    private static final String[][] PLACEHOLDER_HISTORY = {
            {"15×15", "תשחץ #127", "אתמול · 22:30 דקות · 2 רמזים", "+180"},
            {"10×10", "יומי — 2 בנובמבר", "לפני יומיים · 6:05 דקות", "+95"},
            {"10×10", "דו-קרב מול איתי", "לפני 3 ימים · ניצחון", "+120"},
    };

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_settings).setOnClickListener(
                v -> startActivity(new Intent(requireContext(), SettingsActivity.class)));

        LayoutInflater inflater = getLayoutInflater();

        LinearLayout achievements = view.findViewById(R.id.list_achievements);
        for (String[] row : PLACEHOLDER_ACHIEVEMENTS) {
            View item = inflater.inflate(R.layout.item_achievement, achievements, false);
            ((TextView) item.findViewById(R.id.text_achievement_name)).setText(row[0]);
            ((TextView) item.findViewById(R.id.text_achievement_desc)).setText(row[1]);
            // Locked achievements are shown faded
            if (!Boolean.parseBoolean(row[2])) {
                item.setAlpha(0.4f);
            }
            achievements.addView(item);
        }

        LinearLayout history = view.findViewById(R.id.list_history);
        for (String[] row : PLACEHOLDER_HISTORY) {
            View item = inflater.inflate(R.layout.item_history, history, false);
            ((TextView) item.findViewById(R.id.text_size)).setText(row[0]);
            ((TextView) item.findViewById(R.id.text_topic)).setText(row[1]);
            ((TextView) item.findViewById(R.id.text_details)).setText(row[2]);
            ((TextView) item.findViewById(R.id.text_score)).setText(row[3]);
            history.addView(item);
        }
    }
}
