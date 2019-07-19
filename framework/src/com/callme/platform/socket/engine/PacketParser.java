package com.callme.platform.socket.engine;

import java.io.IOException;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：包的解析，将字节数组划分到字节包
 * 作者：huangyong
 * 创建时间：2019/1/28
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class PacketParser {
    protected Config config;

    public PacketParser(Config config) {
        this.config = config;
    }

    public abstract void parse(byte[] buffer, int len, PacketParserCallback callback) throws IOException;

    /**
     * 把字节数据按照指定的配置进行解析
     *
     * @see Config
     */
    public static class BytePacket {
        public byte[] body;
        public byte[] header;
    }

    public static class Config {
        /**
         * 包的头部字节长度
         */
        public int headerLen;
        /**
         * 包的开始标记位
         */
        public byte packetStartFlag;
        /**
         * 包的结束标志位
         */
        public byte packetEndFlag;
    }

    public static interface PacketParserCallback {
        public void call(BytePacket packet);
    }
}
