package com.miui.powerkeeper.thermalcollector.trigger;

import android.annotation.NonNull;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.DTO.EventInfoDTO;
import com.miui.powerkeeper.thermalcollector.collector.f;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseTrigger implements ITrigger {
    private CollectorManager.TriggerCallback mCallback;
    protected final Handler mHandler = new TriggerHandler(CollectorManager.getThread().getLooper());
    private final Map<String, String> mEvents = new ArrayMap(2);
    private final Map<String, IRule> mRules = new ArrayMap(2);
    private final Map<String, Set<String>> mCollectorNames = new ArrayMap(6);

    private class TriggerHandler extends Handler {
        public TriggerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            BaseTrigger.this.onStateChanged(message);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$handleRuleMessage$2(Message message, Map.Entry entry) {
        return ((IRule) entry.getValue()).isTriggerRule(message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$removeRule$0(String str, Map.Entry entry) {
        return ((String) entry.getValue()).equals(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeRule$1(String str) {
        this.mEvents.remove(str);
        this.mRules.remove(str);
        this.mCollectorNames.remove(str);
    }

    @Override // com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        printWriter.println("Using Trigger: " + getClass().getName());
    }

    protected final void handleRuleMessage(final Message message) {
        Set<String> set = (Set) this.mRules.entrySet().stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.trigger.a
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return BaseTrigger.lambda$handleRuleMessage$2(message, (Map.Entry) obj);
            }
        }).map(new f()).collect(Collectors.toSet());
        if (set.isEmpty()) {
            return;
        }
        Stream<String> stream = set.stream();
        final Map<String, Set<String>> map = this.mCollectorNames;
        Objects.requireNonNull(map);
        this.mCallback.collect(set, (Set) stream.map(new Function() { // from class: com.miui.powerkeeper.thermalcollector.trigger.b
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (Set) map.get((String) obj);
            }
        }).filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.trigger.c
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((Set) obj);
            }
        }).flatMap(new com.miui.powerkeeper.feedbackcontrol.abnormallog.c()).collect(Collectors.toSet()));
    }

    protected abstract void onStateChanged(@NonNull Message message);

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public final boolean readyBeDestroy() {
        if (!this.mRules.isEmpty()) {
            return false;
        }
        stopWork();
        return true;
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public final synchronized void removeRule(final String str) {
        ((List) this.mEvents.entrySet().stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.trigger.d
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return BaseTrigger.lambda$removeRule$0(str, (Map.Entry) obj);
            }
        }).map(new f()).collect(Collectors.toList())).forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.trigger.e
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f628a.lambda$removeRule$1((String) obj);
            }
        });
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public final void setCallback(CollectorManager.TriggerCallback triggerCallback) {
        this.mCallback = triggerCallback;
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public final synchronized void setRule(String str, EventInfoDTO eventInfoDTO) {
        try {
            IRule iRule = eventInfoDTO.getTriggers().get(str);
            Set<String> set = eventInfoDTO.getRuleMap().get(iRule);
            if (iRule == null || set == null) {
                throw new RuntimeException("error in set rule");
            }
            String name = iRule.getName();
            String name2 = eventInfoDTO.getName();
            if (!this.mEvents.containsKey(name)) {
                this.mEvents.put(name, name2);
            }
            if (!this.mRules.containsKey(name)) {
                this.mRules.put(name, iRule);
            }
            if (!this.mCollectorNames.containsKey(name)) {
                this.mCollectorNames.put(name, new HashSet());
            }
            this.mCollectorNames.get(name).addAll(set);
        } catch (Throwable th) {
            throw th;
        }
    }
}
