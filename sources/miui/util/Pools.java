package miui.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import miui.util.concurrent.ConcurrentRingQueue;

/* JADX INFO: loaded from: classes.dex */
public final class Pools {
    private static final HashMap<Class<?>, InstanceHolder<?>> mInstanceHolderMap = new HashMap<>();
    private static final HashMap<Class<?>, SoftReferenceInstanceHolder<?>> mSoftReferenceInstanceHolderMap = new HashMap<>();
    private static final Pool<StringBuilder> mStringBuilderPool = createSoftReferencePool(new Manager<StringBuilder>() { // from class: miui.util.Pools.1
        @Override // miui.util.Pools.Manager
        public StringBuilder createInstance() {
            return new StringBuilder();
        }

        @Override // miui.util.Pools.Manager
        public void onRelease(StringBuilder sb) {
            sb.setLength(0);
        }
    }, 4);

    static abstract class BasePool<T> implements Pool<T> {
        private final Object mFinalizeGuardian;
        private IInstanceHolder<T> mInstanceHolder;
        private final Manager<T> mManager;
        private final int mSize;

        public BasePool(Manager<T> manager, int i2) {
            Object obj = new Object() { // from class: miui.util.Pools.BasePool.1
                protected void finalize() throws Throwable {
                    try {
                        BasePool.this.close();
                    } finally {
                        super.finalize();
                    }
                }
            };
            this.mFinalizeGuardian = obj;
            if (manager == null || i2 < 1) {
                this.mSize = obj.hashCode();
                throw new IllegalArgumentException("manager cannot be null and size cannot less then 1");
            }
            this.mManager = manager;
            this.mSize = i2;
            T tCreateInstance = manager.createInstance();
            if (tCreateInstance == null) {
                throw new IllegalStateException("manager create instance cannot return null");
            }
            this.mInstanceHolder = createInstanceHolder(tCreateInstance.getClass(), i2);
            doRelease(tCreateInstance);
        }

        @Override // miui.util.Pools.Pool
        public T acquire() {
            return doAcquire();
        }

        @Override // miui.util.Pools.Pool
        public void close() {
            IInstanceHolder<T> iInstanceHolder = this.mInstanceHolder;
            if (iInstanceHolder != null) {
                destroyInstanceHolder(iInstanceHolder, this.mSize);
                this.mInstanceHolder = null;
            }
        }

        abstract IInstanceHolder<T> createInstanceHolder(Class<T> cls, int i2);

        abstract void destroyInstanceHolder(IInstanceHolder<T> iInstanceHolder, int i2);

        protected final T doAcquire() {
            IInstanceHolder<T> iInstanceHolder = this.mInstanceHolder;
            if (iInstanceHolder == null) {
                throw new IllegalStateException("Cannot acquire object after close()");
            }
            T tCreateInstance = iInstanceHolder.get();
            if (tCreateInstance == null && (tCreateInstance = this.mManager.createInstance()) == null) {
                throw new IllegalStateException("manager create instance cannot return null");
            }
            this.mManager.onAcquire(tCreateInstance);
            return tCreateInstance;
        }

        protected final void doRelease(T t2) {
            if (this.mInstanceHolder == null) {
                throw new IllegalStateException("Cannot release object after close()");
            }
            if (t2 == null) {
                return;
            }
            this.mManager.onRelease(t2);
            if (this.mInstanceHolder.put(t2)) {
                return;
            }
            this.mManager.onDestroy(t2);
        }

        @Override // miui.util.Pools.Pool
        public int getSize() {
            if (this.mInstanceHolder == null) {
                return 0;
            }
            return this.mSize;
        }

        @Override // miui.util.Pools.Pool
        public void release(T t2) {
            doRelease(t2);
        }
    }

    private interface IInstanceHolder<T> {
        T get();

        Class<T> getElementClass();

        int getSize();

        boolean put(T t2);

        void resize(int i2);
    }

    private static class InstanceHolder<T> implements IInstanceHolder<T> {
        private final Class<T> mClazz;
        private final ConcurrentRingQueue<T> mQueue;

        InstanceHolder(Class<T> cls, int i2) {
            this.mClazz = cls;
            this.mQueue = new ConcurrentRingQueue<>(i2, false, true);
        }

        @Override // miui.util.Pools.IInstanceHolder
        public T get() {
            return this.mQueue.get();
        }

        @Override // miui.util.Pools.IInstanceHolder
        public Class<T> getElementClass() {
            return this.mClazz;
        }

