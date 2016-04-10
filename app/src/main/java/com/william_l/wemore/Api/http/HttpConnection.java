package com.william_l.wemore.Api.http;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author William Liu
 * @category 网络连接类
 */
public class HttpConnection {

    private final String TAG = "HttpConnection";

    /**
     * @param httpPost
     * @return
     * @throws IOException
     * @category 客户端请求
     */
    private String clientRequest(HttpPost httpPost)
            throws IOException {

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                90000);
        HttpConnectionParams.setSoTimeout(httpParameters, 30000);
        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpResponse h = client.execute(httpPost);

        if (h.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            return EntityUtils.toString(h.getEntity(), "UTF-8");
        System.out.print(h.getStatusLine().getStatusCode());
        return null;
    }

    /**
     * @param URL
     * @param jsonParam
     * @return String
     * @throws IOException
     * @category json实体参数
     */
    private String setStringEntity(String URL, String jsonParam)
            throws IOException {

        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        if (null != jsonParam) {
            httpPost.setEntity(new StringEntity(jsonParam, "UTF-8"));
        }

        return clientRequest(httpPost);

    }

    public String getData(String url, String[] key, String[] value)
            throws JSONException, IOException {

        String data;
        if (null != key && 0 != key.length) {
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < key.length; i++) {
                jsonObject.put(key[i], value[i]);
            }
            data = setStringEntity(
                    url,
                    jsonObject.toString());
        } else {
            data = setStringEntity(
                    url, null);
        }


        return data;
    }
}
