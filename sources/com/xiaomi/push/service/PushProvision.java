package com.xiaomi.push.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import com.xiaomi.channel.commonutils.misc.BuildSettings;
import com.xiaomi.onetrack.OneTrack;

/* JADX INFO: loaded from: classes.dex */
public class PushProvision {
    private static PushProvision sInstance;
    private Context mContext;
    private int mProvisioned = 0;

    private PushProvision(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static PushProvision getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PushProvision(context);
        }
        return sInstance;
    }

    public boolean checkProvisioned() {
        String str = BuildSettings.ReleaseChannel;
        return str.contains("xmsf") || str.contains("xiaomi") || str.contains(OneTrack.Param.MIUI);
    }

    @SuppressLint({"NewApi"})
    public int getProvisioned() {
        int i2 = this.mProvisioned;
        if (i2 != 0) {
            return i2;
        }
        try {
            this.mProvisioned = Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0);
        } catch (Exception unused) {
        }
        return this.mProvisioned;
    }

    @SuppressLint({"NewApi"})
    public Uri getProvisionedUri() {
        return Settings.Global.getUriFor("device_provisioned");
    }
}
