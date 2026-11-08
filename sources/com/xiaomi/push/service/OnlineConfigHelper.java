package com.xiaomi.push.service;

import android.util.Pair;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.CollectionUtils;
import com.xiaomi.xmpush.thrift.ConfigListType;
import com.xiaomi.xmpush.thrift.ConfigType;
import com.xiaomi.xmpush.thrift.NormalConfig;
import com.xiaomi.xmpush.thrift.OnlineConfigItem;
import com.xiaomi.xmpush.thrift.XmPushActionCustomConfig;
import com.xiaomi.xmpush.thrift.XmPushActionNormalConfig;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class OnlineConfigHelper {
    private static final String TAG = "OnlineConfigHelper";

    /* JADX INFO: renamed from: com.xiaomi.push.service.OnlineConfigHelper$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$xmpush$thrift$ConfigListType;
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$xmpush$thrift$ConfigType;

        static {
            int[] iArr = new int[ConfigType.values().length];
            $SwitchMap$com$xiaomi$xmpush$thrift$ConfigType = iArr;
            try {
                iArr[ConfigType.INT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ConfigType[ConfigType.LONG.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ConfigType[ConfigType.STRING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ConfigType[ConfigType.BOOLEAN.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            int[] iArr2 = new int[ConfigListType.values().length];
            $SwitchMap$com$xiaomi$xmpush$thrift$ConfigListType = iArr2;
            try {
                iArr2[ConfigListType.MISC_CONFIG.ordinal()] = 1;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$ConfigListType[ConfigListType.PLUGIN_CONFIG.ordinal()] = 2;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    private OnlineConfigHelper() {
    }

    private static List<Pair<Integer, Object>> convertMessage(List<OnlineConfigItem> list, boolean z2) {
        Pair pair;
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (OnlineConfigItem onlineConfigItem : list) {
            int key = onlineConfigItem.getKey();
            ConfigType configTypeFindByValue = ConfigType.findByValue(onlineConfigItem.getType());
            if (configTypeFindByValue != null) {
                if (z2 && onlineConfigItem.clear) {
                    arrayList.add(new Pair(Integer.valueOf(key), null));
                } else {
                    int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$xmpush$thrift$ConfigType[configTypeFindByValue.ordinal()];
                    if (i2 == 1) {
                        pair = new Pair(Integer.valueOf(key), Integer.valueOf(onlineConfigItem.getIntValue()));
                    } else if (i2 == 2) {
                        pair = new Pair(Integer.valueOf(key), Long.valueOf(onlineConfigItem.getLongValue()));
                    } else if (i2 != 3) {
                        pair = i2 != 4 ? null : new Pair(Integer.valueOf(key), Boolean.valueOf(onlineConfigItem.isBoolValue()));
                    } else {
                        pair = new Pair(Integer.valueOf(key), onlineConfigItem.getStringValue());
                    }
                    arrayList.add(pair);
                }
            }
        }
        return arrayList;
    }

    public static int getVersion(OnlineConfig onlineConfig, ConfigListType configListType) {
        return onlineConfig.getNormalVersion(configListType, AnonymousClass1.$SwitchMap$com$xiaomi$xmpush$thrift$ConfigListType[configListType.ordinal()] != 1 ? 0 : 1);
    }

    public static void updateCustomConfigs(OnlineConfig onlineConfig, XmPushActionCustomConfig xmPushActionCustomConfig) {
        MyLog.v(TAG, "-->updateCustomConfigs(): onlineConfig=", onlineConfig, ", configMessage=", xmPushActionCustomConfig);
        onlineConfig.updateCustomConfigs(convertMessage(xmPushActionCustomConfig.getCustomConfigs(), true));
        onlineConfig.runCallback();
    }

    public static void updateNormalConfigs(OnlineConfig onlineConfig, XmPushActionNormalConfig xmPushActionNormalConfig) {
        MyLog.v(TAG, "-->updateNormalConfigs(): onlineConfig=", onlineConfig, ", configMessage=", xmPushActionNormalConfig);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (NormalConfig normalConfig : xmPushActionNormalConfig.getNormalConfigs()) {
            arrayList.add(new Pair<>(normalConfig.getType(), Integer.valueOf(normalConfig.getVersion())));
            List<Pair<Integer, Object>> listConvertMessage = convertMessage(normalConfig.configItems, false);
            if (!CollectionUtils.isEmpty(listConvertMessage)) {
                arrayList2.addAll(listConvertMessage);
            }
        }
        onlineConfig.updateNormalConfigs(arrayList, arrayList2);
        onlineConfig.runCallback();
    }
}
