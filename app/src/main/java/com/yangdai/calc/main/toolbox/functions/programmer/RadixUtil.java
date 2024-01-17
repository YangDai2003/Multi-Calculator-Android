package com.yangdai.calc.main.toolbox.functions.programmer;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.BigInteger;


public class RadixUtil {
    private static BigInteger mIntegerOfRadix;
    private static final String DIGITS = "0123456789ABCDEF";

    public static String integerConvertTo10(BigInteger data, int fromRadix) {
        StringBuilder result = new StringBuilder();
        mIntegerOfRadix = new BigInteger(String.valueOf(fromRadix));
        while (!data.toString().equals("0")) {
            result.insert(0, DIGITS.charAt(data.remainder(mIntegerOfRadix).intValue()));
            data = data.divide(mIntegerOfRadix);
        }
        if (TextUtils.isEmpty(result.toString())) {
            result = new StringBuilder("0");
        }
        return result.toString();
    }


    public static String integerConverter(String data, int toRadix, int fromRadix) {
        if (fromRadix == toRadix) {
            return data;
        }
        char[] chars = data.toCharArray();
        int len = chars.length;
        if (toRadix != 10) {
            data = integerConverter(data, 10, fromRadix);
            return integerConvertTo10(new BigInteger(data), toRadix);
        } else {
            BigInteger mBigData = new BigInteger("0");
            for (int i = len - 1; i >= 0; i--) {
                mIntegerOfRadix = BigDecimal.valueOf(DIGITS.indexOf(chars[i]) * Math.pow(fromRadix, len - i - 1)).toBigInteger();
                mBigData = mBigData.add(mIntegerOfRadix);
            }
            return mBigData.toString();
        }
    }

    public static String decimalsConverter(String data, int toRadix, int formRadix, int digit) {

        if (toRadix == formRadix) {
            return data;
        }
        //if specified radix(toRadix) is not base 10, covert decimals in base 10
        if (formRadix != 10) {
            data = decimalsConvertTo10(data, formRadix);
            if (toRadix == 10) {
                return data;
            }
        }
        char[] chars = DIGITS.toCharArray();
        int integer;
        BigDecimal bigDecimal = new BigDecimal("0." + data);
        StringBuilder result = new StringBuilder();
        //covert decimals(base 10) in specified radix(toRadix)
        while (bigDecimal.compareTo(new BigDecimal("0")) != 0) {
            bigDecimal = bigDecimal.multiply(new BigDecimal(toRadix));
            integer = bigDecimal.intValue();
            result.append(chars[integer]);
            bigDecimal = new BigDecimal("0." + bigDecimal.toPlainString().split("\\.")[1]);
            //if length greater then 9(equal to 10) break
            if (result.length() > digit) {
                break;
            }
        }

        if (result.length() == 0) {
            result = new StringBuilder("0");
        }
        return result.toString();

    }

    public static String decimalsConvertTo10(String data, int fromRadix) {

        char[] chars = data.toCharArray();
        BigDecimal sum = new BigDecimal("0");
        for (int i = 0; i < data.length(); i++) {
            int index = DIGITS.indexOf(chars[i]);
            int power = -i - 1;
            sum = sum.add(new BigDecimal(index * Math.pow(fromRadix, power)));
        }
        String[] ss = sum.toString().split("\\.");
        try {
            return ss[1];
        } catch (Exception e) {
            return "0";
        }
    }

    public static boolean checkData(String data, int radix) {
        data = data.replaceAll(" ", "");
        String digits = ".0123456789ABCDEF";
        //can only contains one point
        if (data.split("\\.").length > 2) {
            return false;
        }
        for (int i = 0; i < data.length(); i++) {
            char digit = data.charAt(i);
            int index = digits.indexOf(digit);
            if (index == -1 || index > radix) {
                return false;
            }
        }
        return true;
    }

    /*
     * 	第二种方法模拟人工算术
     *  用来将正数或者负数十进制转换为其他进制数
     * */
    public static String secDecToBin(int num) {
        //定义字符串，用来存放计算出来的二进制数据
        StringBuilder sb = new StringBuilder();
        while (num != 0) {
            //向字符串中添加计算出来的二进制数
            sb.append(num & 1);
            //对num进行无符号位运算，类似于除2运算，具体的区别还需要读者自己查找
            num = num >>> 1;
        }
        //将字符串反序返回
        return sb.reverse().toString();
    }

    public static String secDecToOctal(int num) {
        StringBuilder sb = new StringBuilder();
        while (num != 0) {
            sb.append(num & 7);
            num = num >>> 3;
        }
        return sb.reverse().toString();
    }

    public static String secDecToHex(int num) {
        StringBuilder sb = new StringBuilder();
        while (num != 0) {
            sb.append(num & 15);
            num = num >>> 4;
        }
        return sb.reverse().toString();
    }
}
