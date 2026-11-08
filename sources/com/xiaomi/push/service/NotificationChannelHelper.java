package com.xiaomi.push.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.util.PlayerActions;

/* JADX INFO: loaded from: classes.dex */
public class NotificationChannelHelper {
    private static final int ACTION_COPY_FROM_XMSF = 4;
    private static final int ACTION_COVER_FROM_XMSF = 3;
    private static final int ACTION_CREATE = 1;
    private static final int ACTION_NONE = 0;
    private static final int ACTION_UPDATE = 2;
    private static final String KEY_HAS_COPY_ALL = "has_copy_all";
    private static final String RECORED_FILE = "mipush_channel_copy_sp";
    public static final String REMOVE_CHANNEL_MARK = "REMOVE_CHANNEL_MARK";
    public static final int USER_LOCKED_IMPORTANCE = 4;
    public static final int USER_LOCKED_LIGHTS = 8;
    public static final int USER_LOCKED_SOUND = 32;
    public static final int USER_LOCKED_VIBRATION = 16;
    public static final int USER_LOCKED_VISIBILITY = 2;
    private static final String TAG = "NCHelper";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    @TargetApi(26)
    private static boolean channelConfigLowerCompare(NotificationChannel notificationChannel, NotificationChannel notificationChannel2) {
        boolean z2;
        if (notificationChannel == null || notificationChannel2 == null) {
            return false;
        }
        boolean z3 = true;
        if (TextUtils.equals(notificationChannel.getName(), notificationChannel2.getName())) {
            z2 = false;
        } else {
            if (DEBUG) {
                testLog("appHack channelConfigLowerCompare:getName");
            }
            z2 = true;
        }
        if (!TextUtils.equals(notificationChannel.getDescription(), notificationChannel2.getDescription())) {
            if (DEBUG) {
                testLog("appHack channelConfigLowerCompare:getDescription");
            }
            z2 = true;
        }
        if (notificationChannel.getImportance() != notificationChannel2.getImportance()) {
            notificationChannel.setImportance(Math.min(notificationChannel.getImportance(), notificationChannel2.getImportance()));
            if (DEBUG) {
                testLog("appHack channelConfigLowerCompare:getImportance  " + notificationChannel.getImportance() + " " + notificationChannel2.getImportance());
            }
            z2 = true;
        }
        if (notificationChannel.shouldVibrate() != notificationChannel2.shouldVibrate()) {
            notificationChannel.enableVibration(false);
            if (DEBUG) {
                testLog("appHack channelConfigLowerCompare:enableVibration");
            }
            z2 = true;
        }
        if (notificationChannel.shouldShowLights() != notificationChannel2.shouldShowLights()) {
            notificationChannel.enableLights(false);
            if (DEBUG) {
                testLog("appHack channelConfigLowerCompare:enableLights");
            }
            z2 = true;
        }
        if ((notificationChannel.getSound() != null) != (notificationChannel2.getSound() != null)) {
            notificationChannel.setSound(null, null);
            if (DEBUG) {
                testLog("appHack channelConfigLowerCompare:setSound");
            }
        } else {
            z3 = z2;
        }
        if (DEBUG) {
            testLog("appHack channelConfigLowerCompare:isDifferent:" + z3);
        }
        return z3;
    }

    private static boolean checkCopeidChannel(Context context, String str) {
        if (DEBUG) {
            testLog("checkCopeidChannel:newFullChannelId:" + str + "  " + getSp(context).getBoolean(str, false));
        }
        return getSp(context).getBoolean(str, false);
    }

    private static void clearCopyRecords(Context context, String str) {
        try {
            NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(context, str);
            Set<String> setKeySet = getSp(context).getAll().keySet();
            ArrayList arrayList = new ArrayList();
            for (String str2 : setKeySet) {
                if (notificationManagerHelperFrom.isThisAppChannel(str2)) {
                    arrayList.add(str2);
                    if (DEBUG) {
                        testLog("delete channel copy record:" + str2);
                    }
                }
            }
            deleteCopiedChannelRecord(context, arrayList);
        } catch (Exception unused) {
        }
    }

    @TargetApi(26)
    private static NotificationChannel copyChannelForNewId(String str, NotificationChannel notificationChannel) {
        NotificationChannel notificationChannel2 = new NotificationChannel(str, notificationChannel.getName(), notificationChannel.getImportance());
        notificationChannel2.setDescription(notificationChannel.getDescription());
        notificationChannel2.enableVibration(notificationChannel.shouldVibrate());
        notificationChannel2.enableLights(notificationChannel.shouldShowLights());
        notificationChannel2.setSound(notificationChannel.getSound(), notificationChannel.getAudioAttributes());
        notificationChannel2.setLockscreenVisibility(notificationChannel.getLockscreenVisibility());
        return notificationChannel2;
    }

