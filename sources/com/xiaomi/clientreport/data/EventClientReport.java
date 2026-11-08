package com.xiaomi.clientreport.data;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.clientReport.ReportConstants;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class EventClientReport extends BaseClientReport {
    public String eventContent;
    public String eventId;
    public long eventTime;
    public int eventType;

    public static EventClientReport getBlankInstance() {
        return new EventClientReport();
    }

    @Override // com.xiaomi.clientreport.data.BaseClientReport
    public JSONObject toJson() {
        try {
            JSONObject json = super.toJson();
            if (json == null) {
                return null;
            }
            json.put("eventId", this.eventId);
            json.put(ReportConstants.MESSAGE_EVENT_TYPE, this.eventType);
            json.put("eventTime", this.eventTime);
            String str = this.eventContent;
            if (str == null) {
                str = "";
            }
            json.put("eventContent", str);
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
