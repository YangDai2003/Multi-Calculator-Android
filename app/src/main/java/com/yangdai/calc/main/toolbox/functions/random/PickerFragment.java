package com.yangdai.calc.main.toolbox.functions.random;

import static com.yangdai.calc.utils.Utils.closeKeyboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yangdai.calc.R;

import java.util.Random;

/**
 * @author 30415
 */
public class PickerFragment extends Fragment {
    private TextView textView;
    private EditText minValueEditText;
    private EditText maxValueEditText;
    private Button button;
    private Handler handler;
    private boolean isRolling;
    private final Random random = new Random();

    public PickerFragment() {
    }

    public static PickerFragment newInstance() {
        return new PickerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = view.findViewById(R.id.textView);
        button = view.findViewById(R.id.startButton);
        minValueEditText = view.findViewById(R.id.minValueEditText);
        minValueEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                minValueEditText.clearFocus();
                return true;
            }
            return false;
        });
        maxValueEditText = view.findViewById(R.id.maxValueEditText);
        maxValueEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                maxValueEditText.clearFocus();
                return true;
            }
            return false;
        });
        handler = new Handler(Looper.getMainLooper());

        String startOrStop = getString(R.string.start_pause);
        button.setText(startOrStop.split("/")[0].trim());

        button.setOnClickListener(v -> {
            closeKeyboard(requireActivity());
            if (isRolling) {
                stopRolling();
                button.setText(startOrStop.split("/")[0].trim());
            } else {
                if (validateInput()) {
                    startRolling();
                    button.setText(startOrStop.split("/")[1].trim());
                } else {
                    Toast.makeText(getContext(), getString(R.string.formatError), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInput() {
        String minText = minValueEditText.getText().toString();
        String maxText = maxValueEditText.getText().toString();

        if (TextUtils.isEmpty(minText) || TextUtils.isEmpty(maxText)) {
            return false;
        }

        int min = Integer.parseInt(minText);
        int max = Integer.parseInt(maxText);

        return min < max;
    }

    private synchronized void startRolling() {
        isRolling = true;
        handler.postDelayed(rollingRunnable, 80);
    }

    private synchronized void stopRolling() {
        isRolling = false;
        handler.removeCallbacks(rollingRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRolling();
    }

    private final Runnable rollingRunnable = new Runnable() {
        @Override
        public void run() {
            if (!validateInput()) {
                return;
            }
            try {
                int minValue = Integer.parseInt(minValueEditText.getText().toString());
                int maxValue = Integer.parseInt(maxValueEditText.getText().toString());
                int randomNumber = random.nextInt(maxValue - minValue + 1) + minValue;
                textView.setText(String.valueOf(randomNumber));
            } catch (NumberFormatException e) {
                // Handle invalid input here, display an error message to the user
                Toast.makeText(getContext(), getString(R.string.formatError), Toast.LENGTH_SHORT).show();
                stopRolling();
                return;
            }

            if (isRolling) {
                handler.postDelayed(this, 80);
            }
        }
    };
}