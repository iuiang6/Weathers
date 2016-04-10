package com.william_l.wemore.Home.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.william_l.wemore.Home.Util.ActivityHolder;
import com.william_l.wemore.Home.View.IHomeView;
import com.william_l.wemore.Login.LoginActivity;
import com.william_l.wemore.Weather.WeathersActivity;

/**
 * Created by william on 2016/3/31.
 */
public class HomePresenterCompl implements IHomePresenter {

    public static ActivityHolder activityHolder;

    static {
        activityHolder = new ActivityHolder();
        activityHolder.addActivity("Login", LoginActivity.class);
        activityHolder.addActivity("Weather", WeathersActivity.class);

    }

    private Context context;
    private IHomeView iHomeView;

    public HomePresenterCompl(Context context, IHomeView iHomeView) {
        this.context = context;
        this.iHomeView = iHomeView;
    }

    @Override
    public void loadDatas() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iHomeView.onGetDataList(activityHolder.getNameList());
            }
        }, 2000);


    }

    @Override
    public void onItemClick(int position) {

        Class activity = activityHolder.getActivity(activityHolder.getNameList().get(position));
        if (null != activity)
            context.startActivity(new Intent(context, activity));

    }
}
