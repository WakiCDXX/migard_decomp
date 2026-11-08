package com.xiaomi.clientreport.processor;

import com.xiaomi.clientreport.data.BaseClientReport;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public interface IEventProcessor extends IDataSend, IWrite {
    String bytesToString(byte[] bArr);

    void setEventMap(HashMap<String, ArrayList<BaseClientReport>> map);

    byte[] stringToBytes(String str);
}
