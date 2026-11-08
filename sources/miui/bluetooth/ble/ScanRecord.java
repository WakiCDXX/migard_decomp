package miui.bluetooth.ble;

import android.os.ParcelUuid;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public final class ScanRecord {
    private static final ParcelUuid BASE_UUID = ParcelUuid.fromString("00000000-0000-1000-8000-00805F9B34FB");
    private static final int DATA_TYPE_FLAGS = 1;
    private static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 9;
    private static final int DATA_TYPE_LOCAL_NAME_SHORT = 8;
    private static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 255;
    private static final int DATA_TYPE_SERVICE_DATA = 22;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE = 7;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL = 6;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE = 3;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL = 2;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE = 5;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL = 4;
    private static final int DATA_TYPE_TX_POWER_LEVEL = 10;
    private static final String TAG = "ScanRecord";
    public static final int UUID_BYTES_128_BIT = 16;
    public static final int UUID_BYTES_16_BIT = 2;
    public static final int UUID_BYTES_32_BIT = 4;
    private final int mAdvertiseFlags;
    private final byte[] mBytes;
    private final String mDeviceName;
    private final SparseArray<byte[]> mManufacturerSpecificData;
    private final Map<ParcelUuid, byte[]> mServiceData;
    private final List<ParcelUuid> mServiceUuids;
    private final int mTxPowerLevel;

    private ScanRecord(List<ParcelUuid> list, SparseArray<byte[]> sparseArray, Map<ParcelUuid, byte[]> map, int i2, int i3, String str, byte[] bArr) {
        this.mServiceUuids = list;
        this.mManufacturerSpecificData = sparseArray;
        this.mServiceData = map;
        this.mDeviceName = str;
        this.mAdvertiseFlags = i2;
        this.mTxPowerLevel = i3;
        this.mBytes = bArr;
    }

    private static byte[] extractBytes(byte[] bArr, int i2, int i3) {
        byte[] bArr2 = new byte[i3];
        System.arraycopy(bArr, i2, bArr2, 0, i3);
        return bArr2;
    }

    /* JADX WARN: Code duplicated, block: B:27:0x008d  */
    /* JADX WARN: Code duplicated, block: B:28:0x008f  */
    public static ScanRecord parseFromBytes(byte[] bArr) {
        ArrayList arrayList;
        if (bArr == null) {
            return null;
        }
        ArrayList arrayList2 = new ArrayList();
        SparseArray sparseArray = new SparseArray();
        ArrayMap arrayMap = new ArrayMap();
        int i2 = 0;
        String str = null;
        byte b2 = -2147483648;
        int i3 = -1;
        while (i2 < bArr.length) {
            try {
                int i4 = i2 + 1;
                int i5 = bArr[i2] & DATA_TYPE_MANUFACTURER_SPECIFIC_DATA;
                if (i5 == 0) {
                    if (arrayList2.isEmpty()) {
                        arrayList = null;
                    } else {
                        arrayList = arrayList2;
                    }
                    return new ScanRecord(arrayList, sparseArray, arrayMap, i3, b2, str, bArr);
                }
                int i6 = i5 - 1;
                int i7 = i2 + 2;
                int i8 = bArr[i4] & DATA_TYPE_MANUFACTURER_SPECIFIC_DATA;
                if (i8 == 22) {
                    arrayMap.put(parseUuidFrom(extractBytes(bArr, i7, 2)), extractBytes(bArr, i2 + 4, i5 - 3));
                } else if (i8 != DATA_TYPE_MANUFACTURER_SPECIFIC_DATA) {
                    switch (i8) {
                        case 1:
                            i3 = bArr[i7] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL;
                            break;
                        case 2:
                        case 3:
                            parseServiceUuid(bArr, i7, i6, 2, arrayList2);
                            break;
                        case 4:
                        case 5:
                            parseServiceUuid(bArr, i7, i6, 4, arrayList2);
                            break;
                        case 6:
                        case 7:
                            parseServiceUuid(bArr, i7, i6, 16, arrayList2);
                            break;
                        case 8:
                        case 9:
                            str = new String(extractBytes(bArr, i7, i6));
                            break;
                        case 10:
                            b2 = bArr[i7];
                            break;
                    }
                } else {
                    sparseArray.put(((bArr[i2 + 3] & DATA_TYPE_MANUFACTURER_SPECIFIC_DATA) << 8) + (DATA_TYPE_MANUFACTURER_SPECIFIC_DATA & bArr[i7]), extractBytes(bArr, i2 + 4, i5 - 3));
                }
                i2 = i7 + i6;
            } catch (Exception unused) {
                Log.e(TAG, "unable to parse scan record: " + Arrays.toString(bArr));
                return new ScanRecord(null, null, null, -1, Integer.MIN_VALUE, null, bArr);
            }
        }
        if (arrayList2.isEmpty()) {
            arrayList = null;
        } else {
            arrayList = arrayList2;
        }
        return new ScanRecord(arrayList, sparseArray, arrayMap, i3, b2, str, bArr);
    }

    private static int parseServiceUuid(byte[] bArr, int i2, int i3, int i4, List<ParcelUuid> list) {
        while (i3 > 0) {
            list.add(parseUuidFrom(extractBytes(bArr, i2, i4)));
            i3 -= i4;
            i2 += i4;
        }
        return i2;
    }

    private static ParcelUuid parseUuidFrom(byte[] bArr) {
        long j2;
        if (bArr == null) {
            throw new IllegalArgumentException("uuidBytes cannot be null");
        }
        int length = bArr.length;
        if (length != 2 && length != 4 && length != 16) {
            throw new IllegalArgumentException("uuidBytes length invalid - " + length);
        }
        if (length == 16) {
            ByteBuffer byteBufferOrder = ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN);
            return new ParcelUuid(new UUID(byteBufferOrder.getLong(8), byteBufferOrder.getLong(0)));
        }
        if (length == 2) {
            j2 = ((long) (bArr[0] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)) + ((long) ((bArr[1] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8));
        } else {
            j2 = ((long) ((bArr[3] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 24)) + ((long) (bArr[0] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)) + ((long) ((bArr[1] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8)) + ((long) ((bArr[2] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 16));
        }
        ParcelUuid parcelUuid = BASE_UUID;
        return new ParcelUuid(new UUID(parcelUuid.getUuid().getMostSignificantBits() + (j2 << 32), parcelUuid.getUuid().getLeastSignificantBits()));
    }

    public int getAdvertiseFlags() {
        return this.mAdvertiseFlags;
    }

    public byte[] getBytes() {
        return this.mBytes;
    }

    public String getDeviceName() {
        return this.mDeviceName;
    }

    public SparseArray<byte[]> getManufacturerSpecificData() {
        return this.mManufacturerSpecificData;
    }

    public Map<ParcelUuid, byte[]> getServiceData() {
        return this.mServiceData;
    }

    public List<ParcelUuid> getServiceUuids() {
        return this.mServiceUuids;
    }

    public int getTxPowerLevel() {
        return this.mTxPowerLevel;
    }

    public String toString() {
        return "ScanRecord [mAdvertiseFlags=" + this.mAdvertiseFlags + ", mServiceUuids=" + this.mServiceUuids + ", mManufacturerSpecificData=" + toString(this.mManufacturerSpecificData) + ", mServiceData=" + toString(this.mServiceData) + ", mTxPowerLevel=" + this.mTxPowerLevel + ", mDeviceName=" + this.mDeviceName + "]";
    }

    public byte[] getManufacturerSpecificData(int i2) {
        return this.mManufacturerSpecificData.get(i2);
    }

    public byte[] getServiceData(ParcelUuid parcelUuid) {
        Map<ParcelUuid, byte[]> map;
        if (parcelUuid == null || (map = this.mServiceData) == null) {
            return null;
        }
        return map.get(parcelUuid);
    }

    static String toString(SparseArray<byte[]> sparseArray) {
        if (sparseArray == null) {
            return "null";
        }
        if (sparseArray.size() == 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i2 = 0; i2 < sparseArray.size(); i2++) {
            sb.append(sparseArray.keyAt(i2));
            sb.append("=");
            sb.append(Arrays.toString(sparseArray.valueAt(i2)));
        }
        sb.append('}');
        return sb.toString();
    }

    static <T> String toString(Map<T, byte[]> map) {
        if (map == null) {
            return "null";
        }
        if (map.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Iterator<Map.Entry<T, byte[]>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            T key = it.next().getKey();
            sb.append(key);
            sb.append("=");
            sb.append(Arrays.toString(map.get(key)));
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
