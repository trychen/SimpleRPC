package com.trychen.simplerpc.framework;

import java.util.UUID;

public class MessagePackageInfo {
    protected UUID id;
    protected String channel;
    protected String sender;
    protected String receiver;

    public MessagePackageInfo(String channel, String sender, String receiver) {
        this.id = UUID.randomUUID();
        this.channel = channel;
        this.sender = sender;
        this.receiver = receiver;
    }

    public UUID getId() {
        return id;
    }

    public MessagePackageInfo setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public MessagePackageInfo setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public String getSender() {
        return sender;
    }

    public MessagePackageInfo setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String getReceiver() {
        return receiver;
    }

    public MessagePackageInfo setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    @Override
    public String toString() {
        return "MessagePackage{" +
                "id=" + id +
                ", channel='" + channel + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }
}