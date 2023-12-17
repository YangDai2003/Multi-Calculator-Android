package com.yangdai.calc.main.toolbox.functions.shopping;

import static com.yangdai.calc.utils.Utils.closeKeyboard;
import static com.yangdai.calc.utils.Utils.formatNumberFinance;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.yangdai.calc.R;


/**
 * @author 30415
 */
public class DiscountFragment extends Fragment implements TextWatcher {
    private TextInputEditText etOriginalPrice, etDiscountPercentage;
    private TextView tvDiscountedPrice, tvSavedAmount;

    public DiscountFragment() {
    }

    public static DiscountFragment newInstance() {
        return new DiscountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discount, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etOriginalPrice = view.findViewById(R.id.etOriginalPrice);
        etDiscountPercentage = view.findViewById(R.id.etDiscountPercentage);
        tvDiscountedPrice = view.findViewById(R.id.tvDiscountedPrice);
        tvSavedAmount = view.findViewById(R.id.tvSavedAmount);

        etOriginalPrice.addTextChangedListener(this);
        etDiscountPercentage.addTextChangedListener(this);
        etDiscountPercentage.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                etDiscountPercentage.clearFocus();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        etOriginalPrice.removeTextChangedListener(this);
        etDiscountPercentage.removeTextChangedListener(this);
    }

    @SuppressLint("DefaultLocale")
    private void calculateDiscount() {
        if (!TextUtils.isEmpty(etOriginalPrice.getText()) && !TextUtils.isEmpty(etDiscountPercentage.getText())) {
            String originalPriceStr = etOriginalPrice.getText().toString();
            String discountPercentageStr = etDiscountPercentage.getText().toString();

            try {
                double originalPrice = Double.parseDouble(originalPriceStr);
                double discountPercentage = Double.parseDouble(discountPercentageStr);

                double discountAmount = originalPrice * (discountPercentage / 100);
                double discountedPrice = originalPrice - discountAmount;

                tvDiscountedPrice.setText(formatNumberFinance(String.valueOf(discountedPrice)));
                tvSavedAmount.setText(formatNumberFinance(String.valueOf(discountAmount)));
            } catch (Exception e) {
                tvDiscountedPrice.setText("");
                tvSavedAmount.setText("");
            }

        } else {
            tvDiscountedPrice.setText("");
            tvSavedAmount.setText("");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        calculateDiscount();
    }
}