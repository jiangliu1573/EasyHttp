package com.cosmos.easyhttp.httpstacks;

import com.cosmos.easyhttp.base.Request;
import com.cosmos.easyhttp.base.Response;
import com.cosmos.easyhttp.configs.HttpUrlConnConfig;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by Mark.
 *
 * Des: 基于HttpUrlConnection的HttpStack.
 */
public class HttpUrlConnStack implements HttpStack {


    HttpUrlConnConfig mConfig = HttpUrlConnConfig.getConfig();

    @Override
    public Response performRequest(Request<?> request) {
        HttpURLConnection urlConnection = null;
        try {
            // 构建HttpURLConnection
            urlConnection = createUrlConnection(request.getUrl(), request.getHttpMethod().toString());
            // 设置headers
            setRequestHeaders(urlConnection, request);
            // 设置Body参数
            setRequestParams(urlConnection, request);

            return fetchResponse(urlConnection);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * 创建网络请求.
     *
     * @param url
     * @param httpMethod
     * @return
     * @throws IOException
     */
    private HttpURLConnection createUrlConnection(String url, String httpMethod) throws IOException {
        URL newURL = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) newURL.openConnection();
        urlConnection.setConnectTimeout(mConfig.connTimeOut);
        urlConnection.setReadTimeout(mConfig.soTimeOut);
        urlConnection.setRequestMethod(httpMethod);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        return urlConnection;
    }

    /**
     * 设置http headers.
     *
     * @param connection
     * @param request
     */
    private void setRequestHeaders(HttpURLConnection connection, Request<?> request) {
        Set<String> headersKeys = request.getHeaders().keySet();
        for (String headerName : headersKeys) {
            connection.addRequestProperty(headerName, request.getHeaders().get(headerName));
        }
    }

    /**
     * 设置http body.
     *
     * @param connection
     * @param request
     * @throws IOException
     */
    protected void setRequestParams(HttpURLConnection connection, Request<?> request)
            throws IOException {

        // 添加请求参数
        byte[] body = request.getBody();
        if (body != null) {
            // enable output
            connection.setDoOutput(true);
            // set content type
            connection
                    .addRequestProperty(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
            // write params data to connection
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(body);
            dataOutputStream.close();
        }
    }

    /**
     * 获取Response对象.
     *
     * @param connection
     * @return
     * @throws IOException
     */
    private Response fetchResponse(HttpURLConnection connection) throws IOException {

        // Initialize HttpResponse with data from the HttpURLConnection.
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        // 状态行数据
        StatusLine responseStatus = new BasicStatusLine(protocolVersion,
                connection.getResponseCode(), connection.getResponseMessage());
        // 构建response
        Response response = new Response(responseStatus);
        // 设置response数据
        response.setEntity(entityFromURLConnwction(connection));
        addHeadersToResponse(response, connection);
        return response;
    }

    /**
     * 执行HTTP请求之后获取到的数据流.
     *
     * @param connection
     * @return
     */
    private HttpEntity entityFromURLConnwction(HttpURLConnection connection) {
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = connection.getErrorStream();
        }

        // TODO : GZIP
        entity.setContent(inputStream);
        entity.setContentLength(connection.getContentLength());
        entity.setContentEncoding(connection.getContentEncoding());
        entity.setContentType(connection.getContentType());

        return entity;
    }

    private void addHeadersToResponse(BasicHttpResponse response, HttpURLConnection connection) {
        for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                Header h = new BasicHeader(header.getKey(), header.getValue().get(0));
                response.addHeader(h);
            }
        }
    }
}
