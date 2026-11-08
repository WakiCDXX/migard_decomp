package com.miui.powerkeeper.customerpower;

import android.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class CustomerPowerConstant {
    public static final String ACTION_CUSTOMER_POWER = "action.mi.autotest.TEST_CUSTOMER_POWER";
    public static final String ACTION_FACTORY_CUSTOMER_POWER = "action.mi.autotest.TEST_FACTORY_CUSTOMER_POWER";
    public static final String APP_BG_CPU = "App bg cpu";
    public static final String APP_PARTIAL_WAKELOCK = "App partial wakelock";
    public static final String BAD_SIGNAL = "Bad signal";
    public static final String CUSTOMER_POWER_DATABASE_NAME = "customerpowerchecker.db";
    public static final String CUSTOMER_POWER_FILE = "autotestfile";
    public static final String CUSTOMER_POWER_MAINTENANCE_MODE_DIR_PATH = "/data/local/maintenance_mode";
    public static final int CUSTOMER_POWER_MODE_FACTORY = 1;
    public static final int CUSTOMER_POWER_MODE_NORMAL = 0;
    public static final String CUSTOMER_POWER_NORMAL_MODE_DIR_PATH = "/data/data/com.miui.powerkeeper/databases";
    public static final String CUSTOMER_POWER_RESULT_FAIL = "TEST_CUSTOMER_POWER:FAIL";
    public static final String CUSTOMER_POWER_RESULT_NONE = "TEST_CUSTOMER_POWER:NONE";
    public static final String CUSTOMER_POWER_RESULT_PASS = "TEST_CUSTOMER_POWER:PASS";
    public static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public static final String FACTORY_CUSTOMER_POWER_RESULT_FAIL = "TEST_FACTORY_CUSTOMER_POWER:FAIL";
    public static final String FACTORY_CUSTOMER_POWER_RESULT_NONE = "TEST_FACTORY_CUSTOMER_POWER:NONE";
    public static final String FACTORY_CUSTOMER_POWER_RESULT_PASS = "TEST_FACTORY_CUSTOMER_POWER:PASS";
    public static final String HIGH_BTIGHT = "High brightness";
    public static final String KERNEL_WAKELOCK = "Kernel wakelock";
    public static final String KERNEL_WAKEUP = "Kernel wakeup";
    public static final String MAINTENANCE_MODE_POWER_FILE = "autotestfile";
    public static final int NUM_SIGNAL_STRENGTH_BINS = 6;
    public static final int NUM_TOP_RECORD = 10;
    public static final String POWER_NONE = "Power none";
    public static final String POWER_NORMAL = "Power normal";
    public static final String POWER_NOT_CHECKED = "Power not checked";
    public static final String POWER_TIME_SHORT = "Time is short";
    public static final long PROC_CPU_TIME = 1800000;
    public static final String TAG = "CP_";
    public static final String TEST_CUSTOMER_POWER = "TEST_CUSTOMER_POWER";
    public static final String TEST_FACTORY_CUSTOMER_POWER = "TEST_FACTORY_CUSTOMER_POWER";
}
