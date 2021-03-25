package com.trychen.simplerpc.framework.devices;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.annotation.RPC;

import java.util.concurrent.TimeUnit;

public enum DeviceManager {
    INSTANCE;

    protected static Cache<String, String> devices = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    @RPC("DeviceManager.Join")
    public void join(String deviceName) {
        devices.put(deviceName, deviceName);
    }

    public static void init() {
        SimpleRPC.register(INSTANCE);
    }
}
