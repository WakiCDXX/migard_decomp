package com.miui.powerkeeper.thermal.resource;

import com.miui.powerkeeper.thermal.ScenarioManager;
import com.miui.powerkeeper.thermal.ThermalSwitchHandler;
import com.miui.powerkeeper.thermal.listener.IListener;
import com.miui.powerkeeper.thermal.listener.NullListener;
import com.miui.powerkeeper.thermal.processor.IProcessor;
import com.miui.powerkeeper.thermal.processor.NullProcessor;
import com.miui.powerkeeper.thermal.processor.ThermalProcessor;
import com.miui.powerkeeper.thermal.setting.SettingDTO;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ThermalFactory {
    public IListener createListener(String str) {
        String str2;
        if (str.contains(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT)) {
            String[] strArrSplit = str.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
            str2 = "com.miui.powerkeeper.thermal.listener." + strArrSplit[0] + "Listener" + strArrSplit[1];
        } else {
            str2 = "com.miui.powerkeeper.thermal.listener." + str + "Listener";
        }
        try {
            return (IListener) Class.forName(str2).newInstance();
        } catch (Exception e2) {
            StringWriter stringWriter = new StringWriter();
            e2.printStackTrace(new PrintWriter(stringWriter));
            ScenarioManager.getHistory().log(stringWriter.toString());
            return new NullListener();
        }
    }

    public IProcessor createProcessor(SettingDTO.ProcessorDTO processorDTO) {
        String str;
        String name = processorDTO.getName();
        Map<String, Set<Integer>> groups = processorDTO.getGroups();
        if (name.contains(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT)) {
            String[] strArrSplit = name.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
            str = "com.miui.powerkeeper.thermal.processor." + strArrSplit[0] + "Processor" + strArrSplit[1];
        } else {
            str = "com.miui.powerkeeper.thermal.processor." + name + "Processor";
        }
        try {
            IProcessor iProcessor = (IProcessor) Class.forName(str).newInstance();
            if (!(iProcessor instanceof ThermalProcessor)) {
                return iProcessor;
            }
            ((ThermalProcessor) iProcessor).init(groups);
            return iProcessor;
        } catch (Exception e2) {
            StringWriter stringWriter = new StringWriter();
            e2.printStackTrace(new PrintWriter(stringWriter));
            ThermalSwitchHandler.getHistory().log(stringWriter.toString());
            return new NullProcessor();
        }
    }
}
