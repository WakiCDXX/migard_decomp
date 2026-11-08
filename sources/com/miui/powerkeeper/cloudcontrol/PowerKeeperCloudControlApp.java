package com.miui.powerkeeper.cloudcontrol;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperCloudControlApp implements Parcelable {
    public static final String ACTION_KEY = "action_key";
    public static final String ACTION_LIST = "action_list";
    public static final String ACTION_VALUE = "action_value";
    public static final String ADDED = "added";
    public static final String APP_LIST = "app_list";
    public static final String APP_NAME = "app_name";
    public static final String GROUP_ID = "group_id";
    public Bundle action;
    public boolean added;
    public String appName;
    public int groupId;
    private static final String TAG = PowerKeeperCloudControlApp.class.getSimpleName();
    public static final Parcelable.Creator<PowerKeeperCloudControlApp> CREATOR = new Parcelable.Creator<PowerKeeperCloudControlApp>() { // from class: com.miui.powerkeeper.cloudcontrol.PowerKeeperCloudControlApp.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PowerKeeperCloudControlApp createFromParcel(Parcel parcel) {
            return new PowerKeeperCloudControlApp(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PowerKeeperCloudControlApp[] newArray(int i2) {
            return new PowerKeeperCloudControlApp[i2];
        }
    };

    public PowerKeeperCloudControlApp(boolean z2, int i2, String str, Bundle bundle) {
        this.added = z2;
        this.groupId = i2;
        this.appName = str;
        this.action = bundle;
    }

    public static PowerKeeperCloudControlApp parseFromJson(JSONObject jSONObject) {
        try {
            boolean z2 = jSONObject.getBoolean("added");
            int i2 = jSONObject.getInt("group_id");
            String string = jSONObject.getString("app_name");
            Bundle bundle = new Bundle();
            JSONArray jSONArray = jSONObject.getJSONArray(ACTION_LIST);
            for (int i3 = 0; i3 < jSONArray.length(); i3++) {
                JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i3);
                bundle.putString(jSONObjectOptJSONObject.getString("action_key"), jSONObjectOptJSONObject.getString("action_value"));
            }
            return new PowerKeeperCloudControlApp(z2, i2, string, bundle);
        } catch (JSONException e2) {
            Log.e(TAG, "parseFromJson", e2);
            return null;
        } catch (Exception e3) {
            Log.e(TAG, "parseFromJson", e3);
            return null;
        }
    }

    public static PowerKeeperCloudControlApp parseFromJsonForNewCC(JSONObject jSONObject) {
        try {
            String string = jSONObject.getString("app_name");
            Bundle bundle = new Bundle();
            for (String str : LocalUpdateUtils.sAppMap.keySet()) {
                if (jSONObject.has(str)) {
                    bundle.putString(str, jSONObject.getString(str));
                }
            }
            return new PowerKeeperCloudControlApp(true, 5, string, bundle);
        } catch (JSONException e2) {
            Log.e(TAG, "parseFromJson", e2);
            return null;
        } catch (Exception e3) {
            Log.e(TAG, "parseFromJson", e3);
            return null;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public JSONObject toJsonObject() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("added", String.valueOf(this.added));
            jSONObject.put("group_id", String.valueOf(this.groupId));
            jSONObject.put("app_name", this.appName);
            JSONArray jSONArray = new JSONArray();
            for (String str : this.action.keySet()) {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("action_key", str);
                jSONObject2.put("action_value", this.action.get(str));
                jSONArray.put(jSONObject2);
            }
            jSONObject.put(ACTION_LIST, jSONArray);
            return jSONObject;
        } catch (JSONException unused) {
            return null;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i2) {
        parcel.writeInt(this.added ? 1 : 0);
        parcel.writeInt(this.groupId);
        parcel.writeString(this.appName);
        parcel.writeBundle(this.action);
    }

    public PowerKeeperCloudControlApp(Parcel parcel) {
        this.added = parcel.readInt() > 0;
        this.groupId = parcel.readInt();
        this.appName = parcel.readString();
        this.action = parcel.readBundle();
    }
}
