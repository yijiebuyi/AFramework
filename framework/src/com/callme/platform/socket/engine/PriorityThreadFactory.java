/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.callme.platform.socket.engine;

import android.os.Process;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：A thread factory that creates threads with a given thread priority.
 * 作者：huangyong
 * 创建时间：2019/1/24
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PriorityThreadFactory implements ThreadFactory {

    private final int priority;
    private final String name;
    private final AtomicInteger number = new AtomicInteger();

    public PriorityThreadFactory(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public Thread newThread(Runnable r) {
        return new Thread(r, name + '-' + number.getAndIncrement()) {
            @Override
            public void run() {
                Process.setThreadPriority(priority);
                super.run();
            }
        };
    }

}
