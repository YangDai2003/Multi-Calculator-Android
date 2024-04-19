package com.yangdai.calc.main.toolbox.functions.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 30415
 */
public class UnitTable {
    private UnitTable() {
    }

    private static final Map<String, Map<String, Double>> UNIT_TABLE = new HashMap<>();
    /**
     * 长度单位，以m为基准单位，指定单位换算表
     */
    private static final Map<String, Double> LENGTH_UNIT_MAP = new HashMap<>();
    /**
     * 面积单位，以m2为基准单位，指定单位换算表
     */
    private static final Map<String, Double> AREA_UNIT_MAP = new HashMap<>();
    /**
     * 体积单位，容积单位，以 m3（立方米）为基准单位，指定单位换算表
     */
    private static final Map<String, Double> VOLUME_UNIT_MAP = new HashMap<>();
    private static final Map<String, Double> MASS_UNIT_MAP = new HashMap<>();
    private static final Map<String, Double> STORAGE_UNIT_MAP = new HashMap<>();
    private static final Map<String, Double> HEAT_UNIT_MAP = new HashMap<>();
    private static final Map<String, Double> PRESSURE_UNIT_MAP = new HashMap<>();
    private static final Map<String, Double> VELOCITY_UNIT_MAP = new HashMap<>();
    private static final Map<String, Double> TIME_UNIT_MAP = new HashMap<>();
    private static final Map<String, Double> ANGLE_UNIT_MAP = new HashMap<>();
    private static final Map<String, Double> POWER_UNIT_MAP = new HashMap<>();

