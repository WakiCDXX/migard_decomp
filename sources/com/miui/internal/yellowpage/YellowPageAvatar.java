package com.miui.internal.yellowpage;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import miui.yellowpage.YellowPageContract;
import miui.yellowpage.YellowPageImgLoader;

/* JADX INFO: loaded from: classes.dex */
public class YellowPageAvatar extends YellowPageImgLoader.Image {
    private static final String TAG = "YellowPageAvatar";
    private String mAvatarName;
    private Context mContext;
    private YellowPageAvatarFormat mFormat;

    public enum YellowPageAvatarFormat {
        PHOTO_NUMBER,
        THUMBNAIL_NUMBER,
        PHOTO_YID,
        THUMBNAIL_YID,
        PHOTO_NAME,
        THUMBNAIL_NAME
    }

    public YellowPageAvatar(Context context, String str, YellowPageAvatarFormat yellowPageAvatarFormat) {
        super(str);
        this.mContext = context;
        this.mFormat = yellowPageAvatarFormat;
    }

    private static String getAvatarUrlById(Context context, String str, YellowPageAvatarFormat yellowPageAvatarFormat) {
        Cursor cursorQuery = context.getContentResolver().query(YellowPageContract.PhoneLookup.CONTENT_URI, new String[]{yellowPageAvatarFormat == YellowPageAvatarFormat.PHOTO_YID ? YellowPageContract.PhoneLookup.PHOTO_URL : YellowPageContract.PhoneLookup.THUMBNAIL_URL}, "yid = " + str, null, null);
        String string = null;
        if (cursorQuery != null) {
            try {
                string = cursorQuery.moveToFirst() ? cursorQuery.getString(0) : null;
            } finally {
                cursorQuery.close();
            }
        }
        return string;
    }

    private static String getAvatarUrlByNumber(Context context, String str, YellowPageAvatarFormat yellowPageAvatarFormat) {
        Cursor cursorQuery = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.PhoneLookup.CONTENT_URI, str), new String[]{yellowPageAvatarFormat == YellowPageAvatarFormat.PHOTO_NUMBER ? YellowPageContract.PhoneLookup.PHOTO_URL : YellowPageContract.PhoneLookup.THUMBNAIL_URL}, null, null, null);
        String string = null;
        if (cursorQuery != null) {
            try {
                string = cursorQuery.moveToFirst() ? cursorQuery.getString(0) : null;
            } finally {
                cursorQuery.close();
            }
        }
        return string;
    }

    @Override // miui.yellowpage.YellowPageImgLoader.Image
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof YellowPageAvatar)) {
            return false;
        }
        YellowPageAvatar yellowPageAvatar = (YellowPageAvatar) obj;
        return this.mFormat == yellowPageAvatar.mFormat && TextUtils.equals(this.mUrl, yellowPageAvatar.mUrl);
    }

    @Override // miui.yellowpage.YellowPageImgLoader.Image
    public String getName() {
        if (TextUtils.isEmpty(this.mAvatarName)) {
            YellowPageAvatarFormat yellowPageAvatarFormat = this.mFormat;
            if (yellowPageAvatarFormat == YellowPageAvatarFormat.THUMBNAIL_NUMBER || yellowPageAvatarFormat == YellowPageAvatarFormat.PHOTO_NUMBER) {
                this.mAvatarName = getAvatarUrlByNumber(this.mContext, this.mUrl, yellowPageAvatarFormat);
            } else if (yellowPageAvatarFormat == YellowPageAvatarFormat.PHOTO_YID || yellowPageAvatarFormat == YellowPageAvatarFormat.THUMBNAIL_YID) {
                this.mAvatarName = getAvatarUrlById(this.mContext, this.mUrl, yellowPageAvatarFormat);
            } else {
                this.mAvatarName = this.mUrl;
            }
        }
        if (TextUtils.isEmpty(this.mAvatarName)) {
            return null;
        }
        return YellowPageImgLoader.getDataSha1Digest(this.mAvatarName.getBytes());
    }

    @Override // miui.yellowpage.YellowPageImgLoader.Image
    public String getUrl() {
        getName();
        return this.mAvatarName;
    }
}
