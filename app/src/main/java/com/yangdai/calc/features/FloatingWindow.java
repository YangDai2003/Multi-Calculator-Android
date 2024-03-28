package com.yangdai.calc.features;

import static com.yangdai.calc.main.calculator.CalculatorUtils.highlightSpecialSymbols;
import static com.yangdai.calc.utils.Utils.formatNumber;
import static com.yangdai.calc.utils.Utils.isNumber;
import static com.yangdai.calc.utils.Utils.isNumeric;
import static com.yangdai.calc.utils.Utils.isSymbol;
import static com.yangdai.calc.utils.Utils.removeZeros;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PixelFormat;
import android.icu.math.BigDecimal;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.yangdai.calc.R;
import com.yangdai.calc.main.MainActivity;
import com.yangdai.calc.main.calculator.Calculator;
import com.yangdai.calc.utils.TouchAnimation;

/**
 * @author 30415
 */
public class FloatingWindow extends Service implements View.OnClickListener {

    private ViewGroup floatView;
    private WindowManager.LayoutParams floatWindowLayoutParam;
    private WindowManager windowManager;
    private TextView inputView, outputView;
    private int left = 0, right = 0;
    private ColorStateList color;
    private static final int[] BUTTON_IDS = {R.id.div, R.id.mul, R.id.sub, R.id.add, R.id.seven,
            R.id.eight, R.id.nine, R.id.brackets, R.id.four, R.id.five, R.id.six, R.id.inverse, R.id.delete,
            R.id.three, R.id.two, R.id.one, R.id.dot, R.id.zero, R.id.equal, R.id.Clean};
    private boolean isSmallSize = true;
    private float currentAlpha = 1.0f;

