package com.yangdai.calc.main.toolbox.functions.shopping;

import static com.yangdai.calc.utils.Utils.closeKeyboard;
import static com.yangdai.calc.utils.Utils.formatNumberFinance;

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
public class UnitPriceFragment extends Fragment implements TextWatcher {
    private TextInputEditText etQuantity, etPrice;
    private TextView tvUnitPrice;

    public UnitPriceFragment() {
    }

    public static UnitPriceFragment newInstance() {
        return new UnitPriceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unit_price, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        etQuantity.removeTextChangedListener(this);
        etPrice.removeTextChangedListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etQuantity = view.findViewById(R.id.etQuantity);
        etPrice = view.findViewById(R.id.etPrice);
        tvUnitPrice = view.findViewById(R.id.tvUnitPrice);
        etPrice.addTextChangedListener(this);
        etQuantity.addTextChangedListener(this);
        etQuantity.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                etQuantity.clearFocus();
                return true;
            }
            return false;
        });
    }

    private void calculateUnitPrice() {
        if (!TextUtils.isEmpty(etQuantity.getText()) && !TextUtils.isEmpty(etPrice.getText())) {
            String quantityStr = etQuantity.getText().toString();
            String priceStr = etPrice.getText().toString();

            try {
                double quantity = Double.parseDouble(quantityStr);
                double price = Double.parseDouble(priceStr);
                if (quantity == 0) {
                    return;
                }

                double res = price / quantity;

                tvUnitPrice.setText(formatNumberFinance(String.valueOf(res)));
            } catch (Exception e) {
                tvUnitPrice.setText("");
            }

        } else {
            tvUnitPrice.setText("");
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
        calculateUnitPrice();
    }
}