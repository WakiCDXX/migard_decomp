package com.xiaomi.mipush.sdk;

import com.xiaomi.xmpush.thrift.ConfigKey;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class AssemblePushInfoHelper {
    protected static final String COS_PUSH_MANAGER_CLASS_NAME = "com.xiaomi.assemble.control.COSPushManager";
    protected static final String COS_PUSH_SINGLE_METHOD_NAME = "newInstance";
    protected static final String FCM_PUSH_MANAGER_CLASS_NAME = "com.xiaomi.assemble.control.FCMPushManager";
    protected static final String FCM_PUSH_SINGLE_METHOD_NAME = "newInstance";
    protected static final String FTOS_PUSH_MANAGER_CLASS_NAME = "com.xiaomi.assemble.control.FTOSPushManager";
    protected static final String FTOS_PUSH_SINGLE_METHOD_NAME = "newInstance";
    protected static final String HMS_PUSH_MANAGER_CLASS_NAME = "com.xiaomi.assemble.control.HmsPushManager";
    protected static final String HMS_PUSH_SINGLE_METHOD_NAME = "newInstance";
    private static HashMap<AssemblePush, ManageClassInfo> mHashMaps = new HashMap<>();

    /* JADX INFO: renamed from: com.xiaomi.mipush.sdk.AssemblePushInfoHelper$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush;

        static {
            int[] iArr = new int[AssemblePush.values().length];
            $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush = iArr;
            try {
                iArr[AssemblePush.ASSEMBLE_PUSH_HUAWEI.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[AssemblePush.ASSEMBLE_PUSH_FCM.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[AssemblePush.ASSEMBLE_PUSH_COS.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[AssemblePush.ASSEMBLE_PUSH_FTOS.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    static class ManageClassInfo {
        public String className;
        public String methodName;

        public ManageClassInfo(String str, String str2) {
            this.className = str;
            this.methodName = str2;
        }
    }

    static {
        add(AssemblePush.ASSEMBLE_PUSH_HUAWEI, new ManageClassInfo(HMS_PUSH_MANAGER_CLASS_NAME, "newInstance"));
        add(AssemblePush.ASSEMBLE_PUSH_FCM, new ManageClassInfo(FCM_PUSH_MANAGER_CLASS_NAME, "newInstance"));
        add(AssemblePush.ASSEMBLE_PUSH_COS, new ManageClassInfo(COS_PUSH_MANAGER_CLASS_NAME, "newInstance"));
        add(AssemblePush.ASSEMBLE_PUSH_FTOS, new ManageClassInfo(FTOS_PUSH_MANAGER_CLASS_NAME, "newInstance"));
    }

    private static void add(AssemblePush assemblePush, ManageClassInfo manageClassInfo) {
        if (manageClassInfo != null) {
            mHashMaps.put(assemblePush, manageClassInfo);
        }
    }

    public static ConfigKey getConfigKeyByType(AssemblePush assemblePush) {
        return ConfigKey.AggregatePushSwitch;
    }

    public static ManageClassInfo getManageClassInfoByType(AssemblePush assemblePush) {
        return mHashMaps.get(assemblePush);
    }

    public static RetryType getRetryType(AssemblePush assemblePush) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[assemblePush.ordinal()];
        if (i2 == 1) {
            return RetryType.UPLOAD_HUAWEI_TOKEN;
        }
        if (i2 == 2) {
            return RetryType.UPLOAD_FCM_TOKEN;
        }
        if (i2 == 3) {
            return RetryType.UPLOAD_COS_TOKEN;
        }
        if (i2 != 4) {
            return null;
        }
        return RetryType.UPLOAD_FTOS_TOKEN;
    }
}
