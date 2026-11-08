package com.xiaomi.push.mpcd;

import android.content.Context;
import com.xiaomi.channel.commonutils.android.DataCryptUtils;
import com.xiaomi.channel.commonutils.misc.JobMutualExclusor;
import com.xiaomi.channel.commonutils.string.Base64Coder;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.xmpush.thrift.ConfigKey;

/* JADX INFO: loaded from: classes.dex */
public class CDataHelper {
    private CDataHelper() {
    }

    public static boolean checkDataCollectionJobMutual(Context context, String str, long j2) {
        return OnlineConfig.getInstance(context).getBooleanValue(ConfigKey.DCJobMutualSwitch.getValue(), false) && context.getApplicationInfo().targetSdkVersion < 29 && !JobMutualExclusor.checkPeriodAndRecordWithFileLock(context, str, j2);
    }

    public static byte[] decryptData(String str, byte[] bArr) {
        byte[] bArrDecode = Base64Coder.decode(str);
        try {
            parseKey(bArrDecode);
            return DataCryptUtils.mipushDecrypt(bArrDecode, bArr);
        } catch (Exception unused) {
            return null;
        }
    }

    public static byte[] encryptData(String str, byte[] bArr) {
        byte[] bArrDecode = Base64Coder.decode(str);
        try {
            parseKey(bArrDecode);
            return DataCryptUtils.mipushEncrypt(bArrDecode, bArr);
        } catch (Exception unused) {
            return null;
        }
    }

    private static void parseKey(byte[] bArr) {
        if (bArr.length >= 2) {
            bArr[0] = 99;
            bArr[1] = 100;
        }
    }
}
