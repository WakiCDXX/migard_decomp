package com.xiaomi.push.scene;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class SceneDataCacher {
    private static final boolean ADD_SAME_DATA_REJECTED = true;
    private static final String SP_NAME = "sp_scene_data";
    private FileCache mFileCache;
    private MemoryCache mMemoryCache = new MemoryCache();

    private static class FileCache {
        private SharedPreferences mSp;

        FileCache(Context context) {
            this.mSp = context.getSharedPreferences(SceneDataCacher.SP_NAME, 0);
        }

        void addAll(List<Rule> list) {
            SharedPreferences.Editor editorEdit = this.mSp.edit();
            for (Rule rule : list) {
                String strKey = rule.key();
                if (this.mSp.contains(strKey)) {
                    SceneLog.w("file reject new:" + rule);
                } else {
                    editorEdit.putString(strKey, rule.value());
                }
            }
            editorEdit.commit();
        }

        void clearAll() {
            this.mSp.edit().clear().commit();
        }

        Map<String, ?> getAll() {
            return this.mSp.getAll();
        }

        void removeAll(List<String> list) {
            SharedPreferences.Editor editorEdit = this.mSp.edit();
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                editorEdit.remove(it.next());
            }
            editorEdit.commit();
        }

        void update(List<Rule> list) {
            SharedPreferences.Editor editorEdit = this.mSp.edit();
            for (Rule rule : list) {
                editorEdit.putString(rule.key(), rule.value());
            }
            editorEdit.commit();
        }
    }

    private static class MemoryCache {
        private List<Rule> mAll = new ArrayList();

        MemoryCache() {
        }

        private void addOrReplace(Rule rule) {
            for (int i2 = 0; i2 < this.mAll.size(); i2++) {
                if (TextUtils.equals(rule.key(), this.mAll.get(i2).key())) {
                    SceneLog.w("memory reject new:" + rule);
                    return;
                }
            }
            addUnChecked(rule);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addUnChecked(Rule rule) {
            this.mAll.add(rule);
        }

        void addAll(List<Rule> list) {
            Iterator<Rule> it = list.iterator();
            while (it.hasNext()) {
                addOrReplace(it.next());
            }
        }

        void clearAll() {
            this.mAll.clear();
        }

        List<Rule> find(int i2) {
            ArrayList arrayList = new ArrayList();
            for (Rule rule : this.mAll) {
                if (rule.sceneType == i2) {
                    arrayList.add(rule);
                }
            }
            return arrayList;
        }

        List<Rule> getAll() {
            return new ArrayList(this.mAll);
        }

        void remove(String str) {
            for (int i2 = 0; i2 < this.mAll.size(); i2++) {
                if (TextUtils.equals(str, this.mAll.get(i2).key())) {
                    this.mAll.remove(i2);
                    return;
                }
            }
        }

        void removeAll(List<String> list) {
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                remove(it.next());
            }
        }
    }

    private static class Parser {
        private static final String KEY_END_HOUR = "endHour";
        private static final String KEY_EXPIRE_TIME = "expireTime";
        private static final String KEY_FOREGROUND_PKG = "foregroundPkg";
        private static final String KEY_FREQUENCY = "frequency";
        private static final String KEY_LOCAL_LAST_TRIGGER = "localLastTrigger";
        private static final String KEY_RULE_ID = "ruleId";
        private static final String KEY_SCENE_ATTR = "sceneAttr";
        private static final String KEY_SCENE_TYPE = "sceneType";
        private static final String KEY_START_HOUR = "startHour";

        private Parser() {
        }

        static Rule parseRule(String str) {
            SimpleJson simpleJson = new SimpleJson(str);
            Rule rule = new Rule();
            rule.ruleId = simpleJson.optString(KEY_RULE_ID);
            rule.sceneType = simpleJson.optInt(KEY_SCENE_TYPE);
            rule.startHour = simpleJson.optInt(KEY_START_HOUR);
            rule.endHour = simpleJson.optInt(KEY_END_HOUR);
            rule.frequency = simpleJson.optInt(KEY_FREQUENCY);
            rule.expireTime = simpleJson.optLong(KEY_EXPIRE_TIME);
            rule.localLastTrigger = simpleJson.optLong(KEY_LOCAL_LAST_TRIGGER);
            Rule.SceneAttr sceneAttr = new Rule.SceneAttr();
            sceneAttr.foregroundPkg = simpleJson.optJsonObject(KEY_SCENE_ATTR).optString(KEY_FOREGROUND_PKG);
            rule.constraints = sceneAttr;
            return rule;
        }

        static List<Rule> parseServer(String str) {
            ArrayList arrayList = new ArrayList();
            try {
                JSONArray jSONArray = new JSONArray(str);
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    arrayList.add(parseRule(jSONArray.optJSONObject(i2).toString()));
                }
            } catch (Exception e2) {
                SceneLog.w("parse json error " + e2);
            }
            return arrayList;
        }

        static String toJson(Rule rule) {
            SimpleJson simpleJson = new SimpleJson();
            simpleJson.optPut(KEY_RULE_ID, rule.ruleId);
            simpleJson.optPut(KEY_SCENE_TYPE, Integer.valueOf(rule.sceneType));
            simpleJson.optPut(KEY_START_HOUR, Integer.valueOf(rule.startHour));
            simpleJson.optPut(KEY_END_HOUR, Integer.valueOf(rule.endHour));
            simpleJson.optPut(KEY_FREQUENCY, Integer.valueOf(rule.frequency));
            simpleJson.optPut(KEY_EXPIRE_TIME, Long.valueOf(rule.expireTime));
            simpleJson.optPut(KEY_LOCAL_LAST_TRIGGER, Long.valueOf(rule.localLastTrigger));
            if (rule.constraints != null) {
                SimpleJson simpleJson2 = new SimpleJson();
                simpleJson2.optPut(KEY_FOREGROUND_PKG, rule.constraints.foregroundPkg);
                simpleJson.optPut(KEY_SCENE_ATTR, simpleJson2);
            }
            return simpleJson.toString();
        }
    }

    public static class Rule {
        private SceneAttr constraints;
        private int endHour;
        private long expireTime;
        private int frequency;
        private long localLastTrigger;
        private String ruleId;
        private int sceneType;
        private int startHour;

        private static class SceneAttr {
            private String foregroundPkg;

            private SceneAttr() {
            }

            public String toString() {
                return this.foregroundPkg;
            }
        }

        private static boolean matchConstraints(SceneAttr sceneAttr, String str) {
            return sceneAttr == null || TextUtils.isEmpty(sceneAttr.foregroundPkg) || ToolUtils.contains(sceneAttr.foregroundPkg, str);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String value() {
            return Parser.toJson(this);
        }

        boolean canTrigger(String str) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(jCurrentTimeMillis);
            int i2 = calendar.get(11);
            long j2 = jCurrentTimeMillis - this.localLastTrigger;
            if (SceneLog.DEBUG.booleanValue()) {
                StringBuilder sb = new StringBuilder();
                sb.append("canTrigger.isValid:");
                sb.append(!isExpired());
                SceneLog.i(sb.toString());
                SceneLog.i("canTrigger.hour:" + this.startHour + "<=" + i2 + "<=" + this.endHour);
                SceneLog.i("canTrigger.lastTrigger:" + jCurrentTimeMillis + Constants.ACCEPT_TIME_SEPARATOR_SERVER + this.localLastTrigger + "=" + j2 + ">" + this.frequency);
                StringBuilder sb2 = new StringBuilder();
                sb2.append("canTrigger.constraints:");
                sb2.append(this.constraints);
                sb2.append("=");
                sb2.append(str);
                SceneLog.i(sb2.toString());
            }
            return !isExpired() && this.startHour <= i2 && i2 <= this.endHour && j2 > ((long) this.frequency) && matchConstraints(this.constraints, str);
        }

        String getRuleId() {
            return this.ruleId;
        }

        int getSceneType() {
            return this.sceneType;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > this.expireTime;
        }

        String key() {
            return this.ruleId;
        }

        public String toString() {
            return SceneLog.DEBUG.booleanValue() ? value() : key();
        }

        private Rule() {
        }
    }

    private static class SimpleJson {
        private JSONObject jsonObject;

        private void initJsonObj(JSONObject jSONObject) {
            this.jsonObject = jSONObject;
            if (jSONObject == null) {
                this.jsonObject = new JSONObject();
            }
        }

        int optInt(String str) {
            return this.jsonObject.optInt(str);
        }

        SimpleJson optJsonObject(String str) {
            return new SimpleJson(this.jsonObject.optJSONObject(str));
        }

        long optLong(String str) {
            return this.jsonObject.optLong(str);
        }

        void optPut(String str, Object obj) {
            try {
                this.jsonObject.put(str, obj);
            } catch (Exception unused) {
            }
        }

        String optString(String str) {
            return this.jsonObject.optString(str);
        }

        public String toString() {
            return this.jsonObject.toString();
        }

        private SimpleJson() {
            initJsonObj(null);
        }

        private SimpleJson(JSONObject jSONObject) {
            initJsonObj(jSONObject);
        }

        SimpleJson(String str) {
            JSONObject jSONObject;
            if (str != null) {
                try {
                    jSONObject = new JSONObject(str);
                } catch (Exception unused) {
                    jSONObject = null;
                }
            } else {
                jSONObject = null;
            }
            initJsonObj(jSONObject);
        }
    }

    SceneDataCacher(Context context) {
        this.mFileCache = new FileCache(context);
    }

    private void clearAll() {
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("cache clear all " + getAll());
        }
        this.mMemoryCache.clearAll();
        this.mFileCache.clearAll();
    }

    List<Rule> find(int i2) {
        return this.mMemoryCache.find(i2);
    }

    List<Rule> getAll() {
        return this.mMemoryCache.getAll();
    }

    void load() {
        Map<String, ?> all = this.mFileCache.getAll();
        if (all == null || all.size() <= 0) {
            return;
        }
        Iterator<String> it = all.keySet().iterator();
        while (it.hasNext()) {
            this.mMemoryCache.addUnChecked(Parser.parseRule(String.valueOf(all.get(it.next()))));
        }
    }

    void putJson(String str, boolean z2) {
        List<Rule> server = Parser.parseServer(str);
        if (ToolUtils.isEmptyList(server)) {
            return;
        }
        if (z2) {
            List<Rule> all = getAll();
            if (!ToolUtils.isEmptyList(all)) {
                for (Rule rule : server) {
                    for (Rule rule2 : all) {
                        if (TextUtils.equals(rule.key(), rule2.key())) {
                            rule.localLastTrigger = rule2.localLastTrigger;
                            break;
                        }
                    }
                }
                clearAll();
            }
        }
        this.mMemoryCache.addAll(server);
        this.mFileCache.addAll(server);
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i(z2 + " cache add succ " + server);
        }
    }

    void recordTrigger(List<Rule> list) {
        if (ToolUtils.isEmptyList(list)) {
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        Iterator<Rule> it = list.iterator();
        while (it.hasNext()) {
            it.next().localLastTrigger = jCurrentTimeMillis;
        }
        this.mFileCache.update(list);
    }

    void remove(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        removeAll(arrayList);
    }

    void removeAll(List<String> list) {
        if (ToolUtils.isEmptyList(list)) {
            return;
        }
        this.mMemoryCache.removeAll(list);
        this.mFileCache.removeAll(list);
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("cache remove succ " + list);
        }
    }
}
