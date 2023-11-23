package com.yangdai.calc.main.toolbox.functions.finance;

import static com.yangdai.calc.utils.Utils.closeKeyboard;
import static com.yangdai.calc.utils.Utils.formatNumberFinance;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textfield.TextInputEditText;
import com.yangdai.calc.R;
import com.yangdai.calc.databinding.FragmentBankBinding;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author 30415
 */
public class BankFragment extends Fragment implements TextWatcher {
    FragmentBankBinding binding;
    private TextInputEditText editTextPrincipal, editTextInterestRate, editTextTime;
    int flag;

    public BankFragment() {
    }

    public static BankFragment newInstance() {
        return new BankFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editTextPrincipal.removeTextChangedListener(this);
        editTextInterestRate.removeTextChangedListener(this);
        editTextTime.removeTextChangedListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBankBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextPrincipal = binding.editTextPrincipal;
        editTextInterestRate = binding.editTextInterestRate;
        editTextTime = binding.editTextTime;
        editTextTime.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                editTextTime.clearFocus();
                return true;
            }
            return false;
        });
        editTextTime.addTextChangedListener(this);
        editTextPrincipal.addTextChangedListener(this);
        editTextInterestRate.addTextChangedListener(this);

        Spinner spinner = binding.spinnerPeriod;

        String[] arr = new String[]{getString(R.string.monthly), getString(R.string.quarterly), getString(R.string.half)
                , getString(R.string.yearly), getString(R.string.end)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                flag = i;
                try {
                    calculateInterest();
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void calculateInterest() {
        double principal = Double.parseDouble(Objects.requireNonNull(editTextPrincipal.getText()).toString());
        double interestRate = Double.parseDouble(Objects.requireNonNull(editTextInterestRate.getText()).toString());
        int time = Integer.parseInt(Objects.requireNonNull(editTextTime.getText()).toString());
        int compoundPeriods = 0;
        double interest = 0;
        switch (flag) {
            case 0 -> compoundPeriods = 12;
            case 1 -> compoundPeriods = 4;
            case 2 -> compoundPeriods = 2;
            case 3 -> compoundPeriods = 1;
            case 4 -> interest = principal * (interestRate / 100);
            default -> {
            }
        }
        if (flag != 4) {
            double ratePerPeriod = (interestRate / 100) / compoundPeriods;
            interest = principal * Math.pow(1 + ratePerPeriod, compoundPeriods * (time / 12.0)) - principal;
        }

        double total = interest + principal;

        // 将数据转换为float类型
        float interestValue = Float.parseFloat(String.valueOf(interest));
        float principalValue = Float.parseFloat(String.valueOf(principal));
        // 创建饼状图数据项
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(interestValue, getString(R.string.total_interest)));
        entries.add(new PieEntry(principalValue, getString(R.string.principal)));
        // 创建饼状图数据集
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        // 创建饼状图数据对象
        PieData data = new PieData(dataSet);
        data.setValueTextSize(26);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf(formatNumberFinance(String.valueOf(value)));
            }
        });
        // 获取饼状图视图
        PieChart pieChart = binding.pieChart;
        pieChart.setVisibility(View.VISIBLE);
        // 设置数据
        pieChart.setData(data);
        pieChart.setCenterText(getString(R.string.settlement_amount) + " " + formatNumberFinance(String.valueOf(total)));
        pieChart.setCenterTextSize(22);
        pieChart.getDescription().setEnabled(false);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        // 刷新图表
        pieChart.invalidate();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        try {
            calculateInterest();
        } catch (Exception ignored) {

        }
    }
}
