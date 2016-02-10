package com.cosmos.easyhttp.requests;


import com.cosmos.easyhttp.base.Request;
import com.cosmos.easyhttp.base.Response;

import java.util.Map;

/**
 * Created by Mark.
 *
 * Des: 请求参数为默认格式，返回结果为String对象.
 */
public class StringRequest extends Request<String> {

    private Map<String, String> mParams;

    public StringRequest(String url, Map<String, String> params, RequestListener<String> listener){
        super(HttpMethod.POST, url, listener);
        mParams = params;
    }

    public StringRequest(HttpMethod method, String url, Map<String, String> params, RequestListener<String> listener) {
        super(method, url, listener);
        mParams = params;
    }

    @Override
    public Map<String, String> getParams() {
        return mParams;
    }

    @Override
    public String parseNetworkResponse(Response response) {
        return new String(response.getRawData());
    }
}
