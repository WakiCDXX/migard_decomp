package miui.bluetooth.ble;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class MiBleProfile {
    public static final String ACTION_MIBLE_SERVICE = "miui.bluetooth.mible.Service";
    public static final String ACTION_SELECT_DEVICE = "miui.bluetooth.action.PICK_DEVICE";
    protected static final boolean DBG = true;
    public static final String EXTRA_MIBLE_PROPERTY = "miui.bluetooth.extra.MIBLE_PROPERTY";
    private static final int MSG_PROPERTY = 2;
    private static final int MSG_STATUS = 1;
    public static final int PROPERTY_ALARM_CLOCK = 106;
    public static final int PROPERTY_ALERT_NOTIFICATION = 8;
    public static final int PROPERTY_BATTERY = 6;
    public static final int PROPERTY_DEVICE_CONTROL = 2;
    public static final int PROPERTY_DEVICE_INFO = 101;
    public static final int PROPERTY_FIRMWARE = 3;
    public static final int PROPERTY_IMMEDIATE_ALERT = 5;
    public static final int PROPERTY_LINK_LOSS = 7;
    public static final int PROPERTY_MI_BAND_EVENT = 108;
    public static final int PROPERTY_MI_KEY = 107;

    @Deprecated
    public static final int PROPERTY_PAY = 4;
    public static final int PROPERTY_SPORT_ACTIVITIES = 104;
    public static final int PROPERTY_SPORT_STEPS = 103;
    public static final int PROPERTY_THEME_COLOR = 105;
    public static final int PROPERTY_UNDEFINED = 0;

    @Deprecated
    public static final int PROPERTY_UNLOCK = 1;
    public static final int PROPERTY_USER_INFO = 102;
    public static final int SERVICE_VERSION_UNKNOWN = -1;
    public static final int STATUS_CONNECTED = 2;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_DISCONNECTING = 3;
    public static final int STATUS_ERROR = -1;
    public static final int STATUS_READY = 4;
    protected static final String TAG = "MiBleProfile";
    protected IProfileStateChangeCallback mCallback;
    protected final ParcelUuid mClientId;
    private boolean mConnectWhenBind;
    protected Context mContext;
    protected String mDevice;
    private Handler mHandler;
    private int mProfileState;
    private IBluetoothMiBlePropertyCallback mPropertyCallback;
    private SparseArray<IPropertyNotifyCallback> mPropertyCallbacks;
    protected IBluetoothMiBle mService;
    private IBluetoothMiBleCallback mServiceCallback;
    private ServiceConnection mServiceConnection;
    private final IBinder mToken;

    public interface IProfileStateChangeCallback {
        void onState(int i2);
    }

    public interface IPropertyNotifyCallback {
        void notifyProperty(int i2, byte[] bArr);
    }

    public MiBleProfile(Context context, String str) {
        this(context, str, null);
    }

    public void connect() {
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle != null) {
            try {
                iBluetoothMiBle.connect(this.mDevice, this.mClientId);
                return;
            } catch (RemoteException e2) {
                Log.w(TAG, "connect: ", e2);
                Handler handler = this.mHandler;
                handler.sendMessage(handler.obtainMessage(1, -1, 0));
                return;
            }
        }
        this.mConnectWhenBind = true;
        Intent intent = new Intent(ACTION_MIBLE_SERVICE);
        intent.setClassName("com.xiaomi.bluetooth", "com.android.bluetooth.ble.BluetoothMiBleService");
        intent.setPackage("com.xiaomi.bluetooth");
        boolean zBindService = this.mContext.bindService(intent, this.mServiceConnection, 1);
        if (!zBindService) {
            intent = new Intent(ACTION_MIBLE_SERVICE);
            intent.setClassName("com.android.bluetooth", "com.android.bluetooth.ble.BluetoothMiBleService");
            intent.setPackage("com.android.bluetooth");
            zBindService = this.mContext.bindService(intent, this.mServiceConnection, 1);
        }
        if (zBindService) {
            return;
        }
        Log.e(TAG, "connect: bind service error" + intent.toString());
        Handler handler2 = this.mHandler;
        handler2.sendMessage(handler2.obtainMessage(1, -1, 0));
    }

    public void disconnect() {
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle == null) {
            return;
        }
        try {
            iBluetoothMiBle.unregisterClient(this.mToken, this.mDevice, this.mClientId);
            this.mContext.unbindService(this.mServiceConnection);
        } catch (RemoteException e2) {
            Log.w(TAG, "disconnect: ", e2);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(1, -1, 0));
        } catch (IllegalArgumentException e3) {
            Log.w(TAG, "disconnect: ", e3);
            Handler handler2 = this.mHandler;
            handler2.sendMessage(handler2.obtainMessage(1, -1, 0));
        }
    }

    public String getDeviceAddress() {
        return this.mDevice;
    }

    public byte[] getProperty(int i2) {
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle == null) {
            return null;
        }
        try {
            return iBluetoothMiBle.getProperty(this.mDevice, this.mClientId, i2);
        } catch (RemoteException e2) {
            Log.e(TAG, "getProperty: ", e2);
            return null;
        }
    }

    public int getRssi() {
        if (!isReady()) {
            return Integer.MIN_VALUE;
        }
        try {
            return this.mService.getRssi(this.mDevice, this.mClientId);
        } catch (RemoteException e2) {
            Log.e(TAG, "getRssi: ", e2);
            return Integer.MIN_VALUE;
        }
    }

    public int getServiceVersion() {
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle == null) {
            return -1;
        }
        try {
            return iBluetoothMiBle.getServiceVersion();
        } catch (RemoteException e2) {
            Log.e(TAG, "getServiceVersion", e2);
            return -1;
        }
    }

    public boolean isReady() {
        return this.mProfileState == 4;
    }

    public boolean isSupportProperty(int i2) {
        try {
            IBluetoothMiBle iBluetoothMiBle = this.mService;
            return iBluetoothMiBle != null && iBluetoothMiBle.supportProperty(this.mDevice, i2);
        } catch (RemoteException e2) {
            Log.e(TAG, "isSupportProperty: ", e2);
            return false;
        }
    }

    public boolean registerPropertyNotifyCallback(int i2, IPropertyNotifyCallback iPropertyNotifyCallback) {
        boolean zRegisterPropertyCallback;
        this.mPropertyCallbacks.put(i2, iPropertyNotifyCallback);
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle != null) {
            try {
                zRegisterPropertyCallback = iBluetoothMiBle.registerPropertyCallback(this.mDevice, this.mClientId, i2, this.mPropertyCallback);
            } catch (RemoteException e2) {
                Log.e(TAG, "registerPropertyNotifyCallback: ", e2);
                zRegisterPropertyCallback = false;
            }
        } else {
            zRegisterPropertyCallback = false;
        }
        if (zRegisterPropertyCallback) {
            return true;
        }
        this.mPropertyCallbacks.remove(i2);
        return false;
    }

    public void setProfileStateChangeCallback(IProfileStateChangeCallback iProfileStateChangeCallback) {
        this.mCallback = iProfileStateChangeCallback;
    }

    public boolean setProperty(int i2, byte[] bArr) {
        IBluetoothMiBle iBluetoothMiBle = this.mService;
        if (iBluetoothMiBle == null) {
            return false;
        }
        try {
            return iBluetoothMiBle.setProperty(this.mDevice, this.mClientId, i2, bArr);
        } catch (RemoteException e2) {
            Log.e(TAG, "setProperty: ", e2);
            return false;
        }
    }

    public boolean unregisterPropertyNotifyCallback(int i2) {
        this.mPropertyCallbacks.remove(i2);
        if (this.mPropertyCallbacks.get(i2) != null) {
            return true;
        }
        try {
            IBluetoothMiBle iBluetoothMiBle = this.mService;
            if (iBluetoothMiBle != null) {
                return iBluetoothMiBle.unregisterPropertyCallback(this.mDevice, this.mClientId, i2, this.mPropertyCallback);
            }
            return true;
        } catch (RemoteException e2) {
            Log.e(TAG, "unregisterPropertyNotifyCallback: ", e2);
            return false;
        }
    }

    public MiBleProfile(Context context, String str, IProfileStateChangeCallback iProfileStateChangeCallback) {
        this.mClientId = new ParcelUuid(UUID.randomUUID());
        this.mToken = new Binder();
        this.mConnectWhenBind = false;
        this.mPropertyCallbacks = new SparseArray<>();
        this.mProfileState = 0;
        this.mServiceConnection = new ServiceConnection() { // from class: miui.bluetooth.ble.MiBleProfile.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(MiBleProfile.TAG, "onServiceConnected()");
                MiBleProfile.this.mService = IBluetoothMiBle.Stub.asInterface(iBinder);
                MiBleProfile.this.mHandler.sendMessage(MiBleProfile.this.mHandler.obtainMessage(1, 2, 0));
                try {
                    MiBleProfile miBleProfile = MiBleProfile.this;
                    IBluetoothMiBle iBluetoothMiBle = miBleProfile.mService;
                    IBinder iBinder2 = miBleProfile.mToken;
                    MiBleProfile miBleProfile2 = MiBleProfile.this;
                    iBluetoothMiBle.registerClient(iBinder2, miBleProfile2.mDevice, miBleProfile2.mClientId, miBleProfile2.mServiceCallback);
                } catch (RemoteException e2) {
                    Log.e(MiBleProfile.TAG, "onServiceConnected", e2);
                    MiBleProfile.this.mHandler.sendMessage(MiBleProfile.this.mHandler.obtainMessage(1, -1, 0));
                }
                if (MiBleProfile.this.mConnectWhenBind) {
                    MiBleProfile.this.mConnectWhenBind = false;
                    MiBleProfile.this.connect();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                MiBleProfile miBleProfile = MiBleProfile.this;
                miBleProfile.mService = null;
                miBleProfile.mProfileState = 0;
                MiBleProfile.this.mHandler.sendMessage(MiBleProfile.this.mHandler.obtainMessage(1, 0, 0));
            }
        };
        this.mServiceCallback = new IBluetoothMiBleCallback.Stub() { // from class: miui.bluetooth.ble.MiBleProfile.2
            @Override // miui.bluetooth.ble.IBluetoothMiBleCallback
            public void onConnectionState(ParcelUuid parcelUuid, int i2) throws RemoteException {
                Log.d(MiBleProfile.TAG, "onConnectionState() sate=" + i2);
                if (MiBleProfile.this.mClientId.equals(parcelUuid)) {
                    MiBleProfile.this.mProfileState = i2;
                    MiBleProfile.this.mHandler.sendMessage(MiBleProfile.this.mHandler.obtainMessage(1, i2, 0));
                }
            }
        };
        this.mPropertyCallback = new IBluetoothMiBlePropertyCallback.Stub() { // from class: miui.bluetooth.ble.MiBleProfile.3
            @Override // miui.bluetooth.ble.IBluetoothMiBlePropertyCallback
            public void notifyProperty(ParcelUuid parcelUuid, int i2, byte[] bArr) throws RemoteException {
                Log.d(MiBleProfile.TAG, "mPropertyCallback() property=" + i2);
                if (MiBleProfile.this.mClientId.equals(parcelUuid)) {
                    Message messageObtainMessage = MiBleProfile.this.mHandler.obtainMessage(2);
                    messageObtainMessage.arg1 = i2;
                    messageObtainMessage.obj = bArr;
                    MiBleProfile.this.mHandler.sendMessage(messageObtainMessage);
                }
            }
        };
        this.mDevice = str;
        this.mContext = context;
        this.mCallback = iProfileStateChangeCallback;
        try {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
        } catch (RuntimeException e2) {
            Log.e(TAG, "prepare looper failed", e2);
        }
        this.mHandler = new Handler(new Handler.Callback() { // from class: miui.bluetooth.ble.MiBleProfile.4
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                int i2 = message.what;
                if (i2 == 1) {
                    IProfileStateChangeCallback iProfileStateChangeCallback2 = MiBleProfile.this.mCallback;
                    if (iProfileStateChangeCallback2 != null) {
                        iProfileStateChangeCallback2.onState(message.arg1);
                    }
                    return true;
                }
                if (i2 != 2) {
                    return false;
                }
                int i3 = message.arg1;
                IPropertyNotifyCallback iPropertyNotifyCallback = (IPropertyNotifyCallback) MiBleProfile.this.mPropertyCallbacks.get(i3);
                if (iPropertyNotifyCallback != null) {
                    iPropertyNotifyCallback.notifyProperty(i3, (byte[]) message.obj);
                }
                return true;
            }
        });
    }
}
