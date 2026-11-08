package com.xiaomi.channel.commonutils.android;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class AssetUtils {
    public static boolean extractAssetFile(Context context, String str, String str2) throws Throwable {
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        InputStream inputStream = null;
        try {
            InputStream inputStreamOpen = context.getAssets().open(str);
            try {
                byte[] inputStream2 = IOUtils.readInputStream(inputStreamOpen);
                File file = new File(str2);
                if (file.exists()) {
                    fileInputStream = new FileInputStream(file);
                    try {
                        String md5 = XMStringUtils.getMd5(IOUtils.readInputStream(fileInputStream));
                        String md6 = XMStringUtils.getMd5(inputStream2);
                        if (!TextUtils.isEmpty(md5) && md5.equals(md6)) {
                            IOUtils.closeQuietly(inputStreamOpen);
                            IOUtils.closeQuietly(fileInputStream);
                            IOUtils.closeQuietly(null);
                            return false;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        fileOutputStream = null;
                        inputStream = inputStreamOpen;
                        try {
                            e.printStackTrace();
                            IOUtils.closeQuietly(inputStream);
                            IOUtils.closeQuietly(fileInputStream);
                            IOUtils.closeQuietly(fileOutputStream);
                            return false;
                        } catch (Throwable th) {
                            th = th;
                            IOUtils.closeQuietly(inputStream);
                            IOUtils.closeQuietly(fileInputStream);
                            IOUtils.closeQuietly(fileOutputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        fileOutputStream = null;
                        inputStream = inputStreamOpen;
                        IOUtils.closeQuietly(inputStream);
                        IOUtils.closeQuietly(fileInputStream);
                        IOUtils.closeQuietly(fileOutputStream);
                        throw th;
                    }
                } else {
                    fileInputStream = null;
                }
                fileOutputStream = new FileOutputStream(file);
                try {
                    fileOutputStream.write(inputStream2);
                    fileOutputStream.flush();
                    IOUtils.closeQuietly(inputStreamOpen);
                    IOUtils.closeQuietly(fileInputStream);
                    IOUtils.closeQuietly(fileOutputStream);
                    return true;
                } catch (Exception e3) {
                    e = e3;
                    inputStream = inputStreamOpen;
                    e.printStackTrace();
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(fileInputStream);
                    IOUtils.closeQuietly(fileOutputStream);
                    return false;
                } catch (Throwable th3) {
                    th = th3;
                    inputStream = inputStreamOpen;
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(fileInputStream);
                    IOUtils.closeQuietly(fileOutputStream);
                    throw th;
                }
            } catch (Exception e4) {
                e = e4;
                fileInputStream = null;
                fileOutputStream = null;
            } catch (Throwable th4) {
                th = th4;
                fileInputStream = null;
                fileOutputStream = null;
            }
        } catch (Exception e5) {
            e = e5;
            fileInputStream = null;
            fileOutputStream = null;
        } catch (Throwable th5) {
            th = th5;
            fileInputStream = null;
            fileOutputStream = null;
        }
    }
}
