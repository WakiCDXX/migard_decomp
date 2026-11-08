package com.xiaomi.channel.commonutils.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.android.TelephonyUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.string.MD5;
import com.xiaomi.mipush.sdk.Constants;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class Network {
    public static final String CHINA_3G_CDMA2000 = "CDMA2000";
    public static final String CHINA_3G_TD_SCDMA = "TD-SCDMA";
    public static final String CHINA_3G_WCDMA = "WCDMA";
    public static final String CMWAP_GATEWAY = "10.0.0.172";
    public static final String CMWAP_HEADER_HOST_KEY = "X-Online-Host";
    public static final int CMWAP_PORT = 80;
    public static final int CONNECTION_TIMEOUT = 10000;
    protected static final boolean DEBUG = false;
    public static final String ENCODING_GZIP = "gzip";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String NETWORK_TYPE_3GNET = "3gnet";
    public static final String NETWORK_TYPE_3GWAP = "3gwap";
    public static final String NETWORK_TYPE_CHINATELECOM = "#777";
    public static final String NETWORK_TYPE_WIFI = "wifi";
    public static final int READ_TIMEOUT = 15000;
    protected static final String TAG = "NetworkLog";
    public static final String USER_AGENT = "User-Agent";
    public static final String UserAgent_PC_Chrome = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.464.0 Safari/534.3";
    public static final String UserAgent_PC_Chrome_6_0_464_0 = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.464.0 Safari/534.3";
    private static final AtomicReference<CacheFutureTask<NetworkInfoWrapper>> mNetworkInfo = new AtomicReference<>(createNetworkInfoGetTask());
    public static final Pattern ContentTypePattern_MimeType = Pattern.compile("([^\\s;]+)(.*)");
    public static final Pattern ContentTypePattern_Charset = Pattern.compile("(.*?charset\\s*=[^a-zA-Z0-9]*)([-a-zA-Z0-9]+)(.*)", 2);
    public static final Pattern ContentTypePattern_XmlEncoding = Pattern.compile("(\\<\\?xml\\s+.*?encoding\\s*=[^a-zA-Z0-9]*)([-a-zA-Z0-9]+)(.*)", 2);

    private static class CacheFutureTask<T> extends FutureTask<T> {
        public static final long CACHE_INVALIDE_TIME = 1800000;
        private long mRunTime;

        public CacheFutureTask(Callable<T> callable) {
            super(callable);
        }

        public boolean isInvalid() {
            return MIUIUtils.isXMSF(SystemUtils.getContext()) || (isDone() && Math.abs(SystemClock.elapsedRealtime() - this.mRunTime) > 1800000);
        }

        @Override // java.util.concurrent.FutureTask, java.util.concurrent.RunnableFuture, java.lang.Runnable
        public void run() {
            this.mRunTime = SystemClock.elapsedRealtime();
            super.run();
        }
    }

    public static final class DoneHandlerInputStream extends FilterInputStream {
        private boolean done;

        public DoneHandlerInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] bArr, int i2, int i3) throws IOException {
            int i4;
            if (!this.done && (i4 = super.read(bArr, i2, i3)) != -1) {
                return i4;
            }
            this.done = true;
            return -1;
        }
    }

    private static class DownloadTask extends AsyncTask<Void, Void, Boolean> {
        private boolean bOnlyWifi;
        private PostDownloadHandler handler;
        private Context mContext;
        private OutputStream output;
        private String url;

        public DownloadTask(String str, OutputStream outputStream, PostDownloadHandler postDownloadHandler) {
            this(str, outputStream, postDownloadHandler, false, null);
        }

        public DownloadTask(String str, OutputStream outputStream, PostDownloadHandler postDownloadHandler, boolean z2, Context context) {
            this.url = str;
            this.output = outputStream;
            this.handler = postDownloadHandler;
            this.bOnlyWifi = z2;
            this.mContext = context;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            return Boolean.valueOf(Network.downloadFile(this.url, this.output, this.bOnlyWifi, this.mContext));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            this.handler.OnPostDownload(bool.booleanValue());
        }
    }

    public static class HttpHeaderInfo {
        public Map<String, String> AllHeaders;
        public String ContentType;
        public int ResponseCode;
        public String UserAgent;
        public String realUrl;

        public String toString() {
            return String.format("resCode = %1$d, headers = %2$s", Integer.valueOf(this.ResponseCode), this.AllHeaders.toString());
        }
    }

    public interface PostDownloadHandler {
        void OnPostDownload(boolean z2);
    }

    public static Object addNetworkCallBack(Context context) {
        if (context == null) {
            context = SystemUtils.getContext();
        }
        ConnectivityManager.NetworkCallback networkCallback = null;
        if (context == null || MIUIUtils.isXMSF(context)) {
            return null;
        }
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME);
            NetworkRequest networkRequestBuild = new NetworkRequest.Builder().build();
            ConnectivityManager.NetworkCallback networkCallback2 = new ConnectivityManager.NetworkCallback() { // from class: com.xiaomi.channel.commonutils.network.Network.1
                @Override // android.net.ConnectivityManager.NetworkCallback
                public void onAvailable(android.net.Network network) {
                    super.onAvailable(network);
                    Network.refreshNetworkInfo();
                }

                @Override // android.net.ConnectivityManager.NetworkCallback
                public void onLost(android.net.Network network) {
                    super.onLost(network);
                    Network.refreshNetworkInfo();
                }
            };
            try {
                connectivityManager.registerNetworkCallback(networkRequestBuild, networkCallback2);
                return networkCallback2;
            } catch (Throwable th) {
                th = th;
                networkCallback = networkCallback2;
                MyLog.w("exception occurred in adding network callback :" + th);
                return networkCallback;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static void beginDownloadFile(String str, OutputStream outputStream, PostDownloadHandler postDownloadHandler) {
        new DownloadTask(str, outputStream, postDownloadHandler).execute(new Void[0]);
    }

    private static CacheFutureTask<NetworkInfoWrapper> createNetworkInfoGetTask() {
        return new CacheFutureTask<>(new Callable<NetworkInfoWrapper>() { // from class: com.xiaomi.channel.commonutils.network.Network.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public NetworkInfoWrapper call() {
                NetworkInfo activeNetworkInfo;
                Context context = SystemUtils.getContext();
                if (context == null) {
                    return null;
                }
                try {
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME);
                    if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) {
                        return null;
                    }
                    return new NetworkInfoWrapper(activeNetworkInfo);
                } catch (Exception unused) {
                    return null;
                }
            }
        });
    }

    public static HttpResponse doGzipPost(Context context, String str, Map<String, String> map) throws IOException {
        String strFromParamsMapToString = fromParamsMapToString(map);
        HashMap map2 = new HashMap();
        map2.put(HEADER_CONTENT_ENCODING, ENCODING_GZIP);
        return httpRequest(context, str, "POST", map2, strFromParamsMapToString);
    }

    public static HttpResponse doHttpGet(Context context, String str, Map<String, String> map) throws IOException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String strFromParamsMapToString = fromParamsMapToString(map);
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        if (str.contains("?")) {
            sb.append("&");
            sb.append(strFromParamsMapToString);
        } else {
            sb.append("?");
            sb.append(strFromParamsMapToString);
        }
        return httpRequest(context, sb.toString(), "GET", null, null);
    }

    public static HttpResponse doHttpPost(Context context, String str, Map<String, String> map) throws IOException {
        return httpRequest(context, str, "POST", null, fromParamsMapToString(map));
    }

    public static boolean downloadFile(String str, OutputStream outputStream) {
        return downloadFile(str, outputStream, false, null);
    }

    public static String downloadXml(Context context, URL url) throws IOException {
        return downloadXml(context, url, false, (String) null, "UTF-8", (String) null);
    }

    public static InputStream downloadXmlAsStream(Context context, URL url) throws IOException {
        return downloadXmlAsStream(context, url, true, null, null, null, null);
    }

    public static InputStream downloadXmlAsStreamWithoutRedirect(URL url, String str, String str2) throws IOException {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(15000);
            if (!TextUtils.isEmpty(str)) {
                httpURLConnection.setRequestProperty(USER_AGENT, str);
            }
            if (str2 != null) {
                httpURLConnection.setRequestProperty("Cookie", str2);
            }
            int responseCode = httpURLConnection.getResponseCode();
            return new DoneHandlerInputStream((responseCode < 300 || responseCode >= 400) ? httpURLConnection.getInputStream() : null);
        } catch (IOException e2) {
            throw new IOException("IOException:" + e2.getClass().getSimpleName());
        } catch (Throwable th) {
            throw new IOException(th.getMessage());
        }
    }

    public static String encryptURL(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        new String();
        return String.format("%s&key=%s", str, MD5.MD5_32(String.format("%sbe988a6134bc8254465424e5a70ef037", str)));
    }

    public static String fromParamsMapToString(Map<String, String> map) {
        if (map == null || map.size() <= 0) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                try {
                    stringBuffer.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    stringBuffer.append("=");
                    stringBuffer.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    stringBuffer.append("&");
                } catch (UnsupportedEncodingException e2) {
                    MyLog.w("Failed to convert from params map to string: " + e2);
                    MyLog.w("map: " + map.toString());
                    return null;
                }
            }
        }
        if (stringBuffer.length() > 0) {
            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }

    public static String getActiveConnPoint(Context context) {
        if (isWIFIConnected(context)) {
            return "wifi";
        }
        NetworkInfoWrapper networkInfo = getNetworkInfo();
        if (networkInfo == null) {
            return "";
        }
        return (networkInfo.getTypeName() + Constants.ACCEPT_TIME_SEPARATOR_SERVER + networkInfo.getSubtypeName() + Constants.ACCEPT_TIME_SEPARATOR_SERVER + networkInfo.getExtraInfo()).toLowerCase();
    }

    public static String getActiveNetworkName(Context context) {
        NetworkInfoWrapper networkInfo = getNetworkInfo();
        if (networkInfo == null) {
            return "null";
        }
        return TextUtils.isEmpty(networkInfo.getSubtypeName()) ? networkInfo.getTypeName() : String.format("%s-%s", networkInfo.getTypeName(), networkInfo.getSubtypeName());
    }

    public static int getActiveNetworkType(Context context) {
        NetworkInfoWrapper networkInfo = getNetworkInfo();
        if (networkInfo == null) {
            return -1;
        }
        return networkInfo.getType();
    }

    public static String getCMWapUrl(URL url) {
        StringBuilder sb = new StringBuilder();
        sb.append(url.getProtocol());
        sb.append("://");
        sb.append(CMWAP_GATEWAY);
        sb.append(url.getPath());
        if (!TextUtils.isEmpty(url.getQuery())) {
            sb.append("?");
            sb.append(url.getQuery());
        }
        return sb.toString();
    }

    private static URL getDefaultStreamHandlerURL(String str) throws MalformedURLException {
        return new URL(str);
    }

    public static HttpHeaderInfo getHttpHeaderInfo(String str, String str2, String str3) {
        try {
            URL url = new URL(str);
            if (!url.getProtocol().equals("http") && !url.getProtocol().equals("https")) {
                return null;
            }
            int i2 = 0;
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if (str.indexOf("wap") == -1) {
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
            } else {
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setReadTimeout(15000);
            }
            if (!TextUtils.isEmpty(str2)) {
                httpURLConnection.setRequestProperty(USER_AGENT, str2);
            }
            if (str3 != null) {
                httpURLConnection.setRequestProperty("Cookie", str3);
            }
            HttpHeaderInfo httpHeaderInfo = new HttpHeaderInfo();
            httpHeaderInfo.ResponseCode = httpURLConnection.getResponseCode();
            httpHeaderInfo.UserAgent = str2;
            while (true) {
                String headerFieldKey = httpURLConnection.getHeaderFieldKey(i2);
                String headerField = httpURLConnection.getHeaderField(i2);
                if (headerFieldKey == null && headerField == null) {
                    return httpHeaderInfo;
                }
                if (headerFieldKey != null && headerFieldKey.equals("content-type")) {
                    httpHeaderInfo.ContentType = headerField;
                }
                if (headerFieldKey != null && headerFieldKey.equals("location")) {
                    URI uri = new URI(headerField);
                    if (!uri.isAbsolute()) {
                        uri = new URI(str).resolve(uri);
                    }
                    httpHeaderInfo.realUrl = uri.toString();
                }
                i2++;
            }
        } catch (MalformedURLException e2) {
            MyLog.e("Failed to transform URL. ", e2);
            return null;
        } catch (IOException e3) {
            MyLog.e("Failed to get mime type. ", e3);
            return null;
        } catch (URISyntaxException e4) {
            MyLog.e("Failed to parse URI. ", e4);
            return null;
        } catch (Throwable th) {
            MyLog.e("Failed to get HttpHeaderInfo. ", th);
            return null;
        }
    }

    public static InputStream getHttpPostAsStream(URL url, String str, Map<String, String> map, String str2, String str3) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("url");
        }
        try {
            try {
                HttpURLConnection.setFollowRedirects(true);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                if (!TextUtils.isEmpty(str2)) {
                    httpURLConnection.setRequestProperty(USER_AGENT, str2);
                }
                if (!TextUtils.isEmpty(str3)) {
                    httpURLConnection.setRequestProperty("Cookie", str3);
                }
                OutputStream outputStream = httpURLConnection.getOutputStream();
                try {
                    outputStream.write(str.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    map.put("ResponseCode", httpURLConnection.getResponseCode() + "");
                    int i2 = 0;
                    while (true) {
                        String headerFieldKey = httpURLConnection.getHeaderFieldKey(i2);
                        String headerField = httpURLConnection.getHeaderField(i2);
                        if (headerFieldKey == null && headerField == null) {
                            InputStream inputStream = httpURLConnection.getInputStream();
                            IOUtils.closeQuietly(null);
                            return inputStream;
                        }
                        map.put(headerFieldKey, headerField);
                        i2++;
                        throw new IOException("IOException:" + e.getClass().getSimpleName());
                    }
                } catch (IOException e2) {
                    e = e2;
                } catch (Throwable th) {
                    th = th;
                    throw new IOException(th.getMessage());
                }
            } catch (Throwable th2) {
                IOUtils.closeQuietly(null);
                throw th2;
            }
        } catch (IOException e3) {
            e = e3;
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public static HttpURLConnection getHttpUrlConnection(Context context, URL url) throws IOException {
        if ("http".equals(url.getProtocol())) {
            return isCtwap(context) ? (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80))) : (HttpURLConnection) url.openConnection();
        }
        return (HttpURLConnection) url.openConnection();
    }

    public static String getLocalNetworkType(Context context) {
        if (isWIFIConnected(context)) {
            return "wifi";
        }
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getNetworkInfo(0);
        return TelephonyUtils.isChinaTelecom(context) ? NETWORK_TYPE_CHINATELECOM : networkInfo != null ? networkInfo.getExtraInfo() : "unknown";
    }

    public static NetworkInfoWrapper getNetworkInfo() {
        AtomicReference<CacheFutureTask<NetworkInfoWrapper>> atomicReference = mNetworkInfo;
        CacheFutureTask<NetworkInfoWrapper> cacheFutureTaskCreateNetworkInfoGetTask = atomicReference.get();
        if (cacheFutureTaskCreateNetworkInfoGetTask != null) {
            try {
                if (cacheFutureTaskCreateNetworkInfoGetTask.isInvalid()) {
                    cacheFutureTaskCreateNetworkInfoGetTask = createNetworkInfoGetTask();
                    atomicReference.set(cacheFutureTaskCreateNetworkInfoGetTask);
                }
                if (!cacheFutureTaskCreateNetworkInfoGetTask.isDone()) {
                    cacheFutureTaskCreateNetworkInfoGetTask.run();
                }
                return cacheFutureTaskCreateNetworkInfoGetTask.get();
            } catch (Exception unused) {
            }
        }
        return null;
    }

    public static boolean hasNetwork(Context context) {
        return getActiveNetworkType(context) >= 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static HttpResponse httpRequest(Context context, String str, String str2, Map<String, String> map, String str3) throws IOException {
        boolean zEqualsIgnoreCase;
        BufferedReader bufferedReader;
        HttpResponse httpResponse = new HttpResponse();
        try {
            try {
                try {
                    HttpURLConnection httpUrlConnection = getHttpUrlConnection(context, getDefaultStreamHandlerURL(str));
                    httpUrlConnection.setConnectTimeout(10000);
                    httpUrlConnection.setReadTimeout(15000);
                    String str4 = str2;
                    if (str2 == 0) {
                        str4 = "GET";
                    }
                    httpUrlConnection.setRequestMethod(str4);
                    int i2 = 0;
                    if (map != null) {
                        zEqualsIgnoreCase = ENCODING_GZIP.equalsIgnoreCase(map.get(HEADER_CONTENT_ENCODING));
                        for (String str5 : map.keySet()) {
                            httpUrlConnection.setRequestProperty(str5, map.get(str5));
                        }
                    } else {
                        zEqualsIgnoreCase = false;
                    }
                    if (!TextUtils.isEmpty(str3)) {
                        httpUrlConnection.setDoOutput(true);
                        byte[] bytes = str3.getBytes();
                        OutputStream gZIPOutputStream = zEqualsIgnoreCase ? new GZIPOutputStream(httpUrlConnection.getOutputStream()) : httpUrlConnection.getOutputStream();
                        try {
                            gZIPOutputStream.write(bytes, 0, bytes.length);
                            gZIPOutputStream.flush();
                            gZIPOutputStream.close();
                        } catch (IOException e2) {
                            e = e2;
                        } catch (Throwable th) {
                            th = th;
                            throw new IOException(th.getMessage());
                        }
                    }
                    httpResponse.responseCode = httpUrlConnection.getResponseCode();
                    MyLog.w("Http POST Response Code: " + httpResponse.responseCode);
                    while (true) {
                        String headerFieldKey = httpUrlConnection.getHeaderFieldKey(i2);
                        String headerField = httpUrlConnection.getHeaderField(i2);
                        if (headerFieldKey == null && headerField == null) {
                            try {
                                break;
                            } catch (IOException unused) {
                                bufferedReader = new BufferedReader(new InputStreamReader(new DoneHandlerInputStream(httpUrlConnection.getErrorStream())));
                            }
                        } else {
                            httpResponse.headers.put(headerFieldKey, headerField);
                            i2 += 2;
                        }
                        throw new IOException("err while request " + str + ":" + e.getClass().getSimpleName());
                    }
                    bufferedReader = new BufferedReader(new InputStreamReader(new DoneHandlerInputStream(httpUrlConnection.getInputStream())));
                    try {
                        StringBuffer stringBuffer = new StringBuffer();
                        String property = System.getProperty("line.separator");
                        for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                            stringBuffer.append(line);
                            stringBuffer.append(property);
                        }
                        httpResponse.responseString = stringBuffer.toString();
                        bufferedReader.close();
                        IOUtils.closeQuietly(null);
                        IOUtils.closeQuietly(null);
                        return httpResponse;
                    } catch (IOException e3) {
                        e = e3;
                    } catch (Throwable th2) {
                        th = th2;
                        throw new IOException(th.getMessage());
                    }
                } catch (IOException e4) {
                    e = e4;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Throwable th4) {
            IOUtils.closeQuietly(null);
            IOUtils.closeQuietly(str2);
            throw th4;
        }
    }

    public static boolean is2GConnected(Context context) {
        NetworkInfoWrapper networkInfo = getNetworkInfo(context);
        if (networkInfo == null || networkInfo.getType() != 0) {
            return false;
        }
        int subtype = networkInfo.getSubtype();
        return subtype == 1 || subtype == 2 || subtype == 4 || subtype == 7 || subtype == 11;
    }

    public static boolean is3GConnected(Context context) {
        NetworkInfoWrapper networkInfo = getNetworkInfo(context);
        if (networkInfo == null || networkInfo.getType() != 0) {
            return false;
        }
        String subtypeName = networkInfo.getSubtypeName();
        if (!CHINA_3G_TD_SCDMA.equalsIgnoreCase(subtypeName) && !CHINA_3G_CDMA2000.equalsIgnoreCase(subtypeName) && !CHINA_3G_WCDMA.equalsIgnoreCase(subtypeName)) {
            switch (networkInfo.getSubtype()) {
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                case 15:
                    break;
                case 4:
                case 7:
                case 11:
                case 13:
                default:
                    return false;
            }
        }
        return true;
    }

    public static boolean is4GConnected(Context context) {
        NetworkInfoWrapper networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.getType() == 0 && 13 == networkInfo.getSubtype();
    }

    public static boolean is5GConnected(Context context) {
        NetworkInfoWrapper networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.getType() == 0 && 20 == networkInfo.getSubtype();
    }

    public static boolean isConnected(Context context) {
        NetworkInfoWrapper networkInfo = getNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /* JADX WARN: Code duplicated, block: B:8:0x001c  */
    public static boolean isConnectivity(Context context) {
        boolean zHasCapability;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME);
        if (connectivityManager != null) {
            try {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (networkCapabilities != null) {
                    zHasCapability = networkCapabilities.hasCapability(16);
                } else {
                    zHasCapability = false;
                }
            } catch (Exception unused) {
            }
        } else {
            zHasCapability = false;
        }
        return zHasCapability && isConnected(context);
    }

    public static boolean isCtwap(Context context) {
        NetworkInfoWrapper networkInfo;
        if (!"CN".equalsIgnoreCase(((TelephonyManager) context.getSystemService("phone")).getSimCountryIso()) || (networkInfo = getNetworkInfo()) == null) {
            return false;
        }
        String extraInfo = networkInfo.getExtraInfo();
        return !TextUtils.isEmpty(extraInfo) && extraInfo.length() >= 3 && extraInfo.contains("ctwap");
    }

    public static boolean isUsingMobileDataConnection(Context context) {
        return is5GConnected(context) || is4GConnected(context) || is3GConnected(context) || is2GConnected(context);
    }

    public static boolean isWIFIConnected(Context context) {
        NetworkInfoWrapper networkInfo = getNetworkInfo();
        return networkInfo != null && 1 == networkInfo.getType();
    }

    public static void onNetworkStatusReceive() {
        refreshNetworkInfo();
    }

    public static void refreshNetworkInfo() {
        mNetworkInfo.set(createNetworkInfoGetTask());
    }

    public static void removeCallback(Context context, Object obj) {
        if (context == null || obj == null) {
            return;
        }
        try {
            if (obj instanceof ConnectivityManager.NetworkCallback) {
                ((ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).unregisterNetworkCallback((ConnectivityManager.NetworkCallback) obj);
            }
        } catch (Throwable th) {
            MyLog.w("exception occurred in removing network callback :" + th);
        }
    }

    /* JADX WARN: Code duplicated, block: B:15:0x005f  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v11 */
    /* JADX WARN: Type inference failed for: r8v12 */
    /* JADX WARN: Type inference failed for: r8v6, types: [java.io.Closeable] */
    public static String tryDetectCharsetEncoding(URL url, String str) throws IOException {
        String strGroup;
        String lowerCase;
        ?? r8;
        String strTrim;
        boolean zEndsWith;
        if (url == null) {
            throw new IllegalArgumentException("url");
        }
        HttpURLConnection.setFollowRedirects(true);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(15000);
        if (!TextUtils.isEmpty(str)) {
            httpURLConnection.setRequestProperty(USER_AGENT, str);
        }
        String contentType = httpURLConnection.getContentType();
        if (TextUtils.isEmpty(contentType)) {
            return null;
        }
        Matcher matcher = ContentTypePattern_Charset.matcher(contentType);
        if (!matcher.matches() || matcher.groupCount() < 3) {
            strGroup = null;
        } else {
            strGroup = matcher.group(2);
            if (TextUtils.isEmpty(strGroup)) {
                strGroup = null;
            } else {
                MyLog.w("HTTP charset detected is: " + strGroup);
            }
        }
        if (TextUtils.isEmpty(strGroup)) {
            Matcher matcher2 = ContentTypePattern_MimeType.matcher(contentType);
            if (matcher2.matches() && matcher2.groupCount() >= 2) {
                String strGroup2 = matcher2.group(1);
                if (!TextUtils.isEmpty(strGroup2)) {
                    lowerCase = strGroup2.toLowerCase();
                    if (lowerCase.startsWith("application/") && (lowerCase.startsWith("application/xml") || (zEndsWith = lowerCase.endsWith("+xml")))) {
                        try {
                            try {
                                r8 = lowerCase;
                                r8 = zEndsWith;
                                r8 = lowerCase;
                                DoneHandlerInputStream doneHandlerInputStream = new DoneHandlerInputStream(httpURLConnection.getInputStream());
                                try {
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(doneHandlerInputStream));
                                    do {
                                        try {
                                            String line = bufferedReader.readLine();
                                            if (line != null) {
                                                strTrim = line.trim();
                                            }
                                            String str2 = strGroup;
                                            IOUtils.closeQuietly(doneHandlerInputStream);
                                            IOUtils.closeQuietly(bufferedReader);
                                            return str2;
                                        } catch (IOException e2) {
                                            throw e2;
                                        } catch (Throwable th) {
                                            th = th;
                                            throw new IOException(th.getMessage());
                                        }
                                    } while (strTrim.length() == 0);
                                    Matcher matcher3 = ContentTypePattern_XmlEncoding.matcher(strTrim);
                                    if (matcher3.matches() && matcher3.groupCount() >= 3) {
                                        String strGroup3 = matcher3.group(2);
                                        if (!TextUtils.isEmpty(strGroup3)) {
                                            MyLog.v("XML charset detected is: " + strGroup3);
                                            strGroup = strGroup3;
                                        }
                                    }
                                    String str3 = strGroup;
                                    IOUtils.closeQuietly(doneHandlerInputStream);
                                    IOUtils.closeQuietly(bufferedReader);
                                    return str3;
                                } catch (IOException e3) {
                                    throw e3;
                                } catch (Throwable th2) {
                                    th = th2;
                                }
                            } catch (Throwable th3) {
                                IOUtils.closeQuietly(r8);
                                IOUtils.closeQuietly(null);
                                throw th3;
                            }
                        } catch (IOException e4) {
                            throw e4;
                        } catch (Throwable th4) {
                            th = th4;
                        }
                    }
                }
            }
        }
        r8 = lowerCase;
        r8 = zEndsWith;
        return strGroup;
    }

    public static String uploadFile(String str, File file, String str2) throws IOException {
        return uploadFile(str, null, file, str2);
    }

    public static boolean downloadFile(String str, OutputStream outputStream, boolean z2, Context context) {
        boolean z3 = false;
        InputStream inputStream = null;
        try {
            try {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setReadTimeout(15000);
                    HttpURLConnection.setFollowRedirects(true);
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int i2 = inputStream.read(bArr);
                        if (i2 == -1) {
                            break;
                        }
                        outputStream.write(bArr, 0, i2);
                        if (z2 && context != null && !isWIFIConnected(context)) {
                            z3 = true;
                            break;
                        }
                    }
                    boolean z4 = !z3;
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outputStream);
                    return z4;
                } catch (Throwable th) {
                    MyLog.e("error while download file. " + th);
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outputStream);
                    return false;
                }
            } catch (IOException e2) {
                MyLog.e("error while download file:" + e2.getClass().getSimpleName());
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outputStream);
                return false;
            }
        } catch (Throwable th2) {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            throw th2;
        }
    }

    public static String downloadXml(Context context, URL url, boolean z2, String str, String str2, String str3) throws Throwable {
        InputStream inputStreamDownloadXmlAsStream;
        try {
            inputStreamDownloadXmlAsStream = downloadXmlAsStream(context, url, z2, str, str3);
            try {
                StringBuilder sb = new StringBuilder(1024);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamDownloadXmlAsStream, str2));
                char[] cArr = new char[4096];
                while (true) {
                    int i2 = bufferedReader.read(cArr);
                    if (-1 == i2) {
                        IOUtils.closeQuietly(inputStreamDownloadXmlAsStream);
                        return sb.toString();
                    }
                    sb.append(cArr, 0, i2);
                }
            } catch (Throwable th) {
                th = th;
                IOUtils.closeQuietly(inputStreamDownloadXmlAsStream);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            inputStreamDownloadXmlAsStream = null;
        }
    }

    public static InputStream downloadXmlAsStream(Context context, URL url, boolean z2, String str, String str2) throws IOException {
        return downloadXmlAsStream(context, url, z2, str, str2, null, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static String uploadFile(String str, Map<String, String> map, File file, String str2) throws IOException {
        if (!file.exists()) {
            return null;
        }
        String name = file.getName();
        try {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");
                if (map != null) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                httpURLConnection.setFixedLengthStreamingMode(name.length() + 77 + ((int) file.length()) + str2.length());
                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.writeBytes("--*****\r\n");
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + str2 + "\";filename=\"" + file.getName() + "\"" + com.xiaomi.push.mpcd.Constants.ENDLINE);
                dataOutputStream.writeBytes(com.xiaomi.push.mpcd.Constants.ENDLINE);
                FileInputStream fileInputStream = new FileInputStream(file);
                try {
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int i2 = fileInputStream.read(bArr);
                        if (i2 == -1) {
                            break;
                        }
                        dataOutputStream.write(bArr, 0, i2);
                        dataOutputStream.flush();
                        throw new IOException("IOException:" + e.getClass().getSimpleName());
                    }
                    dataOutputStream.writeBytes(com.xiaomi.push.mpcd.Constants.ENDLINE);
                    dataOutputStream.writeBytes("--");
                    dataOutputStream.writeBytes("*****");
                    dataOutputStream.writeBytes("--");
                    dataOutputStream.writeBytes(com.xiaomi.push.mpcd.Constants.ENDLINE);
                    dataOutputStream.flush();
                    StringBuffer stringBuffer = new StringBuffer();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new DoneHandlerInputStream(httpURLConnection.getInputStream())));
                    while (true) {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                String string = stringBuffer.toString();
                                IOUtils.closeQuietly(fileInputStream);
                                IOUtils.closeQuietly(bufferedReader);
                                return string;
                            }
                            stringBuffer.append(line);
                        } catch (IOException e2) {
                            e = e2;
                        } catch (Throwable th) {
                            th = th;
                            throw new IOException(th.getMessage());
                        }
                    }
                } catch (IOException e3) {
                    e = e3;
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Throwable th3) {
                IOUtils.closeQuietly(null);
                IOUtils.closeQuietly(file);
                throw th3;
            }
        } catch (IOException e4) {
            e = e4;
        } catch (Throwable th4) {
            th = th4;
        }
    }

    public static void beginDownloadFile(String str, OutputStream outputStream, Context context, boolean z2, PostDownloadHandler postDownloadHandler) {
        new DownloadTask(str, outputStream, postDownloadHandler, z2, context).execute(new Void[0]);
    }

    public static InputStream downloadXmlAsStream(Context context, URL url, boolean z2, String str, String str2, Map<String, String> map, HttpHeaderInfo httpHeaderInfo) throws IOException {
        if (context == null) {
            throw new IllegalArgumentException("context");
        }
        if (url != null) {
            URL url2 = !z2 ? new URL(encryptURL(url.toString())) : url;
            try {
                HttpURLConnection.setFollowRedirects(true);
                HttpURLConnection httpUrlConnection = getHttpUrlConnection(context, url2);
                httpUrlConnection.setConnectTimeout(10000);
                httpUrlConnection.setReadTimeout(15000);
                if (!TextUtils.isEmpty(str)) {
                    httpUrlConnection.setRequestProperty(USER_AGENT, str);
                }
                if (str2 != null) {
                    httpUrlConnection.setRequestProperty("Cookie", str2);
                }
                if (map != null) {
                    for (String str3 : map.keySet()) {
                        httpUrlConnection.setRequestProperty(str3, map.get(str3));
                    }
                }
                if (httpHeaderInfo != null && (url.getProtocol().equals("http") || url.getProtocol().equals("https"))) {
                    httpHeaderInfo.ResponseCode = httpUrlConnection.getResponseCode();
                    if (httpHeaderInfo.AllHeaders == null) {
                        httpHeaderInfo.AllHeaders = new HashMap();
                    }
                    int i2 = 0;
                    while (true) {
                        String headerFieldKey = httpUrlConnection.getHeaderFieldKey(i2);
                        String headerField = httpUrlConnection.getHeaderField(i2);
                        if (headerFieldKey == null && headerField == null) {
                            break;
                        }
                        if (!TextUtils.isEmpty(headerFieldKey) && !TextUtils.isEmpty(headerField)) {
                            httpHeaderInfo.AllHeaders.put(headerFieldKey, headerField);
                        }
                        i2++;
                    }
                }
                return new DoneHandlerInputStream(httpUrlConnection.getInputStream());
            } catch (IOException e2) {
                throw new IOException("IOException:" + e2.getClass().getSimpleName());
            } catch (Throwable th) {
                throw new IOException(th.getMessage());
            }
        }
        throw new IllegalArgumentException("url");
    }

    public static NetworkInfoWrapper getNetworkInfo(Context context) {
        return getNetworkInfo();
    }

    public static String downloadXml(Context context, URL url, String str, String str2, Map<String, String> map, HttpHeaderInfo httpHeaderInfo) throws Throwable {
        InputStream inputStreamDownloadXmlAsStream;
        try {
            inputStreamDownloadXmlAsStream = downloadXmlAsStream(context, url, true, str, str2, map, httpHeaderInfo);
            try {
                StringBuilder sb = new StringBuilder(1024);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamDownloadXmlAsStream, "UTF-8"));
                char[] cArr = new char[4096];
                while (true) {
                    int i2 = bufferedReader.read(cArr);
                    if (-1 != i2) {
                        sb.append(cArr, 0, i2);
                    } else {
                        IOUtils.closeQuietly(inputStreamDownloadXmlAsStream);
                        return sb.toString();
                    }
                }
            } catch (Throwable th) {
                th = th;
                IOUtils.closeQuietly(inputStreamDownloadXmlAsStream);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            inputStreamDownloadXmlAsStream = null;
        }
    }

    public static boolean downloadFile(String str, OutputStream outputStream, Context context) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            HttpURLConnection.setFollowRedirects(true);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] bArr = new byte[1024];
            while (true) {
                int i2 = inputStream.read(bArr);
                if (i2 > 0) {
                    outputStream.write(bArr, 0, i2);
                } else {
                    inputStream.close();
                    outputStream.close();
                    return true;
                }
            }
        } catch (IOException e2) {
            MyLog.e("error while download file:" + e2.getClass().getSimpleName());
            return false;
        } catch (Throwable th) {
            MyLog.e("error while download file. " + th);
            return false;
        }
    }
}
