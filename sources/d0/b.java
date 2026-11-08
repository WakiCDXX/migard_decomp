package d0;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.ot.pubsub.util.k;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class b extends Handler {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static int f1758a = 10;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static int f1759b = 10000;

    public b(Looper looper) {
        super(looper);
    }

    private void b(int i2, long j2) {
        removeMessages(i2);
        k.c("UploadTimer", "will post msg, prio=" + i2 + ", delay=" + j2);
        sendEmptyMessageDelayed(i2, j2);
    }

    public void a() {
        if (hasMessages(10)) {
            k.c("UploadTimer", "has delayed msg, return");
        } else {
            b(10, new Random().nextInt(f1758a) * 1000);
        }
    }

    public void c(int i2, boolean z2) {
        if (z2) {
            removeMessages(i2);
        } else if (hasMessages(i2)) {
            k.c("UploadTimer", "has delayed msg:" + i2);
            return;
        }
        long jA = z2 ? 0L : t.k.a(i2);
        k.c("UploadTimer", "will check prio=" + i2 + ", delay=" + jA);
        b(i2, jA);
    }

    public void d(boolean z2) {
        if (z2) {
            removeMessages(11);
        } else if (hasMessages(11)) {
            k.c("UploadTimer", "has delayed msg, return");
            return;
        }
        b(11, z2 ? 0L : e());
    }

    public int e() {
        return f1759b;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        super.handleMessage(message);
        int i2 = message.what;
        if (i2 == 10) {
            d.a().k();
            d.a().e(0);
            d.a().e(1);
            return;
        }
        if (i2 == 11) {
            d.a().k();
            k.c("UploadTimer", "UploadTimer.handleMessage, msg.what=" + message.what);
            return;
        }
        if (i2 == 0 || i2 == 1 || i2 == 2) {
            d.a().e(i2);
            k.c("UploadTimer", "UploadTimer.handleMessage, msg.what=" + message.what);
        }
    }
}
