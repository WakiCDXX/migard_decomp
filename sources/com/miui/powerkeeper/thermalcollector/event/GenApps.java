package com.miui.powerkeeper.thermalcollector.event;

import android.annotation.NonNull;
import android.os.Message;
import android.util.Log;
import com.miui.powerkeeper.thermal.resource.Couple;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.DTO.EventCloudDTO;
import com.miui.powerkeeper.thermalcollector.DTO.EventInfoDTO;
import com.miui.powerkeeper.thermalcollector.DTO.GenAppsCloudDTO;
import com.miui.powerkeeper.thermalcollector.DTO.GenAppsReportDTO;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import com.miui.powerkeeper.thermalcollector.trigger.IRule;
import com.xiaomi.mipush.sdk.stat.db.MessageInfoContract;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import miui.process.ForegroundInfo;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class GenApps extends BaseEvent {
    private static final String KEY_PKN = "pkn";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String TAG = "GenApps";
    private final boolean isV2;
    private final Set<String> mAmbientCollectorNames;
    private final AmbientRule mAmbientRule;
    private final JSONObject mAmbientTempData;
    private final Set<String> mBroadCollectorNames;
    private final JSONObject mBroadData;
    private final BroadRule mBroadRule;
    private boolean mCompoundScenarioTrigger;
    private final GenAppsReportDTO mDTO;
    private final JSONObject mEndData;
    private final Set<String> mFlashCollectorNames;
    private boolean mFlashStateTrigger;
    private final JSONObject mFlashTempData;
    private final FlashTempRule mFlashTempRule;
    private final Set<String> mForegroundEndCollectorNames;
    private final ForegroundRule mForegroundRule;
    private final Set<String> mForegroundStartCollectorNames;
    private boolean mPopUpWindowTrigger;
    private int mPrevCompoundScenarioId;
    private boolean mPrevFlashState;
    private final List<String> mProcessAmbientData;
    private final List<String> mProcessCompoundScenarioData;
    private final List<String> mProcessData;
    private final List<String> mProcessFlashData;
    private final List<String> mProcessPopupWindowData;
    private final JSONObject mStartData;
    private boolean mUndefCScenarioTrigger;
    private final String version;

    private class AmbientRule implements IRule {
        int mCurrentAmbientTemp;
        int mReference;

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public String getName() {
            return CollectorConfig.RULE_GEN_APPS_AMBIENT;
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public boolean isTriggerRule(Message message) {
            int i2;
            boolean z2 = false;
            if (message != null && message.what == -38) {
                int iIntValue = ((Integer) message.obj).intValue();
                String strValueOf = String.valueOf(iIntValue);
                if (iIntValue >= 1000 && strValueOf.length() == 4) {
                    int i3 = Integer.parseInt(strValueOf.substring(1)) / 10;
                    this.mCurrentAmbientTemp = i3;
                    if (!GenApps.this.mForegroundRule.mCurrentForeground.equals("SCREEN_OFF") ? !((i2 = this.mReference) > 35 ? Math.abs(i3 - i2) < 1 : Math.abs(i3 - i2) < 2) : Math.abs(i3 - this.mReference) > 3) {
                        z2 = true;
                    }
                    if (z2) {
                        this.mReference = i3;
                    }
                    return z2;
                }
                if (this.mCurrentAmbientTemp != iIntValue) {
                    this.mCurrentAmbientTemp = iIntValue;
                    return true;
                }
            }
            return false;
        }

        public void setReference() {
            this.mReference = this.mCurrentAmbientTemp;
        }

        @NonNull
        public String toString() {
            return "AmbientRule state: \n\tmReference: " + this.mReference + "\n\tmCurrentAmbientTemp: " + this.mCurrentAmbientTemp;
        }

        private AmbientRule() {
            this.mReference = 0;
            this.mCurrentAmbientTemp = 0;
        }
    }

    private class BroadRule implements IRule {
        int mCurrentBoardTemp;
        int mCurrentPopUpWindowState;
        int mReference;

        /* JADX INFO: Access modifiers changed from: private */
        public void setReference() {
            this.mReference = this.mCurrentBoardTemp;
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public String getName() {
            return CollectorConfig.RULE_GEN_APPS_BROAD;
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public boolean isTriggerRule(Message message) {
            int i2;
            int popUpWindowStatus;
            boolean z2 = false;
            if (message != null && message.what == -37) {
                int iIntValue = ((Integer) message.obj).intValue() / 1000;
                ScenarioCollectorUtils scenarioCollectorUtils = ScenarioCollectorUtils.getInstance();
                if (iIntValue > scenarioCollectorUtils.getMaxBoardTemp()) {
                    scenarioCollectorUtils.setMaxBoardTempInfo(iIntValue, System.currentTimeMillis());
                }
                this.mCurrentBoardTemp = iIntValue;
                if (CollectorManager.getInstance().isPopUpWindowCollector() && (popUpWindowStatus = scenarioCollectorUtils.getPopUpWindowStatus()) != this.mCurrentPopUpWindowState) {
                    this.mCurrentPopUpWindowState = popUpWindowStatus;
                    this.mReference = iIntValue;
                    GenApps.this.mPopUpWindowTrigger = true;
                    CollectorManager.getInstance().setBoardProcessTriggerId("3");
                    return true;
                }
                if (CollectorManager.getInstance().isCompoundScenarioCollector()) {
                    int currentScenarioId = ScenarioCollectorUtils.getCurrentScenarioId();
                    if (scenarioCollectorUtils.isCompoundScenarioId(currentScenarioId) && GenApps.this.mPrevCompoundScenarioId != currentScenarioId) {
                        this.mReference = iIntValue;
                        GenApps.this.mPrevCompoundScenarioId = currentScenarioId;
                        GenApps.this.mCompoundScenarioTrigger = true;
                        CollectorManager.getInstance().setBoardProcessTriggerId("4");
                        return true;
                    }
                    if (!ScenarioCollectorUtils.getInstance().peekUploadScenarioId().isEmpty()) {
                        GenApps.this.mUndefCScenarioTrigger = true;
                        CollectorManager.getInstance().setBoardProcessTriggerId("4");
                        return true;
                    }
                }
                if (CollectorManager.getInstance().isCameraStateCollector() && ScenarioCollectorUtils.getInstance().getCameraTrigger()) {
                    ScenarioCollectorUtils.getInstance().setCameraTrigger(false);
                    this.mReference = iIntValue;
                    CollectorManager.getInstance().setBoardProcessTriggerId("5");
                    return true;
                }
                if (CollectorManager.getInstance().isMicroPhoneStateCollector() && ScenarioCollectorUtils.getInstance().getMicroPhoneTrigger()) {
                    ScenarioCollectorUtils.getInstance().setMicroPhoneTrigger(false);
                    this.mReference = iIntValue;
                    CollectorManager.getInstance().setBoardProcessTriggerId("6");
                    return true;
                }
                if (!GenApps.this.mForegroundRule.mCurrentForeground.equals("SCREEN_OFF") ? !((i2 = this.mReference) <= 10 || i2 >= 55 ? Math.abs(iIntValue - i2) < 5 : i2 > 36 ? i2 > 43 ? i2 >= 55 || Math.abs(iIntValue - i2) < 2 : !GenApps.this.isV2 ? Math.abs(iIntValue - this.mReference) < 3 : Math.abs(iIntValue - this.mReference) < 2 : !GenApps.this.isV2 ? Math.abs(iIntValue - this.mReference) < 4 : Math.abs(iIntValue - this.mReference) < 3) : Math.abs(iIntValue - this.mReference) > 5) {
                    z2 = true;
                }
                if (z2) {
                    CollectorManager.getInstance().setBoardProcessTriggerId("0");
                    this.mReference = iIntValue;
                }
            }
            return z2;
        }

        @NonNull
        public String toString() {
            return "BroadRule state: \n\tmReference: " + this.mReference + "\n\tmCurrentBoardTemp: " + this.mCurrentBoardTemp;
        }

        private BroadRule() {
            this.mReference = 0;
            this.mCurrentBoardTemp = 0;
            this.mCurrentPopUpWindowState = 0;
        }
    }

    private class FlashTempRule implements IRule {
        int mCurrentFlashTemp;
        int mReference;

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public String getName() {
            return CollectorConfig.RULE_GEN_APPS_FLASH;
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public boolean isTriggerRule(Message message) {
            int i2;
            boolean z2 = false;
            if (message != null && message.what == -39) {
                int iIntValue = ((Integer) message.obj).intValue() / 1000;
                ScenarioCollectorUtils scenarioCollectorUtils = ScenarioCollectorUtils.getInstance();
                this.mCurrentFlashTemp = iIntValue;
                boolean flashState = ScenarioCollectorUtils.getInstance().getFlashState();
                if (iIntValue > scenarioCollectorUtils.getMaxFlashTemp()) {
                    scenarioCollectorUtils.setMaxFlashTempInfo(iIntValue, System.currentTimeMillis());
                }
                if (flashState != GenApps.this.mPrevFlashState) {
                    GenApps.this.mPrevFlashState = flashState;
                    if (iIntValue > scenarioCollectorUtils.getMaxFlashTemp() || System.currentTimeMillis() - scenarioCollectorUtils.getMaxFlashTempTimeStamp() > MessageInfoContract.TIMEOUT) {
                        GenApps.this.mFlashStateTrigger = true;
                        z2 = true;
                    }
                } else if (!GenApps.this.mForegroundRule.mCurrentForeground.equals("SCREEN_OFF") ? !((i2 = this.mReference) > 50 ? i2 > 80 ? Math.abs(iIntValue - i2) < 1 : Math.abs(iIntValue - i2) < 2 : Math.abs(iIntValue - i2) < 5) : Math.abs(iIntValue - this.mReference) > 5) {
                    z2 = true;
                }
                if (z2) {
                    this.mReference = iIntValue;
                }
                if (iIntValue > scenarioCollectorUtils.getMaxFlashTemp()) {
                    scenarioCollectorUtils.setMaxFlashTempInfo(iIntValue, System.currentTimeMillis());
                }
            }
            return z2;
        }

        public void setReference() {
            this.mReference = this.mCurrentFlashTemp;
        }

        @NonNull
        public String toString() {
            return "FlashTempRule state: \n\tmReference: " + this.mReference + "\n\tmCurrentFlashTemp: " + this.mCurrentFlashTemp;
        }

        private FlashTempRule() {
            this.mReference = 0;
            this.mCurrentFlashTemp = 0;
        }
    }

    private class ForegroundRule implements IRule {
        private String mCurrentForeground;
        private long mDuration;
        private long mInterval;
        private long mLastChangeTime;
        private String mLastForeground;

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public String getName() {
            return CollectorConfig.RULE_GEN_APPS_FOREGROUND;
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public boolean isTriggerRule(Message message) {
            String str;
            int i2 = message.what;
            if (i2 == -9) {
                str = ((ForegroundInfo) message.obj).mForegroundPackageName;
            } else if (i2 != -5) {
                str = i2 != -4 ? "" : this.mLastForeground;
            } else {
                str = "SCREEN_OFF";
            }
            if (this.mCurrentForeground.equals(str)) {
                return false;
            }
            this.mLastForeground = this.mCurrentForeground;
            this.mCurrentForeground = str;
            GenApps.this.mBroadRule.setReference();
            GenApps.this.mAmbientRule.setReference();
            GenApps.this.mFlashTempRule.setReference();
            long jCurrentTimeMillis = System.currentTimeMillis();
            this.mDuration = jCurrentTimeMillis - this.mLastChangeTime;
            this.mLastChangeTime = jCurrentTimeMillis;
            return true;
        }

        public boolean shouldBeCollect() {
            GenApps.this.mPrevCompoundScenarioId = ScenarioCollectorUtils.getCurrentScenarioId();
            if (!GenApps.this.mPopUpWindowTrigger && !GenApps.this.mFlashStateTrigger && !GenApps.this.mCompoundScenarioTrigger && !GenApps.this.mUndefCScenarioTrigger) {
                return this.mDuration > this.mInterval && !this.mLastForeground.equals("null");
            }
            GenApps.this.mPopUpWindowTrigger = false;
            GenApps.this.mFlashStateTrigger = false;
            GenApps.this.mCompoundScenarioTrigger = false;
            GenApps.this.mUndefCScenarioTrigger = false;
            return true;
        }

        @NonNull
        public String toString() {
            return "ForegroundRule{mInterval=" + this.mInterval + ", mCurrentForeground='" + this.mCurrentForeground + "', mLastForeground='" + this.mLastForeground + "', mLastChangeTime=" + this.mLastChangeTime + ", mDuration=" + this.mDuration + '}';
        }

        private ForegroundRule() {
            this.mCurrentForeground = "null";
            this.mLastForeground = "null";
            this.mLastChangeTime = 0L;
            this.mDuration = 0L;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public GenApps() {
        String dataCollectorVersion = CollectorManager.getDataCollectorVersion();
        this.version = dataCollectorVersion;
        this.isV2 = dataCollectorVersion.equals("2.0");
        this.mForegroundRule = new ForegroundRule();
        this.mBroadRule = new BroadRule();
        this.mAmbientRule = new AmbientRule();
        this.mFlashTempRule = new FlashTempRule();
        this.mBroadData = new JSONObject();
        this.mAmbientTempData = new JSONObject();
        this.mFlashTempData = new JSONObject();
        this.mEndData = new JSONObject();
        this.mStartData = new JSONObject();
        ArrayList arrayList = new ArrayList();
        this.mProcessData = arrayList;
        this.mProcessAmbientData = new ArrayList();
        this.mProcessFlashData = new ArrayList();
        this.mProcessPopupWindowData = new ArrayList();
        this.mProcessCompoundScenarioData = new ArrayList();
        this.mDTO = new GenAppsReportDTO(arrayList);
        this.mForegroundStartCollectorNames = new HashSet(4);
        this.mForegroundEndCollectorNames = new HashSet(7);
        this.mBroadCollectorNames = new HashSet(4);
        this.mAmbientCollectorNames = new HashSet(4);
        this.mFlashCollectorNames = new HashSet(4);
        this.mPrevFlashState = false;
        this.mFlashStateTrigger = false;
        this.mPopUpWindowTrigger = false;
        this.mCompoundScenarioTrigger = false;
        this.mUndefCScenarioTrigger = false;
        this.mPrevCompoundScenarioId = -1;
    }

    private void clearCache() {
        this.mDTO.clear();
        this.mProcessFlashData.clear();
        this.mProcessCompoundScenarioData.clear();
        this.mProcessPopupWindowData.clear();
        this.mProcessAmbientData.clear();
    }

    private void commitData() {
        this.mDTO.setStart(this.mStartData.toString());
        this.mDTO.setEnd(this.mEndData.toString());
        commitData(CollectorConfig.EVENT_GEN_APPS, this.mGson.toJson(this.mDTO));
        List<String> list = this.mProcessFlashData;
        if (list != null && list.size() != 0) {
            commitData(CollectorConfig.EVENT_GEN_APPS_FLASH_TEMP, postProcessCommitData(this.mProcessFlashData).toString());
        }
        List<String> list2 = this.mProcessAmbientData;
        if (list2 != null && list2.size() != 0) {
            commitData(CollectorConfig.EVENT_GEN_APPS_AMBIENT_TEMP, postProcessCommitData(this.mProcessAmbientData).toString());
        }
        List<String> list3 = this.mProcessPopupWindowData;
        if (list3 != null && list3.size() != 0) {
            commitData(CollectorConfig.EVENT_GEN_APPS_POPUP_WINDOW, postProcessCommitData(this.mProcessPopupWindowData).toString());
        }
        List<String> list4 = this.mProcessCompoundScenarioData;
        if (list4 != null && list4.size() != 0) {
            commitData(CollectorConfig.EVENT_GEN_APPS_COMPOUND_SCENARIO, postProcessCommitData(this.mProcessCompoundScenarioData).toString());
        }
        clearCache();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$saveDataForAmbient$2(Couple couple) {
        return this.mAmbientCollectorNames.contains(couple.getK());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDataForAmbient$3(Couple couple) {
        saveJson(this.mAmbientTempData, (JSONObject) couple.getV());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$saveDataFormBroad$0(Couple couple) {
        return this.mBroadCollectorNames.contains(couple.getK());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDataFormBroad$1(Couple couple) {
        saveJson(this.mBroadData, (JSONObject) couple.getV());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$saveDataFormFlash$4(Couple couple) {
        return this.mFlashCollectorNames.contains(couple.getK());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDataFormFlash$5(Couple couple) {
        saveJson(this.mFlashTempData, (JSONObject) couple.getV());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$saveForegroundEndData$6(Couple couple) {
        return this.mForegroundEndCollectorNames.contains(couple.getK());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveForegroundEndData$7(Couple couple) {
        saveJson(this.mEndData, (JSONObject) couple.getV());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$saveForegroundStartData$8(Couple couple) {
        return this.mForegroundStartCollectorNames.contains(couple.getK());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveForegroundStartData$9(Couple couple) {
        saveJson(this.mStartData, (JSONObject) couple.getV());
    }

    private JSONObject postProcessCommitData(List<String> list) {
        JSONObject jSONObject = new JSONObject();
        try {
            if (this.mStartData.optString(KEY_PKN, "").isEmpty()) {
                jSONObject.put(KEY_PKN, "null");
            } else {
                jSONObject.put(KEY_PKN, this.mStartData.getString(KEY_PKN));
            }
            if (this.mEndData.optString("timestamp", "").isEmpty()) {
                jSONObject.put("timestamp", "null");
            } else {
                jSONObject.put("timestamp", this.mEndData.getString("timestamp"));
            }
            jSONObject.put(CollectorConfig.KEY_PROCESS_TRIGGER_PARAMS, list.toString());
        } catch (Exception unused) {
            Log.e(TAG, "postProcessCommitData error!");
        }
        return jSONObject;
    }

    private void processDataToDeal(JSONObject jSONObject) {
        try {
            if (jSONObject.optString(CollectorConfig.KEY_PROCESS_TRIGGER_ID, "").isEmpty()) {
                this.mProcessData.add(jSONObject.toString());
            } else {
                String str = (String) jSONObject.remove(CollectorConfig.KEY_PROCESS_TRIGGER_ID);
                if ("3".equals(str)) {
                    this.mProcessPopupWindowData.add(jSONObject.toString());
                } else if ("4".equals(str)) {
                    this.mProcessCompoundScenarioData.add(jSONObject.toString());
                } else {
                    this.mProcessData.add(jSONObject.toString());
                }
            }
        } catch (Exception unused) {
            Log.e(TAG, "processDataToDeal error!");
        }
    }

    private void putPkn(JSONObject jSONObject) {
        try {
            jSONObject.put(KEY_PKN, this.mForegroundRule.mCurrentForeground);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void saveDataForAmbient(List<Couple<String, JSONObject>> list) {
        list.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.event.d
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f600a.lambda$saveDataForAmbient$2((Couple) obj);
            }
        }).forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.event.e
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f601a.lambda$saveDataForAmbient$3((Couple) obj);
            }
        });
        if (!this.mAmbientTempData.optString(CollectorConfig.KEY_PROCESS_TRIGGER_ID, "").isEmpty()) {
            this.mAmbientTempData.remove(CollectorConfig.KEY_PROCESS_TRIGGER_ID);
        }
        this.mProcessAmbientData.add(this.mAmbientTempData.toString());
    }

    private void saveDataFormBroad(List<Couple<String, JSONObject>> list) {
        list.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.event.f
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f602a.lambda$saveDataFormBroad$0((Couple) obj);
            }
        }).forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.event.g
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f603a.lambda$saveDataFormBroad$1((Couple) obj);
            }
        });
        if (this.isV2) {
            processDataToDeal(this.mBroadData);
        } else {
            this.mProcessData.add(this.mBroadData.toString());
        }
    }

    private void saveDataFormFlash(List<Couple<String, JSONObject>> list) {
        list.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.event.h
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f604a.lambda$saveDataFormFlash$4((Couple) obj);
            }
        }).forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.event.i
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f605a.lambda$saveDataFormFlash$5((Couple) obj);
            }
        });
        if (!this.mFlashTempData.optString(CollectorConfig.KEY_PROCESS_TRIGGER_ID, "").isEmpty()) {
            this.mFlashTempData.remove(CollectorConfig.KEY_PROCESS_TRIGGER_ID);
        }
        this.mProcessFlashData.add(this.mFlashTempData.toString());
    }

    private void saveDataFromForeground(List<Couple<String, JSONObject>> list) {
        saveForegroundEndData(list);
        saveForegroundStartData(list);
    }

    private void saveForegroundEndData(List<Couple<String, JSONObject>> list) {
        ScenarioCollectorUtils.getInstance().setMaxFlashTempInfo(0, 0L);
        ScenarioCollectorUtils.getInstance().setMaxBoardTempInfo(0, 0L);
        if (!this.mForegroundRule.shouldBeCollect()) {
            clearCache();
            return;
        }
        list.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.event.l
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f608a.lambda$saveForegroundEndData$6((Couple) obj);
            }
        }).forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.event.m
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f609a.lambda$saveForegroundEndData$7((Couple) obj);
            }
        });
        ScenarioCollectorUtils.getInstance().setFirstBootState(false);
        commitData();
    }

    private void saveForegroundStartData(List<Couple<String, JSONObject>> list) {
        putPkn(this.mStartData);
        list.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.event.j
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f606a.lambda$saveForegroundStartData$8((Couple) obj);
            }
        }).forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.event.k
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f607a.lambda$saveForegroundStartData$9((Couple) obj);
            }
        });
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.BaseEvent, com.miui.powerkeeper.thermalcollector.event.IEvent, com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        printWriter.println("<<< Event GenApps >>>");
        printWriter.println(this.mForegroundRule);
        printWriter.println(this.mBroadRule);
        printWriter.println(this.mAmbientRule);
        printWriter.println(this.mForegroundStartCollectorNames);
        printWriter.println(this.mForegroundEndCollectorNames);
        printWriter.println(this.mBroadCollectorNames);
        printWriter.println(this.mAmbientCollectorNames);
        printWriter.println(this.mFlashCollectorNames);
        printWriter.println();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:43:0x00d1  */
    @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
    public EventInfoDTO parseParameters(EventCloudDTO.EventParameter eventParameter) {
        byte b2;
        if (!eventParameter.getEventName().equals(CollectorConfig.EVENT_GEN_APPS)) {
            throw new RuntimeException("error data in GenAppsCloudDto");
        }
        String extraInfo = eventParameter.getExtraInfo();
        Set<String> rule = eventParameter.getRule();
        GenAppsCloudDTO genAppsCloudDTO = (GenAppsCloudDTO) this.mGson.fromJson(extraInfo, GenAppsCloudDTO.class);
        if (genAppsCloudDTO == null) {
            throw new RuntimeException("error data in GenAppsCloudDto");
        }
        Set<String> startNames = genAppsCloudDTO.getStartNames();
        Set<String> endNames = genAppsCloudDTO.getEndNames();
        Set<String> tempNames = genAppsCloudDTO.getTempNames();
        Set<String> ambientNames = genAppsCloudDTO.getAmbientNames();
        Set<String> flashTempNames = genAppsCloudDTO.getFlashTempNames();
        this.mForegroundStartCollectorNames.clear();
        if (startNames != null) {
            this.mForegroundStartCollectorNames.addAll(startNames);
        }
        this.mForegroundEndCollectorNames.clear();
        if (endNames != null) {
            this.mForegroundEndCollectorNames.addAll(endNames);
        }
        this.mBroadCollectorNames.clear();
        if (tempNames != null) {
            this.mBroadCollectorNames.addAll(tempNames);
        }
        this.mAmbientCollectorNames.clear();
        if (ambientNames != null) {
            this.mAmbientCollectorNames.addAll(ambientNames);
        }
        this.mFlashCollectorNames.clear();
        if (flashTempNames != null) {
            this.mFlashCollectorNames.addAll(flashTempNames);
        }
        this.mForegroundRule.mInterval = genAppsCloudDTO.getInterval() > 0 ? 1000 * genAppsCloudDTO.getInterval() : 0L;
        HashMap map = new HashMap(2);
        for (String str : rule) {
            switch (str.hashCode()) {
                case -1434436293:
                    if (str.equals(CollectorConfig.RULE_GEN_APPS_AMBIENT)) {
                        b2 = 2;
                    } else {
                        b2 = -1;
                    }
                    break;
                case -1135602203:
                    if (str.equals(CollectorConfig.RULE_GEN_APPS_BROAD)) {
                        b2 = 0;
                    } else {
                        b2 = -1;
                    }
                    break;
                case -1132099757:
                    if (str.equals(CollectorConfig.RULE_GEN_APPS_FLASH)) {
                        b2 = 3;
                    } else {
                        b2 = -1;
                    }
                    break;
                case -43452480:
                    if (str.equals(CollectorConfig.RULE_GEN_APPS_FOREGROUND)) {
                        b2 = 1;
                    } else {
                        b2 = -1;
                    }
                    break;
                default:
                    b2 = -1;
                    break;
            }
            if (b2 == 0) {
                map.put(this.mBroadRule.getName(), this.mBroadRule);
            } else if (b2 == 1) {
                map.put(this.mForegroundRule.getName(), this.mForegroundRule);
            } else if (b2 == 2) {
                map.put(this.mAmbientRule.getName(), this.mAmbientRule);
            } else if (b2 == 3) {
                map.put(this.mFlashTempRule.getName(), this.mFlashTempRule);
            }
        }
        return new EventInfoDTO(eventParameter, map);
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
    public void updateData(Set<String> set, @NonNull List<Couple<String, JSONObject>> list) {
        if (set.contains(CollectorConfig.RULE_GEN_APPS_BROAD)) {
            saveDataFormBroad(list);
            return;
        }
        if (set.contains(CollectorConfig.RULE_GEN_APPS_FOREGROUND)) {
            saveDataFromForeground(list);
        } else if (set.contains(CollectorConfig.RULE_GEN_APPS_AMBIENT)) {
            saveDataForAmbient(list);
        } else if (set.contains(CollectorConfig.RULE_GEN_APPS_FLASH)) {
            saveDataFormFlash(list);
        }
    }
}
