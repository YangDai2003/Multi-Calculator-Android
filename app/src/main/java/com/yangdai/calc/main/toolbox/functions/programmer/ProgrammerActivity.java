package com.yangdai.calc.main.toolbox.functions.programmer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.yangdai.calc.R;
import com.yangdai.calc.main.toolbox.functions.BaseFunctionActivity;
import com.yangdai.calc.utils.TouchAnimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProgrammerActivity extends BaseFunctionActivity implements View.OnClickListener {

    //声明所有组件
    private TextView etText;
    private TextView et2, et8, et10, et16;
    private Button bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9, bt0, bta, btb, btc, btd, bte, btf;
    private int select = 16;
    private int digit = 5;
    private static final int[] BUTTONS = new int[]{R.id.bt_0, R.id.bt_1, R.id.bt_2, R.id.bt_3, R.id.bt_4, R.id.bt_5, R.id.bt_6, R.id.bt_7, R.id.bt_8, R.id.bt_9, R.id.bt_del, R.id.clean,
            R.id.bt_point, R.id.bt_a, R.id.bt_b, R.id.bt_c, R.id.bt_d, R.id.bt_e, R.id.bt_f};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Spinner spScale = findViewById(R.id.spinner_scale);
        String[] scaleList = {getString(R.string.hex), getString(R.string.decimal), getString(R.string.octal), getString(R.string.binary)};
        List<String> allItems = new ArrayList<>(Arrays.asList(scaleList));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spScale.setAdapter(adapter);
        spScale.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                etText.setText("");
                switch (i) {
                    case 0 -> {
                        select = 16;
                        bt0.setEnabled(true);
                        bt1.setEnabled(true);
                        bt2.setEnabled(true);
                        bt3.setEnabled(true);
                        bt4.setEnabled(true);
                        bt5.setEnabled(true);
                        bt6.setEnabled(true);
                        bt7.setEnabled(true);
                        bt8.setEnabled(true);
                        bt9.setEnabled(true);
                        bta.setEnabled(true);
                        btb.setEnabled(true);
                        btc.setEnabled(true);
                        btd.setEnabled(true);
                        bte.setEnabled(true);
                        btf.setEnabled(true);
                    }
                    case 1 -> {
                        select = 10;
                        bt0.setEnabled(true);
                        bt1.setEnabled(true);
                        bt2.setEnabled(true);
                        bt3.setEnabled(true);
                        bt4.setEnabled(true);
                        bt5.setEnabled(true);
                        bt6.setEnabled(true);
                        bt7.setEnabled(true);
                        bt8.setEnabled(true);
                        bt9.setEnabled(true);
                        bta.setEnabled(false);
                        btb.setEnabled(false);
                        btc.setEnabled(false);
                        btd.setEnabled(false);
                        bte.setEnabled(false);
                        btf.setEnabled(false);
                    }
                    case 2 -> {
                        select = 8;
                        bt0.setEnabled(true);
                        bt1.setEnabled(true);
                        bt2.setEnabled(true);
                        bt3.setEnabled(true);
                        bt4.setEnabled(true);
                        bt5.setEnabled(true);
                        bt6.setEnabled(true);
                        bt7.setEnabled(true);
                        bt8.setEnabled(false);
                        bt9.setEnabled(false);
                        bta.setEnabled(false);
                        btb.setEnabled(false);
                        btc.setEnabled(false);
                        btd.setEnabled(false);
                        bte.setEnabled(false);
                        btf.setEnabled(false);
                    }
                    case 3 -> {
                        select = 2;
                        bt0.setEnabled(true);
                        bt1.setEnabled(true);
                        bt2.setEnabled(false);
                        bt3.setEnabled(false);
                        bt4.setEnabled(false);
                        bt5.setEnabled(false);
                        bt6.setEnabled(false);
                        bt7.setEnabled(false);
                        bt8.setEnabled(false);
                        bt9.setEnabled(false);
                        bta.setEnabled(false);
                        btb.setEnabled(false);
                        btc.setEnabled(false);
                        btd.setEnabled(false);
                        bte.setEnabled(false);
                        btf.setEnabled(false);
                    }
                    default -> {
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        etText = findViewById(R.id.et_text);
        et2 = findViewById(R.id.et_2);
        et8 = findViewById(R.id.et_8);
        et10 = findViewById(R.id.et_10);
        et16 = findViewById(R.id.et_16);
        bt1 = findViewById(R.id.bt_1);
        bt2 = findViewById(R.id.bt_2);
        bt3 = findViewById(R.id.bt_3);
        bt4 = findViewById(R.id.bt_4);
        bt5 = findViewById(R.id.bt_5);
        bt6 = findViewById(R.id.bt_6);
        bt7 = findViewById(R.id.bt_7);
        bt8 = findViewById(R.id.bt_8);
        bt9 = findViewById(R.id.bt_9);
        bt0 = findViewById(R.id.bt_0);
        bta = findViewById(R.id.bt_a);
        btb = findViewById(R.id.bt_b);
        btc = findViewById(R.id.bt_c);
        btd = findViewById(R.id.bt_d);
        bte = findViewById(R.id.bt_e);
        btf = findViewById(R.id.bt_f);

        et2.setOnClickListener(this);
        et8.setOnClickListener(this);
        et10.setOnClickListener(this);
        et16.setOnClickListener(this);

        for (int buttonId : BUTTONS) {
            findViewById(buttonId).setHapticFeedbackEnabled(defaultSp.getBoolean("vib", false));
            findViewById(buttonId).setOnClickListener(this);
            TouchAnimation touchAnimation = new TouchAnimation(findViewById(buttonId));
            findViewById(buttonId).setOnTouchListener(touchAnimation);
        }

        //添加文本框变化监听
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                initRadix(input);
            }
        });

        //OnCreate 方法执行时，将shared preference里面的设置赋值给digit
        digit = defaultSp.getInt("scale", 5);
    }

    @Override
    protected void setRootView() {
        setContentView(R.layout.activity_programmer);
    }

    //判断选择的进制
    private void initRadix(String input) {

        //若空则返回
        if (TextUtils.isEmpty(input)) {
            et2.setText("");
            et8.setText("");
            et10.setText("");
            et16.setText("");
            return;
        }

        //若最后一位为"."，则返回
        if (etText.getText().toString().substring(etText.getText().toString().length() - 1).equals("."))
            return;

        //判断选择的RadioButton
        try {
            if (select == 2) {
                et2.setText(sendString(input, 2));
                et8.setText(sendString(input, 8));
                et10.setText(sendString(input, 10));
                et16.setText(sendString(input, 16));
            } else if (select == 8) {
                et2.setText(sendString(input, 2));
                et8.setText(sendString(input, 8));
                et10.setText(sendString(input, 10));
                et16.setText(sendString(input, 16));
            } else if (select == 10) {
                et2.setText(sendString(input, 2));
                et8.setText(sendString(input, 8));
                et10.setText(sendString(input, 10));
                et16.setText(sendString(input, 16));
            } else if (select == 16) {
                et2.setText(sendString(input, 2));
                et8.setText(sendString(input, 8));
                et10.setText(sendString(input, 10));
                et16.setText(sendString(input, 16));
            }
        } catch (Exception e) {
            Snackbar.make(etText, R.string.formatError, Snackbar.LENGTH_SHORT).show();
        }
    }

    //向进制转换工具类传值，分别是：需要转换的字符串，需要转换到的进制，当前进制，该方法会返回一个结果值
    private String sendString(String input, int toRadix) {
        String result;
        String[] array;
        array = input.split("\\.");
        int fromRadix = select;
        //if input data only contains integer
        if (!input.contains(".") || array.length == 1) {
            result = RadixUtil.integerConverter(array[0], toRadix, fromRadix);
        } else {
            result = RadixUtil.integerConverter(array[0], toRadix, fromRadix)
                    + "." + RadixUtil.decimalsConverter(array[1], toRadix, fromRadix, digit - 1);
        }
        return result;
    }

    //文本框和按钮设置监听
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_point) {
            etText.setText(etText.getText() + ".");
        } else if (view.getId() == R.id.bt_del) {
            if (!TextUtils.isEmpty(etText.getText().toString())) {
                String str = etText.getText().toString();
                etText.setText(str.substring(0, str.length() - 1));
            }
        } else if (view.getId() == R.id.bt_0) {
            etText.setText(etText.getText() + "0");
        } else if (view.getId() == R.id.bt_1) {
            etText.setText(etText.getText() + "1");
        } else if (view.getId() == R.id.bt_2) {
            etText.setText(etText.getText() + "2");
        } else if (view.getId() == R.id.bt_3) {
            etText.setText(etText.getText() + "3");
        } else if (view.getId() == R.id.bt_4) {
            etText.setText(etText.getText() + "4");
        } else if (view.getId() == R.id.bt_5) {
            etText.setText(etText.getText() + "5");
        } else if (view.getId() == R.id.bt_6) {
            etText.setText(etText.getText() + "6");
        } else if (view.getId() == R.id.bt_7) {
            etText.setText(etText.getText() + "7");
        } else if (view.getId() == R.id.bt_8) {
            etText.setText(etText.getText() + "8");
        } else if (view.getId() == R.id.bt_9) {
            etText.setText(etText.getText() + "9");
        } else if (view.getId() == R.id.bt_a) {
            etText.setText(etText.getText() + "A");
        } else if (view.getId() == R.id.bt_b) {
            etText.setText(etText.getText() + "B");
        } else if (view.getId() == R.id.bt_c) {
            etText.setText(etText.getText() + "C");
        } else if (view.getId() == R.id.bt_d) {
            etText.setText(etText.getText() + "D");
        } else if (view.getId() == R.id.bt_e) {
            etText.setText(etText.getText() + "E");
        } else if (view.getId() == R.id.bt_f) {
            etText.setText(etText.getText() + "F");
        } else if (view.getId() == R.id.clean) {
            etText.setText("");
        }
    }

}
