package l;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.miui.powerkeeper.event.EventsAggregator;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: compiled from: CommonObserver.java */
/* JADX INFO: loaded from: classes.dex */
abstract class b implements l.a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected Context f2027a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    protected EventsAggregator f2028b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    protected final ArrayList<a> f2029c = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: compiled from: CommonObserver.java */
    static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        WeakReference<Handler> f2030a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        int f2031b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        Map<Integer, Integer> f2032c;

        a(Handler handler, int[] iArr) {
            this.f2030a = new WeakReference<>(handler);
            HashMap map = new HashMap();
            this.f2032c = map;
            if (iArr.length % 2 != 0) {
                map.put(1, Integer.valueOf(iArr[0]));
                this.f2031b = 1;
                return;
            }
            this.f2031b = 0;
            for (int i2 = 0; i2 < iArr.length - 1; i2 += 2) {
                int i3 = i2 + 1;
                this.f2032c.put(Integer.valueOf(iArr[i3]), Integer.valueOf(iArr[i2]));
                this.f2031b |= iArr[i3];
            }
        }

        Handler a() {
            WeakReference<Handler> weakReference = this.f2030a;
            if (weakReference == null) {
                return null;
            }
            return weakReference.get();
        }

        void b(int i2, int i3, int i4) {
            WeakReference<Handler> weakReference = this.f2030a;
            if (weakReference == null || (this.f2031b & i2) != i2) {
                return;
            }
            Handler handler = weakReference.get();
            if (handler == null) {
                this.f2030a = null;
                return;
            }
            Integer num = this.f2032c.get(Integer.valueOf(i2));
            if (num != null) {
                handler.sendMessageAtTime(Message.obtain(handler, num.intValue(), i3, i4), SystemClock.uptimeMillis());
            }
        }

        void c(int i2, int i3, int i4, Object obj) {
            WeakReference<Handler> weakReference = this.f2030a;
            if (weakReference == null || (this.f2031b & i2) != i2) {
                return;
            }
            Handler handler = weakReference.get();
            if (handler == null) {
                this.f2030a = null;
                return;
            }
            Integer num = this.f2032c.get(Integer.valueOf(i2));
            if (num != null) {
                handler.sendMessageAtTime(Message.obtain(handler, num.intValue(), i3, i4, obj), SystemClock.uptimeMillis());
            }
        }

        void d(int i2, Object obj) {
            WeakReference<Handler> weakReference = this.f2030a;
            if (weakReference == null || (this.f2031b & i2) != i2) {
                return;
            }
            Handler handler = weakReference.get();
            if (handler == null) {
                this.f2030a = null;
                return;
            }
            Integer num = this.f2032c.get(Integer.valueOf(i2));
            if (num != null) {
                handler.sendMessageAtTime(Message.obtain(handler, num.intValue(), obj), SystemClock.uptimeMillis());
            }
        }

        public String toString() {
            return "{ " + this.f2031b + ", " + this.f2032c + "}";
        }
    }

    b() {
    }

    private void i(Handler handler) {
        for (int size = this.f2029c.size() - 1; size >= 0; size--) {
            Handler handlerA = this.f2029c.get(size).a();
            if (handlerA == null || handlerA == handler) {
                this.f2029c.remove(size);
            }
        }
    }

    @Override // l.l.a
    public final void a(Handler handler) {
        synchronized (this.f2029c) {
            i(handler);
        }
    }

    @Override // l.l.a
    public final void b(Handler handler, int... iArr) {
        if (iArr == null || iArr.length == 0) {
            return;
        }
        a aVar = new a(handler, iArr);
        synchronized (this.f2029c) {
            i(handler);
            this.f2029c.add(aVar);
        }
    }

    protected final void c(int i2, int i3) {
        d(1, i2, i3);
    }

    protected final void d(int i2, int i3, int i4) {
        synchronized (this.f2029c) {
            try {
                Iterator<a> it = this.f2029c.iterator();
                while (it.hasNext()) {
                    it.next().b(i2, i3, i4);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected final void e(int i2, int i3, int i4, Object obj) {
        synchronized (this.f2029c) {
            try {
                Iterator<a> it = this.f2029c.iterator();
                while (it.hasNext()) {
                    it.next().c(i2, i3, i4, obj);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected final void f(int i2, int i3, Object obj) {
        e(1, i2, i3, obj);
    }

    protected final void g(int i2, Object obj) {
        synchronized (this.f2029c) {
            try {
                Iterator<a> it = this.f2029c.iterator();
                while (it.hasNext()) {
                    it.next().d(i2, obj);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected final void h(Object obj) {
        g(1, obj);
    }
}
