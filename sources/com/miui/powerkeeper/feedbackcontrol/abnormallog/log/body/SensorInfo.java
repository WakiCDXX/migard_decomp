package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class SensorInfo extends LogCollector {
    private final String TABLE;
    private final String TAG;
    private final String THERMAL_PATH;
    private final String THERMAL_ZONE;
    private int mNtcNum;
    private final List<String> mTypeList;

    public SensorInfo(Context context) {
        super(context);
        this.TAG = "SensorInfoCollector";
        this.TABLE = "Ntc Info:";
        this.THERMAL_PATH = "sys/class/thermal/";
        this.THERMAL_ZONE = "thermal_zone";
        this.mNtcNum = -1;
        this.mTypeList = new ArrayList();
        this.mLog.d("SensorInfoCollector", "create");
        new Thread(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.SensorInfo.1
            @Override // java.lang.Runnable
            public void run() {
                SensorInfo sensorInfo = SensorInfo.this;
                sensorInfo.mNtcNum = (int) sensorInfo.getThermalZoneNum();
                SensorInfo.this.mLog.d("SensorInfoCollector", "ntc num: " + SensorInfo.this.mNtcNum);
                for (int i2 = 0; i2 < SensorInfo.this.mNtcNum; i2++) {
                    File file = new File("sys/class/thermal/thermal_zone" + i2 + "/type");
                    if (!file.exists()) {
                        SensorInfo.this.mLog.e("SensorInfoCollector", "ntc type error");
                    }
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        try {
                            SensorInfo.this.mTypeList.add(bufferedReader.readLine());
                            bufferedReader.close();
                        } catch (Throwable th) {
                            try {
                                bufferedReader.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                    } catch (Exception e2) {
                        SensorInfo.this.mLog.e("SensorInfoCollector", "error at num: " + i2);
                        e2.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getThermalZoneNum() {
        File[] fileArrListFiles = new File("sys/class/thermal/").listFiles();
        if (fileArrListFiles == null || fileArrListFiles.length == 0) {
            return 0L;
        }
        final String str = ".*thermal_zone.*";
        return Arrays.stream(fileArrListFiles).filter(new Predicate() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.e
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return SensorInfo.lambda$getThermalZoneNum$0(str, (File) obj);
            }
        }).count();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getThermalZoneNum$0(String str, File file) {
        return Pattern.matches(str, file.getName());
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() {
        if (this.mPublicSpace.isContinueCollect()) {
            this.mLog.d("SensorInfoCollector", "collect");
            StringBuilder sb = new StringBuilder();
            sb.append("Ntc Info:[");
            for (int i2 = 0; i2 < this.mNtcNum; i2++) {
                File file = new File("sys/class/thermal/thermal_zone" + i2 + "/temp");
                if (!file.exists()) {
                    this.mLog.e("SensorInfoCollector", "ntc temp error");
                }
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    try {
                        String line = bufferedReader.readLine();
                        sb.append(this.mTypeList.get(i2));
                        sb.append(":");
                        sb.append(line);
                        sb.append(" ");
                        bufferedReader.close();
                    } catch (Throwable th) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                } catch (Exception e2) {
                    this.mLog.d("SensorInfoCollector", "error at num: " + i2);
                    e2.printStackTrace();
                }
            }
            sb.append("]");
            this.mCache.append(sb.toString());
        }
    }

    public int getNtcNum() {
        return this.mNtcNum;
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("SensorInfoCollector", "logout");
    }
}
