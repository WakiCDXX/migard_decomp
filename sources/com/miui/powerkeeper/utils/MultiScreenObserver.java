package com.miui.powerkeeper.utils;

import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.app.TaskStackListener;
import android.util.Log;
import miui.app.IFreeformCallback;
import miui.app.MiuiFreeFormManager;

/* JADX INFO: loaded from: classes.dex */
public class MultiScreenObserver {
    private static final String ACTIVITYTASKMANAGER = "android.app.ActivityTaskManager";
    private static final String METHOD_GETSERVICE = "getService";
    private static final String TAG = "MultiScreenObserver";
    private boolean mIsSplitScreen = false;
    private boolean mIsSmallScreen = false;
    private boolean mIsMultiScreen = false;
    private IFreeformCallback mFreeformCallback = new IFreeformCallback.Stub() { // from class: com.miui.powerkeeper.utils.MultiScreenObserver.1
        public void dispatchFreeFormStackModeChanged(int i2, MiuiFreeFormManager.MiuiFreeFormStackInfo miuiFreeFormStackInfo) {
            if (i2 == 0) {
                MultiScreenObserver.this.mIsSmallScreen = true;
                Log.d(MultiScreenObserver.TAG, "FULLSCREEN_TO_FREEFORM:" + miuiFreeFormStackInfo.packageName + ", state:" + miuiFreeFormStackInfo.windowState);
            } else if (i2 == 1) {
                MultiScreenObserver.this.mIsSmallScreen = true;
                Log.d(MultiScreenObserver.TAG, "FULLSCREEN_TO_MINIFREEFORM:" + miuiFreeFormStackInfo.packageName + ", state:" + miuiFreeFormStackInfo.windowState);
            } else if (i2 == 2) {
                MultiScreenObserver.this.mIsSmallScreen = true;
                Log.d(MultiScreenObserver.TAG, "FREEFORM_TO_MINIFREEFORM:" + miuiFreeFormStackInfo.packageName + ", state:" + miuiFreeFormStackInfo.windowState);
            } else if (i2 == 3) {
                MultiScreenObserver.this.mIsSmallScreen = false;
                Log.d(MultiScreenObserver.TAG, "FREEFORM_TO_FULLSCREEN:" + miuiFreeFormStackInfo.packageName + ", state:" + miuiFreeFormStackInfo.windowState);
            } else if (i2 == 4) {
                MultiScreenObserver.this.mIsSmallScreen = true;
                Log.d(MultiScreenObserver.TAG, "MINIFREEFORM_TO_FREEFORM:" + miuiFreeFormStackInfo.packageName + ", state:" + miuiFreeFormStackInfo.windowState);
            } else if (i2 != 5) {
                Log.w(MultiScreenObserver.TAG, "warning for access here");
            } else {
                MultiScreenObserver.this.mIsSmallScreen = false;
                Log.d(MultiScreenObserver.TAG, "MINIFREEFORM_TO_FULLSCREEN:" + miuiFreeFormStackInfo.packageName + ", state:" + miuiFreeFormStackInfo.windowState);
            }
            if (MultiScreenObserver.this.mIsSmallScreen || MultiScreenObserver.this.mIsSplitScreen) {
                MultiScreenObserver.this.onMultiScreenChanged(true);
            } else {
                MultiScreenObserver.this.onMultiScreenChanged(false);
            }
        }
    };
    private TaskStackListener mTaskStackListener = new TaskStackListener() { // from class: com.miui.powerkeeper.utils.MultiScreenObserver.2
        public void onTaskStackChanged() {
            MultiScreenObserver multiScreenObserver = MultiScreenObserver.this;
            multiScreenObserver.mIsSplitScreen = multiScreenObserver.isInSplitScreenMode();
            if (MultiScreenObserver.this.mIsSplitScreen || MultiScreenObserver.this.mIsSmallScreen) {
                MultiScreenObserver.this.onMultiScreenChanged(true);
            } else {
                MultiScreenObserver.this.onMultiScreenChanged(false);
            }
            Log.i(MultiScreenObserver.TAG, "onTaskStackChanged, splitScreenMode: " + MultiScreenObserver.this.mIsSplitScreen + " isSmallScreen: " + MultiScreenObserver.this.mIsSmallScreen);
        }
    };

    private IActivityTaskManager getActivityTaskManager() {
        return ActivityTaskManager.getService();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInSplitScreenMode() {
        try {
            Class[] clsArr = new Class[0];
            Object objInvoke = Class.forName(ACTIVITYTASKMANAGER).getMethod(METHOD_GETSERVICE, null).invoke(null, null);
            Class[] clsArr2 = new Class[0];
            return ((Boolean) objInvoke.getClass().getMethod("isInSplitScreenWindowingMode", null).invoke(objInvoke, null)).booleanValue();
        } catch (Exception e2) {
            Log.e(TAG, "getStackInfo exception : " + e2);
            return false;
        }
    }

    protected void onMultiScreenChanged(boolean z2) {
        if (this.mIsMultiScreen != z2) {
            Log.i(TAG, "onMultiScreenChanged, splitScreenMode: " + this.mIsSplitScreen + " isSmallScreen: " + this.mIsSmallScreen);
            this.mIsMultiScreen = z2;
        }
    }

    public void registerFreeformCallback() {
        MiuiFreeFormManager.registerFreeformCallback(this.mFreeformCallback);
    }

    public void registerTaskStackListener() {
        try {
            getActivityTaskManager().registerTaskStackListener(this.mTaskStackListener);
        } catch (Exception unused) {
            Log.w(TAG, "Faild to call registerTaskStackListener");
        }
    }

    public void unregisterFreeformCallback() {
        MiuiFreeFormManager.unregisterFreeformCallback(this.mFreeformCallback);
    }
}
