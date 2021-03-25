package com.trychen.simplerpc.framework.devices;

import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.annotation.RPC;

public interface DeviceSender {
    DeviceSender INSTANCE = SimpleRPC.create(DeviceSender.class);

    @RPC("DeviceManager.Join")
    void join(String deviceName);
}
