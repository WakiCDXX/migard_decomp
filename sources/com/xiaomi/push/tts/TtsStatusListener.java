package com.xiaomi.push.tts;

import android.annotation.SuppressLint;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

/* JADX INFO: loaded from: classes.dex */
@SuppressLint({"NewApi"})
public abstract class TtsStatusListener extends UtteranceProgressListener implements TextToSpeech.OnInitListener {
    abstract void onDie();

    abstract void onFinish(TtsSpeechBean ttsSpeechBean);

    abstract void onSpeak(TtsSpeechBean ttsSpeechBean);
}
