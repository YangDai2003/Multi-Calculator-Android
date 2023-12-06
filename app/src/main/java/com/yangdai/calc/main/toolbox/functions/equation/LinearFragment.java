package com.yangdai.calc.main.toolbox.functions.equation;

import static com.yangdai.calc.utils.Utils.closeKeyboard;

import android.annotation.SuppressLint;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.yangdai.calc.R;
import com.yangdai.calc.utils.Utils;

/**
 * @author 30415
 */
public class LinearFragment extends Fragment {

    private EditText aEditText;
    private EditText bEditText;
    private TextView xTextView;
    private TextView equationView;

    public LinearFragment() {
    }

    public static LinearFragment newInstance() {
        return new LinearFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear, container, false);

        // 获取布局中的视图组件
        aEditText = view.findViewById(R.id.aEditText);
        bEditText = view.findViewById(R.id.bEditText);
        xTextView = view.findViewById(R.id.xTextView);
        equationView = view.findViewById(R.id.equation);

        // 添加输入监听器，以便在参数a或b发生更改时重新计算x
        aEditText.addTextChangedListener(textWatcher);
        bEditText.addTextChangedListener(textWatcher);
        bEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                bEditText.clearFocus();
                return true;
            }
            return false;
        });

        return view;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // 不需要实现
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // 不需要实现
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void afterTextChanged(Editable editable) {
            // 获取输入的参数a和b的值
            String aValue = aEditText.getText().toString();
            String bValue = bEditText.getText().toString();

            // 检查参数a和b是否为空
            if (aValue.isEmpty() && bValue.isEmpty()) {
                equationView.setText("A 𝑥 + B = 0");
                xTextView.setText("");
                return;
            }

            String equation = buildEquation(aValue, bValue);
            equationView.setText(equation);

            try {
                BigDecimal a = parseBigDecimal(aValue);
                BigDecimal b = parseBigDecimal(bValue);
                String x = calculateX(a, b);
                if (getString(R.string.formatError).equals(x)) {
                    xTextView.setText(x);
                    return;
                }

                // 在TextView中显示x的值
                xTextView.setText(Utils.formatNumber(x));
            } catch (Exception e) {
                xTextView.setText("");
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        aEditText.removeTextChangedListener(textWatcher);
        bEditText.removeTextChangedListener(textWatcher);
    }

    private String buildEquation(String aValue, String bValue) {
        StringBuilder equationBuilder = new StringBuilder();

        if (!aValue.isEmpty()) {
            equationBuilder.append(aValue).append(" 𝑥 + ");
        } else {
            equationBuilder.append("A 𝑥 + ");
        }

        if (!bValue.isEmpty()) {
            equationBuilder.append(bValue);
        } else {
            equationBuilder.append("B");
        }

        equationBuilder.append(" = 0");

        return equationBuilder.toString();
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String calculateX(BigDecimal a, BigDecimal b) {
        if (a.compareTo(BigDecimal.ZERO) == 0) {
            return getString(R.string.formatError);
        }

        return (b.negate()).divide(a, 10, BigDecimal.ROUND_HALF_UP).toBigDecimal().toPlainString();
    }
}
