package miui.yellowpage;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class YellowPageMipub {
    private String mMipubId;
    private String mThumbnailName;
    private long mYpId;
    private String mYpName;

    public YellowPageMipub(String str, String str2, long j2, String str3) {
        this.mMipubId = str;
        this.mYpName = str2;
        this.mYpId = j2;
        this.mThumbnailName = str3;
    }

    public String getMipubId() {
        return this.mMipubId;
    }

    public String getThumbnailName() {
        return this.mThumbnailName;
    }

    public long getYpId() {
        return this.mYpId;
    }

    public String getYpName() {
        return this.mYpName;
    }
}
