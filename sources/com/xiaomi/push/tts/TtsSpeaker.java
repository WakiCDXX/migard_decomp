package com.xiaomi.push.tts;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.xiaomi.channel.commonutils.android.SafeRunnable;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.push.extension.LogPools;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.ChannelPushReportAgent;
import com.xiaomi.push.service.MIPushEventProcessor;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(15)
public class TtsSpeaker extends TtsStatusListener {
    private Object mAudioFocus;
    private AudioManager mAudioManager;
    private Context mContext;
    private TtsSpeechBean mCurBean;
    private MyTextToSpeech mTextToSpeech;
    private Handler mTtsMessageHandler;
    private boolean mAdjustVol = false;
    private int mMusicMaxVol = -1;
    private PowerManager.WakeLock mWakeLock = null;
    private int mUserVolume = -1;
    private int mSetMusicVolume = -1;
    private ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    private final ConcurrentHashMap<String, TtsSpeechBean> mSpeakMap = new ConcurrentHashMap<>();
    private final Object mLock = new Object();
    private Timer mTimer = new Timer();

    /* JADX INFO: renamed from: com.xiaomi.push.tts.TtsSpeaker$10, reason: invalid class name */
    class AnonymousClass10 implements AudioManager.OnAudioFocusChangeListener {
        final /* synthetic */ String val$key;

        AnonymousClass10(String str) {
            this.val$key = str;
        }

        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(int i2) {
            if (i2 != 1) {
                TtsSpeaker.this.mAudioManager.abandonAudioFocus((AudioManager.OnAudioFocusChangeListener) TtsSpeaker.this.mAudioFocus);
                TtsSpeaker.this.onDone(this.val$key);
            }
        }
    }

    static class MyTextToSpeech extends TextToSpeech implements IBinder.DeathRecipient {
        private static final int STATUS_FAILED = -1;
        private static final int STATUS_SUCCESS = 0;
        private volatile int mAvailable;
        private String mEngineName;
        private IBinder mIBinder;
        private TtsStatusListener mListener;

        public MyTextToSpeech(Context context, TtsStatusListener ttsStatusListener, String str) {
            super(context, ttsStatusListener, str);
            this.mAvailable = -1;
            this.mIBinder = null;
            this.mEngineName = str;
            this.mListener = ttsStatusListener;
            setOnUtteranceProgressListener(ttsStatusListener);
        }

        boolean available() {
            return this.mAvailable == 0;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            LogPools.TtsLog.d("binder died");
            this.mIBinder.unlinkToDeath(this, 0);
            this.mAvailable = -1;
            this.mListener.onDie();
        }

        void connect() {
            Object field = JavaCalls.getField(this, "mServiceConnection");
            if (field == null) {
                LogPools.TtsLog.d("connection is null");
                return;
            }
            Object field2 = JavaCalls.getField(field, "mService");
            if (field2 == null) {
                LogPools.TtsLog.d("service is null");
                return;
            }
            try {
                IBinder iBinder = (IBinder) JavaCalls.callMethod(field2, "asBinder", new Object[0]);
                this.mIBinder = iBinder;
                if (iBinder != null) {
                    iBinder.linkToDeath(this, 0);
                }
            } catch (Exception e2) {
                LogPools.TtsLog.w("get binder reference error : " + e2);
            }
        }

        void destroy() {
            LogPools.TtsLog.d("tts release");
            setOnUtteranceProgressListener(null);
            this.mListener = null;
            this.mEngineName = null;
            stop();
            shutdown();
            this.mAvailable = -1;
            this.mIBinder = null;
        }

        String getCurEngine() {
            String str = (String) JavaCalls.callMethod(this, "getCurrentEngine", new Object[0]);
            return str == null ? "" : str;
        }

        boolean onInit(int i2) {
            if (i2 == 0) {
                int language = setLanguage(Locale.CHINA);
                if (language == -1 || language == -2 || !getCurEngine().equalsIgnoreCase(this.mEngineName)) {
                    LogPools.TtsLog.d("current engine is : " + getCurEngine());
                } else {
                    this.mAvailable = 0;
                    connect();
                }
            }
            return this.mAvailable == 0;
        }
    }

