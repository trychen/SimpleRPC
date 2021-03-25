package com.trychen.simplerpc.framework.persistence;

import com.google.gson.*;
import com.trychen.simplerpc.util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum JsonPersistence implements Persistence {
    INSTANCE;

    private Charset charset = StandardCharsets.UTF_8;

    @Override
    public byte[] serialize(Object[] objects, Type[] types) throws IOException {
        JsonArray jsonArray = new JsonArray();

        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i]; if (object == null) continue;
            Type type = types[i];

            JsonElement json = JsonUtil.getGeneralGson().toJsonTree(object, type);
            jsonArray.add(json);
        }

        return jsonArray.toString().getBytes(charset);
    }

    @Override
    public Object[] deserialize(byte[] bytes, Type[] types) throws IOException {
        String json = new String(bytes, charset);
        JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
        Object[] objects = new Object[types.length];

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement element = jsonArray.get(i);

            if (element != null && !element.isJsonNull()) {
                objects[i] = JsonUtil.getGeneralGson().fromJson(element, types[i]);
            }
        }

        return objects;
    }
}
