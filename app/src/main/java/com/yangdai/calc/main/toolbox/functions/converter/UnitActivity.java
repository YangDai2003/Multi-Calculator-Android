package com.yangdai.calc.main.toolbox.functions.converter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.icu.math.BigDecimal;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.tabs.TabLayout;
import com.yangdai.calc.R;
import com.yangdai.calc.utils.TouchAnimation;
import com.yangdai.calc.utils.Utils;

import java.util.Objects;

/**
 * @author 30415
 */
public class UnitActivity extends AppCompatActivity implements View.OnClickListener,
        TextWatcher, SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * 1：长度换算；2：面积换算；3,5: 进制转换
     * 4：体积转换 6: 质量 7: 温度 8: 容量 9: 压力 10: 热量 11: 速度 12: 时间 13: 角度 14: 功率
     */
    private int flag = 1;
    private TextView tvInput, tvOutput;
    private String inputStr = "", outputStr = "";
    private String input = "";
    private Button btInput, btOutput;
    private static final String[] ITEMS_DISTANCE = {"km", "m", "dm", "cm", "mm", "ft", "in", "yd", "mi", "NM"};
    private static final String[] ITEMS_AREA = {"km²", "m²", "dm²", "cm²", "a", "ha", "顷", "亩", "ft²", "in²"};
    private static final String[] ITEMS_NUMERATION = {"H", "O", "B"};
    private static final String[] ITEMS_VOLUME = {"L", "m³", "dm³", "cm³", "mL"};
    private static final String[] ITEMS_D = {"D"};
    private static final String[] ITEMS_MASS = {"mg", "g", "kg", "oz", "lb"};
    private static final String[] ITEMS_TEMPE = {"℃", "℉"};
    private static final String[] ITEMS_STORAGE = {"bit", "B", "KB", "KiB", "MB", "MiB", "GB", "GiB", "TB", "TiB"};
    private static final String[] ITEMS_PRESSURE = {"Pa", "bar", "psi", "atm", "mmHg"};
    private static final String[] ITEMS_HEAT = {"J", "cal", "kcal"};
    private static final String[] ITEMS_SPEED = {"m/s", "km/h", "km/s", "mile/h", "knots", "Mach", "c"};
    private static final String[] ITEMS_TIME = {"ms", "s", "min", "h", "d", "wk"};
    private static final String[] ITEMS_ANGLE = {"°", "′", "″", "rad"};
    private static final String[] ITEMS_POWER = {"kW", "W", "J/s", "hp", "ps", "kcal/s", "N•m/s", "kg•m/s", "Btu/s", "ft•lb/s"};

    SharedPreferences defaultSp;
    TabLayout mTabLayout;
    private static final int[] BUTTON_IDS = {R.id.seven, R.id.eight,
            R.id.nine, R.id.four, R.id.five, R.id.six, R.id.three, R.id.clean,
            R.id.two, R.id.one, R.id.zero, R.id.switchUnit, R.id.delete, R.id.dot};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        setContentView(R.layout.activity_unit);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        defaultSp = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSp.registerOnSharedPreferenceChangeListener(this);
        if (defaultSp.getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        init();

        tvInput.addTextChangedListener(this);
        btInput.addTextChangedListener(this);
        btOutput.addTextChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        defaultSp.unregisterOnSharedPreferenceChangeListener(this);
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

        final String[] tabs = {getString(R.string.length), getString(R.string.area),
                getString(R.string.numeration), getString(R.string.volume), getString(R.string.mass),
                getString(R.string.tempe), getString(R.string.storage), getString(R.string.pressure), getString(R.string.heat), getString(R.string.speed), getString(R.string.time), getString(R.string.angle), getString(R.string.powerUnit)};

        for (String s : tabs) {
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(s);
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

                TextView textView = new TextView(UnitActivity.this);
                textView.setText(tab.getText());
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tab.setCustomView(textView);

                btInput.setText("");
                btOutput.setText("");
                tvInput.setText("");
                input = "";
                tvOutput.setText("");

                if (tab.getPosition() == 2) {
                    findViewById(R.id.switchUnit).setEnabled(false);
                } else {
                    findViewById(R.id.switchUnit).setEnabled(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(
                () -> Objects.requireNonNull(mTabLayout.getTabAt(defaultSp.getInt("selectedTab", 0))).select(),
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
            if (input.length() > 0) {
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
                    if (input.length() == 0 || input.contains(".")) {
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
        switch (flag) {
            case 1 -> showOptions(view, ITEMS_DISTANCE, true);
            case 2 -> showOptions(view, ITEMS_AREA, true);
            case 3 -> showOptions(view, ITEMS_D, true);
            case 4 -> showOptions(view, ITEMS_VOLUME, true);
            case 5 -> showOptions(view, ITEMS_MASS, true);
            case 6 -> showOptions(view, ITEMS_TEMPE, true);
            case 7 -> showOptions(view, ITEMS_STORAGE, true);
            case 8 -> showOptions(view, ITEMS_PRESSURE, true);
            case 9 -> showOptions(view, ITEMS_HEAT, true);
            case 10 -> showOptions(view, ITEMS_SPEED, true);
            case 11 -> showOptions(view, ITEMS_TIME, true);
            case 12 -> showOptions(view, ITEMS_ANGLE, true);
            case 13 -> showOptions(view, ITEMS_POWER, true);
            default -> {
            }
        }
    }

    public void showOutputOptions(View view) {
        switch (flag) {
            case 1 -> showOptions(view, ITEMS_DISTANCE, false);
            case 2 -> showOptions(view, ITEMS_AREA, false);
            case 3 -> showOptions(view, ITEMS_NUMERATION, false);
            case 4 -> showOptions(view, ITEMS_VOLUME, false);
            case 5 -> showOptions(view, ITEMS_MASS, false);
            case 6 -> showOptions(view, ITEMS_TEMPE, false);
            case 7 -> showOptions(view, ITEMS_STORAGE, false);
            case 8 -> showOptions(view, ITEMS_PRESSURE, false);
            case 9 -> showOptions(view, ITEMS_HEAT, false);
            case 10 -> showOptions(view, ITEMS_SPEED, false);
            case 11 -> showOptions(view, ITEMS_TIME, false);
            case 12 -> showOptions(view, ITEMS_ANGLE, false);
            case 13 -> showOptions(view, ITEMS_POWER, false);
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
            case 3 -> {
                tvOutput.setText(convertBinaryUnit(x, outputStr));
                return;
            }
            case 4 -> unitValue = convertUnit("c", inputStr, outputStr, num, scale);
            case 5 -> unitValue = convertUnit("e", inputStr, outputStr, num, scale);
            case 6 -> unitValue = convertTemperature(inputStr, outputStr, num, scale);
            case 7 -> unitValue = convertUnit("f", inputStr, outputStr, num, scale);
            case 8 -> unitValue = convertUnit("g", inputStr, outputStr, num, scale);
            case 9 -> unitValue = convertUnit("h", inputStr, outputStr, num, scale);
            case 10 -> unitValue = convertUnit("i", inputStr, outputStr, num, scale);
            case 11 -> unitValue = convertUnit("j", inputStr, outputStr, num, scale);
            case 12 -> unitValue = convertUnit("k", inputStr, outputStr, num, scale);
            case 13 -> unitValue = convertUnit("p", inputStr, outputStr, num, scale);
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

    private String convertBinaryUnit(String x, String outputStr) {
        if (x.contains(".")) {
            long bits = Double.doubleToRawLongBits(Double.parseDouble(x));
            switch (outputStr) {
                case "B" -> x = Long.toBinaryString(bits);
                case "O" -> x = Long.toOctalString(bits);
                case "H" -> x = Long.toHexString(bits);
                default -> {
                }
            }
        } else {
            int n = Integer.parseInt(x);
            switch (outputStr) {
                case "B" -> x = Integer.toBinaryString(n);
                case "O" -> x = Integer.toOctalString(n);
                case "H" -> x = Integer.toHexString(n);
                default -> {
                }
            }
        }
        return x;
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
