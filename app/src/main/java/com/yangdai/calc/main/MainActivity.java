package com.yangdai.calc.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.yangdai.calc.R;
import com.yangdai.calc.features.FloatingWindow;
import com.yangdai.calc.main.sheets.BottomSheetFragment;
import com.yangdai.calc.main.toolbox.ToolBoxFragment;
import com.yangdai.calc.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 30415
 */
public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Menu menu;
    private ViewPager2 viewPager;
    private int currentPosition = 0;
    private ImageView pageIcon;
    private SharedPreferences defaultSharedPrefs;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        if (currentPosition == 1) {
            menu.findItem(R.id.historys).setVisible(false);
            menu.findItem(R.id.view_layout).setVisible(true);
        } else {
            menu.findItem(R.id.historys).setVisible(true);
            menu.findItem(R.id.view_layout).setVisible(false);
        }
        boolean isGrid = defaultSharedPrefs.getBoolean("GridLayout", true);
        if (isGrid) {
            menu.findItem(R.id.view_layout).setIcon(getDrawable(R.drawable.grid_on));
        } else {
            menu.findItem(R.id.view_layout).setIcon(getDrawable(R.drawable.table_rows));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.historys) {
            ViewPager2 viewPager2;
            try {
                viewPager2 = findViewById(R.id.view_pager);
                if (viewPager2.getCurrentItem() == 0) {
                    viewPager2.setCurrentItem(1);
                } else {
                    viewPager2.setCurrentItem(0);
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        } else if (item.getItemId() == R.id.resize) {
            if (!Settings.canDrawOverlays(this)) {
                requestOverlayDisplayPermission();
            } else {
                startService(new Intent(MainActivity.this, FloatingWindow.class));
                finish();
            }
        } else if (item.getItemId() == R.id.setting) {
            BottomSheetFragment.newInstance().show(getSupportFragmentManager(), "dialog");
        } else if (item.getItemId() == R.id.view_layout) {
            boolean isGrid = defaultSharedPrefs.getBoolean("GridLayout", true);
            SharedPreferences.Editor editor = defaultSharedPrefs.edit();
            editor.putBoolean("GridLayout", !isGrid);
            editor.apply();
            if (!isGrid) {
                item.setIcon(getDrawable(R.drawable.grid_on));
            } else {
                item.setIcon(getDrawable(R.drawable.table_rows));
            }
            Bundle result = new Bundle();
            result.putBoolean("GridLayout", !isGrid);
            getSupportFragmentManager().setFragmentResult("ChangeLayout", result);
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestOverlayDisplayPermission() {
        new MaterialAlertDialogBuilder(this)
                .setCancelable(true)
                .setTitle(getString(R.string.Screen_Overlay_Permission_Needed))
                .setMessage(getString(R.string.Permission_Dialog_Messege))
                .setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    // 处理权限未授予的情况
                    Toast.makeText(MainActivity.this, getString(R.string.permission), Toast.LENGTH_SHORT).show();
                })
                .setPositiveButton(getString(R.string.Open_Settings), (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    overlayPermissionLauncher.launch(intent);
                }).show();
    }

    private final ActivityResultLauncher<Intent> overlayPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // 处理权限已授予的情况
                    startService(new Intent(MainActivity.this, FloatingWindow.class));
                    finish();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        setContentView(R.layout.activity_main);

        defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPrefs.registerOnSharedPreferenceChangeListener(this);

        if (defaultSharedPrefs.getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        setupToolbar();
        setupViewPager();
        pageIcon.setOnClickListener(v -> {
            if (currentPosition == 0) {
                currentPosition = 1;
                viewPager.setCurrentItem(1, true);
            } else {
                currentPosition = 0;
                viewPager.setCurrentItem(0, true);
            }
        });
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        getSupportActionBar().setElevation(0f);
        pageIcon = findViewById(R.id.view_pager_icon);
    }

    private void setupViewPager() {
        WormDotsIndicator dotsIndicator = findViewById(R.id.dotsIndicator);
        viewPager = findViewById(R.id.view_pager_main);
        if (viewPager != null) {
            reduceDragSensitivity();
            List<Fragment> fragments = new ArrayList<>();
            fragments.add(MainFragment.newInstance());
            fragments.add(ToolBoxFragment.newInstance());
            MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
            viewPager.setAdapter(pagerAdapter);
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onPageSelected(int position) {
                    currentPosition = position;
                    if (currentPosition == 0) {
                        if (menu != null) {
                            menu.findItem(R.id.historys).setVisible(true);
                            menu.findItem(R.id.view_layout).setVisible(false);
                        }
                        pageIcon.setImageDrawable(getDrawable(R.drawable.calculate_icon));
                    } else {
                        if (menu != null) {
                            menu.findItem(R.id.historys).setVisible(false);
                            menu.findItem(R.id.view_layout).setVisible(true);
                        }
                        pageIcon.setImageDrawable(getDrawable(R.drawable.grid_view_more));
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        if (currentPosition == 0) {
                            if (menu != null) {
                                menu.findItem(R.id.historys).setVisible(true);
                                menu.findItem(R.id.view_layout).setVisible(false);
                            }
                        } else {
                            if (menu != null) {
                                menu.findItem(R.id.historys).setVisible(false);
                                menu.findItem(R.id.view_layout).setVisible(true);
                            }
                        }
                    }
                }
            });
            dotsIndicator.attachTo(viewPager);
        }
    }

    private void reduceDragSensitivity() {
        try {
            Field ff = ViewPager2.class.getDeclaredField("mRecyclerView");
            ff.setAccessible(true);
            RecyclerView recyclerView = (RecyclerView) ff.get(viewPager);
            Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);
            Integer touchSlop = (Integer) touchSlopField.get(recyclerView);
            if (touchSlop != null) {
                touchSlopField.set(recyclerView, touchSlop * 5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (defaultSharedPrefs.getBoolean("vib", false)) {
            Utils.vibrate(this);
        }
        if ("split".equals(key)) {
            Toast.makeText(this, getString(R.string.restart), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        defaultSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }
}