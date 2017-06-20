#ifndef NETSERVER_H
#define NETSERVER_H
#include <QObject>
#include <QTcpServer>
#include <QTcpSocket>
#include <QString>

class NetServer : public QTcpServer
{
public:
    Q_OBJECT
public:
    NetServer(QObject* parent = Q_NULLPTR);
    void StartNetServer();
public slots:
    void CloseNetServer();
protected:
    // 重载incomingConnection用于在新线程中创建QTcpSocket
    void incomingConnection(qintptr socketDescriptor);
};

#endif
