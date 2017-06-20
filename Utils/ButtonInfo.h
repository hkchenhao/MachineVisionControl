#ifndef BUTTONINFO_H
#define BUTTONINFO_H
#include <QCoreApplication>
#include <QVBoxLayout>
#include <QEvent>
#include <QString>
#include <QSettings>
#include <QWidget>
#include <QLabel>
#include <QImage>
#include <QPixmap>

class ButtonInfo : public QWidget
{
    Q_OBJECT
public:
    ButtonInfo(const QString& filepath, const QString& filename, QWidget* parent = nullptr);
    ~ButtonInfo();
    // 事件过滤器函数
    bool eventFilter(QObject* watched, QEvent* event);
    // 获取私有成员变量
    QLabel* GetButtonImagePtr() { return p_buttonImage; }
    QLabel* GetButtonNamePtr() { return p_buttonName; }
    QSettings* GetButtonInfoPtr() { return p_buttonInfo; }
signals:
    // 图像被选中时发出信号
    void SignalCmd_ButtonImageSelected(ButtonInfo* p_buttoninfo);
private:
    // 配置label属性
    void ConfigButtonInfoWidget();
private:
    QString* p_configFilePath;   // 配置文件所在路径
    QString* p_configFileName;   // 配置文件名称
    QSettings* p_buttonInfo;     // 纽扣配置ini信息
    QLabel* p_buttonImage;       // 纽扣图像Label
    QLabel* p_buttonName;        // 纽扣名称Label
    QVBoxLayout* p_layout;       // 纽扣Label布局
};

#endif
