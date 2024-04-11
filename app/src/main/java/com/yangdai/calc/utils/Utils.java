package com.yangdai.calc.utils;

import static android.content.Context.VIBRATOR_MANAGER_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.icu.math.BigDecimal;
import android.icu.number.NumberFormatter;
import android.icu.number.Precision;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author 30415
 */
public class Utils {
    private static VibratorManager vibratorManager = null;
    private static Vibrator vibrator = null;

    /**
     * 震动方法
     */
    public static void vibrate(Context context) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (null == vibratorManager || null == vibrator) {
                    vibratorManager = (VibratorManager) context.getSystemService(VIBRATOR_MANAGER_SERVICE);
                    vibrator = vibratorManager.getDefaultVibrator();
                }
                vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                if (null == vibrator) {
                    vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
                }
                vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } catch (Exception ignored) {

        }
    }

    /**
     * 判断是否是数字
     */
    public static boolean isNumber(String num) {
        return num.equals("0") || num.equals("1") || num.equals("2") || num.equals("3") || num.equals("4")
                || num.equals("5") || num.equals("6") || num.equals("7") || num.equals("8") || num.equals("9")
                || num.equals("e") || num.equals("π");
    }

    /**
     * 判断是否是运算符
     */
    public static boolean isSymbol(String s) {
        return s.equals("+") || s.equals("-") || s.equals("×") || s.equals(".") || s.equals("^")
                || s.equals("÷");
    }

    /**
     * 格式化数字
     */
    public static String formatNumber(String number) {
        try {
            BigDecimal bigDecimal = new BigDecimal(number);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return NumberFormatter
                        .with()
                        .locale(Locale.getDefault())
                        .precision(Precision.maxFraction(10))
                        .format(bigDecimal)
                        .toString();
            } else {
                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMaximumFractionDigits(10);
                return numberFormat.format(bigDecimal);
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 格式化金融数字
     */
    public static String formatNumberFinance(String number) {
        try {
            BigDecimal bigDecimal = new BigDecimal(number);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return NumberFormatter.withLocale(Locale.getDefault())
                        .precision(Precision.maxFraction(2))
                        .format(bigDecimal)
                        .toString();
            } else {
                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMaximumFractionDigits(2);
                return numberFormat.format(bigDecimal);
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 移除多余的 0
     */
    public static String removeZeros(String num) {
        if (num == null || num.isEmpty()) {
            return "";
        }

        if (!num.contains(".")) {
            return num;
        }

        // 分割整数部分和小数部分
        String[] parts = num.split("\\.");
        String integerPart = parts[0];
        String decimalPart = parts[1];

        // 移除小数部分的尾随0
        decimalPart = decimalPart.replaceAll("0+?$", "");

        // 如果小数部分为空，则返回整数部分
        if (decimalPart.isEmpty()) {
            return integerPart;
        }

        // 合并整数部分和小数部分
        return integerPart + "." + decimalPart;
    }

    public static void closeKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /*
     * 判断是否是单一数字
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * 计算最大公约数
     */
    public static BigInteger gcd(BigInteger a, BigInteger b) {
        return a.gcd(b);
    }

    /**
     * 计算最小公倍数
     */
    public static BigInteger lcm(BigInteger a, BigInteger b) {
        try {
            return (a.multiply(b)).divide(gcd(a, b));
        } catch (Exception e) {
            return BigInteger.ZERO;
        }
    }

    /**
     * 计算多个数最大公约数
     */
    public static BigInteger gcdMultiple(List<BigInteger> numbers) {
        BigInteger result = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            result = gcd(result, numbers.get(i));
        }
        return result;
    }

    /**
     * 计算多个数的最小公倍数
     */
    public static BigInteger lcmMultiple(List<BigInteger> numbers) {
        BigInteger result = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            result = lcm(result, numbers.get(i));
        }
        return result;
    }

    /**
     * 分数转小数方法，适用于有限小数和循环小数
     */
    public static String fractionToDecimal(int numerator, int denominator) {
        long numeratorLong = numerator;
        long denominatorLong = denominator;
        // 能整除直接返回
        if (numeratorLong % denominatorLong == 0) {
            return String.valueOf(numeratorLong / denominatorLong);
        }

        StringBuilder sb = new StringBuilder();
        // 用异或符号判断负号
        if (numeratorLong < 0 ^ denominatorLong < 0) {
            sb.append('-');
        }

        numeratorLong = Math.abs(numeratorLong);
        denominatorLong = Math.abs(denominatorLong);

        // 整数部分
        long integerPart = numeratorLong / denominatorLong;
        sb.append(integerPart);
        sb.append('.');

        // 小数部分
        StringBuilder fractionPart = getFractionPart(numeratorLong, denominatorLong);

        sb.append(fractionPart);
        return sb.toString();
    }

    @NonNull
    private static StringBuilder getFractionPart(long numeratorLong, long denominatorLong) {
        StringBuilder fractionPart = new StringBuilder();
        Map<Long, Integer> remainderIndexMap = new HashMap<>();
        long remainder = numeratorLong % denominatorLong;

        int index = 0;
        while (remainder != 0 && !remainderIndexMap.containsKey(remainder)) {
            remainderIndexMap.put(remainder, index);
            remainder *= 10;
            fractionPart.append(remainder / denominatorLong);
            remainder %= denominatorLong;
            index++;
        }
        if (remainder != 0) {
            // 有循环节
            Integer insertIndex = remainderIndexMap.get(remainder);
            if (insertIndex != null) {
                fractionPart.insert(insertIndex.intValue(), '(');
                fractionPart.append(')');
            }
        }
        return fractionPart;
    }

    /**
     * 小数转分数方法，适用于有限小数
     */
    public static String decimalToFractionForNonRepeating(double decimal) {
        String stringNumber = String.valueOf(decimal);
        int numberDigitsDecimals = stringNumber.length() - 1 - stringNumber.indexOf('.');
        int denominator = 1;
        for (int i = 0; i < numberDigitsDecimals; i++) {
            decimal *= 10;
            denominator *= 10;
        }
        int numerator = (int) Math.round(decimal);
        int greatestCommonFactor = gcd(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator)).intValue();
        int numerator1 = numerator / greatestCommonFactor;
        int denominator1 = denominator / greatestCommonFactor;
        return numerator1 + " / " + denominator1;
    }

    /**
     * 小数转分数方法，适用于循环小数
     */
    public static String decimalToFractionForRepeating(String decimal) {
        int wholePart = Integer.parseInt(decimal.substring(0, decimal.indexOf(".")));
        int nonRepeatingPart = Integer.parseInt(decimal.substring(decimal.indexOf(".") + 1, decimal.indexOf("(")));
        int repeatingPart = Integer.parseInt(decimal.substring(decimal.indexOf("(") + 1, decimal.indexOf(")")));

        int nonRepeatingLength = decimal.indexOf("(") - decimal.indexOf(".") - 1;
        int repeatingLength = decimal.indexOf(")") - decimal.indexOf("(") - 1;

        int denominator = (int) Math.pow(10, nonRepeatingLength + repeatingLength) - (int) Math.pow(10, nonRepeatingLength);

        int numerator = (nonRepeatingPart * (int) Math.pow(10, repeatingLength) + repeatingPart) - nonRepeatingPart;

        int wholeNumerator = wholePart * denominator;

        numerator += wholeNumerator;

        int gcd = gcd(BigInteger.valueOf(Math.abs(numerator)), BigInteger.valueOf(denominator)).intValue();
        numerator /= gcd;
        denominator /= gcd;

        return numerator + " / " + denominator;
    }

    /**
     * 通用小数转分数方法
     */
    public static String decimalToFraction(String decimal) {
        try {
            if (decimal.contains("(")) {
                if (!decimal.contains(")")) {
                    decimal += ")";
                }
                return decimalToFractionForRepeating(decimal);
            } else {
                return decimalToFractionForNonRepeating(Double.parseDouble(decimal));
            }
        } catch (Exception e) {
            return "A / B";
        }
    }

}
