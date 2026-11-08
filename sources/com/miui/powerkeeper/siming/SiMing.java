package com.miui.powerkeeper.siming;

import android.annotation.Nullable;
import android.os.HandlerThread;
import com.miui.powerkeeper.siming.shaosi.ShaoSi;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class SiMing {
    private static SiMing sInstance;
    protected HandlerThread mThread;
    private final ShaoSi ss;

    private SiMing() {
        HandlerThread handlerThread = new HandlerThread("SM");
        this.mThread = handlerThread;
        handlerThread.start();
        ShaoSi shaoSiCreateInstance = ShaoSi.createInstance(this.mThread.getLooper());
        this.ss = shaoSiCreateInstance;
        shaoSiCreateInstance.init();
    }

    public static synchronized SiMing getInstance() {
        try {
            if (sInstance == null) {
                sInstance = new SiMing();
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            printWriter.println("Invalid subdivision No.");
            return;
        }
        try {
            int i2 = Integer.parseInt(strArr[0]);
            printWriter.println("SiMing Version=0.1");
            if (strArr.length > 1 && (strArr[1].equals("enable") || strArr[1].equals("disable"))) {
                boolean zEquals = strArr[1].equals("disable");
                if (Subdivisions.isDisabled(i2) == zEquals) {
                    printWriter.println("No update.");
                    return;
                }
                Subdivisions.enable(i2, !zEquals);
                this.ss.update(i2);
                printWriter.println("Update success.");
                return;
            }
            try {
                IDivision division = getDivision(i2);
                if (division == null) {
                    if (!Subdivisions.isDisabled(i2)) {
                        printWriter.println("Can't find the subdivision: " + strArr[0]);
                        return;
                    }
                    printWriter.println("Subdivision " + Subdivisions.idToString(i2) + " is disabled.");
                    return;
                }
                if (strArr.length <= 1 || !strArr[1].equals("-d")) {
                    division.dump(fileDescriptor, printWriter, (String[]) Arrays.copyOfRange(strArr, 1, strArr.length));
                    return;
                }
                if (strArr.length != 2 && !strArr[2].equals("0")) {
                    division.debug(false);
                    printWriter.println("ID " + i2 + " is out debug mode.");
                    return;
                }
                division.debug(true);
                printWriter.println("ID " + i2 + " is in debug mode.");
            } catch (NumberFormatException unused) {
                printWriter.println("Division number format is not correct. number=" + strArr[0]);
            }
        } catch (NumberFormatException unused2) {
            printWriter.println("Division number format is not correct. number=" + strArr[0]);
        }
    }

    @Nullable
    public IDivision getDivision(int i2) {
        if (!Subdivisions.isDisabled(i2) && i2 >= 100 && i2 <= 999) {
            return this.ss.getDivision(i2);
        }
        return null;
    }
}
