package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.LocalLog;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.CommonAdapter;
import com.miui.powerkeeper.utils.PowerModeAdapter;
import com.miui.powerkeeper.utils.Utils;
import com.miui.whetstone.IPowerKeeperPolicy;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import miui.process.ForegroundInfo;
import miui.util.ITouchFeature;
import miui.util.ReflectionUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerModeHandler extends Handler {
    private static final String CLOUD_POWER_MODE_OPTIMIZATION = "power_mode_optimization";
    private static final String FLAG_HARMANKARDON_SWITCH = "flag_harmankardon_switch";
    public static final int POWER_MODE_ENTER_SAVE = 1;
    public static final int POWER_MODE_QUIT_SAVE = 3;
    public static final String SETTINGS_SYSTEM_HARMAN_KARDON_ENABLE = "settings_system_harman_kardon_enable";
    private static final String TAG = "PowerModeHandler";
    private static final String THERMAL_BALANCE_MODE = "/sys/class/thermal/thermal_message/balance_mode";
    private static b.a mCloudObserver;
    private static String mForegroundApplicationPackageName;
    private static String[] mGamePkgName;
    private static ITouchFeature mTouchFeature;
    private static PowerModeHandler sInstance;
    private final boolean DBG_CPU;
    private final int MSG_CLOUD_UPDATE;
    private Context mContext;
    private String mDefalutMode;
    private ForegroundInfo mForegroundInfo;
    private String[] mGpsPkgName;
    private boolean mHarmanKardon;
    private boolean mIsBackgroundresidencyOn;
    private boolean mIsBacklightOn;
    private boolean mIsBoostOn;
    private boolean mIsHarmanKardonOn;
    private boolean mIsTpOn;
    private LocalLog mLocalLog;
    private int mPowerMode;
    public static String[] mDeviceArray = {"cetus", "odin", "cmi", "cas", "lmi", "umi", "apollo", "thyme", "alioth", "star", "mars", "venus", "haydn", "ginkgo", "lime", "cannon", "camellia", "picasso", "gauguin", "chopin", "pissarro", "pissarropro", "psyche", "cezanne", "willow", "laurus", "atom", "bomb", "elish", "aresin", "enuma", "ares", "renoir", "vili", "selene", "agate", "iris", "lisa", "mona", "evergo", "merlin", "eos", "aliothin", "sweet", "lime", "tucana", "dandelion", "toco", "surya", "olive", "olivelite", "pine", "phoenix", "onc", "lavender", "violet", "olivewood", "courbet", "mojito", "vayu", "pissarroin", "pissarroinpro", "amber", "ingres", "zeus", "cupid", "matisse", "rubens", "angelica", "angelican", "angelicain", "cattail", "galahad", "haydnin", "phoenixin", "onclite", "rainbow", "citrus", "cannong", "camellian", "vangogh", "monet", "lemon", "pomelo", "bhima", "sunny", "karna", "curtana", "excalibur", "joyeuse", "gram", "sweetin", "merlinnfc", "evergreen", "gauguinpro", "gauguininpro", "lmipro", "lmiin", "lmiinpro", "munch", "thor", "veux", "peux", "light", "thunder", "zizhan", "xaga", "xagapro", "unicorn", "daumier", "diting", "plato", "mayfly", "nuwa", "fuxi", "mondrian", "socrates", "lilac", "marble", "sunstone", "ishtar", "pearl", "rembrandt", "ruby", "babylon", "corot", "aristotle", "XIG04", "rubyplus", "redwood", "shennong", "houji", "garnet", "zircon", "manet", "vermeer", "duchamp", "aurora", "XIG05", "chenfeng", "ruyi", "goku", "peridot", "rothko", "degas", "dada", "haotian", "miro", "zorn", "rodin", "amethyst", "malachite", "dijun", "xuanyuan"};
    private static int mThermalId = -1;
    private static int mLastTouch = -1;
    private static int mGameEnhance = 0;
    private static int mGamePerformance = 1;
    private static boolean mTouchReportRate = false;

    private PowerModeHandler(Context context, Looper looper) {
        super(looper);
        this.DBG_CPU = Log.isLoggable("power.mode", 3);
        this.MSG_CLOUD_UPDATE = 1;
        this.mGpsPkgName = PowerModeAdapter.mGpsPkgName;
        this.mForegroundInfo = null;
        this.mLocalLog = new LocalLog(128);
        this.mPowerMode = 2;
        this.mIsBacklightOn = false;
        this.mIsBackgroundresidencyOn = false;
        this.mIsTpOn = false;
        this.mIsHarmanKardonOn = false;
        this.mIsBoostOn = false;
        this.mHarmanKardon = false;
        Log.i(TAG, "PowerModeHandler constructive");
        this.mContext = context;
        this.mHarmanKardon = SimpleSettings.Misc.getBoolean(context, FLAG_HARMANKARDON_SWITCH, false);
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            this.mForegroundInfo = eventsAggregator.getForegroundInfo();
            eventsAggregator.registerForForegroundInfo(this);
            eventsAggregator.registerPowerModeListener(this);
        }
        registerCloudObserver();
        updateConfig();
        mTouchFeature = ITouchFeature.getInstance();
    }

    static PowerModeHandler getInstance() {
        return sInstance;
    }

    public static void getThermalId(int i2) {
        if (mThermalId == i2 || mTouchFeature == null) {
            return;
        }
        mThermalId = i2;
        Log.d(TAG, "mThermalId =" + mThermalId);
        if (mThermalId != 16) {
            setTouchReportRate(mGameEnhance);
        }
        if (mThermalId == 16 && ArrayUtils.contains(mGamePkgName, mForegroundApplicationPackageName)) {
            setTouchReportRate(mGamePerformance);
        }
    }

    public static synchronized PowerModeHandler init(Context context, Looper looper) {
        try {
            if (!isSupportDevice()) {
                Log.i(TAG, "PowerModeHandler init failed");
                return null;
            }
            if (sInstance == null) {
                sInstance = new PowerModeHandler(context, looper);
            }
            return sInstance;
        } catch (Throwable th) {
            throw th;
        }
    }

    public static boolean isSupportDevice() {
        for (String str : mDeviceArray) {
            if (Utils.isBelongToDeviceSeries(str)) {
                return true;
            }
        }
        return false;
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        if (!isSupportDevice() || mCloudObserver == null) {
            Log.i(TAG, "power mode onCloudFunctionUpdated failed");
            return;
        }
        if (isSupportDevice() && !mCloudObserver.s(b.b.f36r)) {
            String strOptString = jSONObject.optString(SimpleSettingKeys.KEY_DEFAULT_POWER_MODE);
            Log.i(TAG, "defalutMode newCloud is " + strOptString);
            if ("disable".equals(strOptString)) {
                SimpleSettings.Misc.delete(context, SimpleSettingKeys.KEY_DEFAULT_POWER_MODE);
                SystemProperties.set("persist.sys.power.default.powermode", "");
            } else if (isSupportDevice() || "enhance".equals(strOptString)) {
                SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_DEFAULT_POWER_MODE, "enhance");
                SystemProperties.set("persist.sys.power.default.powermode", "enhance");
            }
        }
        if (!isSupportDevice() || mCloudObserver.s(b.b.f36r)) {
            return;
        }
        String strOptString2 = jSONObject.optString(CLOUD_POWER_MODE_OPTIMIZATION, "");
        String string = SimpleSettings.Misc.getString(context, CLOUD_POWER_MODE_OPTIMIZATION, "");
        Log.i(TAG, "powerModeOptimization newCloud is " + strOptString2 + ", oldCloud is " + string);
        if (strOptString2.isEmpty() || strOptString2.equals(string)) {
            return;
        }
        SimpleSettings.Misc.putString(context, CLOUD_POWER_MODE_OPTIMIZATION, strOptString2);
        PowerModeHandler powerModeHandler = sInstance;
        if (powerModeHandler != null) {
            powerModeHandler.sendMessage(powerModeHandler.obtainMessage(1, strOptString2));
        }
    }

    private void registerCloudObserver() {
        b.a aVarP = b.a.p();
        mCloudObserver = aVarP;
        int i2 = b.b.f36r;
        aVarP.t(i2, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.PowerModeHandler.1
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                try {
                    Log.i(PowerModeHandler.TAG, "Cloud-Observer onChange - power mode object is " + jSONObject);
                    if (jSONObject != null) {
                        PowerModeHandler.this.updateConfigFromSettingDB(jSONObject);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
        Log.i(TAG, "Cloud-Observer isNewCloudConfigured is " + mCloudObserver.s(i2));
        if (mCloudObserver.s(i2)) {
            return;
        }
        this.mContext.getContentResolverForUser(UserHandle.SYSTEM).registerContentObserver(SimpleSettings.Misc.getUriFor(CLOUD_POWER_MODE_OPTIMIZATION), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.PowerModeHandler.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                PowerModeHandler.this.updateConfig();
            }
        });
    }

    private void setHarmanKardon(boolean z2) {
        if (this.mIsHarmanKardonOn) {
            int i2 = Settings.Global.getInt(this.mContext.getContentResolver(), SETTINGS_SYSTEM_HARMAN_KARDON_ENABLE, 2);
            boolean z3 = i2 == 1 || i2 == 2;
            if (z2 && z3) {
                writeLocalLog("Close HarmanKardon Audio");
                Settings.Global.putInt(this.mContext.getContentResolver(), SETTINGS_SYSTEM_HARMAN_KARDON_ENABLE, 0);
                this.mHarmanKardon = z3;
                SimpleSettings.Misc.putBoolean(this.mContext, FLAG_HARMANKARDON_SWITCH, z3);
                return;
            }
            if (z2 || z3 || !this.mHarmanKardon) {
                return;
            }
            writeLocalLog("Open HarmanKardon Audio");
            Settings.Global.putInt(this.mContext.getContentResolver(), SETTINGS_SYSTEM_HARMAN_KARDON_ENABLE, 1);
            this.mHarmanKardon = z3;
            SimpleSettings.Misc.putBoolean(this.mContext, FLAG_HARMANKARDON_SWITCH, z3);
        }
    }

    private void setPowerModeState(int i2) throws Throwable {
        setTouchFeature(i2);
        switchProcessManager(i2);
        writeTpBalanceMode(i2);
        if (i2 == 3) {
            setHarmanKardon(true);
        } else {
            setHarmanKardon(false);
        }
        if (i2 > 1) {
            updateBacklightMode(true);
        } else {
            updateBacklightMode(false);
        }
    }

    private void setTouchFeature(int i2) {
        if (this.mIsTpOn) {
            int i3 = i2 <= 1 ? 3 : 1;
            writeLocalLog("mTouchFeature power_level =" + i3);
            CommonAdapter.setTouch(mTouchFeature, i3);
        }
    }

    private static void setTouchReportRate(int i2) {
        if (mTouchReportRate) {
            int i3 = (i2 == 1 && mThermalId == 16) ? 1 : 0;
            if (mLastTouch == i3) {
                return;
            }
            mLastTouch = i3;
            Log.i(TAG, "ReportRate touch =" + i3);
            CommonAdapter.setTouchPower(mTouchFeature, i3);
        }
    }

    private void switchProcessManager(int i2) {
        int i3;
        if (this.mIsBackgroundresidencyOn) {
            Class clsTryFindClass = ReflectionUtils.tryFindClass("miui.process.ProcessManager", ClassLoader.getSystemClassLoader());
            Field fieldTryFindField = ReflectionUtils.tryFindField(clsTryFindClass, "POWER_POLICY_SAVING_MODE");
            Field fieldTryFindField2 = ReflectionUtils.tryFindField(clsTryFindClass, "POWER_POLICY_EQUILIBRIA_MODE");
            Field fieldTryFindField3 = ReflectionUtils.tryFindField(clsTryFindClass, "POWER_POLICY_PERFORMANCE_MODE");
            try {
                if (i2 == 1) {
                    i3 = fieldTryFindField3.getInt(null);
                } else {
                    if (i2 != 2) {
                        if (i2 != 3) {
                            i3 = 0;
                        } else {
                            i3 = fieldTryFindField.getInt(null);
                        }
                        ReflectionUtils.tryCallStaticMethod(clsTryFindClass, "switchPowerPolicyMode", Void.class, new Object[]{Integer.valueOf(i3)});
                        writeLocalLog("switchPowerPolicyMode: mode =" + i3);
                    }
                    i3 = fieldTryFindField2.getInt(null);
                }
                ReflectionUtils.tryCallStaticMethod(clsTryFindClass, "switchPowerPolicyMode", Void.class, new Object[]{Integer.valueOf(i3)});
                writeLocalLog("switchPowerPolicyMode: mode =" + i3);
            } catch (Exception unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConfig() {
        b.a aVar = mCloudObserver;
        int i2 = b.b.f36r;
        if (aVar.s(i2)) {
            try {
                JSONObject jSONObjectL = mCloudObserver.l(i2);
                Log.i(TAG, "updateConfig - power mode object is " + jSONObjectL);
                if (jSONObjectL != null) {
                    updateConfigFromSettingDB(jSONObjectL);
                    return;
                }
                return;
            } catch (Exception e2) {
                Log.e(TAG, "updateConfig error", e2);
                return;
            }
        }
        String string = SimpleSettings.Misc.getString(this.mContext, CLOUD_POWER_MODE_OPTIMIZATION, "");
        if (string.isEmpty()) {
            if (this.DBG_CPU) {
                Log.d(TAG, "updateConfig empty cloud");
                return;
            }
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(string);
            this.mIsBacklightOn = jSONObject.optBoolean("backlight", false);
            this.mIsBackgroundresidencyOn = jSONObject.optBoolean("background_residency", false);
            this.mIsTpOn = jSONObject.optBoolean("tp", false);
            this.mIsHarmanKardonOn = jSONObject.optBoolean("harman_kardon", false);
            this.mIsBoostOn = jSONObject.optBoolean("boost", false);
            mTouchReportRate = jSONObject.optBoolean("touch_report_rate", false);
            mGamePkgName = jSONObject.optString("touch_game_performance", "").split(",");
        } catch (JSONException e3) {
            Log.e(TAG, "updateConfig error", e3);
        }
        Log.i(TAG, "updateConfig newCloud = " + string + " mIsBacklightOn = " + this.mIsBacklightOn + " mIsBackgroundresidencyOn = " + this.mIsBackgroundresidencyOn + " mIsTpOn = " + this.mIsTpOn + " mIsHarmanKardonOn = " + this.mIsHarmanKardonOn + " mIsBoostOn = " + this.mIsBoostOn + " mTouchReportRate = " + mTouchReportRate + " mGamePkgName = " + Arrays.toString(mGamePkgName));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConfigFromSettingDB(JSONObject jSONObject) {
        this.mIsBacklightOn = jSONObject.optBoolean("backlight", false);
        this.mIsBackgroundresidencyOn = jSONObject.optBoolean("background_residency", false);
        this.mIsTpOn = jSONObject.optBoolean("tp", false);
        this.mIsHarmanKardonOn = jSONObject.optBoolean("harman_kardon", false);
        this.mIsBoostOn = jSONObject.optBoolean("boost", false);
        mTouchReportRate = jSONObject.optBoolean("touch_report_rate", false);
        mGamePkgName = jSONObject.optString("touch_game_performance", "").split(",");
        this.mDefalutMode = jSONObject.optString(SimpleSettingKeys.KEY_DEFAULT_POWER_MODE);
        Log.i(TAG, "updateConfigFromSettingDB - defalutMode is " + this.mDefalutMode);
        if ("disable".equals(this.mDefalutMode)) {
            SystemProperties.set("persist.sys.power.default.powermode", "");
        } else if (isSupportDevice() || "enhance".equals(this.mDefalutMode)) {
            this.mDefalutMode = "enhance";
            SystemProperties.set("persist.sys.power.default.powermode", "enhance");
        }
        Log.i(TAG, "Cloud-Observer mIsBacklightOn=" + this.mIsBacklightOn + " mIsBackgroundresidencyOn = " + this.mIsBackgroundresidencyOn + " mIsTpOn = " + this.mIsTpOn + " mIsHarmanKardonOn = " + this.mIsHarmanKardonOn + " mIsBoostOn = " + this.mIsBoostOn + " mTouchReportRate = " + mTouchReportRate + " mGamePkgName = " + Arrays.toString(mGamePkgName));
    }

    private void writeLocalLog(String str) {
        Log.i(TAG, str);
        this.mLocalLog.log(str);
    }

    private void writeTpBalanceMode(int i2) throws Throwable {
        if (!this.mIsBoostOn) {
            return;
        }
        File file = new File(THERMAL_BALANCE_MODE);
        if (!file.exists()) {
            return;
        }
        FileOutputStream fileOutputStream = null;
        try {
            try {
                try {
                    FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                    try {
                        String string = Integer.toString(i2);
                        writeLocalLog("writeTpBalanceMode tp =" + i2);
                        fileOutputStream2.write(string.getBytes());
                        fileOutputStream2.close();
                    } catch (Exception e2) {
                        e = e2;
                        fileOutputStream = fileOutputStream2;
                        Log.e(TAG, "io failed to write", e);
                        if (fileOutputStream == null) {
                        } else {
                            fileOutputStream.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        fileOutputStream = fileOutputStream2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e3) {
                                Log.e(TAG, "io failed to close", e3);
                            }
                        }
                        throw th;
                    }
                } catch (Exception e4) {
                    e = e4;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e5) {
            Log.e(TAG, "io failed to close", e5);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump PowerModeHandler mIsBacklightOn = " + this.mIsBacklightOn + " mIsBackgroundresidencyOn = " + this.mIsBackgroundresidencyOn + " mIsTpOn = " + this.mIsTpOn + " mIsHarmanKardonOn = " + this.mIsHarmanKardonOn + " mIsBoostOn = " + this.mIsBoostOn + " mTouchReportRate = " + mTouchReportRate + " mGamePkgName = " + Arrays.toString(mGamePkgName) + " mGpsPkgName = " + Arrays.toString(this.mGpsPkgName));
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        int i2 = message.what;
        if (i2 == -22) {
            this.mPowerMode = message.arg1;
            writeLocalLog("powerMode =" + this.mPowerMode);
            setPowerModeState(this.mPowerMode);
            return;
        }
        if (i2 != -9) {
            if (i2 != 1) {
                Log.w(TAG, "warning for access here");
                return;
            }
            if (this.DBG_CPU) {
                Log.d(TAG, "MSG_CONFIG_UPDATE " + message.obj);
            }
            updateConfig();
            setPowerModeState(this.mPowerMode);
            return;
        }
        ForegroundInfo foregroundInfo = (ForegroundInfo) message.obj;
        this.mForegroundInfo = foregroundInfo;
        if (foregroundInfo == null) {
            Log.e(TAG, "foregroundInfo is null");
            return;
        }
        String str = foregroundInfo.mForegroundPackageName;
        mForegroundApplicationPackageName = str;
        if (ArrayUtils.contains(mGamePkgName, str)) {
            writeLocalLog("GamePkgName = " + this.mForegroundInfo.mForegroundPackageName);
            setTouchReportRate(mGamePerformance);
        } else {
            setTouchReportRate(mGameEnhance);
        }
        if (this.mPowerMode == 2 && ArrayUtils.contains(this.mGpsPkgName, this.mForegroundInfo.mForegroundPackageName)) {
            setHarmanKardon(true);
        }
        if (this.mPowerMode != 2 || ArrayUtils.contains(this.mGpsPkgName, this.mForegroundInfo.mForegroundPackageName)) {
            return;
        }
        setHarmanKardon(false);
    }

    public void updateBacklightMode(boolean z2) {
        if (this.mIsBacklightOn) {
            try {
                Method declaredMethod = IPowerKeeperPolicy.class.getDeclaredMethod("isBacklightEnable", Boolean.TYPE);
                IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
                if (iWhetstoneActivityManagerAsInterface == null) {
                    writeLocalLog("updateBacklightMode return for mWSProxy");
                    return;
                }
                IPowerKeeperPolicy powerKeeperPolicy = iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy();
                if (powerKeeperPolicy == null) {
                    writeLocalLog("updateBacklightMode return for null instance");
                    return;
                }
                declaredMethod.invoke(powerKeeperPolicy, Boolean.valueOf(z2));
                writeLocalLog("updateBacklightMode enable =" + z2);
            } catch (Exception e2) {
                writeLocalLog("updateBacklightMode excecption e=" + e2);
            }
        }
    }
}
