package com.xiaomi.channel.commonutils.msa;

import android.content.Context;
import com.xiaomi.channel.commonutils.android.MIUIUtils;

/* JADX INFO: loaded from: classes.dex */
class MsaIdFactory {
    private static final int TYPE_HUAWEI = 2;
    private static final int TYPE_MIUI = 1;
    private static final int TYPE_MSA = 3;
    private static final int TYPE_OPPO = 4;
    private static final int TYPE_OTHER = 0;
    private static final int TYPE_VIVO = 5;
    static int sType;

    private MsaIdFactory() {
    }

    public static IdManager instance(Context context) {
        if (MIUIUtils.isMIUI()) {
            sType = 1;
            return new MiuiIdManager(context);
        }
        if (HuaweiIdManager.isHuaweiPhone(context)) {
            sType = 2;
            return new HuaweiIdManager(context);
        }
        if (OppoIdManager.isOppoPhone(context)) {
            sType = 4;
            return new OppoIdManager(context);
        }
        if (VivoIdManager.isVivoPhone(context)) {
            sType = 5;
            return new VivoIdManager(context);
        }
        if (MdidJLibrary.checkAndLoadMdidSdk(context)) {
            sType = 3;
            return new MdidIdManager(context);
        }
        sType = 0;
        return new OtherIdManager();
    }
}
