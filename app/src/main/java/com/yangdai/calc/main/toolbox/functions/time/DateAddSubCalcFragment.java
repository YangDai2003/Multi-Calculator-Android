package com.yangdai.calc.main.toolbox.functions.time;

import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.yangdai.calc.R;

import java.util.Date;
import java.util.Locale;

/**
 * @author 30415
 */
public class DateAddSubCalcFragment extends Fragment {

    private final Date chosenTimeStart = new Date(MaterialDatePicker.todayInUtcMilliseconds());
    private int inputtedDay = 0;
    private int inputtedMonth = 0;
    private int inputtedYear = 0;
    private boolean inputIsAdd = true;

    private Button btnStartDate;
    private TextView textResultDate;
    private RadioButton radioButtonAdd;
    private RadioButton radioButtonSub;

    public static DateAddSubCalcFragment newInstance() {
        return new DateAddSubCalcFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_addsub_calc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initControls(view);
    }

    private void initControls(View view) {

        MaterialDatePicker<Long> materialDatePickerStart = MaterialDatePicker.Builder.datePicker()
                .setTitleText("")
                .setNegativeButtonText(android.R.string.cancel)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        materialDatePickerStart.addOnPositiveButtonClickListener(selection -> {
            chosenTimeStart.setTime(selection);
            updateTimeStart();
            calculate();
        });

        btnStartDate = view.findViewById(R.id.btn_start_date);
        // Show the dialog when the start date button is clicked
        btnStartDate.setOnClickListener(v -> materialDatePickerStart.show(getParentFragmentManager(), "DATE_PICKER_START"));

        textResultDate = view.findViewById(R.id.text_result_date);
        radioButtonSub = view.findViewById(R.id.radio_sub);
        radioButtonAdd = view.findViewById(R.id.radio_add);

        EditText editDay = view.findViewById(R.id.edit_day);
        EditText editMonth = view.findViewById(R.id.edit_month);
        EditText editYear = view.findViewById(R.id.edit_year);

        editDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    inputtedDay = Integer.parseInt(s.toString());
                } else {
                    inputtedDay = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculate();
            }
        });

        editMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    inputtedMonth = Integer.parseInt(s.toString());
                } else {
                    inputtedMonth = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculate();
            }
        });

        editYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    inputtedYear = Integer.parseInt(s.toString());
                } else {
                    inputtedYear = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculate();
            }
        });

        radioButtonAdd.setOnClickListener((View v) -> {
            inputIsAdd = true;
            radioButtonSub.setChecked(false);
            calculate();
        });

        radioButtonSub.setOnClickListener((View v) -> {
            inputIsAdd = false;
            radioButtonAdd.setChecked(false);
            calculate();
        });

        view.findViewById(R.id.btn_clear).setOnClickListener((v) -> {
            editYear.setText("");
            editMonth.setText("");
            editDay.setText("");
            textResultDate.setText("");
        });

        updateTimeStart();
    }

    private void updateTimeStart() {
        btnStartDate.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(chosenTimeStart));
    }

    private void calculate() {
        try {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(chosenTimeStart);
            calendar1.add(Calendar.DAY_OF_YEAR, inputIsAdd ? inputtedDay : -inputtedDay);
            calendar1.add(Calendar.MONTH, inputIsAdd ? inputtedMonth : -inputtedMonth);
            calendar1.add(Calendar.YEAR, inputIsAdd ? inputtedYear : -inputtedYear);
            textResultDate.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(calendar1.getTime()));
        } catch (Exception e) {
            textResultDate.setText("");
        }
    }
}