package com.xiaomi.smack.util;

import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.service.CommonPacketExtensionProvider;
import com.xiaomi.push.service.PushClientsManager;
import com.xiaomi.push.service.PushServiceConstants;
import com.xiaomi.push.service.RC4Cryption;
import com.xiaomi.smack.Connection;
import com.xiaomi.smack.XMPPException;
import com.xiaomi.smack.packet.CommonPacketExtension;
import com.xiaomi.smack.packet.IQ;
import com.xiaomi.smack.packet.Message;
import com.xiaomi.smack.packet.Packet;
import com.xiaomi.smack.packet.Presence;
import com.xiaomi.smack.packet.StreamError;
import com.xiaomi.smack.packet.XMPPError;
import com.xiaomi.smack.provider.ProviderManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import miui.provider.Notes;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* JADX INFO: loaded from: classes.dex */
public class PacketParserUtils {
    private static XmlPullParser sDecryptedMsgParser;

    private static Object decode(Class cls, String str) throws Exception {
        if (cls.getName().equals("java.lang.String")) {
            return str;
        }
        if (cls.getName().equals("boolean")) {
            return Boolean.valueOf(str);
        }
        if (cls.getName().equals("int")) {
            return Integer.valueOf(str);
        }
        if (cls.getName().equals("long")) {
            return Long.valueOf(str);
        }
        if (cls.getName().equals("float")) {
            return Float.valueOf(str);
        }
        if (cls.getName().equals("double")) {
            return Double.valueOf(str);
        }
        if (cls.getName().equals("java.lang.Class")) {
            return Class.forName(str);
        }
        return null;
    }

    private static String getLanguageAttribute(XmlPullParser xmlPullParser) {
        for (int i2 = 0; i2 < xmlPullParser.getAttributeCount(); i2++) {
            String attributeName = xmlPullParser.getAttributeName(i2);
            if ("xml:lang".equals(attributeName) || ("lang".equals(attributeName) && "xml".equals(xmlPullParser.getAttributePrefix(i2)))) {
                return xmlPullParser.getAttributeValue(i2);
            }
        }
        return null;
    }

    private static String parseContent(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        String str = "";
        while (true) {
            if (xmlPullParser.next() == 3 && xmlPullParser.getDepth() == depth) {
                return str;
            }
            str = str + xmlPullParser.getText();
        }
    }

