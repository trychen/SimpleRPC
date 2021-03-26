package com.trychen.simplerpc.test;

import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.annotation.RPC;

public class BigArrayTest {
    public static void main(String[] args) {
        Sender.INSTANCE.send(new byte[1024 * 100]);
    }

    public interface Sender { // 数据发送接口
        Sender INSTANCE = SimpleRPC.create(Sender.class); // 代理实例

        @RPC(value = "Test.SendBigOne", fast = true)
        void send(byte[] message); // 发送聊天信息
    }
}
