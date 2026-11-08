package miui.yellowpage;

/* JADX INFO: loaded from: classes.dex */
public class AntispamCustomCategory extends AntispamCategory {
    private boolean mIsUserCustom;
    private int mMarkedCount;
    private String mNumber;
    private int mNumberType;

    public AntispamCustomCategory(int i2, String str, int i3, String str2, int i4, String str3, int i5, boolean z2) {
        super(i2, str, i3, str2, i4);
        this.mNumber = str3;
        this.mMarkedCount = i5;
        this.mIsUserCustom = z2;
    }

    public int getMarkedCount() {
        return this.mMarkedCount;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public int getNumberType() {
        return this.mNumberType;
    }

    public boolean isNumberCategoryCustom() {
        return this.mIsUserCustom;
    }

    public void setNumberType(int i2) {
        this.mNumberType = i2;
    }
}
