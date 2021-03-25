package com.trychen.simplerpc.framework;

import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.annotation.RPC;
import com.trychen.simplerpc.framework.persistence.ByteStreamPersistence;
import com.trychen.simplerpc.framework.persistence.JsonPersistence;
import com.trychen.simplerpc.framework.persistence.Persistence;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.logging.Level;

public class RPCManager {
    private static Map<Class<? extends Persistence>, Persistence> persistences = new HashMap<Class<? extends Persistence>, Persistence>() {{
        put(JsonPersistence.class, JsonPersistence.INSTANCE);
        put(ByteStreamPersistence.class, ByteStreamPersistence.INSTANCE);
    }};
    private static Map<String, List<RegisteredListener>> registeredListeners = new HashMap<>();

    public static Map<Class<? extends Persistence>, Persistence> getPersistences() {
        return persistences;
    }

    public static Persistence getPersistence(Class<? extends Persistence> persistence) {
        return persistences.getOrDefault(persistence, persistences.values().stream().findAny().get());
    }

    public static <T> T createProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(SimpleRPC.class.getClassLoader(), new Class[]{clazz}, RPCProxy.INSTANCE);
    }

    public static void registerListener(Object object) {
        for (Method method : object.getClass().getMethods()) {
            if (!method.isAnnotationPresent(RPC.class)) continue;
            RPC rpc = method.getAnnotation(RPC.class);

            try {
                registeredListeners.computeIfAbsent(rpc.value(), key -> new ArrayList<>()).add(new RegisteredListener(object, rpc, method));
                SimpleRPC.LOG.log(Level.FINE, "Registered @Bridge Receiver with key " + rpc.value() + " for " + method.toGenericString());
            } catch (Exception e) {
                SimpleRPC.LOG.log(Level.SEVERE, "Error while register @RPC for " + method.toGenericString(), e);
            }
        }
    }

    public static void receive(MessagePackageInfo packageInfo, byte[] data) {
        List<RegisteredListener> listeners = registeredListeners.getOrDefault(packageInfo.channel, Collections.emptyList());
        if (listeners.isEmpty()) return;

        for (RegisteredListener listener : listeners) {
            try {
                listener.receive(packageInfo, data);
            } catch (Exception e) {
                SimpleRPC.LOG.log(Level.SEVERE, "Error while receive RPC data for " + listener.getMethod().toGenericString(), e);
            }
        }
    }
}