    public static XMPPError parseError(XmlPullParser xmlPullParser) throws Exception {
        ArrayList arrayList = new ArrayList();
        String attributeValue = "-1";
        boolean z2 = false;
        String attributeValue2 = null;
        String attributeValue3 = null;
        for (int i2 = 0; i2 < xmlPullParser.getAttributeCount(); i2++) {
            if (xmlPullParser.getAttributeName(i2).equals("code")) {
                attributeValue = xmlPullParser.getAttributeValue("", "code");
            }
            if (xmlPullParser.getAttributeName(i2).equals("type")) {
                attributeValue3 = xmlPullParser.getAttributeValue("", "type");
            }
            if (xmlPullParser.getAttributeName(i2).equals("reason")) {
                attributeValue2 = xmlPullParser.getAttributeValue("", "reason");
            }
        }
        String str = null;
        String strNextText = null;
        while (!z2) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("text")) {
                    strNextText = xmlPullParser.nextText();
                } else {
                    String name = xmlPullParser.getName();
                    String namespace = xmlPullParser.getNamespace();
                    if ("urn:ietf:params:xml:ns:xmpp-stanzas".equals(namespace)) {
                        str = name;
                    } else {
                        arrayList.add(parsePacketExtension(name, namespace, xmlPullParser));
                    }
                }
            } else if (next == 3) {
                if (xmlPullParser.getName().equals(Message.MSG_TYPE_ERROR)) {
                    z2 = true;
                }
            } else if (next == 4) {
                strNextText = xmlPullParser.getText();
            }
        }
        return new XMPPError(Integer.parseInt(attributeValue), attributeValue3 == null ? "cancel" : attributeValue3, attributeValue2, str, strNextText, arrayList);
    }

    public static IQ parseIQ(XmlPullParser xmlPullParser, Connection connection) throws Exception {
        String attributeValue = xmlPullParser.getAttributeValue("", "id");
        String attributeValue2 = xmlPullParser.getAttributeValue("", "to");
        String attributeValue3 = xmlPullParser.getAttributeValue("", "from");
        String attributeValue4 = xmlPullParser.getAttributeValue("", "chid");
        IQ.Type typeFromString = IQ.Type.fromString(xmlPullParser.getAttributeValue("", "type"));
        HashMap map = new HashMap();
        boolean z2 = false;
        for (int i2 = 0; i2 < xmlPullParser.getAttributeCount(); i2++) {
            String attributeName = xmlPullParser.getAttributeName(i2);
            map.put(attributeName, xmlPullParser.getAttributeValue("", attributeName));
        }
        IQ iq = null;
        XMPPError error = null;
        while (!z2) {
            int next = xmlPullParser.next();
            if (next == 2) {
                String name = xmlPullParser.getName();
                String namespace = xmlPullParser.getNamespace();
                if (name.equals(Message.MSG_TYPE_ERROR)) {
                    error = parseError(xmlPullParser);
                } else {
                    iq = new IQ();
                    iq.addExtension(parsePacketExtension(name, namespace, xmlPullParser));
                }
            } else if (next == 3 && xmlPullParser.getName().equals("iq")) {
                z2 = true;
            }
        }
        if (iq == null) {
            if (IQ.Type.GET == typeFromString || IQ.Type.SET == typeFromString) {
                IQ iq2 = new IQ() { // from class: com.xiaomi.smack.util.PacketParserUtils.1
                    @Override // com.xiaomi.smack.packet.IQ
                    public String getChildElementXML() {
                        return null;
                    }
                };
                iq2.setPacketID(attributeValue);
                iq2.setTo(attributeValue3);
                iq2.setFrom(attributeValue2);
                iq2.setType(IQ.Type.ERROR);
                iq2.setChannelId(attributeValue4);
                iq2.setError(new XMPPError(XMPPError.Condition.feature_not_implemented));
                connection.sendPacket(iq2);
                MyLog.e("iq usage error. send packet in packet parser.");
                return null;
            }
            iq = new IQ() { // from class: com.xiaomi.smack.util.PacketParserUtils.2
                @Override // com.xiaomi.smack.packet.IQ
                public String getChildElementXML() {
                    return null;
                }
            };
        }
        iq.setPacketID(attributeValue);
        iq.setTo(attributeValue2);
        iq.setChannelId(attributeValue4);
        iq.setFrom(attributeValue3);
        iq.setType(typeFromString);
        iq.setError(error);
        iq.setAttributes(map);
        return iq;
    }

    public static Packet parseMessage(XmlPullParser xmlPullParser) throws Exception {
        String attributeValue;
        boolean z2 = false;
        String strNextText = null;
        if ("1".equals(xmlPullParser.getAttributeValue("", RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH))) {
            String attributeValue2 = xmlPullParser.getAttributeValue("", "chid");
            String attributeValue3 = xmlPullParser.getAttributeValue("", "id");
            String attributeValue4 = xmlPullParser.getAttributeValue("", "from");
            String attributeValue5 = xmlPullParser.getAttributeValue("", "to");
            String attributeValue6 = xmlPullParser.getAttributeValue("", "type");
            PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(attributeValue2, attributeValue5);
            if (clientLoginInfoByChidAndUserId == null) {
                clientLoginInfoByChidAndUserId = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(attributeValue2, attributeValue4);
            }
            if (clientLoginInfoByChidAndUserId == null) {
                throw new XMPPException("the channel id is wrong while receiving a encrypted message");
            }
            Packet message = null;
            while (!z2) {
                int next = xmlPullParser.next();
                if (next == 2) {
                    if (!RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH.equals(xmlPullParser.getName())) {
                        throw new XMPPException("error while receiving a encrypted message with wrong format");
                    }
                    if (xmlPullParser.next() != 4) {
                        throw new XMPPException("error while receiving a encrypted message with wrong format");
                    }
                    String text = xmlPullParser.getText();
                    if ("5".equals(attributeValue2) || "6".equals(attributeValue2)) {
                        Message message2 = new Message();
                        message2.setChannelId(attributeValue2);
                        message2.setEncrypted(true);
                        message2.setFrom(attributeValue4);
                        message2.setTo(attributeValue5);
                        message2.setPacketID(attributeValue3);
                        message2.setType(attributeValue6);
                        CommonPacketExtension commonPacketExtension = new CommonPacketExtension(RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH, (String) null, (String[]) null, (String[]) null);
                        commonPacketExtension.setText(text);
                        message2.addExtension(commonPacketExtension);
                        return message2;
                    }
                    resetDecryptedMsgParser(RC4Cryption.decrypt(RC4Cryption.generateKeyForRC4(clientLoginInfoByChidAndUserId.security, attributeValue3), text));
                    sDecryptedMsgParser.next();
                    message = parseMessage(sDecryptedMsgParser);
                } else if (next == 3 && xmlPullParser.getName().equals("message")) {
                    z2 = true;
                }
            }
            if (message != null) {
                return message;
            }
            throw new XMPPException("error while receiving a encrypted message with wrong format");
        }
        Message message3 = new Message();
        String attributeValue7 = xmlPullParser.getAttributeValue("", "id");
        if (attributeValue7 == null) {
            attributeValue7 = "ID_NOT_AVAILABLE";
        }
        message3.setPacketID(attributeValue7);
        message3.setTo(xmlPullParser.getAttributeValue("", "to"));
        message3.setFrom(xmlPullParser.getAttributeValue("", "from"));
        message3.setChannelId(xmlPullParser.getAttributeValue("", "chid"));
        message3.setAppId(xmlPullParser.getAttributeValue("", "appid"));
        try {
            attributeValue = xmlPullParser.getAttributeValue("", "transient");
        } catch (Exception unused) {
            attributeValue = null;
        }
        try {
            String attributeValue8 = xmlPullParser.getAttributeValue("", "seq");
            if (!TextUtils.isEmpty(attributeValue8)) {
                message3.setSeq(attributeValue8);
            }
        } catch (Exception unused2) {
        }
        try {
            String attributeValue9 = xmlPullParser.getAttributeValue("", "mseq");
            if (!TextUtils.isEmpty(attributeValue9)) {
                message3.setMSeq(attributeValue9);
            }
        } catch (Exception unused3) {
        }
        try {
            String attributeValue10 = xmlPullParser.getAttributeValue("", "fseq");
            if (!TextUtils.isEmpty(attributeValue10)) {
                message3.setFSeq(attributeValue10);
            }
        } catch (Exception unused4) {
        }
        try {
            String attributeValue11 = xmlPullParser.getAttributeValue("", "status");
            if (!TextUtils.isEmpty(attributeValue11)) {
                message3.setStatus(attributeValue11);
            }
        } catch (Exception unused5) {
        }
        message3.setIsTransient(!TextUtils.isEmpty(attributeValue) && attributeValue.equalsIgnoreCase("true"));
        message3.setType(xmlPullParser.getAttributeValue("", "type"));
        String languageAttribute = getLanguageAttribute(xmlPullParser);
        if (languageAttribute == null || "".equals(languageAttribute.trim())) {
            Packet.getDefaultLanguage();
        } else {
            message3.setLanguage(languageAttribute);
        }
        while (!z2) {
            int next2 = xmlPullParser.next();
            if (next2 == 2) {
                String name = xmlPullParser.getName();
                String namespace = xmlPullParser.getNamespace();
                if (TextUtils.isEmpty(namespace)) {
                    namespace = PushServiceConstants.XM_CHAT_GROUP_NAMESPACE;
                }
                if (name.equals(Notes.Note.SUBJECT)) {
                    getLanguageAttribute(xmlPullParser);
                    message3.setSubject(parseContent(xmlPullParser));
                } else if (name.equals("body")) {
                    String attributeValue12 = xmlPullParser.getAttributeValue("", "encode");
                    String content = parseContent(xmlPullParser);
                    if (TextUtils.isEmpty(attributeValue12)) {
                        message3.setBody(content);
                    } else {
                        message3.setBody(content, attributeValue12);
                    }
                } else if (name.equals("thread")) {
                    if (strNextText == null) {
                        strNextText = xmlPullParser.nextText();
                    }
                } else if (name.equals(Message.MSG_TYPE_ERROR)) {
                    message3.setError(parseError(xmlPullParser));
                } else {
                    message3.addExtension(parsePacketExtension(name, namespace, xmlPullParser));
                }
            } else if (next2 == 3 && xmlPullParser.getName().equals("message")) {
                z2 = true;
            }
        }
        message3.setThread(strNextText);
        return message3;
    }

    public static CommonPacketExtension parsePacketExtension(String str, String str2, XmlPullParser xmlPullParser) throws Exception {
        Object extensionProvider = ProviderManager.getInstance().getExtensionProvider(PushServiceConstants.EXTENSION_ELE_NAME_ALL, PushServiceConstants.XM_CHAT_NAMESPACE);
        if (extensionProvider == null || !(extensionProvider instanceof CommonPacketExtensionProvider)) {
            return null;
        }
        return ((CommonPacketExtensionProvider) extensionProvider).parseExtension(xmlPullParser);
    }

    public static Presence parsePresence(XmlPullParser xmlPullParser) throws Exception {
        Presence.Type typeValueOf = Presence.Type.available;
        String attributeValue = xmlPullParser.getAttributeValue("", "type");
        if (attributeValue != null && !attributeValue.equals("")) {
            try {
                typeValueOf = Presence.Type.valueOf(attributeValue);
            } catch (IllegalArgumentException unused) {
                System.err.println("Found invalid presence type " + attributeValue);
            }
        }
        Presence presence = new Presence(typeValueOf);
        presence.setTo(xmlPullParser.getAttributeValue("", "to"));
        presence.setFrom(xmlPullParser.getAttributeValue("", "from"));
        presence.setChannelId(xmlPullParser.getAttributeValue("", "chid"));
        String attributeValue2 = xmlPullParser.getAttributeValue("", "id");
        if (attributeValue2 == null) {
            attributeValue2 = "ID_NOT_AVAILABLE";
        }
        presence.setPacketID(attributeValue2);
        boolean z2 = false;
        while (!z2) {
            int next = xmlPullParser.next();
            if (next == 2) {
                String name = xmlPullParser.getName();
                String namespace = xmlPullParser.getNamespace();
                if (name.equals("status")) {
                    presence.setStatus(xmlPullParser.nextText());
                } else if (name.equals("priority")) {
                    try {
                        presence.setPriority(Integer.parseInt(xmlPullParser.nextText()));
                    } catch (NumberFormatException unused2) {
                    } catch (IllegalArgumentException unused3) {
                        presence.setPriority(0);
                    }
                } else if (name.equals("show")) {
                    String strNextText = xmlPullParser.nextText();
                    try {
                        presence.setMode(Presence.Mode.valueOf(strNextText));
                    } catch (IllegalArgumentException unused4) {
                        System.err.println("Found invalid presence mode " + strNextText);
                    }
                } else if (name.equals(Message.MSG_TYPE_ERROR)) {
                    presence.setError(parseError(xmlPullParser));
                } else {
                    presence.addExtension(parsePacketExtension(name, namespace, xmlPullParser));
                }
            } else if (next == 3 && xmlPullParser.getName().equals("presence")) {
                z2 = true;
            }
        }
        return presence;
    }

    public static StreamError parseStreamError(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        StreamError streamError = null;
        boolean z2 = false;
        while (!z2) {
            int next = xmlPullParser.next();
            if (next == 2) {
                streamError = new StreamError(xmlPullParser.getName());
            } else if (next == 3 && xmlPullParser.getName().equals(Message.MSG_TYPE_ERROR)) {
                z2 = true;
            }
        }
        return streamError;
    }

    public static Object parseWithIntrospection(String str, Class cls, XmlPullParser xmlPullParser) throws Exception {
        Object objNewInstance = cls.newInstance();
        boolean z2 = false;
        while (!z2) {
            int next = xmlPullParser.next();
            if (next == 2) {
                String name = xmlPullParser.getName();
                String strNextText = xmlPullParser.nextText();
                Class[] clsArr = new Class[0];
                Class<?> returnType = objNewInstance.getClass().getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1), null).getReturnType();
                Object objDecode = decode(returnType, strNextText);
                objNewInstance.getClass().getMethod("set" + Character.toUpperCase(name.charAt(0)) + name.substring(1), returnType).invoke(objNewInstance, objDecode);
            } else if (next == 3 && xmlPullParser.getName().equals(str)) {
                z2 = true;
            }
        }
        return objNewInstance;
    }

    private static void resetDecryptedMsgParser(byte[] bArr) throws XmlPullParserException {
        if (sDecryptedMsgParser == null) {
            try {
                XmlPullParser xmlPullParserNewPullParser = XmlPullParserFactory.newInstance().newPullParser();
                sDecryptedMsgParser = xmlPullParserNewPullParser;
                xmlPullParserNewPullParser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
            } catch (XmlPullParserException e2) {
                e2.printStackTrace();
            }
        }
        sDecryptedMsgParser.setInput(new InputStreamReader(new ByteArrayInputStream(bArr)));
    }
}
