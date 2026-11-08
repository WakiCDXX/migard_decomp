package miui.bluetooth.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class MiBleDeviceManager {
    public static final String ACTION_BLE_EVENT = "miui.bluetooth.BLE_EVENT";
    public static final int BLE_EVENT_AWAKE = 5;
    public static final int BLE_EVENT_CLICK = 1;
    public static final int BLE_EVENT_CUSTOM = 256;
    public static final int BLE_EVENT_DOUBLE_CLICK = 2;
    public static final int BLE_EVENT_LINK_LOSS = 6;
    public static final int BLE_EVENT_LONG_CLICK = 3;
    public static final int BLE_EVENT_PHONE_HANGUP = 7;
    public static final int BLE_EVENT_PHONE_IGNORE = 8;
    public static final int BLE_EVENT_SLEEP = 4;
    public static final int BLE_EVENT_UNKNOWN = 0;
    public static final String BLE_IMMEDIATE_ALERT_PERMISSION = "miui.permission.BLE_IMMEDIATE_ALERT";
    private static final boolean DBG = true;
    public static final String EXTRA_DEVICE = "miui.bluetooth.extras.DEVICE";
    public static final String EXTRA_EVENT = "miui.bluetooth.extras.EVENT";
    public static final String EXTRA_EVENT_DATA = "miui.bluetooth.extras.EVENT_DATA";
    public static final String MI_WEAR_SETTING_ENCRYPT_ENABLED = "mi_wear_setting_encrypt_enabled";
    public static final int SERVICE_VERSION_UNKNOWN = -1;
    public static final String SETTING_BIND_DEVICE = "device_type";
    public static final String SETTING_IMMEDIATE_ALERT_ALARM_ENABLED = "alert_alarm_enabled";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_DELAYED = "alert_incall_delayed";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_ENABLED = "alert_incall_enabled";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_IN_CONTACTS_ENABLED = "alert_incall_enabled_in_contacts";
    public static final String SETTING_IMMEDIATE_ALERT_INCALL_NO_CONTACTS_ENABLED = "alert_incall_enabled_no_contacts";
    public static final String SETTING_IMMEDIATE_ALERT_SMS_ENABLED = "alert_sms_enabled";
    public static final String SETTING_IMMEDIATE_ALERT_SMS_IN_CONTACTS_ENABLED = "alert_sms_enabled_in_contacts";
    public static final String SETTING_IMMEDIATE_ALERT_SMS_NO_CONTACTS_ENABLED = "alert_sms_enabled_no_contacts";
    private static final String TAG = "MiBleDeviceManager";
    public static final int TYPE_MI_BAND = 1;
    public static final int TYPE_MI_KEY = 69;
    public static final int TYPE_MI_VR_CONTROLLER = 2;
    public static final int TYPE_UNKNOWN = 0;
    private ParcelUuid mClientId;
    private Context mContext;
    private MiBleDeviceManagerListener mListener;
    private IMiBleDeviceManager mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: miui.bluetooth.ble.MiBleDeviceManager.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MiBleDeviceManager.this.setServie(IMiBleDeviceManager.Stub.asInterface(iBinder));
            if (MiBleDeviceManager.this.mListener != null) {
                MiBleDeviceManager.this.mListener.onInit(MiBleDeviceManager.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            MiBleDeviceManager.this.setServie(null);
            if (MiBleDeviceManager.this.mListener != null) {
                MiBleDeviceManager.this.mListener.onDestroy();
            }
        }
    };
    private IBinder mToken;

    public interface MiBleDeviceManagerListener {
        void onDestroy();

        void onInit(MiBleDeviceManager miBleDeviceManager);
    }

    private MiBleDeviceManager(Context context, MiBleDeviceManagerListener miBleDeviceManagerListener) {
        if (context == null || context.getApplicationContext() == null) {
            throw new IllegalArgumentException("context not valid");
        }
        this.mClientId = new ParcelUuid(UUID.randomUUID());
        this.mToken = new Binder();
        this.mContext = context.getApplicationContext();
        this.mListener = miBleDeviceManagerListener;
    }

    private boolean checkReady() {
        if (this.mService != null) {
            return true;
        }
        Log.w(TAG, "Manager is not ready");
        return false;
    }

    public static MiBleDeviceManager createManager(Context context, MiBleDeviceManagerListener miBleDeviceManagerListener) {
        MiBleDeviceManager miBleDeviceManager = new MiBleDeviceManager(context, miBleDeviceManagerListener);
        miBleDeviceManager.init();
        return miBleDeviceManager;
    }

    public static String getSettingKeyForEvent(int i2) {
        return String.format("miui_ble_event_%d", Integer.valueOf(i2));
    }

    private void init() {
        Intent intent = new Intent("miui.bluetooth.mible.DeviceManagerService");
        intent.setComponent(new ComponentName("com.xiaomi.bluetooth", "com.android.bluetooth.ble.app.MiBleDeviceManagerService"));
        boolean zBindService = this.mContext.bindService(intent, this.mServiceConnection, 1);
        if (!zBindService) {
            intent.setComponent(new ComponentName("com.android.bluetooth", "com.android.bluetooth.ble.app.MiBleDeviceManagerService"));
            zBindService = this.mContext.bindService(intent, this.mServiceConnection, 1);
        }
        if (zBindService) {
            return;
        }
        Log.e(TAG, "bind manager service error: " + intent);
        if (this.mListener != null) {
            new Handler().post(new Runnable() { // from class: miui.bluetooth.ble.MiBleDeviceManager.2
                @Override // java.lang.Runnable
                public void run() {
                    MiBleDeviceManager.this.mListener.onDestroy();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setServie(IMiBleDeviceManager iMiBleDeviceManager) {
        this.mService = iMiBleDeviceManager;
    }

    public boolean bindDevice(String str) {
        if (checkReady()) {
            try {
                return this.mService.setSettingInteger(str, SETTING_BIND_DEVICE, 0);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public void close() {
        try {
            this.mContext.unbindService(this.mServiceConnection);
        } catch (Exception e2) {
            Log.e(TAG, "Close manager service error", e2);
        }
    }

    public List<String> getBoundDevices() {
        if (!checkReady()) {
            return null;
        }
        try {
            return this.mService.getBoundDevices();
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public int getDeviceType(String str) {
        if (!checkReady()) {
            return 0;
        }
        try {
            return this.mService.getDeviceType(str);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    public String getRegisterAppForBleEvent(String str, int i2) {
        if (!checkReady()) {
            return null;
        }
        try {
            return this.mService.getRegisterAppForBleEvent(str, i2);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public ScanResult getScanResult(String str) {
        if (!checkReady()) {
            return null;
        }
        try {
            return this.mService.getScanResult(str);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public int getServiceVersion() {
        if (!checkReady()) {
            return -1;
        }
        try {
            return this.mService.getServiceVersion();
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    public boolean getSettingsBoolean(String str, String str2) {
        if (checkReady()) {
            try {
                return this.mService.getSettingInteger(str, str2) != 0;
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public int getSettingsInteger(String str, String str2) {
        if (!checkReady()) {
            return 0;
        }
        try {
            return this.mService.getSettingInteger(str, str2);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    public String getSettingsString(String str, String str2) {
        if (!checkReady()) {
            return null;
        }
        try {
            return this.mService.getSettingString(str, str2);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public boolean isRegisted(String str) {
        try {
            Log.d(TAG, "manager isRegisted");
            return this.mService.isRegisted(str);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean registerAppForBleEvent(String str, int i2) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.setSettingString(str, getSettingKeyForEvent(i2), this.mContext.getPackageName());
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean registerBleEventListener(String str, int i2, IBleEventCallback iBleEventCallback) {
        if (iBleEventCallback != null && checkReady()) {
            try {
                return this.mService.registerBleEventListener(str, i2, iBleEventCallback);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public boolean registerSysNotificationService(String str, String str2, ISystemNotificationCallback iSystemNotificationCallback) {
        try {
            Log.d(TAG, "manager registerSysNotificationService");
            return this.mService.registerSysNotificationService(str, str2, iSystemNotificationCallback);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean setSettings(String str, String str2, boolean z2) {
        if (checkReady()) {
            try {
                return this.mService.setSettingInteger(str, str2, z2 ? 1 : 0);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public void setSysNotificationCallback(String str, ISystemNotificationCallback iSystemNotificationCallback) {
        try {
            Log.d(TAG, "manager setSysNotificationCallback");
            this.mService.setSysNotificationCallback(str, iSystemNotificationCallback);
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
    }

    public boolean startScanDevice(final int i2, final BluetoothAdapter.LeScanCallback leScanCallback) {
        if (!checkReady()) {
            return false;
        }
        try {
            this.mService.startScanDevice(this.mToken, this.mClientId, i2, new IScanDeviceCallback.Stub() { // from class: miui.bluetooth.ble.MiBleDeviceManager.3
                @Override // miui.bluetooth.ble.IScanDeviceCallback
                public void onScanDevice(int i3, BluetoothDevice bluetoothDevice, int i4, byte[] bArr) throws RemoteException {
                    if (i3 == i2) {
                        leScanCallback.onLeScan(bluetoothDevice, i4, bArr);
                    }
                }
            });
            return false;
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public void stopScanDevice() {
        if (checkReady()) {
            try {
                this.mService.stopScanDevice(this.mClientId);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
    }

    public boolean unRegisterSysNotificationService(String str) {
        try {
            Log.d(TAG, "manager unRegisterSysNotificationService");
            return this.mService.unRegisterSysNotificationService(str);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean unbindDevice(String str) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.deleteSettings(str);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean unregisterAppForBleEvent(String str, int i2) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.setSettingString(str, getSettingKeyForEvent(i2), null);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean unregisterBleEventListener(String str, int i2, IBleEventCallback iBleEventCallback) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.unregisterBleEventListener(str, i2, iBleEventCallback);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean bindDevice(String str, byte[] bArr) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.setToken(str, bArr);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean setSettings(String str, String str2, int i2) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.setSettingInteger(str, str2, i2);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean setSettings(String str, String str2, String str3) {
        if (!checkReady()) {
            return false;
        }
        try {
            return this.mService.setSettingString(str, str2, str3);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
