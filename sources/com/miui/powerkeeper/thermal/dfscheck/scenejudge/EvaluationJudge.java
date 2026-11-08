package com.miui.powerkeeper.thermal.dfscheck.scenejudge;

import android.os.Build;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class EvaluationJudge {
    private static final long DURING_EVALUATION_SCENARIO = 300000;
    private static final long EVALUATION_SCENARIO_DURATION_TEMP_THRESHOLD = 40000;
    private static final long EVALUATION_SCENARIO_DURATION_THRESHOLD = 60000;
    private static final String TAG = "powerkeeper.dfsevalujudge";
    private long mEvaluationDuration;
    private long mLastEvaluationTimeStamp;
    private String mPreScenarioName = "";
    private static final boolean DEBUG = Build.IS_USERDEBUG;
    private static EvaluationJudge mEvaluationJudgeInstance = null;

    public static synchronized EvaluationJudge getEvaluationJudgeInstance() {
        try {
            if (mEvaluationJudgeInstance == null) {
                mEvaluationJudgeInstance = new EvaluationJudge();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mEvaluationJudgeInstance;
    }

    public boolean isInNolimitInflunce() {
        if (System.currentTimeMillis() - this.mLastEvaluationTimeStamp < 300000) {
            Log.d(TAG, " is in nolimit scene influnce");
            return true;
        }
        if (!DEBUG) {
            return false;
        }
        Log.d(TAG, "is not in nolimit influnce during time is less thershold");
        return false;
    }

    public void setIsNolimit(long j2, int i2) {
        long jCurrentTimeMillis = System.currentTimeMillis() - j2;
        this.mEvaluationDuration = jCurrentTimeMillis;
        if (jCurrentTimeMillis > EVALUATION_SCENARIO_DURATION_THRESHOLD && i2 > EVALUATION_SCENARIO_DURATION_TEMP_THRESHOLD) {
            this.mLastEvaluationTimeStamp = System.currentTimeMillis();
            Log.d(TAG, "is in evaluation the time is " + this.mLastEvaluationTimeStamp + " duration is " + this.mEvaluationDuration + " temp is " + i2);
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "not in evaluation the time is " + this.mLastEvaluationTimeStamp + " duration is " + this.mEvaluationDuration + " temp is " + i2);
        }
    }
}
