package com.yangdai.calc.main.toolbox.functions.fraction;

import static com.yangdai.calc.utils.Utils.closeKeyboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.yangdai.calc.R;

/**
 * @author 30415
 */
public class FractorizationFragment extends Fragment implements TextWatcher {

    TextInputEditText edEnter;
    TextView tvIsPrim, tvFactors;

    public FractorizationFragment() {
    }

    public static FractorizationFragment newInstance() {
        return new FractorizationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fractorization, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edEnter = view.findViewById(R.id.enter);
        tvIsPrim = view.findViewById(R.id.isPrim);
        tvFactors = view.findViewById(R.id.factor);
        edEnter.addTextChangedListener(this);
        edEnter.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                edEnter.clearFocus();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        edEnter.removeTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == null || editable.toString().isEmpty()) {
            tvIsPrim.setText("");
            tvFactors.setText("");
            return;
        }
        try {
            calculate(editable.toString());
        } catch (Exception e) {
            tvIsPrim.setText("");
            tvFactors.setText("");
        }
    }

    private boolean prime(int x) {
        //判断是否为质数（因子和原数，为质数时返回值为true）
        boolean s = true;
        for (int i = 2; i <= Math.sqrt(x); i++) {
            if (x % i == 0) {
                s = false;
                break;
            }
        }
        return s;
    }

    private String fac(int x) {
        StringBuilder sb = new StringBuilder();
        //计算因式分解式
        for (int i = 2; i <= Math.sqrt(x); i++) {
            if (prime(i)) {
                //判断i是否为质数
                while (x % i == 0) {
                    //判断质数i是否为y的质因数
                    sb.append(i).append("⨯");
                    x = x / i;
                    //除以第一个质因数后计算后面的质因数
                    if (prime(x)) {
                        //是否分解完全
                        sb.append(x);
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }

    private void calculate(String str) {
        int x = Integer.parseInt(str);
        if (x == 0 || x == 1) {
            tvIsPrim.setText(getString(R.string.isPrime_false));
            tvFactors.setText("");
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            boolean isPrime = prime(x);
            String factors = fac(x);
            handler.post(() -> {
                tvIsPrim.setText(isPrime ? getString(R.string.isPrime_true) : getString(R.string.isPrime_false));
                tvFactors.setText(isPrime ? str : factors);
            });
        }).start();
    }
}