package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.Configuration;

/* JADX INFO: loaded from: classes.dex */
public class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final int f1039a = 1;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final int f1040b = 2;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final int f1041c = 4;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final int f1042d = 8;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final int f1043e = 16;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v13 */
    /* JADX WARN: Type inference failed for: r0v14 */
    /* JADX WARN: Type inference failed for: r0v15 */
    /* JADX WARN: Type inference failed for: r0v16 */
    /* JADX WARN: Type inference failed for: r0v2 */
    /* JADX WARN: Type inference failed for: r0v3, types: [int] */
    public static int a(Configuration configuration) {
        ?? r0;
        if (configuration == null) {
            return 0;
        }
        boolean zIsGAIDEnable = configuration.isGAIDEnable();
        if (configuration.isIMSIEnable()) {
            r0 = zIsGAIDEnable;
            r0 = (zIsGAIDEnable ? 1 : 0) | 2;
        }
        r0 = zIsGAIDEnable;
        ?? r1 = r0;
        if (configuration.isIMEIEnable()) {
            r1 = (r0 == true ? 1 : 0) | 4;
        }
        ?? r2 = r1;
        if (configuration.isExceptionCatcherEnable()) {
            r2 = (r1 == true ? 1 : 0) | 8;
        }
        return configuration.isOverrideMiuiRegionSetting() ? r2 | 16 : r2;
    }
}
