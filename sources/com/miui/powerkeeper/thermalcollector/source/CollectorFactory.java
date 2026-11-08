package com.miui.powerkeeper.thermalcollector.source;

import android.annotation.NonNull;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.collector.AmbientCollector;
import com.miui.powerkeeper.thermalcollector.collector.AudioCollector;
import com.miui.powerkeeper.thermalcollector.collector.BluetoothCollector;
import com.miui.powerkeeper.thermalcollector.collector.BoardCollector;
import com.miui.powerkeeper.thermalcollector.collector.CameraStateCollector;
import com.miui.powerkeeper.thermalcollector.collector.CompoundScenarioCollector;
import com.miui.powerkeeper.thermalcollector.collector.DisplayCollector;
import com.miui.powerkeeper.thermalcollector.collector.FlashCollector;
import com.miui.powerkeeper.thermalcollector.collector.ForegroundEndCollector;
import com.miui.powerkeeper.thermalcollector.collector.ForegroundStartCollector;
import com.miui.powerkeeper.thermalcollector.collector.HeadCollector;
import com.miui.powerkeeper.thermalcollector.collector.ICollector;
import com.miui.powerkeeper.thermalcollector.collector.MicroPhoneCollector;
import com.miui.powerkeeper.thermalcollector.collector.MiscCollector;
import com.miui.powerkeeper.thermalcollector.collector.NetCollector;
import com.miui.powerkeeper.thermalcollector.collector.OldThermalCollector;
import com.miui.powerkeeper.thermalcollector.collector.PopUpWindowCollector;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollector;
import com.miui.powerkeeper.thermalcollector.collector.SettingCollector;
import com.miui.powerkeeper.thermalcollector.collector.ThermalCollector;
import com.miui.powerkeeper.thermalcollector.collector.ThermalPlusCollector;
import com.miui.powerkeeper.thermalcollector.event.Apps;
import com.miui.powerkeeper.thermalcollector.event.GenApps;
import com.miui.powerkeeper.thermalcollector.event.IEvent;
import com.miui.powerkeeper.thermalcollector.event.Scenario;
import com.miui.powerkeeper.thermalcollector.event.Total;
import com.miui.powerkeeper.thermalcollector.trigger.AmbientTempTrigger;
import com.miui.powerkeeper.thermalcollector.trigger.BatteryTrigger;
import com.miui.powerkeeper.thermalcollector.trigger.BroadTempTrigger;
import com.miui.powerkeeper.thermalcollector.trigger.FlashTempTrigger;
import com.miui.powerkeeper.thermalcollector.trigger.ForegroundTrigger;
import com.miui.powerkeeper.thermalcollector.trigger.ITrigger;
import java.io.PrintWriter;
import java.io.StringWriter;

/* JADX INFO: loaded from: classes.dex */
public class CollectorFactory {

