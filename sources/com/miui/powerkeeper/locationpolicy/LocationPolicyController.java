package com.miui.powerkeeper.locationpolicy;

import android.content.Context;
import android.database.ContentObserver;
import android.location.LocationPolicy;
import android.location.LocationPolicyManager;
import android.os.Handler;
import android.util.Log;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class LocationPolicyController {
    private static final String TAG = "LocationPolicyController";
    private Context mContext;
    private final Handler mHandler;
    private ContentObserver mLocationObserver;

    public LocationPolicyController(Context context) {
        Handler handler = new Handler(PowerKeeperManager.getThread().getLooper());
        this.mHandler = handler;
        this.mLocationObserver = new ContentObserver(handler) { // from class: com.miui.powerkeeper.locationpolicy.LocationPolicyController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                super.onChange(z2);
                LocationPolicyController.this.setLocationPolicy();
            }
        };
        this.mContext = context;
        context.getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_NETWORK_MIN_INTERVAL), false, this.mLocationObserver);
        handler.post(new Runnable() { // from class: com.miui.powerkeeper.locationpolicy.LocationPolicyController.2
            @Override // java.lang.Runnable
            public void run() {
                LocationPolicyController.this.setLocationPolicy();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLocationPolicy() {
        try {
            LocationPolicy locationPolicy = LocationPolicy.getLocationPolicy("network", Integer.parseInt(SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_NETWORK_MIN_INTERVAL, "0")));
            LocationPolicy[] locationPolicies = LocationPolicyManager.from(this.mContext).getLocationPolicies();
            ArrayList arrayListNewArrayList = Lists.newArrayList();
            arrayListNewArrayList.clear();
            if (locationPolicies != null) {
                for (LocationPolicy locationPolicy2 : locationPolicies) {
                    if (!locationPolicy2.matches("network")) {
                        arrayListNewArrayList.add(locationPolicy2);
                    }
                }
            }
            arrayListNewArrayList.add(locationPolicy);
            LocationPolicyManager.from(this.mContext).setLocationPolicies((LocationPolicy[]) arrayListNewArrayList.toArray(new LocationPolicy[arrayListNewArrayList.size()]));
        } catch (Exception e2) {
            Log.e(TAG, e2.getMessage());
        }
    }

    public void unregisterLocationObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mLocationObserver);
    }
}
