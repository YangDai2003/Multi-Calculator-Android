package com.yangdai.calc;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.window.embedding.RuleController;

import com.google.android.material.color.DynamicColors;

/**
 * @author 30415
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        SharedPreferences defaultSp = getDefaultSharedPreferences(getApplicationContext());
        int theme = defaultSp.getInt("themeSetting", 2);
        switch (theme) {
            case 0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            case 1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            case 2 ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            default -> {
            }
        }
        boolean isActivityEmbeddingEnabled = defaultSp.getBoolean("split", false);
        if (isActivityEmbeddingEnabled) {
            RuleController.getInstance(this).setRules(RuleController.parseRules(this, R.xml.main_split_config));
        }
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            MyCrashHandler.init(getApplicationContext());
            Toast.makeText(this, "Debug", Toast.LENGTH_SHORT).show();
        }
    }
}
