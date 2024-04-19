package com.yangdai.calc.main.toolbox.functions.converter;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.icu.math.BigDecimal;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.tabs.TabLayout;
import com.yangdai.calc.R;
import com.yangdai.calc.main.toolbox.functions.BaseFunctionActivity;
import com.yangdai.calc.utils.TouchAnimation;
import com.yangdai.calc.utils.Utils;

import java.util.Objects;

/**
 * @author 30415
 */
public class UnitActivity extends BaseFunctionActivity implements View.OnClickListener, TextWatcher {
    /**
     * 1：长度换算；2：面积换算；
     * 3：体积转换 4: 质量 5: 温度 6: 容量 7: 压力 8: 热量 9: 速度 10: 时间 11: 角度 12: 功率
     */
    private int flag = 1;
    private TextView tvInput, tvOutput;
    private String inputStr = "", outputStr = "";
    private String input = "";
    private Button btInput, btOutput;
    private static final String[] ITEMS_DISTANCE = {"km", "m", "dm", "cm", "mm", "ft", "in", "yd", "mi", "NM"};
    private static final String[] ITEMS_AREA = {"km²", "m²", "dm²", "cm²", "a", "ha", "顷", "亩", "坪", "ft²", "in²"};
    private static final String[] ITEMS_VOLUME = {"L", "m³", "dm³", "cm³", "mL"};
    private static final String[] ITEMS_MASS = {"mg", "g", "kg", "oz", "lb"};
    private static final String[] ITEMS_TEMPE = {"℃", "℉"};
    private static final String[] ITEMS_STORAGE = {"bit", "B", "KB", "KiB", "MB", "MiB", "GB", "GiB", "TB", "TiB"};
    private static final String[] ITEMS_PRESSURE = {"Pa", "bar", "psi", "atm", "mmHg"};
    private static final String[] ITEMS_HEAT = {"J", "cal", "kcal"};
    private static final String[] ITEMS_SPEED = {"m/s", "km/h", "km/s", "mile/h", "knots", "Mach", "c"};
    private static final String[] ITEMS_TIME = {"ms", "s", "min", "h", "d", "wk"};
    private static final String[] ITEMS_ANGLE = {"°", "′", "″", "rad"};
    private static final String[] ITEMS_POWER = {"kW", "W", "J/s", "hp", "ps", "kcal/s", "N•m/s", "kg•m/s", "Btu/s", "ft•lb/s"};
    TabLayout mTabLayout;
    private static final int[] BUTTON_IDS = {R.id.seven, R.id.eight,
            R.id.nine, R.id.four, R.id.five, R.id.six, R.id.three, R.id.clean,
            R.id.two, R.id.one, R.id.zero, R.id.switchUnit, R.id.delete, R.id.dot};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        tvInput.addTextChangedListener(this);
        btInput.addTextChangedListener(this);
        btOutput.addTextChangedListener(this);
    }

    @Override
    protected void setRootView() {
        setContentView(R.layout.activity_unit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tvInput.removeTextChangedListener(this);
        btInput.removeTextChangedListener(this);
        btOutput.removeTextChangedListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        btInput = findViewById(R.id.input);
        btOutput = findViewById(R.id.output);
        tvInput = findViewById(R.id.change_1);
        tvOutput = findViewById(R.id.change_result);
        findViewById(R.id.parent).setBackgroundColor(SurfaceColors.SURFACE_0.getColor(this));
        mTabLayout = findViewById(R.id.tab_view);

        final String[] tabs = {getString(R.string.length), getString(R.string.area), getString(R.string.volume), getString(R.string.mass), getString(R.string.tempe),
                getString(R.string.storage), getString(R.string.pressure), getString(R.string.heat), getString(R.string.speed), getString(R.string.time), getString(R.string.angle), getString(R.string.powerUnit)};

        final int[] icons = {R.drawable.length_icon, R.drawable.area_icon, R.drawable.volume_icon, R.drawable.mass_icon, R.drawable.tempe_icon,
                R.drawable.data_icon, R.drawable.pressure_icon, R.drawable.heat_icon, R.drawable.speed_icon, R.drawable.time_icon, R.drawable.angle_icon, R.drawable.power_icon};

        for (int i = 0; i < tabs.length; i++) {
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(tabs[i]);
            tab.setIcon(icons[i]);
            mTabLayout.addTab(tab);
        }

        mTabLayout.setSmoothScrollingEnabled(true);
        mTabLayout.selectTab(null);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                flag = tab.getPosition() + 1;
                SharedPreferences.Editor editor = defaultSp.edit();
                editor.putInt("selectedTab", tab.getPosition());
                editor.apply();

                btInput.setText("");
                btOutput.setText("");
                tvInput.setText("");
                input = "";
                tvOutput.setText("");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(
                () -> {
                    int tabNum = defaultSp.getInt("selectedTab", 0);
                    if (tabNum < tabs.length) {
                        Objects.requireNonNull(mTabLayout.getTabAt(tabNum)).select();
                    } else {
                        Objects.requireNonNull(mTabLayout.getTabAt(0)).select();
                    }
                },
                100);

        for (int buttonId : BUTTON_IDS) {
            findViewById(buttonId).setHapticFeedbackEnabled(defaultSp.getBoolean("vib", false));
            findViewById(buttonId).setOnClickListener(this);
            TouchAnimation touchAnimation = new TouchAnimation(findViewById(buttonId));
            findViewById(buttonId).setOnTouchListener(touchAnimation);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.delete) {
            if (!input.isEmpty()) {
                input = input.substring(0, input.length() - 1);
                tvInput.setText(Utils.formatNumber(input));
            }
        } else if (v.getId() == R.id.clean) {
            tvInput.setText("");
            tvOutput.setText("");
            input = "";
        } else if (v.getId() == R.id.switchUnit) {
            // 交换字符串的值
            String temp = inputStr;
            inputStr = outputStr;
            outputStr = temp;
            btInput.setText(inputStr);
            btOutput.setText(outputStr);
        } else {
            // 限制输入长度
            if (input.length() < 9) {
                String append = ((MaterialButton) v).getText().toString();
                if (".".equals(append)) {
                    if (input.isEmpty() || input.contains(".")) {
                        return;
                    }
                }
                input = input + append;
                tvInput.setText(Utils.formatNumber(input));
            }
        }
    }

    private void showOptions(View view, String[] items, boolean isInput) {
        ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAnchorView(view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, items);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setOnItemClickListener((parent, view1, position, id) -> {
            if (isInput) {
                inputStr = items[position];
                btInput.setText(inputStr);
            } else {
                outputStr = items[position];
                btOutput.setText(outputStr);
            }
            listPopupWindow.dismiss();
        });
        listPopupWindow.show();
    }

    public void showInputOptions(View view) {
        switchOption(view, true);
    }

    public void showOutputOptions(View view) {
        switchOption(view, false);
    }

    private void switchOption(View view, boolean isInput) {
        switch (flag) {
            case 1 -> showOptions(view, ITEMS_DISTANCE, isInput);
            case 2 -> showOptions(view, ITEMS_AREA, isInput);
            case 3 -> showOptions(view, ITEMS_VOLUME, isInput);
            case 4 -> showOptions(view, ITEMS_MASS, isInput);
            case 5 -> showOptions(view, ITEMS_TEMPE, isInput);
            case 6 -> showOptions(view, ITEMS_STORAGE, isInput);
            case 7 -> showOptions(view, ITEMS_PRESSURE, isInput);
            case 8 -> showOptions(view, ITEMS_HEAT, isInput);
            case 9 -> showOptions(view, ITEMS_SPEED, isInput);
            case 10 -> showOptions(view, ITEMS_TIME, isInput);
            case 11 -> showOptions(view, ITEMS_ANGLE, isInput);
            case 12 -> showOptions(view, ITEMS_POWER, isInput);
            default -> {
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void change() {
        String x = input;
        int scale = defaultSp.getInt("scale", 10);
        BigDecimal num;
        try {
            num = new BigDecimal(x);
        } catch (Exception e) {
            tvOutput.setText("");
            return;
        }

        UnitValue unitValue;
        switch (flag) {
            case 1 -> unitValue = convertUnit("a", inputStr, outputStr, num, scale);
            case 2 -> unitValue = convertUnit("b", inputStr, outputStr, num, scale);
            case 3 -> unitValue = convertUnit("c", inputStr, outputStr, num, scale);
            case 4 -> unitValue = convertUnit("e", inputStr, outputStr, num, scale);
            case 5 -> unitValue = convertTemperature(inputStr, outputStr, num, scale);
            case 6 -> unitValue = convertUnit("f", inputStr, outputStr, num, scale);
            case 7 -> unitValue = convertUnit("g", inputStr, outputStr, num, scale);
            case 8 -> unitValue = convertUnit("h", inputStr, outputStr, num, scale);
            case 9 -> unitValue = convertUnit("i", inputStr, outputStr, num, scale);
            case 10 -> unitValue = convertUnit("j", inputStr, outputStr, num, scale);
            case 11 -> unitValue = convertUnit("k", inputStr, outputStr, num, scale);
            case 12 -> unitValue = convertUnit("p", inputStr, outputStr, num, scale);
            default -> unitValue = null;
        }

        if (unitValue != null) {
            String res = unitValue.getValue().toBigDecimal().toPlainString();
            tvOutput.setText(Utils.formatNumber(res));
        } else {
            tvOutput.setText("");
        }
    }

    private UnitValue convertUnit(String unitType, String inputStr, String outputStr, BigDecimal num, int scale) {
        try {
            return UnitConverter.convert(unitType, inputStr, outputStr, num, scale);
        } catch (Exception e) {
            return null;
        }
    }

    private UnitValue convertTemperature(String inputStr, String outputStr, BigDecimal num, int scale) {
        BigDecimal temp;
        switch (inputStr) {
            case "℃" -> {
                temp = num.multiply(BigDecimal.valueOf(1.8));
                temp = temp.add(BigDecimal.valueOf(32));
                temp.setScale(scale, BigDecimal.ROUND_HALF_UP);
            }
            case "℉" -> {
                temp = num.subtract(BigDecimal.valueOf(32));
                temp = temp.divide(BigDecimal.valueOf(1.8), scale, BigDecimal.ROUND_HALF_UP);
            }
            default -> temp = null;
        }
        return new UnitValue(temp, outputStr);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (!input.isEmpty()
                && !btInput.getText().toString().isEmpty() && !btOutput.getText().toString().isEmpty()) {
            change();
        }
        if (input.isEmpty()) {
            tvOutput.setText("");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        if ("vib".equals(s)) {
            for (int buttonId : BUTTON_IDS) {
                findViewById(buttonId).setHapticFeedbackEnabled(defaultSp.getBoolean("vib", false));
            }
        } else if ("scale".equals(s)) {
            if (!input.isEmpty()
                    && !btInput.getText().toString().isEmpty() && !btOutput.getText().toString().isEmpty()) {
                change();
            }
            if (input.isEmpty()) {
                tvOutput.setText("");
            }
        }
    }
}
