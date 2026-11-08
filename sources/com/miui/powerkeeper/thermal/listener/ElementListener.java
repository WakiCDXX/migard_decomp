package com.miui.powerkeeper.thermal.listener;

import android.annotation.NonNull;
import android.os.Handler;
import android.os.Message;
import b.b;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.cloudcontrol.CloudControlManager;
import com.miui.powerkeeper.thermal.resource.Couple;
import com.miui.powerkeeper.thermal.resource.IDumpable;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public abstract class ElementListener extends AutoRegister implements IListener, IElement, IDumpable {
    public static final int DO_NOT_CHANGE_INDEX = -1;
    protected static final String TAG = "ElementListener";
    protected int mCurrentState = 97;
    protected Handler mElementHandler;
    private int mIndex;
    private Handler mMainHandler;

    private static class ElementHandler extends Handler {
        WeakReference<IElement> mElementReference;

        public ElementHandler(IElement iElement) {
            this.mElementReference = new WeakReference<>(iElement);
        }

        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            this.mElementReference.get().elementChanged(message.what, message.obj, message.arg1, message.arg2);
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println(ShellUtils.COMMAND_LINE_END + getClass().getName() + " index=" + this.mIndex);
    }

    @Override // com.miui.powerkeeper.cloudcontrol.AutoRegister
    protected int getModel() {
        return b.C;
    }

    abstract void init();

    abstract int initCurrentState();

    @Override // com.miui.powerkeeper.thermal.listener.IListener
    public final int initDefaultValue() {
        int iInitCurrentState = initCurrentState();
        this.mCurrentState = iInitCurrentState;
        return iInitCurrentState;
    }

    final void modifyCurrentState(int i2) {
        if (this.mCurrentState != i2) {
            this.mCurrentState = i2;
            stateChangedTo(i2);
        }
    }

    @Override // com.miui.powerkeeper.thermal.listener.IListener
    public final void register(Handler handler, int i2) {
        this.mMainHandler = handler;
        this.mElementHandler = new ElementHandler(this);
        if (i2 != -1) {
            this.mIndex = i2;
            updateCloudData();
        }
        init();
    }

    final void sendExtra(String str, Object obj) {
        Message messageObtainMessage = this.mMainHandler.obtainMessage(14);
        messageObtainMessage.arg1 = this.mIndex;
        messageObtainMessage.obj = new Couple(str, obj);
        messageObtainMessage.sendToTarget();
    }

    final void stateChangedTo(int i2) {
        Message messageObtainMessage = this.mMainHandler.obtainMessage(13);
        messageObtainMessage.arg1 = this.mIndex;
        messageObtainMessage.arg2 = i2;
        messageObtainMessage.sendToTarget();
    }

    public void uninstall() {
        CloudControlManager.getInstance().unregisterListener(this);
    }

    @Override // com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
    }
}
