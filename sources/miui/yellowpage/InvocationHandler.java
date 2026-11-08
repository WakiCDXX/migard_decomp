package miui.yellowpage;

import android.content.Context;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public abstract class InvocationHandler {
    public static Bundle invoke(Context context, String str) {
        return invoke(context, str, null, null);
    }

    public static Bundle invoke(Context context, String str, String str2) {
        return invoke(context, str, str2, null);
    }

    public static Bundle invoke(Context context, String str, String str2, Bundle bundle) {
        try {
            Bundle bundleCall = context.getContentResolver().call(YellowPageContract.INVOACTION_URI, str, str2, bundle);
            if (bundleCall != null) {
                return bundleCall;
            }
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        }
        return new Bundle();
    }
}
