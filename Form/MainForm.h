#ifndef MAINFORM_H
#define MAINFORM_H
#include <QWidget>
#include <QLabel>
#include <QMutex>
#include <QProcess>
#include <QSettings>
#include <QTime>
#include "Data/DataPacketEnum.h"
#include "Utils/SystemConstants.h"

//尽量少直接包含其他的头文件，使用前置申明加快编译速度，减少后期维护的时间
namespace Ui
{
    class MainWidget;
}
class InfoCheckResForm;
class InfoSizeForm;
class QJsonAnalysis;

// 纽扣检测结果结构体
struct ButtonCheckResult
{
    // 检测结果
    qint32 total_num;
    qint32 right_num;
    qint32 wrong_num;
    float pass_rate;
    float fps_rate;
    // 检测图像QLabel指针
    QLabel* plable_image_;
    QLabel* plable_resulticon_;
    // 检测结果QLabel指针
    QLabel* plable_totalnum;
    QLabel* plable_rightnum;
    QLabel* plable_wrongnum;
    QLabel* plable_passrate;
    QLabel* plable_fpsrate;
};

class MainForm : public QWidget
{
    Q_OBJECT
public:
    explicit MainForm(QWidget* parent = nullptr);
    ~MainForm();
    void SetUserAccessPermission(qint32 user_id);
    Ui::MainWidget* GetWidgetPtr() { return ui; }

    //Q_INVOKABLE void Notified(int notificationId, const QByteArray& data);


protected:
    virtual void customEvent(QEvent* event);

private slots:
    /********** 自定义slot函数 **********/

    /********** 系统控件slot函数 **********/
    void on_ButtonSelectButton_clicked();       // 纽扣选择组控件slot函数
    void on_WorkStartButton_clicked();          // 检测控制组控件slot函数
    void on_WorkStopButton_clicked();
    void on_pushButton_AppClose_clicked();      // 程序关闭控件slot函数
    void on_pushButton_Statistics_clicked();    // 统计分析界面slot函数
    void on_pushButton_Learn_clicked();
    void on_pushButton_Settings_clicked();
    void on_pushButton_Manage_clicked();
    void on_pushButton_Help_clicked();

    void on_Button_ccxx_clicked();
    void on_Button_slxx_clicked();

    void on_Button_clicked();
    void on_Button_test_clicked();

signals:
    void SignalDetectControl(bool isstart);    // 检测开始与暂停信号
    void SignalNetSendPacket(DataPacketEnum datapacket_type, QByteArray databyte = QByteArray());
    void SignalNetSendJson(const QByteArray& databyte = QByteArray());
    void SignalNetClose();

private:
    Ui::MainWidget* ui;
    QVector<QRgb> vcolorTable; //生成灰度颜色表
    // 纽扣json文件
    QString buttonname;
    QJsonAnalysis* p_buttonjson;
    // 相机label控件指针数组
    QLabel* buf_cameralinkstatus_[CAMERA_NUM];
    // 相机检测结果数组
    ButtonCheckResult buf_buttoncheckresult_[CAMERA_NUM];
    InfoCheckResForm* p_infochenckresform;
    InfoSizeForm* p_infosizeform;
};

#endif
