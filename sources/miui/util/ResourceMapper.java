package miui.util;

import android.content.res.Resources;
import android.util.TypedValue;

/* JADX INFO: loaded from: classes.dex */
public class ResourceMapper {
    public static int resolveReference(Resources resources, int i2) {
        TypedValue typedValue = new TypedValue();
        resources.getValue(i2, typedValue, true);
        int i3 = typedValue.resourceId;
        return i3 == 0 ? i2 : i3;
    }
}
