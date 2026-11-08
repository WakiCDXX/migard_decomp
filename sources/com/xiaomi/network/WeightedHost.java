package com.xiaomi.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class WeightedHost implements Comparable<WeightedHost> {
    private static final int HISTORY_SIZE = 30;
    private final LinkedList<AccessHistory> accessHistories;
    String host;
    private long touchedTime;
    protected int weight;

    public WeightedHost() {
        this(null, 0);
    }

    protected synchronized void addAccessHistory(AccessHistory accessHistory) {
        if (accessHistory != null) {
            try {
                this.accessHistories.add(accessHistory);
                int weight = accessHistory.getWeight();
                if (weight > 0) {
                    this.weight += accessHistory.getWeight();
                } else {
                    int i2 = 0;
                    for (int size = this.accessHistories.size() - 1; size >= 0 && this.accessHistories.get(size).getWeight() < 0; size--) {
                        i2++;
                    }
                    this.weight += weight * i2;
                }
                if (this.accessHistories.size() > 30) {
                    this.weight -= this.accessHistories.remove().getWeight();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public synchronized WeightedHost fromJSON(JSONObject jSONObject) throws JSONException {
        this.touchedTime = jSONObject.getLong("tt");
        this.weight = jSONObject.getInt("wt");
        this.host = jSONObject.getString("host");
        JSONArray jSONArray = jSONObject.getJSONArray("ah");
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            this.accessHistories.add(new AccessHistory().fromJSON(jSONArray.getJSONObject(i2)));
        }
        return this;
    }

    public synchronized ArrayList<AccessHistory> getAccessHistory() {
        return new ArrayList<>(this.accessHistories);
    }

    public synchronized ArrayList<AccessHistory> getUnTouchedAccessHistory() {
        ArrayList<AccessHistory> arrayList;
        try {
            arrayList = new ArrayList<>();
            for (AccessHistory accessHistory : this.accessHistories) {
                if (accessHistory.getTime() > this.touchedTime) {
                    arrayList.add(accessHistory);
                }
            }
            this.touchedTime = System.currentTimeMillis();
        } catch (Throwable th) {
            throw th;
        }
        return arrayList;
    }

    public synchronized JSONObject toJSON() throws JSONException {
        JSONObject jSONObject;
        try {
            jSONObject = new JSONObject();
            jSONObject.put("tt", this.touchedTime);
            jSONObject.put("wt", this.weight);
            jSONObject.put("host", this.host);
            JSONArray jSONArray = new JSONArray();
            Iterator<AccessHistory> it = this.accessHistories.iterator();
            while (it.hasNext()) {
                jSONArray.put(it.next().toJSON());
            }
            jSONObject.put("ah", jSONArray);
        } catch (Throwable th) {
            throw th;
        }
        return jSONObject;
    }

    public String toString() {
        return this.host + ":" + this.weight;
    }

    public WeightedHost(String str) {
        this(str, 0);
    }

    @Override // java.lang.Comparable
    public int compareTo(WeightedHost weightedHost) {
        if (weightedHost == null) {
            return 1;
        }
        return weightedHost.weight - this.weight;
    }

    public WeightedHost(String str, int i2) {
        this.accessHistories = new LinkedList<>();
        this.touchedTime = 0L;
        this.host = str;
        this.weight = i2;
    }
}
