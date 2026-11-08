package com.xiaomi.push.scene;

import android.app.ActivityManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.push.extension.AppRunningUtils;
import com.xiaomi.push.extension.QueuedWork;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.xmpush.thrift.ConfigKey;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class ForegroundScene extends AbsScene {
    private static final long DURATION_THRESHOLD = 1200000;
    private Handler mHandler;
    private volatile boolean mListened;

    public ForegroundScene(int i2, AbsScene.OnTriggerSceneCallback onTriggerSceneCallback) {
        super(i2, onTriggerSceneCallback);
        this.mListened = false;
        this.mHandler = QueuedWork.newHandler(new Handler.Callback() { // from class: com.xiaomi.push.scene.ForegroundScene.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                String processNameByPid = AppRunningUtils.getProcessNameByPid(ForegroundScene.this.getContext(), message.arg1);
                if (SceneLog.DEBUG.booleanValue()) {
                    SceneLog.i("ForegroundScene handleMessage:" + message.what + " pkg:" + processNameByPid);
                }
                if (TextUtils.isEmpty(processNameByPid)) {
                    return true;
                }
                ForegroundScene.this.trigger(processNameByPid);
                return true;
            }
        });
    }

    private long getForegroundDuration() {
        return OnlineConfig.getInstance(getContext()).getLongValue(ConfigKey.ScenePushForegroundDurationLong.getValue(), DURATION_THRESHOLD);
    }

    private void sendOrRemoveMessage(int i2, int i3, boolean z2) {
        int iHashCode = (i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + i3).hashCode();
        if (!z2) {
            if (SceneLog.DEBUG.booleanValue()) {
                SceneLog.i("ForegroundScene:removeMessage:" + iHashCode);
            }
            this.mHandler.removeMessages(iHashCode);
            return;
        }
        Message messageObtain = Message.obtain();
        messageObtain.what = iHashCode;
        messageObtain.arg1 = i2;
        messageObtain.arg2 = i3;
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("ForegroundScene:sendMessage:" + iHashCode);
        }
        this.mHandler.sendMessageDelayed(messageObtain, getForegroundDuration());
    }

    public void judgeCondition(int i2, int i3, boolean z2) {
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("ForegroundScene:pid-uid:" + i2 + Constants.ACCEPT_TIME_SEPARATOR_SERVER + i3 + " # pkg:" + AppRunningUtils.getProcessNameByPid(getContext(), i2) + " # foreground-listened:" + z2 + " " + this.mListened + " foregroundDuration:" + getForegroundDuration());
        }
        if (this.mListened) {
            sendOrRemoveMessage(i2, i3, z2);
        }
    }

    @Override // com.xiaomi.push.scene.AbsScene
    protected void listen() {
        if (this.mListened) {
            return;
        }
        this.mListened = true;
        List<ActivityManager.RunningAppProcessInfo> foregroundProcess = AppRunningUtils.getForegroundProcess(getContext());
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("ForegroundScene first listen infos:" + foregroundProcess);
        }
        if (ToolUtils.isEmptyList(foregroundProcess)) {
            return;
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : foregroundProcess) {
            if (SceneLog.DEBUG.booleanValue()) {
                SceneLog.i("ForegroundScene first listen pid-uid:" + runningAppProcessInfo.pid + Constants.ACCEPT_TIME_SEPARATOR_SERVER + runningAppProcessInfo.uid + " # pkg:" + runningAppProcessInfo.processName);
            }
            sendOrRemoveMessage(runningAppProcessInfo.pid, runningAppProcessInfo.uid, true);
        }
    }

    @Override // com.xiaomi.push.scene.AbsScene
    protected void unlisten() {
        this.mListened = false;
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("ForegroundScene:removeCallbacksAndMessages");
        }
        this.mHandler.removeCallbacksAndMessages(null);
    }
}
