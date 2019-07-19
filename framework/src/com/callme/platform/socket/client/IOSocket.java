package com.callme.platform.socket.client;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.callme.platform.socket.engine.Manager;
import com.callme.platform.socket.engine.PacketParser;
import com.callme.platform.socket.engine.Parser;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：Socket 相关配置 & builder
 * 作者：huangyong
 * 创建时间：2019/1/21
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class IOSocket {
    private final static String DEFAULT_HOST = "gms.huwochuxing.com";
    private final static int DEFAULT_PORT = 5050;

    private static AtomicInteger counter = new AtomicInteger(0);
    private int socketId = counter.getAndIncrement();

    private Manager io;

    private IOSocket(Builder builder) {
        String host = builder.host;
        int port = builder.port;
        Options options = builder.options;
        io = new Manager(host, port, options);
    }

    public void connect() {
        io.connect();
    }

    public void connect(ConnectionListener listener) {
        io.connect(listener);
    }

    public void setConnectListener(ConnectionListener listener) {
        io.setConnectionListener(listener);
    }

    public void shutdown() {
        io.shutdown();
    }

    public void close() {
        io.releaseSocket();
    }

    public <T> void send(T data) {
        io.send(data);
    }

    public <T> void send(T data, Ack ack) {
        io.send(data, ack);
    }

    public <T> void send(int serialId, T data, Ack ack) {
        io.send(serialId, data, ack);
    }

    public int getId() {
        return socketId;
    }

    public boolean isConnected() {
        return io.isConnected();
    }

    /**
     * builder
     */
    public static final class Builder {
        private String host;
        private int port;
        private Options options;

        public Builder() {
            this(Options.get());
        }

        public Builder(@NonNull Options options) {
            this.host = DEFAULT_HOST;
            this.port = DEFAULT_PORT;
            this.options = options;
        }

        public Builder url(String host, int port) {
            this.host = host;
            this.port = port;
            return this;
        }

        public Builder url(String url) {
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("Invalid url: " + url);
            }
            int index = url.indexOf(':');
            if (index != -1) {
                host = url.substring(0, index);
                port = Integer.parseInt(url.substring(index + 1));
            } else {
                host = DEFAULT_HOST;
                port = DEFAULT_PORT;
            }
            return this;
        }

        public Builder reconnection(boolean reconnection) {
            options.reconnection = reconnection;

            return this;
        }

        public Builder reconnectionDelay(int reconnectionDelay) {
            options.reconnectionDelay = reconnectionDelay;

            return this;
        }

        public Builder reconnectionDelayMax(int reconnectionDelayMax) {
            options.reconnectionDelayMax = reconnectionDelayMax;

            return this;
        }

        public Builder reconnectionAttempts(int reconnectionAttempts) {
            options.reconnectionAttempts = reconnectionAttempts;

            return this;
        }

        public Builder opExceptionReconnectCount(int reconnectionCount) {
            options.opExceptionReconnectCount = reconnectionCount;

            return this;
        }

        public Builder conTimeout(int conTimeout) {
            options.conTimeout = conTimeout;

            return this;
        }

        public Builder opTimeout(int opTimeout) {
            options.opTimeout = opTimeout;

            return this;
        }

        public Builder encoder(Parser.Encoder encoder) {
            options.encoder = encoder;

            return this;
        }

        public Builder decoder(Parser.Decoder decoder) {
            options.decoder = decoder;

            return this;
        }

        public Builder pushHandler(PushHandler pushHandler) {
            options.pushHandler = pushHandler;

            return this;
        }

        public Builder packetParseConfig(PacketParser packetParser) {
            options.packetParser = packetParser;

            return this;
        }

        public IOSocket build() {
            return new IOSocket(this);
        }
    }
}
