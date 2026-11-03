package com.yuval.tashchetz.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yuval.tashchetz.R;

/**
 * Shown when the puzzle is solved: time, hints, points and a star rating.
 * Sketch only: the numbers are placeholders in the layout.
 */
public class PuzzleSolvedDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.dialog_solved)
                .setPositiveButton("לתשחץ הבא", (dialog, which) -> requireActivity().finish())
                .setNegativeButton("למסך הבית", (dialog, which) -> requireActivity().finish())
                .create();
    }
}
