package com.miui.powerkeeper.feedbackcontrol;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.tracker.TrackerConfig;
import com.miui.powerkeeper.utils.PackageUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CpuruntimeResource {
    private static final String PKG_RUNTIME_INFO_TOTAL_USAGE_PATH = "/proc/package/pkg/show_fb_showall";
    private static final String TAG = "CpuruntimeResource";
    private static CpuruntimeResource sInstance;
    private Context ctx;
    private Map<String, CpuUidPacket> packetMap = new HashMap();
    private File cpuFile = new File(PKG_RUNTIME_INFO_TOTAL_USAGE_PATH);

    private class CpuUidPacket {
        public long bgbigtime;
        public long bgmidtime;
        public long bgsmatime;
        public long bgtime;
        public long fgbigtime;
        public long fgmidtime;
        public long fgsmatime;
        public long fgtime;
        public long lastbgbigtime;
        public long lastbgmidtime;
        public long lastbgsmatime;
        public long lastbgtime;
        public long lastfgbigtime;
        public long lastfgmidtime;
        public long lastfgsmatime;
        public long lastfgtime;
        public long lasttotal;
        public long total;
        public int uid;

        private CpuUidPacket() {
            this.uid = 0;
            this.total = 0L;
            this.fgtime = 0L;
            this.fgbigtime = 0L;
            this.fgmidtime = 0L;
            this.fgsmatime = 0L;
            this.lastfgbigtime = 0L;
            this.lastfgmidtime = 0L;
            this.lastfgsmatime = 0L;
            this.bgtime = 0L;
            this.bgbigtime = 0L;
            this.bgmidtime = 0L;
            this.bgsmatime = 0L;
            this.lastbgbigtime = 0L;
            this.lastbgmidtime = 0L;
            this.lastbgsmatime = 0L;
            this.lasttotal = 0L;
            this.lastfgtime = 0L;
            this.lastbgtime = 0L;
        }
    }

    public CpuruntimeResource(Context context) {
        this.ctx = null;
        this.ctx = context;
    }

    public static CpuruntimeResource getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CpuruntimeResource(context);
        }
        return sInstance;
    }

    /* JADX WARN: Code duplicated, block: B:128:0x03f1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:132:0x0410 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:157:? A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:160:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:48:0x0173  */
    private void getPkgRuntimeInfo(boolean z2) throws Throwable {
        Throwable th;
        BufferedReader bufferedReader;
        Throwable th2;
        String str;
        BufferedReader bufferedReader2;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7 = ":";
        String str8 = "<ALL>";
        String str9 = "exception: ";
        String str10 = "<BACK>";
        String str11 = "<FRONT>";
        String str12 = TAG;
        if (!this.cpuFile.exists()) {
            return;
        }
        try {
            BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(new FileInputStream(this.cpuFile)));
            String strSubstring = null;
            String strSubstring2 = null;
            String str13 = null;
            String strSubstring3 = null;
            while (true) {
                try {
                    String line = bufferedReader3.readLine();
                    if (line == null) {
                        String str14 = str9;
                        String str15 = str12;
                        try {
                            bufferedReader3.close();
                            return;
                        } catch (Exception e2) {
                            str = str14 + e2.getMessage();
                            str12 = str15;
                            Log.e(str12, str);
                            return;
                        }
                    }
                    try {
                        if (line.contains(str8)) {
                            try {
                                if (line.contains(str7)) {
                                    String strSubstring4 = line.substring(0, line.indexOf(str7));
                                    if (line.contains(str11)) {
                                        try {
                                            strSubstring = line.substring(line.indexOf(str8) + 5, line.indexOf(str11));
                                        } catch (Exception e3) {
                                            e = e3;
                                            str9 = str9;
                                            str12 = str12;
                                            bufferedReader = bufferedReader3;
                                            try {
                                                Log.e(str12, "getPkgRuntimeInfo exception: " + e.getMessage());
                                                if (bufferedReader != null) {
                                                    try {
                                                        bufferedReader.close();
                                                        return;
                                                    } catch (Exception e4) {
                                                        str = str9 + e4.getMessage();
                                                        Log.e(str12, str);
                                                        return;
                                                    }
                                                }
                                                return;
                                            } catch (Throwable th3) {
                                                th2 = th3;
                                                th = th2;
                                                if (bufferedReader == null) {
                                                    throw th;
                                                }
                                                try {
                                                    bufferedReader.close();
                                                    throw th;
                                                } catch (Exception e5) {
                                                    Log.e(str12, str9 + e5.getMessage());
                                                    throw th;
                                                }
                                            }
                                        } catch (Throwable th4) {
                                            th2 = th4;
                                            str9 = str9;
                                            str12 = str12;
                                            bufferedReader = bufferedReader3;
                                            th = th2;
                                            if (bufferedReader == null) {
                                                throw th;
                                            }
                                            bufferedReader.close();
                                            throw th;
                                        }
                                    }
                                    if (line.contains(str10)) {
                                        strSubstring2 = line.substring(line.indexOf(str11) + 8, line.indexOf(str10));
                                        strSubstring3 = line.substring(line.indexOf(str10) + 7);
                                    }
                                    String packageNameByUid = PackageUtil.getPackageNameByUid(this.ctx, Integer.parseInt(strSubstring4.trim()));
                                    if (this.packetMap.containsKey(packageNameByUid)) {
                                        CpuUidPacket cpuUidPacket = this.packetMap.get(packageNameByUid);
                                        if (z2) {
                                            cpuUidPacket.uid = Integer.parseInt(strSubstring4.trim());
                                            bufferedReader2 = bufferedReader3;
                                            try {
                                                cpuUidPacket.total = Long.parseLong(strSubstring.trim()) - cpuUidPacket.lasttotal;
                                                cpuUidPacket.fgtime = Long.parseLong(strSubstring2.trim()) - cpuUidPacket.lastfgtime;
                                                cpuUidPacket.bgtime = Long.parseLong(strSubstring3.trim()) - cpuUidPacket.lastbgtime;
                                            } catch (Exception e6) {
                                                e = e6;
                                                bufferedReader = bufferedReader2;
                                                Log.e(str12, "getPkgRuntimeInfo exception: " + e.getMessage());
                                                if (bufferedReader != null) {
                                                    bufferedReader.close();
                                                    return;
                                                }
                                                return;
                                            } catch (Throwable th5) {
                                                th2 = th5;
                                                str9 = str9;
                                                str12 = str12;
                                                bufferedReader = bufferedReader2;
                                                th = th2;
                                                if (bufferedReader == null) {
                                                    throw th;
                                                }
                                                bufferedReader.close();
                                                throw th;
                                            }
                                        } else {
                                            bufferedReader2 = bufferedReader3;
                                        }
                                        cpuUidPacket.lasttotal = Long.parseLong(strSubstring.trim());
                                        cpuUidPacket.lastfgtime = Long.parseLong(strSubstring2.trim());
                                        cpuUidPacket.lastbgtime = Long.parseLong(strSubstring3.trim());
                                        str10 = str10;
                                        str11 = str11;
                                    } else {
                                        str7 = str7;
                                        str8 = str8;
                                        bufferedReader2 = bufferedReader3;
                                        CpuUidPacket cpuUidPacket2 = new CpuUidPacket();
                                        cpuUidPacket2.uid = Integer.parseInt(strSubstring4.trim());
                                        if (z2) {
                                            cpuUidPacket2.total = Long.parseLong(strSubstring.trim()) - cpuUidPacket2.lasttotal;
                                            cpuUidPacket2.fgtime = Long.parseLong(strSubstring2.trim()) - cpuUidPacket2.lastfgtime;
                                            cpuUidPacket2.bgtime = Long.parseLong(strSubstring3.trim()) - cpuUidPacket2.lastbgtime;
                                        }
                                        cpuUidPacket2.lasttotal = cpuUidPacket2.total;
                                        cpuUidPacket2.lastfgtime = cpuUidPacket2.fgtime;
                                        cpuUidPacket2.lastbgtime = cpuUidPacket2.bgtime;
                                        this.packetMap.put(packageNameByUid, cpuUidPacket2);
                                    }
                                    str13 = packageNameByUid;
                                } else {
                                    str7 = str7;
                                    str8 = str8;
                                    str10 = str10;
                                    str11 = str11;
                                    bufferedReader2 = bufferedReader3;
                                }
                            } catch (Exception e7) {
                                e = e7;
                                bufferedReader2 = bufferedReader3;
                            } catch (Throwable th6) {
                                th2 = th6;
                                bufferedReader2 = bufferedReader3;
                            }
                        } else {
                            str7 = str7;
                            str8 = str8;
                            str10 = str10;
                            str11 = str11;
                            bufferedReader2 = bufferedReader3;
                        }
                        try {
                            String str16 = "<s>";
                            String str17 = ", <s>";
                            String str18 = "<b>";
                            if (line.contains("<FRONT>:<l>")) {
                                str4 = str10;
                                String strSubstring5 = line.substring(line.indexOf("<l>") + 3, line.indexOf(", <b>"));
                                str5 = strSubstring;
                                String strSubstring6 = line.substring(line.indexOf("<b>") + 3, line.indexOf(", <s>"));
                                String strSubstring7 = line.substring(line.indexOf("<s>") + 3);
                                str6 = strSubstring2;
                                if (this.packetMap.containsKey(str13)) {
                                    CpuUidPacket cpuUidPacket3 = this.packetMap.get(str13);
                                    if (z2) {
                                        str2 = str9;
                                        try {
                                            cpuUidPacket3.fgbigtime = Long.parseLong(strSubstring5.trim()) - cpuUidPacket3.lastfgbigtime;
                                            str3 = str12;
                                            try {
                                                cpuUidPacket3.fgmidtime = Long.parseLong(strSubstring6.trim()) - cpuUidPacket3.lastfgmidtime;
                                                cpuUidPacket3.fgsmatime = Long.parseLong(strSubstring7.trim()) - cpuUidPacket3.lastfgsmatime;
                                            } catch (Exception e8) {
                                                e = e8;
                                                bufferedReader = bufferedReader2;
                                                str12 = str3;
                                                str9 = str2;
                                                Log.e(str12, "getPkgRuntimeInfo exception: " + e.getMessage());
                                                if (bufferedReader != null) {
                                                    bufferedReader.close();
                                                    return;
                                                }
                                                return;
                                            } catch (Throwable th7) {
                                                th = th7;
                                                th = th;
                                                bufferedReader = bufferedReader2;
                                                str12 = str3;
                                                str9 = str2;
                                                if (bufferedReader == null) {
                                                    throw th;
                                                }
                                                bufferedReader.close();
                                                throw th;
                                            }
                                        } catch (Exception e9) {
                                            e = e9;
                                            str3 = str12;
                                            bufferedReader = bufferedReader2;
                                            str12 = str3;
                                            str9 = str2;
                                            Log.e(str12, "getPkgRuntimeInfo exception: " + e.getMessage());
                                            if (bufferedReader != null) {
                                                bufferedReader.close();
                                                return;
                                            }
                                            return;
                                        } catch (Throwable th8) {
                                            th = th8;
                                            str3 = str12;
                                            th = th;
                                            bufferedReader = bufferedReader2;
                                            str12 = str3;
                                            str9 = str2;
                                            if (bufferedReader == null) {
                                                throw th;
                                            }
                                            bufferedReader.close();
                                            throw th;
                                        }
                                    } else {
                                        str2 = str9;
                                        str3 = str12;
                                    }
                                    cpuUidPacket3.lastfgbigtime = Long.parseLong(strSubstring5.trim());
                                    cpuUidPacket3.lastfgmidtime = Long.parseLong(strSubstring6.trim());
                                    cpuUidPacket3.lastfgsmatime = Long.parseLong(strSubstring7.trim());
                                    str18 = "<b>";
                                } else {
                                    str2 = str9;
                                    str16 = "<s>";
                                    str17 = ", <s>";
                                    str3 = str12;
                                    CpuUidPacket cpuUidPacket4 = new CpuUidPacket();
                                    if (z2) {
                                        cpuUidPacket4.fgbigtime = Long.parseLong(strSubstring5.trim()) - cpuUidPacket4.lastfgbigtime;
                                        cpuUidPacket4.fgmidtime = Long.parseLong(strSubstring6.trim()) - cpuUidPacket4.lastfgmidtime;
                                        cpuUidPacket4.fgsmatime = Long.parseLong(strSubstring7.trim()) - cpuUidPacket4.lastfgsmatime;
                                    }
                                    cpuUidPacket4.lastfgbigtime = cpuUidPacket4.fgbigtime;
                                    cpuUidPacket4.lastfgmidtime = cpuUidPacket4.fgmidtime;
                                    cpuUidPacket4.lastfgsmatime = cpuUidPacket4.fgsmatime;
                                    this.packetMap.put(str13, cpuUidPacket4);
                                }
                            } else {
                                str4 = str10;
                                str2 = str9;
                                str16 = "<s>";
                                str17 = ", <s>";
                                str3 = str12;
                                str18 = "<b>";
                                str5 = strSubstring;
                                str6 = strSubstring2;
                            }
                            if (line.contains("<BACK>:<l>")) {
                                String strSubstring8 = line.substring(line.indexOf("<l>") + 3, line.indexOf(", <b>"));
                                String strSubstring9 = line.substring(line.indexOf(str18) + 3, line.indexOf(str17));
                                String strSubstring10 = line.substring(line.indexOf(str16) + 3);
                                if (this.packetMap.containsKey(str13)) {
                                    CpuUidPacket cpuUidPacket5 = this.packetMap.get(str13);
                                    if (z2) {
                                        cpuUidPacket5.bgbigtime = Long.parseLong(strSubstring8.trim()) - cpuUidPacket5.lastbgbigtime;
                                        cpuUidPacket5.bgmidtime = Long.parseLong(strSubstring9.trim()) - cpuUidPacket5.lastbgmidtime;
                                        cpuUidPacket5.bgsmatime = Long.parseLong(strSubstring10.trim()) - cpuUidPacket5.lastbgsmatime;
                                    }
                                    cpuUidPacket5.lastbgbigtime = Long.parseLong(strSubstring8.trim());
                                    cpuUidPacket5.lastbgmidtime = Long.parseLong(strSubstring9.trim());
                                    cpuUidPacket5.lastbgsmatime = Long.parseLong(strSubstring10.trim());
                                } else {
                                    CpuUidPacket cpuUidPacket6 = new CpuUidPacket();
                                    if (z2) {
                                        cpuUidPacket6.bgbigtime = Long.parseLong(strSubstring8.trim()) - cpuUidPacket6.lastbgbigtime;
                                        cpuUidPacket6.bgmidtime = Long.parseLong(strSubstring9.trim()) - cpuUidPacket6.lastbgmidtime;
                                        cpuUidPacket6.bgsmatime = Long.parseLong(strSubstring10.trim()) - cpuUidPacket6.lastbgsmatime;
                                    }
                                    cpuUidPacket6.lastbgbigtime = cpuUidPacket6.bgbigtime;
                                    cpuUidPacket6.lastbgmidtime = cpuUidPacket6.bgmidtime;
                                    cpuUidPacket6.lastbgsmatime = cpuUidPacket6.bgsmatime;
                                    this.packetMap.put(str13, cpuUidPacket6);
                                }
                            }
                            str11 = str11;
                            str7 = str7;
                            str8 = str8;
                            bufferedReader3 = bufferedReader2;
                            str10 = str4;
                            strSubstring = str5;
                            strSubstring2 = str6;
                            str12 = str3;
                            str9 = str2;
                        } catch (Exception e10) {
                            e = e10;
                            str2 = str9;
                        } catch (Throwable th9) {
                            th = th9;
                            str2 = str9;
                        }
                    } catch (Exception e11) {
                        e = e11;
                        str2 = str9;
                        str3 = str12;
                        bufferedReader2 = bufferedReader3;
                    } catch (Throwable th10) {
                        th = th10;
                        str2 = str9;
                        str3 = str12;
                        bufferedReader2 = bufferedReader3;
                    }
                } catch (Exception e12) {
                    e = e12;
                    bufferedReader2 = bufferedReader3;
                } catch (Throwable th11) {
                    str9 = str9;
                    str12 = str12;
                    th = th11;
                    bufferedReader = bufferedReader3;
                }
            }
        } catch (Exception e13) {
            e = e13;
            str9 = "exception: ";
            str12 = TAG;
            bufferedReader = null;
        } catch (Throwable th12) {
            str9 = "exception: ";
            str12 = TAG;
            th = th12;
            bufferedReader = null;
        }
    }

    public JSONObject getcpuinfo(boolean z2) throws Throwable {
        try {
            getPkgRuntimeInfo(z2);
            if (!z2) {
                return null;
            }
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            for (Map.Entry<String, CpuUidPacket> entry : this.packetMap.entrySet()) {
                CpuUidPacket value = entry.getValue();
                if (value.total / 2000 != 0) {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("packageName", entry.getKey());
                    jSONObject2.put("fgtime", value.fgtime);
                    value.fgtime = 0L;
                    jSONObject2.put("fgbigtime", value.fgbigtime);
                    value.fgbigtime = 0L;
                    jSONObject2.put("fgmidtime", value.fgmidtime);
                    value.fgmidtime = 0L;
                    jSONObject2.put("fgsmatime", value.fgsmatime);
                    value.fgsmatime = 0L;
                    jSONObject2.put("bgtime", value.bgtime);
                    value.bgtime = 0L;
                    jSONObject2.put("bgbigtime", value.bgbigtime);
                    value.bgbigtime = 0L;
                    jSONObject2.put("bgmidtime", value.bgmidtime);
                    value.bgmidtime = 0L;
                    jSONObject2.put("bgsmatime", value.bgsmatime);
                    value.bgsmatime = 0L;
                    jSONArray.put(jSONObject2);
                }
            }
            jSONObject.put(TrackerConfig.EVENT_CPU, jSONArray);
            return jSONObject;
        } catch (Exception e2) {
            Log.e(TAG, "makeJson error", e2);
            return null;
        }
    }

    public void resetData() {
        this.packetMap.clear();
    }
}
