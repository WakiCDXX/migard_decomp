package com.xiaomi.push.headsup;

import android.content.Context;
import android.graphics.Region;
import android.view.View;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* JADX INFO: loaded from: classes.dex */
class TouchableRegionHelper implements InvocationHandler {
    private static final String COMPUTE_NAME = "onComputeInternalInsets";
    private static final String LOAD_CLASS_NAME = "android.view.ViewTreeObserver$OnComputeInternalInsetsListener";
    public static final int TOUCHABLE_INSETS_REGION = 3;
    private Object mClassObject;
    private Region mSetRegion = new Region();
    private ViewContainer mViewContainer;

    public TouchableRegionHelper(ViewContainer viewContainer) {
        this.mViewContainer = viewContainer;
        try {
            Context context = viewContainer.getContext();
            this.mClassObject = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{SystemUtils.loadClass(context, LOAD_CLASS_NAME)}, this);
        } catch (Throwable unused) {
        }
    }

    private void setTouchableInsets(Object obj) {
        View contentView = this.mViewContainer.getContentView();
        if (contentView == null || obj == null) {
            return;
        }
        Object field = JavaCalls.getField(obj, "touchableRegion");
        if (field instanceof Region) {
            this.mSetRegion.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(), contentView.getBottom());
            JavaCalls.callMethod(obj, "setTouchableInsets", 3);
            ((Region) field).set(this.mSetRegion);
        }
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        if (method == null || !COMPUTE_NAME.equalsIgnoreCase(method.getName()) || objArr == null || objArr.length < 1) {
            return null;
        }
        setTouchableInsets(objArr[0]);
        return null;
    }

    public void onAdd() {
        JavaCalls.callMethod(this.mViewContainer.getViewTreeObserver(), "addOnComputeInternalInsetsListener", this.mClassObject);
    }

    public void onRemove() {
        JavaCalls.callMethod(this.mViewContainer.getViewTreeObserver(), "removeOnComputeInternalInsetsListener", this.mClassObject);
    }
}
