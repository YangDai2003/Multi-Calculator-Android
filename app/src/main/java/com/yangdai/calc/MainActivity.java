package com.yangdai.calc;

import static com.yangdai.calc.utils.Utils.calculateAllFactorial;
import static com.yangdai.calc.utils.Utils.formatNumber;
import static com.yangdai.calc.utils.Utils.isNumber;
import static com.yangdai.calc.utils.Utils.isSymbol;
import static com.yangdai.calc.utils.Utils.removeZeros;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.icu.math.BigDecimal;
import android.os.Bundle;

import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.yangdai.calc.compass.Compass;
import com.yangdai.calc.converter.UnitActivity;
import com.yangdai.calc.exchange.ExchangeActivity;
import com.yangdai.calc.finance.FinanceActivity;
import com.yangdai.calc.relationship.RelationshipActivity;
import com.yangdai.calc.time.DateRangeActivity;
import com.yangdai.calc.utils.Calculator;
import com.yangdai.calc.utils.TTS;
import com.yangdai.calc.utils.TouchAnimation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author 30415
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private TextView inputView, outputView, historyView;
    SharedPreferences settings, history;
    private int left = 0, right = 0;
    private GestureDetector gestureDetector;
    private DrawerLayout drawerLayout;
    private DrawerLayout.SimpleDrawerListener simpleDrawerListener = null;
    private ActionBarDrawerToggle drawerToggle = null;
    private boolean switched = false;
    private MyBottomSheetDialog bottomSheetDialog = null;
    private TTS tts;
    private static final int[] BUTTON_IDS = {R.id.add, R.id.subtract, R.id.multiply, R.id.divide, R.id.seven,
            R.id.eight, R.id.nine, R.id.brackets, R.id.four, R.id.five, R.id.six, R.id.inverse, R.id.delete,
            R.id.e, R.id.pi, R.id.factorial, R.id.time, R.id.SHOW_ALL, R.id.percentage, R.id.g,
            R.id.switchViews, R.id.sin, R.id.cos, R.id.tan, R.id.cot,
            R.id.three, R.id.two, R.id.one, R.id.dot, R.id.zero, R.id.equal, R.id.Clean};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.resize).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (item.getItemId() == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (item.getItemId() == android.R.id.home) {
            if (drawerLayout != null) {
                if (drawerLayout.isOpen()) {
                    drawerLayout.close();
                } else {
                    drawerLayout.open();
                }
            }
            return true;
        } else if (item.getItemId() == R.id.historys) {
            showBottomSheet();
            return true;
        } else if (item.getItemId() == R.id.resize) {
            Rect rect = new Rect(0, 0, 1000, 2000);
            // 设置小窗口的尺寸
            ActivityOptions options = ActivityOptions.makeBasic();
            options.setLaunchBounds(rect);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent, options.toBundle());
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showBottomSheet() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new MyBottomSheetDialog();
        }
        // 显示Bottom Sheet
        bottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet_tag");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // 保存状态数据
        savedInstanceState.putString("input", inputView.getText().toString());
        // 可以保存多个数据，使用不同的键值对
        savedInstanceState.putString("output", outputView.getText().toString());
        savedInstanceState.putInt("left", left);
        savedInstanceState.putInt("right", right);
        savedInstanceState.putString("history", historyView.getText().toString());
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_5.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_5.getColor(this)));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        setContentView(R.layout.activity_main);

        inputView = findViewById(R.id.edit);
        inputView.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        outputView = findViewById(R.id.view);
        outputView.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        historyView = findViewById(R.id.historyView);
        historyView.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        historyView.setOnClickListener(v -> {
            if (!historyView.getText().toString().isEmpty()) {
                String lastRes = historyView.getText().toString().split("=")[1].trim();
                inputView.setText(lastRes);
                outputView.setText("");
            }
        });

        try {
            drawerLayout = findViewById(R.id.drawerLayout);
            if (!(drawerLayout instanceof DrawerLayout)) {
                drawerLayout = null;
            }
        } catch (Exception e) {
            drawerLayout = null;
        }
        if (null == drawerLayout) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        }

        // 恢复状态数据
        if (null != savedInstanceState) {
            inputView.setText(savedInstanceState.getString("input", ""));
            outputView.setText(savedInstanceState.getString("output", ""));
            left = savedInstanceState.getInt("left", 0);
            right = savedInstanceState.getInt("right", 0);
            historyView.setText(savedInstanceState.getString("history", ""));
        }

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);
        history = getSharedPreferences("history", MODE_PRIVATE);

        if (settings.getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        for (int buttonId : BUTTON_IDS) {
            findViewById(buttonId).setHapticFeedbackEnabled(settings.getBoolean("vib", false));
            findViewById(buttonId).setOnClickListener(this);
            TouchAnimation touchAnimation = new TouchAnimation(findViewById(buttonId));
            findViewById(buttonId).setOnTouchListener(touchAnimation);
        }

        getSupportFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            if (null != bundle.getString("select")) {
                inputView.setText(bundle.getString("select"));
                outputView.setText("");
            }
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(e1.getY() - e2.getY()) > 300) {
                    if (null != drawerLayout && drawerLayout.isOpen()) {
                        return false;
                    }
                    if (e1.getY() > e2.getY()) {
                        historyView.setVisibility(View.GONE);
                    } else {
                        historyView.setVisibility(View.VISIBLE);
                    }
                } else if (Math.abs(e1.getX() - e2.getX()) > 300) {
                    if (e1.getX() < e2.getX()) {
                        if (null != drawerLayout) {
                            if (!drawerLayout.isOpen()) {
                                drawerLayout.open();
                            }
                        } else {
                            return false;
                        }
                    }
                }
                return true;
            }
        });

        // 初始化TextToSpeech对象
        tts = new TTS();
        tts.ttsCreate(this);

        findViewById(R.id.speak).bringToFront();
        findViewById(R.id.speak).setOnClickListener(v -> {
            if (!inputView.getText().toString().isEmpty() && !outputView.getText().toString().isEmpty()) {
                String text = inputView.getText().toString() + "= " + outputView.getText().toString();
                tts.ttsSpeak(text);
            }
        });

        ((NavigationView) findViewById(R.id.navigationView)).setNavigationItemSelectedListener(item -> {
            if (drawerLayout != null) {
                simpleDrawerListener = new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                        handleItemSelected(item);
                        drawerLayout.removeDrawerListener(this);
                    }
                };
                drawerLayout.addDrawerListener(simpleDrawerListener);
                drawerLayout.close();
            } else {
                return handleItemSelected(item);
            }
            return true;
        });

        if (drawerLayout != null) {
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
            drawerLayout.addDrawerListener(drawerToggle);
        }

        if (!history.getBoolean("hintShowed", false)) {
            inputView.setText(getString(R.string.hint));
            SharedPreferences.Editor editor = history.edit();
            editor.putBoolean("hintShowed", true);
            editor.apply();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        if ("vib".equals(s)) {
            for (int buttonId : BUTTON_IDS) {
                findViewById(buttonId).setHapticFeedbackEnabled(settings.getBoolean("vib", false));
            }
        } else if ("scale".equals(s) || "mode".equals(s)) {
            String inputStr1 = inputView.getText().toString();
            //自动运算
            boolean useDeg = settings.getBoolean("mode", false);
            if (inputStr1.length() > 0) {
                Calculator formulaUtil1 = new Calculator(useDeg);
                handleEqualButton(inputStr1, formulaUtil1, false);
            }
        }
    }

    private boolean handleItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.units) {
            startActivity(new Intent(MainActivity.this, UnitActivity.class));
        } else if (item.getItemId() == R.id.compass) {
            startActivity(new Intent(MainActivity.this, Compass.class));
        } else if (item.getItemId() == R.id.date) {
            startActivity(new Intent(MainActivity.this, DateRangeActivity.class));
        } else if (item.getItemId() == R.id.bmi) {
            startActivity(new Intent(MainActivity.this, BMIActivity.class));
        } else if (item.getItemId() == R.id.exchange) {
            startActivity(new Intent(MainActivity.this, ExchangeActivity.class));
        } else if (item.getItemId() == R.id.chineseNumber) {
            startActivity(new Intent(MainActivity.this, ChineseNumberConversionActivity.class));
        } else if (item.getItemId() == R.id.relationship) {
            startActivity(new Intent(MainActivity.this, RelationshipActivity.class));
        } else if (item.getItemId() == R.id.discount) {
            startActivity(new Intent(MainActivity.this, DiscountActivity.class));
        } else if (item.getItemId() == R.id.finance) {
            startActivity(new Intent(MainActivity.this, FinanceActivity.class));
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 在语言变化时重新初始化TextToSpeech对象
        tts.ttsCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放TextToSpeech资源
        tts.ttsDestroy();
        settings.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }


    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onClick(View v) {

        boolean useDeg = settings.getBoolean("mode", false);
        boolean canSpeak = settings.getBoolean("voice", false);

        //获取输入
        String inputStr = inputView.getText().toString();
        Calculator formulaUtil = new Calculator(useDeg);

        try {
            if (v.getId() == R.id.equal && inputStr.length() > 0) {
                if (canSpeak) {
                    tts.ttsSpeak(getString(R.string.equal));
                }
                handleEqualButton(inputStr, formulaUtil, true);
            } else if (v.getId() == R.id.Clean) {
                if (canSpeak) {
                    tts.ttsSpeak(getString(R.string.resetInput));
                }
                handleCleanButton();
            } else if (v.getId() == R.id.factorial) {
                handleFactorial(inputStr);
            } else if (v.getId() == R.id.delete) {
                handleDeleteButton(inputStr);
            } else if (v.getId() == R.id.brackets) {
                if (canSpeak) {
                    tts.ttsSpeak(getString(R.string.bracket));
                }
                handleBracketsButton(inputStr);
            } else if (v.getId() == R.id.inverse) {
                if (canSpeak) {
                    tts.ttsSpeak(getString(R.string.inverse));
                }
                handleInverseButton(inputStr);
            } else if (v.getId() == R.id.SHOW_ALL) {
                TableRow tableRow = findViewById(R.id.hideRow);
                TableRow tableRow1 = findViewById(R.id.hideRow1);
                if (tableRow.getVisibility() == View.VISIBLE) {
                    tableRow.setVisibility(View.GONE);
                    if (tableRow1 != null) {
                        tableRow1.setVisibility(View.GONE);
                    }
                    ((ImageButton) v).setImageDrawable(getDrawable(R.drawable.combined_drawable_down));
                } else {
                    tableRow.setVisibility(View.VISIBLE);
                    if (tableRow1 != null) {
                        tableRow1.setVisibility(View.VISIBLE);
                    }
                    ((ImageButton) v).setImageDrawable(getDrawable(R.drawable.combined_drawable_up));
                }
            } else if (v.getId() == R.id.switchViews) {
                if (!switched) {
                    ((Button) findViewById(R.id.sin)).setText("asin");
                    ((Button) findViewById(R.id.cos)).setText("acos");
                    ((Button) findViewById(R.id.tan)).setText("atan");
                    ((Button) findViewById(R.id.cot)).setText("acot");
                    ((Button) findViewById(R.id.g)).setText("ln");
                } else {
                    ((Button) findViewById(R.id.sin)).setText("sin");
                    ((Button) findViewById(R.id.cos)).setText("cos");
                    ((Button) findViewById(R.id.tan)).setText("tan");
                    ((Button) findViewById(R.id.cot)).setText("cot");
                    ((Button) findViewById(R.id.g)).setText("log");
                }
                switched = !switched;
            } else {
                handleOtherButtons(v, inputStr);
            }
            String inputStr1 = inputView.getText().toString();
            //自动运算
            if (inputStr1.length() > 0) {
                Calculator formulaUtil1 = new Calculator(useDeg);
                handleEqualButton(inputStr1, formulaUtil1, false);
            }
        } catch (Exception e) {
            outputView.setText("");
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleFactorial(String inputStr) {
        boolean canSpeak = settings.getBoolean("voice", false);
        if (!inputStr.isEmpty()) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            if (isNumber(lastChar + "") && lastChar != 'e' && lastChar != 'g' && lastChar != 'π') {
                for (int i = inputStr.length() - 1; i >= 0; i--) {
                    if (inputStr.charAt(i) == '.') {
                        return;
                    }
                    if (isSymbol(inputStr.charAt(i) + "")) {
                        inputView.setText(inputStr + "!");
                        if (canSpeak) {
                            tts.ttsSpeak(getString(R.string.factorial));
                        }
                        return;
                    }
                }
                if (canSpeak) {
                    tts.ttsSpeak(getString(R.string.factorial));
                }
                inputView.setText(inputStr + "!");
            } else if (lastChar == '!') {
                char secondLastChar = inputStr.charAt(inputStr.length() - 2);
                if (secondLastChar != '!') {
                    inputView.setText(inputStr + "!");
                    if (canSpeak) {
                        tts.ttsSpeak(getString(R.string.double_factorial));
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleEqualButton(String inputStr, Calculator formulaUtil, boolean clicked) {
        if (inputStr.isEmpty()) {
            return;
        }

        if ("e".equals(inputStr) || "π".equals(inputStr)) {
            outputView.setText(inputStr);
            return;
        }

        if (isSymbol(String.valueOf(inputStr.charAt(inputStr.length() - 1)))) {
            inputStr += "0";
        }

        // 处理左右括号数量不一致的情况
        if (left != right) {
            int addCount = Math.abs(left - right);
            StringBuilder inputStrBuilder = new StringBuilder(inputStr);
            for (int j = 0; j < addCount; j++) {
                inputStrBuilder.append(")");
            }
            inputStr = inputStrBuilder.toString();
        }

        if (inputStr.contains("%")) {
            // 优化百分号(%)
            StringBuilder optimizedInputStrBuilder = new StringBuilder(inputStr);
            for (int k = optimizedInputStrBuilder.length() - 1; k >= 0; k--) {
                char currentChar = optimizedInputStrBuilder.charAt(k);
                if (currentChar == '%') {
                    int startIndex = k - 1;
                    if (optimizedInputStrBuilder.charAt(startIndex) == ')') {
                        while (startIndex >= 0) {
                            char prevChar = optimizedInputStrBuilder.charAt(startIndex);
                            if (prevChar == '(') {
                                optimizedInputStrBuilder.insert(startIndex + 1, '(');
                                break;
                            }
                            startIndex--;
                        }
                    } else {
                        boolean atBeginning = true;
                        while (startIndex >= 0) {
                            char prevChar = optimizedInputStrBuilder.charAt(startIndex);
                            if (!(isNumber(String.valueOf(prevChar)) || prevChar == '.')) {
                                optimizedInputStrBuilder.insert(startIndex + 1, '(');
                                atBeginning = false;
                                break;
                            }
                            startIndex--;
                        }
                        if (atBeginning) {
                            optimizedInputStrBuilder.insert(0, '(');
                        }
                    }
                    optimizedInputStrBuilder.insert(k + 2, ')');
                }
            }
            inputStr = optimizedInputStrBuilder.toString();
        }

        if (inputStr.contains("!")) {
            // 优化！阶乘和！！双阶乘
            inputStr = calculateAllFactorial(inputStr);
        }

        //替换常数
        inputStr = inputStr.replace("e", String.valueOf(Math.E))
                .replace("π", String.valueOf(Math.PI))
                .replace("%", "÷100");
        BigDecimal bigDecimal = formulaUtil.calc(inputStr)
                .setScale(settings.getInt("scale", 10), BigDecimal.ROUND_HALF_UP);
        String res = (bigDecimal != null) ? bigDecimal.toBigDecimal().toPlainString() : getString(R.string.error);

        if (!res.equals(getString(R.string.error))) {
            res = removeZeros(res);
            if (clicked) {
                Set<String> historys = history.getStringSet("historys", new HashSet<>());
                Set<String> temp = new HashSet<>(historys);
                if (temp.size() > 100) {
                    temp.clear();
                }
                temp.add(inputStr + "\n" + "=" + res);
                SharedPreferences.Editor editor = history.edit();
                editor.putStringSet("historys", temp);
                editor.apply();
                historyView.setText(inputView.getText() + "\n" + "=" + res);
                inputView.setText(res);
                outputView.setText("");
                return;
            }
            outputView.setText(formatNumber(res));
        } else {
            outputView.setText(res);
        }
    }

    private void handleCleanButton() {
        inputView.setText("");
        outputView.setText("");
        left = 0;
        right = 0;
    }

    private void handleDeleteButton(String inputStr) {
        if (inputStr.length() > 0) {
            if (inputStr.endsWith("asin(") || inputStr.endsWith("acos(")
                    || inputStr.endsWith("atan(") || inputStr.endsWith("acot(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 5);
                left--;
            } else if (inputStr.endsWith("sin(") || inputStr.endsWith("cos(") ||
                    inputStr.endsWith("tan(") || inputStr.endsWith("cot(") || inputStr.endsWith("log(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 4);
                left--;
            } else if (inputStr.endsWith("ln(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 3);
                left--;
            } else {
                char lastChar = inputStr.charAt(inputStr.length() - 1);
                if (lastChar == ')') {
                    right--;
                }
                if (lastChar == '(') {
                    left--;
                }
                inputStr = inputStr.substring(0, inputStr.length() - 1);
            }
            inputView.setText(inputStr);
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleBracketsButton(String inputStr) {
        if (inputStr.length() > 0) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            if (left > right && isNumber(String.valueOf(lastChar))
                    || left > right && lastChar == '%' || left > right && lastChar == ')') {
                inputView.setText(inputStr + ")");
                right++;
                return;
            } else if (lastChar == ')' || isNumber(String.valueOf(lastChar))) {
                inputView.setText(inputStr + "×(");
            } else {
                inputView.setText(inputStr + "(");
            }
        } else {
            inputView.setText(inputStr + "(");
        }
        left++;
    }

    @SuppressLint("SetTextI18n")
    private void handleInverseButton(String inputStr) {
        // 取反
        if (inputStr.length() > 0) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            //最后一位是数字
            if (isNumber(String.valueOf(lastChar))) {
                StringBuilder n = new StringBuilder();
                n.insert(0, lastChar);
                // 如果长度大于一， 从后向前遍历直到数字前一位
                if (inputStr.length() > 1) {
                    for (int i = inputStr.length() - 2; i >= 0; i--) {
                        char curr = inputStr.charAt(i);
                        if (isNumber(String.valueOf(curr)) || curr == '.') {
                            n.insert(0, curr);
                        } else {
                            // 遇到负号，如果负号前是 (-，则去掉 (-
                            if (curr == '-') {
                                if (i >= 1 && "(-".equals(inputStr.substring(i - 1, i + 1))) {
                                    inputStr = inputStr.substring(0, i - 1);
                                    inputView.setText(inputStr + n);
                                    left--;
                                    return;
                                }
                            }  // + × ÷ (  ^ 特殊情况 )
                            inputStr = inputStr.substring(0, i + 1);
                            String prefix = (curr == ')') ? "×(-" : "(-";
                            inputView.setText(inputStr + prefix + n);
                            left++;
                            return;
                        }
                    }
                }
                //只有数字
                inputView.setText("(-" + n);
                left++;
                return;
            } else if (lastChar == '-') {
                // 最后是 (-， 直接去掉
                if (inputStr.length() > 1 && (inputStr.charAt(inputStr.length() - 2) == '(')) {
                    inputView.setText(inputStr.substring(0, inputStr.length() - 2));
                    left--;
                    return;
                }
            }
            String prefix = (lastChar == ')' || lastChar == '!') ? "×(-" : "(-";
            inputView.setText(inputStr + prefix);
        } else {
            inputView.setText("(-");
        }
        left++;
    }

    @SuppressLint("SetTextI18n")
    private void handleOtherButtons(View v, String inputStr) {
        String append = ((MaterialButton) v).getText().toString();
        //开头不能为符号
        if (inputStr.length() == 0) {
            if (append.matches("[+\\-×÷.=^%]")) {
                return;
            }
        }
        boolean canSpeak = settings.getBoolean("voice", false);
        if (canSpeak) {
            readOut(append);
        }
        //长度大于0时
        if (inputStr.length() > 0) {
            char lastInput = inputStr.charAt(inputStr.length() - 1);
            // )、e、π 后输入数字默认加上*
            if ((")".equals(String.valueOf(lastInput))
                    || "e".equals(String.valueOf(lastInput)) || "π".equals(String.valueOf(lastInput)))
                    && isNumber(append)) {
                inputView.setText(inputStr + "×" + append);
                return;
            }
            // 最后一位是符号时，再次输入符号则替换最后一位
            if (isSymbol(String.valueOf(lastInput)) && isSymbol(append)) {
                inputView.setText(inputStr.substring(0, inputStr.length() - 1) + append);
                return;
            }
            // 最后一位是数字时，输入e、π默认加上*
            if (isNumber(String.valueOf(lastInput)) && ("e".equals(append) || "π".equals(append))) {
                inputView.setText(inputStr + "×" + append);
                return;
            }
        }
        inputView.setText(inputStr + append);
        if ("sin".equals(append) || "cos".equals(append) || "tan".equals(append) || "cot".equals(append)
                || "asin".equals(append) || "acos".equals(append) || "atan".equals(append) || "acot".equals(append)
                || "log".equals(append) || "ln".equals(append)) {
            inputView.setText(inputView.getText() + "(");
            left++;
        }
    }

    private void readOut(String append) {
        switch (append) {
            case "%":
                tts.ttsSpeak(getString(R.string.percentage));
                break;
            case "^":
                tts.ttsSpeak(getString(R.string.power));
                break;
            case ".":
                tts.ttsSpeak(getString(R.string.point));
                break;
            case "e":
                tts.ttsSpeak(getString(R.string.natural_base));
                break;
            case "g":
                tts.ttsSpeak(getString(R.string.g));
                break;
            case "π":
                tts.ttsSpeak(getString(R.string.pi));
                break;
            case "+":
                tts.ttsSpeak(getString(R.string.addNum));
                break;
            case "-":
                tts.ttsSpeak(getString(R.string.minusNum));
                break;
            case "×":
                tts.ttsSpeak(getString(R.string.multiplyNum));
                break;
            case "÷":
                tts.ttsSpeak(getString(R.string.divideNum));
                break;
            case "sin":
                tts.ttsSpeak(getString(R.string.sin));
                break;
            case "asin":
                tts.ttsSpeak(getString(R.string.asin));
                break;
            case "cos":
                tts.ttsSpeak(getString(R.string.cos));
                break;
            case "acos":
                tts.ttsSpeak(getString(R.string.acos));
                break;
            case "tan":
                tts.ttsSpeak(getString(R.string.tan));
                break;
            case "atan":
                tts.ttsSpeak(getString(R.string.atan));
                break;
            case "cot":
                tts.ttsSpeak(getString(R.string.cot));
                break;
            case "acot":
                tts.ttsSpeak(getString(R.string.acot));
                break;
            case "log":
                tts.ttsSpeak(getString(R.string.log));
                break;
            case "ln":
                tts.ttsSpeak(getString(R.string.ln));
                break;
            default:
                tts.ttsSpeak(append);
                break;
        }
    }

}
