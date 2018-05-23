package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.ui.StatisticsActivity;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.ui.view.XAxisValueFormatter;
import com.hanyu.hust.testnet.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hanyu.hust.testnet.utils.PreferencesUtils.getInt;

/**
 * Created by HeQian on 2018/2
 * 统计分析界面下柱状图碎片
 */

public class FragmentBarchat extends Fragment  implements OnChartValueSelectedListener, View.OnClickListener {
    /*界面资源声明*/
    final String TAG = "FragmentBarchart";
    private View mView;
    private BarChart mBarChart;  // 柱状图控件
    //显示顶点值
    private Button btn_show_values;
    //保存到sd卡
    private Button btn_save_pic;
    //返回
    private Button btn_return_upper;

    private  boolean flag = false;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof StatisticsActivity) {
            StatisticsActivity statisticsActivity = (StatisticsActivity) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fr_statistics_barchart, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: 0");
        initView();
        Log.d(TAG, "onActivityCreated: 1");
    }

    /**
     * 初始化柱状图控件
     */
    private void initView() {
        
        flag = ((StatisticsActivity)getActivity()).getDataSrc();

        Log.d(TAG, "initView: 0");
        //创建柱状图
        mBarChart = (BarChart) getView().findViewById(R.id.mBarChart);
        Log.d(TAG, "initView: 0");
        //基本属性设置
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawGridBackground(false);
        mBarChart.setExtraOffsets(30, 60, 30, 50);
        Log.d(TAG, "initView: 1");
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
        Log.d(TAG, "initView: 2");
//        mBarChart.animateY(2500);//Y轴动画
        // 配置X轴标签
        List<String> xlabels = setxlabels();
        xAxis.setValueFormatter(new XAxisValueFormatter(xlabels));

        //关闭图例
        mBarChart.getLegend().setEnabled(false);
        //设置数据
        ArrayList<Integer> datas = getCountData();
        Log.d(TAG, "initView: 3");
        setData(datas);

    }


    /**
     * 设置数据
     * @param datas
     */
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
    @OnClick({R.id.bt_static_exit,R.id.bt_static_save,R.id.bt_barchart_func1})
    public void onClick(View v) {
        switch (v.getId()) {
            //显示顶点值
            case R.id.bt_barchart_func1:
                for (IDataSet set : mBarChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());
                mBarChart.invalidate();
                break;
            //保存到sd卡
            case R.id.bt_static_save:
                if (mBarChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
                    Toast.makeText(getActivity(), "保存成功",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "保存失败",
                            Toast.LENGTH_SHORT).show();
                break;
            //返回统计界面
            case R.id.bt_static_exit:
                getActivity().finish();
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
            int neg1 = getInt(getActivity(), "neg1");       //一号相机检测次品数
            int pos1 = PreferencesUtils.getInt(getActivity(), "pos1");//一号相机检测正品数
            int neg2 = PreferencesUtils.getInt(getActivity(), "neg2");//二号相机检测次品数
            int pos2 = PreferencesUtils.getInt(getActivity(), "pos2");//二号相机检测正品数
            int neg3 = PreferencesUtils.getInt(getActivity(), "neg3");//三号相机检测次品数
            int pos3 = PreferencesUtils.getInt(getActivity(), "pos3");//三号相机检测正品数

            data.add(pos1);
            data.add(neg1);
            data.add(pos2);
            data.add(neg2);
            data.add(pos3);
            data.add(neg3);
        }
        else{
            //从历史统计文件读取统计数据
            if (AppContext.btnCfgProfile == null){//未选择测试纽扣时弹出消息提示用户
                EToast.showToast(getActivity(), "读取历史数据出错：未选择钮扣文件");
                return data;
            }
            int count;

            final JSONObject json = AppContext.btnCfgProfile.getStatics();
            if(json == null) {//历史统计文件为空时，提示用户还未保存当前纽扣数据
                EToast.showToast( getActivity(), "读取历史数据出错：未保存钮扣历史数据");
                return data;
            }
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


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

}
