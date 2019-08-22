package com.callme.platform.util;

import android.text.TextUtils;
import android.util.Log;

import com.callme.platform.util.thdpool.PriorityThreadFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：文件日志辅助类
 * 用于跟踪特殊场景下， 异常上报，行为日志都不能分析问题时
 * 作者：huangyong
 * 创建时间：2019/1/15
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class FileLogHelper {
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 2;
    private static final int KEEP_ALIVE_TIME = 2; // 2 seconds

    private static final int CHECK_DELAY = 1000;
    private static final int CHECK_PERIOD = 5 * 60 * 1000;

    /**
     * special log
     */
    private final static String LOG_DIR = "huwo/slog";
    private final static int MAX_CACHE_FILE_COUNT = 10;
    private final static String FILE_NAME_FORMAT = "yyyyMMdd";

    private static File CACHE_DIR;
    private static String ROOT_DIR_STR;

    /**
     * 当前待缓存的文件
     */
    private File mCurrCacheFile;
    /**
     *
     */
    private OutputStreamWriter mOutputStreamWriter;
    /**
     * 文件writer
     */
    private BufferedWriter mBufferedWriter;
    private ExecutorService mExecutorService;

    /**
     * constructor
     */
    private FileLogHelper() {
        String rootDir = FileUtil.getExternalDir();
        if (!TextUtils.isEmpty(rootDir)) {
            CACHE_DIR = getExternalCacheDir(rootDir, LOG_DIR);
            ROOT_DIR_STR = rootDir;
            mExecutorService = new ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                    new PriorityThreadFactory("file-log-thread-pool",
                            android.os.Process.THREAD_PRIORITY_BACKGROUND));

            startCheckFileExceedTimer();
        } else {
            Log.w("FileLogHelper", "cache dir is null!");
        }
    }

    private static class SingletonInstance {
        private static final FileLogHelper INSTANCE = new FileLogHelper();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static FileLogHelper getInstance() {
        return SingletonInstance.INSTANCE;
    }

    /**
     * 写消息
     * @param partLine 是否分行
     * @param srcMsg
     */
    public void write(final boolean partLine, final String srcMsg) {
        if (mExecutorService != null) {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    StringBuilder sb = null;
                    try {
                        String time = TimeUtil.formatDate(System.currentTimeMillis(),
                                TimeUtil.TIME_YYYY_MM_DD_HH_MM_SS);
                        sb = new StringBuilder();
                        if (partLine) {
                            sb.append("\n");
                        }
                        sb.append(time);
                        sb.append(": ");
                        sb.append(srcMsg);
                        sb.append("\n");

                        checkCacheFileValid();
                        if (mBufferedWriter != null) {
                            mBufferedWriter.write(sb.toString());
                            mBufferedWriter.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        sb = null;
                    }
                }
            });
        }
    }

    /**
     * 如果文件超过最大缓存数量，则删除最早的文件
     */
    public void delFileIfFileExceed() {
        if (CACHE_DIR != null) {
            String[] fileNameList = CACHE_DIR.list();
            if (fileNameList != null && fileNameList.length > MAX_CACHE_FILE_COUNT) {
                Long[] numFileNames = new Long[fileNameList.length];
                try {
                    int i = 0;
                    for (String file : fileNameList) {
                        numFileNames[i++] = (TimeUtil.getTime(file));
                    }
                } catch (Exception e) {

                }

                Arrays.sort(numFileNames);

                String delFileName = TimeUtil.formatDate(numFileNames[0], FILE_NAME_FORMAT);
                File delFile = new File(CACHE_DIR, delFileName);
                delFile.delete();
            }
        }
    }

    /**
     * 获取打印的文件
     *
     * @return
     */
    private File getLogFile() {
        if (mCurrCacheFile != null) {
            return mCurrCacheFile;
        } else {
            if (CACHE_DIR == null && !TextUtils.isEmpty(ROOT_DIR_STR)) {
                CACHE_DIR = getExternalCacheDir(ROOT_DIR_STR, LOG_DIR);
            }

            if (CACHE_DIR != null) {
                String dir = CACHE_DIR.getAbsolutePath();
                if (TextUtils.isEmpty(dir)) {
                    return null;
                }

                try {
                    String fileName = TimeUtil.formatDate(System.currentTimeMillis(), FILE_NAME_FORMAT);
                    File file = new File(CACHE_DIR, fileName);
                    if (!file.exists()) {
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }

                        if (file.createNewFile()) {
                            mCurrCacheFile = file;
                        }
                    } else {
                        mCurrCacheFile = file;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                delFileIfFileExceed();
            }

            return mCurrCacheFile;
        }
    }

    /**
     * 获得存储目录
     *
     * @return
     */
    public static File getExternalCacheDir(String rootDir, String cacheDir) {
        File childDir = null;
        childDir = new File(rootDir, cacheDir);
        if (!childDir.exists()) {
            childDir.mkdirs();
        }

        return childDir;
    }

    /**
     * 检测缓存文件
     */
    private void checkCacheFileValid() {
        mCurrCacheFile = getLogFile();
        mBufferedWriter = getBufferedWriter();
    }

    /**
     * 获取缓存文件Writer
     *
     * @return
     */
    private BufferedWriter getBufferedWriter() {
        if (mBufferedWriter != null) {
            return mBufferedWriter;
        } else {
            if (mCurrCacheFile != null) {
                try {
                    OutputStream os = new FileOutputStream(mCurrCacheFile, true);
                    OutputStreamWriter writer = new OutputStreamWriter(os);
                    mOutputStreamWriter = writer;
                    mBufferedWriter = new BufferedWriter(writer);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return mBufferedWriter;
        }
    }

    /**
     * 开启文件超过最大缓存的定时器
     */
    private void startCheckFileExceedTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                //LogUtil.i("FileLogHelper", "timer run");
                delFileIfFileExceed();

                if (mCurrCacheFile != null) {
                    String currFileName = mCurrCacheFile.getName();
                    int index = currFileName.lastIndexOf(".");
                    if (index > -1) {
                        currFileName = currFileName.substring(0, index);
                    }

                    String targetFileName = TimeUtil.formatDate(System.currentTimeMillis(), FILE_NAME_FORMAT);
                    if (!TextUtils.equals(currFileName, targetFileName)) {
                        reset();
                    }
                }
            }
        }, CHECK_DELAY, CHECK_PERIOD);
    }

    /**
     * 重置文件名和文件
     */
    private void reset() {
        if (mBufferedWriter != null) {
            IOUtils.closeQuietly(mBufferedWriter);
            IOUtils.closeQuietly(mOutputStreamWriter);
        }
        mCurrCacheFile = null;
        mBufferedWriter = null;
    }

    public File getCacheDir() {
        return CACHE_DIR;
    }

    public File[] getAllCacheFiles() {
        if (CACHE_DIR == null || !CACHE_DIR.exists()) {
            return null;
        }

        return CACHE_DIR.listFiles();
    }
}
