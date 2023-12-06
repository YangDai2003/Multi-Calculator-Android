package com.yangdai.calc.main.toolbox.functions.equation;

import static com.yangdai.calc.utils.Utils.closeKeyboard;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yangdai.calc.R;
import com.yangdai.calc.utils.Utils;

/**
 * @author 30415
 */
public class QuadraticFragment extends Fragment {

    private EditText aEditText, bEditText, cEditText;
    private TextView x1TextView, x2TextView, equationView;

    public QuadraticFragment() {
    }

    public static QuadraticFragment newInstance() {
        return new QuadraticFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quadratic, container, false);

        // 获取布局中的视图组件
        aEditText = view.findViewById(R.id.aEditText);
        bEditText = view.findViewById(R.id.bEditText);
        cEditText = view.findViewById(R.id.cEditText);
        x1TextView = view.findViewById(R.id.x1TextView);
        x2TextView = view.findViewById(R.id.x2TextView);
        equationView = view.findViewById(R.id.equation);

        // 添加输入监听器，以便在参数a、b或c发生更改时重新计算x1和x2
        aEditText.addTextChangedListener(textWatcher);
        bEditText.addTextChangedListener(textWatcher);
        cEditText.addTextChangedListener(textWatcher);
        cEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                cEditText.clearFocus();
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

        @Override
        public void afterTextChanged(Editable editable) {
            // 获取输入的参数a、b和c的值
            String aValue = aEditText.getText().toString().trim();
            String bValue = bEditText.getText().toString().trim();
            String cValue = cEditText.getText().toString().trim();

            // 检查参数a、b和c是否为空
            if (aValue.isEmpty() && bValue.isEmpty() && cValue.isEmpty()) {
                x1TextView.setText("");
                x2TextView.setText("");
                equationView.setText("A 𝑥² + B 𝑥 + C = 0");
                return;
            }

            equationView.setText(buildEquation(aValue, bValue, cValue));

            try {
                // 解析输入的参数为BigDecimal类型
                BigDecimal a = parseBigDecimal(aValue);
                BigDecimal b = parseBigDecimal(bValue);
                BigDecimal c = parseBigDecimal(cValue);

                // 计算一元二次方程的解
                String result = calculateQuadraticEquation(a, b, c);
                String[] res = new String[2];
                if ("error".equals(result)) {
                    res[0] = getString(R.string.formatError);
                    res[1] = getString(R.string.formatError);
                } else if ("No real roots".equals(result)) {
                    res[0] = getString(R.string.noRoot);
                    res[1] = getString(R.string.noRoot);
                } else {
                    res = result.split(",");
                    res[0] = Utils.formatNumber(res[0]);
                    res[1] = Utils.formatNumber(res[1]);
                }

                // 在TextView中显示解的值
                x1TextView.setText(res[0]);
                x2TextView.setText(res[1]);
            } catch (Exception ignored) {

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        aEditText.removeTextChangedListener(textWatcher);
        bEditText.removeTextChangedListener(textWatcher);
        cEditText.removeTextChangedListener(textWatcher);
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String calculateQuadraticEquation(BigDecimal a, BigDecimal b, BigDecimal c) {
        BigDecimal discriminant = b.pow(BigDecimal.valueOf(2)).subtract(a.multiply(c).multiply(new BigDecimal(4)));

        if (a.compareTo(BigDecimal.ZERO) == 0) {
            return "error";
        } else if (discriminant.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal sqrtDiscriminant = sqrt(discriminant);
            BigDecimal x1 = b.negate().add(sqrtDiscriminant).divide(a.multiply(new BigDecimal(2)), 10, BigDecimal.ROUND_HALF_UP);
            BigDecimal x2 = b.negate().subtract(sqrtDiscriminant).divide(a.multiply(new BigDecimal(2)), 10, BigDecimal.ROUND_HALF_UP);
            return x1.toBigDecimal().toPlainString() + "," + x2.toBigDecimal().toPlainString();
        } else if (discriminant.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal x = b.negate().divide(a.multiply(new BigDecimal(2)));
            return x.toBigDecimal().toPlainString() + "," + x.toBigDecimal().toPlainString();
        } else {
            return "No real roots";
        }
    }

    private BigDecimal sqrt(BigDecimal value) {
        BigDecimal sqrt = BigDecimal.valueOf(Math.sqrt(value.doubleValue()));
        return sqrt.setScale(10, BigDecimal.ROUND_HALF_UP);
    }

    private String buildEquation(String aValue, String bValue, String cValue) {
        StringBuilder equationBuilder = new StringBuilder();

        if (!aValue.isEmpty()) {
            equationBuilder.append(aValue).append(" 𝑥² + ");
        } else {
            equationBuilder.append("A 𝑥² + ");
        }

        if (!bValue.isEmpty()) {
            equationBuilder.append(bValue).append(" 𝑥 + ");
        } else {
            equationBuilder.append("B 𝑥 + ");
        }

        if (!cValue.isEmpty()) {
            equationBuilder.append(cValue);
        } else {
            equationBuilder.append("C");
        }
        equationBuilder.append(" = 0");

        return equationBuilder.toString();
    }
}
