package com.xiaomi.push.scene;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.view.Display;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(17)
class SceneUtils {
    SceneUtils() {
    }

    public static int getAudioMode(Context context) {
        try {
            return ((AudioManager) context.getSystemService("audio")).getMode();
        } catch (Exception e2) {
            SceneLog.w("get audio mode error:" + e2);
            return -2;
        }
    }

    public static int getScreenRotate(Context context) {
        try {
            Display display = ((DisplayManager) context.getSystemService("display")).getDisplay(0);
            Field declaredField = display.getClass().getDeclaredField("mDisplayInfo");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(display);
            Field declaredField2 = obj.getClass().getDeclaredField("rotation");
            declaredField2.setAccessible(true);
            return ((Integer) declaredField2.get(obj)).intValue();
        } catch (Exception e2) {
            SceneLog.w("get rotate error:" + e2);
            return -1;
        }
    }
}
