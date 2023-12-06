package com.yangdai.calc.main.toolbox.functions.finance;

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
public class FinanceActivity extends BaseFunctionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    @Override
    protected void setRootView() {
        setContentView(R.layout.activity_finance);
    }

    private void initView() {
        ViewPager2 mViewPager = findViewById(R.id.view_pager_main);
        TabLayout mTabLayout = findViewById(R.id.tab_view);

        final String[] tabs = new String[]{getString(R.string.bankFragment), getString(R.string.roiFragment), getString(R.string.loanFragment), getString(R.string.vat)};
        final int[] icons = new int[]{R.drawable.bank_icon, R.drawable.invest_icon, R.drawable.loan_icon, R.drawable.tax_icon};

        mViewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return BankFragment.newInstance();
                } else if (position == 1) {
                    return InvestmentFragment.newInstance();
                } else if (position == 2) {
                    return LoanFragment.newInstance();
                } else {
                    return VatFragment.newInstance();
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