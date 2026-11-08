package com.miui.powerkeeper.siming.shaosi;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.siming.IDivision;
import com.miui.powerkeeper.siming.SiUtils;
import com.miui.powerkeeper.siming.Subdivisions;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class S implements IDivision {
    public static final String TAG = "SM_S_S";
    protected int SI;
    protected Handler mHandler;
    private String whereIamFrom;
    protected Context mContext = PowerKeeperApplication.b();
    protected boolean bDebug = false;

    protected S(Looper looper, int i2) {
        this.whereIamFrom = "S";
        this.mHandler = new Handler(looper) { // from class: com.miui.powerkeeper.siming.shaosi.S.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                S.this.handleMessage(message);
            }
        };
        this.SI = i2;
        this.whereIamFrom = Subdivisions.idToString(i2);
    }

    @Override // com.miui.powerkeeper.siming.IDivision
    public void debug(boolean z2) {
        this.bDebug = z2;
    }

    @Override // com.miui.powerkeeper.siming.IDivision
    public void doAction(int i2) {
        SiUtils.Log.w(TAG, "I'm doAction. I'm from " + this.whereIamFrom + ". I'm on vacation. So I will not carry out any of your orders.");
    }

    @Override // com.miui.powerkeeper.siming.IDivision
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("I'm S. I am from " + this.whereIamFrom);
    }

    public void log(String str, String str2) {
        if (this.bDebug) {
            SiUtils.Log.d(str, str2);
        }
    }

    @Override // com.miui.powerkeeper.siming.IDivision
    public void notify(int i2, Bundle bundle) {
        SiUtils.Log.w(TAG, "I'm notify. I'm from " + this.whereIamFrom + ". I'm on vacation...");
    }

    public void handleMessage(Message message) {
    }
}
