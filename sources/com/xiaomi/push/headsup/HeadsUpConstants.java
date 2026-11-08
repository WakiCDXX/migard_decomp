package com.xiaomi.push.headsup;

import com.xiaomi.push.service.MIPushNotificationHelper;

/* JADX INFO: loaded from: classes.dex */
public class HeadsUpConstants {
    public static final int DEFAULT_RESCHEDULE_DELAY_SECONDS = 1;
    public static final int DEFAULT_TIMEOUT = 5000;
    public static final String EXTRA_CLICK_BUTTON = "notification_click_button";
    public static final String EXTRA_HEADSUP_CANCEL_REASON = "headsup_cancel_reason";
    public static final String EXTRA_HEADSUP_CUSTOM_EXTRA = "mipush_custom_extra";
    public static final String EXTRA_HEADSUP_DESC = "headsup_desc";
    public static final String EXTRA_HEADSUP_MIN_SUPPORT_MIUI = "headsup_min_support_miui";
    public static final String EXTRA_HEADSUP_REPORT_TYPE = "headsup_report_type";
    public static final String EXTRA_HEADSUP_RESCHEDULE_DELAY_SECONDS = "headsup_rs_dsec";
    public static final String EXTRA_HEADSUP_STYLE = "headsup_style";
    public static final String EXTRA_HEADSUP_TIMEOUT = "headsup_timeout";
    public static final String EXTRA_HEADSUP_TITLE = "headsup_title";
    public static final String EXTRA_HEADSUP_UI_TYPE = "headsup_ui_type";
    public static final String EXTRA_IS_HEADSUP_MODE = "is_headsup_mode";
    public static final String EXTRA_MSG_ID = "message_id";
    public static final int FLAG_REPORT_TYPE_CANCEL = 2;
    public static final int FLAG_REPORT_TYPE_EXPOSE = 1;
    public static final String KEY_SUPPORT_STYLE = "supportStyle";
    public static final int MIN_SUPPORT_PLATFORM = 28;
    public static final int STYLE_NONE = -1;
    public static final int STYLE_SCENE_PUSH = 0;
    public static final int STYLE_VOIP_PUSH = 1;
    public static final int UI_TYPE_DEFAULT = 0;
    public static final int UI_TYPE_NONE = -1;
    public static final int UI_TYPE_VOIP = 1;

    public enum ActionAfterRemoved {
        DidRemoved,
        RecoverToStatusBar
    }

    public static String getStyleStr(int i2) {
        if (i2 != 0) {
            return i2 != 1 ? "" : MIPushNotificationHelper.MSG_BUSI_TYPE_VOIP;
        }
        return MIPushNotificationHelper.MSG_BUSI_TYPE_SCENE;
    }
}
