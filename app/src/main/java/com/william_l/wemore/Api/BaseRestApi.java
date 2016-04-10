package com.william_l.wemore.Api;

import android.os.Looper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidParameterException;

/**
 * Created by william on 2016/4/6.
 */
public abstract class BaseRestApi {

    private String _url;
    private String _method;

    public BaseRestApi(String url, String method) {
        this._url = url;
        this._method = method;
    }

    public void call() throws Exception {
        call(true);
    }

    public void call(Boolean async) throws Exception {

        call(async, 15000);

    }

    public void call(Boolean async, int timeout) throws Exception {

        if (!async && Thread.currentThread() == Looper.getMainLooper().getThread()) {
            throw new Exception("不允许在主线程中使用同步方法调用此接口");
        }

        if (this.isSimulate()) {

            if (async) {
                new MyAsyncTask() {

                    @Override
                    protected String onExecute(Void... params) throws Exception {
                        onResponseString(simulateResponse());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result, Exception exception) {
                        if (exception != null) {
                            try {
                                onResponseException(exception);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.execute();
            } else {
                onResponseString(simulateResponse());
            }
            return;
        }

        JSONObject jsonRequest = this.requestJson();

        if (null == jsonRequest) {
            InputStream requestStream = this.requestStream();
            if (null == requestStream) {
                HttpGet httpGet = new HttpGet(_url);
            } else {
                InputStream in = null;
                try {
                    URL url = new URL("ftp://mirror.csclub.uwaterloo.ca/index.html");
                    URLConnection urlConnection = url.openConnection();
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != in) {
                        in.close();
                    }
                }

            }
        } else {
            InputStream in = null;
            try {
                URL url = new URL("ftp://mirror.csclub.uwaterloo.ca/index.html");
                URLConnection urlConnection = url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != in) {
                    in.close();
                }

            }
        }

    }

    private void onResponse(HttpResponse httpResponsen) {


    }

    private void onResponseException(Exception e) {


    }

    private void onResponseString(String responseString) {
        if (responseString == null)
            throw new InvalidParameterException();
        this.onResponseString(responseString, 200);

    }

    private void onResponseString(String responseString, int statusCode) {


    }

    protected JSONObject requestJson() {

        return null;

    }

    protected InputStream requestStream() {
        return null;
    }


    protected abstract boolean parseResponseData(String responseString);

    protected boolean isSimulate() {
        return false;
    }

    private String simulateResponse() {
        return null;
    }


}
