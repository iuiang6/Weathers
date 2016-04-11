package com.william_l.wemore.Weather.Presenter;

import android.content.Context;

import com.william_l.wemore.Api.Constant.Perference;
import com.william_l.wemore.Api.MyThreadPool;
import com.william_l.wemore.Api.http.HttpConnection;
import com.william_l.wemore.Weather.View.IWeatherView;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;


/**
 * Created by william on 2016/4/4.
 */
public class WeahtersPresenterCompl implements IWeathersPresenter {


    private Context context;
    private IWeatherView iWeatherView;

    public WeahtersPresenterCompl(Context context, IWeatherView iWeatherView) {
        this.context = context;
        this.iWeatherView = iWeatherView;
    }

    @Override
    public void loadDatas() {


        ExecutorService cachedExecutor = MyThreadPool.getInstance();

        for (int i = 0; i < 10; i++) {


            cachedExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpConnection connection = new HttpConnection();
                        String url = String.format(Perference.WeatherUrl, Perference.WeatherAppid);
                        String jsonString = connection.getData(url, null, null);

                        if (null != jsonString) {
                            System.out.println(jsonString);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        // iWeatherView.loadWeatherData();

    }
}
