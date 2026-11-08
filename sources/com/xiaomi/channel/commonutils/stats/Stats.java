package com.xiaomi.channel.commonutils.stats;

import java.util.LinkedList;

/* JADX INFO: loaded from: classes.dex */
public class Stats {
    private static final int MAX_STATS_ITEMS = 100;
    private LinkedList<Item> statsQueue = new LinkedList<>();

    private void checkSize() {
        if (this.statsQueue.size() > 100) {
            this.statsQueue.removeFirst();
        }
    }

    public static Stats instance() {
        return Item.sStats;
    }

    public synchronized void count(int i2) {
        this.statsQueue.add(new Item(i2, (String) null));
        checkSize();
    }

    public synchronized int getCount() {
        return this.statsQueue.size();
    }

    public synchronized LinkedList<Item> getStats() {
        LinkedList<Item> linkedList;
        linkedList = this.statsQueue;
        this.statsQueue = new LinkedList<>();
        return linkedList;
    }

    public synchronized void stat(Object obj) {
        this.statsQueue.add(new Item(0, obj));
        checkSize();
    }

    public static class Item {
        private static final Stats sStats = new Stats();
        public String annotation;
        public int key;
        public Object obj;

        Item(int i2, String str) {
            this.key = i2;
            this.annotation = str;
        }

        Item(int i2, Object obj) {
            this.key = i2;
            this.obj = obj;
        }
    }

    public synchronized void count(int i2, String str) {
        this.statsQueue.add(new Item(i2, str));
        checkSize();
    }
}
