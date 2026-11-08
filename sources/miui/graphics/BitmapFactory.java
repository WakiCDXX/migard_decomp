package miui.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import java.io.FileOutputStream;
import java.io.IOException;
import miui.io.ResettableInputStream;
import q.c;
import q.d;

/* JADX INFO: loaded from: classes.dex */
public class BitmapFactory extends android.graphics.BitmapFactory {
    public static final int BITMAP_COLOR_MODE_DARK = 0;
    public static final int BITMAP_COLOR_MODE_LIGHT = 2;
    public static final int BITMAP_COLOR_MODE_MEDIUM = 1;
    public static final int MODE_DARK = 1;
    public static final int MODE_DAYNIGHT = 2;
    public static final int MODE_LIGHT = 0;
    private static byte[] PNG_HEAD_FORMAT = {-119, 80, 78, 71, 13, 10, 26, 10};
    private static final ThreadLocal<Canvas> sCanvasCache = new ThreadLocal<>();
    private static final Paint sSrcInPaint;

    public static class CropOption {
        public int borderColor;
        public int borderWidth;
        public int rx;
        public int ry;
        public Rect srcBmpDrawingArea;

        public CropOption() {
        }

        public CropOption(int i2, int i3, int i4, int i5) {
            this.rx = i2;
            this.ry = i3;
            this.borderWidth = i4;
            this.borderColor = i5;
        }

        public CropOption(CropOption cropOption) {
            this.rx = cropOption.rx;
            this.ry = cropOption.ry;
            this.borderWidth = cropOption.borderWidth;
            this.borderColor = cropOption.borderColor;
            this.srcBmpDrawingArea = cropOption.srcBmpDrawingArea;
        }
    }

    static {
        Paint paint = new Paint(1);
        sSrcInPaint = paint;
        paint.setFilterBitmap(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    protected BitmapFactory() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    private static int between(int i2, int i3, int i4) {
        return Math.min(i3, Math.max(i2, i4));
    }

    public static Bitmap composeBitmap(Bitmap bitmap, Bitmap bitmap2, Drawable drawable, Drawable drawable2, Drawable drawable3) {
        return composeBitmap(bitmap, bitmap2, drawable, drawable2, drawable3, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), bitmap2 != null ? new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight()) : new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
    }

    private static int computeSampleSize(ResettableInputStream resettableInputStream, int i2) {
        if (i2 <= 0) {
            return 1;
        }
        android.graphics.BitmapFactory.Options bitmapSize = getBitmapSize(resettableInputStream);
        return (int) Math.sqrt((((double) bitmapSize.outWidth) * ((double) bitmapSize.outHeight)) / ((double) i2));
    }

    private static Bitmap copyToEmpty(Bitmap bitmap) {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        if (bitmap.getConfig() != null) {
            config = bitmap.getConfig();
        }
        return Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
    }

    public static Bitmap createPhoto(Context context, Bitmap bitmap) {
        return createPhoto(context, bitmap, context.getResources().getDimensionPixelSize(c.f2200e));
    }

    public static Bitmap cropBitmap(Bitmap bitmap, CropOption cropOption) {
        if (bitmap != null) {
            return cropBitmap(bitmap, copyToEmpty(bitmap), cropOption);
        }
        return null;
    }

    public static Bitmap decodeBitmap(String str, boolean z2) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(str);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, -1, z2);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static int getBitmapColorMode(Bitmap bitmap, int i2) {
        int height = bitmap.getHeight() / i2;
        int width = bitmap.getWidth() / i2;
        int i3 = (width * height) / 5;
        Bitmap bitmapScaleBitmap = scaleBitmap(bitmap, width, height);
        int i4 = 2;
        int i5 = 0;
        for (int i6 = 0; i6 < width; i6++) {
            for (int i7 = 0; i7 < height; i7++) {
                int pixel = bitmapScaleBitmap.getPixel(i6, i7);
                if (((int) ((((double) ((16711680 & pixel) >> 16)) * 0.3d) + (((double) ((65280 & pixel) >> 8)) * 0.59d) + (((double) (pixel & 255)) * 0.11d))) < 180) {
                    i5++;
                    if (i5 > i3) {
                        i4 = 1;
                    }
                    if (i5 > i3 * 2) {
                        i4 = 0;
                        break;
                    }
                }
            }
        }
        if (bitmapScaleBitmap != bitmap) {
            bitmapScaleBitmap.recycle();
        }
        return i4;
    }

