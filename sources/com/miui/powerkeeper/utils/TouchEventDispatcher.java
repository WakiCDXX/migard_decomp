package com.miui.powerkeeper.utils;

import android.os.Looper;
import android.util.Log;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.MotionEvent;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class TouchEventDispatcher extends InputEventReceiver {
    private static final String TAG = "TouchEventDispatcher";
    private final InputChannel mInputChannel;
    private final ArrayList<TouchEventListener> mListeners;
    private TouchEventListener[] mListenersArray;

    public TouchEventDispatcher(InputChannel inputChannel, Looper looper) {
        super(inputChannel, looper);
        this.mListeners = new ArrayList<>();
        this.mListenersArray = new TouchEventListener[0];
        this.mInputChannel = inputChannel;
    }

    public void dispose() {
        super.dispose();
        this.mInputChannel.dispose();
        synchronized (this) {
            this.mListeners.clear();
            this.mListenersArray = null;
        }
    }

    public int getGestureListenerCount() {
        int size;
        synchronized (this) {
            size = this.mListeners.size();
        }
        return size;
    }

    public void onInputEvent(InputEvent inputEvent) {
        if ((inputEvent instanceof MotionEvent) && (inputEvent.getSource() & 2) != 0) {
            MotionEvent motionEvent = (MotionEvent) inputEvent;
            synchronized (this) {
                try {
                    if (this.mListenersArray == null) {
                        ArrayList<TouchEventListener> arrayList = this.mListeners;
                        if (arrayList == null) {
                            return;
                        }
                        TouchEventListener[] touchEventListenerArr = new TouchEventListener[arrayList.size()];
                        this.mListenersArray = touchEventListenerArr;
                        this.mListeners.toArray(touchEventListenerArr);
                    }
                    for (TouchEventListener touchEventListener : this.mListenersArray) {
                        touchEventListener.onPointerEvent(motionEvent);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        finishInputEvent(inputEvent, false);
    }

    public void registerInputEventListener(TouchEventListener touchEventListener) {
        synchronized (this) {
            try {
                if (!this.mListeners.contains(touchEventListener)) {
                    this.mListeners.add(touchEventListener);
                    this.mListenersArray = null;
                    return;
                }
                Log.e(TAG, "registerInputEventListener: trying to register" + touchEventListener + " twice.");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterInputEventListener(TouchEventListener touchEventListener) {
        synchronized (this) {
            try {
                if (this.mListeners.contains(touchEventListener)) {
                    this.mListeners.remove(touchEventListener);
                    this.mListenersArray = null;
                    return;
                }
                Log.e(TAG, "unregisterInputEventListener: " + touchEventListener + " not registered.");
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
