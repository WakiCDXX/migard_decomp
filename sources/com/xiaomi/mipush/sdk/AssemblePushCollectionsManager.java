package com.xiaomi.mipush.sdk;

import android.content.Context;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.xmpush.thrift.ConfigKey;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class AssemblePushCollectionsManager implements AbstractPushManager {
    private static volatile AssemblePushCollectionsManager sInstance;
    private PushConfiguration mConfiguration;
    private Context mContext;
    private boolean oldOCValue = false;
    private Map<AssemblePush, AbstractPushManager> mManagers = new HashMap();

    /* JADX INFO: renamed from: com.xiaomi.mipush.sdk.AssemblePushCollectionsManager$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush;

        static {
            int[] iArr = new int[AssemblePush.values().length];
            $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush = iArr;
            try {
                iArr[AssemblePush.ASSEMBLE_PUSH_HUAWEI.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[AssemblePush.ASSEMBLE_PUSH_FCM.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[AssemblePush.ASSEMBLE_PUSH_COS.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[AssemblePush.ASSEMBLE_PUSH_FTOS.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private AssemblePushCollectionsManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static AssemblePushCollectionsManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AssemblePushCollectionsManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new AssemblePushCollectionsManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0082  */
    /* JADX WARN: Code duplicated, block: B:35:0x0114  */
    /* JADX WARN: Code duplicated, block: B:50:0x019b  */
    private void initAssemblePushManager() {
        AssemblePush assemblePush;
        AbstractPushManager manager;
        AssemblePush assemblePush2;
        AbstractPushManager manager2;
        AssemblePush assemblePush3;
        AbstractPushManager manager3;
        AbstractPushManager manager4;
        PushConfiguration pushConfiguration = this.mConfiguration;
        if (pushConfiguration != null) {
            if (pushConfiguration.getOpenHmsPush()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ASSEMBLE_PUSH : ");
                sb.append(" HW user switch : " + this.mConfiguration.getOpenHmsPush() + " HW online switch : " + AssemblePushHelper.isOpenAssemblePushOnlineSwitch(this.mContext, AssemblePush.ASSEMBLE_PUSH_HUAWEI) + " HW isSupport : " + AssemblePushUtils.isHmsSupport(this.mContext));
                MyLog.w(sb.toString());
            }
            if (this.mConfiguration.getOpenHmsPush()) {
                Context context = this.mContext;
                AssemblePush assemblePush4 = AssemblePush.ASSEMBLE_PUSH_HUAWEI;
                if (AssemblePushHelper.isOpenAssemblePushOnlineSwitch(context, assemblePush4) && AssemblePushUtils.isHmsSupport(this.mContext)) {
                    if (!contain(assemblePush4)) {
                        addManager(assemblePush4, PushManagerFactory.getManager(this.mContext, assemblePush4));
                    }
                    MyLog.v("hw manager add to list");
                } else {
                    assemblePush = AssemblePush.ASSEMBLE_PUSH_HUAWEI;
                    if (contain(assemblePush) && (manager = getManager(assemblePush)) != null) {
                        removeManager(assemblePush);
                        manager.unregister();
                    }
                }
            } else {
                assemblePush = AssemblePush.ASSEMBLE_PUSH_HUAWEI;
                if (contain(assemblePush)) {
                    removeManager(assemblePush);
                    manager.unregister();
                }
            }
            if (this.mConfiguration.getOpenFCMPush()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("ASSEMBLE_PUSH : ");
                sb2.append(" FCM user switch : " + this.mConfiguration.getOpenFCMPush() + " FCM online switch : " + AssemblePushHelper.isOpenAssemblePushOnlineSwitch(this.mContext, AssemblePush.ASSEMBLE_PUSH_FCM) + " FCM isSupport : " + AssemblePushUtils.isGoogleServiceSatisfied(this.mContext));
                MyLog.w(sb2.toString());
            }
            if (this.mConfiguration.getOpenFCMPush()) {
                Context context2 = this.mContext;
                AssemblePush assemblePush5 = AssemblePush.ASSEMBLE_PUSH_FCM;
                if (AssemblePushHelper.isOpenAssemblePushOnlineSwitch(context2, assemblePush5) && AssemblePushUtils.isGoogleServiceSatisfied(this.mContext)) {
                    if (!contain(assemblePush5)) {
                        addManager(assemblePush5, PushManagerFactory.getManager(this.mContext, assemblePush5));
                    }
                    MyLog.v("fcm manager add to list");
                } else {
                    assemblePush2 = AssemblePush.ASSEMBLE_PUSH_FCM;
                    if (contain(assemblePush2) && (manager2 = getManager(assemblePush2)) != null) {
                        removeManager(assemblePush2);
                        manager2.unregister();
                    }
                }
            } else {
                assemblePush2 = AssemblePush.ASSEMBLE_PUSH_FCM;
                if (contain(assemblePush2)) {
                    removeManager(assemblePush2);
                    manager2.unregister();
                }
            }
            if (this.mConfiguration.getOpenCOSPush()) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("ASSEMBLE_PUSH : ");
                sb3.append(" COS user switch : " + this.mConfiguration.getOpenCOSPush() + " COS online switch : " + AssemblePushHelper.isOpenAssemblePushOnlineSwitch(this.mContext, AssemblePush.ASSEMBLE_PUSH_COS) + " COS isSupport : " + AssemblePushUtils.isColorOSPushSupport(this.mContext));
                MyLog.w(sb3.toString());
            }
            if (this.mConfiguration.getOpenCOSPush()) {
                Context context3 = this.mContext;
                AssemblePush assemblePush6 = AssemblePush.ASSEMBLE_PUSH_COS;
                if (AssemblePushHelper.isOpenAssemblePushOnlineSwitch(context3, assemblePush6) && AssemblePushUtils.isColorOSPushSupport(this.mContext)) {
                    addManager(assemblePush6, PushManagerFactory.getManager(this.mContext, assemblePush6));
                } else {
                    assemblePush3 = AssemblePush.ASSEMBLE_PUSH_COS;
                    if (contain(assemblePush3) && (manager3 = getManager(assemblePush3)) != null) {
                        removeManager(assemblePush3);
                        manager3.unregister();
                    }
                }
            } else {
                assemblePush3 = AssemblePush.ASSEMBLE_PUSH_COS;
                if (contain(assemblePush3)) {
                    removeManager(assemblePush3);
                    manager3.unregister();
                }
            }
            if (this.mConfiguration.getOpenFTOSPush()) {
                Context context4 = this.mContext;
                AssemblePush assemblePush7 = AssemblePush.ASSEMBLE_PUSH_FTOS;
                if (AssemblePushHelper.isOpenAssemblePushOnlineSwitch(context4, assemblePush7) && AssemblePushUtils.isFunTouchOSPushSupport(this.mContext)) {
                    addManager(assemblePush7, PushManagerFactory.getManager(this.mContext, assemblePush7));
                    return;
                }
            }
            AssemblePush assemblePush8 = AssemblePush.ASSEMBLE_PUSH_FTOS;
            if (!contain(assemblePush8) || (manager4 = getManager(assemblePush8)) == null) {
                return;
            }
            removeManager(assemblePush8);
            manager4.unregister();
        }
    }

    public void addManager(AssemblePush assemblePush, AbstractPushManager abstractPushManager) {
        if (abstractPushManager != null) {
            if (this.mManagers.containsKey(assemblePush)) {
                this.mManagers.remove(assemblePush);
            }
            this.mManagers.put(assemblePush, abstractPushManager);
        }
    }

    public boolean contain(AssemblePush assemblePush) {
        return this.mManagers.containsKey(assemblePush);
    }

    public AbstractPushManager getManager(AssemblePush assemblePush) {
        return this.mManagers.get(assemblePush);
    }

    public boolean getUserSwitch(AssemblePush assemblePush) {
        int i2 = AnonymousClass2.$SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[assemblePush.ordinal()];
        boolean openCOSPush = false;
        if (i2 == 1) {
            PushConfiguration pushConfiguration = this.mConfiguration;
            if (pushConfiguration != null) {
                return pushConfiguration.getOpenHmsPush();
            }
            return false;
        }
        if (i2 == 2) {
            PushConfiguration pushConfiguration2 = this.mConfiguration;
            if (pushConfiguration2 != null) {
                return pushConfiguration2.getOpenFCMPush();
            }
            return false;
        }
        if (i2 == 3) {
            PushConfiguration pushConfiguration3 = this.mConfiguration;
            if (pushConfiguration3 != null) {
                openCOSPush = pushConfiguration3.getOpenCOSPush();
            }
        } else if (i2 != 4) {
            return false;
        }
        PushConfiguration pushConfiguration4 = this.mConfiguration;
        return pushConfiguration4 != null ? pushConfiguration4.getOpenFTOSPush() : openCOSPush;
    }

    @Override // com.xiaomi.mipush.sdk.AbstractPushManager
    public void register() {
        MyLog.w("ASSEMBLE_PUSH : assemble push register");
        if (this.mManagers.size() <= 0) {
            initAssemblePushManager();
        }
        if (this.mManagers.size() > 0) {
            for (AbstractPushManager abstractPushManager : this.mManagers.values()) {
                if (abstractPushManager != null) {
                    abstractPushManager.register();
                }
            }
            AssemblePushHelper.checkAssemblePushStatus(this.mContext);
        }
    }

    public void removeManager(AssemblePush assemblePush) {
        this.mManagers.remove(assemblePush);
    }

    public void setConfiguration(PushConfiguration pushConfiguration) {
        this.mConfiguration = pushConfiguration;
        this.oldOCValue = OnlineConfig.getInstance(this.mContext).getBooleanValue(ConfigKey.AggregatePushSwitch.getValue(), true);
        if (this.mConfiguration.getOpenHmsPush() || this.mConfiguration.getOpenFCMPush() || this.mConfiguration.getOpenCOSPush() || this.mConfiguration.getOpenFTOSPush()) {
            OnlineConfig.getInstance(this.mContext).addOCUpdateCallbacks(new OnlineConfig.OCUpdateCallback(101, "assemblePush") { // from class: com.xiaomi.mipush.sdk.AssemblePushCollectionsManager.1
                @Override // com.xiaomi.push.service.OnlineConfig.OCUpdateCallback
                protected void onCallback() {
                    boolean booleanValue = OnlineConfig.getInstance(AssemblePushCollectionsManager.this.mContext).getBooleanValue(ConfigKey.AggregatePushSwitch.getValue(), true);
                    if (AssemblePushCollectionsManager.this.oldOCValue != booleanValue) {
                        AssemblePushCollectionsManager.this.oldOCValue = booleanValue;
                        AssemblePushHelper.registerAssemblePush(AssemblePushCollectionsManager.this.mContext);
                    }
                }
            });
        }
    }

    @Override // com.xiaomi.mipush.sdk.AbstractPushManager
    public void unregister() {
        MyLog.w("ASSEMBLE_PUSH : assemble push unregister");
        for (AbstractPushManager abstractPushManager : this.mManagers.values()) {
            if (abstractPushManager != null) {
                abstractPushManager.unregister();
            }
        }
        this.mManagers.clear();
    }
}
