package com.trychen.simplerpc.network;

import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.framework.MessagePackageInfo;
import com.trychen.simplerpc.util.DataUtil;
import com.trychen.simplerpc.util.JsonUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BroadcastingClient {
    public static void broadcast(String channel, byte[] data) {
        try {
            MessagePackageInfo info = new MessagePackageInfo(channel, buildSenderName(), "ALL");
            broadcast(info, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(MessagePackageInfo object, byte[] data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(SimpleRPC.BUFFER_SIZE);
        DataOutputStream output = new DataOutputStream(byteArrayOutputStream);

        // 写头部
        String header = JsonUtil.getGeneralGson().toJson(object);
        DataUtil.writeVarInt(output, header.length());
        output.write(header.getBytes(StandardCharsets.UTF_8));

        // 写数据
        DataUtil.writeVarInt(output, data.length);
        output.write(data);

        byte[] message = byteArrayOutputStream.toByteArray();
        listAllBroadcastAddresses().forEach(address -> {
            try {
                rawBroadcast(address, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void rawBroadcast(InetAddress address, byte[] raw) throws IOException {
        DatagramSocket ds = new DatagramSocket();
        DatagramPacket dp = new DatagramPacket(raw, raw.length, address, SimpleRPC.PORT);
        ds.send(dp);
        ds.close();
    }

    public static List<InetAddress> listAllBroadcastAddresses() throws UnknownHostException {
        try {
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
        } catch (Exception e) {
            return Collections.singletonList(InetAddress.getByName("255.255.255.255"));
        }
    }

    public static String buildSenderName() {
        if (SimpleRPC.CUSTOM_NAME != null) return SimpleRPC.CUSTOM_NAME;

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return UUID.randomUUID().toString();
        }
    }
}
