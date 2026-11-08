package com.xiaomi.channel.commonutils.msa;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import com.xiaomi.channel.commonutils.android.SystemProperties;

/* JADX INFO: loaded from: classes.dex */
class VivoIdManager implements IdManager {
    private Context mContext;
    private static String URI = "content://com.vivo.vms.IdProvider/IdentifierId/";
    private static String URI_OAID = URI + "OAID";
    private static String URI_VAID = URI + "VAID_";
    private static String URI_AAID = URI + "AAID_";
    private static String URI_OAID_STATUS = URI + "OAIDSTATUS";
    private static String KEY_PROP_SUPPORTED = "persist.sys.identifierid.supported";

    public VivoIdManager(Context context) {
        this.mContext = context;
    }

    private String getOpenId(String str) throws Throwable {
        Cursor cursorQuery;
        Cursor cursor = null;
        string = null;
        string = null;
        string = null;
        String string = null;
        try {
            cursorQuery = this.mContext.getContentResolver().query(Uri.parse(str), null, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToNext()) {
                        string = cursorQuery.getString(cursorQuery.getColumnIndex("value"));
                    }
                } catch (Exception unused) {
                    if (cursorQuery != null) {
                    }
                    return string;
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (Exception unused2) {
            cursorQuery = null;
        } catch (Throwable th2) {
            th = th2;
        }
        return string;
    }

    public static boolean isVivoPhone(Context context) {
        try {
            ProviderInfo providerInfoResolveContentProvider = context.getPackageManager().resolveContentProvider(Uri.parse(URI).getAuthority(), 128);
            return (providerInfoResolveContentProvider == null || (providerInfoResolveContentProvider.applicationInfo.flags & 1) == 0) ? false : true;
        } catch (Exception unused) {
            return false;
        }
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getAAID() {
        return null;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getOAID() {
        return getOpenId(URI_OAID);
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
        return "1".equals(getOpenId(URI_OAID_STATUS));
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public boolean isSupported() {
        return "1".equals(SystemProperties.get(KEY_PROP_SUPPORTED, "0"));
    }
}
