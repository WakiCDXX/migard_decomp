package com.miui.powerkeeper.statemachine;

import android.content.ComponentName;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.LocalLog;
import android.util.Log;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.MultiScreenObserver;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import miui.process.IActivityChangeListener;
import miui.process.ProcessManager;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class SocOptimizationHandler extends Handler {
    private static final String CLOUD_SOC_PLATFORM_OPTIMIZATION = "soc_platform_optimization";
    private static final int DELAY_TIME = 50;
    private static final String TAG = "SocOptimizationHandler";
    private static b.a mCloudObserver;
    private static SocOptimizationHandler sInstance;
    private final int APP_DEFAULT;
    private final int BROWSING;
    private final int IM;
    private final int MSG_CONFIG_UPDATE;
    private final int MSG_MULTI_SCREEN_CHANGE;
    private final int MSG_ROTATION_CHANGE;
    private final int READING;
    private final int VENDOR_SCENARIO_CLASSIFICATION_POWER_HINT_ID;
    private final int VIDEO_CALL;
    private final int VIDEO_DEFAULT;
    private IActivityChangeListener.Stub mActListener;
    private ArrayMap<String, Integer> mActPerflockMap;
    private ArrayList<Integer> mArr;
    private Context mContext;
    private MyCoreObserver mCoreObserver;
    private String mForegroundApplicationActivityName;
    private String mForegroundApplicationPackageName;
    private boolean mIsFeatureOn;
    private boolean mIsMultiScreen;
    private int mLastType;
    private LocalLog mLocalLog;
    private List<String> mPkgList;
    private ArrayMap<String, Integer> mPkgPerflockMap;
    private h0.a mPowerModule;
    private RotationWatcher mRotationWatcher;
    Runnable mRunnable;
    private IWindowManager mWindowManager;

    private class MyCoreObserver extends MultiScreenObserver {
        @Override // com.miui.powerkeeper.utils.MultiScreenObserver
        protected void onMultiScreenChanged(boolean z2) {
            SocOptimizationHandler socOptimizationHandler = SocOptimizationHandler.sInstance;
            if (socOptimizationHandler != null) {
                socOptimizationHandler.sendMessage(socOptimizationHandler.obtainMessage(3, Boolean.valueOf(z2)));
            }
        }

        private MyCoreObserver() {
        }
    }

    class RotationWatcher extends IRotationWatcher.Stub {
        RotationWatcher() {
        }

        public void onRotationChanged(int i2) throws RemoteException {
            SocOptimizationHandler socOptimizationHandler = SocOptimizationHandler.sInstance;
            if (socOptimizationHandler != null) {
                socOptimizationHandler.sendMessage(socOptimizationHandler.obtainMessage(2, Integer.valueOf(i2)));
            }
        }
    }

    private SocOptimizationHandler(Context context, Looper looper) {
        super(looper);
        this.VENDOR_SCENARIO_CLASSIFICATION_POWER_HINT_ID = 5616;
        this.MSG_CONFIG_UPDATE = 1;
        this.MSG_ROTATION_CHANGE = 2;
        this.MSG_MULTI_SCREEN_CHANGE = 3;
        this.APP_DEFAULT = 0;
        this.VIDEO_DEFAULT = 128;
        this.VIDEO_CALL = 129;
        this.IM = 130;
        this.BROWSING = 131;
        this.READING = 132;
        this.mLocalLog = new LocalLog(128);
        this.mIsFeatureOn = false;
        this.mIsMultiScreen = false;
        this.mPkgList = new ArrayList();
        this.mActPerflockMap = new ArrayMap<>();
        this.mPkgPerflockMap = new ArrayMap<>();
        this.mLastType = 0;
        this.mArr = new ArrayList<>();
        this.mRunnable = new Runnable() { // from class: com.miui.powerkeeper.statemachine.SocOptimizationHandler.1
            @Override // java.lang.Runnable
            public void run() {
                Integer num = (Integer) SocOptimizationHandler.this.mActPerflockMap.get(SocOptimizationHandler.this.mForegroundApplicationActivityName);
                Integer num2 = (Integer) SocOptimizationHandler.this.mPkgPerflockMap.get(SocOptimizationHandler.this.mForegroundApplicationPackageName);
                if (num != null && !SocOptimizationHandler.this.mIsMultiScreen) {
                    SocOptimizationHandler.this.perfHint(num.intValue());
                } else if (num2 == null || num2.intValue() == 128 || SocOptimizationHandler.this.mIsMultiScreen) {
                    SocOptimizationHandler.this.perfHint(0);
                } else {
                    SocOptimizationHandler.this.perfHint(num2.intValue());
                }
            }
        };
        this.mActListener = new IActivityChangeListener.Stub() { // from class: com.miui.powerkeeper.statemachine.SocOptimizationHandler.2
            public void onActivityChanged(ComponentName componentName, ComponentName componentName2) {
                String className = componentName2.getClassName();
                String packageName = componentName2.getPackageName();
                SocOptimizationHandler.this.mForegroundApplicationPackageName = packageName;
                SocOptimizationHandler.this.mForegroundApplicationActivityName = className;
                if (SocOptimizationHandler.this.mPkgList == null || SocOptimizationHandler.this.mActPerflockMap.size() == 0 || !SocOptimizationHandler.this.mPkgList.contains(packageName)) {
                    SocOptimizationHandler.this.perfHint(0);
                } else {
                    SocOptimizationHandler socOptimizationHandler = SocOptimizationHandler.this;
                    socOptimizationHandler.postDelayed(socOptimizationHandler.mRunnable, 50L);
                }
            }
        };
        this.mContext = context;
        this.mRotationWatcher = new RotationWatcher();
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        registerCloudObserver();
        updateConfig();
        MyCoreObserver myCoreObserver = new MyCoreObserver();
        this.mCoreObserver = myCoreObserver;
        myCoreObserver.registerFreeformCallback();
        this.mCoreObserver.registerTaskStackListener();
        registerActivityChangeListener();
        registerRotationListener();
        powerModule();
        perfHint(0);
    }

    public static synchronized SocOptimizationHandler getInstance(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new SocOptimizationHandler(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        String strOptString = jSONObject.optString(CLOUD_SOC_PLATFORM_OPTIMIZATION, "");
        if (strOptString.equals(SimpleSettings.Misc.getString(context, CLOUD_SOC_PLATFORM_OPTIMIZATION, ""))) {
            return;
        }
        SimpleSettings.Misc.putString(context, CLOUD_SOC_PLATFORM_OPTIMIZATION, strOptString);
        SocOptimizationHandler socOptimizationHandler = sInstance;
        if (socOptimizationHandler != null) {
            socOptimizationHandler.sendMessage(socOptimizationHandler.obtainMessage(1, strOptString));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void perfHint(int i2) {
        if (this.mIsFeatureOn && this.mLastType != i2) {
            this.mLastType = i2;
            try {
                h0.a aVar = this.mPowerModule;
                if (aVar != null) {
                    aVar.a(5616, "powermodule", -1, i2, this.mArr);
                    writeLocalLog("Sending hints from powermodule service, HintType is " + i2);
                }
            } catch (Exception e2) {
                Log.e(TAG, "powermodule e:" + e2.getMessage());
            }
        }
    }

    private void powerModule() {
        try {
            this.mPowerModule = h0.a.getService();
            if (Constant.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("IPowerModule getService return:");
                sb.append(this.mPowerModule != null ? "sucess" : "null");
                Log.d(TAG, sb.toString());
            }
        } catch (RemoteException e2) {
            Log.e(TAG, "IPowerModule exception:" + e2.getMessage());
        }
    }

    private void praseCloudData(JSONObject jSONObject) {
        this.mPkgList.clear();
        this.mPkgPerflockMap.clear();
        this.mActPerflockMap.clear();
        this.mIsFeatureOn = jSONObject.optBoolean("fucSwitch", false);
        String[] strArrSplit = jSONObject.optString("pkgName", "").split(",");
        String[] strArrSplit2 = jSONObject.optString("classLongVideo", "").split(",");
        String[] strArrSplit3 = jSONObject.optString("classVideo", "").split(",");
        String[] strArrSplit4 = jSONObject.optString("classVideoCall", "").split(",");
        String[] strArrSplit5 = jSONObject.optString("classIM", "").split(",");
        String[] strArrSplit6 = jSONObject.optString("classBrowsing", "").split(",");
        String[] strArrSplit7 = jSONObject.optString("classReading", "").split(",");
        String[] strArrSplit8 = jSONObject.optString("classBrowsingApk", "").split(",");
        if (strArrSplit != null) {
            Collections.addAll(this.mPkgList, strArrSplit);
            for (String str : strArrSplit2) {
                this.mPkgPerflockMap.put(str, 128);
            }
            for (String str2 : strArrSplit7) {
                this.mPkgPerflockMap.put(str2, 132);
            }
            for (String str3 : strArrSplit8) {
                this.mPkgPerflockMap.put(str3, 131);
            }
            for (String str4 : strArrSplit3) {
                this.mActPerflockMap.put(str4, 128);
            }
            for (String str5 : strArrSplit4) {
                this.mActPerflockMap.put(str5, 129);
            }
            for (String str6 : strArrSplit5) {
                this.mActPerflockMap.put(str6, 130);
            }
            for (String str7 : strArrSplit6) {
                this.mActPerflockMap.put(str7, 131);
            }
        }
    }

    private void registerActivityChangeListener() {
        IActivityChangeListener.Stub stub = this.mActListener;
        if (stub != null) {
            ProcessManager.unregisterActivityChanageListener(stub);
            ArrayList arrayList = new ArrayList();
            Iterator<String> it = this.mActPerflockMap.keySet().iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
            ProcessManager.registerActivityChangeListener(this.mPkgList, arrayList, this.mActListener);
        }
    }

    private void registerCloudObserver() {
        b.a aVarP = b.a.p();
        mCloudObserver = aVarP;
        int i2 = b.b.U;
        aVarP.t(i2, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.SocOptimizationHandler.3
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                SocOptimizationHandler socOptimizationHandler = SocOptimizationHandler.sInstance;
                if (jSONObject == null || socOptimizationHandler == null) {
                    return;
                }
                socOptimizationHandler.sendMessage(socOptimizationHandler.obtainMessage(1, jSONObject));
            }
        });
        Log.i(TAG, "Cloud-Observer isNewCloudConfigured is " + mCloudObserver.s(i2));
        if (mCloudObserver.s(i2)) {
            return;
        }
        this.mContext.getContentResolverForUser(UserHandle.SYSTEM).registerContentObserver(SimpleSettings.Misc.getUriFor(CLOUD_SOC_PLATFORM_OPTIMIZATION), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.SocOptimizationHandler.4
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                SocOptimizationHandler socOptimizationHandler = SocOptimizationHandler.this;
                socOptimizationHandler.sendMessage(socOptimizationHandler.obtainMessage(1, uri));
            }
        });
    }

    private void registerRotationListener() {
        try {
            this.mWindowManager.removeRotationWatcher(this.mRotationWatcher);
            this.mWindowManager.watchRotation(this.mRotationWatcher, 0);
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
    }

    private void updateConfig() {
        try {
            b.a aVar = mCloudObserver;
            int i2 = b.b.U;
            if (aVar.s(i2)) {
                JSONObject jSONObjectL = mCloudObserver.l(i2);
                if (Constant.DEBUG) {
                    Log.d(TAG, "updateConfig - soc platform optomization object is " + jSONObjectL);
                }
                if (jSONObjectL != null) {
                    praseCloudData(jSONObjectL);
                    return;
                }
                return;
            }
            String string = SimpleSettings.Misc.getString(this.mContext, CLOUD_SOC_PLATFORM_OPTIMIZATION, "");
            if (string.isEmpty()) {
                if (Constant.DEBUG) {
                    Log.d(TAG, "updateConfig empty cloud");
                    return;
                }
                return;
            }
            praseCloudData(new JSONObject(string));
            if (Constant.DEBUG) {
                Log.i(TAG, "updateConfig newCloud =" + string);
            }
        } catch (Exception e2) {
            Log.e(TAG, "updateConfig error", e2);
        }
    }

    private void writeLocalLog(String str) {
        if (Constant.DEBUG) {
            Log.i(TAG, str);
        }
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump SocOptimizationHandler mIsFeatureOn=" + this.mIsFeatureOn + " mPkgList =" + Arrays.asList(this.mPkgList) + " mActivityList =" + this.mActPerflockMap.keySet());
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == 1) {
            if (Constant.DEBUG) {
                Log.d(TAG, "MSG_CONFIG_UPDATE " + message.obj);
            }
            updateConfig();
            return;
        }
        if (i2 == 2) {
            if (1 != ((Integer) message.obj).intValue()) {
                perfHint(0);
                return;
            }
            Integer num = this.mPkgPerflockMap.get(this.mForegroundApplicationPackageName);
            if (num == null || num.intValue() != 128 || this.mIsMultiScreen) {
                return;
            }
            perfHint(128);
            return;
        }
        if (i2 != 3) {
            Log.w(TAG, "warning for access here");
            return;
        }
        if (!((Boolean) message.obj).booleanValue()) {
            if (this.mIsMultiScreen) {
                this.mIsMultiScreen = false;
            }
        } else {
            if (this.mIsMultiScreen) {
                return;
            }
            this.mIsMultiScreen = true;
            perfHint(0);
        }
    }
}
