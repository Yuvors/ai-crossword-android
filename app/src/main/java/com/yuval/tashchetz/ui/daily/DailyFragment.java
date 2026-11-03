package com.yuval.tashchetz.ui.daily;

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
 * Daily tab: today's puzzle, a countdown to the next one, and the leaderboard.
 * Sketch only: the leaderboard and the countdown are placeholder data.
 */
public class DailyFragment extends Fragment {

    /** Placeholder rows: name, solving time. */
    private static final String[][] PLACEHOLDER_LEADERBOARD = {
            {"נועה", "4:12"},
            {"איתי", "4:58"},
            {"מאיה", "5:31"},
            {"יובל (אתה)", "6:05"},
            {"דניאל", "6:47"},
    };

    public DailyFragment() {
        super(R.layout.fragment_daily);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_play_daily).setOnClickListener(
                v -> startActivity(new Intent(requireContext(), GameActivity.class)));

        LinearLayout list = view.findViewById(R.id.list_leaderboard);
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < PLACEHOLDER_LEADERBOARD.length; i++) {
            View item = inflater.inflate(R.layout.item_leaderboard, list, false);
            ((TextView) item.findViewById(R.id.text_rank)).setText(String.valueOf(i + 1));
            ((TextView) item.findViewById(R.id.text_name)).setText(PLACEHOLDER_LEADERBOARD[i][0]);
            ((TextView) item.findViewById(R.id.text_time)).setText(PLACEHOLDER_LEADERBOARD[i][1]);
            list.addView(item);
        }
    }
}
