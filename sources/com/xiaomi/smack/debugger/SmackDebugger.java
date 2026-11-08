package com.xiaomi.smack.debugger;

import com.xiaomi.smack.PacketListener;

/* JADX INFO: loaded from: classes.dex */
public interface SmackDebugger {
    PacketListener getReaderListener();

    PacketListener getWriterListener();
}