    private static class CannotFindTargetClass extends RuntimeException {
        public CannotFindTargetClass(String str, String str2) {
            super("Class: " + str + " " + str2 + " not exit");
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:65:0x00e7  */
    private ICollector createCollectorReally(String str) {
        byte b2;
        switch (str.hashCode()) {
            case -2002188934:
                if (!str.equals(CollectorConfig.COLLECTOR_MICRO_PHONE_STATE)) {
                    b2 = -1;
                } else {
                    b2 = 19;
                }
                break;
            case -1983488107:
                if (!str.equals(CollectorConfig.COLLECTOR_DISPLAY)) {
                    b2 = -1;
                } else {
                    b2 = 2;
                }
                break;
            case -1947592405:
                if (!str.equals(CollectorConfig.COLLECTOR_FOREGROUND_END)) {
                    b2 = -1;
                } else {
                    b2 = 11;
                }
                break;
            case -1916869185:
                if (!str.equals(CollectorConfig.COLLECTOR_CAMERA_STATE)) {
                    b2 = -1;
                } else {
                    b2 = 18;
                }
                break;
            case -1740267327:
                if (!str.equals(CollectorConfig.COLLECTOR_BLUETOOTH)) {
                    b2 = -1;
                } else {
                    b2 = 1;
                }
                break;
            case -1669311197:
                if (!str.equals(CollectorConfig.COLLECTOR_SETTING)) {
                    b2 = -1;
                } else {
                    b2 = 6;
                }
                break;
            case -1139959004:
                if (!str.equals(CollectorConfig.COLLECTOR_THERMAL_PLUS)) {
                    b2 = -1;
                } else {
                    b2 = 8;
                }
                break;
            case -709829014:
                if (!str.equals("CollectorThermal")) {
                    b2 = -1;
                } else {
                    b2 = 7;
                }
                break;
            case -252429973:
                if (!str.equals(CollectorConfig.COLLECTOR_AMBIENT)) {
                    b2 = -1;
                } else {
                    b2 = 13;
                }
                break;
            case 151138285:
                if (!str.equals(CollectorConfig.COLLECTOR_HEAD)) {
                    b2 = -1;
                } else {
                    b2 = 3;
                }
                break;
            case 151291641:
                if (!str.equals(CollectorConfig.COLLECTOR_MISC)) {
                    b2 = -1;
                } else {
                    b2 = 4;
                }
                break;
            case 384334697:
                if (!str.equals(CollectorConfig.COLLECTOR_AUDIO)) {
                    b2 = -1;
                } else {
                    b2 = 0;
                }
                break;
            case 385076857:
                if (!str.equals(CollectorConfig.COLLECTOR_BOARD)) {
                    b2 = -1;
                } else {
                    b2 = 14;
                }
                break;
            case 388681603:
                if (!str.equals(CollectorConfig.COLLECTOR_FLASH)) {
                    b2 = -1;
                } else {
                    b2 = 15;
                }
                break;
            case 982548658:
                if (!str.equals(CollectorConfig.COLLECTOR_FOREGROUND_START)) {
                    b2 = -1;
                } else {
                    b2 = 12;
                }
                break;
            case 1047597359:
                if (!str.equals(CollectorConfig.COLLECTOR_POPUP_WINDOW)) {
                    b2 = -1;
                } else {
                    b2 = 16;
                }
                break;
            case 1508211645:
                if (!str.equals(CollectorConfig.COLLECTOR_OLD_THERMAL)) {
                    b2 = -1;
                } else {
                    b2 = 9;
                }
                break;
            case 1805996528:
                if (!str.equals(CollectorConfig.COLLECTOR_NET)) {
                    b2 = -1;
                } else {
                    b2 = 5;
                }
                break;
            case 1832484072:
                if (!str.equals(CollectorConfig.COLLECTOR_COMPOUND_SCENARIO)) {
                    b2 = -1;
                } else {
                    b2 = 17;
                }
                break;
            case 1875707677:
                if (!str.equals(CollectorConfig.COLLECTOR_SCENARIO)) {
                    b2 = -1;
                } else {
                    b2 = 10;
                }
                break;
            default:
                b2 = -1;
                break;
        }
        switch (b2) {
            case 0:
                return new AudioCollector();
            case 1:
                return new BluetoothCollector();
            case 2:
                return new DisplayCollector();
            case 3:
                return new HeadCollector();
            case 4:
                return new MiscCollector();
            case 5:
                return new NetCollector();
            case 6:
                return new SettingCollector();
            case 7:
                return new ThermalCollector();
            case 8:
                return new ThermalPlusCollector();
            case 9:
                return new OldThermalCollector();
            case 10:
                return new ScenarioCollector();
            case 11:
                return new ForegroundEndCollector();
            case 12:
                return new ForegroundStartCollector();
            case 13:
                return new AmbientCollector();
            case 14:
                return new BoardCollector();
            case 15:
                return new FlashCollector();
            case 16:
                return new PopUpWindowCollector();
            case 17:
                return new CompoundScenarioCollector();
            case 18:
                return new CameraStateCollector();
            case 19:
                return new MicroPhoneCollector();
            default:
                throw new CannotFindTargetClass("Collector", str);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:17:0x0033  */
    private IEvent createEventReally(String str) {
        byte b2;
        switch (str.hashCode()) {
            case -1585010888:
                if (!str.equals(CollectorConfig.EVENT_THERMAL_SCENARIO)) {
                    b2 = -1;
                } else {
                    b2 = 3;
                }
                break;
            case -81222174:
                if (!str.equals(CollectorConfig.EVENT_GEN_APPS)) {
                    b2 = -1;
                } else {
                    b2 = 0;
                }
                break;
            case 3000946:
                if (!str.equals("apps")) {
                    b2 = -1;
                } else {
                    b2 = 1;
                }
                break;
            case 110549828:
                if (!str.equals("total")) {
                    b2 = -1;
                } else {
                    b2 = 2;
                }
                break;
            default:
                b2 = -1;
                break;
        }
        if (b2 == 0) {
            return new GenApps();
        }
        if (b2 == 1) {
            return new Apps();
        }
        if (b2 == 2) {
            return new Total();
        }
        if (b2 == 3) {
            return new Scenario();
        }
        throw new CannotFindTargetClass("event", str);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:20:0x003e  */
    private ITrigger createTriggerReally(String str) {
        byte b2;
        switch (str.hashCode()) {
            case 48128725:
                if (!str.equals(CollectorConfig.TRIGGER_FOREGROUND)) {
                    b2 = -1;
                } else {
                    b2 = 3;
                }
                break;
            case 870355723:
                if (!str.equals(CollectorConfig.TRIGGER_BATTERY)) {
                    b2 = -1;
                } else {
                    b2 = 4;
                }
                break;
            case 1681594708:
                if (!str.equals(CollectorConfig.TRIGGER_FLASH_TEMP)) {
                    b2 = -1;
                } else {
                    b2 = 2;
                }
                break;
            case 1866210178:
                if (!str.equals(CollectorConfig.TRIGGER_BROAD_TEMP)) {
                    b2 = -1;
                } else {
                    b2 = 0;
                }
                break;
            case 2134300076:
                if (!str.equals(CollectorConfig.TRIGGER_AMBIENT_TEMP)) {
                    b2 = -1;
                } else {
                    b2 = 1;
                }
                break;
            default:
                b2 = -1;
                break;
        }
        if (b2 == 0) {
            return new BroadTempTrigger();
        }
        if (b2 == 1) {
            return new AmbientTempTrigger();
        }
        if (b2 == 2) {
            return new FlashTempTrigger();
        }
        if (b2 == 3) {
            return new ForegroundTrigger();
        }
        if (b2 == 4) {
            return new BatteryTrigger();
        }
        throw new CannotFindTargetClass("Trigger", str);
    }

    @NonNull
    public ICollector createCollector(String str) {
        try {
            return createCollectorReally(str);
        } catch (RuntimeException e2) {
            StringWriter stringWriter = new StringWriter();
            e2.printStackTrace(new PrintWriter(stringWriter));
            CollectorManager.getHistory().log(stringWriter.toString());
            return ICollector.sNullCollector;
        }
    }

    @NonNull
    public IEvent createEvent(String str) {
        try {
            return createEventReally(str);
        } catch (RuntimeException e2) {
            StringWriter stringWriter = new StringWriter();
            e2.printStackTrace(new PrintWriter(stringWriter));
            CollectorManager.getHistory().log(stringWriter.toString());
            return IEvent.sNullEvent;
        }
    }

    @NonNull
    public ITrigger createTrigger(String str) {
        try {
            return createTriggerReally(str);
        } catch (RuntimeException e2) {
            StringWriter stringWriter = new StringWriter();
            e2.printStackTrace(new PrintWriter(stringWriter));
            CollectorManager.getHistory().log(stringWriter.toString());
            return ITrigger.sNullTrigger;
        }
    }
}
