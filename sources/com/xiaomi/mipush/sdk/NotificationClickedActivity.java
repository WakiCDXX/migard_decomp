package com.xiaomi.mipush.sdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import com.xiaomi.channel.commonutils.android.ReceiverHelper;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.PushConstants;

/* JADX INFO: loaded from: classes.dex */
public final class NotificationClickedActivity extends Activity {
    private static final String TAG = "PushClickedActivity";
    private static final int TIME_OUT = 3000;
    private BroadcastReceiver mBroadcastReceiver;
    private Handler mHandler;

    private void handleIntent(Intent intent) {
        try {
            if (intent != null) {
                Intent intent2 = (Intent) intent.getParcelableExtra(PushConstants.MIPUSH_EXTRA_INTENT_PAYLOAD);
                if (intent2 != null) {
                    intent2.setComponent(new ComponentName(getPackageName(), "com.xiaomi.mipush.sdk.PushMessageHandler"));
                    intent2.putExtra(PushConstants.EXTRA_IS_CLICKED_ACTIVITY_CALL, true);
                    MyLog.w(TAG, "clicked activity start service.");
                    startService(intent2);
                } else {
                    MyLog.persist(TAG, "clicked activity start service, newIntent is null");
                }
            } else {
                MyLog.persist(TAG, "clicked activity start service, missing intent");
            }
        } catch (Exception e2) {
            MyLog.e(e2);
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.height = 1;
        attributes.width = 1;
        attributes.gravity = 8388659;
        window.setAttributes(attributes);
        Handler handler = new Handler();
        this.mHandler = handler;
        handler.postDelayed(new Runnable() { // from class: com.xiaomi.mipush.sdk.NotificationClickedActivity.1
            @Override // java.lang.Runnable
            public void run() {
                MyLog.persist("clicked activity finish by timeout.");
                NotificationClickedActivity.this.finish();
            }
        }, 3000L);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PushConstants.ACTION_CLICKED_ACTIVITY_FINISH);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.xiaomi.mipush.sdk.NotificationClickedActivity.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                MyLog.i("clicked activity finish by normal.");
                NotificationClickedActivity.this.finish();
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        try {
            ReceiverHelper.registerReceiver(this, broadcastReceiver, intentFilter, AppPermission.getMipushReceivePermission(this), null, 4);
        } catch (Exception unused) {
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.mHandler.removeCallbacksAndMessages(null);
        try {
            unregisterReceiver(this.mBroadcastReceiver);
        } catch (Exception unused) {
        }
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent());
    }
}
