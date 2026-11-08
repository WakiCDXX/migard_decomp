package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes.dex */
public class TransmitPower extends SamplingCatchedLog {
    private final String[] HOOK_METHODS;
    private ConcurrentHashMap<String, Method> mMethodMap;
    private Object mQcHook;
    private boolean mStarted;

    class TxPowerData extends SamplingCatchedLog.Data {
        long[] txPowerSlot;

        TxPowerData() {
            super();
            this.txPowerSlot = new long[2];
        }
    }

    public TransmitPower(Context context) {
        super(context);
        this.mStarted = false;
        this.HOOK_METHODS = new String[]{"getLtePuschTx", "getNr5gPuschTx"};
        this.mMethodMap = new ConcurrentHashMap<>();
    }

    private void getTxPower() {
        TxPowerData txPowerData = new TxPowerData();
        try {
            txPowerData.time = System.currentTimeMillis();
            for (int i2 = 0; i2 <= 1; i2++) {
                if (is5G(i2)) {
                    Method method = this.mMethodMap.get("getNr5gPuschTx");
                    if (method != null) {
                        txPowerData.txPowerSlot[i2] = ((Integer) method.invoke(this.mQcHook, Integer.valueOf(i2))).intValue();
                    }
                } else {
                    Method method2 = this.mMethodMap.get("getLtePuschTx");
                    if (method2 != null) {
                        txPowerData.txPowerSlot[i2] = ((Integer) method2.invoke(this.mQcHook, Integer.valueOf(i2))).intValue();
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e2) {
            e2.printStackTrace();
        }
        this.mList.add(txPowerData);
    }

    private boolean is5G(int i2) {
        try {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            StringBuilder sb = new StringBuilder();
            sb.append(CommonStaticLog.FIVEG_ICON_GROUP_SETTING_PREFIX);
            sb.append(i2);
            return Settings.Global.getInt(contentResolver, sb.toString()) != 0;
        } catch (Settings.SettingNotFoundException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    private void qcomHook() {
        try {
            Class<?> cls = Class.forName("com.qualcomm.qcrilhook.QcRilHook");
            Method[] methods = cls.getMethods();
            for (String str : this.HOOK_METHODS) {
                for (Method method : methods) {
                    if (str.equals(method.getName())) {
                        this.mMethodMap.put(str, method);
                        break;
                    }
                }
            }
            Constructor<?> constructor = cls.getConstructor(Context.class, null, String.class);
            Context context = this.mContext;
            this.mQcHook = constructor.newInstance(context, null, context.getPackageName());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override // com.miui.powerkeeper.logsystem.log.sampling.SamplingCatchedLog
    public void catchLog() {
        addContent("Modem Tx Power (only QCom support):");
        for (SamplingCatchedLog.Data data : this.mList) {
            addContent("  Time: " + LSUtils.getStringDate(data.time));
            StringBuilder sb = new StringBuilder();
            sb.append("  Slot0: ");
            TxPowerData txPowerData = (TxPowerData) data;
            sb.append(txPowerData.txPowerSlot[0]);
            addContent(sb.toString());
            addContent("  Slot1: " + txPowerData.txPowerSlot[0]);
        }
        this.mStarted = false;
        super.catchLog();
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return LogType.LOG_TYPE_SAMPLING_TRANSMIT_POWER;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SAMPLING_TRANSMIT_POWER_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        if (!this.mStarted) {
            this.mStarted = true;
            qcomHook();
        }
        getTxPower();
        return true;
    }
}
