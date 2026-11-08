package com.miui.powerkeeper.batterylife.observer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import com.miui.powerkeeper.batterylife.ProcAudioPowerManager;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class AudioChangeObserver {
    private AudioBroadcastReceiver mAudioBroadcastReceiver;
    private AudioChangeListener mAudioChangeListener;
    private AudioManager mAudioManager;
    private Context mContext;
    private boolean mRegistered = false;

    private static class AudioBroadcastReceiver extends BroadcastReceiver {
        private WeakReference<AudioChangeObserver> mObserverWeakReference;

        public AudioBroadcastReceiver(AudioChangeObserver audioChangeObserver) {
            this.mObserverWeakReference = new WeakReference<>(audioChangeObserver);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            AudioChangeListener audioChangeListener;
            AudioChangeObserver audioChangeObserver = this.mObserverWeakReference.get();
            if (audioChangeObserver == null || (audioChangeListener = audioChangeObserver.getAudioChangeListener()) == null) {
                return;
            }
            audioChangeListener.onAudioChanged(intent);
        }
    }

    public interface AudioChangeListener {
        void onAudioChanged(Intent intent);
    }

    public AudioChangeObserver(Context context) {
        this.mContext = context;
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
        }
    }

    public AudioChangeListener getAudioChangeListener() {
        return this.mAudioChangeListener;
    }

    public int getCurrentMusicVolume() {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            return audioManager.getStreamVolume(3);
        }
        return -1;
    }

    public int getMaxMusicVolume() {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            return audioManager.getStreamMaxVolume(3);
        }
        return 15;
    }

    public void registerReceiver() {
        this.mAudioBroadcastReceiver = new AudioBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ProcAudioPowerManager.VOLUME_CHANGED_ACTION);
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        this.mContext.registerReceiver(this.mAudioBroadcastReceiver, intentFilter);
        this.mRegistered = true;
    }

    public void setAudioChangeListener(AudioChangeListener audioChangeListener) {
        this.mAudioChangeListener = audioChangeListener;
    }

    public void unregisterReceiver() {
        if (this.mRegistered) {
            try {
                this.mContext.unregisterReceiver(this.mAudioBroadcastReceiver);
                this.mAudioBroadcastReceiver = null;
                this.mRegistered = false;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
