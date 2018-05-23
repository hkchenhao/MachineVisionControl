package com.hanyu.hust.testnet.utils;


public class Constants {

    public final static int REQUEST_CODE_FILE_MANAGER = 1;

    public final static int REQUEST_CODE_CAMERA_PARAMS = 2;

    public final static int REQUEST_CODE_SYS_SETTINGS = 3;

    public final static int REQUEST_CODE_STATSICS_SETTINGS = 4;

    public final static int REQUEST_CODE_MACHINE_LEARNING = 5;

    public final static int REQUEST_CODE_HELP = 6;

    public final static int REQUEST_CODE_BTN_CFG = 7;

    public final static int REQUEST_CODE_ALG_CFG = 8;

    public static final int ERROR_MESSAGE = 0x5555;


    public static final int TIME_TASK = 2017;

    /**
     * 连接超时时间
     */
    public static final int TIMEOUT = 5000;

    /**
     * 连接成功状态码
     */
    public static final int CONNECT_SUCCESS = 100;
    /**
     * 连接失败状态码
     */
    public static final int CONNECT_FAIL = 101;

    /**
     * 连接异常状态码
     */
    public static final int CONNECT_EXCEPTION = 102;


    /**
     * 连接断开成功状态码
     */
    public static final int CLOSE_FAIL = 103;

    public final static String btnCfgRetStr = "btnCfg";
    public final static String algCfgRetStr = "algCfg";
    public final static String optCfgRetStr = "optCfg";

    public final static String[] cameraList = {
            "1",
            "2",
            "3"
    };

    /*相机列表*/
    public final static String[] camList = {
            "相机1","相机2","相机3"  };
    /*纽扣主色列表*/
    public final static String[] btnColorTypeCnList = {
            "白色","红色","橙色","黄色","绿色","青色","蓝色","紫色","黑色"};
    public final static String[] btnColorTypeEnList = {
            "white","red","orange","yellow","green","cyan","blue","purple","black"};
    public static String btnColorEn2Cn(String color){
        for (int i = 0; i < btnColorTypeEnList.length; i++) {
            if (color.equals(btnColorTypeEnList[i])) {
                return btnColorTypeCnList[i];
            }
        }
        return "不明颜色";
    }
    /*纽扣花色列表*/
    public final static String[] btnPatternTypeCnList = {
            "纯色","拼色","花纹","字符","图案"};
    public final static String[] btnPatternTypeEnList = {
            "solidColor","multiColor","texture","character","picture"};
    public static String btnPatternEn2Cn(String color){
        for (int i = 0; i < btnPatternTypeEnList.length; i++) {
            if (color.equals(btnPatternTypeEnList[i])) {
                return btnPatternTypeCnList[i];
            }
        }
        return "不明颜色";
    }

    /*纽扣透明程度列表*/
    public final static String[] btnLightTypeCnList = {
            "透明","半透明","不透明"};
    public final static String[] btnLightTypeEnList = {
            "transparent","semiTransparent","none"};
    public static String btnLightEn2Cn(String color){
        for (int i = 0; i < btnLightTypeEnList.length; i++) {
            if (color.equals(btnLightTypeEnList[i])) {
                return btnLightTypeCnList[i];
            }
        }
        return "不明";
    }

    /*纽扣形状列表*/
    public final static String[] btnShapeTypeCnList = {
            "圆形","心形","三角形","方形","五角形","梅花形","异性"};

    public final static String[] btnShapeTypeEnList = {
            "circle","heart","triangle","square","pentagon","quincunx","other" };

    public static String btnShapeEn2Cn(String color){
        for (int i = 0; i < btnShapeTypeEnList.length; i++) {
            if (color.equals(btnShapeTypeEnList[i])) {
                return btnShapeTypeCnList[i];
            }
        }
        return "不明";
    }


    /*纽扣材质列表*/
    public final static String[] btnMaterialTypeCnList = {
            "树脂","果实","贝壳","金属","玉石"};
    public final static String[] btnMaterialTypeEnList = {
            "resin","fruit","shell","metal","jade"};
    public static String btnMaterialEn2Cn(String color){
        for (int i = 0; i < btnMaterialTypeEnList.length; i++) {
            if (color.equals(btnMaterialTypeEnList[i])) {
                return btnMaterialTypeCnList[i];
            }
        }
        return "不明";
    }

    /*纽扣线孔列表*/
    public final static String[] btnHoleNumCnList = {
            "2","4"};
    public final static String[] btnHoleNumEnList = {
            "2","4"};

//    added by heqian , 2017/9
    /*纽扣几何算法列表*/
    public final static String[] btnGeoALGCnList = {
        "不检测", "胡氏算法"};

    public final static String[] btnGeoALGEnList = {
             "None","ZWHAlgorithm"};

    public static String btnGeoALGEn2Cn(String str){
        for (int i = 0; i < btnGeoALGEnList.length; i++) {
            if (str.equals(btnGeoALGEnList[i])) {
                return btnGeoALGCnList[i];
            }
        }
        return "不明";
    }



    //    added by heqian , 2017/9
    /*纽扣表面算法列表*/
    public final static String[] btnSurALGCnList = {
            "不检测", "刘力哲法","魏陶然法","舒博士法","童星法","正反面训练"};

    public final static String[] btnSurALGEnList = {
            "None","LLLAlgorithm","TRWAlgorithm","DoctorSUAlgorithm","ChildStarAlgorithm","LLLTrainAlgorithm"} ;

    public static String btnSurALGEn2Cn(String str){
        for (int i = 0; i < btnSurALGEnList.length; i++) {
            if (str.equals(btnSurALGEnList[i])) {
                return btnSurALGCnList[i];
            }
        }
        return "不明";
    }

    //    added by heqian , 2017/9
    /*缺陷类型列表*/
    public final static  String[] btnFlawCnList = {
            "崩边", "堵孔", "几何偏差", "不均匀", "气泡", "表面破损", "异物", "其他"
    };

    public final static String[] algCnList = {
            "无",
            "raw2RGB",
            "ROI提取（彩色）",
            "水平展开",
            "ROI裁剪",
            "HOG特征",
            "SVM模型"};

    public final static String[] algEnList = {
            "none",
            "raw2RGB",
            "getRoiPlus",
            "polarTransform",
            "cropRoi",
            "hogFeature",
            "probabilitySvmPrediction"};




}
