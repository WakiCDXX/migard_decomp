package com.miui.powerkeeper.siming.shaosi;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.miui.powerkeeper.siming.dasi.MIY;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class SleepModeN extends N {
    public static final String TAG = "SM_S_SleepModeN";
    private boolean mUserConfigEnabled;

    protected SleepModeN(Looper looper) {
        super(looper, 102);
        this.mUserConfigEnabled = true;
    }

    public boolean isSleepModeEnabled() {
        return this.mUserConfigEnabled;
    }

    public void noteSleepModeChanged(boolean z2, String str) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MIY.BUNDLE_PARAM_SLEEP, z2);
        MIY.notify(5, bundle);
    }

    public void noteSleepUserConfigChanged(boolean z2) {
        this.mUserConfigEnabled = z2;
        Log.d(TAG, "Sleep mode user config changed, status=" + z2);
    }

    public void noteInProgress() {
    }

    @Override // com.miui.powerkeeper.siming.shaosi.S, com.miui.powerkeeper.siming.IDivision
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }
}
