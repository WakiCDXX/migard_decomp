package com.miui.powerkeeper.logsystem.utils;

import android.annotation.NonNull;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class LSUtils {
    public static final boolean CHECK_BY_CALC_ENABLED = false;
    public static final boolean DEBUG = true;
    private static final int DEFAULT_SHELL_CMD_TIMEOUT = 5;
    public static final boolean DEV = false;
    public static final long LOG_CLEAR_INTERVAL = 24;
    public static final long LOG_PRODUCE_INTERVAL = 360;
    public static final String LS_CLOUD_CONFIG = "log_system_config";
    public static final String LS_SP_NAME = "log_system_config";
    public static final String ONE_TRACK_EVENT = "log_system";
    public static final String PARAM_AVG_CURRENT = "avg_current";
    public static final String PARAM_SCENARIO_NAME = "scenario_name";
    public static final String TAG = "LS-LSUtils";
    public static final boolean USE_LOCAL_CONFIG = false;

    private LSUtils() {
    }

    /* JADX WARN: Code duplicated, block: B:55:0x00d6 A[Catch: IOException -> 0x00d2, TRY_LEAVE, TryCatch #5 {IOException -> 0x00d2, blocks: (B:51:0x00ce, B:55:0x00d6), top: B:82:0x00ce }] */
    /* JADX WARN: Code duplicated, block: B:66:0x00ec A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:67:0x00ee A[Catch: IOException -> 0x00ea, TRY_LEAVE, TryCatch #7 {IOException -> 0x00ea, blocks: (B:63:0x00e6, B:67:0x00ee), top: B:86:0x00e6 }] */
    /* JADX WARN: Code duplicated, block: B:71:0x00f7  */
    /* JADX WARN: Code duplicated, block: B:86:0x00e6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:99:? A[SYNTHETIC] */
    public static void execCommand(String str, String str2, boolean z2) throws Throwable {
        InputStream inputStream;
        Process process;
        DataOutputStream dataOutputStream;
        if (isEmptyString(str) || isEmptyString(str2)) {
            return;
        }
        DataOutputStream dataOutputStream2 = null;
        InputStream inputStream2 = null;
        Process process2 = null;
        dataOutputStream2 = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str2, true);
            try {
                Process processExec = Runtime.getRuntime().exec(z2 ? ShellUtils.COMMAND_SU : ShellUtils.COMMAND_SH);
                try {
                    dataOutputStream = new DataOutputStream(processExec.getOutputStream());
                    try {
                        dataOutputStream.write(str.getBytes());
                        dataOutputStream.writeBytes(ShellUtils.COMMAND_LINE_END);
                        dataOutputStream.flush();
                        dataOutputStream.writeBytes(ShellUtils.COMMAND_EXIT);
                        dataOutputStream.flush();
                        inputStream2 = processExec.getInputStream();
                        fileOutputStream.write(("\nCmd: " + str + "\nTime: " + getStringDate(System.currentTimeMillis()) + ShellUtils.COMMAND_LINE_END).getBytes());
                        byte[] bArr = new byte[4096];
                        while (true) {
                            int i2 = inputStream2.read(bArr);
                            if (i2 >= 0) {
                                fileOutputStream.write(bArr, 0, i2);
                            } else {
                                try {
                                    break;
                                } catch (Exception e2) {
                                    e = e2;
                                    process = processExec;
                                    inputStream = inputStream2;
                                    dataOutputStream2 = dataOutputStream;
                                } catch (Throwable th) {
                                    th = th;
                                    process = processExec;
                                    inputStream = inputStream2;
                                    dataOutputStream2 = dataOutputStream;
                                    if (dataOutputStream2 != null) {
                                        try {
                                            dataOutputStream2.close();
                                            if (inputStream != null) {
                                                inputStream.close();
                                            }
                                        } catch (IOException e3) {
                                            e3.printStackTrace();
                                            if (process != null) {
                                                throw th;
                                            }
                                            process.destroy();
                                            throw th;
                                        }
                                    } else if (inputStream != null) {
                                        inputStream.close();
                                    }
                                    if (process != null) {
                                        throw th;
                                    }
                                    process.destroy();
                                    throw th;
                                }
                            }
                            try {
                                e.printStackTrace();
                                if (dataOutputStream2 != null) {
                                    try {
                                        dataOutputStream2.close();
                                        if (inputStream != null) {
                                            inputStream.close();
                                        }
                                    } catch (IOException e4) {
                                        e4.printStackTrace();
                                    }
                                } else if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (process != null) {
                                    process.destroy();
                                    return;
                                }
                                return;
                            } catch (Throwable th2) {
                                th = th2;
                                if (dataOutputStream2 != null) {
                                    dataOutputStream2.close();
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                } else if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (process != null) {
                                    throw th;
                                }
                                process.destroy();
                                throw th;
                            }
                        }
                        fileOutputStream.close();
                        try {
                            dataOutputStream.close();
                            inputStream2.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                        processExec.destroy();
                    } catch (Throwable th3) {
                        th = th3;
                        InputStream inputStream3 = inputStream2;
                        process2 = processExec;
                        inputStream = inputStream3;
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th4) {
                            try {
                                th.addSuppressed(th4);
                            } catch (Exception e6) {
                                e = e6;
                                Process process3 = process2;
                                dataOutputStream2 = dataOutputStream;
                                process = process3;
                            } catch (Throwable th5) {
                                th = th5;
                                Process process4 = process2;
                                dataOutputStream2 = dataOutputStream;
                                process = process4;
                                if (dataOutputStream2 != null) {
                                    dataOutputStream2.close();
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                } else if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (process != null) {
                                    throw th;
                                }
                                process.destroy();
                                throw th;
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    dataOutputStream = null;
                    process2 = processExec;
                    inputStream = null;
                }
            } catch (Throwable th7) {
                th = th7;
                inputStream = null;
                dataOutputStream = null;
            }
        } catch (Exception e7) {
            e = e7;
            inputStream = null;
            process = null;
        } catch (Throwable th8) {
            th = th8;
            inputStream = null;
            process = null;
        }
    }

    public static void execShellCommand(String str, String str2) throws Throwable {
        execShellCommand(str, str2, 5);
    }

    /* JADX WARN: Code duplicated, block: B:60:0x00b7  */
    /* JADX WARN: Not initialized variable reg: 2, insn: 0x0064: MOVE (r1 I:??[OBJECT, ARRAY]) = (r2 I:??[OBJECT, ARRAY]) (LINE:101), block:B:29:0x0064 */
    public static String execShellCommandForResult(String str, int i2, String str2, String str3) throws Throwable {
        Process processExec;
        Process process;
        StringBuilder sb = new StringBuilder();
        Process process2 = null;
        try {
            try {
                processExec = Runtime.getRuntime().exec(str);
                try {
                    processExec.waitFor(i2, TimeUnit.SECONDS);
                    try {
                        InputStream inputStream = processExec.getInputStream();
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            try {
                                boolean z2 = false;
                                for (String line = bufferedReader.readLine(); line != null && (!z2 || !line.equals(str3)); line = bufferedReader.readLine()) {
                                    if (z2) {
                                        sb.append(line);
                                        sb.append(ShellUtils.COMMAND_LINE_END);
                                    } else if (line.trim().startsWith(str2)) {
                                        sb.append(line);
                                        sb.append(ShellUtils.COMMAND_LINE_END);
                                        z2 = true;
                                    }
                                }
                                String string = sb.toString();
                                bufferedReader.close();
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                processExec.destroy();
                                return string;
                            } catch (Throwable th) {
                                try {
                                    bufferedReader.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            }
                            throw th3;
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        processExec.destroy();
                        return null;
                    }
                } catch (Exception e3) {
                    e = e3;
                    Log.e(TAG, "// Exception from " + str + " : " + e.getMessage());
                    if (processExec != null) {
                        processExec.destroy();
                    }
                    return null;
                }
            } catch (Throwable th5) {
                th = th5;
                process2 = process;
                if (process2 != null) {
                    process2.destroy();
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            processExec = null;
        } catch (Throwable th6) {
            th = th6;
            if (process2 != null) {
                process2.destroy();
            }
            throw th;
        }
    }

    public static long getLongValue(String str) {
        try {
            return Long.valueOf(str).longValue();
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            return 0L;
        }
    }

    public static String getPkgNameByUid(Context context, int i2) {
        String[] packagesForUid = context.getPackageManager().getPackagesForUid(i2);
        if (packagesForUid == null || packagesForUid.length <= 0) {
            return null;
        }
        return packagesForUid[0];
    }

    public static String getStringDate(long j2) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(j2));
    }

    public static boolean isCurrentDevice(@NonNull String str) {
        if (isEmptyString(str)) {
            return false;
        }
        return str.equals(Build.DEVICE);
    }

    public static boolean isDeviceSupport(String str) {
        if (isEmptyString(str)) {
            return false;
        }
        for (String str2 : str.split(",")) {
            if (isCurrentDevice(str2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmptyString(String str) {
        return str == null || str.length() == 0;
    }

    public static String readFromDynamicFile(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        File file = new File(str);
        if (!file.exists()) {
            return null;
        }
        try {
            FileReader fileReader = new FileReader(file);
            try {
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                try {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            String string = sb.toString();
                            bufferedReader.close();
                            fileReader.close();
                            return string;
                        }
                        sb.append(line);
                        sb.append(ShellUtils.COMMAND_LINE_END);
                        try {
                            fileReader.close();
                        } catch (Throwable th) {
                            th.addSuppressed(th);
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                    throw th2;
                }
            } catch (Throwable th4) {
                fileReader.close();
                throw th4;
            }
        } catch (IOException e2) {
            Log.e(TAG, TAG, e2);
            return null;
        }
    }

    /* JADX WARN: Code duplicated, block: B:62:0x0073 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:66:0x007d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:81:? A[SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v2, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1, types: [java.io.FileReader] */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r7v10, types: [java.io.FileReader, java.io.Reader] */
    /* JADX WARN: Type inference failed for: r7v2, types: [int] */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v6 */
    /* JADX WARN: Type inference failed for: r7v7, types: [java.io.FileReader] */
    public static String readFromFile(String str, int i2, int i3) throws Throwable {
        ?? r1;
        BufferedReader bufferedReader;
        ?? r2 = 0;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (i3 <= 0) {
            i3 = Integer.MAX_VALUE;
        }
        if (i2 >= i3) {
            return null;
        }
        File file = new File(str);
        if (file.exists()) {
            ?? fileReader = (file.length() > 0L ? 1 : (file.length() == 0L ? 0 : -1));
            try {
                if (fileReader > 0) {
                    try {
                        fileReader = new FileReader(file);
                        try {
                            bufferedReader = new BufferedReader(fileReader);
                            try {
                                String internal = readInternal(bufferedReader, i2, i3);
                                try {
                                    fileReader.close();
                                } catch (IOException e2) {
                                    Log.e(TAG, TAG, e2);
                                }
                                try {
                                    bufferedReader.close();
                                } catch (IOException e3) {
                                    Log.e(TAG, TAG, e3);
                                }
                                return internal;
                            } catch (IOException e4) {
                                e = e4;
                                Log.e(TAG, TAG, e);
                                if (fileReader != 0) {
                                    try {
                                        fileReader.close();
                                    } catch (IOException e5) {
                                        Log.e(TAG, TAG, e5);
                                    }
                                }
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e6) {
                                        Log.e(TAG, TAG, e6);
                                    }
                                }
                                return null;
                            }
                        } catch (IOException e7) {
                            e = e7;
                            bufferedReader = null;
                        } catch (Throwable th) {
                            th = th;
                            file = null;
                            r2 = fileReader;
                            r1 = file;
                            if (r2 != 0) {
                                try {
                                    r2.close();
                                } catch (IOException e8) {
                                    Log.e(TAG, TAG, e8);
                                }
                            }
                            if (r1 != 0) {
                                throw th;
                            }
                            try {
                                r1.close();
                                throw th;
                            } catch (IOException e9) {
                                Log.e(TAG, TAG, e9);
                                throw th;
                            }
                        }
                    } catch (IOException e10) {
                        e = e10;
                        fileReader = 0;
                        bufferedReader = null;
                    } catch (Throwable th2) {
                        th = th2;
                        r1 = 0;
                        if (r2 != 0) {
                            r2.close();
                        }
                        if (r1 != 0) {
                            throw th;
                        }
                        r1.close();
                        throw th;
                    }
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }
        return null;
    }

    private static String readInternal(BufferedReader bufferedReader, int i2, int i3) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i4 = 0;
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            i4++;
            if (i4 >= i2) {
                sb.append(line);
                sb.append(ShellUtils.COMMAND_LINE_END);
                break;
            }
        }
        do {
            String line2 = bufferedReader.readLine();
            if (line2 == null) {
                break;
            }
            sb.append(line2);
            sb.append(ShellUtils.COMMAND_LINE_END);
            i4++;
        } while (i4 < i3);
        return sb.toString();
    }

    public static String readSysNodeInfo(String str) {
        File file = new File(str);
        if (!file.exists() || file.length() <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(str));
            boolean z2 = true;
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (z2) {
                        z2 = false;
                    } else {
                        sb.append(ShellUtils.COMMAND_LINE_END);
                    }
                    sb.append(line);
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
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return sb.toString();
    }

    /* JADX WARN: Code duplicated, block: B:42:0x0083  */
    /* JADX WARN: Code duplicated, block: B:51:0x0079 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:59:? A[SYNTHETIC] */
    public static void execShellCommand(String str, String str2, int i2) throws Throwable {
        Process processExec;
        InputStream inputStream;
        if (isEmptyString(str) || isEmptyString(str2)) {
            return;
        }
        InputStream inputStream2 = null;
        inputStream2 = null;
        inputStream2 = null;
        Process process = null;
        try {
            processExec = Runtime.getRuntime().exec(str);
            try {
                processExec.waitFor(i2, TimeUnit.SECONDS);
                inputStream2 = processExec.getInputStream();
                FileUtil.copyToFile(inputStream2, new File(str2));
                if (inputStream2 != null) {
                    try {
                        inputStream2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                processExec.destroy();
            } catch (Exception e3) {
                e = e3;
                inputStream = inputStream2;
                process = processExec;
                try {
                    Log.e(TAG, "// Exception from " + str + " : " + e.getMessage());
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    if (process != null) {
                        process.destroy();
                    }
                } catch (Throwable th) {
                    th = th;
                    processExec = process;
                    inputStream2 = inputStream;
                    if (inputStream2 != null) {
                        try {
                            inputStream2.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                    if (processExec != null) {
                        throw th;
                    }
                    processExec.destroy();
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                if (inputStream2 != null) {
                    inputStream2.close();
                }
                if (processExec != null) {
                    throw th;
                }
                processExec.destroy();
                throw th;
            }
        } catch (Exception e6) {
            e = e6;
            inputStream = null;
        } catch (Throwable th3) {
            th = th3;
            processExec = null;
        }
    }

    public static String readFromFile(String str, ArrayList<FileReadBoundary> arrayList) {
        String line;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        File file = new File(str);
        if (!file.exists() || file.length() <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(file);
            try {
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    try {
                        if (!isEmptyString(arrayList.get(i2).start)) {
                            while (true) {
                                String line2 = bufferedReader.readLine();
                                if (line2 == null) {
                                    break;
                                }
                                if (line2.trim().startsWith(arrayList.get(i2).start)) {
                                    sb.append(line2);
                                    sb.append(ShellUtils.COMMAND_LINE_END);
                                    break;
                                }
                            }
                        }
                        if (!isEmptyString(arrayList.get(i2).end)) {
                            while (true) {
                                line = bufferedReader.readLine();
                                if (line == null || line.trim().startsWith(arrayList.get(i2).end)) {
                                    break;
                                    break;
                                }
                                sb.append(line);
                                sb.append(ShellUtils.COMMAND_LINE_END);
                            }
                        } else {
                            while (true) {
                                line = bufferedReader.readLine();
                                if (line == null) {
                                    break;
                                }
                                sb.append(line);
                                sb.append(ShellUtils.COMMAND_LINE_END);
                            }
                        }
                        if (line == null) {
                            break;
                        }
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
                fileReader.close();
            } catch (Throwable th3) {
                try {
                    fileReader.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
                throw th3;
            }
        } catch (IOException e2) {
            Log.e(TAG, TAG, e2);
        }
        return sb.toString();
    }

    public static String readFromFile(String str, @NonNull String str2, @NonNull String str3) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FileReadBoundary(str2, str3));
        return readFromFile(str, (ArrayList<FileReadBoundary>) arrayList);
    }

    public static String readFromFile(String str, int i2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        File file = new File(str);
        if (file.exists() && file.length() > 0) {
            try {
                FileReader fileReader = new FileReader(file);
                try {
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    try {
                        StringBuilder sb = new StringBuilder();
                        LinkedList linkedList = new LinkedList();
                        while (true) {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            linkedList.add(line);
                            if (linkedList.size() > i2) {
                                linkedList.remove(0);
                            }
                            try {
                                fileReader.close();
                            } catch (Throwable th) {
                                th.addSuppressed(th);
                            }
                            throw th;
                        }
                        Iterator it = linkedList.iterator();
                        while (it.hasNext()) {
                            sb.append((String) it.next());
                            sb.append(ShellUtils.COMMAND_LINE_END);
                        }
                        String string = sb.toString();
                        bufferedReader.close();
                        fileReader.close();
                        return string;
                    } catch (Throwable th2) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                        }
                        throw th2;
                    }
                } catch (Throwable th4) {
                    fileReader.close();
                    throw th4;
                }
            } catch (IOException e2) {
                Log.e(TAG, TAG, e2);
            }
        }
        return null;
    }
}
