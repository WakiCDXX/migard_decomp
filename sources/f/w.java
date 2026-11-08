package f;

import android.content.Context;
import android.os.HandlerThread;
import android.util.Log;
import com.miui.powerkeeper.statemachine.PowerScheduleController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

/* JADX INFO: compiled from: PowerScheduleModule.java */
/* JADX INFO: loaded from: classes.dex */
public class w extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private PowerScheduleController f1817c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private HandlerThread f1818d = null;

    public j b(Context context) {
        this.f1812b = context;
        if (!e.f.n()) {
            return null;
        }
        HandlerThread handlerThread = new HandlerThread("schedule_task");
        this.f1818d = handlerThread;
        handlerThread.start();
        this.f1817c = PowerScheduleController.init(this.f1812b, this.f1818d.getLooper());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->PowerSchedule", " destroy");
        if (this.f1811a) {
            if (this.f1818d != null) {
                try {
                    PowerScheduleController powerScheduleController = this.f1817c;
                    if (powerScheduleController != null) {
                        powerScheduleController.destroy();
                        this.f1817c.removeCallbacksAndMessages(null);
                        this.f1817c = null;
                    }
                    this.f1818d.quitSafely();
                    this.f1818d = null;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->PowerSchedule", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1817c == null) {
            printWriter.println("PowerScheduleController disabled");
        } else {
            printWriter.println("PowerScheduleController enabled");
            this.f1817c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
