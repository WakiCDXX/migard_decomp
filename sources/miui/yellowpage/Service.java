package miui.yellowpage;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.payment.PaymentManager;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class Service {
    private static final String TAG = "Service";
    private String mActions;
    private Map<String, Integer> mExtraData;
    private String mIcon;
    private boolean mIsMiFamily;
    private int mMid;
    private String mName;
    private String mRawData;

    public static Service fromJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return fromJson(new JSONObject(str));
        } catch (JSONException e2) {
            Log.e(TAG, "Failed to get json object! ", e2);
            return null;
        }
    }

    public static void serviceOnClick(Context context, String str) {
        serviceOnClick(context, str, null, null);
    }

    public String getActions() {
        return this.mActions;
    }

    public Map<String, Integer> getExtraData() {
        return this.mExtraData;
    }

    public String getIcon() {
        return this.mIcon;
    }

    public boolean getIsMiFamily() {
        return this.mIsMiFamily;
    }

    public List<String> getMiStatKeyArgs(String str) {
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(str)) {
            arrayList.add(str);
        }
        arrayList.add(String.valueOf(this.mMid));
        if (!TextUtils.isEmpty(this.mName)) {
            arrayList.add(this.mName);
        }
        return arrayList;
    }

    public int getMid() {
        return this.mMid;
    }

    public String getName() {
        return this.mName;
    }

    public String getRawData() {
        return this.mRawData;
    }

    public boolean hasExtraData() {
        Map<String, Integer> map = this.mExtraData;
        return (map == null || map.size() == 0) ? false : true;
    }

    public Service setActions(String str) {
        this.mActions = str;
        return this;
    }

    public Service setExtraData(Map<String, Integer> map) {
        this.mExtraData = map;
        return this;
    }

    public Service setIcon(String str) {
        this.mIcon = str;
        return this;
    }

    public Service setIsMiFamily(boolean z2) {
        this.mIsMiFamily = z2;
        return this;
    }

    public Service setMid(int i2) {
        this.mMid = i2;
        return this;
    }

    public Service setName(String str) {
        this.mName = str;
        return this;
    }

    public void setRawData(String str) {
        this.mRawData = str;
    }

    public static void serviceOnClick(Context context, String str, String str2, String str3) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            Intent intent = (Intent) InvocationHandler.invoke(context, YellowPageContract.Method.MODULE_TO_INTENT, str).getParcelable(PaymentManager.KEY_INTENT);
            if (intent != null) {
                int iOptInt = jSONObject.optInt("mid", 0);
                intent.putExtra(Tag.Intent.EXTRA_WEB_TITLE, jSONObject.optString("name"));
                intent.putExtra("mid", iOptInt);
                String stringExtra = intent.getStringExtra(Tag.Intent.EXTRA_WEB_URL);
                context.startActivity(intent);
                YellowPageStatistic.clickModuleItem(context, String.valueOf(iOptInt), stringExtra, str2, str3, 0);
            }
        } catch (Exception e2) {
            Log.e(TAG, "Failed to get json object! ", e2);
        }
    }

    public static Service fromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        int iOptInt = jSONObject.optInt("mid");
        String strOptString = jSONObject.optString("name");
        String strOptString2 = jSONObject.optString("icon");
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("extraData");
        HashMap map = new HashMap();
        if (jSONObjectOptJSONObject != null) {
            map.put(Tag.TagServicesData.SERVICE_IS_PROMOTE, Integer.valueOf(jSONObjectOptJSONObject.optInt(Tag.TagServicesData.SERVICE_IS_PROMOTE)));
            map.put(Tag.TagServicesData.SERVICE_IS_HOT, Integer.valueOf(jSONObjectOptJSONObject.optInt(Tag.TagServicesData.SERVICE_IS_HOT)));
            map.put(Tag.TagServicesData.SERVICE_IS_NEW, Integer.valueOf(jSONObjectOptJSONObject.optInt(Tag.TagServicesData.SERVICE_IS_NEW)));
        }
        String strOptString3 = jSONObject.optString(Tag.TagServicesData.SERVICE_ACTIONS);
        boolean zOptBoolean = jSONObject.optBoolean(Tag.TagServicesData.IS_MIFAMILY_ENTRANCE);
        if (TextUtils.isEmpty(strOptString)) {
            return null;
        }
        return new Service().setMid(iOptInt).setName(strOptString).setIcon(strOptString2).setExtraData(map).setActions(strOptString3).setIsMiFamily(zOptBoolean);
    }
}
