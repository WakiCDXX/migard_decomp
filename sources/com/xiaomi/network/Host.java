package com.xiaomi.network;

import java.net.InetSocketAddress;

/* JADX INFO: loaded from: classes.dex */
public final class Host {
    private String hostAddress;
    private int port;

    public Host(String str, int i2) {
        this.hostAddress = str;
        this.port = i2;
    }

    public static InetSocketAddress from(String str, int i2) {
        Host host = parse(str, i2);
        return new InetSocketAddress(host.getHost(), host.getPort());
    }

    public static Host parse(String str, int i2) {
        int iLastIndexOf = str.lastIndexOf(":");
        if (iLastIndexOf != -1) {
            String strSubstring = str.substring(0, iLastIndexOf);
            try {
                int i3 = Integer.parseInt(str.substring(iLastIndexOf + 1));
                if (i3 > 0) {
                    i2 = i3;
                }
            } catch (NumberFormatException unused) {
            }
            str = strSubstring;
        }
        return new Host(str, i2);
    }

    public String getHost() {
        return this.hostAddress;
    }

    public int getPort() {
        return this.port;
    }

    public String toString() {
        if (this.port <= 0) {
            return this.hostAddress;
        }
        return this.hostAddress + ":" + this.port;
    }
}