        @Override // miui.util.Pools.IInstanceHolder
        public int getSize() {
            return this.mQueue.getCapacity();
        }

        @Override // miui.util.Pools.IInstanceHolder
        public boolean put(T t2) {
            return this.mQueue.put(t2);
        }

        @Override // miui.util.Pools.IInstanceHolder
        public synchronized void resize(int i2) {
            try {
                int capacity = i2 + this.mQueue.getCapacity();
                if (capacity <= 0) {
                    synchronized (Pools.mInstanceHolderMap) {
                        Pools.mInstanceHolderMap.remove(getElementClass());
                    }
                } else {
                    if (capacity > 0) {
                        this.mQueue.increaseCapacity(capacity);
                    } else {
                        this.mQueue.decreaseCapacity(-capacity);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public interface Pool<T> {
        T acquire();

        void close();

        int getSize();

        void release(T t2);
    }

    public static class SimplePool<T> extends BasePool<T> {
        SimplePool(Manager<T> manager, int i2) {
            super(manager, i2);
        }

        @Override // miui.util.Pools.BasePool, miui.util.Pools.Pool
        public /* bridge */ /* synthetic */ Object acquire() {
            return super.acquire();
        }

        @Override // miui.util.Pools.BasePool, miui.util.Pools.Pool
        public /* bridge */ /* synthetic */ void close() {
            super.close();
        }

        @Override // miui.util.Pools.BasePool
        final IInstanceHolder<T> createInstanceHolder(Class<T> cls, int i2) {
            return Pools.onPoolCreate(cls, i2);
        }

        @Override // miui.util.Pools.BasePool
        final void destroyInstanceHolder(IInstanceHolder<T> iInstanceHolder, int i2) {
            Pools.onPoolClose((InstanceHolder) iInstanceHolder, i2);
        }

        @Override // miui.util.Pools.BasePool, miui.util.Pools.Pool
        public /* bridge */ /* synthetic */ int getSize() {
            return super.getSize();
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // miui.util.Pools.BasePool, miui.util.Pools.Pool
        public /* bridge */ /* synthetic */ void release(Object obj) {
            super.release(obj);
        }
    }

    private static class SoftReferenceInstanceHolder<T> implements IInstanceHolder<T> {
        private final Class<T> mClazz;
        private volatile SoftReference<T>[] mElements;
        private volatile int mIndex = 0;
        private volatile int mSize;

        SoftReferenceInstanceHolder(Class<T> cls, int i2) {
            this.mClazz = cls;
            this.mSize = i2;
            this.mElements = new SoftReference[i2];
        }

        @Override // miui.util.Pools.IInstanceHolder
        public synchronized T get() {
            int i2 = this.mIndex;
            SoftReference<T>[] softReferenceArr = this.mElements;
            while (i2 != 0) {
                i2--;
                SoftReference<T> softReference = softReferenceArr[i2];
                if (softReference != null) {
                    T t2 = softReference.get();
                    softReferenceArr[i2] = null;
                    if (t2 != null) {
                        this.mIndex = i2;
                        return t2;
                    }
                }
            }
            return null;
        }

        @Override // miui.util.Pools.IInstanceHolder
        public Class<T> getElementClass() {
            return this.mClazz;
        }

        @Override // miui.util.Pools.IInstanceHolder
        public int getSize() {
            return this.mSize;
        }

        @Override // miui.util.Pools.IInstanceHolder
        public synchronized boolean put(T t2) {
            int i2;
            try {
                int i3 = this.mIndex;
                SoftReference<T>[] softReferenceArr = this.mElements;
                if (i3 < this.mSize) {
                    softReferenceArr[i3] = new SoftReference<>(t2);
                    this.mIndex = i3 + 1;
                    return true;
                }
                for (0; i2 < i3; i2 + 1) {
                    SoftReference<T> softReference = softReferenceArr[i2];
                    i2 = (softReference == null || softReference.get() == null) ? 0 : i2 + 1;
                    softReferenceArr[i2] = new SoftReference<>(t2);
                    return true;
                }
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }

        @Override // miui.util.Pools.IInstanceHolder
        public synchronized void resize(int i2) {
            try {
                int i3 = i2 + this.mSize;
                if (i3 <= 0) {
                    synchronized (Pools.mSoftReferenceInstanceHolderMap) {
                        Pools.mSoftReferenceInstanceHolderMap.remove(getElementClass());
                    }
                    return;
                }
                this.mSize = i3;
                SoftReference<T>[] softReferenceArr = this.mElements;
                int i4 = this.mIndex;
                if (i3 > softReferenceArr.length) {
                    SoftReference<T>[] softReferenceArr2 = new SoftReference[i3];
                    System.arraycopy(softReferenceArr, 0, softReferenceArr2, 0, i4);
                    this.mElements = softReferenceArr2;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static class SoftReferencePool<T> extends BasePool<T> {
        SoftReferencePool(Manager<T> manager, int i2) {
            super(manager, i2);
        }

        @Override // miui.util.Pools.BasePool, miui.util.Pools.Pool
        public /* bridge */ /* synthetic */ Object acquire() {
            return super.acquire();
        }

        @Override // miui.util.Pools.BasePool, miui.util.Pools.Pool
        public /* bridge */ /* synthetic */ void close() {
            super.close();
        }

        @Override // miui.util.Pools.BasePool
        final IInstanceHolder<T> createInstanceHolder(Class<T> cls, int i2) {
            return Pools.onSoftReferencePoolCreate(cls, i2);
        }

        @Override // miui.util.Pools.BasePool
        final void destroyInstanceHolder(IInstanceHolder<T> iInstanceHolder, int i2) {
            Pools.onSoftReferencePoolClose((SoftReferenceInstanceHolder) iInstanceHolder, i2);
        }

        @Override // miui.util.Pools.BasePool, miui.util.Pools.Pool
        public /* bridge */ /* synthetic */ int getSize() {
            return super.getSize();
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // miui.util.Pools.BasePool, miui.util.Pools.Pool
        public /* bridge */ /* synthetic */ void release(Object obj) {
            super.release(obj);
        }
    }

    public static <T> SimplePool<T> createSimplePool(Manager<T> manager, int i2) {
        return new SimplePool<>(manager, i2);
    }

    public static <T> SoftReferencePool<T> createSoftReferencePool(Manager<T> manager, int i2) {
        return new SoftReferencePool<>(manager, i2);
    }

    public static Pool<StringBuilder> getStringBuilderPool() {
        return mStringBuilderPool;
    }

    static <T> void onPoolClose(InstanceHolder<T> instanceHolder, int i2) {
        synchronized (mInstanceHolderMap) {
            instanceHolder.resize(-i2);
        }
    }

    static <T> InstanceHolder<T> onPoolCreate(Class<T> cls, int i2) {
        IInstanceHolder iInstanceHolder;
        HashMap<Class<?>, InstanceHolder<?>> map = mInstanceHolderMap;
        synchronized (map) {
            try {
                iInstanceHolder = (InstanceHolder<T>) map.get(cls);
                if (iInstanceHolder == null) {
                    iInstanceHolder = (InstanceHolder<T>) new InstanceHolder(cls, i2);
                    map.put((Class<?>) cls, (InstanceHolder<?>) iInstanceHolder);
                } else {
                    iInstanceHolder.resize(i2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return (InstanceHolder<T>) iInstanceHolder;
    }

    static <T> void onSoftReferencePoolClose(SoftReferenceInstanceHolder<T> softReferenceInstanceHolder, int i2) {
        synchronized (mSoftReferenceInstanceHolderMap) {
            softReferenceInstanceHolder.resize(-i2);
        }
    }

    static <T> SoftReferenceInstanceHolder<T> onSoftReferencePoolCreate(Class<T> cls, int i2) {
        IInstanceHolder iInstanceHolder;
        HashMap<Class<?>, SoftReferenceInstanceHolder<?>> map = mSoftReferenceInstanceHolderMap;
        synchronized (map) {
            try {
                iInstanceHolder = (SoftReferenceInstanceHolder<T>) map.get(cls);
                if (iInstanceHolder == null) {
                    iInstanceHolder = (SoftReferenceInstanceHolder<T>) new SoftReferenceInstanceHolder(cls, i2);
                    map.put((Class<?>) cls, (SoftReferenceInstanceHolder<?>) iInstanceHolder);
                } else {
                    iInstanceHolder.resize(i2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return (SoftReferenceInstanceHolder<T>) iInstanceHolder;
    }

    public static abstract class Manager<T> {
        public abstract T createInstance();

        public void onAcquire(T t2) {
        }

        public void onDestroy(T t2) {
        }

        public void onRelease(T t2) {
        }
    }
}
