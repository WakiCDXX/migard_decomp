package com.xiaomi.smack.provider;

import com.xiaomi.smack.packet.PacketExtension;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public interface PacketExtensionProvider {
    PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception;
}
