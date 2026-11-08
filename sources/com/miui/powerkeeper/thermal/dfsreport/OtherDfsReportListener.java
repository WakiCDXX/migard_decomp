package com.miui.powerkeeper.thermal.dfsreport;

import android.util.Log;
import com.miui.misight.MiEvent;
import com.miui.misight.MiSight;
import com.miui.powerkeeper.thermal.dfscheck.CheckUtils;
import com.miui.powerkeeper.thermal.dfscheck.CollectionData;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class OtherDfsReportListener implements ThermalDfsInterface.EventListener {
    private static final String TAG = "powerkeeper.dfsolisten";
    public static OtherDfsReportListener otherDfsReportListenerInstance;
    private Map<String, String> mEventDataMap;
    private String mPackageName;
    private ReportEventNum mReportEventNum;
    private int mReportType;
    private String mScenarioName;
    private CollectionData mCollectionData = CollectionData.getCollectionDataInstance();
    private CheckUtils mCheckUtils = CheckUtils.getCheckUtilsInstance();

    /* JADX INFO: renamed from: com.miui.powerkeeper.thermal.dfsreport.OtherDfsReportListener$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$powerkeeper$thermal$dfsreport$ReportEventNum;

        static {
            int[] iArr = new int[ReportEventNum.values().length];
            $SwitchMap$com$miui$powerkeeper$thermal$dfsreport$ReportEventNum = iArr;
            try {
                iArr[ReportEventNum.SceneNumErrorNum.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$powerkeeper$thermal$dfsreport$ReportEventNum[ReportEventNum.PhoneCallSceneErrorNUM.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$powerkeeper$thermal$dfsreport$ReportEventNum[ReportEventNum.PerPowerModeErrorNum.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$powerkeeper$thermal$dfsreport$ReportEventNum[ReportEventNum.IecSceneErrorNum.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$miui$powerkeeper$thermal$dfsreport$ReportEventNum[ReportEventNum.CloudControlErrorNum.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$miui$powerkeeper$thermal$dfsreport$ReportEventNum[ReportEventNum.OtherErrorNum.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    private OtherDfsReportListener() {
    }

    public static synchronized OtherDfsReportListener getOtherDfsReportListenerInstance() {
        try {
            if (otherDfsReportListenerInstance == null) {
                otherDfsReportListenerInstance = new OtherDfsReportListener();
            }
        } catch (Throwable th) {
            throw th;
        }
        return otherDfsReportListenerInstance;
    }

    @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.EventListener
    public void onEventReceived(DfsEventData dfsEventData) {
        if (dfsEventData == null) {
            Log.d(TAG, "Data is null or build version is early N");
            return;
        }
        Log.d(TAG, "get notifier run received event " + dfsEventData.getReportEventType().getDataNum());
        ReportEventNum reportEventType = dfsEventData.getReportEventType();
        this.mReportEventNum = reportEventType;
        this.mReportType = reportEventType.getDataNum();
        this.mEventDataMap = dfsEventData.getData();
        this.mPackageName = this.mCollectionData.getPackageName();
        this.mScenarioName = this.mCollectionData.getScenarioName();
        switch (AnonymousClass1.$SwitchMap$com$miui$powerkeeper$thermal$dfsreport$ReportEventNum[this.mReportEventNum.ordinal()]) {
            case 1:
                sendSceneNumErrorNum();
                break;
            case 2:
                sendPhoneCallSceneErrorNum();
                break;
            case 3:
                sendPerPowerModeErrorNum();
                break;
            case 4:
                sendIecSceneErrorNum();
                break;
            case 5:
                sendCloudControlErrorNum();
                break;
            case 6:
                sendOtherErrorNum();
                break;
        }
    }

    public void sendCloudControlErrorNum() {
        MiEvent miEvent = new MiEvent(this.mReportType);
        miEvent.addStr("mode", this.mEventDataMap.getOrDefault("mode", "none"));
        miEvent.addStr("region", this.mEventDataMap.getOrDefault("region", "none"));
        miEvent.addStr("errType", this.mEventDataMap.getOrDefault("errType", "none"));
        miEvent.addStr("errorinfo", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        MiSight.sendEvent(miEvent);
        this.mCollectionData.setDumpMap(this.mReportType + " ", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        Log.d(TAG, "send report " + this.mReportType);
    }

    public void sendIecSceneErrorNum() {
        MiEvent miEvent = new MiEvent(this.mReportType);
        miEvent.addStr("packageName", this.mPackageName);
        miEvent.addStr("scenarioName", this.mScenarioName);
        Boolean boolTransToBoolean = this.mCheckUtils.transToBoolean(TAG, this.mEventDataMap.get("IECtest"));
        if (boolTransToBoolean != null) {
            miEvent.addBool("IECtest", boolTransToBoolean.booleanValue());
        }
        miEvent.addStr("thermalId", this.mEventDataMap.getOrDefault("thermalId", "-2"));
        miEvent.addStr("errorinfo", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        MiSight.sendEvent(miEvent);
        this.mCollectionData.setDumpMap(this.mReportType + " ", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        Log.d(TAG, "send report " + this.mReportType);
    }

    public void sendOtherErrorNum() {
        MiEvent miEvent = new MiEvent(this.mReportType);
        miEvent.addStr("type", this.mEventDataMap.getOrDefault("type", "none"));
        miEvent.addStr("errorinfo", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        MiSight.sendEvent(miEvent);
        this.mCollectionData.setDumpMap(this.mReportType + " ", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        Log.d(TAG, "send report " + this.mReportType);
    }

    public void sendPerPowerModeErrorNum() {
        MiEvent miEvent = new MiEvent(this.mReportType);
        miEvent.addStr("packageName", this.mPackageName);
        miEvent.addStr("scenarioName", this.mScenarioName);
        Boolean boolTransToBoolean = this.mCheckUtils.transToBoolean(TAG, this.mEventDataMap.get("isPerformance"));
        if (boolTransToBoolean != null) {
            miEvent.addBool("isPerformance", boolTransToBoolean.booleanValue());
        }
        miEvent.addStr("thermalId", this.mEventDataMap.getOrDefault("thermalId", "-2"));
        miEvent.addStr("errorinfo", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        MiSight.sendEvent(miEvent);
        this.mCollectionData.setDumpMap(this.mReportType + " ", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        Log.d(TAG, "send report " + this.mReportType);
    }

    public void sendPhoneCallSceneErrorNum() {
        MiEvent miEvent = new MiEvent(this.mReportType);
        miEvent.addStr("packageName", this.mPackageName);
        miEvent.addStr("scenarioName", this.mScenarioName);
        Boolean boolTransToBoolean = this.mCheckUtils.transToBoolean(TAG, this.mEventDataMap.get("callingState"));
        if (boolTransToBoolean != null) {
            miEvent.addBool("callingState", boolTransToBoolean.booleanValue());
        }
        miEvent.addStr("thermalId", this.mEventDataMap.getOrDefault("thermalId", "-2"));
        miEvent.addStr("errorinfo", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        MiSight.sendEvent(miEvent);
        this.mCollectionData.setDumpMap(this.mReportType + " ", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        Log.d(TAG, "send report " + this.mReportType);
    }

    public void sendSceneNumErrorNum() {
        MiEvent miEvent = new MiEvent(this.mReportType);
        miEvent.addStr("packageName", this.mPackageName);
        miEvent.addStr("scenarioName", this.mScenarioName);
        miEvent.addStr("thermalId", this.mEventDataMap.getOrDefault("thermalId", "-2"));
        miEvent.addStr("errorinfo", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        MiSight.sendEvent(miEvent);
        this.mCollectionData.setDumpMap(this.mReportType + " ", this.mEventDataMap.getOrDefault("errorinfo", "none"));
        Log.d(TAG, "send report " + this.mReportType);
    }
}
