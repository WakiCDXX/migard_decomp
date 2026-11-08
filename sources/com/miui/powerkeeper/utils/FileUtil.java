package com.miui.powerkeeper.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.push.service.PushConstants;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class FileUtil {
    private static final int BUFFER_SIZE = 2048;
    private static final int PROFILE_MAX_BYTE = 1048576;
    private static final String TAG = "FileUtil";
    private static Map<String, String[]> sDirectoryList = new HashMap();

    public static boolean appendFileString(String str, String str2) {
        File file = new File(str);
        if (!file.exists()) {
            createFile(str);
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            try {
                fileOutputStream.write(str2.getBytes());
                fileOutputStream.close();
                return true;
            } catch (Throwable th) {
                try {
                    fileOutputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException e2) {
            Log.e(TAG, "FileUtil appendFileString", e2);
            return false;
        }
    }

    public static boolean checkConfigReady(String str, String str2) {
        File file = new File(str2);
        if (!file.exists() || !file.isDirectory() || file.listFiles() == null || file.listFiles().length <= 0) {
            Log.d(str, "ConfigReady:false");
            return false;
        }
        Log.d(str, "ConfigReady:true");
        return true;
    }

    public static boolean checkFileReady(String str, String str2) {
        File file = new File(str2);
        if (!file.exists() || file.isDirectory()) {
            Log.d(str, "ConfigReady:false");
            return false;
        }
        Log.d(str, "ConfigReady:true");
        return true;
    }

    public static boolean copyFile(File file, File file2) {
        if (createFile(file2.toString()) != null) {
            return doCopyFile(file, file2);
        }
        return false;
    }

    public static boolean copyToFile(InputStream inputStream, File file) throws Throwable {
        if (file.exists() && !file.delete()) {
            return false;
        }
        FileOutputStream fileOutputStream = null;
        try {
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                try {
                    byte[] bArr = new byte[4096];
                    while (true) {
                        int i2 = inputStream.read(bArr);
                        if (i2 >= 0) {
                            fileOutputStream2.write(bArr, 0, i2);
                        } else {
                            try {
                                break;
                            } catch (IOException e2) {
                                Log.e(TAG, TAG, e2);
                            }
                        }
                    }
                    fileOutputStream2.flush();
                    fileOutputStream2.getFD().sync();
                    fileOutputStream2.close();
                    return true;
                } catch (Exception e3) {
                    e = e3;
                    fileOutputStream = fileOutputStream2;
                    Log.e(TAG, "copyToFile", e);
                    if (fileOutputStream == null) {
                        return false;
                    }
                    try {
                        fileOutputStream.flush();
                        fileOutputStream.getFD().sync();
                        fileOutputStream.close();
                        return false;
                    } catch (IOException e4) {
                        Log.e(TAG, TAG, e4);
                        return false;
                    }
                } catch (Throwable th) {
                    th = th;
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.flush();
                            fileOutputStream.getFD().sync();
                            fileOutputStream.close();
                        } catch (IOException e5) {
                            Log.e(TAG, TAG, e5);
                        }
                    }
                    throw th;
                }
            } catch (Exception e6) {
                e = e6;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static File createFile(String str) {
        File file = new File(str);
        if (file.exists()) {
            return file;
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            return parentFile;
        }
        try {
            if (file.createNewFile()) {
                return file;
            }
            return null;
        } catch (IOException e2) {
            Log.e(TAG, TAG, e2);
            return null;
        }
    }

    public static boolean deleteFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return file.delete();
        }
        for (File file2 : file.listFiles()) {
            if (!deleteFile(file2)) {
                return false;
            }
        }
        return file.delete();
    }

    public static boolean deleteFiles(File... fileArr) {
        for (File file : fileArr) {
            if (!deleteFile(file)) {
                return false;
            }
        }
        return true;
    }

    public static boolean doCopyFile(File file, File file2) {
        try {
            InputStream inputStreamNewInputStream = Files.newInputStream(file.toPath(), new OpenOption[0]);
            try {
                boolean zCopyToFile = copyToFile(inputStreamNewInputStream, file2);
                if (inputStreamNewInputStream != null) {
                    inputStreamNewInputStream.close();
                }
                return zCopyToFile;
            } catch (Throwable th) {
                if (inputStreamNewInputStream != null) {
                    try {
                        inputStreamNewInputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException unused) {
            return false;
        }
    }

    /* JADX WARN: Code duplicated, block: B:102:? A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:76:0x0093 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:88:0x009d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static byte[] getFileBytes(String str) throws Throwable {
        FileInputStream fileInputStream;
        ByteArrayOutputStream byteArrayOutputStream;
        ByteArrayOutputStream byteArrayOutputStream2 = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        File file = new File(str);
        if (!file.exists() || file.length() <= 0) {
            return null;
        }
        try {
            fileInputStream = new FileInputStream(file);
            try {
                byte[] bArr = new byte[1024];
                byteArrayOutputStream = null;
                while (true) {
                    try {
                        try {
                            int i2 = fileInputStream.read(bArr);
                            if (i2 <= 0) {
                                break;
                            }
                            if (byteArrayOutputStream == null) {
                                byteArrayOutputStream = new ByteArrayOutputStream();
                            }
                            byteArrayOutputStream.write(bArr, 0, i2);
                        } catch (Exception e2) {
                            e = e2;
                            Log.e(TAG, TAG, e);
                            if (byteArrayOutputStream != null) {
                                try {
                                    byteArrayOutputStream.close();
                                } catch (IOException e3) {
                                    Log.e(TAG, TAG, e3);
                                }
                            }
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e4) {
                                    Log.e(TAG, TAG, e4);
                                }
                            }
                            return null;
                        }
                    } catch (Throwable th) {
                        th = th;
                        byteArrayOutputStream2 = byteArrayOutputStream;
                    }
                    th = th;
                    byteArrayOutputStream2 = byteArrayOutputStream;
                    if (byteArrayOutputStream2 != null) {
                        try {
                            byteArrayOutputStream2.close();
                        } catch (IOException e5) {
                            Log.e(TAG, TAG, e5);
                        }
                    }
                    if (fileInputStream != null) {
                        throw th;
                    }
                    try {
                        fileInputStream.close();
                        throw th;
                    } catch (IOException e6) {
                        Log.e(TAG, TAG, e6);
                        throw th;
                    }
                }
                if (byteArrayOutputStream != null) {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException e7) {
                        Log.e(TAG, TAG, e7);
                    }
                    try {
                        fileInputStream.close();
                    } catch (IOException e8) {
                        Log.e(TAG, TAG, e8);
                    }
                    return byteArray;
                }
                if (byteArrayOutputStream != null) {
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException e9) {
                        Log.e(TAG, TAG, e9);
                    }
                }
                try {
                    fileInputStream.close();
                } catch (IOException e10) {
                    Log.e(TAG, TAG, e10);
                }
                return null;
            } catch (Exception e11) {
                e = e11;
                byteArrayOutputStream = null;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e12) {
            e = e12;
            fileInputStream = null;
            byteArrayOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            fileInputStream = null;
        }
        th = th;
        byteArrayOutputStream2 = byteArrayOutputStream;
        if (byteArrayOutputStream2 != null) {
            byteArrayOutputStream2.close();
        }
        if (fileInputStream != null) {
            throw th;
        }
        fileInputStream.close();
        throw th;
    }

    public static String getFileString(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return getFileString(new File(str));
    }

    public static String getRawFileString(Context context, int i2) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(i2)));
        StringBuilder sb = new StringBuilder();
        try {
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e2) {
            Log.e(TAG, TAG, e2);
            try {
                return null;
            } catch (Exception e3) {
                return null;
            }
        } finally {
            try {
                bufferedReader.close();
            } catch (Exception e4) {
                Log.e(TAG, TAG, e4);
            }
        }
    }

    public static String getRawString(byte[] bArr) {
        return getRawString(bArr, "UTF-8");
    }

    public static String readFile(String str, int i2) {
        File file = new File(str);
        String str2 = null;
        if (!file.exists()) {
            return null;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            int i3 = 0;
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (i3 == i2) {
                        str2 = line;
                        break;
                    }
                    i3++;
                    str2 = line;
                } catch (Throwable th) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
            bufferedReader.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return str2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.io.InputStream] */
    public static String readFromFile(String str) throws Throwable {
        FileInputStream fileInputStream;
        StringBuilder sb;
        String str2;
        ?? r2 = 0;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        File file = new File(str);
        if (file.exists()) {
            try {
                if (file.length() > 0) {
                    try {
                        fileInputStream = new FileInputStream(file);
                        try {
                            int iAvailable = fileInputStream.available();
                            if (iAvailable > 1048576 || iAvailable < 0) {
                                Log.e(TAG, "readFromFile, fis.available() error length: " + iAvailable);
                                str2 = null;
                            } else {
                                byte[] bArr = new byte[iAvailable];
                                if (fileInputStream.read(bArr) < 0) {
                                    fileInputStream.close();
                                    try {
                                        fileInputStream.close();
                                    } catch (IOException e2) {
                                        Log.e(TAG, "readFromFile, finally fis.close() IOException e:" + e2.getMessage());
                                    }
                                    return null;
                                }
                                str2 = new String(bArr, StandardCharsets.UTF_8);
                                fileInputStream.close();
                            }
                            try {
                                fileInputStream.close();
                                return str2;
                            } catch (IOException e3) {
                                e = e3;
                                sb = new StringBuilder();
                                sb.append("readFromFile, finally fis.close() IOException e:");
                                sb.append(e.getMessage());
                                Log.e(TAG, sb.toString());
                                return null;
                            }
                        } catch (IOException e4) {
                            e = e4;
                            Log.e(TAG, "readFromFile, IOException e:" + e.getMessage());
                            if (fileInputStream == null) {
                                return null;
                            }
                            try {
                                fileInputStream.close();
                                return null;
                            } catch (IOException e5) {
                                e = e5;
                                sb = new StringBuilder();
                                sb.append("readFromFile, finally fis.close() IOException e:");
                                sb.append(e.getMessage());
                                Log.e(TAG, sb.toString());
                                return null;
                            }
                        }
                    } catch (IOException e6) {
                        e = e6;
                        fileInputStream = null;
                    } catch (Throwable th) {
                        th = th;
                        if (r2 != 0) {
                            try {
                                r2.close();
                            } catch (IOException e7) {
                                Log.e(TAG, "readFromFile, finally fis.close() IOException e:" + e7.getMessage());
                            }
                        }
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                r2 = str;
            }
        }
        Log.e(TAG, "readFromFile, " + str + " does not exist");
        return null;
    }

    /* JADX WARN: Code duplicated, block: B:38:0x0043 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static boolean saveFileString(String str, String str2) throws Throwable {
        FileOutputStream fileOutputStream;
        Throwable th;
        IOException e2;
        File file = new File(str);
        if (!file.exists()) {
            createFile(str);
        }
        try {
            fileOutputStream = new FileOutputStream(file);
            try {
                try {
                    fileOutputStream.write(str2.getBytes());
                    try {
                        fileOutputStream.close();
                        return true;
                    } catch (IOException e3) {
                        Log.e(TAG, TAG, e3);
                        return true;
                    }
                } catch (IOException e4) {
                    e2 = e4;
                    Log.e(TAG, TAG, e2);
                    if (fileOutputStream == null) {
                        return false;
                    }
                    try {
                        fileOutputStream.close();
                        return false;
                    } catch (IOException e5) {
                        Log.e(TAG, TAG, e5);
                        return false;
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e6) {
                        Log.e(TAG, TAG, e6);
                    }
                }
                throw th;
            }
        } catch (IOException e7) {
            fileOutputStream = null;
            e2 = e7;
        } catch (Throwable th3) {
            fileOutputStream = null;
            th = th3;
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:138:0x0096 A[EXC_TOP_SPLITTER, PHI: r1 r2 r3 r4
      0x0096: PHI (r1v4 java.io.InputStream) = (r1v5 java.io.InputStream), (r1v2 java.io.InputStream) binds: [B:73:0x0138, B:26:0x0094] A[DONT_GENERATE, DONT_INLINE]
      0x0096: PHI (r2v3 java.io.BufferedInputStream) = (r2v4 java.io.BufferedInputStream), (r2v1 java.io.BufferedInputStream) binds: [B:73:0x0138, B:26:0x0094] A[DONT_GENERATE, DONT_INLINE]
      0x0096: PHI (r3v3 java.io.FileOutputStream) = (r3v4 java.io.FileOutputStream), (r3v1 java.io.FileOutputStream) binds: [B:73:0x0138, B:26:0x0094] A[DONT_GENERATE, DONT_INLINE]
      0x0096: PHI (r4v3 java.io.BufferedOutputStream) = (r4v4 java.io.BufferedOutputStream), (r4v1 java.io.BufferedOutputStream) binds: [B:73:0x0138, B:26:0x0094] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    public static void unZipFile(String str, String str2) {
        if (unsafePath(str)) {
            return;
        }
        try {
            ZipFile zipFile = new ZipFile(str);
            Enumeration<? extends ZipEntry> enumerationEntries = zipFile.entries();
            new File(str2).mkdirs();
            InputStream inputStream = null;
            BufferedInputStream bufferedInputStream = null;
            FileOutputStream fileOutputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            while (enumerationEntries.hasMoreElements()) {
                try {
                    try {
                        ZipEntry zipEntryNextElement = enumerationEntries.nextElement();
                        if (zipEntryNextElement.isDirectory()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("unZipFile:---------------> ");
                            sb.append(str2);
                            String str3 = File.separator;
                            sb.append(str3);
                            sb.append(zipEntryNextElement.getName());
                            Log.i(TAG, sb.toString());
                            if (!zipEntryNextElement.getName().contains("..")) {
                                new File(str2 + str3 + zipEntryNextElement.getName()).mkdirs();
                            }
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException unused) {
                                }
                            }
                            if (bufferedOutputStream != null) {
                                try {
                                    bufferedOutputStream.flush();
                                    bufferedOutputStream.close();
                                } catch (IOException unused2) {
                                }
                            }
                            if (bufferedInputStream != null) {
                                try {
                                    bufferedInputStream.close();
                                } catch (IOException unused3) {
                                }
                            }
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException unused4) {
                                }
                            }
                        } else {
                            inputStream = zipFile.getInputStream(zipEntryNextElement);
                            BufferedInputStream bufferedInputStream2 = new BufferedInputStream(inputStream);
                            try {
                                File file = new File(str2 + File.separator + zipEntryNextElement.getName());
                                File parentFile = file.getParentFile();
                                if (parentFile != null && !parentFile.exists()) {
                                    parentFile.mkdirs();
                                }
                                FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                                try {
                                    BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(fileOutputStream2, 2048);
                                    try {
                                        byte[] bArr = new byte[2048];
                                        while (true) {
                                            int i2 = bufferedInputStream2.read(bArr, 0, 2048);
                                            if (i2 == -1) {
                                                break;
                                            } else {
                                                bufferedOutputStream2.write(bArr, 0, i2);
                                            }
                                        }
                                        if (inputStream != null) {
                                            try {
                                                inputStream.close();
                                            } catch (IOException unused5) {
                                            }
                                        }
                                        try {
                                            bufferedOutputStream2.flush();
                                            bufferedOutputStream2.close();
                                        } catch (IOException unused6) {
                                        }
                                        try {
                                            bufferedInputStream2.close();
                                        } catch (IOException unused7) {
                                        }
                                        try {
                                            fileOutputStream2.close();
                                        } catch (IOException unused8) {
                                        }
                                        bufferedOutputStream = bufferedOutputStream2;
                                        fileOutputStream = fileOutputStream2;
                                        bufferedInputStream = bufferedInputStream2;
                                    } catch (IOException unused9) {
                                        bufferedOutputStream = bufferedOutputStream2;
                                        fileOutputStream = fileOutputStream2;
                                        bufferedInputStream = bufferedInputStream2;
                                        if (inputStream != null) {
                                            try {
                                                inputStream.close();
                                            } catch (IOException unused10) {
                                            }
                                        }
                                        if (bufferedOutputStream != null) {
                                            try {
                                                bufferedOutputStream.flush();
                                                bufferedOutputStream.close();
                                            } catch (IOException unused11) {
                                            }
                                        }
                                        if (bufferedInputStream != null) {
                                            try {
                                                bufferedInputStream.close();
                                            } catch (IOException unused12) {
                                            }
                                        }
                                        if (fileOutputStream != null) {
                                            fileOutputStream.close();
                                        }
                                    } catch (Throwable th) {
                                        th = th;
                                        bufferedOutputStream = bufferedOutputStream2;
                                        fileOutputStream = fileOutputStream2;
                                        bufferedInputStream = bufferedInputStream2;
                                        if (inputStream != null) {
                                            try {
                                                inputStream.close();
                                            } catch (IOException unused13) {
                                            }
                                        }
                                        if (bufferedOutputStream != null) {
                                            try {
                                                bufferedOutputStream.flush();
                                                bufferedOutputStream.close();
                                            } catch (IOException unused14) {
                                            }
                                        }
                                        if (bufferedInputStream != null) {
                                            try {
                                                bufferedInputStream.close();
                                            } catch (IOException unused15) {
                                            }
                                        }
                                        if (fileOutputStream == null) {
                                            throw th;
                                        }
                                        try {
                                            fileOutputStream.close();
                                            throw th;
                                        } catch (IOException unused16) {
                                            throw th;
                                        }
                                    }
                                } catch (IOException unused17) {
                                } catch (Throwable th2) {
                                    th = th2;
                                }
                            } catch (IOException unused18) {
                            } catch (Throwable th3) {
                                th = th3;
                            }
                        }
                    } catch (IOException unused19) {
                    } catch (Throwable th4) {
                        th = th4;
                    }
                } catch (Throwable th5) {
                    try {
                        zipFile.close();
                    } catch (IOException unused20) {
                    }
                    throw th5;
                }
            }
            try {
                zipFile.close();
            } catch (IOException unused21) {
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private static boolean unsafePath(String str) {
        if (str == null || "".equals(str.trim())) {
            return false;
        }
        return str.matches(".*\\.\\./.*");
    }

    public static boolean writeToFile(String str, int i2) {
        return writeToFile(str, String.valueOf(i2));
    }

    /* JADX WARN: Code duplicated, block: B:100:? A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:54:0x009c A[Catch: IOException -> 0x0063, TRY_ENTER, TRY_LEAVE, TryCatch #13 {IOException -> 0x0063, blocks: (B:24:0x005f, B:54:0x009c), top: B:88:0x0001 }] */
    /* JADX WARN: Code duplicated, block: B:72:0x00b6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:74:0x0092 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:81:0x00ac A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:83:0x0088 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:85:0x00a2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:99:? A[RETURN, SYNTHETIC] */
    public static void zipLog(String str) throws Throwable {
        ZipOutputStream zipOutputStream;
        FileOutputStream fileOutputStream;
        Throwable th;
        FileInputStream fileInputStream;
        Exception e2;
        try {
            try {
                File file = new File(str);
                File file2 = new File(str + PushConstants.UPLOAD_FILE_ZIP_POSTFIX);
                fileInputStream = new FileInputStream(file);
                try {
                    fileOutputStream = new FileOutputStream(file2);
                    try {
                        zipOutputStream = new ZipOutputStream(fileOutputStream);
                        try {
                            try {
                                byte[] bArr = new byte[2048];
                                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                                while (true) {
                                    int i2 = fileInputStream.read(bArr);
                                    if (i2 == -1) {
                                        break;
                                    } else {
                                        zipOutputStream.write(bArr, 0, i2);
                                    }
                                }
                                zipOutputStream.closeEntry();
                                try {
                                    fileInputStream.close();
                                } catch (IOException e3) {
                                    e3.printStackTrace();
                                }
                                try {
                                    zipOutputStream.close();
                                } catch (IOException e4) {
                                    e4.printStackTrace();
                                }
                                fileOutputStream.close();
                            } catch (Exception e5) {
                                e2 = e5;
                                e2.printStackTrace();
                                if (fileInputStream != null) {
                                    try {
                                        fileInputStream.close();
                                    } catch (IOException e6) {
                                        e6.printStackTrace();
                                    }
                                }
                                if (zipOutputStream != null) {
                                    try {
                                        zipOutputStream.close();
                                    } catch (IOException e7) {
                                        e7.printStackTrace();
                                    }
                                }
                                if (fileOutputStream == null) {
                                } else {
                                    fileOutputStream.close();
                                }
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e8) {
                                    e8.printStackTrace();
                                }
                            }
                            if (zipOutputStream != null) {
                                try {
                                    zipOutputStream.close();
                                } catch (IOException e9) {
                                    e9.printStackTrace();
                                }
                            }
                            if (fileOutputStream != null) {
                                throw th;
                            }
                            try {
                                fileOutputStream.close();
                                throw th;
                            } catch (IOException e10) {
                                e10.printStackTrace();
                                throw th;
                            }
                        }
                    } catch (Exception e11) {
                        e = e11;
                        zipOutputStream = null;
                        e2 = e;
                        e2.printStackTrace();
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        if (zipOutputStream != null) {
                            zipOutputStream.close();
                        }
                        if (fileOutputStream == null) {
                            fileOutputStream.close();
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        zipOutputStream = null;
                        th = th;
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        if (zipOutputStream != null) {
                            zipOutputStream.close();
                        }
                        if (fileOutputStream != null) {
                            throw th;
                        }
                        fileOutputStream.close();
                        throw th;
                    }
                } catch (Exception e12) {
                    e = e12;
                    zipOutputStream = null;
                    fileOutputStream = null;
                } catch (Throwable th4) {
                    th = th4;
                    zipOutputStream = null;
                    fileOutputStream = null;
                }
            } catch (IOException e13) {
                e13.printStackTrace();
            }
        } catch (Exception e14) {
            zipOutputStream = null;
            fileOutputStream = null;
            e2 = e14;
            fileInputStream = null;
        } catch (Throwable th5) {
            zipOutputStream = null;
            fileOutputStream = null;
            th = th5;
            fileInputStream = null;
        }
    }

    public static String getRawString(byte[] bArr, String str) {
        try {
            return new String(bArr, str);
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
            return new String(bArr);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v13 */
    /* JADX WARN: Type inference failed for: r2v14 */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r2v8 */
    public static boolean writeToFile(String str, String str2) throws Throwable {
        StringBuilder sb;
        boolean z2 = false;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        ?? r2 = 0;
        FileOutputStream fileOutputStream = null;
        try {
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(new File(str));
                try {
                    Charset charset = StandardCharsets.UTF_8;
                    fileOutputStream2.write(str2.getBytes(charset));
                    try {
                        fileOutputStream2.close();
                        z2 = true;
                        r2 = charset;
                    } catch (IOException e2) {
                        e = e2;
                        sb = new StringBuilder();
                        sb.append("writeToFile, finally fos.close() IOException e:");
                        sb.append(e.getMessage());
                        Log.e(TAG, sb.toString());
                    }
                } catch (IOException e3) {
                    e = e3;
                    fileOutputStream = fileOutputStream2;
                    Log.e(TAG, "writeToFile, IOException e:" + e.getMessage());
                    r2 = fileOutputStream;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                            r2 = fileOutputStream;
                        } catch (IOException e4) {
                            e = e4;
                            sb = new StringBuilder();
                            sb.append("writeToFile, finally fos.close() IOException e:");
                            sb.append(e.getMessage());
                            Log.e(TAG, sb.toString());
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    r2 = fileOutputStream2;
                    if (r2 != 0) {
                        try {
                            r2.close();
                        } catch (IOException e5) {
                            Log.e(TAG, "writeToFile, finally fos.close() IOException e:" + e5.getMessage());
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e6) {
            e = e6;
        }
        return z2;
    }

    public static boolean copyFile(String str, String str2) {
        return copyFile(new File(str), new File(str2));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v1, types: [java.io.FileReader] */
    /* JADX WARN: Type inference failed for: r2v16 */
    /* JADX WARN: Type inference failed for: r2v17 */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r6v11 */
    /* JADX WARN: Type inference failed for: r6v16 */
    /* JADX WARN: Type inference failed for: r6v2, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r6v5 */
    public static String getFileString(File file) throws Throwable {
        ?? r6;
        ?? r2;
        BufferedReader bufferedReader;
        FileReader fileReader;
        FileReader fileReader2;
        if (file != null) {
            boolean zExists = file.exists();
            try {
                try {
                    if (zExists) {
                        try {
                            FileReader fileReader3 = new FileReader(file);
                            try {
                                bufferedReader = new BufferedReader(fileReader3);
                                try {
                                    StringBuilder sb = new StringBuilder();
                                    while (true) {
                                        String line = bufferedReader.readLine();
                                        if (line == null) {
                                            break;
                                        }
                                        sb.append(line);
                                    }
                                    String string = sb.toString();
                                    try {
                                        fileReader3.close();
                                    } catch (IOException e2) {
                                        Log.e(TAG, TAG, e2);
                                    }
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e3) {
                                        Log.e(TAG, TAG, e3);
                                    }
                                    return string;
                                } catch (FileNotFoundException e4) {
                                    e = e4;
                                    fileReader2 = fileReader3;
                                    Log.e(TAG, TAG, e);
                                    if (fileReader2 != null) {
                                        try {
                                            fileReader2.close();
                                        } catch (IOException e5) {
                                            Log.e(TAG, TAG, e5);
                                        }
                                    }
                                    if (bufferedReader != null) {
                                        bufferedReader.close();
                                    }
                                    return null;
                                } catch (IOException e6) {
                                    e = e6;
                                    fileReader = fileReader3;
                                    Log.e(TAG, TAG, e);
                                    if (fileReader != null) {
                                        try {
                                            fileReader.close();
                                        } catch (IOException e7) {
                                            Log.e(TAG, TAG, e7);
                                        }
                                    }
                                    if (bufferedReader != null) {
                                        bufferedReader.close();
                                    }
                                    return null;
                                }
                            } catch (FileNotFoundException e8) {
                                e = e8;
                                bufferedReader = null;
                                fileReader2 = fileReader3;
                            } catch (IOException e9) {
                                e = e9;
                                bufferedReader = null;
                                fileReader = fileReader3;
                            } catch (Throwable th) {
                                th = th;
                                r6 = 0;
                                r2 = fileReader3;
                                if (r2 != 0) {
                                    try {
                                        r2.close();
                                    } catch (IOException e10) {
                                        Log.e(TAG, TAG, e10);
                                    }
                                }
                                if (r6 != 0) {
                                    try {
                                        r6.close();
                                        throw th;
                                    } catch (IOException e11) {
                                        Log.e(TAG, TAG, e11);
                                        throw th;
                                    }
                                }
                                throw th;
                            }
                        } catch (FileNotFoundException e12) {
                            e = e12;
                            bufferedReader = null;
                            fileReader2 = null;
                        } catch (IOException e13) {
                            e = e13;
                            bufferedReader = null;
                            fileReader = null;
                        } catch (Throwable th2) {
                            r2 = 0;
                            th = th2;
                            r6 = 0;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    r2 = zExists;
                    r6 = file;
                }
            } catch (IOException e14) {
                Log.e(TAG, TAG, e14);
            }
        }
        return null;
    }

    public static boolean deleteFile(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return deleteFile(new File(str));
    }
}
