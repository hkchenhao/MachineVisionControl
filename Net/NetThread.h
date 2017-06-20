#ifndef NETTHREAD_H
#define NETTHREAD_H
#include <QObject>
#include <QCoreApplication>
#include <QThread>
#include <QTime>
#include <QTcpServer>
#include <QTcpSocket>
#include <QString>
#include "Data/DataPacket.h"
#include "Data/DataPacketEnum.h"
#include "Utils/SystemConstants.h"

class NetThread : public QObject
{
    Q_OBJECT
public:
    NetThread(qintptr socketDescriptor, QObject* parent = nullptr);
    ~NetThread(); 
private slots:
    // 开启线程与网络链接
    //void StartThread();
    void StartNet();
    // 关闭线程与网络链接
    void CloseThread();
    void CloseNet();
    // 发送网络数据包
    void SendNetPacket(DataPacketEnum datapacket_type, const QByteArray& databyte);
    // 获取网络数据包
    void GetNetPacket();
    // 处理网络数据包
    void HandleNetPacket();
signals:
    void SignalSentPic(char* ptr);
private:
    QThread* net_thread_;
    QTcpSocket* net_socket_;
    QTime net_timecal_;
    DataPacketReceived net_packet_;
    CameraIdentifier camera_id_;
    qint32 netpacket_preread_length_;   // 数据包已经读取的数据长度
    bool is_netpacket_done_;            // 数据包是否接收完成
    qint32 temp;                        // 临时变量

};

#endif
