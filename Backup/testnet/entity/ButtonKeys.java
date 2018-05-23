package com.hanyu.hust.testnet.entity;

/**
 * Created by Administrator on 2017/6/11.
 * Button文件的键值
 */

public class ButtonKeys {
/*纽扣
* 首字母大写为一级字段
* 小写字母为二级字段*/
    public static final String NAME = "name";

    public static final String FRONT = "infoFront";
    public static final String materialF = "materialF";
    public static final String sizeF = "sizeF";
    public static final String shapeF = "shapeF";
    public static final String holeNumF = "holeNumF";
    public static final String lightF = "lightF";
    public static final String patternF = "patternF";
    public static final String colorF = "colorF";


    public static final String TASK_SIZE = "taskSize";


    /*方法选择*/
    public static final String ALG = "algorithm";
    public static final String geometryF = "geometryF";
    public static final String surfaceF = "surfaceF";

    /*几何检测方法配置*/
    public static final String ALG_GEOMETRY_SET = "geometryAlgorithm";

    public static final String[ ] AlgGeometryF = {"getROI","getOutline","holeNumberDetect",
            "holeOutlineDetect","OutlineDetect","sizeDetect"   };//获取ROI 、 轮廓提取 、孔数量检测、孔形状缺陷检测、边缘破损缺陷检测、尺寸偏差缺陷检测
    /*表面检测方法配置*/
    public static final String ALG_SURFACE_SET = "surfaceAlgorithm";




    public static final String GEOMETRY_ALGPARA = "geometryalgPra";




/*以下为STA文件关键字段*/
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

    public static final String LIGHT = "light";
    public static final String[] litgtSy = {
            "light1","light2","light3","light4","light5","light6"
    };



}
