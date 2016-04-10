package com.william_l.wemore.Login.Model;

/**
 * Created by william on 2016/4/1.
 */
public interface IUser {

    String getName();

    String getPassword();

    int checkUserValidity(String name, String password);

}
