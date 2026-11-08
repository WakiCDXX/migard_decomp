package com.xiaomi.clientreport.manager;

import android.content.Context;
import android.os.Process;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.clientreport.data.Config;
import com.xiaomi.clientreport.data.EventClientReport;
import com.xiaomi.clientreport.data.PerfClientReport;
import com.xiaomi.clientreport.processor.DefaultEventProcessor;
import com.xiaomi.clientreport.processor.DefaultPerfProcessor;
import com.xiaomi.clientreport.processor.IEventProcessor;
import com.xiaomi.clientreport.processor.IPerfProcessor;

/* JADX INFO: loaded from: classes.dex */
public class ClientReportClient {
    public static void init(Context context) {
        init(context, Config.defaultConfig(context), new DefaultEventProcessor(context), new DefaultPerfProcessor(context));
    }

    public static void reportEvent(Context context, EventClientReport eventClientReport) {
        if (eventClientReport != null) {
            ClientReportLogicManager.getInstance(context).writeEvent(eventClientReport);
        }
    }

    public static void reportPerf(Context context, PerfClientReport perfClientReport) {
        if (perfClientReport != null) {
            ClientReportLogicManager.getInstance(context).writePerf(perfClientReport);
        }
    }

    public static void updateConfig(Context context, Config config) {
        if (config == null) {
            return;
        }
        ClientReportLogicManager.getInstance(context).updateConfig(config.isEventUploadSwitchOpen(), config.isPerfUploadSwitchOpen(), config.getEventUploadFrequency(), config.getPerfUploadFrequency());
    }

    public static void init(Context context, Config config) {
        init(context, config, new DefaultEventProcessor(context), new DefaultPerfProcessor(context));
    }

    public static void init(Context context, Config config, IEventProcessor iEventProcessor, IPerfProcessor iPerfProcessor) {
        MyLog.v("init in  pid :" + Process.myPid() + " threadId: " + Thread.currentThread().getId());
        ClientReportLogicManager.getInstance(context).init(config, iEventProcessor, iPerfProcessor);
        if (AppInfoUtils.isAppMainProc(context)) {
            MyLog.v("init in process\u3000start scheduleJob");
            ClientReportLogicManager.getInstance(context).startScheduleJob();
        }
    }
}
