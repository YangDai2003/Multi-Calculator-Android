package com.yangdai.calc;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.elevation.SurfaceColors;
import com.yangdai.calc.utils.TouchAnimation;

import java.util.Objects;

/**
 * @author 30415
 */
public class ChineseNumberConversionActivity extends AppCompatActivity implements View.OnClickListener
        , SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] CN_UPPER_MONETARY_UNIT = {"分", "角", "元", "拾", "佰", "仟",
            "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾", "佰", "仟"};
    private static final String CN_FULL = "整";
    private static final String CN_NEGATIVE = "负";
    private static final String CN_ZERO_FULL = "零元";
    private static final int MONEY_PRECISION = 2;
    private TextView tvInput;
    private TextView tvResults;

    private String showText = "0";
    private String resultsText = "零元整";
    private SharedPreferences settings;
    private static final int[] BUTTON_IDS = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5,
            R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_pt, R.id.btn_clr, R.id.btn_negate, R.id.iv_del};


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        setContentView(R.layout.activity_chinese_number_conversion);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);

        if (settings.getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        tvInput = findViewById(R.id.input_textview);
        tvResults = findViewById(R.id.output_textview);

        for (int buttonId : BUTTON_IDS) {
            findViewById(buttonId).setHapticFeedbackEnabled(settings.getBoolean("vib", false));
            findViewById(buttonId).setOnClickListener(this);
            TouchAnimation touchAnimation = new TouchAnimation(findViewById(buttonId));
            findViewById(buttonId).setOnTouchListener(touchAnimation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settings.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() != R.id.iv_del && v.getId() != R.id.btn_clr
                && v.getId() != R.id.btn_negate && v.getId() != R.id.btn_pt) {
            if (!showText.contains(".") && showText.length() >= 16) {
                return;
            }
        }

        String inputText = "";
        if (v.getId() != R.id.iv_del) {
            inputText = ((TextView) v).getText().toString();
        }

        if (v.getId() == R.id.btn_clr) {
            clear();
        } else if (v.getId() == R.id.iv_del) {
            if (Double.parseDouble(showText) <= 0 && showText.length() == 2) {
                showText = "0";
                operation();
                refreshText();
            } else {
                delete();
            }
        } else if (v.getId() == R.id.btn_negate) {
            if (Double.parseDouble(showText) > 0) {
                showText = "-" + showText;
            } else if (Double.parseDouble(showText) < 0) {
                showText = showText.substring(1);
            }
            operation();
            refreshText();
        } else if (v.getId() == R.id.btn_pt) {
            if (!showText.contains(".")) {
                showText = showText + ".";
            }
            refreshText();
        } else {
            if (showText.contains(".")) {
                if (showText.substring(showText.indexOf(".")).length() >= 3) {
                    return;
                }
            }
            if ("0".equals(showText)) {
                showText = inputText;
            } else {
                showText = showText + inputText;
            }
            operation();
            refreshText();
        }
    }

    private void clear() {
        showText = "0";
        resultsText = "零元整";
        refreshText();
    }

    private void refreshText() {
        tvInput.setText(showText);
        tvResults.setText(resultsText);
    }

    private void delete() {
        if (showText.length() != 0) {
            showText = showText.substring(0, showText.length() - 1);
            if (showText.length() == 0) {
                showText = "0";
            }
            operation();
            refreshText();
        }
    }

    private void operation() {
        BigDecimal numberOfMoney = new BigDecimal(showText);
        StringBuilder sb = new StringBuilder();
        int signum = numberOfMoney.signum();
        if (signum == 0) {
            resultsText = CN_ZERO_FULL + CN_FULL;
            return;
        }

        BigDecimal scaledMoney = numberOfMoney.movePointRight(MONEY_PRECISION).setScale(0, 4).abs();
        long number = scaledMoney.longValue();
        int numUnit;
        int numIndex = 0;
        boolean getZero = false;
        int zeroSize = 0;

        while (number > 0) {
            numUnit = (int) (number % 10);
            if (numUnit > 0) {
                if (numIndex == 9 || numIndex == 13 || numIndex == 17) {
                    if (zeroSize >= 3) {
                        sb.insert(0, CN_UPPER_MONETARY_UNIT[numIndex]);
                    }
                }
                sb.insert(0, CN_UPPER_MONETARY_UNIT[numIndex]);
                sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                getZero = false;
                zeroSize = 0;
            } else {
                ++zeroSize;
                if (numIndex != 0 && numIndex != 1 && numIndex != 2 &&
                        numIndex != 6 && numIndex != 10 && numIndex != 14) {
                    if (!getZero) {
                        sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                    }
                }

                if (numIndex == 2) {
                    sb.insert(0, CN_UPPER_MONETARY_UNIT[numIndex]);
                } else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
                    sb.insert(0, CN_UPPER_MONETARY_UNIT[numIndex]);
                }
                getZero = true;
            }

            number /= 10;
            ++numIndex;
        }

        if (signum == -1) {
            sb.insert(0, CN_NEGATIVE);
        }
        if (!sb.toString().contains("分") && !sb.toString().contains("角")) {
            sb.append(CN_FULL);
        }
        resultsText = sb.toString();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        if ("vib".equals(s)) {
            for (int buttonId : BUTTON_IDS) {
                findViewById(buttonId).setHapticFeedbackEnabled(settings.getBoolean("vib", false));
            }
        }
    }
}
