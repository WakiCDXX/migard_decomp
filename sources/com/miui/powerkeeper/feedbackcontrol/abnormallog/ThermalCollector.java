package com.miui.powerkeeper.feedbackcontrol.abnormallog;

import android.annotation.NonNull;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.internal.os.BackgroundThread;
import com.google.gson.Gson;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.control.MessageType;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.LogPrinter;
import com.miui.powerkeeper.provider.ThermalDB.ThermalDurationConfigure;
import com.miui.powerkeeper.provider.ThermalDB.ThermalDurationDTO;
import com.miui.powerkeeper.thermal.ThermalService;
import com.miui.powerkeeper.tracker.TrackerManager;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.Utils;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/* JADX INFO: loaded from: classes.dex */
public class ThermalCollector {
    public static final String TAG = "ThermalCollector";
    public static final float VERSION = 1.0f;
    private static volatile ThermalCollector sInstance;
    private final Context mContext;
    private int mCurrentRange;
    private final MyHandler mHandler;
    private long mLastChangeTime;
    private PendingIntent mPendingIntent;
    private final Map<String, Map<Integer, Long>> mRecord;
    private final LogPrinter mLog = LogPrinter.getLogPrinter();
    private final ThermalState mState = new ThermalState();

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            int i2 = message.what;
            if (i2 == -32) {
                ThermalCollector.this.mState.setCompoundState(((Integer) message.obj).intValue());
                return;
            }
            if (i2 == -1) {
                ThermalCollector.this.init();
                return;
            }
            if (i2 == -5) {
                ThermalCollector.this.mState.setScreenState(99);
                return;
            }
            if (i2 == -4) {
                ThermalCollector.this.mState.setScreenState(98);
            } else if (i2 == 3) {
                ThermalCollector.this.settlement(false);
            } else {
                if (i2 != 4) {
                    return;
                }
                ThermalCollector.this.onTimingTrigger();
            }
        }
    }

    private class ThermalState {
        private PowerKeeperInterface.d mBatteryChangedCallback;
        private final Map<Integer, String> mScenarioNameMap;
        private String mCurrentScenario = "other";
        private int mChargingState = 0;
        private int mScreenState = 0;
        private int mCompoundState = 0;
        private PowerKeeperInterface.m mBatteryChangedListener = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.ThermalCollector.ThermalState.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.m
            public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
                if (i6 != ThermalState.this.mChargingState) {
                    ThermalState.this.mChargingState = i6;
                    ThermalState.this.onChanged();
                }
            }
        };

        ThermalState() {
            HashMap map = new HashMap();
            this.mScenarioNameMap = map;
            map.put(2000, MessageType.SCENARIO_SGAME_MM);
            map.put(2001, MessageType.SCENARIO_SGAME_MM);
            map.put(2002, MessageType.SCENARIO_SGAME_MM);
            map.put(2003, MessageType.SCENARIO_SGAME_MM);
            map.put(2004, MessageType.SCENARIO_APP_RIMET);
            map.put(Integer.valueOf(MessageType.COMPOUND_ID_APP_5), MessageType.SCENARIO_APP_RIMET);
            map.put(Integer.valueOf(MessageType.COMPOUND_ID_APP_6), MessageType.SCENARIO_APP_RIMET);
            map.put(Integer.valueOf(MessageType.COMPOUND_ID_APP_7), MessageType.SCENARIO_APP_RIMET);
            this.mBatteryChangedCallback = new PowerKeeperInterface.d(ThermalCollector.this.mHandler, this.mBatteryChangedListener);
            PowerKeeperManager powerKeeperManager = PowerKeeperManager.getInstance();
            powerKeeperManager.registerBatteryChangedListener(this.mBatteryChangedCallback);
            powerKeeperManager.getEventsAggregator().registerScenarioListener(ThermalCollector.this.mHandler);
            powerKeeperManager.getEventsAggregator().registerForScreenOn(ThermalCollector.this.mHandler, null);
            powerKeeperManager.getEventsAggregator().registerForScreenOff(ThermalCollector.this.mHandler, null);
        }

        void onChanged() {
            String str;
            int i2 = this.mScreenState;
            if (i2 == 99 && this.mChargingState == 0) {
                str = MessageType.SCREEN_OFF;
            } else {
                str = (i2 == 98 && this.mScenarioNameMap.containsKey(Integer.valueOf(this.mCompoundState))) ? this.mScenarioNameMap.get(Integer.valueOf(this.mCompoundState)) : "other";
            }
            if (str.equals(this.mCurrentScenario)) {
                return;
            }
            ThermalCollector.this.onScenarioChanged();
            this.mCurrentScenario = str;
        }

        void setCompoundState(int i2) {
            this.mCompoundState = i2;
            onChanged();
        }

        void setScreenState(int i2) {
            this.mScreenState = i2;
            onChanged();
        }
    }

    private ThermalCollector(Context context) {
        HashMap map = new HashMap();
        this.mRecord = map;
        this.mCurrentRange = 1;
        map.put(MessageType.ENTIRETY, new HashMap());
        map.put("other", new HashMap());
        map.put(MessageType.SCREEN_OFF, new HashMap());
        map.put(MessageType.SCENARIO_SGAME_MM, new HashMap());
        map.put(MessageType.SCENARIO_APP_RIMET, new HashMap());
        this.mContext = context;
        MyHandler myHandler = new MyHandler(BackgroundThread.getHandler().getLooper());
        this.mHandler = myHandler;
        myHandler.sendEmptyMessage(-1);
    }

    private void cleanStaleData() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -7);
        ThermalDurationConfigure.cleanStaleData(this.mContext, calendar);
    }

    public static ThermalCollector getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ThermalCollector.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new ThermalCollector(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void init() {
        resetData();
        restoreInterrupt();
        startFileObserver();
    }

    private boolean isToday(long j2) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(j2);
        return calendar.get(1) == calendar2.get(1) && calendar.get(6) == calendar2.get(6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$uploadNewData$0(long j2, boolean z2, Map.Entry entry) {
        new ThermalDurationConfigure(j2, ((Integer) entry.getKey()).intValue(), ((Long) entry.getValue()).longValue(), z2, 1.0f, this.mState.mCurrentScenario).insertToDB(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onScenarioChanged() {
        sumUpDuration();
    }

    private void onTempChanged(int i2) {
        int i3;
        int i4 = i2 / 1000;
        if (i4 < 26) {
            i3 = -1;
        } else {
            i3 = i4 >= 55 ? 15 : (i4 - 26) / 2;
        }
        if (i3 == this.mCurrentRange) {
            return;
        }
        sumUpDuration();
        this.mCurrentRange = i3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimingTrigger() {
        String fileString = FileUtil.getFileString("/sys/class/thermal/thermal_message/board_sensor_temp");
        if (Utils.isNumber(fileString)) {
            onTempChanged(Integer.parseInt(fileString));
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(4), 10000L);
        }
    }

    private void registerAlarmManager() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(11) >= 23) {
            calendar.add(5, 1);
        }
        calendar.set(11, 23);
        calendar.set(12, 0);
        Intent intent = new Intent(this.mContext, (Class<?>) ThermalService.class);
        intent.setAction(MessageType.ACTION_SETTLEMENT);
        this.mPendingIntent = PendingIntent.getService(this.mContext, 1, intent, 67108864);
        ((AlarmManager) this.mContext.getSystemService("alarm")).setRepeating(0, calendar.getTimeInMillis(), 86400000L, this.mPendingIntent);
    }

    private void resetData() {
        this.mRecord.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.a
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Map) obj).clear();
            }
        });
        this.mLastChangeTime = System.currentTimeMillis();
    }

    private void restoreInterrupt() {
        Map<Integer, Long> map;
        for (ThermalDurationConfigure thermalDurationConfigure : ThermalDurationConfigure.getList(this.mContext)) {
            if (thermalDurationConfigure.getInterrupt() && thermalDurationConfigure.getVersion().equals(String.valueOf(1.0f)) && isToday(thermalDurationConfigure.getDate()) && (map = this.mRecord.get(thermalDurationConfigure.getScenario())) != null) {
                map.put(Integer.valueOf(thermalDurationConfigure.getRange()), Long.valueOf(thermalDurationConfigure.getDuration()));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void settlement(boolean z2) {
        sumUpDuration();
        cleanStaleData();
        uploadNewData(z2);
        resetData();
    }

    private void startFileObserver() {
        this.mHandler.obtainMessage(4).sendToTarget();
    }

    private synchronized void sumUpDuration() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        Map<Integer, Long> orDefault = this.mRecord.getOrDefault(this.mState.mCurrentScenario, new HashMap());
        Map<Integer, Long> orDefault2 = this.mRecord.getOrDefault(MessageType.ENTIRETY, new HashMap());
        long j2 = jCurrentTimeMillis - this.mLastChangeTime;
        orDefault.put(Integer.valueOf(this.mCurrentRange), Long.valueOf(orDefault.getOrDefault(Integer.valueOf(this.mCurrentRange), 0L).longValue() + j2));
        orDefault2.put(Integer.valueOf(this.mCurrentRange), Long.valueOf(orDefault2.getOrDefault(Integer.valueOf(this.mCurrentRange), 0L).longValue() + j2));
        this.mRecord.put(this.mState.mCurrentScenario, orDefault);
        this.mRecord.put(MessageType.ENTIRETY, orDefault2);
        this.mLastChangeTime = jCurrentTimeMillis;
    }

    private void uploadNewData(final boolean z2) {
        Gson gson = new Gson();
        final long jCurrentTimeMillis = System.currentTimeMillis();
        ThermalDurationDTO thermalDurationDTO = new ThermalDurationDTO(jCurrentTimeMillis, this.mRecord, 1.0f);
        if (!z2) {
            TrackerManager.getInstance(this.mContext).track(1, "thermal_duration", gson.toJson(thermalDurationDTO));
        }
        this.mRecord.values().stream().map(new Function() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.b
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Map) obj).entrySet();
            }
        }).flatMap(new c()).forEach(new Consumer() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.d
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f396a.lambda$uploadNewData$0(jCurrentTimeMillis, z2, (Map.Entry) obj);
            }
        });
    }

    public float getSystemVersion() {
        return 1.0f;
    }

    public void onDestroy() {
        ((AlarmManager) this.mContext.getSystemService("alarm")).cancel(this.mPendingIntent);
        settlement(true);
    }

    public void saveTempInfo() {
        this.mHandler.sendEmptyMessage(3);
    }
}
