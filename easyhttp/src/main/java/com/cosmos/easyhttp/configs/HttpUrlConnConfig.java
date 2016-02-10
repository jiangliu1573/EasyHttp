package com.cosmos.easyhttp.configs;


/**
 * Created by Mark.
 *
 * Des: HttpURLConnection配置.
 */
public class HttpUrlConnConfig extends HttpConfig {

    private static HttpUrlConnConfig sConfig = new HttpUrlConnConfig();


    private HttpUrlConnConfig() {
    }

    public static HttpUrlConnConfig getConfig() {
        return sConfig;
    }

}
