package com.hanyu.hust.testnet.entity;
// modified by heqian on 2017/8/3

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.hanyu.hust.testnet.ui.fragment.FragmentCanLightSrcDrv;
import com.hanyu.hust.testnet.ui.fragment.FragmentCanMotorCtrlCard;
import com.hanyu.hust.testnet.utils.Constants;

import org.json.JSONObject;

/**
 * 配置文件结构体
 */
public class ButtonJson {
    public String name;
    public String time;

    /**
     * 正面信息
     */
    public InfoFront infoFront;

    /**
     * 背面信息
     */
    public InfoBack infoBack;

    /**
     * 尺寸检测
     */
    public TaskSize taskSize;

    /**
     * 方法配置
     */
    public Operator operator;
//
//    // 算法信息
//    public Algorithm algorithm;

    public AlgParameter algParameter;
    // 系统配置信息
    public SystemParameter systemParameter;

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

//    public class Algorithm {
//        public String geometryF;
//        public String surfaceF;
//    }

    public class Operator {
        public String geometryMethod;
        public String geometryOperatorEnable;
        public String geometryOperator1;
        public String geometryOperator2;
        public String geometryOperator3;
        public String surfaceMethod;
        public String surfaceOperatorEnable;
        public String surfaceOperator1;
        public String surfaceOperator2;
        public String surfaceOperator3;
    }


    public class AlgParameter {
        public int surfaceMethodParameter1;
        public int surfaceMethodParameter2;
        public int surfaceMethodParameter3;
        public int surfaceMethodParameter4;
        public int surfaceMethodParameter5;
        public int surfaceMethodParameter6;
        public int surfaceMethodParameter7;
        public int surfaceMethodParameter8;
        public int geometryMethodParameter1;
        public int geometryMethodParameter2;
        public int geometryMethodParameter3;
        public int geometryMethodParameter4;
        public int geometryMethodParameter5;
        public int geometryMethodParameter6;
        public int geometryMethodParameter7;
        public int geometryMethodParameter8;
    }


    public class SystemParameter {
        public int turntable;
        public int transmit;
        public int charge;
        public int pressure;
        public int lastTime;
        public int light1;
        public int light2;
        public int light3;
        public int light4;
        public int light5;
        public int light6;
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

    public static ButtonJson getButtonJson(JSONObject json) {
        JsonParser jParser = new JsonParser();
        Gson gson = new Gson();
        return gson.fromJson(jParser.parse(json.toString()).getAsJsonObject().toString(), ButtonJson.class);
    }

    /**
     * 将钮扣前面信息转成字符串
     *
     * @return
     */
    public String infoFrontToCnString() {
        if (this.infoFront != null) {
            StringBuffer str_buffer = new StringBuffer();
            str_buffer.append("材质:" + Constants.btnMaterialEn2Cn(this.infoFront.materialF) + "\n");
            str_buffer.append("尺寸:" + this.infoFront.sizeF + "\n");
            str_buffer.append("外形:" + Constants.btnShapeEn2Cn(this.infoFront.shapeF) + "\n");
            str_buffer.append("线孔数:" + this.infoFront.holeNumF + "\n");
            str_buffer.append("透明性:" + Constants.btnLightEn2Cn(this.infoFront.lightF) + "\n");
            str_buffer.append("颜色:" + Constants.btnPatternEn2Cn(this.infoFront.patternF)
                    + "/" + Constants.btnColorEn2Cn(this.infoFront.colorF));
            return str_buffer.toString();
        } else {
            return "材质:\n尺寸:\n外形:\n线孔数:\n透明性:\n颜色:";
        }
    }


    /**
     * 简略地将尺寸信息转成字符串
     *
     * @return 简略版地尺寸信息字符串
     */
    public String briefSizeInfoToString() {
        if (this.taskSize != null) {
            StringBuffer str_buffer = new StringBuffer(
                    "外径: " + this.taskSize.outDia + "[" + this.taskSize.outDiaDevDown + "," + this.taskSize.outDiaDevUp + "]\n"
                            + "线径: " + this.taskSize.holeDia + "[" + this.taskSize.holeDiaDevDown + "," + this.taskSize.holeDiaDevUp + "]\n"
                            + "线孔距: " + this.taskSize.holeDist + "[" + this.taskSize.holeDistDevDown + "," + this.taskSize.holeDistDevUp + "]");
            return str_buffer.toString();
        } else {
            return "外径:\n线径:\n线孔距:";
        }
    }

    /**
     * @return 完整版的尺寸信息字符串
     */
    public String sizeInfoToString() {
        if (this.taskSize != null) {
            StringBuffer str_buffer = new StringBuffer(
                    "外径d:        " + this.taskSize.outDia + "\n" +
                            "外径上偏差Δd+: " + this.taskSize.outDiaDevUp + "\n" +
                            "外径下偏差Δd-: " + this.taskSize.outDiaDevDown + "\n" +

                            "孔径d0:        " + this.taskSize.holeDia + "\n" +
                            "孔径上偏差Δd0+: " + this.taskSize.holeDiaDevUp + "\n" +
                            "孔径下偏差Δd0-: " + this.taskSize.holeDiaDevDown + "\n" +

                            "孔距e:       " + this.taskSize.holeDist + "\n" +
                            "孔距上偏差e+: " + this.taskSize.holeDistDevUp + "\n" +
                            "孔距下偏差e-: " + this.taskSize.holeDistDevDown + "\n"
            );
            return str_buffer.toString();
        } else {
            StringBuffer str_buffer = new StringBuffer(
                    "外径d:\n外径上偏差Δd+:\n外径下偏差Δd-:\n" +
                            "孔径d0:\n孔径上偏差Δd0+:\n孔径下偏差Δd0-:\n" +
                            "孔距e:\n孔距上偏差e+:\n孔距下偏差e-:\n"
            );
            return str_buffer.toString();
        }
    }


    public String sysCtrlParamToString() {
        if (this.systemParameter != null) {
            return "上料速度:" + FragmentCanMotorCtrlCard.DefaultFeederSpd
                    + "\n玻璃盘速度:" + this.systemParameter.turntable +
                    "\n相机1光源:" + this.systemParameter.light1 + "/" + this.systemParameter.light2 +
                    "\n相机2光源:" + this.systemParameter.light3 + "/" + this.systemParameter.light4 +
                    "\n相机3光源:" + this.systemParameter.light5 + "/" + this.systemParameter.light6;
        } else {
            return "上料速度:\n玻璃盘速度:\n相机1光源:\n相机2光源:\n相机3光源:";
        }
    }

    @Override
    public String toString() {
        return "外径d:        " + this.taskSize.outDia + "\n" +
                "外径上偏差Δd+: " + this.taskSize.outDiaDevUp + "\n" +
                "外径下偏差Δd-: " + this.taskSize.outDiaDevDown + "\n" +

                "孔径d0:        " + this.taskSize.holeDia + "\n" +
                "孔径上偏差Δd0+: " + this.taskSize.holeDiaDevUp + "\n" +
                "孔径下偏差Δd0-: " + this.taskSize.holeDiaDevDown + "\n" +

                "孔距e:       " + this.taskSize.holeDist + "\n" +
                "孔距上偏差e+: " + this.taskSize.holeDistDevUp + "\n" +
                "孔距下偏差e-: " + this.taskSize.holeDistDevDown + "\n";
    }
}
