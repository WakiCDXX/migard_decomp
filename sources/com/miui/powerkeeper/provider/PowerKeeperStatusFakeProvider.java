package com.miui.powerkeeper.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.miui.powerkeeper.utils.ThermalStoreUtils;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperStatusFakeProvider {
    public static final String AUTHORITY = "com.miui.powerkeeper.ui.search";
    public static final String METHOD_MISC_BOOLEAN = "Misc.getBoolean";
    public static final String METHOD_WARM_CONTROL_MODE_SUPPORTED = "warmControlModeSupported";
    public static final Uri URI = Uri.parse("content://com.miui.powerkeeper.ui.search");
    private static HashSet<String> sMethods;
    private Context mContext;

    static {
        HashSet<String> hashSet = new HashSet<>();
        sMethods = hashSet;
        hashSet.add(METHOD_WARM_CONTROL_MODE_SUPPORTED);
        sMethods.add(METHOD_MISC_BOOLEAN);
    }

    PowerKeeperStatusFakeProvider(Context context) {
        this.mContext = context;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        Bundle bundle2 = new Bundle();
        if (METHOD_WARM_CONTROL_MODE_SUPPORTED.equals(str)) {
            bundle2.putBoolean(METHOD_WARM_CONTROL_MODE_SUPPORTED, ThermalStoreUtils.warmControlModeSupported(this.mContext));
        } else if (METHOD_MISC_BOOLEAN.equals(str)) {
            bundle2.putBoolean(METHOD_MISC_BOOLEAN, SimpleSettings.Misc.getBoolean(this.mContext, str2, false));
        }
        return bundle2;
    }

    public boolean checkMethod(String str) {
        return sMethods.contains(str);
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
