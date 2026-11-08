package com.miui.powerkeeper.active;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.provider.PowerKeeperConfigure;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ActiveFakeProvider {
    private static final String[] ACTIVE_INFO_COLUMN_NAMES = {"uid", "type"};
    private static final int ACTIVE_LIST_BY_APP = 1;
    private static final int ACTIVE_LIST_BY_TYPE = 0;
    public static final String KEY_PIDS = "pids";
    public static final String METHOD_GET_AUDIO_ACTIVE_PIDS = "getActiveAudioPids";
    private static final UriMatcher sMatcher;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sMatcher = uriMatcher;
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "active/bytype/#", 0);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "active/byapp/#", 1);
    }

    private int getInt(Uri uri) {
        try {
            return Integer.parseInt(uri.getLastPathSegment());
        } catch (NumberFormatException unused) {
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        if (!METHOD_GET_AUDIO_ACTIVE_PIDS.equals(str)) {
            return null;
        }
        ArrayList<Integer> activeAudioPids = PowerKeeperManager.getInstance().getActiveController().getActiveAudioPids();
        Bundle bundle2 = new Bundle();
        bundle2.putIntegerArrayList(KEY_PIDS, activeAudioPids);
        return bundle2;
    }

    public boolean checkMethod(String str) {
        return METHOD_GET_AUDIO_ACTIVE_PIDS.equals(str);
    }

    public boolean checkUri(Uri uri) {
        int iMatch = sMatcher.match(uri);
        return iMatch == 0 || iMatch == 1;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        MatrixCursor matrixCursor = new MatrixCursor(ACTIVE_INFO_COLUMN_NAMES);
        int iMatch = sMatcher.match(uri);
        if (iMatch == 0) {
            PowerKeeperManager.getInstance().getActiveController().loadActiveListByType(matrixCursor, getInt(uri));
        } else {
            if (iMatch != 1) {
                matrixCursor.close();
                return null;
            }
            PowerKeeperManager.getInstance().getActiveController().loadActiveListByApp(matrixCursor, getInt(uri));
        }
        return matrixCursor;
    }
}
