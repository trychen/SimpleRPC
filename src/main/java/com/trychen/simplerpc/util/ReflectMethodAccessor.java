package com.trychen.simplerpc.util;

import com.github.mouse0w0.fastreflection.MethodAccessor;

import java.lang.reflect.Method;

public class ReflectMethodAccessor implements MethodAccessor {
    protected Method method;

    public ReflectMethodAccessor(Method method) {
        this.method = method;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object invoke(Object o, Object... objects) throws Exception {
        return method.invoke(o, objects);
    }
}
