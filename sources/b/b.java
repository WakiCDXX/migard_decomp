package b;

import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.cloudcontrol.LocalUpdateUtils;
import com.miui.powerkeeper.powerchecker.PowerCheckerCloudConfigure;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.statemachine.PowerSaveModeHandler;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.DimController;
import java.util.ArrayList;

/* JADX INFO: compiled from: SubModule.java */
/* JADX INFO: loaded from: classes.dex */
public class b {
    public static final int A;
    public static final int B;
    public static final int C;
    public static final int D;
    public static final int E;
    public static final int F;
    public static final int G;
    public static final int H;
    public static final int I;
    public static final int J;
    public static final int K;
    public static final int L;
    public static final int M;
    public static final int N;
    public static final int O;
    public static final int P;
    public static final int Q;
    public static final int R;
    public static final int S;
    public static final int T;
    public static final int U;
    public static final int V;
    public static final int W;
    public static final ArrayList<String> X;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static int f19a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f20b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final int f21c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final int f22d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final int f23e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final int f24f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final int f25g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final int f26h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final int f27i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final int f28j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    public static final int f29k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public static final int f30l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final int f31m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final int f32n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final int f33o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final int f34p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final int f35q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final int f36r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public static final int f37s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public static final int f38t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public static final int f39u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    public static final int f40v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    public static final int f41w;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    public static final int f42x;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public static final int f43y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public static final int f44z;

    static {
        int i2 = (-1) + 1;
        f20b = i2;
        int i3 = i2 + 1;
        f21c = i3;
        int i4 = i2 + 2;
        f22d = i4;
        int i5 = i2 + 3;
        f23e = i5;
        int i6 = i2 + 4;
        f24f = i6;
        int i7 = i2 + 5;
        f25g = i7;
        int i8 = i2 + 6;
        f26h = i8;
        int i9 = i2 + 7;
        f27i = i9;
        int i10 = i2 + 8;
        f28j = i10;
        int i11 = i2 + 9;
        f29k = i11;
        int i12 = i2 + 10;
        f30l = i12;
        int i13 = i2 + 11;
        f31m = i13;
        int i14 = i2 + 12;
        f32n = i14;
        int i15 = i2 + 13;
        f33o = i15;
        int i16 = i2 + 14;
        f34p = i16;
        int i17 = i2 + 15;
        f35q = i17;
        int i18 = i2 + 16;
        f36r = i18;
        int i19 = i2 + 17;
        f37s = i19;
        int i20 = i2 + 18;
        f38t = i20;
        int i21 = i2 + 19;
        f39u = i21;
        int i22 = i2 + 20;
        f40v = i22;
        int i23 = i2 + 21;
        f41w = i23;
        int i24 = i2 + 22;
        f42x = i24;
        int i25 = i2 + 23;
        f43y = i25;
        int i26 = i2 + 24;
        f44z = i26;
        int i27 = i2 + 25;
        A = i27;
        int i28 = i2 + 26;
        B = i28;
        int i29 = i2 + 27;
        C = i29;
        int i30 = i2 + 28;
        D = i30;
        int i31 = i2 + 29;
        E = i31;
        int i32 = i2 + 30;
        F = i32;
        int i33 = i2 + 31;
        G = i33;
        int i34 = i2 + 32;
        H = i34;
        int i35 = i2 + 33;
        I = i35;
        int i36 = i2 + 34;
        J = i36;
        int i37 = i2 + 35;
        K = i37;
        int i38 = i2 + 36;
        L = i38;
        int i39 = i2 + 37;
        M = i39;
        int i40 = i2 + 38;
        N = i40;
        int i41 = i2 + 39;
        O = i41;
        int i42 = i2 + 40;
        P = i42;
        int i43 = i2 + 41;
        Q = i43;
        int i44 = i2 + 42;
        R = i44;
        int i45 = i2 + 43;
        S = i45;
        int i46 = i2 + 44;
        T = i46;
        int i47 = i2 + 45;
        U = i47;
        int i48 = i2 + 46;
        f19a = i48;
        V = i48;
        W = i48;
        ArrayList<String> arrayList = new ArrayList<>();
        X = arrayList;
        arrayList.add(i2, BLUtils.CLOUD_BATTERY_LIFE);
        arrayList.add(i3, DimController.CLOUD_DIM_CONFIG);
        arrayList.add(i4, BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE);
        arrayList.add(i5, "freeze_config");
        arrayList.add(i6, "cabc_group");
        arrayList.add(i7, "custom_switch");
        arrayList.add(i8, "fps_tp_exclude");
        arrayList.add(i9, "note_pkg");
        arrayList.add(i10, "display_fps_group");
        arrayList.add(i11, "fps_idle_config");
        arrayList.add(i12, "fps_group_config");
        arrayList.add(i13, "perf_engine_config");
        arrayList.add(i14, "smart_backlight_mode");
        arrayList.add(i15, "eye_protection_mode");
        arrayList.add(i16, "violent_mode_switch");
        arrayList.add(i17, "dynamic_turbo_power");
        arrayList.add(i18, "power_mode_optimization");
        arrayList.add(i19, Constant.FUNC_SCREEN_OFF_CLEAN_APP);
        arrayList.add(i20, Constant.FUNC_NIGHT_CLEAN_PROCESS);
        arrayList.add(i21, "clear_unactive_apps");
        arrayList.add(i22, SimpleSettingKeys.KEY_AUDIO_ACTIVE_CONTROL);
        arrayList.add(i23, PowerCheckerCloudConfigure.CUSTOMER_POWER_CONTROL);
        arrayList.add(i24, "checker_app_list");
        arrayList.add(i25, "checker_detect_param");
        arrayList.add(i26, "thermal_collectors");
        arrayList.add(i27, Constants.BUCKET_NAME_THERMAL_CONFIG_PATH);
        arrayList.add(i28, SimpleSettingKeys.CLOUD_THERMAL_COMMON);
        arrayList.add(i29, "thermal_scenario_switch");
        arrayList.add(i30, "app_list_controller");
        arrayList.add(i31, "union_power_base");
        arrayList.add(i32, "union_power_special");
        arrayList.add(i33, "union_power_other");
        arrayList.add(i34, "modules_controller_list");
        arrayList.add(i35, "kill_app_switch");
        arrayList.add(i36, "iris_x7_video_params");
        arrayList.add(i37, "sleep_mode_config");
        arrayList.add(i38, Constant.FUNC_SLEEP_MODE_CLOUD);
        arrayList.add(i39, SimpleSettingKeys.KEY_EVENT_NOTIFY_CONTROL);
        arrayList.add(i40, Constant.FUNC_DEEP_SLEEP_MODE_CLOUD);
        arrayList.add(i41, "dfs_power_config");
        arrayList.add(i42, "dfs_check_list");
        arrayList.add(i43, LocalUpdateUtils.DOZE_WHITELIST_APPS);
        arrayList.add(i44, "debug_node_config");
        arrayList.add(i45, "satellite_telephony_controller");
        arrayList.add(i46, Constant.FUNC_ALARM_CONTROL);
        arrayList.add(i47, "soc_platform_optimization");
        arrayList.add(i48, PowerSaveModeHandler.CONTENT_KEY);
    }
}
