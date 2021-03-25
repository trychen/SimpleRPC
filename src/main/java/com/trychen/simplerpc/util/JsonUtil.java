package com.trychen.simplerpc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    protected static Gson generalGson;

    public static Gson getGeneralGson() {
        if (generalGson == null) {
            generalGson = new GsonBuilder().create();
        }
        return generalGson;
    }
}
