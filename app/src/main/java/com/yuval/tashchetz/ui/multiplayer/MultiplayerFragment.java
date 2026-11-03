package com.yuval.tashchetz.ui.multiplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yuval.tashchetz.R;

/**
 * Multiplayer tab: choose a mode, join a room by code, or pick an open room.
 * Sketch only: the list of open rooms is placeholder data.
 */
public class MultiplayerFragment extends Fragment {

    /** Placeholder rows: room name, details. */
    private static final String[][] PLACEHOLDER_ROOMS = {
            {"החדר של נועה", "פתרון משותף · 10×10 · 2/4 שחקנים"},
            {"דו-קרב מהיר", "דו-קרב · 10×10 · 1/2 שחקנים"},
            {"כיתה י\"א 3", "פתרון משותף · 15×15 · 5/8 שחקנים"},
    };

    public MultiplayerFragment() {
        super(R.layout.fragment_multiplayer);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout list = view.findViewById(R.id.list_rooms);
        LayoutInflater inflater = getLayoutInflater();
        for (String[] row : PLACEHOLDER_ROOMS) {
            View item = inflater.inflate(R.layout.item_room, list, false);
            ((TextView) item.findViewById(R.id.text_room_name)).setText(row[0]);
            ((TextView) item.findViewById(R.id.text_room_details)).setText(row[1]);
            list.addView(item);
        }
    }
}
