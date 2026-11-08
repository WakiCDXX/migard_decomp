package com.xiaomi.channel.commonutils.msa;

import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* JADX INFO: loaded from: classes.dex */
class MdidIdManager implements IdManager, InvocationHandler {
    private static final String CORE_CLASS_MDID = "com.bun.miitmdid.core.MdidSdk";
    private static final String[][] LISTENER_SUPPLIER_CLASS_ARRAY = {new String[]{"com.bun.supplier.IIdentifierListener", "com.bun.supplier.IdSupplier"}, new String[]{"com.bun.miitmdid.core.IIdentifierListener", "com.bun.miitmdid.supplier.IdSupplier"}};
    private static final String LOG_PREFIX = "mdid:";
    private static final int MAX_RETRY_COUNT = 3;
    private static final int TIME_WAIT_LOCK = 3000;
    private Context mContext;
    private Class mClassMdid = null;
    private Class mClassIIdentifierListener = null;
    private Method mMethodInitSdk = null;
    private Method mMethodGetUDID = null;
    private Method mMethodGetOAID = null;
    private Method mMethodGetVAID = null;
    private Method mMethodGetAAID = null;
    private Method mMethodIsSupported = null;
    private Method mMethodShutDown = null;
    private final Object mLockObj = new Object();
    private volatile int mRetryCount = 0;
    private volatile long mGettingOrGotTime = 0;
    private volatile IdData mIdData = null;

    private class IdData {
        String aaid;
        Boolean isSupport;
        String oaid;
        String udid;
        String vaid;

        private IdData() {
            this.isSupport = null;
            this.udid = null;
            this.oaid = null;
            this.vaid = null;
            this.aaid = null;
        }

        boolean reviseSelf() {
            if (!TextUtils.isEmpty(this.udid) || !TextUtils.isEmpty(this.oaid) || !TextUtils.isEmpty(this.vaid) || !TextUtils.isEmpty(this.aaid)) {
                this.isSupport = Boolean.TRUE;
            }
            return this.isSupport != null;
        }
    }

    public MdidIdManager(Context context) {
        this.mContext = context.getApplicationContext();
        initClass(context);
        callInitSdk(context);
    }

    private void callInitSdk(Context context) {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j2 = -jElapsedRealtime;
        Class cls = this.mClassIIdentifierListener;
        if (cls != null) {
            try {
                ClassLoader classLoader = cls.getClassLoader();
                if (classLoader == null) {
                    classLoader = context.getClassLoader();
                }
                invokeMethod(this.mMethodInitSdk, this.mClassMdid.newInstance(), context, Proxy.newProxyInstance(classLoader, new Class[]{this.mClassIIdentifierListener}, this));
            } catch (Throwable th) {
                outLog("call init sdk error:" + th);
                jElapsedRealtime = j2;
            }
        } else {
            jElapsedRealtime = j2;
        }
        this.mGettingOrGotTime = jElapsedRealtime;
    }

    private void cancelWait() {
        synchronized (this.mLockObj) {
            try {
                this.mLockObj.notifyAll();
            } catch (Exception unused) {
            }
        }
    }

    private static Method getMethod(Class<?> cls, String str, Class<?>... clsArr) {
        if (cls == null) {
            return null;
        }
        try {
            return cls.getMethod(str, clsArr);
        } catch (Throwable unused) {
            return null;
        }
    }

    private void initClass(Context context) {
        Class<?> clsLoadClass = loadClass(context, CORE_CLASS_MDID);
        Class<?> cls = null;
        Class<?> cls2 = null;
        int i2 = 0;
        while (true) {
            String[][] strArr = LISTENER_SUPPLIER_CLASS_ARRAY;
            if (i2 >= strArr.length) {
                break;
            }
            String[] strArr2 = strArr[i2];
            Class<?> clsLoadClass2 = loadClass(context, strArr2[0]);
            Class<?> clsLoadClass3 = loadClass(context, strArr2[1]);
            if (clsLoadClass2 != null && clsLoadClass3 != null) {
                outLog("found class in index " + i2);
                cls2 = clsLoadClass3;
                cls = clsLoadClass2;
                break;
            }
            i2++;
            cls2 = clsLoadClass3;
            cls = clsLoadClass2;
        }
        this.mClassMdid = clsLoadClass;
        this.mMethodInitSdk = getMethod(clsLoadClass, "InitSdk", Context.class, cls);
        this.mClassIIdentifierListener = cls;
        this.mMethodGetOAID = getMethod(cls2, "getOAID", new Class[0]);
        this.mMethodIsSupported = getMethod(cls2, "isSupported", new Class[0]);
        this.mMethodShutDown = getMethod(cls2, "shutDown", new Class[0]);
    }

