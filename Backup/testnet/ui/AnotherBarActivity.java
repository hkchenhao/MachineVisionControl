package com.hanyu.hust.testnet.ui;

/*
 *  项目名：  MPAndroidChartSample 
 *  包名：    com.liuguilin.mpandroidchartsample.activity
 *  文件名:   AnotherBarActivity
 *  创建者:   LGL
 *  创建时间:  2016/11/18 11:27
 *  描述：    底部值柱形图
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.ui.view.XAxisValueFormatter;
import com.hanyu.hust.testnet.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hanyu.hust.testnet.utils.PreferencesUtils.getInt;

/**
 * 柱状图显示界面，对应统计分析
 */
public class AnotherBarActivity extends Activity implements View.OnClickListener {

    /**
     * 柱状图控件
     */
    private BarChart mBarChart;

    //显示顶点值
    private Button btn_show_values;
    //保存到sd卡
    private Button btn_save_pic;
    //返回
    private Button btn_return_upper;
    private  boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);
        Intent intent = getIntent();
        flag = intent.getBooleanExtra("DadaFlag",false);
        initView();
    }

    /**
     * 初始化柱状图控件
     */
    private void initView() {

        //基本控件
        btn_show_values = (Button) findViewById(R.id.btn_show_values);
        btn_show_values.setOnClickListener(this);
        btn_save_pic = (Button) findViewById(R.id.btn_save_pic);
        btn_save_pic.setOnClickListener(this);
        btn_return_upper = (Button) findViewById(R.id.btn_return_upper);
        btn_return_upper.setOnClickListener(this);


        //创建柱状图
        mBarChart = (BarChart) findViewById(R.id.mBarChart);
        //基本属性设置
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawGridBackground(false);
        mBarChart.setExtraOffsets(30, 60, 30, 50);

        // 创建X轴
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(30);
       //创建Y轴
        YAxis yAxis = mBarChart.getAxisLeft();
        mBarChart.getAxisRight().setEnabled(false);//关闭右侧y轴
        mBarChart.getAxisLeft().setDrawGridLines(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setTextSize(30);
//        mBarChart.animateY(2500);//Y轴动画
        // 配置X轴标签
        List<String> xlabels = setxlabels();
        xAxis.setValueFormatter(new XAxisValueFormatter(xlabels));

        //关闭图例
        mBarChart.getLegend().setEnabled(false);
        //设置数据
        ArrayList<Integer> datas = getCountData();
        setData(datas);

    }



    //设置数据
    private void setData(List<Integer> datas) {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            yVals1.add(new BarEntry(i, datas.get(i)));
        }

        BarDataSet set1;
        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "日期设置");
            //设置多彩 也可以单一颜色
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);


            BarData data = new BarData(dataSets);
            mBarChart.setData(data);
            mBarChart.setFitBars(true);
        }
        mBarChart.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //显示顶点值
            case R.id.btn_show_values:
                for (IDataSet set : mBarChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());
                mBarChart.invalidate();
                break;
            //保存到sd卡
            case R.id.btn_save_pic:
                if (mBarChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
                    Toast.makeText(getApplicationContext(), "保存成功",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "保存失败",
                            Toast.LENGTH_SHORT).show();
                break;
            //返回统计界面
            case R.id.btn_return_upper:
                this.finish();
                break;

        }
    }


    /**
     * 从sharedPrefrence中读取数据
     * @return
     */
    public ArrayList<Integer> getCountData() {
        ArrayList<Integer> data = new ArrayList<Integer>();

        if (flag == false) {
            // 从本地读取统计数据
            int neg1 = getInt(this, "neg1");
            int pos1 = PreferencesUtils.getInt(this, "pos1");
            int neg2 = PreferencesUtils.getInt(this, "neg2");
            int pos2 = PreferencesUtils.getInt(this, "pos2");
            int neg3 = PreferencesUtils.getInt(this, "neg3");
            int pos3 = PreferencesUtils.getInt(this, "pos3");

            data.add(pos1);
            data.add(neg1);
            data.add(pos2);
            data.add(neg2);
            data.add(pos3);
            data.add(neg3);
        }
        else{
            if (AppContext.btnCfgProfile == null){
                EToast.showToast(getApplicationContext(), "读取历史数据出错：未选择纽扣文件");
                return data;
            }
            int count;
            Log.d("TESTbug", "getTypeData: 0");
            final JSONObject json = AppContext.btnCfgProfile.getStatics();
            Log.d("TESTbug", "getTypeData: 1");
            if(json == null) {
                EToast.showToast(getApplicationContext(), "读取历史数据出错：未保存纽扣历史数据");
                return data;
            }
            Log.d("TESTbug", "getTypeData: 2");
            try {
                JSONObject Countjson = json.getJSONObject(ButtonKeys.Count);
                for (int i=0; i<ButtonKeys.countS.length; i++) {
                    count = Countjson.getInt(ButtonKeys.countS[i]);
                    data.add(count);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return data;
            }
        }

        // 模拟测试
//        data.add(20);
//        data.add(3);
//        data.add(0);
//        data.add(0);
//        data.add(0);
//        data.add(0);

        return data;
    }


    public List<String> setxlabels() {
        List<String> xlabels = new ArrayList<>();
        xlabels.add("相机1正品");
        xlabels.add("相机1次品");
        xlabels.add("相机2正品");
        xlabels.add("相机2次品");
        xlabels.add("相机3正品");
        xlabels.add("相机3次品");
        return xlabels;
    }

}