package com.yangdai.calc.main.toolbox.functions.fraction;

import static com.yangdai.calc.utils.Utils.closeKeyboard;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.yangdai.calc.R;
import com.yangdai.calc.utils.Utils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author 30415
 */
public class ToFractionFragment extends Fragment implements TextWatcher {

    TextView textView;
    TextInputEditText editText;

    public ToFractionFragment() {
    }

    public static ToFractionFragment newInstance() {
        return new ToFractionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_fraction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = view.findViewById(R.id.equation);
        editText = view.findViewById(R.id.xEditText);
        editText.addTextChangedListener(this);
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                editText.clearFocus();
                return true;
            }
            return false;
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void afterTextChanged(Editable editable) {
        String input = Objects.requireNonNull(editText.getText()).toString();
        if (input.isEmpty()) {
            textView.setText("_.__ = A / B");
            return;
        }
        // 使用正则表达式进行匹配
        String pattern = "^[0-9.()]+$";
        boolean isMatch = Pattern.matches(pattern, input);
        if (isMatch) {
            StringBuilder sb = new StringBuilder();
            sb.append(input).append(" = ");
            String res = Utils.decimalToFraction(input);
            sb.append(res);
            textView.setText(sb);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editText.removeTextChangedListener(this);
    }
}