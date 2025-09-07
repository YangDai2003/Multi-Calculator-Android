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

    /**
     * 核心转换方法，将数字字符串转换为中文大写金额。
     */
    private void operation() {
        BigDecimal numberOfMoney;
        try {
            // 处理无效输入，避免程序崩溃
            if (showText == null || showText.isEmpty() || "-".equals(showText) || ".".equals(showText)) {
                numberOfMoney = BigDecimal.ZERO;
            } else {
                numberOfMoney = new BigDecimal(showText);
            }
        } catch (NumberFormatException e) {
            numberOfMoney = BigDecimal.ZERO;
        }

        // 当输入数字为 0 时，直接返回“零元整”
        if (numberOfMoney.compareTo(BigDecimal.ZERO) == 0) {
            resultsText = "零元整";
            return;
        }

        StringBuilder result = new StringBuilder();

        // 处理负数
        if (numberOfMoney.compareTo(BigDecimal.ZERO) < 0) {
            result.append(CN_NEGATIVE);
            numberOfMoney = numberOfMoney.abs();
        }

        // 拆分整数和小数部分
        // 使用 setScale 保证有两位小数，方便处理
        String numberString = numberOfMoney.setScale(MONEY_PRECISION, BigDecimal.ROUND_HALF_UP).toString();
        String[] parts = numberString.split("\\.");
        String integerPartString = parts[0];
        String fractionalPartString = parts[1];
        boolean hasIntegerPart = Long.parseLong(integerPartString) > 0;

        // 转换整数部分
        String integerChinese = convertInteger(integerPartString);
        if (hasIntegerPart) {
            result.append(integerChinese).append(CN_MONETARY_UNIT);
        }

        // 转换小数部分
        String fractionalChinese = convertFractional(fractionalPartString, hasIntegerPart);
        result.append(fractionalChinese);

        // 处理结尾的“整”
        // 如果没有小数部分，并且有整数部分，则添加“整”
        if (fractionalChinese.isEmpty() && hasIntegerPart) {
            result.append(CN_FULL);
        }

        resultsText = result.toString();
    }

    /**
     * 转换整数部分。
     *
     * @param integerPartString 整数部分的字符串
     * @return 中文大写整数
     */
    private String convertInteger(String integerPartString) {
        if (Long.parseLong(integerPartString) == 0) {
            return "";
        }

        StringBuilder resultIntegerPart = new StringBuilder();
        int zeroCount = 0;
        final int integerLength = integerPartString.length();

        for (int i = 0; i < integerLength; i++) {
            String digit = integerPartString.substring(i, i + 1);
            // p: 从右到左的位置 (0-based)
            int p = integerLength - i - 1;
            // q: 大单位的组索引 (0 for [个-仟], 1 for [万], 2 for [亿], etc.)
            int q = p / 4;
            // r: 在4位数组中的位置 (0 for 个, 1 for 拾, 2 for 佰, 3 for 仟)
            int r = p % 4;

            if ("0".equals(digit)) {
                zeroCount++;
            } else {
                if (zeroCount > 0) {
                    resultIntegerPart.append(CN_UPPER_NUMBER[0]);
                }
                zeroCount = 0;
                resultIntegerPart.append(CN_UPPER_NUMBER[Integer.parseInt(digit)]).append(CN_UPPER_UNITS[r]);
            }

            if (r == 0 && zeroCount < 4) {
                // 在4位数的末尾（万，亿，兆），添加大单位
                // 清理末尾的 "零" 再添加大单位，例如 "壹仟零万" 变为 "壹仟万"
                if (resultIntegerPart.toString().endsWith(CN_UPPER_NUMBER[0])) {
                    resultIntegerPart.deleteCharAt(resultIntegerPart.length() - 1);
                }
                resultIntegerPart.append(CN_UPPER_BIG_UNITS[q]);
            }
        }

        // 再次清理末尾可能出现的 "零"
        if (resultIntegerPart.toString().endsWith(CN_UPPER_NUMBER[0])) {
            resultIntegerPart.deleteCharAt(resultIntegerPart.length() - 1);
        }

        return resultIntegerPart.toString();
    }

    /**
     * 改进后的小数部分转换逻辑。
     *
     * @param fractionalPartString 小数部分的字符串 (e.g., "05", "50")
     * @param hasIntegerPart       整数部分是否大于0
     * @return 中文大写小数
     */
    private String convertFractional(String fractionalPartString, boolean hasIntegerPart) {
        int jiao = Integer.parseInt(fractionalPartString.substring(0, 1));
        int fen = Integer.parseInt(fractionalPartString.substring(1, 2));

        // 如果角和分都为0，则返回空
        if (jiao == 0 && fen == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        // 特殊情况：当整数部分存在且角为0，但分不为0时，需要加 "零"
        // 例如：1.05 -> "壹元零伍分"
        if (hasIntegerPart && jiao == 0) {
            result.append(CN_UPPER_NUMBER[0]);
        }

        if (jiao > 0) {
            result.append(CN_UPPER_NUMBER[jiao]).append(CN_FRAC_UNITS[0]);
        }

        if (fen > 0) {
            result.append(CN_UPPER_NUMBER[fen]).append(CN_FRAC_UNITS[1]);
        }

        return result.toString();
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
