package com.xiaomi.onetrack.util;

import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
final class h implements Comparator<String> {
    h() {
    }

    @Override // java.util.Comparator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compare(String str, String str2) {
        return str.compareToIgnoreCase(str2);
    }
}
