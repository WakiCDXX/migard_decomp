package com.miui.powerkeeper.thermalcollector.collector;

import android.content.Context;
import android.os.Handler;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.thermal.resource.Couple;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseCollector implements ICollector {
    protected static final int VALUE_DEFAULT = -1;
    private final Couple<String, JSONObject> mData;
    private final Set<String> mUsingEvent = new HashSet();
    protected final Context mContext = PowerKeeperApplication.b();
    protected final Handler mHandler = CollectorManager.getInstance().getHandler();

    BaseCollector(String str) {
        this.mData = new Couple<>(str, new JSONObject());
    }

    protected abstract void collect();

    @Override // com.miui.powerkeeper.thermalcollector.collector.ICollector
    public final Couple<String, JSONObject> collectData() {
        collect();
        return this.mData;
    }

    @Override // com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        printWriter.println("Using collector: " + getClass().getName());
    }

    protected final JSONObject getJson() {
        return this.mData.getV();
    }

    protected abstract void prepareBeDestroy();

    @Override // com.miui.powerkeeper.thermalcollector.collector.ICollector
    public final boolean readyBeDestroy() {
        if (!this.mUsingEvent.isEmpty()) {
            return false;
        }
        prepareBeDestroy();
        return true;
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.ICollector
    public final void removeEvent(String str) {
        this.mUsingEvent.remove(str);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.ICollector
    public final void setEvent(String str) {
        this.mUsingEvent.add(str);
    }
}
