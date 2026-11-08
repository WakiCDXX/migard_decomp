package com.miui.powerkeeper.batterylife.monitor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public interface Resource {
    void dump(PrintWriter printWriter, String[] strArr);

    ArrayList<Map<String, Object>> getUploadData();

    void reset();
}
