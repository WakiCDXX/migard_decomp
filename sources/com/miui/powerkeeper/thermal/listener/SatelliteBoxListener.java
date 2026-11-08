package com.miui.powerkeeper.thermal.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Slog;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class SatelliteBoxListener extends ElementListener {
    public static final String ACTION_BLUETOOTH_CONNECTION_STATE = "com.xiaomi.bluetooth.action.box_connection_state";
    public static final int BOX_CONNECTION_APP = 2;
    public static final int BOX_CONNECTION_CAR = 1;
    public static final String BOX_CONNECTION_STATE = "box_connection_state";
    private final int DEFAULT_VALUE = 99;
    private int mCurrentState = 99;
    private String mSettingSource = "";
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.thermal.listener.SatelliteBoxListener.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(SatelliteBoxListener.ACTION_BLUETOOTH_CONNECTION_STATE)) {
                int intExtra = intent.getIntExtra("type", 0);
                int intExtra2 = intent.getIntExtra(YellowPageContract.Permission.STATE, 0);
                int i2 = 1;
                if (intExtra != 0 || intExtra2 != 1) {
                    i2 = (intExtra == 1 && intExtra2 == 1) ? 2 : 99;
                }
                if (i2 != SatelliteBoxListener.this.mCurrentState) {
                    SatelliteBoxListener.this.mCurrentState = i2;
                    SatelliteBoxListener satelliteBoxListener = SatelliteBoxListener.this;
                    satelliteBoxListener.modifyCurrentState(satelliteBoxListener.mCurrentState);
                }
            }
        }
    };

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BLUETOOTH_CONNECTION_STATE);
        AutoRegister.getContext().registerReceiver(this.mReceiver, intentFilter, 2);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        if (!judgeIsCarMode()) {
            return 99;
        }
        Slog.d("SatelliteBoxListener", "judgeIsCarMode:true");
        return 1;
    }

    public boolean judgeIsCarMode() {
        String[] strArrSplit;
        String string = Settings.Secure.getString(AutoRegister.getContext().getContentResolver(), BOX_CONNECTION_STATE);
        this.mSettingSource = string;
        if (string == null || string.isEmpty() || (strArrSplit = this.mSettingSource.split(",")) == null || strArrSplit.length != 2 || !strArrSplit[0].equals("0") || !strArrSplit[1].equals("1")) {
            return false;
        }
        Slog.d("SatelliteBoxListener", "values[0]=0,values[1]=1");
        return true;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.listener.IListener
    public void uninstall() {
        super.uninstall();
        AutoRegister.getContext().unregisterReceiver(this.mReceiver);
    }

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
    }
}
