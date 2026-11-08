package com.miui.powerkeeper.thermalcollector;

import android.annotation.NonNull;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.LocalLog;
import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.resource.Couple;
import com.miui.powerkeeper.thermalcollector.DTO.EventCloudDTO;
import com.miui.powerkeeper.thermalcollector.DTO.EventInfoDTO;
import com.miui.powerkeeper.thermalcollector.collector.ICollector;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils;
import com.miui.powerkeeper.thermalcollector.event.IEvent;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import com.miui.powerkeeper.thermalcollector.source.CollectorFactory;
import com.miui.powerkeeper.thermalcollector.trigger.ITrigger;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import com.miui.powerkeeper.utils.Utils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import miui.yellowpage.YellowPageContract;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CollectorManager extends AutoRegister {
    private static final String COLLECT_DATA_CONFIG_PATH = "thermal_collector_config/%s/thermal_collector.json";
    private static final boolean DEBUG = ThermalInfoUtils.isTestMode();
    public static final String TAG = "collectorManager";
    private static final LocalLog sHistory;
    private static CollectorManager sInstance;
    private static final HandlerThread sThread;
    private String mBoardProcessTriggerId;
    private final TriggerCallback mCallback;
    private final Map<String, ICollector> mCollectors;
    private final Map<String, IEvent> mEvents;
    Gson mGson;
    private final Handler mHandler;
    private final SharedPrefUtil mSp;
    private final Map<String, ITrigger> mTriggers;

    private class ManagerHandler extends Handler {
        private static final int MESSAGE_INIT = 0;
        private static final int MESSAGE_RESTART = 1;

        public ManagerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            int i2 = message.what;
            if (i2 != 0) {
                if (i2 != 1) {
                    return;
                } else {
                    CollectorManager.this.stopWork();
                }
            }
            CollectorManager.this.init();
        }
    }

    public class TriggerCallback {
        public TriggerCallback() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$collect$0(Couple couple) {
            Log.d(CollectorManager.TAG, ((String) couple.getK()) + " " + couple.getV());
        }

        public void collect(final Set<String> set, Set<String> set2) {
            Stream<String> stream = set2.stream();
            final Map map = CollectorManager.this.mCollectors;
            Objects.requireNonNull(map);
            final List list = (List) stream.map(new Function() { // from class: com.miui.powerkeeper.thermalcollector.o
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return (ICollector) map.get((String) obj);
                }
            }).filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.p
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Objects.nonNull((ICollector) obj);
                }
            }).map(new Function() { // from class: com.miui.powerkeeper.thermalcollector.q
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((ICollector) obj).collectData();
                }
            }).collect(Collectors.toList());
            if (CollectorManager.DEBUG) {
                list.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.r
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        CollectorManager.TriggerCallback.lambda$collect$0((Couple) obj);
                    }
                });
            }
            CollectorManager.this.mEvents.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.s
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((IEvent) obj).updateData(set, list);
                }
            });
        }
    }

    static {
        HandlerThread handlerThread = new HandlerThread("ThermalCollectorManager");
        sThread = handlerThread;
        sHistory = new LocalLog(16);
        handlerThread.start();
    }

    private CollectorManager() {
        ManagerHandler managerHandler = new ManagerHandler(sThread.getLooper());
        this.mHandler = managerHandler;
        this.mEvents = new ArrayMap();
        this.mTriggers = new ArrayMap();
        this.mCollectors = new ArrayMap();
        this.mCallback = new TriggerCallback();
        this.mSp = SharedPrefUtil.getInstance(AutoRegister.getContext(), "Thermal");
        this.mGson = CollectorConfig.sGson;
        this.mBoardProcessTriggerId = "0";
        managerHandler.sendEmptyMessage(0);
    }

    private void dumpHistory(PrintWriter printWriter) {
        printWriter.println("<<< CollectorManager History >>>");
        printWriter.println();
        sHistory.dump(printWriter);
        printWriter.println();
    }

    private void getBothEvents(final Set<String> set, final Set<String> set2, EventCloudDTO eventCloudDTO) {
        final ArrayList arrayList = new ArrayList(this.mEvents.keySet());
        final List<String> targetEvents = getTargetEvents(eventCloudDTO);
        List<String> parametersChanged = getParametersChanged(eventCloudDTO);
        Stream streamFilter = arrayList.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.f
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CollectorManager.lambda$getBothEvents$1(targetEvents, (String) obj);
            }
        });
        Objects.requireNonNull(set);
        streamFilter.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.g
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                set.add((String) obj);
            }
        });
        Stream<String> streamFilter2 = targetEvents.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermalcollector.h
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CollectorManager.lambda$getBothEvents$2(arrayList, (String) obj);
            }
        });
        Objects.requireNonNull(set2);
        streamFilter2.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.g
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                set2.add((String) obj);
            }
        });
        set.addAll(parametersChanged);
        set2.addAll(parametersChanged);
    }

    public static String getCollectedCompoundScenarios() {
        return SimpleSettings.Misc.getString(AutoRegister.getContext(), "thermal_collector_compound_scenarios", "");
    }

    public static String getDataCollectorVersion() {
        return SimpleSettings.Misc.getString(AutoRegister.getContext(), "thermal_collector_version", "");
    }

    public static LocalLog getHistory() {
        return sHistory;
    }

    public static synchronized CollectorManager getInstance() {
        try {
            if (sInstance == null) {
                sInstance = new CollectorManager();
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private List<String> getParametersChanged(EventCloudDTO eventCloudDTO) {
        ArrayList arrayList = new ArrayList();
        for (EventCloudDTO.EventParameter eventParameter : eventCloudDTO.getParameters()) {
            String strMD5 = Utils.MD5(eventParameter.toString());
            String eventName = eventParameter.getEventName();
            if (!strMD5.equals(this.mSp.getStringValue(eventName, ""))) {
                arrayList.add(eventName);
                this.mSp.save(eventName, strMD5);
            }
        }
        return arrayList;
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0028  */
    private List<String> getTargetEvents(EventCloudDTO eventCloudDTO) {
        byte b2;
        String status = eventCloudDTO.getStatus();
        int iHashCode = status.hashCode();
        if (iHashCode != 94756405) {
            if (iHashCode == 103145323 && status.equals(YellowPageContract.Search.LOCAL_SEARCH)) {
                b2 = 1;
            } else {
                b2 = -1;
            }
        } else if (status.equals("cloud")) {
            b2 = 0;
        } else {
            b2 = -1;
        }
        return (b2 == 0 || b2 == 1) ? new ArrayList(eventCloudDTO.getEventNames()) : Collections.emptyList();
    }

    public static HandlerThread getThread() {
        return sThread;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void init() {
        initEvents(new CollectorFactory(), prepareData());
        startWork();
    }

    private void initEvents(final CollectorFactory collectorFactory, final EventCloudDTO eventCloudDTO) {
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        getBothEvents(hashSet, hashSet2, eventCloudDTO);
        hashSet.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.d
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f594a.uninstallEvent((String) obj);
            }
        });
        hashSet2.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.e
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f595a.lambda$initEvents$0(collectorFactory, eventCloudDTO, (String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: installCollector, reason: merged with bridge method [inline-methods] */
    public void lambda$installEvent$4(String str, CollectorFactory collectorFactory, EventInfoDTO eventInfoDTO) {
        ICollector iCollectorCreateCollector = this.mCollectors.containsKey(str) ? this.mCollectors.get(str) : collectorFactory.createCollector(str);
        if (iCollectorCreateCollector != ICollector.sNullCollector) {
            this.mCollectors.put(str, iCollectorCreateCollector);
        }
        Objects.requireNonNull(iCollectorCreateCollector);
        iCollectorCreateCollector.setEvent(eventInfoDTO.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: installEvent, reason: merged with bridge method [inline-methods] */
    public void lambda$initEvents$0(String str, final CollectorFactory collectorFactory, EventCloudDTO eventCloudDTO) {
        IEvent iEventCreateEvent = collectorFactory.createEvent(str);
        if (iEventCreateEvent == IEvent.sNullEvent) {
            return;
        }
        this.mEvents.put(str, iEventCreateEvent);
        final EventInfoDTO parameters = iEventCreateEvent.parseParameters(eventCloudDTO.getParameterByEvent(str));
        parameters.getTriggerNames().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.n
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f619a.lambda$installEvent$3(collectorFactory, parameters, (String) obj);
            }
        });
        parameters.getAllCollectorNames().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.b
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f588a.lambda$installEvent$4(collectorFactory, parameters, (String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: installTrigger, reason: merged with bridge method [inline-methods] */
    public void lambda$installEvent$3(String str, CollectorFactory collectorFactory, EventInfoDTO eventInfoDTO) {
        ITrigger iTrigger;
        if (this.mTriggers.containsKey(str)) {
            iTrigger = this.mTriggers.get(str);
        } else {
            ITrigger iTriggerCreateTrigger = collectorFactory.createTrigger(str);
            iTriggerCreateTrigger.setCallback(this.mCallback);
            this.mTriggers.put(str, iTriggerCreateTrigger);
            iTrigger = iTriggerCreateTrigger;
        }
        Objects.requireNonNull(iTrigger);
        iTrigger.setRule(str, eventInfoDTO);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getBothEvents$1(List list, String str) {
        return !list.contains(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getBothEvents$2(List list, String str) {
        return !list.contains(str);
    }

    private void prepareCompoundScenarioData() {
        String str;
        String collectedCompoundScenarios = getCollectedCompoundScenarios();
        if (collectedCompoundScenarios.isEmpty()) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(collectedCompoundScenarios);
            String strOptString = jSONObject.optString(CollectorConfig.CLOUD_COMPOUND_SCENARIOS_UPLOAD_ID, "");
            String strOptString2 = jSONObject.optString(CollectorConfig.CLOUD_COMPOUND_SCENARIOS_UPLOAD_SCENARIOS, "");
            String strOptString3 = jSONObject.optString(CollectorConfig.CLOUD_COMPOUND_SCENARIOS_FOREGROUND_INFO, "");
            String strOptString4 = jSONObject.optString(CollectorConfig.CLOUD_COMPOUND_SCENARIOS_CALL_INFO, "");
            String strOptString5 = jSONObject.optString(CollectorConfig.CLOUD_COMPOUND_SCENARIOS_FLOATWINDOW_INFO, "");
            String strOptString6 = jSONObject.optString(CollectorConfig.CLOUD_COMPOUND_SCENARIOS_BACKGROUND_INFO, "");
            HashMap map = new HashMap();
            HashMap map2 = new HashMap();
            HashMap map3 = new HashMap();
            HashMap map4 = new HashMap();
            ArrayList arrayList = new ArrayList();
            String[] strArrSplit = strOptString3.split(";");
            int length = strArrSplit.length;
            String str2 = strOptString;
            int i2 = 0;
            while (i2 < length) {
                int i3 = length;
                String[] strArrSplit2 = strArrSplit[i2].split(":");
                String[] strArr = strArrSplit;
                if (strArrSplit2.length == 2) {
                    map.put(strArrSplit2[0], strArrSplit2[1]);
                }
                i2++;
                length = i3;
                strArrSplit = strArr;
            }
            String[] strArrSplit3 = strOptString4.split(";");
            int length2 = strArrSplit3.length;
            int i4 = 0;
            while (i4 < length2) {
                String[] strArrSplit4 = strArrSplit3[i4].split(":");
                String[] strArr2 = strArrSplit3;
                int i5 = length2;
                if (strArrSplit4.length == 2) {
                    map2.put(strArrSplit4[0], strArrSplit4[1]);
                }
                i4++;
                strArrSplit3 = strArr2;
                length2 = i5;
            }
            String[] strArrSplit5 = strOptString5.split(";");
            int length3 = strArrSplit5.length;
            int i6 = 0;
            while (i6 < length3) {
                String[] strArrSplit6 = strArrSplit5[i6].split(":");
                String[] strArr3 = strArrSplit5;
                if (strArrSplit6.length == 2) {
                    map3.put(strArrSplit6[0], strArrSplit6[1]);
                }
                i6++;
                strArrSplit5 = strArr3;
            }
            for (String str3 : strOptString6.split(";")) {
                String[] strArrSplit7 = str3.split(":");
                if (strArrSplit7.length == 2) {
                    map4.put(strArrSplit7[0], strArrSplit7[1]);
                }
            }
            if (map.size() > 0) {
                arrayList.add(new Pair(CollectorConfig.CLOUD_COMPOUND_SCENARIOS_FOREGROUND_INFO, map));
                ScenarioCollectorUtils.getInstance().setUndefListenForeGroundPkgs(String.join(":", map.values()));
            }
            if (map2.size() > 0) {
                arrayList.add(new Pair(CollectorConfig.CLOUD_COMPOUND_SCENARIOS_CALL_INFO, map2));
                ScenarioCollectorUtils.getInstance().setUndefListenCallStates(String.join(":", map2.values()));
            }
            if (map3.size() > 0) {
                arrayList.add(new Pair(CollectorConfig.CLOUD_COMPOUND_SCENARIOS_FLOATWINDOW_INFO, map3));
                ScenarioCollectorUtils.getInstance().setUndefListenFloatWindows(String.join(":", map3.values()));
            }
            if (map4.size() > 0) {
                arrayList.add(new Pair(CollectorConfig.CLOUD_COMPOUND_SCENARIOS_BACKGROUND_INFO, map4));
                ScenarioCollectorUtils.getInstance().setUndefListenBackGroundPkgs(String.join(":", map4.values()));
            }
            if (str2.isEmpty() || strOptString2.isEmpty()) {
                return;
            }
            String[] strArrSplit8 = strOptString2.split(";");
            int length4 = strArrSplit8.length;
            int i7 = 0;
            while (i7 < length4) {
                String[] strArrSplit9 = strArrSplit8[i7].split(":");
                if (strArrSplit9.length == 2) {
                    StringBuilder sb = new StringBuilder();
                    str = str2;
                    sb.append(str);
                    sb.append(strArrSplit9[0]);
                    String string = sb.toString();
                    String str4 = strArrSplit9[1];
                    int length5 = str4.toCharArray().length / 4;
                    HashMap map5 = new HashMap();
                    for (int i8 = 0; i8 < length5; i8++) {
                        if (i8 < arrayList.size()) {
                            Pair pair = (Pair) arrayList.get(i8);
                            String strSubstring = str4.substring(i8 * 4, (i8 + 1) * 4);
                            if (!"0000".equals(strSubstring) && ((Map) pair.second).containsKey(strSubstring)) {
                                map5.put((String) pair.first, (String) ((Map) pair.second).get(strSubstring));
                            }
                        }
                    }
                    ScenarioCollectorUtils.getInstance().setUndefCompoundScenarios(string, map5);
                } else {
                    str = str2;
                }
                i7++;
                str2 = str;
            }
        } catch (Exception unused) {
            Log.d(TAG, "parse compoundScenarios error !");
        }
    }

    private EventCloudDTO prepareData() {
        String string = SimpleSettings.Misc.getString(AutoRegister.getContext(), CollectorConfig.CLOUD_THERMAL_COLLECTOR, "");
        if (string.isEmpty()) {
            String dataCollectorVersion = getDataCollectorVersion();
            string = dataCollectorVersion.isEmpty() ? Utils.getJsonFromAssert(PowerKeeperApplication.b(), "thermal_collector.json") : Utils.getJsonFromAssert(PowerKeeperApplication.b(), String.format(Locale.CHINA, COLLECT_DATA_CONFIG_PATH, dataCollectorVersion));
        }
        prepareCompoundScenarioData();
        Gson gson = this.mGson;
        EventCloudDTO eventCloudDTO = gson != null ? (EventCloudDTO) gson.fromJson(string, EventCloudDTO.class) : null;
        return eventCloudDTO == null ? new EventCloudDTO() : eventCloudDTO;
    }

    private void startWork() {
        this.mTriggers.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.j
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ITrigger) obj).startWork();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopWork() {
        this.mTriggers.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.i
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ITrigger) obj).stopWork();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void uninstallEvent(String str) {
        IEvent iEvent = this.mEvents.get(str);
        this.mEvents.remove(str);
        if (iEvent == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, ITrigger> entry : this.mTriggers.entrySet()) {
            String key = entry.getKey();
            ITrigger value = entry.getValue();
            value.removeRule(str);
            if (value.readyBeDestroy()) {
                arrayList.add(key);
            }
        }
        final Map<String, ITrigger> map = this.mTriggers;
        Objects.requireNonNull(map);
        arrayList.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.c
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                map.remove((String) obj);
            }
        });
        ArrayList arrayList2 = new ArrayList();
        for (Map.Entry<String, ICollector> entry2 : this.mCollectors.entrySet()) {
            String key2 = entry2.getKey();
            ICollector value2 = entry2.getValue();
            value2.removeEvent(str);
            if (value2.readyBeDestroy()) {
                arrayList2.add(key2);
            }
        }
        final Map<String, ICollector> map2 = this.mCollectors;
        Objects.requireNonNull(map2);
        arrayList2.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.c
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                map2.remove((String) obj);
            }
        });
    }

    /* JADX WARN: Code duplicated, block: B:22:0x003c  */
    public void dump(final PrintWriter printWriter, String[] strArr, int i2) {
        byte b2;
        String str = i2 < strArr.length ? strArr[i2] : "";
        int iHashCode = str.hashCode();
        if (iHashCode != -1059891784) {
            if (iHashCode != 96891546) {
                if (iHashCode == 949444906 && str.equals("collect")) {
                    b2 = 2;
                } else {
                    b2 = -1;
                }
            } else if (str.equals("event")) {
                b2 = 0;
            } else {
                b2 = -1;
            }
        } else if (str.equals("trigger")) {
            b2 = 1;
        } else {
            b2 = -1;
        }
        if (b2 == 0) {
            this.mEvents.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.k
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((IEvent) obj).dump(printWriter);
                }
            });
            return;
        }
        if (b2 == 1) {
            this.mTriggers.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.l
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ITrigger) obj).dump(printWriter);
                }
            });
        } else if (b2 != 2) {
            dumpHistory(printWriter);
        } else {
            this.mCollectors.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.m
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ICollector) obj).dump(printWriter);
                }
            });
        }
    }

    public String getBoardProcessTriggerId() {
        return this.mBoardProcessTriggerId;
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    @Override // com.miui.powerkeeper.cloudcontrol.AutoRegister
    protected int getModel() {
        return b.b.f44z;
    }

    public boolean isAmbientTempTrigger() {
        return this.mTriggers.containsKey(CollectorConfig.TRIGGER_AMBIENT_TEMP);
    }

    public boolean isCameraStateCollector() {
        return this.mCollectors.containsKey(CollectorConfig.COLLECTOR_CAMERA_STATE);
    }

    public boolean isCompoundScenarioCollector() {
        return this.mCollectors.containsKey(CollectorConfig.COLLECTOR_COMPOUND_SCENARIO);
    }

    public boolean isFlashTempTrigger() {
        return this.mTriggers.containsKey(CollectorConfig.TRIGGER_FLASH_TEMP);
    }

    public boolean isMicroPhoneStateCollector() {
        return this.mCollectors.containsKey(CollectorConfig.COLLECTOR_MICRO_PHONE_STATE);
    }

    public boolean isPopUpWindowCollector() {
        return this.mCollectors.containsKey(CollectorConfig.COLLECTOR_POPUP_WINDOW);
    }

    public void setBoardProcessTriggerId(String str) {
        this.mBoardProcessTriggerId = str;
    }

    public void settlement() {
        this.mEvents.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.a
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((IEvent) obj).settlement();
            }
        });
    }

    @Override // com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        this.mHandler.sendEmptyMessage(1);
    }
}
