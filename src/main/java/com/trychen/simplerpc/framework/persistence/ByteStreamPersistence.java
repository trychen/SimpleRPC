package com.trychen.simplerpc.framework.persistence;

import com.trychen.bytedatastream.ByteSerialization;

import java.io.IOException;
import java.lang.reflect.Type;

public enum ByteStreamPersistence implements Persistence {
    INSTANCE;

    @Override
    public byte[] serialize(Object[] objects, Type[] types) throws IOException {
        if (types.length == 0) {
            return new byte[0];
        }
        return ByteSerialization.serialize(objects, types);
    }

    @Override
    public Object[] deserialize(byte[] bytes, Type[] types) throws IOException {
        if (types.length == 0) {
            return new Object[0];
        }
        return ByteSerialization.deserialize(bytes, types);
    }
}
