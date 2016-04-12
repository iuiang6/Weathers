package com.william_l.wemore.Login.Presenter;

import android.os.Handler;
import android.os.Looper;

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
    Handler handler;

    public LoginPresenterCompl(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
        initUser();
        handler = new Handler(Looper.getMainLooper());

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
                LoginRestApi loginRA = new LoginRestApi("", "");
                try {
                    loginRA.call(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                iLoginView.onLoginResult(result, code);
//            }
//        }, 3000);
    }

    @Override
    public void setProgressBarVisiblity(int visiblity) {
        iLoginView.setProgressBarVisiblity(visiblity);
    }

    void initUser() {
        user = new UserModel("mvp", "mvp");
    }
}
