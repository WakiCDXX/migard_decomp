package com.miui.powerkeeper.thermal.resource;

import android.annotation.Nullable;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.ArrayMap;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.cloudcontrol.IUseCloud;
import com.miui.powerkeeper.cloudcontrol.h;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.c;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class DBObserver {
    private static DBObserver sInstance;
    private final Handler mHandler;
    private final ContentObserver mObserver;
    private final Context mContext = PowerKeeperApplication.b();
    private final Map<String, Set<IUseCloud>> mUsers = new ArrayMap();

    /* JADX INFO: Access modifiers changed from: private */
    class DBContentObserver extends ContentObserver {
        public DBContentObserver(Handler handler) {
            super(handler);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onChange$0(Uri uri, Map.Entry entry) {
            return uri.toString().contains((CharSequence) entry.getKey());
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, @Nullable final Uri uri) {
            if (uri == null) {
                return;
            }
            DBObserver.this.mUsers.entrySet().stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermal.resource.a
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return DBObserver.DBContentObserver.lambda$onChange$0(uri, (Map.Entry) obj);
                }
            }).map(new Function() { // from class: com.miui.powerkeeper.thermal.resource.b
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return (Set) ((Map.Entry) obj).getValue();
                }
            }).flatMap(new c()).forEach(new h());
        }
    }

    private DBObserver() {
        Handler handler = BackgroundThread.getHandler();
        this.mHandler = handler;
        this.mObserver = new DBContentObserver(handler);
    }

    private void addObserver(String str) {
        this.mContext.getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(str), false, this.mObserver);
    }

    public static synchronized DBObserver getInstance() {
        try {
            if (sInstance == null) {
                sInstance = new DBObserver();
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private void removeObserver(String str) {
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
    }

    public void registerUser(String str, IUseCloud iUseCloud) {
        synchronized (this.mUsers) {
            try {
                Set<IUseCloud> orDefault = this.mUsers.getOrDefault(str, new HashSet());
                orDefault.add(iUseCloud);
                if (!this.mUsers.containsKey(str)) {
                    this.mUsers.put(str, orDefault);
                    addObserver(str);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterUser(String str, IUseCloud iUseCloud) {
        synchronized (this.mUsers) {
            try {
                if (this.mUsers.containsKey(str)) {
                    Set<IUseCloud> set = this.mUsers.get(str);
                    set.remove(iUseCloud);
                    if (set.isEmpty()) {
                        this.mUsers.remove(str);
                    }
                    if (this.mUsers.isEmpty()) {
                        removeObserver(str);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
