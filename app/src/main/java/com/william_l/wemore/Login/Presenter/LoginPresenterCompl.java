package com.william_l.wemore.Login.Presenter;

import android.app.Activity;

import com.william_l.wemore.Api.BaseRestApi;
import com.william_l.wemore.Api.Constant.Perference;
import com.william_l.wemore.Api.LoginRestApi;
import com.william_l.wemore.Api.MyThreadPool;
import com.william_l.wemore.Login.Model.IUser;
import com.william_l.wemore.Login.Model.UserModel;
import com.william_l.wemore.Login.View.ILoginView;

import java.util.concurrent.ExecutorService;

/**
 * Created by william on 2016/4/1.
 */
public class LoginPresenterCompl implements ILoginPresenter {


    IUser user;
    ILoginView iLoginView;

    public LoginPresenterCompl(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
        initUser();

    }

    @Override
    public void clean() {
        iLoginView.clearText();
    }

    @Override
    public void doLogin(String name, String password) {
        Boolean isLoginSuccess = true;
        final int code = user.checkUserValidity(name, password);
        if (-1 == code) {
            isLoginSuccess = false;
        }
        final Boolean result = isLoginSuccess;


        ExecutorService cachedExecutor = MyThreadPool.getInstance();
        cachedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LoginRestApi loginRA = new LoginRestApi(String.format(Perference.WeatherUrl, Perference.WeatherAppid), "");
                BaseRestApi.BaseRestApiListener baseRestApiListener = new BaseRestApi.BaseRestApiListener() {
                    @Override
                    public void onSuccessed(BaseRestApi object) {
                        ((Activity)iLoginView).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iLoginView.onLoginResult(true, code);
                            }
                        });
                        System.out.println("right");
                    }

                    @Override
                    public void onFailed(BaseRestApi object, String message) {

                    }

                    @Override
                    public void onError(BaseRestApi object, Exception e) {

                    }

                    @Override
                    public void onTimeout(BaseRestApi object) {

                    }

                    @Override
                    public void onCancelled(BaseRestApi object) {

                    }
                };
                loginRA.setListener(baseRestApiListener);
                try {
                    loginRA.call(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void setProgressBarVisiblity(int visiblity) {
        iLoginView.setProgressBarVisiblity(visiblity);
    }

    void initUser() {
        user = new UserModel("mvp", "mvp");
    }
}
