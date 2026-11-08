package com.miui.powerkeeper.utils;

import android.util.Log;
import com.xiaomi.push.mpcd.Constants;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class ReflectionUtils {
    private static final String ILLEGAL_ACCESS_EXCEPTION = "IllegalAccessException";
    private static final String ILLEGAL_ARGUMENT_EXCEPTION = "IllegalArgumentException";
    private static final String INVOCATION_TARGET_EXCEPTION = "InvocationTargetException";
    private static final String NO_SUCH_METHOD_EXCEPTION = "NoSuchMethodException";
    private static final String SECURITY_EXCEPTION = "SecurityException";
    private static final String TAG = "PowerKeeper-ReflectionUtils";

    public static Object callMethod(Object obj, String str, Object... objArr) {
        try {
            Class[] parameterTypes = getParameterTypes(objArr);
            Log.i(TAG, "obj is " + obj + ", methodName is " + str + ", paramsClass");
            Method methodFindMethod = findMethod(obj.getClass(), str, parameterTypes);
            if (methodFindMethod == null) {
                return null;
            }
            methodFindMethod.setAccessible(true);
            return methodFindMethod.invoke(obj, objArr);
        } catch (Exception e2) {
            Log.w(TAG, "failed to callMethod " + obj.getClass() + Constants.DOT_SEPARATOR + str, e2);
            return null;
        }
    }

    public static Object callObjectMethod(Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        try {
            return callObjectMethodOrThrow(obj, str, clsArr, objArr);
        } catch (Exception e2) {
            e2.printStackTrace();
            Log.e(TAG, "callObjectMethod error", e2);
            return null;
        }
    }

    public static <T> T callObjectMethodCall(String str, Object obj, Class<T> cls, String str2, Class<?>[] clsArr, Object... objArr) {
        try {
            return (T) callObjectMethodOrThrow(obj, str2, cls, clsArr, objArr);
        } catch (IllegalAccessException e2) {
            Log.e(str, ILLEGAL_ACCESS_EXCEPTION, e2);
            return null;
        } catch (IllegalArgumentException e3) {
            Log.e(str, ILLEGAL_ARGUMENT_EXCEPTION, e3);
            return null;
        } catch (NoSuchMethodException e4) {
            Log.e(str, NO_SUCH_METHOD_EXCEPTION, e4);
            return null;
        } catch (SecurityException e5) {
            Log.e(str, SECURITY_EXCEPTION, e5);
            return null;
        } catch (InvocationTargetException e6) {
            Log.e(str, INVOCATION_TARGET_EXCEPTION, e6);
            return null;
        }
    }

    public static <T> T callObjectMethodOrThrow(Object obj, String str, Class<T> cls, Class<?>[] clsArr, Object... objArr) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return (T) declaredMethod.invoke(obj, objArr);
    }

    public static Object callPrivateMethod(Object obj, String str, Object... objArr) {
        try {
            Method method = obj.getClass().getMethod(str, getParameterTypes(objArr));
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            return method.invoke(obj, objArr);
        } catch (Exception e2) {
            Log.w(TAG, "failed to callPrivateMethod " + obj.getClass() + Constants.DOT_SEPARATOR + str, e2);
            return null;
        }
    }

    public static Object callStaticMethod(Class cls, String str, Object... objArr) {
        try {
            Method method = cls.getMethod(str, getParameterTypes(objArr));
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            return method.invoke(null, objArr);
        } catch (Exception e2) {
            Log.w(TAG, "failed to callStaticMethod " + cls + Constants.DOT_SEPARATOR + str, e2);
            return null;
        }
    }

    public static <T> T callStaticObjectMethod(Class<?> cls, Class<T> cls2, String str, Class<?>[] clsArr, Object... objArr) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return (T) declaredMethod.invoke(null, objArr);
    }

    public static boolean copyAllFields(Class<?> cls, Object obj, Object obj2) {
        if (obj == null || obj2 == null) {
            return false;
        }
        while (cls != null) {
            for (Field field : cls.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    field.set(obj2, field.get(obj));
                } catch (Exception unused) {
                }
            }
            cls = cls.getSuperclass();
        }
        return true;
    }

    public static Field findField(Class<?> cls, String str) throws NoSuchFieldException {
        try {
            Log.i(TAG, "clsType is " + cls);
            Field field = cls.getField(str);
            if (field != null) {
                return field;
            }
        } catch (Exception unused) {
        }
        for (Class<?> superclass = cls; superclass != null; superclass = superclass.getSuperclass()) {
            try {
                Log.i(TAG, "clsType is " + cls);
                Field declaredField = superclass.getDeclaredField(str);
                declaredField.setAccessible(true);
                return declaredField;
            } catch (NoSuchFieldException unused2) {
            }
        }
        throw new NoSuchFieldException();
    }

    public static Field findFieldNoThrow(Class<?> cls, String str) {
        try {
            return findField(cls, str);
        } catch (NoSuchFieldException unused) {
            return null;
        }
    }

    public static Method findMethod(Class<?> cls, String str, Class<?>... clsArr) throws NoSuchMethodException {
        try {
            Log.i(TAG, "clsType is " + cls);
            Method method = cls.getMethod(str, clsArr);
            if (method != null) {
                return method;
            }
        } catch (Exception unused) {
        }
        for (Class<?> superclass = cls; superclass != null; superclass = superclass.getSuperclass()) {
            try {
                Log.i(TAG, "clsType is " + cls);
                Method declaredMethod = superclass.getDeclaredMethod(str, clsArr);
                declaredMethod.setAccessible(true);
                return declaredMethod;
            } catch (NoSuchMethodException unused2) {
            }
        }
        throw new NoSuchMethodException();
    }

    public static Method findMethodNoThrow(Class<?> cls, String str, Class<?>... clsArr) {
        try {
            Method methodFindMethod = findMethod(cls, str, clsArr);
            try {
                methodFindMethod.setAccessible(true);
                return methodFindMethod;
            } catch (NoSuchMethodException unused) {
                return methodFindMethod;
            }
        } catch (NoSuchMethodException unused2) {
            return null;
        }
    }

    public static Class<?> getClass(String str) throws ClassNotFoundException {
        return Class.forName(str);
    }

    public static Object getField(Object obj, String str) {
        return getField(obj, str, obj.getClass());
    }

    public static <T> T getObjectField(Object obj, String str, Class<T> cls) {
        try {
            return (T) getObjectFieldOrThrow(obj, str, cls);
        } catch (Exception e2) {
            Log.e(TAG, "getObjectField error", e2);
            return null;
        }
    }

    public static Object getObjectFieldOrThrow(Object obj, String str) throws IllegalAccessException, NoSuchFieldException, SecurityException, IllegalArgumentException {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    private static Class[] getParameterTypes(Object... objArr) throws Exception {
        if (objArr == null) {
            return null;
        }
        Class[] clsArr = new Class[objArr.length];
        int length = objArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            Object obj = objArr[i2];
            if (obj instanceof Integer) {
                clsArr[i2] = Integer.TYPE;
            } else if (obj instanceof Byte) {
                clsArr[i2] = Byte.TYPE;
            } else if (obj instanceof Short) {
                clsArr[i2] = Short.TYPE;
            } else if (obj instanceof Float) {
                clsArr[i2] = Float.TYPE;
            } else if (obj instanceof Double) {
                clsArr[i2] = Double.TYPE;
            } else if (obj instanceof Character) {
                clsArr[i2] = Character.TYPE;
            } else if (obj instanceof Long) {
                clsArr[i2] = Long.TYPE;
            } else if (obj instanceof Boolean) {
                clsArr[i2] = Boolean.TYPE;
            } else {
                clsArr[i2] = obj.getClass();
            }
        }
        return clsArr;
    }

    public static Object getPrivateField(Object obj, String str) {
        return getPrivateField(obj, str, obj.getClass());
    }

    public static Class<?> loadClassNoThrow(ClassLoader classLoader, String str) {
        try {
            return classLoader.loadClass(str);
        } catch (ClassNotFoundException e2) {
            Log.w(TAG, "failed to loadClass " + str, e2);
            return null;
        }
    }

    public static Object getField(Object obj, String str, Class cls) {
        try {
            Field fieldFindField = findField(cls, str);
            fieldFindField.setAccessible(true);
            return fieldFindField.get(obj);
        } catch (Exception e2) {
            Log.w(TAG, "failed to callPrivateMethod " + obj.getClass() + Constants.DOT_SEPARATOR + str, e2);
            return null;
        }
    }

    public static Object getPrivateField(Object obj, String str, Class cls) {
        try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.get(obj);
        } catch (Exception e2) {
            Log.w(TAG, "failed to callPrivateMethod " + obj.getClass() + Constants.DOT_SEPARATOR + str, e2);
            return null;
        }
    }

    public static <T> T callObjectMethodOrThrow(Object obj, String str, Class<T> cls) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Class[] clsArr = new Class[0];
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, null);
        declaredMethod.setAccessible(true);
        return (T) declaredMethod.invoke(obj, null);
    }

    public static <T> T getObjectFieldOrThrow(Object obj, String str, Class<T> cls) throws IllegalAccessException, NoSuchFieldException, SecurityException, IllegalArgumentException {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return (T) declaredField.get(obj);
    }

    public static Object callPrivateMethod(Class cls, Object obj, String str, Object... objArr) {
        try {
            Method declaredMethod = cls.getDeclaredMethod(str, getParameterTypes(objArr));
            if (declaredMethod == null) {
                return null;
            }
            declaredMethod.setAccessible(true);
            return declaredMethod.invoke(obj, objArr);
        } catch (Exception e2) {
            Log.w(TAG, "failed to callPrivateMethod " + str.getClass() + Constants.DOT_SEPARATOR + str, e2);
            return null;
        }
    }

    public static Object callObjectMethodOrThrow(Object obj, String str, Class<?>[] clsArr, Object... objArr) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }
}
