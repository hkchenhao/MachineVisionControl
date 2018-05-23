package com.hanyu.hust.testnet.entity;

/**
 * Created by Administrator on 2017/6/11.
 * Button文件的键值
 */

public class ButtonKeys {
/*钮扣
* 首字母大写为一级字段
* 小写字母为二级字段*/
    /*名字*/
    public static final String NAME = "name";
    /*正面信息字段*/
    public static final String FRONT = "infoFront";
    public static final String materialF = "materialF";
    public static final String sizeF = "sizeF";
    public static final String shapeF = "shapeF";
    public static final String holeNumF = "holeNumF";
    public static final String lightF = "lightF";
    public static final String reflectF = "reflectF";
    public static final String patternF = "patternF";
    public static final String colorF = "colorF";


    /*几何尺寸*/
    public static final String TASK_SIZE = "taskSize";

    /*系统参数*/
    public static final String SYSTEMPARAMETER = "systemParameter";
    public static final String[] blowSy ={
      "lastTime","pressure"   //持续时间/压力值
    };
    public static final String[] enginSpeedSy = {
        "charge","transmit","turntable" //
    };
    public static final String[] litgtSy = {
            "light1","light2","light3","light4","light5","light6"//
    };

    /*检测项目*/
    public static final String METHOD = "method";
    //几何字段
    public static final String[] GeoMehodCs = {"geometryMethodCs1","geometryMethodCs2","geometryMethodCs3","geometryMethodCs4"
    };
    public static final String[] GeoMehodOb = {"geometryMethodOb1","geometryMethodOb2","geometryMethodOb3","geometryMethodOb4"
    };
    //表面字段
    public static final String[] SurMehodCs = {
            "surfaceMethodCs1","surfaceMethodCs2","surfaceMethodCs3","surfaceMethodCs4","surfaceMethodCs5","surfaceMethodCs6","surfaceMethodCs7"
    };
    public static final String[] SurMehodOb = {
            "surfaceMethodOb1","surfaceMethodOb2","surfaceMethodOb3","surfaceMethodOb4","surfaceMethodOb5","surfaceMethodOb6","surfaceMethodOb7"
    };

    /*自定方法*/
    public static final String OPERATOR = "operator";
    public static final String[] GeoOperator = {"geometryOperator1","geometryOperator2","geometryOperator3","geometryOperatorEnable" };
    public static final String[] SurOperator = {"surfaceOperator1","surfaceOperator2","surfaceOperator3" ,"surfaceOperatorEnable"};
    public static final String GeoMethod = "geometryMethod";
    public static final String SurMethod = "surfaceMethod";

    /*方法参数*/
    public static final String ALGPARA = "algParameter";
    //几何字段
    public static final String[] GeoMethodPara = {
            "geometryMethodParameter1", "geometryMethodParameter2", "geometryMethodParameter3", "geometryMethodParameter4",
            "geometryMethodParameter5", "geometryMethodParameter6" ,"geometryMethodParameter7", "geometryMethodParameter8"
    };
    //表面字段
    public static final String[] SurMethodPara = {
            "surfaceMethodParameter1", "surfaceMethodParameter2", "surfaceMethodParameter3", "surfaceMethodParameter4",
            "surfaceMethodParameter5", "surfaceMethodParameter6", "surfaceMethodParameter7", "surfaceMethodParameter8"
    };

    /*几何方法参数字段*/
    public static final String GEOMETRY_ALGPARA = "geometryalgPra";

    /*
* *以下为STA文件关键字段
* */
    public static final String STA = "statistics";

    public static final String STAtime = "staTime";

    public static final String Count = "count";
    public final static String[] countS = {
            "pos1S","neg1S","pos2S","neg2S","pos3S","neg3S"
    };
    public static final String Flaw = "flaw";
    public final static String[] flawS = {
            "edgeS","holeS","geomS","unfiS","blobS","pitsS","foreS","othrS"
    };





}
