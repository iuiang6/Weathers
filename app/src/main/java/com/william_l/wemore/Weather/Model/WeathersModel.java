package com.william_l.wemore.Weather.Model;


import com.william_l.wemore.Model.WeatherData;

/**
 * Created by william on 2016/4/4.
 */
public class WeathersModel implements IWeathers {

    private WeatherData weatherData;

    public WeathersModel(WeatherData weatherData) {
        this.weatherData = weatherData;
    }

    @Override
    public WeatherData getData() {
        return weatherData;
    }
}
