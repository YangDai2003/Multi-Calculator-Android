package com.yangdai.calc.main.toolbox.functions.relationship;

import androidx.annotation.Nullable;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.yangdai.calc.R;
import com.yangdai.calc.main.toolbox.functions.BaseFunctionActivity;
import com.yangdai.calc.utils.TouchAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 30415
 */
public class RelationshipActivity extends BaseFunctionActivity implements View.OnClickListener {
    MaterialRadioButton male, female;
    RadioGroup radioGroup;
    TextView tvInput;
    TextView tvOutput;

    // 初始值的性别
    private boolean initSex;
    // 当前结果的性别
    private boolean isFemale;
    // 操作数数组
    private final List<String> callList = new ArrayList<>();
    // 显示的文本内容
    private String showText = "";
    // 当前运算结果
    private String resultsText = "";
    private Button[] buttonArr;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button[] buttons = {
                findViewById(R.id.btn_h),
                findViewById(R.id.btn_w),
                findViewById(R.id.btn_f),
                findViewById(R.id.btn_m),
                findViewById(R.id.btn_ob),
                findViewById(R.id.btn_lb),
                findViewById(R.id.btn_os),
                findViewById(R.id.btn_ls),
                findViewById(R.id.btn_s),
                findViewById(R.id.btn_d),
                findViewById(R.id.btn_each),
                findViewById(R.id.btn_eq),
                findViewById(R.id.btn_clr),
                findViewById(R.id.iv_del)
        };
        buttonArr = buttons;

        radioGroup = findViewById(R.id.ratioGroup);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        tvInput = findViewById(R.id.input_textview);
        tvOutput = findViewById(R.id.output_textview);

