package com.miui.powerkeeper.utils;

import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ClusterUtils {
    public static String ADD_REBIND_TASK_BIG = null;
    public static String ADD_REBIND_TASK_LIT = null;
    public static String ADD_REBIND_TASK_MID = null;
    public static String CLUSTER_ADD_BCLUS = null;
    public static String CLUSTER_ADD_LCLUS = null;
    public static String CLUSTER_ADD_MCLUS = null;
    public static String CLUSTER_DEL_BCLUS = null;
    public static String CLUSTER_DEL_LCLUS = null;
    public static String CLUSTER_DEL_MCLUS = null;
    public static String CLUSTER_ROOT = null;
    public static String CLUSTER_SHOW_BCLUS = null;
    public static String CLUSTER_SHOW_LCLUS = null;
    public static String CLUSTER_SHOW_MCLUS = null;
    public static String DEL_REBIND_TASK_BIG = null;
    public static String DEL_REBIND_TASK_LIT = null;
    public static String DEL_REBIND_TASK_MID = null;
    private static final String TAG = "ClusterUtils";

    public enum Cluster {
        LCLUS,
        MCLUS,
        BCLUS,
        LTASK,
        MTASK,
        BTASK
    }

    static {
        String affinityRoot = getAffinityRoot();
        CLUSTER_ROOT = affinityRoot;
        if (affinityRoot != null) {
            CLUSTER_ADD_LCLUS = CLUSTER_ROOT + "/add_lclus_affinity_uidlist";
            CLUSTER_DEL_LCLUS = CLUSTER_ROOT + "/del_lclus_affinity_uidlist";
            CLUSTER_SHOW_LCLUS = CLUSTER_ROOT + "/show_lclus_affinity_uidlist";
            CLUSTER_ADD_MCLUS = CLUSTER_ROOT + "/add_mclus_affinity_uidlist";
            CLUSTER_DEL_MCLUS = CLUSTER_ROOT + "/del_mclus_affinity_uidlist";
            CLUSTER_SHOW_MCLUS = CLUSTER_ROOT + "/show_mclus_affinity_uidlist";
            CLUSTER_ADD_BCLUS = CLUSTER_ROOT + "/add_bclus_affinity_uidlist";
            CLUSTER_DEL_BCLUS = CLUSTER_ROOT + "/del_bclus_affinity_uidlist";
            CLUSTER_SHOW_BCLUS = CLUSTER_ROOT + "/show_bclus_affinity_uidlist";
            ADD_REBIND_TASK_LIT = CLUSTER_ROOT + "/add_rebind_task_lit";
            ADD_REBIND_TASK_MID = CLUSTER_ROOT + "/add_rebind_task_mid";
            ADD_REBIND_TASK_BIG = CLUSTER_ROOT + "/add_rebind_task_big";
            DEL_REBIND_TASK_LIT = CLUSTER_ROOT + "/del_rebind_task_lit";
            DEL_REBIND_TASK_MID = CLUSTER_ROOT + "/del_rebind_task_mid";
            DEL_REBIND_TASK_BIG = CLUSTER_ROOT + "/del_rebind_task_big";
        }
    }

    public static void addAppToCluster(int i2, Cluster cluster) throws Throwable {
        String str;
        if (cluster == Cluster.LCLUS) {
            str = CLUSTER_ADD_LCLUS;
        } else if (cluster == Cluster.MCLUS) {
            str = CLUSTER_ADD_MCLUS;
        } else if (cluster != Cluster.BCLUS) {
            return;
        } else {
            str = CLUSTER_ADD_BCLUS;
        }
        FileUtil.writeToFile(str, String.valueOf(i2));
    }

    public static void addAppsToCluster(List<Integer> list, Cluster cluster) throws Throwable {
        String str;
        if (cluster == Cluster.LCLUS) {
            str = CLUSTER_ADD_LCLUS;
        } else if (cluster == Cluster.MCLUS) {
            str = CLUSTER_ADD_MCLUS;
        } else if (cluster != Cluster.BCLUS) {
            return;
        } else {
            str = CLUSTER_ADD_BCLUS;
        }
        FileUtil.writeToFile(str, listToString(list, ':'));
    }

    public static void addProcessToCluster(List<Integer> list, Cluster cluster) throws Throwable {
        String str;
        if (cluster == Cluster.LTASK) {
            str = ADD_REBIND_TASK_LIT;
        } else if (cluster == Cluster.MTASK) {
            str = ADD_REBIND_TASK_MID;
        } else if (cluster != Cluster.BTASK) {
            return;
        } else {
            str = ADD_REBIND_TASK_BIG;
        }
        FileUtil.writeToFile(str, listToString(list, ':'));
    }

    public static void delAppFromCluster(int i2, Cluster cluster) throws Throwable {
        String str;
        if (cluster == Cluster.LCLUS) {
            str = CLUSTER_DEL_LCLUS;
        } else if (cluster == Cluster.MCLUS) {
            str = CLUSTER_DEL_MCLUS;
        } else if (cluster != Cluster.BCLUS) {
            return;
        } else {
            str = CLUSTER_DEL_BCLUS;
        }
        FileUtil.writeToFile(str, String.valueOf(i2));
    }

    public static void delAppsFromCluster(List<Integer> list, Cluster cluster) throws Throwable {
        String str;
        if (cluster == Cluster.LCLUS) {
            str = CLUSTER_DEL_LCLUS;
        } else if (cluster == Cluster.MCLUS) {
            str = CLUSTER_DEL_MCLUS;
        } else if (cluster != Cluster.BCLUS) {
            return;
        } else {
            str = CLUSTER_DEL_BCLUS;
        }
        FileUtil.writeToFile(str, listToString(list, ':'));
    }

    public static void delProcessFromCluster(List<Integer> list, Cluster cluster) throws Throwable {
        String str;
        if (cluster == Cluster.LTASK) {
            str = DEL_REBIND_TASK_LIT;
        } else if (cluster == Cluster.MTASK) {
            str = DEL_REBIND_TASK_MID;
        } else if (cluster != Cluster.BTASK) {
            return;
        } else {
            str = DEL_REBIND_TASK_BIG;
        }
        FileUtil.writeToFile(str, listToString(list, ':'));
    }

    private static String getAffinityRoot() {
        String[] strArr = {"/sys/module/metis/parameters", "/sys/module/migt/parameters", "/sys/module/turbo_sched/parameters"};
        for (int i2 = 0; i2 < 3; i2++) {
            String str = strArr[i2];
            File file = new File(str);
            if (file.exists() && file.canRead()) {
                File file2 = new File(str + "/add_lclus_affinity_uidlist");
                if (file2.exists() && file2.canWrite()) {
                    return str;
                }
            }
        }
        Log.d(TAG, "can not find cluster affinity files");
        return null;
    }

    public static String listToString(List list, char c2) {
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < list.size(); i2++) {
            sb.append(list.get(i2));
            if (i2 < list.size() - 1) {
                sb.append(c2);
            }
        }
        return sb.toString();
    }

    public static List<Integer> showCluster(Cluster cluster) throws Throwable {
        ArrayList arrayList = new ArrayList();
        String fromFile = FileUtil.readFromFile(cluster == Cluster.LCLUS ? CLUSTER_SHOW_LCLUS : cluster == Cluster.MCLUS ? CLUSTER_SHOW_MCLUS : CLUSTER_SHOW_BCLUS);
        if (!TextUtils.isEmpty(fromFile)) {
            for (String str : fromFile.split(":")) {
                arrayList.add(Integer.valueOf(Integer.parseInt(str)));
            }
        }
        return arrayList;
    }
}
