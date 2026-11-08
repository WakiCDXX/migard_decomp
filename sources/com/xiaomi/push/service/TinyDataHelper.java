package com.xiaomi.push.service;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.push.service.clientReport.ReportConstants;
import com.xiaomi.xmpush.thrift.ClientUploadData;
import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/* JADX INFO: loaded from: classes.dex */
public class TinyDataHelper {
    public static final int DATA_MAX_SIZE = 30720;
    public static final String KEY_UPLOAD_WAY = "uploadWay";
    public static final String SDK_CHANNEL = "push_sdk_channel";
    public static final String XMSF_CHANNEL = "xmsf_channel";
    private static String dayPrefix;
    private static AtomicLong idGen = new AtomicLong(0);
    private static SimpleDateFormat sdf;

    static {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        sdf = simpleDateFormat;
        dayPrefix = simpleDateFormat.format(Long.valueOf(System.currentTimeMillis()));
    }

    public static void cacheTinyData(Context context, String str, String str2, long j2, String str3) {
        ClientUploadDataItem clientUploadDataItem = new ClientUploadDataItem();
        clientUploadDataItem.setCategory(str);
        clientUploadDataItem.setName(str2);
        clientUploadDataItem.setCounter(j2);
        clientUploadDataItem.setData(str3);
        clientUploadDataItem.setChannel("push_sdk_channel");
        clientUploadDataItem.setPkgName(context.getPackageName());
        clientUploadDataItem.setSourcePackage(context.getPackageName());
        clientUploadDataItem.setFromSdk(true);
        clientUploadDataItem.setTimestamp(System.currentTimeMillis());
        clientUploadDataItem.setId(nextTinyDataItemId());
        TinyDataStorage.cacheTinyData(context, clientUploadDataItem);
    }

    private static XmPushActionNotification generateUploadTinyDataNotification(String str, String str2, ClientUploadData clientUploadData) {
        return new XmPushActionNotification("-1", false).setPackageName(str).setAppId(str2).setBinaryExtra(IOUtils.gZip(XmPushThriftSerializeUtils.convertThriftObjectToBytes(clientUploadData))).setType(NotificationType.UploadTinyData.value);
    }

    public static synchronized String nextTinyDataItemId() {
        String str;
        try {
            str = sdf.format(Long.valueOf(System.currentTimeMillis()));
            if (!TextUtils.equals(dayPrefix, str)) {
                idGen.set(0L);
                dayPrefix = str;
            }
        } catch (Throwable th) {
            throw th;
        }
        return str + Constants.ACCEPT_TIME_SEPARATOR_SERVER + idGen.incrementAndGet();
    }

    public static ArrayList<XmPushActionNotification> pack(List<ClientUploadDataItem> list, String str, String str2, int i2) {
        int length;
        if (list == null) {
            MyLog.e("requests can not be null in TinyDataHelper.transToThriftObj().");
            return null;
        }
        if (list.size() == 0) {
            MyLog.e("requests.length is 0 in TinyDataHelper.transToThriftObj().");
            return null;
        }
        ArrayList<XmPushActionNotification> arrayList = new ArrayList<>();
        ClientUploadData clientUploadData = new ClientUploadData();
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            ClientUploadDataItem clientUploadDataItem = list.get(i4);
            if (clientUploadDataItem != null) {
                if (clientUploadDataItem.getExtra() == null || !clientUploadDataItem.getExtra().containsKey(ReportConstants.UPLOAD_DATA_ITEM_EXTRA_KEY_SIZE)) {
                    length = 0;
                } else {
                    String str3 = clientUploadDataItem.getExtra().get(ReportConstants.UPLOAD_DATA_ITEM_EXTRA_KEY_SIZE);
                    if (TextUtils.isEmpty(str3)) {
                        length = 0;
                    } else {
                        try {
                            length = Integer.parseInt(str3);
                        } catch (Exception unused) {
                            length = 0;
                        }
                    }
                    if (clientUploadDataItem.getExtra().size() == 1) {
                        clientUploadDataItem.setExtra(null);
                    } else {
                        clientUploadDataItem.getExtra().remove(ReportConstants.UPLOAD_DATA_ITEM_EXTRA_KEY_SIZE);
                    }
                }
                if (length <= 0) {
                    length = XmPushThriftSerializeUtils.convertThriftObjectToBytes(clientUploadDataItem).length;
                }
                if (length > i2) {
                    MyLog.e("TinyData is too big, ignore upload request item:" + clientUploadDataItem.getId());
                } else {
                    if (i3 + length > i2) {
                        arrayList.add(generateUploadTinyDataNotification(str, str2, clientUploadData));
                        clientUploadData = new ClientUploadData();
                        i3 = 0;
                    }
                    clientUploadData.addToUploadDataItems(clientUploadDataItem);
                    i3 += length;
                }
            }
        }
        if (clientUploadData.getUploadDataItemsSize() != 0) {
            arrayList.add(generateUploadTinyDataNotification(str, str2, clientUploadData));
        }
        return arrayList;
    }

    public static boolean shouldUpload(String str) {
        return !SystemUtils.isGlobalVersion() || "com.miui.hybrid".equals(str);
    }

    public static boolean verify(ClientUploadDataItem clientUploadDataItem, boolean z2) {
        if (clientUploadDataItem == null) {
            MyLog.w("item is null, verfiy ClientUploadDataItem failed.");
            return true;
        }
        if (!z2 && TextUtils.isEmpty(clientUploadDataItem.channel)) {
            MyLog.w("item.channel is null or empty, verfiy ClientUploadDataItem failed.");
            return true;
        }
        if (TextUtils.isEmpty(clientUploadDataItem.category)) {
            MyLog.w("item.category is null or empty, verfiy ClientUploadDataItem failed.");
            return true;
        }
        if (TextUtils.isEmpty(clientUploadDataItem.name)) {
            MyLog.w("item.name is null or empty, verfiy ClientUploadDataItem failed.");
            return true;
        }
        if (!XMStringUtils.checkAllAscii(clientUploadDataItem.category)) {
            MyLog.w("item.category can only contain ascii char, verfiy ClientUploadDataItem failed.");
            return true;
        }
        if (!XMStringUtils.checkAllAscii(clientUploadDataItem.name)) {
            MyLog.w("item.name can only contain ascii char, verfiy ClientUploadDataItem failed.");
            return true;
        }
        String str = clientUploadDataItem.data;
        if (str == null || str.length() <= 30720) {
            return false;
        }
        MyLog.w("item.data is too large(" + clientUploadDataItem.data.length() + "), max size for data is 30720 , verfiy ClientUploadDataItem failed.");
        return true;
    }
}
