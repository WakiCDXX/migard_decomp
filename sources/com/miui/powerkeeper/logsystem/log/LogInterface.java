package com.miui.powerkeeper.logsystem.log;

import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public interface LogInterface {
    boolean clearSystemLog(int i2, long j2);

    void dumpSystemLog(PrintWriter printWriter, int i2, String str);

    int getInterfaceLogType();

    String getInterfaceName();

    int getLogCatchType();

    boolean produceSystemLog(int i2, long j2);
}
