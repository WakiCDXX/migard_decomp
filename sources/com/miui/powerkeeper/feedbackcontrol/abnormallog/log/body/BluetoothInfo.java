package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothInfo extends LogCollector {
    private final String TABLE;
    private final String TAG;

    public BluetoothInfo(Context context) {
        super(context);
        this.TAG = "BluetoothInfoCollector";
        this.TABLE = "Bluetooth Info:";
        this.mLog.d("BluetoothInfoCollector", "create");
    }

    private List<BluetoothDevice> getBluetoothDevices() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter.isEnabled()) {
            return defaultAdapter.getConnectionState() == 2 ? (List) defaultAdapter.getBondedDevices().stream().filter(new Predicate() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.a
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((BluetoothDevice) obj).isConnected();
                }
            }).collect(Collectors.toList()) : new ArrayList();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$collect$0(StringBuilder sb, String str) {
        sb.append(str);
        sb.append(" ");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() {
        if (this.mPublicSpace.isContinueCollect()) {
            this.mLog.d("BluetoothInfoCollector", "collect");
            List<BluetoothDevice> bluetoothDevices = getBluetoothDevices();
            final StringBuilder sb = new StringBuilder();
            sb.append("Bluetooth Info:[");
            if (bluetoothDevices == null) {
                sb.append("bluetooth is not enabled");
            } else if (bluetoothDevices.size() == 0) {
                sb.append("bluetooth not connected");
            } else {
                bluetoothDevices.stream().map(new Function() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.b
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return ((BluetoothDevice) obj).getName();
                    }
                }).forEach(new Consumer() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.c
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        BluetoothInfo.lambda$collect$0(sb, (String) obj);
                    }
                });
            }
            sb.append("] ");
            this.mCache.append(sb.toString());
        }
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("BluetoothInfoCollector", "logout");
    }
}
