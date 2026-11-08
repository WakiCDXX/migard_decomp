package com.xiaomi.push.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channel.commonutils.file.FileUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.network.HostManager;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.ServiceConfig;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
class LogFilter {
    private static String MIPUSH_LOG_PATH = "/MiPushLog";
    private int mCurrentLen;
    private String mEndTime;
    private String mFromTime;
    private boolean mStartFound;

    @SuppressLint({"SimpleDateFormat"})
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int mMaxLen = LogType.LOG_TYPE_SAMPLING_NETWORK_MODE;
    private ArrayList<File> mFiles = new ArrayList<>();

    LogFilter() {
    }

    private void doFilter(BufferedReader bufferedReader, BufferedWriter bufferedWriter, Pattern pattern) throws IOException {
        char[] cArr = new char[4096];
        int i2 = bufferedReader.read(cArr);
        boolean z2 = false;
        while (i2 != -1 && !z2) {
            String str = new String(cArr, 0, i2);
            Matcher matcher = pattern.matcher(str);
            int i3 = 0;
            int i4 = 0;
            while (i3 < i2 && matcher.find(i3)) {
                int iStart = matcher.start();
                String strSubstring = str.substring(iStart, this.mFromTime.length() + iStart);
                if (this.mStartFound) {
                    if (strSubstring.compareTo(this.mEndTime) > 0) {
                        z2 = true;
                        i2 = iStart;
                        break;
                    }
                } else if (strSubstring.compareTo(this.mFromTime) >= 0) {
                    this.mStartFound = true;
                    i4 = iStart;
                }
                int iIndexOf = str.indexOf(10, iStart);
                if (iIndexOf == -1) {
                    iIndexOf = this.mFromTime.length();
                }
                i3 = iStart + iIndexOf;
            }
            if (this.mStartFound) {
                int i5 = i2 - i4;
                this.mCurrentLen += i5;
                if (z2) {
                    bufferedWriter.write(cArr, i4, i5);
                    return;
                } else {
                    bufferedWriter.write(cArr, i4, i5);
                    if (this.mCurrentLen > this.mMaxLen) {
                        return;
                    }
                }
            }
            i2 = bufferedReader.read(cArr);
        }
    }

