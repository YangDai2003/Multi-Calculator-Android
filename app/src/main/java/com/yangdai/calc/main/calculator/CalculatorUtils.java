package com.yangdai.calc.main.calculator;

import static com.yangdai.calc.utils.Utils.isNumber;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.math.BigInteger;

public class CalculatorUtils {
    /**
     * 普通阶乘
     */
    public static BigInteger calculateFactorial(BigInteger num) {
        if (num.compareTo(BigInteger.ZERO) <= 0) {
            return BigInteger.ONE;
        }

        BigInteger factorial = BigInteger.ONE;
        for (BigInteger i = BigInteger.ONE; i.compareTo(num) <= 0; i = i.add(BigInteger.ONE)) {
            factorial = factorial.multiply(i);
        }
        return factorial;
    }

    /**
     * 双阶乘
     */
    public static BigInteger calculateDoubleFactorial(BigInteger num) {
        if (num.compareTo(BigInteger.ZERO) <= 0) {
            return BigInteger.ONE;
        }

        BigInteger result = BigInteger.ONE;
        int n = num.intValue();
        while (n > 0) {
            result = result.multiply(BigInteger.valueOf(n));
            n -= 2;
        }
        return result;
    }

    /**
     * 通用阶乘计算方法
     */
    public static String calculateAllFactorial(String str) {
        StringBuilder sb = new StringBuilder(str);
        int index = sb.indexOf("!");
        while (index >= 0) {
            int start = index - 1;
            while (start >= 0 && Character.isDigit(sb.charAt(start))) {
                start--;
            }
            start++;
            String num = sb.substring(start, index);
            BigInteger result;

            if (num.length() >= 4) {
                return "数值过大";
            }

            if (index + 1 < sb.length() && sb.charAt(index + 1) == '!') {
                // 连续出现两次的阶乘
                result = calculateDoubleFactorial(new BigInteger(num));
                sb.replace(start, index + 2, result.toString());
            } else {
                // 连续出现一次的阶乘
                result = calculateFactorial(new BigInteger(num));
                sb.replace(start, index + 1, result.toString());
            }
            index = sb.indexOf("!", start + result.toString().length());
        }
        return sb.toString();
    }

    public static String optimizePercentage(String inputStr) {
        if (inputStr.contains("%")) {
            // 优化百分号(%)
            StringBuilder optimizedInputStrBuilder = new StringBuilder(inputStr);
            for (int k = optimizedInputStrBuilder.length() - 1; k >= 0; k--) {
                char currentChar = optimizedInputStrBuilder.charAt(k);
                if (currentChar == '%') {
                    int startIndex = k - 1;
                    if (optimizedInputStrBuilder.charAt(startIndex) == ')') {
                        while (startIndex >= 0) {
                            char prevChar = optimizedInputStrBuilder.charAt(startIndex);
                            if (prevChar == '(') {
                                optimizedInputStrBuilder.insert(startIndex + 1, '(');
                                break;
                            }
                            startIndex--;
                        }
                    } else {
                        boolean atBeginning = true;
                        while (startIndex >= 0) {
                            char prevChar = optimizedInputStrBuilder.charAt(startIndex);
                            if (!(isNumber(String.valueOf(prevChar)) || prevChar == '.')) {
                                optimizedInputStrBuilder.insert(startIndex + 1, '(');
                                atBeginning = false;
                                break;
                            }
                            startIndex--;
                        }
                        if (atBeginning) {
                            optimizedInputStrBuilder.insert(0, '(');
                        }
                    }
                    optimizedInputStrBuilder.insert(k + 2, ')');
                }
            }
            inputStr = optimizedInputStrBuilder.toString();
        }
        return inputStr;
    }

    public static void highlightSpecialSymbols(TextView textView) {
        String text = textView.getText().toString();
        SpannableString spannableString = new SpannableString(text);

        // 遍历文本，找到特定符号并设置颜色
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // 设置特殊符号的颜色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GRAY);
            if (c == '+' || c == '-' || c == '×' || c == '÷' || c == '^') {
                spannableString.setSpan(colorSpan, i, i + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        // 将带有特殊颜色的 SpannableString 设置给 TextView
        textView.setText(spannableString);
    }
}
