package y;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.ot.pubsub.util.t;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class b {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f2403d = "b";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static volatile b f2404e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static String f2405f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static String f2406g;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private boolean f2409c = false;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f2407a = com.ot.pubsub.util.b.a();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final Context f2408b = com.ot.pubsub.util.b.c();

    private b() {
        f2406g = com.ot.pubsub.util.b.e();
    }

    public static b a() {
        if (f2404e == null) {
            synchronized (b.class) {
                try {
                    if (f2404e == null) {
                        f2404e = new b();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f2404e;
    }

    private void d(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            Uri uri = Uri.parse("content://com.miui.analytics.OneTrackProvider/insId");
            ContentValues contentValues = new ContentValues();
            contentValues.put(f2406g, str);
            this.f2408b.getContentResolver().insert(uri, contentValues);
        } catch (Exception e2) {
            t.r(str);
            com.ot.pubsub.util.k.e(f2403d, "setRemoteCacheInstanceId e", e2);
        }
    }

    private String e() {
        String string = null;
        try {
            Uri.Builder builderBuildUpon = Uri.parse("content://com.miui.analytics.OneTrackProvider/insId").buildUpon();
            builderBuildUpon.appendQueryParameter("pkg", f2406g);
            builderBuildUpon.appendQueryParameter("sign", u.a.a("insId" + f2406g));
            Cursor cursorQuery = this.f2408b.getContentResolver().query(builderBuildUpon.build(), null, null, null, null);
            if (cursorQuery != null) {
                while (cursorQuery.moveToNext()) {
                    string = cursorQuery.getString(0);
                }
                cursorQuery.close();
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.e(f2403d, "getRemoteCacheInstanceId e", e2);
        }
        return string;
    }

    private String f() {
        String strB = t.b(this.f2407a);
        if (TextUtils.isEmpty(strB)) {
            return t.s();
        }
        t.r(strB);
        return strB;
    }

    public void b(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        f2405f = str;
        if (this.f2409c) {
            d(str);
        }
        t.r(f2405f);
    }

    public String c() {
        String strF;
        if (!TextUtils.isEmpty(f2405f)) {
            return f2405f;
        }
        if (this.f2409c) {
            strF = e();
            String strF2 = f();
            if (TextUtils.isEmpty(strF) && !TextUtils.isEmpty(strF2)) {
                d(strF2);
                strF = strF2;
            } else if (!TextUtils.isEmpty(strF) && TextUtils.isEmpty(strF2)) {
                t.r(strF);
            }
        } else {
            strF = f();
        }
        if (TextUtils.isEmpty(strF)) {
            String string = UUID.randomUUID().toString();
            f2405f = string;
            if (this.f2409c) {
                d(string);
            }
            t.r(f2405f);
        } else {
            f2405f = strF;
        }
        return f2405f;
    }
}
