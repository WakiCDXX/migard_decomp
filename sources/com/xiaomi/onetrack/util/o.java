package com.xiaomi.onetrack.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class o {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1580a = "o";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f1581b = "content://com.miui.analytics.OneTrackProvider/insId";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1582c = "insId";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1583d = "pkg";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1584e = "sign";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static volatile o f1585f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static String f1586g;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static String f1587j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private boolean f1590k = false;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private final Context f1588h = com.xiaomi.onetrack.f.a.a();

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private final Context f1589i = com.xiaomi.onetrack.f.a.b();

    private o() {
        f1587j = com.xiaomi.onetrack.f.a.e();
    }

    public static o a() {
        if (f1585f == null) {
            synchronized (o.class) {
                try {
                    if (f1585f == null) {
                        f1585f = new o();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f1585f;
    }

    private String c() {
        String string = null;
        try {
            Uri.Builder builderBuildUpon = Uri.parse(f1581b).buildUpon();
            builderBuildUpon.appendQueryParameter("pkg", f1587j);
            builderBuildUpon.appendQueryParameter(f1584e, com.xiaomi.onetrack.d.a.a(f1582c + f1587j));
            Cursor cursorQuery = this.f1589i.getContentResolver().query(builderBuildUpon.build(), null, null, null, null);
            if (cursorQuery != null) {
                while (cursorQuery.moveToNext()) {
                    string = cursorQuery.getString(0);
                }
                cursorQuery.close();
            }
        } catch (Exception e2) {
            p.a(f1580a, "getRemoteCacheInstanceId e", e2);
        }
        return string;
    }

    private String d() {
        String strA = aa.a(this.f1588h);
        if (TextUtils.isEmpty(strA)) {
            return aa.m();
        }
        aa.e(strA);
        return strA;
    }

    public String b() {
        String strD;
        if (!TextUtils.isEmpty(f1586g)) {
            return f1586g;
        }
        if (this.f1590k) {
            strD = c();
            String strD2 = d();
            if (TextUtils.isEmpty(strD) && !TextUtils.isEmpty(strD2)) {
                b(strD2);
                strD = strD2;
            } else if (!TextUtils.isEmpty(strD) && TextUtils.isEmpty(strD2)) {
                aa.e(strD);
            }
        } else {
            strD = d();
        }
        if (TextUtils.isEmpty(strD)) {
            String string = UUID.randomUUID().toString();
            f1586g = string;
            if (this.f1590k) {
                b(string);
            }
            aa.e(f1586g);
        } else {
            f1586g = strD;
        }
        return f1586g;
    }

    public void a(Boolean bool) {
        this.f1590k = bool.booleanValue();
    }

    public void a(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        f1586g = str;
        if (this.f1590k) {
            b(str);
        }
        aa.e(f1586g);
    }

    private void b(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            Uri uri = Uri.parse(f1581b);
            ContentValues contentValues = new ContentValues();
            contentValues.put(f1587j, str);
            this.f1589i.getContentResolver().insert(uri, contentValues);
        } catch (Exception e2) {
            aa.e(str);
            p.a(f1580a, "setRemoteCacheInstanceId e", e2);
        }
    }
}
