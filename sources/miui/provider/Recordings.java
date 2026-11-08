package miui.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import miui.date.DateUtils;
import miui.os.Environment;
import miui.os.FileUtils;
import miui.util.HashUtils;

/* JADX INFO: loaded from: classes.dex */
public class Recordings {
    public static final String APP_RECORD_DIR;
    public static final String AUTHORITY = "records";
    public static final String CALL_RECORD_DIR;
    public static final String FM_RECORD_DIR;
    private static final String[] HEXDIGITS;
    private static final String MEDIA_SCANNER_CLASS = "com.android.providers.media.MediaScannerReceiver";
    private static final String MEDIA_SCANNER_PACKAGE = "com.android.providers.media";
    public static final String RECORDER_ROOT_PATH;
    public static final String SAMPLE_DEFAULT_DIR = "/sound_recorder";
    private static final String TAG = "SoundRecorder:SoundRecorder";

    public static class CachedAccount {
        public static final Uri CONTENT_URI = Uri.parse("content://records/cached_account");
        public static final String TABLE_NAME = "cached_account";
        public static final String URI_PATH = "cached_account";

        public static final class Columns implements BaseColumns {
            public static final String ACCOUNT_NAME = "account_name";
        }
    }

    public static class CallRecords {
        public static final Uri CONTENT_URI = Uri.parse("content://records/call_records");
        public static final String TABLE_NAME = "call_records";
        public static final String URI_PATH = "call_records";

        public static final class Columns implements BaseColumns {
            public static final String NUMBER = "number";
            public static final String RECORD_ID = "record_id";
        }
    }

    public static class CallRecordsView {
        public static final Uri CONTENT_URI = Uri.parse("content://records/call_records_view");
        public static final String URI_PATH = "call_records_view";
        public static final String VIEW_NAME = "call_records_view";
    }

    public static class Downloads {
        public static final Uri CONTENT_URI = Uri.parse("content://records/downloads");
        public static final String TABLE_NAME = "downloads";
        public static final String URI_PATH = "downloads";

        public static final class Columns implements BaseColumns {
            public static final String FILE_ID = "file_id";
            public static final String FILE_NAME = "file_name";
            public static final String FILE_PATH = "file_path";
            public static final String FILE_SIZE = "file_size";
            public static final String PROGRESS = "progress";
            public static final String REC_ID = "rec_id";
            public static final String STATUS = "status";
        }

        public static final class Status {
            public static final int Downloading = 1;
            public static final int Failed = 4;
            public static final int Paused = 3;
            public static final int Pendding = 2;
            public static final int PenddingByNetwork = 5;
            public static final int Success = 0;
        }
    }

    public static class MarkPoints {
        public static final Uri CONTENT_URI = Uri.parse("content://records/mark_points");
        public static final String TABLE_NAME = "mark_points";
        public static final String URI_PATH = "mark_points";

        public static final class Columns implements BaseColumns {
            public static final String DESCRIPTION = "desp";
            public static final String E_TAG = "e_tag";
            public static final String FILE_SHA1 = "file_sha1";
            public static final String PATH = "path";
            public static final String RECORD_ID = "record_id";
            public static final String SYNC_DIRTY = "sync_dirty";
            public static final String TIME_POINT = "time_point";
            public static final String TYPE = "type";
        }

        public static final class SyncDirty {
            public static final int DIRTY = 1;
            public static final int SYNCED = 0;
            public static final int SYNC_ERROR = 2;
        }
    }

    public static class MarkpointsOperations {
        public static final Uri CONTENT_URI = Uri.parse("content://records/markpoint_operations");
        public static final String TABLE_NAME = "markpoint_operations";
        public static final String URI_PATH = "markpoint_operations";

        public static final class Columns implements BaseColumns {
            public static final String CLOUD_RECORD_ID = "cloud_record_id";
            public static final String E_TAG = "e_tag";
            public static final String OPER = "oper";
            public static final String REC_ID = "rec_id";
        }

        public static final class Opers {
            public static final int ADD = 103;
            public static final int DELETE = 101;
            public static final int UPDATE = 102;
        }
    }

    public static class Operations {
        public static final Uri CONTENT_URI = Uri.parse("content://records/operations");
        public static final String TABLE_NAME = "operations";
        public static final String URI_PATH = "operations";

