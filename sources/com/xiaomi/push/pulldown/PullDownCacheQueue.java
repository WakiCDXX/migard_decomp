package com.xiaomi.push.pulldown;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.extension.QueuedWork;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
class PullDownCacheQueue implements PullDownTask.OnTaskChecked {
    private static final String DIR_NAME = "pullDownFileData";
    private static final String FILE_NAME_FORMAT = "%s_%s_%s_%s";
    private static final int MAX_SIZE_DATAS_BYTE = 1048576;
    private static DataMonitor monitor;
    private FileCache mFileCache;
    private MemoryCache mMemoryCache = new MemoryCache();

    public class DataMonitor {
        private Handler mHandler = QueuedWork.newHandler(null);

        DataMonitor() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void broadcast(String str) throws Throwable {
            PullDownLog.w("DataMonitor broadcast container");
            byte[] bArr = PullDownCacheQueue.this.mFileCache.get(str);
            if (bArr == null) {
                PullDownLog.w("PullDownCacheQueue DataMonitor datas is null");
                return;
            }
            XmPushActionContainer xmPushActionContainer = new XmPushActionContainer();
            try {
                XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionContainer, bArr);
            } catch (TException e2) {
                PullDownLog.w("PullDownCacheQueue DataMonitor broadcast error : " + e2);
            }
            PullDownTask.onTaskExpired(xmPushActionContainer);
        }

        void cancelMonitor(String str) {
            PullDownLog.w("DataMonitor cancel container");
            this.mHandler.removeCallbacksAndMessages(str);
        }

        public void checkDataExpired(String str) throws Throwable {
            String[] strArrSplit = str.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
            long j2 = ToolUtils.parseLong(strArrSplit[1], 0L);
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (jCurrentTimeMillis < j2) {
                PullDownLog.w("PullDownCacheQueue DataMonitor checkExpired time be changed");
                broadcast(str);
            } else {
                if (strArrSplit.length <= 3 || jCurrentTimeMillis < ToolUtils.parseLong(strArrSplit[1], 0L) + ((long) ToolUtils.parseInt(strArrSplit[2], 0))) {
                    return;
                }
                PullDownLog.w("PullDownCacheQueue DataMonitor checkExpired timeout");
                broadcast(str);
            }
        }

        public void resetMonitor(final String str) throws Throwable {
            String[] strArrSplit = str.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
            long j2 = ToolUtils.parseLong(strArrSplit[1], 0L);
            int i2 = ToolUtils.parseInt(strArrSplit[2], 0);
            if (System.currentTimeMillis() < j2) {
                PullDownLog.w("PullDownCacheQueue DataMonitor resetMonitor time has been changed");
                broadcast(str);
                return;
            }
            long jCurrentTimeMillis = (j2 + ((long) i2)) - System.currentTimeMillis();
            if (jCurrentTimeMillis > 0) {
                this.mHandler.postDelayed(new Runnable() { // from class: com.xiaomi.push.pulldown.PullDownCacheQueue.DataMonitor.2
                    @Override // java.lang.Runnable
                    public void run() throws Throwable {
                        PullDownLog.w("PullDownCacheQueue DataMonitor resetMonitor time normal");
                        DataMonitor.this.broadcast(str);
                    }
                }, str, jCurrentTimeMillis);
            } else {
                PullDownLog.w("PullDownCacheQueue DataMonitor resetMonitor time out");
                broadcast(str);
            }
        }

