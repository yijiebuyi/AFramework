package com.callme.platform.util;

import android.os.Environment;
import android.util.Log;

import com.callme.platform.util.thdpool.ThreadPool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    private static final byte LOG_NULL = 0;            // null
    // device

    // log
    public static final byte LOG_CONSOLE = 1;            // console
    // log
    public static final byte LOG_FILE = 2;            // file
    // log
    public static final byte LOG_BOTH = 3;            // both

    private static byte logDevice = LOG_FILE;

    private static FileLogHandler fileLog;

    private static boolean enableCustomProxy = false;
    private static final String DIR_EXT_MAIN = "huwo";
    private static final String FILE_LOG = "log.dat";

    public static boolean isEnableCustomProxy() {
        return enableCustomProxy;
    }


    static {
        if (logDevice > LOG_CONSOLE) {
            fileLog = new FileLogHandler();
        }
    }

    public static void d(String TAG, String msg) {
        d(TAG, msg, logDevice);
    }

    public static void d(String TAG, String label, Throwable throwable) {
        String msg = throwable != null ? throwable.getLocalizedMessage() : "";
        msg = label + ":" + msg;
        d(TAG, msg, logDevice);
    }

    public static void e(String tag, Throwable throwable) {
        if (throwable == null)
            return;

        StackTraceElement[] stacks = new Throwable().getStackTrace();
        if (stacks.length > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("class : ").append(stacks[1].getClassName()).append("; line : ").append(stacks[1].getLineNumber());
            Log.d(tag, sb.toString());
        }

        throwable.printStackTrace();
    }

    public static void d(String tag, String msg, int device) {
        if (msg == null)
            msg = "NULL MSG";

        //TODO
        msg = System.currentTimeMillis() + ": " + msg;
        switch (device) {
            case LOG_CONSOLE:
                Log.d(tag, msg);
                break;
            case LOG_FILE:
                writeToLog(tag + "\t" + msg);
                break;
            case LOG_BOTH:
                Log.d(tag, msg);
                writeToLog(tag + "\t" + msg);
                break;
            case LOG_NULL:
            default:
                break;
        }
    }

    public static void timeStamp(Exception exception, String step) {
        StackTraceElement stackTraceElement = exception.getStackTrace()[0];
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        int lineNum = stackTraceElement.getLineNumber();
        if (step == null)
            step = "";
        else
            step += "-";
        Log.d("TimeStamp", step + className + "." + methodName + "():" + lineNum);
    }

    private static void writeToLog(final String log) {
        ThreadPool.getInstance().submit(new ThreadPool.Job<Void>() {
            @Override
            public Void run(ThreadPool.JobContext jc) {
                fileLog.cacheLogToLocal(log);
                return null;
            }
        });
    }

    /**
     * Get whether log switch is on.
     */
    public static boolean getIsLogged() {
        return (logDevice != LOG_NULL);
    }

    /**
     * Set whether log switch is on.
     */
    public static void setIsLogged(boolean isLogged) {
        if (isLogged) {
            logDevice = LOG_CONSOLE;
        } else {
            logDevice = LOG_NULL;
        }
    }

    public static void init() {
        if (logDevice > LOG_CONSOLE) {
            fileLog = new FileLogHandler();
        }
    }


    private static class FileLogHandler {
        private boolean hasSDCard = true;
        private FileOutputStream logOutput;
        private File logFile;

        FileLogHandler() {
            hasSDCard = hasExternalStorage();
            if (hasSDCard) {
                try {
                    logFile = getLogFile();
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }
                } catch (IOException e) {
                }
            }
        }


        public void cacheLogToLocal(final String srcLog) {
            if (!hasSDCard) {
                return;
            }

            try {
                String log = srcLog + "\n";
                if (log != null) {
                    byte[] logData = log.getBytes();
                    getLogOutput().write(logData, 0, logData.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        FileOutputStream getLogOutput() throws Exception {
            if (logOutput == null) {
                logOutput = new FileOutputStream(logFile, true);
            }
            return logOutput;
        }
    }

    public static final boolean FILE_LOGABLE = true;

    // 写日志到sdcard. 用于跟踪特殊场景下， 异常上报，行为日志都不能分析问题时，调用. 慎用.
    public static void writeLog(String file, String log) {
        if (!FILE_LOGABLE)
            return;
        if (hasExternalStorage()) {
            try {
                File logFile = new File(file);
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(logFile, true));
                printWriter.println(log);
                printWriter.flush();
                printWriter.close();

            } catch (FileNotFoundException e) {
                e.getMessage();
            }
        }
    }

    public static File getLogFile() {
        return new File(getExternalRootDir(), FILE_LOG);
    }

    /**
     * 获得存储目录
     *
     * @return
     */
    public static File getExternalRootDir() {
        File childDir = null;
        // Environment.getExternalStorageDirectory() 返回/sdcard/
        // 使用系统方法，避免这个名字可能不同
        childDir = new File(Environment.getExternalStorageDirectory(), DIR_EXT_MAIN);
        if (!childDir.exists())
            childDir.mkdirs();

        return childDir;
    }

    public static boolean hasExternalStorage() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
