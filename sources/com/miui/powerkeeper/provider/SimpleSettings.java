package com.miui.powerkeeper.provider;

import android.content.AttributionSource;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.database.SQLException;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.BaseColumns;
import android.util.AndroidException;
import android.util.Log;
import com.miui.powerkeeper.utils.Utils;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class SimpleSettings {
    public static final String AUTHORITY = "com.miui.powerkeeper.configure/SimpleSettings";
    public static final String CALL_METHOD_USER_KEY = "_user";
    public static final String DIRECOTRY = "SimpleSettings";
    private static final boolean LOCAL_LOGV = false;
    private static final String TAG = "SimpleSettings";

    public static final class Misc extends NameValueTable {
        public static final String CALL_METHOD_DEL_MISC = "Del_misc";
        public static final String CALL_METHOD_GET_MISC = "GET_misc";
        public static final String CALL_METHOD_PUT_MISC = "PUT_misc";
        public static final Uri CONTENT_URI;
        public static final String TABLE = "misc";
        private static final NameValueCache sNameValueCache;

        static {
            Uri uri = Uri.parse("content://com.miui.powerkeeper.configure/SimpleSettings/misc");
            CONTENT_URI = uri;
            sNameValueCache = new NameValueCache(uri, CALL_METHOD_GET_MISC, CALL_METHOD_PUT_MISC, CALL_METHOD_DEL_MISC);
        }

        public static boolean contains(Context context, String str) {
            return containsForUser(context, str, UserHandle.myUserId());
        }

        public static boolean containsForUser(Context context, String str, int i2) {
            return getStringForUser(context, str, i2) != null;
        }

        public static boolean delete(Context context, String str) {
            return deleteForUser(context, str, UserHandle.myUserId());
        }

        public static boolean deleteForUser(Context context, String str, int i2) {
            return sNameValueCache.deleteStringForUser(context.getContentResolver(), str, i2);
        }

        public static boolean getBoolean(Context context, String str, boolean z2) {
            return getBooleanForUser(context, str, z2, UserHandle.myUserId());
        }

        public static boolean getBooleanForUser(Context context, String str, boolean z2, int i2) {
            String stringForUser = getStringForUser(context, str, i2);
            return stringForUser != null ? Boolean.parseBoolean(stringForUser) : z2;
        }

        public static float getFloat(Context context, String str, float f2) {
            return getFloatForUser(context, str, f2, UserHandle.myUserId());
        }

        public static float getFloatForUser(Context context, String str, float f2, int i2) {
            String stringForUser = getStringForUser(context, str, i2);
            if (stringForUser == null) {
                return f2;
            }
            try {
                return Float.parseFloat(stringForUser);
            } catch (NumberFormatException unused) {
                return f2;
            }
        }

        public static int getInt(Context context, String str, int i2) {
            return getIntForUser(context, str, i2, UserHandle.myUserId());
        }

        public static int getIntForUser(Context context, String str, int i2, int i3) {
            String stringForUser = getStringForUser(context, str, i3);
            if (stringForUser == null) {
                return i2;
            }
            try {
                return Integer.parseInt(stringForUser);
            } catch (NumberFormatException unused) {
                return i2;
            }
        }

        public static Set<Integer> getIntegerSet(String str) {
            return getIntegerSetForUser(str, UserHandle.myUserId());
        }

        public static Set<Integer> getIntegerSetForUser(String str, int i2) {
            String stringForUser = getStringForUser(Utils.APP_CONTEXT, str, i2);
            if (stringForUser == null) {
                return new HashSet(1);
            }
            String[] strArrSplit = stringForUser.split(",");
            HashSet hashSet = new HashSet(strArrSplit.length);
            for (String str2 : strArrSplit) {
                if (!str2.isEmpty()) {
                    try {
                        hashSet.add(Integer.valueOf(Integer.parseInt(str2)));
                    } catch (NumberFormatException unused) {
                    }
                }
            }
            return hashSet;
        }

        public static long getLong(Context context, String str, long j2) {
            return getLongForUser(context, str, j2, UserHandle.myUserId());
        }

        public static long getLongForUser(Context context, String str, long j2, int i2) {
            String stringForUser = getStringForUser(context, str, i2);
            if (stringForUser == null) {
                return j2;
            }
            try {
                return Long.parseLong(stringForUser);
            } catch (NumberFormatException unused) {
                return j2;
            }
        }

        public static String getString(Context context, String str) {
            return getStringForUser(context, str, UserHandle.myUserId());
        }

        public static String getStringForUser(Context context, String str, int i2) {
            return sNameValueCache.getStringForUser(context.getContentResolver(), str, i2);
        }

        public static Uri getUriFor(String str) {
            return NameValueTable.getUriFor(CONTENT_URI, str);
        }

        public static boolean putBoolean(Context context, String str, boolean z2) {
            return putBooleanForUser(context, str, z2, UserHandle.myUserId());
        }

        public static boolean putBooleanForUser(Context context, String str, boolean z2, int i2) {
            return putStringForUser(context, str, Boolean.toString(z2), i2);
        }

        public static boolean putFloat(Context context, String str, float f2) {
            return putFloatForUser(context, str, f2, UserHandle.myUserId());
        }

        public static boolean putFloatForUser(Context context, String str, float f2, int i2) {
            return putStringForUser(context, str, Float.toString(f2), i2);
        }

        public static boolean putInt(Context context, String str, int i2) {
            return putIntForUser(context, str, i2, UserHandle.myUserId());
        }

        public static boolean putIntForUser(Context context, String str, int i2, int i3) {
            return putStringForUser(context, str, Integer.toString(i2), i3);
        }

        public static boolean putIntegerSet(String str, Set<Integer> set) {
            return putIntegerSetForUser(str, set, UserHandle.myUserId());
        }

        public static boolean putIntegerSetForUser(String str, Set<Integer> set, int i2) {
            String string = null;
            if (set != null) {
                for (Integer num : set) {
                    string = string == null ? Integer.toString(num.intValue()) : string + ',' + num;
                }
            }
            Context context = Utils.APP_CONTEXT;
            if (string == null) {
                string = "";
            }
            return putStringForUser(context, str, string, i2);
        }

        public static boolean putLong(Context context, String str, long j2) {
            return putLongForUser(context, str, j2, UserHandle.myUserId());
        }

        public static boolean putLongForUser(Context context, String str, long j2, int i2) {
            return putStringForUser(context, str, Long.toString(j2), i2);
        }

        public static boolean putString(Context context, String str, String str2) {
            return putStringForUser(context, str, str2, UserHandle.myUserId());
        }

        public static boolean putStringForUser(Context context, String str, String str2, int i2) {
            return sNameValueCache.putStringForUser(context.getContentResolver(), str, str2, i2);
        }

        public static boolean getBoolean(Context context, String str) throws SimpleSettingNotFoundException {
            return getBooleanForUser(context, str, UserHandle.myUserId());
        }

        public static float getFloat(Context context, String str) throws SimpleSettingNotFoundException {
            return getFloatForUser(context, str, UserHandle.myUserId());
        }

        public static int getInt(Context context, String str) throws SimpleSettingNotFoundException {
            return getIntForUser(context, str, UserHandle.myUserId());
        }

        public static long getLong(Context context, String str) throws SimpleSettingNotFoundException {
            return getLongForUser(context, str, UserHandle.myUserId());
        }

        public static String getString(Context context, String str, String str2) {
            return getStringForUser(context, str, str2, UserHandle.myUserId());
        }

        public static String getStringForUser(Context context, String str, String str2, int i2) {
            String stringForUser = getStringForUser(context, str, i2);
            return stringForUser != null ? stringForUser : str2;
        }

        public static boolean getBooleanForUser(Context context, String str, int i2) throws SimpleSettingNotFoundException {
            String stringForUser = getStringForUser(context, str, i2);
            if (stringForUser != null) {
                return Boolean.parseBoolean(stringForUser);
            }
            throw new SimpleSettingNotFoundException(str);
        }

        public static float getFloatForUser(Context context, String str, int i2) throws SimpleSettingNotFoundException {
            String stringForUser = getStringForUser(context, str, i2);
            if (stringForUser != null) {
                try {
                    return Float.parseFloat(stringForUser);
                } catch (NumberFormatException unused) {
                    throw new SimpleSettingNotFoundException(str);
                }
            }
            throw new SimpleSettingNotFoundException(str);
        }

        public static int getIntForUser(Context context, String str, int i2) throws SimpleSettingNotFoundException {
            String stringForUser = getStringForUser(context, str, i2);
            if (stringForUser != null) {
                try {
                    return Integer.parseInt(stringForUser);
                } catch (NumberFormatException unused) {
                    throw new SimpleSettingNotFoundException(str);
                }
            }
            throw new SimpleSettingNotFoundException(str);
        }

        public static long getLongForUser(Context context, String str, int i2) throws SimpleSettingNotFoundException {
            String stringForUser = getStringForUser(context, str, i2);
            if (stringForUser != null) {
                try {
                    return Long.parseLong(stringForUser);
                } catch (NumberFormatException unused) {
                    throw new SimpleSettingNotFoundException(str);
                }
            }
            throw new SimpleSettingNotFoundException(str);
        }
    }

    private static class NameValueCache {
        private final String mCallDelCommand;
        private final String mCallGetCommand;
        private final String mCallSetCommand;
        private IContentProvider mContentProvider = null;
        private final Uri mUri;

        public NameValueCache(Uri uri, String str, String str2, String str3) {
            this.mUri = uri;
            this.mCallGetCommand = str;
            this.mCallSetCommand = str2;
            this.mCallDelCommand = str3;
        }

        private IContentProvider lazyGetProvider(ContentResolver contentResolver) {
            IContentProvider iContentProviderAcquireProvider;
            synchronized (this) {
                try {
                    iContentProviderAcquireProvider = this.mContentProvider;
                    if (iContentProviderAcquireProvider == null) {
                        iContentProviderAcquireProvider = contentResolver.acquireProvider(this.mUri.getAuthority());
                        this.mContentProvider = iContentProviderAcquireProvider;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            return iContentProviderAcquireProvider;
        }

        public boolean deleteStringForUser(ContentResolver contentResolver, String str, int i2) {
            try {
                Bundle bundle = new Bundle();
                bundle.putInt(SimpleSettings.CALL_METHOD_USER_KEY, i2);
                lazyGetProvider(contentResolver).call(new AttributionSource(Binder.getCallingUid(), contentResolver.getPackageName(), null), this.mUri.getAuthority(), this.mCallDelCommand, str, bundle);
                return true;
            } catch (RemoteException e2) {
                Log.w(SimpleSettings.TAG, "Can't delete key " + str + " in " + this.mUri, e2);
                return false;
            }
        }

        public String getStringForUser(ContentResolver contentResolver, String str, int i2) {
            Bundle bundle;
            boolean z2 = i2 == UserHandle.myUserId();
            IContentProvider iContentProviderLazyGetProvider = lazyGetProvider(contentResolver);
            if (this.mCallGetCommand != null) {
                if (z2) {
                    bundle = null;
                } else {
                    try {
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt(SimpleSettings.CALL_METHOD_USER_KEY, i2);
                        bundle = bundle2;
                    } catch (RemoteException e2) {
                        Log.w(SimpleSettings.TAG, "Can't get key " + str + " in " + this.mUri, e2);
                    }
                }
                Bundle bundleCall = iContentProviderLazyGetProvider.call(new AttributionSource(Binder.getCallingUid(), contentResolver.getPackageName(), null), this.mUri.getAuthority(), this.mCallGetCommand, str, bundle);
                if (bundleCall != null) {
                    return bundleCall.getPairValue();
                }
            }
            return null;
        }

        public boolean putStringForUser(ContentResolver contentResolver, String str, String str2, int i2) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("value", str2);
                bundle.putInt(SimpleSettings.CALL_METHOD_USER_KEY, i2);
                lazyGetProvider(contentResolver).call(new AttributionSource(Binder.getCallingUid(), contentResolver.getPackageName(), null), this.mUri.getAuthority(), this.mCallSetCommand, str, bundle);
                return true;
            } catch (RemoteException e2) {
                Log.w(SimpleSettings.TAG, "Can't set key " + str + " in " + this.mUri, e2);
                return false;
            }
        }
    }

    public static class NameValueTable implements BaseColumns {
        public static final String NAME = "name";
        public static final String SELECTION = "name = ? AND userId = ?";
        public static final String USER = "userId";
        public static final String VALUE = "value";
        public static final String[] VALUE_COLUMN = {"value"};

        public static Uri getUriFor(Uri uri, String str) {
            return Uri.withAppendedPath(uri, str);
        }

        protected static boolean putString(ContentResolver contentResolver, Uri uri, String str, String str2) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", str);
                contentValues.put("value", str2);
                contentResolver.insert(uri, contentValues);
                return true;
            } catch (SQLException e2) {
                Log.w(SimpleSettings.TAG, "Can't set key " + str + " in " + uri, e2);
                return false;
            }
        }
    }

    public static class SimpleSettingNotFoundException extends AndroidException {
        public SimpleSettingNotFoundException(String str) {
            super(str);
        }
    }
}
