package com.miui.powerkeeper.thermal.resource;

/* JADX INFO: loaded from: classes.dex */
public class Couple<K, V> {
    private final K mK;
    private final V mV;

    public Couple(K k2, V v2) {
        this.mK = k2;
        this.mV = v2;
    }

    public K getK() {
        return this.mK;
    }

    public V getV() {
        return this.mV;
    }

    public String toString() {
        return "Entry{mK=" + this.mK + ", mV=" + this.mV + '}';
    }
}
