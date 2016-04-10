package com.william_l.wemore.Home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.william_l.wemore.Home.Presenter.HomePresenterCompl;
import com.william_l.wemore.Home.Presenter.IHomePresenter;
import com.william_l.wemore.Home.View.IHomeView;
import com.william_l.wemore.R;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity implements IHomeView, AdapterView.OnItemClickListener {

    private ListView listView;
    private IHomePresenter homePresenter;
    private List<String> datas = new ArrayList<>();
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find view
        listView = (ListView) this.findViewById(R.id.home_list);

        listView.setOnItemClickListener(this);


        View loadingView = LayoutInflater.from(this).inflate(R.layout.item_empty_view, null);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.layout_home);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        viewGroup.addView(loadingView, layoutParams);
        listView.setEmptyView(loadingView);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas);
        listView.setAdapter(adapter);
        homePresenter = new HomePresenterCompl(this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        homePresenter.loadDatas();
    }


    @Override
    public void onGetDataList(List<String> datas) {

        if (datas != null && datas.size() > 0) {
            this.datas.clear();
            this.datas.addAll(datas);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        homePresenter.onItemClick(position);

    }
}
