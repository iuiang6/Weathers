package com.william_l.wemore;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;

import org.apache.http.HttpEntity;
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

import android.os.Looper;
import android.text.TextUtils;

import com.bysun.supply.bean.User;
import com.bysun.supply.common.JsonHelper;
import com.bysun.supply.common.LogManager;
import com.bysun.supply.enums.RestApiCode;
import com.bysun.supply.preference.Workspace;

public abstract class BaseRestApi {

	private final static String DATA_HEAD = "appkey";
	protected final static String CHARSET = "UTF-8";
	protected final static String PLATFORM = "2";

	protected BaseRestApiListener _listener;
	protected boolean _isCancelled;
	protected boolean _isTimeout;
	protected boolean _isSuccessed;
	protected boolean _isCompleted;
	protected Exception _exception;

	protected String _url;
	protected HttpPost _httpPost;
	protected HttpGet _httpGet;
	protected int _httpcode;

	// 返回值
	public RestApiCode code;
	
	
	public int ret; 
	public String msg; 


	protected String _method; // 请求方法 默认post
	protected String TAG = "BaseRestApi"; // 请求方法 默认post

	public BaseRestApi(String url) {
		_url = url;
	}

	protected BaseRestApi(String url, String method) {
		_url = url;
		_method = url;
	}

	public void cancel() {
		if (_httpPost != null && !_httpPost.isAborted()) {
			_httpPost.abort();
		}

		if (_httpGet != null && !_httpGet.isAborted()) {
			_httpGet.abort();
		}
	}

	public void call() {
		LogManager.d("demo", _url);
		this.call(true);
	}

	public void call(boolean async) {
		this.call(async, 15000);
	}

