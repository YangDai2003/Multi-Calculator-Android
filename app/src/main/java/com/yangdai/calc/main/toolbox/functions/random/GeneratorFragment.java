package com.yangdai.calc.main.toolbox.functions.random;

import static com.yangdai.calc.utils.Utils.closeKeyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.yangdai.calc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author 30415
 */
public class GeneratorFragment extends Fragment {

    private TextView randomNumberTextView, randomNumber;
    private EditText countEditText;
    private EditText minValueEditText;
    private EditText maxValueEditText;
    private List<Integer> randomNumbersList;
    private int index = 0;
    private boolean repeat = false;
    private Button generateButton;
    private AnimatorSet animatorSet;

    public GeneratorFragment() {
    }

    public static GeneratorFragment newInstance() {
        return new GeneratorFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        randomNumberTextView = view.findViewById(R.id.randomNumberTextView);
        countEditText = view.findViewById(R.id.countEditText);
        minValueEditText = view.findViewById(R.id.minValueEditText);
        maxValueEditText = view.findViewById(R.id.maxValueEditText);
        maxValueEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                maxValueEditText.clearFocus();
                return true;
            }
            return false;
        });
        generateButton = view.findViewById(R.id.generateButton);
        randomNumber = view.findViewById(R.id.randomNumber);
        MaterialCheckBox checkBox = view.findViewById(R.id.checkbox);

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> repeat = b);

        generateButton.setOnClickListener(v -> {
            closeKeyboard(requireActivity());
            if (generateButton.getText().toString().equals(getString(R.string.jump))) {
                animatorSet.cancel();
                randomNumber.setVisibility(View.GONE);
                index = 0;
                randomNumberTextView.setText(TextUtils.join(";  ", randomNumbersList));
                generateButton.setText(getString(R.string.generate));
                return;
            }
            if (validateInput()) {
                randomNumber.setVisibility(View.VISIBLE);
                index = 0;
                randomNumberTextView.setText("");
                generateRandomNumbers();
            } else {
                Toast.makeText(getContext(), getString(R.string.formatError), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        String countText = countEditText.getText().toString();
        String minText = minValueEditText.getText().toString();
        String maxText = maxValueEditText.getText().toString();

        if (countText.isEmpty() || minText.isEmpty() || maxText.isEmpty()) {
            return false;
        }

        int count = Integer.parseInt(countText);
        int min = Integer.parseInt(minText);
        int max = Integer.parseInt(maxText);

        if (count == 0) {
            return false;
        }

        if (!repeat) {
            if (count > Math.abs(max - min) + 1) {
                return false;
            }
        }

        return min <= max;
    }

    @SuppressLint("SetTextI18n")
    private void generateRandomNumbers() {
        int count = Integer.parseInt(countEditText.getText().toString());
        int min = Integer.parseInt(minValueEditText.getText().toString());
        int max = Integer.parseInt(maxValueEditText.getText().toString());
        generateButton.setText(getString(R.string.jump));

        if (!repeat) {
            randomNumbersList = ThreadLocalRandom.current()
                    .ints(min, max + 1)
                    .distinct()
                    .limit(count)
                    .boxed()
                    .collect(Collectors.toList());
        } else {
            randomNumbersList = ThreadLocalRandom.current()
                    .ints(min, max + 1)
                    .limit(count)
                    .boxed()
                    .collect(Collectors.toList());
        }
        animateRandomNumbers(randomNumbersList);
    }

    private void animateRandomNumbers(List<Integer> randomNumbersList) {
        animatorSet = new AnimatorSet();
        List<Animator> animatorList = new ArrayList<>();

        for (int randomNumber : randomNumbersList) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, randomNumber);
            valueAnimator.setDuration(400);
            valueAnimator.setStartDelay(250);

            valueAnimator.addUpdateListener(animation -> {
                int animatedValue = (int) animation.getAnimatedValue();
                this.randomNumber.setText(String.valueOf(animatedValue));
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    updateTextView();
                }
            });

            animatorList.add(valueAnimator);
        }

        animatorSet.playSequentially(animatorList);
        animatorSet.start();
    }

    @SuppressLint("SetTextI18n")
    private void updateTextView() {
        if (index < randomNumbersList.size()) {
            if (index == randomNumbersList.size() - 1) {
                randomNumber.setVisibility(View.GONE);
                generateButton.setText(getString(R.string.generate));
                randomNumberTextView.setText(randomNumberTextView.getText().toString() + randomNumbersList.get(index));
            } else {
                randomNumberTextView.setText(randomNumberTextView.getText().toString() + randomNumbersList.get(index) + ";  ");
            }
            index++;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }
}