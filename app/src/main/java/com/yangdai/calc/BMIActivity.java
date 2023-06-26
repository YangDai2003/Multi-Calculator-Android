package com.yangdai.calc;

import static com.yangdai.calc.utils.Utils.closeKeyboard;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.elevation.SurfaceColors;
import com.yangdai.calc.databinding.ActivityBmiBinding;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * @author 30415
 */
public class BMIActivity extends AppCompatActivity {
    float heightCm = 0, weightKg = 0, sum = 0, totalMeter = 0, totalWeight = 0, newSum = 0;
    String strWeight = "";
    ActivityBmiBinding binding;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            resetColor();

            //set All Text Null
            binding.heightCm.setText("");
            binding.weightKg.setText("");
            binding.bmi.setText(R.string._00_00);
            binding.comment.setText("");
            sum = 0;
            heightCm = 0;
            weightKg = 0;
            totalMeter = 0;
            totalWeight = 0;
            newSum = 0;
            strWeight = "";
            binding.commentLayout.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding = ActivityBmiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        binding.cardView.setCardBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.CardViewRes.setCardBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));

        DecimalFormat precision = new DecimalFormat("0.00");

        //Button Clicked
        binding.calculate.setOnClickListener(view -> {

            resetColor();
            binding.bmi.setText(R.string._00_00);
            binding.comment.setText("");
            sum = 0;
            newSum = 0;
            strWeight = "";
            binding.commentLayout.setVisibility(View.GONE);


            try {
                String cm = binding.heightCm.getText().toString();
                String kg = binding.weightKg.getText().toString();
                if (TextUtils.isEmpty(kg)) {
                    binding.weightKg.setError("This Field Can't be Empty or 0");
                } else {
                    weightKg = Float.parseFloat(binding.weightKg.getText().toString());
                    if (weightKg == 0) {
                        binding.weightKg.setError("This Field Can't be Empty or 0");
                    } else {
                        totalWeight = weightKg;
                    }
                }

                if (TextUtils.isEmpty(cm)) {
                    binding.heightCm.setError("This Field Can't be Empty or 0");
                } else {
                    heightCm = Float.parseFloat(binding.heightCm.getText().toString());
                    if (heightCm == 0 || weightKg == 0) {
                        binding.heightCm.setError("This Field Can't be Empty or 0");
                    } else {
                        totalMeter = heightCm / 100;
                        sum = totalWeight / (totalMeter * totalMeter);
                        binding.bmi.setText(precision.format(sum));
                    }
                }
            } catch (Exception ignored) {

            }

            if (sum > 0) {
                setMessageBackground();
                findRecommended();
            }

        });
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
    }

    private void findRecommended() {

        if (sum < 18.50) {
            for (int i = 1; i < 100; i++) {
                float newWeight = totalWeight + i;
                newSum = newWeight / (totalMeter * totalMeter);
                if (newSum >= 18.5) {
                    strWeight = String.valueOf(i);
                    binding.commentLayout.setVisibility(View.VISIBLE);
                    binding.weightNeed.setText(R.string.need_healthy);
                    binding.showKg.setText(R.string.kg);
                    binding.reCommand.setText(strWeight);
                    break;
                }
            }

        } else if (sum > 24.90) {
            for (int i = 1; i < 150; i++) {
                float newWeight = totalWeight - i;
                newSum = newWeight / (totalMeter * totalMeter);
                if (newSum <= 24.9) {
                    strWeight = String.valueOf(i);
                    binding.commentLayout.setVisibility(View.VISIBLE);
                    binding.weightNeed.setText(R.string.lose_healthy);
                    binding.showKg.setText(R.string.kg);
                    binding.reCommand.setText(strWeight);
                    break;
                }
            }
        } else {
            strWeight = String.valueOf(0);
        }
    }

    private void setMessageBackground() {
        if (sum < 16.0) {
            binding.comment.setText(R.string.very_severely_underweight);
            binding.verySeverelyUnderweight.setBackgroundColor(getColor(R.color.Very_Severely_underweight));
        } else if (sum >= 16.0 && sum <= 16.99) {
            binding.comment.setText(R.string.severely_underweight);
            binding.severelyUnderweight.setBackgroundColor(getColor(R.color.Severely_underweight));
        } else if (sum >= 17.0 && sum <= 18.49) {
            binding.comment.setText(R.string.underweight);
            binding.underweight.setBackgroundColor(getColor(R.color.Underweight));

        } else if (sum >= 18.5 && sum <= 24.99) {
            binding.comment.setText(R.string.healthy);
            binding.healthy.setBackgroundColor(getColor(R.color.Healthy));

        } else if (sum >= 25.0 && sum <= 29.99) {
            binding.comment.setText(R.string.overweight);
            binding.overweight.setBackgroundColor(getColor(R.color.Overweight));

        } else if (sum >= 30.0 && sum <= 34.99) {
            binding.comment.setText(R.string.obese_class_i);
            binding.obeseClassI.setBackgroundColor(getColor(R.color.Obese_Class_I));

        } else if (sum >= 35.0 && sum <= 39.99) {
            binding.comment.setText(R.string.obese_class_ii);
            binding.obeseClassIi.setBackgroundColor(getColor(R.color.Obese_Class_ii));

        } else if (sum >= 40.0) {
            binding.comment.setText(R.string.obese_class_ii);
            binding.obeseClassIii.setBackgroundColor(getColor(R.color.Obese_Class_iii));
        }
    }

    private void resetColor() {
        //Chart Background set null
        binding.verySeverelyUnderweight.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.severelyUnderweight.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.underweight.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.healthy.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.overweight.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.obeseClassI.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.obeseClassIi.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.obeseClassIii.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
    }
}