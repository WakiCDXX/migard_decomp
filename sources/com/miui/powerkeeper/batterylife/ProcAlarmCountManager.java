package com.miui.powerkeeper.batterylife;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import com.miui.powerkeeper.DeviceIdlePolicyHelper;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.data.AlarmData;
import com.miui.powerkeeper.event.EventMonitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import miui.process.ForegroundInfo;
import miui.process.ProcessManager;

/* JADX INFO: loaded from: classes.dex */
public class ProcAlarmCountManager {
    public static final String TAG = "ProcAlarmCountManager";
    private Context mContext;
    private SparseArray<AlarmData> mAlarmDataMap = new SparseArray<>();
    private EventMonitor.EventCallback eventCallback = new EventMonitor.EventCallback() { // from class: com.miui.powerkeeper.batterylife.ProcAlarmCountManager.1
        @Override // com.miui.powerkeeper.event.EventMonitor.EventCallback
        public void notifyEvent(int i2, Bundle bundle) {
            if (bundle == null) {
                Log.d(ProcAlarmCountManager.TAG, "EventMonitor.RESOURCE_ID_AlARM_TRIGGER bundle = null");
                return;
            }
            String string = bundle.getString("sourcePkg");
            int i3 = bundle.getInt("creatorUid");
            bundle.getString("statsTag");
            bundle.getLong("elapsedRealtime");
            String string2 = bundle.getString("type");
            ForegroundInfo foregroundInfo = ProcessManager.getForegroundInfo();
            boolean z2 = string.equals(foregroundInfo == null ? null : foregroundInfo.mForegroundPackageName) && (foregroundInfo == null ? -1 : foregroundInfo.mForegroundUid) == i3;
            AlarmData alarmData = (AlarmData) ProcAlarmCountManager.this.mAlarmDataMap.get(i3);
            if (alarmData == null) {
                alarmData = new AlarmData(string, i3);
                ProcAlarmCountManager.this.mAlarmDataMap.put(i3, alarmData);
            }
            if (z2) {
                alarmData.setForegroundCount(alarmData.getForegroundCount() + 1);
            } else {
                alarmData.setBackgroundCount(alarmData.getBackgroundCount() + 1);
                if (!PowerKeeperManager.getInstance().isCharging()) {
                    alarmData.setBackgroundUnChargeCount(alarmData.getBackgroundUnChargeCount() + 1);
                }
                if ("RTC_WAKEUP".equals(string2) || "ELAPSED_WAKEUP".equals(string2)) {
                    alarmData.setBackgroundWakeupCount(alarmData.getBackgroundWakeupCount() + 1);
                } else {
                    alarmData.setBackgroundNotWakeupCount(alarmData.getBackgroundNotWakeupCount() + 1);
                }
            }
            alarmData.setWhitelistApp(DeviceIdlePolicyHelper.i(ProcAlarmCountManager.this.mContext).k(string));
        }
    };

    ProcAlarmCountManager(Context context) {
        this.mContext = context;
        EventMonitor.getInstance().registerCallback(128, this.eventCallback);
    }

    public void destroy() {
        EventMonitor.getInstance().unregisterCallback(128, this.eventCallback);
        this.mAlarmDataMap.clear();
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < this.mAlarmDataMap.size(); i2++) {
            sb.append(this.mAlarmDataMap.valueAt(i2).toString() + '\n');
        }
        return sb.toString();
    }

    public void resetAllData() {
        this.mAlarmDataMap.clear();
    }

    public List<AlarmData> sortDataToList() {
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.mAlarmDataMap.size(); i2++) {
            arrayList.add(this.mAlarmDataMap.valueAt(i2));
        }
        Collections.sort(arrayList);
        return arrayList;
    }
}
