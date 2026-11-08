package miui.yellowpage;

import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
public class YellowPageProvider {
    public static final YellowPageProvider DEFAULT_PROVIDER = new YellowPageProvider(0, YellowPageContract.Provider.PNAME_DEFAULT, null, null);
    private Bitmap mIcon;
    private Bitmap mIconBig;
    private int mId;
    private String mName;

    public YellowPageProvider(int i2, String str, Bitmap bitmap, Bitmap bitmap2) {
        this.mId = i2;
        this.mName = str;
        this.mIcon = bitmap;
        this.mIconBig = bitmap2;
    }

    public Bitmap getBigIcon() {
        return this.mIconBig;
    }

    public Bitmap getIcon() {
        return this.mIcon;
    }

    public int getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public boolean isMiui() {
        return this.mId == 0;
    }
}
