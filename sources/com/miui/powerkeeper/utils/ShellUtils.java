package com.miui.powerkeeper.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ShellUtils {
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_SU = "su";

    private ShellUtils() {
    }

    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    public static CommandResult execCommand(String str, boolean z2) {
        return execCommand(new String[]{str}, z2, true);
    }

    public static class CommandResult {
        public String errorMsg;
        public int result;
        public String successMsg;

        public CommandResult(int i2) {
            this.result = i2;
        }

        public CommandResult(int i2, String str, String str2) {
            this.result = i2;
            this.successMsg = str;
            this.errorMsg = str2;
        }
    }

    public static CommandResult execCommand(List<String> list, boolean z2) {
        return execCommand(list == null ? null : (String[]) list.toArray(new String[list.size()]), z2, true);
    }

    public static CommandResult execCommand(String[] strArr, boolean z2) {
        return execCommand(strArr, z2, true);
    }

    public static CommandResult execCommand(String str, boolean z2, boolean z3) {
        return execCommand(new String[]{str}, z2, z3);
    }

    public static CommandResult execCommand(List<String> list, boolean z2, boolean z3) {
        return execCommand(list == null ? null : (String[]) list.toArray(new String[0]), z2, z3);
    }

    /* JADX WARN: Code duplicated, block: B:103:0x012a  */
    /* JADX WARN: Code duplicated, block: B:104:0x012c  */
    /* JADX WARN: Code duplicated, block: B:107:0x0133  */
    /* JADX WARN: Code duplicated, block: B:128:0x0160  */
    /* JADX WARN: Code duplicated, block: B:132:0x0116 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:134:0x0156 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:138:0x010c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:140:0x014c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:144:0x00e7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:148:0x0102 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:150:0x0142 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:152:0x00dd A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:170:? A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:99:0x0120  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v18 */
    /* JADX WARN: Type inference failed for: r11v8 */
    /* JADX WARN: Type inference failed for: r11v9, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r3v23 */
    /* JADX WARN: Type inference failed for: r3v24 */
    /* JADX WARN: Type inference failed for: r3v5, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v25 */
    /* JADX WARN: Type inference failed for: r4v26 */
    /* JADX WARN: Type inference failed for: r4v27 */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v4, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r5v15 */
    /* JADX WARN: Type inference failed for: r5v16 */
    /* JADX WARN: Type inference failed for: r5v2, types: [java.io.BufferedReader] */
    public static CommandResult execCommand(String[] strArr, boolean z2, boolean z3) throws Throwable {
        Process processExec;
        Object obj;
        StringBuilder sb;
        Process process;
        DataOutputStream dataOutputStream;
        ?? r4;
        DataOutputStream dataOutputStream2;
        DataOutputStream dataOutputStream3;
        Object obj2;
        ?? r3;
        ?? r5;
        ?? r6;
        ?? r11;
        String string;
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2;
        StringBuilder sb2;
        BufferedReader bufferedReader3;
        BufferedReader bufferedReader4;
        BufferedReader bufferedReader5;
        BufferedReader bufferedReader6;
        StringBuilder sb3;
        BufferedReader bufferedReader7;
        BufferedReader bufferedReader8;
        BufferedReader bufferedReader9;
        DataOutputStream dataOutputStream4 = null;
        int iWaitFor = -1;
        if (strArr != null && strArr.length != 0) {
            try {
                processExec = Runtime.getRuntime().exec(z2 ? COMMAND_SU : COMMAND_SH);
                try {
                    dataOutputStream = new DataOutputStream(processExec.getOutputStream());
                    try {
                        try {
                            for (String str : strArr) {
                                if (str != null) {
                                    dataOutputStream.write(str.getBytes());
                                    dataOutputStream.writeBytes(COMMAND_LINE_END);
                                    dataOutputStream.flush();
                                }
                            }
                            dataOutputStream.writeBytes(COMMAND_EXIT);
                            dataOutputStream.flush();
                            iWaitFor = processExec.waitFor();
                            try {
                                if (z3) {
                                    sb = new StringBuilder();
                                    try {
                                        StringBuilder sb4 = new StringBuilder();
                                        try {
                                            BufferedReader bufferedReader10 = new BufferedReader(new InputStreamReader(processExec.getInputStream()));
                                            try {
                                                BufferedReader bufferedReader11 = new BufferedReader(new InputStreamReader(processExec.getErrorStream()));
                                                while (true) {
                                                    try {
                                                        String line = bufferedReader10.readLine();
                                                        if (line == null) {
                                                            break;
                                                        }
                                                        sb.append(line);
                                                        sb.append(COMMAND_LINE_END);
                                                    } catch (Exception e2) {
                                                        e = e2;
                                                        bufferedReader9 = bufferedReader11;
                                                        bufferedReader7 = bufferedReader10;
                                                        bufferedReader8 = bufferedReader9;
                                                        sb3 = sb4;
                                                        bufferedReader5 = bufferedReader7;
                                                        bufferedReader6 = bufferedReader8;
                                                        process = processExec;
                                                        obj2 = sb3;
                                                        r6 = bufferedReader5;
                                                        r5 = bufferedReader6;
                                                        try {
                                                            e.printStackTrace();
                                                            if (dataOutputStream != null) {
                                                                try {
                                                                    dataOutputStream.close();
                                                                } catch (IOException e3) {
                                                                    e3.printStackTrace();
                                                                }
                                                            }
                                                            if (r6 != 0) {
                                                                try {
                                                                    r6.close();
                                                                } catch (IOException e4) {
                                                                    e4.printStackTrace();
                                                                }
                                                            }
                                                            if (r5 != 0) {
                                                                try {
                                                                    r5.close();
                                                                } catch (IOException e5) {
                                                                    e5.printStackTrace();
                                                                }
                                                            }
                                                            if (process != null) {
                                                                process.destroy();
                                                            }
                                                            sb = sb;
                                                            r11 = obj2;
                                                            iWaitFor = iWaitFor;
                                                        } catch (Throwable th) {
                                                            th = th;
                                                            processExec = process;
                                                            dataOutputStream4 = dataOutputStream;
                                                            r3 = r6;
                                                            r4 = r5;
                                                            if (dataOutputStream4 != null) {
                                                                try {
                                                                    dataOutputStream4.close();
                                                                } catch (IOException e6) {
                                                                    e6.printStackTrace();
                                                                }
                                                            }
                                                            if (r3 != 0) {
                                                                try {
                                                                    r3.close();
                                                                } catch (IOException e7) {
                                                                    e7.printStackTrace();
                                                                }
                                                            }
                                                            if (r4 != 0) {
                                                                try {
                                                                    r4.close();
                                                                } catch (IOException e8) {
                                                                    e8.printStackTrace();
                                                                }
                                                            }
                                                            if (processExec != null) {
                                                                processExec.destroy();
                                                                throw th;
                                                            }
                                                            throw th;
                                                        }
                                                    } catch (Throwable th2) {
                                                        th = th2;
                                                        bufferedReader = bufferedReader10;
                                                        bufferedReader2 = bufferedReader11;
                                                        dataOutputStream4 = dataOutputStream;
                                                        r3 = bufferedReader;
                                                        r4 = bufferedReader2;
                                                        if (dataOutputStream4 != null) {
                                                            dataOutputStream4.close();
                                                        }
                                                        if (r3 != 0) {
                                                            r3.close();
                                                        }
                                                        if (r4 != 0) {
                                                            r4.close();
                                                        }
                                                        if (processExec != null) {
                                                            processExec.destroy();
                                                            throw th;
                                                        }
                                                        throw th;
                                                    }
                                                }
                                                while (true) {
                                                    String line2 = bufferedReader11.readLine();
                                                    bufferedReader3 = bufferedReader10;
                                                    bufferedReader4 = bufferedReader11;
                                                    sb2 = sb4;
                                                    if (line2 == null) {
                                                        break;
                                                    }
                                                    sb4.append(line2);
                                                    sb4.append(COMMAND_LINE_END);
                                                }
                                                dataOutputStream.close();
                                                if (bufferedReader3 != null) {
                                                    try {
                                                        bufferedReader3.close();
                                                    } catch (IOException e9) {
                                                        e9.printStackTrace();
                                                    }
                                                }
                                                if (bufferedReader4 != null) {
                                                    try {
                                                        bufferedReader4.close();
                                                    } catch (IOException e10) {
                                                        e10.printStackTrace();
                                                    }
                                                }
                                                processExec.destroy();
                                                r11 = sb2;
                                            } catch (Exception e11) {
                                                bufferedReader9 = null;
                                                e = e11;
                                            } catch (Throwable th3) {
                                                th = th3;
                                                bufferedReader2 = null;
                                                bufferedReader = bufferedReader10;
                                            }
                                        } catch (Exception e12) {
                                            bufferedReader7 = null;
                                            bufferedReader8 = null;
                                            processExec = processExec;
                                            sb = sb;
                                            e = e12;
                                        }
                                    } catch (Exception e13) {
                                        bufferedReader5 = null;
                                        bufferedReader6 = null;
                                        sb3 = null;
                                        processExec = processExec;
                                        sb = sb;
                                        e = e13;
                                    }
                                    if (sb == null) {
                                        string = null;
                                    } else {
                                        string = sb.toString();
                                    }
                                    return new CommandResult(iWaitFor, string, r11 != 0 ? r11.toString() : null);
                                }
                                sb = null;
                                sb2 = null;
                                bufferedReader3 = null;
                                bufferedReader4 = null;
                                dataOutputStream.close();
                            } catch (IOException e14) {
                                e14.printStackTrace();
                            }
                            if (bufferedReader3 != null) {
                                bufferedReader3.close();
                            }
                            if (bufferedReader4 != null) {
                                bufferedReader4.close();
                            }
                            processExec.destroy();
                            r11 = sb2;
                        } catch (Throwable th4) {
                            th = th4;
                            bufferedReader = null;
                            bufferedReader2 = null;
                        }
                    } catch (Exception e15) {
                        e = e15;
                        process = processExec;
                        sb = null;
                        dataOutputStream3 = null;
                        dataOutputStream2 = null;
                        dataOutputStream = dataOutputStream;
                        iWaitFor = iWaitFor;
                        obj2 = dataOutputStream2;
                        r6 = dataOutputStream3;
                        r5 = dataOutputStream2;
                        e.printStackTrace();
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }
                        if (r6 != 0) {
                            r6.close();
                        }
                        if (r5 != 0) {
                            r5.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        sb = sb;
                        r11 = obj2;
                        iWaitFor = iWaitFor;
                        if (sb == null) {
                            string = null;
                        } else {
                            string = sb.toString();
                        }
                        return new CommandResult(iWaitFor, string, r11 != 0 ? r11.toString() : null);
                    }
                } catch (Exception e16) {
                    e = e16;
                    process = processExec;
                    sb = null;
                    dataOutputStream = null;
                    DataOutputStream dataOutputStream5 = dataOutputStream;
                    dataOutputStream2 = dataOutputStream5;
                    dataOutputStream3 = dataOutputStream5;
                    iWaitFor = iWaitFor;
                    obj2 = dataOutputStream2;
                    r6 = dataOutputStream3;
                    r5 = dataOutputStream2;
                    e.printStackTrace();
                    if (dataOutputStream != null) {
                        dataOutputStream.close();
                    }
                    if (r6 != 0) {
                        r6.close();
                    }
                    if (r5 != 0) {
                        r5.close();
                    }
                    if (process != null) {
                        process.destroy();
                    }
                    sb = sb;
                    r11 = obj2;
                    iWaitFor = iWaitFor;
                    if (sb == null) {
                        string = null;
                    } else {
                        string = sb.toString();
                    }
                    return new CommandResult(iWaitFor, string, r11 != 0 ? r11.toString() : null);
                } catch (Throwable th5) {
                    th = th5;
                    obj = null;
                    r4 = obj;
                    r3 = obj;
                    if (dataOutputStream4 != null) {
                        dataOutputStream4.close();
                    }
                    if (r3 != 0) {
                        r3.close();
                    }
                    if (r4 != 0) {
                        r4.close();
                    }
                    if (processExec != null) {
                        processExec.destroy();
                        throw th;
                    }
                    throw th;
                }
            } catch (Exception e17) {
                e = e17;
                sb = null;
                process = null;
                dataOutputStream = null;
            } catch (Throwable th6) {
                th = th6;
                processExec = null;
                obj = null;
            }
            if (sb == null) {
                string = null;
            } else {
                string = sb.toString();
            }
            return new CommandResult(iWaitFor, string, r11 != 0 ? r11.toString() : null);
        }
        return new CommandResult(-1, null, null);
    }
}
