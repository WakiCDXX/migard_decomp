package com.xiaomi.push.headsup;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import com.xiaomi.channel.commonutils.android.SafeHandler;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.push.service.NotificationUtils;

/* JADX INFO: loaded from: classes.dex */
class ViewDisplayController {
    private Handler mHandler = new SafeHandler(Looper.getMainLooper());
    private WindowManager.LayoutParams mLayoutParams;
    private OnVisibilityChangedListener mListener;
    private Runnable mTimeoutDismissRunnable;
    private TouchableRegionHelper mTouchableRegionHelper;
    private ViewContainer mViewContainer;
    private WindowManager mWindowManager;

    interface OnVisibilityChangedListener {
        void onDismiss(int i2, HeadsUpNotification headsUpNotification);

        void onShow(HeadsUpNotification headsUpNotification);
    }

    public ViewDisplayController(Context context) {
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.mLayoutParams = layoutParams;
        layoutParams.flags = 296;
        layoutParams.type = 2010;
        layoutParams.width = -1;
        layoutParams.height = -2;
        layoutParams.gravity = 8388659;
        layoutParams.format = -3;
        layoutParams.x = 0;
        layoutParams.y = 70;
        layoutParams.alpha = 1.0f;
        ViewContainer viewContainer = new ViewContainer(context);
        this.mViewContainer = viewContainer;
        viewContainer.setOnStateListener(new ViewContainer.OnStateListener() { // from class: com.xiaomi.push.headsup.ViewDisplayController.1
            @Override // com.xiaomi.push.headsup.ViewContainer.OnStateListener
            public void onStateChanged(int i2) {
                if (i2 == 0) {
                    ViewDisplayController.this.forceDismiss();
                } else if (i2 == 1) {
                    ViewDisplayController.this.dismissByReason(3);
                } else {
                    if (i2 != 2) {
                        return;
                    }
                    ViewDisplayController.this.dismissByReason(1);
                }
            }
        });
        this.mTimeoutDismissRunnable = new Runnable() { // from class: com.xiaomi.push.headsup.ViewDisplayController.2
            @Override // java.lang.Runnable
            public void run() {
                ViewDisplayController.this.dismissByReason(4);
            }
        };
        this.mTouchableRegionHelper = new TouchableRegionHelper(this.mViewContainer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissByReason(int i2) {
        HeadsUpLog.d("cancel notification reason:" + i2);
        if (!isShowing()) {
            HeadsUpLog.w("cancel forbidden, because not show");
            return;
        }
        boolean z2 = (i2 == 2 || i2 == 3 || i2 == 5 || i2 == 7) ? false : true;
        HeadsUpNotification currentCardInfo = getCurrentCardInfo();
        doDismiss(z2);
        OnVisibilityChangedListener onVisibilityChangedListener = this.mListener;
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onDismiss(i2, currentCardInfo);
        }
    }

    private void doDismiss(boolean z2) {
        HeadsUpLog.d("dismiss do anim " + z2);
        if (z2) {
            this.mViewContainer.hideAnim();
        } else {
            forceDismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShow(AbsCardView absCardView) {
        this.mViewContainer.attachCard(absCardView);
        this.mWindowManager.addView(this.mViewContainer, this.mLayoutParams);
        this.mViewContainer.showAnim();
        this.mTouchableRegionHelper.onAdd();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceDismiss() {
        HeadsUpLog.d("forceDismiss remove view:" + getCurrentCardInfo());
        this.mHandler.removeCallbacks(this.mTimeoutDismissRunnable);
        if (this.mViewContainer.getParent() == null) {
            HeadsUpLog.w("forceDismiss invalid call, not attached!!!");
            return;
        }
        this.mTouchableRegionHelper.onRemove();
        this.mViewContainer.removeViews();
        this.mWindowManager.removeView(this.mViewContainer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isShowing() {
        return this.mViewContainer.getParent() != null;
    }

    public void dismiss(int i2) {
        HeadsUpNotification currentCardInfo = getCurrentCardInfo();
        if (currentCardInfo != null && currentCardInfo.id == i2) {
            this.mHandler.post(new Runnable() { // from class: com.xiaomi.push.headsup.ViewDisplayController.4
                @Override // java.lang.Runnable
                public void run() {
                    ViewDisplayController.this.dismissByReason(6);
                }
            });
            return;
        }
        HeadsUpLog.w("dismiss " + i2 + " not equals current " + currentCardInfo);
    }

    public Context getContext() {
        return this.mViewContainer.getContext();
    }

    public HeadsUpNotification getCurrentCardInfo() {
        AbsCardView activeCard = this.mViewContainer.getActiveCard();
        if (activeCard != null) {
            return activeCard.getData();
        }
        return null;
    }

    public void keyguardLocked() {
        this.mHandler.post(new Runnable() { // from class: com.xiaomi.push.headsup.ViewDisplayController.5
            @Override // java.lang.Runnable
            public void run() {
                ViewDisplayController.this.dismissByReason(5);
            }
        });
    }

    public void setOnVisibilityChangedListener(OnVisibilityChangedListener onVisibilityChangedListener) {
        this.mListener = onVisibilityChangedListener;
    }

    public void show(final HeadsUpNotification headsUpNotification) {
        HeadsUpLog.d("show " + headsUpNotification);
        this.mHandler.post(new Runnable() { // from class: com.xiaomi.push.headsup.ViewDisplayController.3
            @Override // java.lang.Runnable
            public void run() {
                if (ViewDisplayController.this.isShowing()) {
                    HeadsUpNotification currentCardInfo = ViewDisplayController.this.getCurrentCardInfo();
                    int i2 = (currentCardInfo == null || currentCardInfo.id != headsUpNotification.id) ? 2 : 7;
                    HeadsUpLog.w("replace or update: " + i2);
                    ViewDisplayController.this.dismissByReason(i2);
                }
                HeadsUpNotification headsUpNotification2 = headsUpNotification;
                AbsCardView absCardView = headsUpNotification2.contentView;
                absCardView.bindData(headsUpNotification2);
                ViewDisplayController.this.doShow(absCardView);
                ViewDisplayController.this.mHandler.postDelayed(ViewDisplayController.this.mTimeoutDismissRunnable, ToolUtils.parseInt((String) NotificationUtils.resolveNotification(headsUpNotification.f1747n, HeadsUpConstants.EXTRA_HEADSUP_TIMEOUT), 5000));
                if (ViewDisplayController.this.mListener != null) {
                    ViewDisplayController.this.mListener.onShow(headsUpNotification);
                }
            }
        });
    }
}
