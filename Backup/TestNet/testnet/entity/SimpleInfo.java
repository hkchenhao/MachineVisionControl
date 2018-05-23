package com.hanyu.hust.testnet.entity;

/**
 * Created by Administrator on 2017/5/5.
 * 尺寸测量的简洁版Json格式
 */
public class SimpleInfo {

    public Button button;

    public double getOutDia() {
        return button.outDia;
    }

    public double getOutDiaDev() {
        return button.outDiaDev;
    }

    class Button {
        public double outDia;

        public double outDiaDev;
    }
}
