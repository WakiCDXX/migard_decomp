package com.xiaomi.push.clientreport;

import android.content.Context;
import com.xiaomi.slim.Blob;
import com.xiaomi.smack.PacketListener;
import com.xiaomi.smack.filter.PacketFilter;
import com.xiaomi.smack.packet.Packet;

/* JADX INFO: loaded from: classes.dex */
public class DataReceiveListener implements PacketListener, PacketFilter {
    private Context mContext;

    public DataReceiveListener(Context context) {
        this.mContext = context;
    }

    @Override // com.xiaomi.smack.filter.PacketFilter
    public boolean accept(Packet packet) {
        return true;
    }

    @Override // com.xiaomi.smack.PacketListener
    public void process(Blob blob) {
        if (blob != null && blob.getChannelId() == 0 && Blob.CMD_PING.equals(blob.getCmd())) {
            PowerStatsHelper.onPong(this.mContext);
        } else {
            PowerStatsHelper.onReceiveMsg(this.mContext);
        }
    }

    @Override // com.xiaomi.smack.PacketListener
    public void processPacket(Packet packet) {
        PowerStatsHelper.onReceiveMsg(this.mContext);
    }
}
