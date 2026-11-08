package miui.yellowpage;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import miui.graphics.BitmapFactory;

/* JADX INFO: loaded from: classes.dex */
public class ContactThumbnailProcessor implements YellowPageImgLoader.Image.ImageProcessor {
    private static final int sPhotoSize = 134;
    private int mBackgroundRes;
    private Context mContext;
    private boolean mDefaultPhoto = true;
    private int mForegroundRes;
    private int mMaskRes;

    public ContactThumbnailProcessor(Context context, int i2, int i3, int i4) {
        this.mContext = context;
        this.mForegroundRes = i2;
        this.mBackgroundRes = i3;
        this.mMaskRes = i4;
    }

    @Override // miui.yellowpage.YellowPageImgLoader.Image.ImageProcessor
    public Bitmap processImage(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        if (this.mDefaultPhoto) {
            return BitmapFactory.createPhoto(this.mContext, bitmap, sPhotoSize);
        }
        Resources resources = this.mContext.getResources();
        return BitmapFactory.composeBitmap(bitmap, null, resources.getDrawable(this.mMaskRes), resources.getDrawable(this.mForegroundRes), resources.getDrawable(this.mBackgroundRes), sPhotoSize);
    }

    public ContactThumbnailProcessor(Context context) {
        this.mContext = context;
    }
}
