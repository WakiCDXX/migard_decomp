package miui.graphics;

import android.graphics.Bitmap;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class BitmapUtil {
    private static final int COLOR_BYTE_SIZE = 4;
    private static final String TAG = "BitmapUtil";

    public static byte[] getBuffer(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int byteCount = bitmap.getByteCount() / 4;
        byte[] bArr = null;
        try {
            int[] iArr = new int[byteCount];
            bitmap.getPixels(iArr, 0, bitmap.getRowBytes() / 4, 0, 0, width, height);
            bArr = new byte[bitmap.getByteCount()];
            for (int i2 = 0; i2 < byteCount; i2++) {
                int i3 = i2 * 4;
                int i4 = iArr[i2];
                bArr[i3 + 3] = (byte) ((i4 >> 24) & 255);
                bArr[i3] = (byte) ((i4 >> 16) & 255);
                bArr[i3 + 1] = (byte) ((i4 >> 8) & 255);
                bArr[i3 + 2] = (byte) (i4 & 255);
            }
        } catch (OutOfMemoryError e2) {
            Log.e(TAG, "failed to get buffer, baseWidth = " + width + ", baseHeight = " + height, e2);
        }
        return bArr;
    }
}
