package com.yangdai.calc.random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yangdai.calc.R;

import java.util.Objects;

/**
 * @author 30415
 */
public class RandomNumberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        setContentView(R.layout.activity_random_number);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        initView();
    }

    private void initView() {
        ViewPager2 mViewPager = findViewById(R.id.view_pager_main);
        TabLayout mTabLayout = findViewById(R.id.tab_view);

        final String[] tabs = new String[]{getString(R.string.generator), getString(R.string.draw)};

        mViewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return GeneratorFragment.newInstance();
                } else {
                    return PickerFragment.newInstance();
                }
            }

            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });

        new TabLayoutMediator(mTabLayout, mViewPager,
                (tab, position) -> tab.setText(tabs[position])).attach();
    }
}
