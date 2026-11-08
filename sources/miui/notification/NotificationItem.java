package miui.notification;

import android.app.PendingIntent;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class NotificationItem {
    public String action;
    public Drawable actionIcon;
    public PendingIntent clearIntent;
    public PendingIntent clickActionIntent;
    public PendingIntent clickIntent;
    public String content;
    public Drawable icon;
    public int id;
    public String title;

    public String getAction() {
        return this.action;
    }

    public Drawable getActionIcon() {
        return this.actionIcon;
    }

    public PendingIntent getClearIntent() {
        return this.clearIntent;
    }

    public PendingIntent getClickActionIntent() {
        return this.clickActionIntent;
    }

    public PendingIntent getClickIntent() {
        return this.clickIntent;
    }

    public String getContent() {
        return this.content;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setAction(String str) {
        this.action = str;
    }

    public void setActionIcon(Drawable drawable) {
        this.actionIcon = drawable;
    }

    public void setClearIntent(PendingIntent pendingIntent) {
        this.clearIntent = pendingIntent;
    }

    public void setClickActionIntent(PendingIntent pendingIntent) {
        this.clickActionIntent = pendingIntent;
    }

    public void setClickIntent(PendingIntent pendingIntent) {
        this.clickIntent = pendingIntent;
    }

    public void setContent(String str) {
        this.content = str;
    }

    public void setIcon(Drawable drawable) {
        this.icon = drawable;
    }

    public void setId(int i2) {
        this.id = i2;
    }

    public void setTitle(String str) {
        this.title = str;
    }
}
