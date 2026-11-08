package com.miui.powerkeeper.cloudcontrol;

import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.utils.Device;
import com.miui.powerkeeper.utils.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;
import javax.net.ssl.HttpsURLConnection;

/* JADX INFO: loaded from: classes.dex */
public class HttpUtils {
    private static final int CONNECTION_TIME_OUT = 20000;
    public static final int HTTP_FILE_NULL = -104;
    private static final int RESULT_EXCEPTION_CODE = -103;
    private static final int RESULT_NULL_RESPONSE_200_CODE = -102;
    private static final int SOCKET_TIME_OUT = 20000;
    private static final String TAG = "CloudUpdate";
    private static final boolean D = Log.isLoggable(TAG, 3);

    public static String addToken(String str, boolean z2) {
        StringBuilder sb = new StringBuilder();
        String uTCTimeStr = Device.getUTCTimeStr();
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(uTCTimeStr)) {
            String strMD5 = Utils.MD5(str + uTCTimeStr);
            sb.append(z2 ? "&r=" : "?r=");
            sb.append(uTCTimeStr);
            sb.append("&t=");
            sb.append(strMD5);
        }
        return sb.toString();
    }

    public static Map<String, String> generateHeader() {
        String str = String.format("%s__%s__%s__%s", Device.getModel(), Device.getVersion(), Device.getMCC(), Device.getMNC());
        HashMap map = new HashMap();
        map.put("CCPINF", str);
        return map;
    }

    public static String httpGet(String str, String str2) {
        return httpGet(null, null, str, str2);
    }

    /* JADX WARN: Code duplicated, block: B:83:0x00af A[EXC_TOP_SPLITTER, PHI: r2 r8
      0x00af: PHI (r2v1 boolean) = (r2v0 boolean), (r2v0 boolean), (r2v0 boolean), (r2v3 boolean) binds: [B:55:0x00da, B:61:0x00e7, B:67:0x00f4, B:36:0x00ad] A[DONT_GENERATE, DONT_INLINE]
      0x00af: PHI (r8v9 java.io.FileOutputStream) = 
      (r8v6 java.io.FileOutputStream)
      (r8v7 java.io.FileOutputStream)
      (r8v8 java.io.FileOutputStream)
      (r8v19 java.io.FileOutputStream)
     binds: [B:55:0x00da, B:61:0x00e7, B:67:0x00f4, B:36:0x00ad] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    public static boolean loadFile(String str, String str2) throws Throwable {
        FileOutputStream fileOutputStream;
        InputStream inputStream;
        FileOutputStream fileOutputStream2;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream2 = null;
        HttpURLConnection httpURLConnection2 = null;
        HttpURLConnection httpURLConnection3 = null;
        HttpURLConnection httpURLConnection4 = null;
        boolean z2 = false;
        try {
            URL url = new URL(str);
            Log.i("loadFile----------------->", "currUrl" + url + "storeFilePath" + str2);
            HttpURLConnection httpURLConnection5 = (HttpURLConnection) url.openConnection();
            try {
                httpURLConnection5.setConnectTimeout(20000);
                httpURLConnection5.setReadTimeout(20000);
                httpURLConnection5.setRequestMethod("GET");
                httpURLConnection5.setDoOutput(false);
                httpURLConnection5.connect();
                if (httpURLConnection5.getResponseCode() == 200) {
                    inputStream = httpURLConnection5.getInputStream();
                    try {
                        File file = new File(str2, new File(url.getFile()).getName());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        fileOutputStream2 = new FileOutputStream(file);
                        while (true) {
                            try {
                                int i2 = inputStream.read();
                                if (i2 == -1) {
                                    break;
                                }
                                fileOutputStream2.write(i2);
                            } catch (MalformedURLException unused) {
                                httpURLConnection2 = httpURLConnection5;
                                if (httpURLConnection2 != null) {
                                    httpURLConnection2.disconnect();
                                }
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException unused2) {
                                    }
                                }
                                if (fileOutputStream2 != null) {
                                    try {
                                        fileOutputStream2.close();
                                    } catch (IOException unused3) {
                                    }
                                }
                            } catch (ZipException unused4) {
                                httpURLConnection3 = httpURLConnection5;
                                if (httpURLConnection3 != null) {
                                    httpURLConnection3.disconnect();
                                }
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException unused5) {
                                    }
                                }
                                if (fileOutputStream2 != null) {
                                    fileOutputStream2.close();
                                }
                            } catch (Exception unused6) {
                                httpURLConnection4 = httpURLConnection5;
                                if (httpURLConnection4 != null) {
                                    httpURLConnection4.disconnect();
                                }
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException unused7) {
                                    }
                                }
                                if (fileOutputStream2 != null) {
                                    fileOutputStream2.close();
                                }
                            } catch (Throwable th) {
                                httpURLConnection = httpURLConnection5;
                                fileOutputStream = fileOutputStream2;
                                th = th;
                                if (httpURLConnection != null) {
                                    httpURLConnection.disconnect();
                                }
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException unused8) {
                                    }
                                }
                                if (fileOutputStream == null) {
                                    throw th;
                                }
                                try {
                                    fileOutputStream.close();
                                    throw th;
                                } catch (IOException unused9) {
                                    throw th;
                                }
                            }
                        }
                        inputStream.close();
                        fileOutputStream2.close();
                        z2 = true;
                        inputStream2 = inputStream;
                    } catch (MalformedURLException unused10) {
                        fileOutputStream2 = null;
                    } catch (ZipException unused11) {
                        fileOutputStream2 = null;
                    } catch (Exception unused12) {
                        fileOutputStream2 = null;
                    } catch (Throwable th2) {
                        th = th2;
                        httpURLConnection = httpURLConnection5;
                        fileOutputStream = null;
                    }
                } else {
                    fileOutputStream2 = null;
                }
                httpURLConnection5.disconnect();
                if (inputStream2 != null) {
                    try {
                        inputStream2.close();
                    } catch (IOException unused13) {
                    }
                }
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
            } catch (MalformedURLException unused14) {
                fileOutputStream2 = null;
                inputStream = null;
            } catch (ZipException unused15) {
                fileOutputStream2 = null;
                inputStream = null;
            } catch (Exception unused16) {
                fileOutputStream2 = null;
                inputStream = null;
            } catch (Throwable th3) {
                th = th3;
                inputStream = null;
                httpURLConnection = httpURLConnection5;
                fileOutputStream = null;
            }
        } catch (MalformedURLException unused17) {
            fileOutputStream2 = null;
            inputStream = null;
        } catch (ZipException unused18) {
            fileOutputStream2 = null;
            inputStream = null;
        } catch (Exception unused19) {
            fileOutputStream2 = null;
            inputStream = null;
        } catch (Throwable th4) {
            th = th4;
            fileOutputStream = null;
            inputStream = null;
        }
        Log.e("loadFile----------------->", "" + z2);
        return z2;
    }

    /* JADX WARN: Code duplicated, block: B:112:0x0154 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:114:0x014f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:116:0x014a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:118:0x0164 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:122:0x015f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:138:? A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:75:0x0124 A[PHI: r0 r5
      0x0124: PHI (r0v3 java.lang.String) = (r0v2 java.lang.String), (r0v4 java.lang.String) binds: [B:99:0x0167, B:74:0x0122] A[DONT_GENERATE, DONT_INLINE]
      0x0124: PHI (r5v5 ??) = (r5v4 ??), (r5v9 ??) binds: [B:99:0x0167, B:74:0x0122] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:77:0x012a  */
    /* JADX WARN: Code duplicated, block: B:85:0x0145  */
    /* JADX WARN: Code duplicated, block: B:94:0x015a  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v0 */
    /* JADX WARN: Type inference failed for: r5v10 */
    /* JADX WARN: Type inference failed for: r5v11 */
    /* JADX WARN: Type inference failed for: r5v13 */
    /* JADX WARN: Type inference failed for: r5v19 */
    /* JADX WARN: Type inference failed for: r5v20 */
    /* JADX WARN: Type inference failed for: r5v21 */
    /* JADX WARN: Type inference failed for: r5v22 */
    /* JADX WARN: Type inference failed for: r5v3, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r5v4 */
    /* JADX WARN: Type inference failed for: r5v5, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r5v6 */
    /* JADX WARN: Type inference failed for: r5v7 */
    /* JADX WARN: Type inference failed for: r5v8 */
    /* JADX WARN: Type inference failed for: r5v9 */
    public static String httpGet(int[] iArr, String[] strArr, String str, String str2) throws Throwable {
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        ?? r5;
        InputStream inputStream2;
        Exception exc;
        HttpURLConnection httpURLConnection;
        InputStream inputStream3;
        String string = "";
        boolean z2 = str != null && str.contains(Constants.CLOUD_NEW_SERVER_KEY_UPID);
        if (!TextUtils.isEmpty(str2)) {
            str = str + addToken(str2, z2);
        }
        try {
            URL url = new URL(str);
            Log.d(TAG, "currUrl : " + url);
            HttpURLConnection httpURLConnection2 = null;
            InputStream inputStream4 = null;
            httpURLConnection2 = null;
            try {
                if (url.getProtocol().toLowerCase().equals("https")) {
                    httpURLConnection = (HttpsURLConnection) url.openConnection();
                    try {
                        Log.d(TAG, "use HTTPS protocol");
                    } catch (Exception e2) {
                        e = e2;
                        inputStreamReader = null;
                        inputStream2 = null;
                        httpURLConnection2 = httpURLConnection;
                        exc = e;
                        inputStream = inputStream2;
                        r5 = inputStream2;
                        if (iArr != null) {
                            try {
                                iArr[0] = RESULT_EXCEPTION_CODE;
                                if (exc.getMessage() != null && strArr != null) {
                                    strArr[0] = exc.getMessage();
                                    Log.e(TAG, "httpGetException");
                                }
                            } catch (Throwable th) {
                                th = th;
                                if (httpURLConnection2 != null) {
                                    httpURLConnection2.disconnect();
                                }
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException unused) {
                                    }
                                }
                                if (inputStreamReader != null) {
                                    try {
                                        inputStreamReader.close();
                                    } catch (IOException unused2) {
                                    }
                                }
                                if (r5 != 0) {
                                    throw th;
                                }
                                try {
                                    r5.close();
                                    throw th;
                                } catch (IOException unused3) {
                                    throw th;
                                }
                            }
                        }
                        if (httpURLConnection2 != null) {
                            httpURLConnection2.disconnect();
                        }
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException unused4) {
                            }
                        }
                        if (inputStreamReader != null) {
                            try {
                                inputStreamReader.close();
                            } catch (IOException unused5) {
                            }
                        }
                        if (r5 != 0) {
                            r5.close();
                        }
                        return string;
                    } catch (Throwable th2) {
                        th = th2;
                        inputStream3 = null;
                        inputStreamReader = null;
                        r5 = inputStreamReader;
                        inputStream = inputStream3;
                        httpURLConnection2 = httpURLConnection;
                        if (httpURLConnection2 != null) {
                            httpURLConnection2.disconnect();
                        }
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (inputStreamReader != null) {
                            inputStreamReader.close();
                        }
                        if (r5 != 0) {
                            throw th;
                        }
                        r5.close();
                        throw th;
                    }
                } else {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                }
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(false);
                Map<String, String> mapGenerateHeader = generateHeader();
                if (mapGenerateHeader != null && mapGenerateHeader.size() > 0) {
                    for (String str3 : mapGenerateHeader.keySet()) {
                        httpURLConnection.addRequestProperty(str3, mapGenerateHeader.get(str3));
                    }
                }
                httpURLConnection.connect();
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream5 = httpURLConnection.getInputStream();
                    try {
                        inputStreamReader = new InputStreamReader(inputStream5);
                        try {
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                            try {
                                StringBuffer stringBuffer = new StringBuffer();
                                while (true) {
                                    String line = bufferedReader.readLine();
                                    if (line == null) {
                                        break;
                                    }
                                    stringBuffer.append(line);
                                }
                                string = stringBuffer.toString();
                                if (iArr != null && stringBuffer.length() == 0) {
                                    iArr[0] = RESULT_NULL_RESPONSE_200_CODE;
                                }
                                inputStream4 = inputStream5;
                                r5 = bufferedReader;
                            } catch (Exception e3) {
                                HttpURLConnection httpURLConnection3 = httpURLConnection;
                                exc = e3;
                                httpURLConnection2 = httpURLConnection3;
                                inputStream = inputStream5;
                                r5 = bufferedReader;
                                if (iArr != null) {
                                    iArr[0] = RESULT_EXCEPTION_CODE;
                                    if (exc.getMessage() != null) {
                                        strArr[0] = exc.getMessage();
                                        Log.e(TAG, "httpGetException");
                                    }
                                }
                                if (httpURLConnection2 != null) {
                                    httpURLConnection2.disconnect();
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (inputStreamReader != null) {
                                    inputStreamReader.close();
                                }
                                if (r5 != 0) {
                                }
                                return string;
                            } catch (Throwable th3) {
                                th = th3;
                                inputStream = inputStream5;
                                r5 = bufferedReader;
                                httpURLConnection2 = httpURLConnection;
                                if (httpURLConnection2 != null) {
                                    httpURLConnection2.disconnect();
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (inputStreamReader != null) {
                                    inputStreamReader.close();
                                }
                                if (r5 != 0) {
                                    throw th;
                                }
                                r5.close();
                                throw th;
                            }
                        } catch (Exception e4) {
                            r5 = 0;
                            httpURLConnection2 = httpURLConnection;
                            exc = e4;
                            inputStream = inputStream5;
                        } catch (Throwable th4) {
                            th = th4;
                            r5 = 0;
                            inputStream = inputStream5;
                        }
                    } catch (Exception e5) {
                        r5 = 0;
                        httpURLConnection2 = httpURLConnection;
                        exc = e5;
                        inputStreamReader = null;
                        inputStream = inputStream5;
                    } catch (Throwable th5) {
                        th = th5;
                        inputStreamReader = null;
                        inputStream3 = inputStream5;
                        r5 = inputStreamReader;
                        inputStream = inputStream3;
                        httpURLConnection2 = httpURLConnection;
                        if (httpURLConnection2 != null) {
                            httpURLConnection2.disconnect();
                        }
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (inputStreamReader != null) {
                            inputStreamReader.close();
                        }
                        if (r5 != 0) {
                            throw th;
                        }
                        r5.close();
                        throw th;
                    }
                } else {
                    if (iArr != null) {
                        iArr[0] = responseCode;
                    }
                    inputStreamReader = null;
                    r5 = 0;
                }
                httpURLConnection.disconnect();
                if (inputStream4 != null) {
                    try {
                        inputStream4.close();
                    } catch (IOException unused6) {
                    }
                }
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (IOException unused7) {
                    }
                }
                if (r5 != 0) {
                    r5.close();
                }
            } catch (Exception e6) {
                e = e6;
                inputStreamReader = null;
                inputStream2 = null;
            } catch (Throwable th6) {
                th = th6;
                inputStream = null;
                inputStreamReader = null;
                r5 = 0;
            }
        } catch (MalformedURLException | IOException unused8) {
        }
        return string;
    }
}
