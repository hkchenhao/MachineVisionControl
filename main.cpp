#include <QApplication>
#include <QDesktopWidget>
#include <QStyleFactory>
#include "Data/DataPacket.h"
#include "Data/DataPacketEnum.h"
#include "Mgr/FormFrame.h"
#include "Utils/FrmInput.h"

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
    // 加载键盘
    FrmInput::GetInstance()->Init("control", "black");
    // 加载界面
    FormFrame::GetInstance()->show();
    // 程序循环启动
    return app.exec();
}
