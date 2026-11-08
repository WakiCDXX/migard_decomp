package com.xiaomi.channel.commonutils.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CollectionUtils {
    public static <T> List<T> appendFromPosition(List<T> list, List<T> list2, int i2) {
        if (list == null || list.isEmpty()) {
            ArrayList arrayList = new ArrayList();
            if (list2 != null) {
                arrayList.addAll(list2);
            }
            return arrayList;
        }
        if (list2 == null || list2.isEmpty()) {
            return list;
        }
        if (i2 > list.size()) {
            i2 = list.size();
        }
        ArrayList arrayList2 = new ArrayList(list);
        arrayList2.addAll(i2, list2);
        return arrayList2;
    }

    public static <T> ArrayList<T> copyFrom(List<T> list) {
        if (list == null) {
            return null;
        }
        return new ArrayList<>(list);
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> List<T> notNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static <T> List<T> replaceFromPosition(List<T> list, List<T> list2, int i2) {
        if (list == null || list.isEmpty()) {
            return list2;
        }
        if (list2 == null || list2.isEmpty()) {
            return list;
        }
        if (i2 > list.size()) {
            i2 = list.size();
        }
        ArrayList arrayList = new ArrayList(list);
        int size = list2.size() + i2;
        arrayList.addAll(i2, list2);
        while (arrayList.size() > size) {
            arrayList.remove(arrayList.size() - 1);
        }
        return arrayList;
    }
}
