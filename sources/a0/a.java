package a0;

import android.text.TextUtils;
import com.xiaomi.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f5a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public JSONArray f6b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public String f7c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public String f8d;

    public void a(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (this.f5a == 200) {
                this.f6b = jSONObject.optJSONArray("messageIds");
            } else {
                JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject(Message.MSG_TYPE_ERROR);
                this.f7c = jSONObjectOptJSONObject.optString("message");
                this.f8d = jSONObjectOptJSONObject.optString("status");
            }
        } catch (Exception unused) {
        }
    }

    public String toString() {
        return "PublishResponse{code=" + this.f5a + ", messageIds=" + this.f6b + ", message='" + this.f7c + "', status='" + this.f8d + "'}";
    }
}
