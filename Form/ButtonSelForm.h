#ifndef BUTTONSELFORM_H
#define BUTTONSELFORM_H
#include <QCoreApplication>
#include <QVBoxLayout>
#include <QWidget>
#include <QEvent>
#include <QMessageBox>
#include <QDir>
#include <QFileInfo>
#include <QLabel>
#include <QImage>
#include <QPixmap>
#include <QVector>
#include <QString>
#include <QList>
#include <QMap>
#include "Data/DataPacketEnum.h"

namespace Ui
{
    class ButtonSelWidget;
}
class QJsonAnalysis;

/* 纽扣配置Json信息类 */
class ButtonInfoLabel : public QWidget
{
    Q_OBJECT
public:
    ButtonInfoLabel(const QString& filepath, const QString& filename, QWidget* parent = nullptr);
    ~ButtonInfoLabel();
    // 事件过滤器函数
    bool eventFilter(QObject* watched, QEvent* event);
    // 获取私有成员变量
    QLabel* GetButtonImagePtr() { return p_buttonImage; }
    QLabel* GetButtonNamePtr() { return p_buttonName; }
    QJsonAnalysis* GetButtonJsonPtr() { return p_buttonJsonInfo; }
    void SetButtonJsonPtr(QJsonAnalysis* pjson) {p_buttonJsonInfo = pjson; }
    QString* GetButtonJsonPathPtr() { return p_configFileAllPath; }
    QString* GetButtonJsonNamePtr() { return p_configFileName; }
signals:
    // 图像被选中时发出信号
    void Signal_ButtonImageSelected(ButtonInfoLabel* p_buttoninfo);
    void Signal_ButtonInfoNetPacketSend(DataPacketEnum datapacket_type, QByteArray databyte);
private:
    QString* p_configFilePath;          // 配置文件所在路径
    QString* p_configFileName;          // 配置文件名称
    QString* p_configFileAllPath;       // 配置文件完整路径名
    QJsonAnalysis* p_buttonJsonInfo;    // 纽扣配置json信息
    QLabel* p_buttonImage;              // 纽扣图像Label
    QLabel* p_buttonName;               // 纽扣名称Label
    QVBoxLayout* p_layout;              // 纽扣Label布局
};

/* 纽扣选择界面类 */
class ButtonSelForm : public QWidget
{
    Q_OBJECT
    typedef struct
    {
        qint32 buttonImagePageCount;                   // 纽扣信息区的总页数指针
        qint32 buttonImageCurrentPageNum;              // 纽扣信息区的当前页数指针
        qint32 buttonImageLastPageCount;               // 纽扣信息区最后一页图像数量指针
    } ButtonImagePageStruct;
public:
    explicit ButtonSelForm(QWidget* parent = nullptr);
    ~ButtonSelForm();
private:
    void InitFormWidget();
    void LoadAllConfigFileInfo();                               // 加载所有ini配置文件信息
    void ShowButtonImage();                                     // 显示纽扣图像
    void InitButtonPageInfo();                                  // 计算纽扣页面变量
    void UpdateButtonInfoLabel(bool iseditagain, bool isshow);  // 根据条件刷新纽扣信息区信息
private slots:
    // 自定义slot函数
    void SetButtonSelectStaus(ButtonInfoLabel* p_buttoninfo);
    // 控件slot函数
    void on_pushButton_NextPage_clicked();
    void on_pushButton_LastPage_clicked();
    void on_pushButton_Find_clicked();
    void on_pushButton_Clear_clicked();
    void on_pushButton_Esc_clicked();
    void on_pushButton_Enter_clicked();
    void on_pushButton_Edit_clicked();
    void on_checkBox_buttonid_clicked();
    void on_checkBox_buttontime_clicked();
    void on_checkBox_buttoninfo_clicked();

private:
    Ui::ButtonSelWidget* ui;                                // UI界面
    //QStringList* p_buttonAllNameStringList;                // 所有纽扣的命名
    QVector<ButtonInfoLabel*> v_pButtonOriginalInfo;         // 所有纽扣widget指针数组
    QVector<ButtonInfoLabel*> v_pButtonSelectedInfo;         // 筛选纽扣widget指针数组
    QVector<ButtonInfoLabel*>* p_currentButtonInfoVector;
    ButtonImagePageStruct buttonOriginalPageInfo;           // 所有纽扣widget显示页数信息
    ButtonImagePageStruct buttonSelectedPageInfo;           // 筛选纽扣widget显示页数信息
    ButtonImagePageStruct* p_currentButtonPageInfoStruct;
    ButtonInfoLabel* p_currentSeletedButton;                 // 当前被选取的纽扣图像指针
    ButtonInfoLabel* p_lastSeletedButton;                    // 上一个被选取的纽扣图像指针
    QString currentSeletedButtonName;                        // 当前被选取的纽扣ID
};

#endif
