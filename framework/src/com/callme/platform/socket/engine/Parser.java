package com.callme.platform.socket.engine;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：解析接口，编码及解码
 * 作者：huangyong
 * 创建时间：2018/12/11
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface Parser {

    public static interface Encoder {
        public void encode(Packet data, Parser.Encoder.Callback callback);

        public interface Callback {

            public void call(byte[] data);
        }
    }


    public static interface Decoder {

        public void onDecoded(PacketParser.BytePacket bytePacket, Callback callback);

        public interface Callback {

            public void call(Packet data);
        }

    }
}
