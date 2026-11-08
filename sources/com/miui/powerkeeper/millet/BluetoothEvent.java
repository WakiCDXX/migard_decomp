package com.miui.powerkeeper.millet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import com.miui.powerkeeper.utils.PackageUtil;
import com.xiaomi.push.extension.PluginConstants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothEvent {
    private static volatile BluetoothEvent mInstance;
    private Context mContext;
    private Handler mHandler;
    private SettingsObserver mSettingsObserver;
    private String TAG = "BluetoothEvent";
    private Map<Integer, Set<String>> bluetoothSsp = new HashMap();
    private Set<String> bluetoothNames = new HashSet();
    private Set<Integer> bluetoothUids = new HashSet();
    private String mCurrentState = "null";
    private Map<Integer, Set<Integer>> mUidProfile = new HashMap();
    private Set<Integer> socketApps = new HashSet();
    private String mBluetoothKey = "bright_millet_bluetooth";
    private boolean mBluetoothOn = true;
    private Object lock = new Object();

    private class BluetoothStartReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean z2 = BluetoothEvent.this.mBluetoothOn;
            if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action)) {
                switch (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 0)) {
                    case 10:
                    case 13:
                        BluetoothEvent.this.mBluetoothOn = false;
                        break;
                    case 11:
                    case 12:
                        BluetoothEvent.this.mBluetoothOn = true;
                        break;
                }
                Log.d(BluetoothEvent.this.TAG, "mBluetoothOnn: " + BluetoothEvent.this.mBluetoothOn);
                if (z2 != BluetoothEvent.this.mBluetoothOn) {
                    BluetoothEvent.this.removeAll();
                }
            }
        }

        private BluetoothStartReceiver() {
        }
    }

    private final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri) {
            String string = Settings.Global.getString(BluetoothEvent.this.mContext.getContentResolver(), "RECORD_BLE_APPNAME");
            if (BluetoothEvent.this.mHandler == null || string == null) {
                return;
            }
            String[] strArrSplit = string.split("#");
            HashSet hashSet = new HashSet();
            synchronized (BluetoothEvent.this.lock) {
                try {
                    BluetoothEvent.this.bluetoothUids.clear();
                    for (String str : strArrSplit) {
                        if (str != null && str.length() > 5) {
                            hashSet.add(str);
                            BluetoothEvent.this.bluetoothUids.add(Integer.valueOf(PackageUtil.getUidByPackageName(BluetoothEvent.this.mContext, str)));
                        }
                    }
                    Log.d(BluetoothEvent.this.TAG, "onChange ls=" + string + " uids:" + BluetoothEvent.this.bluetoothUids.toString());
                    if (!MilletHandler.isScreenOn()) {
                        BluetoothEvent.this.bluetoothNames.removeAll(hashSet);
                        Iterator it = BluetoothEvent.this.bluetoothNames.iterator();
                        while (it.hasNext()) {
                            BluetoothEvent.this.dealFrozenThaw((String) it.next(), true, -1);
                        }
                    }
                    for (Integer num : BluetoothEvent.this.bluetoothUids) {
                        if (FreezeBinder.getInstance().isUidFrozen(num.intValue())) {
                            BluetoothEvent.this.dealFrozenThaw(null, false, num.intValue());
                        }
                    }
                    BluetoothEvent.this.bluetoothNames.clear();
                    BluetoothEvent.this.bluetoothNames.addAll(hashSet);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    BluetoothEvent(Context context, Handler handler) {
        this.mHandler = handler;
        this.mContext = context;
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("RECORD_BLE_APPNAME"), false, this.mSettingsObserver, -1);
        restoreAppsState();
        Log.d(this.TAG, "initState = " + getState());
        this.mContext.registerReceiver(new BluetoothStartReceiver(), new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"), 2);
    }

    private boolean checkGmsPersist(int[] iArr, int i2) {
        try {
            RunningProcessInfo runningProcessInfoByProcessName = ProcessManager.getRunningProcessInfoByProcessName("com.google.android.gms.persistent");
            if (runningProcessInfoByProcessName != null && iArr[0] == runningProcessInfoByProcessName.mPid) {
                this.socketApps.remove(Integer.valueOf(i2));
                return true;
            }
        } catch (RemoteException e2) {
            Log.d(this.TAG, "checkGmsPersist: " + e2);
        }
        return false;
    }

    private void checkWearOs(int i2) {
        if (i2 == PackageUtil.getUidByPackageName(this.mContext, "com.google.android.wearable.app.cn")) {
            this.socketApps.add(Integer.valueOf(i2));
        }
    }

    private void dealConnect(int i2, int i3) {
        if (!this.mUidProfile.containsKey(Integer.valueOf(i2))) {
            HashSet hashSet = new HashSet();
            hashSet.add(Integer.valueOf(i3));
            this.mUidProfile.put(Integer.valueOf(i2), hashSet);
        } else if (!this.mUidProfile.get(Integer.valueOf(i2)).contains(Integer.valueOf(i3))) {
            this.mUidProfile.get(Integer.valueOf(i2)).add(Integer.valueOf(i3));
        }
        if (FreezeBinder.getInstance().isUidFrozen(i2)) {
            dealFrozenThaw("", false, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dealFrozenThaw(String str, boolean z2, int i2) {
        if (this.mHandler != null) {
            if (i2 == -1) {
                i2 = PackageUtil.getUidByPackageName(this.mContext, str);
            }
            final Message messageObtainMessage = this.mHandler.obtainMessage(1, i2, z2 ? 1 : 0);
            messageObtainMessage.obj = "bluetooth change";
            this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.BluetoothEvent.1
                @Override // java.lang.Runnable
                public void run() {
                    BluetoothEvent.this.mHandler.sendMessage(messageObtainMessage);
                }
            }, z2 ? PluginConstants.TTS_REMOVE_BEAN_TIME : 0L);
        }
    }

    private void dealProfile(Bundle bundle) {
        int uidByPackageName = bundle.getInt("uid");
        int i2 = bundle.getInt(YellowPageContract.Permission.STATE);
        int i3 = bundle.getInt("type");
        if (!MilletUtils.isAppUid(uidByPackageName) && i3 == 1) {
            uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, bundle.getString("pkg"));
        }
        int i4 = i3 != 3 ? i3 : (bundle.getInt("data") + 1) * 10;
        Log.d(this.TAG, "profile state=" + i2 + " uid=" + uidByPackageName + " type=" + i3 + " order=" + i4);
        if (MilletUtils.isAppUid(uidByPackageName)) {
            if (i2 == 1) {
                dealConnect(uidByPackageName, i4);
            } else if (i2 == 0 && this.mUidProfile.containsKey(Integer.valueOf(uidByPackageName))) {
                if (this.mUidProfile.get(Integer.valueOf(uidByPackageName)).contains(Integer.valueOf(i4))) {
                    this.mUidProfile.get(Integer.valueOf(uidByPackageName)).remove(Integer.valueOf(i4));
                }
                if (this.mUidProfile.get(Integer.valueOf(uidByPackageName)).size() == 0) {
                    this.mUidProfile.remove(Integer.valueOf(uidByPackageName));
                }
            }
            write();
        }
    }

    public static BluetoothEvent getInstance(Context context, Handler handler) {
        if (mInstance == null) {
            synchronized (BluetoothEvent.class) {
                try {
                    if (mInstance == null) {
                        mInstance = new BluetoothEvent(context, handler);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return mInstance;
    }

    private String getState() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Set<Integer>> entry : this.mUidProfile.entrySet()) {
            sb.append(entry.getKey() + ":");
            Iterator<Integer> it = entry.getValue().iterator();
            while (it.hasNext()) {
                sb.append(it.next() + ",");
            }
            sb.append(",");
        }
        if (this.bluetoothSsp.size() > 0) {
            sb.append("socket:");
        }
        for (Map.Entry<Integer, Set<String>> entry2 : this.bluetoothSsp.entrySet()) {
            sb.append(entry2.getKey() + ":");
            Iterator<String> it2 = entry2.getValue().iterator();
            while (it2.hasNext()) {
                sb.append(it2.next() + ",");
            }
            sb.append(",");
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeAll() {
        synchronized (this.lock) {
            this.bluetoothUids.clear();
            this.bluetoothNames.clear();
            this.bluetoothSsp.clear();
            this.mUidProfile.clear();
            this.socketApps.clear();
        }
        String str = this.mCurrentState;
        if (str == null || str.isEmpty()) {
            return;
        }
        Settings.Global.putString(this.mContext.getContentResolver(), this.mBluetoothKey, "");
        this.mCurrentState = null;
        Log.d(this.TAG, "state= " + getState());
    }

    private void restoreAppsState() {
        String[] strArr;
        String string = Settings.Global.getString(this.mContext.getContentResolver(), this.mBluetoothKey);
        if (string == null || string.isEmpty()) {
            return;
        }
        String[] strArrSplit = string.split(",,");
        try {
            int length = strArrSplit.length;
            int i2 = 0;
            int i3 = 0;
            while (i3 < length) {
                String str = strArrSplit[i3];
                char c2 = 1;
                if (str.startsWith("socket:")) {
                    String[] strArrSplit2 = str.substring(7).split(",,");
                    int length2 = strArrSplit2.length;
                    int i4 = i2;
                    while (i4 < length2) {
                        String[] strArrSplit3 = strArrSplit2[i4].split(":");
                        Integer numValueOf = Integer.valueOf(strArrSplit3[i2]);
                        int iIntValue = numValueOf.intValue();
                        this.bluetoothSsp.put(numValueOf, new HashSet());
                        String[] strArrSplit4 = strArrSplit3[c2].split(",");
                        int length3 = strArrSplit4.length;
                        int i5 = 0;
                        while (i5 < length3) {
                            this.bluetoothSsp.get(numValueOf).add(strArrSplit4[i5]);
                            i5++;
                            strArrSplit = strArrSplit;
                        }
                        checkWearOs(iIntValue);
                        i4++;
                        strArrSplit = strArrSplit;
                        i2 = 0;
                        c2 = 1;
                    }
                    strArr = strArrSplit;
                } else {
                    strArr = strArrSplit;
                    String[] strArrSplit5 = str.split(":");
                    i2 = 0;
                    Integer numValueOf2 = Integer.valueOf(strArrSplit5[0]);
                    numValueOf2.intValue();
                    this.mUidProfile.put(numValueOf2, new HashSet());
                    for (String str2 : strArrSplit5[1].split(",")) {
                        this.mUidProfile.get(numValueOf2).add(Integer.valueOf(str2));
                    }
                }
                i3++;
                strArrSplit = strArr;
            }
        } catch (Exception e2) {
            Log.d(this.TAG, "restore", e2);
        }
    }

    private void write() {
        String state = getState();
        if (this.mCurrentState != state) {
            this.mCurrentState = state;
            Log.d(this.TAG, "write state = " + this.mCurrentState);
            if (this.mCurrentState != null) {
                Settings.Global.putString(this.mContext.getContentResolver(), this.mBluetoothKey, this.mCurrentState.toString());
            }
        }
    }

    public void dealBluetoothEvent(Bundle bundle) {
        if (bundle.getString("profile") != null && bundle.getString("profile").equals("y")) {
            dealProfile(bundle);
            return;
        }
        int i2 = bundle.getInt("uid");
        String string = bundle.getString("fd");
        int i3 = bundle.getInt(YellowPageContract.Permission.STATE);
        if (i3 == 1 || this.bluetoothSsp.containsKey(Integer.valueOf(i2))) {
            Log.d(this.TAG, "socket uid=" + i2 + " state=" + i3 + " fd=" + string);
        }
        if (MilletUtils.isAppUid(i2)) {
            if (i3 == 1) {
                if (!this.bluetoothSsp.containsKey(Integer.valueOf(i2))) {
                    this.bluetoothSsp.put(Integer.valueOf(i2), new HashSet());
                    checkWearOs(i2);
                }
                this.bluetoothSsp.get(Integer.valueOf(i2)).add(string);
                write();
            } else if (i3 == 0) {
                if (this.bluetoothSsp.containsKey(Integer.valueOf(i2))) {
                    this.bluetoothSsp.get(Integer.valueOf(i2)).remove(string);
                }
                if (this.bluetoothSsp.containsKey(Integer.valueOf(i2)) && this.bluetoothSsp.get(Integer.valueOf(i2)) != null && this.bluetoothSsp.get(Integer.valueOf(i2)).size() == 0) {
                    this.bluetoothSsp.remove(Integer.valueOf(i2));
                    this.socketApps.remove(Integer.valueOf(i2));
                }
                write();
            }
            dealFrozenThaw("", i3 == 0, i2);
        }
    }

    public void dealBluetoothUidDied(int i2) {
        if (this.bluetoothSsp.containsKey(Integer.valueOf(i2))) {
            int[] pidsForUid = MilletUtils.getPidsForUid(i2, this.mContext);
            boolean zCheckGmsPersist = true;
            if (pidsForUid.length != 0) {
                zCheckGmsPersist = pidsForUid.length == 1 ? checkGmsPersist(pidsForUid, i2) : false;
            }
            if (zCheckGmsPersist) {
                this.bluetoothSsp.remove(Integer.valueOf(i2));
                write();
            }
        }
        if (this.mUidProfile.containsKey(Integer.valueOf(i2)) && MilletUtils.getPidsForUid(i2, this.mContext).length == 0) {
            this.mUidProfile.remove(Integer.valueOf(i2));
            write();
        }
    }

    public boolean isBluetoothActive(int i2) {
        if (this.mBluetoothOn) {
            return this.bluetoothUids.contains(Integer.valueOf(i2)) || this.socketApps.size() > 0 || this.mUidProfile.containsKey(Integer.valueOf(i2)) || this.bluetoothSsp.containsKey(Integer.valueOf(i2));
        }
        return false;
    }

    public void removeBluetoothUid(int i2) {
        synchronized (this.lock) {
            this.bluetoothUids.remove(Integer.valueOf(i2));
            this.bluetoothNames.remove(Integer.valueOf(i2));
            this.bluetoothSsp.remove(Integer.valueOf(i2));
            this.mUidProfile.remove(Integer.valueOf(i2));
            this.socketApps.remove(Integer.valueOf(i2));
        }
        write();
    }
}
