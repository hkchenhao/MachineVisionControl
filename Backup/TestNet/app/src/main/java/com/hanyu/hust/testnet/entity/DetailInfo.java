package com.hanyu.hust.testnet.entity;

import com.hanyu.hust.testnet.utils.MyUtils;

/**
 * Created by Administrator on 2017/5/5.
 * // modified by heqian on 2017/8/3
 * 尺寸测量详细版的Json格式
 */
public class DetailInfo {

    private static final String TAG = "DetailInfo";

    public Button button;

    class Button {
        public int outDia =0 ;

        public int outDiaDev =0;

        public int holeDist1 =0;
        public int holeDist2 =0;


        public int holeDist3 =0;
        public int holeDist4 =0;

        public int holeDia1 =0;
        public int holeDia2 =0;
        public int holeDia3 =0;
        public int holeDia4 =0;

        public int centerDrift =0;

        // 崩边21H 堵孔22H 尺寸偏差23H 异物24H 凹坑/破损25H  气泡26H 不均匀27H 多孔 28H 少孔29H  孔偏移2AH 其他2FH
        public int judgeResult =0;
    }

    public int getOutDia() {
        return button.outDia;
    }
    public int getOutDiaDev() {
        return button.outDiaDev;
    }
    public int getHoleDist1() {
        return button.holeDist1;
    }
    public int getHoleDist2()
    {
        return button.holeDist2;
    }
    public int getHoleDist3() {
        return button.holeDist3;
    }
    public int getHoleDist4() {
        return button.holeDist4;
    }
    public int getHoleDia1() {
        return button.holeDia1;
    }
    public int getHoleDia2() {
        return button.holeDia2;
    }
    public int getHoleDia3() {
        return button.holeDia3;
    }
    public int getHoleDia4() {
        return button.holeDia4;
    }
    public int getCenterDrift() {
        return button.centerDrift;
    }
    public int getJudgeResult() {
        return button.judgeResult;
    }

    @Override
    public String toString() {
        int result = getJudgeResult();
        String str = null;

        if (result == 0)
             str = "正常";
        else if (result == 0x21)
            str = "崩边";
        else if (result == 0x22)
            str = "线孔变形";
        else if (result == 0x23)
            str = "尺寸偏差";
        else if (result == 0x24)
            str = "异物";
        else if (result == 0x25)
            str = "凹坑/破损";
        else if (result == 0x26)
            str = "气泡";
        else if (result == 0x27)
            str = "不均匀";
        else if (result == 0x28)
            str = "多孔";
        else if (result == 0x29)
            str = "少孔";
        else if (result == 0x2A)
            str = "孔偏移";
        else if (result == 0x2B)
            str = "轮廓变形";
        else if (result == 0x2F)
            str = "其他";
        else if (result == 0x31)
            str = "光照不适";
        else if (result == 0x32)
            str = "提取ROI出错";

        return  "外径d:      " + MyUtils.getsDouble(getOutDia()) + "\n" +
                "外径偏差Δd: " + MyUtils.getsDouble(getOutDiaDev()) + "\n" +

                "孔径d01:    " + MyUtils.getsDouble(getHoleDia1()) + "\n" +
                "孔径d02:    " + MyUtils.getsDouble(getHoleDia2()) + "\n" +
                "孔径d03:    " + MyUtils.getsDouble(getHoleDia3()) + "\n" +
                "孔径d04:    " + MyUtils.getsDouble(getHoleDia4()) + "\n" +

                "孔距e1:   " + MyUtils.getsDouble(getHoleDist1()) + "\n" +
                "孔距e2:   " + MyUtils.getsDouble(getHoleDist2()) + "\n" +
                "孔距e3:   " + MyUtils.getsDouble(getHoleDist3()) + "\n" +
                "孔距e4:   " + MyUtils.getsDouble(getHoleDist4()) + "\n" +

                "孔偏移l:  " + MyUtils.getsDouble(getCenterDrift()) + "\n" +
                "判别结果:   "  +  str;
    }
}
