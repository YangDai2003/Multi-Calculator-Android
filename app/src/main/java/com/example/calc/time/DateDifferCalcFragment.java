package com.example.calc.time;

import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.calc.R;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Date;
import java.util.Locale;


/**
 * @author 30415
 */
public class DateDifferCalcFragment extends Fragment {
    private Button btnStartDate;
    private Button btnEndDate;
    private TextView textResultDate;

    private Date chosenTimeStart = new Date();
    private Date chosenTimeEnd = new Date();

    public static DateDifferCalcFragment newInstance() {
        return new DateDifferCalcFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_page_date_diff_calc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initControls(view);
    }

    private void initControls(View view) {
        Calendar calendar = Calendar.getInstance();

        // Initialize the MaterialDatePicker for the start date
        MaterialDatePicker.Builder<Long> builderStart = MaterialDatePicker.Builder.datePicker();
        builderStart.setTitleText("");
        builderStart.setSelection(calendar.getTimeInMillis());
        MaterialDatePicker<Long> materialDatePickerStart = builderStart.build();

        // Set the callback for when a start date is selected
        materialDatePickerStart.addOnPositiveButtonClickListener(selection -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selection);
            chosenTimeStart = selectedDate.getTime();
            updateTimeStart();
            calculate();
        });

        // Initialize the MaterialDatePicker for the end date
        MaterialDatePicker.Builder<Long> builderEnd = MaterialDatePicker.Builder.datePicker();
        builderEnd.setTitleText("");
        builderEnd.setSelection(calendar.getTimeInMillis());
        MaterialDatePicker<Long> materialDatePickerEnd = builderEnd.build();

        // Set the callback for when an end date is selected
        materialDatePickerEnd.addOnPositiveButtonClickListener(selection -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selection);
            chosenTimeEnd = selectedDate.getTime();
            updateTimeEnd();
            calculate();
        });

        btnStartDate = view.findViewById(R.id.btn_start_date);
        btnEndDate = view.findViewById(R.id.btn_end_date);

        textResultDate = view.findViewById(R.id.text_result_date);

        // Show the dialog when the start date button is clicked
        btnStartDate.setOnClickListener(v -> materialDatePickerStart.show(getParentFragmentManager(), "DATE_PICKER_START"));

        // Show the dialog when the end date button is clicked
        btnEndDate.setOnClickListener(v -> materialDatePickerEnd.show(getParentFragmentManager(), "DATE_PICKER_END"));

        updateTimeStart();
        updateTimeEnd();
    }

    private void updateTimeStart() {
        btnStartDate.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(chosenTimeStart));
    }

    private void updateTimeEnd() {
        btnEndDate.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(chosenTimeEnd));
    }

    private void calculate() {
        Date chosenTimeStartS;
        Date chosenTimeEndS;

        if (chosenTimeStart.compareTo(chosenTimeEnd) == 0) {
            textResultDate.setText(getString(R.string.text_same_day));
            return;
        }
        if (chosenTimeStart.after(chosenTimeEnd)) {
            chosenTimeStartS = chosenTimeEnd;
            chosenTimeEndS = chosenTimeStart;
        } else {
            chosenTimeStartS = chosenTimeStart;
            chosenTimeEndS = chosenTimeEnd;
        }

        try {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(chosenTimeStartS);
            cal2.setTime(chosenTimeEndS);
            long diff = cal2.getTimeInMillis() - cal1.getTimeInMillis();
            long days = diff / (24 * 60 * 60 * 1000);
            textResultDate.setText(String.valueOf(days));
        } catch (Exception e) {
            textResultDate.setText("");
        }
    }
}