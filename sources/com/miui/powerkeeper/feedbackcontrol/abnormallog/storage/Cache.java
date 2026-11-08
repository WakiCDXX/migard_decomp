package com.miui.powerkeeper.feedbackcontrol.abnormallog.storage;

import android.os.Build;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.storage.Cache;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.LogPrinter;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.PublicSpace;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.Tools;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

/* JADX INFO: loaded from: classes.dex */
public class Cache {
    private static volatile Cache mCache;
    private final String TAG = "LogCache";
    private final int LIST_MAX_CAPACITY = 500;
    private final LogPrinter mLog = LogPrinter.getLogPrinter();
    private StringBuilder mLogBuilder = new StringBuilder();
    private final PublicSpace mPublicSpace = PublicSpace.getInstance();
    private final List<String> mLogList = new ArrayList();
    private int mListSize = 0;

    private Cache() {
    }

    private void addForegroundList(final StringBuilder sb) {
        this.mLog.d("LogCache", "add foreground list");
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append("*************************LOG*************************");
        sb.append(ShellUtils.COMMAND_LINE_END);
        this.mPublicSpace.getForegroundList().forEach(new Consumer() { // from class: c.c
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Cache.lambda$addForegroundList$0(sb, (String) obj);
            }
        });
        sb.append(ShellUtils.COMMAND_LINE_END);
    }

    private void addLogList(final StringBuilder sb) {
        this.mLog.d("LogCache", "add log list");
        this.mLogList.forEach(new Consumer() { // from class: c.d
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Cache.lambda$addLogList$2(sb, (String) obj);
            }
        });
    }

    private void addSceneMap(final StringBuilder sb) {
        this.mLog.d("LogCache", "add scene map");
        final long jSum = this.mPublicSpace.getSceneMap().entrySet().stream().mapToLong(new ToLongFunction() { // from class: c.a
            @Override // java.util.function.ToLongFunction
            public final long applyAsLong(Object obj) {
                return ((Long) ((Map.Entry) obj).getValue()).longValue();
            }
        }).sum();
        this.mPublicSpace.getSceneMap().entrySet().forEach(new Consumer() { // from class: c.b
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Cache.lambda$addSceneMap$1(sb, jSum, (Map.Entry) obj);
            }
        });
        sb.append(ShellUtils.COMMAND_LINE_END);
    }

    private void createFile(StringBuilder sb) throws IOException {
        this.mLog.d("LogCache", "create file: " + sb.length());
        StringBuilder sb2 = new StringBuilder();
        Objects.requireNonNull(this.mPublicSpace);
        sb2.append("/data/vendor/thermal/thermalLog");
        sb2.append("/");
        sb2.append(Build.PRODUCT);
        sb2.append(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
        sb2.append(Build.VERSION.RELEASE);
        sb2.append(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
        sb2.append(Tools.getTimestampFile());
        String string = sb2.toString();
        FileUtil.createFile(string);
        Objects.requireNonNull(this.mPublicSpace);
        FileUtil.copyFile("/data/vendor/thermal/thermal.dump", string);
        FileUtil.appendFileString(string, sb.toString());
    }

    public static Cache getCache() {
        if (mCache == null) {
            mCache = new Cache();
        }
        return mCache;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$addForegroundList$0(StringBuilder sb, String str) {
        sb.append(str);
        sb.append(ShellUtils.COMMAND_LINE_END);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$addLogList$2(StringBuilder sb, String str) {
        sb.append(str);
        sb.append(ShellUtils.COMMAND_LINE_END);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$addSceneMap$1(StringBuilder sb, long j2, Map.Entry entry) {
        sb.append((String) entry.getKey());
        sb.append(": ");
        sb.append((((Long) entry.getValue()).longValue() * 100) / j2);
        sb.append("%");
        sb.append(ShellUtils.COMMAND_LINE_END);
    }

    private void saveLogFile() {
        this.mLog.d("LogCache", "save log file");
        StringBuilder sb = new StringBuilder();
        addForegroundList(sb);
        addSceneMap(sb);
        addLogList(sb);
        try {
            createFile(sb);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        this.mLogList.clear();
        this.mListSize = 0;
        this.mPublicSpace.getForegroundList().clear();
        this.mPublicSpace.getSceneMap().clear();
    }

    public void add() {
        if (this.mListSize > 500) {
            return;
        }
        this.mLog.d("LogCache", "add: " + ((Object) this.mLogBuilder));
        this.mLogList.add(this.mLogBuilder.toString());
        this.mListSize = this.mListSize + 1;
        this.mLogBuilder = new StringBuilder();
    }

    public void append(String str) {
        this.mLogBuilder.append(str + ShellUtils.COMMAND_LINE_END);
    }

    public void commit() {
        this.mLog.d("LogCache", "commit");
        saveLogFile();
    }

    public void insert(String str) {
        insert(str, 0);
    }

    public void insert(String str, int i2) {
        this.mLogBuilder.insert(i2, str);
    }
}