    /*
     API >= 26, TYPE_APPLICATION_OVERLAY, 否则 TYPE.SYSTEM.ALERT
     */
    private static final int LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate() {
        super.onCreate();
        // 计算屏幕宽高
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int trueWidth, trueHeight;
        boolean isHeightSmaller = width >= height;
        if (isHeightSmaller) {
            trueHeight = (int) (height * (0.45f));
            trueWidth = trueHeight * 10 / 16;
        } else {
            trueWidth = (int) (width * (0.38f));
            trueHeight = (int) (height * (0.32f));
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        floatView = (ViewGroup) inflater.inflate(R.layout.floating_layout, null);

        ImageView btMaximize = floatView.findViewById(R.id.open_in_full);
        ImageView btExit = floatView.findViewById(R.id.close_float);
        ImageView btResize = floatView.findViewById(R.id.resizeWindow);
        ImageView btTransparency = floatView.findViewById(R.id.transparency);
        inputView = floatView.findViewById(R.id.edit);
        outputView = floatView.findViewById(R.id.view);
        color = outputView.getTextColors();
        outputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals(getString(R.string.formatError)) || editable.toString().equals(getString(R.string.bigNum))) {
                    outputView.setTextColor(getColor(R.color.wrong));
                } else {
                    outputView.setTextColor(color);
                }
            }
        });

        for (int buttonId : BUTTON_IDS) {
            View view1 = floatView.findViewById(buttonId);
            if (null != view1) {
                view1.setOnClickListener(this);
                TouchAnimation touchAnimation = new TouchAnimation(view1);
                view1.setOnTouchListener(touchAnimation);
            }
        }

        floatWindowLayoutParam = new WindowManager.LayoutParams(
                trueWidth,
                trueHeight,
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // 显示在屏幕中央
        floatWindowLayoutParam.gravity = Gravity.CENTER;
        floatWindowLayoutParam.x = 0;
        floatWindowLayoutParam.y = 0;

        // 添加到屏幕
        windowManager.addView(floatView, floatWindowLayoutParam);

        btMaximize.setOnClickListener(v -> {
            // 返回应用 MainActivity
            Intent backToHome = new Intent(FloatingWindow.this, MainActivity.class);

            // 1) FLAG_ACTIVITY_NEW_TASK flag helps activity to start a new task on the history stack.
            // If a task is already running like the floating window service, a new activity will not be started.
            // Instead the task will be brought back to the front just like the MainActivity here
            // 2) FLAG_ACTIVITY_CLEAR_TASK can be used in the conjunction with FLAG_ACTIVITY_NEW_TASK. This flag will
            // kill the existing task first and then new activity is started.
            backToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(backToHome);
            exit();
        });

        btExit.setOnClickListener(v -> exit());
        btResize.setOnClickListener(v -> {
            if (isSmallSize) {
                // 将悬浮窗大小设置为大尺寸
                floatWindowLayoutParam.width = (int) (trueWidth * 1.3f);
                floatWindowLayoutParam.height = (int) (trueHeight * 1.3f);
            } else {
                // 将悬浮窗大小设置为小尺寸
                floatWindowLayoutParam.width = trueWidth;
                floatWindowLayoutParam.height = trueHeight;
            }
            // 更新悬浮窗的布局参数
            windowManager.updateViewLayout(floatView, floatWindowLayoutParam);
            // 切换大小标志
            isSmallSize = !isSmallSize;
        });
        btTransparency.setOnClickListener(v -> {
            if (currentAlpha == 1.0f) {
                // 将悬浮窗透明度设置为 0.5
                floatWindowLayoutParam.alpha = 0.6f;
                currentAlpha = 0.6f;
            } else {
                // 将悬浮窗透明度设置为 1.0
                floatWindowLayoutParam.alpha = 1.0f;
                currentAlpha = 1.0f;
            }
            // 更新悬浮窗的布局参数
            windowManager.updateViewLayout(floatView, floatWindowLayoutParam);
        });

        // 实现拖动悬浮窗
        floatView.setOnTouchListener(new View.OnTouchListener() {
            final WindowManager.LayoutParams floatWindowLayoutUpdateParam = floatWindowLayoutParam;
            double x;
            double y;
            double px;
            double py;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    // 记录第一次触碰位置
                    case MotionEvent.ACTION_DOWN -> {
                        x = floatWindowLayoutUpdateParam.x;
                        y = floatWindowLayoutUpdateParam.y;


                        px = event.getRawX();
                        py = event.getRawY();
                    }
                    // 更新位置
                    case MotionEvent.ACTION_MOVE -> {
                        floatWindowLayoutUpdateParam.x = (int) ((x + event.getRawX()) - px);
                        floatWindowLayoutUpdateParam.y = (int) ((y + event.getRawY()) - py);
                        windowManager.updateViewLayout(floatView, floatWindowLayoutUpdateParam);
                    }
                }
                return false;
            }
        });


    }

    // It is called when stopService()
    // method is called in MainActivity
    @Override
    public void onDestroy() {
        super.onDestroy();
        exit();
    }

    private void exit() {
        stopSelf();
        windowManager.removeView(floatView);
    }

    @Override
    public void onClick(View v) {
        //获取输入
        String inputStr = inputView.getText().toString();
        Calculator formulaUtil = new Calculator(false);

        try {
            if (v.getId() == R.id.equal && !inputStr.isEmpty()) {
                handleEqualButton(inputStr, formulaUtil, true);
            } else if (v.getId() == R.id.Clean) {
                handleCleanButton();
            } else if (v.getId() == R.id.delete) {
                handleDeleteButton(inputStr);
            } else if (v.getId() == R.id.brackets) {
                handleBracketsButton(inputStr);
            } else if (v.getId() == R.id.inverse) {
                handleInverseButton(inputStr);
            } else {
                handleOtherButtons(v, inputStr);
            }
            String inputStr1 = inputView.getText().toString();
            highlightSpecialSymbols(inputView);
            //自动运算
            if (!inputStr1.isEmpty()) {
                Calculator formulaUtil1 = new Calculator(false);
                handleEqualButton(inputStr1, formulaUtil1, false);
            }
        } catch (Exception e) {
            outputView.setText("");
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleEqualButton(String inputStr, Calculator formulaUtil, boolean clicked) {
        // 忽略特殊情况
        if (inputStr.isEmpty() || isNumeric(inputStr)) {
            outputView.setText("");
            return;
        }

        // 补全
        if (isSymbol(String.valueOf(inputStr.charAt(inputStr.length() - 1)))) {
            inputStr += "0";
        } else if (isSymbol(String.valueOf(inputStr.charAt(0))) || inputStr.charAt(0) == '%') {
            inputStr = "0" + inputStr;
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

        try {
            BigDecimal bigDecimal = formulaUtil.calc(inputStr);
            if (null == bigDecimal) {
                outputView.setText(getString(R.string.bigNum));
                return;
            }
            bigDecimal = bigDecimal.setScale(10, BigDecimal.ROUND_HALF_UP);
            String res = bigDecimal.toBigDecimal().toPlainString();
            res = removeZeros(res);

            if (clicked) {
                inputView.setText(res);
                outputView.setText("");
            } else {
                outputView.setText(formatNumber(res));
            }
        } catch (Exception e) {
            if (clicked) {
                outputView.setText(getString(R.string.formatError));
            }
        }
    }

    private void handleCleanButton() {
        inputView.setText("");
        outputView.setText("");
        left = 0;
        right = 0;
    }

    private void handleDeleteButton(String inputStr) {
        if (!inputStr.isEmpty()) {
            if (inputStr.endsWith("asin(") || inputStr.endsWith("acos(")
                    || inputStr.endsWith("atan(") || inputStr.endsWith("acot(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 5);
                left--;
            } else if (inputStr.endsWith("sin(") || inputStr.endsWith("cos(") || inputStr.endsWith("exp(")
                    || inputStr.endsWith("tan(") || inputStr.endsWith("cot(") || inputStr.endsWith("log(")) {
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
        if (inputStr.isEmpty()) {
            outputView.setText("");
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleBracketsButton(String inputStr) {
        if (!inputStr.isEmpty()) {
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
        if (!inputStr.isEmpty()) {
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

        //长度大于0时
        if (!inputStr.isEmpty()) {
            char lastInput = inputStr.charAt(inputStr.length() - 1);
            // )、e、π 后输入数字默认加上 ×
            if (isNumber(append)) {
                if (")".equals(String.valueOf(lastInput))
                        || "e".equals(String.valueOf(lastInput)) || "π".equals(String.valueOf(lastInput))) {
                    inputView.setText(inputStr + "×" + append);
                    return;
                }
            }
            // 最后一位是两数运算符号时，再次输入符号则替换最后一位
            if (isSymbol(String.valueOf(lastInput)) && isSymbol(append)) {
                inputView.setText(inputStr.substring(0, inputStr.length() - 1) + append);
                return;
            }
            // 最后一位是数字时，输入e、π默认加上 ×
            if (isNumber(String.valueOf(lastInput))) {
                if ("e".equals(append) || "π".equals(append)) {
                    inputView.setText(inputStr + "×" + append);
                    return;
                }
            }
        }

        inputView.setText(inputStr + append);
    }
}
