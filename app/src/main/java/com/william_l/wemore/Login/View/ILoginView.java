package com.william_l.wemore.Login.View;

/**
 * Created by william on 2016/4/1.
 */
public interface ILoginView {

    void clearText();

    void onLoginResult(Boolean result, int code);

    void setProgressBarVisiblity(int visibility);


}
