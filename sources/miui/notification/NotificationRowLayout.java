package miui.notification;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import java.util.HashMap;
import q.e;

/* JADX INFO: loaded from: classes.dex */
public class NotificationRowLayout extends LinearLayout implements SwipeHelper.Callback {
    private static final int APPEAR_ANIM_LEN = 250;
    private static final boolean DEBUG = false;
    private static final int DISAPPEAR_ANIM_LEN = 250;
    private static final boolean SLOW_ANIMATIONS = false;
    private static final String TAG = "NotificationRowLayout";
    boolean mAnimateBounds;
    HashMap<View, ValueAnimator> mAppearingViews;
    private Context mContext;
    HashMap<View, ValueAnimator> mDisappearingViews;
    private LayoutTransition mRealLayoutTransition;
    boolean mRemoveViews;
    private SwipeHelper mSwipeHelper;
    Rect mTmpRect;

    public NotificationRowLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    private void logLayoutTransition() {
        StringBuilder sb = new StringBuilder();
        sb.append("layout ");
        sb.append(this.mRealLayoutTransition.isChangingLayout() ? "is " : "is not ");
        sb.append("in transition and animations ");
        sb.append(this.mRealLayoutTransition.isRunning() ? "are " : "are not ");
        sb.append("running.");
        Log.v(TAG, sb.toString());
    }

    @Override // miui.notification.SwipeHelper.Callback
    public boolean canChildBeDismissed(View view) {
        View viewFindViewById = view.findViewById(e.f2246v);
        return (viewFindViewById == null || viewFindViewById.getVisibility() == 8) ? false : true;
    }

    public void dismissRowAnimated(View view) {
        dismissRowAnimated(view, 0);
    }

    @Override // miui.notification.SwipeHelper.Callback
    public View getChildAtPosition(MotionEvent motionEvent) {
        return getChildAtPosition(motionEvent.getX(), motionEvent.getY());
    }

    public View getChildAtRawPosition(float f2, float f3) {
        int[] iArr = new int[2];
        getLocationOnScreen(iArr);
        return getChildAtPosition(f2 - iArr[0], f3 - iArr[1]);
    }

    @Override // miui.notification.SwipeHelper.Callback
    public void onBeginDrag(View view) {
        requestDisallowInterceptTouchEvent(true);
    }

    @Override // miui.notification.SwipeHelper.Callback
    public void onChildDismissed(View view) {
        View viewFindViewById = view.findViewById(e.f2246v);
        if (viewFindViewById == null || viewFindViewById.getVisibility() == 8 || !this.mRemoveViews) {
            return;
        }
        viewFindViewById.performClick();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mSwipeHelper.setDensityScale(getResources().getDisplayMetrics().density);
        this.mSwipeHelper.setPagingTouchSlop(ViewConfiguration.get(this.mContext).getScaledPagingTouchSlop());
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeHelper.onInterceptTouchEvent(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeHelper.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z2) {
        super.onWindowFocusChanged(z2);
        if (z2) {
            return;
        }
        this.mSwipeHelper.removeLongPressCallback();
    }

    public void setAnimateBounds(boolean z2) {
        this.mAnimateBounds = z2;
    }

    public void setLayoutTransitionsEnabled(boolean z2) {
        if (z2) {
            setLayoutTransition(this.mRealLayoutTransition);
        } else {
            this.mRealLayoutTransition.isRunning();
            setLayoutTransition(null);
        }
    }

    public void setLongPressListener(View.OnLongClickListener onLongClickListener) {
        this.mSwipeHelper.setLongPressListener(onLongClickListener);
    }

    public void setViewRemoval(boolean z2) {
        this.mRemoveViews = z2;
    }

    public NotificationRowLayout(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.mAnimateBounds = true;
        this.mTmpRect = new Rect();
        this.mAppearingViews = new HashMap<>();
        this.mDisappearingViews = new HashMap<>();
        this.mRemoveViews = true;
        this.mContext = context;
        LayoutTransition layoutTransition = new LayoutTransition();
        this.mRealLayoutTransition = layoutTransition;
        layoutTransition.setAnimateParentHierarchy(true);
        setLayoutTransitionsEnabled(true);
        setOrientation(1);
        this.mSwipeHelper = new SwipeHelper(0, this, getResources().getDisplayMetrics().density, ViewConfiguration.get(this.mContext).getScaledPagingTouchSlop());
    }

    public void dismissRowAnimated(View view, int i2) {
        this.mSwipeHelper.dismissChild(view, i2);
    }

    public View getChildAtPosition(float f2, float f3) {
        int childCount = getChildCount();
        int measuredHeight = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() != 8) {
                measuredHeight += childAt.getMeasuredHeight();
                if (f3 > 0.0f && f3 < measuredHeight) {
                    return childAt;
                }
            }
        }
        return null;
    }

    @Override // miui.notification.SwipeHelper.Callback
    public View getChildContentView(View view) {
        return view;
    }

    @Override // miui.notification.SwipeHelper.Callback
    public void onDragCancelled(View view) {
    }
}
