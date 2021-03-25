package com.trychen.simplerpc.test;

import com.trychen.simplerpc.SimpleRPC;
import com.trychen.simplerpc.annotation.RPC;
import com.trychen.simplerpc.framework.MessagePackageInfo;

import java.util.Scanner;

public class ChattingRoom {
    public static void main(String[] args) {
        SimpleRPC.init(); // 启动服务
        SimpleRPC.register(new Receiver()); // 注册接收器

        System.out.println("直接输入聊天文本即可开始聊天！"); // 提示信息
        Scanner scanner = new Scanner(System.in); // 创建 Scanner
        while (scanner.hasNext()) {
            Sender.INSTANCE.send(scanner.nextLine()); // 发送聊天信息
        }
    }

    public static class Receiver { // 数据接收
        @RPC("ChattingRoom.SendMessage")
        public void received(MessagePackageInfo info, String message) {
            System.out.println(info.getSender() + "：" + message); // 输出其它设备发送的信息
        }
    }

    public interface Sender { // 数据发送接口
        Sender INSTANCE = SimpleRPC.create(Sender.class); // 代理实例

        @RPC("ChattingRoom.SendMessage")
        void send(String message); // 发送聊天信息
    }
}
