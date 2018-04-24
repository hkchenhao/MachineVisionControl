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

void CanBusMgr::SerialDelay()
{
    QTime Timer = QTime::currentTime();
    QTime NowTimer;
    do
    {
        NowTimer = QTime::currentTime();
    }
    while(Timer.msecsTo(NowTimer) <= 100);
}

void CanBusMgr::SerialFunc1()
{
    QByteArray atstr1("AT+CG\r\n");
    serialport_->write(atstr1);
}

void CanBusMgr::SerialFunc2()
{
    QByteArray atstr2("AT+CAN_FRAMEFORMAT=1,0,561,0\r\n");
    serialport_->write(atstr2);
}

void CanBusMgr::SerialFunc3()
{
    QByteArray atstr3("AT+CAN_FRAMEFORMAT=?\r\n");
    serialport_->write(atstr3);
}

void CanBusMgr::SetCanID(qint32 id)
{
    SerialFunc1();
    SerialFunc2();
    SerialFunc3();
//    isgetresstr_ = false;
//    QByteArray atstr1("AT+CG\r\n");
//    serialport_->write(atstr1);
    //while(!isgetresstr_);

//    isgetresstr_ = false;
//    QByteArray atstr2("AT+CAN_FRAMEFORMAT=1,0,561,0\r\n");
//    serialport_->write(atstr2);
//    while(!isgetresstr_);

//    isgetresstr_ = false;
//    QByteArray atstr3("AT+CAN_FRAMEFORMAT=?\r\n");
//    serialport_->write(atstr3);
//    while(!isgetresstr_);

//    QByteArray atstr4("AT+ET\r\n");
//    serialport_->write(atstr1);
//    serialport_->write(atstr2);
//    serialport_->write(atstr3);
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
//        QString str;
//        str = dataArray.toHex();//把数据直接转化为16进制的字符串形式
//        ui->textEdit->setText(str);
    }
}
