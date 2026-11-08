package com.xiaomi.channelcollection;

import com.xiaomi.push.service.NotificationUtils;

/* JADX INFO: loaded from: classes.dex */
public class CollectionChannelTracker {
    private long mChannelCount;
    private final long mCollectionTime = System.currentTimeMillis();
    private long mPackCount;
    private long mTotalApp;
    private long mTotalSize;

    public void addChannelCount(long j2) {
        this.mChannelCount += j2;
    }

    public void addTotalSize(long j2) {
        this.mTotalSize += j2;
    }

    public long getChannelCount() {
        return this.mChannelCount;
    }

    public long getCollectionTime() {
        return this.mCollectionTime;
    }

    public long getPackCount() {
        return this.mPackCount;
    }

    public long getTotalApp() {
        return this.mTotalApp;
    }

    public long getTotalSize() {
        return this.mTotalSize;
    }

    public void incrementPackCount() {
        this.mPackCount++;
    }

    public int isBatch() {
        return NotificationUtils.hasBatchSwitchInterface() ? 1 : 0;
    }

    public void setTotalApp(long j2) {
        this.mTotalApp = j2;
    }
}
