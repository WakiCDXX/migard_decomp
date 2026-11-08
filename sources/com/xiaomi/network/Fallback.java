package com.xiaomi.network;

import android.text.TextUtils;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class Fallback {
    protected static final String DEFAULT_DOMAIN = "s.mi1.cc";
    public static final double DEFAULT_UPLOAD_RATIO = 0.1d;
    public String city;
    public String country;
    public String host;
    public String ip;
    public String isp;
    private String mISP;
    public String networkLabel;
    public String province;
    private long timestamp;
    protected String xforward;
    private ArrayList<WeightedHost> fallbackHosts = new ArrayList<>();
    private double mPercent = 0.1d;
    private String mDomain = DEFAULT_DOMAIN;
    private long effectDuration = 86400000;

    public Fallback(String str) {
        this.networkLabel = "";
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("the host is empty");
        }
        this.timestamp = System.currentTimeMillis();
        this.fallbackHosts.add(new WeightedHost(str, -1));
        this.networkLabel = HostManager.getActiveNetworkLabel();
        this.host = str;
    }

    private synchronized void deleteWeightedHost(String str) {
        Iterator<WeightedHost> it = this.fallbackHosts.iterator();
        while (it.hasNext()) {
            if (TextUtils.equals(it.next().host, str)) {
                it.remove();
            }
        }
    }

    public void accessHost(String str, int i2, long j2, long j3, Exception exc) {
        accessHost(str, new AccessHistory(i2, j2, j3, exc));
    }

    public synchronized void addHost(String str) {
        addHost(new WeightedHost(str));
    }

    public synchronized void addPreferredHost(String[] strArr) {
        int i2;
        try {
            int size = this.fallbackHosts.size() - 1;
            while (true) {
                i2 = 0;
                if (size < 0) {
                    break;
                }
                int length = strArr.length;
                while (i2 < length) {
                    if (TextUtils.equals(this.fallbackHosts.get(size).host, strArr[i2])) {
                        this.fallbackHosts.remove(size);
                        break;
                    }
                    i2++;
                }
                size--;
            }
            Iterator<WeightedHost> it = this.fallbackHosts.iterator();
            int i3 = 0;
            while (it.hasNext()) {
                int i4 = it.next().weight;
                if (i4 > i3) {
                    i3 = i4;
                }
            }
            while (i2 < strArr.length) {
                addHost(new WeightedHost(strArr[i2], (strArr.length + i3) - i2));
                i2++;
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public void failedHost(String str, long j2, long j3, Exception exc) {
        accessHost(str, -1, j2, j3, exc);
    }

    public void failedUrl(String str, long j2, long j3, Exception exc) {
        try {
            failedHost(new URL(str).getHost(), j2, j3, exc);
        } catch (MalformedURLException unused) {
        }
    }

    public synchronized Fallback fromJSON(JSONObject jSONObject) throws JSONException {
        this.networkLabel = jSONObject.optString("net");
        this.effectDuration = jSONObject.getLong("ttl");
        this.mPercent = jSONObject.getDouble("pct");
        this.timestamp = jSONObject.getLong("ts");
        this.city = jSONObject.optString("city");
        this.province = jSONObject.optString("prv");
        this.country = jSONObject.optString("cty");
        this.isp = jSONObject.optString("isp");
        this.ip = jSONObject.optString("ip");
        this.host = jSONObject.optString("host");
        this.xforward = jSONObject.optString("xf");
        JSONArray jSONArray = jSONObject.getJSONArray("fbs");
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            addHost(new WeightedHost().fromJSON(jSONArray.getJSONObject(i2)));
        }
        return this;
    }

    public String getDomainName() {
        String str = this.mDomain;
        return str == null ? DEFAULT_DOMAIN : str;
    }

    public synchronized ArrayList<String> getHosts() {
        return getHosts(false);
    }

    public synchronized String getISP() {
        if (!TextUtils.isEmpty(this.mISP)) {
            return this.mISP;
        }
        if (TextUtils.isEmpty(this.isp)) {
            return "hardcode_isp";
        }
        String strJoin = XMStringUtils.join(new String[]{this.isp, this.province, this.city, this.country, this.ip}, RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
        this.mISP = strJoin;
        return strJoin;
    }

    public double getPercent() {
        double d2 = this.mPercent;
        if (d2 < 1.0E-5d) {
            return 0.1d;
        }
        return d2;
    }

    public ArrayList<String> getUrls(String str) throws MalformedURLException {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("the url is empty.");
        }
        URL url = new URL(str);
        if (!TextUtils.equals(url.getHost(), this.host)) {
            throw new IllegalArgumentException("the url is not supported by the fallback");
        }
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator<String> it = getHosts(true).iterator();
        while (it.hasNext()) {
            Host host = Host.parse(it.next(), url.getPort());
            arrayList.add(new URL(url.getProtocol(), host.getHost(), host.getPort(), url.getFile()).toString());
        }
        return arrayList;
    }

    ArrayList<WeightedHost> getWeightedHost() {
        return this.fallbackHosts;
    }

    public boolean isEffective() {
        return System.currentTimeMillis() - this.timestamp < this.effectDuration;
    }

    boolean isExpired() {
        long j2 = this.effectDuration;
        if (864000000 >= j2) {
            j2 = 864000000;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j3 = this.timestamp;
        return jCurrentTimeMillis - j3 > j2 || (jCurrentTimeMillis - j3 > this.effectDuration && this.networkLabel.startsWith("WIFI-"));
    }

    public boolean match() {
        return TextUtils.equals(this.networkLabel, HostManager.getActiveNetworkLabel());
    }

    public void setDomainName(String str) {
        this.mDomain = str;
    }

    public void setEffectiveDuration(long j2) {
        if (j2 > 0) {
            this.effectDuration = j2;
            return;
        }
        throw new IllegalArgumentException("the duration is invalid " + j2);
    }

    public void setPercent(double d2) {
        this.mPercent = d2;
    }

    public void succeedHost(String str, long j2, long j3) {
        accessHost(str, 0, j2, j3, null);
    }

    public void succeedUrl(String str, long j2, long j3) {
        try {
            succeedHost(new URL(str).getHost(), j2, j3);
        } catch (MalformedURLException unused) {
        }
    }

    public synchronized JSONObject toJSON() throws JSONException {
        JSONObject jSONObject;
        try {
            jSONObject = new JSONObject();
            jSONObject.put("net", this.networkLabel);
            jSONObject.put("ttl", this.effectDuration);
            jSONObject.put("pct", this.mPercent);
            jSONObject.put("ts", this.timestamp);
            jSONObject.put("city", this.city);
            jSONObject.put("prv", this.province);
            jSONObject.put("cty", this.country);
            jSONObject.put("isp", this.isp);
            jSONObject.put("ip", this.ip);
            jSONObject.put("host", this.host);
            jSONObject.put("xf", this.xforward);
            JSONArray jSONArray = new JSONArray();
            Iterator<WeightedHost> it = this.fallbackHosts.iterator();
            while (it.hasNext()) {
                jSONArray.put(it.next().toJSON());
            }
            jSONObject.put("fbs", jSONArray);
        } catch (Throwable th) {
            throw th;
        }
        return jSONObject;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.networkLabel);
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append(getISP());
        for (WeightedHost weightedHost : this.fallbackHosts) {
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append(weightedHost.toString());
        }
        sb.append(ShellUtils.COMMAND_LINE_END);
        return sb.toString();
    }

    public synchronized void accessHost(String str, AccessHistory accessHistory) {
        for (WeightedHost weightedHost : this.fallbackHosts) {
            if (TextUtils.equals(str, weightedHost.host)) {
                weightedHost.addAccessHistory(accessHistory);
                break;
            }
        }
    }

    public synchronized ArrayList<String> getHosts(boolean z2) {
        ArrayList<String> arrayList;
        try {
            int size = this.fallbackHosts.size();
            WeightedHost[] weightedHostArr = new WeightedHost[size];
            this.fallbackHosts.toArray(weightedHostArr);
            Arrays.sort(weightedHostArr);
            arrayList = new ArrayList<>();
            for (int i2 = 0; i2 < size; i2++) {
                WeightedHost weightedHost = weightedHostArr[i2];
                if (z2) {
                    arrayList.add(weightedHost.host);
                } else {
                    int iIndexOf = weightedHost.host.indexOf(":");
                    if (iIndexOf != -1) {
                        arrayList.add(weightedHost.host.substring(0, iIndexOf));
                    } else {
                        arrayList.add(weightedHost.host);
                    }
                }
            }
        } catch (Throwable th) {
            throw th;
        }
        return arrayList;
    }

    public boolean match(Fallback fallback) {
        return TextUtils.equals(this.networkLabel, fallback.networkLabel);
    }

    synchronized void addHost(WeightedHost weightedHost) {
        deleteWeightedHost(weightedHost.host);
        this.fallbackHosts.add(weightedHost);
    }
}
