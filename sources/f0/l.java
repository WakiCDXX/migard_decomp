package f0;

import android.content.Context;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedBlockingQueue<a> f1836a = new LinkedBlockingQueue<>(1);

    private class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        Object f1837a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        Method f1838b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        Object[] f1839c;

        public a(Object obj, Method method, Object[] objArr) {
            this.f1837a = obj;
            this.f1838b = method;
            this.f1839c = objArr;
        }
    }

    public class b implements InvocationHandler {
        public b() {
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
            try {
                l.this.f1836a.offer(l.this.new a(obj, method, objArr), 1L, TimeUnit.SECONDS);
                return null;
            } catch (Exception e2) {
                try {
                    e2.printStackTrace();
                    return null;
                } catch (Exception e3) {
                    com.ot.pubsub.util.k.c("MsaSDKHelper", e3.getMessage());
                    return null;
                }
            }
        }
    }

    public String a(Context context) {
        try {
            Class<?> cls = Class.forName("com.bun.miitmdid.core.MdidSdkHelper");
            Class<?> cls2 = Class.forName("com.bun.supplier.IIdentifierListener");
            cls.getDeclaredMethod("InitSdk", Context.class, Boolean.TYPE, cls2).invoke(cls, context, Boolean.TRUE, Proxy.newProxyInstance(context.getClassLoader(), new Class[]{cls2}, new b()));
            a aVarPoll = this.f1836a.poll(1L, TimeUnit.SECONDS);
            if (aVarPoll == null) {
                return "";
            }
            Class[] clsArr = new Class[0];
            return aVarPoll.f1839c[1].getClass().getMethod("getOAID", null).invoke(aVarPoll.f1839c[1], null).toString();
        } catch (Throwable th) {
            com.ot.pubsub.util.k.c("MsaSDKHelper", th.getMessage());
            return "";
        }
    }
}