    private void filter2File(File file) throws Throwable {
        BufferedReader bufferedReader;
        Pattern patternCompile = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        BufferedWriter bufferedWriter = null;
        bufferedReader = null;
        bufferedReader = null;
        bufferedReader = null;
        BufferedReader bufferedReader2 = null;
        bufferedWriter = null;
        bufferedWriter = null;
        bufferedWriter = null;
        try {
            try {
                BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                try {
                    bufferedWriter2.write("model :" + Build.MODEL + "; os :" + Build.VERSION.INCREMENTAL + "; uid :" + ServiceConfig.getDeviceUUID() + "; lng :" + Locale.getDefault().toString() + "; sdk :48; andver :" + Build.VERSION.SDK_INT + ShellUtils.COMMAND_LINE_END);
                    this.mCurrentLen = 0;
                    Iterator<File> it = this.mFiles.iterator();
                    while (it.hasNext()) {
                        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(it.next())));
                        try {
                            doFilter(bufferedReader, bufferedWriter2, patternCompile);
                            bufferedReader.close();
                            bufferedReader2 = bufferedReader;
                        } catch (FileNotFoundException e2) {
                            e = e2;
                            bufferedWriter = bufferedWriter2;
                            MyLog.v("LOG: filter error = " + e.getMessage());
                            IOUtils.closeQuietly(bufferedWriter);
                            IOUtils.closeQuietly(bufferedReader);
                            return;
                        } catch (IOException e3) {
                            e = e3;
                            bufferedWriter = bufferedWriter2;
                            MyLog.v("LOG: filter error = " + e.getMessage());
                            IOUtils.closeQuietly(bufferedWriter);
                            IOUtils.closeQuietly(bufferedReader);
                            return;
                        } catch (Throwable th) {
                            th = th;
                            bufferedWriter = bufferedWriter2;
                            IOUtils.closeQuietly(bufferedWriter);
                            IOUtils.closeQuietly(bufferedReader);
                            throw th;
                        }
                    }
                    bufferedWriter2.write(HostManager.getInstance().dump());
                    IOUtils.closeQuietly(bufferedWriter2);
                    IOUtils.closeQuietly(bufferedReader2);
                } catch (FileNotFoundException e4) {
                    e = e4;
                    bufferedReader = bufferedReader2;
                } catch (IOException e5) {
                    e = e5;
                    bufferedReader = bufferedReader2;
                } catch (Throwable th2) {
                    th = th2;
                    bufferedReader = bufferedReader2;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (FileNotFoundException e6) {
            e = e6;
            bufferedReader = null;
        } catch (IOException e7) {
            e = e7;
            bufferedReader = null;
        } catch (Throwable th4) {
            th = th4;
            bufferedReader = null;
        }
    }

    private void filterXmsfLog2File(Context context, File file) throws Throwable {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            Pattern patternCompile = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            BufferedReader bufferedReader = null;
            int i2 = 0;
            while (true) {
                try {
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(context.getContentResolver().openInputStream(Uri.parse("content://com.xiaomi.xmsf/log/" + i2))));
                    try {
                        doFilter(bufferedReader2, bufferedWriter, patternCompile);
                        bufferedReader2.close();
                        i2++;
                        bufferedReader = bufferedReader2;
                    } catch (FileNotFoundException | Exception unused) {
                        bufferedReader = bufferedReader2;
                        IOUtils.closeQuietly(bufferedReader);
                        IOUtils.closeQuietly(bufferedWriter);
                        return;
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader = bufferedReader2;
                        IOUtils.closeQuietly(bufferedReader);
                        IOUtils.closeQuietly(bufferedWriter);
                        throw th;
                    }
                } catch (FileNotFoundException | Exception unused2) {
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        } catch (FileNotFoundException unused3) {
        }
    }

    LogFilter addFile(File file) {
        if (file.exists()) {
            this.mFiles.add(file);
        }
        return this;
    }

    File filter(Context context, Date date, Date date2, File file) throws Throwable {
        File xmsfLogDir;
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equalsIgnoreCase(context.getPackageName())) {
            xmsfLogDir = LogAgent.getXmsfLogDir(context);
            if (xmsfLogDir == null) {
                return null;
            }
            addFile(new File(xmsfLogDir, "xmsf.log.1"));
            addFile(new File(xmsfLogDir, "xmsf.log"));
        } else {
            File file2 = new File(context.getFilesDir() + MIPUSH_LOG_PATH);
            if (!FileUtils.canWriteInternalFileDir(file2)) {
                return null;
            }
            addFile(new File(file2, "log0.txt"));
            addFile(new File(file2, "log1.txt"));
            xmsfLogDir = file2;
        }
        if (!xmsfLogDir.isDirectory()) {
            return null;
        }
        File file3 = new File(file, date.getTime() + Constants.ACCEPT_TIME_SEPARATOR_SERVER + date2.getTime() + PushConstants.UPLOAD_FILE_ZIP_POSTFIX);
        if (file3.exists()) {
            return null;
        }
        setRange(date, date2);
        long jCurrentTimeMillis = System.currentTimeMillis();
        File file4 = new File(file, "log.txt");
        filter2File(file4);
        MyLog.v("LOG: filter cost = " + (System.currentTimeMillis() - jCurrentTimeMillis));
        if (file4.exists()) {
            long jCurrentTimeMillis2 = System.currentTimeMillis();
            IOUtils.zip(file3, file4);
            MyLog.v("LOG: zip cost = " + (System.currentTimeMillis() - jCurrentTimeMillis2));
            file4.delete();
            if (file3.exists()) {
                return file3;
            }
        }
        return null;
    }

    void setMaxLen(int i2) {
        if (i2 != 0) {
            this.mMaxLen = i2;
        }
    }

    LogFilter setRange(Date date, Date date2) {
        if (date.after(date2)) {
            this.mFromTime = this.dateFormatter.format(date2);
            this.mEndTime = this.dateFormatter.format(date);
        } else {
            this.mFromTime = this.dateFormatter.format(date);
            this.mEndTime = this.dateFormatter.format(date2);
        }
        return this;
    }
}
