package com.miui.powerkeeper.cloudcontrol;

import android.content.Context;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CloudFunctionConfig {
    private static final boolean DEBUG = true;
    private static final String TAG = "CloudFunctionConfig";
    private static CloudFunctionConfig sInstance;
    private Object mLock = new Object();
    private List<ICloudFunctionListener> mCloudFunctionListener = new ArrayList();

    public interface ICloudFunctionListener {
        void onCloudFunctionUpdated();
    }

    private CloudFunctionConfig() {
    }

    public static String[] getFunctionParameter(Context context, String str, String[] strArr) {
        String string = SimpleSettings.Misc.getString(context, str + "_params", null);
        if (string == null) {
            return strArr;
        }
        try {
            return string.split(",");
        } catch (ArrayIndexOutOfBoundsException unused) {
            return strArr;
        }
    }

    public static String getFunctionParameterAtIndex(Context context, String str, int i2, String str2) {
        String string = SimpleSettings.Misc.getString(context, str + "_params", null);
        if (string == null) {
            return str2;
        }
        try {
            return string.split(",")[i2];
        } catch (ArrayIndexOutOfBoundsException unused) {
            return str2;
        }
    }

    public static String getFunctionParameters(Context context, String str, String str2) {
        return SimpleSettings.Misc.getString(context, str + "_params", str2);
    }

    public static String getFunctionParameters2(Context context, String str, String str2) {
        return SimpleSettings.Misc.getString(context, str + "_params2", str2);
    }

    public static String getFunctionParameters2ForUser(Context context, String str, String str2, int i2) {
        return SimpleSettings.Misc.getStringForUser(context, str + "_params2", str2, i2);
    }

    public static String getFunctionParametersForUser(Context context, String str, String str2, int i2) {
        return SimpleSettings.Misc.getStringForUser(context, str + "_params", str2, i2);
    }

    public static synchronized CloudFunctionConfig getInstance() {
        try {
            if (sInstance == null) {
                sInstance = new CloudFunctionConfig();
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public void notifyAllListeners() {
        synchronized (this.mLock) {
            try {
                List<ICloudFunctionListener> list = this.mCloudFunctionListener;
                if (list != null) {
                    for (int size = list.size() - 1; size >= 0; size--) {
                        ICloudFunctionListener iCloudFunctionListener = this.mCloudFunctionListener.get(size);
                        if (iCloudFunctionListener != null) {
                            iCloudFunctionListener.onCloudFunctionUpdated();
                        }
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void registerCloudFunctionConfig(ICloudFunctionListener iCloudFunctionListener) {
        synchronized (this.mLock) {
            try {
                List<ICloudFunctionListener> list = this.mCloudFunctionListener;
                if (list != null) {
                    list.add(iCloudFunctionListener);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unRegisterCloudFunctionConfig(ICloudFunctionListener iCloudFunctionListener) {
        synchronized (this.mLock) {
            try {
                List<ICloudFunctionListener> list = this.mCloudFunctionListener;
                if (list != null) {
                    list.remove(iCloudFunctionListener);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
