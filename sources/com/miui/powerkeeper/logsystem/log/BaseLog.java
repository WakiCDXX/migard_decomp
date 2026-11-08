package com.miui.powerkeeper.logsystem.log;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.logsystem.config.Scene;
import com.miui.powerkeeper.logsystem.log.logcatch.FileCatch;
import com.miui.powerkeeper.logsystem.log.logcatch.IndexCatch;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class BaseLog implements LogInterface {
    public static final String TAG = "LS-BaseLog";
    protected static Scene mScene;
    protected Context mContext;
    private FileCatch mFileCatch;

    protected BaseLog(Context context) {
        this.mContext = context;
        this.mFileCatch = FileCatch.getInstance(context);
    }

    private String getFilePath(int i2, long j2) {
        return this.mFileCatch.getFilePath(i2, j2, this);
    }

    private int getSceneIdFromFileName(String str) {
        try {
            String valueFromFileName = getValueFromFileName(str, FileCatch.FILE_NAME_SCENE_ID_LABEL);
            if (valueFromFileName != null) {
                return Integer.valueOf(valueFromFileName).intValue();
            }
            return -1;
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    private long getTimeFromFileName(String str) {
        try {
            String valueFromFileName = getValueFromFileName(str, FileCatch.FILE_NAME_TIME_LABEL);
            if (valueFromFileName != null) {
                return Long.valueOf(valueFromFileName).longValue();
            }
            return -1L;
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            return -1L;
        }
    }

    private String getValueFromFileName(String str, String str2) {
        int iIndexOf;
        if (str == null || (iIndexOf = str.indexOf(95)) < 0) {
            return null;
        }
        while (iIndexOf < str.length() - 2) {
            int i2 = iIndexOf + 1;
            iIndexOf += 2;
            if (!str.substring(i2, iIndexOf).equals(str2)) {
                iIndexOf = str.indexOf(95, i2);
                if (iIndexOf <= 0) {
                    return null;
                }
            } else if (str.charAt(iIndexOf) >= '0' && str.charAt(iIndexOf) <= '9') {
                int iIndexOf2 = str.indexOf(95, iIndexOf);
                return iIndexOf2 < 0 ? str.substring(iIndexOf) : str.substring(iIndexOf, iIndexOf2);
            }
        }
        return null;
    }

    static void setWorkingScene(Scene scene) {
        mScene = scene;
    }

    public final void catchFileIndex(int i2, long j2) {
        String stringValue = SharedPrefUtil.getInstance(this.mContext, "log_system_config").getStringValue(IndexCatch.getSPkey(i2), "");
        if (!LSUtils.isEmptyString(stringValue)) {
            stringValue = stringValue + ";";
        }
        if (stringValue.contains(j2 + "")) {
            return;
        }
        SharedPrefUtil.getInstance(this.mContext, "log_system_config").save(IndexCatch.getSPkey(i2), stringValue + j2);
    }

    @Override // com.miui.powerkeeper.logsystem.log.LogInterface
    public boolean clearSystemLog(int i2, long j2) {
        File file = new File(getFilePath(i2, j2));
        if (!file.exists() || !file.isFile()) {
            return true;
        }
        if (!file.delete()) {
            return false;
        }
        Log.d(TAG, "Delete log file: " + file.toString());
        return true;
    }

    @Override // com.miui.powerkeeper.logsystem.log.LogInterface
    public void dumpSystemLog(PrintWriter printWriter, int i2, String str) {
        String filePath = getFilePath(i2, LSUtils.getLongValue(str));
        if (new File(filePath).exists()) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "    ");
            indentingPrintWriter.println("++++ " + getInterfaceName() + ":");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println(readFile(filePath));
        }
    }

    protected String[] getArgs() {
        return new String[0];
    }

    @Override // com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return -1;
    }

    @Override // com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return null;
    }

    @Override // com.miui.powerkeeper.logsystem.log.LogInterface
    public int getLogCatchType() {
        return 0;
    }

    protected IBinder getService() {
        return null;
    }

    @Override // com.miui.powerkeeper.logsystem.log.LogInterface
    public final boolean produceSystemLog(int i2, long j2) {
        String filePath = getFilePath(i2, j2);
        Log.d(TAG, "Produced log file: " + filePath + ", logtype = " + getInterfaceName());
        if (getLogCatchType() != 1) {
            return realProduceSystemLog(filePath);
        }
        Log.d(TAG, "MemCatch type");
        return realProduceSystemLog(null);
    }

    public String readFile(String str) {
        return FileUtil.readFromFile(str);
    }

    protected boolean realProduceSystemLog(String str) {
        IBinder service = getService();
        if (service == null) {
            return false;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str);
            try {
                service.dump(fileOutputStream.getFD(), getArgs());
                fileOutputStream.close();
                return true;
            } catch (Throwable th) {
                try {
                    fileOutputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (RemoteException | IOException e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
