package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.BitSet;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class NotificationBarInfoItem implements TBase<NotificationBarInfoItem, Object>, Serializable, Cloneable {
    private static final int __ACTIONS_ISSET_ID = 7;
    private static final int __ARRIVEDTIME_ISSET_ID = 3;
    private static final int __COLOR_ISSET_ID = 10;
    private static final int __CUSTOMLAYOUT_ISSET_ID = 2;
    private static final int __DEFAULTS_ISSET_ID = 9;
    private static final int __FLAGS_ISSET_ID = 5;
    private static final int __NOTIFYID_ISSET_ID = 1;
    private static final int __PRIORITY_ISSET_ID = 6;
    private static final int __REMOVEDTIME_ISSET_ID = 4;
    private static final int __TYPE_ISSET_ID = 0;
    private static final int __VISIBILITY_ISSET_ID = 8;
    private BitSet __isset_bit_vector;
    public int actions;
    public long arrivedTime;
    public String category;
    public int color;
    public String content;
    public boolean customLayout;
    public int defaults;
    public int flags;
    public String intentUri;
    public int notifyId;
    public String packageName;
    public int priority;
    public long removedTime;
    public String style;
    public String title;
    public int type;
    public int visibility;
    private static final g STRUCT_DESC = new g("NotificationBarInfoItem");
    private static final a TYPE_FIELD_DESC = new a("", (byte) 8, 1);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 2);
    private static final a NOTIFY_ID_FIELD_DESC = new a("", (byte) 8, 3);
    private static final a TITLE_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a CONTENT_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a CUSTOM_LAYOUT_FIELD_DESC = new a("", (byte) 2, 6);
    private static final a STYLE_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a INTENT_URI_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a ARRIVED_TIME_FIELD_DESC = new a("", (byte) 10, 9);
    private static final a REMOVED_TIME_FIELD_DESC = new a("", (byte) 10, 10);
    private static final a FLAGS_FIELD_DESC = new a("", (byte) 8, 11);
    private static final a PRIORITY_FIELD_DESC = new a("", (byte) 8, 12);
    private static final a ACTIONS_FIELD_DESC = new a("", (byte) 8, 13);
    private static final a VISIBILITY_FIELD_DESC = new a("", (byte) 8, 14);
    private static final a DEFAULTS_FIELD_DESC = new a("", (byte) 8, 15);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 16);
    private static final a COLOR_FIELD_DESC = new a("", (byte) 8, 17);

    public NotificationBarInfoItem() {
        this.__isset_bit_vector = new BitSet(11);
    }

    public void clear() {
        setTypeIsSet(false);
        this.type = 0;
        this.packageName = null;
        setNotifyIdIsSet(false);
        this.notifyId = 0;
        this.title = null;
        this.content = null;
        setCustomLayoutIsSet(false);
        this.customLayout = false;
        this.style = null;
        this.intentUri = null;
        setArrivedTimeIsSet(false);
        this.arrivedTime = 0L;
        setRemovedTimeIsSet(false);
        this.removedTime = 0L;
        setFlagsIsSet(false);
        this.flags = 0;
        setPriorityIsSet(false);
        this.priority = 0;
        setActionsIsSet(false);
        this.actions = 0;
        setVisibilityIsSet(false);
        this.visibility = 0;
        setDefaultsIsSet(false);
        this.defaults = 0;
        this.category = null;
        setColorIsSet(false);
        this.color = 0;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof NotificationBarInfoItem)) {
            return equals((NotificationBarInfoItem) obj);
        }
        return false;
    }

    public int getActions() {
        return this.actions;
    }

    public long getArrivedTime() {
        return this.arrivedTime;
    }

    public String getCategory() {
        return this.category;
    }

    public int getColor() {
        return this.color;
    }

    public String getContent() {
        return this.content;
    }

    public int getDefaults() {
        return this.defaults;
    }

    public int getFlags() {
        return this.flags;
    }

    public String getIntentUri() {
        return this.intentUri;
    }

    public int getNotifyId() {
        return this.notifyId;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public int getPriority() {
        return this.priority;
    }

    public long getRemovedTime() {
        return this.removedTime;
    }

    public String getStyle() {
        return this.style;
    }

    public String getTitle() {
        return this.title;
    }

    public int getType() {
        return this.type;
    }

    public int getVisibility() {
        return this.visibility;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isCustomLayout() {
        return this.customLayout;
    }

    public boolean isSetActions() {
        return this.__isset_bit_vector.get(7);
    }

    public boolean isSetArrivedTime() {
        return this.__isset_bit_vector.get(3);
    }

    public boolean isSetCategory() {
        return this.category != null;
    }

    public boolean isSetColor() {
        return this.__isset_bit_vector.get(10);
    }

    public boolean isSetContent() {
        return this.content != null;
    }

    public boolean isSetCustomLayout() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetDefaults() {
        return this.__isset_bit_vector.get(9);
    }

    public boolean isSetFlags() {
        return this.__isset_bit_vector.get(5);
    }

    public boolean isSetIntentUri() {
        return this.intentUri != null;
    }

    public boolean isSetNotifyId() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetPriority() {
        return this.__isset_bit_vector.get(6);
    }

    public boolean isSetRemovedTime() {
        return this.__isset_bit_vector.get(4);
    }

    public boolean isSetStyle() {
        return this.style != null;
    }

    public boolean isSetTitle() {
        return this.title != null;
    }

    public boolean isSetType() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetVisibility() {
        return this.__isset_bit_vector.get(8);
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                dVar.r();
                validate();
                return;
            }
            switch (aVarE.f2151c) {
                case 1:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.type = dVar.h();
                        setTypeIsSet(true);
                    }
                    break;
                case 2:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.packageName = dVar.p();
                    }
                    break;
                case 3:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.notifyId = dVar.h();
                        setNotifyIdIsSet(true);
                    }
                    break;
                case 4:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.title = dVar.p();
                    }
                    break;
                case 5:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.content = dVar.p();
                    }
                    break;
                case 6:
                    if (b2 != 2) {
                        e.a(dVar, b2);
                    } else {
                        this.customLayout = dVar.b();
                        setCustomLayoutIsSet(true);
                    }
                    break;
                case 7:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.style = dVar.p();
                    }
                    break;
                case 8:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.intentUri = dVar.p();
                    }
                    break;
                case 9:
                    if (b2 != 10) {
                        e.a(dVar, b2);
                    } else {
                        this.arrivedTime = dVar.i();
                        setArrivedTimeIsSet(true);
                    }
                    break;
                case 10:
                    if (b2 != 10) {
                        e.a(dVar, b2);
                    } else {
                        this.removedTime = dVar.i();
                        setRemovedTimeIsSet(true);
                    }
                    break;
                case 11:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.flags = dVar.h();
                        setFlagsIsSet(true);
                    }
                    break;
                case 12:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.priority = dVar.h();
                        setPriorityIsSet(true);
                    }
                    break;
                case 13:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.actions = dVar.h();
                        setActionsIsSet(true);
                    }
                    break;
                case 14:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.visibility = dVar.h();
                        setVisibilityIsSet(true);
                    }
                    break;
                case 15:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.defaults = dVar.h();
                        setDefaultsIsSet(true);
                    }
                    break;
                case 16:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.category = dVar.p();
                    }
                    break;
                case 17:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.color = dVar.h();
                        setColorIsSet(true);
                    }
                    break;
                default:
                    e.a(dVar, b2);
                    break;
            }
            dVar.f();
        }
    }

    public NotificationBarInfoItem setActions(int i2) {
        this.actions = i2;
        setActionsIsSet(true);
        return this;
    }

    public void setActionsIsSet(boolean z2) {
        this.__isset_bit_vector.set(7, z2);
    }

    public NotificationBarInfoItem setArrivedTime(long j2) {
        this.arrivedTime = j2;
        setArrivedTimeIsSet(true);
        return this;
    }

    public void setArrivedTimeIsSet(boolean z2) {
        this.__isset_bit_vector.set(3, z2);
    }

    public NotificationBarInfoItem setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public NotificationBarInfoItem setColor(int i2) {
        this.color = i2;
        setColorIsSet(true);
        return this;
    }

    public void setColorIsSet(boolean z2) {
        this.__isset_bit_vector.set(10, z2);
    }

    public NotificationBarInfoItem setContent(String str) {
        this.content = str;
        return this;
    }

    public void setContentIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.content = null;
    }

    public NotificationBarInfoItem setCustomLayout(boolean z2) {
        this.customLayout = z2;
        setCustomLayoutIsSet(true);
        return this;
    }

    public void setCustomLayoutIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public NotificationBarInfoItem setDefaults(int i2) {
        this.defaults = i2;
        setDefaultsIsSet(true);
        return this;
    }

    public void setDefaultsIsSet(boolean z2) {
        this.__isset_bit_vector.set(9, z2);
    }

    public NotificationBarInfoItem setFlags(int i2) {
        this.flags = i2;
        setFlagsIsSet(true);
        return this;
    }

    public void setFlagsIsSet(boolean z2) {
        this.__isset_bit_vector.set(5, z2);
    }

    public NotificationBarInfoItem setIntentUri(String str) {
        this.intentUri = str;
        return this;
    }

    public void setIntentUriIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.intentUri = null;
    }

    public NotificationBarInfoItem setNotifyId(int i2) {
        this.notifyId = i2;
        setNotifyIdIsSet(true);
        return this;
    }

    public void setNotifyIdIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public NotificationBarInfoItem setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public NotificationBarInfoItem setPriority(int i2) {
        this.priority = i2;
        setPriorityIsSet(true);
        return this;
    }

    public void setPriorityIsSet(boolean z2) {
        this.__isset_bit_vector.set(6, z2);
    }

    public NotificationBarInfoItem setRemovedTime(long j2) {
        this.removedTime = j2;
        setRemovedTimeIsSet(true);
        return this;
    }

    public void setRemovedTimeIsSet(boolean z2) {
        this.__isset_bit_vector.set(4, z2);
    }

    public NotificationBarInfoItem setStyle(String str) {
        this.style = str;
        return this;
    }

    public void setStyleIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.style = null;
    }

    public NotificationBarInfoItem setTitle(String str) {
        this.title = str;
        return this;
    }

    public void setTitleIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.title = null;
    }

    public NotificationBarInfoItem setType(int i2) {
        this.type = i2;
        setTypeIsSet(true);
        return this;
    }

    public void setTypeIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public NotificationBarInfoItem setVisibility(int i2) {
        this.visibility = i2;
        setVisibilityIsSet(true);
        return this;
    }

    public void setVisibilityIsSet(boolean z2) {
        this.__isset_bit_vector.set(8, z2);
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("NotificationBarInfoItem(");
        boolean z3 = false;
        if (isSetType()) {
            sb.append("type:");
            sb.append(this.type);
            z2 = false;
        } else {
            z2 = true;
        }
        if (isSetPackageName()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("packageName:");
            String str = this.packageName;
            if (str == null) {
                sb.append("null");
            } else {
                sb.append(str);
            }
            z2 = false;
        }
        if (isSetNotifyId()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("notifyId:");
            sb.append(this.notifyId);
            z2 = false;
        }
        if (isSetTitle()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("title:");
            String str2 = this.title;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
            z2 = false;
        }
        if (isSetContent()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("content:");
            String str3 = this.content;
            if (str3 == null) {
                sb.append("null");
            } else {
                sb.append(str3);
            }
            z2 = false;
        }
        if (isSetCustomLayout()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("customLayout:");
            sb.append(this.customLayout);
            z2 = false;
        }
        if (isSetStyle()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("style:");
            String str4 = this.style;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
            z2 = false;
        }
        if (isSetIntentUri()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("intentUri:");
            String str5 = this.intentUri;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
            z2 = false;
        }
        if (isSetArrivedTime()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("arrivedTime:");
            sb.append(this.arrivedTime);
            z2 = false;
        }
        if (isSetRemovedTime()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("removedTime:");
            sb.append(this.removedTime);
            z2 = false;
        }
        if (isSetFlags()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("flags:");
            sb.append(this.flags);
            z2 = false;
        }
        if (isSetPriority()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("priority:");
            sb.append(this.priority);
            z2 = false;
        }
        if (isSetActions()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("actions:");
            sb.append(this.actions);
            z2 = false;
        }
        if (isSetVisibility()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("visibility:");
            sb.append(this.visibility);
            z2 = false;
        }
        if (isSetDefaults()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("defaults:");
            sb.append(this.defaults);
            z2 = false;
        }
        if (isSetCategory()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("category:");
            String str6 = this.category;
            if (str6 == null) {
                sb.append("null");
            } else {
                sb.append(str6);
            }
        } else {
            z3 = z2;
        }
        if (isSetColor()) {
            if (!z3) {
                sb.append(", ");
            }
            sb.append("color:");
            sb.append(this.color);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetActions() {
        this.__isset_bit_vector.clear(7);
    }

    public void unsetArrivedTime() {
        this.__isset_bit_vector.clear(3);
    }

    public void unsetCategory() {
        this.category = null;
    }

    public void unsetColor() {
        this.__isset_bit_vector.clear(10);
    }

    public void unsetContent() {
        this.content = null;
    }

    public void unsetCustomLayout() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetDefaults() {
        this.__isset_bit_vector.clear(9);
    }

    public void unsetFlags() {
        this.__isset_bit_vector.clear(5);
    }

    public void unsetIntentUri() {
        this.intentUri = null;
    }

    public void unsetNotifyId() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetPriority() {
        this.__isset_bit_vector.clear(6);
    }

    public void unsetRemovedTime() {
        this.__isset_bit_vector.clear(4);
    }

    public void unsetStyle() {
        this.style = null;
    }

    public void unsetTitle() {
        this.title = null;
    }

    public void unsetType() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetVisibility() {
        this.__isset_bit_vector.clear(8);
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (isSetType()) {
            dVar.w(TYPE_FIELD_DESC);
            dVar.A(this.type);
            dVar.x();
        }
        if (this.packageName != null && isSetPackageName()) {
            dVar.w(PACKAGE_NAME_FIELD_DESC);
            dVar.I(this.packageName);
            dVar.x();
        }
        if (isSetNotifyId()) {
            dVar.w(NOTIFY_ID_FIELD_DESC);
            dVar.A(this.notifyId);
            dVar.x();
        }
        if (this.title != null && isSetTitle()) {
            dVar.w(TITLE_FIELD_DESC);
            dVar.I(this.title);
            dVar.x();
        }
        if (this.content != null && isSetContent()) {
            dVar.w(CONTENT_FIELD_DESC);
            dVar.I(this.content);
            dVar.x();
        }
        if (isSetCustomLayout()) {
            dVar.w(CUSTOM_LAYOUT_FIELD_DESC);
            dVar.u(this.customLayout);
            dVar.x();
        }
        if (this.style != null && isSetStyle()) {
            dVar.w(STYLE_FIELD_DESC);
            dVar.I(this.style);
            dVar.x();
        }
        if (this.intentUri != null && isSetIntentUri()) {
            dVar.w(INTENT_URI_FIELD_DESC);
            dVar.I(this.intentUri);
            dVar.x();
        }
        if (isSetArrivedTime()) {
            dVar.w(ARRIVED_TIME_FIELD_DESC);
            dVar.B(this.arrivedTime);
            dVar.x();
        }
        if (isSetRemovedTime()) {
            dVar.w(REMOVED_TIME_FIELD_DESC);
            dVar.B(this.removedTime);
            dVar.x();
        }
        if (isSetFlags()) {
            dVar.w(FLAGS_FIELD_DESC);
            dVar.A(this.flags);
            dVar.x();
        }
        if (isSetPriority()) {
            dVar.w(PRIORITY_FIELD_DESC);
            dVar.A(this.priority);
            dVar.x();
        }
        if (isSetActions()) {
            dVar.w(ACTIONS_FIELD_DESC);
            dVar.A(this.actions);
            dVar.x();
        }
        if (isSetVisibility()) {
            dVar.w(VISIBILITY_FIELD_DESC);
            dVar.A(this.visibility);
            dVar.x();
        }
        if (isSetDefaults()) {
            dVar.w(DEFAULTS_FIELD_DESC);
            dVar.A(this.defaults);
            dVar.x();
        }
        if (this.category != null && isSetCategory()) {
            dVar.w(CATEGORY_FIELD_DESC);
            dVar.I(this.category);
            dVar.x();
        }
        if (isSetColor()) {
            dVar.w(COLOR_FIELD_DESC);
            dVar.A(this.color);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(NotificationBarInfoItem notificationBarInfoItem) {
        int iD;
        int iG;
        int iD2;
        int iD3;
        int iD4;
        int iD5;
        int iD6;
        int iE;
        int iE2;
        int iG2;
        int iG3;
        int iL;
        int iG4;
        int iG5;
        int iD7;
        int iG6;
        int iD8;
        if (!getClass().equals(notificationBarInfoItem.getClass())) {
            return getClass().getName().compareTo(notificationBarInfoItem.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetType()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetType()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetType() && (iD8 = g0.a.d(this.type, notificationBarInfoItem.type)) != 0) {
            return iD8;
        }
        int iCompareTo2 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetPackageName()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetPackageName() && (iG6 = g0.a.g(this.packageName, notificationBarInfoItem.packageName)) != 0) {
            return iG6;
        }
        int iCompareTo3 = Boolean.valueOf(isSetNotifyId()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetNotifyId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetNotifyId() && (iD7 = g0.a.d(this.notifyId, notificationBarInfoItem.notifyId)) != 0) {
            return iD7;
        }
        int iCompareTo4 = Boolean.valueOf(isSetTitle()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetTitle()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetTitle() && (iG5 = g0.a.g(this.title, notificationBarInfoItem.title)) != 0) {
            return iG5;
        }
        int iCompareTo5 = Boolean.valueOf(isSetContent()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetContent()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetContent() && (iG4 = g0.a.g(this.content, notificationBarInfoItem.content)) != 0) {
            return iG4;
        }
        int iCompareTo6 = Boolean.valueOf(isSetCustomLayout()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetCustomLayout()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetCustomLayout() && (iL = g0.a.l(this.customLayout, notificationBarInfoItem.customLayout)) != 0) {
            return iL;
        }
        int iCompareTo7 = Boolean.valueOf(isSetStyle()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetStyle()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetStyle() && (iG3 = g0.a.g(this.style, notificationBarInfoItem.style)) != 0) {
            return iG3;
        }
        int iCompareTo8 = Boolean.valueOf(isSetIntentUri()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetIntentUri()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetIntentUri() && (iG2 = g0.a.g(this.intentUri, notificationBarInfoItem.intentUri)) != 0) {
            return iG2;
        }
        int iCompareTo9 = Boolean.valueOf(isSetArrivedTime()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetArrivedTime()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetArrivedTime() && (iE2 = g0.a.e(this.arrivedTime, notificationBarInfoItem.arrivedTime)) != 0) {
            return iE2;
        }
        int iCompareTo10 = Boolean.valueOf(isSetRemovedTime()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetRemovedTime()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetRemovedTime() && (iE = g0.a.e(this.removedTime, notificationBarInfoItem.removedTime)) != 0) {
            return iE;
        }
        int iCompareTo11 = Boolean.valueOf(isSetFlags()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetFlags()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (isSetFlags() && (iD6 = g0.a.d(this.flags, notificationBarInfoItem.flags)) != 0) {
            return iD6;
        }
        int iCompareTo12 = Boolean.valueOf(isSetPriority()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetPriority()));
        if (iCompareTo12 != 0) {
            return iCompareTo12;
        }
        if (isSetPriority() && (iD5 = g0.a.d(this.priority, notificationBarInfoItem.priority)) != 0) {
            return iD5;
        }
        int iCompareTo13 = Boolean.valueOf(isSetActions()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetActions()));
        if (iCompareTo13 != 0) {
            return iCompareTo13;
        }
        if (isSetActions() && (iD4 = g0.a.d(this.actions, notificationBarInfoItem.actions)) != 0) {
            return iD4;
        }
        int iCompareTo14 = Boolean.valueOf(isSetVisibility()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetVisibility()));
        if (iCompareTo14 != 0) {
            return iCompareTo14;
        }
        if (isSetVisibility() && (iD3 = g0.a.d(this.visibility, notificationBarInfoItem.visibility)) != 0) {
            return iD3;
        }
        int iCompareTo15 = Boolean.valueOf(isSetDefaults()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetDefaults()));
        if (iCompareTo15 != 0) {
            return iCompareTo15;
        }
        if (isSetDefaults() && (iD2 = g0.a.d(this.defaults, notificationBarInfoItem.defaults)) != 0) {
            return iD2;
        }
        int iCompareTo16 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetCategory()));
        if (iCompareTo16 != 0) {
            return iCompareTo16;
        }
        if (isSetCategory() && (iG = g0.a.g(this.category, notificationBarInfoItem.category)) != 0) {
            return iG;
        }
        int iCompareTo17 = Boolean.valueOf(isSetColor()).compareTo(Boolean.valueOf(notificationBarInfoItem.isSetColor()));
        if (iCompareTo17 != 0) {
            return iCompareTo17;
        }
        if (!isSetColor() || (iD = g0.a.d(this.color, notificationBarInfoItem.color)) == 0) {
            return 0;
        }
        return iD;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public NotificationBarInfoItem m22deepCopy() {
        return new NotificationBarInfoItem(this);
    }

    public NotificationBarInfoItem(NotificationBarInfoItem notificationBarInfoItem) {
        BitSet bitSet = new BitSet(11);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(notificationBarInfoItem.__isset_bit_vector);
        this.type = notificationBarInfoItem.type;
        if (notificationBarInfoItem.isSetPackageName()) {
            this.packageName = notificationBarInfoItem.packageName;
        }
        this.notifyId = notificationBarInfoItem.notifyId;
        if (notificationBarInfoItem.isSetTitle()) {
            this.title = notificationBarInfoItem.title;
        }
        if (notificationBarInfoItem.isSetContent()) {
            this.content = notificationBarInfoItem.content;
        }
        this.customLayout = notificationBarInfoItem.customLayout;
        if (notificationBarInfoItem.isSetStyle()) {
            this.style = notificationBarInfoItem.style;
        }
        if (notificationBarInfoItem.isSetIntentUri()) {
            this.intentUri = notificationBarInfoItem.intentUri;
        }
        this.arrivedTime = notificationBarInfoItem.arrivedTime;
        this.removedTime = notificationBarInfoItem.removedTime;
        this.flags = notificationBarInfoItem.flags;
        this.priority = notificationBarInfoItem.priority;
        this.actions = notificationBarInfoItem.actions;
        this.visibility = notificationBarInfoItem.visibility;
        this.defaults = notificationBarInfoItem.defaults;
        if (notificationBarInfoItem.isSetCategory()) {
            this.category = notificationBarInfoItem.category;
        }
        this.color = notificationBarInfoItem.color;
    }

    public boolean equals(NotificationBarInfoItem notificationBarInfoItem) {
        if (notificationBarInfoItem == null) {
            return false;
        }
        boolean zIsSetType = isSetType();
        boolean zIsSetType2 = notificationBarInfoItem.isSetType();
        if ((zIsSetType || zIsSetType2) && !(zIsSetType && zIsSetType2 && this.type == notificationBarInfoItem.type)) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = notificationBarInfoItem.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(notificationBarInfoItem.packageName))) {
            return false;
        }
        boolean zIsSetNotifyId = isSetNotifyId();
        boolean zIsSetNotifyId2 = notificationBarInfoItem.isSetNotifyId();
        if ((zIsSetNotifyId || zIsSetNotifyId2) && !(zIsSetNotifyId && zIsSetNotifyId2 && this.notifyId == notificationBarInfoItem.notifyId)) {
            return false;
        }
        boolean zIsSetTitle = isSetTitle();
        boolean zIsSetTitle2 = notificationBarInfoItem.isSetTitle();
        if ((zIsSetTitle || zIsSetTitle2) && !(zIsSetTitle && zIsSetTitle2 && this.title.equals(notificationBarInfoItem.title))) {
            return false;
        }
        boolean zIsSetContent = isSetContent();
        boolean zIsSetContent2 = notificationBarInfoItem.isSetContent();
        if ((zIsSetContent || zIsSetContent2) && !(zIsSetContent && zIsSetContent2 && this.content.equals(notificationBarInfoItem.content))) {
            return false;
        }
        boolean zIsSetCustomLayout = isSetCustomLayout();
        boolean zIsSetCustomLayout2 = notificationBarInfoItem.isSetCustomLayout();
        if ((zIsSetCustomLayout || zIsSetCustomLayout2) && !(zIsSetCustomLayout && zIsSetCustomLayout2 && this.customLayout == notificationBarInfoItem.customLayout)) {
            return false;
        }
        boolean zIsSetStyle = isSetStyle();
        boolean zIsSetStyle2 = notificationBarInfoItem.isSetStyle();
        if ((zIsSetStyle || zIsSetStyle2) && !(zIsSetStyle && zIsSetStyle2 && this.style.equals(notificationBarInfoItem.style))) {
            return false;
        }
        boolean zIsSetIntentUri = isSetIntentUri();
        boolean zIsSetIntentUri2 = notificationBarInfoItem.isSetIntentUri();
        if ((zIsSetIntentUri || zIsSetIntentUri2) && !(zIsSetIntentUri && zIsSetIntentUri2 && this.intentUri.equals(notificationBarInfoItem.intentUri))) {
            return false;
        }
        boolean zIsSetArrivedTime = isSetArrivedTime();
        boolean zIsSetArrivedTime2 = notificationBarInfoItem.isSetArrivedTime();
        if ((zIsSetArrivedTime || zIsSetArrivedTime2) && !(zIsSetArrivedTime && zIsSetArrivedTime2 && this.arrivedTime == notificationBarInfoItem.arrivedTime)) {
            return false;
        }
        boolean zIsSetRemovedTime = isSetRemovedTime();
        boolean zIsSetRemovedTime2 = notificationBarInfoItem.isSetRemovedTime();
        if ((zIsSetRemovedTime || zIsSetRemovedTime2) && !(zIsSetRemovedTime && zIsSetRemovedTime2 && this.removedTime == notificationBarInfoItem.removedTime)) {
            return false;
        }
        boolean zIsSetFlags = isSetFlags();
        boolean zIsSetFlags2 = notificationBarInfoItem.isSetFlags();
        if ((zIsSetFlags || zIsSetFlags2) && !(zIsSetFlags && zIsSetFlags2 && this.flags == notificationBarInfoItem.flags)) {
            return false;
        }
        boolean zIsSetPriority = isSetPriority();
        boolean zIsSetPriority2 = notificationBarInfoItem.isSetPriority();
        if ((zIsSetPriority || zIsSetPriority2) && !(zIsSetPriority && zIsSetPriority2 && this.priority == notificationBarInfoItem.priority)) {
            return false;
        }
        boolean zIsSetActions = isSetActions();
        boolean zIsSetActions2 = notificationBarInfoItem.isSetActions();
        if ((zIsSetActions || zIsSetActions2) && !(zIsSetActions && zIsSetActions2 && this.actions == notificationBarInfoItem.actions)) {
            return false;
        }
        boolean zIsSetVisibility = isSetVisibility();
        boolean zIsSetVisibility2 = notificationBarInfoItem.isSetVisibility();
        if ((zIsSetVisibility || zIsSetVisibility2) && !(zIsSetVisibility && zIsSetVisibility2 && this.visibility == notificationBarInfoItem.visibility)) {
            return false;
        }
        boolean zIsSetDefaults = isSetDefaults();
        boolean zIsSetDefaults2 = notificationBarInfoItem.isSetDefaults();
        if ((zIsSetDefaults || zIsSetDefaults2) && !(zIsSetDefaults && zIsSetDefaults2 && this.defaults == notificationBarInfoItem.defaults)) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = notificationBarInfoItem.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(notificationBarInfoItem.category))) {
            return false;
        }
        boolean zIsSetColor = isSetColor();
        boolean zIsSetColor2 = notificationBarInfoItem.isSetColor();
        if (zIsSetColor || zIsSetColor2) {
            return zIsSetColor && zIsSetColor2 && this.color == notificationBarInfoItem.color;
        }
        return true;
    }

    public void validate() throws TException {
    }
}
