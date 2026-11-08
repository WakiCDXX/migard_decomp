package com.miui.powerkeeper.feedbackcontrol;

import android.util.Log;
import com.google.gson.Gson;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.smack.packet.Message;
import java.io.File;
import java.util.HashMap;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class ThermalLogUploader {
    private static final String BIZ_NAME = "themal";
    private static final String TAG = "ThermalLogUploader";
    private static final String THERMAL_UPLOAD_URL = "https://port.sec.miui.com/mqsas/fileUploadWithUrlId";
    public static final String ZIP_THERMAL_EXCEPTION_LOG_DIR = "/data/vendor/thermal/log";
    private static boolean sIsTestMode = ThermalInfoUtils.isTestMode();

    static class UploadResult {
        public String status;
        public int urlid;

        private UploadResult() {
        }
    }

    private ThermalLogUploader() {
    }

    private static void uploadFile(String str, File file) {
        if (sIsTestMode) {
            Log.i(TAG, "uploadFile");
        }
        HashMap map = new HashMap();
        map.put(PushConstants.UPLOAD_FILE_POST_KEY, file.getAbsolutePath());
        try {
            if ("true".equals(((UploadResult) new Gson().fromJson(HttpUtils.httpPostFiles(str, null, map), UploadResult.class)).status)) {
                boolean zDelete = file.delete();
                if (sIsTestMode) {
                    Log.i(TAG, "delete isSuccess:" + zDelete);
                    Log.i(TAG, String.format("Upload %s done ,delete %s", file.getName(), zDelete ? "successfully" : Message.MSG_TYPE_ERROR));
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            if (sIsTestMode) {
                Log.i(TAG, "UploadFile Failed" + e2.getMessage());
            }
        }
    }

    public static void uploadFiles() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        try {
            File file = new File("/data/vendor/thermal/log");
            if (file.exists()) {
                for (File file2 : file.listFiles()) {
                    uploadFile("https://port.sec.miui.com/mqsas/fileUploadWithUrlId?bizName=themal", file2);
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
