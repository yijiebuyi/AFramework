package com.callme.platform.socket.engine;

import android.util.Log;

import com.callme.platform.socket.client.Ack;
import com.callme.platform.socket.client.ConnectionListener;
import com.callme.platform.socket.client.Options;
import com.callme.platform.socket.client.PushHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：Socket核心管理类
 *        1.初始化socket连接相关配置
 *        2.socket实例化
 *        3.连接socket(连接成功后，开始数据监听，开始发送心跳包)
 *        4.空闲心跳，每个1分钟发一次心跳
 *        5.socket发送数据，使用队列
 *        6.循环读流通道的数据，实时监听服务器发送来的数据
 * 作者：huangyong
 * 创建时间：2019/1/24
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class Manager {
    public static final String TAG = "AND_SOCKET";
    /**
     * 默认操作（读/写）延时
     */
    private final static int DEFAULT_TIME = 10 * 1000;

    private Socket socket;
    private boolean connected;
    private boolean shutdown;
    private boolean encoding;

    private OutputStream writer;
    private InputStream reader;

    private int readErrorCount = 0;

    private String host;
    private int port;
    private Options options;
    private Parser.Decoder decoder;
    private Parser.Encoder encoder;
    private PushHandler<Packet<?>> pushHandler;
    private ConnectionListener connectionListener;
    private SocketWatcher socketWatcher;

    //包的序列号
    private int ids = 0;
    private PacketParser packetParser;

    private Thread observerThread;
    private ExecutorService sendService;
    private ExecutorService threadPool;

    private final ThreadFactory THREAD_FACTORY = new PriorityThreadFactory("thread-pool",
            android.os.Process.THREAD_PRIORITY_BACKGROUND);

    private Map<Integer, Ack> acks = new ConcurrentHashMap<Integer, Ack>();
    private List<Packet> packetBuffer;

    /**
     * 心跳
     */
    private PingPong pingPong;

    /**
     * constructor
     *
     * @param host
     * @param port
     * @param options
     */
    public Manager(String host, int port, Options options) {
        init(host, port, options);
    }

    private void init(String host, int port, Options options) {
        this.host = host;
        this.port = port;
        this.options = options;

        this.encoder = options.encoder;
        this.decoder = options.decoder;
        this.packetParser = options.packetParser;
        this.pushHandler = options.pushHandler;

        this.shutdown = false;
        this.encoding = false;
        this.readErrorCount = 0;
        this.packetBuffer = new ArrayList<Packet>();
        this.socketWatcher = new SimpleSocketWatcher(this);

        this.threadPool = Executors.newFixedThreadPool(2);
        this.sendService = Executors.newSingleThreadExecutor(THREAD_FACTORY);
        Log.i(TAG, "================init===============");
    }

    /**
     * 连接
     */
    public void connect() {
        connect(null);
    }

    /**
     * 连接
     *
     * @param listener
     */
    public void connect(final ConnectionListener listener) {
        if (socketWatcher != null) {
            socketWatcher.resetCurrCount();
        }
        innerConnect(listener);
    }

    private void innerConnect(final ConnectionListener listener) {
        boolean unavailable = false;
        if (threadPool == null || (unavailable = threadPool.isShutdown() || threadPool.isTerminated())) {
            String msg = threadPool != null ? (unavailable ? "unavailable" : "available") : "null";
            Log.i(TAG, "threadPool:" + msg);
            return;
        }

        connectionListener = listener;
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                open(listener);
            }
        });
    }

    private void open(ConnectionListener listener) {
        Log.i(TAG, "======================start open=================");
        if (socket != null && socket.isConnected()) {
            if (listener != null) {
                listener.onConnected();
            }
        } else {
            SocketAddress address = null;
            int conTimeout = options != null && options.conTimeout > 0 ? options.conTimeout : 0;
            try {
                address = getSocketAddress(host, 3000);
            } catch (UnknownHostException e) {
                if (listener != null) {
                    listener.onConnectTimeOut(e);
                }

                if (socketWatcher != null) {
                    socketWatcher.onConnectEx(e);
                }
                return;
            }

            //connect
            Log.i(TAG, "======================start connect=================");
            try {
                if (socket == null || socket.isClosed()) {
                    socket = new Socket();
                    socket.setKeepAlive(true);
                    socket.setTcpNoDelay(true);
                    //read opTimeout
                    socket.setSoTimeout(options != null && options.opTimeout > 0 ? options.opTimeout : DEFAULT_TIME);
                    pingPong = new PingPong(this);
                }

                socket.connect(address, conTimeout);

                connected = true;

                writer = socket.getOutputStream();
                reader = new DataInputStream(socket.getInputStream());

                if (listener != null) {
                    listener.onConnected();
                }

                startDataWatcher();
                pingPong.sendHeartbeat();
            } catch (IOException e) {
                if (listener != null) {
                    if (e instanceof SocketTimeoutException) {
                        listener.onConnectTimeOut(e);
                    } else {
                        listener.onConnectError(e);
                    }

                    if (socketWatcher != null) {
                        socketWatcher.onConnectEx(e);
                    }
                    shutdown();
                }
            }
        }
    }

    public void setConnectionListener(ConnectionListener listener) {
        connectionListener = listener;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public boolean isReconnect() {
        return options != null && options.reconnection;
    }

    public Options getOptions() {
        return options;
    }

    /**
     * 重连
     */
    public void reconnect() {
        releaseSocket();
        innerConnect(connectionListener);
    }

    public void releaseSocket() {
        connected = false;
        readErrorCount = 0;
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }

            if (writer != null) {
                writer.flush();
                writer.close();
                writer = null;
            }

            if (reader != null) {
                reader.close();
                reader = null;
            }

            if (pingPong != null) {
                pingPong.release();
                pingPong = null;
            }
        } catch (IOException e) {
            Log.i(TAG, "releaseSocket:" + e.getMessage());
        }

        if (connectionListener != null) {
            connectionListener.onDisconnect();
        }
    }

    /**
     * 关闭
     */
    public void shutdown() {
        ids = 0;
        shutdown = true;
        releaseSocket();
        Log.i(TAG, "================shutdown===============");
        try {
            if (observerThread != null) {
                observerThread.interrupt();
            }
            if (sendService != null) {
                sendService.shutdown();
            }
            if (threadPool != null) {
                threadPool.shutdown();
            }
            threadPool = null;
        } catch (Exception e) {
            Log.i(TAG, "shutdown:" + e.getMessage());
        }
    }

    /**
     * 发送数据
     *
     * @param data 数据
     * @param <T>
     */
    public <T> void send(final T data) {
        send(++ids, data, null);
    }

    /**
     * 发送数据
     *
     * @param data 数据
     * @param ack  确认
     * @param <T>
     */
    public <T> void send(final T data, final Ack ack) {
        send(++ids, data, ack);
    }

    /**
     * 发送数据
     *
     * @param serialId 包序列号
     * @param data     数据
     * @param ack      确认
     * @param <T>
     */
    public <T> void send(final int serialId, final T data, final Ack ack) {
        sendService.execute(new Runnable() {
            @Override
            public void run() {
                Packet packet = null;
                if (data instanceof Packet) {
                    packet = (Packet) data;
                } else {
                    packet = new Packet(data);
                    packet.id = serialId;
                }

                if (connected) {
                    if (ack != null) {
                        acks.put(packet.id, ack);
                    }

                    packet(packet);
                } else {
                    packetBuffer.add(packet);
                    if (connectionListener != null) {
                        connectionListener.disconnected();
                    }

                    if (socketWatcher != null) {
                        socketWatcher.onWriteEx(new IllegalStateException("socket is closed!"));
                    }
                }
            }
        });
    }

    /**
     * 打包packet
     *
     * @param packet
     */
    private void packet(Packet packet) {
        if (encoder == null) {
            return;
        }

        final Manager self = this;
        if (!self.encoding) {
            self.encoding = true;
            encoder.encode(packet, new Parser.Encoder.Callback() {
                @Override
                public void call(byte[] data) {
                    try {
                        self.writer.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (socketWatcher != null) {
                            socketWatcher.onWriteEx(e);
                        }
                    }

                    self.encoding = false;
                    processPacketQueue();
                }
            });
        } else {
            packetBuffer.add(packet);
        }
    }

    /**
     * 执行队列里面的发送包
     */
    private void processPacketQueue() {
        if (!packetBuffer.isEmpty() && !encoding) {
            Packet pack = packetBuffer.remove(0);
            packet(pack);
        }
    }

    /**
     * 开始数据监听
     */
    private void startDataWatcher() {
        observerThread = new Thread() {
            @Override
            public void run() {
                loopReader();
            }
        };
        observerThread.setName("Observer-Thread");
        observerThread.setPriority(Thread.MAX_PRIORITY);
        observerThread.start();
    }

    /**
     * 循环读socket数据
     */
    private void loopReader() {
        while (true) {
            if (socket == null || socket.isClosed() || !connected) {
                if (connectionListener != null) {
                    connectionListener.disconnected();
                }

                if (socketWatcher != null) {
                    socketWatcher.onReadEx(new IllegalStateException("socket is closed!"));
                }

                break;
            }

            if (reader == null) {
                break;
            }

            try {
                byte[] buffer = null;
                int len = 0;
                boolean available = reader.available() > 0;
                Log.i(TAG, "loop read, available: " + available);
                if (available) {
                    //可用字节，注:网络读取数据时，如果服务器传的100个字节，可能到达客户端小于100个字节.
                    buffer = new byte[reader.available()];
                    len = reader.read(buffer);
                } else {
                    //暂时无数据，阻塞read,防止死循环轮询
                    buffer = new byte[4 * 1024];
                    len = reader.read(buffer);
                    if (len < 0) {
                        Thread.currentThread().sleep(1000);
                        readErrorCount++;
                    }
                }

                if (readErrorCount > 3) {
                    releaseSocket();
                    connect(connectionListener);
                    break;
                }

                Log.i(TAG, "data length:" + len);
                handleResponse(buffer, len);
            } catch (IOException e) {
                Log.w(TAG, "read data ex:" + e.getLocalizedMessage());
                if (socketWatcher != null) {
                    socketWatcher.onReadEx(e);
                }
            } catch (InterruptedException e) {
                Log.w(TAG, "read thread ex:" + e.getLocalizedMessage());
                if (socketWatcher != null) {
                    socketWatcher.onReadEx(e);
                }
            }
        }
    }

    /**
     * 处理响应数据
     *
     * @param buffer
     * @param len
     * @throws IOException
     */
    private void handleResponse(byte[] buffer, int len) throws IOException {
        if (packetParser == null) {
            return;
        }

        packetParser.parse(buffer, len, new PacketParser.PacketParserCallback() {
            @Override
            public void call(PacketParser.BytePacket packet) {
                if (decoder == null) {
                    return;
                }

                decoder.onDecoded(packet, new Parser.Decoder.Callback() {
                    @Override
                    public void call(final Packet data) {
                        if (data == null) {
                            return;
                        }

                        switch (data.type) {
                            case Transport.TYPE_ACK:
                                Ack ack = acks.remove(data.id);
                                if (ack != null) {
                                    ack.call(data);
                                }
                                break;
                            case Transport.TYPE_PUSH:
                                if (threadPool != null && !threadPool.isShutdown()) {
                                    threadPool.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (pushHandler != null) {
                                                pushHandler.onHandlePushMsg(data);
                                            }
                                        }
                                    });
                                } else {
                                    if (pushHandler != null) {
                                        pushHandler.onHandlePushMsg(data);
                                    }
                                }
                                break;
                            case Transport.TYPE_OTHER:

                                break;
                        }

                    }
                });
            }
        });
    }

    /**
     * 获取socket 地址
     *
     * @param host       主机地址
     * @param conTimeout 连接超时
     * @return
     * @throws UnknownHostException
     */
    private SocketAddress getSocketAddress(String host, int conTimeout) throws UnknownHostException {
        DNSLookupThread dnsTh = new DNSLookupThread(host);
        try {
            dnsTh.start();
            dnsTh.join(conTimeout);
        } catch (Exception e) {

        }

        return new InetSocketAddress(dnsTh.get(), port);
    }

}
