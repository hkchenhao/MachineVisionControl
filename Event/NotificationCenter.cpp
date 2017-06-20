#include "NotificationCenter.h"
#include "Data/DataPacket.h"

NotificationCenter::NotificationCenter(QObject* parent) : QObject(parent)
{

}

// 添加观察者
void NotificationCenter::AddObserver(int notificationId, QObject* observer)
{
    if (nullptr == observer)
    {
        return;
    }
    QSet<QObject*> &obs = observers_[notificationId];
    obs.insert(observer);
}

// 删除观察者
void NotificationCenter::RemoveObserver(int notificationId, QObject* observer)
{
    QSet<QObject*> &obs = observers_[notificationId];
    obs.remove(observer);
}

// 删除观察者
void NotificationCenter::RemoveObserver(QObject* observer)
{
    QList<int> notificationIds = observers_.keys();
    foreach (int notificationId, notificationIds)
    {
        RemoveObserver(notificationId, observer);
    }
}

// 给观察者发送通知
void NotificationCenter::Notify(int notificationId, QByteArray data)
{
    QSet<QObject*> obs = observers_[notificationId];
    foreach(QObject* observer, obs)
    {
        // 使用invokeMethod是为可跨线程进行函数调用
        QMetaObject::invokeMethod(observer, "Notified", Q_ARG(int, notificationId), Q_ARG(QByteArray, data));
    }
}

