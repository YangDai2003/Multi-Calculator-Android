package com.yangdai.calc.main.toolbox.functions.converter;

import android.icu.math.BigDecimal;

import androidx.annotation.NonNull;

/**
 * 带单位的值。
 *
 * @author 30415
 */
public class UnitValue {
    private BigDecimal value;
    private String unit;

    public UnitValue() {
        super();
    }

    /**
     * @param value 数值
     * @param unit  单位
     */
    public UnitValue(BigDecimal value, String unit) {
        super();
        this.value = value;
        this.unit = unit;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /** @noinspection unused*/
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        long temp;
        temp = value.longValue();
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UnitValue other = (UnitValue) obj;
        if (unit == null) {
            if (other.unit != null) {
                return false;
            }
        } else if (!unit.equals(other.unit)) {
            return false;
        }
        return value.longValue() == (other.value).longValue();
    }

    @NonNull
    @Override
    public String toString() {
        return value + unit;
    }
}
