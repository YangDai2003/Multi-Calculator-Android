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

    private final MutableLiveData<String> inputText = new MutableLiveData<>();
    private final MutableLiveData<String> outputText = new MutableLiveData<>();
    private int left = 0, right = 0;

    public LiveData<String> getInputTextState() {
        return inputText;
    }

    public LiveData<String> getOutputTextState() {
        return outputText;
    }

    @SuppressLint("SetTextI18n")
    public void handleFactorial(String inputStr, boolean canSpeak, TTS tts, String fac, String doubleFac) {
        if (!inputStr.isEmpty()) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            if (isNumber(lastChar + "") && lastChar != 'e' && lastChar != 'g' && lastChar != 'π') {
                for (int i = inputStr.length() - 1; i >= 0; i--) {
                    if (inputStr.charAt(i) == '.') {
                        return;
                    }
                    if (isSymbol(inputStr.charAt(i) + "")) {
                        inputText.setValue(inputStr + "!");
                        if (canSpeak) {
                            tts.ttsSpeak(fac);
                        }
                        return;
                    }
                }
                if (canSpeak) {
                    tts.ttsSpeak(fac);
                }
                inputText.setValue(inputStr + "!");
            } else if (lastChar == '!') {
                char secondLastChar = inputStr.charAt(inputStr.length() - 2);
                if (secondLastChar != '!') {
                    inputText.setValue(inputStr + "!");
                    if (canSpeak) {
                        tts.ttsSpeak(doubleFac);
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void handleEqualButton(String inputStr, Calculator formulaUtil, SharedPreferences settings, SharedPreferences history, boolean fromUser, String bigNum, String error) {
        // 处理常量情况
        if ("e".equals(inputStr) || "π".equals(inputStr)) {
            inputStr = inputStr.replace("e", String.valueOf(Math.E))
                    .replace("π", String.valueOf(Math.PI));
            outputText.setValue(inputStr);
            return;
        }

        // 忽略特殊情况
        if (inputStr.isEmpty() || isNumeric(inputStr)) {
            outputText.setValue("");
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
                    outputText.setValue(bigNum);
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
                outputText.setValue(bigNum);
                return;
            }
            bigDecimal = bigDecimal.setScale(settings.getInt("scale", 10), BigDecimal.ROUND_HALF_UP);
            String res = bigDecimal.toBigDecimal().toPlainString();
            res = removeZeros(res);

            if (fromUser) {
                String historys = history.getString("newHistory", "");
                List<String> savedStringList = new ArrayList<>(Arrays.asList(historys.split("//")));

                if (savedStringList.size() > 100) {
                    savedStringList.remove(0);
                }
                savedStringList.add(inputStr + "\n" + "=" + res);
                String listString = TextUtils.join("//", savedStringList);
                SharedPreferences.Editor editor = history.edit();
                editor.putString("newHistory", listString);
                editor.apply();
                inputText.setValue(res);
                outputText.setValue("");
            } else {
                outputText.setValue(formatNumber(res));
            }
        } catch (Exception e) {
            if (fromUser) {
                outputText.setValue(error);
            }
        }
    }

    public void handleCleanButton() {
        inputText.setValue("");
        outputText.setValue("");
        left = 0;
        right = 0;
    }

    public void handleDeleteButton(String inputStr) {
        if (inputStr.length() > 0) {
            if (inputStr.endsWith("asin(") || inputStr.endsWith("acos(")
                    || inputStr.endsWith("atan(") || inputStr.endsWith("acot(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 5);
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
            inputText.setValue(inputStr);
        }
        if (inputStr.isEmpty()) {
            outputText.setValue("");
        }
    }

    @SuppressLint("SetTextI18n")
    public void handleBracketsButton(String inputStr) {
        if (inputStr.length() > 0) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            if (left > right && isNumber(String.valueOf(lastChar))
                    || left > right && lastChar == '%' || left > right && lastChar == ')') {
                inputText.setValue(inputStr + ")");
                right++;
                return;
            } else if (lastChar == ')' || isNumber(String.valueOf(lastChar))) {
                inputText.setValue(inputStr + "×(");
            } else {
                inputText.setValue(inputStr + "(");
            }
        } else {
            inputText.setValue(inputStr + "(");
        }
        left++;
    }

    @SuppressLint("SetTextI18n")
    public void handleInverseButton(String inputStr) {
        // 取反
        if (inputStr.length() > 0) {
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
                                    inputText.setValue(inputStr + n);
                                    left--;
                                    return;
                                }
                            }  // + × ÷ (  ^ 特殊情况 )
                            inputStr = inputStr.substring(0, i + 1);
                            String prefix = (curr == ')') ? "×(-" : "(-";
                            inputText.setValue(inputStr + prefix + n);
                            left++;
                            return;
                        }
                    }
                }
                //只有数字
                inputText.setValue("(-" + n);
                left++;
                return;
            } else if (lastChar == '-') {
                // 最后是 (-， 直接去掉
                if (inputStr.length() > 1 && (inputStr.charAt(inputStr.length() - 2) == '(')) {
                    inputText.setValue(inputStr.substring(0, inputStr.length() - 2));
                    left--;
                    return;
                }
            }
            String prefix = (lastChar == ')' || lastChar == '!') ? "×(-" : "(-";
            inputText.setValue(inputStr + prefix);
        } else {
            inputText.setValue("(-");
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
            inputText.setValue(append);

        } else {
            //长度大于0时
            if (inputStr.length() > 0) {
                char lastInput = inputStr.charAt(inputStr.length() - 1);
                // )、e、π 后输入数字默认加上 ×
                if (isNumber(append)) {
                    if (")".equals(String.valueOf(lastInput))
                            || "e".equals(String.valueOf(lastInput)) || "π".equals(String.valueOf(lastInput))) {
                        inputText.setValue(inputStr + "×" + append);
                        return;
                    }
                }
                // 最后一位是两数运算符号时，再次输入符号则替换最后一位
                if (isSymbol(String.valueOf(lastInput)) && isSymbol(append)) {
                    inputText.setValue(inputStr.substring(0, inputStr.length() - 1) + append);
                    return;
                }
                // 最后一位是数字时，输入e、π默认加上 ×
                if (isNumber(String.valueOf(lastInput)) && ("e".equals(append) || "π".equals(append))) {
                    inputText.setValue(inputStr + "×" + append);
                    return;
                }
            }

            //三角函数运算符和对数运算符后自动加上括号
            if ("sin".equals(append) || "cos".equals(append) || "tan".equals(append) || "cot".equals(append)
                    || "asin".equals(append) || "acos".equals(append) || "atan".equals(append) || "acot".equals(append)
                    || "log".equals(append) || "ln".equals(append) || "exp".equals(append)) {
                if (inputStr.length() > 0) {
                    char lastInput = inputStr.charAt(inputStr.length() - 1);
                    if (isNumber(String.valueOf(lastInput)) || ")".equals(String.valueOf(lastInput))) {
                        inputText.setValue(inputStr + "×" + append + "(");
                        left++;
                        return;
                    }
                }
                inputText.setValue(inputStr + append + "(");
                left++;
                return;
            }
            inputText.setValue(inputStr + append);
        }
    }
}
