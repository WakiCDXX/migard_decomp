package miui.yellowpage;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ServicesDataEntry {
    private String mGroupName;
    private Type mItemType;
    private List<Service> mServices;

    public enum Type {
        NONE,
        TOP_SERVICE,
        SINGLE_LINE_BANNER,
        CONVENIENT_SERVICE,
        DOUBLE_BANNER,
        USEFUL_NUMBERS,
        BOTTOM_BANNER
    }

    public ServicesDataEntry(Type type) {
        this.mItemType = type;
    }

    public static ServicesDataEntry fromJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return fromJson(new JSONObject(str));
        } catch (JSONException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public String getGroupName() {
        return this.mGroupName;
    }

    public Type getItemType() {
        return this.mItemType;
    }

    public List<Service> getServices() {
        return this.mServices;
    }

    public void setItemType(Type type) {
        this.mItemType = type;
    }

    public void setName(String str) {
        this.mGroupName = str;
    }

    public void setServices(List<Service> list) {
        this.mServices = list;
    }

    public static ServicesDataEntry fromJson(JSONObject jSONObject) {
        Service serviceFromJson;
        if (jSONObject == null) {
            return null;
        }
        try {
            String strOptString = jSONObject.optString("name");
            int i2 = jSONObject.getInt(Tag.TagServicesData.GROUP_STYLE);
            JSONArray jSONArray = jSONObject.getJSONArray("data");
            ServicesDataEntry servicesDataEntry = new ServicesDataEntry(Type.values()[i2]);
            servicesDataEntry.setName(strOptString);
            if (jSONArray != null && jSONArray.length() > 0) {
                ArrayList arrayList = new ArrayList();
                for (int i3 = 0; i3 < jSONArray.length(); i3++) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i3);
                    if (jSONObject2 != null && (serviceFromJson = Service.fromJson(jSONObject2)) != null) {
                        serviceFromJson.setRawData(jSONObject2.toString());
                        arrayList.add(serviceFromJson);
                    }
                }
                servicesDataEntry.setServices(arrayList);
            }
            return servicesDataEntry;
        } catch (JSONException e2) {
            e2.printStackTrace();
            return null;
        }
    }
}
