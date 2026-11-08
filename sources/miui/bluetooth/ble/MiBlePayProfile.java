package miui.bluetooth.ble;

import android.content.Context;
import android.os.RemoteException;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public class MiBlePayProfile extends MiBleProfile {

    public interface OnRSSIChangedListerner {
        void onRssi(int i2);
    }

    public MiBlePayProfile(Context context, String str, MiBleProfile.IProfileStateChangeCallback iProfileStateChangeCallback) {
        super(context, str, iProfileStateChangeCallback);
    }

    public byte[] encrypt(byte[] bArr) {
        if (!isReady()) {
            return null;
        }
        try {
            return this.mService.encrypt(this.mDevice, this.mClientId, bArr);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public void registerRssiChangedListener(final OnRSSIChangedListerner onRSSIChangedListerner) {
        if (onRSSIChangedListerner == null) {
            return;
        }
        registerPropertyNotifyCallback(4, new MiBleProfile.IPropertyNotifyCallback() { // from class: miui.bluetooth.ble.MiBlePayProfile.1
            @Override // miui.bluetooth.ble.MiBleProfile.IPropertyNotifyCallback
            public void notifyProperty(int i2, byte[] bArr) {
                if (i2 == 4 && bArr != null && bArr.length == 4) {
                    onRSSIChangedListerner.onRssi(ByteBuffer.wrap(bArr).getInt());
                }
            }
        });
    }

    public boolean setEncryptionKey(byte[] bArr) {
        if (!isReady()) {
            return false;
        }
        try {
            return this.mService.setEncryptionKey(this.mDevice, this.mClientId, bArr);
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public void unregisterRssiChangedListener() {
        unregisterPropertyNotifyCallback(4);
    }
}
