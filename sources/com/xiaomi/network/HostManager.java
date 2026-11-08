package com.xiaomi.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.text.TextUtils;
import com.miui.powerkeeper.batterylife.TrackData;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channel.commonutils.android.DataCryptUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.BasicNameValuePair;
import com.xiaomi.channel.commonutils.network.NameValuePair;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.network.NetworkInfoWrapper;
import com.xiaomi.channel.commonutils.string.MD5;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.common.logger.thrift.mfs.HostInfo;
import com.xiaomi.common.logger.thrift.mfs.HttpApi;
import com.xiaomi.common.logger.thrift.mfs.LandNodeInfo;
import com.xiaomi.common.logger.thrift.mfs.Location;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.push.service.AppRegionStorage;
import com.xiaomi.push.service.PushConstants;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import miui.date.DateUtils;
import miui.yellowpage.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class HostManager {
    private static final String BUCKET_URL = "https://%1$s/gslb/?ver=5.0";
    private static final String HOST_FALLBACKS_ENCRYPT_KEY_SALT = "_key_salt";
    private static final String HOST_FALLBACKS_FILE_NAME = "host_fallbacks";
    public static final String RESOLVER_HOST = "resolver.msg.xiaomi.net";
    private static final String TAG = "HostManager";
    private static HostManagerFactory factory;
    protected static Context sAppContext;
    private static String sAppName;
    private static String sAppVersion;
    private static HostManager sInstance;
    private final long MAX_REQUEST_FAILURE_CNT;
    private String currentISP;
    private long lastRemoteRequestTimestamp;
    protected final Map<String, Fallbacks> mHostsMapping;
    private long remoteRequestFailureCount;
    private HostFilter sHostFilter;
    protected HttpGet sHttpGetter;
    private String sUserId;
    protected static final Map<String, Fallback> sReservedHosts = new HashMap();
    protected static boolean hostLoaded = false;

    public interface HostManagerFactory {
        HostManager createHostManager(Context context, HostFilter hostFilter, HttpGet httpGet, String str);
    }

    public interface HttpGet {
        String doGet(String str) throws IOException;
    }

    protected HostManager(Context context, HostFilter hostFilter, HttpGet httpGet, String str) {
        this(context, hostFilter, httpGet, str, null, null);
    }

    public static void addReservedHost(String str, String str2) {
        Map<String, Fallback> map = sReservedHosts;
        Fallback fallback = map.get(str);
        synchronized (map) {
            try {
                if (fallback == null) {
                    Fallback fallback2 = new Fallback(str);
                    fallback2.setEffectiveDuration(604800000L);
                    fallback2.addHost(str2);
                    map.put(str, fallback2);
                } else {
                    fallback.addHost(str2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    static String getActiveNetworkLabel() {
        if (sAppContext == null) {
            return "unknown";
        }
        try {
            NetworkInfoWrapper networkInfo = Network.getNetworkInfo();
            if (networkInfo == null) {
                return "unknown";
            }
            if (networkInfo.getType() == 1) {
                return "WIFI-UNKNOWN";
            }
            return networkInfo.getTypeName() + Constants.ACCEPT_TIME_SEPARATOR_SERVER + networkInfo.getSubtypeName();
        } catch (Throwable unused) {
            return "unknown";
        }
    }

    private byte[] getHostFallbacksEncryptKey() {
        return MD5.getMd5Digest(sAppContext.getPackageName() + HOST_FALLBACKS_ENCRYPT_KEY_SALT);
    }

    private String getHostFallbacksFileName() {
        return HOST_FALLBACKS_FILE_NAME;
    }

    public static synchronized HostManager getInstance() {
        HostManager hostManager;
        hostManager = sInstance;
        if (hostManager == null) {
            throw new IllegalStateException("the host manager is not initialized yet.");
        }
        return hostManager;
    }

    private String getVersionName() {
        try {
            PackageInfo packageInfo = sAppContext.getPackageManager().getPackageInfo(sAppContext.getPackageName(), DateUtils.FORMAT_ABBREV_TIME);
            return packageInfo != null ? packageInfo.versionName : "0";
        } catch (Exception unused) {
            return "0";
        }
    }

    public static synchronized void init(Context context, HostFilter hostFilter, HttpGet httpGet, String str) {
        init(context, hostFilter, httpGet, str, null, null);
    }

    static String obfuscate(String str) {
        try {
            int length = str.length();
            byte[] bytes = str.getBytes("UTF-8");
            for (int i2 = 0; i2 < bytes.length; i2++) {
                byte b2 = bytes[i2];
                int i3 = b2 & 240;
                if (i3 != 240) {
                    bytes[i2] = (byte) (((b2 & 15) ^ ((byte) (((b2 >> 4) + length) & 15))) | i3);
                }
            }
            return new String(bytes);
        } catch (UnsupportedEncodingException unused) {
            return str;
        }
    }

    private String processNetwork(String str) {
        if (TextUtils.isEmpty(str)) {
            return "unknown";
        }
        return str.startsWith("WIFI") ? "WIFI" : str;
    }

    private ArrayList<Fallback> requestRemoteFallbacks(ArrayList<String> arrayList) {
        JSONObject jSONObject;
        purge();
        synchronized (this.mHostsMapping) {
            try {
                checkHostMapping();
                for (String str : this.mHostsMapping.keySet()) {
                    if (!arrayList.contains(str)) {
                        arrayList.add(str);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        Map<String, Fallback> map = sReservedHosts;
        synchronized (map) {
            try {
                for (Object obj : map.values().toArray()) {
                    Fallback fallback = (Fallback) obj;
                    if (!fallback.isEffective()) {
                        sReservedHosts.remove(fallback.host);
                    }
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
        if (!arrayList.contains(getHost())) {
            arrayList.add(getHost());
        }
        ArrayList<Fallback> arrayList2 = new ArrayList<>(arrayList.size());
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            arrayList2.add(null);
        }
        try {
            String str2 = Network.isWIFIConnected(sAppContext) ? "wifi" : "wap";
            String remoteFallbackJSON = getRemoteFallbackJSON(arrayList, str2, this.sUserId, true);
            if (!TextUtils.isEmpty(remoteFallbackJSON)) {
                JSONObject jSONObject2 = new JSONObject(remoteFallbackJSON);
                MyLog.i(remoteFallbackJSON);
                if ("OK".equalsIgnoreCase(jSONObject2.getString("S"))) {
                    JSONObject jSONObject3 = jSONObject2.getJSONObject("R");
                    String string = jSONObject3.getString("province");
                    String string2 = jSONObject3.getString("city");
                    String string3 = jSONObject3.getString("isp");
                    String string4 = jSONObject3.getString("ip");
                    String string5 = jSONObject3.getString(Tag.TagSearch.GeoInfo.COUNTRY);
                    JSONObject jSONObject4 = jSONObject3.getJSONObject(str2);
                    MyLog.v("get bucket: net=" + string3 + ", hosts=" + jSONObject4.toString());
                    int i3 = 0;
                    while (i3 < arrayList.size()) {
                        String str3 = arrayList.get(i3);
                        JSONArray jSONArrayOptJSONArray = jSONObject4.optJSONArray(str3);
                        if (jSONArrayOptJSONArray == null) {
                            MyLog.w("no bucket found for " + str3);
                            jSONObject = jSONObject4;
                        } else {
                            Fallback fallback2 = new Fallback(str3);
                            int i4 = 0;
                            while (i4 < jSONArrayOptJSONArray.length()) {
                                String string6 = jSONArrayOptJSONArray.getString(i4);
                                if (!TextUtils.isEmpty(string6)) {
                                    fallback2.addHost(new WeightedHost(string6, jSONArrayOptJSONArray.length() - i4));
                                }
                                i4++;
                                jSONObject4 = jSONObject4;
                            }
                            jSONObject = jSONObject4;
                            arrayList2.set(i3, fallback2);
                            fallback2.country = string5;
                            fallback2.province = string;
                            fallback2.isp = string3;
                            fallback2.ip = string4;
                            fallback2.city = string2;
                            if (jSONObject3.has("stat-percent")) {
                                fallback2.setPercent(jSONObject3.getDouble("stat-percent"));
                            }
                            if (jSONObject3.has("stat-domain")) {
                                fallback2.setDomainName(jSONObject3.getString("stat-domain"));
                            }
                            if (jSONObject3.has("ttl")) {
                                fallback2.setEffectiveDuration(((long) jSONObject3.getInt("ttl")) * 1000);
                            }
                            setCurrentISP(fallback2.getISP());
                        }
                        i3++;
                        jSONObject4 = jSONObject;
                    }
                    JSONObject jSONObjectOptJSONObject = jSONObject3.optJSONObject(TrackData.RESERVED_PARAM);
                    if (jSONObjectOptJSONObject != null) {
                        long j2 = jSONObject3.has("reserved-ttl") ? ((long) jSONObject3.getInt("reserved-ttl")) * 1000 : 604800000L;
                        Iterator<String> itKeys = jSONObjectOptJSONObject.keys();
                        while (itKeys.hasNext()) {
                            String next = itKeys.next();
                            JSONArray jSONArrayOptJSONArray2 = jSONObjectOptJSONObject.optJSONArray(next);
                            if (jSONArrayOptJSONArray2 == null) {
                                MyLog.w("no bucket found for " + next);
                            } else {
                                Fallback fallback3 = new Fallback(next);
                                fallback3.setEffectiveDuration(j2);
                                for (int i5 = 0; i5 < jSONArrayOptJSONArray2.length(); i5++) {
                                    String string7 = jSONArrayOptJSONArray2.getString(i5);
                                    if (!TextUtils.isEmpty(string7)) {
                                        fallback3.addHost(new WeightedHost(string7, jSONArrayOptJSONArray2.length() - i5));
                                    }
                                }
                                Map<String, Fallback> map2 = sReservedHosts;
                                synchronized (map2) {
                                    try {
                                        if (this.sHostFilter.accept(next)) {
                                            map2.put(next, fallback3);
                                        }
                                    } catch (Throwable th3) {
                                        throw th3;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e2) {
            MyLog.w("failed to get bucket " + e2.getMessage());
        }
        for (int i6 = 0; i6 < arrayList.size(); i6++) {
            Fallback fallback4 = arrayList2.get(i6);
            if (fallback4 != null) {
                updateFallbacks(arrayList.get(i6), fallback4);
            }
        }
        persist();
        return arrayList2;
    }

    public static synchronized void setHostManagerFactory(HostManagerFactory hostManagerFactory) {
        factory = hostManagerFactory;
        sInstance = null;
    }

    protected boolean checkHostMapping() {
        synchronized (this.mHostsMapping) {
            try {
                if (hostLoaded) {
                    return true;
                }
                hostLoaded = true;
                this.mHostsMapping.clear();
                try {
                    String strLoadHosts = loadHosts();
                    if (!TextUtils.isEmpty(strLoadHosts)) {
                        fromJSON(strLoadHosts);
                        MyLog.i("loading the new hosts succeed");
                        return true;
                    }
                } catch (Throwable th) {
                    MyLog.w("load bucket failure: " + th.getMessage());
                }
                return false;
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public void clear() {
        synchronized (this.mHostsMapping) {
            this.mHostsMapping.clear();
        }
    }

    public void deleteOldHostFallbacksFile() {
        String fileName = getFileName();
        try {
            File file = new File(sAppContext.getFilesDir(), fileName);
            if (file.exists()) {
                boolean zDelete = file.delete();
                StringBuilder sb = new StringBuilder();
                sb.append("Delete old host fallbacks file ");
                sb.append(fileName);
                sb.append(zDelete ? " successful." : " failed.");
                MyLog.w(sb.toString());
            } else {
                MyLog.i("Old host fallbacks file " + fileName + " does not exist.");
            }
        } catch (Exception e2) {
            MyLog.w("Delete old host fallbacks file " + fileName + " error: " + e2.getMessage());
        }
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        synchronized (this.mHostsMapping) {
            try {
                for (Map.Entry<String, Fallbacks> entry : this.mHostsMapping.entrySet()) {
                    sb.append(entry.getKey());
                    sb.append(":\n");
                    sb.append(entry.getValue().toString());
                    sb.append(ShellUtils.COMMAND_LINE_END);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return sb.toString();
    }

    protected void fromJSON(String str) throws JSONException {
        synchronized (this.mHostsMapping) {
            try {
                this.mHostsMapping.clear();
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.optInt("ver") != 2) {
                    throw new JSONException("Bad version");
                }
                JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("data");
                if (jSONArrayOptJSONArray != null) {
                    for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                        Fallbacks fallbacksFromJSON = new Fallbacks().fromJSON(jSONArrayOptJSONArray.getJSONObject(i2));
                        this.mHostsMapping.put(fallbacksFromJSON.getHost(), fallbacksFromJSON);
                    }
                }
                JSONArray jSONArrayOptJSONArray2 = jSONObject.optJSONArray(TrackData.RESERVED_PARAM);
                if (jSONArrayOptJSONArray2 != null) {
                    for (int i3 = 0; i3 < jSONArrayOptJSONArray2.length(); i3++) {
                        JSONObject jSONObject2 = jSONArrayOptJSONArray2.getJSONObject(i3);
                        String strOptString = jSONObject2.optString("host");
                        if (!TextUtils.isEmpty(strOptString)) {
                            try {
                                Fallback fallbackFromJSON = new Fallback(strOptString).fromJSON(jSONObject2);
                                sReservedHosts.put(fallbackFromJSON.host, fallbackFromJSON);
                                MyLog.w("load local reserved host for " + fallbackFromJSON.host);
                            } catch (JSONException unused) {
                                MyLog.w("parse reserved host fail.");
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public ArrayList<HttpApi> generateHostStats() throws JSONException {
        ArrayList<HttpApi> arrayList;
        Iterator<WeightedHost> it;
        HostManager hostManager = this;
        synchronized (hostManager.mHostsMapping) {
            try {
                HashMap map = new HashMap();
                Iterator<String> it2 = hostManager.mHostsMapping.keySet().iterator();
                while (it2.hasNext()) {
                    Fallbacks fallbacks = hostManager.mHostsMapping.get(it2.next());
                    if (fallbacks != null) {
                        Iterator<Fallback> it3 = fallbacks.getFallbacks().iterator();
                        while (it3.hasNext()) {
                            Fallback next = it3.next();
                            HttpApi httpApi = (HttpApi) map.get(next.getISP());
                            if (httpApi == null) {
                                httpApi = new HttpApi();
                                httpApi.setCategory("httpapi");
                                httpApi.setClient_ip(next.ip);
                                httpApi.setNetwork(hostManager.processNetwork(next.networkLabel));
                                httpApi.setUuid(hostManager.sUserId);
                                httpApi.setVersion(sAppVersion);
                                httpApi.setVersion_type(sAppName);
                                httpApi.setApp_name(sAppContext.getPackageName());
                                httpApi.setApp_version(getVersionName());
                                Location location = new Location();
                                location.setCity(next.city);
                                location.setContry(next.country);
                                location.setProvince(next.province);
                                location.setIsp(next.isp);
                                httpApi.setLocation(location);
                                map.put(next.getISP(), httpApi);
                            }
                            HostInfo hostInfo = new HostInfo();
                            hostInfo.setHost(next.host);
                            ArrayList arrayList2 = new ArrayList();
                            Iterator<WeightedHost> it4 = next.getWeightedHost().iterator();
                            while (it4.hasNext()) {
                                WeightedHost next2 = it4.next();
                                ArrayList<AccessHistory> unTouchedAccessHistory = next2.getUnTouchedAccessHistory();
                                if (unTouchedAccessHistory.isEmpty()) {
                                    it = it4;
                                } else {
                                    LandNodeInfo landNodeInfo = new LandNodeInfo();
                                    landNodeInfo.setIp(next2.host);
                                    HashMap map2 = new HashMap();
                                    int i2 = 0;
                                    long j2 = 0;
                                    int i3 = 0;
                                    int size = 0;
                                    for (AccessHistory accessHistory : unTouchedAccessHistory) {
                                        if (accessHistory.getWeight() >= 0) {
                                            i2++;
                                            long cost = j2 + accessHistory.getCost();
                                            size = (int) (((long) size) + accessHistory.getSize());
                                            it4 = it4;
                                            j2 = cost;
                                        } else {
                                            String exception = accessHistory.getException();
                                            if (!TextUtils.isEmpty(exception)) {
                                                map2.put(exception, Integer.valueOf(map2.containsKey(exception) ? map2.get(exception).intValue() + 1 : 1));
                                            }
                                            i3++;
                                        }
                                        it4 = it4;
                                    }
                                    it = it4;
                                    landNodeInfo.setExp_info(map2);
                                    landNodeInfo.setSuccess_count(i2);
                                    landNodeInfo.setFailed_count(i3);
                                    landNodeInfo.setDuration(j2);
                                    landNodeInfo.setSize(size);
                                    arrayList2.add(landNodeInfo);
                                }
                                it2 = it2;
                                it3 = it3;
                                it4 = it;
                            }
                            Iterator<String> it5 = it2;
                            Iterator<Fallback> it6 = it3;
                            if (!arrayList2.isEmpty()) {
                                hostInfo.setLand_node_info(arrayList2);
                                httpApi.addToHost_info(hostInfo);
                            }
                            hostManager = this;
                            it2 = it5;
                            it3 = it6;
                        }
                    }
                    hostManager = this;
                    it2 = it2;
                }
                arrayList = new ArrayList<>();
                for (HttpApi httpApi2 : map.values()) {
                    if (httpApi2.getHost_infoSize() > 0) {
                        arrayList.add(httpApi2);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return arrayList;
    }

    public String getCurrentISP() {
        return this.currentISP;
    }

    public Fallback getFallbacksByHost(String str) {
        return getFallbacksByHost(str, true);
    }

    public Fallback getFallbacksByURL(String str) throws MalformedURLException {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("the url is empty");
        }
        return getFallbacksByHost(new URL(str).getHost(), true);
    }

    protected String getFileName() {
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(sAppName)) {
            return sAppName;
        }
        return sAppName + ":pushservice";
    }

    protected String getHost() {
        return RESOLVER_HOST;
    }

    protected Fallback getLocalFallback(String str) {
        Fallbacks fallbacks;
        Fallback fallback;
        synchronized (this.mHostsMapping) {
            checkHostMapping();
            fallbacks = this.mHostsMapping.get(str);
        }
        if (fallbacks == null || (fallback = fallbacks.getFallback()) == null) {
            return null;
        }
        return fallback;
    }

    protected String getRemoteFallbackJSON(ArrayList<String> arrayList, String str, String str2, boolean z2) throws IOException {
        ArrayList<String> arrayList2 = new ArrayList<>();
        ArrayList<NameValuePair> arrayList3 = new ArrayList();
        arrayList3.add(new BasicNameValuePair("type", str));
        if (str.equals("wap")) {
            arrayList3.add(new BasicNameValuePair("conpt", obfuscate(Network.getActiveConnPoint(sAppContext))));
        }
        if (z2) {
            arrayList3.add(new BasicNameValuePair(TrackData.RESERVED_PARAM, "1"));
        }
        arrayList3.add(new BasicNameValuePair("uuid", str2));
        arrayList3.add(new BasicNameValuePair("list", XMStringUtils.join(arrayList, ",")));
        arrayList3.add(new BasicNameValuePair("countrycode", AppRegionStorage.getInstance(sAppContext).getCountryCode()));
        arrayList3.add(new BasicNameValuePair(PushConstants.KEY_PUSH_SDK_VERSION_CODE, String.valueOf(PushConstants.PUSH_VERSION_CODE)));
        String host = getHost();
        Fallback localFallback = getLocalFallback(host);
        String str3 = String.format(Locale.US, BUCKET_URL, host);
        if (localFallback == null) {
            arrayList2.add(str3);
            Map<String, Fallback> map = sReservedHosts;
            synchronized (map) {
                try {
                    Fallback fallback = map.get(host);
                    if (fallback != null) {
                        Iterator<String> it = fallback.getHosts(true).iterator();
                        while (it.hasNext()) {
                            arrayList2.add(String.format(Locale.US, BUCKET_URL, it.next()));
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        } else {
            arrayList2 = localFallback.getUrls(str3);
        }
        Iterator<String> it2 = arrayList2.iterator();
        IOException e2 = null;
        while (it2.hasNext()) {
            Uri.Builder builderBuildUpon = Uri.parse(it2.next()).buildUpon();
            for (NameValuePair nameValuePair : arrayList3) {
                builderBuildUpon.appendQueryParameter(nameValuePair.getName(), nameValuePair.getValue());
            }
            try {
                HttpGet httpGet = this.sHttpGetter;
                return httpGet == null ? Network.downloadXml(sAppContext, new URL(builderBuildUpon.toString())) : httpGet.doGet(builderBuildUpon.toString());
            } catch (IOException e3) {
                e2 = e3;
            }
        }
        if (e2 == null) {
            return null;
        }
        MyLog.w("network exception: " + e2.getMessage());
        throw e2;
    }

    public Fallback getReservedFallback(String str) {
        Fallback fallback;
        Map<String, Fallback> map = sReservedHosts;
        synchronized (map) {
            fallback = map.get(str);
        }
        return fallback;
    }

    protected String loadHosts() {
        BufferedInputStream bufferedInputStream;
        FileInputStream fileInputStream;
        try {
            File file = new File(sAppContext.getFilesDir(), getHostFallbacksFileName());
            try {
                if (!file.isFile()) {
                    IOUtils.closeQuietly(null);
                    IOUtils.closeQuietly(null);
                    return null;
                }
                fileInputStream = new FileInputStream(file);
                try {
                    bufferedInputStream = new BufferedInputStream(fileInputStream);
                    try {
                        String str = new String(DataCryptUtils.mipushDecrypt(getHostFallbacksEncryptKey(), IOUtils.readInputStream(bufferedInputStream)), StandardCharsets.UTF_8);
                        MyLog.i("load host fallbacks = " + str);
                        IOUtils.closeQuietly(bufferedInputStream);
                        IOUtils.closeQuietly(fileInputStream);
                        return str;
                    } catch (Throwable th) {
                        th = th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    bufferedInputStream = null;
                }
                MyLog.w("load host exception " + th.getMessage());
                return null;
            } finally {
                IOUtils.closeQuietly(bufferedInputStream);
                IOUtils.closeQuietly(fileInputStream);
            }
        } catch (Throwable th3) {
            th = th3;
            bufferedInputStream = null;
            fileInputStream = null;
        }
    }

    public void persist() {
        FileOutputStream fileOutputStreamOpenFileOutput;
        synchronized (this.mHostsMapping) {
            BufferedOutputStream bufferedOutputStream = null;
            try {
                try {
                    try {
                        String string = toJSON().toString();
                        MyLog.i("persist host fallbacks = " + string);
                        if (TextUtils.isEmpty(string)) {
                            fileOutputStreamOpenFileOutput = null;
                        } else {
                            fileOutputStreamOpenFileOutput = sAppContext.openFileOutput(getHostFallbacksFileName(), 0);
                            try {
                                BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(fileOutputStreamOpenFileOutput);
                                try {
                                    bufferedOutputStream2.write(DataCryptUtils.mipushEncrypt(getHostFallbacksEncryptKey(), string.getBytes(StandardCharsets.UTF_8)));
                                    bufferedOutputStream2.flush();
                                    bufferedOutputStream = bufferedOutputStream2;
                                } catch (Exception e2) {
                                    e = e2;
                                    bufferedOutputStream = bufferedOutputStream2;
                                    MyLog.w("persist bucket failure: " + e.getMessage());
                                    IOUtils.closeQuietly(bufferedOutputStream);
                                } catch (Throwable th) {
                                    th = th;
                                    bufferedOutputStream = bufferedOutputStream2;
                                    IOUtils.closeQuietly(bufferedOutputStream);
                                    IOUtils.closeQuietly(fileOutputStreamOpenFileOutput);
                                    throw th;
                                }
                            } catch (Exception e3) {
                                e = e3;
                            }
                        }
                        IOUtils.closeQuietly(bufferedOutputStream);
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (Exception e4) {
                    e = e4;
                    fileOutputStreamOpenFileOutput = null;
                } catch (Throwable th3) {
                    th = th3;
                    fileOutputStreamOpenFileOutput = null;
                }
                IOUtils.closeQuietly(fileOutputStreamOpenFileOutput);
            } catch (Throwable th4) {
                throw th4;
            }
        }
    }

    public void purge() {
        String next;
        synchronized (this.mHostsMapping) {
            try {
                Iterator<Fallbacks> it = this.mHostsMapping.values().iterator();
                while (it.hasNext()) {
                    it.next().purge(true);
                }
                while (true) {
                    Iterator<String> it2 = this.mHostsMapping.keySet().iterator();
                    do {
                        if (it2.hasNext()) {
                            next = it2.next();
                        }
                    } while (!this.mHostsMapping.get(next).getFallbacks().isEmpty());
                    this.mHostsMapping.remove(next);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void refreshFallbacks() {
        ArrayList<String> arrayList;
        synchronized (this.mHostsMapping) {
            try {
                checkHostMapping();
                arrayList = new ArrayList<>(this.mHostsMapping.keySet());
                for (int size = arrayList.size() - 1; size >= 0; size--) {
                    Fallbacks fallbacks = this.mHostsMapping.get(arrayList.get(size));
                    if (fallbacks != null && fallbacks.getFallback() != null) {
                        arrayList.remove(size);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        ArrayList<Fallback> arrayListRequestRemoteFallbacks = requestRemoteFallbacks(arrayList);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            if (arrayListRequestRemoteFallbacks.get(i2) != null) {
                updateFallbacks(arrayList.get(i2), arrayListRequestRemoteFallbacks.get(i2));
            }
        }
    }

    protected Fallback requestRemoteFallback(String str) {
        if (System.currentTimeMillis() - this.lastRemoteRequestTimestamp <= this.remoteRequestFailureCount * 60000) {
            return null;
        }
        this.lastRemoteRequestTimestamp = System.currentTimeMillis();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(str);
        Fallback fallback = requestRemoteFallbacks(arrayList).get(0);
        if (fallback != null) {
            this.remoteRequestFailureCount = 0L;
            return fallback;
        }
        long j2 = this.remoteRequestFailureCount;
        if (j2 >= 15) {
            return null;
        }
        this.remoteRequestFailureCount = j2 + 1;
        return null;
    }

    public void setCurrentISP(String str) {
        this.currentISP = str;
    }

    protected JSONObject toJSON() throws JSONException {
        JSONObject jSONObject;
        synchronized (this.mHostsMapping) {
            try {
                jSONObject = new JSONObject();
                jSONObject.put("ver", 2);
                JSONArray jSONArray = new JSONArray();
                Iterator<Fallbacks> it = this.mHostsMapping.values().iterator();
                while (it.hasNext()) {
                    jSONArray.put(it.next().toJSON());
                }
                jSONObject.put("data", jSONArray);
                JSONArray jSONArray2 = new JSONArray();
                Iterator<Fallback> it2 = sReservedHosts.values().iterator();
                while (it2.hasNext()) {
                    jSONArray2.put(it2.next().toJSON());
                }
                jSONObject.put(TrackData.RESERVED_PARAM, jSONArray2);
            } catch (Throwable th) {
                throw th;
            }
        }
        return jSONObject;
    }

    public void updateFallbacks(String str, Fallback fallback) {
        if (TextUtils.isEmpty(str) || fallback == null) {
            throw new IllegalArgumentException("the argument is invalid " + str + ", " + fallback);
        }
        if (this.sHostFilter.accept(str)) {
            synchronized (this.mHostsMapping) {
                try {
                    checkHostMapping();
                    if (this.mHostsMapping.containsKey(str)) {
                        this.mHostsMapping.get(str).addFallback(fallback);
                    } else {
                        Fallbacks fallbacks = new Fallbacks(str);
                        fallbacks.addFallback(fallback);
                        this.mHostsMapping.put(str, fallbacks);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public void updateHostsMapping(Map<String, Fallbacks> map) {
        synchronized (this.mHostsMapping) {
            this.mHostsMapping.clear();
            this.mHostsMapping.putAll(map);
        }
    }

    protected HostManager(Context context, HostFilter hostFilter, HttpGet httpGet, String str, String str2, String str3) {
        this.mHostsMapping = new HashMap();
        this.sUserId = "0";
        this.remoteRequestFailureCount = 0L;
        this.MAX_REQUEST_FAILURE_CNT = 15L;
        this.lastRemoteRequestTimestamp = 0L;
        this.currentISP = "isp_prov_city_country_ip";
        this.sHttpGetter = httpGet;
        if (hostFilter == null) {
            this.sHostFilter = new HostFilter() { // from class: com.xiaomi.network.HostManager.1
                @Override // com.xiaomi.network.HostFilter
                public boolean accept(String str4) {
                    return true;
                }
            };
        } else {
            this.sHostFilter = hostFilter;
        }
        this.sUserId = str;
        sAppName = str2 == null ? context.getPackageName() : str2;
        sAppVersion = str3 == null ? getVersionName() : str3;
    }

    public Fallback getFallbacksByHost(String str, boolean z2) {
        Fallback fallbackRequestRemoteFallback;
        MyLog.v(TAG, "-->getFallbacksByHost(): host=", str, ", fetchRemoteIfNeed=", Boolean.valueOf(z2));
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("the host is empty");
        }
        if (!this.sHostFilter.accept(str)) {
            return null;
        }
        Fallback localFallback = getLocalFallback(str);
        if (localFallback == null || !localFallback.isEffective()) {
            return (z2 && Network.hasNetwork(sAppContext) && (fallbackRequestRemoteFallback = requestRemoteFallback(str)) != null) ? fallbackRequestRemoteFallback : new Fallback(str, localFallback) { // from class: com.xiaomi.network.HostManager.2
                Fallback local;
                final /* synthetic */ Fallback val$localFB;

                {
                    this.val$localFB = localFallback;
                    this.local = localFallback;
                    this.host = this.host;
                    if (localFallback != null) {
                        this.ip = localFallback.ip;
                    }
                }

                @Override // com.xiaomi.network.Fallback
                public synchronized void accessHost(String str2, AccessHistory accessHistory) {
                    Fallback fallback = this.local;
                    if (fallback != null) {
                        fallback.accessHost(str2, accessHistory);
                    }
                }

                @Override // com.xiaomi.network.Fallback
                public synchronized ArrayList<String> getHosts(boolean z3) {
                    ArrayList<String> arrayList;
                    try {
                        arrayList = new ArrayList<>();
                        Fallback fallback = this.local;
                        if (fallback != null) {
                            arrayList.addAll(fallback.getHosts(true));
                        }
                        Map<String, Fallback> map = HostManager.sReservedHosts;
                        synchronized (map) {
                            try {
                                Fallback fallback2 = map.get(this.host);
                                if (fallback2 != null) {
                                    for (String str2 : fallback2.getHosts(true)) {
                                        if (arrayList.indexOf(str2) == -1) {
                                            arrayList.add(str2);
                                        }
                                    }
                                    arrayList.remove(this.host);
                                    arrayList.add(this.host);
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                    } catch (Throwable th2) {
                        throw th2;
                    }
                    return arrayList;
                }

                @Override // com.xiaomi.network.Fallback
                public boolean isEffective() {
                    return false;
                }
            };
        }
        return localFallback;
    }

    public static synchronized void init(Context context, HostFilter hostFilter, HttpGet httpGet, String str, String str2, String str3) {
        try {
            Context applicationContext = context.getApplicationContext();
            sAppContext = applicationContext;
            if (applicationContext == null) {
                sAppContext = context;
            }
            if (sInstance == null) {
                HostManagerFactory hostManagerFactory = factory;
                if (hostManagerFactory == null) {
                    sInstance = new HostManager(context, hostFilter, httpGet, str, str2, str3);
                } else {
                    sInstance = hostManagerFactory.createHostManager(context, hostFilter, httpGet, str);
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }
}
