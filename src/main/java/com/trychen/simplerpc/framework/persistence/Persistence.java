package com.trychen.simplerpc.framework.persistence;

import java.io.IOException;
import java.lang.reflect.Type;

public interface Persistence {
    byte[] serialize(Object[] objects, Type[] types) throws IOException;
    Object[] deserialize(byte[] bytes, Type[] types) throws IOException;
}
