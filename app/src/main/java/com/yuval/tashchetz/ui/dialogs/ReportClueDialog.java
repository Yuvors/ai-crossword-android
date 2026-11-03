package com.yuval.tashchetz.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yuval.tashchetz.R;

/**
 * Report a wrong or unclear clue.
 * Sketch only: the report is not sent anywhere yet.
 */
public class ReportClueDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.dialog_report)
                .setPositiveButton("שליחה", null)
                .setNegativeButton("ביטול", null)
                .create();
    }
}
