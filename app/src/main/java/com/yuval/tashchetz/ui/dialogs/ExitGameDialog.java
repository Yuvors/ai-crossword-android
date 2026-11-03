package com.yuval.tashchetz.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Asks before leaving a puzzle in the middle. Progress is saved, so the user can come back.
 */
public class ExitGameDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("לצאת מהתשחץ?")
                .setMessage("ההתקדמות שלך נשמרת, ותוכל להמשיך מאיפה שעצרת.")
                .setPositiveButton("יציאה", (dialog, which) -> requireActivity().finish())
                .setNegativeButton("להמשיך לפתור", null)
                .create();
    }
}
