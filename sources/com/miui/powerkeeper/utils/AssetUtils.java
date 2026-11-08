package com.miui.powerkeeper.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.cloudcontrol.CloudUpdateReceiver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: classes.dex */
public class AssetUtils {
    public static boolean doesAssetExist(Context context, String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            return false;
        }
        try {
            String[] list = context.getAssets().list(str);
            if (list != null) {
                for (String str3 : list) {
                    if (str2.equals(str3)) {
                        return true;
                    }
                }
            }
        } catch (IOException unused) {
        }
        return false;
    }

    public static InputStream getInputStream(Context context, String str) throws IOException {
        return context.getAssets().open(str);
    }

    /* JADX WARN: Code duplicated, block: B:37:0x0061 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static String getStrFromAssetFile(Context context, String str) {
        try {
            InputStream inputStreamOpen = context.getAssets().open(str);
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamOpen));
                try {
                    int iAvailable = inputStreamOpen.available();
                    if (iAvailable <= 0) {
                        Log.e(CloudUpdateReceiver.TAG, "file " + str + " may be too large");
                    }
                    StringBuilder sb = new StringBuilder(iAvailable + 16);
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            String string = sb.toString();
                            bufferedReader.close();
                            inputStreamOpen.close();
                            return string;
                        }
                        sb.append(line);
                        if (inputStreamOpen != null) {
                            try {
                                inputStreamOpen.close();
                            } catch (Throwable th) {
                                th.addSuppressed(th);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                    throw th2;
                }
            } catch (Throwable th4) {
                if (inputStreamOpen != null) {
                    inputStreamOpen.close();
                }
                throw th4;
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }
}
