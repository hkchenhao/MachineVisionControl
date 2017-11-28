package com.hanyu.hust.testnet.entity;
// modified by heqian on 2017/8/3

/**
 * 配置文件结构体
 */
public class ButtonJson {

    // 正面信息
    public InfoFront infoFront;

    // 背面信息
    public InfoBack infoBack;

    // 检测任务
    public Task task;

    // 尺寸检测
    public TaskSize taskSize;

    // 曝光信息
    public Exposure exposure;

    // 算法信息
    public Alg alg;

    // 系统配置信息
    public SysCtrl sysCtrl;

    // 其它
    public Other other;

    public class InfoFront {
        public String materialF;
        public String sizeF;
        public String shapeF;
        public String holeNumF;
        public String lightF;
        public String patternF;
        public String colorF;
    }

    public class InfoBack {
        public String materialB;
        public String sizeB;
        public String shapeB;
        public String holeNumB;
        public String lightB;
        public String patternB;
        public String colorB;
    }

    public class Task {
        public String taskSize;
        public String taskSurface;
        public String taskPit;
        public String taskDiff;
    }

    public class Exposure {
        public String srcFront;
        public String srcBack;
        public String exFront;
        public String exBack;
    }

    public class Alg {
        public String algSize;
        public String algSurface;
        public String algPit;
        public String algDiff;
    }

    public class SysCtrl {
        public String pan;
        public String speed;
    }

    public class Other {
        public String others;
    }

    // 尺寸检测
    public class TaskSize {
        // 外径d
        public double outDia;
        // 线孔直径d0
        public double holeDia;
        // 线孔距离e
        public double holeDist;

        // 外径上偏差
        public double outDiaDevUp;
        // 外径下偏差
        public double outDiaDevDown;

        // 孔距上偏差
        public double holeDistDevUp;
        // 孔距下偏差
        public double holeDistDevDown;

        // 孔径上偏差
        public double holeDiaDevUp;
        // 孔径下偏差
        public double holeDiaDevDown;
    }

    public double getOutDia() {
        return taskSize.outDia;
    }

    public double getHoleDia() {
        return taskSize.holeDia;
    }

    public double getHoleDist() {
        return taskSize.holeDist;
    }

    public double getOutDiaDevUp() {
        return taskSize.outDiaDevUp;
    }

    public double getOutDiaDevDown() {
        return taskSize.outDiaDevDown;
    }

    public double getHoleDistDevUp() {
        return taskSize.holeDistDevUp;
    }

    public double getHoleDistDevDown() {
        return taskSize.holeDistDevDown;
    }

    public double getHoleDiaDevUp() {
        return taskSize.holeDiaDevUp;
    }

    public double getHoleDiaDevDown() {
        return taskSize.holeDiaDevDown;
    }

    @Override
    public String toString() {
        return  "外径d:        " + getOutDia() + "\n" +
                "外径上偏差Δd+: " + getOutDiaDevUp() + "\n" +
                "外径下偏差Δd-: " + getOutDiaDevDown() + "\n" +

                "孔径d0:        " + getHoleDia() + "\n" +
                "孔径上偏差Δd0+: " + getHoleDiaDevUp() + "\n" +
                "孔径下偏差Δd0-: " + getHoleDiaDevDown() + "\n" +

                "孔距e:       " + getHoleDist() + "\n" +
                "孔距上偏差e+: " + getHoleDistDevUp() + "\n" +
                "孔距下偏差e-: " + getHoleDistDevDown() + "\n";
    }
}
