package miui.bluetooth.ble;

import android.os.ParcelUuid;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;

/* JADX INFO: loaded from: classes.dex */
public class MiServiceData {
    public static final byte CAPABILITY_CENTRAL = 2;
    public static final byte CAPABILITY_CONNECTABLE = 1;
    public static final byte CAPABILITY_ENCRYPT = 4;
    public static final byte CAPABILITY_IO = 24;
    private static final int DATA_MIN_LENGTH = 5;
    private static final int FLAG_BINDING = 512;
    private static final int FLAG_CAPABILITY = 32;
    private static final int FLAG_CENTRAL = 4;
    private static final int FLAG_CONNECTED = 2;
    private static final int FLAG_CUSTOM_DATA = 128;
    private static final int FLAG_ENCRYPTED = 8;
    private static final int FLAG_EVENT = 64;
    private static final int FLAG_MAC_ADDRESS = 16;
    private static final int FLAG_NEW_FACTORY = 1;
    private static final int FLAG_SUBTITLE = 256;
    private byte[] mData;
    private int mFrameControl;
    private int mVersion;
    private static final String UUID_BASE = "0000%4s-0000-1000-8000-00805f9b34fb";
    public static final ParcelUuid MI_SERVICE_UUID = ParcelUuid.fromString(String.format(UUID_BASE, "fe95"));

    public MiServiceData(byte[] bArr) {
        if (bArr == null || bArr.length < 5) {
            throw new IllegalArgumentException("Mi Service data length must >= 5");
        }
        this.mData = bArr;
        int i2 = bArr[0] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL;
        byte b2 = bArr[1];
        this.mFrameControl = i2 | ((b2 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8);
        this.mVersion = (b2 & 240) >> 4;
    }

    public static MiServiceData fromScanRecord(ScanRecord scanRecord) {
        byte[] serviceData;
        if (scanRecord == null || scanRecord.getServiceData() == null || (serviceData = scanRecord.getServiceData(MI_SERVICE_UUID)) == null || serviceData.length < 5) {
            return null;
        }
        return new MiServiceData(serviceData);
    }

    private int getEventDataIndex() {
        int i2 = hasMacAddress() ? 11 : 5;
        return hasCapability() ? i2 + 1 : i2;
    }

    public byte getCapability() {
        if (!hasCapability()) {
            return (byte) 0;
        }
        int i2 = hasMacAddress() ? 11 : 5;
        byte[] bArr = this.mData;
        if (bArr.length >= i2 + 6) {
            return bArr[i2];
        }
        return (byte) 0;
    }

    public byte[] getCustomData() {
        if (!hasCustomData()) {
            return null;
        }
        int i2 = hasMacAddress() ? 11 : 5;
        if (hasCapability()) {
            i2++;
        }
        if (hasEvent()) {
            i2 += 3;
        }
        byte[] bArr = this.mData;
        if (bArr.length <= i2) {
            return null;
        }
        int i3 = bArr[i2];
        byte[] bArr2 = new byte[i3];
        System.arraycopy(bArr, i2 + 1, bArr2, 0, i3);
        return bArr2;
    }

    public byte[] getData() {
        return this.mData;
    }

    public byte getEvent() {
        if (!hasEvent()) {
            return (byte) 0;
        }
        int eventDataIndex = getEventDataIndex();
        byte[] bArr = this.mData;
        if (bArr.length >= eventDataIndex + 3) {
            return bArr[eventDataIndex + 2];
        }
        return (byte) 0;
    }

    public int getEventID() {
        if (!hasEvent()) {
            return 0;
        }
        int eventDataIndex = getEventDataIndex();
        byte[] bArr = this.mData;
        return ((bArr[eventDataIndex + 1] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8) | (bArr[eventDataIndex] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL);
    }

    public int getFrameCounter() {
        return this.mData[4] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL;
    }

    public byte[] getMacAddress() {
        if (!hasMacAddress()) {
            return null;
        }
        byte[] bArr = this.mData;
        if (bArr.length < 11) {
            return null;
        }
        byte[] bArr2 = new byte[6];
        System.arraycopy(bArr, 5, bArr2, 0, 6);
        return bArr2;
    }

    public int getProductID() {
        byte[] bArr = this.mData;
        return ((bArr[3] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8) | (bArr[2] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL);
    }

    public boolean hasCapability() {
        return (this.mFrameControl & 32) != 0;
    }

    public boolean hasCustomData() {
        return (this.mFrameControl & 128) != 0;
    }

    public boolean hasEvent() {
        return (this.mFrameControl & 64) != 0;
    }

    public boolean hasMacAddress() {
        return (this.mFrameControl & 16) != 0;
    }

    public boolean hasSubTitle() {
        return (this.mFrameControl & 256) != 0;
    }

    public boolean isBindingFrame() {
        return (this.mFrameControl & 512) != 0;
    }

    public boolean isCentral() {
        return (this.mFrameControl & 4) != 0;
    }

    public boolean isConnected() {
        return (this.mFrameControl & 2) != 0;
    }

    public boolean isEncrypted() {
        return (this.mFrameControl & 8) != 0;
    }

    public boolean isNewFactory() {
        return (this.mFrameControl & 1) != 0;
    }
}
