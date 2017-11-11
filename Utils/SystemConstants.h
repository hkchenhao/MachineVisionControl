#ifndef SYSTEMCONSTANTS_H
#define SYSTEMCONSTANTS_H

// 枚举常量
// 相机数目
#define CAMERA_NUM 3

#define CAMERA1_IP "115.156.211.1"
#define CAMERA2_IP "115.156.211.2"
#define CAMERA3_IP "115.156.211.3"


// 相机编号（enum值必须是0 1 2）
enum CameraIdentifier
{
    CameraIdentifier_None = -1,
    CameraIdentifier_Num1 = 0,
    CameraIdentifier_Num2 = 1,
    CameraIdentifier_Num3 = 2,
};

#endif
