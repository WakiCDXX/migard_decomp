package com.miui.powerkeeper.ui;

import android.R;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.miui.powerkeeper.utils.PowerLog;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class BgService extends Service {
    private static final String REGION_SPAIN = "ES";
    private static final String TAG = "BgService";
    private Context mContext;
    private boolean mIsHighTempBenchmarkShow = false;
    private boolean mIsHighTempShow = false;
    private boolean mIsStricterHighTempShow = false;
    private boolean mIsKillPkgShow = false;
    private boolean mIsKillSomeBackProShow = false;
    private boolean mIsKillAllBackProShow = false;

    /* JADX INFO: Access modifiers changed from: private */
    public void showAIPreloadToast() {
        Toast.makeText(this.mContext, "Preload by MIUI", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showHigh() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext, R.style.Theme.DeviceDefault.Light.Dialog.Alert);
        builder.setTitle(miui.system.R.string.high_temp_state);
        String string = this.mContext.getString(miui.system.R.string.high_temp_state_message);
        if (isSpainOrangeSim()) {
            string = this.mContext.getString(miui.system.R.string.high_temp_state_message_orange_es);
        }
        builder.setMessage(string);
        builder.setNegativeButton(miui.system.R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.miui.powerkeeper.ui.BgService.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                BgService.this.mIsHighTempShow = false;
                PowerLog.v(BgService.TAG, "showHigh, onClick");
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setCanceledOnTouchOutside(false);
        alertDialogCreate.getWindow().setType(2003);
        alertDialogCreate.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showHighBenchmark(String str) {
        PowerLog.v(TAG, "showHighBenchmark, appName:" + str);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext, R.style.Theme.DeviceDefault.Light.Dialog.Alert);
        builder.setTitle(miui.system.R.string.high_temp_benchmark_state);
        builder.setMessage(this.mContext.getString(miui.system.R.string.high_temp_benchmark_state_message) + " " + str);
        builder.setNegativeButton(miui.system.R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.miui.powerkeeper.ui.BgService.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                BgService.this.mIsHighTempBenchmarkShow = false;
                PowerLog.v(BgService.TAG, "showHighBenchmark, onClick, mIsHighTempBenchmarkShow = false");
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setCanceledOnTouchOutside(false);
        alertDialogCreate.getWindow().setType(2003);
        alertDialogCreate.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showKillAllBackProDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext, R.style.Theme.DeviceDefault.Light.Dialog.Alert);
        builder.setTitle(miui.system.R.string.high_temp_state);
        builder.setMessage(this.mContext.getString(miui.system.R.string.kill_all_back_pro_toast_message));
        builder.setNegativeButton(miui.system.R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.miui.powerkeeper.ui.BgService.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                BgService.this.mIsKillAllBackProShow = false;
                PowerLog.v(BgService.TAG, "showKillAllBackProDialog, onClick");
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setCanceledOnTouchOutside(false);
        alertDialogCreate.getWindow().setType(2003);
        alertDialogCreate.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showKillPkgDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext, R.style.Theme.DeviceDefault.Light.Dialog.Alert);
        builder.setTitle(miui.system.R.string.high_temp_state);
        builder.setMessage(this.mContext.getString(miui.system.R.string.kill_pkg_toast_message));
        builder.setNegativeButton(miui.system.R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.miui.powerkeeper.ui.BgService.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                BgService.this.mIsKillPkgShow = false;
                PowerLog.v(BgService.TAG, "showKillPkgDialog, onClick");
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setCanceledOnTouchOutside(false);
        alertDialogCreate.getWindow().setType(2003);
        alertDialogCreate.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showKillSomeBackProDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext, R.style.Theme.DeviceDefault.Light.Dialog.Alert);
        builder.setTitle(miui.system.R.string.high_temp_state);
        builder.setMessage(this.mContext.getString(miui.system.R.string.kill_some_back_pro_toast_message));
        builder.setNegativeButton(miui.system.R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.miui.powerkeeper.ui.BgService.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                BgService.this.mIsKillSomeBackProShow = false;
                PowerLog.v(BgService.TAG, "showKillSomeBackProDialog, onClick");
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setCanceledOnTouchOutside(false);
        alertDialogCreate.getWindow().setType(2003);
        alertDialogCreate.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSoftbankTimerToast(int i2) {
        if (i2 == 10) {
            Toast.makeText(this.mContext, miui.system.R.string.softbank_high_temp_toast_time_4min, 0).show();
        } else if (i2 == 20) {
            Toast.makeText(this.mContext, miui.system.R.string.softbank_high_temp_toast_time_10min, 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showStricterHigh() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext, R.style.Theme.DeviceDefault.Light.Dialog.Alert);
        builder.setTitle(miui.system.R.string.high_temp_state);
        builder.setMessage(this.mContext.getString(miui.system.R.string.stricter_high_temp_state_message));
        builder.setNegativeButton(miui.system.R.string.confirm, new DialogInterface.OnClickListener() { // from class: com.miui.powerkeeper.ui.BgService.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                BgService.this.mIsStricterHighTempShow = false;
                PowerLog.v(BgService.TAG, "showStricterHigh, onClick");
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setCanceledOnTouchOutside(false);
        alertDialogCreate.getWindow().setType(2003);
        alertDialogCreate.show();
    }

    public boolean isSpainOrangeSim() {
        if (!Build.DEVICE.contains("cupid") || !SystemProperties.get("ro.product.mod_device", "").contains("_eea_") || (!REGION_SPAIN.equals(SystemProperties.get("ro.miui.region", "")) && !REGION_SPAIN.equals(SystemProperties.get("ro.product.locale.region", "")))) {
            return false;
        }
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        int phoneCount = telephonyManager.getPhoneCount();
        for (int i2 = 0; i2 < phoneCount; i2++) {
            String simOperatorNumericForPhone = telephonyManager.getSimOperatorNumericForPhone(i2);
            Log.d(TAG, "simNumeric = " + simOperatorNumericForPhone);
            if ("21403".equals(simOperatorNumericForPhone)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // android.app.Service
    public int onStartCommand(final Intent intent, int i2, int i3) {
        Log.i(TAG, "onStartCommand intent=" + intent);
        Configuration configuration = getResources().getConfiguration();
        configuration.locale = Locale.getDefault();
        this.mContext = getBaseContext().createConfigurationContext(configuration);
        getMainThreadHandler().post(new Runnable() { // from class: com.miui.powerkeeper.ui.BgService.1
            @Override // java.lang.Runnable
            public void run() {
                Intent intent2 = intent;
                if (intent2 != null) {
                    String action = intent2.getAction();
                    if ("show_high_temp_dialog".equals(action)) {
                        if (BgService.this.mIsHighTempShow) {
                            return;
                        }
                        BgService.this.mIsHighTempShow = true;
                        BgService.this.showHigh();
                        return;
                    }
                    if ("show_ai_preload_toast".equals(action)) {
                        BgService.this.showAIPreloadToast();
                        return;
                    }
                    if ("show_high_temp_toast".equals(action)) {
                        if (BgService.this.mIsStricterHighTempShow) {
                            return;
                        }
                        BgService.this.mIsStricterHighTempShow = true;
                        BgService.this.showStricterHigh();
                        return;
                    }
                    if ("show_kill_pkg_toast".equals(action)) {
                        if (BgService.this.mIsKillPkgShow) {
                            return;
                        }
                        BgService.this.mIsKillPkgShow = true;
                        BgService.this.showKillPkgDialog();
                        return;
                    }
                    if ("show_kill_some_back_pro_toast".equals(action)) {
                        if (BgService.this.mIsKillSomeBackProShow) {
                            return;
                        }
                        BgService.this.mIsKillSomeBackProShow = true;
                        BgService.this.showKillSomeBackProDialog();
                        return;
                    }
                    if ("show_kill_all_back_pro_toast".equals(action)) {
                        if (BgService.this.mIsKillAllBackProShow) {
                            return;
                        }
                        BgService.this.mIsKillAllBackProShow = true;
                        BgService.this.showKillAllBackProDialog();
                        return;
                    }
                    if ("temp_state".equals(action)) {
                        BgService.this.showSoftbankTimerToast(intent.getIntExtra("temp_state_data", 0));
                        return;
                    }
                    if ("show_high_temp_benchmark_dialog".equals(action)) {
                        String stringExtra = intent.getStringExtra("app_name");
                        PowerLog.v(BgService.TAG, "onStartCommand, appName:" + stringExtra + ", mIsHighTempBenchmarkShow:" + BgService.this.mIsHighTempBenchmarkShow);
                        if (BgService.this.mIsHighTempBenchmarkShow) {
                            return;
                        }
                        BgService.this.mIsHighTempBenchmarkShow = true;
                        BgService.this.showHighBenchmark(stringExtra);
                    }
                }
            }
        });
        return super.onStartCommand(intent, i2, i3);
    }
}
