package com.yangdai.calc.main;

import android.os.Bundle;

import androidx.activity.BackEventCompat;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yangdai.calc.R;
import com.yangdai.calc.main.calculator.CalculatorFragment;
import com.yangdai.calc.main.calculator.HistoryListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 30415
 */
public class MainFragment extends Fragment {
    private MyPagerAdapter myPagerAdapter;
    private ViewPager2 viewPager2;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(HistoryListFragment.newInstance());
        fragments.add(CalculatorFragment.newInstance());
        myPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), getLifecycle(), fragments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2 = view.findViewById(R.id.view_pager);
        viewPager2.setAdapter(myPagerAdapter);
        viewPager2.setPageTransformer(new DepthPageTransformer());
        viewPager2.setCurrentItem(1, false);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    callback.setEnabled(true);
                }
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    final OnBackPressedCallback callback = new OnBackPressedCallback(false) {

        @Override
        public void handleOnBackPressed() {
            if (viewPager2.getCurrentItem() == 0) {
                viewPager2.setCurrentItem(1);
            }
            this.setEnabled(false);
        }

        @RequiresApi(34)
        @Override
        public void handleOnBackCancelled() {
            super.handleOnBackCancelled();
            viewPager2.setCurrentItem(0);
        }

//        @RequiresApi(34)
//        @Override
//        public void handleOnBackProgressed(@NonNull BackEventCompat backEvent) {
//            super.handleOnBackProgressed(backEvent);
//            viewPager2.setCurrentItem(1);
//        }

        @RequiresApi(34)
        @Override
        public void handleOnBackStarted(@NonNull BackEventCompat backEvent) {
            super.handleOnBackStarted(backEvent);
            viewPager2.setCurrentItem(1);
        }
    };
}