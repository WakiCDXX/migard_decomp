package com.miui.powerkeeper.thermal.processor;

import android.annotation.NonNull;
import android.content.Intent;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.resource.DBObserver;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.utils.ShellUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class GameProcessor extends ThermalProcessor {
    private static final int DEFAULT_UNFOLD_MODE_BOUNDARY = 100;
    private static final int FLAG_MGAME_STATE = 0;
    private static final int FLAG_TGAME_STATE = 1;
    private static final int POWER_MODE_BOUNDARY;
    private final Set<Integer> mGameSet = new HashSet();
    private final DBObserver mDBObserver = DBObserver.getInstance();
    private int mCurrentScenario = 0;
    private boolean mIsEnableOptimize = false;

    static {
        POWER_MODE_BOUNDARY = ScenarioConfig.NEW_DEVICES ? 50 : 30;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$init$0(Map.Entry entry) {
        return ((String) entry.getKey()).equals("tgame");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$init$1(Map.Entry entry) {
        this.mGameSet.addAll((Collection) entry.getValue());
    }

    private void sendBroadcast() {
        Set<Integer> set = this.mGameSet;
        if (set == null || !set.contains(Integer.valueOf(this.mCurrentScenario))) {
            return;
        }
        int i2 = ((PowerKeeperManager.getInstance().getEventsAggregator().getFoldedStatus() ? this.mCurrentScenario : this.mCurrentScenario + (-100)) > POWER_MODE_BOUNDARY || this.mIsEnableOptimize) ? 1 : 0;
        Intent intent = new Intent("action_game_optimize_state");
        intent.putExtra("game_id", i2);
        AutoRegister.getContext().sendStickyBroadcast(intent);
    }

    public void execute(int i2) {
        this.mCurrentScenario = i2;
        sendBroadcast();
    }

    @Override // com.miui.powerkeeper.thermal.processor.ThermalProcessor
    public void init(Map<String, Set<Integer>> map) {
        map.entrySet().stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermal.processor.a
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return GameProcessor.lambda$init$0((Map.Entry) obj);
            }
        }).forEach(new Consumer() { // from class: com.miui.powerkeeper.thermal.processor.b
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f578a.lambda$init$1((Map.Entry) obj);
            }
        });
        this.mDBObserver.registerUser(SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME, this);
        updateCloudData();
    }

    @NonNull
    public String toString() {
        return "--- GameProcessor ---\ngameSet: " + this.mGameSet + "\ncurrentScenario: " + this.mCurrentScenario + "\nisEnableOptimize: " + this.mIsEnableOptimize + ShellUtils.COMMAND_LINE_END;
    }

    @Override // com.miui.powerkeeper.thermal.processor.IProcessor
    public void uninstall() {
        this.mDBObserver.unregisterUser(SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME, this);
    }

    @Override // com.miui.powerkeeper.thermal.processor.ThermalProcessor, com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        String string = SimpleSettings.Misc.getString(AutoRegister.getContext(), SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME, "");
        if (!string.isEmpty()) {
            this.mIsEnableOptimize = Boolean.parseBoolean(string);
        }
        sendBroadcast();
    }
}
