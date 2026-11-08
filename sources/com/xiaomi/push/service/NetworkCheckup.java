package com.xiaomi.push.service;

import android.os.Process;
import android.text.TextUtils;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.network.Host;
import com.xiaomi.push.protobuf.ChannelConfig;
import com.xiaomi.report.ConnReportHandler;
import com.xiaomi.report.ConnReportHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class NetworkCheckup {
    private static final int CONNECTIVITY_CHECK_INTERVAL = 1800000;
    private static final int CONNECTIVITY_CON_PORT = 5222;
    private static final int CONNECTIVITY_CON_TIMEOUT = 5000;
    private static final String GET_GATEWAY = "ip route";
    private static final String PING_TEMPLATE = "ping -W 500 -i 0.2 -c 3 %s";
    private static final Pattern IP_PATTERN = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
    private static long lastCheckTime = 0;
    private static ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, new LinkedBlockingQueue());

    public static void connectivityTest() {
        ChannelConfig.PushServiceConfig config;
        long jCurrentTimeMillis = System.currentTimeMillis();
        if ((sExecutor.getActiveCount() <= 0 || jCurrentTimeMillis - lastCheckTime >= 1800000) && ConnReportHandler.getInstance().isAllowStats() && (config = ServiceConfig.getInstance().getConfig()) != null && config.getTestHostsCount() > 0) {
            lastCheckTime = jCurrentTimeMillis;
            connectivityTest(config.getTestHostsList(), true);
        }
    }

    public static void doCheckup() {
        if (sExecutor.getActiveCount() > 0) {
            return;
        }
        sExecutor.execute(new Runnable() { // from class: com.xiaomi.push.service.NetworkCheckup.1
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                String gateway = NetworkCheckup.getGateway();
                if (TextUtils.isEmpty(gateway)) {
                    MyLog.w("Network Checkup: cannot get gateway");
                } else {
                    MyLog.w("Network Checkup: get gateway:" + gateway);
                    NetworkCheckup.doPing(gateway);
                }
                try {
                    InetAddress byName = InetAddress.getByName("www.baidu.com");
                    MyLog.w("Network Checkup: get address for www.baidu.com:" + byName.getAddress());
                    NetworkCheckup.doPing(byName.getHostAddress());
                } catch (UnknownHostException unused) {
                    MyLog.w("Network Checkup: cannot resolve the host www.baidu.com");
                } catch (Throwable th) {
                    MyLog.w("the checkup failure." + th);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean doConnectTest(String str) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        try {
            MyLog.w("ConnectivityTest: begin to connect to " + str);
            Socket socket = new Socket();
            socket.connect(Host.from(str, 5222), 5000);
            socket.setTcpNoDelay(true);
            MyLog.w("ConnectivityTest: connect to " + str + " in " + (System.currentTimeMillis() - jCurrentTimeMillis));
            socket.close();
            return true;
        } catch (Throwable th) {
            MyLog.e("ConnectivityTest: could not connect to:" + str + " exception: " + th.getClass().getSimpleName() + " description: " + th.getMessage());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v16 */
    /* JADX WARN: Type inference failed for: r1v4, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r5v2 */
    /* JADX WARN: Type inference failed for: r5v21 */
    /* JADX WARN: Type inference failed for: r5v22 */
    /* JADX WARN: Type inference failed for: r5v7, types: [java.lang.Process] */
    public static void doPing(String str) throws Throwable {
        ?? r1;
        Throwable th;
        ?? r5;
        BufferedReader bufferedReader;
        Exception e2;
        Process process;
        IOException e3;
        Process process2;
        Process process3;
        StringBuilder sb = new StringBuilder();
        String str2 = "Network Checkup: Begin to ping ";
        sb.append("Network Checkup: Begin to ping ");
        sb.append(str);
        MyLog.w(sb.toString());
        try {
            try {
                Process processExec = Runtime.getRuntime().exec(String.format(PING_TEMPLATE, str));
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(processExec.getInputStream()));
                    try {
                        for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                            MyLog.w("Network Checkup:" + line);
                        }
                        processExec.waitFor();
                        IOUtils.closeQuietly(bufferedReader);
                        process3 = processExec;
                    } catch (IOException e4) {
                        e3 = e4;
                        process2 = processExec;
                        MyLog.e(e3);
                        IOUtils.closeQuietly(bufferedReader);
                        process3 = process2;
                        if (process2 == null) {
                            return;
                        }
                    } catch (Exception e5) {
                        e2 = e5;
                        process = processExec;
                        MyLog.e(e2);
                        IOUtils.closeQuietly(bufferedReader);
                        process3 = process;
                        if (process == null) {
                            return;
                        }
                    }
                } catch (IOException e6) {
                    bufferedReader = null;
                    e3 = e6;
                    process2 = processExec;
                } catch (Exception e7) {
                    bufferedReader = null;
                    e2 = e7;
                    process = processExec;
                } catch (Throwable th2) {
                    r1 = 0;
                    th = th2;
                    r5 = processExec;
                    IOUtils.closeQuietly(r1);
                    if (r5 != 0) {
                        r5.destroy();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                r1 = str2;
                r5 = str;
            }
        } catch (IOException e8) {
            bufferedReader = null;
            e3 = e8;
            process2 = null;
        } catch (Exception e9) {
            bufferedReader = null;
            e2 = e9;
            process = null;
        } catch (Throwable th4) {
            r1 = 0;
            th = th4;
            r5 = 0;
        }
        process3.destroy();
    }

    public static void dumpNativeNetInfo() throws Throwable {
        String file = readFile("/proc/self/net/tcp");
        if (!TextUtils.isEmpty(file)) {
            MyLog.w("dump tcp for uid = " + Process.myUid());
            MyLog.w(file);
        }
        String file2 = readFile("/proc/self/net/tcp6");
        if (TextUtils.isEmpty(file2)) {
            return;
        }
        MyLog.w("dump tcp6 for uid = " + Process.myUid());
        MyLog.w(file2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:38:0x007e A[PHI: r1 r2
      0x007e: PHI (r1v6 java.lang.Process) = (r1v5 java.lang.Process), (r1v7 java.lang.Process) binds: [B:37:0x007c, B:42:0x0089] A[DONT_GENERATE, DONT_INLINE]
      0x007e: PHI (r2v7 java.lang.String) = (r2v6 java.lang.String), (r2v9 java.lang.String) binds: [B:37:0x007c, B:42:0x0089] A[DONT_GENERATE, DONT_INLINE]] */
    public static String getGateway() throws Throwable {
        BufferedReader bufferedReader;
        Throwable th;
        Process processExec;
        String str;
        BufferedReader bufferedReader2 = null;
        str = null;
        str = null;
        str = null;
        str = null;
        String str2 = null;
        bufferedReader = null;
        bufferedReader2 = null;
        BufferedReader bufferedReader3 = null;
        try {
            try {
                processExec = Runtime.getRuntime().exec(GET_GATEWAY);
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(processExec.getInputStream()));
                    try {
                        String line = bufferedReader.readLine();
                        if (!TextUtils.isEmpty(line) && line.startsWith("default via")) {
                            for (String str3 : line.split(" ")) {
                                if (IP_PATTERN.matcher(str3).matches()) {
                                    str2 = str3;
                                }
                            }
                            processExec.waitFor();
                        }
                        IOUtils.closeQuietly(bufferedReader);
                        processExec.destroy();
                        return str2;
                    } catch (IOException e2) {
                        e = e2;
                        str = str2;
                        bufferedReader3 = bufferedReader;
                        MyLog.e(e);
                        IOUtils.closeQuietly(bufferedReader3);
                        if (processExec != null) {
                            processExec.destroy();
                        }
                        return str;
                    } catch (Exception e3) {
                        e = e3;
                        str = str2;
                        bufferedReader2 = bufferedReader;
                        MyLog.e(e);
                        IOUtils.closeQuietly(bufferedReader2);
                        if (processExec != null) {
                            processExec.destroy();
                        }
                        return str;
                    } catch (Throwable th2) {
                        th = th2;
                        IOUtils.closeQuietly(bufferedReader);
                        if (processExec != null) {
                            processExec.destroy();
                        }
                        throw th;
                    }
                } catch (IOException e4) {
                    e = e4;
                    str = null;
                } catch (Exception e5) {
                    e = e5;
                    str = null;
                }
            } catch (Throwable th3) {
                bufferedReader = null;
                th = th3;
            }
        } catch (IOException e6) {
            e = e6;
            processExec = null;
            str = null;
        } catch (Exception e7) {
            e = e7;
            processExec = null;
            str = null;
        } catch (Throwable th4) {
            bufferedReader = null;
            th = th4;
            processExec = null;
        }
    }

    private static String readFile(String str) throws Throwable {
        BufferedReader bufferedReader;
        Throwable th;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(str)));
            try {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        String string = sb.toString();
                        IOUtils.closeQuietly(bufferedReader);
                        return string;
                    }
                    sb.append(ShellUtils.COMMAND_LINE_END);
                    sb.append(line);
                }
            } catch (Exception unused) {
                IOUtils.closeQuietly(bufferedReader);
                return null;
            } catch (Throwable th2) {
                th = th2;
                IOUtils.closeQuietly(bufferedReader);
                throw th;
            }
        } catch (Exception unused2) {
            bufferedReader = null;
        } catch (Throwable th3) {
            bufferedReader = null;
            th = th3;
        }
    }

    public static void connectivityTest(final List<String> list, final boolean z2) {
        sExecutor.execute(new Runnable() { // from class: com.xiaomi.push.service.NetworkCheckup.2
            @Override // java.lang.Runnable
            public void run() {
                boolean zDoConnectTest = NetworkCheckup.doConnectTest("www.baidu.com:80");
                Iterator it = list.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    zDoConnectTest = zDoConnectTest || NetworkCheckup.doConnectTest((String) it.next());
                    if (zDoConnectTest && !z2) {
                        break;
                    }
                }
                ConnReportHelper.count(zDoConnectTest ? 1 : 2);
            }
        });
    }
}
