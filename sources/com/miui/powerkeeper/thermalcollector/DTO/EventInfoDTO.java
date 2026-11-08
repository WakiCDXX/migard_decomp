package com.miui.powerkeeper.thermalcollector.DTO;

import com.miui.powerkeeper.feedbackcontrol.abnormallog.c;
import com.miui.powerkeeper.thermalcollector.trigger.IRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes.dex */
public class EventInfoDTO {
    private final String mName;
    private final Map<IRule, Set<String>> mRules;
    private final Map<String, IRule> mTriggers;

    public EventInfoDTO() {
        this.mName = "";
        this.mRules = Collections.emptyMap();
        this.mTriggers = Collections.emptyMap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(EventCloudDTO.EventParameter eventParameter, Map.Entry entry) {
        String str = (String) entry.getKey();
        this.mRules.put((IRule) entry.getValue(), eventParameter.getCollectors().get(str));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Map map, Map.Entry entry) {
        String str = (String) entry.getKey();
        this.mTriggers.put((String) entry.getValue(), (IRule) map.get(str));
    }

    public Set<String> getAllCollectorNames() {
        return (Set) this.mRules.values().stream().flatMap(new c()).collect(Collectors.toSet());
    }

    public String getName() {
        return this.mName;
    }

    public List<IRule> getRuleList() {
        return new ArrayList(this.mRules.keySet());
    }

    public Map<IRule, Set<String>> getRuleMap() {
        return this.mRules;
    }

    public Set<String> getTriggerNames() {
        return this.mTriggers.keySet();
    }

    public Map<String, IRule> getTriggers() {
        return this.mTriggers;
    }

    public EventInfoDTO(String str, Map<IRule, Set<String>> map, Map<String, IRule> map2) {
        this.mName = str;
        this.mRules = map;
        this.mTriggers = map2;
    }

    public EventInfoDTO(final EventCloudDTO.EventParameter eventParameter, final Map<String, IRule> map) {
        int size = eventParameter.getRule().size();
        this.mName = eventParameter.getEventName();
        this.mRules = new HashMap(size);
        this.mTriggers = new HashMap(size);
        map.entrySet().forEach(new Consumer() { // from class: g.a
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f1855a.lambda$new$0(eventParameter, (Map.Entry) obj);
            }
        });
        eventParameter.getTrigger().entrySet().forEach(new Consumer() { // from class: g.b
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f1857a.lambda$new$1(map, (Map.Entry) obj);
            }
        });
    }
}
