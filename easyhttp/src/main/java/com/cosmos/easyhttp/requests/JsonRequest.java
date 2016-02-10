package com.cosmos.easyhttp.requests;

import com.cosmos.easyhttp.base.Request;
import com.cosmos.easyhttp.base.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


/**
 * Created by Mark.
 *
 * Des: 请求参数为json格式，返回结果为JSONObject对象.
 */
public class JsonRequest extends Request<JSONObject> {

    private String mRequestBody;

    public JsonRequest(String url, String requestBody, RequestListener<JSONObject> listener) {
        super(HttpMethod.POST, url, listener);

        mRequestBody = requestBody;
    }

    public JsonRequest(HttpMethod method, String url, String requestBody, RequestListener<JSONObject> listener) {
        super(method, url, listener);

        mRequestBody = requestBody;
    }


    @Override
    public JSONObject parseNetworkResponse(Response response) {
        try {
            String jsonString = new String(response.getRawData());
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(DEFAULT_PARAMS_ENCODING);
        } catch (UnsupportedEncodingException uee) {
            return null;
        }
    }

}
