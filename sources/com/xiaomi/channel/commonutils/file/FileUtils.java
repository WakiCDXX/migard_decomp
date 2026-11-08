package com.xiaomi.channel.commonutils.file;

import android.util.Log;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class FileUtils {
    private static final int INTERNAL_FILE_MAX_SIZE_BYTE = 104857600;
    private static final String TAG = "FileUtils";
    private static final HashMap<String, String> mFileTypes;

    static {
        HashMap<String, String> map = new HashMap<>();
        mFileTypes = map;
        map.put("FFD8FF", "jpg");
        map.put("89504E47", "png");
        map.put("47494638", "gif");
        map.put("474946", "gif");
        map.put("424D", "bmp");
    }

    private static String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (byte b2 : bArr) {
            String upperCase = Integer.toHexString(b2 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL).toUpperCase();
            if (upperCase.length() < 2) {
                sb.append(0);
            }
            sb.append(upperCase);
        }
        return sb.toString();
    }

    public static boolean canWriteInternalFileDir(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.exists()) {
                return (file.isDirectory() ? getFolderSize(file) : file.length()) < 104857600;
            }
            return true;
        } catch (Exception e2) {
            Log.e(TAG, "Check if internal file can be written error :" + e2.getMessage());
            return false;
        }
    }

    private static String getFileHeader(String str) throws Throwable {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2 = null;
        strBytesToHexString = null;
        String strBytesToHexString = null;
        try {
            fileInputStream = new FileInputStream(str);
            try {
                byte[] bArr = new byte[3];
                fileInputStream.read(bArr, 0, 3);
                strBytesToHexString = bytesToHexString(bArr);
            } catch (Exception unused) {
                if (fileInputStream != null) {
                }
                return strBytesToHexString;
            } catch (Throwable th) {
                th = th;
                fileInputStream2 = fileInputStream;
                if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
        } catch (Exception unused3) {
            fileInputStream = null;
        } catch (Throwable th2) {
            th = th2;
        }
        try {
            fileInputStream.close();
        } catch (IOException unused4) {
        }
        return strBytesToHexString;
    }

    public static String getFileType(String str) {
        return mFileTypes.get(getFileHeader(str));
    }

    public static long getFolderSize(File file) {
        long folderSize = 0;
        try {
            File[] fileArrListFiles = file.listFiles();
            for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                folderSize += fileArrListFiles[i2].isDirectory() ? getFolderSize(fileArrListFiles[i2]) : fileArrListFiles[i2].length();
            }
        } catch (Exception e2) {
            Log.e(TAG, "Get folder size error: " + e2.getMessage());
        }
        return folderSize;
    }

    public static boolean isGif(String str) {
        return "gif".equals(getFileType(str));
    }
}