    @TargetApi(26)
    public static void copyXmsfAllChannelsToAppWhenProcessInit(final Context context) {
        if (MIUIUtils.isXMSF(context) && NotificationManagerHelper.isRomSupportNotificationBelongToApp(context) && !getSp(context).getBoolean(KEY_HAS_COPY_ALL, false)) {
            ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.service.NotificationChannelHelper.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                        List<NotificationChannel> notificationChannels = notificationManager.getNotificationChannels();
                        if (notificationChannels != null && notificationChannels.size() > 0) {
                            Iterator<NotificationChannel> it = notificationChannels.iterator();
                            while (it.hasNext()) {
                                NotificationChannelHelper.doCopyOneXmsfChannel(context, notificationManager, it.next());
                            }
                        }
                    } catch (Exception unused) {
                    } finally {
                        NotificationChannelHelper.getSp(context).edit().putBoolean(NotificationChannelHelper.KEY_HAS_COPY_ALL, true).apply();
                    }
                }
            });
        }
    }

    @TargetApi(26)
    public static String createChannel(NotificationManagerHelper notificationManagerHelper, String str, CharSequence charSequence, String str2, int i2, int i3, String str3, String str4) {
        String appChannelId = notificationManagerHelper.getAppChannelId(str);
        boolean z2 = DEBUG;
        if (z2) {
            testLog("createChannel: appChannelId:" + appChannelId + " serverChannelId:" + str + " serverChannelName:" + ((Object) charSequence) + " serverChannelDesc:" + str2 + " serverChannelNotifyType:" + i2 + " serverChannelName:" + ((Object) charSequence) + " serverChannelImportance:" + i3 + " channelSoundStr:" + str3 + " channelPermissions:" + str4);
        }
        NotificationChannel notificationChannel = new NotificationChannel(appChannelId, charSequence, i3);
        notificationChannel.setDescription(str2);
        notificationChannel.enableVibration((i2 & 2) != 0);
        notificationChannel.enableLights((i2 & 4) != 0);
        if ((i2 & 1) == 0) {
            notificationChannel.setSound(null, null);
        } else if (!TextUtils.isEmpty(str3)) {
            if (str3.startsWith(MIPushNotificationHelper.ANDROID_RESOURCE + notificationManagerHelper.getTargetPkg())) {
                notificationChannel.setSound(Uri.parse(str3), Notification.AUDIO_ATTRIBUTES_DEFAULT);
            }
        }
        if (z2) {
            testLog("create channel:" + notificationChannel);
        }
        createOrUpdateChannel(notificationManagerHelper, notificationChannel, str4);
        return appChannelId;
    }

    @SuppressLint({"WrongConstant"})
    @TargetApi(26)
    static void createCopyChannelFromXmsfChannel(Context context, NotificationManagerHelper notificationManagerHelper, NotificationChannel notificationChannel, int i2, String str) {
        if (i2 <= 0) {
            notificationManagerHelper.createNotificationChannel(notificationChannel);
            return;
        }
        int iChannelPermFrom = AppInfoUtils.getSystemUIForXmsfCode(context) >= 2 ? ChannelPermissions.channelPermFrom(context.getPackageName(), str) : 0;
        NotificationChannel notificationChannelCopyChannelForNewId = copyChannelForNewId(notificationChannel.getId(), notificationChannel);
        if ((i2 & 32) != 0) {
            if (notificationChannel.getSound() != null) {
                notificationChannelCopyChannelForNewId.setSound(null, null);
            } else {
                notificationChannelCopyChannelForNewId.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, Notification.AUDIO_ATTRIBUTES_DEFAULT);
            }
        }
        if ((i2 & 16) != 0) {
            if (notificationChannel.shouldVibrate()) {
                notificationChannelCopyChannelForNewId.enableVibration(false);
            } else {
                notificationChannelCopyChannelForNewId.enableVibration(true);
            }
        }
        if ((i2 & 8) != 0) {
            if (notificationChannel.shouldShowLights()) {
                notificationChannelCopyChannelForNewId.enableLights(false);
            } else {
                notificationChannelCopyChannelForNewId.enableLights(true);
            }
        }
        if ((i2 & 4) != 0) {
            int importance = notificationChannel.getImportance() - 1;
            if (importance <= 0) {
                importance = 2;
            }
            notificationChannelCopyChannelForNewId.setImportance(importance);
        }
        if ((i2 & 2) != 0) {
            notificationChannelCopyChannelForNewId.setLockscreenVisibility(notificationChannel.getLockscreenVisibility() - 1);
        }
        notificationManagerHelper.createNotificationChannel(notificationChannelCopyChannelForNewId);
        notificationManagerHelper.updateNotificationChannel(notificationChannel, true);
        ChannelPermissions.grantOrRevokePermissions(notificationManagerHelper.getTargetPkg(), notificationChannel.getId(), iChannelPermFrom, 0);
    }

    @TargetApi(26)
    public static String createDefaultChannel(NotificationManagerHelper notificationManagerHelper) {
        String defaultAppChannelId = notificationManagerHelper.getDefaultAppChannelId();
        try {
            NotificationChannel notificationChannel = new NotificationChannel(defaultAppChannelId, AppInfoUtils.getAppLabel(notificationManagerHelper.getAppContext(), notificationManagerHelper.getTargetPkg()), 3);
            if (DEBUG) {
                testLog("create default channel:" + notificationChannel);
            }
            createOrUpdateChannel(notificationManagerHelper, notificationChannel, null);
        } catch (Exception e2) {
            MyLog.w(TAG, "create default channel error " + e2);
        }
        return defaultAppChannelId;
    }

    @TargetApi(26)
    private static void createOrUpdateChannel(NotificationManagerHelper notificationManagerHelper, NotificationChannel notificationChannel, String str) {
        int i2;
        char c2;
        int channelUserLockedFields;
        Context appContext = notificationManagerHelper.getAppContext();
        String id = notificationChannel.getId();
        String strConvertToOldAppChannelId = NotificationManagerHelper.convertToOldAppChannelId(id, notificationManagerHelper.getTargetPkg());
        boolean z2 = DEBUG;
        if (z2) {
            testLog("appChannelId:" + id + " oldChannelId:" + strConvertToOldAppChannelId);
        }
        if (!MIUIUtils.isXMSF(appContext) || TextUtils.equals(id, strConvertToOldAppChannelId)) {
            NotificationChannel notificationChannel2 = notificationManagerHelper.getNotificationChannel(id);
            if (z2) {
                testLog("elseLogic getNotificationChannel:" + notificationChannel2);
            }
            if (notificationChannel2 == null) {
                notificationManagerHelper.createNotificationChannel(notificationChannel);
            }
            i2 = 0;
            c2 = 0;
        } else {
            NotificationManager notificationManager = (NotificationManager) appContext.getSystemService("notification");
            NotificationChannel notificationChannel3 = notificationManager.getNotificationChannel(strConvertToOldAppChannelId);
            NotificationChannel notificationChannel4 = notificationManagerHelper.getNotificationChannel(id);
            if (z2) {
                testLog("xmsfChannel:" + notificationChannel3);
                testLog("appChannel:" + notificationChannel4);
            }
            if (notificationChannel3 != null) {
                NotificationChannel notificationChannelCopyChannelForNewId = copyChannelForNewId(id, notificationChannel3);
                if (z2) {
                    testLog("copyXmsf copyXmsfChannel:" + notificationChannelCopyChannelForNewId);
                }
                if (notificationChannel4 != null) {
                    channelUserLockedFields = getChannelUserLockedFields(notificationChannel4);
                    notificationManagerHelper.updateNotificationChannel(notificationChannelCopyChannelForNewId, channelUserLockedFields == 0);
                    c2 = 3;
                } else {
                    channelUserLockedFields = getChannelUserLockedFields(notificationChannel3);
                    createCopyChannelFromXmsfChannel(appContext, notificationManagerHelper, notificationChannelCopyChannelForNewId, channelUserLockedFields, notificationChannel3.getId());
                    c2 = 4;
                }
                recordCopiedChannel(appContext, id);
                notificationManager.deleteNotificationChannel(strConvertToOldAppChannelId);
            } else if (notificationChannel4 == null) {
                if (z2) {
                    testLog("appHack createNotificationChannel:" + notificationChannel);
                }
                notificationManagerHelper.createNotificationChannel(notificationChannel);
                c2 = 1;
                channelUserLockedFields = 0;
            } else if (checkCopeidChannel(appContext, id) || !channelConfigLowerCompare(notificationChannel, notificationChannel4)) {
                channelUserLockedFields = 0;
                c2 = 0;
            } else {
                if (z2) {
                    testLog("appHack updateNotificationChannel:" + notificationChannel);
                }
                channelUserLockedFields = getChannelUserLockedFields(notificationChannel4);
                notificationManagerHelper.updateNotificationChannel(notificationChannel, channelUserLockedFields == 0);
                c2 = 2;
            }
            i2 = channelUserLockedFields;
        }
        ChannelPermissions.handleChannelPermissions(notificationManagerHelper.getAppContext(), notificationManagerHelper.getTargetPkg(), id, notificationChannel.getImportance(), str, c2 == 1 || c2 == 4 || c2 == 3, i2);
    }

    private static void deleteCopiedChannelRecord(Context context, List<String> list) {
        if (DEBUG) {
            testLog("deleteCopiedChannelRecord:" + list);
        }
        if (list.isEmpty()) {
            return;
        }
        SharedPreferences.Editor editorEdit = getSp(context).edit();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            editorEdit.remove(it.next());
        }
        editorEdit.apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(26)
    public static void doCopyOneXmsfChannel(Context context, NotificationManager notificationManager, NotificationChannel notificationChannel) {
        try {
            String id = notificationChannel.getId();
            String[] strArrSplit = id.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
            if (strArrSplit.length != 3) {
                if (DEBUG) {
                    testLog("copyAll not mipush channel:" + id);
                    return;
                }
                return;
            }
            boolean z2 = true;
            String str = strArrSplit[1];
            String strReplace = id.replace(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_PREFIX + str + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT, "");
            boolean z3 = DEBUG;
            if (z3) {
                testLog("copyAll extract pushServerCid:" + strReplace);
            }
            NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(context, str);
            String appChannelId = notificationManagerHelperFrom.getAppChannelId(strReplace);
            NotificationChannel notificationChannel2 = notificationManagerHelperFrom.getNotificationChannel(appChannelId);
            if (z3) {
                testLog("copyAll xmsf-app channel:" + notificationChannel + " - " + notificationChannel2);
            }
            NotificationChannel notificationChannelCopyChannelForNewId = copyChannelForNewId(appChannelId, notificationChannel);
            if (z3) {
                testLog("copyAll copyXmsfChannel:" + notificationChannelCopyChannelForNewId);
            }
            if (notificationChannel2 != null) {
                if (getChannelUserLockedFields(notificationChannel2) != 0) {
                    z2 = false;
                }
                notificationManagerHelperFrom.updateNotificationChannel(notificationChannelCopyChannelForNewId, z2);
            } else {
                notificationManagerHelperFrom.createNotificationChannel(notificationChannelCopyChannelForNewId);
            }
            recordCopiedChannel(context, appChannelId);
            notificationManager.deleteNotificationChannel(id);
        } catch (Exception unused) {
        }
    }

    private static int getChannelUserLockedFields(NotificationChannel notificationChannel) {
        int iIntValue = 0;
        try {
            iIntValue = ((Integer) JavaCalls.callMethodOrThrow(notificationChannel, "getUserLockedFields", new Object[0])).intValue();
            if (DEBUG) {
                testLog("isUserLockedChannel:" + iIntValue + " " + notificationChannel);
            }
        } catch (Exception e2) {
            MyLog.w(TAG, "is user locked error" + e2);
        }
        return iIntValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(RECORED_FILE, 0);
    }

    public static void onPackageDataCleanedOrUninstall(Context context, String str) {
        if (!MIUIUtils.isXMSF(context) || TextUtils.isEmpty(str)) {
            return;
        }
        clearCopyRecords(context, str);
        ChannelPermissions.removePermissionCacheByPackage(context, str);
    }

    private static void recordCopiedChannel(Context context, String str) {
        if (DEBUG) {
            testLog("recordCopiedChannel:" + str);
        }
        getSp(context).edit().putBoolean(str, true).apply();
    }

    static void removeChannelInfoIfNeed(PushMetaInfo pushMetaInfo) {
        Map<String, String> map;
        if (pushMetaInfo == null || (map = pushMetaInfo.extra) == null || !map.containsKey(REMOVE_CHANNEL_MARK)) {
            return;
        }
        pushMetaInfo.notifyType = 0;
        pushMetaInfo.extra.remove(MIPushNotificationHelper.NOTIFICATION_CHANNEL_ID);
        pushMetaInfo.extra.remove("channel_importance");
        pushMetaInfo.extra.remove(PlayerActions.Out.KEY_CHANNEL_NAME);
        pushMetaInfo.extra.remove("channel_description");
        pushMetaInfo.extra.remove("channel_perm");
        MyLog.w("delete channel info by:" + pushMetaInfo.extra.get(REMOVE_CHANNEL_MARK));
        pushMetaInfo.extra.remove(REMOVE_CHANNEL_MARK);
    }

    private static void testLog(String str) {
        MyLog.w(TAG, str);
    }
}
