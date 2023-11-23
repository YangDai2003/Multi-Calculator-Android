package com.yangdai.calc.main.toolbox.functions.converter;

import android.icu.math.BigDecimal;

import java.util.Map;

/**
 * @author 30415
 */
public class UnitConverter {
    /**
     * 单位转换
     */
    public static UnitValue convert(String physicalName, String from, String to, BigDecimal value, int scale) {
        Map<String, Double> unitTable = UnitTable.getUnitTable(physicalName);
        BigDecimal fromValue = new BigDecimal(String.valueOf(unitTable.get(from)));
        BigDecimal toValue = new BigDecimal(String.valueOf(unitTable.get(to)));

        UnitValue unitValue = new UnitValue();

        value = value.multiply(fromValue);
        value = value.divide(toValue, scale, BigDecimal.ROUND_HALF_UP);
        unitValue.setValue(value);
        unitValue.setUnit(to);
        return unitValue;
    }
}
