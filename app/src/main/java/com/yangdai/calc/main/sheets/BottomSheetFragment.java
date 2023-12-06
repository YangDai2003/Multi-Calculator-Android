package com.yangdai.calc.main.sheets;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yangdai.calc.R;
import com.yangdai.calc.databinding.FragmentBottomSheetDialogBinding;

import java.util.Objects;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     BottomSheetFragment.newInstance().show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class BottomSheetFragment extends BottomSheetDialogFragment {

    private FragmentBottomSheetDialogBinding binding;
    private final AboutFragment aboutFragment;

    public BottomSheetFragment() {
        aboutFragment = AboutFragment.newInstance();
    }

    public static BottomSheetFragment newInstance() {
        return new BottomSheetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Replace the container view with the PreferenceFragmentCompat
        getChildFragmentManager().beginTransaction()
                .replace(binding.settings.getId(), SettingsFragment.newInstance())
                .commit();

        binding.closeButton.setOnClickListener(v -> BottomSheetFragment.this.dismiss());
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.get(0) == R.id.chip1) {
                getChildFragmentManager().beginTransaction()
                        .replace(binding.settings.getId(), SettingsFragment.newInstance())
                        .commit();
            } else {
                getChildFragmentManager().beginTransaction()
                        .replace(binding.settings.getId(), aboutFragment)
                        .commit();
            }
        });

        Objects.requireNonNull(getDialog()).setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet == null) return;
            // 获取屏幕高度
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            // 计算设置的 peekHeight
            int peekHeight = screenHeight * 10 / 21;
            BottomSheetBehavior.from(bottomSheet).setPeekHeight(peekHeight);
            BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        binding.closeButton.setVisibility(View.VISIBLE);
                    } else {
                        binding.closeButton.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}