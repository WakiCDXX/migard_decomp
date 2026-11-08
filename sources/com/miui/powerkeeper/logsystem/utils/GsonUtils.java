package com.miui.powerkeeper.logsystem.utils;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class GsonUtils {
    public static final String TAG = "LS-GsonUtils";
    private static final Gson sGson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class cls) {
            this.clazz = cls;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type[] getActualTypeArguments() {
            return new Type[]{this.clazz};
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type getOwnerType() {
            return null;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type getRawType() {
            return List.class;
        }
    }

    private GsonUtils() {
    }

    public static String GsonString(Object obj) {
        Gson gson = sGson;
        if (gson != null) {
            return gson.toJson(obj);
        }
        return null;
    }

    public static <T> T GsonToBeanSafety(String str, Class<T> cls) {
        try {
            return (T) sGson.fromJson(str, (Class) cls);
        } catch (Exception e2) {
            Log.d(TAG, e2.toString());
            return null;
        }
    }

    public static <T> List<T> GsonToList(String str, Class<T> cls) {
        Gson gson = sGson;
        if (gson != null) {
            return (List) gson.fromJson(str, new ParameterizedTypeImpl(cls));
        }
        return null;
    }

    public static <T> List<Map<String, T>> GsonToListMaps(String str) {
        Gson gson = sGson;
        if (gson != null) {
            return (List) gson.fromJson(str, new TypeToken<List<Map<String, T>>>() { // from class: com.miui.powerkeeper.logsystem.utils.GsonUtils.1
            }.getType());
        }
        return null;
    }

    public static <T> Map<String, T> GsonToMaps(String str) {
        Gson gson = sGson;
        if (gson != null) {
            return (Map) gson.fromJson(str, new TypeToken<Map<String, T>>() { // from class: com.miui.powerkeeper.logsystem.utils.GsonUtils.2
            }.getType());
        }
        return null;
    }

    public static Gson getGson() {
        return sGson;
    }

    public static <T> List<T> GsonToList(JsonArray jsonArray, Class<T> cls) {
        Gson gson = sGson;
        if (gson != null) {
            return (List) gson.fromJson(jsonArray, new ParameterizedTypeImpl(cls));
        }
        return null;
    }

    public static <T> Map<String, T> GsonToMaps(Object obj) {
        Gson gson = sGson;
        if (gson != null) {
            return (Map) gson.fromJson(GsonString(obj), (Class) Map.class);
        }
        return null;
    }
}
