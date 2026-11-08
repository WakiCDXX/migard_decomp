package com.miui.powerkeeper.utils;

import android.os.Handler;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils;

/* JADX INFO: loaded from: classes.dex */
public class TemperatureObserver {
    private static final String BOARD_FILE = "/sys/class/thermal/thermal_message/board_sensor_temp";
    private static final long CYCLE = 30000;
    private final EventsAggregator mAggregator;
    private final Handler mHandler;
    private final Runnable mReadFile;

    public TemperatureObserver(Handler handler) {
        Runnable runnable = new Runnable() { // from class: com.miui.powerkeeper.utils.TemperatureObserver.1
            @Override // java.lang.Runnable
            public void run() {
                int ambientTemp;
                TemperatureObserver.this.mHandler.postDelayed(this, 30000L);
                String fileString = FileUtil.getFileString("/sys/class/thermal/thermal_message/board_sensor_temp");
                if (Utils.isNumber(fileString)) {
                    TemperatureObserver.this.mAggregator.notifyForBoardTemperature(Integer.parseInt(fileString));
                }
                if (CollectorManager.getInstance().isFlashTempTrigger()) {
                    String flashTemp = ThermalManager.ThermalTempInfoHelper.getFlashTemp();
                    if (Utils.isNumber(flashTemp)) {
                        int i2 = Integer.parseInt(flashTemp);
                        TemperatureObserver.this.mAggregator.notifyForFlashTemperature(i2);
                        ScenarioCollectorUtils.getInstance().setFlashTemp(i2);
                    }
                }
                if (!CollectorManager.getInstance().isAmbientTempTrigger() || (ambientTemp = ThermalManager.ThermalTempInfoHelper.getAmbientTemp()) == -1) {
                    return;
                }
                TemperatureObserver.this.mAggregator.notifyForAmbientTemperature(ambientTemp);
                ScenarioCollectorUtils.getInstance().setAmbientTemp(ambientTemp);
            }
        };
        this.mReadFile = runnable;
        this.mAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        this.mHandler = handler;
        handler.postDelayed(runnable, 30000L);
    }
}
