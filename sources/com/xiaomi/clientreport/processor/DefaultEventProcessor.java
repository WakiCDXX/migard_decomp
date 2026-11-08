package com.xiaomi.clientreport.processor;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;
import com.xiaomi.channel.commonutils.android.DataCryptUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ByteUtils;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.clientreport.data.BaseClientReport;
import com.xiaomi.clientreport.data.EventClientReport;
import com.xiaomi.clientreport.manager.ClientReportLogicManager;
import com.xiaomi.clientreport.util.ClientReportUtil;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/* JADX INFO: loaded from: classes.dex */
public class DefaultEventProcessor implements IEventProcessor {
    private static final int DATA_FILE_MAX_SIZE = 5242880;
    private static final int DATA_MAX_SIZE = 4096;
    private static final String FOLDER = "event";
    private static final int MAGIC_NUMBER = -573785174;
    private static final int MAX_SAME_PRODUCTION_FILE_NUM = 100;
    private static final String UPLOAD_FOLDER = "eventUploading";
    protected Context mContext;
    private HashMap<String, ArrayList<BaseClientReport>> mEventMap;

    public DefaultEventProcessor(Context context) {
        setContext(context);
    }

    public static String getFirstEventFileName(BaseClientReport baseClientReport) {
        return String.valueOf(baseClientReport.production);
    }

    private String getWriteFileName(BaseClientReport baseClientReport) {
        File file = new File(this.mContext.getFilesDir(), "event");
        String str = file.getAbsolutePath() + File.separator + getFirstEventFileName(baseClientReport);
        for (int i2 = 0; i2 < 100; i2++) {
            String str2 = str + i2;
            if (ClientReportUtil.isFileCanBeUse(this.mContext, str2)) {
                return str2;
            }
        }
        return null;
    }

