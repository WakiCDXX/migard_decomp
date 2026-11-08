package com.xiaomi.channel.commonutils.android;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* JADX INFO: loaded from: classes.dex */
public class ApkTools {
    public static final String ARMEABI = "armeabi";

    /* JADX WARN: Code duplicated, block: B:50:0x011a A[Catch: Exception -> 0x0110, TRY_ENTER, TRY_LEAVE, TryCatch #0 {Exception -> 0x0110, blocks: (B:44:0x010c, B:50:0x011a), top: B:59:0x0001 }] */
    /* JADX WARN: Code duplicated, block: B:64:0x0120 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:84:? A[RETURN, SYNTHETIC] */
    public static void extractSo(Context context, String str, String str2) throws Throwable {
        Throwable th;
        Exception e2;
        int iIndexOf;
        ZipFile zipFile = null;
        try {
            if (str != null) {
                try {
                    try {
                        String str3 = File.separator;
                        if (!str.endsWith(str3)) {
                            str = str + str3;
                        }
                    } catch (Exception e3) {
                        e2 = e3;
                        e2.printStackTrace();
                        if (zipFile == null) {
                            zipFile.close();
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (Exception e4) {
                            e4.printStackTrace();
                        }
                    }
                    throw th;
                }
            }
            if (str2 != null) {
                String str4 = File.separator;
                if (!str2.endsWith(str4)) {
                    str2 = str2 + str4;
                }
            }
            HashMap map = new HashMap();
            List<String> abiList = getAbiList(context);
            ZipFile zipFile2 = new ZipFile(str);
            try {
                Enumeration<? extends ZipEntry> enumerationEntries = zipFile2.entries();
                byte[] bArr = new byte[1024];
                while (enumerationEntries.hasMoreElements()) {
                    ZipEntry zipEntryNextElement = enumerationEntries.nextElement();
                    MyLog.i("ze.getName() = " + zipEntryNextElement.getName());
                    if (zipEntryNextElement.getName().startsWith("lib/") && !zipEntryNextElement.isDirectory()) {
                        String zipName = getZipName(zipEntryNextElement.getName());
                        String zipAbi = getZipAbi(zipEntryNextElement.getName());
                        String str5 = (String) map.get(zipName);
                        if (TextUtils.isEmpty(str5) || ((iIndexOf = indexOf(abiList, zipAbi)) >= 0 && iIndexOf < indexOf(abiList, str5))) {
                            MyLog.i("use abi " + zipAbi);
                            map.put(zipName, zipAbi);
                            File file = new File(str2 + zipName);
                            if (file.exists()) {
                                file.delete();
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile2.getInputStream(zipEntryNextElement));
                            while (true) {
                                int i2 = bufferedInputStream.read(bArr, 0, 1024);
                                if (i2 == -1) {
                                    break;
                                } else {
                                    fileOutputStream.write(bArr, 0, i2);
                                }
                            }
                            IOUtils.closeQuietly(bufferedInputStream);
                            IOUtils.closeQuietly(fileOutputStream);
                        }
                    }
                }
                zipFile2.close();
            } catch (Exception e5) {
                e2 = e5;
                zipFile = zipFile2;
                e2.printStackTrace();
                if (zipFile == null) {
                } else {
                    zipFile.close();
                }
            } catch (Throwable th3) {
                th = th3;
                zipFile = zipFile2;
                if (zipFile != null) {
                    zipFile.close();
                }
                throw th;
            }
        } catch (Exception e6) {
            e6.printStackTrace();
        }
    }

    public static List<String> getAbiList(Context context) {
        ArrayList arrayList = new ArrayList();
        String appPrimaryAbi = getAppPrimaryAbi(context);
        if (!TextUtils.isEmpty(appPrimaryAbi)) {
            arrayList.add(appPrimaryAbi);
        }
        String str = SystemProperties.get("ro.product.cpu.abi", "");
        if (!TextUtils.isEmpty(str)) {
            arrayList.add(str);
        }
        String str2 = SystemProperties.get("ro.product.cpu.abi2", "");
        if (!TextUtils.isEmpty(str2)) {
            arrayList.add(str2);
        }
        String str3 = SystemProperties.get("ro.product.cpu.abilist", "");
        if (!TextUtils.isEmpty(str3)) {
            String[] strArrSplit = str3.split(",");
            for (int i2 = 0; strArrSplit != null && i2 < strArrSplit.length; i2++) {
                if (!TextUtils.isEmpty(strArrSplit[i2])) {
                    arrayList.add(strArrSplit[i2]);
                }
            }
        }
        arrayList.add(ARMEABI);
        return arrayList;
    }

    public static String getAppPrimaryAbi(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            Field declaredField = SystemUtils.loadClass(context, "android.content.pm.ApplicationInfo").getDeclaredField("primaryCpuAbi");
            declaredField.setAccessible(true);
            return (String) declaredField.get(applicationInfo);
        } catch (Throwable unused) {
            return null;
        }
    }

    private static String getZipAbi(String str) {
        String[] strArrSplit;
        return (str == null || (strArrSplit = str.split("/")) == null || strArrSplit.length <= 1) ? ARMEABI : strArrSplit[strArrSplit.length - 2];
    }

    private static String getZipName(String str) {
        String[] strArrSplit;
        return (str == null || (strArrSplit = str.split("/")) == null || strArrSplit.length <= 0) ? str : strArrSplit[strArrSplit.length - 1];
    }

    private static int indexOf(List<String> list, String str) {
        for (int i2 = 0; list != null && i2 < list.size(); i2++) {
            if (!TextUtils.isEmpty(str) && str.equalsIgnoreCase(list.get(i2))) {
                return i2;
            }
        }
        return -1;
    }
}
