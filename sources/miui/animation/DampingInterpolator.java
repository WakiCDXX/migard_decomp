package miui.animation;

import android.animation.TimeInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class DampingInterpolator implements TimeInterpolator {
    private final double mAtanValue;
    private final float mFactor;

    public DampingInterpolator(float f2) {
        this.mFactor = f2;
        this.mAtanValue = Math.atan(f2);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f2) {
        return (float) (Math.atan(this.mFactor * f2) / this.mAtanValue);
    }
}
