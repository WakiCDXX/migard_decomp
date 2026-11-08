package com.xiaomi.push.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.xiaomi.channel.commonutils.file.FileUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/* JADX INFO: loaded from: classes.dex */
public class NotificationIconHelper {
    private static final int BIG_PICTURE_MAX_SIZE = 2048000;
    private static final int CLEAN_ICON_EXIST_PERIOD = 1209600;
    private static final int CONNECT_TIMEOUT = 8000;
    private static final int MAX_SIZE = 102400;
    private static final String PIC_FILE_DIR_PATH = "mipush_icon";
    private static final int READ_TIMEOUT = 20000;
    private static final int READ_UNIT = 1024;
    private static final int SMALL_ICON_FILE_DIR_MAX_SIZE = 15728640;
    private static final int STANDARD_DENSITY = 160;
    private static final int STANDARD_ICON_SIZE = 48;
    private static long currentPicFileSize;

    public static class GetDataResult {
        byte[] data;
        int downloadSize;

        public GetDataResult(byte[] bArr, int i2) {
            this.data = bArr;
            this.downloadSize = i2;
        }
    }

    public static class GetIconResult {
        public Bitmap bitmap;
        public long downloadSize;

        public GetIconResult(Bitmap bitmap, long j2) {
            this.bitmap = bitmap;
            this.downloadSize = j2;
        }
    }

