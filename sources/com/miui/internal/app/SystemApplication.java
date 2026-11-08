package com.miui.internal.app;

import android.app.Application;

/* JADX INFO: loaded from: classes.dex */
public class SystemApplication extends Application {
    public static Application sInitializingApplication;

    public SystemApplication() {
        sInitializingApplication = this;
    }

    public static Application getCurrentApplication() {
        return sInitializingApplication;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
    }
}
