package f0;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class q {
    public String a(Context context) {
        String string = "";
        try {
            Cursor cursorQuery = context.getContentResolver().query(Uri.parse("content://com.vivo.vms.IdProvider/IdentifierId/OAID"), null, null, null, null);
            if (cursorQuery != null) {
                string = cursorQuery.moveToNext() ? cursorQuery.getString(cursorQuery.getColumnIndex("value")) : "";
                cursorQuery.close();
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("VivoDeviceIDHelper", e2.getMessage());
        }
        return string;
    }
}
