package com.example.calc;

import android.content.Context;
import android.icu.math.BigDecimal;
import android.icu.text.DecimalFormat;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

/**
 * @author 30415
 */
public class Utils {
    static VibratorManager vibratorManager;
    static Vibrator vibrator;

    public static void vibrate(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    public static boolean isNumber(String num) {
        return num.contains("0") || num.contains("1") || num.contains("2") || num.contains("3") || num.contains("4")
                || num.contains("5") || num.contains("6") || num.contains("7") || num.contains("8") || num.contains("9")
                || num.contains("e") || num.contains("π") || num.contains("g");
    }

    public static boolean isSymbol(String s) {
        return s.contains("+") || s.contains("-") || s.contains("×") || s.contains(".") || s.contains("^")
                || s.contains("÷") || s.contains("%");
    }

    public static String formatNumber(String number) {
        BigDecimal bigDecimal = new BigDecimal(number);
        DecimalFormat format = new DecimalFormat("###,###.##########");
        return format.format(bigDecimal);
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
}
