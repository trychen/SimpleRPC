package com.trychen.simplerpc.framework;

public interface RawMessageListener {
    void on(MessagePackageInfo info, byte[] data);
}
