package com.william_l.wemore.Login.Presenter;

/**
 * Created by william on 2016/4/1.
 */
public interface ILoginPresenter {

    void clean();

    void doLogin(String name, String password);

    void setProgressBarVisiblity(int visiblity);

}
