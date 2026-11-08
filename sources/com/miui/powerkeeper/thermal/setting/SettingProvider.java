package com.miui.powerkeeper.thermal.setting;

import android.os.Handler;
import com.android.internal.os.BackgroundThread;
import com.google.gson.Gson;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.utils.AssetUtils;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import com.miui.powerkeeper.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: classes.dex */
public class SettingProvider implements IProvider<SettingDTO> {
    private static final String ASSETS_FILE;
    private static final String CLOUD_SETTING_FILE = "/data/vendor/thermal/config/setting.xml";
    private static final String SETTING_FILE = "/vendor/odm/etc/setting.xml";
    private static final String SP_DTO = "setting_dto";
    private static final String SP_MD5 = "setting_md5";
    private static final IProvider<SettingDTO> sInstance;
    private final Set<IProviderCallback<SettingDTO>> mCallbacks = new HashSet(2);

    /* JADX INFO: Access modifiers changed from: private */
    static class SettingProxy extends AutoRegister implements IProvider<SettingDTO> {
        private final Handler mBackgroundHandler;
        private final SettingProvider mProvider;

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$registerCallback$0(IProviderCallback iProviderCallback) {
            this.mProvider.registerCallback(iProviderCallback);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$unregisterCallback$1(IProviderCallback iProviderCallback) {
            this.mProvider.unregisterCallback(iProviderCallback);
        }

        @Override // com.miui.powerkeeper.cloudcontrol.AutoRegister
        protected int getModel() {
            return b.b.A;
        }

        @Override // com.miui.powerkeeper.thermal.setting.IProvider
        public void registerCallback(final IProviderCallback<SettingDTO> iProviderCallback) {
            this.mBackgroundHandler.post(new Runnable() { // from class: com.miui.powerkeeper.thermal.setting.d
                @Override // java.lang.Runnable
                public final void run() {
                    this.f585a.lambda$registerCallback$0(iProviderCallback);
                }
            });
        }

        @Override // com.miui.powerkeeper.thermal.setting.IProvider
        public void unregisterCallback(final IProviderCallback<SettingDTO> iProviderCallback) {
            this.mBackgroundHandler.post(new Runnable() { // from class: com.miui.powerkeeper.thermal.setting.c
                @Override // java.lang.Runnable
                public final void run() {
                    this.f583a.lambda$unregisterCallback$1(iProviderCallback);
                }
            });
        }

        @Override // com.miui.powerkeeper.cloudcontrol.IUseCloud
        public void updateCloudData() {
            Handler handler = this.mBackgroundHandler;
            final SettingProvider settingProvider = this.mProvider;
            Objects.requireNonNull(settingProvider);
            handler.post(new Runnable() { // from class: com.miui.powerkeeper.thermal.setting.e
                @Override // java.lang.Runnable
                public final void run() {
                    settingProvider.onNewSettingReady();
                }
            });
        }

        private SettingProxy() {
            this.mProvider = new SettingProvider();
            this.mBackgroundHandler = BackgroundThread.getHandler();
        }
    }

    private static class SettingReader extends DefaultHandler {
        private IParser mParser;
        private final SettingDTO mSettingDTO;

        private InputStream createInputStream() throws IOException {
            File file = new File(SettingProvider.CLOUD_SETTING_FILE);
            if (!file.exists()) {
                file = new File(SettingProvider.SETTING_FILE);
            }
            return !file.exists() ? AssetUtils.getInputStream(PowerKeeperApplication.b(), SettingProvider.ASSETS_FILE) : new FileInputStream(file);
        }

        public SettingDTO read() {
            try {
                InputStream inputStreamCreateInputStream = createInputStream();
                try {
                    SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
                    sAXParserFactoryNewInstance.setFeature("http://xml.org/sax/features/external-general-entities", false);
                    sAXParserFactoryNewInstance.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                    sAXParserFactoryNewInstance.newSAXParser().parse(inputStreamCreateInputStream, this);
                    try {
                        inputStreamCreateInputStream.close();
                        return this.mSettingDTO;
                    } catch (IOException e2) {
                        throw new RuntimeException(e2);
                    }
                } catch (IOException | ParserConfigurationException | SAXException e3) {
                    throw new RuntimeException(e3);
                }
            } catch (IOException e4) {
                throw new RuntimeException(e4);
            }
        }

        /* JADX WARN: Code duplicated, block: B:18:0x0034  */
        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
            byte b2;
            int iHashCode = str3.hashCode();
            if (iHashCode != -8339209) {
                if (iHashCode != 422200865) {
                    if (iHashCode == 1726545635 && str3.equals("scenarios")) {
                        b2 = 1;
                    } else {
                        b2 = -1;
                    }
                } else if (str3.equals("processors")) {
                    b2 = 2;
                } else {
                    b2 = -1;
                }
            } else if (str3.equals("elements")) {
                b2 = 0;
            } else {
                b2 = -1;
            }
            if (b2 == 0) {
                this.mParser = new IParser.ElementsParser();
            } else if (b2 == 1) {
                this.mParser = new IParser.ScenariosParser();
            } else if (b2 == 2) {
                this.mParser = new IParser.ProcessorsParser();
            }
            this.mParser.parser(str3, attributes, this.mSettingDTO);
        }

        private SettingReader() {
            this.mSettingDTO = new SettingDTO();
            this.mParser = new IParser.NullParser();
        }
    }

    static {
        ASSETS_FILE = ScenarioConfig.NEW_DEVICES ? "setting.xml" : "setting-old.xml";
        sInstance = new SettingProxy();
    }

    private boolean checkAndSave(SettingDTO settingDTO) {
        SharedPrefUtil sharedPrefUtil = SharedPrefUtil.getInstance(PowerKeeperApplication.b(), "Thermal");
        String strMD5 = Utils.MD5(settingDTO.toString());
        if (sharedPrefUtil.getStringValue(SP_MD5, "").equals(strMD5)) {
            return false;
        }
        sharedPrefUtil.save(SP_MD5, strMD5);
        sharedPrefUtil.save(SP_DTO, new Gson().toJson(settingDTO));
        return true;
    }

    public static synchronized IProvider<SettingDTO> getInstance() {
        return sInstance;
    }

    private void notifyOnRegister(IProviderCallback<SettingDTO> iProviderCallback) {
        String stringValue = SharedPrefUtil.getInstance(PowerKeeperApplication.b(), "Thermal").getStringValue(SP_DTO, "null");
        if (stringValue.equals("null")) {
            onNewSettingReady();
            return;
        }
        SettingDTO settingDTO = (SettingDTO) new Gson().fromJson(stringValue, SettingDTO.class);
        if (settingDTO == null) {
            throw new RuntimeException("error in read SettingDTO");
        }
        iProviderCallback.onChange(settingDTO);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewSettingReady() {
        final SettingDTO file = readFile();
        if (file != null) {
            this.mCallbacks.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermal.setting.b
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((IProviderCallback) obj).onChange(file);
                }
            });
        }
    }

    private SettingDTO readFile() {
        SettingDTO settingDTO = new SettingReader().read();
        if (checkAndSave(settingDTO)) {
            return settingDTO;
        }
        return null;
    }

    @Override // com.miui.powerkeeper.thermal.setting.IProvider
    public void registerCallback(IProviderCallback<SettingDTO> iProviderCallback) {
        this.mCallbacks.add(iProviderCallback);
        notifyOnRegister(iProviderCallback);
    }

    @Override // com.miui.powerkeeper.thermal.setting.IProvider
    public void unregisterCallback(IProviderCallback<SettingDTO> iProviderCallback) {
        this.mCallbacks.remove(iProviderCallback);
    }
}
