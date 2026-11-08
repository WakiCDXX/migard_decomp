package com.xiaomi.channelcollection;

import android.text.TextUtils;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class JSONObjectBytesCount extends JSONObject implements CountBytes {
    private static final int BRACKET_LENGTH = 2;
    private static final int KEY_LENGTH = 3;
    private final LinkedHashMap<String, Integer> nameSize = new LinkedHashMap<>();

    @Override // com.xiaomi.channelcollection.CountBytes
    public int getBytesCount() {
        int iIntValue = BRACKET_LENGTH;
        Iterator<Integer> it = this.nameSize.values().iterator();
        while (it.hasNext()) {
            iIntValue += it.next().intValue();
        }
        return iIntValue + (length() - 1);
    }

    @Override // org.json.JSONObject
    public JSONObject put(String str, int i2) throws JSONException {
        if (!TextUtils.isEmpty(str)) {
            this.nameSize.put(str, Integer.valueOf(str.length() + String.valueOf(i2).length() + KEY_LENGTH));
        }
        return super.put(str, i2);
    }

    @Override // org.json.JSONObject
    public Object remove(String str) {
        this.nameSize.remove(str);
        return super.remove(str);
    }

    @Override // org.json.JSONObject
    public JSONObject put(String str, long j2) throws JSONException {
        if (!TextUtils.isEmpty(str)) {
            this.nameSize.put(str, Integer.valueOf(str.length() + String.valueOf(j2).length() + KEY_LENGTH));
        }
        return super.put(str, j2);
    }

    @Override // org.json.JSONObject
    public JSONObject put(String str, double d2) throws JSONException {
        if (!TextUtils.isEmpty(str)) {
            this.nameSize.put(str, Integer.valueOf(str.length() + String.valueOf(d2).length() + KEY_LENGTH));
        }
        return super.put(str, d2);
    }

    @Override // org.json.JSONObject
    public JSONObject put(String str, Object obj) throws JSONException {
        JSONObject jSONObjectPut = super.put(str, obj);
        if (!TextUtils.isEmpty(str) && obj != null) {
            if (obj instanceof CountBytes) {
                this.nameSize.put(str, Integer.valueOf(str.length() + ((CountBytes) obj).getBytesCount() + KEY_LENGTH));
            } else {
                this.nameSize.put(str, Integer.valueOf(str.length() + String.valueOf(obj).getBytes(StandardCharsets.UTF_8).length + KEY_LENGTH + BRACKET_LENGTH));
            }
        }
        return jSONObjectPut;
    }

    @Override // org.json.JSONObject
    public JSONObject put(String str, boolean z2) throws JSONException {
        if (!TextUtils.isEmpty(str)) {
            this.nameSize.put(str, Integer.valueOf(str.length() + String.valueOf(z2).length() + KEY_LENGTH));
        }
        return super.put(str, z2);
    }
}
