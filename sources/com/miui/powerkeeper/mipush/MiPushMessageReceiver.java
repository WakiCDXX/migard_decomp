package com.miui.powerkeeper.mipush;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.cloudcontrol.CloudUpdateReceiver;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.utils.Device;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.Utils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class MiPushMessageReceiver extends PushMessageReceiver {
    private final String POWERKEEPER_PASS_THROUGH_MESSAGE = "PowerKeeperPassThroughMessage";
    private static final String TAG = "PowerKeeper.Push";
    private static final boolean D = Log.isLoggable(TAG, 3);

    private void parseCommmands(Context context, JSONArray jSONArray) {
        if (jSONArray == null) {
            Log.d(TAG, "commandArray is NULL");
            return;
        }
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            try {
                String string = new JSONObject(jSONArray.getString(i2)).getString("command");
                if (!TextUtils.isEmpty(string) && TextUtils.equals(string, "updateCloudControl")) {
                    Intent intent = new Intent(Constants.UPDATE_PROFILE);
                    intent.setClass(context, CloudUpdateReceiver.class);
                    context.sendBroadcast(intent);
                }
            } catch (Exception e2) {
                Log.d(TAG, "parseCommands: " + e2.getMessage());
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parsePassThrough(Context context, MiPushMessage miPushMessage) {
        if (context == null || miPushMessage == null) {
            return;
        }
        try {
            String content = miPushMessage.getContent();
            JSONObject jSONObject = new JSONObject(content);
            Log.d(TAG, "passThrough: " + content);
            if (!jSONObject.has("PowerKeeperPassThroughMessage")) {
                Log.d(TAG, "not included json: POWERKEEPER_PASS_THROUGH_MESSAGE");
                return;
            }
            JSONObject jSONObject2 = jSONObject.getJSONObject("PowerKeeperPassThroughMessage");
            if ("PowerKeeper".equals(jSONObject2.getString("moduleName")) && jSONObject2.getInt("version") == 1) {
                parseCommmands(context, jSONObject2.getJSONArray("commands"));
            }
        } catch (Exception e2) {
            Log.d(TAG, "passThroughPaser" + e2.getMessage());
        }
    }

    @Override // com.xiaomi.mipush.sdk.PushMessageReceiver
    public void onCommandResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        String command = miPushCommandMessage.getCommand();
        if (!MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
                if (miPushCommandMessage.getResultCode() == 0) {
                    Log.d(TAG, "mipush register alias success");
                    return;
                } else {
                    Log.d(TAG, "mipush register alias failed");
                    return;
                }
            }
            if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
                if (miPushCommandMessage.getResultCode() == 0) {
                    Log.d(TAG, "mipush register subscrible topic  success");
                    return;
                } else {
                    Log.d(TAG, "mipush register subscrible topic failed");
                    return;
                }
            }
            return;
        }
        if (miPushCommandMessage.getResultCode() != 0) {
            Log.d(TAG, "mipush register failed");
            PowerLog.recordKey("mipush failed");
            return;
        }
        List<String> commandArguments = miPushCommandMessage.getCommandArguments();
        String str = (commandArguments == null || commandArguments.size() <= 0) ? null : commandArguments.get(0);
        String str2 = Device.getModelNew() + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + Device.getVersion();
        List<String> allTopic = MiPushClient.getAllTopic(context);
        if (allTopic == null || !allTopic.contains(str2)) {
            MiPushClient.subscribe(context, str2, null);
            if (D) {
                Log.d(TAG, "subscribe deviceName=" + str2);
            }
        }
        if (allTopic == null || !allTopic.contains(Build.VERSION.INCREMENTAL)) {
            String str3 = Build.VERSION.INCREMENTAL;
            MiPushClient.subscribe(context, str3, null);
            if (D) {
                Log.d(TAG, "subscribe VERSION=" + str3);
            }
        }
        String model = Device.getModel();
        if (allTopic == null || !allTopic.contains(model)) {
            MiPushClient.subscribe(context, model, null);
            if (D) {
                Log.d(TAG, "subscribe model=" + model);
            }
        }
        if (D) {
            Log.d(TAG, "registet mRegId: " + str + " registet name: " + str2 + " registet version: " + Build.VERSION.INCREMENTAL + " device model: " + Device.getModel() + "mipush register success topics=" + allTopic);
        }
        PowerLog.recordKey("mipush success r=" + str + " a= i=" + Utils.getLastCharacters("", 3));
    }

    @Override // com.xiaomi.mipush.sdk.PushMessageReceiver
    public void onReceivePassThroughMessage(final Context context, final MiPushMessage miPushMessage) {
        PowerLog.recordKey("mipush receive msg");
        final String content = miPushMessage.getContent();
        final int passThrough = miPushMessage.getPassThrough();
        BackgroundThread.getHandler().post(new Runnable() { // from class: com.miui.powerkeeper.mipush.MiPushMessageReceiver.1
            @Override // java.lang.Runnable
            public void run() {
                if (passThrough != 1 || TextUtils.isEmpty(content)) {
                    return;
                }
                MiPushMessageReceiver.this.parsePassThrough(context, miPushMessage);
            }
        });
    }
}
