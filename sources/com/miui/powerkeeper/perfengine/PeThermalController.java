package com.miui.powerkeeper.perfengine;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import java.io.PrintWriter;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class PeThermalController implements b {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Context f467d;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Handler f464a = null;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private LocalSocket f465b = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private LocalSocketAddress f466c = null;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private long f468e = 0;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private volatile boolean f469f = false;

    class a extends Handler {
        a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) throws Throwable {
            int i2 = message.what;
            if (i2 == 1 || i2 == 2) {
                PeThermalController.this.j(message.arg1);
            }
        }
    }

    public PeThermalController(Context context) {
        this.f467d = null;
        this.f467d = context;
        e();
        PeCloudManager.e(this.f467d).g(this);
    }

    private void c() {
        this.f464a.removeMessages(1);
        this.f464a.removeMessages(2);
    }

    private void d(int i2) {
        Message messageObtain = Message.obtain();
        messageObtain.what = 2;
        messageObtain.arg1 = 0;
        this.f464a.sendMessageDelayed(messageObtain, i2);
    }

    private void e() {
        if (this.f464a != null) {
            return;
        }
        this.f464a = new a(BackgroundThread.getHandler().getLooper());
    }

    private boolean f() {
        try {
            this.f465b = new LocalSocket();
            if (this.f466c == null) {
                this.f466c = new LocalSocketAddress("thermal-perfd-recv-client", LocalSocketAddress.Namespace.RESERVED);
            }
            this.f465b.connect(this.f466c);
            return true;
        } catch (Exception e2) {
            Log.e("PeThermalController", "initSocket exception for : " + e2.toString());
            return false;
        }
    }

    private void g() {
        if (!this.f464a.hasMessages(1) && this.f464a.hasMessages(2)) {
            c();
            return;
        }
        c();
        Message messageObtain = Message.obtain();
        messageObtain.what = 1;
        messageObtain.arg1 = 1;
        this.f464a.sendMessage(messageObtain);
    }

    private void i(int i2) {
        boolean z2 = Build.IS_DEBUGGABLE;
        if (z2) {
            Trace.traceBegin(64L, "PeThermalBreak.sendMessage");
        }
        this.f468e = System.currentTimeMillis();
        g();
        d(i2);
        if (z2) {
            Trace.traceEnd(64L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:63:0x00d9 A[Catch: all -> 0x00d1, Exception -> 0x00d3, TRY_LEAVE, TryCatch #9 {Exception -> 0x00d3, blocks: (B:56:0x00cd, B:61:0x00d5, B:63:0x00d9), top: B:80:0x00cd, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:80:0x00cd A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public void j(int i2) throws Throwable {
        PrintWriter printWriter;
        LocalSocket localSocket;
        if (!f()) {
            Log.e("PeThermalController", "Some errors occurs during socket connection, fail to connect!");
            return;
        }
        boolean z2 = Build.IS_DEBUGGABLE;
        if (z2) {
            Trace.traceBegin(64L, "PeThermalBreak.sendToThermal");
        }
        try {
            printWriter = new PrintWriter(this.f465b.getOutputStream());
            try {
                try {
                    printWriter.write(i2);
                    printWriter.flush();
                    if (z2) {
                        Log.e("PeThermalController", "duration : " + (System.currentTimeMillis() - this.f468e));
                    }
                    try {
                        try {
                            printWriter.close();
                            LocalSocket localSocket2 = this.f465b;
                            if (localSocket2 != null) {
                                localSocket2.close();
                            }
                        } catch (Exception e2) {
                            Log.e("PeThermalController", "sendToThermal exception for : " + e2.toString());
                        }
                    } catch (Throwable th) {
                        this.f465b = null;
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                    Log.e("PeThermalController", "sendToThermal exception for : " + e.toString());
                    if (printWriter != null) {
                        try {
                            try {
                                printWriter.close();
                            } catch (Exception e4) {
                                Log.e("PeThermalController", "sendToThermal exception for : " + e4.toString());
                            }
                        } catch (Throwable th2) {
                            this.f465b = null;
                            throw th2;
                        }
                    }
                    LocalSocket localSocket3 = this.f465b;
                    if (localSocket3 != null) {
                        localSocket3.close();
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                try {
                    if (printWriter != null) {
                        try {
                            printWriter.close();
                        } catch (Exception e5) {
                            Log.e("PeThermalController", "sendToThermal exception for : " + e5.toString());
                            throw th;
                        }
                    }
                    localSocket = this.f465b;
                    if (localSocket != null) {
                        localSocket.close();
                    }
                    throw th;
                } finally {
                    this.f465b = null;
                }
            }
        } catch (Exception e6) {
            e = e6;
            printWriter = null;
        } catch (Throwable th4) {
            th = th4;
            printWriter = null;
            if (printWriter != null) {
                printWriter.close();
            }
            localSocket = this.f465b;
            if (localSocket != null) {
                localSocket.close();
            }
            throw th;
        }
        this.f465b = null;
        if (Build.IS_DEBUGGABLE) {
            Trace.traceEnd(64L);
        }
    }

    @Override // com.miui.powerkeeper.perfengine.b
    public boolean b(String str) {
        return true;
    }

    public synchronized void h(int i2) {
        if (this.f469f) {
            if (!SystemProperties.get("ro.boot.factorybuild", "").equals("1")) {
                i(i2);
            }
        }
    }
}
