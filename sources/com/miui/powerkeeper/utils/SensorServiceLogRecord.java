package com.miui.powerkeeper.utils;

import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class SensorServiceLogRecord {
    public static String FILE_NAME = "sensorLog.txt";
    public static final String TAG = "SensorServiceLogRecord";
    static Object object = new Object();

    private SensorServiceLogRecord() {
    }

    public static void cacheSensorHistoryLog(final File file) {
        if (file == null) {
            return;
        }
        new Thread(new Runnable() { // from class: com.miui.powerkeeper.utils.SensorServiceLogRecord.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (SensorServiceLogRecord.object) {
                    try {
                        SensorServiceLogRecord.writeLogToFile(file);
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /* JADX WARN: Code duplicated, block: B:73:0x008f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:80:0x0085 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:84:0x0099 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:98:? A[SYNTHETIC] */
    private static void dumpFromFile(PrintWriter printWriter, File file) throws Throwable {
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        FileInputStream fileInputStream = null;
        try {
            try {
                FileInputStream fileInputStream2 = new FileInputStream(file);
                try {
                    inputStreamReader = new InputStreamReader(fileInputStream2);
                    try {
                        bufferedReader = new BufferedReader(inputStreamReader);
                        while (true) {
                            try {
                                String line = bufferedReader.readLine();
                                if (line == null) {
                                    break;
                                }
                                printWriter.println("\t" + line);
                            } catch (IOException e2) {
                                e = e2;
                                fileInputStream = fileInputStream2;
                                try {
                                    e.printStackTrace();
                                    if (fileInputStream != null) {
                                        try {
                                            fileInputStream.close();
                                        } catch (IOException e3) {
                                            e3.printStackTrace();
                                        }
                                    }
                                    if (inputStreamReader != null) {
                                        try {
                                            inputStreamReader.close();
                                        } catch (IOException e4) {
                                            e4.printStackTrace();
                                        }
                                    }
                                    if (bufferedReader == null) {
                                        return;
                                    } else {
                                        bufferedReader.close();
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    if (fileInputStream != null) {
                                        try {
                                            fileInputStream.close();
                                        } catch (IOException e5) {
                                            e5.printStackTrace();
                                        }
                                    }
                                    if (inputStreamReader != null) {
                                        try {
                                            inputStreamReader.close();
                                        } catch (IOException e6) {
                                            e6.printStackTrace();
                                        }
                                    }
                                    if (bufferedReader != null) {
                                        throw th;
                                    }
                                    try {
                                        bufferedReader.close();
                                        throw th;
                                    } catch (IOException e7) {
                                        e7.printStackTrace();
                                        throw th;
                                    }
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                fileInputStream = fileInputStream2;
                                if (fileInputStream != null) {
                                    fileInputStream.close();
                                }
                                if (inputStreamReader != null) {
                                    inputStreamReader.close();
                                }
                                if (bufferedReader != null) {
                                    throw th;
                                }
                                bufferedReader.close();
                                throw th;
                            }
                        }
                        printWriter.println("");
                        try {
                            fileInputStream2.close();
                        } catch (IOException e8) {
                            e8.printStackTrace();
                        }
                        try {
                            inputStreamReader.close();
                        } catch (IOException e9) {
                            e9.printStackTrace();
                        }
                        bufferedReader.close();
                    } catch (IOException e10) {
                        e = e10;
                        bufferedReader = null;
                    } catch (Throwable th3) {
                        th = th3;
                        bufferedReader = null;
                    }
                } catch (IOException e11) {
                    e = e11;
                    inputStreamReader = null;
                    bufferedReader = null;
                } catch (Throwable th4) {
                    th = th4;
                    inputStreamReader = null;
                    bufferedReader = null;
                }
            } catch (IOException e12) {
                e12.printStackTrace();
            }
        } catch (IOException e13) {
            e = e13;
            inputStreamReader = null;
            bufferedReader = null;
        } catch (Throwable th5) {
            th = th5;
            inputStreamReader = null;
            bufferedReader = null;
        }
    }

    public static void dumpSensorHistoryLog(PrintWriter printWriter, File file) {
        synchronized (object) {
            try {
                Log.d(TAG, "read from cache file and dump");
                printWriter.println("");
                printWriter.println("dump SensorServiceLogRecord:");
                if (file != null && file.exists()) {
                    dumpFromFile(printWriter, file);
                    return;
                }
                Log.d(TAG, "Sensor History log file is not exist.");
                printWriter.println("");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:48:0x00b9 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:49:0x00bb A[Catch: IOException -> 0x00b7, TryCatch #4 {IOException -> 0x00b7, blocks: (B:45:0x00b3, B:49:0x00bb, B:51:0x00c0), top: B:58:0x00b3 }] */
    /* JADX WARN: Code duplicated, block: B:51:0x00c0 A[Catch: IOException -> 0x00b7, TRY_LEAVE, TryCatch #4 {IOException -> 0x00b7, blocks: (B:45:0x00b3, B:49:0x00bb, B:51:0x00c0), top: B:58:0x00b3 }] */
    /* JADX WARN: Code duplicated, block: B:58:0x00b3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static void writeLogToFile(File file) throws Throwable {
        InputStream inputStream;
        Process process;
        FileOutputStream fileOutputStream = null;
        inputStream = null;
        InputStream inputStream2 = null;
        fileOutputStream = null;
        try {
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                try {
                    Calendar calendar = Calendar.getInstance();
                    fileOutputStream2.write(String.format("time = %tm-%td %tH:%tM:%tS%n", calendar, calendar, calendar, calendar, calendar).getBytes());
                    Process processExec = Runtime.getRuntime().exec("dumpsys sensorservice");
                    try {
                        inputStream2 = processExec.getInputStream();
                        byte[] bArr = new byte[1024];
                        boolean z2 = false;
                        while (true) {
                            int i2 = inputStream2.read(bArr);
                            if (i2 <= 0) {
                                break;
                            }
                            String str = new String(bArr, 0, i2);
                            if (str.contains("Previous Registrations")) {
                                fileOutputStream2.write(str.substring(0, str.indexOf("Previous Registrations")).getBytes());
                                break;
                            } else if (str.contains("Recent Sensor events")) {
                                fileOutputStream2.write(str.substring(str.indexOf("Recent Sensor events")).getBytes());
                                z2 = true;
                            } else if (z2) {
                                fileOutputStream2.write(bArr);
                            }
                        }
                        fileOutputStream2.close();
                        fileOutputStream2.close();
                        inputStream2.close();
                        processExec.destroy();
                    } catch (IOException e2) {
                        e = e2;
                        process = processExec;
                        inputStream = inputStream2;
                        fileOutputStream = fileOutputStream2;
                        try {
                            e.printStackTrace();
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            if (process != null) {
                                process.destroy();
                            }
                        } catch (Throwable th) {
                            th = th;
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                    if (process != null) {
                                        process.destroy();
                                    }
                                } catch (IOException e3) {
                                    e3.printStackTrace();
                                    throw th;
                                }
                            } else {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (process != null) {
                                    process.destroy();
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        process = processExec;
                        inputStream = inputStream2;
                        fileOutputStream = fileOutputStream2;
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            if (process != null) {
                                process.destroy();
                            }
                        } else {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            if (process != null) {
                                process.destroy();
                            }
                        }
                        throw th;
                    }
                } catch (IOException e4) {
                    e = e4;
                    inputStream = null;
                    process = null;
                } catch (Throwable th3) {
                    th = th3;
                    inputStream = null;
                    process = null;
                }
            } catch (IOException e5) {
                e5.printStackTrace();
            }
        } catch (IOException e6) {
            e = e6;
            inputStream = null;
            process = null;
        } catch (Throwable th4) {
            th = th4;
            inputStream = null;
            process = null;
        }
    }
}
