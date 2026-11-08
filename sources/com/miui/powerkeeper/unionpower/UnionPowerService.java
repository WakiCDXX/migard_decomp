package com.miui.powerkeeper.unionpower;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.UserHandle;
import com.miui.powerkeeper.unionpower.traceextension.TraceExtensionService;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import i.a;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class UnionPowerService extends Service {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static HandlerThread f629b;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private a f630a;

    private static void a() {
        if (UnionPowerConfig.f657b && f629b == null) {
            HandlerThread handlerThread = new HandlerThread("UnionPower", 0);
            f629b = handlerThread;
            handlerThread.start();
        }
    }

    public static synchronized HandlerThread b() {
        a();
        return f629b;
    }

    @Override // android.app.Service
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (!UnionPowerConfig.f657b) {
            printWriter.println("UnionPowerService disable");
            return;
        }
        printWriter.println("================UnionPowerService dump==================");
        a aVar = this.f630a;
        if (aVar != null) {
            aVar.b(fileDescriptor, printWriter, strArr);
        }
        printWriter.println("================UnionPowerService dump end==============");
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        if (UserHandle.myUserId() != 0) {
            stopSelf();
        } else {
            if (UnionPowerConfig.f657b) {
                return;
            }
            stopSelf();
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        a aVar = this.f630a;
        if (aVar != null) {
            aVar.e();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i2, int i3) {
        if (UserHandle.myUserId() != 0) {
            stopSelf();
            return 1;
        }
        if (!UnionPowerConfig.f657b) {
            stopSelf();
            return 1;
        }
        if (b() != null && this.f630a == null) {
            this.f630a = a.c(this);
        }
        if (TraceExtensionService.v()) {
            startService(new Intent(this, (Class<?>) TraceExtensionService.class));
        }
        return 1;
    }
}
