#include "DataFactory.h"

// [构造函数]根据包名生成对应数据包
DataFactory::DataFactory(DataPacketEnum datapacket_type, const QByteArray& databyte)
{
    switch(datapacket_type)
    {
        case MSG_NET_GET_VIDEO:
            p_datapacket_ = new DataPacketGetVideo(databyte);
            break;
        case MSG_NET_GENERAL:
            break;
        case MSG_NET_NORMAL:
            p_datapacket_ = new DataPacketSetCameraMode(databyte);
            break;
        case MSG_NET_STATE:
            p_datapacket_ = new DataPacketGetCameraTemp(databyte);
            break;
        case MSG_NET_ALG_TEST_CONFIGURE:
            p_datapacket_ = new DataPacketSetImageFormat(databyte);
            break;
        default:
            break;
    }
}

// [析构函数]
DataFactory::~DataFactory()
{
    if(p_datapacket_ != nullptr)
    {
        delete p_datapacket_;
        p_datapacket_ = nullptr;
    }
}

// 通过网络发送数据包
void DataFactory::SendDataPacketByNet(QTcpSocket* p_tcpsocket)
{
    if((p_tcpsocket != nullptr) && (p_datapacket_ != nullptr))
    {
        p_tcpsocket->write(p_datapacket_->GetDataPacketByteArray());
    }
}




