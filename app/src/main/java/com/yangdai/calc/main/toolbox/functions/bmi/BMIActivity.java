package com.yangdai.calc.main.toolbox.functions.bmi;

import static com.yangdai.calc.utils.Utils.closeKeyboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;

import com.google.android.material.elevation.SurfaceColors;
import com.yangdai.calc.R;
import com.yangdai.calc.databinding.ActivityBmiBinding;
import com.yangdai.calc.main.toolbox.functions.BaseFunctionActivity;

/**
 * @author 30415
 */
public class BMIActivity extends BaseFunctionActivity implements TextWatcher {
    double heightM = 0, weightKg = 0, bmi = 0, recommendedBmi = 0;
    String weightToLose = "";
    ActivityBmiBinding binding;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            resetColor();

            binding.heightCm.setText("");
            binding.weightKg.setText("");
            binding.bmi.setText(R.string._00_00);
            binding.comment.setText("");
            bmi = 0;
            heightM = 0;
            weightKg = 0;
            recommendedBmi = 0;
            weightToLose = "";
            binding.commentLayout.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.cardView.setCardBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.CardViewRes.setCardBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));

        binding.heightCm.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(this);
                binding.heightCm.clearFocus();
                return true;
            }
            return false;
        });
        binding.weightKg.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(this);
                binding.weightKg.clearFocus();
                return true;
            }
            return false;
        });
        binding.heightCm.addTextChangedListener(this);
        binding.weightKg.addTextChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.heightCm.removeTextChangedListener(this);
        binding.weightKg.removeTextChangedListener(this);
    }

    @Override
    protected void setRootView() {
        binding = ActivityBmiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @SuppressLint("DefaultLocale")
    private void calculateBmi(double weight, double height) {
        resetColor();
        binding.bmi.setText(R.string._00_00);
        binding.comment.setText("");
        bmi = 0;
        recommendedBmi = 0;
        weightToLose = "";
        binding.commentLayout.setVisibility(View.GONE);

        try {
            weightKg = weight;
            heightM = height / 100;
            bmi = weight / (heightM * heightM);
            binding.bmi.setText(String.format("%.2f", bmi));
        } catch (Exception ignored) {

        }

        if (bmi > 0) {
            setMessageBackground();
            findRecommendedBmi();
        }
    }

    private void findRecommendedBmi() {
        if (bmi < 18.50) {
            for (int i = 1; i < 100; i++) {
                double newWeight = weightKg + i;
                recommendedBmi = newWeight / (heightM * heightM);
                if (recommendedBmi >= 18.5) {
                    weightToLose = String.valueOf(i);
                    binding.commentLayout.setVisibility(View.VISIBLE);
                    binding.weightNeed.setText(R.string.need_healthy);
                    binding.showKg.setText(R.string.kg);
                    binding.reCommand.setText(weightToLose);
                    break;
                }
            }

        } else if (bmi > 24.90) {
            for (int i = 1; i < 150; i++) {
                double newWeight = weightKg - i;
                recommendedBmi = newWeight / (heightM * heightM);
                if (recommendedBmi <= 24.9) {
                    weightToLose = String.valueOf(i);
                    binding.commentLayout.setVisibility(View.VISIBLE);
                    binding.weightNeed.setText(R.string.lose_healthy);
                    binding.showKg.setText(R.string.kg);
                    binding.reCommand.setText(weightToLose);
                    break;
                }
            }
        } else {
            weightToLose = String.valueOf(0);
        }
    }

    private void setMessageBackground() {
        if (bmi < 16.0) {
            binding.comment.setText(R.string.very_severely_underweight);
            binding.verySeverelyUnderweight.setBackgroundColor(getColor(R.color.Very_Severely_underweight));
        } else if (bmi >= 16.0 && bmi <= 16.99) {
            binding.comment.setText(R.string.severely_underweight);
            binding.severelyUnderweight.setBackgroundColor(getColor(R.color.Severely_underweight));
        } else if (bmi >= 17.0 && bmi <= 18.49) {
            binding.comment.setText(R.string.underweight);
            binding.underweight.setBackgroundColor(getColor(R.color.Underweight));

        } else if (bmi >= 18.5 && bmi <= 24.99) {
            binding.comment.setText(R.string.healthy);
            binding.healthy.setBackgroundColor(getColor(R.color.Healthy));

        } else if (bmi >= 25.0 && bmi <= 29.99) {
            binding.comment.setText(R.string.overweight);
            binding.overweight.setBackgroundColor(getColor(R.color.Overweight));

        } else if (bmi >= 30.0 && bmi <= 34.99) {
            binding.comment.setText(R.string.obese_class_i);
            binding.obeseClassI.setBackgroundColor(getColor(R.color.Obese_Class_I));

        } else if (bmi >= 35.0 && bmi <= 39.99) {
            binding.comment.setText(R.string.obese_class_ii);
            binding.obeseClassIi.setBackgroundColor(getColor(R.color.Obese_Class_ii));

        } else if (bmi >= 40.0) {
            binding.comment.setText(R.string.obese_class_ii);
            binding.obeseClassIii.setBackgroundColor(getColor(R.color.Obese_Class_iii));
        }
    }

    private void resetColor() {
        binding.verySeverelyUnderweight.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.severelyUnderweight.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.underweight.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.healthy.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.overweight.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.obeseClassI.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.obeseClassIi.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.obeseClassIii.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(binding.heightCm.getText()) && !TextUtils.isEmpty(binding.weightKg.getText())) {
            String cm = binding.heightCm.getText().toString();
            String kg = binding.weightKg.getText().toString();
            try {
                double height = Double.parseDouble(cm);
                double weight = Double.parseDouble(kg);
                if (weight != 0 && height != 0) {
                    calculateBmi(weight, height);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}