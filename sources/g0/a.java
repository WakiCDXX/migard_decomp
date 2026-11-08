package g0;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/* JADX INFO: compiled from: TBaseHelper.java */
/* JADX INFO: loaded from: classes.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final Comparator f1859a = new b();

    /* JADX INFO: compiled from: TBaseHelper.java */
    private static class b implements Comparator {
        private b() {
        }

        @Override // java.util.Comparator
        public int compare(Object obj, Object obj2) {
            if (obj == null && obj2 == null) {
                return 0;
            }
            if (obj == null) {
                return -1;
            }
            if (obj2 == null) {
                return 1;
            }
            if (obj instanceof List) {
                return a.h((List) obj, (List) obj2);
            }
            if (obj instanceof Set) {
                return a.j((Set) obj, (Set) obj2);
            }
            if (obj instanceof Map) {
                return a.i((Map) obj, (Map) obj2);
            }
            return obj instanceof byte[] ? a.m((byte[]) obj, (byte[]) obj2) : a.f((Comparable) obj, (Comparable) obj2);
        }
    }

    public static int a(ByteBuffer byteBuffer, byte[] bArr, int i2) {
        int iRemaining = byteBuffer.remaining();
        System.arraycopy(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), bArr, i2, iRemaining);
        return iRemaining;
    }

    public static byte[] b(ByteBuffer byteBuffer) {
        if (r(byteBuffer)) {
            return byteBuffer.array();
        }
        byte[] bArr = new byte[byteBuffer.remaining()];
        a(byteBuffer, bArr, 0);
        return bArr;
    }

    public static int c(byte b2, byte b3) {
        if (b2 < b3) {
            return -1;
        }
        return b3 < b2 ? 1 : 0;
    }

    public static int d(int i2, int i3) {
        if (i2 < i3) {
            return -1;
        }
        return i3 < i2 ? 1 : 0;
    }

    public static int e(long j2, long j3) {
        if (j2 < j3) {
            return -1;
        }
        return j3 < j2 ? 1 : 0;
    }

    public static int f(Comparable comparable, Comparable comparable2) {
        return comparable.compareTo(comparable2);
    }

    public static int g(String str, String str2) {
        return str.compareTo(str2);
    }

    public static int h(List list, List list2) {
        int iD = d(list.size(), list2.size());
        if (iD != 0) {
            return iD;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            int iCompare = f1859a.compare(list.get(i2), list2.get(i2));
            if (iCompare != 0) {
                return iCompare;
            }
        }
        return 0;
    }

    public static int i(Map map, Map map2) {
        int iD = d(map.size(), map2.size());
        if (iD != 0) {
            return iD;
        }
        Comparator comparator = f1859a;
        TreeMap treeMap = new TreeMap(comparator);
        treeMap.putAll(map);
        Iterator it = treeMap.entrySet().iterator();
        TreeMap treeMap2 = new TreeMap(comparator);
        treeMap2.putAll(map2);
        Iterator it2 = treeMap2.entrySet().iterator();
        while (it.hasNext() && it2.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Map.Entry entry2 = (Map.Entry) it2.next();
            Comparator comparator2 = f1859a;
            int iCompare = comparator2.compare(entry.getKey(), entry2.getKey());
            if (iCompare != 0) {
                return iCompare;
            }
            int iCompare2 = comparator2.compare(entry.getValue(), entry2.getValue());
            if (iCompare2 != 0) {
                return iCompare2;
            }
        }
        return 0;
    }

    public static int j(Set set, Set set2) {
        int iD = d(set.size(), set2.size());
        if (iD != 0) {
            return iD;
        }
        Comparator comparator = f1859a;
        TreeSet treeSet = new TreeSet(comparator);
        treeSet.addAll(set);
        TreeSet treeSet2 = new TreeSet(comparator);
        treeSet2.addAll(set2);
        Iterator it = treeSet.iterator();
        Iterator it2 = treeSet2.iterator();
        while (it.hasNext() && it2.hasNext()) {
            int iCompare = f1859a.compare(it.next(), it2.next());
            if (iCompare != 0) {
                return iCompare;
            }
        }
        return 0;
    }

    public static int k(short s2, short s3) {
        if (s2 < s3) {
            return -1;
        }
        return s3 < s2 ? 1 : 0;
    }

    public static int l(boolean z2, boolean z3) {
        return Boolean.valueOf(z2).compareTo(Boolean.valueOf(z3));
    }

    public static int m(byte[] bArr, byte[] bArr2) {
        int iD = d(bArr.length, bArr2.length);
        if (iD != 0) {
            return iD;
        }
        for (int i2 = 0; i2 < bArr.length; i2++) {
            int iC = c(bArr[i2], bArr2[i2]);
            if (iC != 0) {
                return iC;
            }
        }
        return 0;
    }

    public static ByteBuffer n(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return null;
        }
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(new byte[byteBuffer.remaining()]);
        if (byteBuffer.hasArray()) {
            System.arraycopy(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBufferWrap.array(), 0, byteBuffer.remaining());
        } else {
            byteBuffer.slice().get(byteBufferWrap.array());
        }
        return byteBufferWrap;
    }

    public static String o(byte b2) {
        return Integer.toHexString((b2 | 256) & 511).toUpperCase().substring(1);
    }

    public static ByteBuffer p(ByteBuffer byteBuffer) {
        return r(byteBuffer) ? byteBuffer : ByteBuffer.wrap(b(byteBuffer));
    }

    public static void q(ByteBuffer byteBuffer, StringBuilder sb) {
        byte[] bArrArray = byteBuffer.array();
        int iArrayOffset = byteBuffer.arrayOffset();
        int iLimit = byteBuffer.limit();
        int i2 = iLimit - iArrayOffset > 128 ? iArrayOffset + 128 : iLimit;
        for (int i3 = iArrayOffset; i3 < i2; i3++) {
            if (i3 > iArrayOffset) {
                sb.append(" ");
            }
            sb.append(o(bArrArray[i3]));
        }
        if (iLimit != i2) {
            sb.append("...");
        }
    }

    public static boolean r(ByteBuffer byteBuffer) {
        return byteBuffer.hasArray() && byteBuffer.position() == 0 && byteBuffer.arrayOffset() == 0 && byteBuffer.remaining() == byteBuffer.capacity();
    }
}
