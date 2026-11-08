package com.xiaomi.push.headsup;

import android.content.Context;
import android.graphics.Outline;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.push.service.NotificationUtils;

/* JADX INFO: loaded from: classes.dex */
class ViewContainer extends FrameLayout {
    private ViewAnimHelper mAnimHelper;
    private ContentView mContentView;
    private OnStateListener mStateListener;
    private ViewTouchHelper mTouchHelper;

    private class ContentView extends FrameLayout {
        public ContentView(Context context) {
            super(context);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            ViewContainer.this.mTouchHelper.onTouchEvent(motionEvent);
            return super.onTouchEvent(motionEvent);
        }

        public void setState(int i2) {
            if (ViewContainer.this.mStateListener != null) {
                ViewContainer.this.mStateListener.onStateChanged(i2);
            }
        }
    }

    public interface OnStateListener {
        public static final int STATE_ANIM_DISMISS_END = 0;
        public static final int STATE_CARD_CLICK_END = 2;
        public static final int STATE_TOUCH_SWIPE_END = 1;

        void onStateChanged(int i2);
    }

    public ViewContainer(Context context) {
        super(context);
        this.mContentView = generateAndAttachContentView();
        this.mAnimHelper = new ViewAnimHelper(this.mContentView);
        this.mTouchHelper = new ViewTouchHelper(this.mContentView);
        final int iDp2px = ToolUtils.dp2px(getContext(), 16);
        this.mContentView.setClipToOutline(true);
        this.mContentView.setOutlineProvider(new ViewOutlineProvider() { // from class: com.xiaomi.push.headsup.ViewContainer.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), iDp2px);
            }
        });
        this.mContentView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaomi.push.headsup.ViewContainer.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewContainer.this.mTouchHelper.hasMoved()) {
                    HeadsUpLog.w("click card hasMoved, forbidden click");
                    return;
                }
                HeadsUpLog.w("click card do..." + ViewContainer.this.mContentView.getChildCount());
                if (ViewContainer.this.getActiveCard() != null) {
                    ViewContainer.this.getActiveCard().clickCard();
                }
            }
        });
    }

    private ContentView generateAndAttachContentView() {
        int iDp2px = ToolUtils.dp2px(getContext(), 12);
        int iDp2px2 = ToolUtils.dp2px(getContext(), 8);
        int iDp2px3 = ToolUtils.dp2px(getContext(), 18);
        ContentView contentView = new ContentView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
        layoutParams.leftMargin = iDp2px;
        layoutParams.rightMargin = iDp2px;
        layoutParams.topMargin = iDp2px2;
        layoutParams.bottomMargin = iDp2px3;
        layoutParams.gravity = 1;
        contentView.setLayoutParams(layoutParams);
        contentView.setElevation(iDp2px2);
        addView(contentView);
        return contentView;
    }

    private void reMeasureSpec() {
        int iDp2px = ToolUtils.dp2px(getContext(), 12);
        int i2 = getContext().getResources().getDisplayMetrics().widthPixels;
        int i3 = getContext().getResources().getDisplayMetrics().heightPixels;
        int dimenFromSystemUI = (int) NotificationUtils.getDimenFromSystemUI(getContext(), "notification_panel_width", 0);
        int iMin = Math.min(i2, i3);
        if (dimenFromSystemUI <= 0 || iMin <= dimenFromSystemUI) {
            dimenFromSystemUI = iMin == 0 ? -1 : iMin;
        }
        if (dimenFromSystemUI != -1) {
            dimenFromSystemUI -= iDp2px * 2;
        }
        this.mContentView.getLayoutParams().width = dimenFromSystemUI;
    }

    public static void reportViewState(View view, int i2) {
        if (view instanceof ContentView) {
            ((ContentView) view).setState(i2);
        } else {
            HeadsUpLog.w("Error: report view state is not ContentView.");
        }
    }

    public void attachCard(AbsCardView absCardView) {
        if (absCardView != null) {
            reMeasureSpec();
            this.mTouchHelper.resetState();
            this.mContentView.removeAllViews();
            this.mContentView.addView(absCardView);
            this.mContentView.setVisibility(4);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.mAnimHelper.isAnimDoing()) {
            this.mTouchHelper.dispatchTouchEvent(motionEvent);
            return super.dispatchTouchEvent(motionEvent);
        }
        HeadsUpLog.w("container touch forbidden, because is doing anim:" + motionEvent.getAction());
        return false;
    }

    public AbsCardView getActiveCard() {
        if (this.mContentView.getChildCount() > 0) {
            return (AbsCardView) this.mContentView.getChildAt(0);
        }
        return null;
    }

    public View getContentView() {
        return this.mContentView;
    }

    public void hideAnim() {
        this.mAnimHelper.startAnim(false);
    }

    public void removeViews() {
        ContentView contentView = this.mContentView;
        if (contentView != null) {
            contentView.removeAllViews();
        }
    }

    public void setOnStateListener(OnStateListener onStateListener) {
        this.mStateListener = onStateListener;
    }

    public void showAnim() {
        this.mAnimHelper.startAnim(true);
    }
}
