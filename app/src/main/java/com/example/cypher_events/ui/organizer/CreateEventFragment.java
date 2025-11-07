package com.example.cypher_events.ui.organizer;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cypher_events.R;
import com.example.cypher_events.domain.model.Event;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CreateEventFragment extends Fragment {

    // UI
    private View btnStart, btnEnd, btnSubmit, btnExit;
    private TextInputEditText etTitle, etDesc, etLocation, etContact, etRegStart, etRegEnd, etLotterySample, etWaitlistLimit;

    // Chosen times
    private Long regStartUtc = null;
    private Long regEndUtc = null;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // find views
        btnStart = v.findViewById(R.id.btn_reg_start);
        btnEnd   = v.findViewById(R.id.btn_reg_end);
        btnSubmit = v.findViewById(R.id.btn_submit_event);
        btnExit   = v.findViewById(R.id.btn_exit);

        etTitle   = v.findViewById(R.id.et_title);
        etDesc    = v.findViewById(R.id.et_description);
        etLocation= v.findViewById(R.id.et_location);
        etContact = v.findViewById(R.id.et_contact);
        etRegStart= v.findViewById(R.id.et_reg_start);
        etRegEnd  = v.findViewById(R.id.et_reg_end);
        etLotterySample = v.findViewById(R.id.et_lottery_sample);
        etWaitlistLimit = v.findViewById(R.id.et_waitlist_limit);

        // listeners
        btnStart.setOnClickListener(view -> pickDateTime(true));
        btnEnd.setOnClickListener(view -> pickDateTime(false));
        btnSubmit.setOnClickListener(view -> onSubmit());
        btnExit.setOnClickListener(view -> requireActivity().onBackPressed());
    }

    private void pickDateTime(boolean isStart) {
        // 1) Pick a DATE (returns selection at UTC midnight)
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText(isStart ? "Select opening date" : "Select closing date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selectionUtcMidnight -> {
            // 2) Then pick a TIME
            boolean is24 = DateFormat.is24HourFormat(requireContext());
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(is24 ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
                    .setTitleText(isStart ? "Select opening time" : "Select closing time")
                    .build();

            timePicker.addOnPositiveButtonClickListener(ignored -> {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Build a UTC calendar at selected date + chosen time
                Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                utc.setTimeInMillis(selectionUtcMidnight); // this is at 00:00 UTC of that date
                utc.set(Calendar.HOUR_OF_DAY, hour);
                utc.set(Calendar.MINUTE, minute);
                utc.set(Calendar.SECOND, 0);
                utc.set(Calendar.MILLISECOND, 0);
                long millisUtc = utc.getTimeInMillis();

                if (isStart) {
                    regStartUtc = millisUtc;
                    etRegStart.setText(formatForDisplay(millisUtc));
                } else {
                    regEndUtc = millisUtc;
                    etRegEnd.setText(formatForDisplay(millisUtc));
                }
            });

            timePicker.show(getParentFragmentManager(), isStart ? "tp_open" : "tp_close");
        });

        datePicker.show(getParentFragmentManager(), isStart ? "dp_open" : "dp_close");
    }

    // Show in user's local timezone, pretty format
    private String formatForDisplay(long epochMillisUtc) {
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy • h:mm a", Locale.getDefault());
        df.setTimeZone(TimeZone.getDefault());
        return df.format(epochMillisUtc);
    }

    private void onSubmit() {
        // validate basics
        String title = safe(etTitle);
        String desc  = safe(etDesc);
        String loc   = safe(etLocation);
        String contact = safe(etContact);

        if (title.isEmpty()) { toast("Please enter a title."); return; }
        if (regStartUtc == null || regEndUtc == null) { toast("Please set registration open and close."); return; }
        if (regEndUtc < regStartUtc) { toast("Registration must close after it opens."); return; }


        Event event = new Event();
        event.setEvent_id(null);
        event.setEvent_title(title);
        event.setEvent_description(desc);
        event.setEvent_location(loc);
        event.setEvent_signupStartUtc(regStartUtc);
        event.setEvent_signupEndUtc(regEndUtc);
        // optional fields
        // event.setEvent_status("Open");
        // event.setEvent_category(...);
        // event.setEvent_capacity(parseIntSafe(etLotterySample or a capacity field if you add one));
        // event.setEvent_isActive(true);

        // write
        db.collection("events")
                .add(event.toMap())
                .addOnSuccessListener(ref -> {
                    // If you want to store the auto id back into the doc:
                    ref.update("Event_id", ref.getId());
                    toast("Event created.");
                    requireActivity().onBackPressed();
                })
                .addOnFailureListener(e -> toast("Failed to create: " + e.getMessage()));
    }

    private int parseIntSafe(TextInputEditText et) {
        if (et == null) return 0;
        String s = safe(et);
        if (s.isEmpty()) return 0;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }

    private String safe(TextInputEditText et) {
        return et != null && et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void toast(String msg) {
        View root = getView();
        if (root != null) Snackbar.make(root, msg, Snackbar.LENGTH_LONG).show();
    }
}


