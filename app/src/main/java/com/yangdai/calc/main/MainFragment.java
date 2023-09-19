package com.yangdai.calc.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yangdai.calc.R;
import com.yangdai.calc.calculator.CalculatorFragment;
import com.yangdai.calc.calculator.HistoryListFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 30415
 */
public class MainFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ViewPager2 viewPager2;
    private boolean scrollable;
    SharedPreferences history;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2 = view.findViewById(R.id.view_pager);
        history = getActivity().getSharedPreferences("history", MODE_PRIVATE);
        history.registerOnSharedPreferenceChangeListener(this);
        updateScrollState();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(HistoryListFragment.newInstance());
        fragments.add(CalculatorFragment.newInstance());
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getChildFragmentManager(), getLifecycle(), fragments);
        viewPager2.setAdapter(pagerAdapter);
        viewPager2.setCurrentItem(1, false);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    viewPager2.setUserInputEnabled(true);
                } else {
                    viewPager2.setUserInputEnabled(scrollable);
                }
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        if ("newHistory".equals(s)) {
            updateScrollState();
        }
    }

    private void updateScrollState() {
        String historys = history.getString("newHistory", "");
        List<String> savedStringList = new ArrayList<>(Arrays.asList(historys.split("//")));
        savedStringList.removeIf(String::isEmpty);
        scrollable = savedStringList.size() < 8;
    }
}