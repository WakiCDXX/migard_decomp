package miui.os;

import com.xiaomi.push.mpcd.Constants;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import miui.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class FileUtils {
    public static boolean addNoMedia(String str) {
        File file = new File(str);
        if (!file.isDirectory()) {
            return false;
        }
        try {
            return new File(file, ".nomedia").createNewFile();
        } catch (IOException unused) {
            return false;
        }
    }

    public static long checksumCrc32(File file) throws Throwable {
        CRC32 crc32 = new CRC32();
        CheckedInputStream checkedInputStream = null;
        try {
            CheckedInputStream checkedInputStream2 = new CheckedInputStream(new FileInputStream(file), crc32);
            try {
                while (checkedInputStream2.read(new byte[128]) >= 0) {
                }
                long value = crc32.getValue();
                try {
                    checkedInputStream2.close();
                } catch (IOException unused) {
                }
                return value;
            } catch (Throwable th) {
                th = th;
                checkedInputStream = checkedInputStream2;
                if (checkedInputStream != null) {
                    try {
                        checkedInputStream.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static boolean copyFile(File file, File file2) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                return copyToFile(fileInputStream, file2);
            } finally {
                fileInputStream.close();
            }
        } catch (IOException unused) {
            return false;
        }
    }

    public static boolean copyToFile(InputStream inputStream, File file) {
        try {
            if (file.exists() && !file.delete()) {
                return false;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                byte[] bArr = new byte[4096];
                while (true) {
                    int i2 = inputStream.read(bArr);
                    if (i2 < 0) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, i2);
                }
                fileOutputStream.flush();
                try {
                    fileOutputStream.getFD().sync();
                } catch (IOException unused) {
                }
                fileOutputStream.close();
                return true;
            } catch (Throwable th) {
                fileOutputStream.flush();
                try {
                    fileOutputStream.getFD().sync();
                } catch (IOException unused2) {
                }
                fileOutputStream.close();
                throw th;
            }
        } catch (IOException unused3) {
            return false;
        }
    }

    public static String getExtension(String str) {
        int iLastIndexOf;
        return (str == null || str.length() == 0 || (iLastIndexOf = str.lastIndexOf(46)) <= -1) ? "" : str.substring(iLastIndexOf + 1);
    }

    public static String getFileName(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        int iLastIndexOf = str.lastIndexOf(File.separatorChar);
        return iLastIndexOf > -1 ? str.substring(iLastIndexOf + 1) : str;
    }

    public static long getFolderSize(File file) {
        long folderSize = 0;
        if (!file.exists()) {
            return 0L;
        }
        if (!file.isDirectory()) {
            return file.length();
        }
        File[] fileArrListFiles = file.listFiles();
        if (fileArrListFiles != null) {
            for (File file2 : fileArrListFiles) {
                folderSize += getFolderSize(file2);
            }
        }
        return folderSize;
    }

    public static String getName(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        int iLastIndexOf = str.lastIndexOf(File.separatorChar);
        if (iLastIndexOf < 0) {
            iLastIndexOf = -1;
        }
        int iLastIndexOf2 = str.lastIndexOf(Constants.DOT_SEPARATOR);
        return iLastIndexOf2 < 0 ? str.substring(iLastIndexOf + 1) : str.substring(iLastIndexOf + 1, iLastIndexOf2);
    }

    public static String normalizeDirectoryName(String str) {
        if (str.charAt(str.length() - 1) == File.separatorChar) {
            return str;
        }
        return str + File.separator;
    }

    private static byte[] readFileAsBytes(String str) throws Throwable {
        RandomAccessFile randomAccessFile = null;
        try {
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(str, "r");
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) randomAccessFile2.length());
                byte[] bArr = new byte[DateUtils.FORMAT_ABBREV_WEEKDAY];
                while (true) {
                    int i2 = randomAccessFile2.read(bArr);
                    if (i2 == -1) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, i2);
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                try {
                    randomAccessFile2.close();
                } catch (IOException unused) {
                }
                return byteArray;
            } catch (Throwable th) {
                th = th;
                randomAccessFile = randomAccessFile2;
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static String readFileAsString(String str) throws IOException {
        return new String(readFileAsBytes(str), Charset.forName("UTF-8"));
    }

    public static String readTextFile(File file, int i2, String str) throws IOException {
        int i3;
        boolean z2;
        int i4;
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            long length = file.length();
            if (i2 > 0 || (length > 0 && i2 == 0)) {
                if (length > 0 && (i2 == 0 || length < i2)) {
                    i2 = (int) length;
                }
                byte[] bArr = new byte[i2 + 1];
                int i5 = fileInputStream.read(bArr);
                if (i5 <= 0) {
                    fileInputStream.close();
                    return "";
                }
                if (i5 <= i2) {
                    String str2 = new String(bArr, 0, i5);
                    fileInputStream.close();
                    return str2;
                }
                if (str == null) {
                    String str3 = new String(bArr, 0, i2);
                    fileInputStream.close();
                    return str3;
                }
                String str4 = new String(bArr, 0, i2) + str;
                fileInputStream.close();
                return str4;
            }
            if (i2 >= 0) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bArr2 = new byte[1024];
                do {
                    i3 = fileInputStream.read(bArr2);
                    if (i3 > 0) {
                        byteArrayOutputStream.write(bArr2, 0, i3);
                    }
                } while (i3 == 1024);
                String string = byteArrayOutputStream.toString();
                fileInputStream.close();
                return string;
            }
            byte[] bArr3 = null;
            byte[] bArr4 = null;
            boolean z3 = false;
            while (true) {
                z2 = true;
                if (bArr3 != null) {
                    z3 = true;
                }
                if (bArr3 == null) {
                    bArr3 = new byte[-i2];
                }
                i4 = fileInputStream.read(bArr3);
                if (i4 != bArr3.length) {
                    break;
                }
                byte[] bArr5 = bArr4;
                bArr4 = bArr3;
                bArr3 = bArr5;
            }
            if (bArr4 == null && i4 <= 0) {
                fileInputStream.close();
                return "";
            }
            if (bArr4 == null) {
                String str5 = new String(bArr3, 0, i4);
                fileInputStream.close();
                return str5;
            }
            if (i4 > 0) {
                System.arraycopy(bArr4, i4, bArr4, 0, bArr4.length - i4);
                System.arraycopy(bArr3, 0, bArr4, bArr4.length - i4, i4);
            } else {
                z2 = z3;
            }
            if (str != null && z2) {
                String str6 = str + new String(bArr4);
                fileInputStream.close();
                return str6;
            }
            String str7 = new String(bArr4);
            fileInputStream.close();
            return str7;
        } catch (Throwable th) {
            fileInputStream.close();
            throw th;
        }
    }

    public static void stringToFile(String str, String str2) throws IOException {
        FileWriter fileWriter = new FileWriter(str);
        try {
            fileWriter.write(str2);
        } finally {
            fileWriter.close();
        }
    }
}
