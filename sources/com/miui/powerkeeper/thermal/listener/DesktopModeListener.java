package com.miui.powerkeeper.thermal.listener;

import android.annotation.Nullable;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.Settings;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.ScenarioManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import miui.app.IFreeformCallback;
import miui.app.MiuiFreeFormManager;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DesktopModeListener extends ElementListener {
    private static final String ACTION_MULTISENCE_FOCUSED_CHANGE = "com.xiaomi.multisence.action.FOCUSED_CHANGE";
    private static final int ELEMENT_FLOAT_CLASS0 = 3;
    private static final int ELEMENT_FLOAT_GAME = 4;
    private static final int ELEMENT_FLOAT_MORE = 98;
    private static final int ELEMENT_FLOAT_NORMAL = 1;
    private static final int ELEMENT_FLOAT_VIDEO = 2;
    private static final String EXTRA_MULTISENCE_FOCUSED_PACKAGE = "com.xiaomi.multisence.extra.FOCUSED_PACKAGE";
    private static final String MIUI_DKT_MODE = "miui_dkt_mode";
    private static final int NORMAL_WINDOWS_LIMIT = 2;
    private static final String TAG = "DesktopModeListener";
    private final Map<String, Integer> mPackageTypeMap = new HashMap();
    private final int DEFAULT_VALUE = 99;
    private int mCurrentState = 99;
    private boolean mDesktopState = false;
    private int mDesktopMode = 0;
    private String mFocusPackage = "";
    private int mWindowsSize = 0;
    private final Map<String, Integer> mSmallScreenMap = new HashMap();
    private final ContentObserver mDeskModeContentObserver = new ContentObserver(this.mElementHandler) { // from class: com.miui.powerkeeper.thermal.listener.DesktopModeListener.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z2, @Nullable Uri uri) {
            if (uri == null || uri.getLastPathSegment() == null || uri.getLastPathSegment().equals(DesktopModeListener.MIUI_DKT_MODE)) {
                DesktopModeListener.this.judgeDesktopMode();
            }
        }
    };
    private final BroadcastReceiver mFocusReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.thermal.listener.DesktopModeListener.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (action.equals(DesktopModeListener.ACTION_MULTISENCE_FOCUSED_CHANGE)) {
                DesktopModeListener.this.mFocusPackage = intent.getStringExtra(DesktopModeListener.EXTRA_MULTISENCE_FOCUSED_PACKAGE);
                if (DesktopModeListener.this.mFocusPackage == null) {
                    DesktopModeListener.this.mFocusPackage = "";
                }
            }
            DesktopModeListener.this.judgeState();
        }
    };
    private IFreeformCallback mFreeformCallback = new IFreeformCallback.Stub() { // from class: com.miui.powerkeeper.thermal.listener.DesktopModeListener.3
        public void dispatchFreeFormStackModeChanged(int i2, MiuiFreeFormManager.MiuiFreeFormStackInfo miuiFreeFormStackInfo) {
            if (DesktopModeListener.this.mDesktopState) {
                DesktopModeListener.this.mSmallScreenMap.clear();
            } else {
                DesktopModeListener.this.judgeMultiScreen(miuiFreeFormStackInfo.packageName, miuiFreeFormStackInfo.windowState);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void judgeDesktopMode() {
        int i2 = Settings.System.getInt(AutoRegister.getContext().getContentResolver(), MIUI_DKT_MODE, 0);
        if (this.mDesktopMode != i2) {
            this.mDesktopMode = i2;
            this.mDesktopState = mapToState(i2);
            judgeState();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void judgeMultiScreen(String str, int i2) {
        if (i2 != -1) {
            this.mSmallScreenMap.put(str, Integer.valueOf(i2));
        } else if (this.mSmallScreenMap.containsKey(str)) {
            this.mSmallScreenMap.remove(str);
        }
        this.mWindowsSize = this.mSmallScreenMap.size();
        judgeState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void judgeState() {
        int iIntValue;
        if (this.mDesktopState) {
            iIntValue = this.mPackageTypeMap.getOrDefault(this.mFocusPackage, 1).intValue();
        } else {
            iIntValue = this.mWindowsSize >= 2 ? 98 : 99;
        }
        if (iIntValue != this.mCurrentState) {
            this.mCurrentState = iIntValue;
            modifyCurrentState(iIntValue);
        }
    }

    private boolean mapToState(int i2) {
        return i2 == 1;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        IntentFilter intentFilter = new IntentFilter();
        MiuiFreeFormManager.registerFreeformCallback(this.mFreeformCallback);
        intentFilter.addAction(ACTION_MULTISENCE_FOCUSED_CHANGE);
        AutoRegister.getContext().registerReceiver(this.mFocusReceiver, intentFilter, 2);
        AutoRegister.getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor(MIUI_DKT_MODE), true, this.mDeskModeContentObserver);
        judgeDesktopMode();
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 99;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.listener.IListener
    public void uninstall() {
        super.uninstall();
        MiuiFreeFormManager.unregisterFreeformCallback(this.mFreeformCallback);
        AutoRegister.getContext().getContentResolver().unregisterContentObserver(this.mDeskModeContentObserver);
        AutoRegister.getContext().unregisterReceiver(this.mFocusReceiver);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        JSONObject jSONObject;
        try {
            jSONObject = new JSONObject(SimpleSettings.Misc.getString(AutoRegister.getContext(), "thermal_group", ""));
        } catch (Exception e2) {
            StringWriter stringWriter = new StringWriter();
            e2.printStackTrace(new PrintWriter(stringWriter));
            ScenarioManager.getHistory().log(stringWriter.toString());
            jSONObject = null;
        }
        if (jSONObject == null) {
            return;
        }
        this.mPackageTypeMap.clear();
        String strOptString = jSONObject.optString("video", "");
        if (!strOptString.isEmpty()) {
            for (String str : strOptString.split(",")) {
                this.mPackageTypeMap.put(str, 2);
            }
        }
        String strOptString2 = jSONObject.optString("class0", "");
        if (!strOptString2.isEmpty()) {
            for (String str2 : strOptString2.split(",")) {
                this.mPackageTypeMap.put(str2, 3);
            }
        }
        String strOptString3 = jSONObject.optString("game", "");
        if (!strOptString3.isEmpty()) {
            for (String str3 : strOptString3.split(",")) {
                this.mPackageTypeMap.put(str3, 4);
            }
        }
        String strOptString4 = jSONObject.optString("game2", "");
        if (!strOptString4.isEmpty()) {
            for (String str4 : strOptString4.split(",")) {
                this.mPackageTypeMap.put(str4, 4);
            }
        }
        String strOptString5 = jSONObject.optString("pubg", "");
        if (!strOptString5.isEmpty()) {
            for (String str5 : strOptString5.split(",")) {
                this.mPackageTypeMap.put(str5, 4);
            }
        }
        String strOptString6 = jSONObject.optString("yuanshen", "");
        if (strOptString6.isEmpty()) {
            return;
        }
        for (String str6 : strOptString6.split(",")) {
            this.mPackageTypeMap.put(str6, 4);
        }
    }

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
    }
}