    public static android.graphics.BitmapFactory.Options getBitmapSize(ResettableInputStream resettableInputStream) {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        android.graphics.BitmapFactory.decodeStream(resettableInputStream, null, options);
        return options;
    }

    private static Canvas getCachedCanvas() {
        ThreadLocal<Canvas> threadLocal = sCanvasCache;
        Canvas canvas = threadLocal.get();
        if (canvas != null) {
            return canvas;
        }
        Canvas canvas2 = new Canvas();
        threadLocal.set(canvas2);
        return canvas2;
    }

    public static boolean isPngFormat(Context context, Uri uri) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(context, uri);
            try {
                boolean zIsPngFormat = isPngFormat(resettableInputStream2);
                resettableInputStream2.close();
                return zIsPngFormat;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap maskOutBitmap(Bitmap bitmap, Drawable drawable, Bitmap bitmap2, Rect rect, Rect rect2) {
        int i2;
        if (bitmap2 == null && rect2 == null) {
            return null;
        }
        int i3 = 0;
        if (bitmap2 == null) {
            if (rect2.height() <= 0 || rect2.width() <= 0) {
                return null;
            }
            bitmap2 = Bitmap.createBitmap(rect2.width(), rect2.height(), Bitmap.Config.ARGB_8888);
        } else if (rect2 == null) {
            rect2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        }
        Canvas cachedCanvas = getCachedCanvas();
        cachedCanvas.setBitmap(bitmap2);
        cachedCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (drawable != null) {
            drawable.setBounds(rect2);
            drawable.draw(cachedCanvas);
        }
        if (rect == null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int iWidth = rect2.width();
            float fHeight = rect2.height();
            float f2 = iWidth;
            float f3 = fHeight / f2;
            float f4 = width;
            float f5 = f4 / f2;
            float f6 = height;
            float f7 = f6 / fHeight;
            if (f5 > f7) {
                int i4 = (int) (f6 / f3);
                i3 = (width - i4) / 2;
                width = i4;
                i2 = 0;
            } else if (f5 < f7) {
                int i5 = (int) (f3 * f4);
                i2 = (height - i5) / 2;
                height = i5;
            } else {
                i2 = 0;
            }
            rect = new Rect(i3, i2, width + i3, height + i2);
        }
        cachedCanvas.drawBitmap(bitmap, rect, rect2, sSrcInPaint);
        return bitmap2;
    }

    public static boolean saveToFile(Bitmap bitmap, String str) throws IOException {
        return saveToFile(bitmap, str, false);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int i2, int i3) {
        if (bitmap == null) {
            return null;
        }
        if (bitmap.getWidth() == i2 && bitmap.getHeight() == i3) {
            return bitmap;
        }
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        if (bitmap.getConfig() != null) {
            config = bitmap.getConfig();
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i2, i3, config);
        scaleBitmap(bitmap, bitmapCreateBitmap);
        return bitmapCreateBitmap;
    }

    private static Bitmap transferF16ToARGB(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width == 0 || height == 0) {
            return bitmap;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        Paint paint = new Paint();
        paint.setFlags(3);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bitmapCreateBitmap;
    }

    public static boolean saveToFile(Bitmap bitmap, String str, boolean z2) throws Throwable {
        if (bitmap == null) {
            return false;
        }
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream fileOutputStream2 = new FileOutputStream(str);
            try {
                bitmap.compress(z2 ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 100, fileOutputStream2);
                fileOutputStream2.close();
                return true;
            } catch (Throwable th) {
                th = th;
                fileOutputStream = fileOutputStream2;
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap createPhoto(Context context, Bitmap bitmap, int i2) {
        Resources resources = context.getResources();
        return composeBitmap(bitmap, null, resources.getDrawable(d.f2224c), resources.getDrawable(d.f2223b), resources.getDrawable(d.f2222a), i2);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, Bitmap bitmap2, CropOption cropOption) {
        if (bitmap == null || bitmap2 == null) {
            return null;
        }
        CropOption cropOption2 = cropOption == null ? new CropOption() : cropOption;
        Rect rect = cropOption2.srcBmpDrawingArea;
        if (rect == null) {
            rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
        int iBetween = between(0, bitmap.getWidth() - 1, rect.left);
        int iBetween2 = between(iBetween, bitmap.getWidth(), rect.right);
        int iBetween3 = between(0, bitmap.getHeight() - 1, rect.top);
        int iBetween4 = between(iBetween3, bitmap.getHeight(), rect.bottom);
        int i2 = iBetween2 - iBetween;
        int i3 = iBetween4 - iBetween3;
        int width = bitmap2.getWidth();
        int height = bitmap2.getHeight();
        cropOption2.borderWidth = between(0, Math.min(width, height) / 2, cropOption2.borderWidth);
        cropOption2.rx = between(0, width / 2, cropOption2.rx);
        cropOption2.ry = between(0, height / 2, cropOption2.ry);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        paint.setDither(true);
        Canvas canvas = new Canvas(bitmap2);
        canvas.drawARGB(0, 0, 0, 0);
        int i4 = cropOption2.rx;
        int i5 = cropOption2.borderWidth;
        if (i4 - i5 > 0 && cropOption2.ry - i5 > 0) {
            int i6 = cropOption2.borderWidth;
            RectF rectF = new RectF(i6, i6, width - i6, height - i6);
            int i7 = cropOption2.rx;
            int i8 = cropOption2.borderWidth;
            canvas.drawRoundRect(rectF, i7 - i8, cropOption2.ry - i8, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }
        int i9 = cropOption2.borderWidth;
        float f2 = i2;
        float f3 = width - (i9 * 2);
        float f4 = i3;
        float f5 = height - (i9 * 2);
        float fMin = Math.min((f2 * 1.0f) / f3, (1.0f * f4) / f5);
        int i10 = (int) ((f2 - (f3 * fMin)) / 2.0f);
        int i11 = (int) ((f4 - (f5 * fMin)) / 2.0f);
        Rect rect2 = new Rect(iBetween + i10, iBetween3 + i11, iBetween2 - i10, iBetween4 - i11);
        int i12 = cropOption2.borderWidth;
        canvas.drawBitmap(bitmap, rect2, new Rect(i12, i12, width - i12, height - i12), paint);
        if (cropOption2.borderWidth > 0) {
            int i13 = cropOption2.borderColor;
            if ((i13 >>> 24) != 0) {
                paint.setColor(i13);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
                canvas.drawRoundRect(new RectF(0.0f, 0.0f, width, height), cropOption2.rx, cropOption2.ry, paint);
            }
        }
        return bitmap2;
    }

    public static android.graphics.BitmapFactory.Options getBitmapSize(String str) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(str);
            try {
                android.graphics.BitmapFactory.Options bitmapSize = getBitmapSize(resettableInputStream2);
                resettableInputStream2.close();
                return bitmapSize;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap composeBitmap(Bitmap bitmap, Bitmap bitmap2, Drawable drawable, Drawable drawable2, Drawable drawable3, int i2) {
        return composeBitmap(bitmap, bitmap2, drawable, drawable2, drawable3, null, new Rect(0, 0, i2, i2));
    }

    public static Bitmap decodeBitmap(String str, int i2, int i3, boolean z2) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(str);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, i2, i3, z2);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static boolean isPngFormat(String str) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(str);
            try {
                boolean zIsPngFormat = isPngFormat(resettableInputStream2);
                resettableInputStream2.close();
                return zIsPngFormat;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap composeBitmap(Bitmap bitmap, Bitmap bitmap2, Drawable drawable, Drawable drawable2, Drawable drawable3, Rect rect, Rect rect2) {
        if (bitmap2 == null && rect2 == null) {
            return null;
        }
        if (bitmap2 == null) {
            if (rect2.height() <= 0 || rect2.width() <= 0) {
                return null;
            }
            bitmap2 = Bitmap.createBitmap(rect2.width(), rect2.height(), Bitmap.Config.ARGB_8888);
        } else if (rect2 == null) {
            rect2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        }
        Bitmap bitmapMaskOutBitmap = maskOutBitmap(bitmap, drawable, null, rect, rect2);
        Canvas cachedCanvas = getCachedCanvas();
        cachedCanvas.setBitmap(bitmap2);
        if (drawable3 != null) {
            drawable3.setBounds(rect2);
            drawable3.draw(cachedCanvas);
        }
        cachedCanvas.drawBitmap(bitmapMaskOutBitmap, rect2, rect2, (Paint) null);
        bitmapMaskOutBitmap.recycle();
        if (drawable2 != null) {
            drawable2.setBounds(rect2);
            drawable2.draw(cachedCanvas);
        }
        return bitmap2;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, Bitmap bitmap2) {
        if (bitmap == null || bitmap2 == null) {
            return null;
        }
        if (bitmap.getWidth() == bitmap2.getWidth() && bitmap.getHeight() == bitmap2.getHeight()) {
            return bitmap;
        }
        Canvas canvas = new Canvas(bitmap2);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight()), paint);
        return bitmap2;
    }

    public static android.graphics.BitmapFactory.Options getBitmapSize(Context context, Uri uri) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(context, uri);
            try {
                android.graphics.BitmapFactory.Options bitmapSize = getBitmapSize(resettableInputStream2);
                resettableInputStream2.close();
                return bitmapSize;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(Context context, Uri uri, boolean z2) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(context, uri);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, -1, z2);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static boolean isPngFormat(ResettableInputStream resettableInputStream) throws IOException {
        int length = PNG_HEAD_FORMAT.length;
        byte[] bArr = new byte[length];
        if (resettableInputStream.read(bArr) >= length) {
            return isPngFormat(bArr);
        }
        return false;
    }

    private static boolean isPngFormat(byte[] bArr) {
        if (bArr == null || bArr.length < PNG_HEAD_FORMAT.length) {
            return false;
        }
        int i2 = 0;
        while (true) {
            byte[] bArr2 = PNG_HEAD_FORMAT;
            if (i2 >= bArr2.length) {
                return true;
            }
            if (bArr[i2] != bArr2[i2]) {
                return false;
            }
            i2++;
        }
    }

    public static Bitmap decodeBitmap(Context context, Uri uri, int i2, int i3, boolean z2) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(context, uri);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, i2, i3, z2);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(String str, int i2, boolean z2) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(str);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, i2, z2);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(Context context, Uri uri, int i2, boolean z2) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(context, uri);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, i2, z2);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(ResettableInputStream resettableInputStream, int i2, boolean z2) throws IOException {
        Bitmap bitmapDecodeStream;
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inSampleSize = 1;
        int i3 = 0;
        options.inScaled = false;
        options.inSampleSize = computeSampleSize(resettableInputStream, i2);
        while (true) {
            int i4 = i3 + 1;
            bitmapDecodeStream = null;
            if (i3 >= 3) {
                break;
            }
            try {
                resettableInputStream.reset();
                bitmapDecodeStream = android.graphics.BitmapFactory.decodeStream(resettableInputStream, null, options);
                break;
            } catch (OutOfMemoryError e2) {
                if (z2) {
                    options.inSampleSize *= 2;
                    i3 = i4;
                } else {
                    throw e2;
                }
            }
        }
        return bitmapDecodeStream;
    }

    public static Bitmap decodeBitmap(ResettableInputStream resettableInputStream, int i2, int i3, boolean z2) throws IOException {
        int i4 = i2 * i3;
        if (i2 <= 0 || i3 <= 0) {
            i4 = -1;
        }
        Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream, i4, z2);
        if (bitmapDecodeBitmap == null) {
            return null;
        }
        if (i4 <= 0) {
            return bitmapDecodeBitmap;
        }
        Bitmap bitmapScaleBitmap = scaleBitmap(bitmapDecodeBitmap, i2, i3);
        if (bitmapDecodeBitmap != bitmapScaleBitmap) {
            bitmapDecodeBitmap.recycle();
        }
        return bitmapScaleBitmap;
    }
}
