package com.william_l.wemore.Model;

import java.util.List;

/**
 * Created by william on 2016/3/28.
 */
public class WeatherData {

    public City City;

    //  Internal parameter
    public String cod;

    //  Internal parameter
    public float message;

    //Number of lines returned by this API call
    public int cnt;
    public List<Weathers> Weathers;

    public void printData() {

        System.out.println(cod + "," + message + "," + cnt);

    }

}
