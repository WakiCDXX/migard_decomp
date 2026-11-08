package com.miui.powerkeeper.utils;

import android.annotation.NonNull;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.HandlerThread;
import android.util.Log;
import android.view.InputMonitor;

/* JADX INFO: loaded from: classes.dex */
public class TouchEventMonitor {
    private static final String TAG = "TouchEventMonitor";
    private static TouchEventMonitor sTouchEventMonitor;
    private final int mDisplayId;
    private InputMonitor mGestureInputMonitor;
    private final HandlerThread mHandlerThread;
    private TouchEventDispatcher mTouchEventDispatcher;

    private TouchEventMonitor(Context context) {
        HandlerThread handlerThread = new HandlerThread("touchevent-pk");
        this.mHandlerThread = handlerThread;
        this.mDisplayId = context.getDisplayId();
        handlerThread.start();
    }

    public static synchronized TouchEventMonitor getInstance(Context context) {
        try {
            if (sTouchEventMonitor == null) {
                sTouchEventMonitor = new TouchEventMonitor(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sTouchEventMonitor;
    }

    private void registerGestureMonitor() {
        this.mGestureInputMonitor = InputManager.getInstance().monitorGestureInput("touchevent-pk", this.mDisplayId);
        this.mTouchEventDispatcher = new TouchEventDispatcher(this.mGestureInputMonitor.getInputChannel(), this.mHandlerThread.getLooper());
    }

    private void unregisterGestureMonitor() {
        InputMonitor inputMonitor = this.mGestureInputMonitor;
        if (inputMonitor != null) {
            inputMonitor.dispose();
        }
        this.mGestureInputMonitor = null;
        this.mTouchEventDispatcher = null;
    }

    public void registerPointerEventListener(@NonNull TouchEventListener touchEventListener) {
        Log.d(TAG, "registerPointerEventListener");
        TouchEventDispatcher touchEventDispatcher = this.mTouchEventDispatcher;
        if (touchEventDispatcher == null || touchEventDispatcher.getGestureListenerCount() == 0) {
            registerGestureMonitor();
        }
        this.mTouchEventDispatcher.registerInputEventListener(touchEventListener);
    }

    public void unregisterPointerEventListener(@NonNull TouchEventListener touchEventListener) {
        Log.d(TAG, "unregisterPointerEventListener");
        TouchEventDispatcher touchEventDispatcher = this.mTouchEventDispatcher;
        if (touchEventDispatcher == null) {
            return;
        }
        touchEventDispatcher.unregisterInputEventListener(touchEventListener);
        if (this.mTouchEventDispatcher.getGestureListenerCount() == 0) {
            unregisterGestureMonitor();
        }
    }
}
