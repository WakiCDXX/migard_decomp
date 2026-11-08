package com.miui.powerkeeper.unionpower.utils;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Base64;
import android.util.Log;
import b.b;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.powerkeeper.utils.AssetUtils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.json.JSONTokener;

/* JADX INFO: loaded from: classes.dex */
public class UnionPowerConfig {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final boolean f656a = Log.isLoggable("UnionPower", 3);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final boolean f657b = SystemProperties.getBoolean("persist.sys.unionpower.enable", false);

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f658c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f659d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f660e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static Map<String, String> f661f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static Object f662g;

    public enum CloudModuleEnum {
        BASE(b.E, "base", true),
        SPECIAL(b.F, "special", false),
        OTHER(b.G, "other", true);

        private final String cloudMd5Key;
        private final String localMd5Key;
        private final int module;
        private final boolean needPriority;
        private final String prefix;

        CloudModuleEnum(int i2, String str, boolean z2) {
            this.module = i2;
            this.prefix = str;
            this.localMd5Key = "local_union_pwr_" + str;
            this.cloudMd5Key = "cloud_union_pwr_" + str;
            this.needPriority = z2;
        }

        public JSONObject d(Context context) {
            if (context == null) {
                return null;
            }
            return UnionPowerConfig.c(context, this);
        }

        public String e(Context context) {
            if (context == null) {
                return null;
            }
            return SimpleSettings.Misc.getString(context, this.cloudMd5Key, "");
        }

        public String f(Context context) {
            if (context == null) {
                return null;
            }
            return SimpleSettings.Misc.getString(context, this.localMd5Key, "");
        }

        public int g() {
            return this.module;
        }

        public int h(Context context) {
            if (!this.needPriority) {
                return 0;
            }
            return SimpleSettings.Misc.getInt(context, this.prefix + "_prio", 0);
        }

        public boolean i() {
            return this.needPriority;
        }

        public void j(Context context, String str, int i2) {
            if (context == null || str == null) {
                return;
            }
            SimpleSettings.Misc.putString(context, this.cloudMd5Key, str);
            if (this.needPriority) {
                SimpleSettings.Misc.putInt(context, this.prefix + "_prio", i2);
            }
        }

        public void k(Context context, String str) {
            if (context == null || str == null) {
                return;
            }
            SimpleSettings.Misc.putString(context, this.localMd5Key, str);
        }
    }

    public enum ObserverEventEnum {
        TouchEvent,
        FgActivityEvent,
        FgComponentEvent,
        ActiveCtrlEvent,
        MultiWindowEvent,
        PowerModeEvent,
        AMProcessEvent,
        ThermalScenarioEvent
    }

    static {
        String str = Build.DEVICE;
        f658c = str;
        String str2 = str + "/intl";
        f659d = str2;
        f660e = "union_power/" + str2;
        f661f = null;
        f662g = new Object();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static JSONObject c(Context context, CloudModuleEnum cloudModuleEnum) {
        JSONObject jSONObject;
        String strFromAssetFile;
        synchronized (f662g) {
            try {
                if (f661f == null) {
                    Map<String, String> mapE = e(context);
                    f661f = mapE;
                    if (mapE == null) {
                        return new JSONObject();
                    }
                }
                String str = f661f.get(cloudModuleEnum.prefix);
                if (str != null) {
                    try {
                        jSONObject = new JSONObject(new String(Base64.decode(AssetUtils.getStrFromAssetFile(context, str), 0)));
                    } catch (Exception e2) {
                        Log.e("UnionPower", e2.getMessage());
                        jSONObject = new JSONObject();
                    }
                } else {
                    jSONObject = new JSONObject();
                }
                for (Map.Entry<String, String> entry : f661f.entrySet()) {
                    String key = entry.getKey();
                    int iIndexOf = key.indexOf(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
                    if (key.startsWith(cloudModuleEnum.prefix) && iIndexOf != -1 && (strFromAssetFile = AssetUtils.getStrFromAssetFile(context, entry.getValue())) != null) {
                        try {
                            jSONObject.put(key.substring(iIndexOf + 1), new JSONTokener(new String(Base64.decode(strFromAssetFile, 0))).nextValue());
                        } catch (Exception e3) {
                            Log.e("UnionPower", e3.getMessage());
                        }
                    }
                }
                return jSONObject;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean d(String str) {
        return str.equals(Constants.BUCKET_NAME_THERMAL_COMMON) || str.equals(f658c);
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0062  */
    /* JADX WARN: Code duplicated, block: B:22:0x006c  */
    /* JADX WARN: Code duplicated, block: B:27:0x0095 A[Catch: IOException -> 0x00ba, TryCatch #0 {IOException -> 0x00ba, blocks: (B:23:0x0072, B:25:0x0090, B:27:0x0095, B:29:0x009f), top: B:36:0x0072 }] */
    /* JADX WARN: Code duplicated, block: B:29:0x009f A[Catch: IOException -> 0x00ba, TRY_LEAVE, TryCatch #0 {IOException -> 0x00ba, blocks: (B:23:0x0072, B:25:0x0090, B:27:0x0095, B:29:0x009f), top: B:36:0x0072 }] */
    /* JADX WARN: Code duplicated, block: B:43:0x0090 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:45:0x0066 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:48:0x00bc A[SYNTHETIC] */
    /* JADX WARN: Instruction removed from duplicated block: B:29:0x009f, please report this as an issue */
    private static Map<String, String> e(Context context) {
        List list;
        IOException e2;
        Iterator it;
        String str;
        String[] list2;
        int i2;
        HashMap map = new HashMap();
        try {
            String[] list3 = context.getAssets().list("union_power/");
            if (list3 == null) {
                return null;
            }
            list = (List) Arrays.stream(list3).filter(new Predicate() { // from class: p.a
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return UnionPowerConfig.d((String) obj);
                }
            }).collect(Collectors.toList());
            try {
                if (miui.os.Build.IS_INTERNATIONAL_BUILD && context.getAssets().list(f660e) != null) {
                    list.add(f659d);
                }
            } catch (IOException e3) {
                e2 = e3;
                Log.e("UnionPower", "list assets error =" + e2.getMessage());
            }
            if (list != null) {
                it = list.iterator();
                while (it.hasNext()) {
                    try {
                        str = "union_power/" + ((String) it.next()) + "/";
                        list2 = context.getAssets().list(str);
                        if (list2 != null) {
                            for (String str2 : list2) {
                                if (str2.matches(".+\\.config")) {
                                    map.put(str2.split("[.]")[0], str + str2);
                                }
                            }
                        }
                    } catch (IOException e4) {
                        Log.e("UnionPower", "list assets error =" + e4.getMessage());
                    }
                }
            }
            return map;
        } catch (IOException e5) {
            list = null;
            e2 = e5;
        }
        Log.e("UnionPower", "list assets error =" + e2.getMessage());
        if (list != null) {
            it = list.iterator();
            while (it.hasNext()) {
                str = "union_power/" + ((String) it.next()) + "/";
                list2 = context.getAssets().list(str);
                if (list2 != null) {
                    while (i2 < r7) {
                        if (str2.matches(".+\\.config")) {
                            map.put(str2.split("[.]")[0], str + str2);
                        }
                    }
                }
            }
        }
        return map;
    }
}
