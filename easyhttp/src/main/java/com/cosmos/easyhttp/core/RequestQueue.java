package com.cosmos.easyhttp.core;

import com.cosmos.easyhttp.base.Request;
import com.cosmos.easyhttp.httpstacks.HttpStack;
import com.cosmos.easyhttp.httpstacks.HttpUrlConnStack;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Mark.
 *
 * Des: 请求队列.
 */
public final class RequestQueue {

    /** 请求队列, 线程安全. */
    private BlockingQueue<Request<?>> mRequestQueue = new PriorityBlockingQueue<>();

    /** 序列号生成器. */
    private AtomicInteger mSerialNumsGenerator = new AtomicInteger(0);

    /** 工作线程的数量. */
    private int mExecutorNums;

    /** 工作线程集合. */
    private HttpExecutor[] mExecutors;

    /**
     * Http请求的真正执行者
     */
    private HttpStack mHttpStack;

    /**
     * @param coreNums 线程核心数
     * @param httpStack http执行器
     */
    protected RequestQueue(int coreNums, HttpStack httpStack) {
        mExecutorNums = (coreNums > 0) ? coreNums : Runtime.getRuntime().availableProcessors();
        mHttpStack = (httpStack != null) ? httpStack : new HttpUrlConnStack();
    }

    /**
     * 开始.
     */
    public void start() {
        stop();
        startNetworkExecutors();
    }

    /**
     * 停止所有任务.
     */
    public void stop() {
        if (mExecutors != null && mExecutors.length > 0) {
            for (int i = 0; i < mExecutors.length; i++) {
                mExecutors[i].quit();
            }
        }
    }

    /**
     * 创建并启动所有工作线程.
     */
    private final void startNetworkExecutors() {
        mExecutors = new HttpExecutor[mExecutorNums];
        for (int i = 0; i < mExecutorNums; i++) {
            mExecutors[i] = new HttpExecutor(mRequestQueue, mHttpStack);
            mExecutors[i].start();
        }
    }

    /**
     * 添加任务.
     *
     * @param request
     */
    public void addRequest(Request<?> request) {
        // 判断是否重复添加相同的任务
        if (!mRequestQueue.contains(request)) {
            request.setSerialNumber(this.generateSerialNumber());
            mRequestQueue.add(request);
        }
    }

    /**
     * 生成序列号.
     *
     * @return
     */
    private int generateSerialNumber() {
        return mSerialNumsGenerator.incrementAndGet();
    }
}
