package miui.yellowpage;

import android.content.Context;
import miui.util.CoderUtils;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class YellowPageImage extends YellowPageImgLoader.Image {
    private String mName;

    public YellowPageImage(Context context, String str, int i2, int i3, YellowPageImgLoader.Image.ImageFormat imageFormat) {
        super(HostManager.getImageUrl(context, str, i2, i3, imageFormat));
        this.mName = str;
    }

    @Override // miui.yellowpage.YellowPageImgLoader.Image
    public String getName() {
        return CoderUtils.encodeSHA(this.mName);
    }
}