    private static void cleanCachedPic(Context context) {
        File file = new File(context.getCacheDir().getPath() + File.separator + PIC_FILE_DIR_PATH);
        if (file.exists()) {
            if (currentPicFileSize == 0) {
                currentPicFileSize = FileUtils.getFolderSize(file);
            }
            if (currentPicFileSize > 15728640) {
                try {
                    File[] fileArrListFiles = file.listFiles();
                    for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                        if (!fileArrListFiles[i2].isDirectory() && Math.abs(System.currentTimeMillis() - fileArrListFiles[i2].lastModified()) > 1209600) {
                            fileArrListFiles[i2].delete();
                        }
                    }
                } catch (Exception e2) {
                    MyLog.e(e2);
                }
                currentPicFileSize = 0L;
            }
        }
    }

    private static Bitmap getBitmapFromFile(Context context, String str) throws Throwable {
        Throwable th;
        FileInputStream fileInputStream;
        Bitmap bitmap;
        File file = new File(context.getCacheDir().getPath() + File.separator + PIC_FILE_DIR_PATH, XMStringUtils.getMd5Digest(str));
        FileInputStream fileInputStream2 = null;
        Bitmap bitmapDecodeStream = null;
        fileInputStream2 = null;
        if (!file.exists()) {
            return null;
        }
        try {
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    bitmapDecodeStream = BitmapFactory.decodeStream(fileInputStream);
                    file.setLastModified(System.currentTimeMillis());
                    IOUtils.closeQuietly(fileInputStream);
                    return bitmapDecodeStream;
                } catch (Exception e2) {
                    e = e2;
                    Bitmap bitmap2 = bitmapDecodeStream;
                    fileInputStream2 = fileInputStream;
                    bitmap = bitmap2;
                    MyLog.e(e);
                    IOUtils.closeQuietly(fileInputStream2);
                    return bitmap;
                } catch (Throwable th2) {
                    th = th2;
                    IOUtils.closeQuietly(fileInputStream);
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                bitmap = null;
            }
        } catch (Throwable th3) {
            FileInputStream fileInputStream3 = fileInputStream2;
            th = th3;
            fileInputStream = fileInputStream3;
        }
    }

    /* JADX WARN: Code duplicated, block: B:51:0x00df A[PHI: r1
      0x00df: PHI (r1v5 java.net.HttpURLConnection) = (r1v4 java.net.HttpURLConnection), (r1v6 java.net.HttpURLConnection) binds: [B:50:0x00dd, B:54:0x00fa] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Not initialized variable reg: 2, insn: 0x00a5: MOVE (r0 I:??[OBJECT, ARRAY]) = (r2 I:??[OBJECT, ARRAY]) (LINE:166), block:B:33:0x00a5 */
    private static GetDataResult getDataFromUrl(String str, boolean z2) throws Throwable {
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        Closeable closeable;
        Closeable closeable2 = null;
        try {
            try {
                httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                try {
                    httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
                    httpURLConnection.setReadTimeout(READ_TIMEOUT);
                    httpURLConnection.setRequestProperty("User-agent", "Mozilla/5.0 (Linux; U;) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/71.0.3578.141 Mobile Safari/537.36 XiaoMi/MiuiBrowser");
                    httpURLConnection.connect();
                    int contentLength = httpURLConnection.getContentLength();
                    if (z2 && contentLength > MAX_SIZE) {
                        MyLog.w("Bitmap size is too big, max size is 102400  contentLen size is " + contentLength + " from url " + str);
                        IOUtils.closeQuietly(null);
                        httpURLConnection.disconnect();
                        return null;
                    }
                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode != 200) {
                        MyLog.w("Invalid Http Response Code " + responseCode + " received");
                        IOUtils.closeQuietly(null);
                        httpURLConnection.disconnect();
                        return null;
                    }
                    inputStream = httpURLConnection.getInputStream();
                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        int i2 = z2 ? MAX_SIZE : BIG_PICTURE_MAX_SIZE;
                        byte[] bArr = new byte[1024];
                        while (i2 > 0) {
                            int i3 = inputStream.read(bArr, 0, 1024);
                            if (i3 == -1) {
                                break;
                            }
                            i2 -= i3;
                            byteArrayOutputStream.write(bArr, 0, i3);
                        }
                        if (i2 <= 0) {
                            MyLog.w("length 102400 exhausted.");
                            GetDataResult getDataResult = new GetDataResult(null, MAX_SIZE);
                            IOUtils.closeQuietly(inputStream);
                            httpURLConnection.disconnect();
                            return getDataResult;
                        }
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        GetDataResult getDataResult2 = new GetDataResult(byteArray, byteArray.length);
                        IOUtils.closeQuietly(inputStream);
                        httpURLConnection.disconnect();
                        return getDataResult2;
                    } catch (SocketTimeoutException unused) {
                        MyLog.e("Connect timeout to " + str);
                        IOUtils.closeQuietly(inputStream);
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                        return null;
                    } catch (IOException e2) {
                        e = e2;
                        MyLog.e(e);
                        IOUtils.closeQuietly(inputStream);
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                        return null;
                    }
                } catch (SocketTimeoutException unused2) {
                    inputStream = null;
                } catch (IOException e3) {
                    e = e3;
                    inputStream = null;
                } catch (Throwable th) {
                    th = th;
                    IOUtils.closeQuietly(closeable2);
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                closeable2 = closeable;
            }
        } catch (SocketTimeoutException unused3) {
            httpURLConnection = null;
            inputStream = null;
        } catch (IOException e4) {
            e = e4;
            httpURLConnection = null;
            inputStream = null;
        } catch (Throwable th3) {
            th = th3;
            httpURLConnection = null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v3 */
    public static Bitmap getIconFromUri(Context context, String str) throws Throwable {
        InputStream inputStreamOpenInputStream;
        InputStream inputStreamOpenInputStream2;
        Uri uri = Uri.parse(str);
        ?? r0 = 0;
        r0 = 0;
        try {
            try {
                inputStreamOpenInputStream = context.getContentResolver().openInputStream(uri);
                try {
                    int sampleSize = getSampleSize(context, inputStreamOpenInputStream);
                    inputStreamOpenInputStream2 = context.getContentResolver().openInputStream(uri);
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = sampleSize;
                        Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpenInputStream2, null, options);
                        IOUtils.closeQuietly(inputStreamOpenInputStream2);
                        IOUtils.closeQuietly(inputStreamOpenInputStream);
                        return bitmapDecodeStream;
                    } catch (IOException e2) {
                        e = e2;
                        MyLog.e(e);
                        IOUtils.closeQuietly(inputStreamOpenInputStream2);
                        IOUtils.closeQuietly(inputStreamOpenInputStream);
                        return null;
                    }
                } catch (IOException e3) {
                    e = e3;
                    inputStreamOpenInputStream2 = null;
                } catch (Throwable th) {
                    th = th;
                    IOUtils.closeQuietly(r0);
                    IOUtils.closeQuietly(inputStreamOpenInputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                r0 = context;
            }
        } catch (IOException e4) {
            e = e4;
            inputStreamOpenInputStream2 = null;
            inputStreamOpenInputStream = null;
        } catch (Throwable th3) {
            th = th3;
            inputStreamOpenInputStream = null;
        }
    }

    public static GetIconResult getIconFromUrl(Context context, String str, boolean z2) throws Throwable {
        ByteArrayInputStream byteArrayInputStream = null;
        GetIconResult getIconResult = new GetIconResult(null, 0L);
        Bitmap bitmapFromFile = getBitmapFromFile(context, str);
        try {
            if (bitmapFromFile != null) {
                getIconResult.bitmap = bitmapFromFile;
                return getIconResult;
            }
            try {
                GetDataResult dataFromUrl = getDataFromUrl(str, z2);
                if (dataFromUrl == null) {
                    IOUtils.closeQuietly(null);
                    return getIconResult;
                }
                getIconResult.downloadSize = dataFromUrl.downloadSize;
                byte[] bArr = dataFromUrl.data;
                if (bArr != null) {
                    if (z2) {
                        ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(bArr);
                        try {
                            int sampleSize = getSampleSize(context, byteArrayInputStream2);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = sampleSize;
                            getIconResult.bitmap = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
                            byteArrayInputStream = byteArrayInputStream2;
                        } catch (Exception e2) {
                            e = e2;
                            byteArrayInputStream = byteArrayInputStream2;
                            MyLog.e(e);
                        } catch (Throwable th) {
                            th = th;
                            byteArrayInputStream = byteArrayInputStream2;
                            IOUtils.closeQuietly(byteArrayInputStream);
                            throw th;
                        }
                    } else {
                        getIconResult.bitmap = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
                    }
                }
                savePic2File(context, dataFromUrl.data, str);
                IOUtils.closeQuietly(byteArrayInputStream);
                return getIconResult;
            } catch (Exception e3) {
                e = e3;
            }
            MyLog.e(e);
            IOUtils.closeQuietly(byteArrayInputStream);
            return getIconResult;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static int getSampleSize(Context context, InputStream inputStream) {
        int i2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        if (options.outWidth == -1 || options.outHeight == -1) {
            MyLog.w("decode dimension failed for bitmap.");
            return 1;
        }
        int iRound = Math.round((context.getResources().getDisplayMetrics().densityDpi / 160.0f) * 48.0f);
        int i3 = options.outWidth;
        if (i3 <= iRound || (i2 = options.outHeight) <= iRound) {
            return 1;
        }
        return Math.min(i3 / iRound, i2 / iRound);
    }

    private static void savePic2File(Context context, byte[] bArr, String str) throws Throwable {
        FileOutputStream fileOutputStream;
        if (bArr == null) {
            MyLog.w("cannot save small icon cause bitmap is null");
            return;
        }
        cleanCachedPic(context);
        File file = new File(context.getCacheDir().getPath() + File.separator + PIC_FILE_DIR_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, XMStringUtils.getMd5Digest(str));
        BufferedOutputStream bufferedOutputStream = null;
        try {
            if (!file2.exists()) {
                file2.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file2);
            try {
                try {
                    BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(fileOutputStream);
                    try {
                        bufferedOutputStream2.write(bArr);
                        bufferedOutputStream2.flush();
                        IOUtils.closeQuietly(bufferedOutputStream2);
                    } catch (Exception e2) {
                        e = e2;
                        bufferedOutputStream = bufferedOutputStream2;
                        MyLog.e(e);
                        IOUtils.closeQuietly(bufferedOutputStream);
                    } catch (Throwable th) {
                        th = th;
                        bufferedOutputStream = bufferedOutputStream2;
                        IOUtils.closeQuietly(bufferedOutputStream);
                        IOUtils.closeQuietly(fileOutputStream);
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e4) {
            e = e4;
            fileOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream = null;
        }
        IOUtils.closeQuietly(fileOutputStream);
        if (currentPicFileSize == 0) {
            currentPicFileSize = FileUtils.getFolderSize(new File(context.getCacheDir().getPath() + File.separator + PIC_FILE_DIR_PATH)) + file2.length();
        }
    }
}
