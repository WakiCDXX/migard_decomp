package com.miui.powerkeeper.thermal.listener;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.ServiceManager;
import android.view.IWindowManager;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.thermal.ScenarioManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class ScreenListener extends ElementListener {
    private Method mIsKeyguardOccludedMethod;
    private KeyguardManager mKeyguardManager;
    private IWindowManager mWindowManager;

    private boolean isKeyguardOccluded() {
        Method method;
        IWindowManager iWindowManager = this.mWindowManager;
        if (iWindowManager != null && (method = this.mIsKeyguardOccludedMethod) != null) {
            try {
                return ((Boolean) method.invoke(iWindowManager, null)).booleanValue();
            } catch (Exception e2) {
                StringWriter stringWriter = new StringWriter();
                e2.printStackTrace(new PrintWriter(stringWriter));
                ScenarioManager.getHistory().log(stringWriter.toString());
            }
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:11:0x0016  */
    /* JADX WARN: Code duplicated, block: B:14:0x0020  */
    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        int i5 = this.mCurrentState;
        if (i2 == -7) {
            i5 = 98;
        } else if (i2 == -5) {
            i5 = 99;
        } else if (i2 == -4) {
            if (!isKeyguardOccluded() && this.mKeyguardManager.isKeyguardLocked()) {
                i5 = 99;
            } else {
                i5 = 98;
            }
        }
        modifyCurrentState(i5);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        try {
            Context context = AutoRegister.getContext();
            AutoRegister.getContext();
            this.mKeyguardManager = (KeyguardManager) context.getSystemService("keyguard");
            AutoRegister.getContext();
            this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
            Class[] clsArr = new Class[0];
            this.mIsKeyguardOccludedMethod = IWindowManager.class.getDeclaredMethod("isKeyguardOccluded", null);
        } catch (Exception e2) {
            StringWriter stringWriter = new StringWriter();
            e2.printStackTrace(new PrintWriter(stringWriter));
            ScenarioManager.getHistory().log(stringWriter.toString());
        }
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        eventsAggregator.registerForScreenOff(this.mElementHandler, null);
        eventsAggregator.registerForScreenOn(this.mElementHandler, null);
        eventsAggregator.registerForUserPresent(this.mElementHandler, null);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 98;
    }
}
