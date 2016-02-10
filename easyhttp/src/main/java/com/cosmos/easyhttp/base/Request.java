package com.cosmos.easyhttp.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark.
 *
 * Des: 网络请求类.
 */
public abstract class Request<T> implements Comparable<Request<T>> {


    /**
     * 网络请求方式.
     */
    public  enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        /** http request type */
        private String mHttpMethod = "";

        HttpMethod(String method) {
            mHttpMethod = method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    /**
     * 网络请求优先级.
     */
    public  enum Priority {
        LOW,
        NORMAL,
        HIGN,
        IMMEDIATE
    }

    /** 默认编码. */
    public static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /** 默认内容Type*/
    public final static String HEADER_CONTENT_TYPE = "Content-Type";

    /** 请求序列号， 当请求的优先级相同时，按序号执行. */
    protected int mSerialNum = 0;

    /** 优先级默认设置为Normal. */
    protected Priority mPriority = Priority.NORMAL;

    /** 该请求是否已经被取消. */
    protected boolean mHasBeenCanceled = false;

    /** 该请求是否需要被缓存. */
    private boolean mNeedBeCached = true;

    /** 请求Listener. */
    protected RequestListener<T> mRequestListener;

    /** 请求的url. */
    private String mUrl;

    /** 请求的方法. */
    HttpMethod mHttpMethod;

    /** 请求的header. */
    private Map<String, String> mHeaders = new HashMap<>();

    /**
     * @param method
     * @param url
     * @param listener
     */
    public Request(HttpMethod method, String url, RequestListener<T> listener) {
        mHttpMethod = method;
        mUrl = url;
        mRequestListener = listener;
    }


    public void addHeader(String name, String value) {
        mHeaders.put(name, value);
    }

    /**
     * 从原生的网络请求中解析结果.
     *
     * @param response
     * @return
     */
    public abstract T parseNetworkResponse(Response response);

    /**
     * 处理Response,该方法运行在UI线程.
     *
     * @param response
     */
    public final void deliveryResponse(Response response) {
        T result = parseNetworkResponse(response);
        if (mRequestListener != null) {
            mRequestListener.onComplete(result);
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public int getSerialNumber() {
        return mSerialNum;
    }

    public void setSerialNumber(int mSerialNum) {
        this.mSerialNum = mSerialNum;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority mPriority) {
        this.mPriority = mPriority;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public HttpMethod getHttpMethod() {
        return mHttpMethod;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getParams() {
        return null;
    }

    public void setNeedBeCached(boolean needBeCached) {
        this.mNeedBeCached = needBeCached;
    }

    public boolean needBeCached() {
        return mNeedBeCached;
    }

    public void cancel() {
        mHasBeenCanceled = true;
    }

    public boolean hasBeenCanceled() {
        return mHasBeenCanceled;
    }

    /**
     * 将参数编码，并转换成字节数组.
     *
     * @return
     */
    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }


    /**
     * 将参数编码，并转换成字节数组.
     *
     * @return
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    @Override
    public int compareTo(Request<T> another) {
        Priority myPriority = this.getPriority();
        Priority anotherPriority = another.getPriority();
        // 如果优先级相等,那么按照添加到队列的序列号顺序来执行
        return myPriority.equals(anotherPriority) ? this.getSerialNumber()
                - another.getSerialNumber()
                : myPriority.ordinal() - anotherPriority.ordinal();
    }


    /**
     * 网络请求Listener.
     */
    public  interface RequestListener<T> {
        /**
         * 请求完成的回调
         * 
         * @param response
         */
         void onComplete(T response);
    }
}
