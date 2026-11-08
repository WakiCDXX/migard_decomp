package com.xiaomi.push.sweetnotification;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.NotificationIconHelper;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes.dex */
public class ImageDownloadHelper {
    public static final int MAX_DOWNLOAD_ONLINE_PICTURE_WAIT = 180;
    private static ExecutorService sThreadPool = Executors.newCachedThreadPool();

    private static class DownloadOnlinePicTask implements Callable<Bitmap> {
        private Context context;
        private boolean isSizeLimited;
        private String picUrl;

        public DownloadOnlinePicTask(String str, Context context, boolean z2) {
            this.context = context;
            this.picUrl = str;
            this.isSizeLimited = z2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public Bitmap call() throws Exception {
            if (TextUtils.isEmpty(this.picUrl)) {
                MyLog.w("Failed get online picture/icon resource cause picUrl is empty");
                return null;
            }
            if (this.picUrl.startsWith("http")) {
                NotificationIconHelper.GetIconResult iconFromUrl = NotificationIconHelper.getIconFromUrl(this.context, this.picUrl, this.isSizeLimited);
                if (iconFromUrl != null) {
                    return iconFromUrl.bitmap;
                }
                MyLog.w("Failed get online picture/icon resource");
                return null;
            }
            Bitmap iconFromUri = NotificationIconHelper.getIconFromUri(this.context, this.picUrl);
            if (iconFromUri != null) {
                return iconFromUri;
            }
            MyLog.w("Failed get online picture/icon resource");
            return iconFromUri;
        }
    }

    public static Bitmap getOnlinePictureResource(Context context, String str, boolean z2) {
        Future futureSubmit = sThreadPool.submit(new DownloadOnlinePicTask(str, context, z2));
        try {
            try {
                try {
                    Bitmap bitmap = (Bitmap) futureSubmit.get(180L, TimeUnit.SECONDS);
                    if (bitmap != null) {
                        return bitmap;
                    }
                    futureSubmit.cancel(true);
                    return bitmap;
                } catch (ExecutionException e2) {
                    MyLog.e(e2);
                    futureSubmit.cancel(true);
                    return null;
                }
            } catch (InterruptedException e3) {
                MyLog.e(e3);
                futureSubmit.cancel(true);
                return null;
            } catch (TimeoutException e4) {
                MyLog.e(e4);
                futureSubmit.cancel(true);
                return null;
            }
        } catch (Throwable th) {
            futureSubmit.cancel(true);
            throw th;
        }
    }
}