        public void startMonitor(final String str) throws Throwable {
            int i2 = ToolUtils.parseInt(str.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT)[2], 0);
            if (i2 > 0) {
                this.mHandler.postDelayed(new Runnable() { // from class: com.xiaomi.push.pulldown.PullDownCacheQueue.DataMonitor.1
                    @Override // java.lang.Runnable
                    public void run() throws Throwable {
                        PullDownLog.w("PullDownCacheQueue DataMonitor startMonitor time normal");
                        DataMonitor.this.broadcast(str);
                    }
                }, str, i2);
            } else {
                PullDownLog.w("PullDownCacheQueue DataMonitor startMonitor time out");
                broadcast(str);
            }
        }
    }

    public static class FileCache {
        private File mParent;

        FileCache(Context context) {
            this.mParent = new File(context.getFilesDir(), PullDownCacheQueue.DIR_NAME);
        }

        private File getFileById(String str) {
            return new File(this.mParent, str);
        }

        boolean add(String str, XmPushActionContainer xmPushActionContainer) throws Throwable {
            byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(xmPushActionContainer);
            if (bArrConvertThriftObjectToBytes.length < 1048576) {
                FileOutputStream fileOutputStream = null;
                try {
                    try {
                        File file = this.mParent;
                        if (!file.isDirectory()) {
                            file.delete();
                            file.mkdirs();
                        }
                        FileOutputStream fileOutputStream2 = new FileOutputStream(getFileById(str));
                        try {
                            fileOutputStream2.write(bArrConvertThriftObjectToBytes);
                            fileOutputStream2.flush();
                            IOUtils.closeQuietly(fileOutputStream2);
                            return true;
                        } catch (Exception e2) {
                            e = e2;
                            fileOutputStream = fileOutputStream2;
                            remove(str);
                            PullDownLog.w("FileCache add " + str + " error:" + e);
                            IOUtils.closeQuietly(fileOutputStream);
                            return false;
                        } catch (Throwable th) {
                            th = th;
                            fileOutputStream = fileOutputStream2;
                            IOUtils.closeQuietly(fileOutputStream);
                            throw th;
                        }
                    } catch (Exception e3) {
                        e = e3;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            return false;
        }

        byte[] get(String str) throws Throwable {
            ByteArrayOutputStream byteArrayOutputStream;
            FileInputStream fileInputStream;
            byte[] byteArray;
            FileInputStream fileInputStream2 = null;
            try {
                File fileById = getFileById(str);
                if (fileById.length() < 1048576) {
                    fileInputStream = new FileInputStream(fileById);
                    try {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        try {
                            try {
                                byte[] bArr = new byte[4096];
                                while (true) {
                                    int i2 = fileInputStream.read(bArr);
                                    if (i2 == -1) {
                                        break;
                                    }
                                    byteArrayOutputStream.write(bArr, 0, i2);
                                    th = th;
                                    fileInputStream2 = fileInputStream;
                                    IOUtils.closeQuietly(fileInputStream2);
                                    IOUtils.closeQuietly(byteArrayOutputStream);
                                    throw th;
                                }
                                byteArrayOutputStream.flush();
                                byteArray = byteArrayOutputStream.toByteArray();
                                fileInputStream2 = fileInputStream;
                            } catch (Exception e2) {
                                e = e2;
                                PullDownLog.w("FileCache get" + str + "error:" + e);
                                IOUtils.closeQuietly(fileInputStream);
                                IOUtils.closeQuietly(byteArrayOutputStream);
                                return null;
                            }
                        } catch (Throwable th) {
                            th = th;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        byteArrayOutputStream = null;
                    } catch (Throwable th2) {
                        th = th2;
                        byteArrayOutputStream = null;
                    }
                } else {
                    PullDownLog.w("FileCache get" + str + "too max");
                    byteArrayOutputStream = null;
                    byteArray = null;
                }
                IOUtils.closeQuietly(fileInputStream2);
                IOUtils.closeQuietly(byteArrayOutputStream);
                return byteArray;
            } catch (Exception e4) {
                e = e4;
                byteArrayOutputStream = null;
                fileInputStream = null;
            } catch (Throwable th3) {
                th = th3;
                byteArrayOutputStream = null;
            }
        }

        ArrayList<String> getAllFileNames() {
            File file = this.mParent;
            ArrayList<String> arrayList = new ArrayList<>();
            try {
                File[] fileArrListFiles = file.listFiles();
                if (fileArrListFiles != null) {
                    for (File file2 : fileArrListFiles) {
                        arrayList.add(file2.getName());
                    }
                }
            } catch (Exception e2) {
                PullDownLog.w("FileCache getAllerror:" + e2);
            }
            return arrayList;
        }

        boolean remove(String str) {
            boolean zDelete = false;
            try {
                File fileById = getFileById(str);
                if (fileById.exists()) {
                    zDelete = fileById.delete();
                } else {
                    PullDownLog.w("PullDownCacheQueue FileCache file not exists");
                }
            } catch (Exception e2) {
                PullDownLog.e("FileCache " + str + "delete failed error: " + e2);
            }
            return zDelete;
        }
    }

    public static class MemoryCache {
        private List<PullDownData> mIndexes = new ArrayList();
        private boolean mSorted = false;

        MemoryCache() {
        }

        boolean add(String str) {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            this.mSorted = false;
            return this.mIndexes.add(PullDownCacheQueue.getBeanByDataId(str));
        }

        String findDataId(String str) {
            if (ToolUtils.isEmptyList(this.mIndexes)) {
                return null;
            }
            for (int i2 = 0; i2 < this.mIndexes.size(); i2++) {
                if (this.mIndexes.get(i2).msgId.equals(str)) {
                    return this.mIndexes.get(i2).dataId;
                }
            }
            return null;
        }

        void remove(String str) {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            for (int i2 = 0; i2 < this.mIndexes.size(); i2++) {
                if (TextUtils.equals(str, this.mIndexes.get(i2).dataId)) {
                    this.mIndexes.remove(i2);
                }
            }
        }

        public void sort() {
            List<PullDownData> list = this.mIndexes;
            if (list == null || this.mSorted) {
                return;
            }
            Collections.sort(list, new Comparator<PullDownData>() { // from class: com.xiaomi.push.pulldown.PullDownCacheQueue.MemoryCache.1
                @Override // java.util.Comparator
                public int compare(PullDownData pullDownData, PullDownData pullDownData2) {
                    return (int) (pullDownData.createTime - pullDownData2.createTime);
                }
            });
            this.mSorted = true;
        }
    }

    public PullDownCacheQueue(Context context) {
        this.mFileCache = new FileCache(context);
        monitor = new DataMonitor();
        PullDownTask.setsOnTaskChecked(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PullDownData getBeanByDataId(String str) {
        String[] strArrSplit = str.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
        return new PullDownData(str, strArrSplit[0], ToolUtils.parseLong(strArrSplit[1], 0L), ToolUtils.parseInt(strArrSplit[2], 0), strArrSplit[3]);
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0025  */
    private static String getIdByParseContainer(XmPushActionContainer xmPushActionContainer, long j2) {
        String id;
        String str;
        String packageName;
        if (xmPushActionContainer.getMetaInfo() != null) {
            PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
            if (metaInfo.getExtra() != null) {
                str = metaInfo.getExtra().get(PluginConstants.PULL_DOWN_CACHE_TIME);
                id = metaInfo.getId();
                packageName = xmPushActionContainer.getPackageName();
            } else {
                id = "no_msgId";
                str = "0";
                packageName = PushConstants.PUSH_SERVICE_PACKAGE_NAME;
            }
        } else {
            id = "no_msgId";
            str = "0";
            packageName = PushConstants.PUSH_SERVICE_PACKAGE_NAME;
        }
        return String.format(FILE_NAME_FORMAT, id, Long.valueOf(j2), str, packageName);
    }

    boolean add(XmPushActionContainer xmPushActionContainer, long j2) throws Throwable {
        String idByParseContainer = getIdByParseContainer(xmPushActionContainer, j2);
        if (!this.mFileCache.add(idByParseContainer, xmPushActionContainer)) {
            PullDownLog.w("PullDownCacheQueue add failed");
            return false;
        }
        monitor.startMonitor(idByParseContainer);
        this.mMemoryCache.add(idByParseContainer);
        PullDownLog.w("PullDownCacheQueue add success dataId is : " + idByParseContainer);
        return true;
    }

    List<XmPushActionContainer> getAll() throws Throwable {
        ArrayList arrayList = new ArrayList();
        if (!ToolUtils.isEmptyList(this.mMemoryCache.mIndexes)) {
            this.mMemoryCache.sort();
            for (int i2 = 0; i2 < this.mMemoryCache.mIndexes.size(); i2++) {
                byte[] bArr = this.mFileCache.get(((PullDownData) this.mMemoryCache.mIndexes.get(i2)).dataId);
                XmPushActionContainer xmPushActionContainer = new XmPushActionContainer();
                try {
                    XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionContainer, bArr);
                    arrayList.add(xmPushActionContainer);
                } catch (TException e2) {
                    PullDownLog.w("PullDownCacheQueue getAll error : " + e2);
                }
            }
        }
        return arrayList;
    }

    List<String> getAllDataId() {
        ArrayList arrayList = new ArrayList();
        if (!ToolUtils.isEmptyList(this.mMemoryCache.mIndexes)) {
            this.mMemoryCache.sort();
            for (int i2 = 0; i2 < this.mMemoryCache.mIndexes.size(); i2++) {
                arrayList.add(((PullDownData) this.mMemoryCache.mIndexes.get(i2)).dataId);
            }
        }
        return arrayList;
    }

    int getSize() {
        return this.mMemoryCache.mIndexes.size();
    }

    void load() throws Throwable {
        ArrayList<String> allFileNames = this.mFileCache.getAllFileNames();
        if (ToolUtils.isEmptyList(allFileNames)) {
            return;
        }
        Iterator<String> it = allFileNames.iterator();
        while (it.hasNext()) {
            this.mMemoryCache.add(it.next());
        }
        List<String> allDataId = getAllDataId();
        for (int i2 = 0; i2 < allDataId.size(); i2++) {
            monitor.resetMonitor(allDataId.get(i2));
        }
    }

    @Override // com.xiaomi.push.pulldown.PullDownTask.OnTaskChecked
    public void onTaskCheckExpired() throws Throwable {
        List<String> allDataId = getAllDataId();
        if (ToolUtils.isEmptyList(allDataId)) {
            return;
        }
        for (int i2 = 0; i2 < allDataId.size(); i2++) {
            monitor.checkDataExpired(allDataId.get(i2));
        }
    }

    XmPushActionContainer peek() throws Throwable {
        if (ToolUtils.isEmptyList(this.mMemoryCache.mIndexes)) {
            return null;
        }
        this.mMemoryCache.sort();
        byte[] bArr = this.mFileCache.get(((PullDownData) this.mMemoryCache.mIndexes.get(0)).dataId);
        try {
            XmPushActionContainer xmPushActionContainer = new XmPushActionContainer();
            XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionContainer, bArr);
            return xmPushActionContainer;
        } catch (TException unused) {
            MyLog.w("PullDownCacheQueue peek is null");
            return null;
        }
    }

    void remove(XmPushActionContainer xmPushActionContainer) {
        if (xmPushActionContainer == null || xmPushActionContainer.getMetaInfo() == null) {
            return;
        }
        String strFindDataId = this.mMemoryCache.findDataId(xmPushActionContainer.getMetaInfo().getId());
        if (TextUtils.isEmpty(strFindDataId)) {
            return;
        }
        monitor.cancelMonitor(strFindDataId);
        if (!this.mFileCache.remove(strFindDataId)) {
            PullDownLog.w("PullDownCacheQueue remove failed");
            return;
        }
        this.mMemoryCache.remove(strFindDataId);
        PullDownLog.w("PullDownCacheQueue remove success dataId is : " + strFindDataId);
    }

    public static class PullDownData {
        private XmPushActionContainer container;
        private long createTime;
        private String dataId;
        private String msgId;
        private String pkgName;
        private int timeout;

        public PullDownData(String str, String str2, long j2, int i2, String str3) {
            this.dataId = str;
            this.msgId = str2;
            this.createTime = j2;
            this.timeout = i2;
            this.pkgName = str3;
            this.container = null;
        }

        public PullDownData(PullDownData pullDownData, XmPushActionContainer xmPushActionContainer) {
            this.dataId = pullDownData.dataId;
            this.msgId = pullDownData.msgId;
            this.createTime = pullDownData.createTime;
            this.timeout = pullDownData.timeout;
            this.pkgName = pullDownData.pkgName;
            this.container = xmPushActionContainer;
        }
    }
}
