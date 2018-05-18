#include "CanBusMgr.h"

CanBusMgr* CanBusMgr::instance_ = nullptr;

CanBusMgr::CanBusMgr()
{
    isgetresstr_ = false;
    isopen_ = false;
    serialport_ = new QSerialPort();
    serialport_->setPortName("/dev/ttyUSB0");
    serialport_->setBaudRate(115200, QSerialPort::AllDirections);
    serialport_->setDataBits(QSerialPort::Data8);
    serialport_->setParity(QSerialPort::NoParity);
    serialport_->setStopBits(QSerialPort::OneStop);
    serialport_->setFlowControl(QSerialPort::NoFlowControl);
    if(serialport_->open(QIODevice::ReadWrite) == false)
    {
        isopen_ = false;
        serialport_->close();
        delete serialport_;
        return;
    }
    qDebug() << "serial open ok!\r\n";
    isopen_ = true;
    QObject::connect(serialport_, &QSerialPort::readyRead, this, &CanBusMgr::SlotReadCanData);

    QByteArray atstr("AT+AT\r\n");
    serialport_->write(atstr);
}

CanBusMgr::~CanBusMgr()
{
    if(serialport_ != nullptr)
    {
        if(isopen_)
            serialport_->close();
        delete serialport_;
        serialport_ = nullptr;
    }
}

CanBusMgr* CanBusMgr::GetInstance()
{
    if(instance_ == nullptr)
        instance_ = new CanBusMgr();
    return instance_;
}

void CanBusMgr::SendCanData(qint32 id, unsigned char* buf, qint32 len)
{
    unsigned char databuf[17] = {'A', 'T', (id >> 3) & 0xFF, (id & 0x07) << 5, 0, 0, len, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    for(qint32 i = 0; i < len; i++)
        databuf[i+7] = buf[i];
    databuf[len+7] = '\r';
    databuf[len+8] = '\n';
    serialport_->write((char*)databuf, 9 + len);
}

void CanBusMgr::SetMotorSpeed(qint32 speed)
{
//    char databuf[14] = {0x00, 0x00, 0x00, 0x02, 0x31, 0x08,
//                        0x31, 0x11, 0x41, 0x22, 0x00, 0x00, 0x00, 0x00};
//    databuf[10] = (speed) & 0xFF;
//    databuf[11] = (speed >> 8) & 0xFF;
//    databuf[12] = (speed >> 16 )& 0xFF;
//    databuf[13] = (speed >> 24) & 0xFF;

    char databuf[8] = {0x31, 0x11, 0x41, 0x22, 0x00, 0x00, 0x00, 0x00};
    databuf[4] = (speed) & 0xFF;
    databuf[5] = (speed >> 8) & 0xFF;
    databuf[6] = (speed >> 16 )& 0xFF;
    databuf[7] = (speed >> 24) & 0xFF;
    serialport_->write(databuf, 8);
}

void CanBusMgr::SlotReadCanData()
{
    QByteArray dataArray = serialport_->readAll();
    if(!dataArray.isEmpty())
    {
        QString str(dataArray);
        qDebug() << "serial return:" << str;
        if(str == "OK\r\n")
        {
            isgetresstr_ = true;
            qDebug() << "serial get ok";
        }
        else
        {
            qDebug() << dataArray.toHex();
        }
    }
}
