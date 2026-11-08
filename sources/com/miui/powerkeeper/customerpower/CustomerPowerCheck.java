package com.miui.powerkeeper.customerpower;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.miui.powerkeeper.provider.CustomerPowerCheckerConfigure;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class CustomerPowerCheck {
    private static final String TAG = CustomerPowerConstant.TAG + CustomerPowerCheck.class.getSimpleName();
    private static final int WHICH = 0;
    private float mBadSignalTime;
    private Context mContext;
    private float mHighBrightnessTime;
    private long mScreenOffDischargeLevel;
    private long mScreenOffTime;
    private long mScreenOnDischargeLevel;
    private long mScreenOnTime;
    private long mWhichBatteryRealTime;
    private ArrayList<CustomerPowerUtil.TimerEntry> mKernelWakelocks = new ArrayList<>();
    private ArrayList<CustomerPowerUtil.TimerEntry> mKernelWakeups = new ArrayList<>();
    private ArrayList<CPAppStatusRecord> mAppStatusRecords = new ArrayList<>();
    private ArrayList<CPAppAbnormalRecord> listAppAbnormalRecords = new ArrayList<>();
    public IBatteryStats mBatteryStats = BatteryStatsHelper.getBatteryStats();
    private long mRawRealtime = SystemClock.elapsedRealtime() * 1000;

    public CustomerPowerCheck(Context context) {
        this.mContext = context;
    }

    private boolean checkedAppCpuAbnormal() {
        ArrayList<CPAppAbnormalRecord> appAbnormalRecords = getAppAbnormalRecords();
        this.listAppAbnormalRecords = appAbnormalRecords;
        int size = appAbnormalRecords.size();
        if (size > 0) {
            for (int i2 = 0; i2 < size; i2++) {
                if (this.listAppAbnormalRecords.get(i2).getAppType() == 11) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkedAppWakelockAbnormal() {
        ArrayList<CPAppAbnormalRecord> appAbnormalRecords = getAppAbnormalRecords();
        this.listAppAbnormalRecords = appAbnormalRecords;
        int size = appAbnormalRecords.size();
        if (size > 0) {
            for (int i2 = 0; i2 < size; i2++) {
                if (this.listAppAbnormalRecords.get(i2).getAppType() == 12) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkedBadSignal() {
        float f2 = this.mBadSignalTime;
        long j2 = this.mWhichBatteryRealTime;
        float f3 = (100.0f * f2) / j2;
        if (j2 > 7200000) {
            return f3 > 50.0f || f2 > 7200000.0f;
        }
        return false;
    }

    private boolean checkedHighBrightness() {
        float f2 = this.mHighBrightnessTime;
        float f3 = (100.0f * f2) / this.mScreenOnTime;
        if (this.mWhichBatteryRealTime > 3600000) {
            return f3 > 50.0f || f2 > 3600000.0f;
        }
        return false;
    }

    private boolean checkedKernelWakelocks() {
        CustomerPowerUtil.sortTimerEntry(this.mKernelWakelocks);
        int size = this.mKernelWakelocks.size();
        if (size > 0) {
            for (int i2 = 0; i2 < size && i2 < 10; i2++) {
                CustomerPowerUtil.TimerEntry timerEntry = this.mKernelWakelocks.get(i2);
                String str = timerEntry.mName;
                long j2 = timerEntry.mTime;
                if (!"PowerManagerService.WakeLocks".equals(str) && !"PowerManagerService.Display".equals(str) && !"PowerManagerService.Broadcasts".equals(str) && j2 > 0) {
                    long j3 = this.mScreenOffTime;
                    if (j3 > 3600000 && ((100 * j2) / j3 > 50 || j2 > 3600000)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkedKernelWakeups() {
        CustomerPowerUtil.sortTimerEntry(this.mKernelWakeups);
        int size = this.mKernelWakeups.size();
        if (size > 0) {
            for (int i2 = 0; i2 < size && i2 < 10; i2++) {
                CustomerPowerUtil.TimerEntry timerEntry = this.mKernelWakeups.get(i2);
                String str = timerEntry.mName;
                long j2 = timerEntry.mTime;
                int i3 = timerEntry.mCount;
                if (j2 > 0) {
                    long j3 = this.mScreenOffTime;
                    if (j3 > 3600000 && ((100 * j2) / j3 > 50 || j2 > 3600000 || ((long) (i3 * 3600000)) / j3 > 500)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkedScreenOffFast() {
        long j2 = this.mScreenOffTime;
        return j2 > 3600000 && ((float) ((this.mScreenOffDischargeLevel * 3600000) / j2)) > 1.7f;
    }

    private boolean checkedScreenOnFast() {
        long j2 = this.mScreenOnTime;
        return j2 > 3600000 && ((float) ((this.mScreenOnDischargeLevel * 3600000) / j2)) > 15.0f;
    }

    private void outputResultFail(PrintWriter printWriter, int i2) throws Throwable {
        if (printWriter != null) {
            if (i2 == 0) {
                printWriter.println("  TEST_CUSTOMER_POWER");
                printWriter.println("  TEST_CUSTOMER_POWER:FAIL");
            } else if (i2 == 1) {
                printWriter.println("  TEST_FACTORY_CUSTOMER_POWER");
                printWriter.println("  TEST_FACTORY_CUSTOMER_POWER:FAIL");
            }
            Log.d(TAG, "customer power info outputResultNone dump info");
            return;
        }
        Log.d(TAG, "customer power info outputResultNone pw is null");
        boolean zIsMaintenanceModeEnable = CustomerPowerUtil.isMaintenanceModeEnable();
        try {
            try {
                String strTargetFilePath = targetFilePath(zIsMaintenanceModeEnable);
                File file = strTargetFilePath != null ? new File(strTargetFilePath) : null;
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (zIsMaintenanceModeEnable) {
                    file.setWritable(true, false);
                    file.setReadable(true, false);
                }
                PrintWriter printWriter2 = new PrintWriter(new FileOutputStream(file));
                try {
                    if (i2 != 0) {
                        if (i2 == 1) {
                            printWriter2.println(CustomerPowerConstant.TEST_FACTORY_CUSTOMER_POWER);
                            printWriter2.println(CustomerPowerConstant.FACTORY_CUSTOMER_POWER_RESULT_FAIL);
                        }
                        printWriter2.close();
                    }
                    printWriter2.println(CustomerPowerConstant.TEST_CUSTOMER_POWER);
                    printWriter2.println(CustomerPowerConstant.CUSTOMER_POWER_RESULT_FAIL);
                    printWriter2.close();
                } catch (Exception unused) {
                    printWriter = printWriter2;
                    Log.d(TAG, "customer power info outputResultNone sdcard or data/local/maintenance_mode dir is not exists");
                    if (printWriter != null) {
                        printWriter.close();
                    }
                } catch (Throwable th) {
                    printWriter = printWriter2;
                    th = th;
                    if (printWriter != null) {
                        printWriter.close();
                    }
                    throw th;
                }
            } catch (Exception unused2) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Code duplicated, block: B:125:0x023b  */
    /* JADX WARN: Code duplicated, block: B:128:0x0245  */
    /* JADX WARN: Code duplicated, block: B:131:0x024f  */
    /* JADX WARN: Code duplicated, block: B:134:0x0259  */
    /* JADX WARN: Multi-variable type inference failed */
    private void outputResultPassAndNone(PrintWriter printWriter, int i2) throws Throwable {
        PrintWriter printWriter2;
        PrintWriter printWriter3;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        int i15;
        ArrayList<CustomerPowerResultBean> arrayListQuery = CustomerPowerCheckerDatabaseCrud.getInstance(this.mContext).query(CustomerPowerCheckerConfigure.TABLE, i2);
        int size = arrayListQuery.size();
        ArrayList<CustomerPowerResultBean> arrayListQueryAllByWeek = CustomerPowerCheckerDatabaseCrud.getInstance(this.mContext).queryAllByWeek(CustomerPowerCheckerConfigure.TABLE, System.currentTimeMillis());
        int size2 = arrayListQueryAllByWeek.size();
        int i16 = 11;
        int i17 = 0;
        try {
            if (printWriter != null) {
                if (i2 == 0) {
                    printWriter.println("  TEST_CUSTOMER_POWER");
                } else if (i2 == 1) {
                    printWriter.println("  TEST_FACTORY_CUSTOMER_POWER");
                }
                if (size2 > 0) {
                    int i18 = 0;
                    i9 = 0;
                    i10 = 0;
                    i11 = 0;
                    i12 = 0;
                    i13 = 0;
                    i14 = 0;
                    while (i17 < size2) {
                        CustomerPowerResultBean customerPowerResultBean = arrayListQueryAllByWeek.get(i17);
                        int type = customerPowerResultBean.getType();
                        if (type == i16 && !Build.IS_INTERNATIONAL_BUILD) {
                            i12++;
                            printWriter.println("  App bg cpu: " + getLabelByPackageNames(customerPowerResultBean.getDetailName().split(",")));
                        } else if (type == i16 && Build.IS_INTERNATIONAL_BUILD) {
                            i12++;
                            printWriter.println("  App bg cpu");
                        }
                        if (type == 12) {
                            if (Build.IS_INTERNATIONAL_BUILD) {
                                i15 = 12;
                            } else {
                                i13++;
                                printWriter.println("  App partial wakelock: " + getLabelByPackageNames(customerPowerResultBean.getDetailName().split(",")));
                            }
                            if (type == 2) {
                                i18++;
                                printWriter.println("  High brightness");
                            }
                            if (type == 5) {
                                i10++;
                                printWriter.println("  Kernel wakelock");
                            }
                            if (type == 6) {
                                i11++;
                                printWriter.println("  Kernel wakeup");
                            }
                            if (type == 1) {
                                i9++;
                                printWriter.println("  Bad signal");
                            }
                            if (type != 100 && i14 == 0) {
                                i14++;
                            }
                            i17++;
                            i16 = 11;
                        } else {
                            i15 = 12;
                        }
                        if (type == i15 && Build.IS_INTERNATIONAL_BUILD) {
                            i13++;
                            printWriter.println("  App partial wakelock");
                        }
                        if (type == 2) {
                            i18++;
                            printWriter.println("  High brightness");
                        }
                        if (type == 5) {
                            i10++;
                            printWriter.println("  Kernel wakelock");
                        }
                        if (type == 6) {
                            i11++;
                            printWriter.println("  Kernel wakeup");
                        }
                        if (type == 1) {
                            i9++;
                            printWriter.println("  Bad signal");
                        }
                        if (type != 100) {
                        }
                        i17++;
                        i16 = 11;
                    }
                    i17 = i18;
                } else {
                    Log.d(TAG, "dump customer power info power is not checked");
                    i9 = 0;
                    i10 = 0;
                    i11 = 0;
                    i12 = 0;
                    i13 = 0;
                    i14 = 1;
                }
                if (i17 != 0 || i9 != 0 || i10 != 0 || i11 != 0 || i12 != 0 || i13 != 0) {
                    if (i2 == 0) {
                        printWriter.println("  TEST_CUSTOMER_POWER:PASS");
                        return;
                    } else {
                        if (i2 == 1) {
                            printWriter.println("  TEST_FACTORY_CUSTOMER_POWER:PASS");
                            return;
                        }
                        return;
                    }
                }
                if (i14 != 0) {
                    if (i2 == 0) {
                        printWriter.println("  TEST_CUSTOMER_POWER:NONE");
                        return;
                    } else {
                        if (i2 == 1) {
                            printWriter.println("  TEST_FACTORY_CUSTOMER_POWER:NONE");
                            return;
                        }
                        return;
                    }
                }
                return;
            }
            try {
                boolean zIsMaintenanceModeEnable = CustomerPowerUtil.isMaintenanceModeEnable();
                String strTargetFilePath = targetFilePath(zIsMaintenanceModeEnable);
                File file = strTargetFilePath != null ? new File(strTargetFilePath) : null;
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (zIsMaintenanceModeEnable) {
                    file.setWritable(true, false);
                    file.setReadable(true, false);
                }
                printWriter3 = new PrintWriter(new FileOutputStream(file));
                try {
                    if (i2 == 0) {
                        printWriter3.println(CustomerPowerConstant.TEST_CUSTOMER_POWER);
                    } else if (i2 == 1) {
                        printWriter3.println(CustomerPowerConstant.TEST_FACTORY_CUSTOMER_POWER);
                    }
                    if (size > 0) {
                        int i19 = 0;
                        i3 = 0;
                        i4 = 0;
                        i5 = 0;
                        i6 = 0;
                        i7 = 0;
                        i8 = 0;
                        while (i17 < size) {
                            CustomerPowerResultBean customerPowerResultBean2 = arrayListQuery.get(i17);
                            int type2 = customerPowerResultBean2.getType();
                            if (type2 == 11 && i6 == 0 && !Build.IS_INTERNATIONAL_BUILD) {
                                i6++;
                                printWriter3.println("App bg cpu: " + getLabelByPackageNames(customerPowerResultBean2.getDetailName().split(",")));
                            } else if (type2 == 11 && i6 == 0 && Build.IS_INTERNATIONAL_BUILD) {
                                i6++;
                                printWriter3.println(CustomerPowerConstant.APP_BG_CPU);
                            }
                            if (type2 == 12 && i7 == 0 && !Build.IS_INTERNATIONAL_BUILD) {
                                i7++;
                                printWriter3.println("App partial wakelock: " + getLabelByPackageNames(customerPowerResultBean2.getDetailName().split(",")));
                            } else if (type2 == 12 && i7 == 0 && Build.IS_INTERNATIONAL_BUILD) {
                                i7++;
                                printWriter3.println(CustomerPowerConstant.APP_PARTIAL_WAKELOCK);
                            }
                            if (type2 == 2 && i19 == 0) {
                                i19++;
                                printWriter3.println(CustomerPowerConstant.HIGH_BTIGHT);
                            }
                            if (type2 == 5 && i4 == 0) {
                                i4++;
                                printWriter3.println(CustomerPowerConstant.KERNEL_WAKELOCK);
                            }
                            if (type2 == 6 && i5 == 0) {
                                i5++;
                                printWriter3.println(CustomerPowerConstant.KERNEL_WAKEUP);
                            }
                            if (type2 == 1 && i3 == 0) {
                                i3++;
                                printWriter3.println(CustomerPowerConstant.BAD_SIGNAL);
                            }
                            if (type2 == 100 && i8 == 0) {
                                i8++;
                            }
                            i17++;
                        }
                        i17 = i19;
                    } else {
                        Log.d(TAG, "customer power info power is not checked");
                        i3 = 0;
                        i4 = 0;
                        i5 = 0;
                        i6 = 0;
                        i7 = 0;
                        i8 = 1;
                    }
                    if (i17 == 0 && i3 == 0 && i4 == 0 && i5 == 0 && i6 == 0 && i7 == 0) {
                        if (i8 != 0) {
                            if (i2 == 0) {
                                printWriter3.println(CustomerPowerConstant.CUSTOMER_POWER_RESULT_NONE);
                            } else if (i2 == 1) {
                                printWriter3.println(CustomerPowerConstant.FACTORY_CUSTOMER_POWER_RESULT_NONE);
                            }
                        }
                    } else if (i2 == 0) {
                        printWriter3.println(CustomerPowerConstant.CUSTOMER_POWER_RESULT_PASS);
                    } else if (i2 == 1) {
                        printWriter3.println(CustomerPowerConstant.FACTORY_CUSTOMER_POWER_RESULT_PASS);
                    }
                } catch (Exception unused) {
                    Log.e(TAG, "customer power info create autotestfile or write file failed");
                    if (printWriter3 == null) {
                        return;
                    }
                }
            } catch (Exception unused2) {
                printWriter3 = printWriter;
            } catch (Throwable th) {
                th = th;
                printWriter2 = printWriter;
                if (printWriter2 != 0) {
                    printWriter2.close();
                }
                throw th;
            }
            printWriter3.close();
        } catch (Throwable th2) {
            th = th2;
            printWriter2 = arrayListQueryAllByWeek;
        }
    }

    private String targetFilePath(boolean z2) {
        if (z2) {
            return "/data/local/maintenance_mode/autotestfile";
        }
        if (!Environment.getExternalStorageDirectory().exists()) {
            Log.d(TAG, "customer power info outputRecord sdcard dir is not exists");
            return null;
        }
        return Environment.getExternalStorageDirectory().getPath() + "/autotestfile";
    }

    public ArrayList<CustomerPowerResultBean> checkPowerConsume() {
        ArrayList<CustomerPowerResultBean> arrayList = new ArrayList<>();
        if ((this.mScreenOnTime >= 3600000 || this.mScreenOffTime >= 3600000) && this.mWhichBatteryRealTime <= 172800000) {
            boolean zCheckedScreenOnFast = checkedScreenOnFast();
            boolean zCheckedScreenOffFast = checkedScreenOffFast();
            String str = TAG;
            Log.d(str, "customer power info checked screenOnFast = " + zCheckedScreenOnFast + ", screenOffFast = " + zCheckedScreenOffFast);
            if (zCheckedScreenOnFast && zCheckedScreenOffFast) {
                if (checkedAppCpuAbnormal()) {
                    int size = this.listAppAbnormalRecords.size();
                    String str2 = "";
                    for (int i2 = 0; i2 < size; i2++) {
                        int uid = this.listAppAbnormalRecords.get(i2).getUid();
                        int appType = this.listAppAbnormalRecords.get(i2).getAppType();
                        String pkgName = this.listAppAbnormalRecords.get(i2).getPkgName();
                        String procName = this.listAppAbnormalRecords.get(i2).getProcName();
                        if (uid == 1000 || uid == 0) {
                            if (procName != null && appType == 11) {
                                str2 = "".equals(str2) ? str2 + procName : str2 + "," + procName;
                            }
                        } else if (pkgName != null && appType == 11) {
                            str2 = "".equals(str2) ? str2 + pkgName : str2 + "," + pkgName;
                        }
                    }
                    if (!"".equals(str2)) {
                        Log.d(TAG, "customer power info checked checkPowerConsume appNames cpu = " + str2);
                        arrayList.add(new CustomerPowerResultBean(11, CustomerPowerConstant.APP_BG_CPU, str2, 0L, 0));
                    }
                }
                if (checkedAppWakelockAbnormal()) {
                    int size2 = this.listAppAbnormalRecords.size();
                    String str3 = "";
                    for (int i3 = 0; i3 < size2; i3++) {
                        int uid2 = this.listAppAbnormalRecords.get(i3).getUid();
                        int appType2 = this.listAppAbnormalRecords.get(i3).getAppType();
                        String pkgName2 = this.listAppAbnormalRecords.get(i3).getPkgName();
                        String procName2 = this.listAppAbnormalRecords.get(i3).getProcName();
                        if (uid2 == 1000 || uid2 == 0) {
                            if (procName2 != null && appType2 == 12) {
                                str3 = "".equals(str3) ? str3 + pkgName2 : str3 + "," + pkgName2;
                            }
                        } else if (pkgName2 != null && appType2 == 12) {
                            str3 = "".equals(str3) ? str3 + pkgName2 : str3 + "," + pkgName2;
                        }
                    }
                    if (!"".equals(str3)) {
                        Log.d(TAG, "customer power info checked checkPowerConsume appNames wakelock = " + str3);
                        arrayList.add(new CustomerPowerResultBean(12, CustomerPowerConstant.APP_PARTIAL_WAKELOCK, str3, 0L, 0));
                    }
                }
                if (checkedHighBrightness()) {
                    arrayList.add(new CustomerPowerResultBean(2, CustomerPowerConstant.HIGH_BTIGHT, null, 0L, 0));
                }
                if (checkedKernelWakelocks()) {
                    arrayList.add(new CustomerPowerResultBean(5, CustomerPowerConstant.KERNEL_WAKELOCK, null, 0L, 0));
                }
                if (checkedKernelWakeups()) {
                    arrayList.add(new CustomerPowerResultBean(6, CustomerPowerConstant.KERNEL_WAKEUP, null, 0L, 0));
                }
                if (checkedBadSignal()) {
                    arrayList.add(new CustomerPowerResultBean(1, CustomerPowerConstant.BAD_SIGNAL, null, 0L, 0));
                }
            } else if (zCheckedScreenOnFast) {
                if (checkedHighBrightness()) {
                    arrayList.add(new CustomerPowerResultBean(2, CustomerPowerConstant.HIGH_BTIGHT, null, 0L, 0));
                }
                if (checkedBadSignal()) {
                    arrayList.add(new CustomerPowerResultBean(1, CustomerPowerConstant.BAD_SIGNAL, null, 0L, 0));
                }
            } else if (zCheckedScreenOffFast) {
                if (checkedAppCpuAbnormal()) {
                    int size3 = this.listAppAbnormalRecords.size();
                    String str4 = "";
                    for (int i4 = 0; i4 < size3; i4++) {
                        int uid3 = this.listAppAbnormalRecords.get(i4).getUid();
                        int appType3 = this.listAppAbnormalRecords.get(i4).getAppType();
                        String pkgName3 = this.listAppAbnormalRecords.get(i4).getPkgName();
                        String procName3 = this.listAppAbnormalRecords.get(i4).getProcName();
                        if (uid3 == 1000 || uid3 == 0) {
                            if (procName3 != null && appType3 == 11) {
                                str4 = str4 + "," + procName3;
                            }
                        } else if (pkgName3 != null && appType3 == 11) {
                            str4 = str4 + "," + pkgName3;
                        }
                    }
                    if (!"".equals(str4)) {
                        Log.d(TAG, "customer power info checked checkPowerConsume appNames cpu = " + str4);
                        arrayList.add(new CustomerPowerResultBean(11, CustomerPowerConstant.APP_BG_CPU, str4, 0L, 0));
                    }
                }
                if (checkedAppWakelockAbnormal()) {
                    int size4 = this.listAppAbnormalRecords.size();
                    String str5 = "";
                    for (int i5 = 0; i5 < size4; i5++) {
                        int uid4 = this.listAppAbnormalRecords.get(i5).getUid();
                        int appType4 = this.listAppAbnormalRecords.get(i5).getAppType();
                        String pkgName4 = this.listAppAbnormalRecords.get(i5).getPkgName();
                        String procName4 = this.listAppAbnormalRecords.get(i5).getProcName();
                        if (uid4 == 1000 || uid4 == 0) {
                            if (procName4 != null && appType4 == 12) {
                                str5 = str5 + "," + procName4;
                            }
                        } else if (pkgName4 != null && appType4 == 12) {
                            str5 = str5 + "," + pkgName4;
                        }
                    }
                    if (!"".equals(str5)) {
                        Log.d(TAG, "customer power info checked checkPowerConsume appNames wakelock = " + str5);
                        arrayList.add(new CustomerPowerResultBean(12, CustomerPowerConstant.APP_PARTIAL_WAKELOCK, str5, 0L, 0));
                    }
                }
                if (checkedKernelWakelocks()) {
                    arrayList.add(new CustomerPowerResultBean(5, CustomerPowerConstant.KERNEL_WAKELOCK, null, 0L, 0));
                }
                if (checkedKernelWakeups()) {
                    arrayList.add(new CustomerPowerResultBean(6, CustomerPowerConstant.KERNEL_WAKEUP, null, 0L, 0));
                }
                if (checkedBadSignal()) {
                    arrayList.add(new CustomerPowerResultBean(1, CustomerPowerConstant.BAD_SIGNAL, null, 0L, 0));
                }
            } else {
                Log.d(str, "customer power info the screen on consume and screen off consume is all good,it's ok");
                arrayList.add(new CustomerPowerResultBean(100, CustomerPowerConstant.POWER_NONE, null, 0L, 0));
            }
        } else {
            Log.d(TAG, "customer power info checked checkPowerConsume time is too short or too long");
            arrayList.add(new CustomerPowerResultBean(100, CustomerPowerConstant.POWER_NONE, null, 0L, 0));
        }
        return arrayList;
    }

    public ArrayList<CPAppAbnormalRecord> getAppAbnormalRecords() {
        String str;
        String str2;
        String str3;
        long j2;
        long j3;
        long j4;
        String str4;
        ArrayList<CPAppAbnormalRecord> arrayList;
        String str5;
        String str6;
        String str7;
        long j5;
        long j6;
        long j7;
        String str8;
        ArrayList<CPAppAbnormalRecord> arrayList2;
        ArrayList<CPAppAbnormalRecord> arrayList3 = new ArrayList<>();
        if (this.mAppStatusRecords.size() > 0) {
            int size = this.mAppStatusRecords.size();
            int i2 = 0;
            while (i2 < size) {
                CPAppStatusRecord cPAppStatusRecord = this.mAppStatusRecords.get(i2);
                int uid = cPAppStatusRecord.getUid();
                String uidName = cPAppStatusRecord.getUidName();
                String procName = cPAppStatusRecord.getProcName();
                long cpuTime = cPAppStatusRecord.getCpuTime();
                long wakelockTime = cPAppStatusRecord.getWakelockTime();
                long foregroundTime = cPAppStatusRecord.getForegroundTime();
                long j8 = cpuTime - foregroundTime;
                int i3 = size;
                int i4 = i2;
                ArrayList<CPAppAbnormalRecord> arrayList4 = arrayList3;
                if (j8 <= 0) {
                    str = ", cpuTime = ";
                    str2 = ", wakelockTime = ";
                    str3 = ", foregroundTime = ";
                    j2 = foregroundTime;
                    j3 = wakelockTime;
                    j4 = cpuTime;
                    str4 = procName;
                    arrayList = arrayList4;
                } else if (uid == 0 || uid == 1000) {
                    str5 = ", cpuTime = ";
                    str6 = ", wakelockTime = ";
                    str7 = ", foregroundTime = ";
                    j5 = foregroundTime;
                    j6 = wakelockTime;
                    j7 = cpuTime;
                    str8 = procName;
                    arrayList2 = arrayList4;
                    long j9 = this.mWhichBatteryRealTime;
                    if (j9 <= 3600000 || (j8 <= 3600000 && (j8 * 100) / j9 <= 50)) {
                        str4 = str8;
                        arrayList = arrayList2;
                        str = str5;
                        str3 = str7;
                        str2 = str6;
                        j2 = j5;
                        j3 = j6;
                        j4 = j7;
                    } else {
                        Log.d(TAG, "customer power info app uuid = " + uid + ", uidName = " + uidName + ", procName = " + str8 + str5 + j7 + str6 + j6 + str7 + j5);
                        str3 = str7;
                        j3 = j6;
                        j2 = j5;
                        j4 = j7;
                        str = str5;
                        str2 = str6;
                        str4 = str8;
                        arrayList = arrayList2;
                        arrayList.add(new CPAppAbnormalRecord(uid, uidName, str8, 11, "proc_cpu", j8, 0));
                    }
                } else {
                    long j10 = this.mWhichBatteryRealTime;
                    if (j10 <= 3600000) {
                        str = ", cpuTime = ";
                        str2 = ", wakelockTime = ";
                        str3 = ", foregroundTime = ";
                        j3 = wakelockTime;
                        j4 = cpuTime;
                        str4 = procName;
                        arrayList = arrayList4;
                        j2 = foregroundTime;
                    } else if (j8 > 3600000 || (j8 * 100) / j10 > 50) {
                        Log.d(TAG, "customer power info app cuid = " + uid + ", uidName = " + uidName + ", procName = " + procName + ", cpuTime = " + cpuTime + ", wakelockTime = " + wakelockTime + ", foregroundTime = " + foregroundTime);
                        str7 = ", foregroundTime = ";
                        str6 = ", wakelockTime = ";
                        j5 = foregroundTime;
                        j6 = wakelockTime;
                        j7 = cpuTime;
                        str5 = ", cpuTime = ";
                        str8 = procName;
                        CPAppAbnormalRecord cPAppAbnormalRecord = new CPAppAbnormalRecord(uid, uidName, procName, 11, "uid_cpu", j8, 0);
                        arrayList2 = arrayList4;
                        arrayList2.add(cPAppAbnormalRecord);
                        str4 = str8;
                        arrayList = arrayList2;
                        str = str5;
                        str3 = str7;
                        str2 = str6;
                        j2 = j5;
                        j3 = j6;
                        j4 = j7;
                    } else {
                        str = ", cpuTime = ";
                        str2 = ", wakelockTime = ";
                        str3 = ", foregroundTime = ";
                        j3 = wakelockTime;
                        j4 = cpuTime;
                        str4 = procName;
                        arrayList = arrayList4;
                        j2 = foregroundTime;
                    }
                }
                if (this.mWhichBatteryRealTime > 0) {
                    long j11 = this.mScreenOffTime;
                    if (j11 > 3600000) {
                        long j12 = j3;
                        if ((j12 * 100) / j11 > 50 || j12 > 3600000) {
                            String str9 = TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("customer power info app puid = ");
                            sb.append(uid);
                            sb.append(", uidName = ");
                            sb.append(uidName);
                            sb.append(", procName = ");
                            String str10 = str4;
                            sb.append(str10);
                            sb.append(str);
                            sb.append(j4);
                            sb.append(str2);
                            sb.append(j12);
                            sb.append(str3);
                            sb.append(j2);
                            Log.d(str9, sb.toString());
                            arrayList.add(new CPAppAbnormalRecord(uid, uidName, str10, 12, "wakelock_", j12, 0));
                        }
                    }
                }
                i2 = i4 + 1;
                arrayList3 = arrayList;
                size = i3;
            }
        }
        return arrayList3;
    }

    public String getLabelByPackageNames(String[] strArr) {
        String str = "";
        for (String str2 : strArr) {
            int uidByPackageName = CustomerPowerUtil.getUidByPackageName(this.mContext, str2);
            if (uidByPackageName == 1000 || uidByPackageName == 0 || uidByPackageName == -1) {
                str = "".equals(str) ? str + str2 : str + "," + str2;
            } else if ("".equals(str)) {
                str = str + ((Object) CustomerPowerUtil.getLabelByPackageName(this.mContext, str2));
            } else {
                str = str + "," + ((Object) CustomerPowerUtil.getLabelByPackageName(this.mContext, str2));
            }
        }
        return str;
    }

    public void initBatteryInfo() {
        this.mWhichBatteryRealTime = CustomerPowerHelper.getWhichBatteryRealtime(this.mBatteryStats, this.mRawRealtime, 0);
        this.mScreenOnTime = CustomerPowerHelper.getScreenOnTime(this.mBatteryStats, this.mRawRealtime, 0);
        this.mScreenOffTime = CustomerPowerHelper.getScreenOffTime(this.mBatteryStats, this.mRawRealtime, 0);
        this.mScreenOnDischargeLevel = CustomerPowerHelper.getScreenOnDischargedLevel(this.mBatteryStats);
        this.mScreenOffDischargeLevel = CustomerPowerHelper.getScreenOffDischargedLevel(this.mBatteryStats);
        this.mBadSignalTime = CustomerPowerHelper.getBadSignalTime(this.mBatteryStats, this.mRawRealtime, 0);
        this.mHighBrightnessTime = CustomerPowerHelper.getHighBrightnessTime(this.mBatteryStats, this.mRawRealtime, 0);
        this.mKernelWakelocks = CustomerPowerHelper.getKernelWakelockStats(this.mBatteryStats, this.mRawRealtime, 0);
        this.mKernelWakeups = CustomerPowerHelper.getKernelWakeups(this.mBatteryStats, this.mRawRealtime, 0);
        this.mAppStatusRecords = CustomerPowerHelper.getAppStatusRecords(this.mContext, this.mRawRealtime, 0);
        Log.d(TAG, "customer power info mWhichBatteryRealTime = " + this.mWhichBatteryRealTime + ", mScreenOnTime = " + this.mScreenOnTime + ", mScreenOffTime = " + this.mScreenOffTime + ", mScreenOnDischargeLevel = " + this.mScreenOnDischargeLevel + ", mScreenOffDischargeLevel = " + this.mScreenOffDischargeLevel + ", mBadSignalTime = " + this.mBadSignalTime + ", mHighBrightnessTime = " + this.mHighBrightnessTime);
    }

    public void outputFinalResult(PrintWriter printWriter, int i2) {
        if (this.mBatteryStats == null) {
            Log.d(TAG, "customer power info batteryStatsImpl is null");
            outputResultFail(printWriter, i2);
        } else {
            Log.d(TAG, "customer power info batteryStatsImpl is not null");
            initBatteryInfo();
            outputResultPassAndNone(printWriter, i2);
        }
    }
}
