package com.miui.powerkeeper.thermal;

import android.annotation.NonNull;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.LocalLog;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.thermal.listener.ForegroundGroupListener;
import com.miui.powerkeeper.thermal.listener.IListener;
import com.miui.powerkeeper.thermal.resource.Couple;
import com.miui.powerkeeper.thermal.resource.ThermalFactory;
import com.miui.powerkeeper.thermal.setting.IProviderCallback;
import com.miui.powerkeeper.thermal.setting.SettingDTO;
import com.miui.powerkeeper.thermal.setting.SettingProvider;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class ScenarioManager {
    private static final String TAG = "ThermalScenarioManager";
    private static final LocalLog sHistory = new LocalLog(32);
    private static ScenarioManager sInstance;
    private static final HandlerThread sThread;
    private final Handler mHandler = new ScenarioHandler(sThread.getLooper());
    private final EventsAggregator mEventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
    private final ThermalScenario mCurrentScenario = new ThermalScenario();
    private final ScenarioTree mScenarioTree = new ScenarioTree();
    private final Map<String, IListener> mListeners = new HashMap(12);
    private final Map<String, Object> mExtra = new HashMap();

    private class ScenarioHandler extends Handler {
        public ScenarioHandler(Looper looper) {
            super(looper);
        }

        private void onStateChanged(int i2, int i3) {
            ScenarioManager.this.mCurrentScenario.changeElement(i2, i3);
            if (ScenarioManager.this.mScenarioTree.matchScenario()) {
                ScenarioManager.this.onScenarioChanged();
            }
        }

        private void setExtra(int i2, Object obj) {
            try {
                Couple couple = (Couple) obj;
                ScenarioManager.this.mExtra.put((String) couple.getK(), couple.getV());
            } catch (Exception unused) {
                ScenarioManager.sHistory.log("ThermalScenarioManager error type int setExtra from: " + i2);
            }
        }

        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            int i2 = message.what;
            if (i2 == 13) {
                onStateChanged(message.arg1, message.arg2);
            } else {
                if (i2 != 14) {
                    return;
                }
                setExtra(message.arg1, message.obj);
            }
        }
    }

    private static class ScenarioNode {
        private final Map<Integer, ScenarioNode> mChildren;
        private int mScenarioName;

        public void clear() {
            this.mScenarioName = 0;
            this.mChildren.clear();
        }

        @NonNull
        public String toString() {
            if (!this.mChildren.isEmpty()) {
                return this.mChildren.toString();
            }
            return " scenario name : " + this.mScenarioName + " ";
        }

        private ScenarioNode() {
            this.mScenarioName = 0;
            this.mChildren = new HashMap(3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class ScenarioTree {
        private final ScenarioNode mRoot;

        private int traverse(ScenarioNode scenarioNode, int i2) {
            if (scenarioNode == null) {
                return Integer.MIN_VALUE;
            }
            if (scenarioNode.mChildren.isEmpty()) {
                return scenarioNode.mScenarioName;
            }
            if (i2 <= ScenarioManager.this.mCurrentScenario.getSize()) {
                int i3 = ScenarioManager.this.mCurrentScenario.mElements[i2];
                int i4 = i2 + 1;
                return Math.max(traverse((ScenarioNode) scenarioNode.mChildren.getOrDefault(Integer.valueOf(i3), null), i4), traverse((ScenarioNode) scenarioNode.mChildren.getOrDefault(0, null), i4));
            }
            ScenarioManager.sHistory.log("ThermalScenarioManager Error: deep=" + i2 + " > ELEMENT_MAX");
            ScenarioManager.sHistory.log(ScenarioManager.this.mCurrentScenario.toString());
            return scenarioNode.mScenarioName;
        }

        public void addScenario(SettingDTO.ScenarioDTO scenarioDTO) {
            int id = scenarioDTO.getID();
            List<Integer> define = scenarioDTO.getDefine();
            ScenarioNode scenarioNode = this.mRoot;
            for (Integer num : define) {
                num.intValue();
                ScenarioNode scenarioNode2 = (ScenarioNode) scenarioNode.mChildren.getOrDefault(num, null);
                if (scenarioNode2 == null) {
                    scenarioNode2 = new ScenarioNode();
                    scenarioNode.mChildren.put(num, scenarioNode2);
                }
                scenarioNode = scenarioNode2;
            }
            scenarioNode.mScenarioName = id;
        }

        public void init(SettingDTO settingDTO) {
            List<SettingDTO.ScenarioDTO> scenarios = settingDTO.getScenarios();
            if (scenarios.isEmpty()) {
                return;
            }
            this.mRoot.clear();
            scenarios.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermal.g
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f570a.addScenario((SettingDTO.ScenarioDTO) obj);
                }
            });
        }

        public boolean matchScenario() {
            int iTraverse = traverse(this.mRoot, 0);
            if (ScenarioManager.this.mCurrentScenario.mScenarioName == iTraverse) {
                return false;
            }
            ScenarioManager.this.mCurrentScenario.mScenarioName = iTraverse;
            return true;
        }

        @NonNull
        public String toString() {
            return this.mRoot.toString();
        }

        private ScenarioTree() {
            this.mRoot = new ScenarioNode();
        }
    }

    private static class ThermalScenario {
        private String[] mElementNames;
        private int[] mElements;
        private int mScenarioName;
        private int mSize;

        private ThermalScenario() {
        }

        public void changeElement(int i2, int i3) {
            if (i2 < this.mSize) {
                this.mElements[i2] = i3;
                return;
            }
            ScenarioManager.sHistory.log("ThermalScenarioManager on changeElement out of size: index=" + i2 + ", state=" + i3 + ", mSize=" + this.mSize);
        }

        public String getElementName(int i2) {
            if (i2 < this.mSize) {
                return this.mElementNames[i2];
            }
            ScenarioManager.sHistory.log("ThermalScenarioManager on changeElement out of size: index=" + i2 + ", mSize=" + this.mSize);
            return null;
        }

        public int getElementValue(String str) {
            for (int i2 = 0; i2 < this.mSize; i2++) {
                if (this.mElementNames[i2].equals(str)) {
                    return this.mElements[i2];
                }
            }
            return -1;
        }

        public int getSize() {
            return this.mSize;
        }

        public void init(int i2) {
            this.mSize = i2;
            this.mScenarioName = 0;
            this.mElements = new int[i2];
            this.mElementNames = new String[i2];
        }

        public void setDefault(int i2, String str, int i3) {
            if (i2 < this.mSize) {
                this.mElementNames[i2] = str;
                this.mElements[i2] = i3;
                return;
            }
            ScenarioManager.sHistory.log("ThermalScenarioManager on setDefault out of size: index=" + i2 + ", name=" + str + ", state=" + i3 + ", mSize=" + this.mSize);
        }

        @NonNull
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("<<< Current Scenario >>>");
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("current ScenarioName: ");
            sb.append(this.mScenarioName);
            sb.append(ShellUtils.COMMAND_LINE_END);
            if (this.mElements != null) {
                sb.append("Element list : \n");
                for (int i2 = 0; i2 < this.mElements.length; i2++) {
                    sb.append(this.mElementNames[i2]);
                    sb.append(" : ");
                    sb.append(this.mElements[i2]);
                    sb.append(ShellUtils.COMMAND_LINE_END);
                }
            }
            sb.append(ShellUtils.COMMAND_LINE_END);
            return sb.toString();
        }
    }

    static {
        HandlerThread handlerThread = new HandlerThread(TAG);
        sThread = handlerThread;
        handlerThread.start();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private ScenarioManager() {
        SettingProvider.getInstance().registerCallback(new IProviderCallback() { // from class: com.miui.powerkeeper.thermal.d
            @Override // com.miui.powerkeeper.thermal.setting.IProviderCallback
            public final void onChange(Object obj) {
                this.f567a.onNewData((SettingDTO) obj);
            }
        });
    }

    private String extraToString() {
        return "<<< Extra Info >>>" + ShellUtils.COMMAND_LINE_END + this.mExtra + ShellUtils.COMMAND_LINE_END + ShellUtils.COMMAND_LINE_END;
    }

    public static LocalLog getHistory() {
        return sHistory;
    }

    public static synchronized ScenarioManager getInstance() {
        try {
            if (sInstance == null) {
                sInstance = new ScenarioManager();
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public static HandlerThread getThread() {
        return sThread;
    }

    private void initListener(SettingDTO settingDTO) {
        List<String> listeners = settingDTO.getListeners();
        if (listeners.size() > 0) {
            uninstallListener();
            installListenerChecked(listeners, new ThermalFactory());
        }
    }

    private void installListenerChecked(List<String> list, ThermalFactory thermalFactory) {
        int size = list.size();
        this.mCurrentScenario.init(size);
        for (int i2 = 0; i2 < size; i2++) {
            String str = list.get(i2);
            IListener iListenerCreateListener = thermalFactory.createListener(str);
            if (iListenerCreateListener != null) {
                this.mCurrentScenario.setDefault(i2, str, iListenerCreateListener.initDefaultValue());
                iListenerCreateListener.register(this.mHandler, i2);
                this.mListeners.put(str, iListenerCreateListener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onNewData$0(SettingDTO settingDTO) {
        this.mScenarioTree.init(settingDTO);
        initListener(settingDTO);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewData(final SettingDTO settingDTO) {
        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.thermal.e
            @Override // java.lang.Runnable
            public final void run() {
                this.f568a.lambda$onNewData$0(settingDTO);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onScenarioChanged() {
        this.mEventsAggregator.notifyScenarioChanged(Integer.valueOf(this.mCurrentScenario.mScenarioName));
    }

    private void uninstallListener() {
        this.mListeners.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermal.a
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((IListener) obj).uninstall();
            }
        });
        this.mListeners.clear();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:21:0x003b  */
    public void dump(final PrintWriter printWriter, String[] strArr, int i2) {
        byte b2;
        String str = i2 < strArr.length ? strArr[i2] : "";
        switch (str.hashCode()) {
            case 3065394:
                if (!str.equals("curr")) {
                    b2 = -1;
                } else {
                    b2 = 0;
                }
                break;
            case 3568542:
                if (!str.equals("tree")) {
                    b2 = -1;
                } else {
                    b2 = 1;
                }
                break;
            case 926934164:
                if (!str.equals("history")) {
                    b2 = -1;
                } else {
                    b2 = 2;
                }
                break;
            case 1346159796:
                if (!str.equals("listener")) {
                    b2 = -1;
                } else {
                    b2 = 3;
                }
                break;
            default:
                b2 = -1;
                break;
        }
        if (b2 == 0) {
            printWriter.println(this.mCurrentScenario);
            return;
        }
        if (b2 == 1) {
            printWriter.println(this.mScenarioTree);
            return;
        }
        if (b2 == 2) {
            sHistory.dump(printWriter);
            return;
        }
        if (b2 == 3) {
            this.mListeners.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermal.b
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((IListener) obj).dump(printWriter);
                }
            });
            return;
        }
        printWriter.println(this.mCurrentScenario);
        printWriter.println(extraToString());
        this.mListeners.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermal.c
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((IListener) obj).dump(printWriter);
            }
        });
        sHistory.dump(printWriter);
    }

    public int getCurrentScenarioElementValue(String str) {
        return this.mCurrentScenario.getElementValue(str);
    }

    public int getCurrentScenarioName() {
        return this.mCurrentScenario.mScenarioName;
    }

    public Object getExtra(String str) {
        return this.mExtra.getOrDefault(str, null);
    }

    public boolean isGameModeApp(String str) {
        ForegroundGroupListener foregroundGroupListener = (ForegroundGroupListener) this.mListeners.get("ForegroundGroup");
        if (foregroundGroupListener == null) {
            return false;
        }
        return foregroundGroupListener.isGameModeApp(str);
    }
}
