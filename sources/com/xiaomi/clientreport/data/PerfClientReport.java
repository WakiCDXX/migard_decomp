package com.xiaomi.clientreport.data;

import com.xiaomi.channel.commonutils.logger.MyLog;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PerfClientReport extends BaseClientReport {
    private static final long DEFAULT_VALUE = -1;
    public int code;
    public long perfCounts = -1;
    public long perfLatencies = -1;

    public static PerfClientReport getBlankInstance() {
        return new PerfClientReport();
    }

    @Override // com.xiaomi.clientreport.data.BaseClientReport
    public JSONObject toJson() {
        try {
            JSONObject json = super.toJson();
            if (json == null) {
                return null;
            }
            json.put("code", this.code);
            json.put("perfCounts", this.perfCounts);
            json.put("perfLatencies", this.perfLatencies);
            return json;
        } catch (JSONException e2) {
            MyLog.e(e2);
            return null;
        }
    }

    @Override // com.xiaomi.clientreport.data.BaseClientReport
    public String toJsonString() {
        return super.toJsonString();
    }
}
