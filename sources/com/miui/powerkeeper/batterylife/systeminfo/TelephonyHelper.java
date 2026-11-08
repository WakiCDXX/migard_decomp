package com.miui.powerkeeper.batterylife.systeminfo;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.miui.powerkeeper.batterylife.BLUtils;

/* JADX INFO: loaded from: classes.dex */
public class TelephonyHelper {
    public static final String TAG = "BL-TelephonyHelper";
    PhoneStateListener mListener = new PhoneStateListener() { // from class: com.miui.powerkeeper.batterylife.systeminfo.TelephonyHelper.1
        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(int i2, String str) {
            super.onCallStateChanged(i2, str);
        }
    };
    private TelephonyManager mTelephony;

    public TelephonyHelper(Context context) {
        this.mTelephony = (TelephonyManager) context.getSystemService("phone");
    }

    public String getPhoneNumber() {
        return this.mTelephony.getLine1Number();
    }

    public boolean isIdle() {
        return !BLUtils.isFlagSupport(4) || this.mTelephony.getCallState() == 0;
    }

    void registerListen() {
        this.mTelephony.listen(this.mListener, 32);
    }

    void unregisterListen() {
        this.mTelephony.listen(this.mListener, 0);
    }
}