        callList.add("我");
        initSex = isFemale = female.isChecked();
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.male) {
                initSex = isFemale = false;
            } else {
                initSex = isFemale = true;
            }
            clear();
            forbiddenButton();
            emphasisShowInput();
        });
        // 给按钮设置的点击事件
        for (Button button : buttons) {
            button.setHapticFeedbackEnabled(defaultSp.getBoolean("vib", false));
            button.setOnClickListener(this);
            TouchAnimation touchAnimation = new TouchAnimation(button);
            button.setOnTouchListener(touchAnimation);
        }
        forbiddenButton();
    }

    @Override
    protected void setRootView() {
        setContentView(R.layout.activity_relationship);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.btn_clr
                && v.getId() != R.id.iv_del && v.getId() != R.id.btn_eq
                && "关系有点远，年长的就叫长辈~\n同龄人就叫帅哥美女吧".equals(resultsText)) {
            return;
        }

        // 点击了清除按钮
        if (v.getId() == R.id.btn_clr) {
            emphasisShowInput();
            initSex = isFemale = female.isChecked();
            forbiddenButton();
            clear();
        }

        // 点击了删除按钮
        else if (v.getId() == R.id.iv_del) {
            emphasisShowInput();
            delete();
            if (callList.size() > 1) {
                isFemale = !isMan(callList.get(callList.size() - 1));
            } else {
                isFemale = female.isChecked();
            }
            forbiddenButton();
        }

        // 点击了等于按钮
        else if (v.getId() == R.id.btn_eq) {
            if (callList.size() <= 1) {
                return;
            }
            if (!"TA称呼我".equals(showText) && !"关系有点远，年长的就叫长辈~\n同龄人就叫帅哥美女吧".equals(resultsText)) {
                buttonArr[10].setEnabled(true);
                refreshText();
            }
        }

        // 点击了互查按钮
        else if (v.getId() == R.id.btn_each) {
            if (!"TA称呼我".equals(showText)) {
                peerReview();
            } else {
                emphasisShowInput();
                refreshText();
                buttonArr[10].setEnabled(false);
            }
        }

        // 点击了亲戚关系按钮
        else if (v.getId() == R.id.btn_h) {
            emphasisShowInput();
            callList.add("丈夫");
            isFemale = false;
            forbiddenButton();
            refreshText();
        } else if (v.getId() == R.id.btn_w) {
            emphasisShowInput();
            callList.add("妻子");
            isFemale = true;
            forbiddenButton();
            refreshText();
        } else if (v.getId() == R.id.btn_f) {
            emphasisShowInput();
            callList.add("爸爸");
            isFemale = false;
            forbiddenButton();
            refreshText();
        } else if (v.getId() == R.id.btn_m) {
            emphasisShowInput();
            callList.add("妈妈");
            isFemale = true;
            forbiddenButton();
            refreshText();
        } else if (v.getId() == R.id.btn_ob) {
            emphasisShowInput();
            callList.add("哥哥");
            isFemale = false;
            forbiddenButton();
            refreshText();
        } else if (v.getId() == R.id.btn_lb) {
            emphasisShowInput();
            callList.add("弟弟");
            isFemale = false;
            forbiddenButton();
            refreshText();
        } else if (v.getId() == R.id.btn_os) {
            emphasisShowInput();
            callList.add("姐姐");
            isFemale = true;
            forbiddenButton();
            refreshText();
        } else if (v.getId() == R.id.btn_ls) {
            emphasisShowInput();
            callList.add("妹妹");
            isFemale = true;
            forbiddenButton();
            refreshText();
        } else if (v.getId() == R.id.btn_s) {
            emphasisShowInput();
            callList.add("儿子");
            isFemale = false;
            forbiddenButton();
            refreshText();
        } else if (v.getId() == R.id.btn_d) {
            emphasisShowInput();
            callList.add("女儿");
            isFemale = true;
            forbiddenButton();
            refreshText();
        }
    }

    // 清空并初始化
    private void clear() {
        callList.clear();
        callList.add("我");
        resultsText = "";
        refreshText();
    }

    // 刷新文本显示
    private void refreshText() {
        showText = "";
        resultsText = "";
        StringBuilder sbShowText = new StringBuilder();

        for (int i = 0; i < callList.size(); i++) {
            sbShowText.append(callList.get(i));
            if (i == callList.size() - 1) {
                break;
            }
            sbShowText.append("的");
        }
        showText = sbShowText.toString();

        if (callList.size() > 8) {
            resultsText = "关系有点远，年长的就叫长辈~\n同龄人就叫帅哥美女吧";
        } else if (callList.size() > 1) {
            operation(callList, initSex);
        }

        tvInput.setText(showText);
        tvOutput.setText(resultsText);
    }

    // 回退
    private void delete() {
        if (callList.size() > 1) {
            callList.remove(callList.size() - 1);
            operation(callList, initSex);
            refreshText();
        }
    }

    // 运算
    private void operation(List<String> list, boolean b) {
        String[][] relationshipData;
        if (b) {
            relationshipData = new RelationShipData().getRelationShipDataByWoman();
        } else {
            relationshipData = new RelationShipData().getRelationShipDataByMan();
        }
        int column = 0, row = 0;
        String resultValue = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            for (int m = 0; m < relationshipData.length; ++m) {
                if (relationshipData[m][0].equals(resultValue)) {
                    row = m;
                    break;
                }
            }
            for (int n = 0; n < relationshipData[0].length; n++) {
                if (relationshipData[0][n].equals(list.get(i))) {
                    column = n;
                    break;
                }
            }
            resultValue = relationshipData[row][column];
            if (!isExist(resultValue, relationshipData)) {
                resultValue = "未知亲戚";
                break;
            }
        }
        if ("未知亲戚".equals(resultValue) || "".equals(resultValue)) {
            resultsText = "关系有点远，年长的就叫长辈~\n同龄人就叫帅哥美女吧";
        } else {
            resultsText = resultValue;
        }
    }

    // 判断某个值在二维数组中的行首值中是否存在
    public boolean isExist(String value, String[][] array) {
        for (String[] strings : array) {
            if (value.equals(strings[0])) {
                return true;
            }
        }
        return false;
    }

    // 互查
    private void peerReview() {
        showText = "TA称呼我";
        List<String> tempList = new ArrayList<>();
        boolean tempSex;
        tempList.add("我");
        for (int i = callList.size() - 1; i > 0; i--) {
            if (("我".equals(callList.get(i - 1)) && !initSex) || isMan(callList.get(i - 1))) {
                switch (callList.get(i)) {
                    case "儿子", "女儿" -> tempList.add("爸爸");
                    case "弟弟", "妹妹" -> tempList.add("哥哥");
                    case "哥哥", "姐姐" -> tempList.add("弟弟");
                    case "爸爸", "妈妈" -> tempList.add("儿子");
                    case "妻子" -> tempList.add("丈夫");
                    default -> {
                    }
                }
            } else {
                switch (callList.get(i)) {
                    case "儿子", "女儿" -> tempList.add("妈妈");
                    case "弟弟", "妹妹" -> tempList.add("姐姐");
                    case "哥哥", "姐姐" -> tempList.add("妹妹");
                    case "爸爸", "妈妈" -> tempList.add("女儿");
                    case "丈夫" -> tempList.add("妻子");
                    default -> {
                    }
                }
            }
        }
        // 判断“我”的性别
        tempSex = !isMan(callList.get(callList.size() - 1));
        operation(tempList, tempSex);
        tvInput.setText(showText);
        tvOutput.setText(resultsText);
    }

    // 判断该亲戚是否为男性
    private boolean isMan(String s) {
        return "丈夫".equals(s) || "爸爸".equals(s) || "哥哥".equals(s)
                || "弟弟".equals(s) || "儿子".equals(s);
    }

    // 重点显示输入
    private void emphasisShowInput() {
        buttonArr[10].setEnabled(false);
    }

    // 禁用夫 \ 妻按钮
    private void forbiddenButton() {
        if (isFemale) {
            buttonArr[1].setEnabled(false);
            buttonArr[0].setEnabled(true);
        } else {
            buttonArr[0].setEnabled(false);
            buttonArr[1].setEnabled(true);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        if ("vib".equals(s)) {
            for (Button button : buttonArr) {
                button.setHapticFeedbackEnabled(defaultSp.getBoolean("vib", false));
            }
        }
    }

}
