package com.trychen.simplerpc.network;

import com.google.common.collect.Sets;
import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.framework.MessagePackageInfo;
import com.trychen.simplerpc.framework.RPCManager;
import com.trychen.simplerpc.framework.RawMessageListener;
import com.trychen.simplerpc.util.DataUtil;
import com.trychen.simplerpc.util.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BroadcastingServer implements Runnable {
    protected AtomicBoolean closeHook = new AtomicBoolean();
    protected Set<RawMessageListener> listeners = Sets.newConcurrentHashSet();

    @Override
    public void run() {
        byte[] buf = new byte[SimpleRPC.BUFFER_SIZE];//存储发来的消息

        try {
            //绑定端口的
            DatagramSocket ds = new DatagramSocket(SimpleRPC.PORT);
            ds.setReuseAddress(true);

            System.out.println("监听广播端口打开！");

            for (;;) {
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                ds.receive(dp);

                DataInputStream input = new DataInputStream(new ByteArrayInputStream(buf));
                byte[] headerBuf = new byte[DataUtil.readVarInt(input)];
                input.read(headerBuf);
                String header = new String(headerBuf, StandardCharsets.UTF_8);

                MessagePackageInfo packageInfo = JsonUtil.getGeneralGson().fromJson(header, MessagePackageInfo.class);

                byte[] dataBuf = new byte[DataUtil.readVarInt(input)];

                input.read(dataBuf);

//                System.out.println("收到信息：" + packageInfo.toString());
//                System.out.println("   长度：" + dataBuf.length);

                for (RawMessageListener listener : listeners) {
                    listener.on(packageInfo, dataBuf);
                }

                RPCManager.receive(packageInfo, dataBuf);

                if (closeHook.get()) break;
            }

            ds.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        closeHook.set(true);
    }

    public void addListener(RawMessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RawMessageListener listener) {
        listeners.remove(listener);
    }
    static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(InterfaceAddress::getBroadcast)
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }

        return broadcastList;
    }

    public static void main(String[] args) {
        SimpleRPC.init();
    }
}
