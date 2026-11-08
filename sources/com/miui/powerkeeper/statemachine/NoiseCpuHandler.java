package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.LocalLog;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class NoiseCpuHandler extends Handler {
    public static final String ACTION_MTK_PERF_LOCK_ACQUIRE = "joyo_mtk_perfLockAcquire";
    public static final String ACTION_MTK_PERF_LOCK_RELEASE_HANDLER = "joyo_mtk_perfLockReleaseHandler";
    private static final String CLOUD_NOISE_CPU_TUNING = "noise_cpu_tuning";
    private static final String NOISE_STATE_FILE = "/sys/class/thermal/thermal_message/voice_receiver_state";
    private static final String TAG = "NoiseCpuHandler";
    private static final String mCmd = "perflock#0x03400000_3_0x01c3c000_0#3600";
    private static NoiseCpuHandler sInstance;
    private final boolean DBG_CPU;
    private Context mContext;
    private boolean mIsBoost;
    private boolean mIsFeatureOn;
    private LocalLog mLocalLog;
    private String mNoiseProperty;
    private NoiseStateListener mNoiseStateListener;

    private class NoiseStateListener extends FileObserver {
        public NoiseStateListener(String str) throws Throwable {
            super(str);
            NoiseCpuHandler.this.updateNoiseState(str);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) throws Throwable {
            if (i2 != 2) {
                return;
            }
            NoiseCpuHandler.this.updateNoiseState(str);
        }
    }

    private NoiseCpuHandler(Context context, Looper looper) {
        super(looper);
        this.DBG_CPU = Log.isLoggable("power.cpu", 3);
        this.mLocalLog = new LocalLog(128);
        this.mNoiseStateListener = null;
        this.mNoiseProperty = "";
        this.mIsFeatureOn = false;
        this.mIsBoost = false;
        Log.i(TAG, "NoiseCpuHandler constructive");
        this.mContext = context;
        context.getContentResolverForUser(UserHandle.SYSTEM).registerContentObserver(SimpleSettings.Misc.getUriFor(CLOUD_NOISE_CPU_TUNING), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.NoiseCpuHandler.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                NoiseCpuHandler.this.updateConfig();
            }
        });
        updateConfig();
        NoiseStateListener noiseStateListener = new NoiseStateListener(NOISE_STATE_FILE);
        this.mNoiseStateListener = noiseStateListener;
        noiseStateListener.startWatching();
    }

    public static synchronized NoiseCpuHandler getInstance() {
        return sInstance;
    }

    public static synchronized NoiseCpuHandler init(Context context, Looper looper) {
        if (!isSupportDevice()) {
            Log.i(TAG, "NoiseCpuHandler init failed");
            return null;
        }
        if (sInstance == null) {
            sInstance = new NoiseCpuHandler(context, looper);
        }
        return sInstance;
    }

    private static boolean isSupportDevice() {
        return ArrayUtils.contains(new String[]{"ares", "aresin"}, Build.DEVICE);
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        if (isSupportDevice()) {
            String strOptString = jSONObject.optString(CLOUD_NOISE_CPU_TUNING, "");
            if (strOptString.equals(SimpleSettings.Misc.getString(context, CLOUD_NOISE_CPU_TUNING, ""))) {
                return;
            }
            SimpleSettings.Misc.putString(context, CLOUD_NOISE_CPU_TUNING, strOptString);
        }
    }

    private void perfByStatemachine(Context context, boolean z2) {
        if (this.mIsFeatureOn) {
            if (!z2) {
                if (!this.mIsBoost) {
                    writeLocalLog("perfByStatemachine release return, already no boost");
                    return;
                }
                writeLocalLog("perfByStatemachine perfLockRelease");
                Intent intent = new Intent(ACTION_MTK_PERF_LOCK_RELEASE_HANDLER);
                intent.setPackage("com.xiaomi.joyose");
                context.sendBroadcast(intent);
                this.mIsBoost = false;
                return;
            }
            if (this.mIsBoost) {
                writeLocalLog("perfByStatemachine acquire return, already boost");
                return;
            }
            writeLocalLog("perfByStatemachine perfLockAcquire");
            Intent intent2 = new Intent(ACTION_MTK_PERF_LOCK_ACQUIRE);
            intent2.putExtra("cmd", mCmd);
            intent2.setPackage("com.xiaomi.joyose");
            context.sendBroadcast(intent2);
            this.mIsBoost = true;
        }
    }

    private static byte[] readInputStream(FileInputStream fileInputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[512];
        while (true) {
            try {
                try {
                    try {
                        int i2 = fileInputStream.read(bArr, 0, 512);
                        if (i2 <= 0) {
                            break;
                        }
                        byteArrayOutputStream.write(bArr, 0, i2);
                    } catch (Exception e2) {
                        Log.e(TAG, "readInputStream " + e2);
                        try {
                            byteArrayOutputStream.close();
                            return null;
                        } catch (IOException e3) {
                            Log.e(TAG, "readInputStream " + e3);
                            return null;
                        }
                    }
                } catch (Throwable th) {
                    byteArrayOutputStream.close();
                    throw th;
                }
                byteArrayOutputStream.close();
            } catch (IOException e4) {
                Log.e(TAG, "readInputStream " + e4);
            }
            throw th;
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e5) {
            Log.e(TAG, "readInputStream " + e5);
        }
        return byteArray;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConfig() {
        String string = SimpleSettings.Misc.getString(this.mContext, CLOUD_NOISE_CPU_TUNING, "");
        if (string.isEmpty()) {
            if (this.DBG_CPU) {
                Log.d(TAG, "updateConfig empty cloud");
                return;
            }
            return;
        }
        try {
            this.mIsFeatureOn = new JSONObject(string).optBoolean("fucSwitch", false);
        } catch (JSONException e2) {
            Log.e(TAG, "updateConfig error", e2);
        }
        if (this.DBG_CPU) {
            Log.i(TAG, "updateConfig newCloud=" + string + " mIsFeatureOn=" + this.mIsFeatureOn);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v2, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r1v24 */
    /* JADX WARN: Type inference failed for: r1v25 */
    /* JADX WARN: Type inference failed for: r1v26 */
    /* JADX WARN: Type inference failed for: r1v7 */
    /* JADX WARN: Type inference failed for: r1v8 */
    public void updateNoiseState(String str) throws Throwable {
        StringBuilder sb;
        Context context;
        ?? r1 = 0;
        FileInputStream fileInputStream = null;
        try {
            try {
                FileInputStream fileInputStream2 = new FileInputStream(new File(NOISE_STATE_FILE));
                try {
                    byte[] inputStream = readInputStream(fileInputStream2);
                    if (inputStream == null) {
                        try {
                            fileInputStream2.close();
                            return;
                        } catch (IOException e2) {
                            Log.e(TAG, "can not get noise state " + e2);
                            return;
                        }
                    }
                    this.mNoiseProperty = new String(inputStream).trim();
                    writeLocalLog("mNoiseProperty = " + this.mNoiseProperty);
                    if (this.mNoiseProperty.equals("10000")) {
                        Context context2 = this.mContext;
                        perfByStatemachine(context2, true);
                        context = context2;
                    } else {
                        Context context3 = this.mContext;
                        perfByStatemachine(context3, false);
                        context = context3;
                    }
                    try {
                        fileInputStream2.close();
                        r1 = context;
                    } catch (IOException e3) {
                        e = e3;
                        sb = new StringBuilder();
                        sb.append("can not get noise state ");
                        sb.append(e);
                        Log.e(TAG, sb.toString());
                    }
                } catch (Exception e4) {
                    e = e4;
                    fileInputStream = fileInputStream2;
                    Log.e("", "read noise state failed", e);
                    r1 = fileInputStream;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                            r1 = fileInputStream;
                        } catch (IOException e5) {
                            e = e5;
                            sb = new StringBuilder();
                            sb.append("can not get noise state ");
                            sb.append(e);
                            Log.e(TAG, sb.toString());
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    r1 = fileInputStream2;
                    if (r1 != 0) {
                        try {
                            r1.close();
                        } catch (IOException e6) {
                            Log.e(TAG, "can not get noise state " + e6);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e7) {
            e = e7;
        }
    }

    private void writeLocalLog(String str) {
        Log.i(TAG, str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump NoiseCpuHandler mIsFeatureOn=" + this.mIsFeatureOn);
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
    }
}
