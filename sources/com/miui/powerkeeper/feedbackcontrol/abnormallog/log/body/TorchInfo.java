package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;

/* JADX INFO: loaded from: classes.dex */
public class TorchInfo extends LogCollector {
    private final String TABLE;
    private final String TAG;

    public TorchInfo(Context context) {
        super(context);
        this.TAG = "TorchInfoCollector";
        this.TABLE = "Torch Info: ";
        this.mLog.d("TorchInfoCollector", "create");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() {
        if (this.mPublicSpace.isContinueCollect()) {
            this.mLog.d("TorchInfoCollector", "collect");
            String str = ThermalInfoUtils.isTorchState(this.mContext) ? SleepModeControllerNew.SLEEP_MODE_ON : "off";
            this.mCache.append("Torch Info: [" + str + "]");
        }
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("TorchInfoCollector", "logout");
    }
}
