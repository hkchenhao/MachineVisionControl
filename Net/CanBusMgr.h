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
    void SetConveyerBelt(bool state);
    void SetLightsource(qint32 id, qint32 num);
    void SendCanData(qint32 id, unsigned char* buf, qint32 len);

private:
    CanBusMgr();
    void SlotReadCanData();
private:
    static CanBusMgr* instance_;
    bool isopen_;
    bool isgetresstr_;
    QSerialPort* serialport_;
};

#endif
