package com.miui.powerkeeper.thermal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import com.miui.powerkeeper.cloudcontrol.CloudControlManager;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.ThermalCollector;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.control.MessageType;
import com.miui.powerkeeper.provider.ThermalInfoHelper;
import com.miui.powerkeeper.statemachine.CastingHandler;
import com.miui.powerkeeper.thermal.callback.ScenarioCallbackManager;
import com.miui.powerkeeper.thermal.dfscheck.CollectionData;
import com.miui.powerkeeper.thermal.dfsreport.ThermalStateObserver;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.utils.TemperatureObserver;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class ThermalService extends Service {
    private static final String TAG = "ThermalLogService";
    private static final long UPLOAD_LOG_DELAY = 60000;
    private static final HandlerThread sHandlerThread;
    private MyBinderService mBinderService;
    private CloudControlManager mCloudControlManager;
    private CollectorManager mCollectorManager;
    private MyHandler mHandler;
    private ScenarioManager mScenarioManager;
    private TemperatureObserver mTemperatureObserver;
    private ThermalCollector mThermalCollector;
    private ThermalSwitchHandler mThermalSwitchHandler;
    private ThermalStateObserver thermalStateObserver;

    static class MyBinderService extends IThermalScenario.Stub {
        ScenarioCallbackManager mManager = ScenarioCallbackManager.getInstance();

        MyBinderService() {
        }

        @Override // com.miui.powerkeeper.thermal.IThermalScenario
        public int isInCastingWithCamera() {
            CastingHandler castingHandler = CastingHandler.getInstance();
            if (castingHandler == null) {
                return 0;
            }
            return castingHandler.getCurrentState();
        }

        @Override // com.miui.powerkeeper.thermal.IThermalScenario
        public void registerListener(int[] iArr, IThermalScenarioListener iThermalScenarioListener) {
            this.mManager.registerCallback(iArr, iThermalScenarioListener);
        }

        @Override // com.miui.powerkeeper.thermal.IThermalScenario
        public void unregisterListener(IThermalScenarioListener iThermalScenarioListener) {
            this.mManager.unregisterCallback(iThermalScenarioListener);
        }
    }

    private static class MyHandler extends Handler {
        SoftReference<ThermalService> mServiceReference;

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            ThermalService thermalService = this.mServiceReference.get();
            int i2 = message.what;
            if (i2 == -1) {
                thermalService.init();
                return;
            }
            if (i2 == 5) {
                thermalService.setAlarm();
                return;
            }
            if (i2 == 2) {
                thermalService.mThermalCollector.saveTempInfo();
                ThermalInfoHelper.uploadThermalInfo(thermalService);
            } else {
                if (i2 != 3) {
                    return;
                }
                thermalService.mCollectorManager.settlement();
                sendEmptyMessageDelayed(2, ThermalService.UPLOAD_LOG_DELAY);
            }
        }

        private MyHandler(Looper looper, ThermalService thermalService) {
            super(looper);
            this.mServiceReference = new SoftReference<>(thermalService);
        }
    }

    static {
        HandlerThread handlerThread = new HandlerThread(TAG);
        sHandlerThread = handlerThread;
        handlerThread.start();
    }

    public static HandlerThread getThread() {
        return sHandlerThread;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void init() {
        this.mTemperatureObserver = new TemperatureObserver(this.mHandler);
        this.mCloudControlManager = CloudControlManager.getInstance();
        this.mThermalCollector = ThermalCollector.getInstance(this);
        this.mScenarioManager = ScenarioManager.getInstance();
        this.mThermalSwitchHandler = ThermalSwitchHandler.getInstance();
        this.mCollectorManager = CollectorManager.getInstance();
        ThermalStateObserver thermalStateObserverInstance = ThermalStateObserver.getThermalStateObserverInstance();
        this.thermalStateObserver = thermalStateObserverInstance;
        thermalStateObserverInstance.checkAbnormal();
        this.mHandler.sendEmptyMessage(5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAlarm() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(11) >= 18) {
            calendar.add(5, 1);
        }
        calendar.set(11, 18);
        calendar.set(12, 0);
        Intent intent = new Intent(this, (Class<?>) ThermalService.class);
        intent.setAction(MessageType.ACTION_SETTLEMENT);
        ((AlarmManager) getSystemService("alarm")).setRepeating(0, calendar.getTimeInMillis(), 86400000L, PendingIntent.getService(this, 1, intent, 67108864));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:24:0x004c  */
    @Override // android.app.Service
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        byte b2;
        String str = strArr.length > 0 ? strArr[0] : "";
        printWriter.println("======== ThermalService dumping ========");
        switch (str.hashCode()) {
            case 46423:
                if (!str.equals("-cm")) {
                    b2 = -1;
                } else {
                    b2 = 3;
                }
                break;
            case 46914:
                if (!str.equals("-sh")) {
                    b2 = -1;
                } else {
                    b2 = 2;
                }
                break;
            case 46919:
                if (!str.equals("-sm")) {
                    b2 = -1;
                } else {
                    b2 = 1;
                }
                break;
            case 1439972:
                if (!str.equals("-dfs")) {
                    b2 = -1;
                } else {
                    b2 = 4;
                }
                break;
            case 3046207:
                if (!str.equals("cast")) {
                    b2 = -1;
                } else {
                    b2 = 0;
                }
                break;
            default:
                b2 = -1;
                break;
        }
        if (b2 == 0) {
            CastingHandler castingHandler = CastingHandler.getInstance();
            if (castingHandler == null) {
                printWriter.println(0);
            } else {
                printWriter.println(castingHandler.getCurrentState());
                castingHandler.dump(printWriter);
            }
        } else if (b2 == 1) {
            this.mScenarioManager.dump(printWriter, strArr, 1);
        } else if (b2 == 2) {
            this.mThermalSwitchHandler.dump(printWriter);
        } else if (b2 == 3) {
            this.mCollectorManager.dump(printWriter, strArr, 1);
        } else if (b2 != 4) {
            ScenarioManager scenarioManager = this.mScenarioManager;
            if (scenarioManager != null) {
                scenarioManager.dump(printWriter, strArr, 1);
            }
            CollectorManager collectorManager = this.mCollectorManager;
            if (collectorManager != null) {
                collectorManager.dump(printWriter, strArr, 1);
            }
        } else {
            CollectionData.getCollectionDataInstance().dump(printWriter);
        }
        printWriter.println("======== ThermalService end ========");
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (UserHandle.myUserId() != 0) {
            return null;
        }
        if (this.mBinderService == null) {
            this.mBinderService = new MyBinderService();
        }
        return this.mBinderService.asBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        if (UserHandle.myUserId() != 0) {
            stopSelf();
            return;
        }
        MyHandler myHandler = new MyHandler(sHandlerThread.getLooper(), this);
        this.mHandler = myHandler;
        myHandler.sendEmptyMessage(-1);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i2, int i3) {
        if (intent == null || intent.getAction() == null) {
            return 1;
        }
        String action = intent.getAction();
        if (this.mHandler == null) {
            return 1;
        }
        if (action.equals(MessageType.ACTION_SETTLEMENT)) {
            this.mHandler.sendEmptyMessage(3);
            return 1;
        }
        if (!action.equals("upload")) {
            return 1;
        }
        this.mHandler.sendEmptyMessage(2);
        return 1;
    }
}
