#ifndef SYSTEMFRAME_H
#define SYSTEMFRAME_H
#include <QObject>
#include <QReadWriteLock>
#include "Net/NetServer.h"
#include "Net/NetThread.h"
#include "Utils/SystemConstants.h"

class SystemFrame
{
public:
    ~SystemFrame();
    // 静态的单例指针获取接口
    static SystemFrame* GetInstance()
    {
        static SystemFrame instace_;
        return &instace_;
    }
public:
    NetServer* p_netserver_;
    NetThread* p_netthread_;
    NetThread* p_netthread_buf_[CAMERA_NUM];
public:
    QReadWriteLock lock;
private:
    // 私有化的构造函数，确保除了单例接口外，其他对象无法构造出系统管理类
    explicit SystemFrame();
};

#endif
