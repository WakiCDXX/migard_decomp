package com.miui.powerkeeper.thermal;

import android.annotation.NonNull;
import android.os.Handler;
import android.os.Message;
import android.util.LocalLog;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.thermal.processor.IProcessor;
import com.miui.powerkeeper.thermal.resource.ThermalFactory;
import com.miui.powerkeeper.thermal.setting.IProviderCallback;
import com.miui.powerkeeper.thermal.setting.SettingDTO;
import com.miui.powerkeeper.thermal.setting.SettingProvider;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class ThermalSwitchHandler extends Handler {
    public static final String TAG = "ThermalSwitchHandler";
    private static final LocalLog sHistory = new LocalLog(8);
    private static final ThermalSwitchHandler sInstance = new ThermalSwitchHandler();
    private final Map<String, IProcessor> mProcessors;

    public ThermalSwitchHandler() {
        super(ScenarioManager.getThread().getLooper());
        this.mProcessors = new HashMap();
        SettingProvider.getInstance().registerCallback(new IProviderCallback() { // from class: com.miui.powerkeeper.thermal.o
            @Override // com.miui.powerkeeper.thermal.setting.IProviderCallback
            public final void onChange(Object obj) {
                this.f575a.onNewData((SettingDTO) obj);
            }
        });
        PowerKeeperManager.getInstance().getEventsAggregator().registerScenarioListener(this);
    }

    public static LocalLog getHistory() {
        return sHistory;
    }

    public static ThermalSwitchHandler getInstance() {
        return sInstance;
    }

    private void initProcessors(List<SettingDTO.ProcessorDTO> list) {
        final ThermalFactory thermalFactory = new ThermalFactory();
        list.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermal.p
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f576a.lambda$initProcessors$1(thermalFactory, (SettingDTO.ProcessorDTO) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initProcessors$1(ThermalFactory thermalFactory, SettingDTO.ProcessorDTO processorDTO) {
        this.mProcessors.put(processorDTO.getName(), thermalFactory.createProcessor(processorDTO));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onNewData$0(SettingDTO settingDTO) {
        List<SettingDTO.ProcessorDTO> processors = settingDTO.getProcessors();
        if (processors.isEmpty()) {
            return;
        }
        uninstallAll();
        initProcessors(processors);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewData(final SettingDTO settingDTO) {
        post(new Runnable() { // from class: com.miui.powerkeeper.thermal.m
            @Override // java.lang.Runnable
            public final void run() {
                this.f573a.lambda$onNewData$0(settingDTO);
            }
        });
    }

    private void uninstallAll() {
        this.mProcessors.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermal.n
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((IProcessor) obj).uninstall();
            }
        });
        this.mProcessors.clear();
    }

    public void dump(final PrintWriter printWriter) {
        printWriter.println("<<< switch handler >>>");
        this.mProcessors.forEach(new BiConsumer() { // from class: com.miui.powerkeeper.thermal.k
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                printWriter.println((IProcessor) obj2);
            }
        });
        printWriter.println();
        sHistory.dump(printWriter);
    }

    @Override // android.os.Handler
    public void handleMessage(@NonNull Message message) {
        final int iIntValue = ((Integer) message.obj).intValue();
        this.mProcessors.forEach(new BiConsumer() { // from class: com.miui.powerkeeper.thermal.l
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((IProcessor) obj2).execute(iIntValue);
            }
        });
    }

    public boolean isCameraFloat() {
        return false;
    }

    public void onMultiScreenChanged(boolean z2) {
    }

    public void onPerformanceEnabled(int i2) {
    }

    public void onPowerSaveModeChanged(int i2) {
    }
}
