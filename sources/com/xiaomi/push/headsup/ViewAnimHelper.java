package com.xiaomi.push.headsup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.view.View;
import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(28)
class ViewAnimHelper {
    private static final int ANIMATION_DURATION_HEADS_UP_APPEAR = 500;
    private static final int ANIMATION_DURATION_HEADS_UP_DISAPPEAR = 150;
    private static final int ANIM_HIDE = 2;
    private static final int ANIM_NONE = 0;
    private static final int ANIM_SHOW = 1;
    private static final Interpolator INTERPOLATOR_HEADS_UP_APPEAR = new ElasticEaseOutInterpolater(2.0f, 1.4f);
    private static final Interpolator INTERPOLATOR_HEADS_UP_DISAPPEAR = new QuartEaseInInterpolater();
    private View mAnimView;
    private int mLastAnim = 0;

    public static class ElasticEaseOutInterpolater implements Interpolator {
        private float mAmplitude;
        private float mPriod;

        public ElasticEaseOutInterpolater(float f2, float f3) {
            this.mPriod = f2;
            this.mAmplitude = f3;
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float f2) {
            float fAsin;
            float f3 = this.mAmplitude;
            if (f2 == 0.0f) {
                return 0.0f;
            }
            if (f2 == 1.0f) {
                return 1.0f;
            }
            if (f3 < 1.0f) {
                fAsin = this.mPriod / 4.0f;
                f3 = 1.0f;
            } else {
                fAsin = (float) ((((double) this.mPriod) / 6.283185307179586d) * Math.asin(1.0f / f3));
            }
            return (float) ((((double) f3) * Math.pow(2.0d, (-10.0f) * f2) * Math.sin((((double) (f2 - fAsin)) * 6.283185307179586d) / ((double) this.mPriod))) + 1.0d);
        }
    }

    public static class QuartEaseInInterpolater implements Interpolator {
        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float f2) {
            return f2 * f2 * f2 * f2;
        }
    }

    public ViewAnimHelper(View view) {
        this.mAnimView = view;
    }

    public boolean isAnimDoing() {
        return this.mLastAnim != 0;
    }

    public void startAnim(final boolean z2) {
        int i2 = z2 ? 1 : 2;
        HeadsUpLog.d("lastAnim:" + this.mLastAnim + " curAnim:" + i2);
        if (this.mLastAnim != i2) {
            this.mLastAnim = i2;
            this.mAnimView.post(new Runnable() { // from class: com.xiaomi.push.headsup.ViewAnimHelper.1
                @Override // java.lang.Runnable
                public void run() {
                    float f2;
                    int height = ViewAnimHelper.this.mAnimView.getHeight();
                    float f3 = 0.0f;
                    if (z2) {
                        f3 = -height;
                        f2 = 0.0f;
                    } else {
                        f2 = -height;
                    }
                    ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(f3, f2);
                    valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaomi.push.headsup.ViewAnimHelper.1.1
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ViewAnimHelper.this.mAnimView.setTranslationY(((Float) valueAnimator.getAnimatedValue()).floatValue());
                        }
                    });
                    valueAnimatorOfFloat.setDuration(z2 ? 500L : 150L);
                    valueAnimatorOfFloat.setInterpolator(z2 ? ViewAnimHelper.INTERPOLATOR_HEADS_UP_APPEAR : ViewAnimHelper.INTERPOLATOR_HEADS_UP_DISAPPEAR);
                    valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.xiaomi.push.headsup.ViewAnimHelper.1.2
                        @Override // android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator, boolean z3) {
                            HeadsUpLog.d("animView.end:" + ViewAnimHelper.this.mAnimView.getHeight() + " isShow:" + z2);
                            ViewAnimHelper.this.mLastAnim = 0;
                            AnonymousClass1 anonymousClass1 = AnonymousClass1.this;
                            if (z2) {
                                return;
                            }
                            ViewAnimHelper.this.mAnimView.setVisibility(4);
                            ViewContainer.reportViewState(ViewAnimHelper.this.mAnimView, 0);
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animator) {
                            ViewAnimHelper.this.mAnimView.setVisibility(0);
                        }
                    });
                    valueAnimatorOfFloat.start();
                }
            });
        } else {
            HeadsUpLog.w("It's doing anim, forbidden :" + z2);
        }
    }
}
