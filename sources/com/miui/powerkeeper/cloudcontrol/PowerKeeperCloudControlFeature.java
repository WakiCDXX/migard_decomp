package com.miui.powerkeeper.cloudcontrol;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperCloudControlFeature implements Parcelable {
    public static final String ACTION_KEY = "action_key";
    public static final String ACTION_VALUE = "action_value";
    public static final String ADDED = "added";
    public static final String ALARM_ALIGN = "alarm_align";
    public static final String AUDIO_RESTRICT = "audio_restrict";
    public static final String AUROGON_ENABLE = "aurogon_enable";
    public static final String BRIGHT_FROZEN = "bright_millet";
    public static final String BROADCAST_ALARM_STATUS = "broadcast_alarm";
    public static final String FEATURE_LIST = "feature_list";
    public static final String FEATURE_STATUS = "hide_mode";
    public static final String FROZEN_NEW_STATUS = "frozenNew";
    public static final String FROZEN_NEW_WHITELIST = "frozenNew_whitelist";
    public static final String FROZEN_STATUS = "frozen";
    public static final String GROUP_ID = "group_id";
    public static final String LAUNCH_RESTRICT = "launch_restrict";
    public static final String MIUI_STANDBY_STATUS = "miui_standby";
    public static final String NO_CORE_SYSTEM_APPS = "no_core_system_apps";
    public static final String SCREENOFF_MILLET_MODE = "screenoff_millet_mode";
    public boolean added;
    public String featureName;
    public int groupId;
    public String value;
    private static final String TAG = PowerKeeperCloudControlFeature.class.getSimpleName();
    public static final Parcelable.Creator<PowerKeeperCloudControlFeature> CREATOR = new Parcelable.Creator<PowerKeeperCloudControlFeature>() { // from class: com.miui.powerkeeper.cloudcontrol.PowerKeeperCloudControlFeature.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PowerKeeperCloudControlFeature createFromParcel(Parcel parcel) {
            return new PowerKeeperCloudControlFeature(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PowerKeeperCloudControlFeature[] newArray(int i2) {
            return new PowerKeeperCloudControlFeature[i2];
        }
    };

    public PowerKeeperCloudControlFeature(boolean z2, int i2, String str, String str2) {
        this.added = z2;
        this.groupId = i2;
        this.featureName = str;
        this.value = str2;
    }

    public static PowerKeeperCloudControlFeature parseFromJson(JSONObject jSONObject) {
        try {
            return new PowerKeeperCloudControlFeature(jSONObject.getBoolean("added"), jSONObject.getInt("group_id"), jSONObject.getString("action_key"), jSONObject.getString("action_value"));
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
            jSONObject.put("action_key", this.featureName);
            jSONObject.put("action_value", this.value);
            return jSONObject;
        } catch (JSONException unused) {
            return null;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i2) {
        parcel.writeInt(this.added ? 1 : 0);
        parcel.writeInt(this.groupId);
        parcel.writeString(this.featureName);
        parcel.writeString(this.value);
    }

    public PowerKeeperCloudControlFeature(Parcel parcel) {
        this.added = parcel.readInt() > 0;
        this.groupId = parcel.readInt();
        this.featureName = parcel.readString();
        this.value = parcel.readString();
    }
}
