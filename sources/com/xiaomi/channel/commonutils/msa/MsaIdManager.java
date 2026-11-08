package com.xiaomi.channel.commonutils.msa;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class MsaIdManager implements IdManager {
    public static final String KEY_AAID = "aaid";
    public static final String KEY_OAID = "oaid";
    public static final String KEY_TYPE = "oaid_type";
    public static final String KEY_UDID = "udid";
    public static final String KEY_VAID = "vaid";
    private static final int SHORT_STRING_LENGTH = 5;
    private static volatile MsaIdManager sInstance;
    private IdManager mIdManager;
    private int mManagerType = MsaIdFactory.sType;

    private MsaIdManager(Context context) {
        this.mIdManager = MsaIdFactory.instance(context);
        MyLog.w("create id manager is: " + this.mManagerType);
    }

    public static MsaIdManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MsaIdManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new MsaIdManager(context.getApplicationContext());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    private String substringShort(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str.length() > 5 ? str.substring(str.length() - 5) : str;
    }

    private String trim(String str) {
        return str == null ? "" : str;
    }

    public void fillData(Map<String, String> map) {
        if (map == null) {
            return;
        }
        String udid = getUDID();
        if (!TextUtils.isEmpty(udid)) {
            map.put(KEY_UDID, udid);
        }
        String oaid = getOAID();
        if (!TextUtils.isEmpty(oaid)) {
            map.put("oaid", oaid);
        }
        String vaid = getVAID();
        if (!TextUtils.isEmpty(vaid)) {
            map.put(KEY_VAID, vaid);
        }
        String aaid = getAAID();
        if (!TextUtils.isEmpty(aaid)) {
            map.put(KEY_AAID, aaid);
        }
        map.put(KEY_TYPE, String.valueOf(this.mManagerType));
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getAAID() {
        return null;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getOAID() {
        return trim(this.mIdManager.getOAID());
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getUDID() {
        return null;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getVAID() {
        return null;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public boolean isAllowOAID() {
        return this.mIdManager.isAllowOAID();
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public boolean isSupported() {
        return this.mIdManager.isSupported();
    }

    public String toShortString() {
        return "t:" + this.mManagerType + " s:" + isSupported() + " d:" + substringShort(getUDID()) + " | " + substringShort(getOAID()) + " | " + substringShort(getVAID()) + " | " + substringShort(getAAID());
    }

    public void init() {
    }
}
