package com.example.calc;

import static com.example.calc.Utils.formatNumber;
import static com.example.calc.Utils.isNumber;
import static com.example.calc.Utils.isSymbol;
import static com.example.calc.Utils.removeZeros;
import static com.example.calc.Utils.vibrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.PopupMenu;
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

import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calc.compass.Compass;
import com.example.calc.time.DateRangeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.elevation.SurfaceColors;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * @author 30415
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView inputView, outputView;
    SharedPreferences settings, history;
    private int left = 0, right = 0;
    ListView listView = null;
    ArrayAdapter<String> adapter = null;
    private GestureDetector gestureDetector;
    private TextToSpeech textToSpeech;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_menu, menu);
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
            // 创建Material3 Bottom Sheet
            MyBottomSheetDialog bottomSheetDialog = new MyBottomSheetDialog();
            // 显示Bottom Sheet
            bottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet_tag");
            return true;
        } else if (item.getItemId() == R.id.resize) {
            //TODO
            Rect rect = new Rect(0, 0, 1000, 2000); // 设置小窗口的尺寸
            ActivityOptions options = ActivityOptions.makeBasic();
            options.setLaunchBounds(rect);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent, options.toBundle());
        }
        return super.onOptionsItemSelected(item);
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
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_5.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_5.getColor(this)));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(getDrawable(R.drawable.history));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_main);

        inputView = findViewById(R.id.edit);
        inputView.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        outputView = findViewById(R.id.view);
        outputView.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));

        // 恢复状态数据
        if (null != savedInstanceState) {
            inputView.setText(savedInstanceState.getString("input", ""));
            outputView.setText(savedInstanceState.getString("output", ""));
            left = savedInstanceState.getInt("left", 0);
            right = savedInstanceState.getInt("right", 0);
        }

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        history = this.getSharedPreferences("history", MODE_PRIVATE);

        if (settings.getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        int[] buttonIds = {R.id.add, R.id.subtract, R.id.multiply, R.id.divide, R.id.seven, R.id.eight,
                R.id.nine, R.id.brackets, R.id.four, R.id.five, R.id.six, R.id.inverse, R.id.delete,
                R.id.e, R.id.pi, R.id.tool, R.id.time, R.id.SHOW_ALL, R.id.reciprocal, R.id.percentage, R.id.g,
                R.id.three, R.id.two, R.id.one, R.id.dot, R.id.zero, R.id.equal, R.id.Clean};
        for (int buttonId : buttonIds) {
            findViewById(buttonId).setOnClickListener(this);
        }

        getSupportFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            if (null != bundle.getString("select")) {
                inputView.setText(bundle.getString("select"));
                outputView.setText("");
            }
        });

        if (null != findViewById(R.id.linearLayout)) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            listView = findViewById(R.id.list);
            update();
            findViewById(R.id.deleteHistory).setOnClickListener(v -> {
                SharedPreferences.Editor editor = history.edit();
                editor.remove("historys");
                editor.apply();
                update();
            });
        }

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(e1.getY() - e2.getY()) > 200) {
                    if (e1.getY() > e2.getY()) {
                        // 创建Material3 Bottom Sheet
                        MyBottomSheetDialog bottomSheetDialog = new MyBottomSheetDialog();
                        // 显示Bottom Sheet
                        bottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet_tag");
                    }
                }
                return true;
            }
        });

        // 初始化TextToSpeech对象
        initializeTextToSpeech();

        findViewById(R.id.speak).bringToFront();
        findViewById(R.id.speak).setOnClickListener(v -> {
            if (!inputView.getText().toString().isEmpty() && !outputView.getText().toString().isEmpty()) {
                if (textToSpeech != null) {
                    String text = inputView.getText().toString() + "= " + outputView.getText().toString();
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });

        inputView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onClick(findViewById(R.id.equal));
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 在语言变化时重新初始化TextToSpeech对象
        initializeTextToSpeech();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 释放TextToSpeech资源
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void initializeTextToSpeech() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        // 创建新的TextToSpeech对象并设置语言
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // 获取应用语言设置
                Locale appLocale = getResources().getConfiguration().getLocales().get(0);

                // 设置TextToSpeech的语言为应用语言
                int result = textToSpeech.setLanguage(appLocale);
                textToSpeech.setSpeechRate(1.3f);
                textToSpeech.setPitch(0.8f);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech = null;
                    // 语言数据缺失或不支持，无法进行语音播报
                    Toast.makeText(MainActivity.this, "不支持当前语言", Toast.LENGTH_SHORT).show();
                }
            } else {
                textToSpeech = null;
                // 初始化TextToSpeech失败
                Toast.makeText(MainActivity.this, "初始化语音引擎失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void update() {
        Set<String> historys = history.getStringSet("historys", new HashSet<>());
        Set<String> temp = new HashSet<>(historys);
        String[] arr = temp.toArray(new String[0]);
        adapter = new ArrayAdapter<>(this, R.layout.list_item, arr);
        listView.setAdapter(adapter);
        //为列表中选中的项添加单击响应事件
        listView.setOnItemClickListener((parent, view1, i, l) -> inputView.setText(arr[i].split("=")[1]));
    }

    @SuppressLint({"SetTextI18n", "MutatingSharedPrefs", "UseCompatLoadingForDrawables"})
    @Override
    public void onClick(View v) {
        if (settings.getBoolean("vib", false)) {
            vibrate(this);
        }

        //获取输入
        String inputStr = inputView.getText().toString();
        FormulaUtil formulaUtil = new FormulaUtil();

        if (v.getId() == R.id.equal && inputStr.length() > 0) {
            handleEqualButton(inputStr, formulaUtil);
        } else if (v.getId() == R.id.Clean) {
            handleCleanButton();
        } else if (v.getId() == R.id.tool) {
            showPopupMenu(v);
        } else if (v.getId() == R.id.delete) {
            handleDeleteButton(inputStr);
        } else if (v.getId() == R.id.brackets) {
            handleBracketsButton(inputStr);
        } else if (v.getId() == R.id.inverse) {
            handleInverseButton(inputStr);
        } else if (v.getId() == R.id.SHOW_ALL) {
            TableRow tableRow = findViewById(R.id.hideRow);
            if (tableRow.getVisibility() == View.VISIBLE) {
                tableRow.setVisibility(View.GONE);
                ((ImageButton) v).setImageDrawable(getDrawable(R.drawable.combined_drawable_down));
            } else {
                tableRow.setVisibility(View.VISIBLE);
                ((ImageButton) v).setImageDrawable(getDrawable(R.drawable.combined_drawable_up));
            }
        } else if (v.getId() == R.id.reciprocal) {
            if (inputStr.length() == 0) {
                inputView.setText("1÷");
            } else {
                char lastChar = inputStr.charAt(inputStr.length() - 1);
                if (lastChar == ')' || isNumber(String.valueOf(lastChar))) {
                    inputStr += "×1÷";
                } else {
                    inputStr += "1÷";
                }
                inputView.setText(inputStr);
            }
        } else {
            handleOtherButtons(v, inputStr);
        }
    }

    private void handleEqualButton(String inputStr, FormulaUtil formulaUtil) {
        //获取输入
        if (!inputStr.matches(".*[+\\-×÷^%].*")) {
            inputStr = inputStr.replaceAll("[()]", "");
            if (inputStr.isEmpty()) {
                return;
            }
            // 常量直接返回
            if ("e".equals(inputStr) || "π".equals(inputStr) || "g".equals(inputStr)) {
                outputView.setText(inputStr);
                return;
            }
            outputView.setText(formatNumber(inputStr));
            return;
        }
        //优化负号: 若负号在开始位置，负号前不是数字且不是')'，则负号前加0
        StringBuilder stringBuilder = new StringBuilder(inputStr);
        int i = stringBuilder.length() - 1;
        while (i >= 0) {
            if (stringBuilder.charAt(i) == '-') {
                if (i == 0 ||
                        !(isNumber(String.valueOf(stringBuilder.charAt(i - 1))) || stringBuilder.charAt(i - 1) == ')')) {
                    stringBuilder.insert(i, '0');
                }
            }
            i--;
        }
        inputStr = stringBuilder.toString();

        // 处理左右括号数量不一致的情况
        if (left != right) {
            int addCount = Math.abs(left - right);
            StringBuilder inputStrBuilder = new StringBuilder(inputStr);
            for (int j = 0; j < addCount; j++) {
                inputStrBuilder.append(")");
            }
            inputStr = inputStrBuilder.toString();
        }

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


        try {
            //替换常数
            inputStr = inputStr.replace("e", String.valueOf(Math.E))
                    .replace("π", String.valueOf(Math.PI))
                    .replace("g", "9.80665")
                    .replace("%", "÷100");
            BigDecimal bigDecimal = formulaUtil.calculate(inputStr)
                    .setScale(settings.getInt("scale", 10), BigDecimal.ROUND_HALF_UP);
            String res = (bigDecimal != null) ? bigDecimal.toBigDecimal().toPlainString() : getString(R.string.error);

            if (!res.equals(getString(R.string.error))) {
                Set<String> historys = history.getStringSet("historys", new HashSet<>());
                Set<String> temp = new HashSet<>(historys);
                if (temp.size() > 100) {
                    temp.clear();
                }
                res = removeZeros(res);
                temp.add(inputStr + "\n" + "=" + res);
                SharedPreferences.Editor editor = history.edit();
                editor.putStringSet("historys", temp);
                editor.apply();
                outputView.setText(formatNumber(res));
                if (null != adapter) {
                    update();
                }
            } else {
                outputView.setText(res);
            }
        } catch (Exception e) {
            outputView.setText("");
        }
    }

    private void handleCleanButton() {
        inputView.setText("");
        outputView.setText("");
        left = 0;
        right = 0;
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popmenu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.units) {
                startActivity(new Intent(MainActivity.this, ChangeActivity.class));
            } else if (item.getItemId() == R.id.compass) {
                startActivity(new Intent(MainActivity.this, Compass.class));
            } else if (item.getItemId() == R.id.date) {
                startActivity(new Intent(MainActivity.this, DateRangeActivity.class));
            } else if (item.getItemId() == R.id.bmi) {
                startActivity(new Intent(MainActivity.this, BMIActivity.class));
            } else if (item.getItemId() == R.id.exchange) {
                startActivity(new Intent(MainActivity.this, ExchangeActivity.class));
                //Toast.makeText(this, "暂未开放", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == R.id.chineseNumber) {
                startActivity(new Intent(MainActivity.this, ChineseNumberConversionActivity.class));
            }
            return false;
        });
    }

    private void handleDeleteButton(String inputStr) {
        if (inputStr.length() > 0) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            if (lastChar == ')') {
                right--;
            }
            if (lastChar == '(') {
                left--;
            }
            inputStr = inputStr.substring(0, inputStr.length() - 1);
            inputView.setText(inputStr);
            outputView.setText("");
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
            //最后一位是数字
            if (isNumber(String.valueOf(inputStr.charAt(inputStr.length() - 1)))) {
                StringBuilder n = new StringBuilder();
                n.insert(0, inputStr.charAt(inputStr.length() - 1));
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
            } else if (inputStr.charAt(inputStr.length() - 1) == '-') {
                // 最后是 (-， 直接去掉
                if (inputStr.length() > 1 && (inputStr.charAt(inputStr.length() - 2) == '(')) {
                    inputView.setText(inputStr.substring(0, inputStr.length() - 2));
                    left--;
                    return;
                }
            }
            String prefix = (inputStr.charAt(inputStr.length() - 1) == ')') ? "×(-" : "(-";
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
                Toast.makeText(this, getString(R.string.invalid), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //长度大于0时
        if (inputStr.length() > 0) {
            char lastInput = inputStr.charAt(inputStr.length() - 1);
            // )、e、π、g 后输入数字默认加上*
            if ((")".equals(String.valueOf(lastInput))
                    || "e".equals(String.valueOf(lastInput)) || "π".equals(String.valueOf(lastInput))
                    || "g".equals(String.valueOf(lastInput)))
                    && isNumber(append)) {
                inputView.setText(inputStr + "×" + append);
                return;
            }
            // 最后一位是符号时，再次输入符号则替换最后一位
            if (isSymbol(String.valueOf(lastInput)) && isSymbol(append)) {
                inputView.setText(inputStr.substring(0, inputStr.length() - 1) + append);
                return;
            }
            // 最后一位是数字时，输入e、π，默认加上*
            if (isNumber(String.valueOf(lastInput))
                    && ("e".equals(append) || "π".equals(append) || "g".equals(append))) {
                inputView.setText(inputStr + "×" + append);
                return;
            }
        }
        inputView.setText(inputStr + append);
    }

}