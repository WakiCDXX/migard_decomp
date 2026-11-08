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
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class Total extends BaseEvent {
    private final JSONObject mData = new JSONObject();
    private final CapacityRule mCapacityRule = new CapacityRule();
    private final Set<String> mCollectorNames = new HashSet(2);

    private static class CapacityRule implements IRule {
        private long mBattleChangedLevel;
        private long mLastTotalCapacity;

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public String getName() {
            return CollectorConfig.RULE_TOTAL_BATTERY;
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public boolean isTriggerRule(Message message) {
            if (this.mBattleChangedLevel <= 0) {
                return false;
            }
            long j2 = ((int[]) message.obj)[2];
            if (Math.abs(j2 - this.mLastTotalCapacity) <= this.mBattleChangedLevel) {
                return false;
            }
            this.mLastTotalCapacity = j2;
            return true;
        }

        @NonNull
        public String toString() {
            return "CapacityRule{mBattleChangedLevel=" + this.mBattleChangedLevel + ", mLastTotalCapacity=" + this.mLastTotalCapacity + '}';
        }

        private CapacityRule() {
            this.mLastTotalCapacity = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$saveDataFromCapacity$0(Couple couple) {
        return this.mCollectorNames.contains(couple.getK());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDataFromCapacity$1(Couple couple) {
        saveJson(this.mData, (JSONObject) couple.getV());
    }

    private void saveDataFromCapacity(List<Couple<String, JSONObject>> list) {
        list.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.event.t
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f611a.lambda$saveDataFromCapacity$0((Couple) obj);
            }
        }).forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.event.u
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f612a.lambda$saveDataFromCapacity$1((Couple) obj);
            }
        });
        commitData("total", this.mData.toString());
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.BaseEvent, com.miui.powerkeeper.thermalcollector.event.IEvent, com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        printWriter.println("<<< Event Totals >>>");
        printWriter.println(this.mCapacityRule);
        printWriter.println(this.mCollectorNames);
        printWriter.println();
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
    public EventInfoDTO parseParameters(EventCloudDTO.EventParameter eventParameter) {
        if (!eventParameter.getEventName().equals("total")) {
            return null;
        }
        this.mCapacityRule.mBattleChangedLevel = Long.parseLong(eventParameter.getExtraInfo());
        this.mCollectorNames.clear();
        this.mCollectorNames.addAll((Collection) eventParameter.getCollectors().values().stream().flatMap(new com.miui.powerkeeper.feedbackcontrol.abnormallog.c()).collect(Collectors.toSet()));
        return new EventInfoDTO(eventParameter, new HashMap<String, IRule>(1) { // from class: com.miui.powerkeeper.thermalcollector.event.Total.1
            {
                put(Total.this.mCapacityRule.getName(), Total.this.mCapacityRule);
            }
        });
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
    public void updateData(Set<String> set, List<Couple<String, JSONObject>> list) {
        if (set.contains(CollectorConfig.RULE_TOTAL_BATTERY)) {
            saveDataFromCapacity(list);
        }
    }
}
