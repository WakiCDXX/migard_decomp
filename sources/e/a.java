package e;

import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.statemachine.MiawareBcasHandler;

/* JADX INFO: compiled from: ModulesConfig.java */
/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static int f1778a = 5;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static int f1779b = 24;

    public static String a(int i2) {
        if (i2 == 0) {
            return "LogSystemService";
        }
        switch (i2) {
            case 2:
                return "DimController";
            case 3:
                return "KillCameraReceiver";
            case 4:
                return "PerfEngineManager";
            case 5:
                return "UnionPowerService";
            case 6:
                return "CpuDdrHandler";
            case 7:
                return "NoiseCpuHandler";
            default:
                switch (i2) {
                    case 9:
                        return "PowerModeHandler";
                    case 10:
                        return "DisplayDfpsHandler";
                    case 11:
                        return "DisplayCabcHandler";
                    case 12:
                        return "SocOptimizationHandler";
                    case 13:
                        return "DynamicTurboPowerHandler";
                    case 14:
                        return "DisplayStandardVideoHandler";
                    case 15:
                        return "ResolutionPolicyHandler";
                    case 16:
                        return "SyncController";
                    case 17:
                        return "GmsObserver";
                    case 18:
                        return "SatelliteTelephonyController";
                    case 19:
                        return "SleepModeController";
                    case 20:
                        return "PowerSaveModeHandler";
                    case 21:
                        return MiawareBcasHandler.TAG;
                    case 22:
                        return "ExtremeScenarioAwareHandler";
                    case 23:
                        return "PowerScheduleController";
                    default:
                        switch (i2) {
                            case LogType.LOG_TYPE_F_SAMPLING_CURRENT /* 1073741824 */:
                                return "BatteryLifeChecker";
                            case 1073741825:
                                return "PowerCheckerService";
                            case 1073741826:
                                return "DfsPowerManager";
                            case 1073741827:
                                return "ThermalManager";
                            case 1073741828:
                                return "ThermalService";
                            default:
                                return "<Empty>";
                        }
                }
        }
    }
}
