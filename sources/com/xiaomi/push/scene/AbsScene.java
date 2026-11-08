package com.xiaomi.push.scene;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
abstract class AbsScene {
    private OnTriggerSceneCallback mCallback;
    private int mSceneId;

    public interface OnTriggerSceneCallback {
        void onTrigger(int i2, String str);
    }

    public AbsScene(int i2, OnTriggerSceneCallback onTriggerSceneCallback) {
        this.mSceneId = i2;
        this.mCallback = onTriggerSceneCallback;
    }

    protected Context getContext() {
        return ScenePushManager.getContext();
    }

    protected int getSceneId() {
        return this.mSceneId;
    }

    protected abstract void listen();

    protected void trigger() {
        trigger(null);
    }

    protected abstract void unlisten();

    protected void trigger(String str) {
        OnTriggerSceneCallback onTriggerSceneCallback = this.mCallback;
        if (onTriggerSceneCallback != null) {
            onTriggerSceneCallback.onTrigger(this.mSceneId, str);
        }
    }
}
