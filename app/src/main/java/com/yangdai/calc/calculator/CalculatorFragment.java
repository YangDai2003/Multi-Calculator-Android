package com.yangdai.calc.calculator;

import static android.content.Context.MODE_PRIVATE;

import static com.yangdai.calc.calculator.CalculatorUtils.calculateAllFactorial;
import static com.yangdai.calc.calculator.CalculatorUtils.highlightSpecialSymbols;
import static com.yangdai.calc.calculator.CalculatorUtils.optimizePercentage;
import static com.yangdai.calc.utils.Utils.formatNumber;
import static com.yangdai.calc.utils.Utils.isNumber;
import static com.yangdai.calc.utils.Utils.isNumeric;
import static com.yangdai.calc.utils.Utils.isSymbol;
import static com.yangdai.calc.utils.Utils.removeZeros;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.icu.math.BigDecimal;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.yangdai.calc.R;
import com.yangdai.calc.utils.TTS;
import com.yangdai.calc.utils.TTSInitializationListener;
import com.yangdai.calc.utils.TouchAnimation;
import com.yangdai.calc.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 30415
 */
public class CalculatorFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener
        , View.OnClickListener, TTSInitializationListener {
    private TextView inputView;
    private TextView outputView;
    SharedPreferences settings, history;
    private int left = 0, right = 0;
    private boolean switched = false;
    private TTS tts;
    private boolean ttsAvailable;
    private ColorStateList color;
    private boolean fromUser;
    private static final int[] BUTTON_IDS = {R.id.div, R.id.mul, R.id.sub, R.id.add, R.id.seven,
            R.id.eight, R.id.nine, R.id.brackets, R.id.four, R.id.five, R.id.six, R.id.inverse, R.id.delete,
            R.id.e, R.id.pi, R.id.factorial, R.id.time, R.id.SHOW_ALL, R.id.percentage, R.id.g,
            R.id.switchViews, R.id.sin, R.id.cos, R.id.tan, R.id.cot,
            R.id.three, R.id.two, R.id.one, R.id.dot, R.id.zero, R.id.equal, R.id.Clean};

    public CalculatorFragment() {
    }

    public static CalculatorFragment newInstance() {
        return new CalculatorFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // 保存状态数据
        savedInstanceState.putString("input", inputView.getText().toString());
        savedInstanceState.putString("output", outputView.getText().toString());
        savedInstanceState.putInt("left", left);
        savedInstanceState.putInt("right", right);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        settings.registerOnSharedPreferenceChangeListener(this);
        history = getActivity().getSharedPreferences("history", MODE_PRIVATE);

        // 初始化TextToSpeech对象
        tts = new TTS();
        ttsAvailable = tts.ttsCreate(getActivity(), this);

        inputView = view.findViewById(R.id.edit);
        outputView = view.findViewById(R.id.view);
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
                    outputView.setTextColor(getActivity().getColor(R.color.wrong));
                } else {
                    outputView.setTextColor(color);
                }
            }
        });

        for (int buttonId : BUTTON_IDS) {
            View view1 = view.findViewById(buttonId);
            if (null != view1) {
                view1.setHapticFeedbackEnabled(settings.getBoolean("vib", false));
                view1.setOnClickListener(this);
                TouchAnimation touchAnimation = new TouchAnimation(view1);
                view1.setOnTouchListener(touchAnimation);
            }
        }

        updateSpeaker();

        //恢复状态数据
        if (null != savedInstanceState) {
            inputView.setText(savedInstanceState.getString("input", ""));
            outputView.setText(savedInstanceState.getString("output", ""));
            left = savedInstanceState.getInt("left", 0);
            right = savedInstanceState.getInt("right", 0);
        }

        // 处理历史记录点击结果
        getParentFragmentManager().setFragmentResultListener("requestKey", getViewLifecycleOwner(), (requestKey, bundle) -> {
            if (null != bundle.getString("select")) {
                String selected = bundle.getString("select");
                // 负数加括号
                if (selected.contains("-")) {
                    selected = "(" + selected + ")";
                }
                String inputtedEquation = inputView.getText().toString();
                if (inputtedEquation == null || inputtedEquation.isEmpty()) {
                    // 输入框为空，直接显示点击的历史记录
                    inputView.setText(selected);
                } else {
                    char last = inputtedEquation.charAt(inputtedEquation.length() - 1);
                    if (last == '+' || last == '-' || last == '(' || last == '×' || last == '÷' || last == '^') {
                        inputView.setText(inputtedEquation + selected);
                    } else {
                        inputView.setText(inputtedEquation + "+" + selected);
                    }
                    boolean useDeg = settings.getBoolean("mode", false);
                    Calculator formulaUtil1 = new Calculator(useDeg);
                    fromUser = false;
                    handleEqualButton(inputView.getText().toString(), formulaUtil1);
                }
                highlightSpecialSymbols(inputView);
            }
        });
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        if ("vib".equals(s)) {
            for (int buttonId : BUTTON_IDS) {
                View view = getView().findViewById(buttonId);
                if (view != null) {
                    view.setHapticFeedbackEnabled(settings.getBoolean("vib", false));
                }
            }
        } else if ("scale".equals(s) || "mode".equals(s)) {
            String inputStr1 = inputView.getText().toString();
            //自动运算
            boolean useDeg = settings.getBoolean("mode", false);
            if (inputStr1.length() > 0) {
                Calculator formulaUtil1 = new Calculator(useDeg);
                fromUser = false;
                handleEqualButton(inputStr1, formulaUtil1);
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        tts.ttsDestroy();
        // 在语言变化时重新初始化TextToSpeech对象
        ttsAvailable = tts.ttsCreate(getActivity(), this);
        updateSpeaker();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放TextToSpeech资源
        tts.ttsDestroy();
        settings.unregisterOnSharedPreferenceChangeListener(this);
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
                fromUser = true;
                handleEqualButton(inputStr, formulaUtil);
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
            } else if (v.getId() == R.id.switchViews) {
                View view = getView();
                if (!switched) {
                    ((Button) view.findViewById(R.id.sin)).setText("asin");
                    ((Button) view.findViewById(R.id.cos)).setText("acos");
                    ((Button) view.findViewById(R.id.tan)).setText("atan");
                    ((Button) view.findViewById(R.id.cot)).setText("acot");
                    ((Button) view.findViewById(R.id.g)).setText("ln");
                    ((Button) view.findViewById(R.id.e)).setText("exp");
                } else {
                    ((Button) view.findViewById(R.id.sin)).setText("sin");
                    ((Button) view.findViewById(R.id.cos)).setText("cos");
                    ((Button) view.findViewById(R.id.tan)).setText("tan");
                    ((Button) view.findViewById(R.id.cot)).setText("cot");
                    ((Button) view.findViewById(R.id.g)).setText("log");
                    ((Button) view.findViewById(R.id.e)).setText("e");
                }
                switched = !switched;
            } else {
                handleOtherButtons(v, inputStr);
            }
            String inputStr1 = inputView.getText().toString();
            highlightSpecialSymbols(inputView);
            if (v.getId() != R.id.equal) {
                //自动运算
                if (inputStr1.length() > 0) {
                    Calculator formulaUtil1 = new Calculator(useDeg);
                    fromUser = false;
                    handleEqualButton(inputStr1, formulaUtil1);
                }
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
    private void handleEqualButton(String inputStr, Calculator formulaUtil) {
        // 处理常量情况
        if ("e".equals(inputStr) || "π".equals(inputStr)) {
            inputStr = inputStr.replace("e", String.valueOf(Math.E))
                    .replace("π", String.valueOf(Math.PI));
            outputView.setText(inputStr);
            return;
        }

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

        inputStr = optimizePercentage(inputStr);

        try {
            if (inputStr.contains("!")) {
                // 优化！阶乘和！！双阶乘
                inputStr = calculateAllFactorial(inputStr);
                if ("数值过大".equals(inputStr)) {
                    outputView.setText(getString(R.string.bigNum));
                    return;
                }
            }

            // 使用正则表达式进行匹配
            String patternStr = "\\be\\b";
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(inputStr);
            // 替换匹配到的单独 "e"
            inputStr = matcher.replaceAll(String.valueOf(Math.E));
            //替换常数
            inputStr = inputStr.replace("π", String.valueOf(Math.PI))
                    .replace("%", "÷100");
            BigDecimal bigDecimal = formulaUtil.calc(inputStr);
            if (null == bigDecimal) {
                outputView.setText(getString(R.string.bigNum));
                return;
            }
            bigDecimal = bigDecimal.setScale(settings.getInt("scale", 10), BigDecimal.ROUND_HALF_UP);
            String res = bigDecimal.toBigDecimal().toPlainString();
            res = removeZeros(res);

            if (fromUser) {
                String historys = history.getString("newHistory", "");
                List<String> savedStringList = new ArrayList<>(Arrays.asList(historys.split("//")));

                if (savedStringList.size() > 100) {
                    savedStringList.remove(0);
                }
                savedStringList.add(inputStr + "\n" + "=" + res);
                String listString = TextUtils.join("//", savedStringList);
                SharedPreferences.Editor editor = history.edit();
                editor.putString("newHistory", listString);
                editor.apply();
                inputView.setText(res);
                outputView.setText("");
            } else {
                outputView.setText(formatNumber(res));
            }
        } catch (Exception e) {
            if (fromUser) {
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
        if (inputStr.length() > 0) {
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

        boolean canSpeak = settings.getBoolean("voice", false);
        if (canSpeak) {
            tts.ttsSpeak(append);
        }

        if (fromUser && Utils.isNumber(append)) {
            inputView.setText(append);

        } else {
            //长度大于0时
            if (inputStr.length() > 0) {
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
                if (isNumber(String.valueOf(lastInput)) && ("e".equals(append) || "π".equals(append))) {
                    inputView.setText(inputStr + "×" + append);
                    return;
                }
            }

            //三角函数运算符和对数运算符后自动加上括号
            if ("sin".equals(append) || "cos".equals(append) || "tan".equals(append) || "cot".equals(append)
                    || "asin".equals(append) || "acos".equals(append) || "atan".equals(append) || "acot".equals(append)
                    || "log".equals(append) || "ln".equals(append) || "exp".equals(append)) {
                if (inputStr.length() > 0) {
                    char lastInput = inputStr.charAt(inputStr.length() - 1);
                    if (isNumber(String.valueOf(lastInput)) || ")".equals(String.valueOf(lastInput))) {
                        inputView.setText(inputStr + "×" + append + "(");
                        left++;
                        return;
                    }
                }
                inputView.setText(inputView.getText() + append + "(");
                left++;
                return;
            }

            inputView.setText(inputStr + append);
        }
    }

    @Override
    public void onTTSInitialized(boolean isSuccess) {
        ttsAvailable = isSuccess;
        updateSpeaker();
    }

    private void updateSpeaker() {
        try {
            ImageButton readoutButton = requireView().findViewById(R.id.speak);
            if (!ttsAvailable) {
                readoutButton.setVisibility(View.INVISIBLE);
            } else {
                readoutButton.setVisibility(View.VISIBLE);
                readoutButton.bringToFront();
                readoutButton.setOnClickListener(v -> {
                    if (!inputView.getText().toString().isEmpty() && !outputView.getText().toString().isEmpty()) {
                        String text = inputView.getText().toString() + "= " + outputView.getText().toString();
                        tts.ttsSpeak(text);
                    }
                });
            }
        } catch (Exception e) {
            Log.e("updateSpeaker", e.toString());
        }
    }
}
