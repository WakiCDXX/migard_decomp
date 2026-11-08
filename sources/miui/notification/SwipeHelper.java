package miui.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class SwipeHelper implements Gefingerpoken {
    static final float ALPHA_FADE_END = 0.5f;
    private static final boolean CONSTRAIN_SWIPE = true;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_INVALIDATE = false;
    private static final boolean DISMISS_IF_SWIPED_FAR_ENOUGH = true;
    private static final boolean FADE_OUT_DURING_SWIPE = true;
    private static final boolean SLOW_ANIMATIONS = false;
    private static final int SNAP_ANIM_LEN = 150;
    static final String TAG = "com.android.systemui.SwipeHelper";
    public static final int X = 0;
    public static final int Y = 1;
    private Callback mCallback;
    private boolean mCanCurrViewBeDimissed;
    private View mCurrAnimView;
    private View mCurrView;
    private float mDensityScale;
    private boolean mDragging;
    private float mInitialTouchPos;
    private View.OnLongClickListener mLongPressListener;
    private boolean mLongPressSent;
    private float mPagingTouchSlop;
    private int mSwipeDirection;
    private Runnable mWatchLongPress;
    private static LinearInterpolator sLinearInterpolator = new LinearInterpolator();
    public static float ALPHA_FADE_START = 0.0f;
    private float SWIPE_ESCAPE_VELOCITY = 100.0f;
    private int DEFAULT_ESCAPE_ANIMATION_DURATION = 200;
    private int MAX_ESCAPE_ANIMATION_DURATION = 400;
    private int MAX_DISMISS_VELOCITY = 2000;
    private float mMinAlpha = 0.0f;
    private Handler mHandler = new Handler();
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private long mLongPressTimeout = (long) (ViewConfiguration.getLongPressTimeout() * 1.5f);

    public interface Callback {
        boolean canChildBeDismissed(View view);

        View getChildAtPosition(MotionEvent motionEvent);

        View getChildContentView(View view);

        void onBeginDrag(View view);

        void onChildDismissed(View view);

        void onDragCancelled(View view);
    }

    public SwipeHelper(int i2, Callback callback, float f2, float f3) {
        this.mCallback = callback;
        this.mSwipeDirection = i2;
        this.mDensityScale = f2;
        this.mPagingTouchSlop = f3;
    }

    private ObjectAnimator createTranslationAnimation(View view, float f2) {
        return ObjectAnimator.ofFloat(view, this.mSwipeDirection == 0 ? "translationX" : "translationY", f2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getAlphaForOffset(View view) {
        float size = getSize(view);
        float f2 = ALPHA_FADE_END * size;
        float translation = getTranslation(view);
        float f3 = ALPHA_FADE_START;
        float f4 = 1.0f;
        if (translation >= size * f3) {
            f4 = 1.0f - ((translation - (size * f3)) / f2);
        } else if (translation < (1.0f - f3) * size) {
            f4 = 1.0f + (((size * f3) + translation) / f2);
        }
        return Math.max(this.mMinAlpha, f4);
    }

    private float getPerpendicularVelocity(VelocityTracker velocityTracker) {
        return this.mSwipeDirection == 0 ? velocityTracker.getYVelocity() : velocityTracker.getXVelocity();
    }

    private float getPos(MotionEvent motionEvent) {
        return this.mSwipeDirection == 0 ? motionEvent.getX() : motionEvent.getY();
    }

    private float getSize(View view) {
        return this.mSwipeDirection == 0 ? view.getMeasuredWidth() : view.getMeasuredHeight();
    }

    private float getTranslation(View view) {
        return this.mSwipeDirection == 0 ? view.getTranslationX() : view.getTranslationY();
    }

    private float getVelocity(VelocityTracker velocityTracker) {
        return this.mSwipeDirection == 0 ? velocityTracker.getXVelocity() : velocityTracker.getYVelocity();
    }

    public static void invalidateGlobalRegion(View view) {
        invalidateGlobalRegion(view, new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
    }

    private void setTranslation(View view, float f2) {
        if (this.mSwipeDirection == 0) {
            view.setTranslationX(f2);
        } else {
            view.setTranslationY(f2);
        }
    }

    public void dismissChild(final View view, float f2) {
        final View childContentView = this.mCallback.getChildContentView(view);
        final boolean zCanChildBeDismissed = this.mCallback.canChildBeDismissed(view);
        float size = (f2 < 0.0f || (f2 == 0.0f && getTranslation(childContentView) < 0.0f) || (f2 == 0.0f && getTranslation(childContentView) == 0.0f && this.mSwipeDirection == 1)) ? -getSize(childContentView) : getSize(childContentView);
        int iMin = f2 != 0.0f ? Math.min(this.MAX_ESCAPE_ANIMATION_DURATION, (int) ((Math.abs(size - getTranslation(childContentView)) * 1000.0f) / Math.abs(f2))) : this.DEFAULT_ESCAPE_ANIMATION_DURATION;
        childContentView.setLayerType(2, null);
        ObjectAnimator objectAnimatorCreateTranslationAnimation = createTranslationAnimation(childContentView, size);
        objectAnimatorCreateTranslationAnimation.setInterpolator(sLinearInterpolator);
        objectAnimatorCreateTranslationAnimation.setDuration(iMin);
        objectAnimatorCreateTranslationAnimation.addListener(new AnimatorListenerAdapter() { // from class: miui.notification.SwipeHelper.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SwipeHelper.this.mCallback.onChildDismissed(view);
                childContentView.setLayerType(0, null);
            }
        });
        objectAnimatorCreateTranslationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miui.notification.SwipeHelper.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (zCanChildBeDismissed) {
                    View view2 = childContentView;
                    view2.setAlpha(SwipeHelper.this.getAlphaForOffset(view2));
                }
                SwipeHelper.invalidateGlobalRegion(childContentView);
            }
        });
        objectAnimatorCreateTranslationAnimation.start();
    }

    /* JADX WARN: Code duplicated, block: B:18:0x004a  */
    @Override // miui.notification.Gefingerpoken
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mDragging = false;
            this.mLongPressSent = false;
            this.mCurrView = this.mCallback.getChildAtPosition(motionEvent);
            this.mVelocityTracker.clear();
            View view = this.mCurrView;
            if (view != null) {
                this.mCurrAnimView = this.mCallback.getChildContentView(view);
                this.mCanCurrViewBeDimissed = this.mCallback.canChildBeDismissed(this.mCurrView);
                this.mVelocityTracker.addMovement(motionEvent);
                this.mInitialTouchPos = getPos(motionEvent);
                if (this.mLongPressListener != null) {
                    if (this.mWatchLongPress == null) {
                        this.mWatchLongPress = new Runnable() { // from class: miui.notification.SwipeHelper.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (SwipeHelper.this.mCurrView == null || SwipeHelper.this.mLongPressSent) {
                                    return;
                                }
                                SwipeHelper.this.mLongPressSent = true;
                                SwipeHelper.this.mCurrView.sendAccessibilityEvent(2);
                                SwipeHelper.this.mLongPressListener.onLongClick(SwipeHelper.this.mCurrView);
                            }
                        };
                    }
                    this.mHandler.postDelayed(this.mWatchLongPress, this.mLongPressTimeout);
                }
            }
        } else if (action == 1) {
            this.mDragging = false;
            this.mCurrView = null;
            this.mCurrAnimView = null;
            this.mLongPressSent = false;
            removeLongPressCallback();
        } else if (action != 2) {
            if (action == 3) {
                this.mDragging = false;
                this.mCurrView = null;
                this.mCurrAnimView = null;
                this.mLongPressSent = false;
                removeLongPressCallback();
            }
        } else if (this.mCurrView != null && !this.mLongPressSent) {
            this.mVelocityTracker.addMovement(motionEvent);
            if (Math.abs(getPos(motionEvent) - this.mInitialTouchPos) > this.mPagingTouchSlop) {
                this.mCallback.onBeginDrag(this.mCurrView);
                this.mDragging = true;
                this.mInitialTouchPos = getPos(motionEvent) - getTranslation(this.mCurrAnimView);
                removeLongPressCallback();
            }
        }
        return this.mDragging;
    }

    /* JADX WARN: Code duplicated, block: B:18:0x0026  */
    /* JADX WARN: Code duplicated, block: B:20:0x002a  */
    /* JADX WARN: Code duplicated, block: B:22:0x003b  */
    /* JADX WARN: Code duplicated, block: B:24:0x004d  */
    /* JADX WARN: Code duplicated, block: B:26:0x0051  */
    /* JADX WARN: Code duplicated, block: B:27:0x0053  */
    /* JADX WARN: Code duplicated, block: B:28:0x0055  */
    /* JADX WARN: Code duplicated, block: B:31:0x006c  */
    /* JADX WARN: Code duplicated, block: B:33:0x007c  */
    /* JADX WARN: Code duplicated, block: B:35:0x0080  */
    /* JADX WARN: Code duplicated, block: B:37:0x00ba  */
    /* JADX WARN: Code duplicated, block: B:38:0x00bc  */
    /* JADX WARN: Code duplicated, block: B:45:0x00d5  */
    /* JADX WARN: Code duplicated, block: B:46:0x00d7  */
    /* JADX WARN: Code duplicated, block: B:49:0x00e2  */
    /* JADX WARN: Code duplicated, block: B:50:0x00e4  */
    /* JADX WARN: Code duplicated, block: B:52:0x00e7  */
    /* JADX WARN: Code duplicated, block: B:61:0x00ff  */
    @Override // miui.notification.Gefingerpoken
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float f2;
        float velocity;
        boolean z2;
        boolean z3;
        boolean z4;
        float pos;
        float size;
        float f3;
        if (this.mLongPressSent) {
            return true;
        }
        boolean z5 = false;
        if (!this.mDragging) {
            removeLongPressCallback();
            return false;
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int action = motionEvent.getAction();
        if (action == 1) {
            if (this.mCurrView != null) {
                this.mVelocityTracker.computeCurrentVelocity(1000, this.MAX_DISMISS_VELOCITY * this.mDensityScale);
                f2 = this.SWIPE_ESCAPE_VELOCITY * this.mDensityScale;
                velocity = getVelocity(this.mVelocityTracker);
                float perpendicularVelocity = getPerpendicularVelocity(this.mVelocityTracker);
                if (Math.abs(getTranslation(this.mCurrAnimView)) > ((double) getSize(this.mCurrAnimView)) * 0.4d) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (Math.abs(velocity) > f2 && Math.abs(velocity) > Math.abs(perpendicularVelocity)) {
                    if (velocity > 0.0f) {
                        z3 = true;
                    } else {
                        z3 = false;
                    }
                    if (getTranslation(this.mCurrAnimView) > 0.0f) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    if (z3 == z4) {
                        z5 = true;
                    }
                }
                if (this.mCallback.canChildBeDismissed(this.mCurrView) || !(z5 || z2)) {
                    this.mCallback.onDragCancelled(this.mCurrView);
                    snapChild(this.mCurrView, velocity);
                } else {
                    dismissChild(this.mCurrView, z5 ? velocity : 0.0f);
                }
            }
        } else if (action == 2) {
            if (this.mCurrView != null) {
                pos = getPos(motionEvent) - this.mInitialTouchPos;
                if (!this.mCallback.canChildBeDismissed(this.mCurrView)) {
                    size = getSize(this.mCurrAnimView);
                    f3 = 0.15f * size;
                    if (Math.abs(pos) >= size) {
                        pos = ((float) Math.sin(((double) (pos / size)) * 1.5707963267948966d)) * f3;
                    } else if (pos > 0.0f) {
                        pos = f3;
                    } else {
                        pos = -f3;
                    }
                }
                setTranslation(this.mCurrAnimView, pos);
                if (this.mCanCurrViewBeDimissed) {
                    View view = this.mCurrAnimView;
                    view.setAlpha(getAlphaForOffset(view));
                }
                invalidateGlobalRegion(this.mCurrView);
            }
        } else if (action != 3) {
            if (action == 4) {
                if (this.mCurrView != null) {
                    pos = getPos(motionEvent) - this.mInitialTouchPos;
                    if (!this.mCallback.canChildBeDismissed(this.mCurrView)) {
                        size = getSize(this.mCurrAnimView);
                        f3 = 0.15f * size;
                        if (Math.abs(pos) >= size) {
                            pos = ((float) Math.sin(((double) (pos / size)) * 1.5707963267948966d)) * f3;
                        } else if (pos > 0.0f) {
                            pos = f3;
                        } else {
                            pos = -f3;
                        }
                    }
                    setTranslation(this.mCurrAnimView, pos);
                    if (this.mCanCurrViewBeDimissed) {
                        View view2 = this.mCurrAnimView;
                        view2.setAlpha(getAlphaForOffset(view2));
                    }
                    invalidateGlobalRegion(this.mCurrView);
                }
            }
        } else if (this.mCurrView != null) {
            this.mVelocityTracker.computeCurrentVelocity(1000, this.MAX_DISMISS_VELOCITY * this.mDensityScale);
            f2 = this.SWIPE_ESCAPE_VELOCITY * this.mDensityScale;
            velocity = getVelocity(this.mVelocityTracker);
            float perpendicularVelocity2 = getPerpendicularVelocity(this.mVelocityTracker);
            if (Math.abs(getTranslation(this.mCurrAnimView)) > ((double) getSize(this.mCurrAnimView)) * 0.4d) {
                z2 = true;
            } else {
                z2 = false;
            }
            if (Math.abs(velocity) > f2) {
                if (velocity > 0.0f) {
                    z3 = true;
                } else {
                    z3 = false;
                }
                if (getTranslation(this.mCurrAnimView) > 0.0f) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                if (z3 == z4) {
                    z5 = true;
                }
            }
            if (this.mCallback.canChildBeDismissed(this.mCurrView)) {
                this.mCallback.onDragCancelled(this.mCurrView);
                snapChild(this.mCurrView, velocity);
            } else {
                this.mCallback.onDragCancelled(this.mCurrView);
                snapChild(this.mCurrView, velocity);
            }
        }
        return true;
    }

    public void removeLongPressCallback() {
        Runnable runnable = this.mWatchLongPress;
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
            this.mWatchLongPress = null;
        }
    }

    public void setDensityScale(float f2) {
        this.mDensityScale = f2;
    }

    public void setLongPressListener(View.OnLongClickListener onLongClickListener) {
        this.mLongPressListener = onLongClickListener;
    }

    public void setMinAlpha(float f2) {
        this.mMinAlpha = f2;
    }

    public void setPagingTouchSlop(float f2) {
        this.mPagingTouchSlop = f2;
    }

    public void snapChild(View view, float f2) {
        final View childContentView = this.mCallback.getChildContentView(view);
        final boolean zCanChildBeDismissed = this.mCallback.canChildBeDismissed(childContentView);
        ObjectAnimator objectAnimatorCreateTranslationAnimation = createTranslationAnimation(childContentView, 0.0f);
        objectAnimatorCreateTranslationAnimation.setDuration(SNAP_ANIM_LEN);
        objectAnimatorCreateTranslationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miui.notification.SwipeHelper.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (zCanChildBeDismissed) {
                    View view2 = childContentView;
                    view2.setAlpha(SwipeHelper.this.getAlphaForOffset(view2));
                }
                SwipeHelper.invalidateGlobalRegion(childContentView);
            }
        });
        objectAnimatorCreateTranslationAnimation.start();
    }

    public static void invalidateGlobalRegion(View view, RectF rectF) {
        while (view.getParent() != null && (view.getParent() instanceof View)) {
            view = (View) view.getParent();
            view.getMatrix().mapRect(rectF);
            view.invalidate((int) Math.floor(rectF.left), (int) Math.floor(rectF.top), (int) Math.ceil(rectF.right), (int) Math.ceil(rectF.bottom));
        }
    }
}
