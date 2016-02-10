package com.cosmos.easyhttp.core;


import com.cosmos.easyhttp.httpstacks.HttpStack;
import com.cosmos.easyhttp.httpstacks.HttpUrlConnStack;

/**
 * Created by Mark.
 *
 * Des: 主类.
 */
public final class SimpleHttp {

    /**
     * 创建请求队列.
     *
     * @return
     */
    public static RequestQueue newRequestQueue() {
        return newRequestQueue(Runtime.getRuntime().availableProcessors(), new HttpUrlConnStack());
    }


    /**
     * 创建请求队列.
     *
     * @param coreNums
     * @param httpStack
     * @return
     */
    public static RequestQueue newRequestQueue(int coreNums, HttpStack httpStack) {
        RequestQueue queue = new RequestQueue(coreNums, httpStack);
        queue.start();
        return queue;
    }
}
