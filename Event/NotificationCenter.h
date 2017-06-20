#ifndef NOTIFICATIONCENTER_H
#define NOTIFICATIONCENTER_H
#include <QMetaObject>
#include <QObject>
#include <QList>
#include <QSet>
#include <QHash>

class DataPacketReceived;

/**
 * 如果发送通知的线程不是主线程，因为 NotificationCenter 的对象是主线程创建的，则调用此宏发送，当然，同线程也可以使用这个宏来发送消息
 */
#define NOTIFY_CROSS_THREAD(notificationId, data) QMetaObject::invokeMethod(NotificationCenter::GetInstance(), "Notify",\
                                                                            Q_ARG(int, notificationId), Q_ARG(QByteArray, data));

/**
 * @brief 使用观察者模式实现的通知中心，解耦观察者和被观察者，他们无须互相知道，Qt 的信号槽也是观察者模式，在 connect 的时候需要知道 sender 和 receiver，
 * 而通过通知中心发消息的话，通知发送方和接收方都只和通知中心交互: 观察者只需要监听某个通知，不需要知道通知是谁发送的，通知发送者也只管通过通知中心发送通知即可。
 *
 * 注意：观察者需要实现 SLOT 或者 Q_INVOKABLE 的函数 void notified(int notificationId, const QByteArray &data)，这个函数通把通知发送給对应的观察者。
 * 为了简单不再多加个继承，所以没有为观察者定义一个抽象类的接口，然后观察者去继承实现这个抽象类的方法。因为使用了 QMetaObject::invokeMethod() 来实现
 * 有通知到达的调用，而且这样做还有一个好处是可以解决跨线程调用的问题。
 *
 * 如果通知的观察者没有实现 notified() 函数，则程序运行时会在命令行输出错误: QMetaObject::invokeMethod: No such method Xxxx::notified(int,QByteArray)
 */
class NotificationCenter : public QObject
{
    Q_OBJECT

public:
    /**
     * @brief 单例模式
     */
    static NotificationCenter* GetInstance()
    {
        static NotificationCenter instace_;
        return &instace_;
    }
    /**
     * @brief 给传入的notificationId添加观察者observer
     *
     * @param notificationId通知的id
     * @param observer观察者
     */
    Q_INVOKABLE void AddObserver(int notificationId, QObject* observer);
    /**
     * @brief 删除notificationId的观察者observer
     *
     * @param notificationId通知的 id
     * @param observer观察者
     */
    Q_INVOKABLE void RemoveObserver(int notificationId, QObject* observer);
    Q_INVOKABLE void RemoveObserver(QObject* observer);
    /**
     * @brief 給观察者发送notificationId 的通知，通知的内容为 QByteArray。
     * 使用QByteArray是为了便于利用QDataStream 序列化和反序列化。
     *
     * @param notificationId通知的 id
     * @param data通知的内容
     */
    Q_INVOKABLE void Notify(int notificationId, QByteArray data);

private:
    explicit NotificationCenter(QObject* parent = Q_NULLPTR);

private:
    // 某一个通知的观察者: key是通知的id，value是订阅了这个通知的所有观察者
    QHash<int, QSet<QObject*>> observers_;
};

#endif
