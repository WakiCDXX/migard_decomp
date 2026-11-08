package com.miui.powerkeeper.thermalcollector.collector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import miui.yellowpage.Tag;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class MiscCollector extends BaseCollector {
    private static final String KEY_DEVICE_PROTOCOL = "device_protocol";
    private static final String KEY_USB_CONNECTED = "usb_connected";
    private static final String TAG = "CollectorMisc";
    private static final int USB_ACCESSORY = 2;
    private static final int USB_DEVICE = 1;
    private static final int USB_UNCONNECTED = 0;
    private int mDeviceProtocol;
    private final UsbReceiver mUsbReceiver;
    private int mUsbState;

    private class UsbReceiver extends BroadcastReceiver {
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code duplicated, block: B:20:0x003b  */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            byte b2;
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action.hashCode()) {
                case -2114103349:
                    if (!action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                        b2 = -1;
                    } else {
                        b2 = 1;
                    }
                    break;
                case -1608292967:
                    if (!action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                        b2 = -1;
                    } else {
                        b2 = 3;
                    }
                    break;
                case 1099555123:
                    if (!action.equals("android.hardware.usb.action.USB_ACCESSORY_ATTACHED")) {
                        b2 = -1;
                    } else {
                        b2 = 0;
                    }
                    break;
                case 1605365505:
                    if (!action.equals("android.hardware.usb.action.USB_ACCESSORY_DETACHED")) {
                        b2 = -1;
                    } else {
                        b2 = 2;
                    }
                    break;
                default:
                    b2 = -1;
                    break;
            }
            if (b2 == 0) {
                MiscCollector.this.mUsbState = 2;
                return;
            }
            if (b2 != 1) {
                if (b2 == 2 || b2 == 3) {
                    MiscCollector.this.mUsbState = 0;
                    return;
                }
                return;
            }
            MiscCollector.this.mUsbState = 1;
            MiscCollector.this.mDeviceProtocol = ((UsbDevice) intent.getParcelableExtra(Tag.TagPhone.IMEI)).getDeviceProtocol();
        }

        private UsbReceiver() {
        }
    }

    public MiscCollector() {
        super("CollectorMisc");
        this.mUsbReceiver = new UsbReceiver();
        this.mUsbState = 0;
        this.mDeviceProtocol = 0;
        registerBroadcast();
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        this.mContext.registerReceiver(this.mUsbReceiver, intentFilter);
    }

    private void unregisterBroadcast() {
        this.mContext.unregisterReceiver(this.mUsbReceiver);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    public void collect() {
        JSONObject json = getJson();
        try {
            json.put(KEY_USB_CONNECTED, this.mUsbState);
            json.put(KEY_DEVICE_PROTOCOL, this.mDeviceProtocol);
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
        unregisterBroadcast();
    }
}
