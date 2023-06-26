package com.yangdai.calc;

import static com.yangdai.calc.utils.Utils.closeKeyboard;
import static com.yangdai.calc.utils.Utils.formatNumberFinance;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.elevation.SurfaceColors;
import com.yangdai.calc.databinding.ActivityDiscountBinding;

import java.util.Objects;

/**
 * @author 30415
 */
public class DiscountActivity extends AppCompatActivity {

    ActivityDiscountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding = ActivityDiscountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        binding.etOriginalPrice.addTextChangedListener(textWatcher);
        binding.etDiscountPercentage.addTextChangedListener(textWatcher);
        binding.etDiscountPercentage.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(this);
                binding.etDiscountPercentage.clearFocus();
                return true;
            }
            return false;
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            calculateDiscount();
        }
    };

    @SuppressLint("DefaultLocale")
    private void calculateDiscount() {
        String originalPriceStr = binding.etOriginalPrice.getText().toString();
        String discountPercentageStr = binding.etDiscountPercentage.getText().toString();
        if (!originalPriceStr.isEmpty() && !discountPercentageStr.isEmpty()) {
            double originalPrice = Double.parseDouble(originalPriceStr);
            double discountPercentage = Double.parseDouble(discountPercentageStr);

            double discountAmount = originalPrice * (discountPercentage / 100);
            double discountedPrice = originalPrice - discountAmount;

            binding.tvDiscountedPrice.setText(formatNumberFinance(String.valueOf(discountedPrice)));
            binding.tvSavedAmount.setText(formatNumberFinance(String.valueOf(discountAmount)));
        } else {
            binding.tvDiscountedPrice.setText("");
            binding.tvSavedAmount.setText("");
        }
    }
}
