package com.xiaomi.channel.commonutils.file;

import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import miui.date.DateUtils;
import miui.util.HashUtils;

/* JADX INFO: loaded from: classes.dex */
public class IOUtils {
    private static final int BUFFER_SIZE = 1024;
    public static final String[] SUPPORTED_IMAGE_FORMATS = {"jpg", "png", "bmp", "gif", "webp"};

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception unused) {
            }
        }
    }

    public static void copyFile(File file, File file2) throws Throwable {
        FileOutputStream fileOutputStream;
        if (file.getAbsolutePath().equals(file2.getAbsolutePath())) {
            return;
        }
        FileInputStream fileInputStream = null;
        try {
            FileInputStream fileInputStream2 = new FileInputStream(file);
            try {
                fileOutputStream = new FileOutputStream(file2);
                try {
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int i2 = fileInputStream2.read(bArr);
                        if (i2 < 0) {
                            fileInputStream2.close();
                            fileOutputStream.close();
                            return;
                        }
                        fileOutputStream.write(bArr, 0, i2);
                    }
                } catch (Throwable th) {
                    th = th;
                    fileInputStream = fileInputStream2;
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                fileOutputStream = null;
            }
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream = null;
        }
    }

    public static boolean createFileQuietly(File file) {
        try {
            if (file.isDirectory()) {
                return false;
            }
            if (file.exists()) {
                return true;
            }
            File parentFile = file.getParentFile();
            if (parentFile.exists() || parentFile.mkdirs()) {
                return file.createNewFile();
            }
            return false;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    public static void deleteDirs(File file) {
        MyLog.v("deleteDirs filePath = " + file.getAbsolutePath());
        if (file.isDirectory()) {
            File[] fileArrListFiles = file.listFiles();
            if (fileArrListFiles != null && fileArrListFiles.length > 0) {
                for (File file2 : fileArrListFiles) {
                    if (file2.isFile()) {
                        file2.delete();
                    } else {
                        deleteDirs(file2);
                    }
                }
            }
            file.delete();
        }
    }

    public static String fileToStr(File file) throws Throwable {
        InputStreamReader inputStreamReader;
        StringWriter stringWriter = new StringWriter();
        InputStreamReader inputStreamReader2 = null;
        try {
            inputStreamReader = new InputStreamReader(new BufferedInputStream(new FileInputStream(file)));
            try {
                try {
                    char[] cArr = new char[2048];
                    while (true) {
                        int i2 = inputStreamReader.read(cArr);
                        if (i2 == -1) {
                            String string = stringWriter.toString();
                            closeQuietly(inputStreamReader);
                            closeQuietly(stringWriter);
                            return string;
                        }
                        stringWriter.write(cArr, 0, i2);
                        th = th;
                        inputStreamReader2 = inputStreamReader;
                        closeQuietly(inputStreamReader2);
                        closeQuietly(stringWriter);
                        throw th;
                    }
                } catch (IOException e2) {
                    e = e2;
                    MyLog.v("read file :" + file.getAbsolutePath() + " failure :" + e.getMessage());
                    closeQuietly(inputStreamReader);
                    closeQuietly(stringWriter);
                    return null;
                }
            } catch (Throwable th) {
                th = th;
                inputStreamReader2 = inputStreamReader;
            }
        } catch (IOException e3) {
            e = e3;
            inputStreamReader = null;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static byte[] gZip(byte[] bArr) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(bArr);
            gZIPOutputStream.finish();
            gZIPOutputStream.close();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return byteArray;
        } catch (Exception unused) {
            return bArr;
        }
    }

    public static byte[] getFileMD5Digest(String str) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
        FileInputStream fileInputStream = new FileInputStream(new File(str));
        byte[] bArr = new byte[4096];
        while (true) {
            int i2 = fileInputStream.read(bArr);
            if (i2 != -1) {
                messageDigest.update(bArr, 0, i2);
            } else {
                try {
                    break;
                } catch (IOException e2) {
                    MyLog.e(e2);
                }
            }
        }
        fileInputStream.close();
        return messageDigest.digest();
    }

    public static byte[] getFileSha1Digest(String str) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.SHA1);
        FileInputStream fileInputStream = new FileInputStream(new File(str));
        byte[] bArr = new byte[4096];
        while (true) {
            int i2 = fileInputStream.read(bArr);
            if (i2 != -1) {
                messageDigest.update(bArr, 0, i2);
            } else {
                try {
                    break;
                } catch (IOException e2) {
                    MyLog.e(e2);
                }
            }
        }
        fileInputStream.close();
        return messageDigest.digest();
    }

    public static String getFileSuffix(String str) {
        int iLastIndexOf = str.lastIndexOf(46);
        return iLastIndexOf > 0 ? str.substring(iLastIndexOf + 1) : "";
    }

    public static void hideFromMediaScanner(File file) {
        File file2 = new File(file, ".nomedia");
        if (file2.exists() && file2.isFile()) {
            return;
        }
        try {
            file2.createNewFile();
        } catch (IOException e2) {
            MyLog.e(e2);
        }
    }

    public static boolean isSupportImageSuffix(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        for (String str2 : SUPPORTED_IMAGE_FORMATS) {
            if (str2.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    public static byte[] readInputStream(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[DateUtils.FORMAT_ABBREV_WEEKDAY];
        while (true) {
            try {
                try {
                    int i2 = inputStream.read(bArr, 0, DateUtils.FORMAT_ABBREV_WEEKDAY);
                    if (i2 <= 0) {
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        closeQuietly(inputStream);
                        closeQuietly(byteArrayOutputStream);
                        return byteArray;
                    }
                    byteArrayOutputStream.write(bArr, 0, i2);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    closeQuietly(inputStream);
                    closeQuietly(byteArrayOutputStream);
                    return null;
                }
            } catch (Throwable th) {
                closeQuietly(inputStream);
                closeQuietly(byteArrayOutputStream);
                throw th;
            }
        }
    }

    public static void remove(File file) {
        if (!file.isDirectory()) {
            if (file.exists()) {
                file.delete();
            }
        } else {
            for (File file2 : file.listFiles()) {
                remove(file2);
            }
            file.delete();
        }
    }

    public static void strToFile(File file, String str) throws Throwable {
        if (!file.exists()) {
            MyLog.v("mkdir " + file.getAbsolutePath());
            file.getParentFile().mkdirs();
        }
        BufferedWriter bufferedWriter = null;
        try {
            try {
                BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                try {
                    bufferedWriter2.write(str);
                    closeQuietly(bufferedWriter2);
                } catch (IOException e2) {
                    e = e2;
                    bufferedWriter = bufferedWriter2;
                    MyLog.v("write file :" + file.getAbsolutePath() + " failure :" + e.getMessage());
                    closeQuietly(bufferedWriter);
                } catch (Throwable th) {
                    th = th;
                    bufferedWriter = bufferedWriter2;
                    closeQuietly(bufferedWriter);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e3) {
            e = e3;
        }
    }

    public static boolean unZip(String str, String str2) {
        if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str)) {
            if (!str2.endsWith("/")) {
                str2 = str2 + "/";
            }
            try {
                ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(str)));
                byte[] bArr = new byte[4096];
                while (true) {
                    ZipEntry nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry == null) {
                        zipInputStream.close();
                        return true;
                    }
                    String name = nextEntry.getName();
                    File file = new File(str2 + name);
                    if (!name.endsWith("/")) {
                        File file2 = new File(file.getParent());
                        if (!file2.exists() || !file2.isDirectory()) {
                            file2.mkdirs();
                            hideFromMediaScanner(file2);
                        }
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file), 4096);
                        while (true) {
                            int i2 = zipInputStream.read(bArr, 0, 4096);
                            if (i2 == -1) {
                                break;
                            }
                            bufferedOutputStream.write(bArr, 0, i2);
                        }
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                    }
                }
            } catch (IOException e2) {
                MyLog.e("瑙ｅ��缂╁け璐ワ��锛�锛�", e2);
            }
        }
        return false;
    }

    public static void zip(File file, File file2) throws Throwable {
        ZipOutputStream zipOutputStream = null;
        try {
            try {
                ZipOutputStream zipOutputStream2 = new ZipOutputStream(new FileOutputStream(file, false));
                try {
                    zip(zipOutputStream2, file2, null, null);
                    closeQuietly(zipOutputStream2);
                } catch (FileNotFoundException unused) {
                    zipOutputStream = zipOutputStream2;
                    closeQuietly(zipOutputStream);
                } catch (IOException e2) {
                    e = e2;
                    zipOutputStream = zipOutputStream2;
                    MyLog.w("zip file failure + " + e.getMessage());
                    closeQuietly(zipOutputStream);
                } catch (Throwable th) {
                    th = th;
                    zipOutputStream = zipOutputStream2;
                    closeQuietly(zipOutputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (FileNotFoundException unused2) {
        } catch (IOException e3) {
            e = e3;
        }
    }

    public static void zip(ZipOutputStream zipOutputStream, File file, String str, FileFilter fileFilter) throws Throwable {
        File[] fileArrListFiles;
        String str2 = "";
        if (str == null) {
            str = "";
        }
        FileInputStream fileInputStream = null;
        try {
            try {
                if (file.isDirectory()) {
                    if (fileFilter != null) {
                        fileArrListFiles = file.listFiles(fileFilter);
                    } else {
                        fileArrListFiles = file.listFiles();
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    String str3 = File.separator;
                    sb.append(str3);
                    zipOutputStream.putNextEntry(new ZipEntry(sb.toString()));
                    if (!TextUtils.isEmpty(str)) {
                        str2 = str + str3;
                    }
                    for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                        zip(zipOutputStream, fileArrListFiles[i2], str2 + fileArrListFiles[i2].getName(), null);
                    }
                    File[] fileArrListFiles2 = file.listFiles(new FileFilter() { // from class: com.xiaomi.channel.commonutils.file.IOUtils.1
                        @Override // java.io.FileFilter
                        public boolean accept(File file2) {
                            return file2.isDirectory();
                        }
                    });
                    if (fileArrListFiles2 != null) {
                        for (File file2 : fileArrListFiles2) {
                            zip(zipOutputStream, file2, str2 + File.separator + file2.getName(), fileFilter);
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(str)) {
                        zipOutputStream.putNextEntry(new ZipEntry(str));
                    } else {
                        zipOutputStream.putNextEntry(new ZipEntry(String.valueOf(new Date().getTime()) + ".txt"));
                    }
                    FileInputStream fileInputStream2 = new FileInputStream(file);
                    try {
                        byte[] bArr = new byte[1024];
                        while (true) {
                            int i3 = fileInputStream2.read(bArr);
                            if (i3 == -1) {
                                break;
                            } else {
                                zipOutputStream.write(bArr, 0, i3);
                            }
                        }
                        fileInputStream = fileInputStream2;
                    } catch (IOException e2) {
                        e = e2;
                        fileInputStream = fileInputStream2;
                        MyLog.e("zipFiction failed with exception:" + e.toString());
                    } catch (Throwable th) {
                        th = th;
                        fileInputStream = fileInputStream2;
                        closeQuietly(fileInputStream);
                        throw th;
                    }
                }
            } catch (IOException e3) {
                e = e3;
            }
            closeQuietly(fileInputStream);
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static void zip(ZipOutputStream zipOutputStream, String str, InputStream inputStream) {
        try {
            if (!TextUtils.isEmpty(str)) {
                zipOutputStream.putNextEntry(new ZipEntry(str));
            } else {
                zipOutputStream.putNextEntry(new ZipEntry(String.valueOf(new Date().getTime()) + ".txt"));
            }
            byte[] bArr = new byte[1024];
            while (true) {
                int i2 = inputStream.read(bArr);
                if (i2 == -1) {
                    return;
                } else {
                    zipOutputStream.write(bArr, 0, i2);
                }
            }
        } catch (IOException e2) {
            MyLog.e("zipFiction failed with exception:" + e2.toString());
        }
    }
}
