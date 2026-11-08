package com.xiaomi.network;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.network.NameValuePair;
import com.xiaomi.channel.commonutils.network.Network;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class HttpUtils {

    public static class DefaultHttpGetProcessor extends HttpProcessor {
        public DefaultHttpGetProcessor() {
            super(1);
        }

        @Override // com.xiaomi.network.HttpProcessor
        public String visit(Context context, String str, List<NameValuePair> list) throws IOException {
            if (list == null) {
                return Network.downloadXml(context, new URL(str));
            }
            Uri.Builder builderBuildUpon = Uri.parse(str).buildUpon();
            for (NameValuePair nameValuePair : list) {
                builderBuildUpon.appendQueryParameter(nameValuePair.getName(), nameValuePair.getValue());
            }
            return Network.downloadXml(context, new URL(builderBuildUpon.toString()));
        }
    }

    public static String get(Context context, String str, List<NameValuePair> list) {
        return httpRequest(context, str, list, new DefaultHttpGetProcessor(), true);
    }

    static int getHttpGetTxtTraffic(int i2, int i3) {
        return (((i3 + 243) / 1448) * 132) + 1080 + i2 + i3;
    }

    static int getHttpPostTxtTraffic(int i2, int i3, int i4) {
        return (((i3 + 200) / 1448) * 132) + 1011 + i3 + i2 + i4;
    }

    static int getPostDataLength(List<NameValuePair> list) {
        int length = 0;
        for (NameValuePair nameValuePair : list) {
            if (!TextUtils.isEmpty(nameValuePair.getName())) {
                length += nameValuePair.getName().length();
            }
            if (!TextUtils.isEmpty(nameValuePair.getValue())) {
                length += nameValuePair.getValue().length();
            }
        }
        return length * 2;
    }

    static int getStringUTF8Length(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        try {
            return str.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException unused) {
            return 0;
        }
    }

    private static int getTraffic(HttpProcessor httpProcessor, String str, List<NameValuePair> list, String str2) {
        if (httpProcessor.getRequestType() == 1) {
            return getHttpGetTxtTraffic(str.length(), getStringUTF8Length(str2));
        }
        if (httpProcessor.getRequestType() != 2) {
            return -1;
        }
        return getHttpPostTxtTraffic(str.length(), getPostDataLength(list), getStringUTF8Length(str2));
    }

    public static String httpRequest(Context context, String str, List<NameValuePair> list, HttpProcessor httpProcessor) {
        return httpRequest(context, str, list, httpProcessor, true);
    }

    /* JADX WARN: Code duplicated, block: B:50:0x00ad A[Catch: MalformedURLException -> 0x0026, TRY_ENTER, TryCatch #3 {MalformedURLException -> 0x0026, blocks: (B:4:0x000f, B:6:0x0016, B:8:0x0020, B:13:0x002a, B:15:0x0030, B:16:0x0033, B:17:0x0038, B:19:0x003e, B:21:0x0047, B:23:0x004f, B:50:0x00ad, B:51:0x00bf), top: B:62:0x000f }] */
    public static String httpRequest(Context context, String str, List<NameValuePair> list, HttpProcessor httpProcessor, boolean z2) {
        Fallback fallback;
        IOException iOException;
        String str2;
        String str3;
        if (Network.hasNetwork(context)) {
            try {
                ArrayList<String> arrayList = new ArrayList<>();
                if (z2) {
                    Fallback fallbacksByURL = HostManager.getInstance().getFallbacksByURL(str);
                    if (fallbacksByURL != null) {
                        arrayList = fallbacksByURL.getUrls(str);
                    }
                    fallback = fallbacksByURL;
                } else {
                    fallback = null;
                }
                if (!arrayList.contains(str)) {
                    arrayList.add(str);
                }
                String str4 = null;
                for (String str5 : arrayList) {
                    ArrayList arrayList2 = list != null ? new ArrayList(list) : null;
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    try {
                        if (!httpProcessor.prepare(context, str5, arrayList2)) {
                            return str4;
                        }
                        String strVisit = httpProcessor.visit(context, str5, arrayList2);
                        try {
                            if (!TextUtils.isEmpty(strVisit)) {
                                if (fallback != null) {
                                    try {
                                        fallback.succeedUrl(str5, System.currentTimeMillis() - jCurrentTimeMillis, getTraffic(httpProcessor, str5, arrayList2, strVisit));
                                    } catch (IOException e2) {
                                        iOException = e2;
                                        str2 = strVisit;
                                    }
                                }
                                return strVisit;
                            }
                            if (fallback != null) {
                                str3 = strVisit;
                                try {
                                    fallback.failedUrl(str5, System.currentTimeMillis() - jCurrentTimeMillis, getTraffic(httpProcessor, str5, arrayList2, strVisit), null);
                                } catch (IOException e3) {
                                    e = e3;
                                    String str6 = str3;
                                    iOException = e;
                                    str2 = str6;
                                    if (fallback != null) {
                                        fallback.failedUrl(str5, System.currentTimeMillis() - jCurrentTimeMillis, getTraffic(httpProcessor, str5, arrayList2, str2), iOException);
                                    }
                                    iOException.printStackTrace();
                                    str4 = str2;
                                }
                            } else {
                                str3 = strVisit;
                            }
                            str4 = str3;
                        } catch (IOException e4) {
                            e = e4;
                            str3 = strVisit;
                        }
                        String str7 = str3;
                        iOException = e;
                        str2 = str7;
                    } catch (IOException e5) {
                        iOException = e5;
                        str2 = str4;
                    }
                    if (fallback != null) {
                        fallback.failedUrl(str5, System.currentTimeMillis() - jCurrentTimeMillis, getTraffic(httpProcessor, str5, arrayList2, str2), iOException);
                    }
                    iOException.printStackTrace();
                    str4 = str2;
                }
                return str4;
            } catch (MalformedURLException e6) {
                e6.printStackTrace();
            }
        }
        return null;
    }
}