        public static final class Columns implements BaseColumns {
            public static final String DESC = "decs";
            public static final String FILE_ID = "file_id";
            public static final String OPER = "oper";
            public static final String REC_ID = "rec_id";
        }

        public static final class Opers {
            public static final int DELETE = 0;
            public static final int RENAME = 1;
        }
    }

    public static class RecordingNotifications {
        public static final Uri CONTENT_URI = Uri.parse("content://records/recordingnotifications");
        public static final String EXTRA_DIRPATH = "extra_dirpath";
        public static final String EXTRA_RECTYPE = "extra_rectype";
        public static final String TABLE_NAME = "recordingnotifications";
        public static final String URI_PATH = "recordingnotifications";

        public static final class Columns implements BaseColumns {
            public static final String CNT_UNREAD = "cnt_unread";
            public static final String NOTIF_DESC = "NOTIF_DESC";
            public static final String REC_TYPE = "rec_type";
        }
    }

    public static class Records {
        public static final Uri CONTENT_URI = Uri.parse("content://records/records");
        public static final String TABLE_NAME = "records";
        public static final String URI_PATH = "records";

        public static final class Columns implements BaseColumns {
            public static final String CLOUD_SYNC_TIME = "cloud_sync_time";
            public static final String CONTENT = "content";
            public static final String CREATE_TIME = "create_time";
            public static final String DB_SYNC_TIME = "db_sync_time";
            public static final String DURATION = "duration";
            public static final String FILE_ID = "file_id";
            public static final String FILE_NAME = "file_name";
            public static final String FILE_PATH = "file_path";
            public static final String FILE_SIZE = "file_size";
            public static final String IN_CLOUD = "in_cloud";
            public static final String IN_LOCAL = "in_local";
            public static final String REC_DESC = "rec_desc";
            public static final String REC_TYPE = "rec_type";
            public static final String SHA1 = "sha1";
            public static final String SYNC_DIRTY = "sync_dirty";
        }

        public static final class InCloud {
            public static final int IN_CLOUD = 1;
            public static final int NOT_IN_CLOUD = 0;
        }

        public static final class InLocal {
            public static final int IN_LOCAL = 1;
            public static final int NOT_IN_LOCAL = 0;
        }

        public static final class Order {
            public static final String BY_CTREAT_TIME_DESC = "cloud_sync_time DESC";
        }

        public static final class RecType {
            public static final int APP = 3;
            public static final int CALL = 1;
            public static final int FM = 2;
            public static final int NORMAL = 0;
        }

        public static final class SyncDirty {
            public static final int DIRTY = 1;
            public static final int SYNCED = 0;
        }
    }

    public static class SyncTokens {
        public static final Uri CONTENT_URI = Uri.parse("content://records/synctokens");
        public static final String OLD_TABLE_NAME = "markpoint_synctoken";
        public static final String TABLE_NAME = "synctokens";
        public static final String URI_PATH = "synctokens";

        public static final class Columns implements BaseColumns {
            public static final String SYNC_EXTRA_INFO = "sync_extra_info";
            public static final String SYNC_TOKEN = "sync_token";
            public static final String SYNC_TOKEN_TYPE = "sync_token_type";
            public static final String WATER_MARK = "water_mark";
        }

        public static final class TokenType {
            public static final int TYPE_FILE_LIST = 1;
            public static final int TYPE_MARK_POINT = 0;
        }
    }

    static {
        String str = Environment.getExternalStorageMiuiDirectory().getAbsolutePath() + SAMPLE_DEFAULT_DIR;
        RECORDER_ROOT_PATH = str;
        CALL_RECORD_DIR = str + "/call_rec";
        FM_RECORD_DIR = str + "/fm_rec";
        APP_RECORD_DIR = str + "/app_rec";
        HEXDIGITS = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", RegisterAppChannelInfoCollectionJob.JSON_KEY_APP_ID, "b", "c", "d", "e", "f"};
    }

    private static String byteArrayToHexString(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (int i2 = 0; i2 < bArr.length; i2++) {
            String[] strArr = HEXDIGITS;
            stringBuffer.append(strArr[(bArr[i2] >>> 4) & 15]);
            stringBuffer.append(strArr[bArr[i2] & 15]);
        }
        return stringBuffer.toString();
    }

