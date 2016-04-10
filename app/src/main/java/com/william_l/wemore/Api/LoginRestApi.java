package com.william_l.wemore.Api;

import org.json.JSONObject;

/**
 * Created by william on 2016/4/6.
 */
public class LoginRestApi extends BaseRestApi {

    public LoginRestApi(String url, String method) {
        super(url, method);
    }

    @Override
    protected JSONObject requestJson() {

        return null;
    }

    @Override
    protected boolean parseResponseData(String responseString) {
        return false;
    }

    @Override
    protected boolean isSimulate() {
        return true;
    }
}