    private static <T> T invokeMethod(Method method, Object obj, Object... objArr) {
        if (method == null) {
            return null;
        }
        try {
            T t2 = (T) method.invoke(obj, objArr);
            if (t2 != null) {
                return t2;
            }
            return null;
        } catch (Throwable unused) {
            return null;
        }
    }

    private static boolean isPrimitive(Object obj) {
        return (obj instanceof Boolean) || (obj instanceof Character) || (obj instanceof Byte) || (obj instanceof Short) || (obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Float) || (obj instanceof Double);
    }

    private static Class<?> loadClass(Context context, String str) {
        try {
            return SystemUtils.loadClass(context, str);
        } catch (Throwable unused) {
            return null;
        }
    }

    private static void outLog(String str) {
        MyLog.w(LOG_PREFIX + str);
    }

    private void waitAndGettingIfNeed(String str) {
        if (this.mIdData != null) {
            return;
        }
        long j2 = this.mGettingOrGotTime;
        long jElapsedRealtime = SystemClock.elapsedRealtime() - Math.abs(j2);
        int i2 = this.mRetryCount;
        if (jElapsedRealtime > 3000 && i2 < 3) {
            synchronized (this.mLockObj) {
                try {
                    if (this.mGettingOrGotTime == j2 && this.mRetryCount == i2) {
                        outLog("retry, current count is " + i2);
                        this.mRetryCount = this.mRetryCount + 1;
                        callInitSdk(this.mContext);
                        j2 = this.mGettingOrGotTime;
                        jElapsedRealtime = SystemClock.elapsedRealtime() - Math.abs(j2);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        if (this.mIdData != null || j2 < 0 || jElapsedRealtime > 3000 || Looper.myLooper() == Looper.getMainLooper()) {
            return;
        }
        synchronized (this.mLockObj) {
            if (this.mIdData == null) {
                try {
                    outLog(str + " wait...");
                    this.mLockObj.wait(3000L);
                } catch (Exception unused) {
                }
            }
        }
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getAAID() {
        return null;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getOAID() {
        waitAndGettingIfNeed("getOAID");
        if (this.mIdData == null) {
            return null;
        }
        return this.mIdData.oaid;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getUDID() {
        return null;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getVAID() {
        return null;
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object obj, Method method, Object[] objArr) {
        this.mGettingOrGotTime = SystemClock.elapsedRealtime();
        if (objArr != null) {
            IdData idData = new IdData();
            for (Object obj2 : objArr) {
                if (obj2 != null && !isPrimitive(obj2)) {
                    idData.oaid = (String) invokeMethod(this.mMethodGetOAID, obj2, new Object[0]);
                    idData.isSupport = (Boolean) invokeMethod(this.mMethodIsSupported, obj2, new Object[0]);
                    invokeMethod(this.mMethodShutDown, obj2, new Object[0]);
                    if (idData.reviseSelf()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("has get succ, check duplicate:");
                        sb.append(this.mIdData != null);
                        outLog(sb.toString());
                        synchronized (MdidIdManager.class) {
                            try {
                                if (this.mIdData == null) {
                                    this.mIdData = idData;
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                        break;
                    }
                }
            }
        }
        cancelWait();
        return null;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public boolean isAllowOAID() {
        return true;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public boolean isSupported() {
        waitAndGettingIfNeed("isSupported");
        return this.mIdData != null && Boolean.TRUE.equals(this.mIdData.isSupport);
    }
}
