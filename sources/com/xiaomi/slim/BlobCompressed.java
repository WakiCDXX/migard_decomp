package com.xiaomi.slim;

import android.util.Log;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
class BlobCompressed {
    private static final String TAG = "BCompressed";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    BlobCompressed() {
    }

    static byte[] compress(Blob blob, byte[] bArr) {
        try {
            return CompressedDatagram.CompressedDatagramUtils.compress(bArr);
        } catch (Exception e2) {
            MyLog.w(TAG, "compress error " + e2);
            return bArr;
        }
    }

    static byte[] decompress(Blob blob, byte[] bArr) {
        try {
            byte[] bArrDecompress = CompressedDatagram.CompressedDatagramUtils.decompress(bArr);
            if (DEBUG) {
                MyLog.w(TAG, "decompress " + bArr.length + " to " + bArrDecompress.length + " for " + blob);
                if (blob.mStreamType == 1) {
                    MyLog.w(TAG, "decompress not support upStream");
                }
            }
            return bArrDecompress;
        } catch (Exception e2) {
            MyLog.w(TAG, "decompress error " + e2);
            return bArr;
        }
    }
}
