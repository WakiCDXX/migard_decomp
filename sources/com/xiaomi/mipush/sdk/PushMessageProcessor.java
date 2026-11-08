package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.SharedPrefsCompat;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.mipush.sdk.stat.PushStatClientManager;
import com.xiaomi.mipush.sdk.stat.upload.UploadDataHelper;
import com.xiaomi.push.clientreport.PerfMessageHelper;
import com.xiaomi.push.service.ChannelPushReportAgent;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.push.service.OnlineConfigHelper;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.RedundantUploadInfoHelper;
import com.xiaomi.push.service.clientReport.PushClientReportHelper;
import com.xiaomi.push.service.clientReport.PushClientReportManager;
import com.xiaomi.push.service.clientReport.ReportConstants;
import com.xiaomi.push.service.xmpush.Command;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.PushMessage;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.RegistrationReason;
import com.xiaomi.xmpush.thrift.XmPushActionAckMessage;
import com.xiaomi.xmpush.thrift.XmPushActionAckNotification;
import com.xiaomi.xmpush.thrift.XmPushActionCommandResult;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionCustomConfig;
import com.xiaomi.xmpush.thrift.XmPushActionNormalConfig;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushActionRegistrationResult;
import com.xiaomi.xmpush.thrift.XmPushActionSendMessage;
import com.xiaomi.xmpush.thrift.XmPushActionSubscriptionResult;
import com.xiaomi.xmpush.thrift.XmPushActionUnRegistrationResult;
import com.xiaomi.xmpush.thrift.XmPushActionUnSubscriptionResult;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TimeZone;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
public class PushMessageProcessor {
    private static final int MAX_MSG_CACHE_COUNT = 25;
    private static final String PREF_KEY_CACHED_MSGIDS = "pref_msg_ids";
    private static final String TAG = "PushMessageProcessor";
    private static Object lock = new Object();
    private static Queue<String> mCachedMsgIds;
    private static PushMessageProcessor sInstance;
    private Context sAppContext;

