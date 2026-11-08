package miui.util;

import java.lang.ref.SoftReference;

/* JADX INFO: loaded from: classes.dex */
public abstract class SoftReferenceSingleton<T> {
    private SoftReference<T> mInstance = null;

    protected abstract T createInstance();

    public final T get() {
        T tCreateInstance;
        synchronized (this) {
            try {
                SoftReference<T> softReference = this.mInstance;
                if (softReference == null || (tCreateInstance = softReference.get()) == null) {
                    tCreateInstance = createInstance();
                    this.mInstance = new SoftReference<>(tCreateInstance);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return tCreateInstance;
    }
}
