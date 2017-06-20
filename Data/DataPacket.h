#ifndef DATAPACKET_H
#define DATAPACKET_H
#include <QObject>
#include <QByteArray>

/***** DataPacketBase数据包基类 *****/
class DataPacketBase
{
public:
    void CreateDataPacketByteArray();
    QByteArray& GetDataPacketByteArray();
public:
    qint32 magic;
    qint32 version;
    qint32 type;
    qint32 block;
    qint32 length;
    qint32 offset;
    qint32 minid;
    QByteArray data;
protected:
    // 不允许直接实例化
    DataPacketBase();
private:
    QByteArray datapacket_bytearray_;     // 命令数据包ByteArray序列化用于网络发送
};

/***** 接收数据包 *****/
class DataPacketReceived : public DataPacketBase
{
public:
    DataPacketReceived();
    DataPacketReceived& operator=(const DataPacketReceived& datapack);
    QByteArray& GetDataPacketByteArray();
};

/***** 发送数据包 *****/
// 获取相机图像数据包
class DataPacketGetVideo : public DataPacketBase
{
public:
    DataPacketGetVideo(const QByteArray& databyte);
};

// 设置相机模式数据包
class DataPacketSetCameraMode : public DataPacketBase
{
public:
    DataPacketSetCameraMode(const QByteArray& databyte);
};

// 获取相机温度数据包
class DataPacketGetCameraTemp : public DataPacketBase
{
public:
    DataPacketGetCameraTemp(const QByteArray& databyte);
};

// 设置图像格式
class DataPacketSetImageFormat : public DataPacketBase
{
public:
    DataPacketSetImageFormat(const QByteArray& databyte);
};

#endif
