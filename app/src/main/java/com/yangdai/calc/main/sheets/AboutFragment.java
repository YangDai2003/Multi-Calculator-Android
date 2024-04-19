package com.yangdai.calc.main.sheets;

import static android.app.Activity.RESULT_OK;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.yangdai.calc.R;
import com.yangdai.calc.utils.PaymentUtil;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

/**
 * @author 30415
 */
public class AboutFragment extends Fragment {
    private AppUpdateManager appUpdateManager = null;
    private Task<AppUpdateInfo> appUpdateInfoTask = null;
    private final ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private final CustomTabsIntent webIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();

    public AboutFragment() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    // handle callback
                    if (result.getResultCode() != RESULT_OK) {
                        Toast.makeText(requireContext(), getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
                        Log.e("update", "Update flow failed! Result code: " + result.getResultCode());
                    }
                });
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != appUpdateManager) {
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(
                            appUpdateInfo -> {
                                if (appUpdateInfo.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                    // If an in-app update is already running, resume the update.
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            activityResultLauncher,
                                            AppUpdateOptions.newBuilder(IMMEDIATE)
                                                    .setAllowAssetPackDeletion(true)
                                                    .build());
                                }
                            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        appUpdateManager = null;
        appUpdateInfoTask = null;
    }

    @SuppressLint("SetTextI18n")
    private void init(View view) {
        view.findViewById(R.id.about_rate).setOnClickListener(v -> webIntent.launchUrl(requireContext(), Uri.parse("https://play.google.com/store/apps/details?id=com.yangdai.calc")));
        view.findViewById(R.id.about_share).setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareContent));
            startActivity(Intent.createChooser(sendIntent, getString(R.string.app_name)));
        });
        view.findViewById(R.id.about_donate).setOnClickListener(v -> {
            try {
                if (PaymentUtil.isInstalledPackage(requireContext())) {
                    PaymentUtil.startAlipayClient(requireActivity(), "fkx12941hqcc7gpulzphmee"); // 第二步获取到的字符串
                } else {
                    Intent donateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/YangDaiDevelpoer?country.x=DE&locale.x=de_DE"));
                    startActivity(donateIntent);
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Please install Paypal or Alipay.", Toast.LENGTH_SHORT).show();
            }

        });
        view.findViewById(R.id.about_github).setOnClickListener(v -> webIntent.launchUrl(requireContext(), Uri.parse("https://github.com/YangDai-Github/Multi-Calculator-Android")));
        view.findViewById(R.id.about_email).setOnClickListener(v -> {
            Uri uri = Uri.parse("mailto:dy15800837435@gmail.com");
            Intent email = new Intent(Intent.ACTION_SENDTO, uri);
            email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            startActivity(Intent.createChooser(email, "Feedback (E-mail)"));
        });
        view.findViewById(R.id.about_privacy_policy).setOnClickListener(v -> webIntent.launchUrl(requireContext(), Uri.parse("https://github.com/YangDai2003/Multi-Calculator-Android/blob/master/PRIVACY_POLICY.md")));
        TextView textView = view.findViewById(R.id.about_app_version);
        textView.setOnLongClickListener(v -> {
            Toast.makeText(requireContext(), getString(R.string.thank), Toast.LENGTH_LONG).show();
            return true;
        });
        view.findViewById(R.id.about_app_osl).setOnClickListener(v -> {
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.app_osl));
            startActivity(new Intent(requireContext(), OssLicensesMenuActivity.class));
        });
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                textView.setText(getString(R.string.app_version) + " "
                        + requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), PackageManager.PackageInfoFlags.of(0)).versionName);
            } else {
                textView.setText(getString(R.string.app_version) + " "
                        + requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0).versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            textView.setText(getString(R.string.app_version) + " ");
        }
        view.findViewById(R.id.about_app_more).setOnClickListener(v -> webIntent.launchUrl(requireContext(), Uri.parse("https://play.google.com/store/apps/dev?id=7281798021912275557")));
        view.findViewById(R.id.about_app_update).setOnClickListener(view1 -> {
            appUpdateManager = AppUpdateManagerFactory.create(requireContext());
            appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // an activity result launcher registered via registerForActivityResult
                            activityResultLauncher,
                            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                            // flexible updates.
                            AppUpdateOptions.newBuilder(IMMEDIATE)
                                    .setAllowAssetPackDeletion(true)
                                    .build());
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                    Toast.makeText(requireContext(), getString(R.string.newest), Toast.LENGTH_SHORT).show();
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UNKNOWN) {
                    Toast.makeText(requireContext(), getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}