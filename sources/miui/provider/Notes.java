package miui.provider;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.util.Log;
import com.xiaomi.onetrack.BuildConfig;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Notes {
    public static final String AUTHORITY = "notes";
    public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
    private static final String NOTES_PACKAGE_NAME = "com.miui.notes";
    private static final String TAG = "Notes";

    public interface Account extends BaseColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final Uri CONTENT_URI = Uri.parse("content://notes/account");
        public static final String DATA = "data";
    }

    public interface CallData extends Data {
        public static final String CALL_DATE = "data1";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/call_note";
        public static final String PHONE_NUMBER = "data3";
    }

    public interface Data extends BaseColumns {
        public static final String CONTENT = "content";
        public static final Uri CONTENT_URI;
        public static final Uri CONTENT_URI_FOR_SYNC_ADAPTER;
        public static final String CREATED_DATE = "created_date";
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
        public static final String DATA5 = "data5";
        public static final String DIRTY = "dirty";
        public static final String FILE_ID = "file_id";
        public static final String ID = "_id";
        public static final Uri MEDIA_URI;
        public static final String MIME_TYPE = "mime_type";
        public static final String MODIFIED_DATE = "modified_date";
        public static final String NOTE_ID = "note_id";

        static {
            Uri uri = Uri.parse("content://notes/data");
            CONTENT_URI = uri;
            CONTENT_URI_FOR_SYNC_ADAPTER = Notes.appendSyncAdapterFlag(uri);
            MEDIA_URI = Uri.parse("content://notes/data/media");
        }
    }

    public static class Intents {
        public static final String INTENT_ACTION_REFRESH_ALARM = "com.miui.notes.action.REFRESH_ALARM";
        public static final String INTENT_ACTION_REFRESH_WIDGET = "com.miui.notes.action.REFRESH_WIDGET";
        public static final String INTENT_EXTRA_ALERT_DATE = "com.miui.notes.alert_date";
        public static final String INTENT_EXTRA_BACKGROUND_ID = "com.miui.notes.background_color_id";
        public static final String INTENT_EXTRA_CALL_DATE = "com.miui.notes.call_date";
        public static final String INTENT_EXTRA_FOLDER_ID = "com.miui.notes.folder_id";
        public static final String INTENT_EXTRA_SNIPPET = "com.miui.notes.snippet";
        public static final String INTENT_EXTRA_SOURCE_INTENT = "com.miui.notes.source_intent";
        public static final String INTENT_EXTRA_SOURCE_NAME = "com.miui.notes.source_name";
        public static final String INTENT_EXTRA_WIDGET_ID = "com.miui.notes.widget_id";
        public static final String INTENT_EXTRA_WIDGET_TYPE = "com.miui.notes.widget_type";
    }

    public interface Note extends BaseColumns {
        public static final String ACCOUNT_ID = "account_id";
        public static final String ALERTED_DATE = "alert_date";
        public static final String ALERT_TAG = "alert_tag";
        public static final String BG_COLOR_ID = "bg_color_id";
        public static final Uri CONTENT_URI;
        public static final Uri CONTENT_URI_ATOMIC;
        public static final Uri CONTENT_URI_FOR_SYNC_ADAPTER;
        public static final String CREATED_DATE = "created_date";
        public static final String DELETION_TAG = "deletion_tag";
        public static final String HAS_ATTACHMENT = "has_attachment";
        public static final String ID = "_id";
        public static final int ID_CALL_RECORD_FOLDER = -2;
        public static final int ID_PRIVACY_FOLER = -4;
        public static final int ID_ROOT_FOLDER = 0;
        public static final int ID_TEMPARAY_FOLDER = -1;
        public static final int ID_TRASH_FOLER = -3;
        public static final String IN_VALID_FOLDER_SELECTION = "(parent_id>=0 OR parent_id=-2 OR parent_id=-4)";
        public static final String LOCAL_MODIFIED = "local_modified";
        public static final String MODIFIED_DATE = "modified_date";
        public static final String MOVED_DATE = "moved_date";
        public static final String NOTES_COUNT = "notes_count";
        public static final String ORIGIN_PARENT_ID = "origin_parent_id";
        public static final String PARENT_ID = "parent_id";
        public static final String PLAIN_TEXT = "plain_text";
        public static final String SNIPPET = "snippet";
        public static final String SOURCE_INTENT = "source_intent";
        public static final String SOURCE_NAME = "source_name";
        public static final String SOURCE_PACKAGE = "source_package";
        public static final String STICK_DATE = "stick_date";
        public static final String SUBJECT = "subject";
        public static final String SYNC_DATA1 = "sync_data1";
        public static final String SYNC_DATA2 = "sync_data2";
        public static final String SYNC_DATA3 = "sync_data3";
        public static final String SYNC_DATA4 = "sync_data4";
        public static final String SYNC_DATA5 = "sync_data5";
        public static final String SYNC_DATA6 = "sync_data6";
        public static final String SYNC_DATA7 = "sync_data7";
        public static final String SYNC_DATA8 = "sync_data8";
        public static final String SYNC_ID = "sync_id";
        public static final String SYNC_TAG = "sync_tag";
        public static final String THEME_ID = "theme_id";
        public static final String TYPE = "type";
        public static final int TYPE_FOLDER = 1;
        public static final int TYPE_NOTE = 0;
        public static final int TYPE_SYSTEM = 2;
        public static final int TYPE_WIDGET_INVALIDE = -1;
        public static final int TYPE_WIDGET_SIMPLE = 1;
        public static final String VALID_FOLDER_SELECTION = "((type=1 AND parent_id=0) OR _id=-2)";
        public static final String VERSION = "version";
        public static final String WIDGET_ID = "widget_id";
        public static final String WIDGET_TYPE = "widget_type";

        static {
            Uri uri = Uri.parse("content://notes/note");
            CONTENT_URI = uri;
            CONTENT_URI_FOR_SYNC_ADAPTER = Notes.appendSyncAdapterFlag(uri);
            CONTENT_URI_ATOMIC = Uri.parse("content://notes/note/atomic");
        }
    }

    public interface TextData extends Data {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/text_note";
    }

    public static final class Utils {
        public static final int CLEAR_ACCOUNT_WIPE_ALL = 0;
        public static final int CLEAR_ACCOUNT_WIPE_NONE = 2;
        public static final int CLEAR_ACCOUNT_WIPE_SYNC = 1;
        private static final int IMAGE_DIMENSION_MAX = 1920;
        private static final String KEY_DATA_BYTES = "data_bytes";
        private static final String KEY_DATA_VALUES = "data_values";

        private static void addDataValuesToNoteValues(ContentValues contentValues, ArrayList<ContentValues> arrayList) {
            if (arrayList == null || arrayList.isEmpty()) {
                return;
            }
            removeSnippetIfHasDataContent(contentValues, arrayList);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(KEY_DATA_VALUES, arrayList);
            contentValues.put(KEY_DATA_BYTES, marshall(bundle));
        }

        public static boolean clearAccount(Context context, boolean z2) {
            return clearAccount(context, z2 ? 0 : 2);
        }

        public static Bitmap createThumbnail(String str) throws Throwable {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmapScaleBitmap = scaleBitmap(str, options);
            if (bitmapScaleBitmap == null) {
                Log.e(Notes.TAG, "Fail to createThumbnail");
                return null;
            }
            Bitmap bitmapRotateBitmap = rotateBitmap(str, bitmapScaleBitmap, options.outMimeType);
            if (bitmapRotateBitmap != null) {
                return bitmapRotateBitmap;
            }
            Log.e(Notes.TAG, "Fail to rotateBitmap");
            return null;
        }

        private static ContentProviderOperation getAccountDeleteOP() {
            return ContentProviderOperation.newDelete(Account.CONTENT_URI).build();
        }

        private static String getContentFromData(ArrayList<ContentValues> arrayList) {
            for (ContentValues contentValues : arrayList) {
                if (contentValues.containsKey("mime_type") && TextData.CONTENT_ITEM_TYPE.equals(contentValues.getAsString("mime_type")) && contentValues.containsKey("content")) {
                    return contentValues.getAsString("content");
                }
            }
            return null;
        }

        private static ContentProviderOperation getDirtyUpdateOP() {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Data.DIRTY, (Integer) 1);
            contentValues.put("file_id", "");
            return ContentProviderOperation.newUpdate(Data.CONTENT_URI_FOR_SYNC_ADAPTER).withValues(contentValues).build();
        }

        private static ContentProviderOperation getNoteDeleteOP(boolean z2) {
            String str = "_id>0";
            if (z2) {
                str = "_id>0 AND sync_id>0 AND local_modified=0";
            }
            return ContentProviderOperation.newDelete(Note.CONTENT_URI_FOR_SYNC_ADAPTER).withSelection(str, null).build();
        }

        private static ContentProviderOperation getSyncClearOP() {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Note.SYNC_ID, (Integer) 0);
            contentValues.put(Note.SYNC_TAG, "");
            contentValues.put(Note.LOCAL_MODIFIED, (Integer) 1);
            contentValues.put("version", (Integer) 0);
            contentValues.put(Note.ORIGIN_PARENT_ID, (Integer) 0);
            contentValues.put("account_id", (Integer) 0);
            contentValues.put(Note.SYNC_DATA1, (Integer) 0);
            contentValues.put(Note.SYNC_DATA2, (Integer) 0);
            contentValues.put(Note.SYNC_DATA3, (Integer) 0);
            contentValues.put(Note.SYNC_DATA4, (Integer) 0);
            contentValues.put(Note.SYNC_DATA5, (Integer) 0);
            contentValues.put(Note.SYNC_DATA6, "");
            contentValues.put(Note.SYNC_DATA7, "");
            contentValues.put(Note.SYNC_DATA8, "");
            return ContentProviderOperation.newUpdate(Note.CONTENT_URI_FOR_SYNC_ADAPTER).withValues(contentValues).withSelection("_id>0", null).build();
        }

        private static ContentProviderOperation getTemporaryDeleteOP() {
            return ContentProviderOperation.newDelete(Note.CONTENT_URI_FOR_SYNC_ADAPTER).withSelection("not (parent_id>=0 OR parent_id=-2 OR parent_id=-4)", null).build();
        }

        public static int getTotalUnsyncedCount(Context context) {
            int i2 = 0;
            for (int i3 : getUnsyncedCount(context)) {
                i2 += i3;
            }
            return i2;
        }

        public static int[] getUnsyncedCount(Context context) {
            int i2;
            Cursor cursorQuery = context.getContentResolver().query(Note.CONTENT_URI_FOR_SYNC_ADAPTER, new String[]{"_id", "type"}, "local_modified=1 AND _id>0 AND (sync_id>0 OR (sync_id<=0 AND snippet<>''))", null, null);
            int i3 = 0;
            if (cursorQuery != null) {
                i2 = 0;
                while (cursorQuery.moveToNext()) {
                    try {
                        int i4 = cursorQuery.getInt(1);
                        if (i4 == 0) {
                            i3++;
                        } else if (i4 == 1) {
                            i2++;
                        }
                    } catch (Throwable th) {
                        cursorQuery.close();
                        throw th;
                    }
                }
                cursorQuery.close();
            } else {
                Log.e(Notes.TAG, "getUnsyncedCount: cursor is null");
                i2 = 0;
            }
            return new int[]{i3, i2};
        }

        public static Uri insertNoteAtomic(Context context, ContentValues contentValues, ArrayList<ContentValues> arrayList) {
            return insertNoteAtomic(context, contentValues, arrayList, false);
        }

        private static byte[] marshall(Bundle bundle) {
            Parcel parcelObtain = Parcel.obtain();
            try {
                bundle.writeToParcel(parcelObtain, 0);
                return parcelObtain.marshall();
            } finally {
                parcelObtain.recycle();
            }
        }

        private static void releaseCanvas(Canvas canvas) {
            try {
                Class[] clsArr = new Class[0];
                Method declaredMethod = Canvas.class.getDeclaredMethod(BuildConfig.BUILD_TYPE, null);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(canvas, null);
            } catch (Exception e2) {
                Log.w(Notes.TAG, "invoke Canvas.release failed", e2);
            }
        }

        public static ArrayList<ContentValues> removeDataValuesFromNoteValues(ContentValues contentValues) {
            String contentFromData;
            byte[] asByteArray = contentValues.getAsByteArray(KEY_DATA_BYTES);
            contentValues.remove(KEY_DATA_BYTES);
            if (asByteArray == null) {
                return null;
            }
            ArrayList<ContentValues> parcelableArrayList = unmarshall(asByteArray).getParcelableArrayList(KEY_DATA_VALUES);
            if (contentValues.containsKey(Note.SNIPPET) && contentValues.getAsString(Note.SNIPPET) == null && (contentFromData = getContentFromData(parcelableArrayList)) != null) {
                contentValues.put(Note.SNIPPET, contentFromData);
            }
            return parcelableArrayList;
        }

        private static void removeSnippetIfHasDataContent(ContentValues contentValues, ArrayList<ContentValues> arrayList) {
            String contentFromData;
            if (contentValues.containsKey(Note.SNIPPET) && (contentFromData = getContentFromData(arrayList)) != null && contentFromData.equals(contentValues.getAsString(Note.SNIPPET))) {
                contentValues.put(Note.SNIPPET, (String) null);
            }
        }

        /* JADX WARN: Code duplicated, block: B:43:0x008a  */
        private static Bitmap rotateBitmap(String str, Bitmap bitmap, String str2) throws Throwable {
            Canvas canvas;
            Throwable th;
            OutOfMemoryError e2;
            Bitmap bitmapCreateBitmap;
            if (!"image/jpeg".equals(str2)) {
                return bitmap;
            }
            try {
                int attributeInt = new ExifInterface(str).getAttributeInt("Orientation", 1);
                if (attributeInt == 1) {
                    return bitmap;
                }
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Matrix matrix = new Matrix();
                if (attributeInt != 3) {
                    if (attributeInt == 6) {
                        float f2 = height / 2;
                        matrix.postRotate(90.0f, f2, f2);
                    } else if (attributeInt == 8) {
                        float f3 = width / 2;
                        matrix.postRotate(270.0f, f3, f3);
                    }
                    height = width;
                    width = height;
                } else {
                    matrix.postRotate(180.0f, width / 2, height / 2);
                }
                try {
                    try {
                        bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        try {
                            canvas = new Canvas(bitmapCreateBitmap);
                            try {
                                try {
                                    canvas.drawBitmap(bitmap, matrix, null);
                                    bitmap.recycle();
                                } catch (OutOfMemoryError e3) {
                                    e2 = e3;
                                    Log.e(Notes.TAG, "Fail to rotateBitmap", e2);
                                    bitmap.recycle();
                                    if (canvas != null) {
                                    }
                                    return bitmapCreateBitmap;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                bitmap.recycle();
                                if (canvas != null) {
                                    releaseCanvas(canvas);
                                }
                                throw th;
                            }
                        } catch (OutOfMemoryError e4) {
                            canvas = null;
                            e2 = e4;
                        }
                    } catch (OutOfMemoryError e5) {
                        canvas = null;
                        e2 = e5;
                        bitmapCreateBitmap = null;
                    }
                    releaseCanvas(canvas);
                    return bitmapCreateBitmap;
                } catch (Throwable th3) {
                    canvas = null;
                    th = th3;
                    bitmap.recycle();
                    if (canvas != null) {
                        releaseCanvas(canvas);
                    }
                    throw th;
                }
            } catch (IOException e6) {
                Log.e(Notes.TAG, "createThumbnail fail", e6);
                return bitmap;
            }
        }

        private static Bitmap scaleBitmap(String str, BitmapFactory.Options options) throws Throwable {
            Bitmap bitmapScaleBitmapByRegion;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(str, options);
            int i2 = options.outWidth;
            int i3 = options.outHeight;
            if (i2 <= 0 || i3 <= 0) {
                return null;
            }
            try {
                if (i2 > IMAGE_DIMENSION_MAX || i3 > IMAGE_DIMENSION_MAX) {
                    float fMax = Math.max(i2, i3) / 1920.0f;
                    int iMax = Math.max(1, (int) (i2 / fMax));
                    int iMax2 = Math.max(1, (int) (i3 / fMax));
                    int i4 = 1;
                    for (int i5 = (int) fMax; i5 > 1; i5 >>= 1) {
                        i4 <<= 1;
                    }
                    bitmapScaleBitmapByRegion = (i2 * i3) / (i4 * i4) > 7372800 ? scaleBitmapByRegion(str, i2, i3, iMax, iMax2, i4) : miui.graphics.BitmapFactory.decodeBitmap(str, iMax, iMax2, false);
                } else {
                    bitmapScaleBitmapByRegion = miui.graphics.BitmapFactory.decodeBitmap(str, false);
                }
                return bitmapScaleBitmapByRegion;
            } catch (IOException e2) {
                Log.e(Notes.TAG, "Fail to decode " + str, e2);
                return null;
            }
        }

        /* JADX WARN: Code duplicated, block: B:46:0x00bf  */
        /* JADX WARN: Code duplicated, block: B:48:0x00c4  */
        /* JADX WARN: Code duplicated, block: B:50:0x00c9  */
        private static Bitmap scaleBitmapByRegion(String str, int i2, int i3, int i4, int i5, int i6) throws Throwable {
            Bitmap bitmap;
            Bitmap bitmapDecodeRegion;
            Bitmap bitmapCreateBitmap;
            Canvas canvas;
            BitmapRegionDecoder bitmapRegionDecoderNewInstance = BitmapRegionDecoder.newInstance(str, false);
            int i7 = i2 >> 1;
            int i8 = i3 >> 1;
            int i9 = i4 >> 1;
            int i10 = i5 >> 1;
            try {
                try {
                    Rect rect = new Rect();
                    Rect rect2 = new Rect();
                    bitmapCreateBitmap = Bitmap.createBitmap(i4, i5, Bitmap.Config.ARGB_8888);
                    try {
                        canvas = new Canvas(bitmapCreateBitmap);
                        try {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = i6;
                            rect.set(0, 0, i7, i8);
                            rect2.set(0, 0, i9, i10);
                            bitmapDecodeRegion = bitmapRegionDecoderNewInstance.decodeRegion(rect, options);
                            bitmap = null;
                            try {
                                try {
                                    canvas.drawBitmap(bitmapDecodeRegion, (Rect) null, rect2, (Paint) null);
                                    bitmapDecodeRegion.recycle();
                                    try {
                                        rect.set(i7, 0, i2, i8);
                                        rect2.set(i9, 0, i4, i10);
                                        Bitmap bitmapDecodeRegion2 = bitmapRegionDecoderNewInstance.decodeRegion(rect, options);
                                        canvas.drawBitmap(bitmapDecodeRegion2, (Rect) null, rect2, (Paint) null);
                                        bitmapDecodeRegion2.recycle();
                                        rect.set(0, i8, i7, i3);
                                        rect2.set(0, i10, i9, i5);
                                        Bitmap bitmapDecodeRegion3 = bitmapRegionDecoderNewInstance.decodeRegion(rect, options);
                                        bitmap = null;
                                        canvas.drawBitmap(bitmapDecodeRegion3, (Rect) null, rect2, (Paint) null);
                                        bitmapDecodeRegion3.recycle();
                                        rect.set(i7, i8, i2, i3);
                                        rect2.set(i9, i10, i4, i5);
                                        bitmapDecodeRegion = bitmapRegionDecoderNewInstance.decodeRegion(rect, options);
                                        canvas.drawBitmap(bitmapDecodeRegion, (Rect) null, rect2, (Paint) null);
                                        bitmapDecodeRegion.recycle();
                                        bitmapRegionDecoderNewInstance.recycle();
                                        bitmapDecodeRegion.recycle();
                                    } catch (OutOfMemoryError e2) {
                                        e = e2;
                                        bitmap = null;
                                        Log.e(Notes.TAG, "Fail to scaleBitmapByRegion", e);
                                        if (bitmapCreateBitmap != null) {
                                            bitmapCreateBitmap.recycle();
                                            bitmapCreateBitmap = bitmap;
                                        }
                                        if (bitmapRegionDecoderNewInstance != null) {
                                            bitmapRegionDecoderNewInstance.recycle();
                                        }
                                        if (bitmapDecodeRegion != null) {
                                            bitmapDecodeRegion.recycle();
                                        }
                                        if (canvas != null) {
                                        }
                                        return bitmapCreateBitmap;
                                    }
                                } catch (OutOfMemoryError e3) {
                                    e = e3;
                                }
                            } catch (Throwable th) {
                                th = th;
                                if (bitmapRegionDecoderNewInstance != null) {
                                    bitmapRegionDecoderNewInstance.recycle();
                                }
                                if (bitmapDecodeRegion != null) {
                                    bitmapDecodeRegion.recycle();
                                }
                                if (canvas != null) {
                                    releaseCanvas(canvas);
                                }
                                throw th;
                            }
                        } catch (OutOfMemoryError e4) {
                            e = e4;
                            bitmap = null;
                            bitmapDecodeRegion = null;
                        } catch (Throwable th2) {
                            th = th2;
                            bitmapDecodeRegion = null;
                            if (bitmapRegionDecoderNewInstance != null) {
                                bitmapRegionDecoderNewInstance.recycle();
                            }
                            if (bitmapDecodeRegion != null) {
                                bitmapDecodeRegion.recycle();
                            }
                            if (canvas != null) {
                                releaseCanvas(canvas);
                            }
                            throw th;
                        }
                    } catch (OutOfMemoryError e5) {
                        e = e5;
                        bitmap = null;
                        bitmapDecodeRegion = null;
                        canvas = null;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    bitmapDecodeRegion = null;
                    canvas = null;
                }
            } catch (OutOfMemoryError e6) {
                e = e6;
                bitmap = null;
                bitmapDecodeRegion = null;
                bitmapCreateBitmap = null;
                canvas = null;
            }
            releaseCanvas(canvas);
            return bitmapCreateBitmap;
        }

        private static Bundle unmarshall(byte[] bArr) {
            Parcel parcelObtain = Parcel.obtain();
            try {
                parcelObtain.unmarshall(bArr, 0, bArr.length);
                parcelObtain.setDataPosition(0);
                return parcelObtain.readBundle();
            } finally {
                parcelObtain.recycle();
            }
        }

        public static void updateAllAlarms(Context context) {
            Intent intent = new Intent(Intents.INTENT_ACTION_REFRESH_ALARM);
            intent.setPackage(Notes.NOTES_PACKAGE_NAME);
            context.sendBroadcast(intent);
        }

        public static void updateAllWidgets(Context context) {
            Intent intent = new Intent(Intents.INTENT_ACTION_REFRESH_WIDGET);
            intent.setPackage(Notes.NOTES_PACKAGE_NAME);
            context.sendBroadcast(intent);
        }

        public static int updateNoteAtomic(Context context, long j2, ContentValues contentValues, ArrayList<ContentValues> arrayList, String str, String[] strArr) {
            return updateNoteAtomic(context, j2, contentValues, arrayList, str, strArr, false);
        }

        public static boolean clearAccount(Context context, int i2) {
            ArrayList<ContentProviderOperation> arrayList = new ArrayList<>();
            arrayList.add(getAccountDeleteOP());
            if (i2 != 0) {
                if (i2 == 1) {
                    arrayList.add(getNoteDeleteOP(true));
                } else if (i2 != 2) {
                    Log.w(Notes.TAG, "Unknown wipeMode: " + i2);
                }
                arrayList.add(getTemporaryDeleteOP());
                arrayList.add(getSyncClearOP());
                arrayList.add(getDirtyUpdateOP());
            } else {
                arrayList.add(getNoteDeleteOP(false));
            }
            try {
                context.getContentResolver().applyBatch(Notes.AUTHORITY, arrayList);
                if (i2 != 2) {
                    updateAllWidgets(context);
                }
                return true;
            } catch (OperationApplicationException e2) {
                Log.e(Notes.TAG, "Fail to clear account", e2);
                return false;
            } catch (RemoteException e3) {
                Log.e(Notes.TAG, "Fail to clear account", e3);
                return false;
            }
        }

        public static Uri insertNoteAtomic(Context context, ContentValues contentValues, ArrayList<ContentValues> arrayList, boolean z2) {
            addDataValuesToNoteValues(contentValues, arrayList);
            Uri uriAppendSyncAdapterFlag = Note.CONTENT_URI_ATOMIC;
            if (z2) {
                uriAppendSyncAdapterFlag = Notes.appendSyncAdapterFlag(uriAppendSyncAdapterFlag);
            }
            return context.getContentResolver().insert(uriAppendSyncAdapterFlag, contentValues);
        }

        public static int updateNoteAtomic(Context context, long j2, ContentValues contentValues, ArrayList<ContentValues> arrayList, String str, String[] strArr, boolean z2) {
            addDataValuesToNoteValues(contentValues, arrayList);
            Uri uriWithAppendedId = ContentUris.withAppendedId(Note.CONTENT_URI_ATOMIC, j2);
            if (z2) {
                uriWithAppendedId = Notes.appendSyncAdapterFlag(uriWithAppendedId);
            }
            return context.getContentResolver().update(uriWithAppendedId, contentValues, str, strArr);
        }
    }

    public static Uri appendSyncAdapterFlag(Uri uri) {
        return uri.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build();
    }
}
