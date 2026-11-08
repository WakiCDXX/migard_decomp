package miui.yellowpage;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class HostManager {
    protected static final String BASE_URL;
    private static final String DIRECTORY_IMAGE_JPG = "/thumbnail/jpeg/w%dh%d/";
    private static final String DIRECTORY_IMAGE_PHOTO = "/thumbnail/jpeg/h%d/";
    private static final String DIRECTORY_IMAGE_PNG = "/thumbnail/png/w%d/";
    private static final String DIRECTORY_IMAGE_THUMBNAIL = "/thumbnail/jpeg/w100/";
    private static final String FORMAL_BASE_URL = "https://api.huangye.miui.com";
    private static final String GLOBAL_BASE_URL = "https://global.api.huangye.miui.com";
    protected static final String URL_DEFAULT_IMAGE_BASE = "https://file.market.xiaomi.com";
    protected static final String URL_SPBOOK_BASE;
    protected static final String URL_YELLOW_PAGE_BASE;
    private static int sDisplayHeight;
    private static String sImageDomain;

    static {
        String str = Build.IS_INTERNATIONAL_BUILD ? GLOBAL_BASE_URL : FORMAL_BASE_URL;
        BASE_URL = str;
        String str2 = str + "/spbook";
        URL_SPBOOK_BASE = str2;
        URL_YELLOW_PAGE_BASE = str2 + "/yellowpage";
    }

    private HostManager() {
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getDefaultImageBase() {
        return URL_DEFAULT_IMAGE_BASE;
    }

    private static String getImageDomain(Context context) {
        String str = sImageDomain;
        if (str != null) {
            return str;
        }
        sImageDomain = URL_DEFAULT_IMAGE_BASE;
        String string = InvocationHandler.invoke(context, "image_domain").getString("domain");
        if (!TextUtils.isEmpty(string)) {
            sImageDomain = string;
            if (!string.startsWith("https://")) {
                if (sImageDomain.startsWith("http://")) {
                    sImageDomain = sImageDomain.replaceFirst("http://", "https://");
                } else {
                    sImageDomain = "https://" + sImageDomain;
                }
            }
        }
        return sImageDomain;
    }

    public static String getImageUrl(Context context, String str, int i2, int i3, YellowPageImgLoader.Image.ImageFormat imageFormat) {
        return getImageUrl(getImageDomain(context), str, i2, i3, imageFormat);
    }

    private static int getScreenHeight(Context context) {
        if (sDisplayHeight == 0) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
            sDisplayHeight = displayMetrics.heightPixels;
        }
        return sDisplayHeight;
    }

    public static String getSpbookBaseUrl() {
        return URL_SPBOOK_BASE;
    }

    public static String getYellowPageBaseUrl() {
        return URL_YELLOW_PAGE_BASE;
    }

    public static String getYellowPagePhotoUrl(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return getImageDomain(context) + String.format(DIRECTORY_IMAGE_PHOTO, Integer.valueOf(getScreenHeight(context))) + str;
    }

    public static String getYellowPageThumbnail(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return getImageDomain(context) + DIRECTORY_IMAGE_THUMBNAIL + str;
    }

    private static String getImageUrl(String str, String str2, int i2, int i3, YellowPageImgLoader.Image.ImageFormat imageFormat) {
        if (TextUtils.isEmpty(str2) || i2 <= 0 || i3 <= 0 || TextUtils.isEmpty(str)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(String.format(imageFormat == YellowPageImgLoader.Image.ImageFormat.PNG ? DIRECTORY_IMAGE_PNG : DIRECTORY_IMAGE_JPG, Integer.valueOf(i2), Integer.valueOf(i3)));
        sb.append(str2);
        return sb.toString();
    }
}
