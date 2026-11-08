package com.xiaomi.smack.packet;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.string.Base64Coder;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.smack.util.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class Packet {
    protected static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage().toLowerCase();
    private static String DEFAULT_XML_NS = null;
    public static final String ID_NOT_AVAILABLE = "ID_NOT_AVAILABLE";
    public static final DateFormat XEP_0082_UTC_FORMAT;
    private static long id;
    private static String prefix;
    private String chId;
    private XMPPError error;
    private String from;
    private String packageName;
    private List<CommonPacketExtension> packetExtensions;
    private String packetID;
    private final Map<String, Object> properties;
    public long receiveTime;
    private String to;
    private String xmlns;

    static {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        XEP_0082_UTC_FORMAT = simpleDateFormat;
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        prefix = StringUtils.randomString(5) + Constants.ACCEPT_TIME_SEPARATOR_SERVER;
        id = 0L;
    }

    public Packet() {
        this.xmlns = DEFAULT_XML_NS;
        this.packetID = null;
        this.to = null;
        this.from = null;
        this.chId = null;
        this.packageName = null;
        this.packetExtensions = new CopyOnWriteArrayList();
        this.properties = new HashMap();
        this.error = null;
    }

    public static String getDefaultLanguage() {
        return DEFAULT_LANGUAGE;
    }

    public static synchronized String nextID() {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(prefix);
        long j2 = id;
        id = 1 + j2;
        sb.append(Long.toString(j2));
        return sb.toString();
    }

    public static void setDefaultXmlns(String str) {
        DEFAULT_XML_NS = str;
    }

    public void addExtension(CommonPacketExtension commonPacketExtension) {
        this.packetExtensions.add(commonPacketExtension);
    }

    public synchronized void deleteProperty(String str) {
        Map<String, Object> map = this.properties;
        if (map == null) {
            return;
        }
        map.remove(str);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Packet packet = (Packet) obj;
        XMPPError xMPPError = this.error;
        if (xMPPError == null ? packet.error != null : !xMPPError.equals(packet.error)) {
            return false;
        }
        String str = this.from;
        if (str == null ? packet.from != null : !str.equals(packet.from)) {
            return false;
        }
        if (!this.packetExtensions.equals(packet.packetExtensions)) {
            return false;
        }
        String str2 = this.packetID;
        if (str2 == null ? packet.packetID != null : !str2.equals(packet.packetID)) {
            return false;
        }
        String str3 = this.chId;
        if (str3 == null ? packet.chId != null : !str3.equals(packet.chId)) {
            return false;
        }
        Map<String, Object> map = this.properties;
        if (map == null ? packet.properties != null : !map.equals(packet.properties)) {
            return false;
        }
        String str4 = this.to;
        if (str4 == null ? packet.to != null : !str4.equals(packet.to)) {
            return false;
        }
        String str5 = this.xmlns;
        if (str5 != null) {
            if (str5.equals(packet.xmlns)) {
                return true;
            }
        } else if (packet.xmlns == null) {
            return true;
        }
        return false;
    }

    public String getChannelId() {
        return this.chId;
    }

    public XMPPError getError() {
        return this.error;
    }

    public CommonPacketExtension getExtension(String str) {
        return getExtension(str, null);
    }

    public synchronized Collection<CommonPacketExtension> getExtensions() {
        if (this.packetExtensions == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList(this.packetExtensions));
    }

    /* JADX WARN: Code duplicated, block: B:102:0x0129 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:87:0x0123 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    protected synchronized String getExtensionsXML() {
        StringBuilder sb;
        ByteArrayOutputStream byteArrayOutputStream;
        ObjectOutputStream objectOutputStream;
        Exception e2;
        try {
            sb = new StringBuilder();
            Iterator<CommonPacketExtension> it = getExtensions().iterator();
            while (it.hasNext()) {
                sb.append(it.next().toXML());
            }
            Map<String, Object> map = this.properties;
            if (map != null && !map.isEmpty()) {
                sb.append(Base64Coder.decodeString("PHByb3BlcnRpZXMgeG1sbnM9Imh0dHA6Ly93d3cuaml2ZXNvZnR3YXJlLmNvbS94bWxucy94bXBwL3Byb3BlcnRpZXMiPg=="));
                for (String str : getPropertyNames()) {
                    Object property = getProperty(str);
                    sb.append("<property>");
                    sb.append("<name>");
                    sb.append(StringUtils.escapeForXML(str));
                    sb.append("</name>");
                    sb.append("<value type=\"");
                    if (property instanceof Integer) {
                        sb.append("integer\">");
                        sb.append(property);
                        sb.append("</value>");
                    } else if (property instanceof Long) {
                        sb.append("long\">");
                        sb.append(property);
                        sb.append("</value>");
                    } else if (property instanceof Float) {
                        sb.append("float\">");
                        sb.append(property);
                        sb.append("</value>");
                    } else if (property instanceof Double) {
                        sb.append("double\">");
                        sb.append(property);
                        sb.append("</value>");
                    } else if (property instanceof Boolean) {
                        sb.append("boolean\">");
                        sb.append(property);
                        sb.append("</value>");
                    } else if (property instanceof String) {
                        sb.append("string\">");
                        sb.append(StringUtils.escapeForXML((String) property));
                        sb.append("</value>");
                    } else {
                        ObjectOutputStream objectOutputStream2 = null;
                        try {
                            byteArrayOutputStream = new ByteArrayOutputStream();
                            try {
                                objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                try {
                                    try {
                                        objectOutputStream.writeObject(property);
                                        sb.append("java-object\">");
                                        sb.append(StringUtils.encodeBase64(byteArrayOutputStream.toByteArray()));
                                        sb.append("</value>");
                                        try {
                                            objectOutputStream.close();
                                        } catch (Exception unused) {
                                        }
                                    } catch (Exception e3) {
                                        e2 = e3;
                                        e2.printStackTrace();
                                        if (objectOutputStream != null) {
                                            try {
                                                objectOutputStream.close();
                                            } catch (Exception unused2) {
                                            }
                                        }
                                        if (byteArrayOutputStream != null) {
                                        }
                                        sb.append("</property>");
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    objectOutputStream2 = objectOutputStream;
                                    if (objectOutputStream2 != null) {
                                        try {
                                            objectOutputStream2.close();
                                        } catch (Exception unused3) {
                                        }
                                    }
                                    if (byteArrayOutputStream == null) {
                                        throw th;
                                    }
                                    try {
                                        byteArrayOutputStream.close();
                                        throw th;
                                    } catch (Exception unused4) {
                                        throw th;
                                    }
                                }
                            } catch (Exception e4) {
                                e = e4;
                                objectOutputStream = null;
                                e2 = e;
                                e2.printStackTrace();
                                if (objectOutputStream != null) {
                                    objectOutputStream.close();
                                }
                                if (byteArrayOutputStream != null) {
                                    byteArrayOutputStream.close();
                                }
                                sb.append("</property>");
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        } catch (Exception e5) {
                            e = e5;
                            byteArrayOutputStream = null;
                            objectOutputStream = null;
                        } catch (Throwable th3) {
                            th = th3;
                            byteArrayOutputStream = null;
                        }
                        try {
                            byteArrayOutputStream.close();
                        } catch (Exception unused5) {
                        }
                    }
                    sb.append("</property>");
                }
                sb.append("</properties>");
            }
        } catch (Throwable th4) {
            throw th4;
        }
        return sb.toString();
    }

    public String getFrom() {
        return this.from;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getPacketID() {
        if ("ID_NOT_AVAILABLE".equals(this.packetID)) {
            return null;
        }
        if (this.packetID == null) {
            this.packetID = nextID();
        }
        return this.packetID;
    }

    public synchronized Object getProperty(String str) {
        Map<String, Object> map = this.properties;
        if (map == null) {
            return null;
        }
        return map.get(str);
    }

    public synchronized Collection<String> getPropertyNames() {
        if (this.properties == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet(this.properties.keySet()));
    }

    public String getTo() {
        return this.to;
    }

    public String getXmlns() {
        return this.xmlns;
    }

    public int hashCode() {
        String str = this.xmlns;
        int iHashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.packetID;
        int iHashCode2 = (iHashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.to;
        int iHashCode3 = (iHashCode2 + (str3 != null ? str3.hashCode() : 0)) * 31;
        String str4 = this.from;
        int iHashCode4 = (iHashCode3 + (str4 != null ? str4.hashCode() : 0)) * 31;
        String str5 = this.chId;
        int iHashCode5 = (((((iHashCode4 + (str5 != null ? str5.hashCode() : 0)) * 31) + this.packetExtensions.hashCode()) * 31) + this.properties.hashCode()) * 31;
        XMPPError xMPPError = this.error;
        return iHashCode5 + (xMPPError != null ? xMPPError.hashCode() : 0);
    }

    public void removeExtension(CommonPacketExtension commonPacketExtension) {
        this.packetExtensions.remove(commonPacketExtension);
    }

    public void setChannelId(String str) {
        this.chId = str;
    }

    public void setError(XMPPError xMPPError) {
        this.error = xMPPError;
    }

    public void setFrom(String str) {
        this.from = str;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public void setPacketID(String str) {
        this.packetID = str;
    }

    public synchronized void setProperty(String str, Object obj) {
        if (!(obj instanceof Serializable)) {
            throw new IllegalArgumentException("Value must be serialiazble");
        }
        this.properties.put(str, obj);
    }

    public void setTo(String str) {
        this.to = str;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(this.xmlns)) {
            bundle.putString(PushConstants.EXTRA_EXTENSION_NAMESPACE, this.xmlns);
        }
        if (!TextUtils.isEmpty(this.from)) {
            bundle.putString(PushConstants.EXTRA_FROM, this.from);
        }
        if (!TextUtils.isEmpty(this.to)) {
            bundle.putString(PushConstants.EXTRA_TO, this.to);
        }
        if (!TextUtils.isEmpty(this.packetID)) {
            bundle.putString(PushConstants.EXTRA_PACKET_ID, this.packetID);
        }
        if (!TextUtils.isEmpty(this.chId)) {
            bundle.putString(PushConstants.EXTRA_CHID, this.chId);
        }
        XMPPError xMPPError = this.error;
        if (xMPPError != null) {
            bundle.putBundle(PushConstants.EXTRA_ERROR, xMPPError.toBundle());
        }
        List<CommonPacketExtension> list = this.packetExtensions;
        if (list != null) {
            Bundle[] bundleArr = new Bundle[list.size()];
            Iterator<CommonPacketExtension> it = this.packetExtensions.iterator();
            int i2 = 0;
            while (it.hasNext()) {
                Bundle bundle2 = it.next().toBundle();
                if (bundle2 != null) {
                    bundleArr[i2] = bundle2;
                    i2++;
                }
            }
            bundle.putParcelableArray(PushConstants.EXTRA_EXTENSIONS, bundleArr);
        }
        return bundle;
    }

    public abstract String toXML();

    public CommonPacketExtension getExtension(String str, String str2) {
        for (CommonPacketExtension commonPacketExtension : this.packetExtensions) {
            if (str2 == null || str2.equals(commonPacketExtension.getNamespace())) {
                if (str.equals(commonPacketExtension.getElementName())) {
                    return commonPacketExtension;
                }
            }
        }
        return null;
    }

    public Packet(Bundle bundle) {
        this.xmlns = DEFAULT_XML_NS;
        this.packetID = null;
        this.to = null;
        this.from = null;
        this.chId = null;
        this.packageName = null;
        this.packetExtensions = new CopyOnWriteArrayList();
        this.properties = new HashMap();
        this.error = null;
        this.to = bundle.getString(PushConstants.EXTRA_TO);
        this.from = bundle.getString(PushConstants.EXTRA_FROM);
        this.chId = bundle.getString(PushConstants.EXTRA_CHID);
        this.packetID = bundle.getString(PushConstants.EXTRA_PACKET_ID);
        Parcelable[] parcelableArray = bundle.getParcelableArray(PushConstants.EXTRA_EXTENSIONS);
        if (parcelableArray != null) {
            this.packetExtensions = new ArrayList(parcelableArray.length);
            for (Parcelable parcelable : parcelableArray) {
                CommonPacketExtension fromBundle = CommonPacketExtension.parseFromBundle((Bundle) parcelable);
                if (fromBundle != null) {
                    this.packetExtensions.add(fromBundle);
                }
            }
        }
        Bundle bundle2 = bundle.getBundle(PushConstants.EXTRA_ERROR);
        if (bundle2 != null) {
            this.error = new XMPPError(bundle2);
        }
    }
}