    static {
        LENGTH_UNIT_MAP.put("mm", 0.001);
        LENGTH_UNIT_MAP.put("cm", 0.01);
        LENGTH_UNIT_MAP.put("dm", 0.1);
        LENGTH_UNIT_MAP.put("m", 1.0);
        LENGTH_UNIT_MAP.put("km", 1000.0);
        LENGTH_UNIT_MAP.put("ft", 0.3048); // Feet
        LENGTH_UNIT_MAP.put("in", 0.0254); // Inches
        LENGTH_UNIT_MAP.put("yd", 0.9144); // Yards
        LENGTH_UNIT_MAP.put("mi", 1609.34); // Miles
        LENGTH_UNIT_MAP.put("NM", 1852.0); // Nautical miles

        AREA_UNIT_MAP.put("km²", 1000000.0);
        AREA_UNIT_MAP.put("m²", 1.0);
        AREA_UNIT_MAP.put("dm²", 0.01);
        AREA_UNIT_MAP.put("cm²", 0.0001);
        AREA_UNIT_MAP.put("a", 100.0); // Square decameter
        AREA_UNIT_MAP.put("ha", 10000.0); // Hectare
        AREA_UNIT_MAP.put("顷", 66666.6667); // Acre
        AREA_UNIT_MAP.put("亩", 666.6667); // Mu
        AREA_UNIT_MAP.put("坪", 3.30578512397); // Ping
        AREA_UNIT_MAP.put("ft²", 0.092903); // Square foot
        AREA_UNIT_MAP.put("in²", 0.00064516); // Square inch

        VOLUME_UNIT_MAP.put("cm³", 0.000001);
        VOLUME_UNIT_MAP.put("dm³", 0.001);
        VOLUME_UNIT_MAP.put("m³", 1.0);
        VOLUME_UNIT_MAP.put("L", 0.001);
        VOLUME_UNIT_MAP.put("mL", 0.000001);

        MASS_UNIT_MAP.put("mg", 0.000001);
        MASS_UNIT_MAP.put("g", 0.001);
        MASS_UNIT_MAP.put("kg", 1.0);
        MASS_UNIT_MAP.put("t", 1000.0);
        MASS_UNIT_MAP.put("lb", 0.453592); // Pound
        MASS_UNIT_MAP.put("oz", 0.0283495); // Ounce

        STORAGE_UNIT_MAP.put("bit", 1.0);
        STORAGE_UNIT_MAP.put("B", 8.0); // Byte
        STORAGE_UNIT_MAP.put("KB", 8000.0); // Kilobyte
        STORAGE_UNIT_MAP.put("KiB", 8192.0); // Kibibyte
        STORAGE_UNIT_MAP.put("MB", 8000000.0); // Megabyte
        STORAGE_UNIT_MAP.put("MiB", 8388608.0); // Mebibyte
        STORAGE_UNIT_MAP.put("GB", 8000000000.0); // Gigabyte
        STORAGE_UNIT_MAP.put("GiB", 8589934592.0); // Gibibyte
        STORAGE_UNIT_MAP.put("TB", 8000000000000.0); // Terabyte
        STORAGE_UNIT_MAP.put("TiB", 8796093022208.0); // Tebibyte

        HEAT_UNIT_MAP.put("J", 1.0);
        HEAT_UNIT_MAP.put("cal", 4.184);
        HEAT_UNIT_MAP.put("kcal", 4184.0);

        PRESSURE_UNIT_MAP.put("Pa", 1.0);
        PRESSURE_UNIT_MAP.put("bar", 100000.0);
        PRESSURE_UNIT_MAP.put("psi", 6894.76);
        PRESSURE_UNIT_MAP.put("atm", 101325.0);
        PRESSURE_UNIT_MAP.put("mmHg", 133.3223);

        VELOCITY_UNIT_MAP.put("m/s", 1.0);
        VELOCITY_UNIT_MAP.put("km/h", 0.277778);
        VELOCITY_UNIT_MAP.put("mile/h", 0.44704);
        VELOCITY_UNIT_MAP.put("knots", 0.514444);
        VELOCITY_UNIT_MAP.put("Mach", 340.29);
        VELOCITY_UNIT_MAP.put("c", 299792458.0);
        VELOCITY_UNIT_MAP.put("km/s", 1000.0);

        TIME_UNIT_MAP.put("ms", 0.001); // Millisecond
        TIME_UNIT_MAP.put("s", 1.0);    // Second
        TIME_UNIT_MAP.put("min", 60.0); // Minute
        TIME_UNIT_MAP.put("h", 3600.0); // Hour
        TIME_UNIT_MAP.put("d", 86400.0); // Day
        TIME_UNIT_MAP.put("wk", 604800.0); // Week

        ANGLE_UNIT_MAP.put("°", 1.0); // 度
        ANGLE_UNIT_MAP.put("′", 1.0 / 60); // 分
        ANGLE_UNIT_MAP.put("″", 1.0 / 3600); // 秒
        ANGLE_UNIT_MAP.put("rad", 180 / Math.PI); // 弧度

        POWER_UNIT_MAP.put("kW", 1000.0);
        POWER_UNIT_MAP.put("W", 1.0);
        POWER_UNIT_MAP.put("J/s", 1.0);
        POWER_UNIT_MAP.put("hp", 745.69987); // Horsepower
        POWER_UNIT_MAP.put("ps", 735.49875); // Metric horsepower
        POWER_UNIT_MAP.put("kcal/s", 4184.0); // Kilocalorie per second
        POWER_UNIT_MAP.put("N•m/s", 1.0); // Newton-meter per second
        POWER_UNIT_MAP.put("kg•m/s", 9.80665); // Kilogram-meter per second
        POWER_UNIT_MAP.put("Btu/s", 1055.056); // British thermal unit per second
        POWER_UNIT_MAP.put("ft•lb/s", 1.355817948); // Foot-pound force per second

        UNIT_TABLE.put("a", LENGTH_UNIT_MAP);
        UNIT_TABLE.put("b", AREA_UNIT_MAP);
        UNIT_TABLE.put("c", VOLUME_UNIT_MAP);
        UNIT_TABLE.put("e", MASS_UNIT_MAP);
        UNIT_TABLE.put("f", STORAGE_UNIT_MAP);
        UNIT_TABLE.put("g", PRESSURE_UNIT_MAP);
        UNIT_TABLE.put("h", HEAT_UNIT_MAP);
        UNIT_TABLE.put("i", VELOCITY_UNIT_MAP);
        UNIT_TABLE.put("j", TIME_UNIT_MAP);
        UNIT_TABLE.put("k", ANGLE_UNIT_MAP);
        UNIT_TABLE.put("p", POWER_UNIT_MAP);
    }

    public static Map<String, Double> getUnitTable(String unitName) {
        if (UNIT_TABLE.containsKey(unitName)) {
            return UNIT_TABLE.get(unitName);
        } else {
            return new HashMap<>(0);
        }
    }
}
