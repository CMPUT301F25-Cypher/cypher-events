package com.example.cypher_events.ui.entrant;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cypher_events.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog to filter events by interests and availability.
 */

public class FilterDialogFragment extends DialogFragment{

    public interface FilterListener {
        void onApply(List<String> interests, long startDateUtc, long endDateUtc);
    }

    private FilterListener listener;

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_filter);

        Button applyButton = dialog.findViewById(R.id.btnApply);
        CheckBox music = dialog.findViewById(R.id.checkboxMusic);
        CheckBox tech = dialog.findViewById(R.id.checkboxTech);
        DatePicker startDate = dialog.findViewById(R.id.startDatePicker);
        DatePicker endDate = dialog.findViewById(R.id.endDatePicker);

        applyButton.setOnClickListener(v -> {
            List<String> interests = new ArrayList<>();
            if (music.isChecked()) interests.add("Music");
            if (tech.isChecked()) interests.add("Tech");

            // Convert DatePicker to UTC millis
            long startUtc = startDate.getYear() * 1000L;
            long endUtc = endDate.getYear() * 1000L;

            if (listener != null) listener.onApply(interests, startUtc, endUtc);
            dismiss();
        });

        return dialog;
    }
}
    }
}
