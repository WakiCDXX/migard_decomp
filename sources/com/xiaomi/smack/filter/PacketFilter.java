package com.xiaomi.smack.filter;

import com.xiaomi.smack.packet.Packet;

/* JADX INFO: loaded from: classes.dex */
public interface PacketFilter {
    boolean accept(Packet packet);
}
