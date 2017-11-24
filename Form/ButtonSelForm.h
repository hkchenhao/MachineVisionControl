#ifndef BUTTONSELFORM_H
#define BUTTONSELFORM_H
#include <QCoreApplication>
#include <QVector>
#include <QWidget>
#include <QDir>

namespace Ui
{
    class ButtonSelWidget;
}
class ButtonInfo;

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
    // 加载所有ini配置文件信息
    void LoadAllConfigFileInfo();
    // 显示纽扣图像
    void ShowButtonImage();
    // 计算纽扣页面变量
    void InitButtonPageInfo();
    // 根据条件刷新纽扣信息区信息
    void UpdateButtonInfoLabel(bool isshow);

private slots:
    // 自定义slot函数
    void SetButtonSelectStaus(ButtonInfo* p_buttoninfo);
    // 控件slot函数
    void on_pushButton_NextPage_clicked();
    void on_pushButton_LastPage_clicked();
    void on_pushButton_Find_clicked();
    void on_pushButton_Clear_clicked();
    void on_pushButton_Esc_clicked();
    void on_pushButton_Enter_clicked();

private:
    Ui::ButtonSelWidget* ui;                                // UI界面
    QVector<ButtonInfo*> v_pButtonOriginalInfo;             // 所有纽扣widget指针数组
    QVector<ButtonInfo*> v_pButtonSelectedInfo;             // 筛选纽扣widget指针数组
    QVector<ButtonInfo*>* p_currentButtonInfoVector;
    ButtonImagePageStruct buttonOriginalPageInfo;           // 所有纽扣widget显示页数信息
    ButtonImagePageStruct buttonSelectedPageInfo;           // 筛选纽扣widget显示页数信息
    ButtonImagePageStruct* p_currentButtonPageInfoStruct;
    ButtonInfo* p_currentSeletedButton;                     // 当前被选取的纽扣图像指针
    ButtonInfo* p_lastSeletedButton;                        // 上一个被选取的纽扣图像指针
    QString currentSeletedButtonName;                       // 当前被选取的纽扣ID
};

#endif
