package com.miui.powerkeeper.siming;

import android.os.Bundle;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public interface IDivision {
    void debug(boolean z2);

    void doAction(int i2);

    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    void notify(int i2, Bundle bundle);
}
