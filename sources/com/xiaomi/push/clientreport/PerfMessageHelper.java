package com.xiaomi.push.clientreport;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.clientReport.PushClientReportHelper;
import com.xiaomi.push.service.clientReport.PushClientReportManager;
import com.xiaomi.push.service.xmpush.Command;
import com.xiaomi.smack.util.TrafficUtils;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionAckNotification;
import com.xiaomi.xmpush.thrift.XmPushActionCommand;
import com.xiaomi.xmpush.thrift.XmPushActionCommandResult;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
public class PerfMessageHelper {

    /* JADX INFO: renamed from: com.xiaomi.push.clientreport.PerfMessageHelper$1, reason: invalid class name */
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
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.MultiConnectionBroadcast.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.MultiConnectionResult.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.Notification.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ActionType[ActionType.Command.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
        }
    }

    public static void collectPerfData(String str, Context context, int i2, int i3) {
        if (i2 <= 0 || i3 <= 0) {
            return;
        }
        int traffic = getTraffic(context, i3);
        if (i2 != PushClientReportHelper.changeOrdinalToCode(NotificationType.UploadTinyData)) {
            PushClientReportManager.getInstance(context.getApplicationContext()).reportPerf(str, i2, 1L, traffic);
        }
    }

    public static void collectUpStream(String str, Context context, byte[] bArr) {
        if (context == null || bArr == null || bArr.length <= 0) {
            return;
        }
        XmPushActionContainer xmPushActionContainer = new XmPushActionContainer();
        try {
            XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionContainer, bArr);
            collectUpStream(str, context, xmPushActionContainer, bArr.length);
        } catch (TException unused) {
            MyLog.w("fail to convert bytes to container");
        }
    }

    public static int getMessageType(TBase tBase, ActionType actionType) {
        int iChangeValueToCode;
        int iChangeOrdinalToCode;
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$xmpush$thrift$ActionType[actionType.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                return PushClientReportHelper.changeValueToCode(actionType.getValue());
            case 11:
                iChangeValueToCode = PushClientReportHelper.changeValueToCode(actionType.getValue());
                if (tBase != null) {
                    try {
                        if (tBase instanceof XmPushActionAckNotification) {
                            String str = ((XmPushActionAckNotification) tBase).type;
                            if (!TextUtils.isEmpty(str) && PushClientReportHelper.changeOrdinalToCode(PushClientReportHelper.changeValueToNotificationType(str)) != -1) {
                                iChangeOrdinalToCode = PushClientReportHelper.changeOrdinalToCode(PushClientReportHelper.changeValueToNotificationType(str));
                                return iChangeOrdinalToCode;
                            }
                        } else if (tBase instanceof XmPushActionNotification) {
                            String str2 = ((XmPushActionNotification) tBase).type;
                            if (!TextUtils.isEmpty(str2)) {
                                if (PushClientReportHelper.changeOrdinalToCode(PushClientReportHelper.changeValueToNotificationType(str2)) != -1) {
                                    iChangeValueToCode = PushClientReportHelper.changeOrdinalToCode(PushClientReportHelper.changeValueToNotificationType(str2));
                                }
                                if (NotificationType.UploadTinyData.equals(PushClientReportHelper.changeValueToNotificationType(str2))) {
                                    return -1;
                                }
                            }
                        }
                    } catch (Exception unused) {
                        MyLog.e("PERF_ERROR : parse Notification type error");
                        return iChangeValueToCode;
                    }
                }
                return iChangeValueToCode;
            case 12:
                iChangeValueToCode = PushClientReportHelper.changeValueToCode(actionType.getValue());
                if (tBase != null) {
                    try {
                        if (tBase instanceof XmPushActionCommandResult) {
                            String cmdName = ((XmPushActionCommandResult) tBase).getCmdName();
                            if (!TextUtils.isEmpty(cmdName) && Command.getCode(cmdName) != -1) {
                                iChangeValueToCode = Command.getCode(cmdName);
                            }
                        } else if (tBase instanceof XmPushActionCommand) {
                            String cmdName2 = ((XmPushActionCommand) tBase).getCmdName();
                            if (!TextUtils.isEmpty(cmdName2) && Command.getCode(cmdName2) != -1) {
                                iChangeOrdinalToCode = Command.getCode(cmdName2);
                                return iChangeOrdinalToCode;
                            }
                        }
                    } catch (Exception unused2) {
                        MyLog.e("PERF_ERROR : parse Command type error");
                    }
                }
                return iChangeValueToCode;
            default:
                return -1;
        }
    }

    public static int getTraffic(Context context, int i2) {
        int networkType = TrafficUtils.getNetworkType(context);
        if (-1 == networkType) {
            return -1;
        }
        return (i2 * (networkType == 0 ? 13 : 11)) / 10;
    }

    public static int typeToCode(ActionType actionType) {
        return PushClientReportHelper.changeValueToCode(actionType.getValue());
    }

    public static void collectPerfData(String str, Context context, TBase tBase, ActionType actionType, int i2) {
        collectPerfData(str, context, getMessageType(tBase, actionType), i2);
    }

    public static void collectUpStream(String str, Context context, XmPushActionContainer xmPushActionContainer, int i2) {
        ActionType action;
        if (context == null || xmPushActionContainer == null || (action = xmPushActionContainer.getAction()) == null) {
            return;
        }
        int iTypeToCode = typeToCode(action);
        if (i2 <= 0) {
            byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(xmPushActionContainer);
            i2 = bArrConvertThriftObjectToBytes != null ? bArrConvertThriftObjectToBytes.length : 0;
        }
        collectPerfData(str, context, iTypeToCode, i2);
    }
}
