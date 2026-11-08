package miui.os;

import com.miui.powerkeeper.logsystem.config.LogType;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class Environment extends android.os.Environment {
    private static File EXTERNAL_STORAGE_MIUI_DIRECTORY = null;
    private static final String MIUI_DIRECTORY_NAME = "MIUI";
    private static final File MIUI_DATA_DIRECTORY = new File("/data/miui/");
    private static final File MIUI_APP_DIRECTORY = new File(getMiuiDataDirectory(), "apps");
    private static final File MIUI_PRESET_APP_DIRECTORY = new File(getMiuiDataDirectory(), "preset_apps");
    private static final File MIUI_CUSTOMIZED_DIRECTORY = new File(getMiuiDataDirectory(), LogType.LOG_TYPE_F_SAMPLING_CURRENT_NAME);

    protected Environment() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static File getExternalStorageMiuiDirectory() {
        try {
            if (EXTERNAL_STORAGE_MIUI_DIRECTORY == null) {
                EXTERNAL_STORAGE_MIUI_DIRECTORY = new File(android.os.Environment.getExternalStorageDirectory(), "MIUI");
            }
            if (!EXTERNAL_STORAGE_MIUI_DIRECTORY.exists() && android.os.Environment.getExternalStorageDirectory().exists()) {
                EXTERNAL_STORAGE_MIUI_DIRECTORY.mkdir();
            }
            return EXTERNAL_STORAGE_MIUI_DIRECTORY;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static File getMiuiAppDirectory() {
        return MIUI_APP_DIRECTORY;
    }

    public static File getMiuiCustomizedDirectory() {
        return MIUI_CUSTOMIZED_DIRECTORY;
    }

    public static File getMiuiDataDirectory() {
        return MIUI_DATA_DIRECTORY;
    }

    public static File getMiuiPresetAppDirectory() {
        return MIUI_PRESET_APP_DIRECTORY;
    }

    public static boolean isExternalStorageMounted() {
        return "mounted".equals(android.os.Environment.getExternalStorageState());
    }
}
