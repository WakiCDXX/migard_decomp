package com.miui.powerkeeper.statemachine;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.AudioRecordingConfiguration;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Base64;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import android.view.Display;
import android.view.WindowManager;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.controller.FrozenAppController;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.millet.MilletHandler;
import com.miui.powerkeeper.perfengine.PeGameController;
import com.miui.powerkeeper.perfengine.PerfEngineManager;
import com.miui.powerkeeper.powerchecker.PowerCheckerCloudConfigure;
import com.miui.powerkeeper.provider.HighRefreshRateConfigure;
import com.miui.powerkeeper.provider.HomeGestureStatusProvider;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils;
import com.miui.powerkeeper.utils.AssetUtils;
import com.miui.powerkeeper.utils.IrisServiceUtils;
import com.miui.powerkeeper.utils.PowerKeeperPackageManager;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.Utils;
import com.miui.whetstone.IDisplayScrollListener;
import com.miui.whetstone.IPowerKeeperPolicy;
import com.miui.whetstone.IVideoFpsCallBack;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.onetrack.OneTrack;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import miui.hardware.display.DisplayFeatureManager;
import miui.process.ForegroundInfo;
import miui.process.IActivityChangeListener;
import miui.process.ProcessManager;
import miui.securityspace.CrossUserUtils;
import miui.util.FeatureParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DisplayFrameSetting extends Handler {
    private static final String CLOUD_AUTOMODE_VRR_GROUP = "automode_vrr_group";
    private static final String CLOUD_CAMERA_IDLE_GROUP = "camera_idle_group";
    private static final String CLOUD_DISPLAY_FPS_GROUP = "display_fps_group";
    private static final String CLOUD_EBOOK_IDLE_GROUP = "ebook_idle_pkg";
    private static final String CLOUD_EYE_MODE_GROUP = "eye_protection_mode";
    private static final String CLOUD_FOSS_GROUP = "foss_group";
    private static final String CLOUD_FPS_DEFINE_GROUP = "fps_group";
    private static final String CLOUD_FPS_EXCLUDE_PKG = "fps_exclude_pkg";
    private static final String CLOUD_FPS_SMART_GROUP = "fps_smart_group";
    private static final String CLOUD_FPS_TOP_SHORT_VIDEO_PKG = "fps_top_short_video_pkg";
    private static final String CLOUD_FPS_TOP_VIDEO_IDLE_PKG = "fps_top_video_idle_pkg";
    private static final String CLOUD_FPS_TOP_VIDEO_PKG = "fps_top_video_pkg";
    private static final String CLOUD_FULL_SCREEN_GROUP = "full_screen_fps_group";
    private static final String CLOUD_HIGHEST_FPS_GROUP = "highest_fps_group";
    private static final String CLOUD_INPUT_AUDIO_GROUP = "input_audio_group";
    private static final String CLOUD_LOW_FPS_GROUP = "low_fps_group";
    private static final String CLOUD_MAP_FPS_GROUP = "map_fps_group";
    private static final String CLOUD_NOTE_PKG = "note_pkg";
    private static final String CLOUD_PROMOTION_GROUP = "promotion_group";
    private static final String CLOUD_SMART_BACKLIGHT_GROUP = "smart_backlight_mode";
    private static final String CLOUD_TP_IDLE_GROUP = "tp_idle_pkg";
    private static final String CLOUD_VIOLENT_MODE_SWITCH = "violent_mode_switch";
    private static final String CN_DEFAULT_KEY = "cn_default";
    private static final String CUSTOM_SWITCH = "custom_switch";
    private static final String DEFAULT_NULL = "NULL";
    private static final String FPS_FILE_NAME = "fps_local.config";
    private static String FPS_INTL_LOCAL_CONFIG_FILE_PATH = null;
    private static final String FPS_LOCAL_CONFIG_FILE_PATH;
    private static final String FPS_OPT_DEFAULT_LOCAL_CONFIG_PATH = "smartfps/opt_dafault_config";
    private static final String INTL_DEFAULT_KEY = "intl_default";
    private static final int MSG_NOTIFY_GESTURE = 19;
    private static final String TAG = "DisplayFrameSetting";
    private static final String VIDEO_IRIS_STATUS = "video_iris_status";
    private static List<String> mCameraActivityList;
    private static ArrayMap<String, Boolean> mCloudControlStatus;
    private static int mConfigType;
    private static List<String> mExtremeVideoActivityList;
    private static List<String> mExtremeVideoPackageList;
    private static String mIsCustomFpsSwitch;
    private static boolean mIsHandleTopGgameList;
    private static boolean mIsSmart;
    private static List<String> mLowFpsPkgList;
    private static List<String> mShortVideoApps;
    private static DisplayFrameSetting sInstance;
    private final String ACTION_CAMERA_STATE;
    private final String ACTION_HIGH_FPS_LIST;
    private final String ACTION_JOYOSE_TOP_GAME_LIST;
    private final String ACTION_OVERRIDE_GAME_FRESHRATE;
    private final String ACTION_RECENT_TASK;
    private final String ACTION_SET_ACTIVITY_FPS;
    private final String ACTION_SET_FPS;
    private final String ACTION_TOP_GAME_LIST;
    private final int CAMERA_FPS_VALUE;
    private final String CAMERA_PKG;
    private final int CAMERA_STATE_INIT;
    private final int CAMERA_STATE_RELEASE;
    private final int COOKIE_AUTO_MODE;
    private final int COOKIE_CAMERA;
    private final int COOKIE_CHARGING_FULL;
    private final int COOKIE_DEFAULT;
    private final int COOKIE_DISPLAY_FPS;
    private final int COOKIE_EBOOK_IDLE_APP;
    private final int COOKIE_EXCLUDE;
    private final int COOKIE_FULL_SCREEN;
    private final int COOKIE_GALLERY;
    private final int COOKIE_GAME;
    private final int COOKIE_HIGHEST_FPS;
    private final int COOKIE_INPUT;
    private final int COOKIE_LAUNCHER;
    private final int COOKIE_MAP;
    private final int COOKIE_MULTITASK;
    private final int COOKIE_NOTE_APP;
    private final int COOKIE_POLICY;
    private final int COOKIE_PROMOTION;
    private final int COOKIE_SHORT_VIDEO;
    private final int COOKIE_SMART_FPS;
    private final int COOKIE_THERMAL;
    private final int COOKIE_TOP_APP;
    private final int COOKIE_TP_IDLE_APP;
    private final int COOKIE_VIDEO;
    private final int COOKIE_VIDEO_LOWER_FPS;
    private final int COOKIE_VIOLENT;
    private final int COOKIE_VRR;
    private final int COOKIE_WHITELIST;
    private final String CUSTOM_MODE_SWITCH;
    private final boolean DBG_DISPLAY;
    private final int DEFAULT_FPS_VALUE;
    private final int DELAY_TIME;
    private final int DITHER_STATE;
    private final String EXTRA_CAMERA_STATE;
    private final String EXTRA_GAME_LIST;
    private final String EXTRA_IS_APPEND;
    private final String EXTRA_PKG;
    private final String EXTRA_UID;
    private final int FOSS_STATE;
    private final boolean FPS_SWITCH_DEFAULT;
    private final boolean FPS_SWITCH_THERMAL;
    private final String FSTAR_PKG;
    private final String GALLERY_PKG;
    private final boolean IS_ENABLE_CONTENT_DETECTION;
    private final boolean IS_ENABLE_DISPLAY_DITHER;
    private final boolean IS_ENABLE_DISPLAY_FOSS;
    private final boolean IS_ENABLE_DISPLAY_IDLE;
    private final boolean IS_ENABLE_SMART_DFPS;
    private final boolean IS_ENABLE_VIDEO_DECODE_ONLY;
    private final boolean IS_SUPPORTED_IRIS;
    private final String KEY_PRIV_GAMES;
    private final String KEY_TOP_GAMES;
    private final String LAUNCHER_PKG;
    private final int MAX_SUPPORT_FPS;
    private final String MIUI_REFRESH_RATE;
    private final int MSG_APPEND_DEFAULT_LIST;
    private final int MSG_AUTOMODE_VRR;
    private final int MSG_CAMERA_RESTORE_FPS;
    private final int MSG_CAMERA_STATE_CHANGED;
    private final int MSG_CUSTOM_SWITCH;
    private final int MSG_DISPLAY_FPS_LIST;
    private final int MSG_EBOOK_IDLE_LIST;
    private final int MSG_EXTREME_SCENE_CHANGED;
    private final int MSG_FOREGROUND_CHANGE;
    private final int MSG_FOREGROUND_CHANGE_ACTIVITY;
    private final int MSG_FOSS_SWITCH;
    private final int MSG_FULL_SCREEN_SWITCH;
    private final int MSG_HIGHEST_FPS_SWITCH;
    private final int MSG_HIGH_FPS_NOTIFY;
    private final int MSG_INIT;
    private final int MSG_INPUT_IN_LIST;
    private final int MSG_IRIS_CONFIG;
    private final int MSG_LOW_FPS_LIST;
    private final int MSG_MAP_SWITCH;
    private final int MSG_NOTE_SWITCH;
    public final int MSG_NOTIFY_AM_STOP;
    private final int MSG_NOTIFY_VEDIO_IDLE_STATUS;
    private final int MSG_OVERRIDE_GAME_FRESHRATE;
    private final int MSG_PKG_EXISTENT_CHANGED;
    private final int MSG_PROMOTION_LIST;
    private final int MSG_RECENT_TASK;
    private final int MSG_RESET_DEFAULT_LIST;
    private final int MSG_RESET_EXCLUDE_LIST;
    private final int MSG_RESET_EYE_MODE_LIST;
    private final int MSG_RESET_HIGH_PRIORITY_LIST;
    private final int MSG_RESET_SHORT_VIDEO_TOP_LIST;
    private final int MSG_RESET_TOP_DEFINE_LIST;
    private final int MSG_RESET_TOP_SMART_LIST;
    private final int MSG_RESET_VIDEO_IDLE_LIST;
    private final int MSG_RESET_VIDEO_TOP_LIST;
    private final int MSG_SMART_BACKLIGHT_MODE_LIST;
    private final int MSG_SPLIT_SCREEN_CHANGED;
    private final int MSG_SWTICH_TO_IRIS_PQ;
    private final int MSG_TP_IDLE_LIST;
    private final int MSG_UPDATE_FPS;
    private final int MSG_UPDATE_MEMC_STATE;
    private final int MSG_UPDATE_SDR2HDR_STATE;
    private final int MSG_VIDEO_GAME_SCENE_CHANGED;
    private final int MSG_VIOLENT_MODE_SWITCH;
    private final String NOTE_PKG;
    private final int POLICT_BLACKLIST;
    private final int POLICT_WHITELIST;
    private final int SCREEN_DPI_MODE;
    private final int SF_AFFINITY_GOLD;
    private final int SF_AFFINITY_SILIVER;
    private final boolean SF_ENBALE_AFFINITY;
    private final int SMART_DFPS_STATE;
    private final int SMART_FPS_DISABLE;
    private final int SMART_FPS_ENABLE;
    private final String SPEED_MODE_KEY;
    public final int STATUS_ENTER_GAME;
    public final int STATUS_ENTER_VIDEO;
    public final int STATUS_EXIT_GAME;
    public final int STATUS_EXIT_VIDEO;
    private final int SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_CPU_CLUSTER;
    private final int SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_IDLE;
    private final int SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_NOTE;
    private final int SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_SMART_DFPS;
    private final String SYSTEMUI_PKG;
    private final int USER_CONFIGURE_PERFORMANCE;
    private final int VIDEO_FPS_VALUE;
    private final String YOUKU_PKG;
    private IActivityChangeListener.Stub mActListener;
    private boolean mAppletsSwitch;
    private ArrayMap<String, Integer> mAudioApps;
    private AudioManager mAudioManager;
    private boolean mAudioRecordStatus;
    private boolean mAudioRecordSwitch;
    private AudioManager.AudioRecordingCallback mAudioRecordingCallback;
    private ArraySet<String> mAutoModeApps;
    private boolean mBacklightModeSwitch;
    private String[] mBlackListActivityApplets;
    private String[] mBlacklistActivity_Camera;
    private String[] mBlacklistActivity_LowFps;
    private String[] mBlacklistActivity_Vr;
    private boolean mCameraPowerSaveSwitch;
    private int mCameraPowerSaveValue;
    private boolean mChargingSwitch;
    private boolean mCheckPowerSaveCamera;
    private b.a mCloudObserver;
    private String mConfigVersion;
    private Context mContext;
    private String mCurActivityName;
    private String mCurExtremeVideoActivity;
    private String mCurExtremeVideoPackage;
    private int mCurrentCookie;
    private ForegroundInfo mCurrentFgInfo;
    private volatile String mCurrentFgPkg;
    private int mCurrentFps;
    private ArrayMap<String, Integer> mCustomApps;
    public String[] mCustomFpsDeviceArray;
    private ArraySet<String> mCustomHighestFpsApps;
    private int mDefaultFps;
    private ArraySet<String> mDefaultGameApp;
    private ArraySet<String> mDefineHighestFpsApps;
    private Display mDisplay;
    private ArraySet<String> mDisplayFpsApps;
    private final DisplayManager.DisplayListener mDisplayListener;
    private IDisplayScrollListener mDsListener;
    private List<String> mDuelMemcPackage;
    private ArraySet<String> mEbookidleApps;
    private int mEnterHighFpsValue;
    private ArraySet<String> mExcludeApps;
    private boolean mEyeModeSwitch;
    private ArraySet<String> mFullScreenFpsApps;
    private String mGameVideoScenePkg;
    private boolean mHasIrisCommandSet;
    private String[] mHighFpsListActivity;
    private boolean mHighestFpsSwitch;
    private boolean mIdleSceneSwitch;
    private ArrayMap<String, Integer> mInputApps;
    private boolean mInputFlag;
    private boolean mInputModeSwitch;
    private int mInputStatus;
    private int mInvalidValue;
    private boolean mIsAutoModeSwitch;
    private boolean mIsBlackList;
    private boolean mIsCameraIdleSwitch;
    private boolean mIsChargingFullSwitch;
    private boolean mIsCustom;
    private boolean mIsDisplayFpsSwitch;
    private boolean mIsDitherCloudSwitch;
    private boolean mIsDuringGesture;
    private boolean mIsEbookIdleSwitch;
    private boolean mIsEnterGameScene;
    private boolean mIsEnterRecentTask;
    private boolean mIsEnterVideoScene;
    private boolean mIsExcludeSwitch;
    private int mIsExtremeVideoModeOn;
    private boolean mIsFgChange;
    private boolean mIsFossCloudSwitch;
    private boolean mIsFullScreenSwitch;
    private boolean mIsIrisPQOn;
    private boolean mIsLockScreen;
    private boolean mIsLowFps;
    private boolean mIsMapFpsSwitch;
    private boolean mIsNoteCloudSwitch;
    private boolean mIsOnFullScreen;
    private boolean mIsOnScroll;
    private boolean mIsPromotionSwitch;
    private boolean mIsReciverRegistered;
    private boolean mIsShortVideoSwitch;
    private boolean mIsSplitScreen;
    private boolean mIsSupportLowFps;
    private boolean mIsTpIdleSwitch;
    private int mIsUltimate;
    private boolean mIsVideoIdleSwitch;
    private boolean mIsVrrSwitch;
    private boolean mIsWhiteFpsList;
    private boolean mIsWhitelistPolicy;
    private int mLastFossCookie;
    private int mLastFps;
    private boolean mLevelFullSwitch;
    private LocalLog mLocalLog;
    private boolean mLowFpsSwitch;
    private ArraySet<String> mMapFpsApps;
    private String[] mMemcParam;
    private boolean mParseInited;
    private int mPowerMode;
    private String mPrevFgPkg;
    private int mPrevScreenState;
    private ArrayMap<String, Integer> mPrivGames;
    private ArraySet<String> mPromotionApps;
    private float mRealRefreshRate;
    private BroadcastReceiver mReceiver;
    private String[] mSdr2HdrParam;
    private boolean mShortVideoSwitch;
    private List<String> mSingleMemcPackage;
    private ArraySet<String> mSmartFpsApps;
    private boolean mSystemUiDefineChange;
    private ArrayMap<String, Integer> mTopApps;
    private ArrayMap<String, Integer> mTopDefineApps;
    private ArrayMap<String, Integer> mTopSmartApps;
    private ArraySet<String> mTpidleApps;
    private int mUserFps;
    private IActivityChangeListener.Stub mVideoActListener;
    private ArraySet<String> mVideoApps;
    private final IVideoFpsCallBack mVideoFpsCallBack;
    private boolean mVideoGameSwitch;
    private ArraySet<String> mVideoIdleApps;
    private ArraySet<String> mVideoLowerFpsApps;
    private boolean mVideoLowerFpsSwitch;
    private boolean mViolentModeSwitch;
    private ArraySet<String> mVrrApps;
    private String[] mWhitelistActivity;
    private IWhetstoneActivityManager mWs;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("smartfps/");
        String str = Build.DEVICE;
        sb.append(str);
        FPS_LOCAL_CONFIG_FILE_PATH = sb.toString();
        FPS_INTL_LOCAL_CONFIG_FILE_PATH = "smartfps/" + str + "_intl";
        mCloudControlStatus = new ArrayMap<>();
        mCameraActivityList = new ArrayList();
        mLowFpsPkgList = new ArrayList();
        mShortVideoApps = new ArrayList();
        mExtremeVideoActivityList = new ArrayList();
        mExtremeVideoPackageList = new ArrayList();
        mIsHandleTopGgameList = false;
        mIsSmart = false;
    }

    private DisplayFrameSetting(Context context, Looper looper) {
        super(looper);
        this.DBG_DISPLAY = Log.isLoggable("power.display", 3);
        this.MSG_RESET_DEFAULT_LIST = 1;
        this.MSG_APPEND_DEFAULT_LIST = 2;
        this.MSG_RESET_HIGH_PRIORITY_LIST = 3;
        this.MSG_RESET_TOP_DEFINE_LIST = 4;
        this.MSG_RESET_VIDEO_TOP_LIST = 5;
        this.MSG_CAMERA_STATE_CHANGED = 6;
        this.MSG_SPLIT_SCREEN_CHANGED = 7;
        this.MSG_CAMERA_RESTORE_FPS = 8;
        this.MSG_RESET_EXCLUDE_LIST = 9;
        this.MSG_PKG_EXISTENT_CHANGED = 10;
        this.MSG_RECENT_TASK = 11;
        this.MSG_OVERRIDE_GAME_FRESHRATE = 12;
        this.MSG_UPDATE_FPS = 15;
        this.MSG_NOTIFY_AM_STOP = 17;
        this.MSG_VIDEO_GAME_SCENE_CHANGED = 18;
        this.SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_SMART_DFPS = 1107;
        this.SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_IDLE = 31107;
        this.SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_CPU_CLUSTER = 31038;
        this.SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_NOTE = 31118;
        this.MSG_RESET_EYE_MODE_LIST = 20;
        this.MSG_SMART_BACKLIGHT_MODE_LIST = 21;
        this.MSG_EBOOK_IDLE_LIST = 23;
        this.MSG_TP_IDLE_LIST = 24;
        this.MSG_RESET_TOP_SMART_LIST = 25;
        this.MSG_FOREGROUND_CHANGE = 26;
        this.MSG_INPUT_IN_LIST = 27;
        this.MSG_LOW_FPS_LIST = 28;
        this.MSG_NOTIFY_VEDIO_IDLE_STATUS = 29;
        this.MSG_RESET_SHORT_VIDEO_TOP_LIST = 30;
        this.MSG_RESET_VIDEO_IDLE_LIST = 32;
        this.MSG_FOREGROUND_CHANGE_ACTIVITY = 33;
        this.MSG_PROMOTION_LIST = 34;
        this.MSG_DISPLAY_FPS_LIST = 35;
        this.MSG_FULL_SCREEN_SWITCH = 36;
        this.MSG_CUSTOM_SWITCH = 37;
        this.MSG_MAP_SWITCH = 38;
        this.MSG_AUTOMODE_VRR = 39;
        this.MSG_VIOLENT_MODE_SWITCH = 40;
        this.MSG_FOSS_SWITCH = 41;
        this.MSG_NOTE_SWITCH = 42;
        this.MSG_IRIS_CONFIG = 43;
        this.MSG_UPDATE_MEMC_STATE = 44;
        this.MSG_SWTICH_TO_IRIS_PQ = 45;
        this.MSG_UPDATE_SDR2HDR_STATE = 46;
        this.MSG_EXTREME_SCENE_CHANGED = 47;
        this.MSG_HIGH_FPS_NOTIFY = 48;
        this.MSG_INIT = 49;
        this.MSG_HIGHEST_FPS_SWITCH = 50;
        this.mConfigVersion = "";
        this.KEY_TOP_GAMES = "key_top_names";
        this.KEY_PRIV_GAMES = "key_priv_names";
        this.CAMERA_PKG = "com.android.camera";
        this.SYSTEMUI_PKG = "power.systemui";
        this.GALLERY_PKG = "com.miui.gallery";
        this.FSTAR_PKG = "com.fstar.Pattern";
        this.YOUKU_PKG = "com.youku.phone";
        this.MIUI_REFRESH_RATE = "miui_refresh_rate";
        this.CUSTOM_MODE_SWITCH = "custom_mode_switch";
        this.NOTE_PKG = "com.miui.notes";
        this.SPEED_MODE_KEY = "speed_mode";
        this.SCREEN_DPI_MODE = 24;
        this.DEFAULT_FPS_VALUE = 60;
        this.CAMERA_FPS_VALUE = 60;
        this.VIDEO_FPS_VALUE = 60;
        this.SMART_DFPS_STATE = 29;
        this.SMART_FPS_DISABLE = 0;
        this.SMART_FPS_ENABLE = 1;
        this.FOSS_STATE = 40;
        this.DITHER_STATE = 41;
        this.COOKIE_LAUNCHER = 242;
        this.COOKIE_GALLERY = 243;
        this.COOKIE_DEFAULT = 244;
        this.COOKIE_WHITELIST = 245;
        this.COOKIE_EXCLUDE = 247;
        this.COOKIE_VIDEO = 248;
        this.COOKIE_TOP_APP = 249;
        this.COOKIE_MULTITASK = 251;
        this.COOKIE_CAMERA = 252;
        this.COOKIE_THERMAL = 253;
        this.COOKIE_GAME = 254;
        this.COOKIE_POLICY = 256;
        this.COOKIE_EBOOK_IDLE_APP = 257;
        this.COOKIE_TP_IDLE_APP = 258;
        this.COOKIE_SHORT_VIDEO = 259;
        this.COOKIE_PROMOTION = 260;
        this.COOKIE_DISPLAY_FPS = 261;
        this.COOKIE_NOTE_APP = 262;
        this.COOKIE_CHARGING_FULL = 263;
        this.COOKIE_VIOLENT = 264;
        this.COOKIE_FULL_SCREEN = 266;
        this.COOKIE_MAP = 265;
        this.COOKIE_AUTO_MODE = 267;
        this.COOKIE_VRR = 269;
        this.COOKIE_INPUT = 270;
        this.COOKIE_VIDEO_LOWER_FPS = 271;
        this.COOKIE_HIGHEST_FPS = 272;
        this.COOKIE_SMART_FPS = 251;
        this.POLICT_WHITELIST = 2;
        this.POLICT_BLACKLIST = 3;
        this.ACTION_HIGH_FPS_LIST = "com.xiaomi.joyose.HIGH_FPS_LIST";
        this.ACTION_JOYOSE_TOP_GAME_LIST = "intent.action.TOP_GAME_LIST";
        this.ACTION_TOP_GAME_LIST = "com.miui.securitycenter.intent.action.TOP_GAME_LIST";
        this.ACTION_CAMERA_STATE = ScenarioConfig.ACTION_CAMERA_STATE;
        this.ACTION_SET_FPS = "com.miui.powerkeeper.set_fps";
        this.ACTION_RECENT_TASK = "com.miui.powerkeeper.RECENT_TASK";
        this.ACTION_OVERRIDE_GAME_FRESHRATE = "com.xiaomi.joyose.OVERRIDE_GAME_FRESHRATE";
        this.ACTION_SET_ACTIVITY_FPS = "com.miui.powerkeeper.SET_ACTIVITY_FPS";
        this.EXTRA_IS_APPEND = "isAppend";
        this.EXTRA_GAME_LIST = "gameList";
        this.EXTRA_UID = "application_uid";
        this.EXTRA_CAMERA_STATE = "camera_state";
        this.EXTRA_PKG = "package_name";
        this.CAMERA_STATE_INIT = 1;
        this.CAMERA_STATE_RELEASE = 2;
        this.mEyeModeSwitch = false;
        this.mBacklightModeSwitch = false;
        this.mIsSupportLowFps = false;
        this.mInputModeSwitch = true;
        this.mInputFlag = false;
        this.STATUS_ENTER_GAME = 0;
        this.STATUS_EXIT_GAME = 1;
        this.STATUS_ENTER_VIDEO = 2;
        this.STATUS_EXIT_VIDEO = 3;
        this.mPrivGames = new ArrayMap<>();
        this.mDefaultGameApp = new ArraySet<>(64);
        this.mTopApps = new ArrayMap<>();
        this.mTopDefineApps = new ArrayMap<>();
        this.mTopSmartApps = new ArrayMap<>();
        this.mCustomApps = new ArrayMap<>();
        this.mVideoApps = new ArraySet<>(10);
        this.mVideoIdleApps = new ArraySet<>(10);
        this.mExcludeApps = new ArraySet<>(10);
        this.mSmartFpsApps = new ArraySet<>(10);
        this.mEbookidleApps = new ArraySet<>(10);
        this.mTpidleApps = new ArraySet<>(10);
        this.mPromotionApps = new ArraySet<>(10);
        this.mDisplayFpsApps = new ArraySet<>(10);
        this.mFullScreenFpsApps = new ArraySet<>(10);
        this.mVideoLowerFpsApps = new ArraySet<>(10);
        this.mMapFpsApps = new ArraySet<>(10);
        this.mAutoModeApps = new ArraySet<>(10);
        this.mVrrApps = new ArraySet<>(10);
        this.mDefineHighestFpsApps = new ArraySet<>(10);
        this.mCustomHighestFpsApps = new ArraySet<>(10);
        this.mInputApps = new ArrayMap<>();
        this.mAudioApps = new ArrayMap<>();
        this.mCustomFpsDeviceArray = new String[]{"thor", "unicorn", "mayfly", "zizhan", "duchamp", "manet", "vermeer", "diting", "socrates", "mondrian", "rembrandt", "yudi", "liuqin", "zeus", "cupid", "ingres", "psyche", "redwood", "daumier", "matisse", "rubens", "plato", "xaga", "ruby", "rubyplus", "aurora_intl", "chenfeng_intl", "breeze_intl", "ruyi_intl", "sheng_intl", "corot"};
        this.mSingleMemcPackage = new ArrayList();
        this.mDuelMemcPackage = new ArrayList();
        this.IS_SUPPORTED_IRIS = SystemProperties.getBoolean("ro.vendor.display.iris_x7.support", false);
        this.mPrevScreenState = -1;
        this.mParseInited = false;
        this.mIsReciverRegistered = false;
        this.mCurrentCookie = 0;
        this.mCurrentFgInfo = null;
        this.mLocalLog = new LocalLog(256);
        this.mIsSplitScreen = false;
        this.FPS_SWITCH_DEFAULT = SystemProperties.getBoolean("ro.vendor.fps.switch.default", false);
        this.IS_ENABLE_SMART_DFPS = SystemProperties.getBoolean("ro.vendor.smart_dfps.enable", false);
        this.IS_ENABLE_VIDEO_DECODE_ONLY = SystemProperties.getBoolean("ro.vendor.video.decode.only", false);
        this.IS_ENABLE_DISPLAY_FOSS = SystemProperties.getBoolean("ro.vendor.display.foss", false);
        this.IS_ENABLE_DISPLAY_DITHER = SystemProperties.getBoolean("ro.vendor.display.dither", false);
        this.IS_ENABLE_CONTENT_DETECTION = SystemProperties.getBoolean("ro.surface_flinger.use_content_detection_for_refresh_rate", false);
        this.IS_ENABLE_DISPLAY_IDLE = SystemProperties.getBoolean("ro.vendor.display.touch.idle.enable", false);
        this.FPS_SWITCH_THERMAL = SystemProperties.getBoolean("ro.vendor.fps.switch.thermal", false);
        this.SF_ENBALE_AFFINITY = SystemProperties.getBoolean("ro.miui.surfaceflinger_affinity", false);
        this.MAX_SUPPORT_FPS = FeatureParser.getInteger("support_max_fps", 120);
        this.LAUNCHER_PKG = SystemProperties.get("ro.miui.product.home", "com.miui.home");
        this.DELAY_TIME = SystemProperties.getBoolean("persist.sys.smartpower.display.enable", false) ? 0 : 1200;
        this.mIsWhiteFpsList = false;
        this.SF_AFFINITY_SILIVER = 0;
        this.SF_AFFINITY_GOLD = 1;
        this.USER_CONFIGURE_PERFORMANCE = 1;
        this.mIsWhitelistPolicy = false;
        this.mCurrentFps = -1;
        this.mUserFps = 0;
        this.mInputStatus = 0;
        this.mPrevFgPkg = "";
        this.mCurrentFgPkg = "";
        this.mIsFgChange = false;
        this.mLastFps = 60;
        this.mEnterHighFpsValue = 60;
        this.mIsDuringGesture = false;
        this.mIsEnterRecentTask = false;
        this.mIsEnterVideoScene = false;
        this.mIsEnterGameScene = false;
        this.mGameVideoScenePkg = "";
        this.mIsFossCloudSwitch = true;
        this.mIsNoteCloudSwitch = false;
        this.mLastFossCookie = 0;
        this.mInvalidValue = 0;
        this.mVideoGameSwitch = false;
        this.mAudioRecordStatus = false;
        this.mIsDitherCloudSwitch = false;
        this.mAudioRecordSwitch = true;
        this.mLowFpsSwitch = true;
        this.mCameraPowerSaveSwitch = false;
        this.mChargingSwitch = false;
        this.mShortVideoSwitch = true;
        this.mIsEbookIdleSwitch = true;
        this.mIsTpIdleSwitch = true;
        this.mIsVideoIdleSwitch = true;
        this.mIsCameraIdleSwitch = true;
        this.mIsShortVideoSwitch = true;
        this.mIsExcludeSwitch = true;
        this.mIsPromotionSwitch = true;
        this.mIsDisplayFpsSwitch = true;
        this.mIsChargingFullSwitch = false;
        this.mIsOnScroll = false;
        this.mCurActivityName = null;
        this.mIsLowFps = false;
        this.mIsBlackList = false;
        this.mCheckPowerSaveCamera = false;
        this.mIsUltimate = 0;
        this.mCameraPowerSaveValue = 5;
        this.mLevelFullSwitch = false;
        this.mIsCustom = false;
        this.mIsFullScreenSwitch = true;
        this.mIsMapFpsSwitch = true;
        this.mIsAutoModeSwitch = true;
        this.mIsVrrSwitch = true;
        this.mIdleSceneSwitch = false;
        this.mPowerMode = 2;
        this.mViolentModeSwitch = false;
        this.mVideoLowerFpsSwitch = true;
        this.mIsLockScreen = false;
        this.mSystemUiDefineChange = false;
        this.mIsOnFullScreen = false;
        this.mAppletsSwitch = false;
        this.mHighestFpsSwitch = false;
        this.mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent == null) {
                    return;
                }
                String action = intent.getAction();
                if ("intent.action.TOP_GAME_LIST".equals(action)) {
                    synchronized (this) {
                        DisplayFrameSetting.mIsHandleTopGgameList = true;
                        DisplayFrameSetting.this.handleTopGameList(intent);
                        DisplayFrameSetting.this.writeLocalLog("onReceive joyose TOP_GAME_LIST");
                    }
                    return;
                }
                if ("com.miui.securitycenter.intent.action.TOP_GAME_LIST".equals(action)) {
                    if (DisplayFrameSetting.mIsHandleTopGgameList) {
                        return;
                    }
                    synchronized (this) {
                        DisplayFrameSetting.this.handleTopGameList(intent);
                        DisplayFrameSetting.this.writeLocalLog("onReceive securitycenter TOP_GAME_LIST");
                    }
                    return;
                }
                if ("com.xiaomi.joyose.HIGH_FPS_LIST".equals(action)) {
                    ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("high_fps_list");
                    Log.i(DisplayFrameSetting.TAG, "onReceive privGames:" + stringArrayListExtra);
                    DisplayFrameSetting.this.mLocalLog.log("onReceive HIGH_FPS_LIST");
                    if (stringArrayListExtra == null) {
                        return;
                    }
                    DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                    displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(3, stringArrayListExtra));
                    return;
                }
                if (ScenarioConfig.ACTION_CAMERA_STATE.equals(action)) {
                    int intExtra = intent.getIntExtra("application_uid", -1);
                    int intExtra2 = intent.getIntExtra("camera_state", -1);
                    String stringExtra = intent.getStringExtra("package_name");
                    DisplayFrameSetting.this.writeLocalLog("onReceive camera uid=" + intExtra + " state=" + intExtra2 + " pkg=" + stringExtra);
                    if (stringExtra == null || stringExtra.isEmpty() || "com.android.camera".equals(stringExtra)) {
                        return;
                    }
                    DisplayFrameSetting.this.sendMessage(DisplayFrameSetting.this.obtainMessage(6, intExtra, intExtra2, stringExtra));
                    return;
                }
                if ("com.miui.powerkeeper.set_fps".equals(action)) {
                    if (intent.getIntExtra("uid", -1) != -1) {
                        DisplayFrameSetting.this.sendMessage(DisplayFrameSetting.this.obtainMessage(8, Boolean.valueOf(intent.getBooleanExtra("isRestore", false))));
                        return;
                    }
                    if (DisplayFrameSetting.this.mVideoGameSwitch) {
                        String stringExtra2 = intent.getStringExtra("pkg");
                        int intExtra3 = intent.getIntExtra(OneTrack.Param.MODEL, -1);
                        DisplayFrameSetting.this.writeLocalLog("Game or Video scene change pkg:" + stringExtra2 + " model:" + intExtra3);
                        DisplayFrameSetting.this.sendMessage(DisplayFrameSetting.this.obtainMessage(18, intExtra3, -1, stringExtra2));
                        return;
                    }
                    return;
                }
                if ("com.miui.powerkeeper.RECENT_TASK".equals(action)) {
                    boolean booleanExtra = intent.getBooleanExtra("isEnter", false);
                    if (DisplayFrameSetting.this.DBG_DISPLAY) {
                        Log.d(DisplayFrameSetting.TAG, "ACTION_RECENT_TASK isEnter=" + booleanExtra);
                    }
                    DisplayFrameSetting.this.sendMessage(DisplayFrameSetting.this.obtainMessage(11, Boolean.valueOf(booleanExtra)));
                    return;
                }
                if ("com.xiaomi.joyose.OVERRIDE_GAME_FRESHRATE".equals(action)) {
                    String stringExtra3 = intent.getStringExtra("override_pkg_name");
                    int intExtra4 = intent.getIntExtra("override_freshrate", -1);
                    if (DisplayFrameSetting.this.DBG_DISPLAY) {
                        Log.d(DisplayFrameSetting.TAG, "ACTION_OVERRIDE_GAME_FRESHRATE pkg=" + stringExtra3 + " fps=" + intExtra4);
                    }
                    if (stringExtra3 == null || stringExtra3.isEmpty() || intExtra4 <= 0) {
                        return;
                    }
                    DisplayFrameSetting.this.sendMessage(DisplayFrameSetting.this.obtainMessage(12, intExtra4, -1, stringExtra3));
                    return;
                }
                if ("com.miui.powerkeeper.SET_ACTIVITY_FPS".equals(action)) {
                    boolean booleanExtra2 = intent.getBooleanExtra("isEnter", false);
                    String stringExtra4 = intent.getStringExtra("package_name");
                    if (DisplayFrameSetting.this.DBG_DISPLAY) {
                        Log.d(DisplayFrameSetting.TAG, "ACTION_SET_ACTIVITY_FPS pkg=" + stringExtra4 + " isEnter=" + booleanExtra2);
                    }
                    if (stringExtra4 == null || stringExtra4.isEmpty()) {
                        return;
                    }
                    DisplayFrameSetting.this.sendMessage(DisplayFrameSetting.this.obtainMessage(15, booleanExtra2 ? 60 : DisplayFrameSetting.this.mUserFps, -1, stringExtra4));
                }
            }
        };
        this.mVideoActListener = new IActivityChangeListener.Stub() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.7
            public void onActivityChanged(ComponentName componentName, ComponentName componentName2) {
                DisplayFrameSetting.this.mCurExtremeVideoActivity = componentName2.getClassName();
                DisplayFrameSetting.this.mCurExtremeVideoPackage = componentName2.getPackageName();
                if (DisplayFrameSetting.this.mCurExtremeVideoActivity == null || DisplayFrameSetting.this.mCurExtremeVideoPackage == null) {
                    return;
                }
                if (DisplayFrameSetting.this.DBG_DISPLAY) {
                    Log.d(DisplayFrameSetting.TAG, "Extreme Video Mode IActivityChangeListener: activity=" + DisplayFrameSetting.this.mCurExtremeVideoActivity);
                }
                if (DisplayFrameSetting.this.mIsExtremeVideoModeOn != 1) {
                    if (DisplayFrameSetting.this.DBG_DISPLAY) {
                        Log.d(DisplayFrameSetting.TAG, "Extreme Video Mode IActivityChangeListener: mIsExtremeVideoMode=false");
                    }
                } else if (DisplayFrameSetting.this.hasMessages(17)) {
                    if (DisplayFrameSetting.this.DBG_DISPLAY) {
                        Log.d(DisplayFrameSetting.TAG, "Extreme Video Mode IActivityChangeListener: In animation");
                    }
                } else if (ArrayUtils.contains(DisplayFrameSetting.mExtremeVideoActivityList, DisplayFrameSetting.this.mCurExtremeVideoActivity)) {
                    if (DisplayFrameSetting.this.mCurExtremeVideoPackage.equals(DisplayFrameSetting.this.mCurrentFgPkg)) {
                        DisplayFrameSetting.this.sendEmptyMessage(47);
                    }
                } else if (ArrayUtils.contains(DisplayFrameSetting.mExtremeVideoPackageList, DisplayFrameSetting.this.mCurExtremeVideoPackage)) {
                    IrisServiceUtils.getInstance(DisplayFrameSetting.this.mContext).irisConfigureSet(5);
                    Settings.System.putInt(DisplayFrameSetting.this.mContext.getContentResolver(), DisplayFrameSetting.VIDEO_IRIS_STATUS, 0);
                }
            }
        };
        this.mActListener = new IActivityChangeListener.Stub() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.8
            public void onActivityChanged(ComponentName componentName, ComponentName componentName2) {
                DisplayFrameSetting.this.mCurActivityName = componentName2.getClassName();
                String packageName = componentName2.getPackageName();
                if (DisplayFrameSetting.this.mCurActivityName == null || packageName == null) {
                    return;
                }
                if (DisplayFrameSetting.this.DBG_DISPLAY) {
                    Log.d(DisplayFrameSetting.TAG, "IActivityChangeListener:" + DisplayFrameSetting.this.mCurActivityName);
                }
                DisplayFrameSetting.this.removeMessages(26);
                if (ArrayUtils.contains(DisplayFrameSetting.this.mBlacklistActivity_LowFps, DisplayFrameSetting.this.mCurActivityName)) {
                    DisplayFrameSetting.this.registerDisplayScrollListener();
                    DisplayFrameSetting.this.sendEmptyMessageDelayed(33, 500L);
                } else if (ArrayUtils.contains(DisplayFrameSetting.this.mBlacklistActivity_Vr, DisplayFrameSetting.this.mCurActivityName) || ArrayUtils.contains(DisplayFrameSetting.this.mBlackListActivityApplets, DisplayFrameSetting.this.mCurActivityName)) {
                    DisplayFrameSetting.this.sendEmptyMessageDelayed(33, 800L);
                } else if (ArrayUtils.contains(DisplayFrameSetting.this.mHighFpsListActivity, DisplayFrameSetting.this.mCurActivityName)) {
                    DisplayFrameSetting.this.mIsBlackList = true;
                    if (DisplayFrameSetting.this.hasMessages(17)) {
                        if (DisplayFrameSetting.this.DBG_DISPLAY) {
                            Log.d(DisplayFrameSetting.TAG, "IActivityChangeListener: In animation");
                            return;
                        }
                        return;
                    } else if (DisplayFrameSetting.this.mEnterHighFpsValue >= 120) {
                        DisplayFrameSetting.this.sendEmptyMessage(33);
                    }
                } else {
                    DisplayFrameSetting.this.mIsBlackList = false;
                    if (!"com.android.camera".equals(DisplayFrameSetting.this.mCurrentFgPkg)) {
                        DisplayFrameSetting.this.sendEmptyMessage(26);
                    }
                }
                DisplayFrameSetting.this.notifySFIChange();
            }
        };
        this.mDsListener = new IDisplayScrollListener.Stub() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.9
            public void onScroll(boolean z2) {
                DisplayFrameSetting.this.mIsOnScroll = z2;
                DisplayFrameSetting.this.writeLocalLog("IDisplayScrollListener mIsOnScroll:" + DisplayFrameSetting.this.mIsOnScroll);
                DisplayFrameSetting.this.removeMessages(26);
                if (ArrayUtils.contains(DisplayFrameSetting.this.mBlacklistActivity_LowFps, DisplayFrameSetting.this.mCurActivityName)) {
                    DisplayFrameSetting.this.sendEmptyMessage(26);
                }
            }
        };
        this.mVideoFpsCallBack = new IVideoFpsCallBack.Stub() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.10
            public void onVideoFpsChange(int i2, int i3) {
                synchronized (DisplayFrameSetting.this.mVideoFpsCallBack) {
                    try {
                        try {
                            String[] packagesForUid = DisplayFrameSetting.this.mContext.getPackageManager().getPackagesForUid(i2);
                            if (packagesForUid != null && packagesForUid.length > 0) {
                                Log.i(DisplayFrameSetting.TAG, "onVideoFpsChange uid = " + i2 + " pkg = " + packagesForUid[0]);
                                if ("com.youku.phone".equals(packagesForUid[0])) {
                                    DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                                    displayFrameSetting.mEnterHighFpsValue = displayFrameSetting.getSuitableFps(displayFrameSetting.getSupportFps(), i3);
                                    if (DisplayFrameSetting.this.mEnterHighFpsValue >= 120) {
                                        DisplayFrameSetting.this.sendEmptyMessageDelayed(48, 1500L);
                                    }
                                    if (DisplayFrameSetting.this.mEnterHighFpsValue == DisplayFrameSetting.this.mCurrentFps) {
                                        Log.i(DisplayFrameSetting.TAG, "onVideoFpsChange fps has already been set");
                                        return;
                                    } else if (DisplayFrameSetting.this.hasMessages(17)) {
                                        if (DisplayFrameSetting.this.DBG_DISPLAY) {
                                            Log.d(DisplayFrameSetting.TAG, "IVideoFpsCallBack: In animation");
                                        }
                                        return;
                                    } else {
                                        DisplayFrameSetting displayFrameSetting2 = DisplayFrameSetting.this;
                                        displayFrameSetting2.onForegroundChanged(displayFrameSetting2.mCurrentFgInfo);
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        Log.i(DisplayFrameSetting.TAG, "onVideoFpsChange mEnterHighFpsValue = " + DisplayFrameSetting.this.mEnterHighFpsValue + " video fps = " + i3);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mAudioRecordingCallback = new AudioManager.AudioRecordingCallback() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.11
            @Override // android.media.AudioManager.AudioRecordingCallback
            public void onRecordingConfigChanged(List<AudioRecordingConfiguration> list) {
                boolean z2;
                if (DisplayFrameSetting.this.mAudioRecordSwitch) {
                    if (list.isEmpty()) {
                        z2 = false;
                    } else {
                        z2 = false;
                        for (int i2 = 0; i2 < list.size(); i2++) {
                            AudioRecordingConfiguration audioRecordingConfiguration = list.get(i2);
                            if (DisplayFrameSetting.this.mAudioApps.containsKey(audioRecordingConfiguration.getClientPackageName()) && audioRecordingConfiguration.getAudioSource() == 1) {
                                z2 = true;
                            }
                        }
                    }
                    if (z2) {
                        if (DisplayFrameSetting.this.mIsSupportLowFps) {
                            DisplayFrameSetting.this.registerDisplayScrollListener();
                        }
                        DisplayFrameSetting.this.mAudioRecordStatus = true;
                        DisplayFrameSetting.this.sendEmptyMessageDelayed(26, 200L);
                        return;
                    }
                    if (DisplayFrameSetting.this.mAudioRecordStatus) {
                        DisplayFrameSetting.this.mAudioRecordStatus = false;
                        if (DisplayFrameSetting.this.hasMessages(26)) {
                            DisplayFrameSetting.this.removeMessages(26);
                        }
                        DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                        displayFrameSetting.onForegroundChanged(displayFrameSetting.mCurrentFgInfo);
                    }
                }
            }
        };
        this.mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.12
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i2) {
                if (i2 != 0) {
                    return;
                }
                DisplayFrameSetting.this.handleIrisDisplayState();
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i2) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i2) {
            }
        };
        this.mContext = context;
        this.mCloudObserver = b.a.p();
        registerCloudObserver();
        this.mUserFps = getUserFps();
        sendEmptyMessage(49);
    }

    private void appOrHomeToRecentTask() {
        ForegroundInfo foregroundInfo = new ForegroundInfo();
        foregroundInfo.mForegroundPackageName = this.LAUNCHER_PKG;
        this.mCurrentFgInfo = foregroundInfo;
        Integer num = this.mTopApps.get("power.systemui");
        if (num != null) {
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "appOrHomeToRecentTask systemUiFps != null set systemUiFps Fps:" + num);
            }
            setScreenEffect(this.LAUNCHER_PKG, num.intValue(), 249);
            writeLocalLog("appOrHomeToRecentTask systemUiFps:" + num);
            return;
        }
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "appOrHomeToRecentTask systemUiFps == null set user Fps:" + this.mUserFps);
        }
        setScreenEffect(this.LAUNCHER_PKG, this.mUserFps, 249);
        writeLocalLog("appOrHomeToRecentTask mUserFps:" + this.mUserFps);
    }

    private void appToAppGestrue() {
        if (!this.mIsDuringGesture) {
            writeLocalLog("appToAppGestrue. fg is change mCurrentFgPkg:" + this.mCurrentFgPkg);
            return;
        }
        setFpsAync(this.mCurrentFgPkg, this.mLastFps);
        this.mIsDuringGesture = false;
        if (this.IS_SUPPORTED_IRIS && this.mIsExtremeVideoModeOn == 1 && this.mHasIrisCommandSet) {
            if (mExtremeVideoPackageList.contains(this.mCurrentFgPkg)) {
                sendEmptyMessage(46);
            }
            if (mExtremeVideoActivityList.contains(this.mCurExtremeVideoActivity)) {
                sendEmptyMessage(44);
            }
        }
        writeLocalLog("appToAppGestrue. fg no change mCurrentFgPkg:" + this.mCurrentFgPkg + ", restore last fps:" + this.mLastFps + ", mCurrentFps" + this.mCurrentFps);
    }

    private void appToHomeEnd(ForegroundInfo foregroundInfo) {
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "appToHomeEnd new fg is home");
        }
        onForegroundChanged(foregroundInfo);
        this.mIsDuringGesture = false;
    }

    private void appToHomeGestrueStart() {
        writeLocalLog("appToHomeGestrueStart is back to home, during gesture setFps: " + this.mUserFps);
        if (hasMessages(26)) {
            removeMessages(26);
        }
        if (this.IS_SUPPORTED_IRIS && this.mIsExtremeVideoModeOn == 1) {
            if (hasMessages(47)) {
                removeMessages(47);
            }
            if (this.mHasIrisCommandSet) {
                if (mExtremeVideoActivityList.contains(this.mCurExtremeVideoActivity)) {
                    IrisServiceUtils.getInstance(this.mContext).irisConfigureSet(5);
                }
                if (mExtremeVideoPackageList.contains(this.mCurrentFgPkg)) {
                    IrisServiceUtils.getInstance(this.mContext).irisConfigureSet(6);
                }
            }
        }
        this.mLastFps = this.mCurrentFps;
        setFpsAync(this.mCurrentFgPkg, this.mUserFps);
        this.mIsDuringGesture = true;
    }

    private void fillDefaultGameApp(String str, ArrayMap<String, Integer> arrayMap, ArraySet<String> arraySet) {
        if (str == null || str.isEmpty()) {
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        String[] strArrSplit = str.split(",");
        for (int i2 = 0; i2 < strArrSplit.length; i2++) {
            String str2 = strArrSplit[i2];
            if (str2 != null && !str2.isEmpty() && isInstalledPackage(strArrSplit[i2], arraySet) && !arrayMap.containsKey(strArrSplit[i2])) {
                this.mDefaultGameApp.add(strArrSplit[i2]);
            }
        }
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "fillDefaultGameApp mDefaultGameApp=" + this.mDefaultGameApp + " namesStr=" + str);
        }
    }

    public static DisplayFrameSetting getDFSInstance() {
        return sInstance;
    }

    private JSONObject getDefaultLocalConfig(String str) {
        JSONObject jSONObjectOptJSONObject;
        try {
            String strFromAssetFile = AssetUtils.getStrFromAssetFile(this.mContext, "smartfps/opt_dafault_config/fps_local.config");
            if (strFromAssetFile != null && !strFromAssetFile.isEmpty()) {
                String str2 = new String(Base64.decode(strFromAssetFile, 0));
                if (!TextUtils.isEmpty(str2) && (jSONObjectOptJSONObject = new JSONObject(str2).optJSONObject(str)) != null && jSONObjectOptJSONObject.length() != 0) {
                    return jSONObjectOptJSONObject;
                }
            }
            return null;
        } catch (JSONException e2) {
            Log.e(TAG, "getDefaultLocalConfig", e2);
            return null;
        } catch (Exception e3) {
            Log.e(TAG, "getDefaultLocalConfig", e3);
            return null;
        }
    }

    private int getPkgCookie(String str) {
        if (this.mExcludeApps.contains(str)) {
            return 247;
        }
        if (this.mIsWhitelistPolicy || this.mIsWhiteFpsList) {
            return 245;
        }
        if (this.mPrivGames.containsKey(str) || this.mDefaultGameApp.contains(str)) {
            return 254;
        }
        if (this.mDefineHighestFpsApps.contains(str)) {
            return 272;
        }
        if (this.mTopApps.containsKey(str)) {
            return 249;
        }
        if (this.mVideoApps.contains(str)) {
            return 248;
        }
        if (this.mSmartFpsApps.contains(str)) {
            return 251;
        }
        if (!this.mIsBlackList && this.mInputStatus != 1) {
            return 244;
        }
        writeLocalLog("getPkgCookie mIsBlackList mInputStatus=" + this.mInputStatus + "mIsBlackList=" + this.mIsBlackList);
        return 249;
    }

    public static ArrayMap<String, Integer> getPkgFpsConfig() {
        DisplayFrameSetting displayFrameSetting = sInstance;
        if (displayFrameSetting == null || !displayFrameSetting.mParseInited) {
            return new ArrayMap<>();
        }
        ArrayMap<String, Integer> arrayMap = new ArrayMap<>(1024);
        if (mIsSmart) {
            arrayMap.putAll((ArrayMap<? extends String, ? extends Integer>) displayFrameSetting.getTopAppsFps(null, true));
        } else {
            arrayMap.putAll((ArrayMap<? extends String, ? extends Integer>) displayFrameSetting.getTopAppsFps(null, false));
        }
        for (String str : SimpleSettings.Misc.getString(displayFrameSetting.mContext, "key_top_names", "").split(",")) {
            if (str != null && !str.isEmpty()) {
                arrayMap.put(str, 60);
            }
        }
        arrayMap.putAll((ArrayMap<? extends String, ? extends Integer>) displayFrameSetting.getPrivGamesFps(null));
        for (String str2 : displayFrameSetting.getTopVideoPkg().split(",")) {
            if (str2 != null && !str2.isEmpty()) {
                arrayMap.put(str2, 60);
            }
        }
        return arrayMap;
    }

    private ArrayMap<String, Integer> getPrivGamesFps(String str) {
        String[] strArrSplit;
        String str2;
        int i2;
        if (str == null) {
            str = SimpleSettings.Misc.getString(this.mContext, "key_priv_names", "");
        }
        ArrayMap<String, Integer> arrayMap = new ArrayMap<>(128);
        for (String str3 : str.split(",")) {
            if (str3 != null && !str3.isEmpty() && (strArrSplit = str3.split(":")) != null && strArrSplit.length == 2 && (str2 = strArrSplit[0]) != null && !str2.isEmpty() && (i2 = Utils.toInt(strArrSplit[1], -1)) > 0) {
                arrayMap.put(strArrSplit[0], Integer.valueOf(i2));
            }
        }
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "getPrivGamesFps ret=" + arrayMap);
        }
        return arrayMap;
    }

    private int getQsyncStatus(int i2, String str) {
        int iIntValue = this.mUserFps;
        if (this.mTopApps.containsKey(str)) {
            iIntValue = this.mTopApps.get(str).intValue();
        }
        int iMin = Math.min(iIntValue, this.mUserFps);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "getQsyncStatus = " + ((iMin << 16) | i2) + "fps =" + iMin);
        }
        return (iMin << 16) | i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSuitableFps(int[] iArr, int i2) {
        int i3 = this.mDefaultFps;
        if (iArr != null) {
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            for (int i7 : iArr) {
                if (i7 == i2) {
                    i4 = i2;
                    break;
                }
                if (i7 > i2) {
                    if (i5 == 0 || i5 > i7) {
                        i5 = i7;
                    }
                } else if (i6 < i7) {
                    i6 = i7;
                }
            }
            if (i4 != 0) {
                i3 = i4;
            } else if (i5 != 0) {
                i3 = i5;
            } else if (i6 != 0) {
                i3 = i6;
            }
        }
        if (this.DBG_DISPLAY) {
            Log.i(TAG, "getSuitableFps fps=" + i2 + " ret=" + i3);
        }
        return i3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int[] getSupportFps() {
        int[] iArr;
        Display.Mode[] supportedModes = ((DisplayManager) this.mContext.getSystemService("display")).getDisplay(0).getSupportedModes();
        if (supportedModes != null) {
            iArr = new int[supportedModes.length];
            for (int i2 = 0; i2 < supportedModes.length; i2++) {
                iArr[i2] = (int) supportedModes[i2].getRefreshRate();
            }
        } else {
            iArr = null;
        }
        if (this.DBG_DISPLAY) {
            Log.i(TAG, "getSupportFps ret=" + Arrays.toString(iArr));
        }
        return iArr;
    }

    private ArrayMap<String, Integer> getTopAppsFps(String str, boolean z2) {
        if (str == null) {
            Boolean bool = Boolean.TRUE;
            ArrayMap<String, Boolean> arrayMap = mCloudControlStatus;
            ArrayList<String> arrayList = b.b.X;
            int i2 = b.b.f30l;
            if (bool.equals(arrayMap.get(arrayList.get(i2))) && this.mCloudObserver.s(i2)) {
                JSONObject jSONObjectL = this.mCloudObserver.l(i2);
                if (jSONObjectL == null) {
                    writeLocalLog("new cloudFpsJson is null");
                    return new ArrayMap<>();
                }
                str = z2 ? jSONObjectL.optString(CLOUD_FPS_SMART_GROUP, "") : jSONObjectL.optString(CLOUD_FPS_DEFINE_GROUP, "");
            } else {
                str = z2 ? SimpleSettings.Misc.getString(this.mContext, CLOUD_FPS_SMART_GROUP, "") : SimpleSettings.Misc.getString(this.mContext, CLOUD_FPS_DEFINE_GROUP, "");
            }
        }
        ArrayMap<String, Integer> arrayMap2 = new ArrayMap<>(256);
        try {
            JSONObject jSONObject = new JSONObject(str);
            String strOptString = jSONObject.optString("list60", "");
            if (!strOptString.isEmpty()) {
                for (String str2 : strOptString.split(",")) {
                    arrayMap2.put(str2, 60);
                }
            }
            String strOptString2 = jSONObject.optString("list90", "");
            if (!strOptString2.isEmpty()) {
                for (String str3 : strOptString2.split(",")) {
                    arrayMap2.put(str3, 90);
                }
            }
            String strOptString3 = jSONObject.optString("list120", "");
            if (!strOptString3.isEmpty()) {
                for (String str4 : strOptString3.split(",")) {
                    arrayMap2.put(str4, 120);
                }
            }
        } catch (Exception e2) {
            writeLocalLog("getTopAppsFps e=" + e2);
        }
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "getTopAppsFps ret=" + arrayMap2);
        }
        return arrayMap2;
    }

    private String getTopVideoPkg() {
        b.a aVar = this.mCloudObserver;
        int i2 = b.b.f30l;
        JSONObject jSONObjectL = aVar.l(i2);
        if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
            String string = SimpleSettings.Misc.getString(this.mContext, CLOUD_FPS_TOP_VIDEO_PKG, "");
            writeLocalLog("parseTopVideoAppFromDb old cloud = " + string);
            return string;
        }
        String strOptString = jSONObjectL.optString(CLOUD_FPS_TOP_VIDEO_PKG, "");
        writeLocalLog("parseTopVideoAppFromDb new cloud = " + strOptString);
        return strOptString;
    }

    public static String getValidConfigFromData(JSONObject jSONObject, JSONObject jSONObject2, String str, int i2) {
        String strOptString;
        if (jSONObject2 == null) {
            return jSONObject != null ? jSONObject.optString(str, "") : "";
        }
        if (jSONObject != null) {
            strOptString = jSONObject.optString(str, DEFAULT_NULL);
            Log.d(TAG, "load local config " + str + ", configType = " + i2);
        } else {
            strOptString = "";
        }
        if (jSONObject != null && !DEFAULT_NULL.equals(strOptString)) {
            return strOptString;
        }
        String strOptString2 = jSONObject2.optString(str, "");
        Log.d(TAG, "load default local config " + str + ", configType = " + i2);
        return strOptString2;
    }

    public static String getValidLocalConfigPath(Context context) {
        String str = FPS_LOCAL_CONFIG_FILE_PATH;
        if (!AssetUtils.doesAssetExist(context, str, FPS_FILE_NAME)) {
            str = null;
        }
        if (FPS_INTL_LOCAL_CONFIG_FILE_PATH.endsWith("_intl") && ("IN".equalsIgnoreCase(SystemProperties.get("ro.miui.build.region")) || "ID".equalsIgnoreCase(SystemProperties.get("ro.miui.build.region")))) {
            StringBuilder sb = new StringBuilder();
            sb.append("smartfps/");
            String str2 = Build.DEVICE;
            sb.append(str2);
            sb.append("_id");
            String string = sb.toString();
            FPS_INTL_LOCAL_CONFIG_FILE_PATH = string;
            if (AssetUtils.doesAssetExist(context, string, FPS_FILE_NAME)) {
                Log.d(TAG, "switch intl local config to id local config " + FPS_INTL_LOCAL_CONFIG_FILE_PATH);
            } else {
                FPS_INTL_LOCAL_CONFIG_FILE_PATH = "smartfps/" + str2 + "_intl";
                Log.d(TAG, "without id local config, no need to switch intl local config");
            }
        }
        if (str == null) {
            if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
                return AssetUtils.doesAssetExist(context, FPS_INTL_LOCAL_CONFIG_FILE_PATH, FPS_FILE_NAME) ? FPS_INTL_LOCAL_CONFIG_FILE_PATH : null;
            }
            return str;
        }
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            return AssetUtils.doesAssetExist(context, FPS_INTL_LOCAL_CONFIG_FILE_PATH, FPS_FILE_NAME) ? FPS_INTL_LOCAL_CONFIG_FILE_PATH : FPS_OPT_DEFAULT_LOCAL_CONFIG_PATH;
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleIrisDisplayState() {
        Display display = this.mDisplay;
        if (display == null || !this.IS_SUPPORTED_IRIS || this.mIsExtremeVideoModeOn == 0) {
            return;
        }
        float refreshRate = display.getRefreshRate();
        if (this.mCurrentFps != Math.round(this.mRealRefreshRate) && refreshRate != this.mRealRefreshRate) {
            Log.d(TAG, "handleIrisDisplayState mCurrentFps=" + this.mCurrentFps + " actual refreshRate=" + refreshRate + " mRealRefreshRate=" + this.mRealRefreshRate);
            this.mRealRefreshRate = refreshRate;
            if (mExtremeVideoPackageList.contains(this.mCurrentFgInfo.mForegroundPackageName)) {
                sendEmptyMessage(47);
            }
        }
        int state = this.mDisplay.getState();
        if (this.DBG_DISPLAY && (state == 1 || state == 2)) {
            Log.d(TAG, "handleIrisDisplayState newState=" + state);
        }
        if (state != this.mPrevScreenState) {
            this.mPrevScreenState = state;
            if (state == 1 && this.mIsExtremeVideoModeOn == 1 && this.mHasIrisCommandSet && mExtremeVideoPackageList.contains(this.mCurrentFgInfo.mForegroundPackageName)) {
                onExtremeSceneRecovery();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTopGameList(Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra("isAppend", false);
        String stringExtra = intent.getStringExtra("gameList");
        Log.i(TAG, "onReceive isAppend=" + booleanExtra + " gameList:" + stringExtra);
        this.mLocalLog.log("onReceive TOP_GAME_LIST");
        if (stringExtra == null) {
            return;
        }
        sendMessage(booleanExtra ? obtainMessage(2, stringExtra) : obtainMessage(1, stringExtra));
    }

    private void homeToAppEnd(String str) {
        writeLocalLog("homeToAppEnd pkg=" + str);
        ForegroundInfo foregroundInfo = new ForegroundInfo();
        foregroundInfo.mForegroundPackageName = str;
        onForegroundChanged(foregroundInfo);
    }

    static synchronized DisplayFrameSetting init(Context context, Looper looper) {
        try {
            if (!isFeatureOn()) {
                return null;
            }
            if (sInstance == null) {
                sInstance = new DisplayFrameSetting(context, looper);
            }
            return sInstance;
        } catch (Throwable th) {
            throw th;
        }
    }

    public static boolean isFeatureOn() {
        int[] intArray;
        return (!Build.IS_DEBUGGABLE || SystemProperties.getBoolean("debug.power.fps.enable", true)) && (intArray = FeatureParser.getIntArray("fpsList")) != null && intArray.length > 0;
    }

    public static void notifyGesture(String str) {
        DisplayFrameSetting displayFrameSetting = sInstance;
        if (displayFrameSetting == null || !displayFrameSetting.mParseInited) {
            return;
        }
        sInstance.sendMessage(displayFrameSetting.obtainMessage(19, str));
    }

    private void notifyINChange(int i2) {
        if (this.mInputStatus == 0 && i2 == 1 && this.mInputApps.containsKey(this.mCurrentFgPkg)) {
            this.mInputStatus = 1;
            if (this.mIsSupportLowFps) {
                writeLocalLog("registerDisplayScroll input");
                registerDisplayScrollListener();
            }
            if (hasMessages(26)) {
                removeMessages(26);
            }
            sendEmptyMessageDelayed(26, 500L);
            return;
        }
        if (this.mInputStatus == 1) {
            this.mIsOnScroll = false;
            this.mInputStatus = 0;
            if (hasMessages(26)) {
                removeMessages(26);
            }
            onForegroundChanged(this.mCurrentFgInfo);
        }
    }

    private void notifyRecentTask(boolean z2) {
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "notifyRecentTask, isEnter:" + z2);
        }
        if (!z2) {
            recentTaskToHome();
            return;
        }
        ForegroundInfo foregroundInfo = this.mCurrentFgInfo;
        String str = foregroundInfo == null ? "" : foregroundInfo.mForegroundPackageName;
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "notifyRecentTask fgPkg:" + str);
        }
        appOrHomeToRecentTask();
    }

    private void notifySFCPUCluster(int i2) {
        IBinder service = ServiceManager.getService(LogType.LOG_TYPE_SF_NAME);
        if (service != null) {
            Parcel parcelObtain = Parcel.obtain();
            parcelObtain.writeInterfaceToken("android.ui.ISurfaceComposer");
            parcelObtain.writeInt(i2);
            try {
                try {
                    service.transact(31038, parcelObtain, null, 0);
                } catch (RemoteException | SecurityException e2) {
                    Slog.e(TAG, "Failed to notify cpu cluster to SurfaceFlinger", e2);
                }
            } finally {
                parcelObtain.recycle();
            }
        }
    }

    private void notifySFDfpsMode(int i2, int i3, String str) {
        IBinder service = ServiceManager.getService(LogType.LOG_TYPE_SF_NAME);
        if (service != null) {
            Parcel parcelObtain = Parcel.obtain();
            parcelObtain.writeInterfaceToken("android.ui.ISurfaceComposer");
            parcelObtain.writeInt(i2);
            parcelObtain.writeInt(i3);
            parcelObtain.writeString(str);
            try {
                try {
                    service.transact(1107, parcelObtain, null, 0);
                } catch (RemoteException | SecurityException e2) {
                    Slog.e(TAG, "Failed to notify dfps to SurfaceFlinger", e2);
                }
            } finally {
                parcelObtain.recycle();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v10, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v13, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r0v50 */
    /* JADX WARN: Type inference failed for: r0v56 */
    /* JADX WARN: Type inference failed for: r0v9 */
    /* JADX WARN: Type inference failed for: r1v42 */
    /* JADX WARN: Type inference failed for: r1v52 */
    /* JADX WARN: Type inference failed for: r1v8 */
    /* JADX WARN: Type inference failed for: r1v9, types: [boolean, int] */
    public void notifySFIChange() {
        ForegroundInfo foregroundInfo;
        if (!this.IS_ENABLE_DISPLAY_IDLE || (foregroundInfo = this.mCurrentFgInfo) == null) {
            return;
        }
        boolean zContains = this.mEbookidleApps.contains(foregroundInfo.mForegroundPackageName);
        if (zContains != this.mIsEbookIdleSwitch) {
            this.mIsEbookIdleSwitch = zContains;
            notifySFIfpsMode(257, zContains ? 1 : 0, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for ebook idle value=" + (zContains ? 1 : 0));
            }
        }
        boolean zContains2 = this.mTpidleApps.contains(this.mCurrentFgInfo.mForegroundPackageName);
        if (zContains2 != this.mIsTpIdleSwitch) {
            this.mIsTpIdleSwitch = zContains2;
            notifySFIfpsMode(258, zContains2 ? 1 : 0, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for tp idle value=" + (zContains2 ? 1 : 0));
            }
        }
        boolean zContains3 = this.mVideoIdleApps.contains(this.mCurrentFgInfo.mForegroundPackageName);
        if (zContains3 != this.mIsVideoIdleSwitch) {
            this.mIsVideoIdleSwitch = zContains3;
            notifySFIfpsMode(248, zContains3 ? 1 : 0, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for video idle value=" + (zContains3 ? 1 : 0));
            }
        }
        ?? r1 = 0;
        r1 = 0;
        ?? r0 = (this.mCheckPowerSaveCamera || ArrayUtils.contains(this.mBlacklistActivity_Camera, this.mCurActivityName) || ArrayUtils.contains(mLowFpsPkgList, this.mCurrentFgPkg)) ? 1 : 0;
        if (r0 != this.mIsCameraIdleSwitch) {
            this.mIsCameraIdleSwitch = r0;
            notifySFIfpsMode(252, r0, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for camera idle value=" + ((int) r0));
            }
        }
        ?? r2 = (this.mShortVideoSwitch && mShortVideoApps.contains(this.mCurrentFgInfo.mForegroundPackageName) && !ArrayUtils.contains(this.mWhitelistActivity, this.mCurActivityName)) ? 1 : 0;
        if (r2 != this.mIsShortVideoSwitch) {
            this.mIsShortVideoSwitch = r2;
            notifySFIfpsMode(259, r2, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for short video value=" + ((int) r2));
            }
        }
        boolean zContains4 = this.mExcludeApps.contains(this.mCurrentFgInfo.mForegroundPackageName);
        if (zContains4 != this.mIsExcludeSwitch) {
            this.mIsExcludeSwitch = zContains4;
            notifySFIfpsMode(247, zContains4 ? 1 : 0, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for exclude value=" + (zContains4 ? 1 : 0));
            }
        }
        boolean zContains5 = this.mPromotionApps.contains(this.mCurrentFgInfo.mForegroundPackageName);
        if (zContains5 != this.mIsPromotionSwitch) {
            this.mIsPromotionSwitch = zContains5;
            notifySFIfpsMode(260, zContains5 ? 1 : 0, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for promotion value=" + (zContains5 ? 1 : 0));
            }
        }
        boolean zContains6 = this.mDisplayFpsApps.contains(this.mCurrentFgInfo.mForegroundPackageName);
        if (zContains6 != this.mIsDisplayFpsSwitch) {
            this.mIsDisplayFpsSwitch = zContains6;
            notifySFIfpsMode(261, zContains6 ? 1 : 0, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for Display fps value=" + (zContains6 ? 1 : 0));
            }
        }
        if (this.mChargingSwitch && this.mLevelFullSwitch) {
            r1 = 1;
        }
        if (r1 != this.mIsChargingFullSwitch) {
            this.mIsChargingFullSwitch = r1;
            notifySFIfpsMode(263, r1, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for Charging full value=" + ((int) r1));
            }
        }
        boolean zContains7 = this.mFullScreenFpsApps.contains(this.mCurrentFgInfo.mForegroundPackageName);
        if (zContains7 != this.mIsFullScreenSwitch) {
            this.mIsFullScreenSwitch = zContains7;
            notifySFIfpsMode(266, zContains7 ? 1 : 0, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for full screen fps value=" + (zContains7 ? 1 : 0));
            }
        }
        boolean zContains8 = this.mVideoLowerFpsApps.contains(this.mCurrentFgInfo.mForegroundPackageName);
        if (zContains8 != this.mVideoLowerFpsSwitch) {
            this.mVideoLowerFpsSwitch = zContains8;
            notifySFIfpsMode(271, zContains8 ? 1 : 0, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for full screen lower fps value=" + (zContains8 ? 1 : 0));
            }
        }
        boolean zContains9 = this.mMapFpsApps.contains(this.mCurrentFgInfo.mForegroundPackageName);
        if (zContains9 != this.mIsMapFpsSwitch || this.mMapFpsApps.contains(this.mCurrentFgInfo.mLastForegroundPackageName)) {
            this.mIsMapFpsSwitch = zContains9;
            notifySFIfpsMode(265, getQsyncStatus(zContains9 ? 1 : 0, this.mCurrentFgPkg), this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for Map fps value=" + (zContains9 ? 1 : 0));
            }
        }
        boolean zContains10 = this.mAutoModeApps.contains(this.mCurrentFgInfo.mForegroundPackageName);
        if (zContains10 != this.mIsAutoModeSwitch) {
            this.mIsAutoModeSwitch = zContains10;
            int i2 = !zContains10 ? 1 : 0;
            notifySFIfpsMode(267, i2, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for auto mode value=" + i2);
            }
        }
        boolean zContains11 = this.mVrrApps.contains(this.mCurrentFgInfo.mForegroundPackageName);
        if (zContains11 != this.mIsVrrSwitch) {
            this.mIsVrrSwitch = zContains11;
            notifySFIfpsMode(269, zContains11 ? 1 : 0, this.mCurrentFgPkg);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for vrr value=" + (zContains11 ? 1 : 0));
            }
        }
    }

    private void notifySFIfpsMode(int i2, int i3, String str) {
        IBinder service = ServiceManager.getService(LogType.LOG_TYPE_SF_NAME);
        if (service != null) {
            Parcel parcelObtain = Parcel.obtain();
            parcelObtain.writeInterfaceToken("android.ui.ISurfaceComposer");
            parcelObtain.writeInt(i2);
            parcelObtain.writeInt(i3);
            parcelObtain.writeString(str);
            try {
                try {
                    service.transact(31107, parcelObtain, null, 0);
                } catch (RemoteException | SecurityException e2) {
                    Slog.e(TAG, "Failed to notify idle to SurfaceFlinger", e2);
                }
            } finally {
                parcelObtain.recycle();
            }
        }
    }

    private void notifySFNoteMode(int i2, int i3) {
        IBinder service = ServiceManager.getService(LogType.LOG_TYPE_SF_NAME);
        if (service != null) {
            Parcel parcelObtain = Parcel.obtain();
            parcelObtain.writeInterfaceToken("android.ui.ISurfaceComposer");
            parcelObtain.writeInt(i2);
            parcelObtain.writeInt(i3);
            try {
                try {
                    service.transact(31118, parcelObtain, null, 0);
                } catch (RemoteException | SecurityException e2) {
                    Slog.e(TAG, "Failed to notify notes to SurfaceFlinger", e2);
                }
            } finally {
                parcelObtain.recycle();
            }
        }
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject, JSONObject jSONObject2, int i2) {
        boolean z2;
        if (!isFeatureOn()) {
            Log.d(TAG, "onCloudFunctionUpdated shoud return");
            return;
        }
        DisplayFrameSetting displayFrameSetting = sInstance;
        if (i2 != 0 || getValidLocalConfigPath(context) == null) {
            if (i2 == 0) {
                mConfigType = i2;
            }
            z2 = false;
        } else {
            Log.d(TAG, "ignore onCloudFunctionUpdated fps related for configType = " + i2);
            z2 = true;
        }
        Log.d(TAG, "onCloudFunctionUpdated run configType = " + i2 + ", haveLoadNewConfig = " + z2);
        if (!z2) {
            String validConfigFromData = getValidConfigFromData(jSONObject, jSONObject2, CUSTOM_SWITCH, i2);
            if (!validConfigFromData.equals(SimpleSettings.Misc.getString(context, CUSTOM_SWITCH, ""))) {
                if (i2 == 0) {
                    Boolean bool = Boolean.FALSE;
                    if (bool.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f25g), bool))) {
                        if (!validConfigFromData.isEmpty()) {
                            SimpleSettings.Misc.putString(context, CUSTOM_SWITCH, validConfigFromData);
                        }
                        if (displayFrameSetting != null) {
                            Log.d(TAG, "newCustomModeSwitch = " + validConfigFromData);
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(37, validConfigFromData));
                        }
                    }
                } else if (!validConfigFromData.isEmpty()) {
                    SimpleSettings.Misc.putString(context, CUSTOM_SWITCH, validConfigFromData);
                }
            }
            String validConfigFromData2 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_FOSS_GROUP, i2);
            if (!validConfigFromData2.equals(SimpleSettings.Misc.getString(context, CLOUD_FOSS_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_FOSS_GROUP, validConfigFromData2);
                } else {
                    Boolean bool2 = Boolean.FALSE;
                    if (bool2.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f26h), bool2))) {
                        try {
                            SimpleSettings.Misc.putString(context, CLOUD_FOSS_GROUP, validConfigFromData2);
                            JSONObject jSONObject3 = new JSONObject(validConfigFromData2);
                            if (displayFrameSetting != null) {
                                displayFrameSetting.mIsFossCloudSwitch = jSONObject3.optBoolean("fucSwitch", false);
                                displayFrameSetting.mIsDitherCloudSwitch = jSONObject3.optBoolean("ditherSwitch", false);
                                Log.d(TAG, "fossFucSwitch=" + displayFrameSetting.mIsFossCloudSwitch + "ditherSwitch=" + displayFrameSetting.mIsDitherCloudSwitch);
                            }
                        } catch (JSONException e2) {
                            if (displayFrameSetting != null) {
                                displayFrameSetting.writeLocalLog("parsemIsFossCloudSwitch e=" + e2);
                            }
                        }
                    }
                }
            }
            String validConfigFromData3 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_NOTE_PKG, i2);
            if (!validConfigFromData3.equals(SimpleSettings.Misc.getString(context, CLOUD_NOTE_PKG, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_NOTE_PKG, validConfigFromData3);
                } else {
                    Boolean bool3 = Boolean.FALSE;
                    if (bool3.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f27i), bool3))) {
                        try {
                            SimpleSettings.Misc.putString(context, CLOUD_NOTE_PKG, validConfigFromData3);
                            JSONObject jSONObject4 = new JSONObject(validConfigFromData3);
                            if (displayFrameSetting != null) {
                                displayFrameSetting.mIsNoteCloudSwitch = jSONObject4.optBoolean("noteSwitch", false);
                                Log.d(TAG, "noteSwitch=" + displayFrameSetting.mIsNoteCloudSwitch);
                            }
                        } catch (JSONException e3) {
                            if (displayFrameSetting != null) {
                                displayFrameSetting.writeLocalLog("parsemIsNoteCloudSwitch e=" + e3);
                            }
                        }
                    }
                }
            }
            String validConfigFromData4 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_FPS_DEFINE_GROUP, i2);
            if (!validConfigFromData4.equals(SimpleSettings.Misc.getString(context, CLOUD_FPS_DEFINE_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_FPS_DEFINE_GROUP, validConfigFromData4);
                } else {
                    Boolean bool4 = Boolean.FALSE;
                    if (bool4.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f30l), bool4))) {
                        SimpleSettings.Misc.putString(context, CLOUD_FPS_DEFINE_GROUP, validConfigFromData4);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(4, validConfigFromData4));
                        }
                    }
                }
            }
            String validConfigFromData5 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_FPS_SMART_GROUP, i2);
            if (!validConfigFromData5.equals(SimpleSettings.Misc.getString(context, CLOUD_FPS_SMART_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_FPS_SMART_GROUP, validConfigFromData5);
                } else {
                    Boolean bool5 = Boolean.FALSE;
                    if (bool5.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f30l), bool5))) {
                        SimpleSettings.Misc.putString(context, CLOUD_FPS_SMART_GROUP, validConfigFromData5);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(25, validConfigFromData5));
                        }
                    }
                }
            }
            String validConfigFromData6 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_FPS_TOP_VIDEO_PKG, i2);
            if (!validConfigFromData6.equals(SimpleSettings.Misc.getString(context, CLOUD_FPS_TOP_VIDEO_PKG, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_FPS_TOP_VIDEO_PKG, validConfigFromData6);
                } else {
                    Boolean bool6 = Boolean.FALSE;
                    if (bool6.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f30l), bool6))) {
                        SimpleSettings.Misc.putString(context, CLOUD_FPS_TOP_VIDEO_PKG, validConfigFromData6);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(5, validConfigFromData6));
                        }
                    }
                }
            }
            String validConfigFromData7 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_FPS_TOP_VIDEO_IDLE_PKG, i2);
            if (!validConfigFromData7.equals(SimpleSettings.Misc.getString(context, CLOUD_FPS_TOP_VIDEO_IDLE_PKG, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_FPS_TOP_VIDEO_IDLE_PKG, validConfigFromData7);
                } else {
                    Boolean bool7 = Boolean.FALSE;
                    if (bool7.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f29k), bool7))) {
                        SimpleSettings.Misc.putString(context, CLOUD_FPS_TOP_VIDEO_IDLE_PKG, validConfigFromData7);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(32, validConfigFromData7));
                        }
                    }
                }
            }
            String validConfigFromData8 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_FPS_EXCLUDE_PKG, i2);
            if (!validConfigFromData8.equals(SimpleSettings.Misc.getString(context, CLOUD_FPS_EXCLUDE_PKG, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_FPS_EXCLUDE_PKG, validConfigFromData8);
                } else {
                    Boolean bool8 = Boolean.FALSE;
                    if (bool8.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f26h), bool8))) {
                        SimpleSettings.Misc.putString(context, CLOUD_FPS_EXCLUDE_PKG, validConfigFromData8);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(9, validConfigFromData8));
                        }
                    }
                }
            }
            String validConfigFromData9 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_EBOOK_IDLE_GROUP, i2);
            if (!validConfigFromData9.equals(SimpleSettings.Misc.getString(context, CLOUD_EBOOK_IDLE_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_EBOOK_IDLE_GROUP, validConfigFromData9);
                } else {
                    Boolean bool9 = Boolean.FALSE;
                    if (bool9.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f29k), bool9))) {
                        SimpleSettings.Misc.putString(context, CLOUD_EBOOK_IDLE_GROUP, validConfigFromData9);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(23, validConfigFromData9));
                        }
                    }
                }
            }
            String validConfigFromData10 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_TP_IDLE_GROUP, i2);
            if (!validConfigFromData10.equals(SimpleSettings.Misc.getString(context, CLOUD_TP_IDLE_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_TP_IDLE_GROUP, validConfigFromData10);
                } else {
                    Boolean bool10 = Boolean.FALSE;
                    if (bool10.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f26h), bool10))) {
                        SimpleSettings.Misc.putString(context, CLOUD_TP_IDLE_GROUP, validConfigFromData10);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(24, validConfigFromData10));
                        }
                    }
                }
            }
            String validConfigFromData11 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_INPUT_AUDIO_GROUP, i2);
            if (!validConfigFromData11.equals(SimpleSettings.Misc.getString(context, CLOUD_INPUT_AUDIO_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_INPUT_AUDIO_GROUP, validConfigFromData11);
                } else {
                    Boolean bool11 = Boolean.FALSE;
                    if (bool11.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f30l), bool11))) {
                        SimpleSettings.Misc.putString(context, CLOUD_INPUT_AUDIO_GROUP, validConfigFromData11);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(27, validConfigFromData11));
                        }
                    }
                }
            }
            String validConfigFromData12 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_LOW_FPS_GROUP, i2);
            if (!validConfigFromData12.equals(SimpleSettings.Misc.getString(context, CLOUD_LOW_FPS_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_LOW_FPS_GROUP, validConfigFromData12);
                } else {
                    Boolean bool12 = Boolean.FALSE;
                    if (bool12.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f30l), bool12))) {
                        SimpleSettings.Misc.putString(context, CLOUD_LOW_FPS_GROUP, validConfigFromData12);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(28, validConfigFromData12));
                        }
                    }
                }
            }
            String validConfigFromData13 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_FPS_TOP_SHORT_VIDEO_PKG, i2);
            if (!validConfigFromData13.equals(SimpleSettings.Misc.getString(context, CLOUD_FPS_TOP_SHORT_VIDEO_PKG, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_FPS_TOP_SHORT_VIDEO_PKG, validConfigFromData13);
                } else {
                    Boolean bool13 = Boolean.FALSE;
                    if (bool13.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f29k), bool13))) {
                        SimpleSettings.Misc.putString(context, CLOUD_FPS_TOP_SHORT_VIDEO_PKG, validConfigFromData13);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(30, validConfigFromData13));
                        }
                    }
                }
            }
            String validConfigFromData14 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_PROMOTION_GROUP, i2);
            if (!validConfigFromData14.equals(SimpleSettings.Misc.getString(context, CLOUD_PROMOTION_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_PROMOTION_GROUP, validConfigFromData14);
                } else {
                    Boolean bool14 = Boolean.FALSE;
                    if (bool14.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f29k), bool14))) {
                        SimpleSettings.Misc.putString(context, CLOUD_PROMOTION_GROUP, validConfigFromData14);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(34, validConfigFromData14));
                        }
                    }
                }
            }
            String validConfigFromData15 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_DISPLAY_FPS_GROUP, i2);
            if (!validConfigFromData15.equals(SimpleSettings.Misc.getString(context, CLOUD_DISPLAY_FPS_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_DISPLAY_FPS_GROUP, validConfigFromData15);
                } else {
                    Boolean bool15 = Boolean.FALSE;
                    if (bool15.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f28j), bool15))) {
                        SimpleSettings.Misc.putString(context, CLOUD_DISPLAY_FPS_GROUP, validConfigFromData15);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(35, validConfigFromData15));
                        }
                    }
                }
            }
            String validConfigFromData16 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_FULL_SCREEN_GROUP, i2);
            if (!validConfigFromData16.equals(SimpleSettings.Misc.getString(context, CLOUD_FULL_SCREEN_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_FULL_SCREEN_GROUP, validConfigFromData16);
                } else {
                    Boolean bool16 = Boolean.FALSE;
                    if (bool16.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f29k), bool16))) {
                        SimpleSettings.Misc.putString(context, CLOUD_FULL_SCREEN_GROUP, validConfigFromData16);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(36, validConfigFromData16));
                        }
                    }
                }
            }
            String validConfigFromData17 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_MAP_FPS_GROUP, i2);
            if (!validConfigFromData17.equals(SimpleSettings.Misc.getString(context, CLOUD_MAP_FPS_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_MAP_FPS_GROUP, validConfigFromData17);
                } else {
                    Boolean bool17 = Boolean.FALSE;
                    if (bool17.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f29k), bool17))) {
                        SimpleSettings.Misc.putString(context, CLOUD_MAP_FPS_GROUP, validConfigFromData17);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(38, validConfigFromData17));
                        }
                    }
                }
            }
            String validConfigFromData18 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_AUTOMODE_VRR_GROUP, i2);
            if (!validConfigFromData18.equals(SimpleSettings.Misc.getString(context, CLOUD_AUTOMODE_VRR_GROUP, ""))) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_AUTOMODE_VRR_GROUP, validConfigFromData18);
                } else {
                    Boolean bool18 = Boolean.FALSE;
                    if (bool18.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f29k), bool18))) {
                        SimpleSettings.Misc.putString(context, CLOUD_AUTOMODE_VRR_GROUP, validConfigFromData18);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(39, validConfigFromData18));
                        }
                    }
                }
            }
            String validConfigFromData19 = getValidConfigFromData(jSONObject, jSONObject2, CLOUD_HIGHEST_FPS_GROUP, i2);
            String string = SimpleSettings.Misc.getString(context, CLOUD_HIGHEST_FPS_GROUP, "");
            Log.d(TAG, "onCloudFunctionUpdated newHighestFpsCloud=" + validConfigFromData19 + " oldHighestFpsCloud=" + string);
            if (!validConfigFromData19.equals(string)) {
                if (i2 != 0) {
                    SimpleSettings.Misc.putString(context, CLOUD_HIGHEST_FPS_GROUP, validConfigFromData19);
                } else {
                    Boolean bool19 = Boolean.FALSE;
                    if (bool19.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f30l), bool19))) {
                        SimpleSettings.Misc.putString(context, CLOUD_HIGHEST_FPS_GROUP, validConfigFromData19);
                        if (displayFrameSetting != null) {
                            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(50, validConfigFromData19));
                        }
                    }
                }
            }
        }
        if (jSONObject != null) {
            String strOptString = jSONObject.optString(CLOUD_EYE_MODE_GROUP, "");
            if (!strOptString.equals(SimpleSettings.Misc.getString(context, CLOUD_EYE_MODE_GROUP, ""))) {
                Boolean bool20 = Boolean.FALSE;
                if (bool20.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f33o), bool20))) {
                    SimpleSettings.Misc.putString(context, CLOUD_EYE_MODE_GROUP, strOptString);
                    if (displayFrameSetting != null) {
                        displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(20, strOptString));
                    }
                }
            }
            String strOptString2 = jSONObject.optString(CLOUD_SMART_BACKLIGHT_GROUP, "");
            if (!strOptString2.equals(SimpleSettings.Misc.getString(context, CLOUD_SMART_BACKLIGHT_GROUP, ""))) {
                Boolean bool21 = Boolean.FALSE;
                if (bool21.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f32n), bool21))) {
                    SimpleSettings.Misc.putString(context, CLOUD_SMART_BACKLIGHT_GROUP, strOptString2);
                    if (displayFrameSetting != null) {
                        displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(21, strOptString2));
                    }
                }
            }
            String strOptString3 = jSONObject.optString(CLOUD_VIOLENT_MODE_SWITCH, "");
            if (strOptString3.equals(SimpleSettings.Misc.getString(context, CLOUD_VIOLENT_MODE_SWITCH, ""))) {
                return;
            }
            Boolean bool22 = Boolean.FALSE;
            if (bool22.equals(mCloudControlStatus.getOrDefault(b.b.X.get(b.b.f34p), bool22))) {
                SimpleSettings.Misc.putString(context, CLOUD_VIOLENT_MODE_SWITCH, strOptString3);
                if (displayFrameSetting != null) {
                    displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(40, strOptString3));
                }
            }
        }
    }

    private void onExtremeSceneChanged() {
        writeLocalLog("onExtremeSceneChanged");
        notifySFIfpsMode(264, 1, this.mCurrentFgPkg);
        IrisServiceUtils.getInstance(this.mContext).irisConfigureSet(2);
        if (ArrayUtils.contains(this.mDuelMemcPackage, this.mCurrentFgPkg)) {
            sendEmptyMessage(45);
        }
        sendEmptyMessage(46);
        sendEmptyMessage(44);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onExtremeSceneRecovery() {
        writeLocalLog("onExtremeSceneRecovery");
        this.mHasIrisCommandSet = false;
        IrisServiceUtils.getInstance(this.mContext).irisConfigureSet(5);
        IrisServiceUtils.getInstance(this.mContext).irisConfigureSet(6);
        if (this.mIsIrisPQOn) {
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "Notify Iris PQ Off");
            }
            this.mIsIrisPQOn = false;
            IrisServiceUtils.getInstance(this.mContext).irisConfigureSet(4);
        }
        IrisServiceUtils.getInstance(this.mContext).irisConfigureSet(1);
        notifySFIfpsMode(264, 0, this.mCurExtremeVideoPackage);
        Settings.System.putInt(this.mContext.getContentResolver(), VIDEO_IRIS_STATUS, 0);
    }

    private void onHighFpsSendNotify() {
        if (this.mEnterHighFpsValue < 120 || !this.mCurrentFgPkg.equals("com.youku.phone") || !ArrayUtils.contains(this.mHighFpsListActivity, this.mCurActivityName) || this.mUserFps < 120 || this.mPowerMode == 3) {
            return;
        }
        if ((this.mIsCustom && this.mCustomApps.getOrDefault("com.youku.phone", 0).intValue() == 60) || Settings.System.getInt(this.mContext.getContentResolver(), "thermal_limit_refresh_rate", 0) == 60) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction("com.miui.powerkeeper.action.HIGH_FPS_NOTIFY");
        intent.setPackage("com.miui.securitycenter");
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "com.miui.powerkeeper.permission.POWER_NOTIFY");
        writeLocalLog("onForegroundChanged EnterHighFps sendNotify");
    }

    /* JADX WARN: Code duplicated, block: B:112:0x034f  */
    /* JADX WARN: Code duplicated, block: B:116:0x035c  */
    /* JADX WARN: Code duplicated, block: B:119:0x0379  */
    /* JADX WARN: Code duplicated, block: B:122:0x0396  */
    /* JADX WARN: Code duplicated, block: B:125:0x03b3  */
    /* JADX WARN: Code duplicated, block: B:128:0x03d2  */
    /* JADX WARN: Code duplicated, block: B:131:0x03f1  */
    /* JADX WARN: Code duplicated, block: B:134:0x040e  */
    /* JADX WARN: Code duplicated, block: B:137:0x042b  */
    /* JADX WARN: Code duplicated, block: B:140:0x044a  */
    /* JADX WARN: Code duplicated, block: B:143:0x0469  */
    /* JADX WARN: Code duplicated, block: B:146:0x0488  */
    /* JADX WARN: Code duplicated, block: B:149:0x04a7  */
    /* JADX WARN: Code duplicated, block: B:152:0x04c6  */
    /* JADX WARN: Code duplicated, block: B:155:0x04e5  */
    /* JADX WARN: Code duplicated, block: B:158:0x0504  */
    /* JADX WARN: Code duplicated, block: B:161:0x0523  */
    /* JADX WARN: Code duplicated, block: B:164:0x0542  */
    /* JADX WARN: Code duplicated, block: B:167:0x0561  */
    /* JADX WARN: Code duplicated, block: B:170:0x0580  */
    /* JADX WARN: Code duplicated, block: B:173:0x059f  */
    /* JADX WARN: Code duplicated, block: B:176:0x05be  */
    /* JADX WARN: Code duplicated, block: B:205:0x0216 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:210:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:214:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:34:0x0167  */
    /* JADX WARN: Code duplicated, block: B:39:0x0176  */
    /* JADX WARN: Code duplicated, block: B:43:0x019e  */
    /* JADX WARN: Code duplicated, block: B:45:0x01b3  */
    /* JADX WARN: Code duplicated, block: B:47:0x01ca  */
    /* JADX WARN: Code duplicated, block: B:49:0x01d8  */
    /* JADX WARN: Code duplicated, block: B:51:0x01e1  */
    /* JADX WARN: Code duplicated, block: B:82:0x027f  */
    /* JADX WARN: Code duplicated, block: B:85:0x02a1  */
    /* JADX WARN: Instruction removed from duplicated block: B:116:0x035c, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:119:0x0379, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:122:0x0396, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:125:0x03b3, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:128:0x03d2, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:131:0x03f1, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:134:0x040e, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:137:0x042b, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:140:0x044a, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:143:0x0469, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:146:0x0488, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:149:0x04a7, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:152:0x04c6, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:155:0x04e5, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:158:0x0504, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:161:0x0523, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:164:0x0542, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:167:0x0561, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:170:0x0580, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:173:0x059f, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:176:0x05be, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:39:0x0176, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:45:0x01b3, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:51:0x01e1, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:82:0x027f, please report this as an issue */
    private void onPkgExistentChanged(EventsAggregator.EventResult eventResult) throws Throwable {
        Cursor cursorQuery;
        String str;
        int[] supportFps;
        Integer num;
        Cursor cursorQuery2;
        Integer num2;
        Integer num3;
        int suitableFps;
        int suitableFps2;
        Cursor cursor;
        Uri uri;
        ContentValues contentValues;
        UserHandle userHandle;
        Cursor cursor2;
        Cursor cursorQuery3;
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged eventResult=" + eventResult);
        }
        int i2 = eventResult.mFlag;
        if (i2 == 1) {
            PowerKeeperPackageManager.getInstalledThirdPartApps();
            String str2 = (String) eventResult.mObj;
            DisplayFrameSetting displayFrameSetting = sInstance;
            Objects.requireNonNull(displayFrameSetting);
            displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(9, null));
            DisplayFrameSetting displayFrameSetting2 = sInstance;
            Objects.requireNonNull(displayFrameSetting2);
            displayFrameSetting2.sendMessage(displayFrameSetting2.obtainMessage(32, null));
            DisplayFrameSetting displayFrameSetting3 = sInstance;
            Objects.requireNonNull(displayFrameSetting3);
            displayFrameSetting3.sendMessage(displayFrameSetting3.obtainMessage(23, null));
            DisplayFrameSetting displayFrameSetting4 = sInstance;
            Objects.requireNonNull(displayFrameSetting4);
            displayFrameSetting4.sendMessage(displayFrameSetting4.obtainMessage(24, null));
            DisplayFrameSetting displayFrameSetting5 = sInstance;
            Objects.requireNonNull(displayFrameSetting5);
            displayFrameSetting5.sendMessage(displayFrameSetting5.obtainMessage(34, null));
            DisplayFrameSetting displayFrameSetting6 = sInstance;
            Objects.requireNonNull(displayFrameSetting6);
            displayFrameSetting6.sendMessage(displayFrameSetting6.obtainMessage(35, null));
            DisplayFrameSetting displayFrameSetting7 = sInstance;
            Objects.requireNonNull(displayFrameSetting7);
            displayFrameSetting7.sendMessage(displayFrameSetting7.obtainMessage(38, null));
            DisplayFrameSetting displayFrameSetting8 = sInstance;
            Objects.requireNonNull(displayFrameSetting8);
            displayFrameSetting8.sendMessage(displayFrameSetting8.obtainMessage(36, null));
            DisplayFrameSetting displayFrameSetting9 = sInstance;
            Objects.requireNonNull(displayFrameSetting9);
            displayFrameSetting9.sendMessage(displayFrameSetting9.obtainMessage(39, null));
            DisplayFrameSetting displayFrameSetting10 = sInstance;
            Objects.requireNonNull(displayFrameSetting10);
            displayFrameSetting10.sendMessage(displayFrameSetting10.obtainMessage(27, null));
            DisplayFrameSetting displayFrameSetting11 = sInstance;
            Objects.requireNonNull(displayFrameSetting11);
            displayFrameSetting11.sendMessage(displayFrameSetting11.obtainMessage(28, null));
            DisplayFrameSetting displayFrameSetting12 = sInstance;
            Objects.requireNonNull(displayFrameSetting12);
            displayFrameSetting12.sendMessage(displayFrameSetting12.obtainMessage(30, null));
            DisplayFrameSetting displayFrameSetting13 = sInstance;
            Objects.requireNonNull(displayFrameSetting13);
            displayFrameSetting13.sendMessage(displayFrameSetting13.obtainMessage(50, null));
            if (Utils.isContainedWithSeparator(getTopVideoPkg(), str2, ',')) {
                if ("true".equals(mIsCustomFpsSwitch)) {
                    try {
                        Uri uri2 = HighRefreshRateConfigure.CONTENT_URI;
                        ContentValues contentValues2 = new ContentValues();
                        Context context = this.mContext;
                        UserHandle userHandle2 = UserHandle.OWNER;
                        cursorQuery3 = context.getContentResolverForUser(userHandle2).query(uri2, null, "package_name = ?", new String[]{str2}, null);
                        try {
                            try {
                                if (cursorQuery3.moveToNext()) {
                                    str = "onPkgExistentChanged mTopSmartApps=";
                                } else {
                                    contentValues2.put("package_name", str2);
                                    str = "onPkgExistentChanged mTopSmartApps=";
                                    try {
                                        this.mContext.getContentResolverForUser(userHandle2).insert(uri2, contentValues2);
                                        this.mCustomApps.put(str2, 60);
                                    } catch (Exception e2) {
                                        e = e2;
                                        Log.e(TAG, "onPkgExistentChanged video", e);
                                        if (cursorQuery3 != null) {
                                        }
                                        this.mVideoApps.add(str2);
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "onPkgExistentChanged mVideoApps=" + this.mVideoApps);
                                        }
                                        supportFps = getSupportFps();
                                        num = getPrivGamesFps(null).get(str2);
                                        if (num != null) {
                                            this.mPrivGames.put(str2, Integer.valueOf(getSuitableFps(supportFps, num.intValue())));
                                            if (this.DBG_DISPLAY) {
                                                Log.d(TAG, "onPkgExistentChanged mPrivGames=" + this.mPrivGames);
                                            }
                                        } else if (Utils.isContainedWithSeparator(SimpleSettings.Misc.getString(this.mContext, "key_top_names", ""), str2, ',')) {
                                            this.mDefaultGameApp.add(str2);
                                            if (this.DBG_DISPLAY) {
                                                Log.d(TAG, "onPkgExistentChanged mDefaultGameApp=" + this.mDefaultGameApp);
                                            }
                                        }
                                        cursorQuery2 = null;
                                        num2 = getTopAppsFps(null, false).get(str2);
                                        if (num2 != null) {
                                            try {
                                                if ("true".equals(mIsCustomFpsSwitch)) {
                                                    try {
                                                        uri = HighRefreshRateConfigure.CONTENT_URI;
                                                        contentValues = new ContentValues();
                                                        Context context2 = this.mContext;
                                                        userHandle = UserHandle.OWNER;
                                                        cursorQuery2 = context2.getContentResolverForUser(userHandle).query(uri, null, "package_name = ?", new String[]{str2}, null);
                                                        try {
                                                            if (!cursorQuery2.moveToNext()) {
                                                                contentValues.put("package_name", str2);
                                                                this.mContext.getContentResolverForUser(userHandle).insert(uri, contentValues);
                                                                this.mCustomApps.put(str2, 60);
                                                            }
                                                        } catch (Exception e3) {
                                                            e = e3;
                                                            Log.e(TAG, "onPkgExistentChanged TopDefineApps", e);
                                                            if (cursorQuery2 != null) {
                                                            }
                                                            this.mTopDefineApps.put(str2, Integer.valueOf(suitableFps2));
                                                            if (this.DBG_DISPLAY) {
                                                                Log.d(TAG, "onPkgExistentChanged mTopDefineApps=" + this.mTopDefineApps);
                                                            }
                                                            num3 = getTopAppsFps(null, true).get(str2);
                                                            if (num3 != null) {
                                                                return;
                                                            } else {
                                                                return;
                                                            }
                                                        }
                                                    } catch (Exception e4) {
                                                        e = e4;
                                                        cursorQuery2 = null;
                                                    } catch (Throwable th) {
                                                        th = th;
                                                        cursor = null;
                                                        if (cursor != null) {
                                                            cursor.close();
                                                        }
                                                        throw th;
                                                    }
                                                    cursorQuery2.close();
                                                }
                                                this.mTopDefineApps.put(str2, Integer.valueOf(suitableFps2));
                                                if (this.DBG_DISPLAY) {
                                                    Log.d(TAG, "onPkgExistentChanged mTopDefineApps=" + this.mTopDefineApps);
                                                }
                                            } catch (Throwable th2) {
                                                th = th2;
                                                cursor = cursorQuery2;
                                            }
                                        }
                                        num3 = getTopAppsFps(null, true).get(str2);
                                        if (num3 != null) {
                                            return;
                                        } else {
                                            return;
                                        }
                                    }
                                }
                            } catch (Exception e5) {
                                e = e5;
                                str = "onPkgExistentChanged mTopSmartApps=";
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            cursor2 = cursorQuery3;
                            if (cursor2 != null) {
                                cursor2.close();
                            }
                            throw th;
                        }
                    } catch (Exception e6) {
                        e = e6;
                        str = "onPkgExistentChanged mTopSmartApps=";
                        cursorQuery3 = null;
                    } catch (Throwable th4) {
                        th = th4;
                        cursor2 = null;
                        if (cursor2 != null) {
                            cursor2.close();
                        }
                        throw th;
                    }
                    cursorQuery3.close();
                } else {
                    str = "onPkgExistentChanged mTopSmartApps=";
                }
                this.mVideoApps.add(str2);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "onPkgExistentChanged mVideoApps=" + this.mVideoApps);
                }
            } else {
                str = "onPkgExistentChanged mTopSmartApps=";
            }
            supportFps = getSupportFps();
            num = getPrivGamesFps(null).get(str2);
            if (num != null) {
                this.mPrivGames.put(str2, Integer.valueOf(getSuitableFps(supportFps, num.intValue())));
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "onPkgExistentChanged mPrivGames=" + this.mPrivGames);
                }
            } else if (Utils.isContainedWithSeparator(SimpleSettings.Misc.getString(this.mContext, "key_top_names", ""), str2, ',')) {
                this.mDefaultGameApp.add(str2);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "onPkgExistentChanged mDefaultGameApp=" + this.mDefaultGameApp);
                }
            }
            cursorQuery2 = null;
            num2 = getTopAppsFps(null, false).get(str2);
            if (num2 != null && (suitableFps2 = getSuitableFps(supportFps, num2.intValue())) != this.mDefaultFps) {
                if ("true".equals(mIsCustomFpsSwitch)) {
                    uri = HighRefreshRateConfigure.CONTENT_URI;
                    contentValues = new ContentValues();
                    Context context3 = this.mContext;
                    userHandle = UserHandle.OWNER;
                    cursorQuery2 = context3.getContentResolverForUser(userHandle).query(uri, null, "package_name = ?", new String[]{str2}, null);
                    if (!cursorQuery2.moveToNext() && !str2.equals("com.android.camera")) {
                        contentValues.put("package_name", str2);
                        this.mContext.getContentResolverForUser(userHandle).insert(uri, contentValues);
                        this.mCustomApps.put(str2, 60);
                    }
                    cursorQuery2.close();
                }
                this.mTopDefineApps.put(str2, Integer.valueOf(suitableFps2));
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "onPkgExistentChanged mTopDefineApps=" + this.mTopDefineApps);
                }
            }
            num3 = getTopAppsFps(null, true).get(str2);
            if (num3 != null || (suitableFps = getSuitableFps(supportFps, num3.intValue())) == this.mDefaultFps) {
                return;
            }
            this.mTopSmartApps.put(str2, Integer.valueOf(suitableFps));
            if (this.DBG_DISPLAY) {
                Log.d(TAG, str + this.mTopSmartApps);
                return;
            }
            return;
        }
        if (i2 != 0) {
            if (i2 == 2) {
                ArraySet<String> arraySet = (ArraySet) eventResult.mObj;
                ArrayMap<String, Integer> privGamesFps = getPrivGamesFps(null);
                fillDefaultGameApp(SimpleSettings.Misc.getString(this.mContext, "key_top_names", ""), privGamesFps, arraySet);
                int[] supportFps2 = getSupportFps();
                for (int i3 = 0; i3 < privGamesFps.size(); i3++) {
                    if (isInstalledPackage(privGamesFps.keyAt(i3), arraySet)) {
                        this.mPrivGames.put(privGamesFps.keyAt(i3), Integer.valueOf(getSuitableFps(supportFps2, privGamesFps.valueAt(i3).intValue())));
                    }
                }
                parseTopDefineAppFromDb(null, arraySet);
                parseTopSmartAppFromDb(null, arraySet);
                parseTopVideoAppFromDb(null, arraySet);
                parseTopVideoIdleAppFromDb(null, arraySet);
                parseExcludeAppFromDb(null, arraySet);
                parseEbookIdleAppFromDb(null, arraySet);
                parseTpIdleAppFromDb(null, arraySet);
                parseInputAppFromDb(null, arraySet);
                parseLowFpsAppFromDb(null, arraySet);
                parseTopShortVideoAppFromDb(null, arraySet);
                parsePromotionAppFromDb(null, arraySet);
                parseDisplayFpsAppFromDb(null, arraySet);
                parseFullScreenFpsAppFromDb(null, arraySet);
                parseMapFpsAppFromDb(null, arraySet);
                parseAutoModeVrrAppFromDb(null, arraySet);
                parseHighestFpsAppFromDb(null, arraySet);
                return;
            }
            return;
        }
        String str3 = (String) eventResult.mObj;
        if ("true".equals(mIsCustomFpsSwitch)) {
            try {
                Uri uri3 = HighRefreshRateConfigure.CONTENT_URI;
                Context context4 = this.mContext;
                UserHandle userHandle3 = UserHandle.OWNER;
                cursorQuery = context4.getContentResolverForUser(userHandle3).query(uri3, null, "package_name = ?", new String[]{str3}, null);
                try {
                    try {
                        writeLocalLog("remove app = " + cursorQuery);
                        if (cursorQuery.moveToNext()) {
                            writeLocalLog("delete = " + str3);
                            this.mContext.getContentResolverForUser(userHandle3).delete(uri3, "package_name=?", new String[]{str3});
                        }
                    } catch (Exception e7) {
                        e = e7;
                        Log.e(TAG, "remove app", e);
                        if (cursorQuery != null) {
                        }
                        this.mPrivGames.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mPrivGames=" + this.mPrivGames);
                        }
                        this.mDefaultGameApp.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mDefaultGameApp=" + this.mDefaultGameApp);
                        }
                        this.mVideoApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mVideoApps=" + this.mVideoApps);
                        }
                        this.mVideoIdleApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mVideoIdleApps=" + this.mVideoIdleApps);
                        }
                        this.mExcludeApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mExcludeApps=" + this.mExcludeApps);
                        }
                        this.mTopDefineApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mTopDefineApps=" + this.mTopDefineApps);
                        }
                        this.mTopSmartApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mTopSmartApps=" + this.mTopSmartApps);
                        }
                        this.mInputApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mInputApps=" + this.mInputApps);
                        }
                        mLowFpsPkgList.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mCameraPkgList=" + mLowFpsPkgList);
                        }
                        this.mTpidleApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mTpidleApps=" + this.mTpidleApps);
                        }
                        this.mEbookidleApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mEbookidleApps=" + this.mEbookidleApps);
                        }
                        this.mPromotionApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mPromotionApps=" + this.mPromotionApps);
                        }
                        this.mDisplayFpsApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mDisplayFpsApps=" + this.mDisplayFpsApps);
                        }
                        mShortVideoApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mShortVideoApps=" + mShortVideoApps);
                        }
                        this.mFullScreenFpsApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mFullScreenFpsApps=" + this.mFullScreenFpsApps);
                        }
                        this.mVideoLowerFpsApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mVideoLowerFpsApps=" + this.mVideoLowerFpsApps);
                        }
                        this.mMapFpsApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mMapFpsApps=" + this.mMapFpsApps);
                        }
                        this.mAutoModeApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mAutoModeApps=" + this.mAutoModeApps);
                        }
                        this.mVrrApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mVrrApps=" + this.mVrrApps);
                        }
                        this.mDefineHighestFpsApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mDefineHighestFpsApps=" + this.mDefineHighestFpsApps);
                        }
                        this.mCustomHighestFpsApps.remove(str3);
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "onPkgExistentChanged mCustomHighestFpsApps=" + this.mCustomHighestFpsApps);
                        }
                    }
                } catch (Throwable th5) {
                    th = th5;
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    throw th;
                }
            } catch (Exception e8) {
                e = e8;
                cursorQuery = null;
            } catch (Throwable th6) {
                th = th6;
                cursorQuery = null;
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                throw th;
            }
            cursorQuery.close();
        }
        this.mPrivGames.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mPrivGames=" + this.mPrivGames);
        }
        this.mDefaultGameApp.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mDefaultGameApp=" + this.mDefaultGameApp);
        }
        this.mVideoApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mVideoApps=" + this.mVideoApps);
        }
        this.mVideoIdleApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mVideoIdleApps=" + this.mVideoIdleApps);
        }
        this.mExcludeApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mExcludeApps=" + this.mExcludeApps);
        }
        this.mTopDefineApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mTopDefineApps=" + this.mTopDefineApps);
        }
        this.mTopSmartApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mTopSmartApps=" + this.mTopSmartApps);
        }
        this.mInputApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mInputApps=" + this.mInputApps);
        }
        mLowFpsPkgList.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mCameraPkgList=" + mLowFpsPkgList);
        }
        this.mTpidleApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mTpidleApps=" + this.mTpidleApps);
        }
        this.mEbookidleApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mEbookidleApps=" + this.mEbookidleApps);
        }
        this.mPromotionApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mPromotionApps=" + this.mPromotionApps);
        }
        this.mDisplayFpsApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mDisplayFpsApps=" + this.mDisplayFpsApps);
        }
        mShortVideoApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mShortVideoApps=" + mShortVideoApps);
        }
        this.mFullScreenFpsApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mFullScreenFpsApps=" + this.mFullScreenFpsApps);
        }
        this.mVideoLowerFpsApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mVideoLowerFpsApps=" + this.mVideoLowerFpsApps);
        }
        this.mMapFpsApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mMapFpsApps=" + this.mMapFpsApps);
        }
        this.mAutoModeApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mAutoModeApps=" + this.mAutoModeApps);
        }
        this.mVrrApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mVrrApps=" + this.mVrrApps);
        }
        this.mDefineHighestFpsApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mDefineHighestFpsApps=" + this.mDefineHighestFpsApps);
        }
        this.mCustomHighestFpsApps.remove(str3);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onPkgExistentChanged mCustomHighestFpsApps=" + this.mCustomHighestFpsApps);
        }
    }

    private void parseAutoModeVrrAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f29k;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_AUTOMODE_VRR_GROUP, null);
                writeLocalLog("parseAutoModeVrrAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_AUTOMODE_VRR_GROUP, null);
                writeLocalLog("parseAutoModeVrrAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseAutoModeVrrAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            String strOptString = jSONObject.optString("auto_mode", "");
            if (!strOptString.isEmpty()) {
                String[] strArrSplit = strOptString.split(",");
                for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                    if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                        this.mAutoModeApps.add(strArrSplit[i3]);
                    }
                }
            }
            String strOptString2 = jSONObject.optString("vrr_group", "");
            if (!strOptString2.isEmpty()) {
                String[] strArrSplit2 = strOptString2.split(",");
                for (int i4 = 0; i4 < strArrSplit2.length; i4++) {
                    if (isInstalledPackage(strArrSplit2[i4], arraySet)) {
                        this.mVrrApps.add(strArrSplit2[i4]);
                    }
                }
            }
            writeLocalLog("parseAutoModeVrrAppFromDb mAutoModeApps=" + this.mAutoModeApps + "mVrrApps=" + this.mVrrApps);
        } catch (JSONException e2) {
            writeLocalLog("parseAutoModeVrrAppFromDb e=" + e2);
        }
    }

    private void parseBacklightAppFromDb(String str) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f32n;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_SMART_BACKLIGHT_GROUP, null);
                writeLocalLog("parseBacklightAppFromDb old cloud = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.toString();
                writeLocalLog("parseBacklightAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseBacklightAppFromDb empty");
            return;
        }
        ArraySet arraySet = new ArraySet();
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mBacklightModeSwitch = jSONObject.optBoolean("fucSwitch", false);
            writeLocalLog("mBacklightModeSwitch = " + this.mBacklightModeSwitch);
            if (this.mBacklightModeSwitch) {
                String strOptString = jSONObject.optString("pkg", "");
                if (!strOptString.isEmpty()) {
                    for (String str2 : strOptString.split(",")) {
                        arraySet.add(str2);
                    }
                    updateBacklightModeApps((String[]) arraySet.toArray(new String[arraySet.size()]));
                }
            }
            writeLocalLog("parseBacklightAppFromDb mBacklightModeApps=" + arraySet);
        } catch (JSONException e2) {
            writeLocalLog("parseBacklightAppFromDb e=" + e2);
        }
    }

    private void parseCustomModeSwitchFromDb(String str) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f25g;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CUSTOM_SWITCH, null);
                writeLocalLog("parseCustomModeSwitchFromDb old cloud or local config= " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.toString();
                writeLocalLog("parseCustomModeSwitchFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            if (!isSupportDevice()) {
                writeLocalLog("parseCustomModeSwitchFromDb empty");
                return;
            } else {
                writeLocalLog("parseCustomModeSwitchFromDb empty, isSupportDevice= true");
                str = "{\"fucSwitch\":\"true\"}";
            }
        }
        try {
            mIsCustomFpsSwitch = new JSONObject(str).optString("fucSwitch");
            if (this.mPowerMode != 1 || !this.mViolentModeSwitch) {
                Settings.System.putString(this.mContext.getContentResolver(), "custom_mode_switch", mIsCustomFpsSwitch);
            }
            writeLocalLog("parseCustomModeSwitchFromDb mIsCustomFpsSwitch= " + mIsCustomFpsSwitch);
        } catch (JSONException e2) {
            writeLocalLog("parseCustomModeSwitchFromDb e=" + e2);
        }
    }

    private void parseDisplayFpsAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f28j;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_DISPLAY_FPS_GROUP, null);
                writeLocalLog("parseDisplayFpsAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_DISPLAY_FPS_GROUP, null);
                writeLocalLog("parseDisplayFpsAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseDisplayFspAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        String[] strArrSplit = str.split(",");
        for (int i3 = 0; i3 < strArrSplit.length; i3++) {
            if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                this.mDisplayFpsApps.add(strArrSplit[i3]);
            }
        }
        writeLocalLog("parseDisplayFspAppFromDb mDisplayFpsApps = " + this.mDisplayFpsApps);
    }

    private void parseEbookIdleAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f29k;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_EBOOK_IDLE_GROUP, null);
                writeLocalLog("parseEbookIdleAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_EBOOK_IDLE_GROUP, null);
                writeLocalLog("parseEbookIdleAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseEbookIdleAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        String[] strArrSplit = str.split(",");
        for (int i3 = 0; i3 < strArrSplit.length; i3++) {
            if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                this.mEbookidleApps.add(strArrSplit[i3]);
            }
        }
        writeLocalLog("parseEbookIdleAppFromDb mEbookidleApps=" + this.mEbookidleApps);
    }

    private void parseExcludeAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f26h;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_FPS_EXCLUDE_PKG, null);
                writeLocalLog("parseExcludeAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_FPS_EXCLUDE_PKG, null);
                writeLocalLog("parseExcludeAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseExcludeAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        String[] strArrSplit = str.split(",");
        for (int i3 = 0; i3 < strArrSplit.length; i3++) {
            if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                this.mExcludeApps.add(strArrSplit[i3]);
            }
        }
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "parseExcludeAppFromDb mExcludeApps=" + this.mExcludeApps + " appJsonStr=" + str);
        }
    }

    private void parseEyeAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f33o;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_EYE_MODE_GROUP, null);
                writeLocalLog("parseEyeAppFromDb old cloud = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.toString();
                writeLocalLog("parseEyeAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseEyeAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mEyeModeSwitch = jSONObject.optBoolean("fucSwitch", false);
            Log.d(TAG, "parseEyeAppFromDb eyemode fucSwitch=" + this.mEyeModeSwitch);
            if (this.mEyeModeSwitch) {
                String strOptString = jSONObject.optString("pkg", "");
                if (strOptString.isEmpty()) {
                    return;
                }
                String[] strArrSplit = strOptString.split(",");
                ArraySet arraySet2 = new ArraySet();
                for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                    if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                        arraySet2.add(strArrSplit[i3]);
                    }
                }
                updateEyeModeApps((String[]) arraySet2.toArray(new String[arraySet2.size()]));
                writeLocalLog("parseEyeAppFromDb mEyeModeApps=" + arraySet2);
            }
        } catch (JSONException e2) {
            writeLocalLog("parseEyeAppFromDb e=" + e2);
        }
    }

    private void parseFossSwitchFromDb(String str) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f26h;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_FOSS_GROUP, null);
                writeLocalLog("parseFossSwitchFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_FOSS_GROUP, null);
                writeLocalLog("parseFossSwitchFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseFossSwitchFromDb empty");
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mIsFossCloudSwitch = jSONObject.optBoolean("fucSwitch", false);
            this.mIsDitherCloudSwitch = jSONObject.optBoolean("ditherSwitch", false);
            writeLocalLog("fossFucSwitch=" + this.mIsFossCloudSwitch + "ditherSwitch=" + this.mIsDitherCloudSwitch);
        } catch (JSONException e2) {
            writeLocalLog("parseFossSwitchFromDb e=" + e2);
        }
    }

    private void parseFullScreenFpsAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f29k;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_FULL_SCREEN_GROUP, null);
                writeLocalLog("parseFullScreenFpsAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_FULL_SCREEN_GROUP, null);
                writeLocalLog("parseFullScreenFpsAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseFullScreenFpsAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            String strOptString = jSONObject.optString("full_screen", "");
            if (!strOptString.isEmpty()) {
                String[] strArrSplit = strOptString.split(",");
                for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                    if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                        this.mFullScreenFpsApps.add(strArrSplit[i3]);
                    }
                }
            }
            String strOptString2 = jSONObject.optString("video_lower_fps", "");
            if (!strOptString2.isEmpty()) {
                String[] strArrSplit2 = strOptString2.split(",");
                for (int i4 = 0; i4 < strArrSplit2.length; i4++) {
                    if (isInstalledPackage(strArrSplit2[i4], arraySet)) {
                        this.mVideoLowerFpsApps.add(strArrSplit2[i4]);
                    }
                }
            }
            writeLocalLog("parseFullScreenFpsAppFromDb mFullScreenFpsApps=" + this.mFullScreenFpsApps + "mVideoLowerFpsApps=" + this.mVideoLowerFpsApps);
        } catch (JSONException e2) {
            writeLocalLog("parseFullScreenFpsAppFromDb e=" + e2);
        }
    }

    private void parseHighestFpsAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f30l;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_HIGHEST_FPS_GROUP, null);
                writeLocalLog("parseHighestFpsAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_HIGHEST_FPS_GROUP, null);
                writeLocalLog("parseHighestFpsAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseHighestFpsAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mHighestFpsSwitch = jSONObject.optBoolean("highestSwitch", false);
            writeLocalLog("parseHighestFpsAppFromDb mHighestFpsSwitch=" + this.mHighestFpsSwitch);
            if (this.mHighestFpsSwitch) {
                String strOptString = jSONObject.optString("defineList", "");
                if (!strOptString.isEmpty()) {
                    String[] strArrSplit = strOptString.split(",");
                    for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                        if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                            this.mDefineHighestFpsApps.add(strArrSplit[i3]);
                        }
                    }
                }
                writeLocalLog("parseHighestFpsAppFromDb mDefineHighestFpsApps=" + this.mDefineHighestFpsApps);
                String strOptString2 = jSONObject.optString("customList", "");
                if (!strOptString2.isEmpty()) {
                    String[] strArrSplit2 = strOptString2.split(",");
                    for (int i4 = 0; i4 < strArrSplit2.length; i4++) {
                        if (isInstalledPackage(strArrSplit2[i4], arraySet)) {
                            this.mCustomHighestFpsApps.add(strArrSplit2[i4]);
                        }
                    }
                }
                writeLocalLog("parseHighestFpsAppFromDb mCustomHighestFpsApps=" + this.mCustomHighestFpsApps);
            }
        } catch (JSONException e2) {
            writeLocalLog("parseHighestFpsAppFromDb e=" + e2);
        }
    }

    private void parseInit() throws Throwable {
        parseLocalConfig();
        parseFossSwitchFromDb(null);
        parseNoteSwitchFromDb(null);
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.SYSTEM);
        this.mWs = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        parseCustomModeSwitchFromDb(null);
        contentResolverForUser.registerContentObserver(Settings.Secure.getUriFor("user_refresh_rate"), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.mUserFps = displayFrameSetting.getUserFps();
                DisplayFrameSetting displayFrameSetting2 = DisplayFrameSetting.this;
                displayFrameSetting2.mCurrentFps = displayFrameSetting2.mUserFps;
                DisplayFrameSetting.this.setFpsList();
                DisplayFrameSetting.this.writeLocalLog("onChange mUserFps=" + DisplayFrameSetting.this.mUserFps);
            }
        });
        contentResolverForUser.registerContentObserver(Settings.System.getUriFor("is_smart_fps"), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                if (Settings.System.getInt(DisplayFrameSetting.this.mContext.getContentResolver(), "is_smart_fps", 0) == 0) {
                    DisplayFrameSetting.mIsSmart = false;
                    DisplayFrameSetting.this.setFpsList();
                } else {
                    DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                    displayFrameSetting.mTopApps = displayFrameSetting.mTopSmartApps;
                    DisplayFrameSetting.mIsSmart = true;
                    DisplayFrameSetting.this.mIsCustom = false;
                }
                DisplayFrameSetting.this.writeLocalLog("onChange mIsSmart=" + DisplayFrameSetting.mIsSmart);
            }
        });
        if (FeatureParser.getBoolean("support_smart_fps", false) && Settings.System.getInt(this.mContext.getContentResolver(), "is_smart_fps", 1) == 1) {
            this.mTopApps = this.mTopSmartApps;
            mIsSmart = true;
            this.mIsCustom = false;
        } else {
            mIsSmart = false;
            setFpsList();
        }
        contentResolverForUser.registerContentObserver(Settings.System.getUriFor("speed_mode"), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.4
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.mIsUltimate = Settings.System.getInt(displayFrameSetting.mContext.getContentResolver(), "speed_mode", 0);
                if (DisplayFrameSetting.this.mIsUltimate == 1) {
                    Settings.Secure.putInt(DisplayFrameSetting.this.mContext.getContentResolver(), "miui_refresh_rate", DisplayFrameSetting.this.mUserFps);
                } else if (DisplayFrameSetting.this.IS_SUPPORTED_IRIS && DisplayFrameSetting.this.mIsExtremeVideoModeOn == 1) {
                    if (DisplayFrameSetting.this.hasMessages(26)) {
                        DisplayFrameSetting.this.removeMessages(26);
                    }
                    DisplayFrameSetting.this.sendEmptyMessage(26);
                }
                DisplayFrameSetting.this.writeLocalLog("onChange mIsUltimate=" + DisplayFrameSetting.this.mIsUltimate);
            }
        });
        contentResolverForUser.registerContentObserver(Settings.System.getUriFor("custom_mode"), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.5
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                String string = Settings.System.getString(DisplayFrameSetting.this.mContext.getContentResolver(), "custom_mode");
                DisplayFrameSetting.this.updateCustomDataBase(string);
                DisplayFrameSetting.this.writeLocalLog("onChange custom_mode pkg=" + string);
            }
        });
        if (this.IS_SUPPORTED_IRIS) {
            contentResolverForUser.registerContentObserver(Settings.Global.getUriFor(ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.6
                @Override // android.database.ContentObserver
                public void onChange(boolean z2, Uri uri) {
                    if (DisplayFrameSetting.this.hasMessages(26)) {
                        DisplayFrameSetting.this.removeMessages(26);
                    }
                    if (IrisServiceUtils.getInstance(DisplayFrameSetting.this.mContext).getThermalLimitSwitch_X7()) {
                        return;
                    }
                    DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                    displayFrameSetting.mIsExtremeVideoModeOn = Settings.Global.getInt(displayFrameSetting.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, 0);
                    DisplayFrameSetting.this.writeLocalLog("onChange mIsExtremeVideoModeOn=" + DisplayFrameSetting.this.mIsExtremeVideoModeOn);
                    if (ArrayUtils.contains(DisplayFrameSetting.mExtremeVideoPackageList, DisplayFrameSetting.this.mCurrentFgPkg)) {
                        if (DisplayFrameSetting.this.mIsExtremeVideoModeOn != 1) {
                            DisplayFrameSetting.this.onExtremeSceneRecovery();
                        }
                        DisplayFrameSetting.this.sendEmptyMessage(26);
                    }
                }
            });
            this.mIsExtremeVideoModeOn = Settings.Global.getInt(this.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, 0);
            Display defaultDisplay = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay();
            this.mDisplay = defaultDisplay;
            this.mRealRefreshRate = defaultDisplay.getRefreshRate();
            ((DisplayManager) this.mContext.getSystemService("display")).registerDisplayListener(this.mDisplayListener, this);
        }
        this.mIsUltimate = Settings.System.getInt(this.mContext.getContentResolver(), "speed_mode", 0);
        this.mLevelFullSwitch = ((BatteryManager) this.mContext.getSystemService("batterymanager")).getIntProperty(4) == 100;
        this.mChargingSwitch = PowerKeeperManager.getInstance().isCharging();
        ArrayMap<String, Integer> privGamesFps = getPrivGamesFps(null);
        ArraySet<String> installedThirdPartApps = PowerKeeperPackageManager.getInstalledThirdPartApps();
        fillDefaultGameApp(SimpleSettings.Misc.getString(this.mContext, "key_top_names", ""), privGamesFps, installedThirdPartApps);
        int[] supportFps = getSupportFps();
        for (int i2 = 0; i2 < privGamesFps.size(); i2++) {
            String strKeyAt = privGamesFps.keyAt(i2);
            if (isInstalledPackage(strKeyAt, installedThirdPartApps)) {
                this.mPrivGames.put(strKeyAt, Integer.valueOf(getSuitableFps(supportFps, privGamesFps.valueAt(i2).intValue())));
            }
        }
        for (int i3 : supportFps) {
            if (i3 == 30) {
                this.mIsSupportLowFps = true;
            }
        }
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "mPrivGames=" + this.mPrivGames);
        }
        parseTopDefineAppFromDb(null, installedThirdPartApps);
        parseTopSmartAppFromDb(null, installedThirdPartApps);
        parseTopVideoAppFromDb(null, installedThirdPartApps);
        parseTopVideoIdleAppFromDb(null, installedThirdPartApps);
        parseExcludeAppFromDb(null, installedThirdPartApps);
        parseEyeAppFromDb(null, installedThirdPartApps);
        parseBacklightAppFromDb(null);
        parseEbookIdleAppFromDb(null, installedThirdPartApps);
        parseTpIdleAppFromDb(null, installedThirdPartApps);
        parseInputAppFromDb(null, installedThirdPartApps);
        parseLowFpsAppFromDb(null, installedThirdPartApps);
        parseTopShortVideoAppFromDb(null, installedThirdPartApps);
        parsePromotionAppFromDb(null, installedThirdPartApps);
        parseDisplayFpsAppFromDb(null, installedThirdPartApps);
        parseFullScreenFpsAppFromDb(null, installedThirdPartApps);
        parseMapFpsAppFromDb(null, installedThirdPartApps);
        parseAutoModeVrrAppFromDb(null, installedThirdPartApps);
        parseHighestFpsAppFromDb(null, installedThirdPartApps);
        parseViolentModeSwitchFromDb(null);
        parseIrisConfigFromDb(null);
        registerReceiver(this.mTopApps.containsKey("power.systemui"));
        registerActivityChangeListener();
        AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
        this.mAudioManager = audioManager;
        audioManager.registerAudioRecordingCallback(this.mAudioRecordingCallback, this);
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.registerForForegroundInfo(this);
            eventsAggregator.registerForDockedStack(this, 7);
            eventsAggregator.registerForPkgExistentState(this, 10);
            eventsAggregator.registerForScreenOn(this, null);
            eventsAggregator.registerForScreenOff(this, null);
            eventsAggregator.registerForUserPresent(this, null);
            eventsAggregator.registerForInputInfo(this);
            eventsAggregator.registerBatteryLevelListener(this);
            eventsAggregator.registerForCharging(this);
            eventsAggregator.registerPowerModeListener(this);
            this.mCurrentFgInfo = eventsAggregator.getForegroundInfo();
            this.mPowerMode = eventsAggregator.getPowerMode();
            onPowerModeChanged();
            this.mCurrentCookie = 249;
            ForegroundInfo foregroundInfo = this.mCurrentFgInfo;
            if (foregroundInfo != null) {
                onForegroundChanged(foregroundInfo);
            } else {
                writeLocalLog("init mCurrentFgInfo is null");
            }
            notifySFIChange();
        }
        this.mSmartFpsApps.add("com.tencent.mm");
        this.mParseInited = true;
    }

    private void parseInputAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f30l;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_INPUT_AUDIO_GROUP, null);
                writeLocalLog("parseInputAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_INPUT_AUDIO_GROUP, null);
                writeLocalLog("parseInputAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseInputAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mInputModeSwitch = jSONObject.optBoolean("inputSwitch", true);
            this.mAudioRecordSwitch = jSONObject.optBoolean("audioSwitch", true);
            writeLocalLog("parseInputAppFromDb mAudioRecordSwitch=" + this.mAudioRecordSwitch);
            Log.d(TAG, "parseInputAppFromDb inputSwitch=" + this.mInputModeSwitch);
            if (this.mInputModeSwitch) {
                String strOptString = jSONObject.optString("input_list30", "");
                if (!strOptString.isEmpty()) {
                    String[] strArrSplit = strOptString.split(",");
                    for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                        if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                            this.mInputApps.put(strArrSplit[i3], 30);
                        }
                    }
                }
                String strOptString2 = jSONObject.optString("input_list60", "");
                if (!strOptString2.isEmpty()) {
                    String[] strArrSplit2 = strOptString2.split(",");
                    for (int i4 = 0; i4 < strArrSplit2.length; i4++) {
                        if (isInstalledPackage(strArrSplit2[i4], arraySet)) {
                            this.mInputApps.put(strArrSplit2[i4], 60);
                        }
                    }
                }
            }
            if (this.mAudioRecordSwitch) {
                String strOptString3 = jSONObject.optString("audio_list30", "");
                if (!strOptString3.isEmpty()) {
                    String[] strArrSplit3 = strOptString3.split(",");
                    for (int i5 = 0; i5 < strArrSplit3.length; i5++) {
                        if (isInstalledPackage(strArrSplit3[i5], arraySet)) {
                            this.mAudioApps.put(strArrSplit3[i5], 30);
                        }
                    }
                }
                String strOptString4 = jSONObject.optString("audio_list60", "");
                if (strOptString4.isEmpty()) {
                    return;
                }
                String[] strArrSplit4 = strOptString4.split(",");
                for (int i6 = 0; i6 < strArrSplit4.length; i6++) {
                    if (isInstalledPackage(strArrSplit4[i6], arraySet)) {
                        this.mAudioApps.put(strArrSplit4[i6], 60);
                    }
                }
            }
        } catch (JSONException e2) {
            writeLocalLog("parseInputAppFromDb e=" + e2);
        }
    }

    private void parseIrisConfigFromDb(String str) {
        JSONObject jSONObjectL;
        if (!this.IS_SUPPORTED_IRIS) {
            writeLocalLog("parseIrisConfigFromDb failed due to Iris feature not supported");
            return;
        }
        if (str == null && (jSONObjectL = this.mCloudObserver.l(b.b.J)) != null) {
            str = jSONObjectL.toString();
            writeLocalLog("parseIrisConfigFromDb new cloud = " + jSONObjectL);
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseIrisConfigFromDb empty");
            return;
        }
        try {
            JSONArray jSONArrayOptJSONArray = new JSONObject(str).optJSONArray("app_param");
            writeLocalLog("parseIrisConfigFromDb param=" + jSONArrayOptJSONArray.toString());
            int length = jSONArrayOptJSONArray.length();
            this.mMemcParam = new String[length];
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < length; i2++) {
                String[] strArrSplit = jSONArrayOptJSONArray.optString(i2).split("#");
                String[] strArrSplit2 = strArrSplit[0].split("/");
                if (!mExtremeVideoPackageList.contains(strArrSplit2[0])) {
                    if ("D".equals(strArrSplit[1])) {
                        this.mDuelMemcPackage.add(strArrSplit2[0]);
                    } else {
                        this.mSingleMemcPackage.add(strArrSplit2[0]);
                    }
                    mExtremeVideoPackageList.add(strArrSplit2[0]);
                    arrayList.add(strArrSplit[2]);
                }
                mExtremeVideoActivityList.add(strArrSplit2[1]);
                this.mMemcParam[i2] = strArrSplit[3];
            }
            this.mSdr2HdrParam = (String[]) arrayList.toArray(new String[0]);
        } catch (JSONException unused) {
            Log.e(TAG, "parse json config failed");
        } catch (Exception e2) {
            Log.e(TAG, "update config failed" + e2);
        }
        registerActivityChangeListener();
    }

    private void parseLocalConfig() {
        JSONObject defaultLocalConfig;
        try {
            String validLocalConfigPath = getValidLocalConfigPath(this.mContext);
            if (validLocalConfigPath == null) {
                writeLocalLog("parseLocalConfig ignore for new  local config is not exist");
                return;
            }
            String strFromAssetFile = AssetUtils.getStrFromAssetFile(this.mContext, validLocalConfigPath + "/" + FPS_FILE_NAME);
            if (strFromAssetFile == null) {
                Log.e(TAG, "parseLocalConfig error for strFpsConfig is null");
                return;
            }
            String str = FPS_LOCAL_CONFIG_FILE_PATH;
            JSONObject jSONObject = null;
            if (str.equals(validLocalConfigPath) || FPS_INTL_LOCAL_CONFIG_FILE_PATH.equals(validLocalConfigPath)) {
                String str2 = str.equals(validLocalConfigPath) ? CN_DEFAULT_KEY : INTL_DEFAULT_KEY;
                mConfigType = str.equals(validLocalConfigPath) ? 2 : 1;
                defaultLocalConfig = getDefaultLocalConfig(str2);
            } else {
                if (FPS_OPT_DEFAULT_LOCAL_CONFIG_PATH.equals(validLocalConfigPath)) {
                    mConfigType = 3;
                }
                defaultLocalConfig = null;
            }
            if (!strFromAssetFile.isEmpty()) {
                if (mConfigType == 3) {
                    jSONObject = getDefaultLocalConfig(INTL_DEFAULT_KEY);
                } else {
                    String str3 = new String(Base64.decode(strFromAssetFile, 0));
                    if (!TextUtils.isEmpty(str3)) {
                        jSONObject = new JSONObject(str3);
                    }
                }
            }
            if ((jSONObject != null && jSONObject.length() != 0) || (defaultLocalConfig != null && defaultLocalConfig.length() != 0)) {
                this.mConfigVersion = getValidConfigFromData(jSONObject, defaultLocalConfig, "version", -1);
                onCloudFunctionUpdated(this.mContext, jSONObject, defaultLocalConfig, mConfigType);
                return;
            }
            Log.e(TAG, "parseLocalConfig error for local config is empty");
            mConfigType = 0;
        } catch (JSONException e2) {
            mConfigType = 0;
            Log.e(TAG, "parseLocalConfig", e2);
        } catch (Exception e3) {
            mConfigType = 0;
            Log.e(TAG, "parseLocalConfig", e3);
        }
    }

    private void parseLowFpsAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f30l;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_LOW_FPS_GROUP, null);
                writeLocalLog("parseLowFpsAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_LOW_FPS_GROUP, null);
                writeLocalLog("parseLowFpsAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseLowFpsAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mLowFpsSwitch = jSONObject.optBoolean("lowFpsSwitch", true);
            this.mCameraPowerSaveSwitch = jSONObject.optBoolean("cameraPowerSaveSwitch", false);
            this.mCameraPowerSaveValue = jSONObject.optInt("cameraPowerSaveValue", 5);
            writeLocalLog("parseLowFpsAppFromDb mLowFpsSwitch=" + this.mLowFpsSwitch);
            if (this.mLowFpsSwitch) {
                String[] strArrSplit = jSONObject.optString("pkgName", "").split(",");
                this.mBlacklistActivity_Camera = jSONObject.optString("blacklistAtyName_camera", "").split(",");
                this.mBlacklistActivity_LowFps = jSONObject.optString("blacklistAtyName_lowfps", "").split(",");
                this.mBlacklistActivity_Vr = jSONObject.optString("blacklistAtyName_vr", "").split(",");
                this.mHighFpsListActivity = jSONObject.optString("highfpsAtyName", "").split(",");
                this.mBlackListActivityApplets = jSONObject.optString("appletsAtyName", "").split(",");
                writeLocalLog("parseLowFpsAppFromDb pkgs=" + Arrays.toString(strArrSplit) + " mBlacklistActivity_LowFps=" + Arrays.toString(this.mBlacklistActivity_LowFps) + " blacklistAtyName_vr=" + Arrays.toString(this.mBlacklistActivity_Vr) + " mHighFpsListActivity= " + Arrays.toString(this.mHighFpsListActivity) + " mBlackListActivityApplets" + Arrays.toString(this.mBlackListActivityApplets));
                if (strArrSplit != null) {
                    for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                        if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                            Collections.addAll(mLowFpsPkgList, strArrSplit[i3]);
                        }
                    }
                    writeLocalLog("parseLowFpsAppFromDb mLowFpsPkgList=" + mLowFpsPkgList);
                }
                String[] strArr = this.mBlacklistActivity_Camera;
                if (strArr != null) {
                    Collections.addAll(mCameraActivityList, strArr);
                    writeLocalLog("parseLowFpsAppFromDb mCameraActivityList=" + mCameraActivityList);
                }
                String[] strArr2 = this.mBlacklistActivity_LowFps;
                if (strArr2 != null) {
                    Collections.addAll(mCameraActivityList, strArr2);
                }
                String[] strArr3 = this.mBlacklistActivity_Vr;
                if (strArr3 != null) {
                    Collections.addAll(mCameraActivityList, strArr3);
                }
                String[] strArr4 = this.mHighFpsListActivity;
                if (strArr4 != null && strArr4.length >= 1 && !"".equals(strArr4[0])) {
                    Collections.addAll(mCameraActivityList, this.mHighFpsListActivity);
                    registerVideoFpsCallBack();
                }
                String[] strArr5 = this.mBlackListActivityApplets;
                if (strArr5 != null && strArr5.length >= 1 && !"".equals(strArr5[0])) {
                    Collections.addAll(mCameraActivityList, this.mBlackListActivityApplets);
                    this.mAppletsSwitch = true;
                }
            }
            registerActivityChangeListener();
        } catch (JSONException e2) {
            writeLocalLog("parseInputAppFromDb e=" + e2);
        }
    }

    private void parseMapFpsAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f29k;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_MAP_FPS_GROUP, null);
                writeLocalLog("parseMapFpsAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_MAP_FPS_GROUP, null);
                writeLocalLog("parseMapFpsAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseMapFpsAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        String[] strArrSplit = str.split(",");
        for (int i3 = 0; i3 < strArrSplit.length; i3++) {
            if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                this.mMapFpsApps.add(strArrSplit[i3]);
            }
        }
        writeLocalLog("parseMapFpsAppFromDb mMapFpsApps=" + this.mMapFpsApps);
    }

    private void parseNoteSwitchFromDb(String str) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f27i;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_NOTE_PKG, null);
                writeLocalLog("parseNoteSwitchFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.toString();
                writeLocalLog("parseNoteSwitchFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseNoteSwitchFromDb empty");
            return;
        }
        try {
            this.mIsNoteCloudSwitch = new JSONObject(str).optBoolean("noteSwitch", false);
            writeLocalLog("noteSwitch" + this.mIsNoteCloudSwitch);
        } catch (JSONException e2) {
            writeLocalLog("parseNoteSwitchFromDb e=" + e2);
        }
    }

    private void parsePromotionAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f29k;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_PROMOTION_GROUP, null);
                writeLocalLog("parsePromotionAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_PROMOTION_GROUP, null);
                writeLocalLog("parsePromotionAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parsePromotionAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        String[] strArrSplit = str.split(",");
        for (int i3 = 0; i3 < strArrSplit.length; i3++) {
            if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                this.mPromotionApps.add(strArrSplit[i3]);
            }
        }
        writeLocalLog("parsePromotionAppFromDb mPromotionApps=" + this.mPromotionApps);
    }

    /* JADX WARN: Code duplicated, block: B:121:0x0286  */
    /* JADX WARN: Code duplicated, block: B:159:0x0373  */
    /* JADX WARN: Code duplicated, block: B:161:0x037a  */
    /* JADX WARN: Code duplicated, block: B:166:0x038a  */
    /* JADX WARN: Code duplicated, block: B:167:0x039e  */
    /* JADX WARN: Code duplicated, block: B:169:0x03a4  */
    /* JADX WARN: Code duplicated, block: B:170:0x03aa  */
    /* JADX WARN: Code duplicated, block: B:172:0x03b2  */
    /* JADX WARN: Code duplicated, block: B:198:0x0264 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:206:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:35:0x00e6  */
    private void parseTopDefineAppFromDb(String str, ArraySet<String> arraySet) throws Throwable {
        String str2;
        int[] supportFps;
        int integer;
        boolean z2;
        boolean zContainsKey;
        String str3;
        Cursor cursor;
        String str4;
        ArraySet<String> arraySet2;
        Cursor cursorQuery;
        Cursor cursorQuery2;
        String string;
        Cursor cursor2 = null;
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f30l;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                string = SimpleSettings.Misc.getString(this.mContext, CLOUD_FPS_DEFINE_GROUP, null);
                writeLocalLog("parseTopDefineAppFromDb old cloud or local config = " + string);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                string = jSONObjectL.optString(CLOUD_FPS_DEFINE_GROUP, null);
                writeLocalLog("parseTopDefineAppFromDb new cloud = " + string);
            }
            str2 = string;
        } else {
            str2 = str;
        }
        String str5 = "power.systemui";
        boolean zContainsKey2 = this.mTopDefineApps.containsKey("power.systemui");
        this.mTopDefineApps.clear();
        if ("true".equals(mIsCustomFpsSwitch) && Settings.System.getInt(this.mContext.getContentResolver(), "is_update_database_top", 1) == 0) {
            try {
                Uri uri = HighRefreshRateConfigure.CONTENT_URI;
                new ContentValues();
                cursorQuery2 = this.mContext.getContentResolverForUser(UserHandle.OWNER).query(uri, null, null, null, null);
                if (cursorQuery2 != null) {
                    while (cursorQuery2.moveToNext()) {
                        try {
                            try {
                                this.mCustomApps.put(cursorQuery2.getString(cursorQuery2.getColumnIndex("package_name")), 60);
                            } catch (Exception e2) {
                                e = e2;
                                Log.e(TAG, "parseTopDefineAppFromDb first", e);
                                if (cursorQuery2 != null) {
                                }
                                supportFps = getSupportFps();
                                if (str2 != null) {
                                }
                                writeLocalLog("parseTopDefineAppFromDb empty");
                                if (this.mIsWhitelistPolicy) {
                                    this.mDefaultFps = 60;
                                    writeLocalLog("setScreenEffect enable whitelist");
                                    DisplayFeatureManager.getInstance().setScreenEffect(24, 2, 256);
                                    return;
                                } else {
                                    if (this.mIsWhiteFpsList) {
                                        this.mDefaultFps = 60;
                                        writeLocalLog("mIsWhiteFpsList = true ");
                                        return;
                                    }
                                    integer = FeatureParser.getInteger("defaultFps", 60);
                                    this.mDefaultFps = integer;
                                    if (integer == 60) {
                                        this.mDefaultFps = Utils.getArrayMaxMember(supportFps, integer);
                                    }
                                    writeLocalLog("setScreenEffect enable blacklist");
                                    DisplayFeatureManager.getInstance().setScreenEffect(24, 3, 256);
                                    return;
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            cursor2 = cursorQuery2;
                            if (cursor2 != null) {
                                cursor2.close();
                            }
                            throw th;
                        }
                    }
                }
                if (cursorQuery2 != null) {
                    cursorQuery2.close();
                }
            } catch (Exception e3) {
                e = e3;
                cursorQuery2 = null;
            } catch (Throwable th2) {
                th = th2;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        }
        supportFps = getSupportFps();
        if (str2 != null || str2.isEmpty()) {
            writeLocalLog("parseTopDefineAppFromDb empty");
            if (this.mIsWhitelistPolicy) {
                this.mDefaultFps = 60;
                writeLocalLog("setScreenEffect enable whitelist");
                DisplayFeatureManager.getInstance().setScreenEffect(24, 2, 256);
                return;
            } else {
                if (this.mIsWhiteFpsList) {
                    this.mDefaultFps = 60;
                    writeLocalLog("mIsWhiteFpsList = true ");
                    return;
                }
                integer = FeatureParser.getInteger("defaultFps", 60);
                this.mDefaultFps = integer;
                if (integer == 60) {
                    this.mDefaultFps = Utils.getArrayMaxMember(supportFps, integer);
                }
                writeLocalLog("setScreenEffect enable blacklist");
                DisplayFeatureManager.getInstance().setScreenEffect(24, 3, 256);
                return;
            }
        }
        ArraySet<String> installedThirdPartApps = arraySet == null ? PowerKeeperPackageManager.getInstalledThirdPartApps() : arraySet;
        try {
            JSONObject jSONObject = new JSONObject(str2);
            this.mIsWhitelistPolicy = jSONObject.optString(PowerCheckerCloudConfigure.POLICY_ITEM, "blacklist").equals("whitelist");
            boolean zOptBoolean = jSONObject.optBoolean("isWhiteList", false);
            this.mIsWhiteFpsList = zOptBoolean;
            if (this.mIsWhitelistPolicy) {
                this.mDefaultFps = 60;
                writeLocalLog("setScreenEffect enable whitelist");
                DisplayFeatureManager.getInstance().setScreenEffect(24, 2, 256);
            } else if (zOptBoolean) {
                writeLocalLog("mIsWhiteFpsList = true ");
                this.mDefaultFps = 60;
            } else {
                int integer2 = FeatureParser.getInteger("defaultFps", 60);
                this.mDefaultFps = integer2;
                if (integer2 == 60) {
                    this.mDefaultFps = Utils.getArrayMaxMember(supportFps, integer2);
                }
                writeLocalLog("setScreenEffect enable blacklist");
                DisplayFeatureManager.getInstance().setScreenEffect(24, 3, 256);
            }
            if (60 != this.mDefaultFps) {
                String strOptString = jSONObject.optString("list60", "");
                if (strOptString.isEmpty()) {
                    str3 = str2;
                    str5 = "power.systemui";
                    z2 = zContainsKey2;
                } else {
                    String[] strArrSplit = strOptString.split(",");
                    int i3 = 0;
                    while (i3 < strArrSplit.length) {
                        ArraySet<String> arraySet3 = installedThirdPartApps;
                        if (isInstalledPackage(strArrSplit[i3], arraySet3)) {
                            z2 = zContainsKey2;
                            try {
                                this.mTopDefineApps.put(strArrSplit[i3], 60);
                            } catch (JSONException e4) {
                                e = e4;
                                str5 = str5;
                                writeLocalLog("parseTopDefineAppFromDb e=" + e);
                                zContainsKey = this.mTopDefineApps.containsKey(str5);
                                if (z2 != zContainsKey) {
                                    this.mSystemUiDefineChange = true;
                                    if (this.mParseInited) {
                                        unregisterReceiver();
                                    }
                                    registerReceiver(zContainsKey);
                                }
                            }
                        } else {
                            z2 = zContainsKey2;
                        }
                        try {
                            i3++;
                            installedThirdPartApps = arraySet3;
                            zContainsKey2 = z2;
                            str5 = str5;
                        } catch (JSONException e5) {
                            e = e5;
                            writeLocalLog("parseTopDefineAppFromDb e=" + e);
                            zContainsKey = this.mTopDefineApps.containsKey(str5);
                            if (z2 != zContainsKey) {
                                this.mSystemUiDefineChange = true;
                                if (this.mParseInited) {
                                    unregisterReceiver();
                                }
                                registerReceiver(zContainsKey);
                            }
                        }
                    }
                    str5 = str5;
                    z2 = zContainsKey2;
                    ArraySet<String> arraySet4 = installedThirdPartApps;
                    if ("true".equals(mIsCustomFpsSwitch) && 1 == Settings.System.getInt(this.mContext.getContentResolver(), "is_update_database_top", 1)) {
                        int i4 = 0;
                        while (i4 < strArrSplit.length) {
                            if (isInstalledPackage(strArrSplit[i4], arraySet4)) {
                                try {
                                    Uri uri2 = HighRefreshRateConfigure.CONTENT_URI;
                                    ContentValues contentValues = new ContentValues();
                                    Context context = this.mContext;
                                    UserHandle userHandle = UserHandle.OWNER;
                                    cursorQuery = context.getContentResolverForUser(userHandle).query(uri2, null, "package_name = ?", new String[]{strArrSplit[i4]}, null);
                                    try {
                                        try {
                                            if (cursorQuery.moveToNext()) {
                                                str4 = str2;
                                                arraySet2 = arraySet4;
                                            } else {
                                                str4 = str2;
                                                try {
                                                    arraySet2 = arraySet4;
                                                    try {
                                                        if (!strArrSplit[i4].equals("com.android.camera")) {
                                                            contentValues.put("package_name", strArrSplit[i4]);
                                                            this.mContext.getContentResolverForUser(userHandle).insert(uri2, contentValues);
                                                            this.mCustomApps.put(strArrSplit[i4], 60);
                                                        }
                                                    } catch (Exception e6) {
                                                        e = e6;
                                                        Log.e(TAG, "parseTopDefineAppFromDb", e);
                                                        if (cursorQuery != null) {
                                                        }
                                                        i4++;
                                                        str2 = str4;
                                                        arraySet4 = arraySet2;
                                                    }
                                                } catch (Exception e7) {
                                                    e = e7;
                                                    arraySet2 = arraySet4;
                                                    Log.e(TAG, "parseTopDefineAppFromDb", e);
                                                    if (cursorQuery != null) {
                                                        cursorQuery.close();
                                                    }
                                                    i4++;
                                                    str2 = str4;
                                                    arraySet4 = arraySet2;
                                                }
                                            }
                                        } catch (Exception e8) {
                                            e = e8;
                                            str4 = str2;
                                        }
                                    } catch (Throwable th3) {
                                        th = th3;
                                        cursor = cursorQuery;
                                        if (cursor != null) {
                                            cursor.close();
                                        }
                                        throw th;
                                    }
                                } catch (Exception e9) {
                                    e = e9;
                                    str4 = str2;
                                    arraySet2 = arraySet4;
                                    cursorQuery = null;
                                } catch (Throwable th4) {
                                    th = th4;
                                    cursor = null;
                                }
                                cursorQuery.close();
                            } else {
                                str4 = str2;
                                arraySet2 = arraySet4;
                            }
                            i4++;
                            str2 = str4;
                            arraySet4 = arraySet2;
                        }
                        str3 = str2;
                        installedThirdPartApps = arraySet4;
                        Settings.System.putInt(this.mContext.getContentResolver(), "is_update_database_top", 0);
                    } else {
                        str3 = str2;
                        installedThirdPartApps = arraySet4;
                    }
                }
            } else {
                str3 = str2;
                str5 = "power.systemui";
                z2 = zContainsKey2;
            }
            int suitableFps = getSuitableFps(supportFps, 90);
            if (suitableFps != this.mDefaultFps) {
                String strOptString2 = jSONObject.optString("list90", "");
                if (!strOptString2.isEmpty()) {
                    String[] strArrSplit2 = strOptString2.split(",");
                    int i5 = 0;
                    while (i5 < strArrSplit2.length) {
                        ArraySet<String> arraySet5 = installedThirdPartApps;
                        if (isInstalledPackage(strArrSplit2[i5], arraySet5)) {
                            this.mTopDefineApps.put(strArrSplit2[i5], Integer.valueOf(suitableFps));
                        }
                        i5++;
                        installedThirdPartApps = arraySet5;
                    }
                }
            }
            ArraySet<String> arraySet6 = installedThirdPartApps;
            int suitableFps2 = getSuitableFps(supportFps, 120);
            if (suitableFps2 != this.mDefaultFps) {
                String strOptString3 = jSONObject.optString("list120", "");
                if (!strOptString3.isEmpty()) {
                    String[] strArrSplit3 = strOptString3.split(",");
                    for (int i6 = 0; i6 < strArrSplit3.length; i6++) {
                        if (isInstalledPackage(strArrSplit3[i6], arraySet6)) {
                            this.mTopDefineApps.put(strArrSplit3[i6], Integer.valueOf(suitableFps2));
                        }
                    }
                }
            }
            this.mVideoGameSwitch = jSONObject.optBoolean("videoGameSwitch", false);
            if (this.IS_ENABLE_SMART_DFPS) {
                if (jSONObject.optBoolean("isSmart", true)) {
                    writeLocalLog("setScreenEffect smart enable");
                    DisplayFeatureManager.getInstance().setScreenEffect(29, 1);
                } else {
                    writeLocalLog("setScreenEffect smart disable");
                    DisplayFeatureManager.getInstance().setScreenEffect(29, 0);
                }
            }
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "parseTopDefineAppFromDb mTopDefineApps=" + this.mTopDefineApps + " appJsonStr=" + str3);
            }
        } catch (JSONException e10) {
            e = e10;
            str5 = str5;
            z2 = zContainsKey2;
        }
        zContainsKey = this.mTopDefineApps.containsKey(str5);
        if (z2 != zContainsKey) {
            this.mSystemUiDefineChange = true;
            if (this.mParseInited) {
                unregisterReceiver();
            }
            registerReceiver(zContainsKey);
        }
    }

    private void parseTopShortVideoAppFromDb(String str, ArraySet<String> arraySet) {
        String[] strArr;
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f29k;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_FPS_TOP_SHORT_VIDEO_PKG, null);
                writeLocalLog("parseTopShortVideoAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_FPS_TOP_SHORT_VIDEO_PKG, null);
                writeLocalLog("parseTopShortVideoAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseTopShortVideoAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mShortVideoSwitch = jSONObject.optBoolean("shortVideoSwitch", true);
            writeLocalLog("parseTopShortVideoAppFromDb mShortVideoSwitch=" + this.mShortVideoSwitch);
            if (this.mShortVideoSwitch) {
                String[] strArrSplit = jSONObject.optString("pkgName", "").split(",");
                this.mWhitelistActivity = jSONObject.optString("whitelistAtyName", "").split(",");
                writeLocalLog("parseTopShortVideoAppFromDb pkgs=" + Arrays.toString(strArrSplit) + "mWhitelistActivity=" + Arrays.toString(this.mWhitelistActivity));
                if (strArrSplit != null) {
                    for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                        if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                            Collections.addAll(mShortVideoApps, strArrSplit[i3]);
                        }
                    }
                    writeLocalLog("parseTopShortVideoAppFromDb mShortVideoApps=" + mShortVideoApps);
                }
                if (mShortVideoApps.size() > 0 && (strArr = this.mWhitelistActivity) != null) {
                    Collections.addAll(mCameraActivityList, strArr);
                    writeLocalLog("parseCameraIdleAppFromDb mCameraActivityList=" + mCameraActivityList);
                }
            }
            registerActivityChangeListener();
        } catch (JSONException e2) {
            writeLocalLog("parseInputAppFromDb e=" + e2);
        }
    }

    private void parseTopSmartAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f30l;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_FPS_SMART_GROUP, null);
                writeLocalLog("parseTopSmartAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_FPS_SMART_GROUP, null);
                writeLocalLog("parseTopSmartAppFromDb new cloud = " + str);
            }
        }
        this.mTopSmartApps.clear();
        int[] supportFps = getSupportFps();
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseTopSmartAppFromDb empty");
            if (this.mIsWhiteFpsList) {
                writeLocalLog("mIsWhiteFpsList = true ");
                this.mDefaultFps = 60;
                return;
            }
            int integer = FeatureParser.getInteger("defaultFps", 60);
            this.mDefaultFps = integer;
            if (integer == 60) {
                this.mDefaultFps = Utils.getArrayMaxMember(supportFps, integer);
            }
            writeLocalLog("setScreenEffect enable blacklist");
            DisplayFeatureManager.getInstance().setScreenEffect(24, 3, 256);
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (this.mIsWhiteFpsList) {
                writeLocalLog("mIsWhiteFpsList = true ");
                this.mDefaultFps = 60;
            } else {
                int integer2 = FeatureParser.getInteger("defaultFps", 60);
                this.mDefaultFps = integer2;
                if (integer2 == 60) {
                    this.mDefaultFps = Utils.getArrayMaxMember(supportFps, integer2);
                }
            }
            writeLocalLog("setScreenEffect enable blacklist");
            DisplayFeatureManager.getInstance().setScreenEffect(24, 3, 256);
            if (60 != this.mDefaultFps) {
                String strOptString = jSONObject.optString("list60", "");
                if (!strOptString.isEmpty()) {
                    String[] strArrSplit = strOptString.split(",");
                    for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                        if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                            this.mTopSmartApps.put(strArrSplit[i3], 60);
                        }
                    }
                }
            }
            int suitableFps = getSuitableFps(supportFps, 90);
            if (suitableFps != this.mDefaultFps) {
                String strOptString2 = jSONObject.optString("list90", "");
                if (!strOptString2.isEmpty()) {
                    String[] strArrSplit2 = strOptString2.split(",");
                    for (int i4 = 0; i4 < strArrSplit2.length; i4++) {
                        if (isInstalledPackage(strArrSplit2[i4], arraySet)) {
                            this.mTopSmartApps.put(strArrSplit2[i4], Integer.valueOf(suitableFps));
                        }
                    }
                }
            }
            int suitableFps2 = getSuitableFps(supportFps, 120);
            if (suitableFps2 != this.mDefaultFps) {
                String strOptString3 = jSONObject.optString("list120", "");
                if (!strOptString3.isEmpty()) {
                    String[] strArrSplit3 = strOptString3.split(",");
                    for (int i5 = 0; i5 < strArrSplit3.length; i5++) {
                        if (isInstalledPackage(strArrSplit3[i5], arraySet)) {
                            this.mTopSmartApps.put(strArrSplit3[i5], Integer.valueOf(suitableFps2));
                        }
                    }
                }
            }
            this.mVideoGameSwitch = jSONObject.optBoolean("videoGameSwitch", false);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "parseTopSmartAppFromDb mTopSmartApps=" + this.mTopSmartApps + " appJsonStr=" + str);
            }
        } catch (JSONException e2) {
            writeLocalLog("parseTopDefaultAppFromDb e=" + e2);
        }
    }

    private void parseTopVideoAppFromDb(String str, ArraySet<String> arraySet) throws Throwable {
        String str2;
        Cursor cursor;
        Cursor cursorQuery;
        String string;
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f30l;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                string = SimpleSettings.Misc.getString(this.mContext, CLOUD_FPS_TOP_VIDEO_PKG, null);
                writeLocalLog("parseTopVideoAppFromDb old cloud or local config = " + string);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                string = jSONObjectL.optString(CLOUD_FPS_TOP_VIDEO_PKG, null);
                writeLocalLog("parseTopVideoAppFromDb new cloud = " + string);
            }
            str2 = string;
        } else {
            str2 = str;
        }
        if (str2 == null || str2.isEmpty()) {
            writeLocalLog("parseTopVideoAppFromDb empty");
            return;
        }
        ArraySet<String> installedThirdPartApps = arraySet == null ? PowerKeeperPackageManager.getInstalledThirdPartApps() : arraySet;
        String[] strArrSplit = str2.split(",");
        for (int i3 = 0; i3 < strArrSplit.length; i3++) {
            if (isInstalledPackage(strArrSplit[i3], installedThirdPartApps)) {
                this.mVideoApps.add(strArrSplit[i3]);
            }
        }
        if ("true".equals(mIsCustomFpsSwitch) && 1 == Settings.System.getInt(this.mContext.getContentResolver(), "is_update_database_video", 1)) {
            for (int i4 = 0; i4 < strArrSplit.length; i4++) {
                if (isInstalledPackage(strArrSplit[i4], installedThirdPartApps)) {
                    try {
                        Uri uri = HighRefreshRateConfigure.CONTENT_URI;
                        ContentValues contentValues = new ContentValues();
                        Context context = this.mContext;
                        UserHandle userHandle = UserHandle.OWNER;
                        cursorQuery = context.getContentResolverForUser(userHandle).query(uri, null, "package_name = ?", new String[]{strArrSplit[i4]}, null);
                        try {
                            try {
                                if (!cursorQuery.moveToNext()) {
                                    contentValues.put("package_name", strArrSplit[i4]);
                                    this.mContext.getContentResolverForUser(userHandle).insert(uri, contentValues);
                                    this.mCustomApps.put(strArrSplit[i4], 60);
                                }
                            } catch (Throwable th) {
                                th = th;
                                cursor = cursorQuery;
                                if (cursor != null) {
                                    cursor.close();
                                }
                                throw th;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            Log.e(TAG, "parseTopVideoAppFromDb", e);
                            if (cursorQuery != null) {
                            }
                        }
                    } catch (Exception e3) {
                        e = e3;
                        cursorQuery = null;
                    } catch (Throwable th2) {
                        th = th2;
                        cursor = null;
                    }
                    cursorQuery.close();
                }
            }
            Settings.System.putInt(this.mContext.getContentResolver(), "is_update_database_video", 0);
        }
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "parseTopVideoAppFromDb mVideoApps=" + this.mVideoApps + " appJsonStr=" + str2);
        }
    }

    private void parseTopVideoIdleAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f29k;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_FPS_TOP_VIDEO_IDLE_PKG, null);
                writeLocalLog("parseTopVideoIdleAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_FPS_TOP_VIDEO_IDLE_PKG, null);
                writeLocalLog("parseTopVideoIdleAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseTopVideoIdleAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        String[] strArrSplit = str.split(",");
        for (int i3 = 0; i3 < strArrSplit.length; i3++) {
            if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                this.mVideoIdleApps.add(strArrSplit[i3]);
            }
        }
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "parseTopVideoIdleAppFromDb mVideoIdleApps=" + this.mVideoIdleApps + " appJsonStr=" + str);
        }
    }

    private void parseTpIdleAppFromDb(String str, ArraySet<String> arraySet) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f26h;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_TP_IDLE_GROUP, null);
                writeLocalLog("parseTpIdleAppFromDb old cloud or local config = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.optString(CLOUD_TP_IDLE_GROUP, null);
                writeLocalLog("parseTpIdleAppFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseTpIdleAppFromDb empty");
            return;
        }
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        String[] strArrSplit = str.split(",");
        for (int i3 = 0; i3 < strArrSplit.length; i3++) {
            if (isInstalledPackage(strArrSplit[i3], arraySet)) {
                this.mTpidleApps.add(strArrSplit[i3]);
            }
        }
        writeLocalLog("parseTpIdleAppFromDb mTpidleApps=" + this.mTpidleApps);
    }

    private void parseViolentModeSwitchFromDb(String str) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.f34p;
            JSONObject jSONObjectL = aVar.l(i2);
            if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.FALSE);
                str = SimpleSettings.Misc.getString(this.mContext, CLOUD_VIOLENT_MODE_SWITCH, null);
                writeLocalLog("parseViolentModeSwitchFromDb old cloud = " + str);
            } else {
                mCloudControlStatus.put(b.b.X.get(i2), Boolean.TRUE);
                str = jSONObjectL.toString();
                writeLocalLog("parseViolentModeSwitchFromDb new cloud = " + str);
            }
        }
        if (str == null || str.isEmpty()) {
            writeLocalLog("parseViolentModeSwitchFromDb empty");
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mViolentModeSwitch = jSONObject.optBoolean("fucSwitch", false);
            this.mIdleSceneSwitch = jSONObject.optBoolean("idleSwitch", false);
            writeLocalLog("parseViolentModeSwitchFromDb mViolentModeSwitch= " + this.mViolentModeSwitch + " mIdleSceneSwitch= " + this.mIdleSceneSwitch);
        } catch (JSONException e2) {
            writeLocalLog("parseViolentModeSwitchFromDb e=" + e2);
        }
    }

    private void processParam(int i2, String[] strArr) {
        String[] strArrSplit = strArr[i2].split(",");
        int i3 = 0;
        int i4 = Utils.toInt(strArrSplit[0], -1);
        StringBuilder sb = new StringBuilder(128);
        sb.append("config=");
        sb.append(i4);
        sb.append(" values=");
        int length = strArrSplit.length - 1;
        int[] iArr = new int[length];
        while (i3 < length) {
            int i5 = i3 + 1;
            int i6 = Utils.toInt(strArrSplit[i5], -1);
            iArr[i3] = i6;
            sb.append(i6);
            sb.append(" ");
            i3 = i5;
        }
        int iIrisConfigureSet = IrisServiceUtils.getInstance(this.mContext).irisConfigureSet(i4, iArr);
        sb.append(" ret=");
        sb.append(iIrisConfigureSet);
        writeLocalLog(sb.toString());
    }

    private void recentTaskToHome() {
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "recentTaskToHome");
        }
        this.mIsDuringGesture = false;
        writeLocalLog("recentTaskToHome mIsDuringGesture= " + this.mIsDuringGesture);
        onForegroundChanged(this.mCurrentFgInfo);
    }

    private void registerActivityChangeListener() {
        if (this.mActListener != null) {
            writeLocalLog("mActListener != null");
            ProcessManager.unregisterActivityChanageListener(this.mActListener);
            ProcessManager.registerActivityChangeListener(mLowFpsPkgList, mCameraActivityList, this.mActListener);
        }
        if (!this.IS_SUPPORTED_IRIS || this.mVideoActListener == null) {
            return;
        }
        writeLocalLog("mVideoActListener != null");
        ProcessManager.unregisterActivityChanageListener(this.mVideoActListener);
        ProcessManager.registerActivityChangeListener((List) null, mExtremeVideoActivityList, this.mVideoActListener);
    }

    private void registerCloudObserver() {
        this.mCloudObserver.t(b.b.f25g, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.13
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                String string;
                DisplayFrameSetting.this.writeLocalLog("Cloud-Observer onChange ID_CUSTOM_SWITCH");
                if (jSONObject != null) {
                    DisplayFrameSetting.mCloudControlStatus.put(b.b.X.get(b.b.f25g), Boolean.TRUE);
                    string = jSONObject.toString();
                } else {
                    string = null;
                }
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(37, string));
            }
        });
        this.mCloudObserver.t(b.b.f26h, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.14
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                String strOptString;
                String strOptString2;
                DisplayFrameSetting.this.writeLocalLog("Cloud-Observer onChange ID_FPS_TP_EXCLUDE");
                String str = null;
                if (jSONObject != null) {
                    DisplayFrameSetting.mCloudControlStatus.put(b.b.X.get(b.b.f26h), Boolean.TRUE);
                    String strOptString3 = jSONObject.optString(DisplayFrameSetting.CLOUD_FOSS_GROUP, null);
                    strOptString2 = jSONObject.optString(DisplayFrameSetting.CLOUD_FPS_EXCLUDE_PKG, null);
                    strOptString = jSONObject.optString(DisplayFrameSetting.CLOUD_TP_IDLE_GROUP, null);
                    str = strOptString3;
                } else {
                    strOptString = null;
                    strOptString2 = null;
                }
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(41, str));
                DisplayFrameSetting displayFrameSetting2 = DisplayFrameSetting.this;
                displayFrameSetting2.sendMessage(displayFrameSetting2.obtainMessage(9, strOptString2));
                DisplayFrameSetting displayFrameSetting3 = DisplayFrameSetting.this;
                displayFrameSetting3.sendMessage(displayFrameSetting3.obtainMessage(24, strOptString));
            }
        });
        this.mCloudObserver.t(b.b.f27i, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.15
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                String string;
                DisplayFrameSetting.this.writeLocalLog("Cloud-Observer onChange ID_NOTE_PKG");
                if (jSONObject != null) {
                    DisplayFrameSetting.mCloudControlStatus.put(b.b.X.get(b.b.f27i), Boolean.TRUE);
                    string = jSONObject.toString();
                } else {
                    string = null;
                }
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(42, string));
            }
        });
        this.mCloudObserver.t(b.b.f28j, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.16
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                DisplayFrameSetting.this.writeLocalLog("Cloud-Observer onChange ID_DISPLAY_FPS_GROUP");
                String strOptString = null;
                if (jSONObject != null) {
                    DisplayFrameSetting.mCloudControlStatus.put(b.b.X.get(b.b.f28j), Boolean.TRUE);
                    strOptString = jSONObject.optString(DisplayFrameSetting.CLOUD_DISPLAY_FPS_GROUP, null);
                }
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(35, strOptString));
            }
        });
        this.mCloudObserver.t(b.b.f29k, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.17
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                String strOptString;
                String strOptString2;
                String strOptString3;
                String strOptString4;
                String strOptString5;
                String strOptString6;
                DisplayFrameSetting.this.writeLocalLog("Cloud-Observer onChange ID_FPS_IDLE_CONFIG");
                String str = null;
                if (jSONObject != null) {
                    DisplayFrameSetting.mCloudControlStatus.put(b.b.X.get(b.b.f29k), Boolean.TRUE);
                    String strOptString7 = jSONObject.optString(DisplayFrameSetting.CLOUD_FPS_TOP_SHORT_VIDEO_PKG, null);
                    strOptString2 = jSONObject.optString(DisplayFrameSetting.CLOUD_FPS_TOP_VIDEO_IDLE_PKG, null);
                    strOptString3 = jSONObject.optString(DisplayFrameSetting.CLOUD_FULL_SCREEN_GROUP, null);
                    strOptString4 = jSONObject.optString(DisplayFrameSetting.CLOUD_MAP_FPS_GROUP, null);
                    strOptString5 = jSONObject.optString(DisplayFrameSetting.CLOUD_AUTOMODE_VRR_GROUP, null);
                    strOptString6 = jSONObject.optString(DisplayFrameSetting.CLOUD_PROMOTION_GROUP, null);
                    strOptString = jSONObject.optString(DisplayFrameSetting.CLOUD_EBOOK_IDLE_GROUP, null);
                    str = strOptString7;
                } else {
                    strOptString = null;
                    strOptString2 = null;
                    strOptString3 = null;
                    strOptString4 = null;
                    strOptString5 = null;
                    strOptString6 = null;
                }
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(30, str));
                DisplayFrameSetting displayFrameSetting2 = DisplayFrameSetting.this;
                displayFrameSetting2.sendMessage(displayFrameSetting2.obtainMessage(32, strOptString2));
                DisplayFrameSetting displayFrameSetting3 = DisplayFrameSetting.this;
                displayFrameSetting3.sendMessage(displayFrameSetting3.obtainMessage(36, strOptString3));
                DisplayFrameSetting displayFrameSetting4 = DisplayFrameSetting.this;
                displayFrameSetting4.sendMessage(displayFrameSetting4.obtainMessage(38, strOptString4));
                DisplayFrameSetting displayFrameSetting5 = DisplayFrameSetting.this;
                displayFrameSetting5.sendMessage(displayFrameSetting5.obtainMessage(39, strOptString5));
                DisplayFrameSetting displayFrameSetting6 = DisplayFrameSetting.this;
                displayFrameSetting6.sendMessage(displayFrameSetting6.obtainMessage(34, strOptString6));
                DisplayFrameSetting displayFrameSetting7 = DisplayFrameSetting.this;
                displayFrameSetting7.sendMessage(displayFrameSetting7.obtainMessage(23, strOptString));
            }
        });
        this.mCloudObserver.t(b.b.f30l, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.18
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                String strOptString;
                String strOptString2;
                String strOptString3;
                String strOptString4;
                String strOptString5;
                DisplayFrameSetting.this.writeLocalLog("Cloud-Observer onChange ID_FPS_GROUP_CONFIG");
                String str = null;
                if (jSONObject != null) {
                    DisplayFrameSetting.mCloudControlStatus.put(b.b.X.get(b.b.f30l), Boolean.TRUE);
                    strOptString2 = jSONObject.optString(DisplayFrameSetting.CLOUD_FPS_SMART_GROUP, null);
                    String strOptString6 = jSONObject.optString(DisplayFrameSetting.CLOUD_FPS_DEFINE_GROUP, null);
                    strOptString3 = jSONObject.optString(DisplayFrameSetting.CLOUD_FPS_TOP_VIDEO_PKG, null);
                    strOptString4 = jSONObject.optString(DisplayFrameSetting.CLOUD_LOW_FPS_GROUP, null);
                    strOptString5 = jSONObject.optString(DisplayFrameSetting.CLOUD_INPUT_AUDIO_GROUP, null);
                    strOptString = jSONObject.optString(DisplayFrameSetting.CLOUD_HIGHEST_FPS_GROUP, null);
                    str = strOptString6;
                } else {
                    strOptString = null;
                    strOptString2 = null;
                    strOptString3 = null;
                    strOptString4 = null;
                    strOptString5 = null;
                }
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(4, str));
                DisplayFrameSetting displayFrameSetting2 = DisplayFrameSetting.this;
                displayFrameSetting2.sendMessage(displayFrameSetting2.obtainMessage(25, strOptString2));
                DisplayFrameSetting displayFrameSetting3 = DisplayFrameSetting.this;
                displayFrameSetting3.sendMessage(displayFrameSetting3.obtainMessage(5, strOptString3));
                DisplayFrameSetting displayFrameSetting4 = DisplayFrameSetting.this;
                displayFrameSetting4.sendMessage(displayFrameSetting4.obtainMessage(28, strOptString4));
                DisplayFrameSetting displayFrameSetting5 = DisplayFrameSetting.this;
                displayFrameSetting5.sendMessage(displayFrameSetting5.obtainMessage(27, strOptString5));
                DisplayFrameSetting displayFrameSetting6 = DisplayFrameSetting.this;
                displayFrameSetting6.sendMessage(displayFrameSetting6.obtainMessage(50, strOptString));
            }
        });
        this.mCloudObserver.t(b.b.f32n, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.19
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                String string;
                DisplayFrameSetting.this.writeLocalLog("Cloud-Observer onChange ID_SMART_BACKLIGHT_MODE");
                if (jSONObject != null) {
                    DisplayFrameSetting.mCloudControlStatus.put(b.b.X.get(b.b.f32n), Boolean.TRUE);
                    string = jSONObject.toString();
                } else {
                    string = null;
                }
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(21, string));
            }
        });
        this.mCloudObserver.t(b.b.f33o, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.20
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                String string;
                DisplayFrameSetting.this.writeLocalLog("Cloud-Observer onChange ID_EYE_PROTECTION_MODE");
                if (jSONObject != null) {
                    DisplayFrameSetting.mCloudControlStatus.put(b.b.X.get(b.b.f33o), Boolean.TRUE);
                    string = jSONObject.toString();
                } else {
                    string = null;
                }
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(20, string));
            }
        });
        this.mCloudObserver.t(b.b.f34p, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.21
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                String string;
                DisplayFrameSetting.this.writeLocalLog("Cloud-Observer onChange ID_VIOLENT_MODE_SWITCH");
                if (jSONObject != null) {
                    DisplayFrameSetting.mCloudControlStatus.put(b.b.X.get(b.b.f34p), Boolean.TRUE);
                    string = jSONObject.toString();
                } else {
                    string = null;
                }
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(40, string));
            }
        });
        this.mCloudObserver.t(b.b.J, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayFrameSetting.22
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                DisplayFrameSetting.this.writeLocalLog("Cloud-Observer onChange ID_IRIS_CONFIG");
                String string = jSONObject != null ? jSONObject.toString() : null;
                DisplayFrameSetting displayFrameSetting = DisplayFrameSetting.this;
                displayFrameSetting.sendMessage(displayFrameSetting.obtainMessage(43, string));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerDisplayScrollListener() {
        try {
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager != null && this.mLowFpsSwitch) {
                iWhetstoneActivityManager.registerDisplayScroll(this.mDsListener);
            }
        } catch (RemoteException unused) {
            Log.e(TAG, "IWhetstoneActivityManager work abnormal");
        }
    }

    private void registerReceiver(boolean z2) {
        if (this.mIsReciverRegistered) {
            return;
        }
        writeLocalLog("registerReceiver " + z2);
        IntentFilter intentFilter = new IntentFilter("com.miui.securitycenter.intent.action.TOP_GAME_LIST");
        intentFilter.addAction("intent.action.TOP_GAME_LIST");
        intentFilter.addAction("com.xiaomi.joyose.HIGH_FPS_LIST");
        intentFilter.addAction(ScenarioConfig.ACTION_CAMERA_STATE);
        intentFilter.addAction("com.miui.powerkeeper.set_fps");
        if (z2) {
            intentFilter.addAction("com.miui.powerkeeper.RECENT_TASK");
        }
        intentFilter.addAction("com.xiaomi.joyose.OVERRIDE_GAME_FRESHRATE");
        intentFilter.addAction("com.miui.powerkeeper.SET_ACTIVITY_FPS");
        this.mContext.registerReceiver(this.mReceiver, intentFilter, 2);
        this.mIsReciverRegistered = true;
    }

    private void registerVideoFpsCallBack() {
        try {
            Log.i(TAG, MiPushClient.COMMAND_REGISTER);
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager == null) {
                Log.e(TAG, "mWs = null");
            } else {
                iWhetstoneActivityManager.registVideoFpsCallBack(this.mVideoFpsCallBack);
            }
        } catch (RemoteException unused) {
            Log.e(TAG, "IWhetstoneActivityManager work abnormal");
        }
    }

    public static boolean setFpsAync(String str, int i2) {
        return setFpsAync(str, i2, 0);
    }

    private void setFpsAyncInternal(String str, int i2, int i3) {
        sendMessageDelayed(obtainMessage(15, i2, -1, str), i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFpsList() {
        int i2;
        if (mIsSmart) {
            return;
        }
        if ("true".equals(mIsCustomFpsSwitch) && ((i2 = this.mUserFps) == 120 || i2 == 144)) {
            this.mTopApps = this.mCustomApps;
            this.mIsCustom = true;
        } else {
            this.mTopApps = this.mTopDefineApps;
            this.mIsCustom = false;
        }
    }

    private void setScreenEffectInternal(int i2, int i3, String str) {
        if (this.FPS_SWITCH_DEFAULT) {
            if (this.mCurrentFps != i2) {
                Settings.Secure.putInt(this.mContext.getContentResolver(), "miui_refresh_rate", i2);
            }
            if (this.mCurrentCookie != i3) {
                wifiLogControl(i3);
                DisplayFeatureManager.getInstance().setScreenEffect(24, i2, i3);
                if (this.IS_ENABLE_SMART_DFPS && this.IS_ENABLE_CONTENT_DETECTION) {
                    notifySFDfpsMode(i3, i2, str);
                    writeLocalLog("WhitelistPolicy notifySFDfpsMode run cookie=" + i3 + "fps=" + i2 + "pkg=" + str);
                }
            }
        } else {
            DisplayFeatureManager.getInstance().setScreenEffect(24, i2, i3);
        }
        this.mCurrentFps = i2;
        this.mCurrentCookie = i3;
    }

    private void unRegisterVideoFpsCallBack() {
        try {
            Log.i(TAG, MiPushClient.COMMAND_UNREGISTER);
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager == null) {
                Log.e(TAG, "mWs = null");
            } else {
                iWhetstoneActivityManager.unRegistVideoFpsCallBack(this.mVideoFpsCallBack);
            }
        } catch (RemoteException unused) {
            Log.e(TAG, "IWhetstoneActivityManager work abnormal");
        }
    }

    private void unregisterDisplayScrollListener() {
        try {
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager == null) {
                return;
            }
            iWhetstoneActivityManager.unregisterDisplayScroll(this.mDsListener);
        } catch (RemoteException unused) {
            Log.e(TAG, "IWhetstoneActivityManager work abnormal");
        }
    }

    private void unregisterReceiver() {
        BroadcastReceiver broadcastReceiver = this.mReceiver;
        if (broadcastReceiver != null || this.mIsReciverRegistered) {
            this.mIsReciverRegistered = false;
            this.mContext.unregisterReceiver(broadcastReceiver);
            if (!this.mSystemUiDefineChange) {
                this.mReceiver = null;
                return;
            }
            writeLocalLog("mSystemUiDefineChange " + this.mSystemUiDefineChange);
            this.mSystemUiDefineChange = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCustomDataBase(String str) {
        if ("true".equals(mIsCustomFpsSwitch)) {
            String[] strArrSplit = str.split(",");
            if ("0".equals(strArrSplit[1])) {
                writeLocalLog("add pkg = " + strArrSplit[0]);
                this.mCustomApps.put(strArrSplit[0], 60);
            } else {
                writeLocalLog("remove pkg = " + strArrSplit[0]);
                this.mCustomApps.remove(strArrSplit[0]);
            }
            this.mTopApps = this.mCustomApps;
        }
    }

    private void updateIrisState(String str, boolean z2) {
        if (z2) {
            int iIndexOf = mExtremeVideoPackageList.indexOf(str);
            if (iIndexOf < 0) {
                return;
            }
            processParam(iIndexOf, this.mSdr2HdrParam);
            return;
        }
        int iIndexOf2 = mExtremeVideoActivityList.indexOf(str);
        if (iIndexOf2 < 0) {
            return;
        }
        processParam(iIndexOf2, this.mMemcParam);
        Settings.System.putInt(this.mContext.getContentResolver(), VIDEO_IRIS_STATUS, 1);
    }

    private void wifiLogControl(int i2) {
        if (Build.DEVICE.indexOf("pissarro") == -1) {
        }
        switch (i2) {
            case 244:
            case 248:
            case 249:
            case 251:
            case 252:
            case 254:
                try {
                    SystemProperties.set("debug.wifi-log-daemon", "running");
                } catch (Exception e2) {
                    writeLocalLog("wifiLogControl Properties.set e=" + e2);
                    return;
                }
                break;
            case 247:
                try {
                    SystemProperties.set("debug.wifi-log-daemon", "stopped");
                } catch (Exception e3) {
                    writeLocalLog("wifiLogControl Properties.set e=" + e3);
                    return;
                }
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeLocalLog(String str) {
        if (this.DBG_DISPLAY) {
            Log.i(TAG, str);
        }
        this.mLocalLog.log(str);
    }

    public void GameSceneChange(String str, int i2) {
        if (i2 != 0) {
            this.mIsEnterGameScene = false;
            onForegroundChanged(this.mCurrentFgInfo);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "Game Scene Change exit model=" + i2);
                return;
            }
            return;
        }
        this.mIsEnterGameScene = true;
        this.mGameVideoScenePkg = str;
        onForegroundChanged(this.mCurrentFgInfo);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "Game Scene Change enter model=" + i2);
        }
    }

    public void VideoSceneChange(String str, int i2) {
        if (i2 != 2) {
            this.mIsEnterVideoScene = false;
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "Video Scene Change exit model=" + i2);
                return;
            }
            return;
        }
        this.mIsEnterVideoScene = true;
        this.mGameVideoScenePkg = str;
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "Video Scene Change enter model=" + i2);
        }
    }

    void destroy() {
        Log.w(TAG, "destroy");
        this.mSystemUiDefineChange = false;
        unregisterReceiver();
        this.mAudioManager.unregisterAudioRecordingCallback(this.mAudioRecordingCallback);
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.unregisterForDockedStack(this);
            eventsAggregator.unregisterForPkgExistentState(this);
            eventsAggregator.unregisterForScreenOn(this);
            eventsAggregator.unregisterForScreenOff(this);
            eventsAggregator.unregisterForUserPresent(this);
            eventsAggregator.unregisterForForegroundInfo(this);
            eventsAggregator.unregisterForInputInfo(this);
            eventsAggregator.unregisterBatteryLevelListener(this);
            eventsAggregator.unregisterForCharging(this);
            eventsAggregator.unregisterPowerModeListener(this);
        }
        unRegisterVideoFpsCallBack();
        if (this.IS_SUPPORTED_IRIS) {
            ((DisplayManager) this.mContext.getSystemService("display")).unregisterDisplayListener(this.mDisplayListener);
        }
    }

    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("fpsconfig mConfigType=" + mConfigType);
        printWriter.println("fpsconfig mConfigVersion=" + this.mConfigVersion);
        printWriter.println("fpsconfig mPrivGames=" + this.mPrivGames);
        printWriter.println("fpsconfig mDefaultGameApp(60):" + this.mDefaultGameApp);
        printWriter.println("fpsconfig mTopApps=" + this.mTopApps);
        printWriter.println("fpsconfig mVideoApps(60):" + this.mVideoApps);
        printWriter.println("fpsconfig mExcludeApps=" + this.mExcludeApps);
        printWriter.println("fpsconfig mEbookidleApps=" + this.mEbookidleApps);
        printWriter.println("fpsconfig mTpidleApps=" + this.mTpidleApps);
        printWriter.println("fpsconfig mInputApps=" + this.mInputApps);
        printWriter.println("fpsconfig mLowFpsPkgList=" + mLowFpsPkgList);
        printWriter.println("fpsconfig mCameraActivityList=" + mCameraActivityList);
        printWriter.println("fpsconfig mShortVideoApps=" + mShortVideoApps);
        printWriter.println("fpsconfig mVideoIdleApps=" + this.mVideoIdleApps);
        printWriter.println("fpsconfig mPromotionApps=" + this.mPromotionApps);
        printWriter.println("fpsconfig mDisplayFpsApps=" + this.mDisplayFpsApps);
        printWriter.println("fpsconfig mCustomApps=" + this.mCustomApps);
        printWriter.println("fpsconfig mFullScreenFpsApps=" + this.mFullScreenFpsApps);
        printWriter.println("fpsconfig mVideoLowerFpsApps(24)=" + this.mVideoLowerFpsApps);
        printWriter.println("fpsconfig mMapFpsApps=" + this.mMapFpsApps);
        printWriter.println("fpsconfig mAutoModeApps=" + this.mAutoModeApps);
        printWriter.println("fpsconfig mVrrApps=" + this.mVrrApps);
        printWriter.println("fpsconfig mCloudControlStatus=" + mCloudControlStatus);
        printWriter.println("fpsconfig mExtremeVideoActivityList=" + mExtremeVideoActivityList);
        printWriter.println("fpsconfig mExtremeVideoPackageList=" + mExtremeVideoPackageList);
        printWriter.println("fpsconfig mIsWhiteFpsList=" + this.mIsWhiteFpsList);
        printWriter.println("fpsconfig mIsCustomFpsSwitch=" + mIsCustomFpsSwitch);
        printWriter.println("fpsconfig mIsCustom=" + this.mIsCustom);
        printWriter.println("fpsconfig mHighestFpsSwitch=" + this.mHighestFpsSwitch);
        printWriter.println("fpsconfig mDefineHighestFpsApps=" + this.mDefineHighestFpsApps);
        printWriter.println("fpsconfig mCustomHighestFpsApps=" + this.mCustomHighestFpsApps);
    }

    public int getCurrentFps() {
        return this.mCurrentFps;
    }

    public int getMaxFPS() {
        return Utils.getArrayMaxMember(getSupportFps(), this.mDefaultFps);
    }

    public int getUserFps() {
        int i2 = Settings.Secure.getInt(this.mContext.getContentResolver(), "user_refresh_rate", Settings.System.getInt(this.mContext.getContentResolver(), "user_refresh_rate", -1));
        if (i2 == -1) {
            return FeatureParser.getBoolean("support_smart_fps", false) ? FeatureParser.getInteger("smart_fps_value", 120) : FeatureParser.getInteger("defaultFps", 60);
        }
        return i2;
    }

    public int getmUserFps() {
        return this.mUserFps;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        boolean z2;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        int i2 = message.what;
        if (i2 == -25) {
            int i3 = message.arg1;
            if (i3 == 1) {
                if (!this.mInputFlag) {
                    notifySFIfpsMode(270, 1, this.mCurrentFgPkg);
                    this.mInputFlag = true;
                    if (this.DBG_DISPLAY) {
                        Log.d(TAG, "COMMON_MSG_INPUT_INFO_CHANGE inputStatus:" + message.arg1);
                    }
                }
            } else if (this.mInputFlag) {
                notifySFIfpsMode(270, 0, this.mCurrentFgPkg);
                this.mInputFlag = false;
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "COMMON_MSG_INPUT_INFO_CHANGE inputStatus:" + message.arg1);
                }
            }
            notifyINChange(i3);
            return;
        }
        if (i2 == -22) {
            this.mPowerMode = message.arg1;
            onPowerModeChanged();
            return;
        }
        if (i2 == -20) {
            int i4 = message.arg1;
            boolean z3 = i4 <= this.mCameraPowerSaveValue;
            if (!this.mCameraPowerSaveSwitch) {
                this.mCheckPowerSaveCamera = false;
            } else if (z3 != this.mCheckPowerSaveCamera) {
                this.mCheckPowerSaveCamera = z3;
                notifySFIChange();
            }
            z2 = i4 == 100;
            if (this.mLevelFullSwitch != z2) {
                this.mLevelFullSwitch = z2;
                notifySFIChange();
                return;
            }
            return;
        }
        if (i2 == 15) {
            String str7 = (String) message.obj;
            ForegroundInfo foregroundInfo = this.mCurrentFgInfo;
            if (foregroundInfo == null || (str = foregroundInfo.mForegroundPackageName) == null || !(str.equals(str7) || this.mCurrentCookie == 247)) {
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_UPDATE_FPS break for not in " + str7 + ", mCurrentCookie:" + this.mCurrentCookie);
                    return;
                }
                return;
            }
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "MSG_UPDATE_FPS app=" + str7 + " fps=" + message.arg1);
            }
            setScreenEffect(str7, getSuitableFps(getSupportFps(), message.arg1), hasMessages(17) ? this.mCurrentCookie : getPkgCookie(str7));
            return;
        }
        if (i2 == -9) {
            notifyFGChange((ForegroundInfo) message.obj);
            notifySFIChange();
            return;
        }
        if (i2 == -8) {
            z2 = ((Boolean) ((AsyncResult) message.obj).result).booleanValue();
            if (this.mChargingSwitch != z2) {
                this.mChargingSwitch = z2;
                notifySFIChange();
                return;
            }
            return;
        }
        if (i2 == -7) {
            ForegroundInfo foregroundInfo2 = PowerKeeperManager.getInstance().getEventsAggregator().getForegroundInfo();
            if (foregroundInfo2 == null || (str2 = foregroundInfo2.mForegroundPackageName) == null) {
                writeLocalLog("COMMON_MSG_USER_PRESENT break for fg is null !");
                return;
            }
            this.mIsLockScreen = false;
            if (!this.mIsMapFpsSwitch && this.mMapFpsApps.contains(str2)) {
                notifySFIfpsMode(265, getQsyncStatus(1, this.mCurrentFgPkg), this.mCurrentFgPkg);
                this.mIsMapFpsSwitch = true;
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_MSG_USER_PRESENT for Map fps value=1");
                }
            }
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "MSG_MSG_USER_PRESENT fg=" + foregroundInfo2);
            }
            onForegroundChanged(foregroundInfo2);
            return;
        }
        if (i2 == -5) {
            ForegroundInfo foregroundInfo3 = this.mCurrentFgInfo;
            if (foregroundInfo3 == null || foregroundInfo3.mForegroundPackageName == null) {
                writeLocalLog("COMMON_MSG_SCREEN_OFF break for mCurrentFgInfo is null !");
                return;
            }
            this.mIsLockScreen = true;
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "COMMON_MSG_SCREEN_OFF");
            }
            if (this.mIsMapFpsSwitch && this.mMapFpsApps.contains(this.mCurrentFgInfo.mForegroundPackageName)) {
                notifySFIfpsMode(265, getQsyncStatus(0, this.mCurrentFgPkg), this.mCurrentFgPkg);
                this.mIsMapFpsSwitch = false;
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "SCREEN_OFF for Map fps value=0");
                    return;
                }
                return;
            }
            return;
        }
        if (i2 == -4) {
            if (!((KeyguardManager) this.mContext.getSystemService("keyguard")).isDeviceLocked()) {
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_MSG_SCREEN_ON ignore for unlocked");
                    return;
                }
                return;
            }
            int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "kid_user_id", -10000, 0);
            if (CrossUserUtils.getCurrentUserId() == intForUser) {
                writeLocalLog("MSG_MSG_SCREEN_ON ignore for kid space id=" + intForUser);
                return;
            }
            if (PowerKeeperManager.getInstance().getEventsAggregator().isInCall()) {
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_MSG_SCREEN_ON ignore for in call");
                    return;
                }
                return;
            }
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "MSG_MSG_SCREEN_ON");
            }
            if (this.mIsWhitelistPolicy || this.mIsWhiteFpsList) {
                setScreenEffect("SCREEN_ON", this.mUserFps, 245);
                return;
            } else {
                if (this.mCurrentCookie != 244) {
                    setScreenEffect("SCREEN_ON", this.mUserFps, 244);
                    return;
                }
                return;
            }
        }
        StringBuilder sb = null;
        switch (i2) {
            case 1:
                String str8 = (String) message.obj;
                SimpleSettings.Misc.putString(this.mContext, "key_top_names", str8);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_RESET_DEFAULT_LIST: " + str8);
                }
                this.mDefaultGameApp.clear();
                fillDefaultGameApp(str8, getPrivGamesFps(null), null);
                break;
            case 2:
                String str9 = (String) message.obj;
                String str10 = SimpleSettings.Misc.getString(this.mContext, "key_top_names", "") + ',' + ((String) message.obj);
                SimpleSettings.Misc.putString(this.mContext, "key_top_names", str10);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_APPEND_DEFAULT_LIST: " + str9);
                }
                this.mDefaultGameApp.clear();
                fillDefaultGameApp(str10, getPrivGamesFps(null), null);
                break;
            case 3:
                ArrayList<String> arrayList = (ArrayList) message.obj;
                ArraySet<String> installedThirdPartApps = PowerKeeperPackageManager.getInstalledThirdPartApps();
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_RESET_HIGH_PRIORITY_LIST new=" + arrayList);
                }
                this.mPrivGames.clear();
                int[] supportFps = getSupportFps();
                for (String str11 : arrayList) {
                    if (str11 != null && !str11.isEmpty()) {
                        String[] strArrSplit = str11.split(":");
                        if (strArrSplit == null || strArrSplit.length != 2 || (str3 = strArrSplit[0]) == null || str3.isEmpty()) {
                            writeLocalLog("MSG_RESET_HIGH_PRIORITY_LIST invlid item");
                        } else {
                            int i5 = Utils.toInt(strArrSplit[1], -1);
                            if (i5 <= 0) {
                                writeLocalLog("MSG_RESET_HIGH_PRIORITY_LIST invlid value");
                            } else {
                                if (sb == null) {
                                    StringBuilder sb2 = new StringBuilder((arrayList.size() * 32) + 32);
                                    sb2.append(str11);
                                    sb = sb2;
                                } else {
                                    sb.append(',');
                                    sb.append(str11);
                                }
                                if (isInstalledPackage(strArrSplit[0], installedThirdPartApps)) {
                                    this.mPrivGames.put(strArrSplit[0], Integer.valueOf(getSuitableFps(supportFps, i5)));
                                }
                            }
                        }
                    }
                }
                String string = sb == null ? "" : sb.toString();
                SimpleSettings.Misc.putString(this.mContext, "key_priv_names", string);
                this.mDefaultGameApp.clear();
                fillDefaultGameApp(SimpleSettings.Misc.getString(this.mContext, "key_top_names", ""), getPrivGamesFps(string), installedThirdPartApps);
                break;
            case 4:
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_RESET_TOP_DEFINE_LIST");
                }
                parseTopDefineAppFromDb((String) message.obj, null);
                break;
            case 5:
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_RESET_VIDEO_TOP_LIST");
                }
                this.mVideoApps.clear();
                parseTopVideoAppFromDb((String) message.obj, null);
                break;
            case 6:
                ForegroundInfo foregroundInfo4 = this.mCurrentFgInfo;
                if (foregroundInfo4 == null || (str4 = foregroundInfo4.mForegroundPackageName) == null) {
                    writeLocalLog("ignore null fg info");
                } else {
                    String str12 = (String) message.obj;
                    if (str4.equals(str12)) {
                        int i6 = message.arg2;
                        if (i6 == 1) {
                            setScreenEffect(str12, 60, 252);
                        } else if (i6 == 2) {
                            onForegroundChanged(this.mCurrentFgInfo);
                        }
                    } else {
                        writeLocalLog("ignore for fg pkg=" + this.mCurrentFgInfo.mForegroundPackageName + " src=" + str12);
                    }
                }
                break;
            case 7:
                this.mIsSplitScreen = ((Boolean) ((AsyncResult) message.obj).result).booleanValue();
                ForegroundInfo foregroundInfo5 = ProcessManager.getForegroundInfo();
                if (foregroundInfo5 != null) {
                    if (this.DBG_DISPLAY) {
                        Log.d(TAG, "MSG_SPLIT_SCREEN_CHANGED mIsSplitScreen=" + this.mIsSplitScreen + " fgInfo=" + foregroundInfo5);
                    }
                    onForegroundChanged(foregroundInfo5);
                    break;
                }
                break;
            case 8:
                ForegroundInfo foregroundInfo6 = this.mCurrentFgInfo;
                if (foregroundInfo6 == null || (str5 = foregroundInfo6.mForegroundPackageName) == null || !"com.android.camera".equals(str5)) {
                    writeLocalLog("MSG_CAMERA_RESET_FPS break for not camera");
                } else {
                    Integer num = this.mTopApps.get("com.android.camera");
                    if (num == null) {
                        writeLocalLog("MSG_CAMERA_RESET_FPS break for not optimized");
                    } else {
                        boolean zBooleanValue = ((Boolean) message.obj).booleanValue();
                        writeLocalLog("MSG_CAMERA_RESET_FPS isRestore=" + zBooleanValue);
                        if (this.mIsWhitelistPolicy) {
                            if (zBooleanValue) {
                                setScreenEffect("com.android.camera", this.mUserFps, 245);
                            } else {
                                setScreenEffect("com.android.camera", this.mDefaultFps, 245);
                            }
                        } else if (zBooleanValue) {
                            setScreenEffect("com.android.camera", this.mUserFps, 244);
                        } else {
                            setScreenEffect("com.android.camera", num.intValue(), 252);
                        }
                    }
                }
                break;
            case 9:
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_RESET_EVALUATION_LIST");
                }
                this.mExcludeApps.clear();
                parseExcludeAppFromDb((String) message.obj, null);
                break;
            case 10:
                onPkgExistentChanged((EventsAggregator.EventResult) message.obj);
                break;
            case 11:
                boolean zBooleanValue2 = ((Boolean) message.obj).booleanValue();
                this.mIsEnterRecentTask = zBooleanValue2;
                writeLocalLog("MSG_RECENT_TASK isEnter=" + zBooleanValue2 + " mCurrentFgInfo=" + this.mCurrentFgInfo);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_RECENT_TASK isEnter=" + zBooleanValue2 + " mCurrentFgInfo=" + this.mCurrentFgInfo);
                }
                notifyRecentTask(zBooleanValue2);
                break;
            case 12:
                String str13 = (String) message.obj;
                int suitableFps = getSuitableFps(getSupportFps(), message.arg1);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "MSG_OVERRIDE_GAME_FRESHRATE app=" + str13 + " fps=" + suitableFps);
                }
                this.mPrivGames.put(str13, Integer.valueOf(suitableFps));
                ForegroundInfo foregroundInfo7 = this.mCurrentFgInfo;
                if (foregroundInfo7 == null || (str6 = foregroundInfo7.mForegroundPackageName) == null || !str6.equals(str13)) {
                    writeLocalLog("MSG_OVERRIDE_GAME_FRESHRATE break");
                } else {
                    onForegroundChanged(this.mCurrentFgInfo);
                }
                break;
            default:
                switch (i2) {
                    case 17:
                        String str14 = (String) message.obj;
                        if (!TextUtils.isEmpty(str14)) {
                            if (str14.equals("delayed_am_stop")) {
                                if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "handleDelayedMessage MSG_NOTIFY_AM_STOP mPrevFgPkg:" + this.mPrevFgPkg + ",mCurrentFgPkg:" + this.mCurrentFgPkg + ", mIsDuringGesture:" + this.mIsDuringGesture + ", msgObj:" + str14);
                                }
                                removeMessages(17);
                                if (!this.mIsDuringGesture) {
                                    notifyAMStop(this.mCurrentFgPkg);
                                }
                                break;
                            } else {
                                if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "handleMessage MSG_NOTIFY_AM_STOP mPrevFgPkg:" + this.mPrevFgPkg + ",mCurrentFgPkg:" + this.mCurrentFgPkg + ", mIsDuringGesture:" + this.mIsDuringGesture + ", msgObj:" + str14);
                                }
                                if (!this.mIsDuringGesture && str14.equals(this.mPrevFgPkg)) {
                                    notifyAMStop(this.mCurrentFgPkg);
                                    break;
                                }
                            }
                        }
                        break;
                    case 18:
                        String str15 = (String) message.obj;
                        int i7 = message.arg1;
                        if (i7 == 0 || i7 == 1) {
                            GameSceneChange(str15, i7);
                        } else if (i7 == 2 || i7 == 3) {
                            VideoSceneChange(str15, i7);
                        }
                        break;
                    case 19:
                        String str16 = (String) message.obj;
                        if (!TextUtils.isEmpty(str16)) {
                            if (this.DBG_DISPLAY) {
                                Log.d(TAG, "MSG_NOTIFY_GESTURE, gesture:" + str16);
                            }
                            removeMessages(48);
                            if (str16.equals(HomeGestureStatusProvider.VALUE_GESTURE_START)) {
                                if (!this.FPS_SWITCH_DEFAULT && this.mIsLockScreen && !this.mCurrentFgPkg.equals(this.LAUNCHER_PKG)) {
                                    writeLocalLog("MSG_NOTIFY_GESTURE, is in lock, no listen gesture_start mCurrentFgPkg:" + this.mCurrentFgPkg);
                                } else if (!this.mCurrentFgPkg.equals(this.LAUNCHER_PKG)) {
                                    appToHomeGestrueStart();
                                } else if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "MSG_NOTIFY_GESTURE, fg is home, no gesture_start");
                                }
                            } else if (str16.equals(HomeGestureStatusProvider.VALUE_TO_APP) || str16.equals(HomeGestureStatusProvider.VALUE_TO_ONE_HAND) || str16.equals(HomeGestureStatusProvider.VALUE_GESTURE_END)) {
                                if (!this.mCurrentFgPkg.equals(this.LAUNCHER_PKG)) {
                                    appToAppGestrue();
                                } else if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "MSG_NOTIFY_GESTURE, fg is home, no to_app");
                                }
                            }
                        }
                        break;
                    case 20:
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "MSG_RESET_EYE_MODE_LIST");
                        }
                        parseEyeAppFromDb((String) message.obj, null);
                        break;
                    case 21:
                        if (this.DBG_DISPLAY) {
                            Log.d(TAG, "MSG_SMART_BACKLIGHT_MODE_LIST");
                        }
                        parseBacklightAppFromDb((String) message.obj);
                        break;
                    default:
                        switch (i2) {
                            case 23:
                                if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "MSG_EBOOK_IDLE_LIST");
                                }
                                this.mEbookidleApps.clear();
                                parseEbookIdleAppFromDb((String) message.obj, null);
                                break;
                            case 24:
                                if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "MSG_TP_IDLE_LIST");
                                }
                                this.mTpidleApps.clear();
                                parseTpIdleAppFromDb((String) message.obj, null);
                                break;
                            case 25:
                                if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "MSG_RESET_TOP_SMART_LIST");
                                }
                                parseTopSmartAppFromDb((String) message.obj, null);
                                break;
                            case 26:
                                if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "MSG_INPUT_IN_CHANGE");
                                }
                                onForegroundChanged(this.mCurrentFgInfo);
                                break;
                            case 27:
                                if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "MSG_INPUT_IN_LIST");
                                }
                                this.mInputApps.clear();
                                this.mAudioApps.clear();
                                parseInputAppFromDb((String) message.obj, null);
                                break;
                            case 28:
                                if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "MSG_LOW_FPS_LIST");
                                }
                                mCameraActivityList.clear();
                                mLowFpsPkgList.clear();
                                parseLowFpsAppFromDb((String) message.obj, null);
                                break;
                            case 29:
                                if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "MSG_NOTIFY_VEDIO_IDLE_STATUS");
                                }
                                notifySFIChange();
                                break;
                            case 30:
                                if (this.DBG_DISPLAY) {
                                    Log.d(TAG, "MSG_RESET_SHORT_VIDEO_TOP_LIST");
                                }
                                mShortVideoApps.clear();
                                parseTopShortVideoAppFromDb((String) message.obj, null);
                                break;
                            default:
                                switch (i2) {
                                    case 32:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_RESET_VIDEO_IDLE_LIST");
                                        }
                                        this.mVideoIdleApps.clear();
                                        parseTopVideoIdleAppFromDb((String) message.obj, null);
                                        break;
                                    case MilletHandler.MSG_USER_PRESENT /* 33 */:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_FOREGROUND_CHANGE_ACTIVITY");
                                        }
                                        this.mIsBlackList = true;
                                        onForegroundChanged(this.mCurrentFgInfo);
                                        break;
                                    case 34:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_PROMOTION_LIST");
                                        }
                                        this.mPromotionApps.clear();
                                        parsePromotionAppFromDb((String) message.obj, null);
                                        break;
                                    case 35:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_DISPLAY_FPS_LIST");
                                        }
                                        this.mDisplayFpsApps.clear();
                                        parseDisplayFpsAppFromDb((String) message.obj, null);
                                        break;
                                    case 36:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_FULL_SCREEN_SWITCH");
                                        }
                                        this.mFullScreenFpsApps.clear();
                                        this.mVideoLowerFpsApps.clear();
                                        parseFullScreenFpsAppFromDb((String) message.obj, null);
                                        break;
                                    case 37:
                                        parseCustomModeSwitchFromDb((String) message.obj);
                                        break;
                                    case 38:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_MAP_SWITCH");
                                        }
                                        this.mMapFpsApps.clear();
                                        parseMapFpsAppFromDb((String) message.obj, null);
                                        break;
                                    case 39:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_AUTOMODE_VRR");
                                        }
                                        this.mAutoModeApps.clear();
                                        this.mVrrApps.clear();
                                        parseAutoModeVrrAppFromDb((String) message.obj, null);
                                        break;
                                    case FrozenAppController.FREEZE_CONDITION_ALL /* 40 */:
                                        parseViolentModeSwitchFromDb((String) message.obj);
                                        break;
                                    case 41:
                                        parseFossSwitchFromDb((String) message.obj);
                                        break;
                                    case 42:
                                        parseNoteSwitchFromDb((String) message.obj);
                                        break;
                                    case 43:
                                        mExtremeVideoPackageList.clear();
                                        this.mDuelMemcPackage.clear();
                                        this.mSingleMemcPackage.clear();
                                        mExtremeVideoActivityList.clear();
                                        parseIrisConfigFromDb((String) message.obj);
                                        break;
                                    case 44:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_UPDATE_MEMC_STATE");
                                        }
                                        updateIrisState(this.mCurExtremeVideoActivity, false);
                                        break;
                                    case ThermalManager.DEFAULT_ALLOWED_SHOW_BENCHMARK_DIALOG_TEMP_THRESHHOLD /* 45 */:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_SWTICH_TO_IRIS_PQ");
                                        }
                                        if (!this.mIsIrisPQOn) {
                                            if (this.DBG_DISPLAY) {
                                                Log.d(TAG, "Set iris pq on");
                                            }
                                            this.mIsIrisPQOn = true;
                                            IrisServiceUtils.getInstance(this.mContext).irisConfigureSet(3);
                                        }
                                        break;
                                    case 46:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_UPDATE_SDR2HDR_STATE");
                                        }
                                        updateIrisState(this.mCurrentFgPkg, true);
                                        break;
                                    case ThermalManager.DEFAULT_BATTERY_THRESHOLD /* 47 */:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_EXTREME_SCENE_CHANGED");
                                        }
                                        if (this.mRealRefreshRate <= 60.0f) {
                                            this.mHasIrisCommandSet = true;
                                            onExtremeSceneChanged();
                                            break;
                                        }
                                        break;
                                    case 48:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_HIGH_FPS_NOTIFY");
                                        }
                                        onHighFpsSendNotify();
                                        break;
                                    case 49:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_INIT");
                                        }
                                        parseInit();
                                        break;
                                    case 50:
                                        if (this.DBG_DISPLAY) {
                                            Log.d(TAG, "MSG_HIGHEST_FPS_SWITCH");
                                        }
                                        this.mDefineHighestFpsApps.clear();
                                        this.mCustomHighestFpsApps.clear();
                                        parseHighestFpsAppFromDb((String) message.obj, null);
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;
        }
    }

    public boolean isInstalledPackage(String str, ArraySet<String> arraySet) {
        if (arraySet == null) {
            arraySet = PowerKeeperPackageManager.getInstalledThirdPartApps();
        }
        if (arraySet.contains(str)) {
            return true;
        }
        return PowerKeeperPackageManager.isUnremovableApp(str);
    }

    public boolean isSupportDevice() {
        for (String str : this.mCustomFpsDeviceArray) {
            if (Utils.isBelongToDeviceSeries(str)) {
                return true;
            }
            if (str.endsWith("_intl") && miui.os.Build.IS_INTERNATIONAL_BUILD) {
                return Utils.isBelongToDeviceSeries(str.substring(0, str.length() - 5));
            }
        }
        return false;
    }

    public boolean isSupportVideoEnhancePkg(String str) {
        List<String> list = mExtremeVideoPackageList;
        if (list == null || list.size() == 0) {
            return false;
        }
        return mExtremeVideoPackageList.contains(str);
    }

    public void notifyAMStop(String str) {
        if (TextUtils.isEmpty(str)) {
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "notifyAMStop pkg == null return");
                return;
            }
            return;
        }
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "notifyAMStop pkg:" + str);
        }
        if (!this.mIsFgChange) {
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "notifyAMStop fg no change mCurrentFgPkg:" + this.mCurrentFgPkg + ", mIsDuringGesture == false, mCurrentFps" + this.mCurrentFps);
                return;
            }
            return;
        }
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "notifyAMStop fg change, mCurrentFgPkg=" + this.mCurrentFgPkg + ", mCurrentFps:" + this.mCurrentFps);
        }
        this.mIsFgChange = false;
        if (this.mCurrentFgPkg.equals(this.LAUNCHER_PKG)) {
            return;
        }
        homeToAppEnd(this.mCurrentFgPkg);
    }

    public void notifyFGChange(ForegroundInfo foregroundInfo) {
        this.mIsFgChange = true;
        this.mPrevFgPkg = this.mCurrentFgPkg;
        this.mCurrentFgPkg = foregroundInfo.mForegroundPackageName;
        this.mCurrentFgInfo = foregroundInfo;
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "notifyFGChange mPrevFgPkg:" + this.mPrevFgPkg + ", mCurrentFgPkg:" + this.mCurrentFgPkg + ", Recent:" + this.mIsEnterRecentTask);
        }
        if (this.mCurrentFgPkg.equals(this.LAUNCHER_PKG)) {
            if (!this.mIsEnterRecentTask) {
                appToHomeEnd(foregroundInfo);
                return;
            }
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "notifyFGChange mCurrentFgPkg:" + this.mCurrentFgPkg + ", mIsEnterRecentTask:" + this.mIsEnterRecentTask);
                return;
            }
            return;
        }
        if (this.mIsEnterRecentTask && this.mCurrentFgPkg.equals("com.android.settings")) {
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "notifyFGChange, from recent to setting");
            }
            ForegroundInfo foregroundInfo2 = new ForegroundInfo();
            foregroundInfo2.mForegroundPackageName = this.mCurrentFgPkg;
            onForegroundChanged(foregroundInfo2);
            return;
        }
        if (this.mCurrentFgPkg.equals("com.android.camera")) {
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "notifyFGChange camera");
            }
            ForegroundInfo foregroundInfo3 = new ForegroundInfo();
            foregroundInfo3.mForegroundPackageName = this.mCurrentFgPkg;
            onForegroundChanged(foregroundInfo3);
            return;
        }
        if (this.DELAY_TIME == 0 && "com.miui.gallery".equals(this.mCurrentFgPkg) && "com.android.camera".equals(foregroundInfo.mLastForegroundPackageName)) {
            removeMessages(17);
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "notifyFGChange camera to gallery, wait am_stop and sendDelayedMessage DELAY_TIME = 1200ms");
            }
            sendMessageDelayed(obtainMessage(17, "delayed_am_stop"), 1200L);
            return;
        }
        if (this.mIsWhiteFpsList) {
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "notifyFGChange low phone DELAY_TIME = 0");
            }
            sendMessageDelayed(obtainMessage(17, "delayed_am_stop"), 0L);
            return;
        }
        removeMessages(17);
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "notifyFGChange new fg is not home, wait am_stop and sendDelayedMessage DELAY_TIME = " + this.DELAY_TIME);
        }
        sendMessageDelayed(obtainMessage(17, "delayed_am_stop"), this.DELAY_TIME);
    }

    public void onForegroundChanged(ForegroundInfo foregroundInfo) {
        int iC;
        int i2;
        if (foregroundInfo == null) {
            writeLocalLog("appInfo is null");
            return;
        }
        if (Build.DEVICE.equalsIgnoreCase("star") && "com.xiaomi.misubscreenui".equals(foregroundInfo.mForegroundPackageName)) {
            return;
        }
        if (!this.mGameVideoScenePkg.equals(foregroundInfo.mForegroundPackageName)) {
            this.mIsEnterVideoScene = false;
            this.mIsEnterGameScene = false;
        }
        if (this.IS_ENABLE_DISPLAY_DITHER && this.mIsDitherCloudSwitch) {
            if (this.mVideoApps.contains(foregroundInfo.mForegroundPackageName)) {
                DisplayFeatureManager.getInstance().setScreenEffect(41, this.mInvalidValue, 248);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "onForegroundChanged for dither Cookie=248");
                }
            } else {
                DisplayFeatureManager.getInstance().setScreenEffect(41, this.mInvalidValue, 244);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "onForegroundChanged for dither Cookie=244");
                }
            }
        }
        if (this.mIsNoteCloudSwitch) {
            if ("com.miui.notes".equals(foregroundInfo.mForegroundPackageName)) {
                DisplayFeatureManager.getInstance().setScreenEffect(24, 1, 262);
                notifySFNoteMode(262, 1);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "onForegroundChanged for note enter");
                }
            } else {
                DisplayFeatureManager.getInstance().setScreenEffect(24, 0, 262);
                notifySFNoteMode(262, 0);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "onForegroundChanged for note exit");
                }
            }
        }
        if (this.IS_ENABLE_DISPLAY_FOSS && this.mIsFossCloudSwitch) {
            if (this.mVideoApps.contains(foregroundInfo.mForegroundPackageName)) {
                i2 = 248;
            } else if ("com.android.camera".equals(foregroundInfo.mForegroundPackageName)) {
                i2 = 252;
            } else if (this.mDefaultGameApp.contains(foregroundInfo.mForegroundPackageName) || this.mPrivGames.get(foregroundInfo.mForegroundPackageName) != null) {
                i2 = 254;
            } else if ("com.miui.gallery".equals(foregroundInfo.mForegroundPackageName) || "com.fstar.Pattern".equals(foregroundInfo.mForegroundPackageName)) {
                i2 = 243;
            } else {
                i2 = this.LAUNCHER_PKG.equals(foregroundInfo.mForegroundPackageName) ? 242 : 244;
            }
            if (i2 != this.mLastFossCookie) {
                DisplayFeatureManager.getInstance().setScreenEffect(40, this.mInvalidValue, i2);
                this.mLastFossCookie = i2;
            }
        }
        if (this.mInputStatus == 1 && this.mInputApps.containsKey(foregroundInfo.mForegroundPackageName)) {
            writeLocalLog("onForegroundChanged mInputApps=" + foregroundInfo.mForegroundPackageName + "  mIsOnScroll=" + this.mIsOnScroll + " mIsSupportLowFps" + this.mIsSupportLowFps);
            if (this.mInputApps.get(foregroundInfo.mForegroundPackageName).intValue() == 30 && !this.mIsOnScroll && this.mIsSupportLowFps) {
                this.mIsLowFps = true;
                setScreenEffect(foregroundInfo.mForegroundPackageName, 30, 249);
                return;
            } else {
                this.mIsLowFps = false;
                setScreenEffect(foregroundInfo.mForegroundPackageName, 60, 249);
                return;
            }
        }
        if (this.mAudioRecordStatus && this.mAudioApps.containsKey(foregroundInfo.mForegroundPackageName)) {
            writeLocalLog("onForegroundChanged mAudioApps=" + foregroundInfo.mForegroundPackageName + "  mIsOnScroll=" + this.mIsOnScroll + "  mIsSupportLowFps=" + this.mIsSupportLowFps);
            if (this.mAudioApps.get(foregroundInfo.mForegroundPackageName).intValue() == 30 && !this.mIsOnScroll && this.mIsSupportLowFps) {
                this.mIsLowFps = true;
                setScreenEffect(foregroundInfo.mForegroundPackageName, 30, 249);
                return;
            } else {
                this.mIsLowFps = false;
                this.mIsOnScroll = false;
                setScreenEffect(foregroundInfo.mForegroundPackageName, 60, 249);
                return;
            }
        }
        if (this.mIsBlackList && ArrayUtils.contains(this.mBlacklistActivity_LowFps, this.mCurActivityName)) {
            writeLocalLog("onForegroundChanged mBlacklistActivity_LowFps=" + this.mCurActivityName + "  mIsOnScroll=" + this.mIsOnScroll + "  mIsSupportLowFps=" + this.mIsSupportLowFps);
            if (!this.mIsOnScroll && this.mIsSupportLowFps) {
                this.mIsLowFps = true;
                setScreenEffect(foregroundInfo.mForegroundPackageName, 30, 249);
                return;
            } else {
                this.mIsOnScroll = false;
                this.mIsLowFps = false;
                setScreenEffect(foregroundInfo.mForegroundPackageName, 60, 249);
                return;
            }
        }
        if (this.mIsBlackList && ArrayUtils.contains(this.mBlacklistActivity_Vr, this.mCurActivityName)) {
            writeLocalLog("onForegroundChanged mBlacklistActivity_Vr" + this.mCurActivityName);
            setScreenEffect(foregroundInfo.mForegroundPackageName, 60, 249);
            return;
        }
        if (this.mIsBlackList && ArrayUtils.contains(this.mHighFpsListActivity, this.mCurActivityName) && this.mEnterHighFpsValue >= 120 && this.mUserFps >= 120 && this.mPowerMode != 3 && ((this.mTopApps.getOrDefault("com.youku.phone", 0).intValue() == 60 || this.mVideoApps.contains("com.youku.phone")) && (!"true".equals(mIsCustomFpsSwitch) || mIsSmart))) {
            writeLocalLog("onForegroundChanged EnterHighFps enjoy= " + foregroundInfo.mForegroundPackageName + " mCurActivityName= " + this.mCurActivityName + " mEnterHighFpsValue" + this.mEnterHighFpsValue);
            setScreenEffect(foregroundInfo.mForegroundPackageName, 120, 249);
            return;
        }
        if (this.IS_SUPPORTED_IRIS && this.mIsExtremeVideoModeOn == 1) {
            if (hasMessages(47)) {
                removeMessages(47);
            }
            if (ArrayUtils.contains(mExtremeVideoPackageList, foregroundInfo.mForegroundPackageName)) {
                if (this.mIsBlackList && ArrayUtils.contains(this.mHighFpsListActivity, this.mCurActivityName) && foregroundInfo.mForegroundPackageName.equals("com.youku.phone") && this.mEnterHighFpsValue >= 120) {
                    writeLocalLog("onForegroundChanged youku fps enjoy= " + foregroundInfo.mForegroundPackageName + " ignore x7 state");
                    setScreenEffect(foregroundInfo.mForegroundPackageName, 120, 249);
                    return;
                }
                writeLocalLog("onForegroundChanged mExtremeVideoPackageList:" + foregroundInfo.mForegroundPackageName);
                setScreenEffect(foregroundInfo.mForegroundPackageName, 60, 249);
                sendEmptyMessage(47);
                return;
            }
            if (ArrayUtils.contains(mExtremeVideoPackageList, this.mPrevFgPkg) && this.mHasIrisCommandSet) {
                onExtremeSceneRecovery();
            }
        } else if (this.mIsBlackList && ArrayUtils.contains(this.mBlackListActivityApplets, this.mCurActivityName) && this.mIsOnFullScreen && this.mAppletsSwitch) {
            writeLocalLog("onForegroundChanged mBlacklistActivity_Applets" + this.mCurActivityName);
            setScreenEffect(foregroundInfo.mForegroundPackageName, 60, 249);
            return;
        }
        if (this.mInputStatus == 0 && !this.mAudioRecordStatus && !this.mIsBlackList) {
            unregisterDisplayScrollListener();
        }
        if (this.mIsSplitScreen) {
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged for splitScreen CurrentCookie=" + this.mCurrentCookie + " appInfo=" + foregroundInfo);
            }
            if (this.mIsWhitelistPolicy) {
                setScreenEffect(foregroundInfo.mForegroundPackageName, this.mDefaultFps, 245);
                return;
            } else {
                if (this.mCurrentCookie != 244) {
                    setScreenEffect(foregroundInfo.mForegroundPackageName, this.mUserFps, 244);
                    return;
                }
                return;
            }
        }
        String str = foregroundInfo.mForegroundPackageName;
        if (str == null || str.isEmpty()) {
            if (this.DBG_DISPLAY) {
                Log.d(TAG, "onForegroundChanged null appInfo=" + foregroundInfo);
                return;
            }
            return;
        }
        this.mCurrentFgInfo = foregroundInfo;
        this.mCurrentFgPkg = foregroundInfo.mForegroundPackageName;
        if (this.DBG_DISPLAY) {
            Log.d(TAG, "onForegroundChanged appInfo=" + foregroundInfo + ", CurrentCookie=" + this.mCurrentCookie + ", mCurrentFps:" + this.mCurrentFps);
            String str2 = SystemProperties.get("debug.power.fps", "");
            String[] strArrSplit = str2.split(":");
            if (strArrSplit.length == 2 && foregroundInfo.mForegroundPackageName.equals(strArrSplit[0])) {
                int i3 = Utils.toInt(strArrSplit[1], -1);
                Log.w(TAG, "setForDebugFps debug.power.fps=" + str2 + " debugFps=" + i3);
                if (i3 > 0) {
                    setScreenEffect(foregroundInfo.mForegroundPackageName, i3, 249);
                    return;
                }
            }
        }
        if (this.mExcludeApps.contains(foregroundInfo.mForegroundPackageName)) {
            int[] supportFps = getSupportFps();
            int arrayMaxMember = Utils.getArrayMaxMember(supportFps, this.mDefaultFps);
            if (e.e.o(4) && PerfEngineManager.d() != null && (iC = PerfEngineManager.d().g().c(foregroundInfo.mForegroundPackageName)) > 0) {
                arrayMaxMember = getSuitableFps(supportFps, iC);
                writeLocalLog("onForegroundChanged mExcludeApps fps:" + arrayMaxMember + ", perfEngineFps:" + iC);
            }
            setScreenEffect(foregroundInfo.mForegroundPackageName, arrayMaxMember, 247);
            return;
        }
        if (this.mMapFpsApps.contains(foregroundInfo.mForegroundPackageName)) {
            writeLocalLog("onForegroundChanged mMapFpsApps In qsync mode not set fps");
            return;
        }
        if (this.mIsWhitelistPolicy) {
            Integer num = this.mTopApps.get(foregroundInfo.mForegroundPackageName);
            if (num == null) {
                setScreenEffect(foregroundInfo.mForegroundPackageName, this.mDefaultFps, 245);
                return;
            } else {
                setScreenEffect(foregroundInfo.mForegroundPackageName, num.intValue(), 245);
                return;
            }
        }
        if (!this.mIsCustom && !this.IS_ENABLE_VIDEO_DECODE_ONLY && this.mVideoApps.contains(foregroundInfo.mForegroundPackageName) && !this.mIsWhiteFpsList) {
            setScreenEffect(foregroundInfo.mForegroundPackageName, 60, 248);
            return;
        }
        if (PeGameController.n0 && this.mSmartFpsApps.contains(foregroundInfo.mForegroundPackageName) && this.mCurrentFps == PeGameController.l0) {
            if (PowerLog.V) {
                PowerLog.v(TAG, "onForegroundChanged PeGameController.sIsSmartFPSWeChat:" + PeGameController.n0 + ", mCurrentFps:" + this.mCurrentFps + ", PeGameController.sFPS:" + PeGameController.l0 + ", in mSmartFpsApps, no change FPS, return");
                return;
            }
            return;
        }
        if (PowerLog.V) {
            PowerLog.v(TAG, "onForegroundChanged PeGameController.sIsSmartFPSWeChat:" + PeGameController.n0 + ", mCurrentFps:" + this.mCurrentFps + ", PeGameController.sFPS:" + PeGameController.l0);
        }
        Integer num2 = this.mPrivGames.get(foregroundInfo.mForegroundPackageName);
        if (num2 != null) {
            setScreenEffect(foregroundInfo.mForegroundPackageName, num2.intValue(), 254);
            return;
        }
        if (this.mDefaultGameApp.contains(foregroundInfo.mForegroundPackageName)) {
            setScreenEffect(foregroundInfo.mForegroundPackageName, 60, 254);
            return;
        }
        if (this.mIsWhiteFpsList) {
            Integer num3 = this.mTopApps.get(foregroundInfo.mForegroundPackageName);
            if (num3 == null) {
                setScreenEffect(foregroundInfo.mForegroundPackageName, this.mDefaultFps, 245);
                return;
            } else {
                setScreenEffect(foregroundInfo.mForegroundPackageName, num3.intValue(), 245);
                return;
            }
        }
        if (this.mHighestFpsSwitch && this.MAX_SUPPORT_FPS == 144) {
            if (mIsSmart && this.mDefineHighestFpsApps.contains(foregroundInfo.mForegroundPackageName)) {
                Log.d(TAG, "onForegroundChanged: mDefineHighestFpsApps: setScreenEffect fps = " + this.MAX_SUPPORT_FPS);
                setScreenEffect(foregroundInfo.mForegroundPackageName, this.MAX_SUPPORT_FPS, 272);
                return;
            }
            if (this.mIsCustom && this.mCustomHighestFpsApps.contains(foregroundInfo.mForegroundPackageName) && !this.mCustomApps.containsKey(foregroundInfo.mForegroundPackageName)) {
                Log.d(TAG, "onForegroundChanged: mCustomHighestFpsApps: setScreenEffect fps = " + this.MAX_SUPPORT_FPS);
                setScreenEffect(foregroundInfo.mForegroundPackageName, this.MAX_SUPPORT_FPS, 272);
                return;
            }
        }
        Integer num4 = this.mTopApps.get(foregroundInfo.mForegroundPackageName);
        if (num4 == null) {
            if (this.mIsEnterVideoScene) {
                setScreenEffect(foregroundInfo.mForegroundPackageName, this.mUserFps, 248);
                return;
            } else if (this.mIsEnterGameScene) {
                setScreenEffect(foregroundInfo.mForegroundPackageName, 60, 254);
                return;
            } else {
                if (this.mCurrentCookie != 244) {
                    setScreenEffect(foregroundInfo.mForegroundPackageName, this.mUserFps, 244);
                    return;
                }
                return;
            }
        }
        if (foregroundInfo.mForegroundUid == 1000 && "power.systemui".equals(foregroundInfo.mForegroundPackageName)) {
            setScreenEffect(foregroundInfo.mForegroundPackageName, num4.intValue(), 251);
            return;
        }
        if ("com.android.camera".equals(foregroundInfo.mForegroundPackageName)) {
            setScreenEffect(foregroundInfo.mForegroundPackageName, num4.intValue(), 252);
        } else if (this.mIsEnterVideoScene) {
            setScreenEffect(foregroundInfo.mForegroundPackageName, num4.intValue(), 248);
        } else {
            setScreenEffect(foregroundInfo.mForegroundPackageName, num4.intValue(), 249);
        }
    }

    public void onFullScreenChange(boolean z2) {
        this.mIsOnFullScreen = z2;
        if (this.mAppletsSwitch) {
            onForegroundChanged(this.mCurrentFgInfo);
        }
    }

    public void onPowerModeChanged() {
        if (this.mViolentModeSwitch) {
            String string = Settings.System.getString(this.mContext.getContentResolver(), "custom_mode_switch");
            if (this.mPowerMode == 1) {
                Settings.System.putInt(this.mContext.getContentResolver(), "speed_mode", 1);
                if ("true".equals(string)) {
                    Settings.System.putString(this.mContext.getContentResolver(), "custom_mode_switch", "false");
                    Log.i(TAG, "onPowerModeChanged, enter performance-mode,switch CUSTOM_MODE_SWITCH to false");
                    return;
                }
                return;
            }
            Settings.System.putInt(this.mContext.getContentResolver(), "speed_mode", 0);
            if (string == null || string.equals(mIsCustomFpsSwitch)) {
                return;
            }
            Settings.System.putString(this.mContext.getContentResolver(), "custom_mode_switch", mIsCustomFpsSwitch);
            Log.i(TAG, "onPowerModeChanged, exit performance-mode,recovery CUSTOM_MODE_SWITCH " + mIsCustomFpsSwitch);
        }
    }

    public void setAppFps(String str) {
        if (this.mTopApps == null) {
            return;
        }
        try {
            JSONArray jSONArray = new JSONArray(str);
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i2);
                this.mTopApps.put(jSONObject.getString("name"), Integer.valueOf(jSONObject.getInt("fps")));
            }
        } catch (Exception e2) {
            Log.e(TAG, "error jsondata in DisplayFrameSetting.setAppFps", e2);
        }
    }

    public void setScreenEffect(int i2, int i3) {
        if (this.FPS_SWITCH_DEFAULT && this.FPS_SWITCH_THERMAL && i3 == 253) {
            Settings.System.putInt(this.mContext.getContentResolver(), "thermal_limit_refresh_rate", i2);
        }
        DisplayFeatureManager.getInstance().setScreenEffect(24, i2, i3);
    }

    public void updateBacklightModeApps(String[] strArr) {
        try {
            Method declaredMethod = IPowerKeeperPolicy.class.getDeclaredMethod("setBacklightParam", String[].class);
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            if (iWhetstoneActivityManagerAsInterface == null) {
                writeLocalLog("updateBacklightModeApps return for mWSProxy");
                return;
            }
            IPowerKeeperPolicy powerKeeperPolicy = iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy();
            if (powerKeeperPolicy == null) {
                writeLocalLog("updateBacklightModeApps return for null instance");
                return;
            }
            declaredMethod.invoke(powerKeeperPolicy, strArr);
            writeLocalLog("updateBacklightModeApps call setBacklightParam BacklightModeApps=" + Arrays.toString(strArr));
        } catch (Exception e2) {
            writeLocalLog("updateBacklightModeApps excecption e=" + e2);
        }
    }

    public void updateEyeModeApps(String[] strArr) {
        try {
            Method declaredMethod = IPowerKeeperPolicy.class.getDeclaredMethod("setEyeParam", String[].class);
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            if (iWhetstoneActivityManagerAsInterface == null) {
                writeLocalLog("updateEyeModeApps return for mWSProxy");
                return;
            }
            IPowerKeeperPolicy powerKeeperPolicy = iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy();
            if (powerKeeperPolicy == null) {
                writeLocalLog("updateEyeModeApps return for null instance");
                return;
            }
            declaredMethod.invoke(powerKeeperPolicy, strArr);
            writeLocalLog("updateEyeModeApps call setEyeParam eyeModeApps=" + strArr);
        } catch (Exception unused) {
            writeLocalLog("updateEyeModeApps excecption");
        }
    }

    public static boolean setFpsAync(String str, int i2, int i3) {
        DisplayFrameSetting displayFrameSetting = sInstance;
        if (displayFrameSetting == null || !displayFrameSetting.mParseInited) {
            return false;
        }
        displayFrameSetting.setFpsAyncInternal(str, i2, i3);
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:28:0x005f  */
    private void setScreenEffect(String str, int i2, int i3) {
        boolean z2;
        int i4 = i2;
        if (this.mIsUltimate == 1 && i3 != 254 && i3 != 247 && !str.equals("com.android.camera")) {
            if (this.mCurrentFps != this.mUserFps) {
                Settings.Secure.putInt(this.mContext.getContentResolver(), "miui_refresh_rate", this.mUserFps);
                this.mCurrentFps = this.mUserFps;
                return;
            }
            return;
        }
        if (this.mIsWhitelistPolicy) {
            int i5 = i3 == 247 ? 247 : 245;
            if (this.IS_ENABLE_SMART_DFPS) {
                z2 = (this.mCurrentCookie == 247) != (i3 == 247);
            }
            int i6 = this.mUserFps;
            if (i6 <= 60) {
                if (this.mCurrentFps == i6 && !z2) {
                    Log.i(TAG, "setScreenEffect ignore for user selected lower fps");
                    return;
                }
                setScreenEffectInternal(i6, i5, str);
                writeLocalLog("setScreenEffect for user selected lower fps cookie=" + i5);
                return;
            }
            if (i4 > i6) {
                i4 = i6;
            }
            if (i4 == this.mCurrentFps && i3 == this.mCurrentCookie) {
                Log.i(TAG, "setScreenEffect ignore for no change");
                return;
            }
            StringBuilder sb = new StringBuilder(128);
            sb.append("setScreenEffect pkg=");
            sb.append(str);
            sb.append(" fps=");
            sb.append(i4);
            sb.append(" cookie=");
            sb.append(i5);
            writeLocalLog(sb.toString());
            setScreenEffectInternal(i4, i5, str);
            return;
        }
        int i7 = this.mCurrentCookie;
        boolean z3 = (i7 == 248) != (i3 == 248);
        boolean z4 = (i7 == 254) != (i3 == 254);
        boolean z5 = (i7 == 247) != (i3 == 247);
        boolean z6 = this.IS_ENABLE_SMART_DFPS && (z3 || z4 || z5 || ((i7 == 252) != (i3 == 252)));
        if (this.SF_ENBALE_AFFINITY && z4) {
            Log.i(TAG, "notifySFCookie cookie =" + i3);
            if (i3 == 254) {
                notifySFCPUCluster(0);
            } else {
                notifySFCPUCluster(1);
            }
        }
        int i8 = this.mUserFps;
        if (i8 <= 60 && !z6 && i3 != 247 && this.mCurrentFps == i8 && !this.mIsLowFps && i3 != 254) {
            writeLocalLog("setScreenEffect ignore for user selected lower FPS");
            if (PowerLog.V) {
                PowerLog.v(TAG, "setScreenEffect user:" + this.mUserFps + ", c:" + this.mCurrentFps + ", cookie:" + i3);
            }
            this.mCurrentCookie = i3;
            return;
        }
        if (i4 > i8 && i3 != 247) {
            Log.d(TAG, "fps > mUserFps fps = " + i4 + " MAX_SUPPORT_FPS = " + this.MAX_SUPPORT_FPS + " mUserFps = " + this.mUserFps);
            if (i4 != 144 || this.MAX_SUPPORT_FPS != 144 || this.mUserFps != 120) {
                writeLocalLog("setScreenEffect force fps down to user selected");
                i4 = this.mUserFps;
            }
        }
        StringBuilder sb2 = new StringBuilder(128);
        sb2.append("setScreenEffect pkg=");
        sb2.append(str);
        sb2.append(" fps=");
        sb2.append(i4);
        sb2.append(" cookie=");
        sb2.append(i3);
        writeLocalLog(sb2.toString());
        if (this.FPS_SWITCH_DEFAULT) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "miui_refresh_rate", i4);
            if (this.IS_ENABLE_SMART_DFPS && z5) {
                wifiLogControl(i3);
                DisplayFeatureManager.getInstance().setScreenEffect(24, i4, i3);
            }
            if (this.IS_ENABLE_SMART_DFPS && this.IS_ENABLE_CONTENT_DETECTION) {
                notifySFIfpsMode(i3, i4, str);
                if (this.DBG_DISPLAY) {
                    Log.d(TAG, "notifySFIfpsMode run cookie=" + i3 + "fps=" + i4 + "pkg=" + str);
                }
            }
        } else {
            DisplayFeatureManager.getInstance().setScreenEffect(24, i4, i3);
        }
        this.mCurrentFps = i4;
        this.mCurrentCookie = i3;
    }

    public void powerSaveModeChanged(int i2) {
    }
}
