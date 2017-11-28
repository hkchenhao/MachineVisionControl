package com.hanyu.hust.testnet.ui.view;
/*
 *  项目名：  MPAndroidChartSample 
 *  包名：    com.liuguilin.mpandroidchartsample
 *  文件名:   DayAxisValueFormatter
 *  创建者:   LGL
 *  创建时间:  2016/11/17 10:15
 *  描述：    日期转换
 */

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * X 轴菜单数据适配器，用于绘制柱状图
 */
public class XAxisValueFormatter implements IAxisValueFormatter {

    private List<String> labels;

    /**
     * @param labels 要显示的标签字符数组
     */
    public XAxisValueFormatter(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return labels.get((int) value % labels.size());
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
