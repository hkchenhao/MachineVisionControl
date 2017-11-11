#ifndef DATAPACKENUM_H
#define DATAPACKENUM_H
#include <QObject>
#include <QMetaType>

// 子命令枚举量
enum DataPacketEnum
{
    MSG_NET_NONE = 0,
    MSG_NET_GET_VIDEO = 1,      // 获取图像数据
    MSG_NET_GENERAL = 2,        // 设置相机参数
    MSG_NET_NORMAL = 11,		// 设置相机工作模式
    MSG_NET_STATE = 14,         // 获取温度
    MSG_NET_RESULT = 28,

    MSG_NET_ALG_TEST_CONFIGURE = 35,

    MSG_NET_SAVE_VIDEO = 22,    // 获取纽扣图像 DSP->ARM
    MSG_IS_SEND_IMAGE = 195,    // 是否发送图像 ARM->DSP
    MSG_NET_TOTAL_CNT = 196,    // 返回检测个数 DSP->ARM
    MSG_ALG_RESULT = 197,       // 返回判别结果 DSP->ARM
    MSG_ALG_INFO = 198,         // 返回钮扣信息 DSP->ARM
    MSG_NET_ALG_IMAGE = 199,    // 返回ROI图像 DSP->ARM
    MSG_NET_ALG_RESULT = 200,   // 返回算法结果 DSP->ARM
};
Q_DECLARE_METATYPE(DataPacketEnum)

#endif
