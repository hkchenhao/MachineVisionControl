package com.hanyu.hust.testnet.ui;
/*
 *  项目名：  MPAndroidChartSample
 *  包名：    com.liuguilin.mpandroidchartsample
 *  文件名:   PieChartActivity
 *  创建者:   LGL
 *  创建时间:  2016/11/17 11:20
 *  描述：    饼状图
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PieChartActivity extends Activity implements OnChartValueSelectedListener, View.OnClickListener{

    private PieChart mPieChart;

    //显示百分比
    private Button btn_show_percentage;
    //显示类型
    private Button btn_show_type;
    //保存到sd卡
    private Button btn_save_pic;
    //返回按钮
    private Button btn_return_upper;
    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);
        Intent intent = getIntent();
        flag = intent.getBooleanExtra("DadaFlag",false);
        initView();
    }

    //初始化View
    private void initView() {
        //添加按钮监听
        btn_show_percentage = (Button) findViewById(R.id.btn_show_percentage);
        btn_show_percentage.setOnClickListener(this);
        btn_show_type = (Button) findViewById(R.id.btn_show_type);
        btn_show_type.setOnClickListener(this);
        btn_save_pic = (Button) findViewById(R.id.btn_save_pic);
        btn_save_pic.setOnClickListener(this);
        btn_return_upper = (Button) findViewById(R.id.btn_return_upper);
        btn_return_upper.setOnClickListener(this);

        //饼状图
        mPieChart = (PieChart) findViewById(R.id.mPieChart);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(30, 60, 30, 50);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        //设置中间文件
        mPieChart.setCenterText(generateCenterSpannableText());
        mPieChart.setCenterTextSize(30f);

        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);

        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);

        mPieChart.setHoleRadius(58f);
        mPieChart.setTransparentCircleRadius(61f);

        mPieChart.setDrawCenterText(true);

        mPieChart.setRotationAngle(0);
        // 触摸旋转
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);

        //变化监听
        mPieChart.setOnChartValueSelectedListener(this);


        ArrayList<PieEntry> entries = getTypeData();
        //设置数据
        setData(entries);

        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        //图例
        Legend l = mPieChart.getLegend();
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setTextSize(30f);                   //设置图例标签文本的大小
        l.setFormSize(30);

        // 输入标签样式
        mPieChart.setEntryLabelColor(Color.BLACK);
        mPieChart.setEntryLabelTextSize(28f);

    }

    //设置中间文字
    private SpannableString generateCenterSpannableText() {
        //原文：MPAndroidChart\ndeveloped by Philipp Jahoda
        SpannableString s = new SpannableString("缺陷类型");
        //s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        //s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        // s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        //s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        // s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        // s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    //设置数据
    private void setData(ArrayList<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "树脂纽扣");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        //数据和颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(28f);
        data.setValueTextColor(Color.BLACK);
        mPieChart.setData(data);
        mPieChart.highlightValues(null);
        //刷新
        mPieChart.invalidate();
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //显示百分比
            case R.id.btn_show_percentage:
                for (IDataSet<?> set : mPieChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mPieChart.invalidate();
                break;
            //显示类型
            case R.id.btn_show_type:
                if (mPieChart.isDrawHoleEnabled())
                    mPieChart.setDrawHoleEnabled(false);
                else
                    mPieChart.setDrawHoleEnabled(true);
                mPieChart.invalidate();
                break;

            //保存到sd卡
            case R.id.btn_save_pic:
                mPieChart.saveToPath("title" + System.currentTimeMillis(), "");
                break;
            //返回统计界面
            case R.id.btn_return_upper:
                this.finish();
                break;
        }
    }

    public ArrayList<PieEntry> getTypeData() {
        //模拟数据
        ArrayList<PieEntry> data = new ArrayList<PieEntry>();
        // 从本地读取统计数据
        if (flag == false) {
            int data1 = PreferencesUtils.getInt(this, "DEF_EDGE");
            if (data1 != 0)
                data.add(new PieEntry(data1, "崩边"));

            int data2 = PreferencesUtils.getInt(this, "DEF_HOLE");
            if (data2 != 0)
                data.add(new PieEntry(data2, "孔缺陷"));

            int data3 = PreferencesUtils.getInt(this, "DEF_GEOM");
            if (data3 != 0)
                data.add(new PieEntry(data3, "几何偏差"));

            int data4 = PreferencesUtils.getInt(this, "DEF_UNFI");
            if (data4 != 0)
                data.add(new PieEntry(data4, "不均匀"));

            int data5 = PreferencesUtils.getInt(this, "DEF_BLOB");
            if (data5 != 0)
                data.add(new PieEntry(data5, "气泡"));

            int data6 = PreferencesUtils.getInt(this, "DEF_PITS");
            if (data6 != 0)
                data.add(new PieEntry(data6, "表面破损"));

            int data7 = PreferencesUtils.getInt(this, "DEF_FORE");
            if (data7 != 0)
                data.add(new PieEntry(data7, "异物"));

            int data8 = PreferencesUtils.getInt(this, "DEF_OTHR");
            if (data8 != 0)
                data.add(new PieEntry(data8, "其他"));
        }
        else {
            if (AppContext.btnCfgProfile == null){
                EToast.showToast(getApplicationContext(), "读取历史数据出错：未选择纽扣文件");
                return null;
            }
            int flawCount;
            final JSONObject json = AppContext.btnCfgProfile.getStatics();
            if(json == null) {
                EToast.showToast(getApplicationContext(), "读取历史数据出错：未保存纽扣历史数据");
                return null;
            }
            try {
                JSONObject Flawjson = json.getJSONObject(ButtonKeys.Flaw);
                for (int i=0; i<ButtonKeys.flawS.length; i++) {
                    flawCount = Flawjson.getInt(ButtonKeys.flawS[i]);
                    if(flawCount != 0)
                    data.add(new PieEntry(flawCount , Constants.btnFlawCnList[i]));
                }
          } catch (JSONException e) {
                e.printStackTrace();
                return data;
            }
        }
//        data.add(new PieEntry(10, "崩边"));
//        data.add(new PieEntry(20, "堵孔"));
//        data.add(new PieEntry(20, "几何偏差"));
//        data.add(new PieEntry(10, "异物"));
//        data.add(new PieEntry(15, "气泡"));
//        data.add(new PieEntry(15, "表面破损"));
//        data.add(new PieEntry(10, "不均匀"));
//        data.add(new PieEntry(20, "其他"));

        return data;
    }

}
