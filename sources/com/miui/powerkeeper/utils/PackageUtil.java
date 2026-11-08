package com.miui.powerkeeper.utils;

import android.app.ActivityThread;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerCompat;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.collect.Lists;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import miui.content.pm.PreloadedAppPolicy;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;

/* JADX INFO: loaded from: classes.dex */
public class PackageUtil {
    private static final String TAG = "PackageUtil";

    public static boolean checkPackageEnabled(Context context, String str) {
        try {
            int applicationEnabledSetting = context.getPackageManager().getApplicationEnabledSetting(str);
            return (applicationEnabledSetting == 2 || applicationEnabledSetting == 3) ? false : true;
        } catch (IllegalArgumentException unused) {
        }
    }

    public static boolean checkPackageInstalled(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static List<ApplicationInfo> getAllApps(Context context) {
        try {
            return context.getPackageManager().getInstalledApplications(0);
        } catch (Exception e2) {
            Log.e(TAG, "getAllApps", e2);
            return Lists.newArrayList();
        }
    }

    public static String getAppVersion(PackageManager packageManager, String str) {
        try {
            return packageManager.getPackageInfo(str, 8768).versionName;
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e(TAG, "getAppVersion", e2);
            return null;
        }
    }

    public static CharSequence getLabelByPackageName(Context context, String str) {
        ApplicationInfo applicationInfo;
        CharSequence charSequenceLoadLabel;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
            return (packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null || (charSequenceLoadLabel = applicationInfo.loadLabel(packageManager)) == null) ? str : charSequenceLoadLabel;
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e(TAG, "getLabelByPackageName", e2);
        }
    }

