package com.yangdai.calc.main.toolbox.functions.compass;

import android.hardware.GeomagneticField;

/**
 * @author 30415
 */
public class CompassHelper {
    /**
     * 0 ≤ ALPHA ≤ 1
     * 较小的ALPHA值会导致更平滑的传感器数据，但更新速度较慢
     */
    public static final float ALPHA = 0.12f;

    public static float calculateHeading(float[] accelerometerReading, float[] magnetometerReading) {
        float ax = accelerometerReading[0];
        float ay = accelerometerReading[1];
        float az = accelerometerReading[2];

        float ex = magnetometerReading[0];
        float ey = magnetometerReading[1];
        float ez = magnetometerReading[2];

        // 磁场向量和重力向量的叉积
        float hx = ey * az - ez * ay;
        float hy = ez * ax - ex * az;
        float hz = ex * ay - ey * ax;

        // 规范化结果向量的值
        final float invH = 1.0f / (float) Math.sqrt(hx * hx + hy * hy + hz * hz);
        hx *= invH;
        hy *= invH;
        hz *= invH;

        // 规范化重力向量的值
        final float invA = 1.0f / (float) Math.sqrt(ax * ax + ay * ay + az * az);
        ax *= invA;
        //ay *= invA;
        az *= invA;

        // 重力向量和新向量H的叉积
        //final float mx = ay * hz - az * hy;
        final float my = az * hx - ax * hz;
        //final float mz = ax * hy - ay * hx;

        // 使用反正切函数获取弧度表示的方向
        return (float) Math.atan2(hy, my);
    }

    /**
     * 获取真北与磁北的磁偏角
     */
    public static float calculateMagneticDeclination(double latitude, double longitude, double altitude) {
        GeomagneticField geomagneticField =
                new GeomagneticField((float) latitude,
                        (float) longitude,
                        (float) altitude,
                        System.currentTimeMillis());
        return geomagneticField.getDeclination();
    }

    public static float convertRadioDeg(float rad) {
        return (float) (rad / Math.PI) * 180;
    }

    /**
     * 将角度从[-180,180]范围映射到[0,360]范围
     */
    public static float map180to360(float angle) {
        return (angle + 360) % 360;
    }

    public static void lowPassFilter(float[] input, float[] output) {
        if (output == null) {
            return;
        }
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
    }

    public static String convertToDeg(double coordinate) {
        // 判断是否为负数
        boolean isNegative = coordinate < 0;
        // 取绝对值进行计算
        double absoluteCoordinate = Math.abs(coordinate);
        // 度部分
        int degrees = (int) absoluteCoordinate;
        // 分部分
        double decimalMinutes = (absoluteCoordinate - degrees) * 60;
        int minutes = (int) decimalMinutes;
        // 秒部分
        double decimalSeconds = (decimalMinutes - minutes) * 60;
        int seconds = (int) decimalSeconds;

        // 根据是否为负数拼接结果
        String result;
        if (isNegative) {
            result = "-";
        } else {
            result = "";
        }

        result += degrees + "° " + minutes + "' " + seconds + "\" ";
        return result;
    }

}
