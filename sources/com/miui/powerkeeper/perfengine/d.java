package com.miui.powerkeeper.perfengine;

import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONObject;

/* JADX INFO: compiled from: NewAdvPerfNode.java */
/* JADX INFO: loaded from: classes.dex */
public class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public String f503a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public String f504b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public String f505c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public String f506d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f507e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f508f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public int f509g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public int f510h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f511i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public ArrayList<String> f512j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    public String f513k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public String f514l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public String f515m;

    public d() {
        this.f503a = "";
        this.f504b = "";
        this.f505c = "";
        this.f506d = "";
        this.f507e = 60;
        this.f508f = -1;
        this.f509g = -1;
        this.f510h = -1;
        this.f511i = -1;
        this.f512j = new ArrayList<>();
        this.f513k = "";
        this.f514l = "";
        this.f515m = "";
    }

    public void a(d dVar) {
        this.f503a = dVar.f503a;
        this.f504b = dVar.f504b;
        this.f505c = dVar.f505c;
        this.f506d = dVar.f506d;
        this.f507e = dVar.f507e;
        this.f508f = dVar.f508f;
        this.f509g = dVar.f509g;
        this.f510h = dVar.f510h;
        this.f511i = dVar.f511i;
        this.f512j = new ArrayList<>(dVar.f512j);
        this.f513k = dVar.f513k;
        this.f514l = dVar.f514l;
        this.f515m = dVar.f515m;
    }

    public StringBuilder b() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.f506d + ", fps:" + this.f507e + ", cmd:" + this.f508f + ", plQ:" + this.f509g + ", , plM:" + this.f510h + ", , plXR:" + this.f511i);
        Iterator<String> it = this.f512j.iterator();
        while (it.hasNext()) {
            sb.append(it.next() + ", ");
        }
        sb.append("\n Q:" + this.f513k + ",");
        sb.append("\n M:" + this.f514l + ",");
        sb.append("\n XR:" + this.f515m + ",");
        return sb;
    }

    public boolean c(String str) {
        return str.equals(this.f506d);
    }

    public void d() {
        this.f503a = "";
        this.f504b = "";
        this.f505c = "";
        this.f506d = "";
        this.f507e = 60;
        this.f508f = -1;
        this.f509g = -1;
        this.f510h = -1;
        this.f511i = -1;
        this.f512j = new ArrayList<>();
        this.f513k = "";
        this.f514l = "";
        this.f515m = "";
    }

    public d(String str, JSONObject jSONObject) {
        this.f503a = "";
        this.f504b = "";
        this.f505c = "";
        this.f506d = "";
        this.f507e = 60;
        this.f508f = -1;
        this.f509g = -1;
        this.f510h = -1;
        this.f511i = -1;
        this.f512j = new ArrayList<>();
        this.f513k = "";
        this.f514l = "";
        this.f515m = "";
        try {
            this.f506d = str;
            String[] strArrSplit = str.split("#");
            if (strArrSplit.length == 3) {
                this.f503a = strArrSplit[0];
                this.f504b = strArrSplit[1];
                this.f505c = strArrSplit[2];
            }
            if (jSONObject.optString("fps", "high").equals("high")) {
                this.f507e = 1000;
            }
            this.f508f = jSONObject.optInt("cmd", -10);
            this.f509g = jSONObject.optInt("q_perflock", -10);
            this.f510h = jSONObject.optInt("m_perflock", -10);
            this.f511i = jSONObject.optInt("xr_perflock", -10);
        } catch (Exception unused) {
            Log.w("PeGameController", "AntutuPerfNode parse json failed");
        }
    }
}