    /* JADX WARN: Code duplicated, block: B:48:0x009a  */
    /* JADX WARN: Code duplicated, block: B:50:0x00a0  */
    /* JADX WARN: Code duplicated, block: B:69:0x0049 A[EXC_TOP_SPLITTER, PHI: r1 r4
      0x0049: PHI (r1v5 java.io.FileInputStream) = (r1v7 java.io.FileInputStream), (r1v8 java.io.FileInputStream), (r1v8 java.io.FileInputStream) binds: [B:40:0x0077, B:64:0x0049, B:16:0x0046] A[DONT_GENERATE, DONT_INLINE]
      0x0049: PHI (r4v1 miui.process.RunningProcessInfo) = 
      (r4v6 miui.process.RunningProcessInfo)
      (r4v11 miui.process.RunningProcessInfo)
      (r4v11 miui.process.RunningProcessInfo)
     binds: [B:40:0x0077, B:64:0x0049, B:16:0x0046] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:73:0x0074 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:79:0x006f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:87:0x007c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:97:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    public static String getPackageNameByPid(Context context, int i2) throws Throwable {
        FileInputStream fileInputStream;
        RunningProcessInfo inputStreamReader;
        RunningProcessInfo bufferedReader;
        RunningProcessInfo line;
        int iIndexOf;
        RunningProcessInfo runningProcessInfoByPid = null;
        try {
            fileInputStream = new FileInputStream(new File("/proc/" + i2 + "/cmdline"));
            try {
                inputStreamReader = new InputStreamReader(fileInputStream);
                try {
                    bufferedReader = new BufferedReader(inputStreamReader);
                    try {
                        try {
                            line = bufferedReader.readLine();
                            if (line != null) {
                                try {
                                    line = line.replace((char) 0, ' ').trim();
                                } catch (Exception e2) {
                                    e = e2;
                                    Log.e(TAG, "getPackageNameByPid", e);
                                    if (bufferedReader != null) {
                                        try {
                                            bufferedReader.close();
                                        } catch (IOException unused) {
                                        }
                                    }
                                    if (inputStreamReader != null) {
                                        try {
                                            inputStreamReader.close();
                                        } catch (IOException unused2) {
                                        }
                                    }
                                    if (fileInputStream != null) {
                                    }
                                    if (line == null) {
                                        try {
                                            runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(i2);
                                        } catch (Exception e3) {
                                            Log.e(TAG, "getPackageNameByPid pid=" + i2, e3);
                                        }
                                        if (runningProcessInfoByPid != null) {
                                            line = runningProcessInfoByPid.mPkgList[0];
                                        }
                                    }
                                    if (line == null) {
                                        return line;
                                    }
                                }
                            }
                            try {
                                bufferedReader.close();
                            } catch (IOException unused3) {
                            }
                            try {
                                inputStreamReader.close();
                            } catch (IOException unused4) {
                            }
                        } catch (Exception e4) {
                            e = e4;
                            line = null;
                        }
                    } catch (Throwable th) {
                        th = th;
                        runningProcessInfoByPid = bufferedReader;
                        if (runningProcessInfoByPid != null) {
                            try {
                                runningProcessInfoByPid.close();
                            } catch (IOException unused5) {
                            }
                        }
                        if (inputStreamReader != null) {
                            try {
                                inputStreamReader.close();
                            } catch (IOException unused6) {
                            }
                        }
                        if (fileInputStream == null) {
                            throw th;
                        }
                        try {
                            fileInputStream.close();
                            throw th;
                        } catch (IOException unused7) {
                            throw th;
                        }
                    }
                } catch (Exception e5) {
                    e = e5;
                    bufferedReader = null;
                    line = bufferedReader;
                    Log.e(TAG, "getPackageNameByPid", e);
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (line == null) {
                        runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(i2);
                        if (runningProcessInfoByPid != null) {
                            line = runningProcessInfoByPid.mPkgList[0];
                        }
                    }
                    if (line == null) {
                        return line;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e6) {
                e = e6;
                inputStreamReader = null;
                bufferedReader = inputStreamReader;
                line = bufferedReader;
                Log.e(TAG, "getPackageNameByPid", e);
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (line == null) {
                    runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(i2);
                    if (runningProcessInfoByPid != null) {
                        line = runningProcessInfoByPid.mPkgList[0];
                    }
                }
                if (line == null) {
                    return line;
                }
            } catch (Throwable th3) {
                th = th3;
                inputStreamReader = null;
            }
        } catch (Exception e7) {
            e = e7;
            fileInputStream = null;
            inputStreamReader = null;
        } catch (Throwable th4) {
            th = th4;
            fileInputStream = null;
            inputStreamReader = null;
        }
        try {
            fileInputStream.close();
        } catch (IOException unused8) {
        }
        if (line == null) {
            runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(i2);
            if (runningProcessInfoByPid != null) {
                line = runningProcessInfoByPid.mPkgList[0];
            }
        }
        return (line == null || (iIndexOf = line.indexOf(":")) <= 0) ? line : line.substring(0, iIndexOf);
    }

    public static String getPackageNameByProcessName(String str) {
        if (str == null) {
            return null;
        }
        int iIndexOf = str.indexOf(":");
        return iIndexOf > 0 ? str.substring(0, iIndexOf) : str;
    }

    public static String getPackageNameByUid(Context context, int i2) {
        String[] packagesForUid = context.getPackageManager().getPackagesForUid(i2);
        return (packagesForUid == null || packagesForUid.length <= 0) ? Integer.toString(i2) : packagesForUid[0];
    }

    public static List<Integer> getPidByPackageName(String str) {
        List<RunningProcessInfo> runningProcessInfoByPackageName;
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(str)) {
            Log.e(TAG, "packageName is null");
        } else {
            try {
                runningProcessInfoByPackageName = ProcessManager.getRunningProcessInfoByPackageName(str);
            } catch (Exception e2) {
                Log.e(TAG, "getPidByPackageName packageName=" + str, e2);
                runningProcessInfoByPackageName = null;
            }
            if (runningProcessInfoByPackageName != null) {
                for (RunningProcessInfo runningProcessInfo : runningProcessInfoByPackageName) {
                    if (!arrayList.contains(Integer.valueOf(runningProcessInfo.mPid))) {
                        arrayList.add(Integer.valueOf(runningProcessInfo.mPid));
                    }
                }
            }
        }
        return arrayList;
    }

    public static String getProcessNameByPid(int i2) {
        RunningProcessInfo runningProcessInfoByPid;
        try {
            runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(i2);
        } catch (Exception e2) {
            Log.e(TAG, "getProcessNameByPid pid=" + i2, e2);
            runningProcessInfoByPid = null;
        }
        return runningProcessInfoByPid != null ? runningProcessInfoByPid.mProcessName : "";
    }

    public static int getUidByPackageName(Context context, String str) {
        return getUidByPackageName(context, str, context.getUserId());
    }

    public static boolean isSystemApp(Context context, ApplicationInfo applicationInfo) {
        return isSystemApp(applicationInfo) || PreloadedAppPolicy.isProtectedDataApp(context, applicationInfo.packageName, 0);
    }

    public static boolean isThirdPartApp(ApplicationInfo applicationInfo) {
        return UserHandle.getAppId(applicationInfo.uid) >= 10000 && (applicationInfo.flags & 1) == 0;
    }

    public static int getUidByPackageName(Context context, String str, int i2) {
        try {
            return PackageManagerCompat.getPackageUidAsUser(context.getPackageManager(), str, i2);
        } catch (PackageManager.NameNotFoundException unused) {
            return -1;
        }
    }

    public static boolean isSystemApp(Context context, String str) {
        try {
            return isSystemApp(context.getPackageManager().getApplicationInfo(str, 0)) || PreloadedAppPolicy.isProtectedDataApp(context, str, 0);
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e(TAG, "isSystemApp", e2);
            return false;
        }
    }

    public static List<ApplicationInfo> getAllApps(Context context, int i2) {
        try {
            Lists.newArrayList();
            return ActivityThread.getPackageManager().getInstalledApplications(0L, i2).getList();
        } catch (Exception e2) {
            Log.e(TAG, "getAllApps", e2);
            return Lists.newArrayList();
        }
    }

    public static boolean isSystemApp(Context context, String str, int i2) {
        boolean z2 = false;
        try {
            ApplicationInfo applicationInfo = ActivityThread.getPackageManager().getApplicationInfo(str, 0L, i2);
            if (applicationInfo != null) {
                if (isSystemApp(applicationInfo) || PreloadedAppPolicy.isProtectedDataApp(context, str, 0)) {
                    z2 = true;
                }
            } else {
                Log.d(TAG, "info is null when get " + str + " in " + i2);
            }
        } catch (Exception e2) {
            Log.e(TAG, "isSystemApp", e2);
        } catch (Throwable th) {
            throw th;
        }
        return z2;
    }

    private static boolean isSystemApp(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) > 0;
    }
}
