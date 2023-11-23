package com.yangdai.calc.main.toolbox.functions.time;

import android.icu.text.DateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.yangdai.calc.R;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * @author 30415
 */
public class DateDifferCalcFragment extends Fragment {
    private Button btnStartDate;
    private Button btnEndDate;
    private TextView textResultDate;

    private final Date chosenTimeStart = new Date(MaterialDatePicker.todayInUtcMilliseconds());
    private final Date chosenTimeEnd = new Date(MaterialDatePicker.todayInUtcMilliseconds());

    public static DateDifferCalcFragment newInstance() {
        return new DateDifferCalcFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmen_date_diff_calc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initControls(view);
    }

    private void initControls(View view) {

        MaterialDatePicker<Long> materialDatePickerStart = MaterialDatePicker.Builder.datePicker()
                .setTitleText("")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setNegativeButtonText(android.R.string.cancel)
                .build();

        materialDatePickerStart.addOnPositiveButtonClickListener(selection -> {
            chosenTimeStart.setTime(selection);
            updateTimeStart();
            calculate();
        });

        MaterialDatePicker<Long> materialDatePickerEnd = MaterialDatePicker.Builder.datePicker()
                .setTitleText("")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setNegativeButtonText(android.R.string.cancel)
                .build();

        materialDatePickerEnd.addOnPositiveButtonClickListener(selection -> {
            chosenTimeEnd.setTime(selection);
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
            long durationInMillis = chosenTimeEndS.getTime() - chosenTimeStartS.getTime();
            long days = TimeUnit.MILLISECONDS.toDays(durationInMillis);
            textResultDate.setText(String.valueOf(days));
        } catch (Exception e) {
            textResultDate.setText("");
        }
    }
}