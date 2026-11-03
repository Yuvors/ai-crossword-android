package com.yuval.tashchetz.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yuval.tashchetz.R;

/**
 * Hint options: alternative clue, reveal a letter, or reveal the whole word.
 * Sketch only: the options do not do anything yet.
 */
public class HintDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.dialog_hint)
                .setNegativeButton("סגירה", null)
                .create();
    }
}
