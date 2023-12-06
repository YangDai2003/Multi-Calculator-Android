package com.yangdai.calc.main.toolbox.functions.fraction;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yangdai.calc.R;
import com.yangdai.calc.main.toolbox.functions.BaseFunctionActivity;


/**
 * @author 30415
 */
public class FractionActivity extends BaseFunctionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    @Override
    protected void setRootView() {
        setContentView(R.layout.activity_fraction);
    }

    private void initView() {
        ViewPager2 mViewPager = findViewById(R.id.view_pager_main);
        TabLayout mTabLayout = findViewById(R.id.tab_view);

        final String[] tabs = new String[]{getString(R.string.toDecimal), getString(R.string.toFraction), getString(R.string.factorization)};
        final int[] icons = new int[]{R.drawable.fraction_icon, R.drawable.decimal_icon, R.drawable.factor_icon};

        mViewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return ToDecimalFragment.newInstance();
                } else if (position == 1) {
                    return ToFractionFragment.newInstance();
                } else {
                    return FractorizationFragment.newInstance();
                }
            }

            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });

        new TabLayoutMediator(mTabLayout, mViewPager,
                (tab, position) -> {
                    tab.setText(tabs[position]);
                    tab.setIcon(icons[position]);
                }).attach();
    }
}