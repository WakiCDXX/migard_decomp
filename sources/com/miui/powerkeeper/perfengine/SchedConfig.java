package com.miui.powerkeeper.perfengine;

import android.util.Slog;
import com.miui.powerkeeper.cloudcontrol.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class SchedConfig {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public String f479b;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public String f478a = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f480c = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f481d = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f482e = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f483f = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f484g = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f485h = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f486i = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f487j = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private ConcurrentHashMap<String, String> f488k = new ConcurrentHashMap<>();

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private ConcurrentHashMap<String, CopyOnWriteArrayList<String>> f489l = new ConcurrentHashMap<>();

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f490m = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f491n = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f492o = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f493p = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f494q = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f495r = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f496s = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f497t = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f498u = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private CopyOnWriteArrayList<String> f499v = new CopyOnWriteArrayList<>();

    public SchedConfig(String str) {
        this.f479b = null;
        this.f479b = str;
        this.f489l.put("scene_default", this.f490m);
        this.f489l.put("scene_launch", this.f491n);
        this.f489l.put("scene_update", this.f492o);
        this.f489l.put("scene_load", this.f493p);
        this.f489l.put("scene_main", this.f494q);
        this.f489l.put("scene_selfload", this.f495r);
        this.f489l.put("scene_waitload", this.f496s);
        this.f489l.put("scene_fight", this.f497t);
        this.f489l.put("scene_bbq", this.f498u);
        this.f489l.put("scene_victory", this.f499v);
    }

    private boolean a(JSONObject jSONObject) {
        try {
            i("cpu_level_high", jSONObject, this.f480c);
            i("cpu_level_medium", jSONObject, this.f481d);
            i("cpu_level_low", jSONObject, this.f482e);
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse cpu error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean b(JSONObject jSONObject) {
        try {
            JSONObject jSONObject2 = (JSONObject) jSONObject.opt("cpu_level");
            if (jSONObject2 == null) {
                Slog.d("PeSchedController", "no required to update scenes");
                return true;
            }
            r("cpu_level_high", (String) jSONObject2.opt("cpu_level_high"));
            r("cpu_level_medium", (String) jSONObject2.opt("cpu_level_medium"));
            r("cpu_level_low", (String) jSONObject2.opt("cpu_level_low"));
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse cpu level error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean c(JSONObject jSONObject) {
        try {
            JSONObject jSONObject2 = (JSONObject) jSONObject.opt("game_common_config");
            if (jSONObject2 == null) {
                Slog.d("PeSchedController", "no required to update common config");
                return true;
            }
            r("level_game_common", (String) jSONObject2.opt("level_game_common"));
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse common config error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean d(JSONObject jSONObject) {
        try {
            i("level_game_common", jSONObject, this.f486i);
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse common config error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean f(JSONObject jSONObject) {
        try {
            i("gpu_level_high", jSONObject, this.f483f);
            i("gpu_level_medium", jSONObject, this.f484g);
            i("gpu_level_low", jSONObject, this.f485h);
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse gpu error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean g(JSONObject jSONObject) {
        try {
            JSONObject jSONObject2 = jSONObject.getJSONObject("gpu_level");
            if (jSONObject2 == null) {
                Slog.d("PeSchedController", "no required to update scenes");
                return true;
            }
            r("gpu_level_high", (String) jSONObject2.opt("gpu_level_high"));
            r("gpu_level_medium", (String) jSONObject2.opt("gpu_level_medium"));
            r("gpu_level_low", (String) jSONObject2.opt("gpu_level_low"));
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse gpu level error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean h(JSONObject jSONObject) {
        try {
            JSONObject jSONObject2 = (JSONObject) jSONObject.opt("header");
            if (jSONObject2 == null) {
                Slog.d("PeSchedController", "not required to update header");
                return true;
            }
            this.f478a = jSONObject2.getString("user_version");
            return true;
        } catch (JSONException e2) {
            Slog.e("PeSchedController", "parse head error : " + e2.toString());
            return false;
        }
    }

    private boolean i(String str, JSONObject jSONObject, CopyOnWriteArrayList<String> copyOnWriteArrayList) {
        String str2 = this.f488k.get(str);
        if (str2 == null) {
            return true;
        }
        copyOnWriteArrayList.clear();
        try {
            JSONArray jSONArray = (JSONArray) jSONObject.opt(str2);
            if (jSONArray != null && jSONArray.length() > 0) {
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    copyOnWriteArrayList.add(jSONArray.getString(i2));
                }
            }
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse config error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean j(JSONObject jSONObject) {
        try {
            JSONObject jSONObject2 = jSONObject.getJSONObject("perf_configs").getJSONObject(this.f479b);
            return n(jSONObject2) && a(jSONObject2) && f(jSONObject2) && d(jSONObject2) && o(jSONObject2);
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse PerfConfigs error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean k(JSONObject jSONObject) {
        try {
            this.f488k.clear();
            JSONObject jSONObject2 = jSONObject.getJSONObject("perf_map");
            return m(jSONObject2) && b(jSONObject2) && g(jSONObject2) && c(jSONObject2) && p(jSONObject2);
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse perfMaps error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean l(JSONObject jSONObject) {
        try {
            JSONObject jSONObject2 = jSONObject.getJSONObject(Constants.CONFIG_KEY_PERF_SCHED);
            return k(jSONObject2) && j(jSONObject2);
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse perfSched error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean m(JSONObject jSONObject) {
        try {
            JSONObject jSONObject2 = (JSONObject) jSONObject.opt("sceneId");
            if (jSONObject2 == null) {
                Slog.d("PeSchedController", "no required to update scenes");
                return true;
            }
            for (String str : this.f489l.keySet()) {
                r(str, (String) jSONObject2.opt(str));
            }
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse scenes error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean n(JSONObject jSONObject) {
        try {
            for (String str : this.f489l.keySet()) {
                if (this.f488k.containsKey(str)) {
                    i(str, jSONObject, this.f489l.get(str));
                }
            }
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse scenes error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean o(JSONObject jSONObject) {
        try {
            i("sys_orig", jSONObject, this.f487j);
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse system config error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private boolean p(JSONObject jSONObject) {
        try {
            JSONObject jSONObject2 = (JSONObject) jSONObject.opt("system_default");
            if (jSONObject2 == null) {
                Slog.d("PeSchedController", "no required to update system config");
                return true;
            }
            r("sys_orig", (String) jSONObject2.opt("sys_orig"));
            return true;
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse system default error : " + e2.toString());
            e2.printStackTrace();
            return false;
        }
    }

    private void q(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (h(jSONObject) && l(jSONObject)) {
                return;
            }
            Slog.e("PeSchedController", "parseDefaultConfig is failed!");
        } catch (Exception e2) {
            Slog.e("PeSchedController", "invalid default config: " + e2.toString());
            e2.printStackTrace();
        }
    }

    private void r(String str, String str2) {
        if (str2 != null) {
            this.f488k.put(str, str2);
        }
    }

    protected void e(InputStream inputStream) {
        StringBuilder sb;
        try {
            if (inputStream == null) {
                return;
            }
            try {
                int iAvailable = inputStream.available();
                if (iAvailable <= 5242880 && iAvailable >= 0) {
                    byte[] bArr = new byte[iAvailable];
                    if (inputStream.read(bArr) <= 0) {
                        Slog.e("PeSchedController", "updateCloudConfig length <= 0");
                        try {
                            inputStream.close();
                            return;
                        } catch (IOException e2) {
                            Slog.e("PeSchedController", "updateCloudConfig finally fis.close() IOException e:" + e2.getMessage());
                            return;
                        }
                    }
                    String str = new String(bArr, StandardCharsets.UTF_8);
                    inputStream.close();
                    q(str);
                    try {
                        inputStream.close();
                        return;
                    } catch (IOException e3) {
                        e = e3;
                        sb = new StringBuilder();
                        sb.append("updateCloudConfig finally fis.close() IOException e:");
                        sb.append(e.getMessage());
                        Slog.e("PeSchedController", sb.toString());
                    }
                }
                Slog.w("PeSchedController", "updateCloudConfig fis.available() error length: " + iAvailable);
                try {
                    inputStream.close();
                } catch (IOException e4) {
                    Slog.e("PeSchedController", "updateCloudConfig finally fis.close() IOException e:" + e4.getMessage());
                }
            } catch (IOException e5) {
                Slog.e("PeSchedController", "updateCloudConfig IOException e:" + e5.getMessage());
                try {
                    inputStream.close();
                } catch (IOException e6) {
                    e = e6;
                    sb = new StringBuilder();
                    sb.append("updateCloudConfig finally fis.close() IOException e:");
                    sb.append(e.getMessage());
                    Slog.e("PeSchedController", sb.toString());
                }
            }
        } catch (Throwable th) {
            try {
                inputStream.close();
            } catch (IOException e7) {
                Slog.e("PeSchedController", "updateCloudConfig finally fis.close() IOException e:" + e7.getMessage());
            }
            throw th;
        }
    }
}