    public static int getNotificationUnreadCount(Context context, String str) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursorQuery = null;
        try {
            cursorQuery = contentResolver.query(RecordingNotifications.CONTENT_URI, new String[]{RecordingNotifications.Columns.CNT_UNREAD}, "rec_type=?", new String[]{str}, null, null);
            int i2 = 0;
            if (cursorQuery != null && cursorQuery.getCount() == 1) {
                cursorQuery.moveToFirst();
                i2 = cursorQuery.getInt(0);
            }
            return i2;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:31:0x0046 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static String getSha1(File file) throws Throwable {
        FileInputStream fileInputStream;
        String strByteArrayToHexString = null;
        strByteArrayToHexString = null;
        strByteArrayToHexString = null;
        FileInputStream fileInputStream2 = null;
        try {
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.SHA1);
                        byte[] bArr = new byte[DateUtils.FORMAT_ABBREV_WEEKDAY];
                        while (true) {
                            int i2 = fileInputStream.read(bArr);
                            if (i2 < 0) {
                                break;
                            }
                            messageDigest.update(bArr, 0, i2);
                        }
                        strByteArrayToHexString = byteArrayToHexString(messageDigest.digest());
                        fileInputStream.close();
                    } catch (Exception e2) {
                        e = e2;
                        Log.e(TAG, "Exception when getSha1", e);
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        return strByteArrayToHexString;
                    }
                } catch (Throwable th) {
                    th = th;
                    fileInputStream2 = fileInputStream;
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (IOException e3) {
                            Log.e(TAG, "Exception when close inputstream", e3);
                        }
                    }
                    throw th;
                }
            } catch (IOException e4) {
                Log.e(TAG, "Exception when close inputstream", e4);
            }
        } catch (Exception e5) {
            e = e5;
            fileInputStream = null;
        } catch (Throwable th2) {
            th = th2;
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            throw th;
        }
        return strByteArrayToHexString;
    }

    public static void notifyRecording(Context context, String str, long j2) throws Throwable {
        int i2;
        if (str == null) {
            return;
        }
        File file = new File(str);
        if (str.startsWith(CALL_RECORD_DIR)) {
            i2 = 1;
        } else if (str.startsWith(FM_RECORD_DIR)) {
            i2 = 2;
        } else if (str.startsWith(APP_RECORD_DIR)) {
            i2 = 3;
        } else {
            i2 = str.startsWith(RECORDER_ROOT_PATH) ? 0 : -1;
        }
        if (!file.exists() || i2 == -1) {
            return;
        }
        FileUtils.addNoMedia(RECORDER_ROOT_PATH);
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setClassName(MEDIA_SCANNER_PACKAGE, MEDIA_SCANNER_CLASS);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
        String sha1 = getSha1(file);
        if (TextUtils.isEmpty(sha1)) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("file_path", str);
        contentValues.put("file_name", file.getName());
        contentValues.put(Records.Columns.CREATE_TIME, Long.valueOf(file.lastModified()));
        contentValues.put("rec_type", Integer.valueOf(i2));
        contentValues.put(Records.Columns.DB_SYNC_TIME, Long.valueOf(System.currentTimeMillis()));
        contentValues.put("duration", Long.valueOf(j2 / 1000));
        contentValues.put("sync_dirty", (Integer) 1);
        contentValues.put(Records.Columns.IN_LOCAL, (Integer) 1);
        contentValues.put(Records.Columns.IN_CLOUD, (Integer) 0);
        contentValues.put(Records.Columns.SHA1, sha1);
        context.getContentResolver().insert(Records.CONTENT_URI, contentValues);
    }

    public static void setNotificationUnreadCount(Context context, String str, int i2, String str2) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] strArr = {RecordingNotifications.Columns.CNT_UNREAD};
        String[] strArr2 = {str};
        Cursor cursorQuery = null;
        try {
            Uri uri = RecordingNotifications.CONTENT_URI;
            cursorQuery = contentResolver.query(uri, strArr, "rec_type=?", strArr2, null, null);
            boolean z2 = (cursorQuery == null || cursorQuery.getCount() == 0) ? false : true;
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(RecordingNotifications.Columns.CNT_UNREAD, Integer.valueOf(i2));
            if (str2 != null) {
                contentValues.put(RecordingNotifications.Columns.NOTIF_DESC, str2);
            }
            if (z2) {
                contentResolver.update(uri, contentValues, "rec_type=?", strArr2);
            } else {
                contentValues.put("rec_type", str);
                contentResolver.insert(uri, contentValues);
            }
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }
}
