package com.yangdai.calc.main.sheets;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yangdai.calc.R;

public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener {

    Preference themePref, languagePref, cleanPref;
    SharedPreferences defaultSharedPrefs;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        themePref = findPreference("theme");
        languagePref = findPreference("language");
        cleanPref = findPreference("clean");

        languagePref.setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU);
        languagePref.setOnPreferenceClickListener(this);
        themePref.setOnPreferenceClickListener(this);
        cleanPref.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        if ("theme".equals(preference.getKey())) {
            SharedPreferences.Editor editor = defaultSharedPrefs.edit();
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.theme)
                    .setCancelable(false)
                    .setSingleChoiceItems(
                            getResources().getStringArray(R.array.theme_options),
                            defaultSharedPrefs.getInt("themeSetting", 2),
                            (dialog, which) -> {
                                editor.putInt("themeSetting", which);
                                editor.apply();
                            })
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        int theme = defaultSharedPrefs.getInt("themeSetting", 2);
                        switch (theme) {
                            case 0 ->
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            case 1 ->
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            case 2 ->
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            default -> {
                            }
                        }
                    })
                    .show();
        } else if ("language".equals(preference.getKey())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                try {
                    Intent intent = new Intent(Settings.ACTION_APP_LOCALE_SETTINGS);
                    intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                    startActivity(intent);
                } catch (Exception e) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                        startActivity(intent);
                    } catch (Exception ignored) {
                    }
                }
            }
        } else if ("clean".equals(preference.getKey())) {
            SharedPreferences history = requireActivity().getSharedPreferences("history", MODE_PRIVATE);
            SharedPreferences.Editor editor = history.edit();
            editor.putString("newHistory", "");
            if (editor.commit()) {
                Toast.makeText(getContext(), getString(R.string.hasCleaned), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }
}
