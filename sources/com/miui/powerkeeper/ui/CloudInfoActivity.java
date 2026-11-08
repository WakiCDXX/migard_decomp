package com.miui.powerkeeper.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.cloudcontrol.LocalUpdateUtils;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.DebugUtils;
import com.miui.powerkeeper.utils.NetUtils;
import com.xiaomi.channel.commonutils.misc.DebugSwitchConstants;
import miui.system.R;

/* JADX INFO: loaded from: classes.dex */
public class CloudInfoActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CloudInfoActivity";
    private static final int UPDATE_CLOUD_INFO = 1;
    private static final String UPDATE_PROFILE = "update_profile";
    private TextView mTvAllowAutoCloudSync;
    private TextView mTvAppLastTime;
    private TextView mTvCurrentCloudAddr;
    private TextView mTvFeaLastTime;
    protected UiHandler mUiHandler;

    protected final class UiHandler extends Handler {
        protected UiHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                Log.i(CloudInfoActivity.TAG, "update cloud info UI");
                CloudInfoActivity.this.mTvFeaLastTime.setText(CloudInfoActivity.this.getResources().getString(R.string.lasttime_feature_cloud) + LocalUpdateUtils.getLastUpdateTime(CloudInfoActivity.this));
                CloudInfoActivity.this.mTvAppLastTime.setText(CloudInfoActivity.this.getResources().getString(R.string.lasttime_applist_cloud) + SimpleSettings.Misc.getString(CloudInfoActivity.this, Constants.APPLIST_UPDATE_TIME, null));
                sendEmptyMessageDelayed(1, 1000L);
            }
        }
    }

    private void initView() {
        String string = SimpleSettings.Misc.getString(this, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, "");
        Log.i(TAG, "curEnvironment---->" + string);
        if (string.equals(Constants.CLOUD_NEW_SERVER_CHINA)) {
            this.mTvCurrentCloudAddr.setText(getResources().getString(R.string.current_cloud_addr_is) + "china");
        } else if (string.equals(Constants.CLOUD_NEW_SERVER_STAGING)) {
            this.mTvCurrentCloudAddr.setText(getResources().getString(R.string.current_cloud_addr_is) + DebugSwitchConstants.SETTINGS_VALUE_HOST_STAGING);
        } else if (string.equals(Constants.CLOUD_NEW_SERVER_INTERNATIONAL)) {
            this.mTvCurrentCloudAddr.setText(getResources().getString(R.string.current_cloud_addr_is) + "international");
        } else if (string.equals(Constants.CLOUD_NEW_SERVER_RUSSION)) {
            this.mTvCurrentCloudAddr.setText(getResources().getString(R.string.current_cloud_addr_is) + "russion");
        }
        this.mTvFeaLastTime.setText(getResources().getString(R.string.lasttime_feature_cloud) + LocalUpdateUtils.getLastUpdateTime(this));
        this.mTvAppLastTime.setText(getResources().getString(R.string.lasttime_applist_cloud) + SimpleSettings.Misc.getString(this, Constants.APPLIST_UPDATE_TIME, null));
        TextView textView = this.mTvAllowAutoCloudSync;
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.current_allow_sync_is));
        sb.append(LocalUpdateUtils.getCloudAllowState(this) ? getResources().getString(R.string.enable) : getResources().getString(R.string.disable));
        textView.setText(sb.toString());
    }

    private void showPositiveDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.sucess).setMessage(R.string.update_profile_sucess).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.miui.powerkeeper.ui.CloudInfoActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
            }
        }).show();
    }

    private void showWifiUnavailableDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.failure).setMessage(R.string.update_profile_failture_not_connect_wifi).setNegativeButton(R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.miui.powerkeeper.ui.CloudInfoActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
            }
        }).show();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Intent intent = new Intent();
        new Bundle();
        switch (view.getId()) {
            case R.id.btn_cloud_change_addr /* 2131034113 */:
                SimpleSettings.Misc.putBoolean(this, Constants.IS_ALLOW_AUTO_CLOUD_SYNC, !LocalUpdateUtils.getCloudAllowState(this));
                break;
            case R.id.btn_international_addr /* 2131034116 */:
                if (!NetUtils.isWifiConnected(this)) {
                    showWifiUnavailableDialog();
                } else {
                    SimpleSettings.Misc.putString(this, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, Constants.CLOUD_NEW_SERVER_INTERNATIONAL);
                    intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.cloudcontrol.CloudUpdateReceiver");
                    intent.setAction("update_profile");
                    sendBroadcast(intent);
                    showPositiveDialog();
                }
                break;
            case R.id.btn_online_addr /* 2131034117 */:
                if (!NetUtils.isWifiConnected(this)) {
                    showWifiUnavailableDialog();
                } else {
                    SimpleSettings.Misc.putString(this, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, Constants.CLOUD_NEW_SERVER_CHINA);
                    intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.cloudcontrol.CloudUpdateReceiver");
                    intent.setAction("update_profile");
                    sendBroadcast(intent);
                    showPositiveDialog();
                }
                break;
            case R.id.btn_russion_addr /* 2131034118 */:
                if (!NetUtils.isWifiConnected(this)) {
                    showWifiUnavailableDialog();
                } else {
                    SimpleSettings.Misc.putString(this, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, Constants.CLOUD_NEW_SERVER_RUSSION);
                    intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.cloudcontrol.CloudUpdateReceiver");
                    intent.setAction("update_profile");
                    sendBroadcast(intent);
                    showPositiveDialog();
                }
                break;
            case R.id.btn_staging_addr /* 2131034119 */:
                if (!NetUtils.isWifiConnected(this)) {
                    showWifiUnavailableDialog();
                } else {
                    SimpleSettings.Misc.putString(this, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, Constants.CLOUD_NEW_SERVER_STAGING);
                    intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.cloudcontrol.CloudUpdateReceiver");
                    intent.setAction("update_profile");
                    sendBroadcast(intent);
                    showPositiveDialog();
                }
                break;
        }
        initView();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String stringExtra = getIntent().getStringExtra(DebugUtils.CALLER);
        Log.i(TAG, "mIntentData---->" + stringExtra);
        if (stringExtra == null) {
            finish();
        }
        setContentView(R.layout.activity_cloud_info);
        TextView textView = (TextView) findViewById(R.id.btn_cloud_change_addr);
        TextView textView2 = (TextView) findViewById(R.id.btn_online_addr);
        TextView textView3 = (TextView) findViewById(R.id.btn_staging_addr);
        TextView textView4 = (TextView) findViewById(R.id.btn_international_addr);
        TextView textView5 = (TextView) findViewById(R.id.btn_russion_addr);
        this.mTvCurrentCloudAddr = (TextView) findViewById(R.id.tv_current_cloud_addr);
        this.mTvFeaLastTime = (TextView) findViewById(R.id.tv_feature_update_time);
        this.mTvAppLastTime = (TextView) findViewById(R.id.tv_applist_update_time);
        this.mTvAllowAutoCloudSync = (TextView) findViewById(R.id.tv_current_allow_sync_status);
        textView.setOnClickListener(this);
        textView2.setOnClickListener(this);
        textView3.setOnClickListener(this);
        textView4.setOnClickListener(this);
        textView5.setOnClickListener(this);
        initView();
        this.mUiHandler = new UiHandler();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.mUiHandler.removeCallbacksAndMessages(null);
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        this.mUiHandler.sendEmptyMessageDelayed(1, 1000L);
    }
}
