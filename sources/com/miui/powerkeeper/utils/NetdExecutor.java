package com.miui.powerkeeper.utils;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.IBinder;
import android.util.Log;
import com.miui.whetstone.WhetstoneActivityManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
public class NetdExecutor implements Runnable, WatchDog.Action {
    private static final int BUFFER_SIZE = 4096;
    private static final int DEFAULT_TIMEOUT = 60000;
    private Object[] mArgs;
    private String mCmd;
    private String mSocketName;
    private int mTimeoutMs;
    private static final String TAG = NetdExecutor.class.getSimpleName();
    private static final ExecutorService SINGLE_EXECUTOR = Executors.newSingleThreadExecutor();
    public static String CHAIN_GMS_WALL = "gms_wall";
    private LocalSocket mSocket = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;

    private NetdExecutor(int i2, String str, String str2, Object... objArr) {
        this.mTimeoutMs = i2;
        this.mSocketName = str;
        this.mCmd = str2;
        this.mArgs = objArr;
    }

    private static void appendEscaped(StringBuilder sb, String str) {
        boolean z2 = str.indexOf(32) >= 0;
        if (z2) {
            sb.append('\"');
        }
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt == '\"') {
                sb.append("\\\"");
            } else if (cCharAt == '\\') {
                sb.append("\\\\");
            } else {
                sb.append(cCharAt);
            }
        }
        if (z2) {
            sb.append('\"');
        }
    }

    public static synchronized void disableFirewallStandbyChain() {
        execute(60000, "dnsproxyd", "enablemiuistandby", "disable");
    }

    public static void doDesSocketForUid(final String str, final int[] iArr, final boolean z2) {
        SINGLE_EXECUTOR.execute(new Runnable() { // from class: com.miui.powerkeeper.utils.NetdExecutor.1
            @Override // java.lang.Runnable
            public void run() {
                WhetstoneActivityManager.doDesSocketForUid(str, iArr, z2);
            }
        });
    }

    public static void doRestoreSockForUid(final String str) {
        SINGLE_EXECUTOR.execute(new Runnable() { // from class: com.miui.powerkeeper.utils.NetdExecutor.2
            @Override // java.lang.Runnable
            public void run() {
                WhetstoneActivityManager.doRestoreSockForUid(str);
            }
        });
    }

    public static synchronized void enableFirewallStandbyChain() {
        execute(60000, "dnsproxyd", "enablemiuistandby", "enable");
    }

    public static synchronized void execute(int i2, String str, String str2, Object... objArr) {
        new NetdExecutor(i2, str, str2, objArr).execute();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v1 */
    /* JADX WARN: Type inference failed for: r12v17 */
    /* JADX WARN: Type inference failed for: r12v31 */
    /* JADX WARN: Type inference failed for: r12v32 */
    /* JADX WARN: Type inference failed for: r12v4, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r13v1 */
    /* JADX WARN: Type inference failed for: r13v20 */
    /* JADX WARN: Type inference failed for: r13v23 */
    /* JADX WARN: Type inference failed for: r13v35 */
    /* JADX WARN: Type inference failed for: r13v4, types: [java.io.OutputStream] */
    private void executeCommand(int i2, String str, String str2, Object... objArr) throws Throwable {
        ?? r12;
        ?? r13;
        LocalSocket localSocket;
        InputStream inputStream;
        OutputStream outputStream;
        InputStream inputStream2;
        String str3;
        StringBuilder sb;
        try {
            try {
                localSocket = new LocalSocket();
                try {
                    this.mSocket = localSocket;
                    localSocket.connect(new LocalSocketAddress(str, LocalSocketAddress.Namespace.RESERVED));
                    localSocket.setSoTimeout(i2);
                    InputStream inputStream3 = localSocket.getInputStream();
                    try {
                        this.mInputStream = inputStream3;
                        outputStream = localSocket.getOutputStream();
                        try {
                            this.mOutputStream = outputStream;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(str2);
                            for (Object obj : objArr) {
                                String strValueOf = String.valueOf(obj);
                                if (strValueOf.indexOf(0) >= 0) {
                                    throw new IllegalArgumentException("Unexpected argument: " + obj);
                                }
                                sb2.append(' ');
                                appendEscaped(sb2, strValueOf);
                            }
                            sb2.append((char) 0);
                            String string = sb2.toString();
                            outputStream.write(string.getBytes(StandardCharsets.UTF_8));
                            Log.d(TAG, "SND -> {" + string.trim() + "} this=" + this);
                            while (true) {
                                byte[] bArr = new byte[4096];
                                int i3 = inputStream3.read(bArr);
                                if (i3 < 0) {
                                    Log.e(TAG, "got " + i3 + " reading");
                                    try {
                                        outputStream.close();
                                        this.mOutputStream = null;
                                    } catch (IOException e2) {
                                        Log.e(TAG, "Failed closing output stream: " + e2);
                                    }
                                    try {
                                        inputStream3.close();
                                        this.mInputStream = null;
                                    } catch (IOException e3) {
                                        Log.e(TAG, "Failed closing input stream: " + e3);
                                    }
                                    try {
                                        localSocket.close();
                                        this.mSocket = null;
                                        return;
                                    } catch (IOException e4) {
                                        Log.e(TAG, "Failed closing socket: " + e4);
                                        return;
                                    }
                                }
                                for (int i4 = 0; i4 < i3; i4++) {
                                    if (bArr[i4] == 0) {
                                        String str4 = new String(bArr, 0, i4, StandardCharsets.UTF_8);
                                        String[] strArrSplit = str4.split(" ");
                                        if (strArrSplit.length == 0) {
                                            throw new IllegalArgumentException("Insufficient arguments");
                                        }
                                        try {
                                            if (Integer.parseInt(strArrSplit[0]) == 200) {
                                                Log.d(TAG, "Success REV <- {" + str4.trim() + "}");
                                            } else {
                                                Log.d(TAG, "Failed REV <- {" + str4.trim() + "}");
                                            }
                                            try {
                                                outputStream.close();
                                                this.mOutputStream = null;
                                            } catch (IOException e5) {
                                                Log.e(TAG, "Failed closing output stream: " + e5);
                                            }
                                            try {
                                                inputStream3.close();
                                                this.mInputStream = null;
                                            } catch (IOException e6) {
                                                Log.e(TAG, "Failed closing input stream: " + e6);
                                            }
                                            try {
                                                localSocket.close();
                                                this.mSocket = null;
                                                return;
                                            } catch (IOException e7) {
                                                Log.e(TAG, "Failed closing socket: " + e7);
                                                return;
                                            }
                                        } catch (NumberFormatException e8) {
                                            throw new IllegalArgumentException("problem parsing code", e8);
                                        }
                                    }
                                }
                            }
                        } catch (IOException e9) {
                            e = e9;
                            inputStream2 = inputStream3;
                            Log.e(TAG, "Communications error: ", e);
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                    this.mOutputStream = null;
                                } catch (IOException e10) {
                                    Log.e(TAG, "Failed closing output stream: " + e10);
                                }
                            }
                            if (inputStream2 != null) {
                                try {
                                    inputStream2.close();
                                    this.mInputStream = null;
                                } catch (IOException e11) {
                                    Log.e(TAG, "Failed closing input stream: " + e11);
                                }
                            }
                            if (localSocket != null) {
                                try {
                                    localSocket.close();
                                    this.mSocket = null;
                                } catch (IOException e12) {
                                    e = e12;
                                    str3 = TAG;
                                    sb = new StringBuilder();
                                    sb.append("Failed closing socket: ");
                                    sb.append(e);
                                    Log.e(str3, sb.toString());
                                }
                            }
                        } catch (IllegalArgumentException e13) {
                            e = e13;
                            inputStream = inputStream3;
                            Log.e(TAG, "Problem parsing message ", e);
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                    this.mOutputStream = null;
                                } catch (IOException e14) {
                                    Log.e(TAG, "Failed closing output stream: " + e14);
                                }
                            }
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                    this.mInputStream = null;
                                } catch (IOException e15) {
                                    Log.e(TAG, "Failed closing input stream: " + e15);
                                }
                            }
                            if (localSocket != null) {
                                try {
                                    localSocket.close();
                                    this.mSocket = null;
                                } catch (IOException e16) {
                                    e = e16;
                                    str3 = TAG;
                                    sb = new StringBuilder();
                                    sb.append("Failed closing socket: ");
                                    sb.append(e);
                                    Log.e(str3, sb.toString());
                                }
                            }
                        }
                    } catch (IOException e17) {
                        e = e17;
                        outputStream = null;
                        inputStream2 = inputStream3;
                    } catch (IllegalArgumentException e18) {
                        e = e18;
                        outputStream = null;
                        inputStream = inputStream3;
                    } catch (Throwable th) {
                        th = th;
                        r13 = 0;
                        r12 = inputStream3;
                        if (r13 != 0) {
                            try {
                                r13.close();
                                this.mOutputStream = null;
                            } catch (IOException e19) {
                                Log.e(TAG, "Failed closing output stream: " + e19);
                            }
                        }
                        if (r12 != 0) {
                            try {
                                r12.close();
                                this.mInputStream = null;
                            } catch (IOException e20) {
                                Log.e(TAG, "Failed closing input stream: " + e20);
                            }
                        }
                        if (localSocket == null) {
                            throw th;
                        }
                        try {
                            localSocket.close();
                            this.mSocket = null;
                            throw th;
                        } catch (IOException e21) {
                            Log.e(TAG, "Failed closing socket: " + e21);
                            throw th;
                        }
                    }
                } catch (IOException e22) {
                    e = e22;
                    inputStream2 = null;
                    outputStream = null;
                } catch (IllegalArgumentException e23) {
                    e = e23;
                    inputStream = null;
                    outputStream = null;
                } catch (Throwable th2) {
                    th = th2;
                    r12 = 0;
                    r13 = 0;
                }
            } catch (Throwable th3) {
                th = th3;
                r12 = i2;
                r13 = str;
            }
        } catch (IOException e24) {
            e = e24;
            inputStream2 = null;
            outputStream = null;
            localSocket = null;
        } catch (IllegalArgumentException e25) {
            e = e25;
            inputStream = null;
            outputStream = null;
            localSocket = null;
        } catch (Throwable th4) {
            th = th4;
            r12 = 0;
            r13 = 0;
            localSocket = null;
        }
    }

    private static void executeMcdCommand(final String[] strArr) {
        SINGLE_EXECUTOR.execute(new Runnable() { // from class: com.miui.powerkeeper.utils.NetdExecutor.4
            @Override // java.lang.Runnable
            public void run() {
                OctVmNativeProxy.sudebug_command_execute(strArr);
            }
        });
    }

    public static void initGmsChain(String str, int i2, String str2) {
        executeMcdCommand(new String[]{"sudebug", "init_gms_iptable_chain", str, Integer.toString(i2), str2});
    }

    public static void notifyPkProcessDied(final IBinder iBinder) {
        SINGLE_EXECUTOR.execute(new Runnable() { // from class: com.miui.powerkeeper.utils.NetdExecutor.3
            @Override // java.lang.Runnable
            public void run() {
                WhetstoneActivityManager.notifyPkProcessDied(iBinder);
            }
        });
    }

    public static void setGmsChainState(String str, boolean z2) {
        executeMcdCommand(new String[]{"sudebug", "set_chain_state", str, z2 ? "enable" : "disable"});
    }

    public static synchronized void setGmsDnsBlockerState(int i2, boolean z2) {
        try {
            execute(60000, "dnsproxyd", "setuiddnsrule", Integer.valueOf(i2), z2 ? "deny" : "allow");
        } catch (Throwable th) {
            throw th;
        }
    }

    @Override // com.miui.powerkeeper.utils.WatchDog.Action
    public void doAction() {
        Log.w(TAG, "forceClose");
        OutputStream outputStream = this.mOutputStream;
        if (outputStream != null) {
            try {
                outputStream.close();
                this.mOutputStream = null;
            } catch (IOException e2) {
                Log.e(TAG, "Failed closing output stream: " + e2);
            }
        }
        InputStream inputStream = this.mInputStream;
        if (inputStream != null) {
            try {
                inputStream.close();
                this.mInputStream = null;
            } catch (IOException e3) {
                Log.e(TAG, "Failed closing input stream: " + e3);
            }
        }
        LocalSocket localSocket = this.mSocket;
        if (localSocket != null) {
            try {
                localSocket.close();
                this.mSocket = null;
            } catch (IOException e4) {
                Log.e(TAG, "Failed closing socket: " + e4);
            }
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            try {
                WatchDog.get().start(1, this);
                executeCommand(this.mTimeoutMs, this.mSocketName, this.mCmd, this.mArgs);
            } catch (Exception e2) {
                Log.e(TAG, "executeCommand faiked", e2);
            }
        } finally {
            WatchDog.get().cancel(1, this);
        }
    }

    private void execute() {
        SINGLE_EXECUTOR.execute(this);
    }
}