    private List<String> readFile(String str) throws Throwable {
        ArrayList arrayList = new ArrayList();
        byte[] bArr = new byte[4];
        byte[] bArr2 = new byte[4];
        FileInputStream fileInputStream = null;
        try {
            try {
                FileInputStream fileInputStream2 = new FileInputStream(new File(str));
                while (true) {
                    try {
                        int i2 = fileInputStream2.read(bArr);
                        if (i2 == -1) {
                            break;
                        }
                        if (i2 == 4) {
                            if (ByteUtils.toInt(bArr) == MAGIC_NUMBER) {
                                int i3 = fileInputStream2.read(bArr2);
                                if (i3 == -1) {
                                    break;
                                }
                                if (i3 != 4) {
                                    MyLog.e("eventData read from cache file failed cause lengthBuffer error");
                                    break;
                                }
                                int i4 = ByteUtils.toInt(bArr2);
                                if (i4 < 1 || i4 > 4096) {
                                    MyLog.e("eventData read from cache file failed cause lengthBuffer < 1 || lengthBuffer > 4K");
                                    break;
                                }
                                byte[] bArr3 = new byte[i4];
                                if (fileInputStream2.read(bArr3) != i4) {
                                    MyLog.e("eventData read from cache file failed cause buffer size not equal length");
                                    break;
                                }
                                String strBytesToString = bytesToString(bArr3);
                                if (!TextUtils.isEmpty(strBytesToString)) {
                                    arrayList.add(strBytesToString);
                                }
                            } else {
                                MyLog.e("eventData read from cache file failed because magicNumber error");
                                break;
                            }
                        } else {
                            MyLog.e("eventData read from cache file failed because magicNumber error");
                            break;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        fileInputStream = fileInputStream2;
                        MyLog.e(e);
                        IOUtils.closeQuietly(fileInputStream);
                    } catch (Throwable th) {
                        th = th;
                        fileInputStream = fileInputStream2;
                        IOUtils.closeQuietly(fileInputStream);
                        throw th;
                    }
                }
                IOUtils.closeQuietly(fileInputStream2);
            } catch (Exception e3) {
                e = e3;
            }
            return arrayList;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void releaseLock(RandomAccessFile randomAccessFile, FileLock fileLock) {
        if (fileLock != null && fileLock.isValid()) {
            try {
                fileLock.release();
            } catch (IOException e2) {
                MyLog.e(e2);
            }
        }
        IOUtils.closeQuietly(randomAccessFile);
    }

    private void reportDropFile(String str, String str2) {
        EventClientReport eventClientReportNewEvent = ClientReportLogicManager.getInstance(this.mContext).newEvent(5001, "24:" + str + "," + str2);
        ArrayList arrayList = new ArrayList();
        arrayList.add(eventClientReportNewEvent.toJsonString());
        send(arrayList);
    }

    private BaseClientReport[] write2FileLocked(BaseClientReport[] baseClientReportArr) throws Throwable {
        FileLock fileLockLock;
        RandomAccessFile randomAccessFile;
        BufferedOutputStream bufferedOutputStream;
        String writeFileName = getWriteFileName(baseClientReportArr[0]);
        BufferedOutputStream bufferedOutputStream2 = null;
        if (TextUtils.isEmpty(writeFileName)) {
            return null;
        }
        try {
            File file = new File(writeFileName + ".lock");
            IOUtils.createFileQuietly(file);
            randomAccessFile = new RandomAccessFile(file, "rw");
            try {
                fileLockLock = randomAccessFile.getChannel().lock();
                try {
                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(writeFileName), true));
                    try {
                        try {
                            int i2 = 0;
                            for (BaseClientReport baseClientReport : baseClientReportArr) {
                                if (baseClientReport != null) {
                                    byte[] bArrStringToBytes = stringToBytes(baseClientReport.toJsonString());
                                    if (bArrStringToBytes == null || bArrStringToBytes.length < 1 || bArrStringToBytes.length > 4096) {
                                        MyLog.e("event data throw a invalid item ");
                                    } else {
                                        if (!ClientReportUtil.isFileCanBeUse(this.mContext, writeFileName)) {
                                            int length = baseClientReportArr.length - i2;
                                            BaseClientReport[] baseClientReportArr2 = new BaseClientReport[length];
                                            System.arraycopy(baseClientReportArr, i2, baseClientReportArr2, 0, length);
                                            IOUtils.closeQuietly(bufferedOutputStream);
                                            releaseLock(randomAccessFile, fileLockLock);
                                            return baseClientReportArr2;
                                        }
                                        bufferedOutputStream.write(ByteUtils.parseInt(MAGIC_NUMBER));
                                        bufferedOutputStream.write(ByteUtils.parseInt(bArrStringToBytes.length));
                                        bufferedOutputStream.write(bArrStringToBytes);
                                        bufferedOutputStream.flush();
                                        i2++;
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                            MyLog.e("event data write to cache file failed cause exception", e);
                        }
                    } catch (Throwable th) {
                        th = th;
                        bufferedOutputStream2 = bufferedOutputStream;
                        IOUtils.closeQuietly(bufferedOutputStream2);
                        releaseLock(randomAccessFile, fileLockLock);
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                    bufferedOutputStream = null;
                } catch (Throwable th2) {
                    th = th2;
                    IOUtils.closeQuietly(bufferedOutputStream2);
                    releaseLock(randomAccessFile, fileLockLock);
                    throw th;
                }
            } catch (Exception e4) {
                e = e4;
                fileLockLock = null;
                bufferedOutputStream = null;
            } catch (Throwable th3) {
                th = th3;
                fileLockLock = null;
            }
        } catch (Exception e5) {
            e = e5;
            fileLockLock = null;
            randomAccessFile = null;
            bufferedOutputStream = null;
        } catch (Throwable th4) {
            th = th4;
            fileLockLock = null;
            randomAccessFile = null;
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        releaseLock(randomAccessFile, fileLockLock);
        return null;
    }

    @Override // com.xiaomi.clientreport.processor.IEventProcessor
    public String bytesToString(byte[] bArr) {
        byte[] key;
        if (bArr != null && bArr.length >= 1) {
            if (!ClientReportLogicManager.getInstance(this.mContext).getConfig().isEventEncrypted()) {
                return XMStringUtils.bytesToString(bArr);
            }
            String eventKeyWithDefault = ClientReportUtil.getEventKeyWithDefault(this.mContext);
            if (!TextUtils.isEmpty(eventKeyWithDefault) && (key = ClientReportUtil.parseKey(eventKeyWithDefault)) != null && key.length > 0) {
                try {
                    return XMStringUtils.bytesToString(Base64.decode(DataCryptUtils.mipushDecrypt(key, bArr), 2));
                } catch (InvalidAlgorithmParameterException e2) {
                    MyLog.e(e2);
                } catch (InvalidKeyException e3) {
                    MyLog.e(e3);
                } catch (NoSuchAlgorithmException e4) {
                    MyLog.e(e4);
                } catch (BadPaddingException e5) {
                    MyLog.e(e5);
                } catch (IllegalBlockSizeException e6) {
                    MyLog.e(e6);
                } catch (NoSuchPaddingException e7) {
                    MyLog.e(e7);
                }
            }
        }
        return null;
    }

    @Override // com.xiaomi.clientreport.processor.IWrite
    public void preProcess(BaseClientReport baseClientReport) {
        if ((baseClientReport instanceof EventClientReport) && this.mEventMap != null) {
            EventClientReport eventClientReport = (EventClientReport) baseClientReport;
            String firstEventFileName = getFirstEventFileName(eventClientReport);
            ArrayList<BaseClientReport> arrayList = this.mEventMap.get(firstEventFileName);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
            }
            arrayList.add(eventClientReport);
            this.mEventMap.put(firstEventFileName, arrayList);
        }
    }

    @Override // com.xiaomi.clientreport.processor.IWrite
    public void process() throws Throwable {
        HashMap<String, ArrayList<BaseClientReport>> map = this.mEventMap;
        if (map == null) {
            return;
        }
        if (map.size() > 0) {
            Iterator<String> it = this.mEventMap.keySet().iterator();
            while (it.hasNext()) {
                ArrayList<BaseClientReport> arrayList = this.mEventMap.get(it.next());
                if (arrayList != null && arrayList.size() > 0) {
                    BaseClientReport[] baseClientReportArr = new BaseClientReport[arrayList.size()];
                    arrayList.toArray(baseClientReportArr);
                    write(baseClientReportArr);
                }
            }
        }
        this.mEventMap.clear();
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0037 A[PHI: r2 r3 r4
      0x0037: PHI (r2v11 java.nio.channels.FileLock) = (r2v4 java.nio.channels.FileLock), (r2v2 java.nio.channels.FileLock), (r2v2 java.nio.channels.FileLock) binds: [B:68:0x010b, B:32:0x0092, B:19:0x0035] A[DONT_GENERATE, DONT_INLINE]
      0x0037: PHI (r3v15 java.io.RandomAccessFile) = (r3v4 java.io.RandomAccessFile), (r3v2 java.io.RandomAccessFile), (r3v2 java.io.RandomAccessFile) binds: [B:68:0x010b, B:32:0x0092, B:19:0x0035] A[DONT_GENERATE, DONT_INLINE]
      0x0037: PHI (r4v9 java.io.File) = (r4v3 java.io.File), (r4v1 java.io.File), (r4v1 java.io.File) binds: [B:68:0x010b, B:32:0x0092, B:19:0x0035] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // com.xiaomi.clientreport.processor.IDataSend
    public void readAndSend() throws Throwable {
        ClientReportUtil.moveFiles(this.mContext, "event", UPLOAD_FOLDER);
        File[] readFileName = ClientReportUtil.getReadFileName(this.mContext, UPLOAD_FOLDER);
        if (readFileName == null || readFileName.length <= 0) {
            return;
        }
        FileLock fileLockLock = null;
        File file = null;
        RandomAccessFile randomAccessFile = null;
        for (File file2 : readFileName) {
            if (file2 == null) {
                if (fileLockLock != null && fileLockLock.isValid()) {
                    try {
                        fileLockLock.release();
                    } catch (IOException e2) {
                        MyLog.e(e2);
                    }
                }
                IOUtils.closeQuietly(randomAccessFile);
                if (file != null) {
                    file.delete();
                }
            } else {
                try {
                    try {
                        if (file2.length() > 5242880) {
                            MyLog.e("eventData read from cache file failed because " + file2.getName() + " is too big, length " + file2.length());
                            reportDropFile(file2.getName(), Formatter.formatFileSize(this.mContext, file2.length()));
                            file2.delete();
                            if (fileLockLock != null && fileLockLock.isValid()) {
                                try {
                                    fileLockLock.release();
                                } catch (IOException e3) {
                                    MyLog.e(e3);
                                }
                            }
                            IOUtils.closeQuietly(randomAccessFile);
                            if (file != null) {
                                file.delete();
                            }
                        } else {
                            String absolutePath = file2.getAbsolutePath();
                            File file3 = new File(absolutePath + ".lock");
                            try {
                                IOUtils.createFileQuietly(file3);
                                RandomAccessFile randomAccessFile2 = new RandomAccessFile(file3, "rw");
                                try {
                                    fileLockLock = randomAccessFile2.getChannel().lock();
                                    send(readFile(absolutePath));
                                    file2.delete();
                                    if (fileLockLock != null && fileLockLock.isValid()) {
                                        try {
                                            fileLockLock.release();
                                        } catch (IOException e4) {
                                            MyLog.e(e4);
                                        }
                                    }
                                    IOUtils.closeQuietly(randomAccessFile2);
                                    file3.delete();
                                    randomAccessFile = randomAccessFile2;
                                    file = file3;
                                } catch (Exception e5) {
                                    e = e5;
                                    randomAccessFile = randomAccessFile2;
                                    file = file3;
                                    MyLog.e(e);
                                    if (fileLockLock != null && fileLockLock.isValid()) {
                                        try {
                                            fileLockLock.release();
                                        } catch (IOException e6) {
                                            MyLog.e(e6);
                                        }
                                    }
                                    IOUtils.closeQuietly(randomAccessFile);
                                    if (file != null) {
                                        file.delete();
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    randomAccessFile = randomAccessFile2;
                                    file = file3;
                                    if (fileLockLock != null && fileLockLock.isValid()) {
                                        try {
                                            fileLockLock.release();
                                        } catch (IOException e7) {
                                            MyLog.e(e7);
                                        }
                                    }
                                    IOUtils.closeQuietly(randomAccessFile);
                                    if (file == null) {
                                        throw th;
                                    }
                                    file.delete();
                                    throw th;
                                }
                            } catch (Exception e8) {
                                e = e8;
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                    }
                } catch (Exception e9) {
                    e = e9;
                }
            }
        }
    }

    @Override // com.xiaomi.clientreport.processor.IDataSend
    public void send(List<String> list) {
        ClientReportUtil.sendFile(this.mContext, list);
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    @Override // com.xiaomi.clientreport.processor.IEventProcessor
    public void setEventMap(HashMap<String, ArrayList<BaseClientReport>> map) {
        this.mEventMap = map;
    }

    @Override // com.xiaomi.clientreport.processor.IEventProcessor
    public byte[] stringToBytes(String str) {
        byte[] key;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (!ClientReportLogicManager.getInstance(this.mContext).getConfig().isEventEncrypted()) {
            return XMStringUtils.getBytes(str);
        }
        String eventKeyWithDefault = ClientReportUtil.getEventKeyWithDefault(this.mContext);
        byte[] bytes = XMStringUtils.getBytes(str);
        if (!TextUtils.isEmpty(eventKeyWithDefault) && bytes != null && bytes.length > 1 && (key = ClientReportUtil.parseKey(eventKeyWithDefault)) != null) {
            try {
                if (key.length > 1) {
                    return DataCryptUtils.mipushEncrypt(key, Base64.encode(bytes, 2));
                }
            } catch (Exception e2) {
                MyLog.e(e2);
            }
        }
        return null;
    }

    @Override // com.xiaomi.clientreport.processor.IWrite
    public void write(BaseClientReport[] baseClientReportArr) throws Throwable {
        if (baseClientReportArr == null || baseClientReportArr.length == 0 || baseClientReportArr[0] == null) {
            MyLog.w("event data write to cache file failed because data null");
            return;
        }
        do {
            baseClientReportArr = write2FileLocked(baseClientReportArr);
            if (baseClientReportArr == null || baseClientReportArr.length <= 0) {
                return;
            }
        } while (baseClientReportArr[0] != null);
    }
}
