package com.xiaomi.push.service;

import android.content.SharedPreferences;
import android.util.Base64;
import com.google.protobuf.micro.CodedInputStreamMicro;
import com.google.protobuf.micro.CodedOutputStreamMicro;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor;
import com.xiaomi.network.HttpUtils;
import com.xiaomi.push.protobuf.ChannelConfig;
import com.xiaomi.push.protobuf.ChannelMessage;
import com.xiaomi.smack.util.TaskExecutor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ServiceConfig {
    private static final String CLOUDCONFIG = "XMCloudCfg";
    private static final String CONFIG_URL = "https://resolver.msg.xiaomi.net/psc/?t=a";
    private static final String PREF_NAME = "XMPushServiceConfig";
    private static final String PREF_UUID = "DeviceUUID";
    private static String sDeviceUUID;
    private static ServiceConfig sInstance = new ServiceConfig();
    private ChannelConfig.PushServiceConfig mConfig;
    private List<Listener> mListener = new ArrayList();
    private SerializedAsyncTaskProcessor.SerializedAsyncTask mPendingFetchTask;

    private ServiceConfig() {
    }

    private void checkLoad() throws Throwable {
        if (this.mConfig == null) {
            load();
        }
    }

    private void fetchConfig() {
        if (this.mPendingFetchTask != null) {
            return;
        }
        SerializedAsyncTaskProcessor.SerializedAsyncTask serializedAsyncTask = new SerializedAsyncTaskProcessor.SerializedAsyncTask() { // from class: com.xiaomi.push.service.ServiceConfig.1
            boolean success = false;

            @Override // com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
            public void postProcess() {
                Listener[] listenerArr;
                ServiceConfig.this.mPendingFetchTask = null;
                if (this.success) {
                    synchronized (ServiceConfig.this) {
                        listenerArr = (Listener[]) ServiceConfig.this.mListener.toArray(new Listener[ServiceConfig.this.mListener.size()]);
                    }
                    for (Listener listener : listenerArr) {
                        listener.onConfigChange(ServiceConfig.this.mConfig);
                    }
                }
            }

            @Override // com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
            public void process() {
                try {
                    ChannelConfig.PushServiceConfig from = ChannelConfig.PushServiceConfig.parseFrom(Base64.decode(HttpUtils.get(SystemUtils.getContext(), ServiceConfig.CONFIG_URL, null), 10));
                    if (from != null) {
                        ServiceConfig.this.mConfig = from;
                        this.success = true;
                        ServiceConfig.this.save();
                    }
                } catch (Exception e2) {
                    MyLog.w("fetch config failure: " + e2.getMessage());
                }
            }
        };
        this.mPendingFetchTask = serializedAsyncTask;
        TaskExecutor.execute(serializedAsyncTask);
    }

    public static synchronized String getDeviceUUID() {
        try {
            if (sDeviceUUID == null) {
                SharedPreferences sharedPreferences = SystemUtils.getContext().getSharedPreferences(PREF_NAME, 0);
                String string = sharedPreferences.getString(PREF_UUID, null);
                sDeviceUUID = string;
                if (string == null) {
                    String deviceId = DeviceInfo.getDeviceId(SystemUtils.getContext(), false);
                    sDeviceUUID = deviceId;
                    if (deviceId != null) {
                        sharedPreferences.edit().putString(PREF_UUID, sDeviceUUID).commit();
                    }
                }
            }
        } catch (Throwable th) {
            throw th;
        }
        return sDeviceUUID;
    }

    public static ServiceConfig getInstance() {
        return sInstance;
    }

    private void load() throws Throwable {
        BufferedInputStream bufferedInputStream;
        Exception e2;
        BufferedInputStream bufferedInputStream2 = null;
        try {
            try {
                bufferedInputStream = new BufferedInputStream(SystemUtils.getContext().openFileInput(CLOUDCONFIG));
                try {
                    this.mConfig = ChannelConfig.PushServiceConfig.parseFrom(CodedInputStreamMicro.newInstance(bufferedInputStream));
                    bufferedInputStream.close();
                } catch (Exception e3) {
                    e2 = e3;
                    MyLog.w("load config failure: " + e2.getMessage());
                }
            } catch (Throwable th) {
                th = th;
                bufferedInputStream2 = bufferedInputStream;
                IOUtils.closeQuietly(bufferedInputStream2);
                throw th;
            }
        } catch (Exception e4) {
            bufferedInputStream = null;
            e2 = e4;
        } catch (Throwable th2) {
            th = th2;
            IOUtils.closeQuietly(bufferedInputStream2);
            throw th;
        }
        IOUtils.closeQuietly(bufferedInputStream);
        if (this.mConfig == null) {
            this.mConfig = new ChannelConfig.PushServiceConfig();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void save() {
        try {
            if (this.mConfig != null) {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(SystemUtils.getContext().openFileOutput(CLOUDCONFIG, 0));
                CodedOutputStreamMicro codedOutputStreamMicroNewInstance = CodedOutputStreamMicro.newInstance(bufferedOutputStream);
                this.mConfig.writeTo(codedOutputStreamMicroNewInstance);
                codedOutputStreamMicroNewInstance.flush();
                bufferedOutputStream.close();
            }
        } catch (Exception e2) {
            MyLog.w("save config failure: " + e2.getMessage());
        }
    }

    public synchronized void addListener(Listener listener) {
        this.mListener.add(listener);
    }

    synchronized void clear() {
        this.mListener.clear();
    }

    public boolean getBoolSetting(String str, boolean z2) {
        return SystemUtils.getContext().getSharedPreferences(PREF_NAME, 0).getBoolean(str, z2);
    }

    public ChannelConfig.PushServiceConfig getConfig() throws Throwable {
        checkLoad();
        return this.mConfig;
    }

    int getConfigVersion() throws Throwable {
        checkLoad();
        ChannelConfig.PushServiceConfig pushServiceConfig = this.mConfig;
        if (pushServiceConfig != null) {
            return pushServiceConfig.getConfigVersion();
        }
        return 0;
    }

    void handle(ChannelMessage.PushServiceConfigMsg pushServiceConfigMsg) {
        Listener[] listenerArr;
        if (pushServiceConfigMsg.hasCloudVersion() && pushServiceConfigMsg.getCloudVersion() > getConfigVersion()) {
            fetchConfig();
        }
        synchronized (this) {
            List<Listener> list = this.mListener;
            listenerArr = (Listener[]) list.toArray(new Listener[list.size()]);
        }
        for (Listener listener : listenerArr) {
            listener.onConfigMsgReceive(pushServiceConfigMsg);
        }
    }

    public synchronized void removeListener(Listener listener) {
        this.mListener.remove(listener);
    }

    public void setSetting(String str, boolean z2) {
        SystemUtils.getContext().getSharedPreferences(PREF_NAME, 0).edit().putBoolean(str, z2).commit();
    }

    public static abstract class Listener {
        public void onConfigChange(ChannelConfig.PushServiceConfig pushServiceConfig) {
        }

        public void onConfigMsgReceive(ChannelMessage.PushServiceConfigMsg pushServiceConfigMsg) {
        }
    }
}
