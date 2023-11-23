package com.yangdai.calc.main.toolbox.functions.fraction;

import static com.yangdai.calc.utils.Utils.closeKeyboard;

import android.icu.math.BigDecimal;
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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.yangdai.calc.R;
import com.yangdai.calc.utils.Utils;

import java.util.Objects;

/**
 * @author 30415
 */
public class ToDecimalFragment extends Fragment implements TextWatcher {
    TextInputEditText aInput, bInput;
    TextView textView;

    public ToDecimalFragment() {
    }

    public static ToDecimalFragment newInstance() {
        return new ToDecimalFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_decimal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aInput = view.findViewById(R.id.aEditText);
        bInput = view.findViewById(R.id.bEditText);
        bInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                bInput.clearFocus();
                return true;
            }
            return false;
        });
        textView = view.findViewById(R.id.equation);

        aInput.addTextChangedListener(this);
        bInput.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String a = Objects.requireNonNull(aInput.getText()).toString();
        String b = Objects.requireNonNull(bInput.getText()).toString();

        StringBuilder equationStr = new StringBuilder();
        if (a.isEmpty()) {
            equationStr.append("A");
        } else {
            equationStr.append(a);
        }
        equationStr.append(" / ");
        if (b.isEmpty()) {
            equationStr.append("B");
        } else {
            equationStr.append(b);
        }
        equationStr.append(" = ");

        if (!a.isEmpty() && !b.isEmpty()) {
            try {
                if (new BigDecimal(b).compareTo(BigDecimal.ZERO) != 0) {
                    String resStr;
                    try {
                        resStr = Utils.fractionToDecimal(Integer.parseInt(a), Integer.parseInt(b));
                    } catch (Exception e) {
                        resStr = "_.__";
                    }
                    equationStr.append(resStr);
                    textView.setText(equationStr);
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), getString(R.string.formatError), Toast.LENGTH_SHORT).show();
            }
        }
        equationStr.append("_.__");
        textView.setText(equationStr);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        aInput.removeTextChangedListener(this);
        bInput.removeTextChangedListener(this);
    }
}