    /* JADX INFO: renamed from: com.xiaomi.mipush.sdk.PushMessageProcessor$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$xmpush$thrift$ActionType;

        static {
            int[] iArr = new int[ActionType.values().length];
            $SwitchMap$com$xiaomi$xmpush$thrift$ActionType = iArr;
            try {
                iArr[ActionType.SendMessage.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.Registration.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.UnRegistration.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.Subscription.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.UnSubscription.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.Command.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.Notification.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    private PushMessageProcessor(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.sAppContext = applicationContext;
        if (applicationContext == null) {
            this.sAppContext = context;
        }
    }

    private void ackMessage(XmPushActionContainer xmPushActionContainer) {
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        if (metaInfo != null) {
            metaInfo = RedundantUploadInfoHelper.cleanRedundantInfoForAck(metaInfo.m25deepCopy());
        }
        XmPushActionAckMessage xmPushActionAckMessage = new XmPushActionAckMessage();
        xmPushActionAckMessage.setAppId(xmPushActionContainer.getAppid());
        xmPushActionAckMessage.setId(metaInfo.getId());
        xmPushActionAckMessage.setMessageTs(metaInfo.getMessageTs());
        if (!TextUtils.isEmpty(metaInfo.getTopic())) {
            xmPushActionAckMessage.setTopic(metaInfo.getTopic());
        }
        xmPushActionAckMessage.setDeviceStatus(XmPushThriftSerializeUtils.getDeviceStatus(this.sAppContext, xmPushActionContainer));
        PushServiceClient.getInstance(this.sAppContext).sendMessage(xmPushActionAckMessage, ActionType.AckMessage, false, metaInfo);
    }

    private void detectProcessStatus(XmPushActionNotification xmPushActionNotification) {
        Map<String, String> extra = xmPushActionNotification.getExtra();
        if (extra == null) {
            MyLog.w("detect failed because null");
            return;
        }
        String str = (String) NotificationUtils.getValue(extra, "pkgList", null);
        if (TextUtils.isEmpty(str)) {
            MyLog.w("detect failed because empty");
            return;
        }
        Map<String, String> mapDivideAppStatus = AppInfoUtils.divideAppStatus(this.sAppContext, str);
        if (mapDivideAppStatus == null) {
            MyLog.w("detect failed because get status illegal");
            return;
        }
        String str2 = mapDivideAppStatus.get("alive");
        String str3 = mapDivideAppStatus.get("notAlive");
        if (TextUtils.isEmpty(str2)) {
            MyLog.i("detect failed because no alive process");
            return;
        }
        XmPushActionNotification xmPushActionNotification2 = new XmPushActionNotification();
        xmPushActionNotification2.setId(xmPushActionNotification.getId());
        xmPushActionNotification2.setAppId(xmPushActionNotification.getAppId());
        xmPushActionNotification2.setPackageName(xmPushActionNotification.getPackageName());
        xmPushActionNotification2.setType(NotificationType.DetectAppAliveResult.value);
        HashMap map = new HashMap();
        xmPushActionNotification2.extra = map;
        map.put("alive", str2);
        if (Boolean.parseBoolean((String) NotificationUtils.getValue(extra, "reportNotAliveApp", "false")) && !TextUtils.isEmpty(str3)) {
            xmPushActionNotification2.extra.put("notAlive", str3);
        }
        PushServiceClient.getInstance(this.sAppContext).sendMessage(xmPushActionNotification2, ActionType.Notification, false, null);
    }

    public static PushMessageProcessor getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PushMessageProcessor(context);
        }
        return sInstance;
    }

    public static Intent getNotificationMessageIntent(Context context, String str, Map<String, String> map, int i2) {
        return MIPushNotificationHelper.getNotificationMessageIntent(context, str, map, i2);
    }

    private static boolean isDuplicateMessage(Context context, String str) {
        synchronized (lock) {
            try {
                AppInfoHolder.getInstance(context);
                SharedPreferences sharedPreferences = AppInfoHolder.getSharedPreferences(context);
                if (mCachedMsgIds == null) {
                    String[] strArrSplit = sharedPreferences.getString(PREF_KEY_CACHED_MSGIDS, "").split(",");
                    mCachedMsgIds = new LinkedList();
                    for (String str2 : strArrSplit) {
                        mCachedMsgIds.add(str2);
                    }
                }
                if (mCachedMsgIds.contains(str)) {
                    return true;
                }
                mCachedMsgIds.add(str);
                if (mCachedMsgIds.size() > 25) {
                    mCachedMsgIds.poll();
                }
                String strJoin = XMStringUtils.join(mCachedMsgIds, ",");
                SharedPreferences.Editor editorEdit = sharedPreferences.edit();
                editorEdit.putString(PREF_KEY_CACHED_MSGIDS, strJoin);
                SharedPrefsCompat.apply(editorEdit);
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private boolean isHybridMsg(XmPushActionContainer xmPushActionContainer) {
        Map<String, String> extra = xmPushActionContainer.getMetaInfo() == null ? null : xmPushActionContainer.getMetaInfo().getExtra();
        if (extra == null) {
            return false;
        }
        String str = extra.get(Constants.EXTRA_KEY_PUSH_SERVER_ACTION);
        return TextUtils.equals(str, Constants.EXTRA_VALUE_HYBRID_MESSAGE) || TextUtils.equals(str, Constants.EXTRA_VALUE_PLATFORM_MESSAGE);
    }

    private PushMessageHandler.PushMessageInterface processMessage(XmPushActionContainer xmPushActionContainer, byte[] bArr) {
        String str = null;
        try {
            TBase responseMessageBodyFromContainer = PushContainerHelper.getResponseMessageBodyFromContainer(this.sAppContext, xmPushActionContainer);
            if (responseMessageBodyFromContainer == null) {
                MyLog.e("message arrived: receiving an un-recognized message. " + xmPushActionContainer.action);
                return null;
            }
            ActionType action = xmPushActionContainer.getAction();
            MyLog.w("message arrived: processing an arrived message, action=" + action);
            if (AnonymousClass1.$SwitchMap$com$xiaomi$xmpush$thrift$ActionType[action.ordinal()] != 1) {
                return null;
            }
            if (!xmPushActionContainer.isEncryptAction()) {
                MyLog.e("message arrived: receiving an un-encrypt message(SendMessage).");
                return null;
            }
            XmPushActionSendMessage xmPushActionSendMessage = (XmPushActionSendMessage) responseMessageBodyFromContainer;
            PushMessage message = xmPushActionSendMessage.getMessage();
            if (message == null) {
                MyLog.e("message arrived: receive an empty message without push content, drop it");
                return null;
            }
            PushMetaInfo pushMetaInfo = xmPushActionContainer.metaInfo;
            if (pushMetaInfo != null && pushMetaInfo.getExtra() != null) {
                str = xmPushActionContainer.metaInfo.extra.get("jobkey");
            }
            MiPushMessage miPushMessageGenerateMessage = PushMessageHelper.generateMessage(xmPushActionSendMessage, xmPushActionContainer.getMetaInfo(), false);
            miPushMessageGenerateMessage.setArrivedMessage(true);
            MyLog.w("message arrived: receive a message, msgid=" + message.getId() + ", jobkey=" + str);
            return miPushMessageGenerateMessage;
        } catch (DecryptException e2) {
            MyLog.e(e2);
            MyLog.e("message arrived: receive a message but decrypt failed. report when click.");
            return null;
        } catch (TException e3) {
            MyLog.e(e3);
            MyLog.e("message arrived: receive a message which action string is not valid. is the reg expired?");
            return null;
        }
    }

    private void processSendTokenAckNotification(XmPushActionAckNotification xmPushActionAckNotification) {
        MyLog.v("ASSEMBLE_PUSH : " + xmPushActionAckNotification.toString());
        String id = xmPushActionAckNotification.getId();
        Map<String, String> extra = xmPushActionAckNotification.getExtra();
        if (extra != null) {
            String str = extra.get(Constants.ASSEMBLE_PUSH_REG_INFO);
            if (TextUtils.isEmpty(str)) {
                return;
            }
            if (str.contains("brand:" + PhoneBrand.FCM.name())) {
                MyLog.w("ASSEMBLE_PUSH : receive fcm token sync ack");
                Context context = this.sAppContext;
                AssemblePush assemblePush = AssemblePush.ASSEMBLE_PUSH_FCM;
                AssemblePushHelper.saveAssemblePushTokenAfterAck(context, assemblePush, str);
                processSingleTokenACK(id, xmPushActionAckNotification.errorCode, assemblePush);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("brand:");
            PhoneBrand phoneBrand = PhoneBrand.HUAWEI;
            sb.append(phoneBrand.name());
            if (!str.contains(sb.toString())) {
                if (!str.contains("channel:" + phoneBrand.name())) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("brand:");
                    PhoneBrand phoneBrand2 = PhoneBrand.OPPO;
                    sb2.append(phoneBrand2.name());
                    if (!str.contains(sb2.toString())) {
                        if (!str.contains("channel:" + phoneBrand2.name())) {
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("brand:");
                            PhoneBrand phoneBrand3 = PhoneBrand.VIVO;
                            sb3.append(phoneBrand3.name());
                            if (!str.contains(sb3.toString())) {
                                if (!str.contains("channel:" + phoneBrand3.name())) {
                                    return;
                                }
                            }
                            MyLog.w("ASSEMBLE_PUSH : receive FTOS token sync ack");
                            Context context2 = this.sAppContext;
                            AssemblePush assemblePush2 = AssemblePush.ASSEMBLE_PUSH_FTOS;
                            AssemblePushHelper.saveAssemblePushTokenAfterAck(context2, assemblePush2, str);
                            processSingleTokenACK(id, xmPushActionAckNotification.errorCode, assemblePush2);
                            return;
                        }
                    }
                    MyLog.w("ASSEMBLE_PUSH : receive COS token sync ack");
                    Context context3 = this.sAppContext;
                    AssemblePush assemblePush3 = AssemblePush.ASSEMBLE_PUSH_COS;
                    AssemblePushHelper.saveAssemblePushTokenAfterAck(context3, assemblePush3, str);
                    processSingleTokenACK(id, xmPushActionAckNotification.errorCode, assemblePush3);
                    return;
                }
            }
            MyLog.w("ASSEMBLE_PUSH : receive hw token sync ack");
            Context context4 = this.sAppContext;
            AssemblePush assemblePush4 = AssemblePush.ASSEMBLE_PUSH_HUAWEI;
            AssemblePushHelper.saveAssemblePushTokenAfterAck(context4, assemblePush4, str);
            processSingleTokenACK(id, xmPushActionAckNotification.errorCode, assemblePush4);
        }
    }

    private void processSingleTokenACK(String str, long j2, AssemblePush assemblePush) {
        RetryType retryType = AssemblePushInfoHelper.getRetryType(assemblePush);
        if (retryType == null) {
            return;
        }
        if (j2 == 0) {
            synchronized (OperatePushHelper.class) {
                try {
                    if (OperatePushHelper.getInstance(this.sAppContext).isMessageOperating(str)) {
                        OperatePushHelper.getInstance(this.sAppContext).removeOperateMessage(str);
                        if (OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(this.sAppContext).getSyncStatus(retryType))) {
                            OperatePushHelper.getInstance(this.sAppContext).putSyncStatus(retryType, OperatePushHelper.SYNCED);
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            return;
        }
        if (!OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(this.sAppContext).getSyncStatus(retryType))) {
            OperatePushHelper.getInstance(this.sAppContext).removeOperateMessage(str);
            return;
        }
        synchronized (OperatePushHelper.class) {
            try {
                if (OperatePushHelper.getInstance(this.sAppContext).isMessageOperating(str)) {
                    if (OperatePushHelper.getInstance(this.sAppContext).getRetryCount(str) < 10) {
                        OperatePushHelper.getInstance(this.sAppContext).increaseRetryCount(str);
                        PushServiceClient.getInstance(this.sAppContext).sendAssemblePushTokenCommon(str, retryType, assemblePush, "retry");
                    } else {
                        OperatePushHelper.getInstance(this.sAppContext).removeOperateMessage(str);
                    }
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    private void processStatDataACK(XmPushActionAckNotification xmPushActionAckNotification) {
        String id = xmPushActionAckNotification.getId();
        MyLog.i("receive ack " + id);
        Map<String, String> extra = xmPushActionAckNotification.getExtra();
        if (extra != null) {
            String str = extra.get(UploadDataHelper.REAL_SOURCE);
            if (TextUtils.isEmpty(str)) {
                return;
            }
            MyLog.i("receive ack : messageId = " + id + "  realSource = " + str);
            PushStatClientManager.getInstance(this.sAppContext).onResult(id, str, Boolean.valueOf(xmPushActionAckNotification.errorCode == 0));
        }
    }

    public static void removeCachedDupKey(Context context, String str) {
        synchronized (lock) {
            mCachedMsgIds.remove(str);
            AppInfoHolder.getInstance(context);
            SharedPreferences sharedPreferences = AppInfoHolder.getSharedPreferences(context);
            String strJoin = XMStringUtils.join(mCachedMsgIds, ",");
            SharedPreferences.Editor editorEdit = sharedPreferences.edit();
            editorEdit.putString(PREF_KEY_CACHED_MSGIDS, strJoin);
            SharedPrefsCompat.apply(editorEdit);
        }
    }

    private void reportDecryptFail(XmPushActionContainer xmPushActionContainer) {
        MyLog.w("receive a message but decrypt failed. report now.");
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification(xmPushActionContainer.getMetaInfo().id, false);
        xmPushActionNotification.setType(NotificationType.DecryptMessageFail.value);
        xmPushActionNotification.setAppId(xmPushActionContainer.getAppid());
        xmPushActionNotification.setPackageName(xmPushActionContainer.packageName);
        HashMap map = new HashMap();
        xmPushActionNotification.extra = map;
        map.put("regid", MiPushClient.getRegId(this.sAppContext));
        PushServiceClient.getInstance(this.sAppContext).sendMessage(xmPushActionNotification, ActionType.Notification, false, null);
    }

    private void sendAckNotification(XmPushActionNotification xmPushActionNotification) {
        XmPushActionAckNotification xmPushActionAckNotification = new XmPushActionAckNotification();
        xmPushActionAckNotification.setType(NotificationType.CancelPushMessageACK.value);
        xmPushActionAckNotification.setId(xmPushActionNotification.getId());
        xmPushActionAckNotification.setTarget(xmPushActionNotification.getTarget());
        xmPushActionAckNotification.setAppId(xmPushActionNotification.getAppId());
        xmPushActionAckNotification.setPackageName(xmPushActionNotification.getPackageName());
        xmPushActionAckNotification.setErrorCode(0L);
        xmPushActionAckNotification.setReason("success clear push message.");
        PushServiceClient.getInstance(this.sAppContext).sendMessage(xmPushActionAckNotification, ActionType.Notification, false, true, null, false, this.sAppContext.getPackageName(), AppInfoHolder.getInstance(this.sAppContext).getAppID(), false);
    }

    private void tryToReinitialize() {
        SharedPreferences sharedPreferences = this.sAppContext.getSharedPreferences("mipush_extra", 0);
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (Math.abs(jCurrentTimeMillis - sharedPreferences.getLong(Constants.SP_KEY_LAST_REINITIALIZE, 0L)) > 1800000) {
            MiPushClient.reInitialize(this.sAppContext, RegistrationReason.PackageUnregistered);
            sharedPreferences.edit().putLong(Constants.SP_KEY_LAST_REINITIALIZE, jCurrentTimeMillis).commit();
        }
    }

    public List<String> getTimeForTimeZone(TimeZone timeZone, TimeZone timeZone2, List<String> list) {
        if (timeZone.equals(timeZone2)) {
            return list;
        }
        long rawOffset = ((timeZone.getRawOffset() - timeZone2.getRawOffset()) / 1000) / 60;
        long j2 = Long.parseLong(list.get(0).split(":")[0]);
        long j3 = ((((j2 * 60) + Long.parseLong(list.get(0).split(":")[1])) - rawOffset) + 1440) % 1440;
        long j4 = ((((Long.parseLong(list.get(1).split(":")[0]) * 60) + Long.parseLong(list.get(1).split(":")[1])) - rawOffset) + 1440) % 1440;
        ArrayList arrayList = new ArrayList();
        arrayList.add(String.format("%1$02d:%2$02d", Long.valueOf(j3 / 60), Long.valueOf(j3 % 60)));
        arrayList.add(String.format("%1$02d:%2$02d", Long.valueOf(j4 / 60), Long.valueOf(j4 % 60)));
        return arrayList;
    }

    public PushMessageHandler.PushMessageInterface processIntent(Intent intent) {
        String action = intent.getAction();
        MyLog.w("receive an intent from server, action=" + action);
        String stringExtra = intent.getStringExtra(PushConstants.MESSAGE_RECEIVE_TIME);
        if (stringExtra == null) {
            stringExtra = Long.toString(System.currentTimeMillis());
        }
        String stringExtra2 = intent.getStringExtra("messageId");
        int intExtra = intent.getIntExtra(ReportConstants.EVENT_MESSAGE_TYPE, -1);
        if (PushConstants.MIPUSH_ACTION_NEW_MESSAGE.equals(action)) {
            byte[] byteArrayExtra = intent.getByteArrayExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD);
            boolean booleanExtra = intent.getBooleanExtra(MIPushNotificationHelper.FROM_NOTIFICATION, false);
            if (byteArrayExtra == null) {
                MyLog.e("receiving an empty message, drop");
                PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), intent, "12");
                return null;
            }
            XmPushActionContainer xmPushActionContainer = new XmPushActionContainer();
            try {
                XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionContainer, byteArrayExtra);
                AppInfoHolder appInfoHolder = AppInfoHolder.getInstance(this.sAppContext);
                PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
                ActionType action2 = xmPushActionContainer.getAction();
                ActionType actionType = ActionType.SendMessage;
                if (action2 == actionType && metaInfo != null && !appInfoHolder.isPaused() && !booleanExtra) {
                    metaInfo.putToExtra(PushConstants.MESSAGE_RECEIVE_TIME, stringExtra);
                    metaInfo.putToExtra(PushConstants.MESSAGE_ACK_TIME, Long.toString(System.currentTimeMillis()));
                    if (isHybridMsg(xmPushActionContainer)) {
                        MyLog.i("this is a mina's message, ack later");
                        metaInfo.putToExtra(Constants.EXTRA_KEY_HYBRID_MESSAGE_TS, String.valueOf(metaInfo.getMessageTs()));
                        metaInfo.putToExtra(Constants.EXTRA_KEY_HYBRID_DEVICE_STATUS, String.valueOf((int) XmPushThriftSerializeUtils.getDeviceStatus(this.sAppContext, xmPushActionContainer)));
                    } else {
                        ackMessage(xmPushActionContainer);
                    }
                }
                if (xmPushActionContainer.getAction() == actionType && !xmPushActionContainer.isEncryptAction()) {
                    if (MIPushNotificationHelper.isBusinessMessage(xmPushActionContainer)) {
                        MyLog.w(String.format("drop an un-encrypted wake-up messages. %1$s, %2$s", xmPushActionContainer.getPackageName(), metaInfo != null ? metaInfo.getId() : ""));
                        PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), intent, String.format("13: %1$s", xmPushActionContainer.getPackageName()));
                    } else {
                        MyLog.w(String.format("drop an un-encrypted messages. %1$s, %2$s", xmPushActionContainer.getPackageName(), metaInfo != null ? metaInfo.getId() : ""));
                        PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), intent, String.format("14: %1$s", xmPushActionContainer.getPackageName()));
                    }
                    ClickToStartActivityReporter.failedWithMessageNotEncrypt(this.sAppContext, xmPushActionContainer, booleanExtra);
                    return null;
                }
                if (xmPushActionContainer.getAction() == actionType && xmPushActionContainer.isEncryptAction() && MIPushNotificationHelper.isBusinessMessage(xmPushActionContainer) && (!booleanExtra || metaInfo == null || metaInfo.getExtra() == null || !metaInfo.getExtra().containsKey(PushConstants.EXTRA_PARAM_NOTIFY_EFFECT))) {
                    MyLog.w(String.format("drop a wake-up messages which not has 'notify_effect' attr. %1$s, %2$s", xmPushActionContainer.getPackageName(), metaInfo != null ? metaInfo.getId() : ""));
                    PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), intent, String.format("25: %1$s", xmPushActionContainer.getPackageName()));
                    ClickToStartActivityReporter.businessWithoutEffect(this.sAppContext, xmPushActionContainer, booleanExtra);
                    return null;
                }
                if (appInfoHolder.isRegistrationValid() || xmPushActionContainer.action == ActionType.Registration) {
                    if (!appInfoHolder.isRegistrationValid() || !appInfoHolder.invalidated()) {
                        return processMessage(xmPushActionContainer, booleanExtra, byteArrayExtra, stringExtra2, intExtra, intent);
                    }
                    if (xmPushActionContainer.action != ActionType.UnRegistration) {
                        ClickToStartActivityReporter.registerInfoIncorrect(this.sAppContext, xmPushActionContainer, booleanExtra);
                        MiPushClient.unregisterPush(this.sAppContext);
                    } else if (xmPushActionContainer.isEncryptAction()) {
                        appInfoHolder.clear();
                        MiPushClient.clearExtras(this.sAppContext);
                        PushMessageHandler.removeAllPushCallbackClass();
                    } else {
                        MyLog.e("receiving an un-encrypt unregistration message");
                    }
                } else {
                    if (MIPushNotificationHelper.isBusinessMessage(xmPushActionContainer)) {
                        return processMessage(xmPushActionContainer, booleanExtra, byteArrayExtra, stringExtra2, intExtra, intent);
                    }
                    ClickToStartActivityReporter.registerInfoIncorrect(this.sAppContext, xmPushActionContainer, booleanExtra);
                    boolean zHasRegistered = appInfoHolder.hasRegistered();
                    MyLog.e("receive message without registration. need re-register!registered?" + zHasRegistered);
                    PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), intent, ReportConstants.ERROR_UN_REGISTER_NOT_AWAKE_MSG);
                    if (zHasRegistered) {
                        tryToReinitialize();
                    }
                }
            } catch (TException e2) {
                PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), intent, "16");
                MyLog.e(e2);
            } catch (Exception e3) {
                PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), intent, "17");
                MyLog.e(e3);
            }
        } else {
            if (PushConstants.MIPUSH_ACTION_ERROR.equals(action)) {
                MiPushCommandMessage miPushCommandMessage = new MiPushCommandMessage();
                XmPushActionContainer xmPushActionContainer2 = new XmPushActionContainer();
                try {
                    byte[] byteArrayExtra2 = intent.getByteArrayExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD);
                    if (byteArrayExtra2 != null) {
                        XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionContainer2, byteArrayExtra2);
                    }
                } catch (TException unused) {
                }
                miPushCommandMessage.setCommand(String.valueOf(xmPushActionContainer2.getAction()));
                miPushCommandMessage.setResultCode(intent.getIntExtra(PushConstants.MIPUSH_EXTRA_ERROR_CODE, 0));
                miPushCommandMessage.setReason(intent.getStringExtra(PushConstants.MIPUSH_EXTRA_ERROR_MSG));
                MyLog.e("receive a error message. code = " + intent.getIntExtra(PushConstants.MIPUSH_EXTRA_ERROR_CODE, 0) + ", msg= " + intent.getStringExtra(PushConstants.MIPUSH_EXTRA_ERROR_MSG));
                return miPushCommandMessage;
            }
            if (PushConstants.MIPUSH_ACTION_MESSAGE_ARRIVED.equals(action)) {
                byte[] byteArrayExtra3 = intent.getByteArrayExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD);
                if (byteArrayExtra3 == null) {
                    MyLog.e("message arrived: receiving an empty message, drop");
                    return null;
                }
                XmPushActionContainer xmPushActionContainer3 = new XmPushActionContainer();
                try {
                    XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionContainer3, byteArrayExtra3);
                    AppInfoHolder appInfoHolder2 = AppInfoHolder.getInstance(this.sAppContext);
                    if (MIPushNotificationHelper.isBusinessMessage(xmPushActionContainer3)) {
                        MyLog.e("message arrived: receive ignore reg message, ignore!");
                    } else if (!appInfoHolder2.isRegistrationValid()) {
                        MyLog.e("message arrived: receive message without registration. need unregister or re-register!");
                    } else {
                        if (!appInfoHolder2.isRegistrationValid() || !appInfoHolder2.invalidated()) {
                            return processMessage(xmPushActionContainer3, byteArrayExtra3);
                        }
                        MyLog.e("message arrived: app info is invalidated");
                    }
                } catch (Exception e4) {
                    MyLog.e("fail to deal with arrived message. " + e4);
                }
            }
        }
        return null;
    }

    private void ackMessage(XmPushActionSendMessage xmPushActionSendMessage, XmPushActionContainer xmPushActionContainer) {
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        if (metaInfo != null) {
            metaInfo = RedundantUploadInfoHelper.cleanRedundantInfoForAck(metaInfo.m25deepCopy());
        }
        XmPushActionAckMessage xmPushActionAckMessage = new XmPushActionAckMessage();
        xmPushActionAckMessage.setAppId(xmPushActionSendMessage.getAppId());
        xmPushActionAckMessage.setId(xmPushActionSendMessage.getId());
        xmPushActionAckMessage.setMessageTs(xmPushActionSendMessage.getMessage().getCreateAt());
        if (!TextUtils.isEmpty(xmPushActionSendMessage.getTopic())) {
            xmPushActionAckMessage.setTopic(xmPushActionSendMessage.getTopic());
        }
        if (!TextUtils.isEmpty(xmPushActionSendMessage.getAliasName())) {
            xmPushActionAckMessage.setAliasName(xmPushActionSendMessage.getAliasName());
        }
        xmPushActionAckMessage.setDeviceStatus(XmPushThriftSerializeUtils.getDeviceStatus(this.sAppContext, xmPushActionContainer));
        PushServiceClient.getInstance(this.sAppContext).sendMessage(xmPushActionAckMessage, ActionType.AckMessage, metaInfo);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private PushMessageHandler.PushMessageInterface processMessage(XmPushActionContainer xmPushActionContainer, boolean z2, byte[] bArr, String str, int i2, Intent intent) {
        String id;
        String str2;
        MiPushMessage miPushMessage;
        PushMetaInfo pushMetaInfo;
        List<String> timeForTimeZone;
        ArrayList arrayList = null;
        try {
            TBase responseMessageBodyFromContainer = PushContainerHelper.getResponseMessageBodyFromContainer(this.sAppContext, xmPushActionContainer);
            if (responseMessageBodyFromContainer == null) {
                MyLog.e("receiving an un-recognized message. " + xmPushActionContainer.action);
                PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, "18");
                ClickToStartActivityReporter.dencryptMessageFailed(this.sAppContext, xmPushActionContainer, z2);
                return null;
            }
            ActionType action = xmPushActionContainer.getAction();
            MyLog.w("processing a message, action=", action, ", hasNotified=", Boolean.valueOf(z2));
            switch (AnonymousClass1.$SwitchMap$com$xiaomi$xmpush$thrift$ActionType[action.ordinal()]) {
                case 1:
                    if (!xmPushActionContainer.isEncryptAction()) {
                        MyLog.e("receiving an un-encrypt message(SendMessage).");
                        return null;
                    }
                    if (AppInfoHolder.getInstance(this.sAppContext).isPaused() && !z2) {
                        MyLog.w("receive a message in pause state. drop it");
                        PushClientReportManager.getInstance(this.sAppContext).reportEvent4NeedDrop(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, "12");
                        return null;
                    }
                    XmPushActionSendMessage xmPushActionSendMessage = (XmPushActionSendMessage) responseMessageBodyFromContainer;
                    PushMessage message = xmPushActionSendMessage.getMessage();
                    if (message == null) {
                        MyLog.e("receive an empty message without push content, drop it");
                        PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, "22");
                        ClickToStartActivityReporter.messageContentEmpty(this.sAppContext, xmPushActionContainer, z2);
                        return null;
                    }
                    int intExtra = intent.getIntExtra("notification_click_button", 0);
                    if (z2) {
                        if (MIPushNotificationHelper.isBusinessMessage(xmPushActionContainer)) {
                            MiPushClient.reportIgnoreRegMessageClicked(this.sAppContext, message.getId(), xmPushActionContainer.getMetaInfo(), xmPushActionContainer.packageName, message.getAppId());
                        } else {
                            if (xmPushActionContainer.getMetaInfo() != null) {
                                pushMetaInfo = new PushMetaInfo(xmPushActionContainer.getMetaInfo());
                            } else {
                                pushMetaInfo = new PushMetaInfo();
                            }
                            if (pushMetaInfo.getExtra() == null) {
                                pushMetaInfo.setExtra(new HashMap());
                            }
                            pushMetaInfo.getExtra().put("notification_click_button", String.valueOf(intExtra));
                            MiPushClient.reportMessageClicked(this.sAppContext, message.getId(), pushMetaInfo, message.getAppId());
                        }
                    }
                    if (!z2) {
                        if (!TextUtils.isEmpty(xmPushActionSendMessage.getAliasName()) && MiPushClient.aliasSetTime(this.sAppContext, xmPushActionSendMessage.getAliasName()) < 0) {
                            MiPushClient.addAlias(this.sAppContext, xmPushActionSendMessage.getAliasName());
                        } else if (!TextUtils.isEmpty(xmPushActionSendMessage.getTopic()) && MiPushClient.topicSubscribedTime(this.sAppContext, xmPushActionSendMessage.getTopic()) < 0) {
                            MiPushClient.addTopic(this.sAppContext, xmPushActionSendMessage.getTopic());
                        }
                    }
                    PushMetaInfo pushMetaInfo2 = xmPushActionContainer.metaInfo;
                    if (pushMetaInfo2 == null || pushMetaInfo2.getExtra() == null) {
                        id = null;
                        str2 = null;
                    } else {
                        id = xmPushActionContainer.metaInfo.extra.get("jobkey");
                        str2 = id;
                    }
                    if (TextUtils.isEmpty(id)) {
                        id = message.getId();
                    }
                    if (!z2 && isDuplicateMessage(this.sAppContext, id)) {
                        MyLog.w("drop a duplicate message, key=" + id);
                        PushClientReportManager.getInstance(this.sAppContext).reportEvent4DUPMD(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, "2:" + id);
                        miPushMessage = null;
                    } else {
                        MiPushMessage miPushMessageGenerateMessage = PushMessageHelper.generateMessage(xmPushActionSendMessage, xmPushActionContainer.getMetaInfo(), z2);
                        if (miPushMessageGenerateMessage.getPassThrough() == 0 && !z2 && MIPushNotificationHelper.isNotifyForeground(miPushMessageGenerateMessage.getExtra())) {
                            MIPushNotificationHelper.notifyPushMessage(this.sAppContext, xmPushActionContainer, bArr);
                            return null;
                        }
                        String notifyEffectByButton = MIPushNotificationHelper.getNotifyEffectByButton(miPushMessageGenerateMessage.getExtra(), intExtra);
                        MyLog.w("receive a message, msgid=", message.getId(), ", jobkey=", id, ", btn=", Integer.valueOf(intExtra), ", typeId=", notifyEffectByButton, ", hasNotified=", Boolean.valueOf(z2));
                        if (z2 && miPushMessageGenerateMessage.getExtra() != null && !TextUtils.isEmpty(notifyEffectByButton)) {
                            Map<String, String> extra = miPushMessageGenerateMessage.getExtra();
                            if (intExtra != 0 && xmPushActionContainer.getMetaInfo() != null) {
                                PushServiceClient.getInstance(this.sAppContext).clearNotification(xmPushActionContainer.getMetaInfo().getNotifyId(), intExtra);
                            }
                            if (MIPushNotificationHelper.isBusinessMessage(xmPushActionContainer)) {
                                Intent notificationMessageIntent = getNotificationMessageIntent(this.sAppContext, xmPushActionContainer.packageName, extra, intExtra);
                                notificationMessageIntent.putExtra(ReportConstants.EVENT_MESSAGE_TYPE, i2);
                                notificationMessageIntent.putExtra("messageId", str);
                                notificationMessageIntent.putExtra("jobkey", str2);
                                String payload = message.getPayload();
                                if (!TextUtils.isEmpty(payload)) {
                                    notificationMessageIntent.putExtra("payload", payload);
                                }
                                this.sAppContext.startActivity(notificationMessageIntent);
                                ClickToStartActivityReporter.startActivity(this.sAppContext, xmPushActionContainer);
                                PushClientReportManager.getInstance(this.sAppContext).reportEvent(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, ReportConstants.AWAKE_TYPE_PROCESS_AFTER_CLICK, notifyEffectByButton);
                                MyLog.w(TAG, "start business activity succ");
                            } else {
                                Context context = this.sAppContext;
                                Intent notificationMessageIntent2 = getNotificationMessageIntent(context, context.getPackageName(), extra, intExtra);
                                if (notificationMessageIntent2 != null) {
                                    if (!notifyEffectByButton.equals(PushConstants.NOTIFICATION_CLICK_WEB_PAGE)) {
                                        notificationMessageIntent2.putExtra(PushMessageHelper.KEY_MESSAGE, miPushMessageGenerateMessage);
                                        notificationMessageIntent2.putExtra(ReportConstants.EVENT_MESSAGE_TYPE, i2);
                                        notificationMessageIntent2.putExtra("messageId", str);
                                        notificationMessageIntent2.putExtra("jobkey", str2);
                                    }
                                    this.sAppContext.startActivity(notificationMessageIntent2);
                                    ClickToStartActivityReporter.startActivity(this.sAppContext, xmPushActionContainer);
                                    MyLog.w(TAG, "start activity succ");
                                    PushClientReportManager.getInstance(this.sAppContext).reportEvent(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, 1006, notifyEffectByButton);
                                    if (notifyEffectByButton.equals(PushConstants.NOTIFICATION_CLICK_WEB_PAGE)) {
                                        PushClientReportManager.getInstance(this.sAppContext).reportEvent4NeedDrop(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, "13");
                                    }
                                } else {
                                    MyLog.persist(TAG, "missing target intent for message: " + message.getId() + ", typeId=" + notifyEffectByButton);
                                }
                            }
                            MyLog.w(TAG, "pre-def msg process done.");
                            return null;
                        }
                        miPushMessage = miPushMessageGenerateMessage;
                    }
                    if (xmPushActionContainer.getMetaInfo() == null && !z2) {
                        ackMessage(xmPushActionSendMessage, xmPushActionContainer);
                    }
                    return miPushMessage;
                case 2:
                    XmPushActionRegistrationResult xmPushActionRegistrationResult = (XmPushActionRegistrationResult) responseMessageBodyFromContainer;
                    String str3 = AppInfoHolder.getInstance(this.sAppContext).appRegRequestId;
                    if (!TextUtils.isEmpty(str3) && TextUtils.equals(str3, xmPushActionRegistrationResult.getId())) {
                        long lastRegRequestTime = PushServiceClient.getInstance(this.sAppContext).getLastRegRequestTime();
                        if (lastRegRequestTime > 0 && SystemClock.elapsedRealtime() - lastRegRequestTime > 900000) {
                            MyLog.w("The received registration result has expired.");
                            PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, ReportConstants.ERROR_RECEIVED_REGISTRATION_RESULT_EXPIRED);
                            return null;
                        }
                        AppInfoHolder.getInstance(this.sAppContext).appRegRequestId = null;
                        if (xmPushActionRegistrationResult.errorCode == 0) {
                            AppInfoHolder.getInstance(this.sAppContext).putRegIDAndSecret(xmPushActionRegistrationResult.regId, xmPushActionRegistrationResult.regSecret, xmPushActionRegistrationResult.region);
                            FCMPushHelper.persistIfXmsfSupDecrypt(this.sAppContext);
                            PushClientReportManager.getInstance(this.sAppContext).reportEvent(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, ReportConstants.REGISTER_TYPE_APP_SUCCESS, "1");
                        } else {
                            PushClientReportManager.getInstance(this.sAppContext).reportEvent(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, ReportConstants.REGISTER_TYPE_APP_SUCCESS, "2");
                        }
                        if (!TextUtils.isEmpty(xmPushActionRegistrationResult.regId)) {
                            arrayList = new ArrayList();
                            arrayList.add(xmPushActionRegistrationResult.regId);
                        }
                        MiPushCommandMessage miPushCommandMessageGenerateCommandMessage = PushMessageHelper.generateCommandMessage(Command.COMMAND_REGISTER.value, arrayList, xmPushActionRegistrationResult.errorCode, xmPushActionRegistrationResult.reason, null, xmPushActionRegistrationResult.getAutoMarkPkgs());
                        PushServiceClient.getInstance(this.sAppContext).processPendRequest();
                        return miPushCommandMessageGenerateCommandMessage;
                    }
                    MyLog.w("bad Registration result:");
                    PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, ReportConstants.ERROR_BAD_REGISTRATION_RESULT);
                    return null;
                case 3:
                    if (!xmPushActionContainer.isEncryptAction()) {
                        MyLog.e("receiving an un-encrypt message(UnRegistration).");
                        return null;
                    }
                    if (((XmPushActionUnRegistrationResult) responseMessageBodyFromContainer).errorCode == 0) {
                        AppInfoHolder.getInstance(this.sAppContext).clear();
                        MiPushClient.clearExtras(this.sAppContext);
                    }
                    PushMessageHandler.removeAllPushCallbackClass();
                    return null;
                case 4:
                    XmPushActionSubscriptionResult xmPushActionSubscriptionResult = (XmPushActionSubscriptionResult) responseMessageBodyFromContainer;
                    if (xmPushActionSubscriptionResult.errorCode == 0) {
                        MiPushClient.addTopic(this.sAppContext, xmPushActionSubscriptionResult.getTopic());
                    }
                    if (!TextUtils.isEmpty(xmPushActionSubscriptionResult.getTopic())) {
                        arrayList = new ArrayList();
                        arrayList.add(xmPushActionSubscriptionResult.getTopic());
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("resp-cmd:");
                    Command command = Command.COMMAND_SUBSCRIBE_TOPIC;
                    sb.append(command);
                    sb.append(", ");
                    sb.append(xmPushActionSubscriptionResult.getId());
                    MyLog.persist(sb.toString());
                    return PushMessageHelper.generateCommandMessage(command.value, arrayList, xmPushActionSubscriptionResult.errorCode, xmPushActionSubscriptionResult.reason, xmPushActionSubscriptionResult.getCategory(), null);
                case 5:
                    XmPushActionUnSubscriptionResult xmPushActionUnSubscriptionResult = (XmPushActionUnSubscriptionResult) responseMessageBodyFromContainer;
                    if (xmPushActionUnSubscriptionResult.errorCode == 0) {
                        MiPushClient.removeTopic(this.sAppContext, xmPushActionUnSubscriptionResult.getTopic());
                    }
                    if (!TextUtils.isEmpty(xmPushActionUnSubscriptionResult.getTopic())) {
                        arrayList = new ArrayList();
                        arrayList.add(xmPushActionUnSubscriptionResult.getTopic());
                    }
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("resp-cmd:");
                    Command command2 = Command.COMMAND_UNSUBSCRIBE_TOPIC;
                    sb2.append(command2);
                    sb2.append(", ");
                    sb2.append(xmPushActionUnSubscriptionResult.getId());
                    MyLog.persist(sb2.toString());
                    return PushMessageHelper.generateCommandMessage(command2.value, arrayList, xmPushActionUnSubscriptionResult.errorCode, xmPushActionUnSubscriptionResult.reason, xmPushActionUnSubscriptionResult.getCategory(), null);
                case 6:
                    PerfMessageHelper.collectPerfData(this.sAppContext.getPackageName(), this.sAppContext, responseMessageBodyFromContainer, ActionType.Command, bArr.length);
                    XmPushActionCommandResult xmPushActionCommandResult = (XmPushActionCommandResult) responseMessageBodyFromContainer;
                    String cmdName = xmPushActionCommandResult.getCmdName();
                    List<String> cmdArgs = xmPushActionCommandResult.getCmdArgs();
                    if (xmPushActionCommandResult.errorCode != 0) {
                        timeForTimeZone = cmdArgs;
                    } else if (TextUtils.equals(cmdName, Command.COMMAND_SET_ACCEPT_TIME.value) && cmdArgs != null && cmdArgs.size() > 1) {
                        MiPushClient.addAcceptTime(this.sAppContext, cmdArgs.get(0), cmdArgs.get(1));
                        if ("00:00".equals(cmdArgs.get(0)) && "00:00".equals(cmdArgs.get(1))) {
                            AppInfoHolder.getInstance(this.sAppContext).setPaused(true);
                        } else {
                            AppInfoHolder.getInstance(this.sAppContext).setPaused(false);
                        }
                        timeForTimeZone = getTimeForTimeZone(TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault(), cmdArgs);
                    } else {
                        if (TextUtils.equals(cmdName, Command.COMMAND_SET_ALIAS.value) && cmdArgs != null && cmdArgs.size() > 0) {
                            MiPushClient.addAlias(this.sAppContext, cmdArgs.get(0));
                        } else if (TextUtils.equals(cmdName, Command.COMMAND_UNSET_ALIAS.value) && cmdArgs != null && cmdArgs.size() > 0) {
                            MiPushClient.removeAlias(this.sAppContext, cmdArgs.get(0));
                        } else if (TextUtils.equals(cmdName, Command.COMMAND_SET_ACCOUNT.value) && cmdArgs != null && cmdArgs.size() > 0) {
                            MiPushClient.addAccount(this.sAppContext, cmdArgs.get(0));
                        } else if (TextUtils.equals(cmdName, Command.COMMAND_UNSET_ACCOUNT.value) && cmdArgs != null && cmdArgs.size() > 0) {
                            MiPushClient.removeAccount(this.sAppContext, cmdArgs.get(0));
                        } else if (TextUtils.equals(cmdName, Command.COMMAND_CHK_VDEVID.value)) {
                            return null;
                        }
                        timeForTimeZone = cmdArgs;
                    }
                    MyLog.persist("resp-cmd:" + cmdName + ", " + xmPushActionCommandResult.getId());
                    return PushMessageHelper.generateCommandMessage(cmdName, timeForTimeZone, xmPushActionCommandResult.errorCode, xmPushActionCommandResult.reason, xmPushActionCommandResult.getCategory(), null);
                case 7:
                    PerfMessageHelper.collectPerfData(this.sAppContext.getPackageName(), this.sAppContext, responseMessageBodyFromContainer, ActionType.Notification, bArr.length);
                    if (responseMessageBodyFromContainer instanceof XmPushActionAckNotification) {
                        XmPushActionAckNotification xmPushActionAckNotification = (XmPushActionAckNotification) responseMessageBodyFromContainer;
                        String id2 = xmPushActionAckNotification.getId();
                        MyLog.persist("resp-type:" + xmPushActionAckNotification.getType() + ", code:" + xmPushActionAckNotification.errorCode + ", " + id2);
                        if (NotificationType.DisablePushMessage.value.equalsIgnoreCase(xmPushActionAckNotification.type)) {
                            if (xmPushActionAckNotification.errorCode == 0) {
                                synchronized (OperatePushHelper.class) {
                                    try {
                                        if (OperatePushHelper.getInstance(this.sAppContext).isMessageOperating(id2)) {
                                            OperatePushHelper.getInstance(this.sAppContext).removeOperateMessage(id2);
                                            OperatePushHelper operatePushHelper = OperatePushHelper.getInstance(this.sAppContext);
                                            RetryType retryType = RetryType.DISABLE_PUSH;
                                            if (OperatePushHelper.SYNCING.equals(operatePushHelper.getSyncStatus(retryType))) {
                                                OperatePushHelper.getInstance(this.sAppContext).putSyncStatus(retryType, OperatePushHelper.SYNCED);
                                                MiPushClient.clearNotification(this.sAppContext);
                                                MiPushClient.clearLocalNotificationType(this.sAppContext);
                                                PushMessageHandler.removeAllPushCallbackClass();
                                                PushServiceClient.getInstance(this.sAppContext).closePush();
                                            }
                                        }
                                    } catch (Throwable th) {
                                        throw th;
                                    }
                                }
                            } else if (OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(this.sAppContext).getSyncStatus(RetryType.DISABLE_PUSH))) {
                                synchronized (OperatePushHelper.class) {
                                    try {
                                        if (OperatePushHelper.getInstance(this.sAppContext).isMessageOperating(id2)) {
                                            if (OperatePushHelper.getInstance(this.sAppContext).getRetryCount(id2) < 10) {
                                                OperatePushHelper.getInstance(this.sAppContext).increaseRetryCount(id2);
                                                PushServiceClient.getInstance(this.sAppContext).sendPushEnableDisableMessage(true, id2);
                                            } else {
                                                OperatePushHelper.getInstance(this.sAppContext).removeOperateMessage(id2);
                                            }
                                        }
                                    } catch (Throwable th2) {
                                        throw th2;
                                    }
                                }
                            } else {
                                OperatePushHelper.getInstance(this.sAppContext).removeOperateMessage(id2);
                            }
                            break;
                        } else if (NotificationType.EnablePushMessage.value.equalsIgnoreCase(xmPushActionAckNotification.type)) {
                            if (xmPushActionAckNotification.errorCode == 0) {
                                synchronized (OperatePushHelper.class) {
                                    try {
                                        if (OperatePushHelper.getInstance(this.sAppContext).isMessageOperating(id2)) {
                                            OperatePushHelper.getInstance(this.sAppContext).removeOperateMessage(id2);
                                            OperatePushHelper operatePushHelper2 = OperatePushHelper.getInstance(this.sAppContext);
                                            RetryType retryType2 = RetryType.ENABLE_PUSH;
                                            if (OperatePushHelper.SYNCING.equals(operatePushHelper2.getSyncStatus(retryType2))) {
                                                OperatePushHelper.getInstance(this.sAppContext).putSyncStatus(retryType2, OperatePushHelper.SYNCED);
                                            }
                                        }
                                    } catch (Throwable th3) {
                                        throw th3;
                                    }
                                }
                            } else if (OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(this.sAppContext).getSyncStatus(RetryType.ENABLE_PUSH))) {
                                synchronized (OperatePushHelper.class) {
                                    try {
                                        if (OperatePushHelper.getInstance(this.sAppContext).isMessageOperating(id2)) {
                                            if (OperatePushHelper.getInstance(this.sAppContext).getRetryCount(id2) < 10) {
                                                OperatePushHelper.getInstance(this.sAppContext).increaseRetryCount(id2);
                                                PushServiceClient.getInstance(this.sAppContext).sendPushEnableDisableMessage(false, id2);
                                            } else {
                                                OperatePushHelper.getInstance(this.sAppContext).removeOperateMessage(id2);
                                            }
                                        }
                                    } catch (Throwable th4) {
                                        throw th4;
                                    }
                                }
                            } else {
                                OperatePushHelper.getInstance(this.sAppContext).removeOperateMessage(id2);
                            }
                            break;
                        } else if (NotificationType.ThirdPartyRegUpdate.value.equalsIgnoreCase(xmPushActionAckNotification.type)) {
                            processSendTokenAckNotification(xmPushActionAckNotification);
                        } else if (NotificationType.UploadTinyData.value.equalsIgnoreCase(xmPushActionAckNotification.type)) {
                            processStatDataACK(xmPushActionAckNotification);
                        }
                    } else if (responseMessageBodyFromContainer instanceof XmPushActionNotification) {
                        XmPushActionNotification xmPushActionNotification = (XmPushActionNotification) responseMessageBodyFromContainer;
                        if ("registration id expired".equalsIgnoreCase(xmPushActionNotification.type)) {
                            List<String> allAlias = MiPushClient.getAllAlias(this.sAppContext);
                            List<String> allTopic = MiPushClient.getAllTopic(this.sAppContext);
                            List<String> allUserAccount = MiPushClient.getAllUserAccount(this.sAppContext);
                            String acceptTime = MiPushClient.getAcceptTime(this.sAppContext);
                            MyLog.persist("resp-type:" + xmPushActionNotification.type + ", " + xmPushActionNotification.getId());
                            MiPushClient.reInitialize(this.sAppContext, RegistrationReason.RegIdExpired);
                            for (String str4 : allAlias) {
                                MiPushClient.removeAlias(this.sAppContext, str4);
                                MiPushClient.setAlias(this.sAppContext, str4, null);
                            }
                            for (String str5 : allTopic) {
                                MiPushClient.removeTopic(this.sAppContext, str5);
                                MiPushClient.subscribe(this.sAppContext, str5, null);
                            }
                            for (String str6 : allUserAccount) {
                                MiPushClient.removeAccount(this.sAppContext, str6);
                                MiPushClient.setUserAccount(this.sAppContext, str6, null);
                            }
                            String[] strArrSplit = acceptTime.split(",");
                            if (strArrSplit.length == 2) {
                                MiPushClient.removeAcceptTime(this.sAppContext);
                                MiPushClient.addAcceptTime(this.sAppContext, strArrSplit[0], strArrSplit[1]);
                            }
                        } else if (NotificationType.ClientInfoUpdateOk.value.equalsIgnoreCase(xmPushActionNotification.type)) {
                            if (xmPushActionNotification.getExtra() != null && xmPushActionNotification.getExtra().containsKey(Constants.EXTRA_KEY_APP_VERSION)) {
                                AppInfoHolder.getInstance(this.sAppContext).updateVersionName(xmPushActionNotification.getExtra().get(Constants.EXTRA_KEY_APP_VERSION));
                            }
                        } else {
                            try {
                                if (NotificationType.NormalClientConfigUpdate.value.equalsIgnoreCase(xmPushActionNotification.type)) {
                                    XmPushActionNormalConfig xmPushActionNormalConfig = new XmPushActionNormalConfig();
                                    XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionNormalConfig, xmPushActionNotification.getBinaryExtra());
                                    OnlineConfigHelper.updateNormalConfigs(OnlineConfig.getInstance(this.sAppContext), xmPushActionNormalConfig);
                                } else if (NotificationType.CustomClientConfigUpdate.value.equalsIgnoreCase(xmPushActionNotification.type)) {
                                    XmPushActionCustomConfig xmPushActionCustomConfig = new XmPushActionCustomConfig();
                                    XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionCustomConfig, xmPushActionNotification.getBinaryExtra());
                                    OnlineConfigHelper.updateCustomConfigs(OnlineConfig.getInstance(this.sAppContext), xmPushActionCustomConfig);
                                } else if (NotificationType.SyncInfoResult.value.equalsIgnoreCase(xmPushActionNotification.type)) {
                                    SyncInfoHelper.saveInfo(this.sAppContext, xmPushActionNotification);
                                } else if (NotificationType.ForceSync.value.equalsIgnoreCase(xmPushActionNotification.type)) {
                                    MyLog.w("receive force sync notification");
                                    SyncInfoHelper.doSyncInfoAsync(this.sAppContext, false);
                                } else if (NotificationType.CancelPushMessage.value.equals(xmPushActionNotification.type)) {
                                    MyLog.persist("resp-type:" + xmPushActionNotification.type + ", " + xmPushActionNotification.getId());
                                    if (xmPushActionNotification.getExtra() != null) {
                                        int i3 = -2;
                                        if (xmPushActionNotification.getExtra().containsKey(PushConstants.PUSH_NOTIFY_ID)) {
                                            String str7 = xmPushActionNotification.getExtra().get(PushConstants.PUSH_NOTIFY_ID);
                                            if (!TextUtils.isEmpty(str7)) {
                                                try {
                                                    i3 = Integer.parseInt(str7);
                                                } catch (NumberFormatException e2) {
                                                    e2.printStackTrace();
                                                }
                                            }
                                        }
                                        if (i3 >= -1) {
                                            MiPushClient.clearNotification(this.sAppContext, i3);
                                        } else {
                                            String str8 = "";
                                            String str9 = "";
                                            if (xmPushActionNotification.getExtra().containsKey(PushConstants.PUSH_TITLE)) {
                                                str8 = xmPushActionNotification.getExtra().get(PushConstants.PUSH_TITLE);
                                            }
                                            if (xmPushActionNotification.getExtra().containsKey(PushConstants.PUSH_DESCRIPTION)) {
                                                str9 = xmPushActionNotification.getExtra().get(PushConstants.PUSH_DESCRIPTION);
                                            }
                                            MiPushClient.clearNotification(this.sAppContext, str8, str9);
                                        }
                                    }
                                    sendAckNotification(xmPushActionNotification);
                                    break;
                                } else if (NotificationType.HybridRegisterResult.value.equals(xmPushActionNotification.type)) {
                                    try {
                                        XmPushActionRegistrationResult xmPushActionRegistrationResult2 = new XmPushActionRegistrationResult();
                                        XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionRegistrationResult2, xmPushActionNotification.getBinaryExtra());
                                        MiPushClient4Hybrid.onReceiveRegisterResult(this.sAppContext, xmPushActionRegistrationResult2);
                                    } catch (TException e3) {
                                        MyLog.e(e3);
                                    }
                                    break;
                                } else if (NotificationType.HybridUnregisterResult.value.equals(xmPushActionNotification.type)) {
                                    try {
                                        XmPushActionUnRegistrationResult xmPushActionUnRegistrationResult = new XmPushActionUnRegistrationResult();
                                        XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionUnRegistrationResult, xmPushActionNotification.getBinaryExtra());
                                        MiPushClient4Hybrid.onReceiveUnregisterResult(this.sAppContext, xmPushActionUnRegistrationResult);
                                    } catch (TException e4) {
                                        MyLog.e(e4);
                                    }
                                    break;
                                } else if (!NotificationType.PushLogUpload.value.equals(xmPushActionNotification.type)) {
                                    if (NotificationType.DetectAppAlive.value.equals(xmPushActionNotification.type)) {
                                        MyLog.i("receive detect msg");
                                        detectProcessStatus(xmPushActionNotification);
                                    } else if (ChannelPushReportAgent.receive(xmPushActionNotification)) {
                                        MyLog.i("receive notification handle by cpra");
                                    }
                                }
                                break;
                            } catch (TException unused) {
                            }
                        }
                    }
                    return null;
                default:
                    return null;
            }
        } catch (DecryptException e5) {
            MyLog.e(e5);
            reportDecryptFail(xmPushActionContainer);
            PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, ReportConstants.ERROR_DECRYPT_MSG_FAILED);
            ClickToStartActivityReporter.dencryptMessageFailed(this.sAppContext, xmPushActionContainer, z2);
            return null;
        } catch (TException e6) {
            MyLog.e(e6);
            MyLog.e("receive a message which action string is not valid. is the reg expired?");
            PushClientReportManager.getInstance(this.sAppContext).reportEvent4ERROR(this.sAppContext.getPackageName(), PushClientReportHelper.getInterfaceIdByType(i2), str, "20");
            ClickToStartActivityReporter.dencryptMessageFailed(this.sAppContext, xmPushActionContainer, z2);
            return null;
        }
    }
}
