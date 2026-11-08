package miui.app.constants;

import miui.os.Environment;
import miui.os.FileUtils;

/* JADX INFO: loaded from: classes.dex */
public interface ResourceBrowserConstants {
    public static final String ACTION_PICK_RESOURCE = "miui.intent.action.PICK_RESOURCE";
    public static final String CONFIG_PATH;
    public static final String MAML_CONFIG_PATH;
    public static final String MIUI_PATH;
    public static final String REQUEST_CURRENT_USING_PATH = "REQUEST_CURRENT_USING_PATH";
    public static final String REQUEST_TRACK_ID = "REQUEST_TRACK_ID";
    public static final String RESPONSE_PICKED_RESOURCE = "RESPONSE_PICKED_RESOURCE";
    public static final String RESPONSE_TRACK_ID = "RESPONSE_TRACK_ID";

    static {
        String strNormalizeDirectoryName = FileUtils.normalizeDirectoryName(Environment.getExternalStorageMiuiDirectory().getAbsolutePath());
        MIUI_PATH = strNormalizeDirectoryName;
        String str = strNormalizeDirectoryName + ".config/";
        CONFIG_PATH = str;
        MAML_CONFIG_PATH = str + "maml/";
    }
}
