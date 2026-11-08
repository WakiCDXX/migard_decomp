package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.PowerKeeperManager;
import java.util.HashMap;
import java.util.Map;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class SpecialForegroundListener extends ElementListener {
    protected final Map<String, Integer> mSpecialForegroundMap;

    public SpecialForegroundListener() {
        HashMap map = new HashMap(4);
        this.mSpecialForegroundMap = map;
        map.put("com.tencent.tmgp.sgame", 1);
        map.put("com.tencent.mm", 2);
        map.put("com.sina.weibo", 2);
        map.put("com.ss.android.article.news", 2);
        map.put("air.tv.douyu.android", 3);
    }

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 != -9) {
            return;
        }
        modifyCurrentState(this.mSpecialForegroundMap.getOrDefault(((ForegroundInfo) obj).mForegroundPackageName, 97).intValue());
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        PowerKeeperManager.getInstance().getEventsAggregator().registerForForegroundInfo(this.mElementHandler);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 97;
    }
}
