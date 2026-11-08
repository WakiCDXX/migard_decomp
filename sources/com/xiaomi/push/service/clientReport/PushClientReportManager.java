package com.xiaomi.push.service.clientReport;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.xiaomi.clientreport.data.BaseClientReport;
import com.xiaomi.clientreport.data.EventClientReport;
import com.xiaomi.clientreport.data.PerfClientReport;
import com.xiaomi.clientreport.manager.ClientReportClient;
import com.xiaomi.push.service.PushConstants;

/* JADX INFO: loaded from: classes.dex */
public class PushClientReportManager {
    private static volatile PushClientReportManager sInstance;
    private Context mContext;

    private PushClientReportManager(Context context) {
        this.mContext = context;
    }

    private void collectData(BaseClientReport baseClientReport) {
        if (baseClientReport instanceof PerfClientReport) {
            ClientReportClient.reportPerf(this.mContext, (PerfClientReport) baseClientReport);
        } else if (baseClientReport instanceof EventClientReport) {
            ClientReportClient.reportEvent(this.mContext, (EventClientReport) baseClientReport);
        }
    }

    public static PushClientReportManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PushClientReportManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new PushClientReportManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    protected void reportEvent(String str, int i2, String str2, int i3, String str3, int i4, long j2, String str4) {
        if (i2 < 0 || TextUtils.isEmpty(str2) || i3 < 0 || TextUtils.isEmpty(str3) || i4 < 0) {
            return;
        }
        EventClientReport blankInstance = EventClientReport.getBlankInstance();
        blankInstance.production = i2;
        blankInstance.clientInterfaceId = str2;
        blankInstance.reportType = i3;
        blankInstance.eventId = str3;
        blankInstance.eventType = i4;
        blankInstance.eventTime = j2;
        blankInstance.eventContent = str4;
        blankInstance.setAppPackageName(str);
        blankInstance.setSdkVersion(PushConstants.PUSH_VERSION_NAME);
        collectData(blankInstance);
    }

    public void reportEvent4DUPMD(String str, String str2, String str3, String str4) {
        reportEvent(str, str2, str3, ReportConstants.DUP_MESSAGE_DROP, System.currentTimeMillis(), str4);
    }

    public void reportEvent4ERROR(String str, String str2, String str3, String str4) {
        reportEvent(str, str2, str3, 5001, System.currentTimeMillis(), str4);
    }

    public void reportEvent4NeedDrop(String str, String str2, String str3, String str4) {
        reportEvent(str, str2, str3, ReportConstants.NOT_SATISFY_CONDITION_DROP, System.currentTimeMillis(), str4);
    }

    public void reportPerf(String str, int i2, long j2, long j3) {
        if (i2 < 0 || j3 < 0 || j2 <= 0) {
            return;
        }
        PerfClientReport perfClientReportReportPerf2Object = PushClientReportHelper.reportPerf2Object(this.mContext, i2, j2, j3);
        perfClientReportReportPerf2Object.setAppPackageName(str);
        perfClientReportReportPerf2Object.setSdkVersion(PushConstants.PUSH_VERSION_NAME);
        collectData(perfClientReportReportPerf2Object);
    }

    public void reportEvent4ERROR(String str, Intent intent, String str2) {
        if (intent == null) {
            return;
        }
        reportEvent(str, PushClientReportHelper.getInterfaceIdByType(intent.getIntExtra(ReportConstants.EVENT_MESSAGE_TYPE, -1)), intent.getStringExtra("messageId"), 5001, System.currentTimeMillis(), str2);
    }

    public void reportPerf(String str, int i2, String str2, int i3, int i4, long j2, long j3) {
        if (i2 < 0 || TextUtils.isEmpty(str2) || i3 < 0 || i4 < 0 || j3 < 0 || j2 <= 0) {
            return;
        }
        PerfClientReport blankInstance = PerfClientReport.getBlankInstance();
        blankInstance.production = i2;
        blankInstance.clientInterfaceId = str2;
        blankInstance.reportType = i3;
        blankInstance.code = i4;
        blankInstance.perfCounts = j2;
        blankInstance.perfLatencies = j3;
        blankInstance.setAppPackageName(str);
        blankInstance.setSdkVersion(PushConstants.PUSH_VERSION_NAME);
        collectData(blankInstance);
    }

    public void reportEvent4ERROR(String str, String str2, String str3, Throwable th) {
        reportEvent(str, str2, str3, 5001, System.currentTimeMillis(), th.getMessage());
    }

    public void reportEvent4ERROR(String str, Intent intent, Throwable th) {
        if (intent == null) {
            return;
        }
        reportEvent(str, PushClientReportHelper.getInterfaceIdByType(intent.getIntExtra(ReportConstants.EVENT_MESSAGE_TYPE, -1)), intent.getStringExtra("messageId"), 5001, System.currentTimeMillis(), th.getMessage());
    }

    public void reportEvent(String str, String str2, String str3, int i2, long j2, String str4) {
        if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str3)) {
            return;
        }
        EventClientReport eventClientReportReportEvent2Object = PushClientReportHelper.reportEvent2Object(this.mContext, str2, str3, i2, j2, str4);
        eventClientReportReportEvent2Object.setAppPackageName(str);
        eventClientReportReportEvent2Object.setSdkVersion(PushConstants.PUSH_VERSION_NAME);
        collectData(eventClientReportReportEvent2Object);
    }

    private void reportPerf(String str, int i2, long j2, long j3, String str2) {
        reportPerf(str, i2, j2, j3);
    }

    public void reportEvent(String str, String str2, String str3, int i2, String str4) {
        reportEvent(str, str2, str3, i2, System.currentTimeMillis(), str4);
    }

    public void reportEvent(String str, String str2, String str3, int i2) {
        reportEvent(str, str2, str3, i2, System.currentTimeMillis(), "");
    }

    public void reportEvent(String str, Intent intent, int i2) {
        if (intent == null) {
            return;
        }
        reportEvent(str, intent, i2, "");
    }

    public void reportEvent(String str, Intent intent, int i2, String str2) {
        if (intent == null) {
            return;
        }
        reportEvent(str, PushClientReportHelper.getInterfaceIdByType(intent.getIntExtra(ReportConstants.EVENT_MESSAGE_TYPE, -1)), intent.getStringExtra("messageId"), i2, System.currentTimeMillis(), str2);
    }
}
