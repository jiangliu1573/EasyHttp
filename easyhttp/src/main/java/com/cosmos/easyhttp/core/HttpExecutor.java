package com.cosmos.easyhttp.core;


import com.cosmos.easyhttp.base.Request;
import com.cosmos.easyhttp.base.Response;
import com.cosmos.easyhttp.cache.Cache;
import com.cosmos.easyhttp.cache.LruMemCache;
import com.cosmos.easyhttp.httpstacks.HttpStack;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Mark.
 *
 * Des: 工作线程.
 */
final class HttpExecutor extends Thread {

    /** 任务队列. */
    private BlockingQueue<Request<?>> mRequestQueue;

    /** Http请求栈. */
    private HttpStack mHttpStack;

    /** 结果传送. */
    private static ResponseDelivery mResponseDelivery = new ResponseDelivery();

    /** 缓存. */
    private static Cache<String, Response> mReqCache = new LruMemCache();

    /** 任务停止标志. */
    private boolean mHasBeenStoped = false;

    public HttpExecutor(BlockingQueue<Request<?>> queue, HttpStack httpStack) {
        mRequestQueue = queue;
        mHttpStack = httpStack;
    }

    @Override
    public void run() {
        try {
            while (!mHasBeenStoped) {
                final Request<?> request = mRequestQueue.take();
                if (request.hasBeenCanceled()) {
                    continue;
                }
                Response response;
                if (isInCache(request)) {
                    // 从缓存中取
                    response = mReqCache.get(request.getUrl());
                } else {
                    // 从网络上获取数据
                    response = mHttpStack.performRequest(request);
                    // 如果该请求需要缓存,那么请求成功则缓存到mResponseCache中
                    if (request.needBeCached() && isSuccess(response)) {
                        mReqCache.put(request.getUrl(), response);
                    }
                }
                // 分发请求结果
                mResponseDelivery.deliveryResponse(request, response);
            }
        } catch (InterruptedException e) {
        }

    }

    private boolean isSuccess(Response response) {
        return response != null && response.getStatusCode() == 200;
    }

    /**
     * 判断请求的结果是否在缓存中.
     *
     * @param request
     * @return
     */
    private boolean isInCache(Request<?> request) {
        return request.needBeCached() && mReqCache.get(request.getUrl()) != null;
    }

    public void quit() {
        mHasBeenStoped = true;
        interrupt();
    }
}
