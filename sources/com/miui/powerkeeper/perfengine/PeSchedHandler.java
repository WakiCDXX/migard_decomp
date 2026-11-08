package com.miui.powerkeeper.perfengine;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.statemachine.DisplayFrameSetting;
import com.miui.powerkeeper.utils.PowerLog;
import com.xiaomi.push.mpcd.Constants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import miui.hardware.display.DisplayFeatureManager;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class PeSchedHandler extends Handler {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private PeGameController f455a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private String f456b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private String f457c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private boolean f458d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private String f459e;

    public PeSchedHandler(Looper looper, PeGameController peGameController, Context context) {
        super(looper);
        this.f456b = "";
        this.f457c = "";
        this.f458d = false;
        this.f459e = "";
        this.f455a = peGameController;
        sendMessageDelayed(obtainMessage(7), 500L);
    }

    private void i(ArrayList<String> arrayList) {
        PowerLog.v("PeGameController_PeSchedHandler", "filterSpecailCMD start cmds.size:" + arrayList.size());
        if (arrayList.size() == 0) {
            return;
        }
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            String str = arrayList.get(size);
            if (str.startsWith("specail_cmd_set_aal_by_pass")) {
                if (SystemProperties.getBoolean("ro.vendor.display.benchmark_app", false)) {
                    String str2 = str.split("#")[1];
                    if (str2.equals("0")) {
                        DisplayFeatureManager.getInstance().setScreenEffect(40, 0, -1);
                    } else if (str2.equals("1")) {
                        DisplayFeatureManager.getInstance().setScreenEffect(40, 1, -1);
                    }
                    PowerLog.v("PeGameController_PeSchedHandler", "filterSpecailCMD start with specail_cmd_set_aal_by_pass :" + str2);
                    arrayList.remove(size);
                } else {
                    PowerLog.v("PeGameController_PeSchedHandler", "filterSpecailCMD start with specail_cmd_set_aal_by_pass, ro.vendor.display.benchmark_app false, continue");
                }
            }
        }
        PowerLog.v("PeGameController_PeSchedHandler", "filterSpecailCMD end cmds.size:" + arrayList.size());
    }

    private void j(ArrayList<String> arrayList) throws Throwable {
        StringBuilder sb;
        if (arrayList.size() == 0) {
            return;
        }
        FileOutputStream fileOutputStream = null;
        try {
            try {
                File file = new File("/data/system/whetstone/perf_data");
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                try {
                    for (String str : arrayList) {
                        if (PowerLog.V) {
                            PowerLog.v("PeGameController_PeSchedHandler", "optSched cmds: " + str);
                        }
                        fileOutputStream2.write((str + Constants.ENDLINE).getBytes());
                    }
                    try {
                        fileOutputStream2.close();
                    } catch (IOException e2) {
                        e = e2;
                        sb = new StringBuilder();
                        sb.append("optSched IOexception : ");
                        sb.append(e.toString());
                        Log.e("PeGameController_PeSchedHandler", sb.toString());
                    }
                } catch (Exception e3) {
                    e = e3;
                    fileOutputStream = fileOutputStream2;
                    Log.e("PeGameController_PeSchedHandler", e.toString());
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e4) {
                            e = e4;
                            sb = new StringBuilder();
                            sb.append("optSched IOexception : ");
                            sb.append(e.toString());
                            Log.e("PeGameController_PeSchedHandler", sb.toString());
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e5) {
                            Log.e("PeGameController_PeSchedHandler", "optSched IOexception : " + e5.toString());
                        }
                    }
                    throw th;
                }
            } catch (Exception e6) {
                e = e6;
            }
            PerfUtils.c("/data/system/whetstone/perf_data");
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void a(ComponentName componentName) {
        sendMessage(obtainMessage(6, componentName));
    }

    public void b(ComponentName componentName) {
        sendMessage(obtainMessage(5, componentName));
    }

    public void c(ComponentName componentName) {
        sendMessage(obtainMessage(4, componentName));
    }

    public void d(String str, String str2) {
        if (PowerLog.V) {
            PowerLog.v("PeGameController_PeSchedHandler", "PeSchedHandler::appendForgroundActivityChange");
        }
        if (hasMessages(11)) {
            removeMessages(11);
        }
        Bundle bundle = new Bundle();
        bundle.putString("pkg", str);
        bundle.putString("activity", str2);
        sendMessage(obtainMessage(11, bundle));
    }

    public void e(String str, String str2, long j2) {
        if (PowerLog.V) {
            PowerLog.v("PeGameController_PeSchedHandler", "PeSchedHandler::appendForgroundActivityChangeDelayed");
        }
        Bundle bundle = new Bundle();
        bundle.putString("pkg", str);
        bundle.putString("activity", str2);
        sendMessageDelayed(obtainMessage(11, bundle), j2);
    }

    public void f(List<String> list) {
        sendMessage(obtainMessage(0, list));
    }

    public void g(List<String> list) {
        sendMessage(obtainMessage(1, list));
    }

    public void h(int i2) {
        sendMessage(obtainMessage(3, Integer.valueOf(i2)));
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        if (PowerLog.V) {
            PowerLog.v("PeGameController_PeSchedHandler", "handleMessage msg.what:" + message.what);
        }
        int i2 = message.what;
        if (i2 == -22) {
            int i3 = message.arg1;
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "COMMON_MSG_POWER_MODE_CHANGED: " + i3);
            }
            this.f455a.d0(i3);
            return;
        }
        if (i2 == 7) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "MSG_INIT");
            }
            PeGameController.m0 = DisplayFrameSetting.isFeatureOn();
            if (DisplayFrameSetting.getDFSInstance() != null) {
                PeGameController.l0 = DisplayFrameSetting.getDFSInstance().getUserFps();
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_PeSchedHandler", "MSG_INIT sFPS:" + PeGameController.l0);
                    return;
                }
                return;
            }
            return;
        }
        if (i2 == 0) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "MSG_PERF ++++++++++");
            }
            ArrayList<String> arrayList = (ArrayList) message.obj;
            if (arrayList != null && arrayList.size() > 0) {
                i(arrayList);
            }
            if (arrayList == null || arrayList.size() <= 0) {
                return;
            }
            j(arrayList);
            return;
        }
        if (i2 == 1) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "MSG_RESTORE ----------");
            }
            ArrayList<String> arrayList2 = (ArrayList) message.obj;
            if (arrayList2 != null && arrayList2.size() > 0) {
                i(arrayList2);
            }
            if (arrayList2 != null && arrayList2.size() > 0) {
                j(arrayList2);
            }
            try {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_PeSchedHandler", "handle MSG_RESTORE sleep 1000ms");
                }
                Thread.sleep(1000L);
                return;
            } catch (InterruptedException unused) {
                return;
            }
        }
        if (i2 == 2) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "PeSchedHandler::case MSG_FG_CHANGE");
                return;
            }
            return;
        }
        if (i2 == 3) {
            int iIntValue = ((Integer) message.obj).intValue();
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "MSG_SCREEN_TOGGLE screen: " + iIntValue + ", mFGPkg:" + this.f456b);
            }
            if (iIntValue != 0) {
                this.f458d = false;
                return;
            }
            this.f458d = true;
            this.f455a.C("key_guard");
            this.f456b = "key_guard";
            return;
        }
        if (i2 == 4) {
            ComponentName componentName = (ComponentName) message.obj;
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "MSG_ACTIVITY_RESUME componentName: " + componentName.toString() + ", mFGPkg:" + this.f456b);
            }
            if (this.f458d) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_PeSchedHandler", "Screen off, return MSG_ACTIVITY_RESUME");
                    return;
                }
                return;
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "Unlock Screen on, go on MSG_ACTIVITY_RESUME");
            }
            if ("key_guard".equals(this.f456b)) {
                new ForegroundInfo();
                this.f455a.B(this.f459e, "");
                this.f455a.C(componentName.getPackageName());
                this.f456b = componentName.getPackageName();
                return;
            }
            return;
        }
        if (i2 != 11) {
            if (i2 != 12) {
                return;
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "PeSchedHandler::case MSG_GET_KEY_LOG");
            }
            String str = (String) message.obj;
            if (TextUtils.isEmpty(str)) {
                return;
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "PeSchedHandler::case MSG_GET_KEY_LOG:" + str);
            }
            this.f455a.B("", str);
            return;
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController_PeSchedHandler", "PeSchedHandler::case MSG_FG_ACTIVITY_CHANGE");
        }
        Bundle bundle = (Bundle) message.obj;
        String string = bundle.getString("pkg", "");
        String string2 = bundle.getString("activity", "");
        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2)) {
            return;
        }
        this.f459e = string2;
        if (PowerLog.V) {
            PowerLog.v("PeGameController_PeSchedHandler", "PeSchedHandler::case MSG_FG_ACTIVITY_CHANGE:" + string + "/" + string2);
        }
        this.f455a.B(string2, "");
        if (this.f456b.equals(string)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "MSG_FG_ACTIVITY_CHANGE mFGPkg:" + this.f456b + ", not change");
                return;
            }
            return;
        }
        if (this.f458d) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PeSchedHandler", "ScreenOff MSG_FG_ACTIVITY_CHANGE ,ignore mFGPkg:" + this.f456b);
                return;
            }
            return;
        }
        this.f456b = string;
        if (PowerLog.V) {
            PowerLog.v("PeGameController_PeSchedHandler", "ScreenOn MSG_FG_ACTIVITY_CHANGE mFGPkg:" + this.f456b);
        }
        this.f455a.C(string);
    }
}
