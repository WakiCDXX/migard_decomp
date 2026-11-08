package com.xiaomi.push.service.notification;

/* JADX INFO: loaded from: classes.dex */
public class CustomNotificationConstants {
    public static final int CUSTOM_STYLE_BUTTON_INDEX_0 = 0;
    public static final int CUSTOM_STYLE_BUTTON_INDEX_1 = 1;
    public static final int CUSTOM_STYLE_BUTTON_INDEX_2 = 2;
    public static final String EXTRA_CUSTOM_ACTIONS = "mipush.customActions";
    public static final String EXTRA_CUSTOM_CONTENT = "mipush.customContent";
    public static final String EXTRA_CUSTOM_LARGE_ICON_ID = "mipush.customLargeIconId";
    public static final String EXTRA_CUSTOM_NOTIFICATION = "mipush.customNotification";
    public static final String EXTRA_CUSTOM_TITLE = "mipush.customTitle";
    public static final String ID_LARGE_ICON = "large_icon";

    public static int getButtonPlace(int i2) {
        if (i2 == 0) {
            return 1;
        }
        if (i2 != 1) {
            return i2 != 2 ? -1 : 3;
        }
        return 2;
    }
}
