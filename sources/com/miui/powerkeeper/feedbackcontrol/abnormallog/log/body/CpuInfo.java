package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes.dex */
public class CpuInfo extends LogCollector {
    private final int CPU_COUNT;
    private final String TABLE;
    private final String TAG;

    public CpuInfo(Context context) {
        super(context);
        this.TAG = "CpuInfoCollector";
        this.TABLE = "Cpu Freq:";
        this.CPU_COUNT = 5;
        this.mLog.d("CpuInfoCollector", "create");
    }

    private boolean isAbNormal(List<ThermalManager.ProcessInfo> list) {
        return !((List) list.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.d
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CpuInfo.lambda$isAbNormal$0((ThermalManager.ProcessInfo) obj);
            }
        }).collect(Collectors.toList())).isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isAbNormal$0(ThermalManager.ProcessInfo processInfo) {
        return processInfo.mPercent > 850;
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() throws Throwable {
        this.mLog.d("CpuInfoCollector", "collect");
        List<ThermalManager.ProcessInfo> topProcessInfoByCpuRate = ThermalInfoUtils.getTopProcessInfoByCpuRate(5);
        boolean zIsAbNormal = isAbNormal(topProcessInfoByCpuRate);
        this.mPublicSpace.setContinueCollect(zIsAbNormal);
        this.mCache.append("Cpu Freq:[" + topProcessInfoByCpuRate.toString() + "Is Abnormal:" + zIsAbNormal + "] ");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("CpuInfoCollector", "logout");
    }
}
