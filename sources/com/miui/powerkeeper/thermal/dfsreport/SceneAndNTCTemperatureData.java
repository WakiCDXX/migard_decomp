package com.miui.powerkeeper.thermal.dfsreport;

import android.util.Log;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.thermal.dfscheck.CheckUtils;
import com.miui.powerkeeper.thermal.dfscheck.CollectionData;
import com.miui.powerkeeper.utils.AssetUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* JADX INFO: loaded from: classes.dex */
public class SceneAndNTCTemperatureData {
    public static final int DEFAULT_NTC_LOWEST_TEMP = -30000;
    private static final int DEFAULT_NTC_TEMP = 95000;
    private static final int DEFAULT_SCENE_TEMP = 48000;
    private static final String DFS_SCENE_SRC = "odm/etc/scene_temp.xml";
    private static final String TAG = "powerkeeper.dfsscene";
    private static SceneAndNTCTemperatureData mSceneAndNTCTemperatureDataInstance;
    private CollectionData mCollectionData;
    private CheckUtils mCheckUtils = CheckUtils.getCheckUtilsInstance();
    private HashMap<String, Integer> mSceneTempMap = new HashMap<>();
    private HashMap<String, Integer> mNtcTempMap = new HashMap<>();
    private int mDefaultNtcTemp = DEFAULT_NTC_TEMP;
    private int mDefaultSceneTemp = DEFAULT_SCENE_TEMP;

    private SceneAndNTCTemperatureData() {
        this.mCollectionData = null;
        this.mCollectionData = CollectionData.getCollectionDataInstance();
    }

    public static synchronized SceneAndNTCTemperatureData getSceneTemperatureDataInstance() {
        try {
            if (mSceneAndNTCTemperatureDataInstance == null) {
                mSceneAndNTCTemperatureDataInstance = new SceneAndNTCTemperatureData();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mSceneAndNTCTemperatureDataInstance;
    }

    public void clearSceneTempMap() {
        HashMap<String, Integer> map = this.mSceneTempMap;
        if (map != null) {
            synchronized (map) {
                this.mSceneTempMap.clear();
            }
        }
    }

    public int getCurNtcLimitTemp(String str) {
        HashMap<String, Integer> map = this.mNtcTempMap;
        if (map != null && map.containsKey(str)) {
            return this.mNtcTempMap.get(str).intValue();
        }
        Log.d(TAG, "get " + str + " is null return " + this.mDefaultNtcTemp);
        return this.mDefaultNtcTemp;
    }

    public int getCurSceneLimitTemp(String str) {
        HashMap<String, Integer> map = this.mSceneTempMap;
        if (map != null && map.get(str) != null) {
            return this.mSceneTempMap.get(str).intValue();
        }
        Log.d(TAG, "get " + str + " is null return " + this.mDefaultSceneTemp);
        return this.mDefaultSceneTemp;
    }

    public int getDefaultNtcTemp() {
        return this.mDefaultNtcTemp;
    }

    public int getDefaultSceneTemp() {
        return this.mDefaultSceneTemp;
    }

    public HashMap<String, Integer> getSceneTempMap() {
        return this.mSceneTempMap;
    }

    public void parseSceneTempToMap() {
        InputStream inputStream;
        File file = new File(DFS_SCENE_SRC);
        try {
            if (file.exists()) {
                inputStream = Files.newInputStream(file.toPath(), new OpenOption[0]);
                this.mCollectionData.setIsOdmXml(true);
            } else {
                inputStream = AssetUtils.getInputStream(PowerKeeperApplication.b(), "scene_temp.xml");
                this.mCollectionData.setIsOdmXml(false);
            }
            if (inputStream != null) {
                xmltoPullParser(inputStream);
            }
        } catch (IOException unused) {
            Log.e(TAG, "xml getInputStream is fail");
        }
    }

    public void printMapInDump() {
        String str = "";
        String str2 = "";
        for (Map.Entry<String, Integer> entry : this.mSceneTempMap.entrySet()) {
            Log.d(TAG, " get scene key " + entry.getKey() + " get value " + entry.getValue());
            str2 = str2 + entry.getKey() + ":" + entry.getValue() + " ";
        }
        CollectionData.getCollectionDataInstance().setDumpMap("sceneMap", str2);
        for (Map.Entry<String, Integer> entry2 : this.mNtcTempMap.entrySet()) {
            str = str + entry2.getKey() + ":" + entry2.getValue() + " ";
        }
        CollectionData.getCollectionDataInstance().setDumpMap("ntcMap", str);
    }

    public void setNtcTempMap(String str, int i2) {
        HashMap<String, Integer> map = this.mNtcTempMap;
        if (map != null) {
            synchronized (map) {
                this.mNtcTempMap.put(str, Integer.valueOf(i2));
            }
        }
    }

    public void setSceneTempMap(String str, int i2) {
        HashMap<String, Integer> map = this.mSceneTempMap;
        if (map != null) {
            synchronized (map) {
                this.mSceneTempMap.put(str, Integer.valueOf(i2));
            }
        }
    }

    public void xmltoPullParser(InputStream inputStream) {
        try {
            XmlPullParser xmlPullParserNewPullParser = XmlPullParserFactory.newInstance().newPullParser();
            String attributeValue = null;
            xmlPullParserNewPullParser.setInput(inputStream, null);
            for (int eventType = xmlPullParserNewPullParser.getEventType(); eventType != 1; eventType = xmlPullParserNewPullParser.next()) {
                String name = xmlPullParserNewPullParser.getName();
                if (eventType == 2) {
                    if (name.equals("sceneid")) {
                        attributeValue = xmlPullParserNewPullParser.getAttributeValue(0);
                    } else if (name.equals("name")) {
                        String strNextText = xmlPullParserNewPullParser.nextText();
                        if (attributeValue != null && strNextText != null) {
                            this.mCollectionData.putScenarioMap(this.mCheckUtils.transToInteger(TAG, attributeValue).intValue(), strNextText);
                            Log.d(TAG, "put id is " + attributeValue + " name is " + strNextText);
                        }
                    } else if (name.equals("temp")) {
                        Integer numTransToInteger = this.mCheckUtils.transToInteger(TAG, xmlPullParserNewPullParser.nextText());
                        int iIntValue = numTransToInteger.intValue();
                        if (attributeValue != null) {
                            Log.d(TAG, "put id is " + attributeValue + " value is " + iIntValue);
                            this.mSceneTempMap.put(attributeValue, numTransToInteger);
                            if (attributeValue.equals("-1")) {
                                this.mDefaultSceneTemp = iIntValue;
                                Log.d(TAG, "default scene temp is " + iIntValue);
                            }
                        }
                    } else if (name.equals("ntc")) {
                        String attributeValue2 = xmlPullParserNewPullParser.getAttributeValue(0);
                        int iIntValue2 = this.mCheckUtils.transToInteger(TAG, xmlPullParserNewPullParser.getAttributeValue(1)).intValue();
                        if (!attributeValue2.equals("defult") || iIntValue2 <= 40) {
                            setNtcTempMap(attributeValue2, iIntValue2);
                        } else {
                            this.mDefaultNtcTemp = iIntValue2;
                        }
                        Log.d(TAG, "ntc:" + attributeValue2 + " temp:" + iIntValue2);
                    }
                }
            }
        } catch (FileNotFoundException unused) {
            Log.e(TAG, "can not find xml file");
        } catch (IOException unused2) {
            Log.e(TAG, "can not find xml next");
        } catch (XmlPullParserException unused3) {
            Log.e(TAG, "Xmlparse get fail");
        }
    }
}
