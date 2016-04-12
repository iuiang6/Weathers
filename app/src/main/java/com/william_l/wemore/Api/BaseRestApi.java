package com.william_l.wemore.Api;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.InvalidParameterException;

/**
 * Created by william on 2016/4/6.
 */
public abstract class BaseRestApi {

    private BaseRestApiListener _listener;
    private String _url;
    private String _method;

    private HttpPost _httpPost;
    private HttpGet _httpGet;

    private boolean _isSuccessed;
    private boolean _isCompleted;
    private boolean _isCancelled;
    private boolean _isTimeout;
    private int _httpcode;

    private Exception _exception;

    public BaseRestApi(String url) {
        _url = url;
    }

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

        try {
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
                    _httpGet = new HttpGet(_url);
                } else {
                    _httpPost = new HttpPost(_url);
                    InputStreamEntity reqEntity = new InputStreamEntity(
                            requestStream, requestStream.available());
                    reqEntity.setChunked(false);
                    _httpPost.setEntity(reqEntity);
                }
            } else {
                if (TextUtils.isEmpty(_method)) {

                    _httpPost.setHeader("Accept", "application/json");
                    _httpPost.setHeader("Content-type", "application/json");
                    _httpPost.setEntity(new StringEntity("json", "UTF-8"));
                } else {
                    _httpGet = new HttpGet(_url);
                }
            }


            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
            HttpConnectionParams.setSoTimeout(httpParameters, timeout);

            final HttpClient httpClient = new DefaultHttpClient(httpParameters);

            if (async) {

                new MyAsyncTask() {

                    @Override
                    protected String onExecute(Void... params) throws Exception {
                        HttpResponse httpResponse = null;
                        if (null != _httpPost) {
                            httpResponse = httpClient.execute(_httpPost);
                        } else {
                            httpResponse = httpClient.execute(_httpGet);
                        }
                        BaseRestApi.this.onResponse(httpResponse);
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

                HttpResponse httpResponse = null;
                if (null != _httpPost) {
                    httpResponse = httpClient.execute(_httpPost);
                } else {
                    httpResponse = httpClient.execute(_httpGet);
                }
                onResponse(httpResponse);
            }
        } catch (UnsupportedEncodingException e) {
            this.onResponseException(e);
        } catch (JSONException e) {
            this.onResponseException(e);
        } catch (ClientProtocolException e) {
            this.onResponseException(e);
        } catch (IOException e) {
            this.onResponseException(e);
        } catch (Exception e) {
            this.onResponseException(e);
        }

    }

    private void onResponseException(Exception e) throws RestApiException {

        do {
            // e.printStackTrace();
            this._isCompleted = true;
            this._exception = e;

            if (e instanceof ConnectTimeoutException
                    || e instanceof SocketTimeoutException) {
                this._isTimeout = true;
                this._exception = null;
                if (_listener != null) {
                    _listener.onTimeout(this);
                }

                // umeng统计
                // this.statistics(this.timeoutEventCode());
                break;
            }

            if (e instanceof IOException) {
                this._isCancelled = true;
                this._exception = null;
                if (_listener != null) {
                    _listener.onCancelled(this);
                }
                break;
            }

            if (_listener != null) {
                _listener.onError(this, e);
            }
        } while (false);

        setListener(null);

    }

    private void onResponse(HttpResponse httpResponse) {

        this._isCompleted = true;

        String responseString = "";
        try {
            responseString = EntityUtils.toString(httpResponse.getEntity(),
                    "UTF-8");

        } catch (Exception e) {
            if (_listener != null) {
                _listener.onError(this, e);
            }

            setListener(null);
            return;
        }

        this.onResponseString(responseString, httpResponse.getStatusLine()
                .getStatusCode());
    }

    private void onResponseString(String responseString) {
        if (responseString == null)
            throw new InvalidParameterException();
        this.onResponseString(responseString, 200);

    }

    private void onResponseString(String responseString, int statusCode) {

        try {
            this._httpcode = statusCode;
            if (this._httpcode != HttpStatus.SC_OK) {
                if (_listener != null) {
                    _listener.onError(this, null);
                }

            } else {
                // TODO 判断是否我所约定的规则
                if (true) {
                    try {
                        this.parseJsonResponse(responseString);
                    } catch (JSONException e) {
                        if (_listener != null) {
                            _listener.onError(this, e);
                        }
                    }
                }

                if (this.parseResponseData(responseString)) {
                    this._isSuccessed = true;

                    // 服务器请求成功
                    if (_listener != null) {
                        _listener.onSuccessed(this);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("", e.getMessage());

        }

        setListener(null);

    }

    protected JSONObject requestJson() {
        return null;
    }

    protected InputStream requestStream() {
        return null;
    }


    protected abstract boolean parseResponseData(String responseString);

    protected boolean parseJsonResponse(String responseJson) throws JSONException {
        return parseResponseData(responseJson);
    }

    protected boolean isSimulate() {
        return false;
    }

    private String simulateResponse() {
        return null;
    }

    public void setListener(BaseRestApiListener listener) {
        _listener = listener;
    }

    public interface BaseRestApiListener {

        public abstract void onSuccessed(BaseRestApi object);

        public abstract void onFailed(BaseRestApi object, String message);

        public abstract void onError(BaseRestApi object, Exception e);

        public abstract void onTimeout(BaseRestApi object);

        public abstract void onCancelled(BaseRestApi object);
    }

    private class RestApiException extends Exception {
    }

}
