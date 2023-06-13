package com.example.calc;

import static com.example.calc.Utils.formatNumber;
import static com.example.calc.Utils.vibrate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.icu.math.BigDecimal;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calc.converter.*;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

/**
 * @author 30415
 */
public class ChangeActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 1：长度换算；2：面积换算；3,5: 进制转换
     * 4：体积转换 6: 质量 7: 温度 8: 容量 9: 压力 10: 热量 11: 速度 12: 时间 13: 角度
     */
    private int flag = 1;
    private TextView input, output;
    private String inputStr = "", outputStr = "";
    private Button btu2, btu3;
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
    private static final String[] ITEMS_TIME = {"s", "min", "h", "d", "wk"};
    private static final String[] ITEMS_ANGLE = {"°", "′", "″", "rad"};

    SharedPreferences settings;
    TabLayout mTabLayout;
    GestureDetector gestureDetector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_change);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        init();
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                change();
            }
        });


    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        btu2 = findViewById(R.id.input);
        btu3 = findViewById(R.id.output);
        input = findViewById(R.id.change_1);
        output = findViewById(R.id.change_result);
        findViewById(R.id.parent).setBackgroundColor(SurfaceColors.SURFACE_0.getColor(this));
        mTabLayout = findViewById(R.id.tab_view);

        final String[] tabs = {getString(R.string.length), getString(R.string.area),
                getString(R.string.numeration), getString(R.string.volume), getString(R.string.mass),
                getString(R.string.tempe), getString(R.string.storage), getString(R.string.pressure), getString(R.string.heat), getString(R.string.speed), getString(R.string.time), getString(R.string.angle)};

        for (String s : tabs) {
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(s);
            mTabLayout.addTab(tab);
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                flag = tab.getPosition() + 1;
                TextView textView = new TextView(ChangeActivity.this);
                if (textView != null) {
                    textView.setText(tab.getText());
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
                tab.setCustomView(textView);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        int[] buttonIds = {R.id.seven, R.id.eight,
                R.id.nine, R.id.four, R.id.five, R.id.six, R.id.delete,
                R.id.three, R.id.two, R.id.one, R.id.dot, R.id.zero};
        for (int buttonId : buttonIds) {
            findViewById(buttonId).setOnClickListener(this);
        }
        findViewById(R.id.delete).setOnLongClickListener(v -> {
            input.setText("");
            return true;
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(e1.getX() - e2.getX()) > 200) {
                    if (e1.getX() < e2.getX()) {
                        // swipe right
                        int selectedTabPosition = mTabLayout.getSelectedTabPosition();
                        if (selectedTabPosition > 0) {
                            mTabLayout.getTabAt(selectedTabPosition - 1).select();
                        }
                    } else if (e1.getX() > e2.getX()) {
                        // swipe left
                        int selectedTabPosition = mTabLayout.getSelectedTabPosition();
                        if (selectedTabPosition < mTabLayout.getTabCount() - 1) {
                            mTabLayout.getTabAt(selectedTabPosition + 1).select();
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (settings.getBoolean("vib", false)) {
            vibrate(this);
        }

        //获取输入
        CharSequence editText = input.getText();

        if (v.getId() == R.id.delete) {
            if (editText.length() > 0) {
                editText = editText.subSequence(0, editText.length() - 1);
                input.setText(editText);
            }
        } else {
            String append = ((MaterialButton) v).getText().toString();
            if (".".equals(append)) {
                if (editText.length() == 0) {
                    Toast.makeText(this, getString(R.string.invalid), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            input.setText(editText + append);
        }
    }

    public void showInputOptions(View view) {
        ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAnchorView(view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        listPopupWindow.setAdapter(adapter);
        switch (flag) {
            case 1:
                adapter.addAll(ITEMS_DISTANCE);
                listPopupWindow.setOnItemClickListener((parent, view1, position, id) -> {
                    inputStr = ITEMS_DISTANCE[position];
                    btu2.setText(ITEMS_DISTANCE[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 2:
                adapter.addAll(ITEMS_AREA);
                listPopupWindow.setOnItemClickListener((parent, view12, position, id) -> {
                    inputStr = ITEMS_AREA[position];
                    btu2.setText(ITEMS_AREA[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 3:
                adapter.addAll(ITEMS_D);
                listPopupWindow.setOnItemClickListener((parent, view13, position, id) -> {
                    inputStr = ITEMS_D[position];
                    btu2.setText(ITEMS_D[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 4:
                adapter.addAll(ITEMS_VOLUME);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    inputStr = ITEMS_VOLUME[position];
                    btu2.setText(ITEMS_VOLUME[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 5:
                adapter.addAll(ITEMS_MASS);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    inputStr = ITEMS_MASS[position];
                    btu2.setText(ITEMS_MASS[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 6:
                adapter.addAll(ITEMS_TEMPE);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    inputStr = ITEMS_TEMPE[position];
                    btu2.setText(ITEMS_TEMPE[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 7:
                adapter.addAll(ITEMS_STORAGE);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    inputStr = ITEMS_STORAGE[position];
                    btu2.setText(ITEMS_STORAGE[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 8:
                adapter.addAll(ITEMS_PRESSURE);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    inputStr = ITEMS_PRESSURE[position];
                    btu2.setText(ITEMS_PRESSURE[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 9:
                adapter.addAll(ITEMS_HEAT);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    inputStr = ITEMS_HEAT[position];
                    btu2.setText(ITEMS_HEAT[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 10:
                adapter.addAll(ITEMS_SPEED);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    inputStr = ITEMS_SPEED[position];
                    btu2.setText(ITEMS_SPEED[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 11:
                adapter.addAll(ITEMS_TIME);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    inputStr = ITEMS_TIME[position];
                    btu2.setText(ITEMS_TIME[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 12:
                adapter.addAll(ITEMS_ANGLE);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    inputStr = ITEMS_ANGLE[position];
                    btu2.setText(ITEMS_ANGLE[position]);
                    listPopupWindow.dismiss();
                });
                break;
            default:
                return;
        }
        listPopupWindow.show();
    }

    public void showOutputOptions(View view) {
        ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAnchorView(view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        listPopupWindow.setAdapter(adapter);
        switch (flag) {
            case 1:
                adapter.addAll(ITEMS_DISTANCE);
                listPopupWindow.setOnItemClickListener((parent, view1, position, id) -> {
                    outputStr = ITEMS_DISTANCE[position];
                    btu3.setText(ITEMS_DISTANCE[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 2:
                adapter.addAll(ITEMS_AREA);
                listPopupWindow.setOnItemClickListener((parent, view12, position, id) -> {
                    outputStr = ITEMS_AREA[position];
                    btu3.setText(ITEMS_AREA[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 3:
                adapter.addAll(ITEMS_NUMERATION);
                listPopupWindow.setOnItemClickListener((parent, view13, position, id) -> {
                    outputStr = ITEMS_NUMERATION[position];
                    btu3.setText(ITEMS_NUMERATION[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 4:
                adapter.addAll(ITEMS_VOLUME);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    outputStr = ITEMS_VOLUME[position];
                    btu3.setText(ITEMS_VOLUME[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 5:
                adapter.addAll(ITEMS_MASS);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    outputStr = ITEMS_MASS[position];
                    btu3.setText(ITEMS_MASS[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 6:
                adapter.addAll(ITEMS_TEMPE);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    outputStr = ITEMS_TEMPE[position];
                    btu3.setText(ITEMS_TEMPE[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 7:
                adapter.addAll(ITEMS_STORAGE);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    outputStr = ITEMS_STORAGE[position];
                    btu3.setText(ITEMS_STORAGE[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 8:
                adapter.addAll(ITEMS_PRESSURE);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    outputStr = ITEMS_PRESSURE[position];
                    btu3.setText(ITEMS_PRESSURE[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 9:
                adapter.addAll(ITEMS_HEAT);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    outputStr = ITEMS_HEAT[position];
                    btu3.setText(ITEMS_HEAT[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 10:
                adapter.addAll(ITEMS_SPEED);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    outputStr = ITEMS_SPEED[position];
                    btu3.setText(ITEMS_SPEED[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 11:
                adapter.addAll(ITEMS_TIME);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    outputStr = ITEMS_TIME[position];
                    btu3.setText(ITEMS_TIME[position]);
                    listPopupWindow.dismiss();
                });
                break;
            case 12:
                adapter.addAll(ITEMS_ANGLE);
                listPopupWindow.setOnItemClickListener((parent, view14, position, id) -> {
                    outputStr = ITEMS_ANGLE[position];
                    btu3.setText(ITEMS_ANGLE[position]);
                    listPopupWindow.dismiss();
                });
                break;
            default:
                return;
        }
        listPopupWindow.show();
    }

    @SuppressLint("SetTextI18n")
    public void change() {
        String x = input.getText().toString();
        int scale = settings.getInt("scale", 10);
        BigDecimal num;
        try {
            num = new BigDecimal(x);
        } catch (Exception e) {
            output.setText("");
            return;
        }

        UnitValue unitValue;
        switch (flag) {
            case 1:
                unitValue = convertUnit("a", inputStr, outputStr, num, scale);
                break;
            case 2:
                unitValue = convertUnit("b", inputStr, outputStr, num, scale);
                break;
            case 3:
                output.setText(convertBinaryUnit(x, outputStr));
                return;
            case 4:
                unitValue = convertUnit("c", inputStr, outputStr, num, scale);
                break;
            case 5:
                unitValue = convertUnit("e", inputStr, outputStr, num, scale);
                break;
            case 6:
                unitValue = convertTemperature(inputStr, outputStr, num, scale);
                break;
            case 7:
                unitValue = convertUnit("f", inputStr, outputStr, num, scale);
                break;
            case 8:
                unitValue = convertUnit("g", inputStr, outputStr, num, scale);
                break;
            case 9:
                unitValue = convertUnit("h", inputStr, outputStr, num, scale);
                break;
            case 10:
                unitValue = convertUnit("i", inputStr, outputStr, num, scale);
                break;
            case 11:
                unitValue = convertUnit("j", inputStr, outputStr, num, scale);
                break;
            case 12:
                unitValue = convertUnit("k", inputStr, outputStr, num, scale);
                break;
            default:
                unitValue = null;
                break;
        }

        if (unitValue != null) {
            String res = unitValue.getValue().toBigDecimal().toPlainString();
            output.setText(formatNumber(res));
        } else {
            output.setText("");
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
                case "B":
                    x = Long.toBinaryString(bits);
                    break;
                case "O":
                    x = Long.toOctalString(bits);
                    break;
                case "H":
                    x = Long.toHexString(bits);
                    break;
                default:
                    break;
            }
        } else {
            int n = Integer.parseInt(x);
            switch (outputStr) {
                case "B":
                    x = Integer.toBinaryString(n);
                    break;
                case "O":
                    x = Integer.toOctalString(n);
                    break;
                case "H":
                    x = Integer.toHexString(n);
                    break;
                default:
                    break;
            }
        }
        return x;
    }

    private UnitValue convertTemperature(String inputStr, String outputStr, BigDecimal num, int scale) {
        BigDecimal temp;
        switch (inputStr) {
            case "℃":
                temp = num.multiply(BigDecimal.valueOf(1.8));
                temp = temp.add(BigDecimal.valueOf(32));
                temp.setScale(scale, BigDecimal.ROUND_HALF_UP);
                break;
            case "℉":
                temp = num.subtract(BigDecimal.valueOf(32));
                temp = temp.divide(BigDecimal.valueOf(1.8), scale, BigDecimal.ROUND_HALF_UP);
                break;
            default:
                temp = null;
                break;
        }
        return new UnitValue(temp, outputStr);
    }
}
