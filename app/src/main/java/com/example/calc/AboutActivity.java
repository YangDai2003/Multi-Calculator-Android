package com.example.calc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.elevation.SurfaceColors;

import java.util.Objects;

/**
 * @author 30415
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.about_activity);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        findViewById(R.id.about_rate).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.yangdai.calc");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        findViewById(R.id.about_share).setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareContent));
            startActivity(Intent.createChooser(sendIntent, getString(R.string.app_name)));
        });
        findViewById(R.id.about_donate).setOnClickListener(v -> {
            if (PaymentUtil.isInstalledPackage(this)) {
                PaymentUtil.startAlipayClient(this, "fkx12941hqcc7gpulzphmee"); // 第二步获取到的字符串
            } else {
                try {
                    Intent donateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/YangDaiDevelpoer?country.x=DE&locale.x=de_DE"));
                    startActivity(donateIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Please install Paypal or Alipay.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        findViewById(R.id.about_github).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://github.com/YangDai-Github/Calc-Android");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        findViewById(R.id.about_email).setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.setType("message/rfc822");
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"dy15800837435@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            startActivity(Intent.createChooser(email, "feedback"));
        });
        findViewById(R.id.about_privacy_policy).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://note.youdao.com/s/G2D1lqzp");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        TextView textView = findViewById(R.id.about_app_version);
        textView.setOnLongClickListener(v -> {
            Toast.makeText(this, getString(R.string.thank), Toast.LENGTH_LONG).show();
            return true;
        });
        findViewById(R.id.about_app_osl).setOnClickListener(v -> {
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.app_osl));
            startActivity(new Intent(this, OssLicensesMenuActivity.class));
        });
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                textView.setText(getString(R.string.app_version) + " "
                        + getPackageManager().getPackageInfo(getPackageName(), PackageManager.PackageInfoFlags.of(0)).versionName);
            } else {
                textView.setText(getString(R.string.app_version) + " "
                        + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            textView.setText(getString(R.string.app_version) + " ");
        }
    }
}