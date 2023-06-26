package com.yangdai.calc.utils;

import static android.content.Context.VIBRATOR_MANAGER_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.icu.math.BigDecimal;
import android.icu.text.DecimalFormat;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author 30415
 */
public class Utils {
    private static VibratorManager vibratorManager = null;
    private static Vibrator vibrator = null;

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

    public static boolean isNumber(String num) {
        return num.contains("0") || num.contains("1") || num.contains("2") || num.contains("3") || num.contains("4")
                || num.contains("5") || num.contains("6") || num.contains("7") || num.contains("8") || num.contains("9")
                || num.contains("e") || num.contains("π");
    }

    public static boolean isSymbol(String s) {
        return s.contains("+") || s.contains("-") || s.contains("×") || s.contains(".") || s.contains("^")
                || s.contains("÷");
    }

    public static String formatNumber(String number) {
        try {
            BigDecimal bigDecimal = new BigDecimal(number);
            DecimalFormat format = new DecimalFormat("###,###.##########");
            return format.format(bigDecimal);
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatNumberFinance(String number) {
        try {
            BigDecimal bigDecimal = new BigDecimal(number);
            DecimalFormat format = new DecimalFormat("###,###.##");
            return format.format(bigDecimal);
        } catch (Exception e) {
            return "";
        }
    }

    public static String removeZeros(String num) {
        if (num.indexOf(".") > 0) {
            // 去掉多余的0
            num = num.replaceAll("0+?$", "");
            // 如果最后一位是. 则去掉
            num = num.replaceAll("[.]$", "");
        }
        return num;
    }

    // 阶乘计算
    public static BigDecimal calculateFactorial(BigDecimal num) {
        if (num.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ONE;
        }

        BigDecimal result = BigDecimal.ONE;
        int n = num.intValue();
        while (n > 0) {
            result = result.multiply(BigDecimal.valueOf(n));
            n--;
        }
        return result;
    }

    // 双阶乘计算
    public static BigDecimal calculateDoubleFactorial(BigDecimal num) {
        if (num.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ONE;
        }

        BigDecimal result = BigDecimal.ONE;
        int n = num.intValue();
        while (n > 0) {
            result = result.multiply(BigDecimal.valueOf(n));
            n -= 2;
        }
        return result;
    }

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
            BigDecimal result;

            if (index + 1 < sb.length() && sb.charAt(index + 1) == '!') {
                // 连续出现两次的阶乘
                result = calculateDoubleFactorial(new BigDecimal(num));
                sb.replace(start, index + 2, result.toString());
            } else {
                // 连续出现一次的阶乘
                result = calculateFactorial(new BigDecimal(num));
                sb.replace(start, index + 1, result.toString());
            }

            index = sb.indexOf("!", start + result.toString().length());
        }
        return sb.toString();
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
}
