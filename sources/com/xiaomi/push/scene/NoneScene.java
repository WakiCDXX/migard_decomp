package com.xiaomi.push.scene;

/* JADX INFO: loaded from: classes.dex */
class NoneScene extends AbsScene {
    public NoneScene(int i2, AbsScene.OnTriggerSceneCallback onTriggerSceneCallback) {
        super(i2, onTriggerSceneCallback);
    }

    @Override // com.xiaomi.push.scene.AbsScene
    protected void listen() {
        trigger();
    }

    @Override // com.xiaomi.push.scene.AbsScene
    protected void unlisten() {
    }
}
