package com.miui.powerkeeper.thermal.dfsreport;

import android.os.Handler;

/* JADX INFO: loaded from: classes.dex */
public class ThermalDfsInterface {

    public interface DfsDataInterface {
        DfsEventData creatData(ReportEventNum reportEventNum);
    }

    public interface EventListener {
        void onEventReceived(DfsEventData dfsEventData);
    }

    public interface ReportDfsEventInterface {
        void notifierListener(EventNotifier eventNotifier);
    }

    public interface deviceRegister {
        void registerDeviceInMap(String str, String str2);

        void unRegisterDeviceInMap(String str);
    }

    public interface dfsAbnormalCheckRegister {
        void registerCallbackInArray(ThermalAbnormalCallback thermalAbnormalCallback);

        void unRegisterCallbackInArray(ThermalAbnormalCallback thermalAbnormalCallback);
    }

    public interface dfsCheck {
        boolean checkTarget();
    }

    public interface ntcRegister {
        void registerNtcInMap(String str, String str2);

        void unRegisterNtcInMap(String str);
    }

    public static class ThermalAbnormalCallback {
        public dfsCheck mDfsc;
        public Handler mHandler;

        public ThermalAbnormalCallback() {
            clearStatus();
        }

        public void clearStatus() {
            this.mHandler = null;
            this.mDfsc = null;
        }

        public ThermalAbnormalCallback(Handler handler, dfsCheck dfscheck) {
            this.mHandler = handler;
            this.mDfsc = dfscheck;
        }
    }
}
