package miui.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import miui.content.ExtraIntent;

/* JADX INFO: loaded from: classes.dex */
public class MiCloudSmsCmd {
    private static final String ACTIVATION_SMS_PREFIX = "AC/";
    private static final String ACTIVATION_SMS_PREFIX_2 = "ACT/";
    private static final int CMD_INDEX = 1;
    private static final String EXTRA_FIND_DEVICE_V2_COMMAND_TYPE_SMS = "sms";
    private static final int HEADER_INDEX = 0;
    private static final int MSG_ID_INDEX = 2;
    private static final String SEPARATOR = ",";
    private static final String SMS_CMD_HEADER = "mfc";
    private static final String SMS_CMD_HEADER_V1 = "mf";
    private static final String SMS_CMD_TAIL = "##";
    private static final String SMS_CMD_TAIL_V1 = "#";
    private static final String TAG = "MiCloudSmsCmd";
    private static final int TIME_INDEX = 3;
    public static final String TYPE_DISCARD_TOKEN = "d";
    public static final String TYPE_LOCATION = "l";
    public static final String TYPE_LOCK = "k";
    public static final String TYPE_NOISE = "n";
    public static final String TYPE_WIPE = "w";
    private static boolean[] sHexChars = new boolean[123];

    static {
        for (char c2 = 'A'; c2 <= 'Z'; c2 = (char) (c2 + 1)) {
            sHexChars[c2] = true;
        }
        for (char c3 = 'a'; c3 <= 'z'; c3 = (char) (c3 + 1)) {
            sHexChars[c3] = true;
        }
        for (char c4 = '0'; c4 <= '9'; c4 = (char) (c4 + 1)) {
            sHexChars[c4] = true;
        }
    }

    private MiCloudSmsCmd() {
    }

    public static boolean checkAndDispatchActivationSms(Context context, int i2, String str, String str2) {
        int iIndexOf = str2.indexOf(ACTIVATION_SMS_PREFIX);
        if (iIndexOf == -1) {
            return checkAndDispatchActivationSms2(context, i2, str, str2);
        }
        Log.v(TAG, "checkAndDispatchActivationSms: The message looks like an activation");
        int i3 = iIndexOf + 3;
        int i4 = iIndexOf + 35;
        int i5 = iIndexOf + 36;
        int i6 = iIndexOf + 47;
        if (str2.length() < i6) {
            Log.v(TAG, "checkAndDispatchActivationSms: length check failed, " + str2.length() + " < " + i6);
            return false;
        }
        if (!isStrHex(str2, i3, i4)) {
            Log.v(TAG, "checkAndDispatchActivationSms: left hex check failed");
            return false;
        }
        if (str2.charAt(i4) != ':') {
            Log.v(TAG, "checkAndDispatchActivationSms: colon check failed");
            return false;
        }
        if (!isStrHex(str2, i5, i6)) {
            Log.v(TAG, "checkAndDispatchActivationSms: right hex check failed");
            return false;
        }
        Log.v(TAG, "checkAndDispatchActivationSms: activation SMS acknowledged, broadcasting...");
        Intent intent = new Intent("com.xiaomi.action.ACTIVATION_SMS_RECEIVED");
        intent.putExtra("extra_sim_index", i2);
        intent.putExtra("extra_address", str);
        intent.putExtra("extra_msg_id", str2.substring(i5, i6));
        intent.putExtra("extra_vkey1", str2.substring(i3, i4));
        intent.setPackage("com.xiaomi.simactivate.service");
        context.sendBroadcastAsUser(intent, getAllUser());
        return true;
    }

    private static boolean checkAndDispatchActivationSms2(Context context, int i2, String str, String str2) {
        int iIndexOf = str2.indexOf(ACTIVATION_SMS_PREFIX_2);
        if (iIndexOf == -1) {
            return false;
        }
        Log.v(TAG, "checkAndDispatchActivationSmsi2: The message looks like an activation");
        int i3 = iIndexOf + 4;
        int i4 = iIndexOf + 36;
        int i5 = iIndexOf + 37;
        int i6 = iIndexOf + 48;
        if (str2.length() < i6) {
            Log.v(TAG, "checkAndDispatchActivationSms2: length check failed, " + str2.length() + " < " + i6);
            return false;
        }
        if (!isStrHex(str2, i3, i4)) {
            Log.v(TAG, "checkAndDispatchActivationSms2: left hex check failed");
            return false;
        }
        if (str2.charAt(i4) != ':') {
            Log.v(TAG, "checkAndDispatchActivationSms2: colon check failed");
            return false;
        }
        if (!isStrHex(str2, i5, i6)) {
            Log.v(TAG, "checkAndDispatchActivationSms2: right hex check failed");
            return false;
        }
        Log.v(TAG, "checkAndDispatchActivationSms2: activation SMS acknowledged, broadcasting...");
        Intent intent = new Intent("com.xiaomi.action.ACTIVATION_SMS_2_RECEIVED");
        intent.putExtra("extra_sim_index", i2);
        intent.putExtra("extra_address", str);
        intent.putExtra("extra_sms", str2);
        intent.setPackage("com.xiaomi.simactivate.service");
        context.sendBroadcastAsUser(intent, getAllUser());
        return true;
    }

