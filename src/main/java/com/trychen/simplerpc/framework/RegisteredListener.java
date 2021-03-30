package com.trychen.simplerpc.framework;

import com.github.mouse0w0.fastreflection.FastReflection;
import com.github.mouse0w0.fastreflection.MethodAccessor;
import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.annotation.RPC;
import com.trychen.simplerpc.framework.persistence.ByteStreamPersistence;
import com.trychen.simplerpc.framework.persistence.Persistence;
import com.trychen.simplerpc.util.ReflectMethodAccessor;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;

public class RegisteredListener {
    protected Object instance;
    protected RPC rpc;
    protected MethodAccessor methodAccessor;
    protected Method method;
    protected boolean withMessagePackageInfo;
    protected Persistence persistence;
    protected Type[] actualParameterTypes;

    public RegisteredListener(Object instance, RPC rpc, Method method) throws Exception {
        this.instance = instance;
        this.rpc = rpc;
        this.method = method;
        this.methodAccessor = SimpleRPC.FAST_REFLECTION ? FastReflection.create(method) : new ReflectMethodAccessor(method);
        this.persistence = rpc.fast() ? ByteStreamPersistence.INSTANCE : RPCManager.getPersistence(rpc.persistence());
        this.withMessagePackageInfo = method.getParameterTypes().length > 0 && MessagePackageInfo.class.isAssignableFrom(method.getParameterTypes()[0]);
        this.actualParameterTypes = withMessagePackageInfo ? ArrayUtils.remove(method.getGenericParameterTypes(), 0) :method.getGenericParameterTypes();
    }

    public RPC getRpc() {
        return rpc;
    }

    public MethodAccessor getMethodAccessor() {
        return methodAccessor;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isWithMessagePackageInfo() {
        return withMessagePackageInfo;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public Type[] getActualParameterTypes() {
        return actualParameterTypes;
    }

    public void receive(MessagePackageInfo packageInfo, byte[] data) throws Exception {
        Object[] objects = persistence.deserialize(data, actualParameterTypes);
        if (withMessagePackageInfo) {
            objects = ArrayUtils.add(objects, 0, packageInfo);
        }
        methodAccessor.invoke(instance, objects);
    }
}
