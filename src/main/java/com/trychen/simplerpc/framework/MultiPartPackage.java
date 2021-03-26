package com.trychen.simplerpc.framework;

import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.util.GZIPUtils;

public class MultiPartPackage {
    private MessagePackageInfo info;
    private byte[][] allParts;
    private int size;

    public MultiPartPackage(MessagePackageInfo info) {
        this.info = info;
        this.allParts = new byte[info.getParts()][];
    }

    public MessagePackageInfo getInfo() {
        return info;
    }

    public void accept(MessagePackageInfo info, byte[] other) {
        allParts[info.getNumberOfPart()] = other;
        size += other.length;
    }

    public boolean isComplete() {
        for (byte[] packet : allParts) {
            if (packet == null) return false;
        }
        return true;
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[size];
        int cursor = 0;
        for (byte[] packet : allParts) {
            System.arraycopy(packet, 0, bytes, cursor, packet.length);
            cursor += packet.length;
        }

        return bytes;
    }
}
