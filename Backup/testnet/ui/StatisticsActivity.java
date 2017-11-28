package com.hanyu.hust.testnet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.ui.view.EToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/6/12.
 * 统计分析
 */

public class StatisticsActivity extends BasePannelActivity {
    private Spinner sp_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示返回按钮
        setContentView(R.layout.activity_option);
        initData();
        wholeMenu = new MenuWithSubMenu(R.array.option_count_num, 0);
        init_widget();

    }

    private void initData() {

        sp_data = (Spinner)findViewById(R.id.sp_data_choose);

        String[] cam_list = {"本次数据", "历史数据"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(AppContext.getAppContext(), R.layout.layout_spinner, cam_list);
        sp_data.setAdapter(adapter1);
    }

    @Override
    protected void init_widget() {
        super.init_widget();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0: // 检测数量统计
                    {
                        boolean flag = getdatasrc();
                        Intent intent = new Intent(StatisticsActivity.this, AnotherBarActivity.class);
                        intent.putExtra("DadaFlag",flag);
                        startActivity(intent);
                    }
                    break;
                    case 1: // 缺陷类型统计
                    {
                        boolean flag = getdatasrc();
                        Intent intent = new Intent(StatisticsActivity.this, PieChartActivity.class);
                        intent.putExtra("DadaFlag",flag);
                        startActivity(intent);
                    }
                    break;
//                    case 2:
//                    {
//                        startActivity(new Intent(StatisticsActivity.this, LineChartActivity.class));
//                    }
//                    break;
                }
            }
        });
    }
    /*根据spinner获取相机ID*/
    private boolean getdatasrc(){

        switch (sp_data.getSelectedItemPosition()){
            case 0:
                return false;
            case 1:
                return true;
            default:
                return false;
        }
    }


}

