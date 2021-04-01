package com.trychen.simplerpc.network;

import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.framework.MessagePackageInfo;
import com.trychen.simplerpc.util.DataUtil;
import com.trychen.simplerpc.util.GZIPUtils;
import com.trychen.simplerpc.util.JsonUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.trychen.simplerpc.SimpleRPC.SINGLE_PART_SIZE;

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
        if (SimpleRPC.GZIP) data = GZIPUtils.compress(data);
        if (data.length > SINGLE_PART_SIZE) {
            // 写头部
            object.setParts((data.length / SINGLE_PART_SIZE) + (data.length % SINGLE_PART_SIZE > 0 ? 1 : 0));

            for (Integer i = 0; i < object.getParts(); i++) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(SimpleRPC.BUFFER_SIZE);
                DataOutputStream output = new DataOutputStream(byteArrayOutputStream);

                object.setNumberOfPart(i);
                byte[] subPartData = ArrayUtils.subarray(data, i * SINGLE_PART_SIZE, Math.min(data.length, (i + 1) * SINGLE_PART_SIZE));

                String header = JsonUtil.getGeneralGson().toJson(object);
                byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
                DataUtil.writeVarInt(output, headerBytes.length);
                output.write(headerBytes);

                // 写数据
                DataUtil.writeVarInt(output, subPartData.length);
                output.write(subPartData);

                byte[] message = byteArrayOutputStream.toByteArray();

                if (SimpleRPC.BROADCAST_ADDRESS == null) {
                    SimpleRPC.BROADCAST_ADDRESS = listAllBroadcastAddresses().toArray(new InetAddress[0]);
                }

                for (InetAddress address : SimpleRPC.BROADCAST_ADDRESS) try {
                    rawBroadcast(address, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(SimpleRPC.BUFFER_SIZE);
            DataOutputStream output = new DataOutputStream(byteArrayOutputStream);
            // 写头部
            String header = JsonUtil.getGeneralGson().toJson(object);
            byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
            DataUtil.writeVarInt(output, headerBytes.length);
            output.write(headerBytes);

            // 写数据
            DataUtil.writeVarInt(output, data.length);
            output.write(data);

            byte[] message = byteArrayOutputStream.toByteArray();

            if (SimpleRPC.BROADCAST_ADDRESS == null) {
                SimpleRPC.BROADCAST_ADDRESS = listAllBroadcastAddresses().toArray(new InetAddress[0]);
            }

            for (InetAddress address : SimpleRPC.BROADCAST_ADDRESS) try {
                rawBroadcast(address, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
