package com.callme.platform.socket.engine;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：DNS查询线程
 * 作者：huangyong
 * 创建时间：2019/1/23
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DNSLookupThread extends Thread {
    private InetAddress addr;
    private String hostname;
    private UnknownHostException ex;

    public DNSLookupThread(String host) {
        hostname = host;
    }

    public void run() {
        try {
            InetAddress add = InetAddress.getByName(hostname);
            set(add);
        } catch (UnknownHostException e) {
            ex = e;
        }
    }

    private synchronized void set(InetAddress netAddr) {
        addr = netAddr;
    }

    public synchronized InetAddress get() throws UnknownHostException {
        if (ex != null) {
            throw ex;
        }

        if (addr == null) {
            throw new UnknownHostException("Unable to resolve host \"" + hostname + "\"" +
                    ": Getting InetAddress time out");
        }

        return addr;
    }
}
