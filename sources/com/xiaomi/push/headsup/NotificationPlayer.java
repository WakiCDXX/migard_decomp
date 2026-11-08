package com.xiaomi.push.headsup;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import com.xiaomi.push.extension.QueuedWork;
import com.xiaomi.push.service.NotificationManagerHelper;
import com.xiaomi.push.service.NotificationUtils;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(28)
class NotificationPlayer {
    private static final long[] DEFAULT_VIBRATE_PATTERN = {0, 250, 250, 250};
    private SoundPlayer mSoundPlayer;
    private VibratePlayer mVibratePlayer;

    private class SoundPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        private AudioManager mLastAudioFocus;
        private volatile MediaPlayer mLastPlayer;

        private SoundPlayer() {
        }

        private boolean checkCanSound(Context context, String str, NotificationChannel notificationChannel) {
            Uri sound = notificationChannel.getSound();
            AudioAttributes audioAttributes = notificationChannel.getAudioAttributes();
            if (sound == null || audioAttributes == null) {
                HeadsUpLog.w("np-sp check no uri");
                return false;
            }
            int iCheckNotificationSetting = NotificationUtils.checkNotificationSetting(context, str, notificationChannel.getId(), NotificationUtils.SETTING_SOUND);
            if (iCheckNotificationSetting != 1) {
                HeadsUpLog.w("np-sp check no permission " + str + " " + iCheckNotificationSetting);
                return false;
            }
            try {
                AudioManager audioManager = (AudioManager) context.getSystemService("audio");
                if (audioManager.getRingerMode() == 1 || audioManager.getRingerMode() == 0 || audioManager.getStreamVolume(audioAttributes.getVolumeControlStream()) == 0) {
                    HeadsUpLog.w("np-sp check no volume");
                    return false;
                }
            } catch (Exception unused) {
            }
            HeadsUpLog.d("np-sp check succ: " + notificationChannel.getId() + " " + notificationChannel.getSound());
            return true;
        }

        private void playSound(Context context, Uri uri, AudioAttributes audioAttributes) {
            MediaPlayer mediaPlayer;
            MediaPlayer mediaPlayer2 = null;
            try {
                recycle(this.mLastPlayer);
                mediaPlayer = new MediaPlayer();
                if (audioAttributes == null) {
                    try {
                        audioAttributes = new AudioAttributes.Builder().setUsage(5).setContentType(4).build();
                    } catch (Exception e2) {
                        e = e2;
                        HeadsUpLog.w("np-sp start error " + e);
                        recycle(mediaPlayer);
                    }
                }
                mediaPlayer.setAudioAttributes(audioAttributes);
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.setLooping(false);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
                mediaPlayer.prepare();
                HeadsUpLog.d("np-sp prepare");
                AudioManager audioManager = (AudioManager) context.getSystemService("audio");
                audioManager.requestAudioFocus(new AudioFocusRequest.Builder(3).setAudioAttributes(audioAttributes).build());
                this.mLastAudioFocus = audioManager;
                try {
                    Thread.sleep(500L);
                } catch (Exception unused) {
                }
                mediaPlayer.start();
                HeadsUpLog.w("np-sp start " + mediaPlayer);
                mediaPlayer2 = mediaPlayer;
            } catch (Exception e3) {
                e = e3;
                mediaPlayer = null;
            }
            this.mLastPlayer = mediaPlayer2;
        }

        private void recycle(MediaPlayer mediaPlayer) {
            if (this.mLastAudioFocus != null) {
                HeadsUpLog.d("np-sp abandoning focus");
                this.mLastAudioFocus.abandonAudioFocus(null);
                this.mLastAudioFocus = null;
            } else {
                HeadsUpLog.d("np-sp no need to abandon focus");
            }
            if (mediaPlayer == this.mLastPlayer) {
                this.mLastPlayer = null;
            }
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            HeadsUpLog.w("np-sp recycle " + mediaPlayer);
        }

