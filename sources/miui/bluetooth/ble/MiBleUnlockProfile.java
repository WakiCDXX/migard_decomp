package miui.bluetooth.ble;

import android.content.Context;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public class MiBleUnlockProfile extends MiBleProfile {

    public interface OnUnlockStateChangeListener {
        public static final byte STATE_AUTHORISED = 1;
        public static final byte STATE_LOCKED = 0;
        public static final byte STATE_UNLOCKED = 2;

        void onUnlocked(byte b2);
    }

    public MiBleUnlockProfile(Context context, String str, MiBleProfile.IProfileStateChangeCallback iProfileStateChangeCallback) {
        super(context, str, iProfileStateChangeCallback);
    }

    public void registerUnlockListener(final OnUnlockStateChangeListener onUnlockStateChangeListener) {
        if (onUnlockStateChangeListener == null) {
            return;
        }
        registerPropertyNotifyCallback(1, new MiBleProfile.IPropertyNotifyCallback() { // from class: miui.bluetooth.ble.MiBleUnlockProfile.1
            @Override // miui.bluetooth.ble.MiBleProfile.IPropertyNotifyCallback
            public void notifyProperty(int i2, byte[] bArr) {
                if (i2 == 1) {
                    onUnlockStateChangeListener.onUnlocked(bArr != null ? bArr[0] : (byte) 0);
                }
            }
        });
    }

    public boolean setLock(String str) {
        if (!isReady()) {
            return false;
        }
        try {
            return this.mService.authorize(this.mDevice, this.mClientId, str);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean setRssiThreshold(int i2) {
        if (!isReady()) {
            return false;
        }
        try {
            return this.mService.setRssiThreshold(this.mDevice, this.mClientId, i2);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean unlock() {
        if (!isReady()) {
            return false;
        }
        try {
            return this.mService.authenticate(this.mDevice, this.mClientId);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public void unregisterUnlockListener() {
        unregisterPropertyNotifyCallback(1);
    }
}
