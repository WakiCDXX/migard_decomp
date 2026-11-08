package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.DataCryptUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.string.Base64Coder;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.Target;
import com.xiaomi.xmpush.thrift.XmPushActionAckMessage;
import com.xiaomi.xmpush.thrift.XmPushActionAckNotification;
import com.xiaomi.xmpush.thrift.XmPushActionCommandResult;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushActionRegistrationResult;
import com.xiaomi.xmpush.thrift.XmPushActionSendFeedbackResult;
import com.xiaomi.xmpush.thrift.XmPushActionSendMessage;
import com.xiaomi.xmpush.thrift.XmPushActionSubscriptionResult;
import com.xiaomi.xmpush.thrift.XmPushActionUnRegistrationResult;
import com.xiaomi.xmpush.thrift.XmPushActionUnSubscriptionResult;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.nio.ByteBuffer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
public class PushContainerHelper {

    /* JADX INFO: renamed from: com.xiaomi.mipush.sdk.PushContainerHelper$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$xmpush$thrift$ActionType;

        static {
            int[] iArr = new int[ActionType.values().length];
            $SwitchMap$com$xiaomi$xmpush$thrift$ActionType = iArr;
            try {
                iArr[ActionType.Registration.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.UnRegistration.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.Subscription.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.UnSubscription.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.SendMessage.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.AckMessage.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.SetConfig.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.ReportFeedback.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.Notification.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.Command.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
        }
    }

    protected static <T extends TBase<T, ?>> XmPushActionContainer constructResponseContainer(Context context, T t2, ActionType actionType, boolean z2, String str, String str2) {
        return generateContainer(context, t2, actionType, z2, str, str2, false);
    }

    private static TBase createRespMessageFromAction(ActionType actionType, boolean z2) {
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$xmpush$thrift$ActionType[actionType.ordinal()]) {
            case 1:
                return new XmPushActionRegistrationResult();
            case 2:
                return new XmPushActionUnRegistrationResult();
            case 3:
                return new XmPushActionSubscriptionResult();
            case 4:
                return new XmPushActionUnSubscriptionResult();
            case 5:
                return new XmPushActionSendMessage();
            case 6:
                return new XmPushActionAckMessage();
            case 7:
                return new XmPushActionCommandResult();
            case 8:
                return new XmPushActionSendFeedbackResult();
            case 9:
                if (z2) {
                    return new XmPushActionNotification();
                }
                XmPushActionAckNotification xmPushActionAckNotification = new XmPushActionAckNotification();
                xmPushActionAckNotification.setErrorCodeIsSet(true);
                return xmPushActionAckNotification;
            case 10:
                return new XmPushActionCommandResult();
            default:
                return null;
        }
    }

    protected static <T extends TBase<T, ?>> XmPushActionContainer generateContainer(Context context, T t2, ActionType actionType, boolean z2, String str, String str2, boolean z3) {
        byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(t2);
        if (bArrConvertThriftObjectToBytes == null) {
            MyLog.w("invoke convertThriftObjectToBytes method, return null.");
            return null;
        }
        XmPushActionContainer xmPushActionContainer = new XmPushActionContainer();
        if (z2) {
            String regSecret = AppInfoHolder.getInstance(context).getRegSecret();
            if (TextUtils.isEmpty(regSecret)) {
                MyLog.w("regSecret is empty, return null");
                return null;
            }
            try {
                bArrConvertThriftObjectToBytes = DataCryptUtils.mipushEncrypt(Base64Coder.decode(regSecret), bArrConvertThriftObjectToBytes);
            } catch (Exception unused) {
                MyLog.e("encryption error. ");
            }
        }
        Target target = new Target();
        target.channelId = 5L;
        target.userId = "fakeid";
        xmPushActionContainer.setTarget(target);
        xmPushActionContainer.setPushAction(ByteBuffer.wrap(bArrConvertThriftObjectToBytes));
        xmPushActionContainer.setAction(actionType);
        xmPushActionContainer.setIsRequest(z3);
        xmPushActionContainer.setPackageName(str);
        xmPushActionContainer.setEncryptAction(z2);
        xmPushActionContainer.setAppid(str2);
        return xmPushActionContainer;
    }

    protected static <T extends TBase<T, ?>> XmPushActionContainer generateRequestContainer(Context context, T t2, ActionType actionType) {
        return generateRequestContainer(context, t2, actionType, !actionType.equals(ActionType.Registration), context.getPackageName(), AppInfoHolder.getInstance(context).getAppID());
    }

    public static TBase getIgnoreRegMessageBodyFromContainer(Context context, XmPushActionContainer xmPushActionContainer) throws TException {
        if (xmPushActionContainer.isEncryptAction()) {
            return null;
        }
        byte[] pushAction = xmPushActionContainer.getPushAction();
        TBase tBaseCreateRespMessageFromAction = createRespMessageFromAction(xmPushActionContainer.getAction(), xmPushActionContainer.isRequest);
        if (tBaseCreateRespMessageFromAction != null) {
            XmPushThriftSerializeUtils.convertByteArrayToThriftObject(tBaseCreateRespMessageFromAction, pushAction);
        }
        return tBaseCreateRespMessageFromAction;
    }

    public static TBase getResponseMessageBodyFromContainer(Context context, XmPushActionContainer xmPushActionContainer) throws DecryptException, TException {
        byte[] pushAction;
        if (xmPushActionContainer.isEncryptAction()) {
            byte[] bArrCheckAndGetAssemblePushKeys = AssemblePushHelper.checkAndGetAssemblePushKeys(context, xmPushActionContainer, AssemblePush.ASSEMBLE_PUSH_FCM);
            if (bArrCheckAndGetAssemblePushKeys == null) {
                bArrCheckAndGetAssemblePushKeys = Base64Coder.decode(AppInfoHolder.getInstance(context).getRegSecret());
            }
            try {
                pushAction = DataCryptUtils.mipushDecrypt(bArrCheckAndGetAssemblePushKeys, xmPushActionContainer.getPushAction());
            } catch (Exception e2) {
                throw new DecryptException("the aes decrypt failed.", e2);
            }
        } else {
            pushAction = xmPushActionContainer.getPushAction();
        }
        TBase tBaseCreateRespMessageFromAction = createRespMessageFromAction(xmPushActionContainer.getAction(), xmPushActionContainer.isRequest);
        if (tBaseCreateRespMessageFromAction != null) {
            XmPushThriftSerializeUtils.convertByteArrayToThriftObject(tBaseCreateRespMessageFromAction, pushAction);
        }
        return tBaseCreateRespMessageFromAction;
    }

    protected static <T extends TBase<T, ?>> XmPushActionContainer generateRequestContainer(Context context, T t2, ActionType actionType, String str, String str2) {
        return generateRequestContainer(context, t2, actionType, !actionType.equals(ActionType.Registration), str, str2);
    }

    protected static <T extends TBase<T, ?>> XmPushActionContainer generateRequestContainer(Context context, T t2, ActionType actionType, boolean z2, String str, String str2) {
        return generateContainer(context, t2, actionType, z2, str, str2, true);
    }
}
