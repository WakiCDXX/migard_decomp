package com.xiaomi.push.extension;

import android.os.Handler;
import android.os.Message;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public class LifecycleQueue<V> {
    private Handler mHandler;
    private OnLifecycleListener<V> mListener;
    private Map<String, V> mMap;
    private List<String> mSortedIndex;

    public interface OnLifecycleListener<V> {
        void onLifecycleDied(String str, V v2);
    }

    public LifecycleQueue() {
        this(false);
    }

    private String trimKey(String str) {
        return str == null ? "" : str;
    }

    public void clearAll() {
        this.mMap.clear();
        List<String> list = this.mSortedIndex;
        if (list != null) {
            list.clear();
        }
        this.mHandler.removeCallbacksAndMessages(null);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.mHandler.removeCallbacksAndMessages(null);
        this.mListener = null;
    }

    public V getAndMonitor(String str) {
        return this.mMap.get(trimKey(str));
    }

    public V getAndNotMonitor(String str) {
        String strTrimKey = trimKey(str);
        this.mHandler.removeMessages(strTrimKey.hashCode());
        return getAndMonitor(strTrimKey);
    }

    public String nextKeyAndMonitor() {
        if (size() <= 0) {
            return "";
        }
        List<String> list = this.mSortedIndex;
        return list != null ? list.get(0) : this.mMap.keySet().iterator().next();
    }

    public void putAndMonitor(String str, V v2, long j2) {
        String strTrimKey = trimKey(str);
        removeAndNotMonitor(strTrimKey);
        this.mMap.put(strTrimKey, v2);
        List<String> list = this.mSortedIndex;
        if (list != null) {
            list.add(strTrimKey);
        }
        Message messageObtain = Message.obtain();
        messageObtain.what = strTrimKey.hashCode();
        messageObtain.obj = strTrimKey;
        this.mHandler.sendMessageDelayed(messageObtain, j2);
    }

    public void removeAndNotMonitor(String str) {
        String strTrimKey = trimKey(str);
        this.mHandler.removeMessages(strTrimKey.hashCode());
        this.mMap.remove(strTrimKey);
        List<String> list = this.mSortedIndex;
        if (list != null) {
            list.remove(strTrimKey);
        }
    }

    public void setOnLifecycleListener(OnLifecycleListener<V> onLifecycleListener) {
        this.mListener = onLifecycleListener;
    }

    public int size() {
        return this.mMap.size();
    }

    public LifecycleQueue(boolean z2) {
        this.mMap = new ConcurrentHashMap();
        this.mHandler = QueuedWork.newHandler(new Handler.Callback() { // from class: com.xiaomi.push.extension.LifecycleQueue.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                if (LifecycleQueue.this.mListener == null || message == null) {
                    return true;
                }
                String strValueOf = String.valueOf(message.obj);
                Object obj = LifecycleQueue.this.mMap.get(strValueOf);
                LifecycleQueue.this.removeAndNotMonitor(strValueOf);
                LifecycleQueue.this.mListener.onLifecycleDied(strValueOf, obj);
                return true;
            }
        });
        if (z2) {
            this.mSortedIndex = new CopyOnWriteArrayList();
        }
    }
}
