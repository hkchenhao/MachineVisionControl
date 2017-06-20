#ifndef DATAFACTORY_H
#define DATAFACTORY_H
#include <QTcpSocket>
#include "DataPacket.h"
#include "DataPacketEnum.h"

class DataFactory
{
public:
    DataFactory(DataPacketEnum datapacket_type, const QByteArray& databyte = QByteArray());
    ~DataFactory();
    void SendDataPacketByNet(QTcpSocket* p_tcpsocket);
private:
    // 数据包指针
    DataPacketBase* p_datapacket_;
};

#endif
