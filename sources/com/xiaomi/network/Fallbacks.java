package com.xiaomi.network;

import android.text.TextUtils;
import com.miui.powerkeeper.utils.ShellUtils;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class Fallbacks {
    private String host;
    private final ArrayList<Fallback> mFallbacks = new ArrayList<>();

    public Fallbacks(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("the host is empty");
        }
        this.host = str;
    }

    public synchronized void addFallback(Fallback fallback) {
        int i2 = 0;
        while (i2 < this.mFallbacks.size()) {
            try {
                if (this.mFallbacks.get(i2).match(fallback)) {
                    this.mFallbacks.set(i2, fallback);
                    break;
                }
                i2++;
            } catch (Throwable th) {
                throw th;
            }
        }
        if (i2 >= this.mFallbacks.size()) {
            this.mFallbacks.add(fallback);
        }
    }

    public synchronized void addFallbacks(ArrayList<Fallback> arrayList) {
        Iterator<Fallback> it = arrayList.iterator();
        while (it.hasNext()) {
            addFallback(it.next());
        }
    }

    public synchronized Fallbacks fromJSON(JSONObject jSONObject) throws JSONException {
        this.host = jSONObject.getString("host");
        JSONArray jSONArray = jSONObject.getJSONArray("fbs");
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            this.mFallbacks.add(new Fallback(this.host).fromJSON(jSONArray.getJSONObject(i2)));
        }
        return this;
    }

    public synchronized Fallback getFallback() {
        for (int size = this.mFallbacks.size() - 1; size >= 0; size--) {
            Fallback fallback = this.mFallbacks.get(size);
            if (fallback.match()) {
                HostManager.getInstance().setCurrentISP(fallback.getISP());
                return fallback;
            }
        }
        return null;
    }

    public ArrayList<Fallback> getFallbacks() {
        return this.mFallbacks;
    }

    public String getHost() {
        return this.host;
    }

    public synchronized void purge(boolean z2) {
        try {
            for (int size = this.mFallbacks.size() - 1; size >= 0; size--) {
                Fallback fallback = this.mFallbacks.get(size);
                if (z2) {
                    if (fallback.isExpired()) {
                        this.mFallbacks.remove(size);
                    }
                } else if (!fallback.isEffective()) {
                    this.mFallbacks.remove(size);
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized JSONObject toJSON() throws JSONException {
        JSONObject jSONObject;
        try {
            jSONObject = new JSONObject();
            jSONObject.put("host", this.host);
            JSONArray jSONArray = new JSONArray();
            Iterator<Fallback> it = this.mFallbacks.iterator();
            while (it.hasNext()) {
                jSONArray.put(it.next().toJSON());
            }
            jSONObject.put("fbs", jSONArray);
        } catch (Throwable th) {
            throw th;
        }
        return jSONObject;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.host);
        sb.append(ShellUtils.COMMAND_LINE_END);
        Iterator<Fallback> it = this.mFallbacks.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
        }
        return sb.toString();
    }

    public Fallbacks() {
    }
}