    public TtsSpeaker(Context context) {
        this.mContext = context;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        HandlerThread handlerThread = new HandlerThread("tts_msg_queue");
        handlerThread.start();
        this.mTtsMessageHandler = new Handler(handlerThread.getLooper()) { // from class: com.xiaomi.push.tts.TtsSpeaker.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                Object obj;
                LogPools.LogModule logModule = LogPools.TtsLog;
                logModule.d("--- process a SpeechItem");
                if (message == null || (obj = message.obj) == null) {
                    return;
                }
                String strValueOf = String.valueOf(obj);
                if (TextUtils.isEmpty(strValueOf)) {
                    return;
                }
                TtsSpeaker ttsSpeaker = TtsSpeaker.this;
                ttsSpeaker.mCurBean = (TtsSpeechBean) ttsSpeaker.mSpeakMap.get(strValueOf);
                if (TtsSpeaker.this.mCurBean == null) {
                    return;
                }
                logModule.d("SpeechItem: " + TtsSpeaker.this.mCurBean.report.msgId);
                if (TtsSpeaker.this.isSpeechReady()) {
                    TtsSpeaker ttsSpeaker2 = TtsSpeaker.this;
                    ttsSpeaker2.onSpeak(ttsSpeaker2.mCurBean);
                } else {
                    TtsSpeaker ttsSpeaker3 = TtsSpeaker.this;
                    ttsSpeaker3.initEngine(ttsSpeaker3.mCurBean.engine);
                }
                synchronized (TtsSpeaker.this.mLock) {
                    try {
                        TtsSpeaker.this.mLock.wait();
                    } catch (Exception e2) {
                        LogPools.TtsLog.e("block the next SpeechItem failed : " + e2);
                    }
                }
                LogPools.TtsLog.d("--- finish a SpeechItem");
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SpeakNow(TtsSpeechBean ttsSpeechBean) {
        int iSpeak;
        int errorSceneCode = TtsUtils.getErrorSceneCode(this.mContext, this.mAudioManager);
        ttsSpeechBean.updateScene(errorSceneCode);
        LogPools.LogModule logModule = LogPools.TtsLog;
        logModule.w("scene code : " + errorSceneCode);
        if (errorSceneCode != 88700) {
            ttsSpeechBean.updateScene(errorSceneCode);
            onError(ttsSpeechBean.key);
            return;
        }
        this.mTimer.schedule(ttsSpeechBean.timeoutTask, PluginConstants.TTS_REVERT_VOLUME_TIME);
        if (this.mAdjustVol) {
            this.mAdjustVol = false;
            if (this.mMusicMaxVol == -1) {
                this.mMusicMaxVol = this.mAudioManager.getStreamMaxVolume(3);
            }
            int i2 = ToolUtils.parseInt(ttsSpeechBean.vol, 0);
            if (adjustVolume(ttsSpeechBean.vol)) {
                ttsSpeechBean.report.volume = i2;
            } else {
                ttsSpeechBean.report.volume = TtsUtils.percent(this.mAudioManager.getStreamVolume(3), this.mMusicMaxVol);
                logModule.d("tts current vol percent is : " + ttsSpeechBean.report.volume);
            }
        }
        if (!requestAudioFocus(ttsSpeechBean.key)) {
            logModule.w("request audio focus failed");
            ttsSpeechBean.updateScene(PluginConstants.TTS_ERROR_CODE_REQUEST_FOCUS_FAILED);
            onError(ttsSpeechBean.key);
            return;
        }
        ttsSpeechBean.report.ttsSpeakWayTime = System.currentTimeMillis();
        int i3 = 0;
        while (true) {
            iSpeak = speak(ttsSpeechBean.content, 0, ttsSpeechBean.bundle, ttsSpeechBean.key);
            if (iSpeak == 0 || i3 >= 3) {
                break;
            }
            i3++;
            try {
                Thread.sleep(i3 * 300);
            } catch (Exception unused) {
            }
        }
        if (iSpeak == 0) {
            ttsSpeechBean.isSpeaking = true;
        } else {
            ttsSpeechBean.updateScene(PluginConstants.TTS_ERROR_CODE_CALLBACK_ON_ERROR);
            onError(ttsSpeechBean.key);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void abandonAudioFocusRequest() {
        Object obj = this.mAudioFocus;
        if (obj != null) {
            this.mAudioManager.abandonAudioFocusRequest((AudioFocusRequest) obj);
            this.mAudioFocus = null;
        }
    }

    private boolean adjustVolume(String str) {
        if (this.mAudioManager.isWiredHeadsetOn()) {
            LogPools.TtsLog.w("headset not support adjust vol");
            return false;
        }
        if (this.mAudioManager.isBluetoothA2dpOn()) {
            LogPools.TtsLog.w("bluetooth not support adjust vol");
            return false;
        }
        int iVolIndex = TtsUtils.volIndex(str, this.mMusicMaxVol);
        int streamVolume = this.mAudioManager.getStreamVolume(3);
        if (streamVolume >= iVolIndex) {
            LogPools.TtsLog.w("vol satisfy need");
            return false;
        }
        this.mSetMusicVolume = iVolIndex;
        this.mUserVolume = streamVolume;
        this.mAudioManager.setStreamVolume(3, iVolIndex, 0);
        LogPools.TtsLog.w("adjust the volume to : " + this.mSetMusicVolume);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasNext() {
        if (this.mTextToSpeech == null) {
            LogPools.TtsLog.w("queue size : " + this.mSpeakMap.size());
            return false;
        }
        LogPools.TtsLog.d("tts is speaking : " + this.mTextToSpeech.isSpeaking() + ", current size : " + this.mSpeakMap.size());
        return this.mTextToSpeech != null && this.mSpeakMap.size() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initEngine(String str) {
        LogPools.TtsLog.d("init engine");
        this.mTextToSpeech = new MyTextToSpeech(this.mContext, this, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSpeechReady() {
        MyTextToSpeech myTextToSpeech = this.mTextToSpeech;
        return myTextToSpeech != null && myTextToSpeech.getCurEngine().equalsIgnoreCase(this.mCurBean.engine) && this.mTextToSpeech.available();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseResources() {
        LogPools.TtsLog.d("release resources");
        this.mTextToSpeech.destroy();
        this.mTextToSpeech = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportTtsData(TtsSpeechBean ttsSpeechBean) {
        if (ttsSpeechBean.report.disableReport) {
            LogPools.TtsLog.d("disable report tts data flag is true");
            return;
        }
        LogPools.TtsLog.d("report tts data : " + ttsSpeechBean.report.msgId);
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        xmPushActionNotification.setType(NotificationType.TtsReporter.value);
        xmPushActionNotification.setId(ttsSpeechBean.report.msgId);
        xmPushActionNotification.setAppId(ttsSpeechBean.report.appId);
        xmPushActionNotification.setRequireAck(false);
        HashMap map = new HashMap();
        xmPushActionNotification.extra = map;
        map.put("mipush_custom_extra", ttsSpeechBean.serverExtra.get("mipush_custom_extra"));
        xmPushActionNotification.extra.put(PluginConstants.TTS_INIT_SUC_TIME, String.valueOf(ttsSpeechBean.report.ttsInitSuccessTime));
        xmPushActionNotification.extra.put(PluginConstants.TTS_SPEAK_TIME, String.valueOf(ttsSpeechBean.report.ttsSpeakWayTime));
        xmPushActionNotification.extra.put(PluginConstants.TTS_START_TIME, String.valueOf(ttsSpeechBean.report.ttsStartPlayTime));
        xmPushActionNotification.extra.put(PluginConstants.TTS_FINISH_TIME, String.valueOf(ttsSpeechBean.report.ttsFinTime));
        xmPushActionNotification.extra.put(PluginConstants.TTS_PLAY_RESULT, String.valueOf(ttsSpeechBean.report.ttsPlayResult));
        xmPushActionNotification.extra.put(PluginConstants.TTS_ACK_TIME, String.valueOf(ttsSpeechBean.report.ttsAckTime));
        xmPushActionNotification.extra.put(PluginConstants.TTS_QUEUE_NUMBER, String.valueOf(ttsSpeechBean.report.queueNum));
        xmPushActionNotification.extra.put(PluginConstants.TTS_VOLUME, String.valueOf(ttsSpeechBean.report.volume));
        xmPushActionNotification.extra.put(PluginConstants.TTS_ERROR_CODE, String.valueOf(ttsSpeechBean.report.scene));
        xmPushActionNotification.extra.put(PluginConstants.TTS_SEND_ARRIVED_BROADCAST_TIME, String.valueOf(ttsSpeechBean.report.sendArrivedBroadcastTime));
        ChannelPushReportAgent.report(xmPushActionNotification);
    }

    private boolean requestAudioFocus(final String str) {
        AudioFocusRequest audioFocusRequestBuild = new AudioFocusRequest.Builder(4).setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() { // from class: com.xiaomi.push.tts.TtsSpeaker.11
            @Override // android.media.AudioManager.OnAudioFocusChangeListener
            public void onAudioFocusChange(int i2) {
                if (i2 != 1) {
                    TtsSpeaker.this.mAudioManager.abandonAudioFocusRequest((AudioFocusRequest) TtsSpeaker.this.mAudioFocus);
                    TtsSpeaker.this.onDone(str);
                }
            }
        }).build();
        this.mAudioFocus = audioFocusRequestBuild;
        return this.mAudioManager.requestAudioFocus(audioFocusRequestBuild) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void revertVolume() {
        int i2;
        int streamVolume = this.mAudioManager.getStreamVolume(3);
        if (streamVolume != this.mSetMusicVolume || (i2 = this.mUserVolume) == -1) {
            LogPools.TtsLog.d("should not revert, cur vol : " + streamVolume + " not equals " + this.mSetMusicVolume + " user vol : " + this.mUserVolume);
        } else {
            this.mAudioManager.setStreamVolume(3, i2, 0);
            LogPools.TtsLog.d("cur vol : " + streamVolume + ", set vol : " + this.mSetMusicVolume + ", should revert vol to  " + this.mUserVolume);
        }
        this.mSetMusicVolume = -1;
        this.mUserVolume = -1;
    }

    private void sendMessageArrivedBroadcast(Context context, TtsSpeechBean ttsSpeechBean) {
        if (ttsSpeechBean.isBusinessMessage) {
            return;
        }
        sendMessageBroadcast(context, ttsSpeechBean.pkgName, ttsSpeechBean.decryptedContent);
    }

    public static void sendMessageBroadcast(Context context, String str, byte[] bArr) {
        MIPushEventProcessor.broadcastMessageArrived(context, str, bArr);
    }

    private int speak(String str, int i2, Bundle bundle, String str2) {
        return this.mTextToSpeech.speak(str, i2, bundle, str2);
    }

    public void broadcastAndRemove(TtsSpeechBean ttsSpeechBean, int i2) {
        if (ttsSpeechBean == null || TextUtils.isEmpty(ttsSpeechBean.key)) {
            return;
        }
        LogPools.TtsLog.w("broadcast " + ttsSpeechBean.report.msgId + " error code " + i2);
        this.mSpeakMap.remove(ttsSpeechBean.key);
        sendMessageArrivedBroadcast(this.mContext, ttsSpeechBean);
        ttsSpeechBean.updateScene(i2);
        ttsSpeechBean.report.sendArrivedBroadcastTime = System.currentTimeMillis();
        reportTtsData(ttsSpeechBean);
    }

    public void enqueue(TtsSpeechBean ttsSpeechBean) {
        TtsSpeechBean ttsSpeechBean2 = this.mSpeakMap.get(ttsSpeechBean.key);
        if (ttsSpeechBean2 != null) {
            if (ttsSpeechBean2.isSpeaking) {
                LogPools.TtsLog.d("it is not allowed to update, because msg is playing");
                return;
            } else {
                ttsSpeechBean2.deepCopyFrom(ttsSpeechBean);
                return;
            }
        }
        if (this.mSpeakMap.size() == 0) {
            this.mAdjustVol = true;
        }
        final TtsSpeechBean ttsSpeechBean3 = new TtsSpeechBean();
        ttsSpeechBean3.deepCopyFrom(ttsSpeechBean);
        ttsSpeechBean3.report.queueNum = this.mSpeakMap.size();
        ttsSpeechBean3.timeoutTask = new TtsTask() { // from class: com.xiaomi.push.tts.TtsSpeaker.9
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                TtsSpeechBean ttsSpeechBean4 = (TtsSpeechBean) TtsSpeaker.this.mSpeakMap.get(ttsSpeechBean3.key);
                if (ttsSpeechBean4 != null) {
                    ttsSpeechBean4.updateScene(PluginConstants.TTS_ERROR_CODE_MSG_TIME_OUT);
                    TtsSpeaker.this.onFinish(ttsSpeechBean4);
                }
            }
        };
        this.mSpeakMap.put(ttsSpeechBean3.key, ttsSpeechBean3);
        Message messageObtain = Message.obtain();
        messageObtain.obj = ttsSpeechBean3.key;
        this.mTtsMessageHandler.sendMessage(messageObtain);
        LogPools.TtsLog.w("msg enqueue : " + ttsSpeechBean3.report.msgId);
    }

    @Override // com.xiaomi.push.tts.TtsStatusListener
    public void onDie() {
        this.mSingleThreadExecutor.execute(new SafeRunnable() { // from class: com.xiaomi.push.tts.TtsSpeaker.8
            @Override // com.xiaomi.channel.commonutils.android.SafeRunnable
            public void safeRun() {
                LogPools.TtsLog.d("--- --- onDie");
                if (TtsSpeaker.this.mCurBean == null || !TtsSpeaker.this.mCurBean.isSpeaking) {
                    return;
                }
                TtsSpeaker.this.mCurBean.updateScene(PluginConstants.TTS_ERROR_CODE_CALLBACK_ON_ERROR);
                TtsSpeaker ttsSpeaker = TtsSpeaker.this;
                ttsSpeaker.onError(ttsSpeaker.mCurBean.key);
            }
        });
    }

    @Override // android.speech.tts.UtteranceProgressListener
    public void onDone(final String str) {
        this.mSingleThreadExecutor.execute(new SafeRunnable() { // from class: com.xiaomi.push.tts.TtsSpeaker.5
            @Override // com.xiaomi.channel.commonutils.android.SafeRunnable
            public void safeRun() {
                LogPools.TtsLog.d("--- --- onDone");
                TtsSpeechBean ttsSpeechBean = (TtsSpeechBean) TtsSpeaker.this.mSpeakMap.get(str);
                if (ttsSpeechBean != null) {
                    ttsSpeechBean.isSpeaking = false;
                    TtsSpeaker.this.onFinish(ttsSpeechBean);
                }
            }
        });
    }

    @Override // android.speech.tts.UtteranceProgressListener
    public void onError(final String str) {
        this.mSingleThreadExecutor.execute(new SafeRunnable() { // from class: com.xiaomi.push.tts.TtsSpeaker.6
            @Override // com.xiaomi.channel.commonutils.android.SafeRunnable
            public void safeRun() {
                LogPools.TtsLog.d("--- --- onError");
                TtsSpeechBean ttsSpeechBean = (TtsSpeechBean) TtsSpeaker.this.mSpeakMap.get(str);
                if (ttsSpeechBean != null) {
                    ttsSpeechBean.isSpeaking = false;
                    TtsSpeaker.this.onFinish(ttsSpeechBean);
                }
            }
        });
    }

    @Override // com.xiaomi.push.tts.TtsStatusListener
    public void onFinish(final TtsSpeechBean ttsSpeechBean) {
        this.mSingleThreadExecutor.execute(new Runnable() { // from class: com.xiaomi.push.tts.TtsSpeaker.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (TtsSpeaker.this.mSpeakMap.remove(ttsSpeechBean.key) == null) {
                        if (TtsSpeaker.this.mWakeLock != null && TtsSpeaker.this.mWakeLock.isHeld()) {
                            try {
                                LogPools.TtsLog.w("-running");
                                TtsSpeaker.this.mWakeLock.release();
                            } catch (Throwable th) {
                                try {
                                    LogPools.TtsLog.w("release -running failed: " + th);
                                } catch (Throwable th2) {
                                    TtsSpeaker.this.mWakeLock = null;
                                    throw th2;
                                }
                            }
                            TtsSpeaker.this.mWakeLock = null;
                        }
                        synchronized (TtsSpeaker.this.mLock) {
                            try {
                                LogPools.TtsLog.d("reset the block");
                                TtsSpeaker.this.mLock.notify();
                            } catch (Exception unused) {
                            }
                        }
                        return;
                    }
                    LogPools.LogModule logModule = LogPools.TtsLog;
                    logModule.d("--- --- onFinish");
                    ttsSpeechBean.report.ttsFinTime = System.currentTimeMillis();
                    ttsSpeechBean.cancelTask();
                    TtsSpeaker.this.abandonAudioFocusRequest();
                    if (!TtsSpeaker.this.hasNext()) {
                        TtsSpeaker.this.mAdjustVol = false;
                        TtsSpeaker.this.mCurBean = null;
                        TtsSpeaker.this.revertVolume();
                        TtsSpeaker.this.releaseResources();
                    }
                    TtsSpeaker.this.reportTtsData(ttsSpeechBean);
                    if (TtsSpeaker.this.mWakeLock != null && TtsSpeaker.this.mWakeLock.isHeld()) {
                        try {
                            logModule.w("-running");
                            TtsSpeaker.this.mWakeLock.release();
                        } catch (Throwable th3) {
                            try {
                                LogPools.TtsLog.w("release -running failed: " + th3);
                            } catch (Throwable th4) {
                                TtsSpeaker.this.mWakeLock = null;
                                throw th4;
                            }
                        }
                        TtsSpeaker.this.mWakeLock = null;
                    }
                    synchronized (TtsSpeaker.this.mLock) {
                        try {
                            LogPools.TtsLog.d("reset the block");
                            TtsSpeaker.this.mLock.notify();
                        } catch (Exception unused2) {
                        }
                    }
                } catch (Throwable th5) {
                    try {
                        LogPools.LogModule logModule2 = LogPools.TtsLog;
                        logModule2.w("onFinish throw: " + th5);
                        if (TtsSpeaker.this.mWakeLock != null && TtsSpeaker.this.mWakeLock.isHeld()) {
                            try {
                                logModule2.w("-running");
                                TtsSpeaker.this.mWakeLock.release();
                            } catch (Throwable th6) {
                                try {
                                    LogPools.TtsLog.w("release -running failed: " + th6);
                                } catch (Throwable th7) {
                                    TtsSpeaker.this.mWakeLock = null;
                                    throw th7;
                                }
                            }
                            TtsSpeaker.this.mWakeLock = null;
                        }
                        synchronized (TtsSpeaker.this.mLock) {
                            try {
                                LogPools.TtsLog.d("reset the block");
                                TtsSpeaker.this.mLock.notify();
                            } catch (Exception unused3) {
                            }
                        }
                    } catch (Throwable th8) {
                        if (TtsSpeaker.this.mWakeLock != null && TtsSpeaker.this.mWakeLock.isHeld()) {
                            try {
                                LogPools.TtsLog.w("-running");
                                TtsSpeaker.this.mWakeLock.release();
                            } catch (Throwable th9) {
                                try {
                                    LogPools.TtsLog.w("release -running failed: " + th9);
                                } catch (Throwable th10) {
                                    TtsSpeaker.this.mWakeLock = null;
                                    throw th10;
                                }
                            }
                            TtsSpeaker.this.mWakeLock = null;
                        }
                        synchronized (TtsSpeaker.this.mLock) {
                            try {
                                LogPools.TtsLog.d("reset the block");
                                TtsSpeaker.this.mLock.notify();
                            } catch (Exception unused4) {
                            }
                            throw th8;
                        }
                    }
                }
            }
        });
    }

    @Override // android.speech.tts.TextToSpeech.OnInitListener
    public void onInit(final int i2) {
        this.mSingleThreadExecutor.execute(new SafeRunnable() { // from class: com.xiaomi.push.tts.TtsSpeaker.2
            @Override // com.xiaomi.channel.commonutils.android.SafeRunnable
            public void safeRun() {
                LogPools.LogModule logModule = LogPools.TtsLog;
                logModule.d("--- --- onInit : " + i2);
                if (TtsSpeaker.this.mSpeakMap.size() <= 0) {
                    logModule.d("queue is null");
                    return;
                }
                if (TtsSpeaker.this.mCurBean == null) {
                    return;
                }
                if (TtsSpeaker.this.mTextToSpeech == null || !TtsSpeaker.this.mTextToSpeech.onInit(i2)) {
                    logModule.d("init failed");
                    TtsSpeaker ttsSpeaker = TtsSpeaker.this;
                    ttsSpeaker.broadcastAndRemove(ttsSpeaker.mCurBean, PluginConstants.TTS_ERROR_CODE_CALLBACK_ON_ERROR);
                } else {
                    TtsSpeaker.this.mCurBean.report.ttsInitSuccessTime = System.currentTimeMillis();
                    TtsSpeaker ttsSpeaker2 = TtsSpeaker.this;
                    ttsSpeaker2.onSpeak(ttsSpeaker2.mCurBean);
                }
            }
        });
    }

    @Override // com.xiaomi.push.tts.TtsStatusListener
    public void onSpeak(final TtsSpeechBean ttsSpeechBean) {
        this.mSingleThreadExecutor.execute(new SafeRunnable() { // from class: com.xiaomi.push.tts.TtsSpeaker.3
            @Override // com.xiaomi.channel.commonutils.android.SafeRunnable
            public void safeRun() {
                LogPools.LogModule logModule = LogPools.TtsLog;
                logModule.d("--- --- onSpeak");
                if (!ToolUtils.isScreenOn(TtsSpeaker.this.mContext) && ttsSpeechBean.wakeLock) {
                    if (TtsSpeaker.this.mWakeLock == null) {
                        PowerManager powerManager = (PowerManager) TtsSpeaker.this.mContext.getSystemService(NightStandbyRecord.KEY_POWER);
                        TtsSpeaker.this.mWakeLock = powerManager.newWakeLock(1, ttsSpeechBean.pkgName + ":tts");
                    }
                    try {
                        if (!TtsSpeaker.this.mWakeLock.isHeld()) {
                            logModule.w("+running");
                            TtsSpeaker.this.mWakeLock.acquire(PluginConstants.TTS_REMOVE_BEAN_TIME);
                        }
                    } catch (Throwable th) {
                        LogPools.TtsLog.w("keep +running failed：" + th);
                    }
                }
                TtsSpeaker.this.SpeakNow(ttsSpeechBean);
            }
        });
    }

    @Override // android.speech.tts.UtteranceProgressListener
    public void onStart(final String str) {
        this.mSingleThreadExecutor.execute(new SafeRunnable() { // from class: com.xiaomi.push.tts.TtsSpeaker.4
            @Override // com.xiaomi.channel.commonutils.android.SafeRunnable
            public void safeRun() {
                LogPools.TtsLog.d("--- --- onStart");
                TtsSpeechBean ttsSpeechBean = (TtsSpeechBean) TtsSpeaker.this.mSpeakMap.get(str);
                if (ttsSpeechBean != null) {
                    ttsSpeechBean.report.ttsStartPlayTime = System.currentTimeMillis();
                }
            }
        });
    }

    public void removeFromQueue(String str) {
        TtsSpeechBean ttsSpeechBean = this.mSpeakMap.get(str);
        if (ttsSpeechBean != null) {
            if (ttsSpeechBean.isSpeaking) {
                LogPools.TtsLog.d("cur msg is speaking: " + ttsSpeechBean.report.msgId);
                return;
            }
            if (ttsSpeechBean.independent) {
                return;
            }
            LogPools.TtsLog.d("notification removed  : " + ttsSpeechBean.report.msgId);
            broadcastAndRemove(ttsSpeechBean, PluginConstants.TTS_ERROR_CODE_TTS_NOTIFICATION_REMOVED);
        }
    }
}
