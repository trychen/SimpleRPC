package com.trychen.simplerpc;

import com.trychen.simplerpc.framework.RPCManager;
import com.trychen.simplerpc.framework.devices.DeviceSender;
import com.trychen.simplerpc.network.BroadcastingClient;
import com.trychen.simplerpc.network.BroadcastingServer;
import com.trychen.simplerpc.framework.devices.DeviceManager;

import java.net.InetAddress;
import java.util.List;
import java.util.logging.Logger;

public class SimpleRPC {
    public static Logger LOG = Logger.getLogger("SimpleRPC");

    public static boolean STARTED = false;
    public static boolean GZIP = true;
    public static int BUFFER_SIZE = 64 * 1024;
    public static int PORT = 25540;
    public static int SINGLE_PART_SIZE = 1200;
    public static boolean FAST_REFLECTION = true;

    public static InetAddress[] BROADCAST_ADDRESS;
    public static String CUSTOM_NAME;
    public static BroadcastingServer SERVER;
    public static Thread THREAD;

    public static void init() {
        STARTED = true;
        SERVER = new BroadcastingServer();
        THREAD = new Thread(SERVER);
        THREAD.start();

        DeviceManager.init();
        DeviceSender.INSTANCE.join(BroadcastingClient.buildSenderName());
    }

    public static <T> T create(Class<T> clazz) {
        return RPCManager.createProxy(clazz);
    }

    public static void register(Object object) {
        RPCManager.registerListener(object);
    }

    public static void close() {
        if (SERVER != null) SERVER.close();
    }
}
