package com.miui.powerkeeper;

import android.content.Context;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class AppRuleCheckerRegardBackground extends AppRuleChecker {
    public AppRuleCheckerRegardBackground(Context context, PowerKeeperManager powerKeeperManager, String str, Bundle bundle) {
        super(context, powerKeeperManager, str, bundle);
    }

    @Override // com.miui.powerkeeper.AppRuleChecker
    protected int y(AppRuleChecker.j jVar) {
        return (jVar.f162d.f156a != 0 && jVar.f160b == 2) ? 1 : 0;
    }
}
