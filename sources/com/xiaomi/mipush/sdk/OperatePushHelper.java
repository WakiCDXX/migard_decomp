package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class OperatePushHelper {
    public static final int MAX_RETRY_COUNT = 10;
    public static final String SYNCED = "synced";
    public static final String SYNCING = "syncing";
    public static final int TIME_OUT = 5000;
    private static volatile OperatePushHelper sInstance;
    private Context appContext;
    private List<MessageBean> operateMessages = new ArrayList();

    private OperatePushHelper(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.appContext = applicationContext;
        if (applicationContext == null) {
            this.appContext = context;
        }
    }

    public static OperatePushHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (OperatePushHelper.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new OperatePushHelper(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    public int getRetryCount(String str) {
        synchronized (this.operateMessages) {
            try {
                MessageBean messageBean = new MessageBean();
                messageBean.messageId = str;
                if (this.operateMessages.contains(messageBean)) {
                    for (MessageBean messageBean2 : this.operateMessages) {
                        if (messageBean2.equals(messageBean)) {
                            return messageBean2.count;
                        }
                    }
                }
                return 0;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public synchronized String getSyncStatus(RetryType retryType) {
        return this.appContext.getSharedPreferences("mipush_extra", 0).getString(retryType.name(), "");
    }

    public void increaseRetryCount(String str) {
        synchronized (this.operateMessages) {
            try {
                MessageBean messageBean = new MessageBean();
                messageBean.messageId = str;
                if (this.operateMessages.contains(messageBean)) {
                    for (MessageBean messageBean2 : this.operateMessages) {
                        if (messageBean.equals(messageBean2)) {
                            messageBean = messageBean2;
                            break;
                        }
                    }
                }
                messageBean.count++;
                this.operateMessages.remove(messageBean);
                this.operateMessages.add(messageBean);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean isMessageOperating(String str) {
        synchronized (this.operateMessages) {
            try {
                MessageBean messageBean = new MessageBean();
                messageBean.messageId = str;
                return this.operateMessages.contains(messageBean);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public synchronized void putSyncStatus(RetryType retryType, String str) {
        SharedPreferences sharedPreferences = this.appContext.getSharedPreferences("mipush_extra", 0);
        sharedPreferences.edit().putString(retryType.name(), str).apply();
    }

    public void removeOperateMessage(String str) {
        synchronized (this.operateMessages) {
            try {
                MessageBean messageBean = new MessageBean();
                messageBean.messageId = str;
                if (this.operateMessages.contains(messageBean)) {
                    this.operateMessages.remove(messageBean);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void resetOperateMessage(String str) {
        synchronized (this.operateMessages) {
            try {
                MessageBean messageBean = new MessageBean();
                messageBean.count = 0;
                messageBean.messageId = str;
                if (this.operateMessages.contains(messageBean)) {
                    this.operateMessages.remove(messageBean);
                }
                this.operateMessages.add(messageBean);
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
