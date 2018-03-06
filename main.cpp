#include <QApplication>
#include <QDesktopWidget>
#include <QStyleFactory>
#include "Data/DataPacket.h"
#include "Data/DataPacketEnum.h"
#include "Mgr/FormFrame.h"
#include "Utils/FrmInput.h"

#include <QCanBus>
#include <QList>

int main(int argc, char *argv[])
{
    // 创建应用程序
    QApplication app(argc, argv);
    // 样式风格
    QApplication::setStyle(QStyleFactory::create("Fusion"));
    // 注册类型
    qRegisterMetaType<DataPacketEnum>("DataPacketEnum");
    qRegisterMetaType<DataPacketEnum>("DataPacketEnum&");
    qRegisterMetaType<DataPacketReceived>("DataPacketReceived");
    qRegisterMetaType<DataPacketReceived>("DataPacketReceived&");

//    QString error;
//    QCanBusDevice *device = QCanBus::instance()->createDevice(QStringLiteral("socketcan"), QStringLiteral("can1"));
//    qDebug() << device->connectDevice();
//    QList<QCanBusDeviceInfo> m =  QCanBus::instance()->availableDevices("socketcan", &error);
//    qDebug() << m.length();
//    for(auto i : m)
//        qDebug() << i.name() << endl;

    // 加载键盘
    //FrmInput::GetInstance()->Init("control", "black");
    // 加载界面
    FormFrame::GetInstance()->show();
    // 程序循环启动
    return app.exec();
}
