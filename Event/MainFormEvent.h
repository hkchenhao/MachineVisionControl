#ifndef MAINFORMEVENT_H
#define MAINFORMEVENT_H
#include <QObject>
#include <QEvent>
#include <QString>
#include "FormEventEnum.h"
#include "Data/DataPacket.h"
#include "Utils/SystemConstants.h"

class MainFormEvent : public QEvent
{
public:
    enum MainFormEventType
    {
        EventType_None= 0,
        EventType_CarmeraCheckResult,
        EventType_CarmeraLinkStatus,
        EventType_ButtonSelectedResult,
    };
    MainFormEvent(MainFormEventType eventtype = EventType_None, CameraIdentifier cameraid = CameraIdentifier_None);
public:
    MainFormEventType eventtype_;                   // 主界面事件类型
    CameraIdentifier cameraid_;                     // 相机编号
    DataPacketReceived checkresult_packet_;         // 相机检测结果数据包指针
    bool linkstatus_;                               // 相机连接结果
    QString button_id_;                             // 纽扣ID编号
};

#endif
