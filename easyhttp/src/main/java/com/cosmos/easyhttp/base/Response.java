package com.cosmos.easyhttp.base;


import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Mark.
 *
 * Des: 请求结果类.
 */
public class Response extends BasicHttpResponse {

    public byte[] rawData = new byte[0];

    public Response(StatusLine statusLine) {
        super(statusLine);
    }

    @Override
    public void setEntity(HttpEntity entity) {
        super.setEntity(entity);
        rawData = entityToBytes(getEntity());
    }

    public byte[] getRawData() {
        return rawData;
    }

    public int getStatusCode() {
        return getStatusLine().getStatusCode();
    }

    public String getMessage() {
        return getStatusLine().getReasonPhrase();
    }

    /** Reads the contents of HttpEntity into a byte[]. */
    private byte[] entityToBytes(HttpEntity entity) {
        try {
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
