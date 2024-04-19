package com.yangdai.calc.main.calculator;

import static com.yangdai.calc.main.calculator.CalculatorUtils.calculateAllFactorial;
import static com.yangdai.calc.main.calculator.CalculatorUtils.optimizePercentage;
import static com.yangdai.calc.utils.Utils.formatNumber;
import static com.yangdai.calc.utils.Utils.isNumber;
import static com.yangdai.calc.utils.Utils.isNumeric;
import static com.yangdai.calc.utils.Utils.isSymbol;
import static com.yangdai.calc.utils.Utils.removeZeros;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.icu.math.BigDecimal;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.button.MaterialButton;
import com.yangdai.calc.utils.TTS;
import com.yangdai.calc.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 30415
 */
public class CalculatorViewModel extends ViewModel {

    private final MutableLiveData<String> expression = new MutableLiveData<>();
    private final MutableLiveData<String> result = new MutableLiveData<>();
    private int left = 0, right = 0;

    public LiveData<String> getInputTextState() {
        return expression;
    }

    public LiveData<String> getOutputTextState() {
        return result;
    }

    @SuppressLint("SetTextI18n")
    public void handleFactorial(String inputStr, boolean canSpeak, TTS tts, String fac, String doubleFac) {
        if (!inputStr.isEmpty()) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            if (isNumber(String.valueOf(lastChar)) && lastChar != 'e' && lastChar != 'g' && lastChar != 'π') {
                for (int i = inputStr.length() - 1; i >= 0; i--) {
                    if (inputStr.charAt(i) == '.') {
                        return;
                    }
                    if (isSymbol(String.valueOf(inputStr.charAt(i)))) {
                        expression.setValue(inputStr + "!");
                        if (canSpeak) {
                            tts.ttsSpeak(fac);
                        }
                        return;
                    }
                }
                if (canSpeak) {
                    tts.ttsSpeak(fac);
                }
                expression.setValue(inputStr + "!");
            } else if (lastChar == '!') {
                char secondLastChar = inputStr.charAt(inputStr.length() - 2);
                if (secondLastChar != '!') {
                    expression.setValue(inputStr + "!");
                    if (canSpeak) {
                        tts.ttsSpeak(doubleFac);
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void handleEqualButton(String inputStr, Calculator formulaUtil, SharedPreferences defaultSp, SharedPreferences history, boolean fromUser, String bigNum, String error) {
        // 处理常量情况
        if ("e".equals(inputStr) || "π".equals(inputStr)) {
            inputStr = inputStr.replace("e", String.valueOf(Math.E))
                    .replace("π", String.valueOf(Math.PI));
            result.setValue(inputStr);
            return;
        }

        // 忽略特殊情况
        if (inputStr.isEmpty() || isNumeric(inputStr)) {
            result.setValue("");
            return;
        }

        // 补全
        if (isSymbol(String.valueOf(inputStr.charAt(inputStr.length() - 1)))) {
            inputStr += "0";
        } else if (isSymbol(String.valueOf(inputStr.charAt(0))) || inputStr.charAt(0) == '%') {
            inputStr = "0" + inputStr;
        }

        // 处理左右括号数量不一致的情况
        if (left != right) {
            int addCount = Math.abs(left - right);
            StringBuilder inputStrBuilder = new StringBuilder(inputStr);
            for (int j = 0; j < addCount; j++) {
                inputStrBuilder.append(")");
            }
            inputStr = inputStrBuilder.toString();
        }

        inputStr = optimizePercentage(inputStr);

        try {
            if (inputStr.contains("!")) {
                // 优化！阶乘和！！双阶乘
                inputStr = calculateAllFactorial(inputStr);
                if ("数值过大".equals(inputStr)) {
                    result.setValue(bigNum);
                    return;
                }
            }

            // 使用正则表达式进行匹配
            String patternStr = "\\be\\b";
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(inputStr);
            // 替换匹配到的单独 "e"
            inputStr = matcher.replaceAll(String.valueOf(Math.E));
            //替换常数
            inputStr = inputStr.replace("π", String.valueOf(Math.PI))
                    .replace("%", "÷100");
            BigDecimal bigDecimal = formulaUtil.calc(inputStr);
            if (null == bigDecimal) {
                result.setValue(bigNum);
                return;
            }
            bigDecimal = bigDecimal.setScale(defaultSp.getInt("scale", 10), BigDecimal.ROUND_HALF_UP);
            String res = bigDecimal.toBigDecimal().toPlainString();
            res = removeZeros(res);

            if (fromUser) {
                String historys = history.getString("newHistory", "");
                List<String> savedStringList = new ArrayList<>(Arrays.asList(historys.split("//")));

                if (savedStringList.size() >= defaultSp.getInt("historyNum", 100)) {
                    int removeCount = savedStringList.size() - defaultSp.getInt("historyNum", 100) + 1;
                    savedStringList.removeAll(savedStringList.subList(0, removeCount));
                }
                savedStringList.add(inputStr + "\n" + "=" + res);
                String listString = TextUtils.join("//", savedStringList);
                SharedPreferences.Editor editor = history.edit();
                editor.putString("newHistory", listString);
                editor.apply();
                expression.setValue(res);
                result.setValue("");
                left = 0;
                right = 0;
            } else {
                result.setValue(formatNumber(res));
            }
        } catch (Exception e) {
            if (fromUser) {
                result.setValue(error);
            }
        }
    }

    public void handleCleanButton() {
        expression.setValue("");
        result.setValue("");
        left = 0;
        right = 0;
    }

    public void handleDeleteButton(String inputStr) {
        if (!inputStr.isEmpty()) {
            if (inputStr.endsWith("sin⁻¹(") || inputStr.endsWith("cos⁻¹(")
                    || inputStr.endsWith("tan⁻¹(") || inputStr.endsWith("cot⁻¹(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 6);
                left--;
            } else if (inputStr.endsWith("sin(") || inputStr.endsWith("cos(") || inputStr.endsWith("exp(")
                    || inputStr.endsWith("tan(") || inputStr.endsWith("cot(") || inputStr.endsWith("log(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 4);
                left--;
            } else if (inputStr.endsWith("ln(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 3);
                left--;
            } else {
                char lastChar = inputStr.charAt(inputStr.length() - 1);
                if (lastChar == ')') {
                    right--;
                }
                if (lastChar == '(') {
                    left--;
                }
                inputStr = inputStr.substring(0, inputStr.length() - 1);
            }
            expression.setValue(inputStr);
        }
        if (inputStr.isEmpty()) {
            result.setValue("");
        }
    }

    @SuppressLint("SetTextI18n")
    public void handleBracketsButton(String inputStr) {
        if (!inputStr.isEmpty()) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            if (left > right && (isNumber(String.valueOf(lastChar))
                    || lastChar == '!' || lastChar == '%' || lastChar == ')')) {
                expression.setValue(inputStr + ")");
                right++;
                return;
            } else if (lastChar == ')' || isNumber(String.valueOf(lastChar))) {
                expression.setValue(inputStr + "×(");
            } else {
                expression.setValue(inputStr + "(");
            }
        } else {
            expression.setValue(inputStr + "(");
        }
        left++;
    }

    @SuppressLint("SetTextI18n")
    public void handleInverseButton(String inputStr) {
        // 取反
        if (!inputStr.isEmpty()) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            //最后一位是数字
            if (isNumber(String.valueOf(lastChar))) {
                StringBuilder n = new StringBuilder();
                n.insert(0, lastChar);
                // 如果长度大于一， 从后向前遍历直到数字前一位
                if (inputStr.length() > 1) {
                    for (int i = inputStr.length() - 2; i >= 0; i--) {
                        char curr = inputStr.charAt(i);
                        if (isNumber(String.valueOf(curr)) || curr == '.') {
                            n.insert(0, curr);
                        } else {
                            // 遇到负号，如果负号前是 (-，则去掉 (-
                            if (curr == '-') {
                                if (i >= 1 && "(-".equals(inputStr.substring(i - 1, i + 1))) {
                                    inputStr = inputStr.substring(0, i - 1);
                                    expression.setValue(inputStr + n);
                                    left--;
                                    return;
                                }
                            }  // + × ÷ (  ^ 特殊情况 )
                            inputStr = inputStr.substring(0, i + 1);
                            String prefix = (curr == ')') ? "×(-" : "(-";
                            expression.setValue(inputStr + prefix + n);
                            left++;
                            return;
                        }
                    }
                }
                //只有数字
                expression.setValue("(-" + n);
                left++;
                return;
            } else if (lastChar == '-') {
                // 最后是 (-， 直接去掉
                if (inputStr.length() > 1 && (inputStr.charAt(inputStr.length() - 2) == '(')) {
                    expression.setValue(inputStr.substring(0, inputStr.length() - 2));
                    left--;
                    return;
                }
            }
            String prefix = (lastChar == ')' || lastChar == '!') ? "×(-" : "(-";
            expression.setValue(inputStr + prefix);
        } else {
            expression.setValue("(-");
        }
        left++;
    }

    @SuppressLint("SetTextI18n")
    public void handleOtherButtons(View v, String inputStr, boolean canSpeak, TTS tts, boolean fromUser) {
        String append = ((MaterialButton) v).getText().toString();

        if (canSpeak) {
            tts.ttsSpeak(append);
        }

        if (fromUser && Utils.isNumber(append)) {
            // 点击等号后，再次输入数字时清空结果
            expression.setValue(append);
        } else {
            //长度大于0时
            if (!inputStr.isEmpty()) {
                char lastInput = inputStr.charAt(inputStr.length() - 1);
                // )、e、π、！、% 后输入数字默认加上 ×
                if (isNumber(append)) {
                    if (")".equals(String.valueOf(lastInput)) || "!".equals(String.valueOf(lastInput)) || "%".equals(String.valueOf(lastInput))
                            || "e".equals(String.valueOf(lastInput)) || "π".equals(String.valueOf(lastInput))) {
                        expression.setValue(inputStr + "×" + append);
                        return;
                    }
                }
                // 最后一位是两数运算符号时，再次输入符号则替换最后一位
                if (isSymbol(String.valueOf(lastInput)) && isSymbol(append)) {
                    expression.setValue(inputStr.substring(0, inputStr.length() - 1) + append);
                    return;
                }
                // 最后一位是数字时，输入e、π默认加上 ×
                if (isNumber(String.valueOf(lastInput)) && ("e".equals(append) || "π".equals(append))) {
                    expression.setValue(inputStr + "×" + append);
                    return;
                }
            }

            //三角函数运算符和对数运算符后自动加上括号
            if ("sin".equals(append) || "cos".equals(append) || "tan".equals(append) || "cot".equals(append)
                    || "sin⁻¹".equals(append) || "cos⁻¹".equals(append) || "tan⁻¹".equals(append) || "cot⁻¹".equals(append)
                    || "log".equals(append) || "ln".equals(append) || "exp".equals(append)) {
                if (!inputStr.isEmpty()) {
                    char lastInput = inputStr.charAt(inputStr.length() - 1);
                    if (isNumber(String.valueOf(lastInput)) || ")".equals(String.valueOf(lastInput))
                            || "!".equals(String.valueOf(lastInput)) || "%".equals(String.valueOf(lastInput))) {
                        expression.setValue(inputStr + "×" + append + "(");
                        left++;
                        return;
                    }
                }
                expression.setValue(inputStr + append + "(");
                left++;
                return;
            }
            expression.setValue(inputStr + append);
        }
    }
}
