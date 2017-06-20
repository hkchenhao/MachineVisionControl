#include "SystemUtils.h"

// 根据部分IP子串获取本地IPv4地址
QHostAddress SystemUtils::GetLocalHostIPByPortion(const QString& str_ip_portion)
{
  QList<QHostAddress> ipaddresslist = QNetworkInterface::allAddresses();
  foreach(QHostAddress ipaddress, ipaddresslist)
  {
      if(ipaddress.protocol() == QAbstractSocket::IPv4Protocol && \
         ipaddress != QHostAddress::Null && \
         ipaddress != QHostAddress::LocalHost)
      {
          if (ipaddress.toString().contains(str_ip_portion))
          {
              return ipaddress;
          }
      }
  }
  return QHostAddress("0.0.0.0");
}

qint32 SystemUtils::LittleToBigEndian(qint32 src)
{
    qint32 temp1 = (src & 0x000000FF) << 24;
    qint32 temp2 = (src & 0x0000FF00) << 8;
    qint32 temp3 = (src & 0x00FF0000) >> 8;
    qint32 temp4 = (src & 0xFF000000) >> 24;
    return (temp1 | temp2 | temp3 | temp4);
}

// 纽扣配置文件所在路径
QString SystemUtils::GetPathForButtonConfigFile()
{
    return QCoreApplication::applicationDirPath() + QString("/Configs/");
}
