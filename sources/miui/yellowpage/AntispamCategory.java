package miui.yellowpage;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class AntispamCategory {
    private String mCustomName;
    private String mIcon;
    private int mId;
    private HashMap<String, String> mNameMap;
    private String mNames;
    private int mOrder;
    private int mType;

    public AntispamCategory(int i2, String str, int i3, String str2, int i4) {
        this.mId = i2;
        this.mNames = str;
        this.mType = i3;
        if (isUserCustom()) {
            this.mCustomName = this.mNames;
        } else {
            for (String str3 : this.mNames.split(";")) {
                String[] strArrSplit = str3.split(":");
                String str4 = strArrSplit[0];
                String str5 = strArrSplit[1];
                if (this.mNameMap == null) {
                    this.mNameMap = new HashMap<>();
                }
                this.mNameMap.put(str4, str5);
            }
        }
        this.mIcon = str2;
        this.mOrder = i4;
    }

    public String getCategoryAllNames() {
        return this.mNames;
    }

    public int getCategoryId() {
        return this.mId;
    }

    public String getCategoryName() {
        if (isUserCustom()) {
            return this.mCustomName;
        }
        String str = this.mNameMap.get(Locale.getDefault().toString());
        return !TextUtils.isEmpty(str) ? str : this.mNameMap.get(Locale.US.toString());
    }

    public int getCategoryType() {
        return this.mType;
    }

    public String getIcon() {
        return this.mIcon;
    }

    public int getOrder() {
        return this.mOrder;
    }

    public boolean isUserCustom() {
        return this.mId >= 10000;
    }

    public AntispamCategory(int i2, String str, String str2, int i3) {
        this(i2, str, 0, str2, i3);
    }
}
