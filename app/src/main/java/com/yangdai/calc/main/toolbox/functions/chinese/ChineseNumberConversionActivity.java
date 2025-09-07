package com.yangdai.calc.main.toolbox.functions.chinese;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yangdai.calc.R;
import com.yangdai.calc.main.toolbox.functions.BaseFunctionActivity;
import com.yangdai.calc.utils.TouchAnimation;
import com.yangdai.calc.utils.Utils;

/**
 * @author 30415
 */
public class ChineseNumberConversionActivity extends BaseFunctionActivity implements View.OnClickListener {
    private static final String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] CN_UPPER_UNITS = {"", "拾", "佰", "仟"};
    private static final String[] CN_UPPER_BIG_UNITS = {"", "万", "亿", "兆"};
    private static final String[] CN_FRAC_UNITS = {"角", "分"};
    private static final String CN_MONETARY_UNIT = "元";
    private static final String CN_FULL = "整";
    private static final String CN_NEGATIVE = "负";
    private static final int MONEY_PRECISION = 2;
    private TextView tvInput;
    private TextView tvResults;

    private String showText = "0";
    private String resultsText = "零元整";
    private static final int[] BUTTON_IDS = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5,
            R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_pt, R.id.btn_clr, R.id.btn_negate, R.id.iv_del};


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvInput = findViewById(R.id.input_textview);
        tvResults = findViewById(R.id.output_textview);

        for (int buttonId : BUTTON_IDS) {
            findViewById(buttonId).setHapticFeedbackEnabled(defaultSp.getBoolean("vib", false));
            findViewById(buttonId).setOnClickListener(this);
            TouchAnimation touchAnimation = new TouchAnimation(findViewById(buttonId));
            findViewById(buttonId).setOnTouchListener(touchAnimation);
        }
    }

    @Override
    protected void setRootView() {
        setContentView(R.layout.activity_chinese_number_conversion);
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
        tvInput.setText(Utils.formatNumber(showText));
        tvResults.setText(resultsText);
    }

    private void delete() {
        if (!showText.isEmpty()) {
            showText = showText.substring(0, showText.length() - 1);
            if (showText.isEmpty()) {
                showText = "0";
            }
            operation();
            refreshText();
        }
    }

    private void operation() {
        BigDecimal numberOfMoney = new BigDecimal(showText);
        StringBuilder sb = new StringBuilder();
        String sign = "";

        // Handle negative number
        if (numberOfMoney.compareTo(BigDecimal.ZERO) < 0) {
            sign = CN_NEGATIVE;
            numberOfMoney = numberOfMoney.abs();
        }
        sb.append(sign);

        // Split into integer and fractional parts and convert to String
        String numberString = numberOfMoney.toString();
        String integerPartString;
        String fractionalPartString;
        if (!numberString.contains(".")) {
            numberString += "." + "0".repeat(MONEY_PRECISION);
        }
        String[] parts = numberString.split("\\.");
        integerPartString = parts[0];
        fractionalPartString = parts[1];

        // Convert integer part
        StringBuilder resultIntegerPart = new StringBuilder();
        int zeroCount = 0;
        final int integerLength = integerPartString.length();

        for (int i = 0; i < integerLength; i++) {
            String digit = integerPartString.substring(i, i + 1);
            int p = integerLength - i - 1;
            int q = p / 4;
            int r = p % 4;

            if (digit.equals("0")) {
                zeroCount++;
            } else {
                if (zeroCount > 0) {
                    resultIntegerPart.append(CN_UPPER_NUMBER[0]);
                }
                zeroCount = 0;
                resultIntegerPart.append(CN_UPPER_NUMBER[Integer.parseInt(digit)]).append(CN_UPPER_UNITS[r]);
            }

            if (r == 0 && zeroCount < 4) {
                resultIntegerPart.append(CN_UPPER_BIG_UNITS[q]);
            }
        }
        resultIntegerPart.append(CN_MONETARY_UNIT);

        // Convert fractional part
        StringBuilder resultFractionalPart = new StringBuilder();
        final int fractionalLength = fractionalPartString.length();
        zeroCount = 0;
        for (int i = 0; i < Math.min(fractionalLength, MONEY_PRECISION); i++) {
            String digit = fractionalPartString.substring(i, i + 1);
            if (digit.equals("0")) {
                zeroCount++;
            } else {
                if (zeroCount > 0) {
                    resultFractionalPart.append(CN_UPPER_NUMBER[0]);
                }
                zeroCount = 0;
                resultFractionalPart.append(CN_UPPER_NUMBER[Integer.parseInt(digit)]).append(CN_FRAC_UNITS[i]);
            }
        }

        sb.append(resultIntegerPart);
        if (resultFractionalPart.length() > 0) {
            sb.append(resultFractionalPart);
        } else {
            sb.append(CN_FULL);
        }
        resultsText = sb.toString();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        if ("vib".equals(s)) {
            for (int buttonId : BUTTON_IDS) {
                findViewById(buttonId).setHapticFeedbackEnabled(defaultSp.getBoolean("vib", false));
            }
        }
    }
}
