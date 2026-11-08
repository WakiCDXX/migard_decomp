package com.xiaomi.push.headsup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.xiaomi.mipush.sdk.Constants;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(28)
class ViewTouchHelper {
    private static final int DEFAULT_ESCAPE_DURATION_X = 200;
    private static final int DEFAULT_ESCAPE_DURATION_Y = 100;
    private static final int DIRECTION_X = 0;
    private static final int DIRECTION_Y = 1;
    private static final Interpolator INTERPOLATOR_FAST_OUT_LINEAR_IN = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    private static final int INVALID_VALUE = -1;
    private static final int MAX_DISMISS_VELOCITY = 4000;
    private static final int SWIPE_ESCAPE_VELOCITY = 500;
    private int mDirection = -1;
    private int mDownPosX;
    private int mDownPosY;
    private boolean mIsDoingFlingAnim;
    private float mMaxSpeed;
    private float mMinSpeed;
    private int mStartPos;
    private int mTouchSlop;
    private View mTouchView;
    private float mTranslation;
    private VelocityTracker mVelocityTracker;

    public ViewTouchHelper(View view) {
        this.mTouchView = view;
        float f2 = view.getContext().getResources().getDisplayMetrics().density;
        this.mMaxSpeed = 4000.0f * f2;
        this.mMinSpeed = f2 * 500.0f;
        this.mTouchSlop = ViewConfiguration.get(view.getContext()).getScaledPagingTouchSlop();
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    private void animateTouchScale(float f2) {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.mTouchView.getScaleX(), f2);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaomi.push.headsup.ViewTouchHelper.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                ViewTouchHelper.this.mTouchView.setScaleX(fFloatValue);
                ViewTouchHelper.this.mTouchView.setScaleY(fFloatValue);
            }
        });
        valueAnimatorOfFloat.setDuration(150L);
        valueAnimatorOfFloat.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doTranslation(float f2) {
        if (this.mDirection == 0) {
            this.mTouchView.setTranslationX(f2);
        } else {
            if (f2 > 0.0f) {
                f2 = 0.0f;
            }
            this.mTouchView.setTranslationY(f2);
        }
        setAlpha(f2);
    }

    private void forceResetView() {
        setAlpha(0.0f);
        this.mTouchView.setTranslationX(0.0f);
        this.mTouchView.setTranslationY(0.0f);
        this.mTouchView.setScaleX(1.0f);
        this.mTouchView.setScaleY(1.0f);
    }

    private int getMaxDuration() {
        return this.mDirection == 0 ? 200 : 100;
    }

    private int getPos(MotionEvent motionEvent) {
        return (int) (this.mDirection == 0 ? motionEvent.getX() : motionEvent.getY());
    }

    private int getSize() {
        View view = this.mTouchView;
        if (view.getParent() instanceof ViewGroup) {
            view = (ViewGroup) view.getParent();
        }
        return this.mDirection == 0 ? view.getWidth() : view.getHeight();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getTranslation() {
        int i2 = this.mDirection;
        View view = this.mTouchView;
        return i2 == 0 ? view.getTranslationX() : view.getTranslationY();
    }

    private float getVelocity(VelocityTracker velocityTracker) {
        return this.mDirection == 0 ? velocityTracker.getXVelocity() : velocityTracker.getYVelocity();
    }

    private void handleUpEvent(float f2) {
        float f3;
        float f4;
        float translation = getTranslation();
        float fSignum = Math.signum(translation);
        float fAbs = Math.abs(translation);
        int size = getSize();
        if (f2 > this.mMinSpeed || Math.abs(fAbs) > size * 0.6f) {
            float f5 = fSignum * fAbs;
            float f6 = fSignum * size;
            f3 = f5;
            f4 = f6;
        } else {
            f3 = fSignum * fAbs;
            f4 = 0.0f;
        }
        this.mIsDoingFlingAnim = true;
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(f3, f4);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaomi.push.headsup.ViewTouchHelper.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewTouchHelper.this.doTranslation(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.xiaomi.push.headsup.ViewTouchHelper.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator, boolean z2) {
                if (ViewTouchHelper.this.getTranslation() != 0.0f) {
                    ViewTouchHelper.this.mTouchView.setVisibility(4);
                    ViewContainer.reportViewState(ViewTouchHelper.this.mTouchView, 1);
                }
                ViewTouchHelper.this.resetState();
            }
        });
        valueAnimatorOfFloat.setInterpolator(INTERPOLATOR_FAST_OUT_LINEAR_IN);
        float f7 = size;
        int maxDuration = (int) (((f7 - fAbs) / f7) * getMaxDuration());
        HeadsUpLog.d("duration:" + maxDuration);
        valueAnimatorOfFloat.setDuration((long) maxDuration);
        valueAnimatorOfFloat.start();
    }

    private boolean hasDown() {
        return (this.mDownPosX == -1 || this.mDownPosY == -1) ? false : true;
    }

    private boolean isPointInView(MotionEvent motionEvent) {
        float x2 = motionEvent.getX();
        float y2 = motionEvent.getY();
        float x3 = this.mTouchView.getX();
        float y3 = this.mTouchView.getY();
        return x2 >= x3 && x2 <= x3 + ((float) this.mTouchView.getWidth()) && y2 >= y3 && y2 <= y3 + ((float) this.mTouchView.getHeight());
    }

    private void setAlpha(float f2) {
        int size = getSize();
        this.mTouchView.setAlpha(1.0f - (size > 0 ? Math.abs(f2 * 0.6666667f) / size : 0.0f));
    }

    /* JADX WARN: Code duplicated, block: B:32:0x0089  */
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (this.mIsDoingFlingAnim) {
            HeadsUpLog.d("is doing fling anim, return:" + MotionEvent.actionToString(action));
            return false;
        }
        int i2 = action & 255;
        if (i2 == 0) {
            this.mVelocityTracker.clear();
            if (isPointInView(motionEvent)) {
                this.mDownPosX = (int) motionEvent.getX();
                this.mDownPosY = (int) motionEvent.getY();
                this.mVelocityTracker.addMovement(motionEvent);
            }
        } else if (i2 == 1) {
            if (this.mDirection != -1 && hasDown()) {
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxSpeed);
                float velocity = getVelocity(this.mVelocityTracker);
                this.mVelocityTracker.clear();
                HeadsUpLog.d("speed:" + velocity + " " + this.mMinSpeed + Constants.ACCEPT_TIME_SEPARATOR_SERVER + this.mMaxSpeed);
                handleUpEvent(Math.abs(velocity));
            }
        } else if (i2 != 2) {
            if (i2 == 3 || i2 == 5 || i2 == 6) {
                if (this.mDirection != -1) {
                    this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxSpeed);
                    float velocity2 = getVelocity(this.mVelocityTracker);
                    this.mVelocityTracker.clear();
                    HeadsUpLog.d("speed:" + velocity2 + " " + this.mMinSpeed + Constants.ACCEPT_TIME_SEPARATOR_SERVER + this.mMaxSpeed);
                    handleUpEvent(Math.abs(velocity2));
                }
            }
        } else if (hasDown()) {
            if (this.mDirection == -1) {
                int x2 = (int) motionEvent.getX();
                int y2 = (int) motionEvent.getY();
                int iAbs = Math.abs(x2 - this.mDownPosX);
                int iAbs2 = Math.abs(y2 - this.mDownPosY);
                int i3 = this.mTouchSlop;
                if (iAbs > i3 || iAbs2 > i3) {
                    if (iAbs > iAbs2) {
                        this.mStartPos = x2;
                        this.mDirection = 0;
                    } else {
                        this.mStartPos = y2;
                        this.mDirection = 1;
                    }
                    this.mTranslation = getTranslation();
                    this.mVelocityTracker.addMovement(motionEvent);
                    doTranslation(this.mTranslation + (getPos(motionEvent) - this.mStartPos));
                }
            } else {
                this.mVelocityTracker.addMovement(motionEvent);
                doTranslation(this.mTranslation + (getPos(motionEvent) - this.mStartPos));
            }
        }
        return true;
    }

    public boolean hasMoved() {
        return getTranslation() != 0.0f;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            animateTouchScale(0.95f);
        } else if (action == 1 || action == 3 || action == 5 || action == 6) {
            animateTouchScale(1.0f);
        }
        return true;
    }

    public void resetState() {
        forceResetView();
        this.mStartPos = -1;
        this.mDownPosX = -1;
        this.mDownPosY = -1;
        this.mDirection = -1;
        this.mTranslation = -1.0f;
        this.mIsDoingFlingAnim = false;
    }
}
