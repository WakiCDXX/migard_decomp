package miui.provider;

import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import java.util.List;
import miui.accounts.ExtraAccountManager;

/* JADX INFO: loaded from: classes.dex */
public abstract class CloudPushProvider extends ContentProvider {
    public static final String NAME_COLUMNS = "name";
    public static final int NAME_COLUMNS_INDEX = 0;
    public static final String[] PROJECTION = {"name", "value", "type"};
    public static final String TYPE_COLUMNS = "type";
    public static final int TYPE_COLUMNS_INDEX = 2;
    public static final String VALUE_COLUMNS = "value";
    public static final int VALUE_COLUMNS_INDEX = 1;
    private static final int WATERMARK_LIST = 1;
    private static final String WATERMARK_LIST_PATH = "watermark_list";

    public static class Watermark {
        public String mName;
        public String mType;
        public long mValue;

        public Watermark(String str, long j2, String str2) {
            this.mName = str;
            this.mValue = j2;
            this.mType = str2;
        }
    }

    private MatrixCursor getWatermarkListCursor(Account account) {
        if (account == null) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(PROJECTION);
        for (Watermark watermark : getWatermarkList(getContext(), account)) {
            matrixCursor.addRow(new Object[]{watermark.mName, Long.valueOf(watermark.mValue), watermark.mType});
        }
        return matrixCursor;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    protected abstract String getAuthority();

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    protected abstract List<Watermark> getWatermarkList(Context context, Account account);

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        UriMatcher uriMatcher = new UriMatcher(-1);
        uriMatcher.addURI(getAuthority(), WATERMARK_LIST_PATH, 1);
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(getContext());
        if (uriMatcher.match(uri) == 1) {
            return getWatermarkListCursor(xiaomiAccount);
        }
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
