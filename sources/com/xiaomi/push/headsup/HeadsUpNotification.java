package com.xiaomi.push.headsup;

import android.app.Notification;
import android.content.Context;
import com.xiaomi.push.service.NotificationUtils;

/* JADX INFO: loaded from: classes.dex */
public class HeadsUpNotification {
    public static final int MODE_DISCARDED_WHEN_KEYGUARD_LOCKED = 1;
    public static final int MODE_RECOVERED_WHEN_KEYGUARD_UNLOCKED = 2;
    public static final int MODE_STATUSBAR_WHEN_KEYGUARD_LOCKED = 3;
    public HeadsUpConstants.ActionAfterRemoved actionAfterRemoved;
    public int clickStyleButtonIndex;
    public AbsCardView contentView;
    public boolean discardWhenChannelClosed;
    public int id;
    public int keyguardLockedMode;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public Notification f1747n;
    public String pkgName;
    public boolean recovered;
    public int style;
    public boolean suppressEffects;

    public static class Builder {
        private Context mContext;
        private HeadsUpNotification mN;

        public Builder(Context context) {
            this(context, null);
        }

        public static Builder recoverBuilder(Context context, HeadsUpNotification headsUpNotification) {
            return new Builder(context, headsUpNotification);
        }

        HeadsUpNotification build() {
            HeadsUpNotification headsUpNotification = this.mN;
            if (headsUpNotification.contentView == null) {
                headsUpNotification.contentView = new DefaultCardView(this.mContext);
            }
            return this.mN;
        }

        public Builder setContentView(AbsCardView absCardView) {
            this.mN.contentView = absCardView;
            return this;
        }

        public Builder setDiscardWhenChannelClosed(boolean z2) {
            this.mN.discardWhenChannelClosed = z2;
            return this;
        }

        Builder setId(int i2) {
            this.mN.id = i2;
            return this;
        }

        public Builder setKeyguardLockedMode(int i2) {
            this.mN.keyguardLockedMode = i2;
            return this;
        }

        Builder setNotification(Notification notification) {
            HeadsUpNotification headsUpNotification = this.mN;
            headsUpNotification.f1747n = notification;
            headsUpNotification.pkgName = NotificationUtils.getTargetPackage(notification);
            this.mN.style = HeadsUpUtils.resolveHeadsUpStyle(notification);
            return this;
        }

        public Builder setSuppressEffects(boolean z2) {
            this.mN.suppressEffects = z2;
            return this;
        }

        private Builder(Context context, HeadsUpNotification headsUpNotification) {
            this.mContext = context;
            if (headsUpNotification == null) {
                this.mN = new HeadsUpNotification();
            } else {
                this.mN = headsUpNotification;
                headsUpNotification.recovered = true;
            }
        }
    }

    public String toString() {
        return this.pkgName + "|" + this.id + "|" + this.style;
    }

    private HeadsUpNotification() {
        this.clickStyleButtonIndex = -1;
        this.actionAfterRemoved = HeadsUpConstants.ActionAfterRemoved.DidRemoved;
    }
}
