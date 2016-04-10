package com.william_l.wemore.Weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.william_l.wemore.R;
import com.william_l.wemore.Weather.Presenter.IWeathersPresenter;
import com.william_l.wemore.Weather.Presenter.WeahtersPresenterCompl;
import com.william_l.wemore.Weather.View.IWeatherView;

import java.util.ArrayList;
import java.util.List;

public class WeathersActivity extends Activity implements IWeatherView {

    private TextView tv_city;
    private TextView tv_weather;
    private ListView list_weathers;

    private List<String> datas = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    private IWeathersPresenter weathersPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weathers);

        tv_city = (TextView) findViewById(R.id.tv_city_info);
        tv_weather = (TextView) findViewById(R.id.tv_weather_info);
        list_weathers = (ListView) findViewById(R.id.list_weathers);

        View loadingView = LayoutInflater.from(this).inflate(R.layout.item_empty_view, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        list_weathers.setEmptyView(loadingView);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.datas);
        list_weathers.setAdapter(adapter);

        weathersPresenter = new WeahtersPresenterCompl(this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        weathersPresenter.loadDatas();
    }

    @Override
    public void loadWeatherData(List<String> datas) {

        if (null != datas && datas.size() != 0) {

            this.datas.clear();
            this.datas.addAll(datas);
            adapter.notifyDataSetChanged();

        }

    }

}
