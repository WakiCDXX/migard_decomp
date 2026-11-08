package t;

/* JADX INFO: loaded from: classes.dex */
public class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static String f2359a = "ConfigProvider";

    public static int a(int i2) {
        int iIntValue;
        if (com.ot.pubsub.util.k.f727d) {
            com.ot.pubsub.util.k.c(f2359a, "debug upload mode, send events immediately");
            return 0;
        }
        try {
            iIntValue = d.d().get(Integer.valueOf(i2 + 1)).intValue();
        } catch (Exception unused) {
            iIntValue = 60000;
        }
        com.ot.pubsub.util.k.c(f2359a, "getUploadInterval " + iIntValue);
        return iIntValue;
    }
}
