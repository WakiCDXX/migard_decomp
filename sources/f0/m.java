package f0;

import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class m {
    public String a(Context context) {
        try {
            ContentProviderClient contentProviderClientAcquireContentProviderClient = context.getContentResolver().acquireContentProviderClient(Uri.parse("content://cn.nubia.identity/identity"));
            Bundle bundleCall = contentProviderClientAcquireContentProviderClient.call("getOAID", null, null);
            contentProviderClientAcquireContentProviderClient.close();
            return bundleCall.getInt("code", -1) == 0 ? bundleCall.getString("id") : "";
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("NubiaDeviceIDHelper", e2.getMessage());
            return "";
        }
    }
}
