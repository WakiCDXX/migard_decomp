package com.miui.powerkeeper.thermalcollector.event;

import android.annotation.NonNull;
import android.os.Message;
import com.miui.powerkeeper.thermal.resource.Couple;
import com.miui.powerkeeper.thermalcollector.DTO.EventCloudDTO;
import com.miui.powerkeeper.thermalcollector.DTO.EventInfoDTO;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import com.miui.powerkeeper.thermalcollector.trigger.IRule;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import miui.process.ForegroundInfo;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class Apps extends BaseEvent {
    private static final String KEY_EXCEP = "excep";
    private static final String KEY_PKN = "pkn";
    private final ForegroundRule mForegroundRule = new ForegroundRule();
    private final JSONObject mEndData = new JSONObject();
    private final Set<String> mCollectorNames = new HashSet(2);

    private static class ForegroundRule implements IRule {
        private String mCurrentForeground;
        private long mDuration;
        private long mInterval;
        private long mLastChangeTime;
        private String mLastForeground;

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public String getName() {
            return CollectorConfig.RULE_APPS_FOREGROUND;
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
            long jCurrentTimeMillis = System.currentTimeMillis();
            long j2 = jCurrentTimeMillis - this.mLastChangeTime;
            this.mDuration = j2;
            this.mLastChangeTime = jCurrentTimeMillis;
            return j2 > this.mInterval && !this.mLastForeground.equals("null");
        }

        private ForegroundRule() {
            this.mCurrentForeground = "null";
            this.mLastForeground = "null";
            this.mLastChangeTime = 0L;
            this.mDuration = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$saveDataFromForeground$0(Couple couple) {
        return this.mCollectorNames.contains(couple.getK());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDataFromForeground$1(Couple couple) {
        saveJson(this.mEndData, (JSONObject) couple.getV());
    }

    private void putExtraInfo(JSONObject jSONObject) {
        try {
            jSONObject.put(KEY_EXCEP, jSONObject.getInt("KEY_BROAD_TEMP") * 1000 >= 48000);
            jSONObject.put(KEY_PKN, this.mForegroundRule.mLastForeground);
        } catch (Exception unused) {
        }
    }

    private void saveDataFromForeground(List<Couple<String, JSONObject>> list) {
        list.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.event.a
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f598a.lambda$saveDataFromForeground$0((Couple) obj);
            }
        }).forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.event.b
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f599a.lambda$saveDataFromForeground$1((Couple) obj);
            }
        });
        putExtraInfo(this.mEndData);
        commitData("apps", this.mEndData.toString());
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.BaseEvent, com.miui.powerkeeper.thermalcollector.event.IEvent, com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        printWriter.println("<<< Event Apps >>>");
        printWriter.println(this.mForegroundRule);
        printWriter.println(this.mCollectorNames);
        printWriter.println();
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
    public EventInfoDTO parseParameters(EventCloudDTO.EventParameter eventParameter) {
        if (!eventParameter.getEventName().equals("apps")) {
            return null;
        }
        long j2 = Long.parseLong(eventParameter.getExtraInfo());
        this.mForegroundRule.mInterval = j2 > 0 ? 1000 * j2 : 0L;
        this.mCollectorNames.clear();
        this.mCollectorNames.addAll((Collection) eventParameter.getCollectors().values().stream().flatMap(new com.miui.powerkeeper.feedbackcontrol.abnormallog.c()).collect(Collectors.toSet()));
        return new EventInfoDTO(eventParameter, new HashMap<String, IRule>(1) { // from class: com.miui.powerkeeper.thermalcollector.event.Apps.1
            {
                put(Apps.this.mForegroundRule.getName(), Apps.this.mForegroundRule);
            }
        });
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
    public void updateData(Set<String> set, @NonNull List<Couple<String, JSONObject>> list) {
        if (set.contains(CollectorConfig.RULE_APPS_FOREGROUND)) {
            saveDataFromForeground(list);
        }
    }
}
