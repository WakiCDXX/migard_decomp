package com.xiaomi.clientreport.processor;

import com.xiaomi.clientreport.data.BaseClientReport;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public interface IPerfProcessor extends IDataSend, IWrite {
    void setPerfMap(HashMap<String, HashMap<String, BaseClientReport>> map);
}
