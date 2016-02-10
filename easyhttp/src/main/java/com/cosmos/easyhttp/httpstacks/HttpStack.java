package com.cosmos.easyhttp.httpstacks;


import com.cosmos.easyhttp.base.Request;
import com.cosmos.easyhttp.base.Response;

/**
 * Created by Mark.
 *
 * Des: 执行网络请求的接口.
 */
public interface HttpStack {
    /**
     * 执行Http请求
     * 
     * @param request 待执行的请求
     * @return
     */
     Response performRequest(Request<?> request);
}
