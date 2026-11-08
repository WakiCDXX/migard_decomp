package com.xiaomi.push.tts;

import android.os.Bundle;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class TtsSpeechBean {
    Bundle bundle;
    byte[] decryptedContent;
    String key;
    String pkgName;
    Map<String, String> serverExtra;
    TtsTask timeoutTask;
    String content = "";
    String engine = "";
    String vol = "";
    boolean independent = false;
    boolean wakeLock = false;
    boolean isSpeaking = false;
    boolean isBusinessMessage = false;
    Report report = new Report();

    static class Report {
        String msgId = "";
        String appId = "";
        long ttsInitSuccessTime = -1;
        long ttsSpeakWayTime = -1;
        long ttsStartPlayTime = -1;
        long sendArrivedBroadcastTime = -1;
        long ttsAckTime = -1;
        long ttsFinTime = -1;
        boolean ttsPlayResult = false;
        boolean disableReport = false;
        int volume = -1;
        int scene = -1;
        int queueNum = 0;

        Report() {
        }
    }

    public void cancelTask() {
        TtsTask ttsTask = this.timeoutTask;
        if (ttsTask != null) {
            ttsTask.cancel();
            this.timeoutTask = null;
        }
    }

    public void deepCopyFrom(TtsSpeechBean ttsSpeechBean) {
        this.key = ttsSpeechBean.key;
        this.vol = ttsSpeechBean.vol;
        this.engine = ttsSpeechBean.engine;
        this.content = ttsSpeechBean.content;
        this.independent = ttsSpeechBean.independent;
        this.wakeLock = ttsSpeechBean.wakeLock;
        Report report = this.report;
        Report report2 = ttsSpeechBean.report;
        report.msgId = report2.msgId;
        report.appId = report2.appId;
        this.serverExtra = ttsSpeechBean.serverExtra;
        report.disableReport = report2.disableReport;
        report.ttsAckTime = report2.ttsAckTime;
        this.pkgName = ttsSpeechBean.pkgName;
        this.decryptedContent = ttsSpeechBean.decryptedContent;
        this.isBusinessMessage = ttsSpeechBean.isBusinessMessage;
        this.bundle = MIUIUtils.deepCopyForBundle(ttsSpeechBean.bundle);
    }

    public void updateScene(int i2) {
        Report report = this.report;
        report.scene = i2;
        report.ttsPlayResult = i2 == 88700;
    }
}
