package com.xiaomi.mipush.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.PushServiceConstants;
import com.xiaomi.push.service.clientReport.PushClientReportManager;
import com.xiaomi.push.service.xmpush.Command;
import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class PushMessageHandler extends BaseService {
    private static final String TAG = "PushMessageHandler";
    private static List<MiPushClient.ICallbackResult> sICallbackResult = new ArrayList();
    private static List<MiPushClient.MiPushClientCallback> sCallbacks = new ArrayList();
    private static ThreadPoolExecutor sPool = new ThreadPoolExecutor(1, 1, 15, TimeUnit.SECONDS, new LinkedBlockingQueue());

    interface PushMessageInterface extends Serializable {
    }

    public static void addJob(Context context, Intent intent) {
        MyLog.v(TAG, "addjob PushMessageHandler " + intent);
        if (intent != null) {
            scheduleJob(context, intent);
            startService(context);
        }
    }

    protected static void addPushCallbackClass(MiPushClient.MiPushClientCallback miPushClientCallback) {
        synchronized (sCallbacks) {
            try {
                if (!sCallbacks.contains(miPushClientCallback)) {
                    sCallbacks.add(miPushClientCallback);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected static void addUPSCallback(MiPushClient.ICallbackResult iCallbackResult) {
        synchronized (sICallbackResult) {
            try {
                if (!sICallbackResult.contains(iCallbackResult)) {
                    sICallbackResult.add(iCallbackResult);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private static void callbackClickedActivityCall(Context context) {
        try {
            Intent intent = new Intent();
            intent.setPackage(context.getPackageName());
            intent.setAction(PushConstants.ACTION_CLICKED_ACTIVITY_FINISH);
            context.sendBroadcast(intent, AppPermission.getMipushReceivePermission(context));
        } catch (Exception e2) {
            MyLog.w(TAG, "callback sync error" + e2);
        }
    }

    private static void handleNewMessage(Context context, Intent intent, ResolveInfo resolveInfo, boolean z2) {
        try {
            MessageHandleService.MessageHandleJob messageHandleJob = new MessageHandleService.MessageHandleJob(intent, (PushMessageReceiver) SystemUtils.loadClass(context, resolveInfo.activityInfo.name).newInstance());
            if (z2) {
                MessageHandleService.processJobSync(context.getApplicationContext(), messageHandleJob);
            } else {
                MessageHandleService.addJob(context.getApplicationContext(), messageHandleJob);
            }
            MessageHandleService.onHandleIntent(context, new Intent(context.getApplicationContext(), (Class<?>) MessageHandleService.class));
        } catch (Throwable th) {
            MyLog.e(th);
        }
    }

    public static boolean isCallbackEmpty() {
        return sCallbacks.isEmpty();
    }

    protected static boolean isCategoryMatch(String str, String str2) {
        return (TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) || TextUtils.equals(str, str2);
    }

    protected static void onCommandResult(Context context, String str, String str2, long j2, String str3, List<String> list) {
        synchronized (sCallbacks) {
            try {
                for (MiPushClient.MiPushClientCallback miPushClientCallback : sCallbacks) {
                    if (isCategoryMatch(str, miPushClientCallback.getCategory())) {
                        miPushClientCallback.onCommandResult(str2, j2, str3, list);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected static void onHandleIntent(Context context, Intent intent) {
        ResolveInfo next;
        boolean booleanExtra = false;
        try {
            booleanExtra = intent.getBooleanExtra(PushConstants.EXTRA_IS_CLICKED_ACTIVITY_CALL, false);
        } catch (Throwable th) {
            MyLog.w(TAG, "intent unparcel error:" + th);
        }
        try {
            MyLog.w(TAG, "-->onHandleIntent(): action=", intent.getAction());
            if (PushConstants.MIPUSH_ACTION_SEND_TINYDATA.equals(intent.getAction())) {
                ClientUploadDataItem clientUploadDataItem = new ClientUploadDataItem();
                XmPushThriftSerializeUtils.convertByteArrayToThriftObject(clientUploadDataItem, intent.getByteArrayExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD));
                MyLog.v(TAG, "PushMessageHandler.onHandleIntent " + clientUploadDataItem.getId());
                MiTinyDataClient.upload(context, clientUploadDataItem);
            } else if (1 == PushMessageHelper.getPushMode(context)) {
                if (isCallbackEmpty()) {
                    MyLog.e(TAG, "receive a message before application calling initialize");
                    if (booleanExtra) {
                        callbackClickedActivityCall(context);
                        return;
                    }
                    return;
                }
                PushMessageInterface pushMessageInterfaceProcessIntent = PushMessageProcessor.getInstance(context).processIntent(intent);
                if (pushMessageInterfaceProcessIntent != null) {
                    processMessageForCallback(context, pushMessageInterfaceProcessIntent);
                }
            } else if (!PushServiceConstants.ACTION_SYNC_LOG.equals(intent.getAction())) {
                Intent intent2 = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
                intent2.setPackage(context.getPackageName());
                intent2.putExtras(intent);
                try {
                    List<ResolveInfo> listQueryBroadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent2, 32);
                    if (listQueryBroadcastReceivers == null) {
                        next = null;
                        break;
                    }
                    Iterator<ResolveInfo> it = listQueryBroadcastReceivers.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            next = null;
                            break;
                        }
                        next = it.next();
                        ActivityInfo activityInfo = next.activityInfo;
                        if (activityInfo != null && activityInfo.packageName.equals(context.getPackageName()) && PushMessageReceiver.class.isAssignableFrom(SystemUtils.loadClass(context, next.activityInfo.name))) {
                            break;
                        }
                    }
                    if (next != null) {
                        handleNewMessage(context, intent2, next, booleanExtra);
                    } else {
                        MyLog.e(TAG, "cannot find the receiver to handler this message, check your manifest");
                        PushClientReportManager.getInstance(context).reportEvent4ERROR(context.getPackageName(), intent, "11");
                    }
                } catch (Exception e2) {
                    MyLog.e(TAG, e2);
                    PushClientReportManager.getInstance(context).reportEvent4ERROR(context.getPackageName(), intent, "9");
                }
            }
            if (!booleanExtra) {
            }
        } catch (Throwable th2) {
            try {
                MyLog.e(TAG, th2);
                PushClientReportManager.getInstance(context).reportEvent4ERROR(context.getPackageName(), intent, "10");
            } finally {
                if (booleanExtra) {
                    callbackClickedActivityCall(context);
                }
            }
        }
    }

    public static void onInitializeResult(long j2, String str, String str2) {
        synchronized (sCallbacks) {
            try {
                Iterator<MiPushClient.MiPushClientCallback> it = sCallbacks.iterator();
                while (it.hasNext()) {
                    it.next().onInitializeResult(j2, str, str2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void onReceiveMessage(Context context, MiPushMessage miPushMessage) {
        synchronized (sCallbacks) {
            try {
                for (MiPushClient.MiPushClientCallback miPushClientCallback : sCallbacks) {
                    if (isCategoryMatch(miPushMessage.getCategory(), miPushClientCallback.getCategory())) {
                        miPushClientCallback.onReceiveMessage(miPushMessage.getContent(), miPushMessage.getAlias(), miPushMessage.getTopic(), miPushMessage.isNotified());
                        miPushClientCallback.onReceiveMessage(miPushMessage);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected static void onSubscribeResult(Context context, String str, long j2, String str2, String str3) {
        synchronized (sCallbacks) {
            try {
                for (MiPushClient.MiPushClientCallback miPushClientCallback : sCallbacks) {
                    if (isCategoryMatch(str, miPushClientCallback.getCategory())) {
                        miPushClientCallback.onSubscribeResult(j2, str2, str3);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected static void onUPSRegisterResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        synchronized (sICallbackResult) {
            try {
                for (MiPushClient.ICallbackResult iCallbackResult : sICallbackResult) {
                    if (iCallbackResult instanceof MiPushClient.UPSRegisterCallBack) {
                        MiPushClient.TokenResult tokenResult = new MiPushClient.TokenResult();
                        if (miPushCommandMessage != null && miPushCommandMessage.getCommandArguments() != null && miPushCommandMessage.getCommandArguments().size() > 0) {
                            tokenResult.setResultCode(miPushCommandMessage.getResultCode());
                            tokenResult.setToken(miPushCommandMessage.getCommandArguments().get(0));
                        }
                        iCallbackResult.onResult(tokenResult);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected static void onUnsubscribeResult(Context context, String str, long j2, String str2, String str3) {
        synchronized (sCallbacks) {
            try {
                for (MiPushClient.MiPushClientCallback miPushClientCallback : sCallbacks) {
                    if (isCategoryMatch(str, miPushClientCallback.getCategory())) {
                        miPushClientCallback.onUnsubscribeResult(j2, str2, str3);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void processMessageForCallback(Context context, PushMessageInterface pushMessageInterface) {
        if (pushMessageInterface instanceof MiPushMessage) {
            onReceiveMessage(context, (MiPushMessage) pushMessageInterface);
            return;
        }
        if (pushMessageInterface instanceof MiPushCommandMessage) {
            MiPushCommandMessage miPushCommandMessage = (MiPushCommandMessage) pushMessageInterface;
            String command = miPushCommandMessage.getCommand();
            String str = null;
            if (Command.COMMAND_REGISTER.value.equals(command)) {
                List<String> commandArguments = miPushCommandMessage.getCommandArguments();
                if (commandArguments != null && !commandArguments.isEmpty()) {
                    str = commandArguments.get(0);
                }
                onInitializeResult(miPushCommandMessage.getResultCode(), miPushCommandMessage.getReason(), str);
                return;
            }
            if (Command.COMMAND_SET_ALIAS.value.equals(command) || Command.COMMAND_UNSET_ALIAS.value.equals(command) || Command.COMMAND_SET_ACCEPT_TIME.value.equals(command)) {
                onCommandResult(context, miPushCommandMessage.getCategory(), command, miPushCommandMessage.getResultCode(), miPushCommandMessage.getReason(), miPushCommandMessage.getCommandArguments());
                return;
            }
            if (Command.COMMAND_SUBSCRIBE_TOPIC.value.equals(command)) {
                List<String> commandArguments2 = miPushCommandMessage.getCommandArguments();
                if (commandArguments2 != null && !commandArguments2.isEmpty()) {
                    str = commandArguments2.get(0);
                }
                onSubscribeResult(context, miPushCommandMessage.getCategory(), miPushCommandMessage.getResultCode(), miPushCommandMessage.getReason(), str);
                return;
            }
            if (Command.COMMAND_UNSUBSCRIBE_TOPIC.value.equals(command)) {
                List<String> commandArguments3 = miPushCommandMessage.getCommandArguments();
                if (commandArguments3 != null && !commandArguments3.isEmpty()) {
                    str = commandArguments3.get(0);
                }
                onUnsubscribeResult(context, miPushCommandMessage.getCategory(), miPushCommandMessage.getResultCode(), miPushCommandMessage.getReason(), str);
            }
        }
    }

    protected static void removeAllPushCallbackClass() {
        synchronized (sCallbacks) {
            sCallbacks.clear();
        }
    }

    protected static void removeAllUPSCallback() {
        synchronized (sICallbackResult) {
            sICallbackResult.clear();
        }
    }

    protected static void removePushCallbackClass(MiPushClient.MiPushClientCallback miPushClientCallback) {
        synchronized (sCallbacks) {
            sCallbacks.remove(miPushClientCallback);
        }
    }

    protected static void removeUPSCallback(MiPushClient.ICallbackResult iCallbackResult) {
        synchronized (sICallbackResult) {
            sICallbackResult.remove(iCallbackResult);
        }
    }

    private static void scheduleJob(final Context context, final Intent intent) {
        if (intent != null && !sPool.isShutdown()) {
            sPool.execute(new Runnable() { // from class: com.xiaomi.mipush.sdk.PushMessageHandler.1
                @Override // java.lang.Runnable
                public void run() {
                    PushMessageHandler.onHandleIntent(context, intent);
                }
            });
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("-->scheduleJob() fail, case");
        sb.append(intent == null ? "0" : "1");
        MyLog.persist(TAG, sb.toString());
    }

    public static void startService(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, (Class<?>) PushMessageHandler.class));
        try {
            context.startService(intent);
        } catch (Exception e2) {
            MyLog.w(TAG, e2.getMessage());
        }
    }

    @Override // com.xiaomi.mipush.sdk.BaseService
    protected boolean hasJob() {
        ThreadPoolExecutor threadPoolExecutor = sPool;
        return (threadPoolExecutor == null || threadPoolExecutor.getQueue() == null || sPool.getQueue().size() <= 0) ? false : true;
    }

    @Override // com.xiaomi.mipush.sdk.BaseService, android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // com.xiaomi.mipush.sdk.BaseService, android.app.Service
    public void onStart(Intent intent, int i2) {
        super.onStart(intent, i2);
        scheduleJob(getApplicationContext(), intent);
    }
}
