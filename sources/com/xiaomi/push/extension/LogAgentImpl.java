package com.xiaomi.push.extension;

import android.content.Context;
import com.xiaomi.push.log.ILogAgent;
import com.xiaomi.push.service.PushConstants;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class LogAgentImpl implements ILogAgent {
    @Override // com.xiaomi.push.log.ILogAgent
    public File getXmsfLogDir(Context context) {
        if (context == null) {
            return null;
        }
        File file = new File(context.getExternalFilesDir(null), PushConstants.sXmsfLogDir);
        if (!file.exists()) {
            file = new File(context.getFilesDir(), PushConstants.sXmsfLogDir);
        }
        return !file.exists() ? context.getFilesDir() : file;
    }
}
