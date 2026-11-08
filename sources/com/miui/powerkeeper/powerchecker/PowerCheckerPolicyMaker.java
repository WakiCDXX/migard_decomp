package com.miui.powerkeeper.powerchecker;

/* JADX INFO: loaded from: classes.dex */
public abstract class PowerCheckerPolicyMaker {
    public abstract int getRule(int i2, String str, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, PowerCheckerProcessStats.StatsInfo statsInfo, boolean z2);
}
