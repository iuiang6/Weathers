package com.william_l.wemore.Model;

/**
 * Created by william on 2016/3/28.
 */
public class main {

    // Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    public float temp;

    // Minimum temperature at the moment of calculation. This is deviation from 'temp' that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    public float temp_min;

    // Maximum temperature at the moment of calculation. This is deviation from 'temp' that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    public float temp_max;

    // Atmospheric pressure on the sea level by default, hPa
    public float pressure;

    // Atmospheric pressure on the sea level, hPa
    public float sea_level;

    // Atmospheric pressure on the ground level, hPa
    public float grnd_level;

    // Humidity, %
    public float humidity;

    // Internal parameter
    public float temp_kf;

}
