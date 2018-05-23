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

    public final static String[] cameraList = {
            "1",
            "2",
            "3"
    };

    /**
     * 相机列表
     */
    public final static String[] camList = {
            "相机1", "相机2", "相机3"};
    /**
     * 钮扣主色列表
     */
    public final static String[] btnColorTypeCnList = {
            "白色", "红色", "橙色", "黄色", "绿色", "青色", "蓝色", "紫色", "黑色"};
    public final static String[] btnColorTypeEnList = {
            "white", "red", "orange", "yellow", "green", "cyan", "blue", "purple", "black"};

    public static String btnColorEn2Cn(String color) {
        for (int i = 0; i < btnColorTypeEnList.length; i++) {
            if (color.equals(btnColorTypeEnList[i])) {
                return btnColorTypeCnList[i];
            }
        }
        return "不明颜色";
    }

    /**
     * 钮扣花色列表
     */
    public final static String[] btnPatternTypeCnList = {
            "纯色", "拼色", "花纹", "印字", "图案"};
    public final static String[] btnPatternTypeEnList = {
            "solidColor", "multiColor", "texture", "character", "picture"};

    public static String btnPatternEn2Cn(String color) {
        for (int i = 0; i < btnPatternTypeEnList.length; i++) {
            if (color.equals(btnPatternTypeEnList[i])) {
                return btnPatternTypeCnList[i];
            }
        }
        return "不明颜色";
    }

    /**
     * 钮扣透明程度列表
     */
    public final static String[] btnLightTypeCnList = {
            "透明", "半透明", "不透明"};
    public final static String[] btnLightTypeEnList = {
            "transparent", "semiTransparent", "none"};

    public static String btnLightEn2Cn(String color) {
        for (int i = 0; i < btnLightTypeEnList.length; i++) {
            if (color.equals(btnLightTypeEnList[i])) {
                return btnLightTypeCnList[i];
            }
        }
        return "不明";
    }

    /**
     * 钮扣反光程度列表
     */
    public final static String[] btnReflectTypeCnList = {
            "不反光", "反光", "高反光"};
    public final static String[] btnReflectTypeEnList = {
            "none", "reflect", "highReflect"};

    public static String btnReflectEn2Cn(String color) {
        for (int i = 0; i < btnReflectTypeEnList.length; i++) {
            if (color.equals(btnReflectTypeEnList[i])) {
                return btnReflectTypeCnList[i];
            }
        }
        return "none";
    }

    /**
     * 钮扣形状列表
     */
    public final static String[] btnShapeTypeCnList = {
            "圆形", "梅花形", "心形", "方形", "其它"};

    public final static String[] btnShapeTypeEnList = {
            "circle", "quincunx", "heart", "square", "other"};

    public static String btnShapeEn2Cn(String color) {
        for (int i = 0; i < btnShapeTypeEnList.length; i++) {
            if (color.equals(btnShapeTypeEnList[i])) {
                return btnShapeTypeCnList[i];
            }
        }
        return "不明";
    }


    /**
     * 钮扣材质列表
     */
    public final static String[] btnMaterialTypeCnList = {
            "树脂", "果实", "贝壳", "金属", "玉石"};
    public final static String[] btnMaterialTypeEnList = {
            "resin", "fruit", "shell", "metal", "jade"};

    public static String btnMaterialEn2Cn(String color) {
        for (int i = 0; i < btnMaterialTypeEnList.length; i++) {
            if (color.equals(btnMaterialTypeEnList[i])) {
                return btnMaterialTypeCnList[i];
            }
        }
        return "不明";
    }

    /**
     * 钮扣线孔列表
     */
    public final static String[] btnHoleNumCnList = {
            "2", "4"};
    public final static String[] btnHoleNumEnList = {
            "2", "4"};

    /**
     * 算法选择页面:钮扣几何算法列表
     */
    public final static String[] btnGeoALGCnList = {
            "不检测", "几何检测算法"};

    public final static String[] btnGeoALGEnList = {
            "None", "ZWHAlgorithm"};

    /**
     * 算法选择页面:钮扣表面算法列表
     */
    public final static String[] btnSurALGCnList = {
            "不检测", "CNN算法", "结构算法", "色差算法", "纹理算法", "正反面训练算法"};

    public final static String[] btnSurALGEnList = {
            "None", "LLLAlgorithm", "TRWAlgorithm", "DoctorSUAlgorithm", "ChildStarAlgorithm", "LLLTrainAlgorithm"};

    /**
     * 缺陷类型列表
     */
    public final static String[] btnFlawCnList = {
            "崩边", "线孔变形", "几何偏差", "不均匀", "气泡", "表面破损", "异物", "其他"
    };


    /**
     * 光源配置
     */
    public final static String[] LSDSettingCnList = {
            "无",
            "相机1下光源",
            "相机2上光源",
            "相机2下光源",
            "相机3上光源",
            "相机3下光源"
    };

    /**
     * 方法选择配置
     */
    public final static String[] ALGGeoMethodNewCnList = {
            "不检测", "综合方法1（几何检测算法）", "自定义几何方法1"
    };
    public final static String[] ALGGeoMethodEnList = {
            "None", "ZWHAlgorithm", "UserGeoMethod1"
    };

    public final static String[] ALGSurMethodNewCnList = {
            "不检测", "综合方法1（CNN算法）", "综合方法2（结构算法）", "综合方法3（色差算法）", "综合方法4（纹理检测算法）", "综合方法5（正反面训练算法）", "自定义表面方法1"
    };
    public final static String[] ALGSurMethodEnList = {
            "None", "CNNAlgorithm", "frameworkAlgorithm", "aberrationAlgorithm", "textureAlgorithm", "trainAlgorithm", "UserSurMethod1"
    };

    public final static String[][] ALGGeoOperatorCnList = {
            {"默认"},
            {"默认", "几何检测算法"},
            {"默认", "几何检测算法"}
    };
    public final static String[][] ALGGeoOperatorEnList = {
            {"operator_Geotect_hzw"},
            {"operator_Geotect_hzw", "operator_Geotect_hzw"},
            {"operator_Geotect_hzw", "operator_Geotect_hzw"}
    };
    public final static String[][] ALGSurOperatorCnList = {
            {"无", "ROI提取算法1", "ROI提取算法2","ROI提取算法3","ROI提取算法4"},
            {"无", "正反面检测算法1", "正反面检测算法2"},
            {"无", "CNN算法", "结构算法", "色差算法", "纹理检测算法", "正反面训练算法"}
    };
    public final static String[][] ALGSurOperatorEnList = {
            {"none", "operator_getROI_llz", "operator_getROI_tx","operator_getROI_llz2","operator_getROI_llz3"},
            {"none", "operator_FBdetect_llz", "operator_FBdetect_tx"},
            {"none", "CNNAlgorithm", "frameworkAlgorithm", "aberrationAlgorithm", "textureAlgorithm", "trainAlgorithm"}
    };


    public final static String[] CameraInputTypeList = {"ICX414AL", "ICX424AL", "ICX424AQ", "ICX415AL", "ICX285AL",
            "MT9V032M", "MT9V032C", "0V7620", "SAAA7111", "PAL"};
    public final static String[] CameraOutputTypeList = {"lcd", "net", "crt"};

    public final static String NoButtonCfgFileWarnning = "请先选择钮扣配置文件";
    public final static String Camera1DisconnectedWarnning = "相机1未连接";
    public final static String Camera2DisconnectedWarnning = "相机2未连接";
    public final static String Camera3DisconnectedWarnning = "相机3未连接";
    public final static String[] CameraDisconnectedWarnning = {"相机1未连接", "相机2未连接", "相机3未连接"};
    public final static String NoOperatorWarnning = "配置文件中无方法配置内容，无法启动";
    public final static String BtnTrigger1DisconnectedWarnning = "钮扣触发装置1未连接，请检查";
    public final static String BtnTrigger2DisconnectedWarnning = "钮扣触发装置2未连接，请检查";
    public final static String BtnTrigger3DisconnectedWarnning = "钮扣触发装置3未连接，请检查";
    public final static String MotorCtrlCardDisconnectedWarnning = "电机控制卡未连接，请检查";
    public final static String LightSrcDrvDisconnectedWarnning = "光源控制器未连接，请检查";
    public final static String Password = "nanwu424";
    public final static String DefaultPassword = "nanwu424";
}