    public static boolean checkSmsCmd(Intent intent, Context context, String str, String str2, int i2, int i3) {
        if (TextUtils.isEmpty(str2)) {
            return false;
        }
        if (str2.indexOf(SMS_CMD_HEADER) < 0 && str2.indexOf(SMS_CMD_HEADER_V1) < 0) {
            return false;
        }
        if (str2.indexOf(SMS_CMD_TAIL) < 0 && str2.indexOf("#") < 0) {
            return false;
        }
        transferToFindDevice(intent, context, str, str2, i2, i3);
        return true;
    }

    private static UserHandle getAllUser() {
        try {
            return (UserHandle) UserHandle.class.getField("ALL").get(null);
        } catch (IllegalAccessException e2) {
            throw new IllegalStateException("will not reach here", e2);
        } catch (NoSuchFieldException e3) {
            throw new IllegalStateException("will not reach here", e3);
        }
    }

    private static boolean isStrHex(String str, int i2, int i3) {
        while (i2 < i3) {
            char cCharAt = str.charAt(i2);
            boolean[] zArr = sHexChars;
            if (cCharAt >= zArr.length || !zArr[cCharAt]) {
                return false;
            }
            i2++;
        }
        return true;
    }

    private static void sendOldVersionCommandToFindDeviceService(Context context, String str, String str2, String str3, String str4, String str5, String str6) {
        String str7;
        Intent intent = new Intent();
        intent.putExtra("android.intent.extra.device_msgId", str);
        intent.putExtra("android.intent.extra.device_time", str2);
        intent.putExtra("android.intent.extra.device_digest", str5);
        intent.putExtra("android.intent.extra.device_cmd", str3);
        intent.putExtra("android.intent.extra.lock_password", str4);
        intent.putExtra("android.intent.extra.from_address", str6);
        if ("l".equals(str3)) {
            str7 = "miui.intent.action.REQUEST_LOCATION";
        } else if (TYPE_NOISE.equals(str3)) {
            str7 = "miui.intent.action.NOISE";
        } else if (TYPE_LOCK.equals(str3)) {
            str7 = "miui.intent.action.LOCK_DEVICE";
        } else if (TYPE_WIPE.equals(str3)) {
            str7 = "miui.intent.action.WIPE_DATA";
        } else {
            str7 = "d".equals(str3) ? "miui.intent.action.DISCARD_FIND_DEVICE_TOKEN" : null;
        }
        if (TextUtils.isEmpty(str7)) {
            return;
        }
        intent.setAction(str7);
        intent.setPackage("com.xiaomi.finddevice");
        if (context.startService(intent) == null) {
            intent.setPackage("com.miui.cloudservice");
            context.startService(intent);
        }
    }

    private static void transferToFindDevice(Intent intent, Context context, final String str, final String str2, int i2, int i3) {
        final PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) context.getSystemService(NightStandbyRecord.KEY_POWER)).newWakeLock(1, MiCloudSmsCmd.class.getName());
        wakeLockNewWakeLock.acquire();
        Intent intent2 = new Intent(ExtraIntent.INTENT_FIND_DEVICE_V2_CMD_RECEIVER);
        intent2.putExtra("android.intent.extra.finddevice.command_type", "sms");
        intent2.putExtra("android.intent.extra.from_address", str);
        intent2.putExtra("android.intent.extra.from_slotId", i2);
        intent2.putExtra("android.intent.extra.from_subId", i3);
        intent2.putExtra("android.intent.extra.finddevice.command", str2);
        if (intent != null) {
            intent2.putExtra("android.intent.extra.finddevice.intercept_sms_intent", intent);
        }
        context.sendOrderedBroadcast(intent2, "miui.cloud.finddevice.RECEIVE_COMMAND", new BroadcastReceiver() { // from class: miui.provider.MiCloudSmsCmd.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent3) {
                try {
                    if (getResultCode() == 0) {
                        MiCloudSmsCmd.transferToOldVersionFindDevice(context2, str, str2);
                    }
                } finally {
                    wakeLockNewWakeLock.release();
                }
            }
        }, null, 0, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void transferToOldVersionFindDevice(Context context, String str, String str2) {
        String str3;
        String str4;
        String[] strArrSplit = str2.split(",");
        if (strArrSplit == null || strArrSplit.length < 5 || strArrSplit[0].indexOf(SMS_CMD_HEADER) < 0) {
            Log.e(TAG, "Bad find device command: " + str2 + ". ");
            return;
        }
        String str5 = strArrSplit[1];
        String str6 = strArrSplit[2];
        String str7 = strArrSplit[3];
        if (TYPE_LOCK.equals(str5)) {
            str4 = strArrSplit[4];
            str3 = strArrSplit[5];
        } else {
            str3 = strArrSplit[4];
            str4 = null;
        }
        sendOldVersionCommandToFindDeviceService(context, str6, str7, str5, str4, str3.substring(0, str3.lastIndexOf(SMS_CMD_TAIL)), str);
    }

    public static boolean checkSmsCmd(Context context, String str, String str2, int i2, int i3) {
        return checkSmsCmd(null, context, str, str2, i2, i3);
    }

    public static boolean checkSmsCmd(Context context, String str, String str2, int i2) {
        return checkSmsCmd(context, str, str2, i2, -1);
    }

    public static boolean checkSmsCmd(Context context, String str, String str2) {
        return checkSmsCmd(context, str, str2, -1, -1);
    }
}
