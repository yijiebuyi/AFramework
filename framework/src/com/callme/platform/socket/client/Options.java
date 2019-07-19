package com.callme.platform.socket.client;

import com.callme.platform.socket.engine.PacketParser;
import com.callme.platform.socket.engine.Parser;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：socket配置
 * 作者：huangyong
 * 创建时间：2018/11/30
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class Options {
    private static final Options OPTIONS = getDefault();

    static Options get() {
        return OPTIONS;
    }

    private static Options getDefault() {
        Options ops = new Options();
        ops.conTimeout = 10 * 1000;

        ops.reconnection = true;
        ops.reconnectionAttempts = 3;
        ops.reconnectionDelay = 20 * 1000;
        ops.reconnectionDelayMax = 60 * 1000;

        ops.opTimeout = 30 * 1000;
        ops.opExceptionReconnectCount = 4;
        return ops;
    }

    /**
     * 连接超时
     */
    public int conTimeout;

    /**
     * 是否能重连
     */
    public boolean reconnection;
    /**
     * 重连尝试次数
     */
    public int reconnectionAttempts;

    /**
     * 多少时间后重连
     */
    public int reconnectionDelay;
    /**
     * 最多几次重连
     */
    public int reconnectionDelayMax;
    /**
     * 读/写流异常事，重连计数
     */
    public int opExceptionReconnectCount;
    /**
     * 读/写流超时
     */
    public int opTimeout;

    /**
     * 解码
     */
    public Parser.Decoder decoder;
    /**
     * 编码
     */
    public Parser.Encoder encoder;
    /**
     * push消息处理器
     */
    public PushHandler pushHandler;
    /**
     * 字节包解码器
     */
    public PacketParser packetParser;
    /**
     * 心跳数据
     */
    public byte[] heartbeatData;
}
