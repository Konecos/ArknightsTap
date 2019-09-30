package com.icebem.akt.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.icebem.akt.BuildConfig;
import com.icebem.akt.R;
import com.icebem.akt.app.CoreApplication;
import com.icebem.akt.service.CoreService;
import com.icebem.akt.util.PreferencesManager;

public class MainActivity extends Activity {
    private int timer_positive;
    private ImageView img_status;
    private TextView txt_status, txt_tips;
    private Button btn_timer, btn_service;
    private PreferencesManager manager;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBar(findViewById(R.id.toolbar_main));
        loadViews();
        loadPreferences();
    }

    private void loadViews() {
        img_status = findViewById(R.id.img_service_status);
        txt_status = findViewById(R.id.txt_service_status);
        txt_tips = findViewById(R.id.txt_service_tips);
        btn_timer = findViewById(R.id.btn_timer);
        btn_service = findViewById(R.id.btn_service);
        findViewById(R.id.btn_archives).setOnClickListener(this::onClick);
        btn_timer.setOnClickListener(this::onClick);
        btn_service.setOnClickListener(this::onClick);
    }

    private void loadPreferences() {
        manager = new PreferencesManager(this);
        if (manager.pointsAdapted()) btn_service.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }

    private void updateViews() {
        if (manager.pointsAdapted()) {
            img_status.setImageDrawable(getDrawable(isServiceEnabled() ? R.drawable.ic_auto : R.drawable.ic_done));
            txt_status.setText(isServiceEnabled() ? R.string.info_service_running : R.string.info_service_ready);
            txt_tips.setText(isServiceEnabled() ? getString(R.string.tip_service_running) : String.format(getString(R.string.tip_timer_time), manager.getTimerTime()));
            btn_timer.setEnabled(!isServiceEnabled());
            btn_service.setText(isServiceEnabled() ? R.string.action_service_disable : R.string.action_service_enable);
            ((AnimatedVectorDrawable) img_status.getDrawable()).start();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_archives:
                Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_timer:
                timer_positive = manager.getTimerPositive();
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.action_timer);
                builder.setSingleChoiceItems(manager.getTimerStrings(this), timer_positive, this::onClick);
                builder.setPositiveButton(android.R.string.ok, this::onClick);
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.create().show();
                break;
            case R.id.btn_service:
                if (isServiceEnabled()) {
                    ((CoreApplication) getApplication()).getAccessibilityService().disableSelf();
                    updateViews();
                } else {
                    Toast.makeText(this, R.string.info_service_request, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                }
                break;
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case AlertDialog.BUTTON_POSITIVE:
                manager.setTimerTime(timer_positive);
                if (!isServiceEnabled())
                    txt_tips.setText(String.format(getString(R.string.tip_timer_time), manager.getTimerTime()));
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.lanzous.com/b943175")));
                break;
            default:
                timer_positive = which;
        }
    }

    private boolean isServiceEnabled() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (am != null)
            //getRunningServices方法在Android 8.0已过时，只会获取应用自身正在运行的服务，所以列表条数最大值不用太大
            for (ActivityManager.RunningServiceInfo info : am.getRunningServices(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? Integer.MAX_VALUE : 5)) {
                if (info.service.getClassName().equals(CoreService.class.getName())) {
                    return true;
                }
            }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        builder = new AlertDialog.Builder(this);
        switch (item.getItemId()) {
            case R.id.action_donate:
                builder.setTitle(R.string.action_donate);
                builder.setMessage(R.string.msg_donate);
                builder.setPositiveButton(R.string.action_donate_alipay, (dialog, which) -> {
                    try {
                        startActivity(Intent.parseUri("intent://platformapi/startapp?saId=10000007&qrcode=https://qr.alipay.com/tsx00051lrjyg1ylmp9h359#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end", Intent.URI_INTENT_SCHEME));
                    } catch (Exception e) {
                        Log.w(getClass().getSimpleName(), e);
                    }
                    Toast.makeText(this, R.string.info_donate_thanks, Toast.LENGTH_LONG).show();
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                break;
            case R.id.action_about:
                builder.setTitle(getString(R.string.app_name) + BuildConfig.VERSION_NAME);
                builder.setMessage(R.string.msg_about);
                builder.setPositiveButton(R.string.got_it, null);
                builder.setNeutralButton(R.string.action_update, this::onClick);
                break;
        }
        builder.create().show();
        return super.onOptionsItemSelected(item);
    }
}