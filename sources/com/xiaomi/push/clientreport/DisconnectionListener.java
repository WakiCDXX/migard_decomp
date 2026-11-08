package com.xiaomi.push.clientreport;

import android.content.Context;
import com.xiaomi.smack.Connection;
import com.xiaomi.smack.ConnectionListener;

/* JADX INFO: loaded from: classes.dex */
public class DisconnectionListener implements ConnectionListener {
    private Context mContext;

    public DisconnectionListener(Context context) {
        this.mContext = context;
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void connectionClosed(Connection connection, int i2, Exception exc) {
        DisconnectStatsHelper.onDisconnection(this.mContext, connection.getHost(), i2);
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void reconnectionSuccessful(Connection connection) {
        DisconnectStatsHelper.onReconnection(this.mContext);
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void connectionStarted(Connection connection) {
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void reconnectionFailed(Connection connection, Exception exc) {
    }
}
