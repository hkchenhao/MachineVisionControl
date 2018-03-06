#include "DataPacket.h"
#include "DataPacketEnum.h"

/***** DataPacketBase数据包基类 *****/
DataPacketBase::DataPacketBase()
{
    magic = 0x695A695A;     //NetUtils::LittleToBigEndian(0x695a695a);
    version = 0;
    type = 0;
    block = 5000;
    length = 28;
    offset = 28;
    minid = 0;
    data.resize(0);
}

void DataPacketBase::CreateDataPacketByteArray()
{
    datapacket_bytearray_.resize(0);  // 清空数组
    datapacket_bytearray_.append((char*)&magic, 4);
    datapacket_bytearray_.append((char*)&version, 4);
    datapacket_bytearray_.append((char*)&type, 4);
    datapacket_bytearray_.append((char*)&block, 4);
    datapacket_bytearray_.append((char*)&length, 4);
    datapacket_bytearray_.append((char*)&offset, 4);
    datapacket_bytearray_.append((char*)&minid, 4);
    datapacket_bytearray_.append(data);
}

QByteArray& DataPacketBase::GetDataPacketByteArray()
{
    return datapacket_bytearray_;
}

/***** 接收数据包 *****/
DataPacketReceived::DataPacketReceived()
{
    magic = 0;
    block = 0;
    length = 0;
    offset = 0;
}

DataPacketReceived& DataPacketReceived::operator=(const DataPacketReceived& datapack)
{
    this->magic = datapack.magic;
    this->version = datapack.version;;
    this->type = datapack.type;
    this->block = datapack.block;
    this->length = datapack.length;
    this->offset = datapack.offset;
    this->minid = datapack.minid;
    this->data = datapack.data;
    return *this;
}

QByteArray& DataPacketReceived::GetDataPacketByteArray()
{
    CreateDataPacketByteArray();
    return DataPacketBase::GetDataPacketByteArray();
}

/***** 发送数据包 *****/
// 获取相机图像数据包
DataPacketGetVideo::DataPacketGetVideo(const QByteArray& databyte)
{
    type = 1;
    minid = 1;
    data.append(databyte);
    CreateDataPacketByteArray();
}

// 设置相机参数数据包
DataPacketSetCameraMode::DataPacketSetCameraMode(const QByteArray& databyte)
{
    length = 28 + databyte.size();
    minid = 11;
    data.append(databyte);
    //data.append(1, 0x04);
    CreateDataPacketByteArray();
}

// 获取相机温度数据包
DataPacketGetCameraTemp::DataPacketGetCameraTemp(const QByteArray& databyte)
{
    type = 1;
    length = 28 + databyte.size();
    minid = 14;
    data.append(databyte);
//    data.append(1, 0x01);
//    data.append(3, 0x00);
    CreateDataPacketByteArray();
}

// 设置图像格式
DataPacketSetImageFormat::DataPacketSetImageFormat(const QByteArray& databyte)
{
    length = 28 + databyte.size();
    minid = MSG_NET_ALG_TEST_CONFIGURE;
    data.append(databyte);
    //data.append(1, 0x04);
    CreateDataPacketByteArray();
}

// 发送json文件
DataPacketSendJson::DataPacketSendJson(const QByteArray& databyte)
{
    length = 28 + databyte.size();
    minid = MSG_NET_SEND_JSON;
    data.append(databyte);
    CreateDataPacketByteArray();
}
