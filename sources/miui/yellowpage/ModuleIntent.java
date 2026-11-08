package miui.yellowpage;

import android.content.Intent;
import java.io.Serializable;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ModuleIntent implements Serializable {
    private static final long serialVersionUID = 757699801853589L;
    private int mHotId;
    private int mHotShowCount;
    private Intent mIntent;
    private int mModuleId;
    private boolean mSubItemsFlag;
    private ArrayList<ModuleIntent> mSubModuleIntent;
    private String mTitle;

    public ModuleIntent(String str, Intent intent, int i2) {
        this.mTitle = str;
        this.mIntent = intent;
        this.mModuleId = i2;
    }

    public int getHotId() {
        return this.mHotId;
    }

    public int getHotShowCount() {
        return this.mHotShowCount;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public int getModuleId() {
        return this.mModuleId;
    }

    public boolean getSubItemsFlag() {
        return this.mSubItemsFlag;
    }

    public ArrayList<ModuleIntent> getSubModuleIntent() {
        return this.mSubModuleIntent;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setSubModuleIntent(ArrayList<ModuleIntent> arrayList) {
        this.mSubModuleIntent = arrayList;
    }

    public ModuleIntent(String str, Intent intent, int i2, boolean z2) {
        this.mTitle = str;
        this.mIntent = intent;
        this.mModuleId = i2;
        this.mSubItemsFlag = z2;
    }

    public ModuleIntent(String str, Intent intent, int i2, boolean z2, int i3, int i4) {
        this(str, intent, i2, z2);
        this.mHotId = i3;
        this.mHotShowCount = i4;
    }
}
