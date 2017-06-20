#ifndef NETUTILS_H
#define NETUTILS_H
#include <QCoreApplication>
#include <QHostAddress>
#include <QNetworkInterface>
#include <QString>

class SystemUtils
{
public:
    // 根据部分IP子串获取本地IPv4地址
    static QHostAddress GetLocalHostIPByPortion(const QString& str_ip_portion);
    static qint32 LittleToBigEndian(qint32 src);
    // 纽扣配置文件所在路径
    static QString GetPathForButtonConfigFile();
};

#endif
