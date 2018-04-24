#ifndef CANBUSMGR_H
#define CANBUSMGR_H
#include <QObject>
#include <QDebug>
#include <QSerialPort>
#include <QTime>

class CanBusMgr : public QObject
{
public:
    ~CanBusMgr();
    static CanBusMgr* GetInstance();
    void SetMotorSpeed(qint32 speed);
    void SetCanID(qint32 id);
private:
    CanBusMgr();
    void SerialDelay();
    void SlotReadCanData();

    void SerialFunc1();
    void SerialFunc2();
    void SerialFunc3();
private:
    static CanBusMgr* instance_;
    bool isopen_;
    bool isgetresstr_;
    QSerialPort* serialport_;
};

#endif
