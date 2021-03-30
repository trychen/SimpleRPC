package com.trychen.simplerpc.framework;

import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.annotation.RPC;
import com.trychen.simplerpc.framework.persistence.ByteStreamPersistence;
import com.trychen.simplerpc.framework.persistence.Persistence;
import com.trychen.simplerpc.network.BroadcastingClient;

import java.lang.reflect.*;
import java.util.logging.Level;

public enum RPCProxy implements InvocationHandler {
    INSTANCE;

    @SuppressWarnings("Duplicates")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            SimpleRPC.LOG.log(Level.SEVERE, "default method isn't support in this version", new IllegalAccessException());
            return null;
        }

        RPC rpc = method.getAnnotation(RPC.class);

        if (rpc == null) return null;
        String channel = rpc.value();

        Persistence persistence = rpc.fast() ? ByteStreamPersistence.INSTANCE : RPCManager.getPersistence(rpc.persistence());

        byte[] bytes = persistence.serialize(args == null ? new Object[0] : args, method.getGenericParameterTypes());

        BroadcastingClient.broadcast(channel, bytes);

        return null;
    }
}
