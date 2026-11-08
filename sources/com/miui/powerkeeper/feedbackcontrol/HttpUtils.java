package com.miui.powerkeeper.feedbackcontrol;

import android.util.Log;
import com.xiaomi.channel.commonutils.network.Network;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class HttpUtils {
    private static final String BOUNDARY = "----thermalBoundaryLM6lcG1l3wG4dBm0";
    private static final String NEWLINE = "\r\n";
    private static final String PREFIX = "--";
    private static final String TAG = "HttpUtils";
    private static boolean sIsTestMode = ThermalInfoUtils.isTestMode();

    private HttpUtils() {
    }

    private static void addHeader(HttpURLConnection httpURLConnection, HashMap<String, String> map) throws IOException {
        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(30000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        httpURLConnection.setRequestProperty(Network.USER_AGENT, "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
        httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----thermalBoundaryLM6lcG1l3wG4dBm0");
        if (map != null) {
            try {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:100:? A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:80:0x0110 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:90:0x011a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static String httpPostFiles(String str, HashMap<String, String> map, HashMap<String, String> map2) throws Throwable {
        HttpURLConnection httpURLConnection;
        FileInputStream fileInputStream;
        HttpURLConnection httpURLConnection2;
        if (map2 == null || map2.size() == 0) {
            return "";
        }
        DataOutputStream dataOutputStream = null;
        fileInputStream = null;
        fileInputStream = null;
        FileInputStream fileInputStream2 = null;
        dataOutputStream = null;
        dataOutputStream = null;
        try {
            try {
                URL url = new URL(str);
                if (sIsTestMode) {
                    Log.i(TAG, "serverUrl :" + url);
                }
                httpURLConnection2 = (HttpURLConnection) url.openConnection();
                try {
                    addHeader(httpURLConnection2, map);
                    DataOutputStream dataOutputStream2 = new DataOutputStream(httpURLConnection2.getOutputStream());
                    try {
                        for (Map.Entry<String, String> entry : map2.entrySet()) {
                            String key = entry.getKey();
                            File file = new File(entry.getValue());
                            dataOutputStream2.writeBytes("\r\n------thermalBoundaryLM6lcG1l3wG4dBm0\r\n");
                            dataOutputStream2.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + file.getName() + "\"");
                            dataOutputStream2.writeBytes("\r\n");
                            dataOutputStream2.writeBytes("Content-Type:multipart/form-data");
                            dataOutputStream2.writeBytes("\r\n");
                            dataOutputStream2.writeBytes("\r\n");
                            fileInputStream = new FileInputStream(file);
                            try {
                                byte[] bArr = new byte[4096];
                                while (true) {
                                    int i2 = fileInputStream.read(bArr);
                                    if (i2 == -1) {
                                        break;
                                    }
                                    dataOutputStream2.write(bArr, 0, i2);
                                }
                                fileInputStream.close();
                                dataOutputStream2.writeBytes("\r\n------thermalBoundaryLM6lcG1l3wG4dBm0--\r\n");
                                fileInputStream2 = fileInputStream;
                            } catch (Exception e2) {
                                e = e2;
                                dataOutputStream = dataOutputStream2;
                                httpURLConnection = httpURLConnection2;
                                e = e;
                                try {
                                    e.printStackTrace();
                                    if (dataOutputStream != null) {
                                        try {
                                            dataOutputStream.close();
                                        } catch (IOException e3) {
                                            e3.printStackTrace();
                                        }
                                    }
                                    if (fileInputStream != null) {
                                        try {
                                            fileInputStream.close();
                                        } catch (IOException e4) {
                                            e4.printStackTrace();
                                        }
                                    }
                                    httpURLConnection2 = httpURLConnection;
                                    return httpResponse(httpURLConnection2);
                                } catch (Throwable th) {
                                    th = th;
                                    if (dataOutputStream != null) {
                                        try {
                                            dataOutputStream.close();
                                        } catch (IOException e5) {
                                            e5.printStackTrace();
                                        }
                                    }
                                    if (fileInputStream != null) {
                                        throw th;
                                    }
                                    try {
                                        fileInputStream.close();
                                        throw th;
                                    } catch (IOException e6) {
                                        e6.printStackTrace();
                                        throw th;
                                    }
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                dataOutputStream = dataOutputStream2;
                                if (dataOutputStream != null) {
                                    dataOutputStream.close();
                                }
                                if (fileInputStream != null) {
                                    throw th;
                                }
                                fileInputStream.close();
                                throw th;
                            }
                        }
                        dataOutputStream2.flush();
                        dataOutputStream2.close();
                        try {
                            dataOutputStream2.close();
                        } catch (IOException e7) {
                            e7.printStackTrace();
                        }
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (IOException e8) {
                                e8.printStackTrace();
                            }
                        }
                    } catch (Exception e9) {
                        e = e9;
                        fileInputStream = fileInputStream2;
                    } catch (Throwable th3) {
                        th = th3;
                        fileInputStream = fileInputStream2;
                    }
                } catch (Exception e10) {
                    fileInputStream = null;
                    httpURLConnection = httpURLConnection2;
                    e = e10;
                }
            } catch (Throwable th4) {
                th = th4;
                fileInputStream = null;
            }
        } catch (Exception e11) {
            e = e11;
            httpURLConnection = null;
            fileInputStream = null;
        }
        return httpResponse(httpURLConnection2);
    }

    /* JADX WARN: Code duplicated, block: B:70:0x0093 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:76:0x0089 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static String httpResponse(HttpURLConnection httpURLConnection) throws Throwable {
        Throwable th;
        InputStream errorStream;
        InputStream inputStream;
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        if (httpURLConnection == null) {
            if (httpURLConnection != null) {
            }
            return null;
        }
        try {
            errorStream = httpURLConnection.getResponseCode() != 200 ? httpURLConnection.getErrorStream() : httpURLConnection.getInputStream();
            try {
                BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(errorStream), 2048);
                try {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = bufferedReader3.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                    }
                    bufferedReader3.close();
                    String string = sb.toString();
                    try {
                        bufferedReader3.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    if (errorStream != null) {
                        try {
                            errorStream.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    httpURLConnection.disconnect();
                    return string;
                } catch (Exception e4) {
                    inputStream = errorStream;
                    e = e4;
                    bufferedReader = bufferedReader3;
                    try {
                        e.printStackTrace();
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e5) {
                                e5.printStackTrace();
                            }
                        }
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e6) {
                                e6.printStackTrace();
                            }
                        }
                        httpURLConnection.disconnect();
                        return null;
                    } catch (Throwable th2) {
                        bufferedReader2 = bufferedReader;
                        InputStream inputStream2 = inputStream;
                        th = th2;
                        errorStream = inputStream2;
                        if (bufferedReader2 != null) {
                            try {
                                bufferedReader2.close();
                            } catch (IOException e7) {
                                e7.printStackTrace();
                            }
                        }
                        if (errorStream != null) {
                            try {
                                errorStream.close();
                            } catch (IOException e8) {
                                e8.printStackTrace();
                            }
                        }
                        httpURLConnection.disconnect();
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    bufferedReader2 = bufferedReader3;
                    if (bufferedReader2 != null) {
                        bufferedReader2.close();
                    }
                    if (errorStream != null) {
                        errorStream.close();
                    }
                    httpURLConnection.disconnect();
                    throw th;
                }
            } catch (Exception e9) {
                bufferedReader = null;
                inputStream = errorStream;
                e = e9;
            } catch (Throwable th4) {
                th = th4;
            }
        } catch (Exception e10) {
            e = e10;
            inputStream = null;
            bufferedReader = null;
        } catch (Throwable th5) {
            th = th5;
            errorStream = null;
        }
        httpURLConnection.disconnect();
        return null;
    }
}
