package com.xiaomi.push.service;

import com.xiaomi.smack.packet.CommonPacketExtension;
import com.xiaomi.smack.provider.PacketExtensionProvider;
import com.xiaomi.smack.provider.ProviderManager;
import com.xiaomi.smack.util.StringUtils;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* JADX INFO: loaded from: classes.dex */
public class CommonPacketExtensionProvider implements PacketExtensionProvider {
    public static CommonPacketExtension parseExtensionFromStartTag(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        String[] strArr;
        String[] strArr2;
        String strTrim;
        ArrayList arrayList;
        if (xmlPullParser.getEventType() != 2) {
            return null;
        }
        String name = xmlPullParser.getName();
        String namespace = xmlPullParser.getNamespace();
        if (xmlPullParser.getAttributeCount() > 0) {
            String[] strArr3 = new String[xmlPullParser.getAttributeCount()];
            String[] strArr4 = new String[xmlPullParser.getAttributeCount()];
            for (int i2 = 0; i2 < xmlPullParser.getAttributeCount(); i2++) {
                strArr3[i2] = xmlPullParser.getAttributeName(i2);
                strArr4[i2] = StringUtils.unescapeFromXML(xmlPullParser.getAttributeValue(i2));
            }
            strArr = strArr3;
            strTrim = null;
            arrayList = null;
            strArr2 = strArr4;
        } else {
            strArr = null;
            strArr2 = null;
            strTrim = null;
            arrayList = null;
        }
        while (true) {
            int next = xmlPullParser.next();
            if (next == 3) {
                return new CommonPacketExtension(name, namespace, strArr, strArr2, strTrim, arrayList);
            }
            if (next == 4) {
                strTrim = xmlPullParser.getText().trim();
            } else if (next == 2) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                CommonPacketExtension extensionFromStartTag = parseExtensionFromStartTag(xmlPullParser);
                if (extensionFromStartTag != null) {
                    arrayList.add(extensionFromStartTag);
                }
            }
        }
    }

    public static CommonPacketExtension parseExtensionFromXpp(String str) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParserNewPullParser = XmlPullParserFactory.newInstance().newPullParser();
        xmlPullParserNewPullParser.setInput(new StringReader(str));
        int eventType = xmlPullParserNewPullParser.getEventType();
        while (eventType != 1 && eventType != 2) {
            eventType = xmlPullParserNewPullParser.next();
        }
        if (eventType == 2) {
            return parseExtensionFromStartTag(xmlPullParserNewPullParser);
        }
        return null;
    }

    public void register() {
        ProviderManager.getInstance().addExtensionProvider(PushServiceConstants.EXTENSION_ELE_NAME_ALL, PushServiceConstants.XM_CHAT_NAMESPACE, this);
    }

    @Override // com.xiaomi.smack.provider.PacketExtensionProvider
    public CommonPacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        int eventType = xmlPullParser.getEventType();
        while (eventType != 1 && eventType != 2) {
            eventType = xmlPullParser.next();
        }
        if (eventType == 2) {
            return parseExtensionFromStartTag(xmlPullParser);
        }
        return null;
    }
}
