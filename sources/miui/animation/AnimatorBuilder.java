package miui.animation;

import android.animation.Animator;
import android.animation.TimeInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class AnimatorBuilder {
    protected Animator mAnimator;

    public AnimatorBuilder(Animator animator) {
        this.mAnimator = animator;
    }

    public static AnimatorBuilder of(Animator animator) {
        return new AnimatorBuilder(animator);
    }

    public AnimatorBuilder addListener(Animator.AnimatorListener animatorListener) {
        this.mAnimator.addListener(animatorListener);
        return this;
    }

    public Animator animator() {
        return this.mAnimator;
    }

    public AnimatorBuilder setDuration(long j2) {
        this.mAnimator.setDuration(j2);
        return this;
    }

    public AnimatorBuilder setInterpolator(TimeInterpolator timeInterpolator) {
        this.mAnimator.setInterpolator(timeInterpolator);
        return this;
    }

    public AnimatorBuilder setStartDelay(long j2) {
        this.mAnimator.setStartDelay(j2);
        return this;
    }

    public Animator start() {
        this.mAnimator.start();
        return this.mAnimator;
    }

    public static ViewPropertyAnimator.Builder of(ViewPropertyAnimator viewPropertyAnimator) {
        return new ViewPropertyAnimator.Builder(viewPropertyAnimator);
    }
}
