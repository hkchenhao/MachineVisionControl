#include "NetServer.h"
#include "NetThread.h"
#include "Form/MainForm.h"
#include "Mgr/FormFrame.h"
#include "Mgr/SystemFrame.h"
#include "Utils/SystemConstants.h"

const int LISTEN_PORT = 6020;

NetServer::NetServer(QObject* parent) : QTcpServer(parent)
{
    QObject::connect(FormFrame::GetInstance()->p_mainform_, &MainForm::SignalNetClose, this, &NetServer::CloseNetServer, Qt::QueuedConnection);
}

// 启动TCP服务器监听
void NetServer::StartNetServer()
{
    // 如果端口监听出错则直接返回
    if(this->listen(QHostAddress::AnyIPv4, LISTEN_PORT) == false)
    {
        if(SystemFrame::GetInstance()->p_netserver_ != nullptr)
        {
            SystemFrame::GetInstance()->p_netserver_->deleteLater();
            SystemFrame::GetInstance()->p_netserver_ = nullptr;
        }
        return;
    }
}

// 关闭TCP服务器监听并释放资源
void NetServer::CloseNetServer()
{
    this->close();
    if(SystemFrame::GetInstance()->p_netserver_ != nullptr)
    {
        SystemFrame::GetInstance()->p_netserver_->deleteLater();

        SystemFrame::GetInstance()->p_netserver_ = nullptr;
    }
}

// 重载incomingConnection用于在新线程中创建QTcpSocket
void NetServer::incomingConnection(qintptr socketDescriptor)
{
    // 创建NetThread后自动启动线程
    NetThread* p_netthread = new NetThread(socketDescriptor);
    //SystemFrame::GetInstance()->p_netthread_ = new NetThread(socketDescriptor);
}