        void cancelSound() {
            MediaPlayer mediaPlayer = this.mLastPlayer;
            HeadsUpLog.d("np-sp stop:" + mediaPlayer);
            if (mediaPlayer == null) {
                return;
            }
            try {
                mediaPlayer.stop();
            } catch (Exception e2) {
                HeadsUpLog.w("np-sp stop error " + e2);
            }
            recycle(mediaPlayer);
        }

        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mediaPlayer) {
            HeadsUpLog.d("np-sp complete");
            recycle(mediaPlayer);
        }

        @Override // android.media.MediaPlayer.OnErrorListener
        public boolean onError(MediaPlayer mediaPlayer, int i2, int i3) {
            HeadsUpLog.d("np-sp error:" + i2 + " " + i3);
            recycle(mediaPlayer);
            return true;
        }

        void tryPlaySound(Context context, String str, NotificationChannel notificationChannel) {
            if (checkCanSound(context, str, notificationChannel)) {
                playSound(context, notificationChannel.getSound(), notificationChannel.getAudioAttributes());
            }
        }
    }

    private class VibratePlayer {
        private VibratePlayer() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cancelVibration(Context context) {
            try {
                ((Vibrator) context.getSystemService("vibrator")).cancel();
                HeadsUpLog.d("np-vp cancel");
            } catch (Exception unused) {
            }
        }

        private boolean checkCanVibrate(Context context, String str, NotificationChannel notificationChannel) {
            if (notificationChannel.getAudioAttributes() == null) {
                HeadsUpLog.w("np-vp check no attr");
                return false;
            }
            if (!notificationChannel.shouldVibrate()) {
                HeadsUpLog.w("np-vp check not allow");
                return false;
            }
            int iCheckNotificationSetting = NotificationUtils.checkNotificationSetting(context, str, notificationChannel.getId(), NotificationUtils.SETTING_VIBRATE);
            if (iCheckNotificationSetting == 1) {
                HeadsUpLog.d("np-vp check succ:" + notificationChannel.getId());
                return true;
            }
            HeadsUpLog.w("np-vp check no permission " + str + " " + iCheckNotificationSetting);
            return false;
        }

        private void playVibration(Context context, long[] jArr, AudioAttributes audioAttributes) {
            try {
                cancelVibration(context);
                ((Vibrator) context.getSystemService("vibrator")).vibrate(VibrationEffect.createWaveform(jArr, -1), audioAttributes);
                HeadsUpLog.d("np-vp play");
            } catch (Exception unused) {
            }
        }

        void tryPlayVibration(Context context, String str, NotificationChannel notificationChannel) {
            if (checkCanVibrate(context, str, notificationChannel)) {
                playVibration(context, NotificationPlayer.DEFAULT_VIBRATE_PATTERN, notificationChannel.getAudioAttributes());
            }
        }
    }

    NotificationPlayer() {
        this.mSoundPlayer = new SoundPlayer();
        this.mVibratePlayer = new VibratePlayer();
    }

    void play(Context context, HeadsUpNotification headsUpNotification) {
        if (context == null || headsUpNotification == null || TextUtils.isEmpty(headsUpNotification.pkgName)) {
            HeadsUpLog.w("np play abandoned, because params are null");
            return;
        }
        if (headsUpNotification.suppressEffects) {
            HeadsUpLog.w("np play abandoned, because should suppress effects");
            return;
        }
        if (HeadsUpUtils.isDndMode(context)) {
            HeadsUpLog.w("np play abandoned, because DndMode");
            return;
        }
        final Context applicationContext = context.getApplicationContext();
        final String str = headsUpNotification.pkgName;
        final NotificationChannel notificationChannel = NotificationManagerHelper.from(applicationContext, str).getNotificationChannel(headsUpNotification.f1747n.getChannelId());
        if (notificationChannel == null) {
            HeadsUpLog.w("np play abandoned, because no channel");
        } else {
            QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.headsup.NotificationPlayer.1
                @Override // java.lang.Runnable
                public void run() {
                    NotificationPlayer.this.mSoundPlayer.tryPlaySound(applicationContext, str, notificationChannel);
                    NotificationPlayer.this.mVibratePlayer.tryPlayVibration(applicationContext, str, notificationChannel);
                }
            });
        }
    }

    void stop(Context context, String str) {
        HeadsUpLog.d("np stop by " + str);
        final Context applicationContext = context.getApplicationContext();
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.headsup.NotificationPlayer.2
            @Override // java.lang.Runnable
            public void run() {
                NotificationPlayer.this.mSoundPlayer.cancelSound();
                NotificationPlayer.this.mVibratePlayer.cancelVibration(applicationContext);
            }
        });
    }
}