	public void call(boolean async, int timeout) {

		try {

			if (!async
					&& Thread.currentThread() == Looper.getMainLooper()
							.getThread()) {
				throw new RestApiException("不允许在主线程中使用同步方式调用此接口");
			}

			if (this.isSimulate()) {

				if (async) {
					new MyAsyncTask<Void>() {
						@Override
						protected Void onExecute(Void... params)
								throws Exception {
							onResponseString(simulateResponse());
							return null;
						}

						@Override
						protected void onPostExecute(Void result,
								Exception exception) {
							if (exception != null) {
								try {
									onResponseException(exception);
								} catch (RestApiException e) {
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

			JSONObject requestJson = this.requestJson();

			if (requestJson == null) {
				InputStream requestStream = this.requestStream();

				if (requestStream == null) {
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
					_httpPost = new HttpPost(_url);
					_httpPost.setHeader("Accept", ContentType.APPLICATION_JSON);
					_httpPost.setHeader("Content-type",
							ContentType.APPLICATION_JSON);

					User user = Workspace.getUserPreference().getUserInfo();
					if (user != null) {
						_httpPost.addHeader("Auth_Account", user.account);
						_httpPost.addHeader("Auth_Token", user.token);

					}
					HttpEntity httpEntity = new StringEntity(new JSONObject()
							.put(DATA_HEAD, requestJson).toString(), CHARSET);
					// 打印request数据
					LogManager.d("demo",
							EntityUtils.toString(httpEntity, CHARSET));

					_httpPost.setEntity(httpEntity);
				} else {
					_httpGet = new HttpGet(_url);

				}

			}

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
			HttpConnectionParams.setSoTimeout(httpParameters, timeout);

			final HttpClient httpClient = new DefaultHttpClient(httpParameters);

			if (async) {
				MyAsyncTask<HttpResponse> task = new MyAsyncTask<HttpResponse>() {
					@Override
					protected HttpResponse onExecute(Void... params)
							throws Exception {
						HttpResponse httpResponse = null;
						if (null != _httpPost) {
							httpResponse = httpClient.execute(_httpPost);
						} else {
							httpResponse = httpClient.execute(_httpGet);
						}
						BaseRestApi.this.onResponse(httpResponse);
						return httpResponse;
					}

					@Override
					protected void onPostExecute(HttpResponse result,
							Exception exception) {
						if (exception != null) {
							try {
								onResponseException(exception);
							} catch (RestApiException e) {
								e.printStackTrace();
							}
						}
					}
				};
				task.execute();
			} else {
				HttpResponse httpResponse = null;
				if (null != _httpPost) {
					httpResponse = httpClient.execute(_httpPost);
				} else {
					httpResponse = httpClient.execute(_httpGet);
				}
				BaseRestApi.this.onResponse(httpResponse);
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

	protected String encrypt(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = (str + "hukdb").toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	void onResponseException(Exception e) throws RestApiException {

		do {
			// e.printStackTrace();
			this._isCompleted = true;
			this._exception = e;

			if (e instanceof ConnectTimeoutException
					|| e instanceof SocketTimeoutException) {
				this.code = RestApiCode.RestApi_Internal_TimeoutException;
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
				this.code = RestApiCode.RestApi_Internal_IOException;
				if (_listener != null) {
					_listener.onCancelled(this);
				}
				break;
			}

			// umeng统计
			// this.statistics(this.errorEventCode());

			if (e instanceof UnsupportedEncodingException) {
				this.code = RestApiCode.RestApi_Internal_UnsupportedEncodingException;
			}

			if (e instanceof JSONException) {
				this.code = RestApiCode.RestApi_Internal_JSONException;
			}

			if (e instanceof ClientProtocolException) {
				this.code = RestApiCode.RestApi_Internal_ClientProtocolException;
			}

			if (_listener != null) {
				_listener.onError(this, e);
			}
		} while (false);

		setListener(null);
	}

	void onResponse(HttpResponse httpResponse) {

		this._isCompleted = true;

		String responseString = "";
		try {
			responseString = EntityUtils.toString(httpResponse.getEntity(),
					CHARSET);

		} catch (Exception e) {
			// umeng统计
			// this.statistics(this.errorEventCode());

			if (_listener != null) {
				_listener.onError(this, e);
			}

			setListener(null);
			return;
		}

		this.onResponseString(responseString, httpResponse.getStatusLine()
				.getStatusCode());
	}

	void onResponseString(String responseString) {
		if (responseString == null)
			throw new InvalidParameterException();
		this.onResponseString(responseString, HttpStatus.SC_OK);
	}

	void onResponseString(String responseString, int statusCode) {
		this.printResponse(responseString);

		try {
			this._httpcode = statusCode;
			if (this._httpcode != HttpStatus.SC_OK) {
				this.code = RestApiCode.RestApi_Internal_HTTP_Failed;
				// umeng统计
				// this.statistics(this.errorEventCode());

				if (_listener != null) {
					_listener.onError(this, null);
				}

			} else {

				if (this.isRestApiJsonResponse()) {
					try {
						this.parseJsonResponse(responseString);
					} catch (JSONException e) {
						this.code = RestApiCode.RestApi_Internal_JSONException;
						this.msg = "网络异常";

						if (_listener != null) {
							_listener.onError(this, e);
						}
					}
				}

				if (this.parseResponseData(responseString)) {
					this._isSuccessed = true;

					// umeng统计
					// this.statistics(this.successedEventCode());

					// 服务器请求成功
					if (_listener != null) {
						_listener.onSuccessed(this);
					}
				}
			}
		} catch (Exception e) {
			LogManager.d(e.getMessage());
		}

		setListener(null);
	}

	void parseJsonResponse(String responseString) throws JSONException {

		JSONObject json = new JSONObject(responseString);

		this.msg = JsonHelper.getString(json, "msg", "");
		int code = JsonHelper.getStringAsInt(json, "code", -1);
		this.code = RestApiCode.createCode(code);

	}

	void printResponse(String responseString) {
		// 打印response数据
		try {
			LogManager.d("demo", responseString);
		} catch (Exception e) {
		}
	}

	protected boolean isSimulate() {
		return false;
	}

	protected String simulateResponse() {
		return null;
	}

	
	protected boolean isRestApiJsonResponse() {
		return true;
	}

	protected abstract boolean parseResponseData(String responseString);

	public void setListener(BaseRestApiListener listener) {
		_listener = listener;
	}

	public boolean isCancelled() {
		return _isCancelled;
	}

	public boolean isTimeout() {
		return _isTimeout;
	}

	public boolean isSuccessed() {
		return _isSuccessed;
	}

	public boolean isCompleted() {
		return _isCompleted;
	}

	public Exception exception() {
		return _exception;
	}

	protected JSONObject requestJson() throws JSONException {

		return null;
	}

	protected InputStream requestStream() {
		return null;
	}

	public int httpcode() {
		return _httpcode;
	}

	// Rest Api 监听器
	public static interface BaseRestApiListener {

		public abstract void onSuccessed(BaseRestApi object);

		public abstract void onFailed(BaseRestApi object, RestApiCode code,
				String message);

		public abstract void onError(BaseRestApi object, Exception e);

		public abstract void onTimeout(BaseRestApi object);

		public abstract void onCancelled(BaseRestApi object);
	}

	public static class RestApiException extends AssertionError {

		public RestApiException(String detailMessage) {
			super(detailMessage);
		}

		private static final long serialVersionUID = 2632591212433634947L;
	}

	public static interface Method {

		String GET = "GET";
		String PUT = "PUT";
		String POST = "POST";
		String DELETE = "DELETE";

	}

	public static interface ResponseCode extends HttpStatus {
	}

	public static interface ContentType {

		String APPLICATION_FORM_DATA = "application/x-www-form-urlencoded";
		String APPLICATION_JSON = "application/json";
		String MULTIPART = "multipart/form-data";
		String TEXT_PLAIN = "text/plain";

	}

	public static interface Header {

		// Request
		String USER_AGENT = "User-Agent";
		String ACCEPT_ENCODING = "Accept-Encoding";
		String ACCEPT_CHARSET = "Accept-Charset";
		String CACHE_CONTROL = "Cache-Control";
		String CONNECTION = "Connection";
		String IF_MODIFIED_SINCE = "If-Modified-Since";
		String IF_NONE_MATCH = "If-None-Match";

		// Response
		String CONTENT_LENGTH = "Content-Length";
		String CONTENT_TYPE = "Content-Type";
		String CONTENT_ENCODING = "Content-Encoding";
		String ETAG = "ETag";

		String DATE = "Date";
		String LAST_MODIFIED = "Last-Modified";
		String ESPIRES = "Expires";

		// Value
		String KEEP_ALIVE = "keep-alive";
		String NO_CACHE = "no-cache";

	}
}
