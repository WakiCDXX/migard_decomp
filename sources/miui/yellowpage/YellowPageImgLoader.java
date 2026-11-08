package miui.yellowpage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ImageView;
import com.miui.internal.yellowpage.ImageLoader;
import com.miui.internal.yellowpage.YellowPageAvatar;
import com.miui.powerkeeper.logsystem.config.LogType;
import java.security.MessageDigest;
import miui.util.HashUtils;

/* JADX INFO: loaded from: classes.dex */
public class YellowPageImgLoader {
    private static final String YELLOWPAGE_PHOTO_DOWNLOAD_WIFI_ONLY = "yellowpage_photo_download_wifi_only";

    private YellowPageImgLoader() {
    }

    public static void cancelLoading(Context context, ImageView imageView) {
        ImageLoader.getInstance(context).cancelRequest(imageView);
    }

    public static String getDataSha1Digest(byte[] bArr) {
        if (bArr != null && bArr.length != 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.SHA1);
                messageDigest.update(bArr);
                return getHexString(messageDigest.digest());
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    private static String getHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < bArr.length; i2++) {
            int i3 = (bArr[i2] & 240) >> 4;
            sb.append((char) ((i3 < 0 || i3 > 9) ? i3 + 87 : i3 + 48));
            int i4 = bArr[i2] & 15;
            sb.append((char) ((i4 < 0 || i4 > 9) ? i4 + 87 : i4 + 48));
        }
        return sb.toString();
    }

    private static String getImageUrl(Context context, String str, int i2, int i3, Image.ImageFormat imageFormat) {
        Cursor cursorQuery;
        Uri.Builder builderBuildUpon = Uri.withAppendedPath(YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_URL, Uri.encode(str)).buildUpon();
        builderBuildUpon.appendQueryParameter(Tag.TagWebService.ContentGetImage.PARAM_IMAGE_WIDTH, String.valueOf(i2));
        builderBuildUpon.appendQueryParameter("height", String.valueOf(i3));
        builderBuildUpon.appendQueryParameter("format", imageFormat == Image.ImageFormat.JPG ? "jpg" : "png");
        Uri uriBuild = builderBuildUpon.build();
        if (YellowPageUtils.isContentProviderInstalled(context, uriBuild) && (cursorQuery = context.getContentResolver().query(uriBuild, null, null, null, null)) != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    return cursorQuery.getString(0);
                }
            } finally {
                cursorQuery.close();
            }
        }
        return null;
    }

    private static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (connectivityManager.isActiveNetworkMetered() || activeNetworkInfo == null || !activeNetworkInfo.isConnected()) ? false : true;
    }

    private static boolean isYellowPagePhotoDownloadWifiOnly(Context context) {
        return Settings.System.getInt(context.getContentResolver(), YELLOWPAGE_PHOTO_DOWNLOAD_WIFI_ONLY, 1) == 1;
    }

    public static void loadImage(Context context, ImageView imageView, Image image, int i2) {
        ImageLoader.getInstance(context).loadImage(imageView, image, i2);
    }

    public static Bitmap loadPhoneDisplayAd(Context context, long j2, String str, boolean z2) {
        String string;
        Bitmap bitmap;
        String lastPathSegment;
        int i2 = z2 ? 1 : 2;
        Uri.Builder builderBuildUpon = YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_PHONE_AD.buildUpon();
        builderBuildUpon.appendQueryParameter("number", str);
        builderBuildUpon.appendQueryParameter("yid", String.valueOf(j2));
        builderBuildUpon.appendQueryParameter("callType", String.valueOf(i2));
        Uri uriBuild = builderBuildUpon.build();
        if (!YellowPageUtils.isContentProviderInstalled(context, uriBuild)) {
            return null;
        }
        Cursor cursorQuery = context.getContentResolver().query(uriBuild, null, null, null, null);
        if (cursorQuery != null) {
            try {
                string = cursorQuery.moveToFirst() ? cursorQuery.getString(0) : null;
                cursorQuery.close();
            } catch (Throwable th) {
                cursorQuery.close();
                throw th;
            }
        } else {
            string = null;
        }
        if (TextUtils.isEmpty(string)) {
            bitmap = null;
            lastPathSegment = string;
        } else {
            Bitmap bitmapLoadImageBitmap = ImageLoader.getInstance(context).loadImageBitmap(new Image(string), true);
            lastPathSegment = Uri.parse(string).getLastPathSegment();
            bitmap = bitmapLoadImageBitmap;
        }
        YellowPageStatistic.viewYellowPageInPhoneCall(context, str, i2, true, String.valueOf(j2), lastPathSegment, bitmap != null);
        return bitmap;
    }

    public static Bitmap loadPhoto(Context context, long j2, boolean z2) {
        return ImageLoader.getInstance(context).loadImageBitmap(new YellowPageAvatar(context, String.valueOf(j2), YellowPageAvatar.YellowPageAvatarFormat.PHOTO_YID), z2 && (!isYellowPagePhotoDownloadWifiOnly(context) || isWifiConnected(context)));
    }

    public static Bitmap loadPhotoByName(Context context, String str, boolean z2) {
        return ImageLoader.getInstance(context).loadImageBitmap(new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.PHOTO_NAME), z2 && (!isYellowPagePhotoDownloadWifiOnly(context) || isWifiConnected(context)));
    }

    public static void loadThumbnail(Context context, ImageView imageView, Image.ImageProcessor imageProcessor, long j2, int i2) {
        YellowPageAvatar yellowPageAvatar = new YellowPageAvatar(context, String.valueOf(j2), YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_YID);
        yellowPageAvatar.setImageProcessor(imageProcessor);
        ImageLoader.getInstance(context).loadImage(imageView, yellowPageAvatar, i2);
    }

    public static byte[] loadThumbnailByName(Context context, String str, boolean z2, int i2, int i3) {
        return ImageLoader.getInstance(context).loadImageBytes(new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NAME), z2, i2, i3);
    }

    public static void pauseLoading(Context context) {
        ImageLoader.getInstance(context).pauseLoading();
    }

    public static void resumeLoading(Context context) {
        ImageLoader.getInstance(context).resumeLoading();
    }

    public static void loadImage(Context context, ImageView imageView, Image.ImageProcessor imageProcessor, Image.ImageFormat imageFormat, String str, int i2, int i3, int i4) {
        Image image = new Image(HostManager.getImageUrl(context, str, i2, i3, imageFormat), imageFormat);
        image.setImageProcessor(imageProcessor);
        ImageLoader.getInstance(context).loadImage(imageView, image, i4);
    }

    public static class Image {
        private ImageFormat mFormat;
        private ImageProcessor mImageProcesser;
        protected String mUrl;

        public enum ImageFormat {
            JPG,
            PNG
        }

        public interface ImageProcessor {
            Bitmap processImage(Bitmap bitmap);
        }

        public Image(String str) {
            this.mUrl = str;
            this.mFormat = ImageFormat.JPG;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Image) {
                return TextUtils.equals(((Image) obj).mUrl, this.mUrl);
            }
            return false;
        }

        public ImageFormat getFormat() {
            return this.mFormat;
        }

        public String getName() {
            return HashUtils.getSHA1(this.mUrl);
        }

        public String getUrl() {
            return this.mUrl;
        }

        public int hashCode() {
            String str = this.mUrl;
            if (str == null) {
                return 0;
            }
            return str.hashCode();
        }

        public boolean isValid() {
            return !TextUtils.isEmpty(this.mUrl);
        }

        public Bitmap proccessImage(Bitmap bitmap) {
            ImageProcessor imageProcessor = this.mImageProcesser;
            return imageProcessor != null ? imageProcessor.processImage(bitmap) : bitmap;
        }

        public void setImageProcessor(ImageProcessor imageProcessor) {
            this.mImageProcesser = imageProcessor;
        }

        public Image(String str, ImageFormat imageFormat) {
            this.mUrl = str;
            this.mFormat = imageFormat;
        }
    }

    public static byte[] loadThumbnailByName(Context context, String str, boolean z2) {
        return ImageLoader.getInstance(context).loadImageBytes(new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NAME), z2, 0, 0);
    }

    public static void loadThumbnail(Context context, ImageView imageView, Image.ImageProcessor imageProcessor, String str, int i2) {
        YellowPageAvatar yellowPageAvatar = new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NUMBER);
        yellowPageAvatar.setImageProcessor(imageProcessor);
        ImageLoader.getInstance(context).loadImage(imageView, yellowPageAvatar, i2);
    }

    public static void loadThumbnailByName(Context context, ImageView imageView, Image.ImageProcessor imageProcessor, String str, int i2) {
        YellowPageAvatar yellowPageAvatar = new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NAME);
        yellowPageAvatar.setImageProcessor(imageProcessor);
        ImageLoader.getInstance(context).loadImage(imageView, yellowPageAvatar, i2);
    }

    public static Bitmap loadThumbnail(Context context, String str, boolean z2) {
        return ImageLoader.getInstance(context).loadImageBitmap(new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NUMBER), z2);
    }
}
