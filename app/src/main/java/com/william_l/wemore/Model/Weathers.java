package com.william_l.wemore.Model;

import java.util.List;

/**
 * Created by william on 2016/3/28.
 */
public class Weathers {

    // Time of data forecasted, unix, UTC
    public int dt;
    public main main;

    // (more info Weather condition codes)
    public List<Weather> weather;
    public Clouds Clouds;
    public Wind Wind;
    public Sys_pod Sys_pod;

    // Data/time of caluclation, UTC
    public String dt_txt;

}
